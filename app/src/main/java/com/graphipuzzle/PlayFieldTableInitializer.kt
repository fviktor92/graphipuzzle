package com.graphipuzzle

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.view.setMargins
import com.google.android.material.button.MaterialButton
import com.graphipuzzle.data.FieldData
import com.graphipuzzle.databinding.FragmentPlayFieldBinding

/**
 * Responsible for initializing the Play field table. Including:
 * - The column values
 * - The row values
 * - The table buttons
 * - The style of the play field
 * - The listeners
 */
class PlayFieldTableInitializer(
	ctx: Context,
	playField: PlayField,
	fragmentPlayFieldBinding: FragmentPlayFieldBinding
)
{
	private val ctx: Context = ctx
	private val playField: PlayField = playField
	private val fragmentPlayFieldBinding: FragmentPlayFieldBinding = fragmentPlayFieldBinding

	fun initializePlayFieldTables()
	{
		initializePlayFieldColumnValuesTable()
		initializePlayFieldRowValuesTable()
		initializePlayFieldTable()
	}

	/**
	 * Initializes the top [TableLayout] that is supposed to contain the column group values.
	 */
	private fun initializePlayFieldColumnValuesTable()
	{
		val playFieldColumnValuesTable: TableLayout =
			this.fragmentPlayFieldBinding.playFieldColumnValuesTable
		val fieldColumns = this.playField.getFieldColumns()
		val row = TableRow(this.ctx)
		row.layoutParams = TableLayout.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT)

		for (columnIndex in fieldColumns.indices)
		{
			val columnValues: MutableList<Int> = fieldColumns[columnIndex]
			val columnValueText = TextView(this.ctx)
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
	private fun initializePlayFieldRowValuesTable()
	{
		val playFieldRowValuesTable: TableLayout =
			this.fragmentPlayFieldBinding.playFieldRowValuesTable
		val fieldRows = this.playField.getFieldRows()

		for (rowIndex in fieldRows.indices)
		{
			val rowValues: MutableList<Int> = fieldRows[rowIndex]
			val row = TableRow(this.ctx)
			row.layoutParams = TableLayout.LayoutParams(
				0,
				TableLayout.LayoutParams.WRAP_CONTENT,
				1.0f
			)

			val rowValueText = TextView(this.ctx)
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
	private fun initializePlayFieldTable()
	{
		val playFieldTable: TableLayout = this.fragmentPlayFieldBinding.playFieldTable
		val fieldValues = this.playField.getFieldValues()

		addBorderInTable(playFieldTable)

		for (rowIndex in fieldValues.indices)
		{
			val row = TableRow(this.ctx)
			row.layoutParams = TableLayout.LayoutParams(
				TableLayout.LayoutParams.WRAP_CONTENT,
				TableLayout.LayoutParams.WRAP_CONTENT,
				1.0f
			)
			playFieldTable.addView(row)
			addBorderInRow(row)

			if ((rowIndex + 1) % 5 == 0)
			{
				addBorderInTable(playFieldTable)
			}

			val rowValues: MutableList<FieldData> = fieldValues[rowIndex]
			for (columnIndex in rowValues.indices)
			{
				val fieldButton = MaterialButton(this.ctx, null, R.attr.materialButtonOutlinedStyle)
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

				if ((columnIndex + 1) % 5 == 0)
				{
					addBorderInRow(row)
				}
			}
		}
	}

	private fun addBorderInRow(row: TableRow)
	{
		val border = View(this.ctx)
		val borderLayoutParams =
			TableRow.LayoutParams(2, TableRow.LayoutParams.MATCH_PARENT)
		border.layoutParams = borderLayoutParams
		border.setBackgroundColor(Color.GRAY)
		row.addView(border)
	}

	private fun addBorderInTable(tableLayout: TableLayout)
	{
		val border = View(this.ctx)
		val borderLayoutParams =
			TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 2)
		border.layoutParams = borderLayoutParams
		border.setBackgroundColor(Color.GRAY)
		tableLayout.addView(border)
	}
}