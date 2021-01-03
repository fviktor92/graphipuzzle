package com.graphipuzzle

import com.graphipuzzle.data.FieldData
import com.graphipuzzle.data.PlayFieldData
import kotlinx.serialization.Serializable
import kotlin.math.ceil

/**
 * Responsible for the logic of the play field. Initializes the field values, row values, column values
 */
@Serializable
class PlayField(private val playFieldData: PlayFieldData)
{
	private val fieldValues: MutableList<MutableList<FieldData>> = this.playFieldData.fieldValues
	private val fieldSize = this.fieldValues.size
	private val maxGroups = ceil(fieldSize / 2.0).toInt()
	private val fieldColumns: MutableList<MutableList<Int>> = ArrayList(maxGroups)
	private val fieldRows: MutableList<MutableList<Int>> = ArrayList(maxGroups)

	init
	{
		loadValues()
	}

	fun getFieldValues(): MutableList<MutableList<FieldData>>
	{
		return this.fieldValues
	}

	fun getFieldColumns(): MutableList<MutableList<Int>>
	{
		return this.fieldColumns
	}

	fun getFieldRows(): MutableList<MutableList<Int>>
	{
		return this.fieldRows
	}

	private fun loadValues()
	{
		var rowCount = 0
		var colCount = IntArray(fieldSize)
		var rowPrev = 0
		var colPrev = IntArray(fieldSize)

		for (i in this.fieldValues[0].indices)
		{
			colCount[i] = 0
			colPrev[i] = 0
			this.fieldColumns.add(ArrayList(maxGroups))
		}

		for (row in 0 until this.fieldSize)
		{
			rowPrev = 0
			this.fieldRows.add(ArrayList(maxGroups))

			for (col in 0 until this.fieldSize)
			{
				val currentFieldValue = this.fieldValues[row][col].isPaintable
				if (currentFieldValue)
				{
					rowCount++
					colCount[col]++
				} else
				{
					if (rowPrev == 1)
					{
						this.fieldRows[row].add(rowCount)
						rowCount = 0
					}
					if (colPrev[col] == 1)
					{
						this.fieldColumns[col].add(colCount[col])
						colCount[col] = 0
					}
				}

				rowPrev = if (currentFieldValue) 1 else 0
				colPrev[col] = if (currentFieldValue) 1 else 0

				if ((col == fieldSize - 1) && (currentFieldValue))
				{
					this.fieldRows[row].add(rowCount)
					rowCount = 0
				}
				if ((row == fieldSize - 1) && (currentFieldValue))
				{
					this.fieldColumns[col].add(colCount[col])
					colCount[col] = 0
				}
			}
		}
	}

	fun setTileValue(row: Int, col: Int)
	{

	}

	fun validate()
	{

	}
}