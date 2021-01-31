package com.graphipuzzle.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.graphipuzzle.PlayField
import com.graphipuzzle.R
import com.graphipuzzle.databinding.FragmentLevelChooserBinding
import com.graphipuzzle.read.PlayFieldLevel
import com.graphipuzzle.read.ReadPlayField
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

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View?
	{
		levelChooserBinding =
			DataBindingUtil.inflate(inflater, R.layout.fragment_level_chooser, container, false)

		this.levelChooserBinding.startSmallGame.setOnClickListener {
			this.playFieldJson = Json.encodeToString(
				PlayField(
					ReadPlayField(
						requireContext(), PlayFieldLevel.EASY, "easy_10_10_sailboat.json"
					).getPlayFieldData()
				)
			)
			beginPlayFieldFragmentTransaction(savedInstanceState)
		}
		this.levelChooserBinding.startBigGame.setOnClickListener {
			Log.d("LevelChooserFragment", "kaka")
			this.playFieldJson = Json.encodeToString(
				PlayField(
					ReadPlayField(
						requireContext(),
						PlayFieldLevel.HARD,
						"hard_15_15_dog_and_boy_playing_ball.json"
					).getPlayFieldData()
				)
			)
			beginPlayFieldFragmentTransaction(savedInstanceState)
		}
		// Inflate the layout for this fragment
		return levelChooserBinding.root
	}

	private fun beginPlayFieldFragmentTransaction(savedInstanceState: Bundle?)
	{
		if (savedInstanceState == null)
		{
			val bundle = bundleOf(PLAY_FIELD to this@LevelChooserFragment.playFieldJson)
			parentFragmentManager.commit {
				setReorderingAllowed(true)
				replace(
					R.id.content_fragment_container_view,
					PlayFieldFragment::class.java,
					bundle
				)
				addToBackStack(LEVEL_CHOOSER_FRAGMENT)
			}
		}
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