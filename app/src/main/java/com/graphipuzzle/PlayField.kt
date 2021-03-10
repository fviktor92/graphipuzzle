package com.graphipuzzle

import com.graphipuzzle.data.PlayFieldData
import com.graphipuzzle.data.TileData
import com.graphipuzzle.read.PlayFieldDifficulty
import kotlinx.serialization.Serializable
import kotlin.math.ceil

/**
 * Responsible for the logic of the play field. Initializes the field values, row values, column values
 */
@Serializable
class PlayField(private val playFieldData: PlayFieldData)
{
	private val name = this.playFieldData.name
	private val playFieldDifficulty = this.playFieldData.difficulty

	private val tileDatas: ArrayList<ArrayList<TileData>> = this.playFieldData.tileValues
	private val fieldSize = this.tileDatas.size
	private val maxGroups = ceil(fieldSize / 2.0).toInt()
	private lateinit var fieldColumns: ArrayList<ArrayList<Int>>
	private lateinit var fieldRows: ArrayList<ArrayList<Int>>
	private var paintableTilesCount: Int = 0
	private var paintedTilesCount: Int = 0

	/**
	 * This is the actual state of the play field, modified by the user
	 */
	private val tileStates = Array(fieldSize) { IntArray(fieldSize) }

	init
	{
		loadValues()
		this.paintableTilesCount = countPaintableTiles()
	}

	fun getName(): String
	{
		return this.name
	}

	fun getPlayFieldDifficulty(): PlayFieldDifficulty
	{
		return this.playFieldDifficulty
	}

	/**
	 * @return The expected result of the game. See [TileData]
	 */
	fun getTileDatas(): ArrayList<ArrayList<TileData>>
	{
		return this.tileDatas
	}

	fun setTileIsPaintable(row: Int, col: Int, isPaintable: Boolean)
	{
		this.tileDatas[row][col].isPaintable = isPaintable
	}

	fun setTileHexColorCode(row: Int, col: Int, hexColorCode: String)
	{
		this.tileDatas[row][col].hexColorCode = hexColorCode
	}

	fun getFieldSize(): Int
	{
		return this.fieldSize
	}

	fun getMaxGroups(): Int
	{
		return this.maxGroups
	}

	/**
	 * @return The actual state of the tiles. A state can be either 0: Not painted, 1: Painted BLACK, or 2: Painted GRAY.
	 */
	fun getTileStates(): Array<IntArray>
	{
		return this.tileStates
	}

	/**
	 * @return the number of tiles which are paintable to 'BLACK' (1)
	 */
	fun getPaintableTilesCount(): Int
	{
		return this.paintableTilesCount
	}

	/**
	 * @return the number of tiles which are currently painted to 'BLACK' (1)
	 */
	fun getPaintedTilesCount(): Int
	{
		return this.paintedTilesCount
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
	 * The tile value can be either 0: WHITE, 1: BLACK or 2: GRAY.
	 * @throws IllegalArgumentException If the tileValue is not 0, 1 or 2.
	 * @throws IllegalArgumentException If the row or col is negative or greater than the field size.
	 */
	fun setTileState(tileValue: Int, row: Int, col: Int)
	{
		if (!IntRange(0, 2).contains(tileValue))
		{
			throw IllegalArgumentException("The tile value must be either 0, 1 or 2! It was: $tileValue")
		} else if (!IntRange(0, this.fieldSize).contains(col))
		{
			throw IllegalArgumentException("The col must be greater than 0 or lower than $fieldSize. It was $col")
		} else if (!IntRange(0, this.fieldSize).contains(row))
		{
			throw IllegalArgumentException("The row must be greater than 0 or lower than $fieldSize. It was $row")
		}

		val tileState = this.tileStates[row][col]
		if (tileState != tileValue)
		{
			if ((tileValue == 0 || tileValue == 2) && tileState == 1)
			{
				this.paintedTilesCount--
			} else if (tileValue == 1 && (tileState == 0 || tileState == 2))
			{
				this.paintedTilesCount++
			}
			this.tileStates[row][col] = tileValue
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
				val paintable = this.tileDatas[row][col].isPaintable
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
				if (this.tileDatas[row][col].isPaintable && this.tileStates[row][col] != 1)
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
		var allGroups: Array<IntArray> = Array(this.fieldSize) { IntArray(this.maxGroups) }

		for (col in 0 until this.fieldSize)
		{
			val expectedGroupsInColumn = this.fieldColumns[col]
			var isGroup = false
			var groupIndex = 0
			var groupStartingIndex = 0
			var groupEndingIndex = 0
			var groupSize = 0
			var actualGroupsInColumn = IntArray(this.maxGroups)

			for (row in 0 until this.fieldSize)
			{
				if (this.tileStates[row][col] == 1 && !isGroup)
				{
					isGroup = true
					groupStartingIndex = row
					groupEndingIndex = row

					if (row + 1 == this.fieldSize)
					{
						groupSize = groupEndingIndex - groupStartingIndex + 1
						groupIndex = getGroupIndexFromArray(
							expectedGroupsInColumn,
							actualGroupsInColumn,
							groupStartingIndex,
							groupEndingIndex
						)
						if (groupIndex > -1) actualGroupsInColumn[groupIndex] = groupSize
						groupStartingIndex = 0
						isGroup = false
					}
				} else if (this.tileStates[row][col] == 1 && isGroup)
				{
					groupEndingIndex = row
					if (row + 1 == this.fieldSize)
					{
						groupSize = groupEndingIndex - groupStartingIndex + 1
						groupIndex = getGroupIndexFromArray(
							expectedGroupsInColumn,
							actualGroupsInColumn,
							groupStartingIndex,
							groupEndingIndex
						)
						if (groupIndex > -1) actualGroupsInColumn[groupIndex] = groupSize
						groupStartingIndex = 0
						isGroup = false
					}
				} else if (this.tileStates[row][col] != 1 && isGroup)
				{
					groupEndingIndex = row - 1
					groupSize = groupEndingIndex - groupStartingIndex + 1
					groupIndex = getGroupIndexFromArray(
						expectedGroupsInColumn,
						actualGroupsInColumn,
						groupStartingIndex,
						groupEndingIndex
					)

					if (groupIndex > -1) actualGroupsInColumn[groupIndex] = groupSize
					groupStartingIndex = 0
					isGroup = false
				}
			}
			allGroups[col] = actualGroupsInColumn
		}

		return allGroups
	}

	/**
	 * @return the row group sizes currently according to the tile states. Could be used for coloring the column texts.
	 */
	fun getRowGroupStates(): Array<IntArray>
	{
		var allGroups: Array<IntArray> = Array(this.fieldSize) { IntArray(this.maxGroups) }

		for (row in 0 until this.fieldSize)
		{
			val expectedGroupsInRow = this.fieldRows[row]
			var isGroup = false
			var groupIndex = 0
			var groupStartingIndex = 0
			var groupEndingIndex = 0
			var groupSize = 0
			var actualGroupsInRow = IntArray(this.maxGroups)

			for (col in 0 until this.fieldSize)
			{
				if (this.tileStates[row][col] == 1 && !isGroup)
				{
					isGroup = true
					groupStartingIndex = col
					groupEndingIndex = col

					if (col + 1 == this.fieldSize)
					{
						groupSize = groupEndingIndex - groupStartingIndex + 1
						groupIndex = getGroupIndexFromArray(
							expectedGroupsInRow,
							actualGroupsInRow,
							groupStartingIndex,
							groupEndingIndex
						)
						if (groupIndex > -1) actualGroupsInRow[groupIndex] = groupSize
						groupStartingIndex = 0
						isGroup = false
					}
				} else if (this.tileStates[row][col] == 1 && isGroup)
				{
					groupEndingIndex = col
					if (col + 1 == this.fieldSize)
					{
						groupSize = groupEndingIndex - groupStartingIndex + 1
						groupIndex = getGroupIndexFromArray(
							expectedGroupsInRow,
							actualGroupsInRow,
							groupStartingIndex,
							groupEndingIndex
						)
						if (groupIndex > -1) actualGroupsInRow[groupIndex] = groupSize
						groupStartingIndex = 0
						isGroup = false
					}
				} else if (this.tileStates[row][col] != 1 && isGroup)
				{
					groupEndingIndex = col - 1
					groupSize = groupEndingIndex - groupStartingIndex + 1
					groupIndex = getGroupIndexFromArray(
						expectedGroupsInRow,
						actualGroupsInRow,
						groupStartingIndex,
						groupEndingIndex
					)

					if (groupIndex > -1) actualGroupsInRow[groupIndex] = groupSize
					groupStartingIndex = 0
					isGroup = false
				}
			}
			allGroups[row] = actualGroupsInRow
		}

		return allGroups
	}

	private fun getGroupIndexFromArray(
		expectedGroupsInArray: ArrayList<Int>,
		actualGroupsInArray: IntArray,
		groupStartingIndex: Int,
		groupEndingIndex: Int
	): Int
	{
		val groupSize = groupEndingIndex - groupStartingIndex + 1
		val afterGroupLength = this.fieldSize - groupEndingIndex - 1
		var groupIndex = 0
		loop@ for (i in 0 until expectedGroupsInArray.size)
		{
			val smallestGroup = expectedGroupsInArray.minOrNull()!!
			val previousGroupsInArray = expectedGroupsInArray.subList(0, i)
			val nextGroupsInArray = expectedGroupsInArray.subList(i + 1, expectedGroupsInArray.size)
			val previousGroupsCanFit =
				groupStartingIndex >= (previousGroupsInArray.sum() + previousGroupsInArray.size)
			val nextGroupsCanFit =
				afterGroupLength >= (nextGroupsInArray.sum() + nextGroupsInArray.size)

			// If it's the first group and there is enough space after the group to fit all the next groups from the array
			if ((groupSize == expectedGroupsInArray[i]) && (i == 0) && actualGroupsInArray[0] == 0 && nextGroupsCanFit)
			{
				groupIndex = i
				break@loop
			}
			// If it's not the first group and there is enough space before the group to fit all the previous and next groups from the array
			else if ((groupSize == expectedGroupsInArray[i]) && (i > 0) && actualGroupsInArray[i] != groupSize && previousGroupsCanFit && nextGroupsCanFit)
			{
				groupIndex = i
				break@loop
			}
			// If it's the last group and there isn't enough space after the group to fit the smallest group from the array and there is enough space before the group to fit all the previous groups from the array
			else if ((groupSize == expectedGroupsInArray[i]) && (i == expectedGroupsInArray.size - 1) && (afterGroupLength <= smallestGroup + 1) && previousGroupsCanFit)
			{
				groupIndex = i
				break@loop
			} else
			{
				groupIndex = -1
			}
		}
		return groupIndex
	}

	private fun loadValues()
	{
		fieldColumns = ArrayList(maxGroups)
		fieldRows = ArrayList(maxGroups)
		var rowCount = 0
		var colCount = IntArray(fieldSize)
		var rowPrev = 0
		var colPrev = IntArray(fieldSize)

		for (i in this.tileDatas[0].indices)
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
				val currentTileValue = this.tileDatas[row][col].isPaintable
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

	private fun countPaintableTiles(): Int
	{
		return this.tileDatas.flatMap { row -> row.toList() }
			.count { tileData -> tileData.isPaintable }
	}
}