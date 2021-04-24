package com.graphipuzzle.fragments

import android.R
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TableRow
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.graphipuzzle.PlayField
import com.graphipuzzle.PlayFieldSolver
import com.graphipuzzle.data.PlayFieldData
import com.graphipuzzle.data.TileData
import com.graphipuzzle.read.FieldSize
import com.graphipuzzle.read.PlayFieldDifficulty
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
		this.playField = PlayField(createEmptyPlayFieldData(10))
	}

	override fun onStart()
	{
		super.onStart()
		this.fragmentPlayFieldBinding.coloredTilesCounterText.visibility = View.INVISIBLE
		this.fragmentPlayFieldBinding.helpButton.visibility = View.INVISIBLE
		this.fragmentPlayFieldBinding.createButton.visibility = View.VISIBLE
		this.fragmentPlayFieldBinding.playFieldName.visibility = View.VISIBLE
		this.fragmentPlayFieldBinding.fieldSize.visibility = View.VISIBLE
		this.fragmentPlayFieldBinding.fieldSize.adapter = ArrayAdapter(
			requireContext(), R.layout.simple_spinner_item, FieldSize.values().map { fieldSize -> fieldSize.size }
		)
		this.fragmentPlayFieldBinding.fieldSize.setSelection(0, false)
		this.fragmentPlayFieldBinding.fieldSize.onItemSelectedListener = object : AdapterView.OnItemSelectedListener
		{
			override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long)
			{
				val selectedItem = parent?.selectedItem.toString()
				when
				{
					selectedItem.toInt() == 10 ->
					{
						this@PlayFieldCreateFragment.playField = PlayField(createEmptyPlayFieldData(10))
						this@PlayFieldCreateFragment.fragmentPlayFieldBinding.playFieldTable.removeAllViews()
						loadPlayField()
					}
					selectedItem.toInt() == 15 ->
					{
						this@PlayFieldCreateFragment.playField = PlayField(createEmptyPlayFieldData(15))
						this@PlayFieldCreateFragment.fragmentPlayFieldBinding.playFieldTable.removeAllViews()
						loadPlayField()
					}
					selectedItem.toInt() == 20 ->
					{
						this@PlayFieldCreateFragment.playField = PlayField(createEmptyPlayFieldData(20))
						this@PlayFieldCreateFragment.fragmentPlayFieldBinding.playFieldTable.removeAllViews()
						loadPlayField()
					}
				}

			}

			override fun onNothingSelected(p0: AdapterView<*>?)
			{
			}
		}
		this.fragmentPlayFieldBinding.difficulty.visibility = View.VISIBLE
		this.fragmentPlayFieldBinding.difficulty.adapter = ArrayAdapter(
			requireContext(), R.layout.simple_spinner_item, PlayFieldDifficulty.values()
		)
		this.fragmentPlayFieldBinding.createButton.setOnClickListener { createPlayField() }
		this.fragmentPlayFieldBinding.colorPickerButton.setOnClickListener { showColorPickerDialog() }
		this.fragmentPlayFieldBinding.tileColorSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
			if (isChecked)
			{
				colorPlayFieldTiles()
				this.fragmentPlayFieldBinding.colorPickerButton.visibility = View.INVISIBLE
			} else
			{
				colorPlayFieldTiles()
				this.fragmentPlayFieldBinding.colorPickerButton.visibility = View.VISIBLE
			}
		}
	}

	private fun createEmptyPlayFieldData(playfieldSize: Int): PlayFieldData
	{
		val tileValues: ArrayList<ArrayList<TileData>> = ArrayList(playfieldSize)

		for (i in 0 until playfieldSize)
		{
			tileValues.add(ArrayList(playfieldSize))
			for (j in 0 until playfieldSize)
			{
				val tileData = TileData(false, "")
				tileValues[i].add(tileData)
			}
		}

		return PlayFieldData("", PlayFieldDifficulty.EASY, tileValues)
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
		val colorPickerView = builder.colorPickerView
		colorPickerView.flagView = CustomFlag(requireContext(), com.graphipuzzle.R.layout.flag_color_picker)
		builder.show()
	}

	private fun createPlayField()
	{
		val alertDialog = AlertDialog.Builder(requireContext()).setTitle("Error creating play field")
		val fieldSize = this.playField.getFieldSize()
		val playFieldName = this.fragmentPlayFieldBinding.playFieldName.text.toString()
		val playFieldDifficulty = this.fragmentPlayFieldBinding.difficulty.selectedItem.toString()
		val tileDatas = this.playField.getTileDatas()
		val isSolveable = PlayFieldSolver().isSolveable(this.playField.getRowGroupStates(), this.playField.getColumnGroupStates())
		var playFieldData: PlayFieldData


		if (playFieldName == "")
		{
			alertDialog.setMessage("The play field name must not be empty!")
			alertDialog.show()
		} else if (playFieldDifficulty == null || PlayFieldDifficulty.values()
				.none { difficulty -> difficulty.name == playFieldDifficulty }
		)
		{
			alertDialog.setMessage("Invalid play field difficulty: $playFieldDifficulty")
			alertDialog.show()
		} else if (tileDatas.flatten().any { tileData -> tileData.hexColorCode == "" })
		{
			alertDialog.setMessage("All tiles must be painted!")
			alertDialog.show()
		} else if (!isSolveable && tileDatas.flatten().all { tileData -> !tileData.isPaintable })
		{
			alertDialog.setMessage("This play field is not solveable!")
			alertDialog.show()
		} else
		{
			playFieldData = PlayFieldData(playFieldName, PlayFieldDifficulty.valueOf(playFieldDifficulty), tileDatas)
			val UNDERSCORE_SEPARATOR = '_'
			val fileName =
				PlayFieldDifficulty.valueOf(playFieldDifficulty).difficulty.toLowerCase() + UNDERSCORE_SEPARATOR + fieldSize + UNDERSCORE_SEPARATOR + playFieldName.toLowerCase()
					.replace(' ', UNDERSCORE_SEPARATOR) + ".json"
			requireContext().openFileOutput(fileName, Context.MODE_PRIVATE)
				.use { it.write(Json.encodeToString(playFieldData).toByteArray()) }
			Toast.makeText(requireContext(), "Successfully created: $fileName", Toast.LENGTH_SHORT).show()
		}
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
				this.playField.setTileState(1, rowIndex, columnIndex)
				this.playField.setTileIsPaintable(rowIndex, columnIndex, true)
			} else if (this.firstTouchedButtonColor == Color.BLACK && actualFieldButtonColor == Color.BLACK)
			{
				v.backgroundTintList =
					ColorStateList.valueOf(ContextCompat.getColor(requireContext(), com.graphipuzzle.R.color.white))
				this.playField.setTileState(0, rowIndex, columnIndex)
				this.playField.setTileIsPaintable(rowIndex, columnIndex, false)
			}
		} else
		{
			v.backgroundTintList = ColorStateList.valueOf(tileColorSwitchUncheckedColor)
			val hexColorCode = String.format("#%06X", 0xFFFFFF and tileColorSwitchUncheckedColor)
			this.playField.setTileHexColorCode(rowIndex, columnIndex, hexColorCode)
		}
	}

	override fun colorPlayFieldTiles()
	{
		for (row in 0 until this.playField.getFieldSize())
		{
			val tableRow = this.fragmentPlayFieldBinding.playFieldTable
				.findViewWithTag<TableRow>(ROW_TAG_PREFIX + row)
			for (col in 0 until this.playField.getFieldSize())
			{
				val materialButton =
					tableRow.findViewWithTag<MaterialButton>(COLUMN_TAG_PREFIX + col)
				var originalColor = materialButton.backgroundTintList!!.getColorForState(
					intArrayOf(android.R.attr.state_enabled),
					0
				)

				var newColor: Int = if (this.fragmentPlayFieldBinding.tileColorSwitch.isChecked)
				{
					if (this.playField.getTileDatas()[row][col].isPaintable) Color.BLACK else Color.WHITE
				} else
				{
					val hexColorCode = this.playField.getTileDatas()[row][col].hexColorCode
					if (hexColorCode != "")
					{
						Color.parseColor(hexColorCode)
					} else
					{
						if (this.playField.getTileDatas()[row][col].isPaintable) Color.BLACK else Color.WHITE
					}
				}

				val colorAnimation =
					ValueAnimator.ofObject(ArgbEvaluator(), originalColor, newColor)
				colorAnimation.duration = 1000L

				colorAnimation.addUpdateListener { animator ->
					materialButton.backgroundTintList = ColorStateList.valueOf(animator.animatedValue as Int)
				}
				colorAnimation.start()
			}
		}
	}
}