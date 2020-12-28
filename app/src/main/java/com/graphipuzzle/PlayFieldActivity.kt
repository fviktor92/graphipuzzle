package com.graphipuzzle

import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
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

	private fun initializePlayFieldColumnValuesTable(binding: ActivityPlayFieldBinding, playField: PlayField)
	{
		val playFieldColumnValuesTable: TableLayout = binding.playFieldColumnValuesTable
		val fieldColumns = playField.getFieldColumns()
		var rowCounter = -1

		for (i in fieldColumns.indices)
		{
			val column: MutableList<Int> = fieldColumns[i]
			for (j in column.indices)
			{
				if (rowCounter < j)
				{
					val newRow = TableRow(this)
					newRow.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT)
					playFieldColumnValuesTable.addView(newRow)
					rowCounter++
				}

				val row = playFieldColumnValuesTable.getChildAt(j) as TableRow
				val columnValueText = TextView(this)
				columnValueText.layoutParams = ConstraintLayout.LayoutParams(0, ConstraintLayout.LayoutParams.MATCH_PARENT)
				columnValueText.background = resources.getDrawable(R.drawable.table_border)
				columnValueText.text = column[j].toString()
				columnValueText.setTextColor(Color.BLACK)
				row.addView(columnValueText)
			}
		}
	}

	private fun initializePlayFieldRowValuesTable(binding: ActivityPlayFieldBinding, playField: PlayField)
	{
		val playFieldRowValuesTable: TableLayout = binding.playFieldRowValuesTable
	}

	private fun initializePlayFieldTable(binding: ActivityPlayFieldBinding, playField: PlayField)
	{
		val playFieldTable: TableLayout = binding.playFieldTable
	}
}