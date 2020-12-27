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
class ReadPlayFieldTest
{
	val context = ApplicationProvider.getApplicationContext<Context>()

	@Test
	fun getPlayFieldDataIsCorrect()
	{
		// Given a 10x10 play field
		val readPlayField = ReadPlayField(context, PlayFieldSize.SMALL, "level_1.json")
		val playFieldData = readPlayField.getPlayFieldData()
		assertEquals("Play field size did not match", 10, playFieldData.fieldValues.size)
	}
}