package com.graphipuzzle

class PlayField(val fieldValues: Array<IntArray>)
{
	private val fieldColumns: Array<IntArray> =
		Array(this.fieldValues.size) { IntArray(this.fieldValues[0].size) }
	private val fieldRows: Array<IntArray> =
		Array(this.fieldValues.size) { IntArray(this.fieldValues[0].size) }

	init
	{
		load()
	}

	fun load()
	{
		var rowCount = 0
		var colCount = IntArray(this.fieldValues[0].size)
		var rowPrev = 0
		var colPrev = IntArray(this.fieldValues[0].size)

		for (i in this.fieldValues[0].indices)
		{
			colCount[i] = 0
			colPrev[i] = 0
		}

		for (row in this.fieldValues.indices)
		{
			rowPrev = 0
			for (col in this.fieldValues[row].indices)
			{
				if (this.fieldValues[row][col] == 1)
				{
					rowCount++
					colCount[col]++
				} else
				{
					if (rowPrev == 1)
					{
						this.fieldRows[row][0] = rowCount
						rowCount = 0
					}
					if (colPrev[col] == 1)
					{
						this.fieldColumns[col][0] = colCount[col]
						colCount[col] = 0
					}
				}

				rowPrev = this.fieldValues[row][col]
				colPrev[col] = this.fieldValues[row][col]

				if ((col == this.fieldValues[row].size - 1) && (this.fieldValues[row][col] == 1))
				{
					this.fieldRows[row][0] = rowCount
					rowCount = 0
				}
				if ((row == this.fieldValues.size - 1) && (this.fieldValues[row][col] == 1))
				{
					this.fieldColumns[col][0] = colCount[col]
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