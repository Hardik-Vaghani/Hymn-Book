package com.hardik.hymnbook.presentation.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.hardik.hymnbook.R
import com.hardik.hymnbook.common.Constants.BASE_TAG

class SettingsFragment : PreferenceFragmentCompat() {
    private val TAG = BASE_TAG + SettingsFragment::class.java.simpleName

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        // Find the ListPreferences
        val listPreference = findPreference<ListPreference>("system_ui")

        // Set listener for preference changes
        listPreference?.setOnPreferenceChangeListener { preference, newValue ->
            val selectedValue = newValue as String
            Log.d(TAG, "List preference changed to: $selectedValue")

            // Save the preference value to SharedPreferences
            val sharedPreferences = context?.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
            val editor = sharedPreferences?.edit()
            editor?.putString("list_preference", selectedValue)
            editor?.apply()

            // Trigger recreation of MainActivity
            requireActivity().recreate()

            true // Return true to persist the change
        }

        // Find the ListPreferences
        val listPreferenceLanguage = findPreference<ListPreference>("index_item_language")

        // Set listener for preference changes
        listPreferenceLanguage?.setOnPreferenceChangeListener { preference, newValue ->
            val selectedValue = newValue as String
            Log.d(TAG, "List preference language changed to: $selectedValue")

            // Save the preference value to SharedPreferences
            val sharedPreferences = context?.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
            val editor = sharedPreferences?.edit()
            editor?.putString("index_item_language", selectedValue)
            editor?.apply()

            // Trigger recreation of MainActivity
            requireActivity().recreate()

            true // Return true to persist the change
        }


        // Find the SwitchPreferenceCompat
        val drawerAttachmentSwitch = findPreference<SwitchPreferenceCompat>("drawer_attachment")

        drawerAttachmentSwitch?.setOnPreferenceChangeListener{
            preference, newValue ->
            val isChecked = newValue as Boolean
            Log.d(TAG, "Drawer attachment preference changed to: ${isChecked}")

            // Save the preference value to SharedPreferences
            val sharedPreferences = context?.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
            val editor = sharedPreferences?.edit()
            editor?.putBoolean("drawer_attachment", isChecked)
            editor?.apply()

            true // Return true to persist the change
        }


        // Find the SwitchPreferenceCompat
        val historyTrackerSwitch = findPreference<SwitchPreferenceCompat>("history_tracker")

        // Set listener for preference changes
        historyTrackerSwitch?.setOnPreferenceChangeListener { preference, newValue ->
            val isChecked = newValue as Boolean
            Log.d(TAG, "History tracker preference changed to: $isChecked")

            // Save the preference value to SharedPreferences
            val sharedPreferences = context?.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
            val editor = sharedPreferences?.edit()
            editor?.putBoolean("history_tracker", isChecked)
            editor?.apply()

            true // Return true to persist the change
        }
    }
}