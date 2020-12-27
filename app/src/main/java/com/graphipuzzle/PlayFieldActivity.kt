package com.graphipuzzle

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.graphipuzzle.databinding.ActivityPlayFieldBinding

class PlayFieldActivity : AppCompatActivity()
{
	private lateinit var binding: ActivityPlayFieldBinding

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		binding = DataBindingUtil.setContentView(this, R.layout.activity_play_field)

		var textView: TextView = binding.playFieldText
		val playField = PlayField(ReadPlayField(this, "level_1").getPlayFieldData())

		binding.apply {
			invalidateAll() // Refresh the UI with the new data, invalidating all binding expressions so that they get recreated with the correct data
			textView.text = playField.getFieldRows().toString()
		}
	}
}