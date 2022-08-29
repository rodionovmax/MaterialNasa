package com.rodionovmax.materialnasa.ui.pod

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.rodionovmax.materialnasa.databinding.FragmentPodBinding
import com.rodionovmax.materialnasa.domain.model.Pod

class PodFragment : Fragment() {

    private var _binding: FragmentPodBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PodViewModel by lazy { ViewModelProvider(this)[PodViewModel::class.java] }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPodBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initViewModel()
    }

    private fun initViews() {
        binding.chipGroup.setOnCheckedChangeListener { chipGroup, position ->
            val chip: Chip = binding.chipGroup.findViewById(position)
            viewModel.setDateOnChipClicked(position)
            Toast.makeText(
                requireContext(),
                "Selected picture from ${chip.text}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun initViewModel() {
        viewModel.progressLiveData.observe(viewLifecycleOwner) { showProgress(it) }
        viewModel.podLiveData.observe(viewLifecycleOwner, Observer<Pod> { showPod(it) })
        viewModel.errorLiveData.observe(viewLifecycleOwner) { showError(it) }
    }

    private fun showPod(pod: Pod) {
        Glide.with(requireContext()).load(pod.url).into(binding.podImage)
        binding.podPictureTitle.text = pod.title
        binding.podPictureDescription.text = pod.description
    }

    private fun showProgress(inProgress: Boolean) {
        binding.progressBar.isVisible = inProgress
        binding.podPictureTitle.isVisible = !inProgress
    }

    private fun showError(error: Throwable) {
        Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}