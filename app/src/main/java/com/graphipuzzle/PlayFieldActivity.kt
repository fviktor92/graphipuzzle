package com.graphipuzzle

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.graphipuzzle.databinding.ActivityPlayFieldBinding
import com.graphipuzzle.databinding.FragmentPlayFieldBinding
import com.graphipuzzle.playfieldfragments.PlayFieldFragment
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

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
			Json.decodeFromString<PlayField>(intent.getStringExtra(PLAY_FIELD_MESSAGE)!!)

		val playFieldFragment =
			PlayFieldFragment.newInstance(R.id.play_field_fragment)
		supportFragmentManager.beginTransaction()
			.add(activityPlayFieldBinding.playFieldFragmentContainerView.id, playFieldFragment)

		val playFieldTableInitializer =
			PlayFieldFragmentInitializer(this, playField, fragmentPlayFieldBinding)

		activityPlayFieldBinding.apply {
			playFieldTableInitializer.initializePlayFieldFragment()
			invalidateAll() // Refresh the UI with the new data, invalidating all binding expressions so that they get recreated with the correct data
		}
	}
}