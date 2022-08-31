package com.rodionovmax.materialnasa.ui.pod

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
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
import com.google.android.material.chip.ChipGroup
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

    @SuppressLint("RestrictedApi")
    private fun initViews() {
        showProgress(false)
        binding.chipGroup.setOnCheckedChangeListener { chipGroup: ChipGroup, position: Int ->
            val selectedChip: Chip? = binding.chipGroup.findViewById(position)
            var index = 0
            for (i in 0 until chipGroup.childCount) {
                val chip = chipGroup.getChildAt(i)
                if (chip == selectedChip) {
                    index = i
                    break
                }
            }
            Log.d("myTag", "chip index: $index")
            viewModel.setDateOnChipClicked(index)
            Toast.makeText(
                requireContext(),
                "Selected picture from ${selectedChip?.text}",
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