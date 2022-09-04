package com.rodionovmax.materialnasa.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.rodionovmax.materialnasa.app
import com.rodionovmax.materialnasa.databinding.FragmentGalleryBinding
import com.rodionovmax.materialnasa.domain.model.Pod

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!
    private val adapter = GalleryAdapter()
    private val viewModel by lazy { GalleryViewModel(app.localRepo) }

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