package com.graphipuzzle.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.graphipuzzle.PlayField
import com.graphipuzzle.data.PlayFieldData
import com.graphipuzzle.data.TileData
import com.graphipuzzle.read.LevelPack
import com.graphipuzzle.read.PlayFieldDifficulty
import com.graphipuzzle.read.ReadPlayField
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class PlayFieldCreateFragment : PlayFieldFragment()
{
	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		super.playField = PlayField(ReadPlayField(requireActivity(), LevelPack.EMPTY, "empty_10.json").getPlayFieldData())
	}

	override fun onStart()
	{
		super.onStart()
		super.fragmentPlayFieldBinding.coloredTilesCounterText.visibility = View.INVISIBLE
		super.fragmentPlayFieldBinding.createButton.visibility = View.VISIBLE
		super.fragmentPlayFieldBinding.playFieldName.visibility = View.VISIBLE
		super.fragmentPlayFieldBinding.difficulty.visibility = View.VISIBLE
		super.fragmentPlayFieldBinding.difficulty.adapter = ArrayAdapter(
			requireContext(), android.R.layout.simple_spinner_item, PlayFieldDifficulty.values()
		)
		super.fragmentPlayFieldBinding.createButton.setOnClickListener { createPlayField() }
	}

	fun createPlayField()
	{
		val fieldSize = super.playField.getFieldSize()
		val playFieldName = super.fragmentPlayFieldBinding.playFieldName.text.toString()
		val playFieldDifficulty: PlayFieldDifficulty =
			PlayFieldDifficulty.valueOf(super.fragmentPlayFieldBinding.difficulty.selectedItem.toString())
		val tileValues: ArrayList<ArrayList<TileData>> = ArrayList(fieldSize)

		for (row in 0 until fieldSize)
		{
			tileValues.add(ArrayList(fieldSize))
			for (col in 0 until fieldSize)
			{
				val isPaintable = super.playField.getTileState(row, col) == 1
				val hexColorCode = if (isPaintable) "#000000" else "#ffffff"
				val tileData = TileData(isPaintable, hexColorCode)
				tileValues[row].add(tileData)
			}
		}

		val playFieldData = PlayFieldData(playFieldName, playFieldDifficulty, tileValues)
		val UNDERSCORE_SEPARATOR = '_'
		val fileName =
			playFieldDifficulty.difficulty.toLowerCase() + UNDERSCORE_SEPARATOR + fieldSize + UNDERSCORE_SEPARATOR + playFieldName.toLowerCase()
				.replace(' ', UNDERSCORE_SEPARATOR) + ".json"
		requireContext().openFileOutput(fileName, Context.MODE_PRIVATE)
			.use { it.write(Json.encodeToString(playFieldData).toByteArray()) }
		Toast.makeText(requireContext(), "Successfully created: $fileName", Toast.LENGTH_SHORT)
	}
}