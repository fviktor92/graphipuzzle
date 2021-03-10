package com.graphipuzzle.fragments

import android.R
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.graphipuzzle.PlayField
import com.graphipuzzle.data.PlayFieldData
import com.graphipuzzle.read.LevelPack
import com.graphipuzzle.read.PlayFieldDifficulty
import com.graphipuzzle.read.ReadPlayField
import com.graphipuzzle.util.SoundPoolUtil
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.lang.String


class PlayFieldCreateFragment : PlayFieldFragment()
{
	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		this.playField = PlayField(ReadPlayField(requireActivity(), LevelPack.EMPTY, "empty_10.json").getPlayFieldData())
	}

	override fun onStart()
	{
		super.onStart()
		this.fragmentPlayFieldBinding.coloredTilesCounterText.visibility = View.INVISIBLE
		this.fragmentPlayFieldBinding.helpButton.visibility = View.INVISIBLE
		this.fragmentPlayFieldBinding.colorPickerButton.visibility = View.VISIBLE
		this.fragmentPlayFieldBinding.createButton.visibility = View.VISIBLE
		this.fragmentPlayFieldBinding.playFieldName.visibility = View.VISIBLE
		this.fragmentPlayFieldBinding.difficulty.visibility = View.VISIBLE
		this.fragmentPlayFieldBinding.difficulty.adapter = ArrayAdapter(
			requireContext(), R.layout.simple_spinner_item, PlayFieldDifficulty.values()
		)
		this.fragmentPlayFieldBinding.createButton.setOnClickListener { createPlayField() }
		this.fragmentPlayFieldBinding.colorPickerButton.setOnClickListener { showColorPickerDialog() }
		this.fragmentPlayFieldBinding.tileColorSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
			if (isChecked)
			{
				colorPlayFieldTiles(false)
			} else
			{
				colorPlayFieldTiles(true)
			}
		}
	}

	private fun showColorPickerDialog()
	{
		val builder: ColorPickerDialog.Builder =
			ColorPickerDialog.Builder(requireContext(), R.style.Theme_DeviceDefault_Dialog_Alert)
		builder.setTitle("ColorPicker Dialog")
		builder.setPreferenceName("MyColorPickerDialog")
		builder.setPositiveButton("Confirm", object : ColorEnvelopeListener
		{
			override fun onColorSelected(envelope: ColorEnvelope?, fromUser: Boolean)
			{
				ColorStateList.valueOf(ContextCompat.getColor(requireContext(), com.graphipuzzle.R.color.black))
				fragmentPlayFieldBinding.tileColorSwitch.thumbTintList = ColorStateList(
					arrayOf(
						intArrayOf(-R.attr.state_enabled),
						intArrayOf(R.attr.state_checked),
						intArrayOf()
					),
					intArrayOf(
						envelope!!.color,
						Color.BLACK,
						envelope!!.color
					)
				)
			}
		})
		builder.setNegativeButton("Cancel") { dialogInterface, i -> dialogInterface.dismiss() }
		builder.attachAlphaSlideBar(false)
		builder.show()
	}

	private fun createPlayField()
	{
		val fieldSize = this.playField.getFieldSize()
		val playFieldName = this.fragmentPlayFieldBinding.playFieldName.text.toString()
		val playFieldDifficulty: PlayFieldDifficulty =
			PlayFieldDifficulty.valueOf(this.fragmentPlayFieldBinding.difficulty.selectedItem.toString())

		val playFieldData = PlayFieldData(playFieldName, playFieldDifficulty, this.playField.getTileDatas())
		val UNDERSCORE_SEPARATOR = '_'
		val fileName =
			playFieldDifficulty.difficulty.toLowerCase() + UNDERSCORE_SEPARATOR + fieldSize + UNDERSCORE_SEPARATOR + playFieldName.toLowerCase()
				.replace(' ', UNDERSCORE_SEPARATOR) + ".json"
		requireContext().openFileOutput(fileName, Context.MODE_PRIVATE)
			.use { it.write(Json.encodeToString(playFieldData).toByteArray()) }
		Toast.makeText(requireContext(), "Successfully created: $fileName", Toast.LENGTH_SHORT)
	}

	override fun performOnTileTouchActions(rowIndex: Int, columnIndex: Int)
	{
		// Play a click sound
		SoundPoolUtil.getInstance(requireContext()).playSound(com.graphipuzzle.R.raw.tile_paint_sound)
	}

	override fun setFieldButtonColor(v: View, rowIndex: Int, columnIndex: Int)
	{
		val actualFieldButtonColor = v.backgroundTintList!!.getColorForState(intArrayOf(R.attr.state_enabled), 0)
		val tileColorSwitchCheckedColor = fragmentPlayFieldBinding.tileColorSwitch.thumbTintList!!.getColorForState(
			intArrayOf(R.attr.state_checked), Color.BLACK
		)
		val tileColorSwitchUncheckedColor = fragmentPlayFieldBinding.tileColorSwitch.thumbTintList!!.getColorForState(
			intArrayOf(-R.attr.state_checked), 0
		)

		if (this.fragmentPlayFieldBinding.tileColorSwitch.isChecked)
		{
			if (this.firstTouchedButtonColor == Color.WHITE || firstTouchedButtonColor == Color.TRANSPARENT)
			{
				v.backgroundTintList = ColorStateList.valueOf(tileColorSwitchCheckedColor)
				this.playField.setTileIsPaintable(rowIndex, columnIndex, true)
			} else if (this.firstTouchedButtonColor == Color.BLACK && actualFieldButtonColor == Color.BLACK)
			{
				v.backgroundTintList =
					ColorStateList.valueOf(ContextCompat.getColor(requireContext(), com.graphipuzzle.R.color.white))
				this.playField.setTileIsPaintable(rowIndex, columnIndex, false)
			}
		} else
		{
			v.backgroundTintList = ColorStateList.valueOf(tileColorSwitchUncheckedColor)
			val hexColorCode = String.format("#%06X", 0xFFFFFF and tileColorSwitchUncheckedColor)
			this.playField.setTileHexColorCode(rowIndex, columnIndex, hexColorCode)
		}
	}
}