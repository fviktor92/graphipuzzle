package com.graphipuzzle.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.graphipuzzle.R

val KEY_SOUND = "sound"

class SettingsFragment : PreferenceFragmentCompat()
{
	override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?)
	{
		setPreferencesFromResource(R.xml.root_preferences, rootKey)
	}
}