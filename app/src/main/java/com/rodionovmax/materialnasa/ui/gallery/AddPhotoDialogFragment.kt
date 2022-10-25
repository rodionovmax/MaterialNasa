package com.rodionovmax.materialnasa.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.rodionovmax.materialnasa.databinding.FragmentAddPhotoDialogBinding

// example of dialog fragment that called in gallery
class AddPhotoDialogFragment : DialogFragment() {

    private var _binding: FragmentAddPhotoDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddPhotoDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cameraButton.setOnClickListener {
            Toast.makeText(requireContext(), "Going to camera app...", Toast.LENGTH_SHORT).show()
        }

        binding.libraryButton.setOnClickListener {
            Toast.makeText(requireContext(), "Going to photo gallery...", Toast.LENGTH_SHORT).show()
        }
    }
}