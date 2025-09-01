package com.hardik.hymnbook.presentation.ui

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.hardik.hymnbook.R
import com.hardik.hymnbook.common.Constants.BASE_TAG

class SettingsFragment : PreferenceFragmentCompat() {
    private val TAG = BASE_TAG + SettingsFragment::class.java.simpleName

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        // --- System UI --- Find the ListPreferences
        val listPreference = findPreference<ListPreference>("system_ui")

        // Set listener for preference changes
        listPreference?.setOnPreferenceChangeListener { preference, newValue ->
            val selectedValue = newValue as String

            // Save the preference value to SharedPreferences
            val sharedPreferences = context?.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
            val editor = sharedPreferences?.edit()
            editor?.putString("list_preference", selectedValue)
            editor?.apply()

            // Trigger recreation of MainActivity
            requireActivity().recreate()

            true // Return true to persist the change
        }

        // --- Index language --- Find the ListPreferences
        val listPreferenceLanguage = findPreference<ListPreference>("index_item_language")

        // Set listener for preference changes
        listPreferenceLanguage?.setOnPreferenceChangeListener { preference, newValue ->
            val selectedValue = newValue as String

            // Save the preference value to SharedPreferences
            val sharedPreferences = context?.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
            val editor = sharedPreferences?.edit()
            editor?.putString("index_item_language", selectedValue)
            editor?.apply()

            // Trigger recreation of MainActivity
            requireActivity().recreate()

            true // Return true to persist the change
        }


        // --- Drawer attachment --- Find the SwitchPreferenceCompat
        val drawerAttachmentSwitch = findPreference<SwitchPreferenceCompat>("drawer_attachment")

        drawerAttachmentSwitch?.setOnPreferenceChangeListener{
            preference, newValue ->
            val isChecked = newValue as Boolean

            // Save the preference value to SharedPreferences
            val sharedPreferences = context?.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
            val editor = sharedPreferences?.edit()
            editor?.putBoolean("drawer_attachment", isChecked)
            editor?.apply()

            true // Return true to persist the change
        }


        // --- History tracker --- Find the SwitchPreferenceCompat
        val historyTrackerSwitch = findPreference<SwitchPreferenceCompat>("history_tracker")

        // Set listener for preference changes
        historyTrackerSwitch?.setOnPreferenceChangeListener { preference, newValue ->
            val isChecked = newValue as Boolean

            // Save the preference value to SharedPreferences
            val sharedPreferences = context?.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
            val editor = sharedPreferences?.edit()
            editor?.putBoolean("history_tracker", isChecked)
            editor?.apply()

            true // Return true to persist the change
        }

        // --- Typing_mode switch ---
        val typingModeSwitch = findPreference<SwitchPreferenceCompat>("typing_mode")

        typingModeSwitch?.setOnPreferenceChangeListener { preference, newValue ->
            val isChecked = newValue as Boolean

            // Save manually if you want custom SharedPreferences
            val sharedPreferences = context?.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
            sharedPreferences?.edit()
                ?.putBoolean("typing_mode", isChecked)
                ?.apply()

            true // return true to let Preference framework also persist it
        }

        // --- Typing speed preference ---
        val typingSpeedPreference = findPreference<ListPreference>("typing_speed")

        typingSpeedPreference?.setOnPreferenceChangeListener { preference, newValue ->
            val selectedValue = newValue as String

            // Save manually if you want to keep using custom SharedPreferences
            val sharedPreferences = context?.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
            sharedPreferences?.edit()
                ?.putString("typing_speed", selectedValue)
                ?.apply()

            true // Return true to let Preference framework also persist it
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the fullscreenBackgroundColor from the theme
        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(R.attr.fullscreenBackgroundColor, typedValue, true)

        // Set the background color to the view
        view.setBackgroundColor(typedValue.data)
        // Directly set a hardcoded color for testing
//        view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.black_overlay)) // Replace with your test color

        // Background for whole fragment
        view.setBackgroundResource(R.drawable.background_open)
        // or: view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.my_color))

        // Padding for preference list (RecyclerView)
        val recyclerView = listView
        val padding = (16 * resources.displayMetrics.density).toInt()
        recyclerView.setPadding(padding, padding, padding, padding)
        recyclerView.clipToPadding = false
    }
}