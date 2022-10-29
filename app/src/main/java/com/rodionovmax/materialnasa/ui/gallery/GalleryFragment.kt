package com.rodionovmax.materialnasa.ui.gallery

import android.content.Context.MODE_PRIVATE
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rodionovmax.materialnasa.R
import com.rodionovmax.materialnasa.app
import com.rodionovmax.materialnasa.data.local.CameraPhotoEntity
import com.rodionovmax.materialnasa.data.model.InternalStoragePhoto
import com.rodionovmax.materialnasa.data.model.Pod
import com.rodionovmax.materialnasa.databinding.FragmentGalleryBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*


class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!
    private val viewModel by lazy { GalleryViewModel(app.localRepo) }
    lateinit var itemTouchHelper: ItemTouchHelper
    private lateinit var takePhoto: ActivityResultLauncher<Void?>

    companion object {
        private const val CAMERA_PERMISSION_CODE = 1
        private const val CAMERA_REQUEST_CODE = 2
    }

    private val adapter: GalleryAdapter by lazy {
        GalleryAdapter(
            object : GalleryListeners.OnDeleteButtonClickedListener {
                override fun removeFromDatabase(pod: Pod) {
                    viewModel.removeFromGallery(pod)
                    Toast.makeText(requireActivity(), "Removed from the gallery", Toast.LENGTH_SHORT).show()
                }
            },
            object : GalleryListeners.OnStartDragListener {
                override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
                    itemTouchHelper.startDrag(viewHolder)
                }
            },
            object : GalleryListeners.OnPositionsChangedListener {
                override fun updatePositionsInDb(posFrom: Int, posTo: Int, pod: Pod) {
                    viewModel.updateGalleryItemPositionsInDb(posFrom, posTo, pod)
                }
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initViewModel()
        setFab()

        itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback(adapter, requireActivity()))
        itemTouchHelper.attachToRecyclerView(binding.galleryRecycler)

        takePhoto = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) {
            val fileName = UUID.randomUUID().toString()
            val isSavedSuccessfully =
                it?.let { bmp -> savePhotoToInternalStorage(fileName, bmp) }
            if (isSavedSuccessfully == true) {
                loadLatestPhotoFromInternalStorage()
            }
            val gallerySize = adapter.countItems()
            viewModel.addToGallery(CameraPhotoEntity(gallerySize + 1, "$fileName.jpg", it!!))
        }
    }

    private fun loadLatestPhotoFromInternalStorage() {
        lifecycleScope.launch {
            val photos = loadPhotosFromInternalStorage()
            // check latest photo
            Log.d("my_tag", photos[0].name)
        }
    }

    private suspend fun loadPhotosFromInternalStorage(): List<InternalStoragePhoto> {
        return withContext(Dispatchers.IO) {
            val files = requireContext().filesDir.listFiles()
            files?.sortByDescending { it.lastModified() }
            files?.filter { it.canRead() && it.isFile && it.name.endsWith(".jpg") }?.map {
                val bytes = it.readBytes()
                val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                InternalStoragePhoto(it.name, bmp)
            } ?: listOf()
        }
    }

    private fun savePhotoToInternalStorage(filename: String, bmp: Bitmap): Boolean {
        return try {
            requireContext().openFileOutput("$filename.jpg", MODE_PRIVATE).use { stream ->
                if (!bmp.compress(Bitmap.CompressFormat.JPEG, 95, stream)) {
                    throw IOException("Couldn't save bitmap")
                }
            }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
    }

    private fun setFab() {
        binding.fab.setOnClickListener {
            showAddPhotoAlertDialog()
//            showDialog() // option 2
        }
    }

    // option 1. build alert dialog
    private fun showAddPhotoAlertDialog() {
        val customLayout: View = layoutInflater.inflate(R.layout.fragment_add_photo_dialog, null)

        val alertDialog = AlertDialog.Builder(requireActivity())
            .setView(customLayout)
            .setNegativeButton("Cancel") { dialog, i ->
                dialog.dismiss()
            }
            .create()

        customLayout.findViewById<View>(R.id.camera_button).setOnClickListener {
            // cancelling dialog after clicking on a button
            alertDialog.cancel()

            if ((ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA)) == PackageManager.PERMISSION_GRANTED) {
                takePhoto.launch()
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(android.Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_CODE
                )
            }

            // old version of starting another activity
            /*if ((ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA)) == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, CAMERA_REQUEST_CODE)
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(android.Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_CODE
                )
            }*/
            Toast.makeText(requireContext(), "Going to camera app...", Toast.LENGTH_SHORT).show()
        }
        customLayout.findViewById<View>(R.id.library_button).setOnClickListener {
            Toast.makeText(requireContext(), "Going to photo gallery...", Toast.LENGTH_SHORT).show()
        }

        alertDialog.show()
    }

    // option 2. call dialog from another fragment using fragment manager
    private fun showDialog() {
        val fm: FragmentManager = activity?.supportFragmentManager ?: throw Exception("Activity does not exist")
        val addPhotoDialogFragment = AddPhotoDialogFragment()
        addPhotoDialogFragment.show(fm, "fragment_edit_name")
    }

    private fun initViewModel() {
        viewModel.progressLiveData.observe(viewLifecycleOwner) { showProgress(it) }
        viewModel.galleryLiveData.observe(viewLifecycleOwner) { showGallery(it) }
        viewModel.errorLiveData.observe(viewLifecycleOwner) { showError(it) }
    }

    private fun showProgress(inProgress: Boolean) {
        binding.progressHorizontal.isVisible = inProgress
        binding.galleryRecycler.isVisible = !inProgress
    }

    private fun showGallery(gallery: List<Pod>) {
        adapter.setData(gallery)
//        adapter.setNewData(gallery)
    }

    private fun showError(error: Throwable) {
        Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
    }

    private fun initViews() {
        viewModel.getGallery()
        initRecyclerView()
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.getGallery()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun initRecyclerView() {
        binding.galleryRecycler.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.galleryRecycler.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // old version of requesting permission
    /*override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, CAMERA_REQUEST_CODE)
            } else {
                Toast.makeText(requireContext(), "Oops you just denied the permission for camera", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // old version of getting an activity result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode ==Activity.RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE) {
                val picture: Bitmap = data?.extras?.get("data") as Bitmap
//                iv_image.setImageBitmap(thumbNail)
            }
        }
    }*/

}
