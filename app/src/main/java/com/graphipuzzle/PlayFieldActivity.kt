package com.graphipuzzle

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.commit
import com.graphipuzzle.databinding.ActivityPlayFieldBinding
import com.graphipuzzle.databinding.FragmentPlayFieldBinding
import com.graphipuzzle.playfieldfragments.PLAY_FIELD
import com.graphipuzzle.playfieldfragments.PlayFieldFragment
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class PlayFieldActivity : AppCompatActivity(R.layout.activity_play_field)
{
	private lateinit var activityPlayFieldBinding: ActivityPlayFieldBinding

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)

		activityPlayFieldBinding =
			DataBindingUtil.setContentView(this, R.layout.activity_play_field)

		val playFieldJsonString = intent.getStringExtra(PLAY_FIELD_MESSAGE)

		if (savedInstanceState == null)
		{
			val bundle = bundleOf(PLAY_FIELD to playFieldJsonString)
			supportFragmentManager.commit {
				setReorderingAllowed(true)
				add(R.id.play_field_fragment_container_view, PlayFieldFragment::class.java, bundle)
			}
		}
	}
}