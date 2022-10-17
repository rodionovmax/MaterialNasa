package com.rodionovmax.materialnasa.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rodionovmax.materialnasa.app
import com.rodionovmax.materialnasa.data.model.Pod
import com.rodionovmax.materialnasa.databinding.FragmentGalleryBinding

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!
    private val viewModel by lazy { GalleryViewModel(app.localRepo) }
    lateinit var itemTouchHelper: ItemTouchHelper

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

        itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback(adapter, requireActivity()))
        itemTouchHelper.attachToRecyclerView(binding.galleryRecycler)
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

}
