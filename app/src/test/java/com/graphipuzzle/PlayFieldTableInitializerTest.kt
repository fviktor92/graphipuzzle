package com.graphipuzzle

import android.content.Context
import androidx.databinding.DataBindingUtil
import androidx.test.core.app.ApplicationProvider
import com.graphipuzzle.databinding.FragmentPlayFieldBinding
import com.graphipuzzle.read.PlayFieldSize
import com.graphipuzzle.read.ReadPlayField
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * FIXME: Finish this with PowerMockito?
 */
@RunWith(RobolectricTestRunner::class)
class PlayFieldTableInitializerTest
{
	val context = ApplicationProvider.getApplicationContext<Context>()
	lateinit var playFieldTableInitializer: PlayFieldTableInitializer
	lateinit var playField: PlayField
	lateinit var fragmentPlayFieldBinding: FragmentPlayFieldBinding

	@BeforeClass
	fun beforeClass()
	{
		playField = PlayField(ReadPlayField(context, PlayFieldSize.SMALL, "level_1.json").getPlayFieldData())
	}

	@Test
	fun initializePlayFieldTables_rowValuesAreCorrect()
	{

	}

	@Test
	fun initializePlayFieldTables_columnValuesAreCorrect()
	{

	}

	@Test
	fun initializePlayFieldTables_tableFieldsAreCorrect()
	{

	}
}