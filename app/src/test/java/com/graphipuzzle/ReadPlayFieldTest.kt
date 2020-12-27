package com.graphipuzzle

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ReadPlayFieldTest
{
	val context = ApplicationProvider.getApplicationContext<Context>()

	@Test
	fun getPlayFieldDataIsCorrect()
	{
		// Given a 10x10 play field
		val readPlayField = ReadPlayField(context, "level_1")

		// When the play field data is returned
		val playFieldData = readPlayField.getPlayFieldData()

		// Then the size should be 100
		assertEquals(100, playFieldData.fieldValues.size)
	}
}