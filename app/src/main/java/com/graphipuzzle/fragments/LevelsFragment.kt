package com.graphipuzzle.fragments

import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.GridLayoutManager
import com.graphipuzzle.PlayField
import com.graphipuzzle.R
import com.graphipuzzle.databinding.FragmentLevelsBinding
import com.graphipuzzle.read.LevelPack
import com.graphipuzzle.read.ReadPlayField
import com.graphipuzzle.util.SoundPoolUtil
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

const val LEVEL_PACK = "levelPack"

class LevelsFragment : Fragment()
{
	private lateinit var levelsBinding: FragmentLevelsBinding
	private lateinit var levelPack: LevelPack

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		arguments?.let {
			levelPack = LevelPack.valueOf(it.getString(LEVEL_PACK)!!)
		}
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View?
	{
		this.levelsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_levels, container, false)

		setHasOptionsMenu(true)

		val levelsAdapter = LevelsAdapter(requireContext(), this.levelPack)
		this.levelsBinding.levelsRecyclerView.adapter = levelsAdapter
		this.levelsBinding.levelsRecyclerView.layoutManager =
			GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)

		// Inflate the layout for this fragment
		return levelsBinding.root
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