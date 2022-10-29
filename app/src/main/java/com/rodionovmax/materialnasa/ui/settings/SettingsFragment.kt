package com.rodionovmax.materialnasa.ui.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.rodionovmax.materialnasa.databinding.FragmentSettingsBinding

const val SHARED_PREFS = "settings"
const val IS_EXTERNAL_STORAGE = "external"

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by lazy { SettingsViewModel() }
    private val prefs: SharedPreferences by lazy { requireActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE) }
//    var isFragmentResumed = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.photoStorageSwitch.isChecked = prefs.getBoolean(IS_EXTERNAL_STORAGE, true)

        binding.photoStorageSwitch.setOnCheckedChangeListener { _, toggleValue ->
//            if (savedInstanceState == null) {
//                isFragmentResumed = false
//            }
//            if (!isFragmentResumed) {
//                writeToSharedPrefs(toggleValue)
//            }
            writeToSharedPrefs(toggleValue)
        }
    }

    private fun writeToSharedPrefs(toggleValue: Boolean) {
        prefs.edit().apply {
            putBoolean(IS_EXTERNAL_STORAGE, toggleValue)
            apply()
        }
//        isFragmentResumed = false
    }

//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        outState.putBoolean(IS_EXTERNAL_STORAGE, binding.photoStorageSwitch.isChecked)
//    }
//
//    override fun onViewStateRestored(savedInstanceState: Bundle?) {
//        super.onViewStateRestored(savedInstanceState)
//        if (savedInstanceState != null) {
//            binding.photoStorageSwitch.isChecked =
//                savedInstanceState.getBoolean(IS_EXTERNAL_STORAGE, false)
//        }
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}