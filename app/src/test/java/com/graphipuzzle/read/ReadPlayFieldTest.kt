package com.graphipuzzle.read

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class ReadPlayFieldTest
{
	val context = ApplicationProvider.getApplicationContext<Context>()

	@Test
	fun getPlayFieldDataIsCorrect()
	{
		// Given a 10x10 play field
		val readPlayField = ReadPlayField(context, LevelPack.VEHICLES, "0_easy_10_sailboat.json")
		val playFieldData = readPlayField.getPlayFieldData()

		assertEquals(10, playFieldData.tileValues.size, "Play field size did not match")
		assertEquals("Sailboat", playFieldData.name, "Play field name did not match")
		assertEquals(PlayFieldDifficulty.EASY, playFieldData.difficulty, "Play field difficulty did not match")
	}
}