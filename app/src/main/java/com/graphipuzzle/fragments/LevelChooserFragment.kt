package com.graphipuzzle.fragments

import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.graphipuzzle.PlayField
import com.graphipuzzle.R
import com.graphipuzzle.databinding.FragmentLevelChooserBinding
import com.graphipuzzle.read.PlayFieldLevel
import com.graphipuzzle.read.ReadPlayField
import com.graphipuzzle.util.SoundPoolUtil
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

const val LEVEL_CHOOSER_FRAGMENT = "level_chooser_fragment"

/**
 * A simple [Fragment] subclass.
 * Use the [LevelChooserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LevelChooserFragment : Fragment()
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

		this.levelChooserBinding.startSmallGame.setOnClickListener { view: View ->
			startLevelSetOnClickListener(view, PlayFieldLevel.EASY, "easy_10_10_sailboat.json", savedInstanceState)
		}
		this.levelChooserBinding.startBigGame.setOnClickListener { view: View ->
			startLevelSetOnClickListener(
				view,
				PlayFieldLevel.HARD,
				"hard_15_15_dog_and_boy_playing_ball.json",
				savedInstanceState
			)
		}

		setHasOptionsMenu(true)

		// Inflate the layout for this fragment
		return levelChooserBinding.root
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
		playFieldLevel: PlayFieldLevel,
		playFieldFileName: String,
		savedInstanceState: Bundle?
	)
	{
		SoundPoolUtil.getInstance(requireContext()).playSound(R.raw.button_sound)
		this.playFieldJson =
			Json.encodeToString(PlayField(ReadPlayField(requireContext(), playFieldLevel, playFieldFileName).getPlayFieldData()))
		val bundle = bundleOf(PLAY_FIELD to this.playFieldJson)
		view.findNavController().navigate(R.id.action_levelChooserFragment_to_playFieldFragment, bundle)
	}

	companion object
	{
		/**
		 * Use this factory method to create a new instance of
		 * this fragment using the provided parameters.

		 * @return A new instance of fragment LevelChooserFragment.
		 */
		@JvmStatic
		fun newInstance() =
			LevelChooserFragment().apply {
				arguments = Bundle().apply {

				}
			}
	}
}