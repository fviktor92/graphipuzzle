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
	/**
	 * This is the expected result of the game
	 */
	private val tileValues: ArrayList<ArrayList<TileData>> = this.playFieldData.tileValues
	private val fieldSize = this.tileValues.size
	private val maxGroups = ceil(fieldSize / 2.0).toInt()
	private lateinit var fieldColumns: ArrayList<ArrayList<Int>>
	private lateinit var fieldRows: ArrayList<ArrayList<Int>>

	/**
	 * This is the actual state of the play field, modified by the user
	 */
	private val tileStates = Array(fieldSize) { IntArray(fieldSize) }


	init
	{
		loadValues()
	}

	fun getTileValues(): ArrayList<ArrayList<TileData>>
	{
		return this.tileValues
	}

	fun getFieldSize(): Int
	{
		return this.fieldSize
	}

	fun getMaxGroups(): Int
	{
		return this.maxGroups
	}

	fun getTileStates(): Array<IntArray>
	{
		return this.tileStates
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
	 * The tile value can be either 0: GRAY, or 1: BLACK.
	 * @throws IllegalArgumentException If the tileValue is not 0 or 1.
	 * @throws IllegalArgumentException If the row or col is negative or greater than the field size.
	 */
	fun setTileState(tileValue: Int, row: Int, col: Int)
	{
		if (!IntRange(0, 1).contains(tileValue))
		{
			throw IllegalArgumentException("The tile value must be either 0 or 1! It was: $tileValue")
		} else if (!IntRange(0, this.fieldSize).contains(col))
		{
			throw IllegalArgumentException("The col must be greater than 0 or lower than $fieldSize. It was $col")
		} else if (!IntRange(0, this.fieldSize).contains(row))
		{
			throw IllegalArgumentException("The row must be greater than 0 or lower than $fieldSize. It was $row")
		}

		if (this.tileStates[row][col] != tileValue)
		{
			this.tileStates[row][col] = tileValue
			Log.d("PlayField", "Set tile state at row $row and col $col to $tileValue")
		}
	}

	/**
	 * @return Values for every group in a column.
	 */
	fun getFieldColumns(): ArrayList<ArrayList<Int>>
	{
		return this.fieldColumns
	}

	/**
	 * @return Values for every group in a row
	 */
	fun getFieldRows(): ArrayList<ArrayList<Int>>
	{
		return this.fieldRows
	}

	/**
	 * @return Whether the current state of the play field is the same as the expected result or not.
	 */
	fun validate(): Boolean
	{
		for (row in 0 until this.fieldSize)
		{
			for (col in 0 until this.fieldSize)
			{
				val paintable = this.tileValues[row][col].isPaintable
				val painted = this.tileStates[row][col] == 1
				if ((paintable && !painted) || (!paintable && painted))
				{
					return false
				}
			}
		}
		return true
	}

	/**
	 * @return the (row, column) indices of a paintable tile, that is not yet painted. If all the required fields are painted, returns (-1,-1)
	 */
	fun help(): Pair<Int, Int>
	{
		var notPaintedTiles: ArrayList<Pair<Int, Int>> = ArrayList()

		for (row in 0 until this.fieldSize)
		{
			for (col in 0 until this.fieldSize)
			{
				if (this.tileValues[row][col].isPaintable && this.tileStates[row][col] != 1)
				{
					notPaintedTiles.add(Pair(row, col))
				}
			}
		}

		return if (notPaintedTiles.isEmpty()) Pair(-1, -1) else notPaintedTiles.random()
	}

	/**
	 * @return the column group sizes currently according to the tile states. Could be used for coloring the column texts.
	 */
	fun getColumnGroupStates(): Array<IntArray>
	{
		var groups: Array<IntArray> = Array(this.fieldSize) { IntArray(this.maxGroups) }
		var groupSize: Int
		var colIndex: Int
		var isGroup: Boolean
		var arrayColumn: IntArray

		for (col in 0 until this.fieldSize)
		{
			groupSize = 0
			colIndex = -1
			isGroup = false
			arrayColumn = IntArray(this.maxGroups)
			for (row in 0 until this.fieldSize)
			{
				// If it is the first painted tile in the column
				val currentColumn = this.fieldColumns[col]
				val currentColumnWithoutLast = currentColumn.subList(0, currentColumn.size - 1)
				if (this.tileStates[row][col] == 1 && !isGroup)
				{
					groupSize++
					colIndex++
					isGroup = true

					// TODO: DO THE SAME LOGIC FOR ROW. ALSO VERIFY THE INNER GROUPS. Ki kellene számolgatni, hogy van-e már az arrayColumn-ban group, és az alapján eldönteni, hogy a végére kerüljön-e vagy ne

					// If there is a group already in the column and the remaining number of tiles in the column is lesser than or equal to the size of the last group in the column
					if (arrayColumn.count { element -> element == 1 } > 1 && (currentColumnWithoutLast.sum() + currentColumnWithoutLast.size) <= row - groupSize + 1) // FIXME: THERE IS A BUG HERE
					{
						colIndex = currentColumn.size - 1
						arrayColumn[colIndex] = groupSize
					} else if (row + 1 == this.fieldSize)
					{
						groupSize = 0
						isGroup = false
					}
				}
				// If the tile is painted and is in a group
				else if (this.tileStates[row][col] == 1 && isGroup)
				{
					groupSize++
					// If the remaining number of tiles in the column is lesser than or equal to the size of the last group in the column
					if ((currentColumnWithoutLast.sum() + currentColumnWithoutLast.size) <= row - groupSize + 1)
					{
						colIndex = currentColumn.size - 1
						arrayColumn[colIndex] = groupSize
					} else if (row + 1 == this.fieldSize)
					{
						groupSize = 0
						isGroup = false
					}
				} else if (this.tileStates[row][col] != 1 && isGroup)
				{
					arrayColumn[colIndex] = groupSize
					groupSize = 0
					isGroup = false
				}
			}
			groups[col] = arrayColumn
		}

		return groups
	}

	/**
	 * @return the row group sizes currently according to the tile states. Could be used for coloring the column texts.
	 */
	fun getRowGroupStates(): Array<IntArray>
	{
		var groups: Array<IntArray> = Array(this.fieldSize) { IntArray(this.maxGroups) }
		var groupSize: Int
		var rowIndex: Int
		var isGroup: Boolean
		var arrayRow: IntArray

		for (row in 0 until this.fieldSize)
		{
			groupSize = 0
			rowIndex = -1
			isGroup = false
			arrayRow = IntArray(this.maxGroups)
			for (col in 0 until this.fieldSize)
			{
				if (this.tileStates[row][col] == 1 && !isGroup)
				{
					groupSize++
					rowIndex++
					isGroup = true
					if (col + 1 == this.fieldSize)
					{
						arrayRow[rowIndex] = groupSize
						groupSize = 0
						isGroup = false
					}
				} else if (this.tileStates[row][col] == 1 && isGroup)
				{
					groupSize++
					if (col + 1 == this.fieldSize)
					{
						arrayRow[rowIndex] = groupSize
						groupSize = 0
						isGroup = false
					}
				} else if (this.tileStates[row][col] != 1 && isGroup)
				{
					arrayRow[rowIndex] = groupSize
					groupSize = 0
					isGroup = false
				}
			}
			groups[row] = arrayRow
		}

		return groups
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
}