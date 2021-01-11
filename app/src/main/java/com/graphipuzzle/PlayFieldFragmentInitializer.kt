package com.graphipuzzle

import android.content.Context
import android.graphics.Color
import android.text.Html
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.core.view.get
import androidx.core.view.setMargins
import com.google.android.material.button.MaterialButton
import com.graphipuzzle.data.TileData
import com.graphipuzzle.databinding.FragmentPlayFieldBinding

/**
 * Responsible for initializing the Play field Fragment. Including:
 * - The column values for the play field table
 * - The row values for the play field table
 * - The table buttons
 * - The style of the play field
 * - The listeners
 */
class PlayFieldFragmentInitializer(
	private val ctx: Context,
	private val playField: PlayField,
	private val fragmentPlayFieldBinding: FragmentPlayFieldBinding
)
{
	private val ROW_TAG_PREFIX = "row_"
	private val COLUMN_TAG_PREFIX = "column_"

	fun initializePlayFieldFragment()
	{
		initializePlayFieldColumnValuesTable()
		initializePlayFieldRowValuesTable()
		initializePlayFieldTable()
		setCompleteButtonOnTouchListenerForValidation()
		setHelpButtonOnTouchListener()
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
			val columnValues: ArrayList<Int> = fieldColumns[columnIndex]
			val columnValueText = createColumnValueTextView(columnValues)
			row.addView(columnValueText)
		}

		playFieldColumnValuesTable.addView(row)
	}

	private fun createColumnValueTextView(columnValues: ArrayList<Int>): TextView
	{
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
		return columnValueText
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
			val rowValues: ArrayList<Int> = fieldRows[rowIndex]
			val row = TableRow(this.ctx)
			row.layoutParams = TableLayout.LayoutParams(
				0,
				TableLayout.LayoutParams.WRAP_CONTENT,
				1.0f
			)

			val rowValueText = createRowValueTextView(rowValues)
			row.addView(rowValueText)
			playFieldRowValuesTable.addView(row)
		}
	}

	private fun createRowValueTextView(rowValues: ArrayList<Int>): TextView
	{
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
		return rowValueText
	}


	/**
	 * Initializes tha center [TableLayout] that is supposed to contain the play field buttons.
	 */
	private fun initializePlayFieldTable()
	{
		val playFieldTable: TableLayout = this.fragmentPlayFieldBinding.playFieldTable
		val fieldValues = this.playField.getTileValues()

		addBorderInTable(playFieldTable)

		for (rowIndex in fieldValues.indices)
		{
			val row = createPlayFieldTableRow(rowIndex)
			playFieldTable.addView(row)
			addBorderInRow(row)

			if ((rowIndex + 1) % 5 == 0)
			{
				addBorderInTable(playFieldTable)
			}

			val rowValues: ArrayList<TileData> = fieldValues[rowIndex]
			for (columnIndex in rowValues.indices)
			{
				val fieldButton = createPlayFieldButton(rowIndex, columnIndex)

				row.addView(fieldButton)

				if ((columnIndex + 1) % 5 == 0)
				{
					addBorderInRow(row)
				}
			}
		}
	}

	private fun createPlayFieldTableRow(rowIndex: Int): TableRow
	{
		val row = TableRow(this.ctx)
		row.tag = "$ROW_TAG_PREFIX$rowIndex"
		row.layoutParams = TableLayout.LayoutParams(
			TableLayout.LayoutParams.WRAP_CONTENT,
			TableLayout.LayoutParams.WRAP_CONTENT,
			1.0f
		)
		return row
	}

	private fun createPlayFieldButton(rowIndex: Int, columnIndex: Int): MaterialButton
	{
		val fieldButton = MaterialButton(this.ctx, null, R.attr.materialButtonOutlinedStyle)
		val layoutParams = TableRow.LayoutParams(
			TableRow.LayoutParams.WRAP_CONTENT,
			TableRow.LayoutParams.WRAP_CONTENT, 1.0f
		)
		layoutParams.setMargins(0)
		fieldButton.tag = "$COLUMN_TAG_PREFIX$columnIndex"
		fieldButton.layoutParams = layoutParams
		fieldButton.setPadding(0, 0, 0, 0)
		fieldButton.insetTop = 0
		fieldButton.insetBottom = 0
		fieldButton.textAlignment = Button.TEXT_ALIGNMENT_CENTER
		fieldButton.setBackgroundColor(Color.WHITE)
		fieldButton.cornerRadius = 0

		fieldButton.setOnTouchListener { v, event ->
			when (event?.action)
			{
				MotionEvent.ACTION_DOWN ->
				{
					if (fragmentPlayFieldBinding.tileColorSwitch.isChecked)
					{
						v?.setBackgroundColor(Color.BLACK)
						this.playField.setTileState(1, rowIndex, columnIndex)
					} else
					{
						v?.setBackgroundColor(Color.GRAY)
						this.playField.setTileState(0, rowIndex, columnIndex)
					}

				}
				MotionEvent.ACTION_UP ->
				{
					colorColumnTextView(columnIndex)
					colorRowTextView(rowIndex)
				}
			}

			v?.onTouchEvent(event) ?: true
		}

		return fieldButton
	}

	private fun colorColumnTextView(columnIndex: Int)
	{
		val columnGroupStates: IntArray = this.playField.getColumnGroupStates()[columnIndex]
		val tableRow = this.fragmentPlayFieldBinding.playFieldColumnValuesTable[0] as TableRow
		val textView = tableRow[columnIndex] as TextView
		var columnValues: Array<String> = textView.text.split(Regex("\n")).toTypedArray()

		for (i in columnValues.indices)
		{
			val columnGroupState = columnGroupStates[i]
			if (columnGroupState != 0 && columnGroupState == columnValues[i].toInt())
			{
				var recoloredGroup = "<font color=#888888>" + columnValues[i] + "</font>"
				columnValues[i] = recoloredGroup
				var newText = columnValues.joinToString("<br>")
				textView.text = Html.fromHtml(newText)
			} else if (columnGroupState != 0 && columnGroupState != columnValues[i].toInt())
			{
				var recoloredGroup = "<font color=#000000>" + columnValues[i] + "</font>"
				columnValues[i] = recoloredGroup
				var newText = columnValues.joinToString("<br>")
				textView.text = Html.fromHtml(newText)
			}
		}
	}

	private fun colorRowTextView(rowIndex: Int)
	{
		val rowGroupStates: IntArray = this.playField.getRowGroupStates()[rowIndex]
		val tableRow = this.fragmentPlayFieldBinding.playFieldRowValuesTable[rowIndex] as TableRow
		val textView = tableRow[0] as TextView
		var rowValues: Array<String> = textView.text.split(Regex(" ")).toTypedArray()

		for (i in rowValues.indices)
		{
			val rowGroupState = rowGroupStates[i]
			if (rowGroupState != 0 && rowGroupState == rowValues[i].toInt())
			{
				var recoloredGroup = "<font color=#888888>" + rowValues[i] + "</font>"
				rowValues[i] = recoloredGroup
				var newText = rowValues.joinToString(" ")
				textView.text = Html.fromHtml(newText)
			} else if (rowGroupState != 0 && rowGroupState != rowValues[i].toInt())
			{
				var recoloredGroup = "<font color=#000000>" + rowValues[i] + "</font>"
				rowValues[i] = recoloredGroup
				var newText = rowValues.joinToString(" ")
				textView.text = Html.fromHtml(newText)
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

	private fun setCompleteButtonOnTouchListenerForValidation()
	{
		this.fragmentPlayFieldBinding.completeButton.setOnTouchListener { v, event ->

			if (event.action == MotionEvent.ACTION_DOWN)
			{
				if (this.playField.validate())
				{
					Toast.makeText(ctx.applicationContext, "Congrats!", Toast.LENGTH_SHORT).show()
				} else
				{
					Toast.makeText(
						ctx.applicationContext,
						"Not yet complete! Keep trying!",
						Toast.LENGTH_SHORT
					).show()
				}
			}
			v?.onTouchEvent(event) ?: true
		}
	}

	private fun setHelpButtonOnTouchListener()
	{
		this.fragmentPlayFieldBinding.helpButton.setOnTouchListener { v, event ->
			val paintableIndices = this.playField.help()
			if (event.action == MotionEvent.ACTION_DOWN)
			{
				if (paintableIndices != Pair(-1, -1))
				{
					this.fragmentPlayFieldBinding.playFieldTable.findViewWithTag<TableRow>(
						ROW_TAG_PREFIX + paintableIndices.first
					).findViewWithTag<MaterialButton>(COLUMN_TAG_PREFIX + paintableIndices.second)
						.setBackgroundColor(Color.BLACK)
					this.playField.setTileState(1, paintableIndices.first, paintableIndices.second)
					colorColumnTextView(paintableIndices.second)
					colorRowTextView(paintableIndices.first)
				}
			}

			v?.onTouchEvent(event) ?: true
		}
	}
}