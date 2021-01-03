package com.graphipuzzle

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.graphipuzzle.databinding.ActivityPlayFieldBinding
import com.graphipuzzle.databinding.FragmentPlayFieldBinding
import com.graphipuzzle.playfieldfragments.PlayFieldFragment
import com.graphipuzzle.read.PlayFieldSize
import com.graphipuzzle.read.ReadPlayField

class PlayFieldActivity : AppCompatActivity()
{
	private lateinit var activityPlayFieldBinding: ActivityPlayFieldBinding
	private lateinit var fragmentPlayFieldBinding: FragmentPlayFieldBinding

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		activityPlayFieldBinding =
			DataBindingUtil.setContentView(this, R.layout.activity_play_field)
		fragmentPlayFieldBinding =
			DataBindingUtil.setContentView(this, R.layout.fragment_play_field)

		val playField =
			PlayField(ReadPlayField(this, PlayFieldSize.BIG, "level_1.json").getPlayFieldData())

		val playFieldFragment =
			PlayFieldFragment.newInstance(playField, R.id.play_field_fragment)
		supportFragmentManager.beginTransaction()
			.add(activityPlayFieldBinding.playFieldFragmentContainerView.id, playFieldFragment)

		val playFieldTableInitializer =
			PlayFieldTableInitializer(this, playField, fragmentPlayFieldBinding)

		activityPlayFieldBinding.apply {
			playFieldTableInitializer.initializePlayFieldTables()
			invalidateAll() // Refresh the UI with the new data, invalidating all binding expressions so that they get recreated with the correct data
		}
	}
}