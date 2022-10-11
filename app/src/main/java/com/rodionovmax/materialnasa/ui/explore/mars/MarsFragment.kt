package com.rodionovmax.materialnasa.ui.explore.mars

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.rodionovmax.materialnasa.R
import com.rodionovmax.materialnasa.app
import com.rodionovmax.materialnasa.databinding.FragmentMarsBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class MarsFragment : Fragment() {

    private var _binding: FragmentMarsBinding? = null
    private val binding get() = _binding!!

    private val adapter = MarsAdapter()
    val viewModel: MarsViewModel by viewModels { MarsViewModelFactory(this, app.fetchMarsPhotosUseCase) }
    private var camera: String? = null
    private var selectedDate: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMarsBinding.inflate(inflater, container, false)

        initViews()
        observeViewModel()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        // to keep exposed dropdown menu after screen rotation
        setListOfCameras()
        // restore camera and date after fragment recreated
        camera = viewModel.cameraState.value
        selectedDate = viewModel.dateState.value
    }

    private fun initViews() {
        initRecyclerView()
        selectCamera()
        selectDate()
    }

    private fun initRecyclerView() {
        binding.marsRecyclerview.layoutManager = GridLayoutManager(context, 2)
        binding.marsRecyclerview.adapter = adapter
    }

    private fun setListOfCameras() {
        val cameraList = resources.getStringArray(R.array.cameras)
        val camerasArrayAdapter =
            ArrayAdapter(requireContext(), R.layout.camera_dropdown_item, cameraList)
        binding.tvCamera.setAdapter(camerasArrayAdapter)
    }

    private fun selectCamera() {
        binding.tvCamera.setOnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
            when (position) {
                0 -> camera = "ALL"
                1 -> camera = "FHAZ"
                2 -> camera = "RHAZ"
                3 -> camera = "MAST"
                4 -> camera = "CHEMCAM"
                5 -> camera = "MAHLI"
                6 -> camera = "MARDI"
                7 -> camera = "NAVCAM"
                8 -> camera = "PANCAM"
                9 -> camera = "MINITES"
                else -> throw ArrayIndexOutOfBoundsException("Index of the camera clicked in the dropdown is out of bound")
            }
            camera?.let { viewModel.setCamera(it) }
            onCameraSelected()
        }
        Toast.makeText(
            requireContext(),
            "${viewModel.cameraState.value} was selected",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun selectDate() {
        binding.datePicker.setOnClickListener {
            showDatePickerDialog()
        }
    }

    private fun showDatePickerDialog() {
        val maxDate = MaterialDatePicker.todayInUtcMilliseconds()
        val constraints = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointBackward.before(maxDate))
            .build()

        val materialDatePicker = MaterialDatePicker.Builder
            .datePicker()
            .setTitleText("Select Date")
            .setCalendarConstraints(constraints)
            .build()

        materialDatePicker.addOnPositiveButtonClickListener {
            binding.calendarDropdown.editText?.setText(materialDatePicker.headerText)
            selectedDate = outputDateFormat.format(it)

            selectedDate?.let { date -> viewModel.setDate(date) }
        }
        materialDatePicker.show(childFragmentManager, "tag")
    }

    private val outputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    private fun fetchPhotosFromMars() {
        viewModel.fetchMarsPhotos()
    }

    private fun onCameraSelected() {
        if (selectedDate == null) {
            Toast.makeText(requireContext(), "Select date first!", Toast.LENGTH_SHORT).show()
        } else {
            camera?.let { camera -> fetchPhotosFromMars() }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    when (state) {
                        is MarsUiState.Empty -> {
                            binding.marsFragmentLoader.visibility = View.GONE
                            binding.marsRecyclerview.visibility = View.GONE
                        }
                        is MarsUiState.Success -> {
                            binding.marsFragmentLoader.visibility = View.GONE
                            binding.marsRecyclerview.visibility = View.VISIBLE
                            adapter.setData(state.data)
                        }
                        is MarsUiState.Error -> {
                            binding.marsFragmentLoader.visibility = View.GONE
                            binding.marsRecyclerview.visibility = View.GONE
                            Toast.makeText(
                                requireActivity(),
                                state.error.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        is MarsUiState.Loading -> {
                            binding.marsFragmentLoader.visibility = View.VISIBLE
                            binding.marsRecyclerview.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }


}

