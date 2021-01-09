package com.graphipuzzle

import android.util.Log
import com.graphipuzzle.data.PlayFieldData
import com.graphipuzzle.data.TileData
import kotlinx.serialization.Serializable
import kotlin.math.ceil

/**
 * Responsible for the logic of the play field. Initializes the field values, row values, column values
 */
@Serializable
class PlayField(private val playFieldData: PlayFieldData)
{
	private val tileValues: MutableList<MutableList<TileData>> = this.playFieldData.tileValues
	private val fieldSize = this.tileValues.size
	private val tileStates = Array(fieldSize) { IntArray(fieldSize) }
	private val maxGroups = ceil(fieldSize / 2.0).toInt()
	private lateinit var fieldColumns: MutableList<MutableList<Int>>
	private lateinit var fieldRows: MutableList<MutableList<Int>>

	init
	{
		loadValues()
	}

	fun getTileValues(): MutableList<MutableList<TileData>>
	{
		return this.tileValues
	}

	/**
	 * @throws IllegalArgumentException If the row or col is negative or greater than the field size.
	 * @return the value of the tile at the given position. The tile value can be either 0: WHITE, 1: BLACK, or 2: GRAY.
	 */
	fun getTileState(row: Int, col: Int): Int
	{
		return this.tileStates[row][col]
	}

	/**
	 * Sets the tile value at the given position.
	 * The tile value can be either 1: BLACK, or 2: GRAY.
	 * @throws IllegalArgumentException If the tileValue is not 1 or 2.
	 * @throws IllegalArgumentException If the row or col is negative or greater than the field size.
	 */
	fun setTileState(tileValue: Int, row: Int, col: Int)
	{
		if (tileValue < 1 || tileValue > 2)
		{
			throw IllegalArgumentException("The tile value must be either 1 or 2! It was: $tileValue")
		} else if (col < 0 || col >= fieldSize)
		{
			throw IllegalArgumentException("The col must be greater than 0 or lower than $fieldSize. It was $col")
		} else if (row < 0 || row >= fieldSize)
		{
			throw IllegalArgumentException("The row must be greater than 0 or lower than $fieldSize. It was $row")
		}

		this.tileStates[row][col] = tileValue
		Log.d("PlayField", "Set tile state at row $row and col $col to $tileValue")
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
		fieldColumns = ArrayList(maxGroups)
		fieldRows = ArrayList(maxGroups)
		var rowCount = 0
		var colCount = IntArray(fieldSize)
		var rowPrev = 0
		var colPrev = IntArray(fieldSize)

		for (i in this.tileValues[0].indices)
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
				val currentTileValue = this.tileValues[row][col].isPaintable
				if (currentTileValue)
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

				rowPrev = if (currentTileValue) 1 else 0
				colPrev[col] = if (currentTileValue) 1 else 0

				if ((col == fieldSize - 1) && (currentTileValue))
				{
					this.fieldRows[row].add(rowCount)
					rowCount = 0
				}
				if ((row == fieldSize - 1) && (currentTileValue))
				{
					this.fieldColumns[col].add(colCount[col])
					colCount[col] = 0
				}
			}
		}
	}

	fun validate()
	{

	}
}