package com.graphipuzzle

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.graphipuzzle.read.PlayFieldLevel
import com.graphipuzzle.read.ReadPlayField
import org.junit.Assert.assertArrayEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class PlayFieldTest
{
	val context = ApplicationProvider.getApplicationContext<Context>()

	@Test
	fun getFieldValuesIsCorrect_SmallField()
	{
		val smallPlayFieldData =
			ReadPlayField(context, PlayFieldLevel.EASY, "easy_10_10_sailboat.json").getPlayFieldData()
		val smallPlayField = PlayField(smallPlayFieldData)
		val actualSmallFieldColumns: ArrayList<ArrayList<Int>> =
			smallPlayField.getFieldColumns()
		val actualSmallFieldRows: ArrayList<ArrayList<Int>> = smallPlayField.getFieldRows()
		val expectedSmallFieldColumns = arrayListOf(
			arrayListOf(1),
			arrayListOf(2, 2),
			arrayListOf(2, 2, 3),
			arrayListOf(7, 2),
			arrayListOf(2, 3),
			arrayListOf(2, 3),
			arrayListOf(2, 2, 3),
			arrayListOf(7, 2),
			arrayListOf(2, 2, 3),
			arrayListOf(2)
		)
		val expectedSmallFieldRows = arrayListOf(
			arrayListOf(1, 1),
			arrayListOf(3, 3),
			arrayListOf(3, 3),
			arrayListOf(1, 1),
			arrayListOf(3, 4),
			arrayListOf(3, 4),
			arrayListOf(1, 1),
			arrayListOf(3, 3, 2),
			arrayListOf(9),
			arrayListOf(7)
		)

		assertEquals(
			expectedSmallFieldColumns,
			actualSmallFieldColumns,
			"Small field column values did not match"
		)

		assertEquals(
			expectedSmallFieldRows,
			actualSmallFieldRows,
			"Small field rows values did not match"
		)
	}

	@Test
	fun getFieldValuesIsCorrect_BigField()
	{
		val bigPlayFieldData =
			ReadPlayField(context, PlayFieldLevel.HARD, "hard_15_15_dog_and_boy_playing_ball.json").getPlayFieldData()
		val bigPlayField = PlayField(bigPlayFieldData)
		val actualBigFieldColumns: ArrayList<ArrayList<Int>> =
			bigPlayField.getFieldColumns()
		val actualBigFieldRows: ArrayList<ArrayList<Int>> = bigPlayField.getFieldRows()
		val expectedBigFieldColumns = arrayListOf(
			arrayListOf(1, 2),
			arrayListOf(1, 3),
			arrayListOf(7, 1),
			arrayListOf(7),
			arrayListOf(2, 2, 3),
			arrayListOf(4, 1),
			arrayListOf(4, 1, 1),
			arrayListOf(2, 1, 4),
			arrayListOf(1, 1, 5),
			arrayListOf(3, 1, 8),
			arrayListOf(1, 2, 1, 3),
			arrayListOf(2, 1, 1, 4),
			arrayListOf(5, 5),
			arrayListOf(2, 4, 3),
			arrayListOf(3)
		)
		val expectedBigFieldRows = arrayListOf(
			arrayListOf(2, 4),
			arrayListOf(4, 1, 3),
			arrayListOf(4, 1, 2),
			arrayListOf(2, 1, 1, 1),
			arrayListOf(1, 4),
			arrayListOf(2, 1, 1),
			arrayListOf(1, 3, 3, 1),
			arrayListOf(3, 1, 1),
			arrayListOf(4, 1, 1),
			arrayListOf(3, 1, 4),
			arrayListOf(1, 3, 5),
			arrayListOf(1, 2, 7),
			arrayListOf(2, 3, 4),
			arrayListOf(1, 3, 3),
			arrayListOf(2, 3, 1)
		)

		assertEquals(
			expectedBigFieldColumns,
			actualBigFieldColumns,
			"Big field column values did not match"
		)

		assertEquals(
			expectedBigFieldRows,
			actualBigFieldRows,
			"Big field rows values did not match"
		)
	}

	@Test
	fun setTileStateIsCorrect()
	{
		val playFieldData =
			ReadPlayField(context, PlayFieldLevel.EASY, "easy_10_10_sailboat.json").getPlayFieldData()
		val playField = PlayField(playFieldData)

		playField.setTileState(1, 1, 1)
		assertEquals(1, playField.getTileState(1, 1))

		playField.setTileState(0, 2, 2)
		assertEquals(0, playField.getTileState(2, 2))

		assertEquals(0, playField.getTileState(9, 9))
	}

	@Test
	fun setTileStateHandlesInvalidValues()
	{
		val playFieldData =
			ReadPlayField(context, PlayFieldLevel.EASY, "easy_10_10_sailboat.json").getPlayFieldData()
		val playField = PlayField(playFieldData)

		var expectedException = assertFailsWith(IllegalArgumentException::class) {
			playField.setTileState(2, 0, 0)
		}

		assertEquals("The tile value must be either 0 or 1! It was: 2", expectedException.message)

		expectedException = assertFailsWith(IllegalArgumentException::class) {
			playField.setTileState(-1, 0, 0)
		}

		assertEquals("The tile value must be either 0 or 1! It was: -1", expectedException.message)

		expectedException = assertFailsWith(IllegalArgumentException::class) {
			playField.setTileState(1, 100, 0)
		}

		assertEquals(
			"The row must be greater than 0 or lower than 10. It was 100",
			expectedException.message
		)

		expectedException = assertFailsWith(IllegalArgumentException::class) {
			playField.setTileState(1, 9, -1)
		}

		assertEquals(
			"The col must be greater than 0 or lower than 10. It was -1",
			expectedException.message
		)
	}

	@Test
	fun validateIsCorrect()
	{
		val playFieldData =
			ReadPlayField(context, PlayFieldLevel.EASY, "easy_10_10_sailboat.json").getPlayFieldData()
		val playField = PlayField(playFieldData)

		// Initial state
		assertFalse(playField.validate())

		// Complete the play field by last tile still missing
		playField.setTileState(1, 0, 3)
		playField.setTileState(1, 0, 7)
		playField.setTileState(1, 1, 2)
		playField.setTileState(1, 1, 3)
		playField.setTileState(1, 1, 4)
		playField.setTileState(1, 1, 6)
		playField.setTileState(1, 1, 7)
		playField.setTileState(1, 1, 8)
		playField.setTileState(1, 1, 2)
		playField.setTileState(1, 2, 2)
		playField.setTileState(1, 2, 3)
		playField.setTileState(1, 2, 4)
		playField.setTileState(1, 2, 6)
		playField.setTileState(1, 2, 7)
		playField.setTileState(1, 2, 8)
		playField.setTileState(1, 2, 2)
		playField.setTileState(1, 3, 3)
		playField.setTileState(1, 3, 7)
		playField.setTileState(1, 4, 1)
		playField.setTileState(1, 4, 2)
		playField.setTileState(1, 4, 3)
		playField.setTileState(1, 4, 5)
		playField.setTileState(1, 4, 6)
		playField.setTileState(1, 4, 7)
		playField.setTileState(1, 4, 8)
		playField.setTileState(1, 5, 1)
		playField.setTileState(1, 5, 2)
		playField.setTileState(1, 5, 3)
		playField.setTileState(1, 5, 5)
		playField.setTileState(1, 5, 6)
		playField.setTileState(1, 5, 7)
		playField.setTileState(1, 5, 8)
		playField.setTileState(1, 6, 3)
		playField.setTileState(1, 6, 7)
		playField.setTileState(1, 7, 0)
		playField.setTileState(1, 7, 1)
		playField.setTileState(1, 7, 2)
		playField.setTileState(1, 7, 4)
		playField.setTileState(1, 7, 5)
		playField.setTileState(1, 7, 6)
		playField.setTileState(1, 7, 8)
		playField.setTileState(1, 7, 9)
		playField.setTileState(1, 8, 1)
		playField.setTileState(1, 8, 2)
		playField.setTileState(1, 8, 3)
		playField.setTileState(1, 8, 4)
		playField.setTileState(1, 8, 5)
		playField.setTileState(1, 8, 6)
		playField.setTileState(1, 8, 7)
		playField.setTileState(1, 8, 8)
		playField.setTileState(1, 8, 9)
		playField.setTileState(1, 9, 2)
		playField.setTileState(1, 9, 3)
		playField.setTileState(1, 9, 4)
		playField.setTileState(1, 9, 5)
		playField.setTileState(1, 9, 6)
		playField.setTileState(1, 9, 7)

		assertFalse(playField.validate())

		// Set the last tile, the playfield should be completed
		playField.setTileState(1, 9, 8)
		assertTrue(playField.validate())

		// Set a tile that should not be painted to painted
		playField.setTileState(1, 0, 0)
		assertFalse(playField.validate())
	}

	@Test
	fun helpTest()
	{
		val playFieldData =
			ReadPlayField(context, PlayFieldLevel.EASY, "easy_10_10_sailboat.json").getPlayFieldData()
		val playField = PlayField(playFieldData)

		val paintableTile = playField.help()

		assertTrue(playField.getTileValues()[paintableTile.first][paintableTile.second].isPaintable)

		// Complete the play field so no help can be given
		playField.setTileState(1, 0, 3)
		playField.setTileState(1, 0, 7)
		playField.setTileState(1, 1, 2)
		playField.setTileState(1, 1, 3)
		playField.setTileState(1, 1, 4)
		playField.setTileState(1, 1, 6)
		playField.setTileState(1, 1, 7)
		playField.setTileState(1, 1, 8)
		playField.setTileState(1, 1, 2)
		playField.setTileState(1, 2, 2)
		playField.setTileState(1, 2, 3)
		playField.setTileState(1, 2, 4)
		playField.setTileState(1, 2, 6)
		playField.setTileState(1, 2, 7)
		playField.setTileState(1, 2, 8)
		playField.setTileState(1, 2, 2)
		playField.setTileState(1, 3, 3)
		playField.setTileState(1, 3, 7)
		playField.setTileState(1, 4, 1)
		playField.setTileState(1, 4, 2)
		playField.setTileState(1, 4, 3)
		playField.setTileState(1, 4, 5)
		playField.setTileState(1, 4, 6)
		playField.setTileState(1, 4, 7)
		playField.setTileState(1, 4, 8)
		playField.setTileState(1, 5, 1)
		playField.setTileState(1, 5, 2)
		playField.setTileState(1, 5, 3)
		playField.setTileState(1, 5, 5)
		playField.setTileState(1, 5, 6)
		playField.setTileState(1, 5, 7)
		playField.setTileState(1, 5, 8)
		playField.setTileState(1, 6, 3)
		playField.setTileState(1, 6, 7)
		playField.setTileState(1, 7, 0)
		playField.setTileState(1, 7, 1)
		playField.setTileState(1, 7, 2)
		playField.setTileState(1, 7, 4)
		playField.setTileState(1, 7, 5)
		playField.setTileState(1, 7, 6)
		playField.setTileState(1, 7, 8)
		playField.setTileState(1, 7, 9)
		playField.setTileState(1, 8, 1)
		playField.setTileState(1, 8, 2)
		playField.setTileState(1, 8, 3)
		playField.setTileState(1, 8, 4)
		playField.setTileState(1, 8, 5)
		playField.setTileState(1, 8, 6)
		playField.setTileState(1, 8, 7)
		playField.setTileState(1, 8, 8)
		playField.setTileState(1, 8, 9)
		playField.setTileState(1, 9, 2)
		playField.setTileState(1, 9, 3)
		playField.setTileState(1, 9, 4)
		playField.setTileState(1, 9, 5)
		playField.setTileState(1, 9, 6)
		playField.setTileState(1, 9, 7)
		playField.setTileState(1, 9, 8)

		assertEquals(Pair(-1, -1), playField.help())
	}

	@Test
	fun getColumnGroupStatesTest()
	{
		val playFieldData =
			ReadPlayField(context, PlayFieldLevel.EASY, "easy_10_10_sailboat.json").getPlayFieldData()
		val playField = PlayField(playFieldData)
		val initialMatchingGroups: Array<IntArray> = Array(playField.getFieldSize()) {
			IntArray(playField.getMaxGroups())
		}

		// Calling the method for initial play field should return initial array with all 0 values
		assertArrayEquals(initialMatchingGroups, playField.getColumnGroupStates())

		// Complete the first column that only has 1 group
		playField.setTileState(1, 7, 0)

		assertEquals(1, playField.getColumnGroupStates()[0][0])

		// Complete the third column that has 3 groups with play field ending
		playField.setTileState(1, 1, 2)
		playField.setTileState(1, 2, 2)
		playField.setTileState(1, 4, 2)
		playField.setTileState(1, 5, 2)
		playField.setTileState(1, 7, 2)
		playField.setTileState(1, 8, 2)
		playField.setTileState(1, 9, 2)

		assertArrayEquals(intArrayOf(2, 2, 3, 0, 0), playField.getColumnGroupStates()[2])

		// Uncomplete the third column
		playField.setTileState(0, 9, 2)
		assertArrayEquals(intArrayOf(2, 2, 0, 0, 0), playField.getColumnGroupStates()[2])

		// Set a single tile at field ending
		playField.setTileState(1, 9, 9)
		assertArrayEquals(intArrayOf(0, 0, 0, 0, 0), playField.getColumnGroupStates()[9])
	}

	@Test
	fun getRowGroupStatesTest()
	{
		val playFieldData =
			ReadPlayField(context, PlayFieldLevel.EASY, "easy_10_10_sailboat.json").getPlayFieldData()
		val playField = PlayField(playFieldData)
		val initialMatchingGroups: Array<IntArray> = Array(playField.getFieldSize()) {
			IntArray(playField.getMaxGroups())
		}

		// Calling the method for initial play field should return initial array with all 0 values
		assertArrayEquals(initialMatchingGroups, playField.getRowGroupStates())

		// Complete the last row that only has 1 group
		playField.setTileState(1, 9, 2)
		playField.setTileState(1, 9, 3)
		playField.setTileState(1, 9, 4)
		playField.setTileState(1, 9, 5)
		playField.setTileState(1, 9, 6)
		playField.setTileState(1, 9, 7)
		playField.setTileState(1, 9, 8)

		assertEquals(7, playField.getRowGroupStates()[9][0])

		// Complete the eighth row that has 3 groups with play field ending
		playField.setTileState(1, 7, 0)
		playField.setTileState(1, 7, 1)
		playField.setTileState(1, 7, 2)
		playField.setTileState(1, 7, 4)
		playField.setTileState(1, 7, 5)
		playField.setTileState(1, 7, 6)
		playField.setTileState(1, 7, 8)
		playField.setTileState(1, 7, 9)

		assertArrayEquals(intArrayOf(3, 3, 2, 0, 0), playField.getRowGroupStates()[7])

		// Uncomplete the eighth row
		playField.setTileState(0, 7, 9)
		assertArrayEquals(intArrayOf(3, 3, 0, 0, 0), playField.getRowGroupStates()[7])

		// Set a single tile at field ending
		playField.setTileState(1, 0, 9)
		assertArrayEquals(intArrayOf(0, 1, 0, 0, 0), playField.getRowGroupStates()[0])
	}
}