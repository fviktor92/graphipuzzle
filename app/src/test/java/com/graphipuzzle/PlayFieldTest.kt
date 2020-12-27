package com.graphipuzzle

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.graphipuzzle.read.PlayFieldSize
import com.graphipuzzle.read.ReadPlayField
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

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
		val actualSmallFieldColumns: MutableList<MutableList<Int>> =
			smallPlayField.getFieldColumns()
		val actualSmallFieldRows: MutableList<MutableList<Int>> = smallPlayField.getFieldRows()
		val expectedSmallFieldColumns = mutableListOf(
			mutableListOf(1),
			mutableListOf(2, 2),
			mutableListOf(2, 2, 3),
			mutableListOf(7, 2),
			mutableListOf(2, 3),
			mutableListOf(2, 3),
			mutableListOf(2, 2, 3),
			mutableListOf(7, 2),
			mutableListOf(2, 2, 3),
			mutableListOf(2)
		)
		val expectedSmallFieldRows = mutableListOf(
			mutableListOf(1, 1),
			mutableListOf(3, 3),
			mutableListOf(3, 3),
			mutableListOf(1, 1),
			mutableListOf(3, 4),
			mutableListOf(3, 4),
			mutableListOf(1, 1),
			mutableListOf(3, 3, 2),
			mutableListOf(9),
			mutableListOf(7)
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
		val actualBigFieldColumns: MutableList<MutableList<Int>> =
			bigPlayField.getFieldColumns()
		val actualBigFieldRows: MutableList<MutableList<Int>> = bigPlayField.getFieldRows()
		val expectedBigFieldColumns = mutableListOf(
			mutableListOf(1, 2),
			mutableListOf(1, 3),
			mutableListOf(7, 1),
			mutableListOf(7),
			mutableListOf(2, 2, 3),
			mutableListOf(4, 1),
			mutableListOf(4, 1, 1),
			mutableListOf(2, 1, 4),
			mutableListOf(1, 1, 5),
			mutableListOf(3, 1, 8),
			mutableListOf(1, 2, 1, 3),
			mutableListOf(2, 1, 1, 4),
			mutableListOf(5, 5),
			mutableListOf(2, 4, 3),
			mutableListOf(3)
		)
		val expectedBigFieldRows = mutableListOf(
			mutableListOf(2, 4),
			mutableListOf(4, 1, 3),
			mutableListOf(4, 1, 2),
			mutableListOf(2, 1, 1, 1),
			mutableListOf(1, 4),
			mutableListOf(2, 1, 1),
			mutableListOf(1, 3, 3, 1),
			mutableListOf(3, 1, 1),
			mutableListOf(4, 1, 1),
			mutableListOf(3, 1, 4),
			mutableListOf(1, 3, 5),
			mutableListOf(1, 2, 7),
			mutableListOf(2, 3, 4),
			mutableListOf(1, 3, 3),
			mutableListOf(2, 3, 1)
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
}