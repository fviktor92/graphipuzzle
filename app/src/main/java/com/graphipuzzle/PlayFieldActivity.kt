package com.graphipuzzle

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.databinding.DataBindingUtil
import com.google.android.material.button.MaterialButton
import com.graphipuzzle.data.FieldData
import com.graphipuzzle.databinding.ActivityPlayFieldBinding
import com.graphipuzzle.read.PlayFieldSize
import com.graphipuzzle.read.ReadPlayField

class PlayFieldActivity : AppCompatActivity()
{
	private lateinit var binding: ActivityPlayFieldBinding

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		binding = DataBindingUtil.setContentView(this, R.layout.activity_play_field)

		val playField =
			PlayField(ReadPlayField(this, PlayFieldSize.SMALL, "level_1.json").getPlayFieldData())

		binding.apply {
			initalizePlayfield(binding, playField)
			invalidateAll() // Refresh the UI with the new data, invalidating all binding expressions so that they get recreated with the correct data
		}
	}

	private fun initalizePlayfield(binding: ActivityPlayFieldBinding, playField: PlayField)
	{
		initializePlayFieldRowValuesTable(binding, playField)
		initializePlayFieldColumnValuesTable(binding, playField)
		initializePlayFieldTable(binding, playField)
	}

	/**
	 * Initializes the top [TableLayout] that is supposed to contain the column group values.
	 */
	private fun initializePlayFieldColumnValuesTable(
		binding: ActivityPlayFieldBinding,
		playField: PlayField
	)
	{
		val playFieldColumnValuesTable: TableLayout = binding.playFieldColumnValuesTable
		val fieldColumns = playField.getFieldColumns()
		val longestColumnSize = fieldColumns.stream().map { c -> c.size }.max(Int::compareTo).get()

		for (j in 0..longestColumnSize)
		{
			val newRow = TableRow(this)
			newRow.layoutParams = TableLayout.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT)
			playFieldColumnValuesTable.addView(newRow)
		}

		for (columnIndex in fieldColumns.indices)
		{
			val columnValues: MutableList<Int> = fieldColumns[columnIndex]
			for (rowIndex in 0 until longestColumnSize)
			{
				val tableRowIndex = longestColumnSize - rowIndex - 1
				if (rowIndex < columnValues.size)
				{
					addValueTextView(
						playFieldColumnValuesTable,
						tableRowIndex,
						columnIndex,
						columnValues[rowIndex].toString()
					)
				} else
				{
					addValueTextView(playFieldColumnValuesTable, tableRowIndex, columnIndex, "")
				}
			}
		}
	}

	/**
	 * Initializes the left [TableLayout] that is supposed to contain the row group values.
	 */
	private fun initializePlayFieldRowValuesTable(
		binding: ActivityPlayFieldBinding,
		playField: PlayField
	)
	{
		val playFieldRowValuesTable: TableLayout = binding.playFieldRowValuesTable
		val fieldRows = playField.getFieldRows()
		val longestRowSize = fieldRows.stream().map { r -> r.size }.max(Int::compareTo).get()

		repeat(fieldRows.size) {
			val newRow = TableRow(this)
			newRow.layoutParams = TableLayout.LayoutParams(
				TableLayout.LayoutParams.WRAP_CONTENT,
				TableLayout.LayoutParams.MATCH_PARENT,
				1.0f
			)
			playFieldRowValuesTable.addView(newRow)
		}

		for (rowIndex in fieldRows.indices)
		{
			val rowValues: MutableList<Int> = fieldRows[rowIndex]
			for (columnIndex in 0 until longestRowSize)
			{
				if (columnIndex < rowValues.size)
				{
					addValueTextView(
						playFieldRowValuesTable,
						rowIndex,
						columnIndex,
						rowValues[columnIndex].toString()
					)
				} else
				{
					addValueTextView(playFieldRowValuesTable, rowIndex, columnIndex, "")
				}
			}
		}
	}

	/**
	 * Initializes tha center [TableLayout] that is supposed to contain the play field buttons.
	 */
	private fun initializePlayFieldTable(binding: ActivityPlayFieldBinding, playField: PlayField)
	{
		val playFieldTable: TableLayout = binding.playFieldTable
		val fieldValues = playField.getFieldValues()

		for (rowIndex in fieldValues.indices)
		{
			val newTableRow = TableRow(this)
			newTableRow.layoutParams = TableLayout.LayoutParams(
				TableLayout.LayoutParams.WRAP_CONTENT,
				TableLayout.LayoutParams.MATCH_PARENT
			)
			playFieldTable.addView(newTableRow)

			val rowValues: MutableList<FieldData> = fieldValues[rowIndex]
			for (columnIndex in rowValues.indices)
			{
				val themeWrapper = ContextThemeWrapper(this, R.style.field_unpainted)
				val fieldButton = MaterialButton(themeWrapper)
				fieldButton.layoutParams = TableRow.LayoutParams(
					TableRow.LayoutParams.WRAP_CONTENT,
					TableRow.LayoutParams.MATCH_PARENT,
					1.0f
				)
				newTableRow.addView(fieldButton)
			}
		}
	}

	/**
	 * Adds a text view with a given text to a table layout's table row by the index of the row.
	 * @param playFieldColumnValuesTable The [TableLayout] that will contain the text view
	 * @param tableRowIndex The index of the [TableRow] that will contain the text view
	 * @param textViewIndex The index of the [TextView] (column)
	 * @param value The text of the text view. Should be either a group value or an empty string
	 */
	private fun addValueTextView(
		playFieldColumnValuesTable: TableLayout,
		tableRowIndex: Int,
		textViewIndex: Int,
		value: String
	)
	{
		val row = playFieldColumnValuesTable.getChildAt(tableRowIndex) as TableRow
		val columnValueText = TextView(this)
		columnValueText.layoutParams = TableRow.LayoutParams(
			TableRow.LayoutParams.WRAP_CONTENT,
			TableRow.LayoutParams.MATCH_PARENT,
			1.0f
		)
		columnValueText.background = resources.getDrawable(R.drawable.table_border)
		columnValueText.text = value
		columnValueText.textAlignment = View.TEXT_ALIGNMENT_CENTER
		columnValueText.setTextColor(Color.BLACK)
		row.addView(columnValueText, textViewIndex)
	}
}