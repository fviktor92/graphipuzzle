package com.graphipuzzle

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.graphipuzzle.databinding.ActivityPlayFieldBinding
import com.graphipuzzle.read.PlayFieldSize
import com.graphipuzzle.read.ReadPlayField

class PlayFieldActivity : AppCompatActivity()
{
	private lateinit var binding: ActivityPlayFieldBinding

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		binding = DataBindingUtil.setContentView(this, R.layout.activity_play_field)

		val playField =
			PlayField(ReadPlayField(this, PlayFieldSize.SMALL, "level_1.json").getPlayFieldData())

		binding.apply {
			invalidateAll() // Refresh the UI with the new data, invalidating all binding expressions so that they get recreated with the correct data
		}
	}
}