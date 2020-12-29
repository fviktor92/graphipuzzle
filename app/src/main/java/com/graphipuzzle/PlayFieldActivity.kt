package com.graphipuzzle

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
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

		for (i in fieldColumns.indices)
		{
			val column: MutableList<Int> = fieldColumns[i]
			for (j in 0 until longestColumnSize)
			{
				if (j < column.size)
				{
					addValueTextView(
						playFieldColumnValuesTable,
						longestColumnSize - j - 1,
						column[j].toString()
					)
				} else
				{
					addValueTextView(playFieldColumnValuesTable, longestColumnSize - j - 1, "")
				}
			}
		}
	}

	private fun addValueTextView(
		playFieldColumnValuesTable: TableLayout,
		j: Int,
		value: String
	)
	{
		val row = playFieldColumnValuesTable.getChildAt(j) as TableRow
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
		row.addView(columnValueText)
	}

	private fun initializePlayFieldRowValuesTable(
		binding: ActivityPlayFieldBinding,
		playField: PlayField
	)
	{
		val playFieldRowValuesTable: TableLayout = binding.playFieldRowValuesTable
	}

	private fun initializePlayFieldTable(binding: ActivityPlayFieldBinding, playField: PlayField)
	{
		val playFieldTable: TableLayout = binding.playFieldTable
	}
}