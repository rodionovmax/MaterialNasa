package com.rodionovmax.materialnasa.ui.explore.picture

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.rodionovmax.materialnasa.R
import com.rodionovmax.materialnasa.app
import com.rodionovmax.materialnasa.data.model.MarsPhoto
import com.rodionovmax.materialnasa.databinding.FragmentPictureBinding
import com.rodionovmax.materialnasa.ui.explore.mars.MarsViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class PictureFragment : Fragment() {

    private var _binding: FragmentPictureBinding? = null
    private val binding get() = _binding!!
    val viewModel by lazy { PictureViewModel(app.localRepo) }
    private val pictureBundle: MarsPhoto by lazy { arguments?.getParcelable(BUNDLE_PICTURE)!! }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPictureBinding.inflate(inflater, container, false)

//        initViews()
        subscribeObservers()
        displayRoverPhoto(pictureBundle)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initViews() {

    }

    private fun displayRoverPhoto(photo: MarsPhoto) {
        Glide.with(requireContext()).load(photo.imgSrc).into(binding.picture)
    }

    private fun subscribeObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    when (state) {
                        is PictureUiState.Empty -> {
                            binding.progressBar.visibility = View.GONE
                        }
                        is PictureUiState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            binding.picture.visibility = View.VISIBLE
                        }
                        is PictureUiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.picture.visibility = View.GONE
                            state.error.message?.let { message ->
                                Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT)
                            }
                        }
                        is PictureUiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.picture.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val BUNDLE_PICTURE = "picture"
        fun newInstance(bundle: Bundle) = PictureFragment().apply {
            arguments = bundle
        }
    }
}