package com.graphipuzzle

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.graphipuzzle.read.PlayFieldSize
import com.graphipuzzle.read.ReadPlayField
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
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
			ReadPlayField(context, PlayFieldSize.SMALL, "level_1.json").getPlayFieldData()
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
			"Small field column values did not match",
			expectedSmallFieldColumns,
			actualSmallFieldColumns
		)

		assertEquals(
			"Small field rows values did not match",
			expectedSmallFieldRows,
			actualSmallFieldRows
		)
	}

	@Test
	fun getFieldValuesIsCorrect_BigField()
	{
		val bigPlayFieldData =
			ReadPlayField(context, PlayFieldSize.BIG, "level_1.json").getPlayFieldData()
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
			"Big field column values did not match",
			expectedBigFieldColumns,
			actualBigFieldColumns
		)

		assertEquals(
			"Big field rows values did not match",
			expectedBigFieldRows,
			actualBigFieldRows
		)
	}

	@Test
	fun setTileStateIsCorrect()
	{
		val playFieldData =
			ReadPlayField(context, PlayFieldSize.SMALL, "level_1.json").getPlayFieldData()
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
			ReadPlayField(context, PlayFieldSize.SMALL, "level_1.json").getPlayFieldData()
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
			ReadPlayField(context, PlayFieldSize.SMALL, "level_1.json").getPlayFieldData()
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
}