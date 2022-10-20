package com.rodionovmax.materialnasa.ui.pod

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.rodionovmax.materialnasa.R
import com.rodionovmax.materialnasa.app
import com.rodionovmax.materialnasa.data.model.Pod
import com.rodionovmax.materialnasa.databinding.FragmentPodBinding


class PodFragment : Fragment() {

    private var _binding: FragmentPodBinding? = null
    private val binding get() = _binding!!
    val viewModel: PodViewModel by viewModels { PodViewModelFactory(this, app.remoteRepo, app.localRepo) }
    val prefs: SharedPreferences by lazy { requireActivity().getSharedPreferences("pref", Context.MODE_PRIVATE) }
    private lateinit var title: CharSequence

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

    override fun onResume() {
        super.onResume()

        title = viewModel.title.value.toString()
        val fgColor = viewModel.foregroundColor.value
        val bgColor = viewModel.backgroundColor.value
        if (fgColor != null && bgColor != null) {
            getColorsFromViewModel(fgColor, bgColor)
        }
    }

    override fun onStop() {
        super.onStop()

        viewModel.saveTitle(binding.podPictureTitle.text)
    }

    private fun initViews() {
        showProgress(false)
        selectPod()
        addToGallery()
        highlightText()
    }

    private fun highlightText() {
        binding.highlightTextSwitch.setOnCheckedChangeListener { button, isChecked ->
            if (isChecked) {
                backgroundColorOn()
            } else {
                backgroundColorOff()
            }
        }
    }

    private fun backgroundColorOn() {
        val spannableTitle = SpannableString(binding.podPictureTitle.text)
        spannableTitle.setSpan(
            ForegroundColorSpan(Color.BLUE),
            0, binding.podPictureTitle.text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableTitle.setSpan(
            BackgroundColorSpan(Color.YELLOW),
            0, binding.podPictureTitle.text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.podPictureTitle.text = spannableTitle
        // save colors to ViewModel
        viewModel.setForegroundColor(Color.BLUE)
        viewModel.setBackgroundColor(Color.YELLOW)
    }

    private fun backgroundColorOff() {
        val spannableTitle = SpannableString(binding.podPictureTitle.text)
        spannableTitle.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.grey)),
            0, binding.podPictureTitle.text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableTitle.setSpan(
            BackgroundColorSpan(Color.TRANSPARENT),
            0, binding.podPictureTitle.text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.podPictureTitle.text = spannableTitle
        // save colors to ViewModel
        viewModel.setForegroundColor(resources.getColor(R.color.grey))
        viewModel.setBackgroundColor(Color.TRANSPARENT)
    }

    private fun getColorsFromViewModel(foregroundColor: Int, backgroundColor: Int) {
        val spannableTitle = SpannableString(title)
        spannableTitle.setSpan(
            ForegroundColorSpan(foregroundColor),
            0, title.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableTitle.setSpan(
            BackgroundColorSpan(backgroundColor),
            0, title.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.podPictureTitle.text = spannableTitle
    }

    private fun initViewModel() {
        viewModel.progressLiveData.observe(viewLifecycleOwner) { showProgress(it) }
        viewModel.podLiveData.observe(viewLifecycleOwner, Observer<Pod> { showPod(it) })
        viewModel.errorLiveData.observe(viewLifecycleOwner) { showError(it) }
        lifecycleScope.launchWhenStarted {
            viewModel.eventFlow.collect { event ->
                when(event) {
                    is PodViewModel.PodEvent.PodToast -> {
                        Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun selectPod() {
        /*binding.chipGroup.setOnCheckedChangeListener { chipGroup: ChipGroup, position: Int ->
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
            viewModel.triggerEvent()
        }*/

        binding.chipGroup.setOnCheckedStateChangeListener { chipGroup, checkedIds ->
            val selectedChip: Chip? = binding.chipGroup.findViewById(checkedIds[0])
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
            viewModel.triggerEvent(selectedChip?.text as String)
            // dismiss switch toggle when new pod is clicked
            binding.highlightTextSwitch.isChecked = false
        }
    }

    private fun addToGallery() {
        binding.addToGalleryBtn.setOnClickListener {
            val currentPod: Pod? = viewModel.podLiveData.value
            currentPod?.let {
                viewModel.savePodToGallery(currentPod)
                Toast.makeText(requireContext(), "Picture saved to Gallery", Toast.LENGTH_SHORT).show()
                binding.addToGalleryBtn.isVisible = false
            }
        }
    }

    private fun showPod(pod: Pod) {
        Glide.with(requireContext()).load(pod.url).into(binding.podImage)
        binding.podPictureTitle.text = pod.title
        binding.podPictureDescription.text = pod.description
        binding.addToGalleryBtn.isVisible = !pod.isSaved
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