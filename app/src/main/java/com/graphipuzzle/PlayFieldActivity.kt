package com.graphipuzzle

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setMargins
import androidx.databinding.DataBindingUtil
import com.google.android.material.button.MaterialButton
import com.graphipuzzle.data.FieldData
import com.graphipuzzle.databinding.ActivityPlayFieldBinding
import com.graphipuzzle.databinding.FragmentPlayFieldBinding
import com.graphipuzzle.playfieldfragments.PlayFieldFragment
import com.graphipuzzle.read.PlayFieldSize
import com.graphipuzzle.read.ReadPlayField

class PlayFieldActivity : AppCompatActivity()
{
	private lateinit var activityPlayFieldBinding: ActivityPlayFieldBinding
	private lateinit var fragmentPlayFieldBinding: FragmentPlayFieldBinding

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		activityPlayFieldBinding =
			DataBindingUtil.setContentView(this, R.layout.activity_play_field)
		fragmentPlayFieldBinding =
			DataBindingUtil.setContentView(this, R.layout.fragment_play_field)

		val playField =
			PlayField(ReadPlayField(this, PlayFieldSize.BIG, "level_1.json").getPlayFieldData())

		val playFieldFragment =
			PlayFieldFragment.newInstance(playField, R.id.play_field_fragment)
		supportFragmentManager.beginTransaction()
			.add(activityPlayFieldBinding.playFieldFragmentContainerView.id, playFieldFragment)

		activityPlayFieldBinding.apply {
			initializePlayFieldTables(playField, fragmentPlayFieldBinding)
			invalidateAll() // Refresh the UI with the new data, invalidating all binding expressions so that they get recreated with the correct data
		}
	}

	private fun initializePlayFieldTables(
		playField: PlayField,
		fragmentPlayFieldBinding: FragmentPlayFieldBinding
	)
	{
		initializePlayFieldColumnValuesTable(
			playField,
			fragmentPlayFieldBinding.playFieldColumnValuesTable
		)
		initializePlayFieldRowValuesTable(
			playField,
			fragmentPlayFieldBinding.playFieldRowValuesTable
		)
		initializePlayFieldTable(playField, fragmentPlayFieldBinding.playFieldTable)
	}

	/**
	 * Initializes the top [TableLayout] that is supposed to contain the column group values.
	 */
	private fun initializePlayFieldColumnValuesTable(
		playField: PlayField,
		columnValuesTableLayout: TableLayout
	)
	{
		val playFieldColumnValuesTable: TableLayout = columnValuesTableLayout
		val fieldColumns = playField.getFieldColumns()
		val row = TableRow(this)
		row.layoutParams = TableLayout.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT)

		for (columnIndex in fieldColumns.indices)
		{
			val columnValues: MutableList<Int> = fieldColumns[columnIndex]
			val columnValueText = TextView(this)
			columnValueText.layoutParams = TableRow.LayoutParams(
				TableRow.LayoutParams.WRAP_CONTENT,
				TableRow.LayoutParams.MATCH_PARENT, 1.0f
			)
			columnValueText.gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
			columnValueText.textAlignment = TextView.TEXT_ALIGNMENT_GRAVITY
			columnValueText.setTextColor(Color.BLACK)
			columnValueText.textSize = 10.0f
			columnValueText.text = columnValues.joinToString(separator = System.lineSeparator())
			row.addView(columnValueText)
		}

		playFieldColumnValuesTable.addView(row)
	}


	/**
	 * Initializes the left [TableLayout] that is supposed to contain the row group values.
	 */
	private fun initializePlayFieldRowValuesTable(
		playField: PlayField,
		rowValuesTableLayout: TableLayout
	)
	{
		val playFieldRowValuesTable: TableLayout = rowValuesTableLayout
		val fieldRows = playField.getFieldRows()

		for (rowIndex in fieldRows.indices)
		{
			val rowValues: MutableList<Int> = fieldRows[rowIndex]
			val row = TableRow(this)
			row.layoutParams = TableLayout.LayoutParams(
				0,
				TableLayout.LayoutParams.WRAP_CONTENT,
				1.0f
			)

			val rowValueText = TextView(this)
			rowValueText.layoutParams = TableRow.LayoutParams(
				TableRow.LayoutParams.WRAP_CONTENT,
				TableRow.LayoutParams.MATCH_PARENT, 1.0f
			)
			rowValueText.gravity = Gravity.END or Gravity.CENTER_VERTICAL
			rowValueText.textAlignment = TextView.TEXT_ALIGNMENT_GRAVITY
			rowValueText.setTextColor(Color.BLACK)
			rowValueText.textSize = 10.0f
			rowValueText.text = rowValues.joinToString(separator = " ")
			row.addView(rowValueText)
			playFieldRowValuesTable.addView(row)
		}
	}


	/**
	 * Initializes tha center [TableLayout] that is supposed to contain the play field buttons.
	 */
	private fun initializePlayFieldTable(
		playField: PlayField,
		playFieldTableLayout: TableLayout
	)
	{
		val playFieldTable: TableLayout = playFieldTableLayout
		val fieldValues = playField.getFieldValues()

		for (rowIndex in fieldValues.indices)
		{
			val row = TableRow(this)
			row.layoutParams = TableLayout.LayoutParams(
				TableLayout.LayoutParams.WRAP_CONTENT,
				TableLayout.LayoutParams.WRAP_CONTENT,
				1.0f
			)
			playFieldTable.addView(row)

			val rowValues: MutableList<FieldData> = fieldValues[rowIndex]
			for (columnIndex in rowValues.indices)
			{
				val fieldButton = MaterialButton(this, null, R.attr.materialButtonOutlinedStyle)
				val layoutParams = TableRow.LayoutParams(
					TableRow.LayoutParams.WRAP_CONTENT,
					TableRow.LayoutParams.WRAP_CONTENT, 1.0f
				)
				layoutParams.setMargins(0)
				fieldButton.layoutParams = layoutParams
				fieldButton.setPadding(0, 0, 0, 0)
				fieldButton.insetTop = 0
				fieldButton.insetBottom = 0
				fieldButton.textAlignment = Button.TEXT_ALIGNMENT_CENTER
				fieldButton.setBackgroundColor(Color.WHITE)
				fieldButton.cornerRadius = 0
				row.addView(fieldButton)
			}
		}
	}
}