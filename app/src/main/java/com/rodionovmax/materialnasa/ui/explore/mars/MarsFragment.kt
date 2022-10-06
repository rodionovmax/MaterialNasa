package com.rodionovmax.materialnasa.ui.explore.mars

import android.app.DatePickerDialog
import android.graphics.Camera
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.rodionovmax.materialnasa.R
import com.rodionovmax.materialnasa.app
import com.rodionovmax.materialnasa.databinding.FragmentMarsBinding
import java.text.SimpleDateFormat
import java.util.*


class MarsFragment : Fragment() {

    private var _binding: FragmentMarsBinding? = null
    private val binding get() = _binding!!

    private val adapter = MarsAdapter()
    private val viewModel by lazy { MarsViewModel(app.fetchMarsPhotosUseCase) }
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

    private fun initViews() {
        initRecyclerView()
        setListOfCameras()
        selectCamera()
        selectDate()
    }

    private fun observeViewModel() {

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
            viewModel.selectCamera(camera!!)
        }
        Toast.makeText(requireContext(), "$camera was selected", Toast.LENGTH_SHORT).show()
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
        }
        materialDatePicker.show(childFragmentManager, "tag")
    }

    private val outputDateFormat = SimpleDateFormat("yyyy-mm-dd", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    private fun fetchPhotosFromMars(camera: String, date: String) {
        viewModel.fetchMarsPhotos(camera, date)
    }

    fun onCameraSelected() {

    }


}