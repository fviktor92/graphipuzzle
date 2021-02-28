package com.graphipuzzle.fragments

import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.preference.PreferenceManager
import com.graphipuzzle.PlayField
import com.graphipuzzle.R
import com.graphipuzzle.databinding.FragmentLevelChooserBinding
import com.graphipuzzle.read.LevelPack
import com.graphipuzzle.read.ReadPlayField
import com.graphipuzzle.util.SoundPoolUtil
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

const val LEVEL_CHOOSER_FRAGMENT = "level_chooser_fragment"

class LevelChooserFragment : Fragment(R.layout.fragment_level_chooser)
{
	private lateinit var levelChooserBinding: FragmentLevelChooserBinding
	private var playFieldJson: String = ""

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View?
	{
		levelChooserBinding =
			DataBindingUtil.inflate(inflater, R.layout.fragment_level_chooser, container, false)

		setHasOptionsMenu(true)

		// Inflate the layout for this fragment
		return levelChooserBinding.root
	}

	override fun onResume()
	{
		super.onResume()
		this.levelChooserBinding.startSmallGame.setOnClickListener { view: View ->
			startLevelSetOnClickListener(view, LevelPack.VEHICLES, "easy_10_heart.json")
		}
		val continuePlayFieldJson = PreferenceManager.getDefaultSharedPreferences(requireActivity()).getString(PLAY_FIELD, "")!!
		if (continuePlayFieldJson != "")
		{
			this.playFieldJson = continuePlayFieldJson
			val bundle = bundleOf(PLAY_FIELD to this.playFieldJson)
			this.levelChooserBinding.continueGame.visibility = View.VISIBLE
			this.levelChooserBinding.continueGame.setOnClickListener { view: View ->
				SoundPoolUtil.getInstance(requireContext()).playSound(R.raw.button_sound)
				view.findNavController().navigate(R.id.action_levelChooserFragment_to_playFieldFragment, bundle)
			}
		}
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater)
	{
		super.onCreateOptionsMenu(menu, inflater)
		inflater?.inflate(R.menu.navigation_bar_menu, menu)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean
	{
		return NavigationUI.onNavDestinationSelected(item!!, this.findNavController()) || super.onOptionsItemSelected(item)
	}

	private fun startLevelSetOnClickListener(
		view: View,
		levelPack: LevelPack,
		playFieldFileName: String,
	)
	{
		SoundPoolUtil.getInstance(requireContext()).playSound(R.raw.button_sound)
		this.playFieldJson =
			Json.encodeToString(PlayField(ReadPlayField(requireContext(), levelPack, playFieldFileName).getPlayFieldData()))
		val bundle = bundleOf(PLAY_FIELD to this.playFieldJson)
		view.findNavController().navigate(R.id.action_levelChooserFragment_to_playFieldFragment, bundle)
	}
}