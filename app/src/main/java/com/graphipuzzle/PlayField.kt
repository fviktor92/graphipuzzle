package com.graphipuzzle

import kotlin.math.ceil


class PlayField(private val fieldValues: MutableList<MutableList<Int>>)
{
	private val fieldSize = this.fieldValues[0].size
	private val maxGroups = ceil(fieldSize / 2.0).toInt()
	private val fieldColumns: MutableList<MutableList<Int>> = ArrayList(maxGroups)
	private val fieldRows: MutableList<MutableList<Int>> = ArrayList(maxGroups)

	init
	{
		loadValues()
	}

	fun getFieldValues(): MutableList<MutableList<Int>>
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
				if (this.fieldValues[row][col] == 1)
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

				rowPrev = this.fieldValues[row][col]
				colPrev[col] = this.fieldValues[row][col]

				if ((col == fieldSize - 1) && (this.fieldValues[row][col] == 1))
				{
					this.fieldRows[row].add(rowCount)
					rowCount = 0
				}
				if ((row == fieldSize - 1) && (this.fieldValues[row][col] == 1))
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