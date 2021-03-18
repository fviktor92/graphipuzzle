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
import androidx.recyclerview.widget.LinearLayoutManager
import com.graphipuzzle.PlayField
import com.graphipuzzle.R
import com.graphipuzzle.databinding.FragmentLevelPacksBinding
import com.graphipuzzle.read.LevelPack
import com.graphipuzzle.read.ReadPlayField
import com.graphipuzzle.util.SoundPoolUtil
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

const val LEVEL_PACKS_FRAGMENT = "level_packs_fragment"

class LevelPacksFragment : Fragment(R.layout.fragment_level_packs)
{
	private lateinit var levelChooserBinding: FragmentLevelPacksBinding
	private var playFieldJson: String = ""

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View?
	{
		this.levelChooserBinding =
			DataBindingUtil.inflate(inflater, R.layout.fragment_level_packs, container, false)

		setHasOptionsMenu(true)

		val levelPacksAdapter = LevelPacksAdapter(requireContext(), LevelPack.values())
		this.levelChooserBinding.levelPacksRecyclerView.adapter = levelPacksAdapter
		this.levelChooserBinding.levelPacksRecyclerView.layoutManager =
			LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true)
		// Inflate the layout for this fragment
		return levelChooserBinding.root
	}

	override fun onResume()
	{
		super.onResume()
		val continuePlayFieldJson = PreferenceManager.getDefaultSharedPreferences(requireActivity()).getString(PLAY_FIELD, "")!!
		if (continuePlayFieldJson != "")
		{
			this.playFieldJson = continuePlayFieldJson
			val bundle = bundleOf(PLAY_FIELD to this.playFieldJson)
			this.levelChooserBinding.continueGame.visibility = View.VISIBLE
			this.levelChooserBinding.continueGame.setOnClickListener { view: View ->
				SoundPoolUtil.getInstance(requireContext()).playSound(R.raw.button_sound)
				view.findNavController().navigate(R.id.action_levelPacksFragment_to_levelsFragment, bundle)
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
}