package com.rodionovmax.materialnasa.ui.gallery

import android.Manifest
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
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
import com.rodionovmax.materialnasa.data.model.SharedStoragePhoto
import com.rodionovmax.materialnasa.databinding.FragmentGalleryBinding
import com.rodionovmax.materialnasa.ui.settings.IS_EXTERNAL_STORAGE
import com.rodionovmax.materialnasa.ui.settings.SHARED_PREFS
import com.rodionovmax.materialnasa.utils.sdk29AndUp
import kotlinx.coroutines.*
import java.io.IOException
import java.util.*


open class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!
    private val viewModel by lazy { GalleryViewModel(app.localRepo) }
    lateinit var itemTouchHelper: ItemTouchHelper
    private lateinit var takePhoto: ActivityResultLauncher<Void?>
    private val prefs: SharedPreferences by lazy { requireActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE) }

    private var readPermissionGranted = false
    private var writePermissionGranted = false
    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>

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

        permissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            readPermissionGranted = permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: readPermissionGranted
            writePermissionGranted = permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: writePermissionGranted


        }
        updateOrRequestPermissions()

        takePhoto = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->

            val isExternalStorage = prefs.getBoolean(IS_EXTERNAL_STORAGE, false)
            val fileName = UUID.randomUUID().toString()
            val gallerySize = adapter.countItems()

            lifecycleScope.launch(Dispatchers.Main) {
                val isSavedSuccessfully = when {
                    !isExternalStorage -> bitmap?.let { it ->
                        savePhotoToInternalStorage(fileName, it)
                    }
                    writePermissionGranted -> bitmap?.let { it ->
                        savePhotoToExternalStorage(fileName, it)
                    }
                    else -> false
                }

                if (!isExternalStorage) {
                    saveLatestPhotoFromInternalStorageToDb()
                    viewModel.addCameraPhotoToDb(
                        CameraPhotoEntity(
                            gallerySize + 1,
                            "$fileName.jpg",
                            bitmap!!
                        )
                    )
                } else {
                    delay(1000) // replace by job.await() or other async
                    saveLatestPhotoFromExternalStorageToDb()
                }
                val storage = if (isExternalStorage) {
                    "external"
                } else {
                    "internal"
                }
                if (isSavedSuccessfully == true) {
                    Toast.makeText(
                        requireContext(),
                        "Photo saved successfully to $storage storage",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(requireContext(), "Failed to save photo", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private suspend fun loadPhotosFromExternalStorage(): List<SharedStoragePhoto> {
        return withContext(Dispatchers.IO) {
            val collection = sdk29AndUp {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI

            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.WIDTH,
                MediaStore.Images.Media.HEIGHT,
            )
            val photos = mutableListOf<SharedStoragePhoto>()
            requireContext().contentResolver.query(
                collection,
                projection,
                null,
                null,
//                "${MediaStore.Images.Media.DISPLAY_NAME} ASC"
                "${MediaStore.Images.Media.DATE_MODIFIED} DESC"
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)
                val heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)

                while(cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val displayName = cursor.getString(displayNameColumn)
                    val width = cursor.getInt(widthColumn)
                    val height = cursor.getInt(heightColumn)
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                    photos.add(SharedStoragePhoto(id, displayName, width, height, contentUri))
                }
                photos.toList()
            } ?: listOf()
        }
    }

    private fun updateOrRequestPermissions() {
        val hasReadPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        val hasWritePermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        val minSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

        readPermissionGranted = hasReadPermission
        writePermissionGranted = hasWritePermission || minSdk29

        val permissionsToRequest = mutableListOf<String>()
        if (!writePermissionGranted) {
            permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (!readPermissionGranted) {
            permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (permissionsToRequest.isNotEmpty()) {
            permissionsLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    private suspend fun savePhotoToExternalStorage(displayName: String, bmp: Bitmap): Boolean {
        return withContext(Dispatchers.Default) {
            val imageCollection = sdk29AndUp {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI

            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "$displayName.jpg")
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.WIDTH, bmp.width)
                put(MediaStore.Images.Media.HEIGHT, bmp.height)
            }
            try {
                requireContext().contentResolver.insert(imageCollection, contentValues)?.also { uri ->
                    requireContext().contentResolver.openOutputStream(uri).use { outputStream ->
                        if(!bmp.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)) {
                            throw IOException("Couldn't save bitmap")
                        }
                    }
                } ?: throw IOException("Couldn't create MediaStore entry")
                true
            } catch(e: IOException) {
                e.printStackTrace()
                false
            }
        }
    }

    private fun saveLatestPhotoFromInternalStorageToDb() {
        lifecycleScope.launch {
            val photos = loadPhotosFromInternalStorage()
            // check latest photo
            Log.d("my_tag", photos[0].name)
        }
    }

    private fun saveLatestPhotoFromExternalStorageToDb() {
        lifecycleScope.launch {
            val photos = async { loadPhotosFromExternalStorage() }.await()
            val latestPhoto = photos[0]
            viewModel.addGalleryPictureToDb(latestPhoto)
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

    private suspend fun savePhotoToInternalStorage(filename: String, bmp: Bitmap): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                requireContext().openFileOutput("$filename.jpg", MODE_PRIVATE).use { stream ->
                    if (!bmp.compress(Bitmap.CompressFormat.JPEG, 95, stream)) {
                        throw IOException("Couldn't save bitmap")
                    }
                }
                true
            } catch (e: IOException) {
                e.printStackTrace()
                false
            }
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
