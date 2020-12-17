package com.graphipuzzle

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PlayFieldActivity : AppCompatActivity()
{
	lateinit var playField: PlayField
	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_play_field)
		val textView: TextView = findViewById(R.id.textview)
		textView.text = "SZAR"
		playField = PlayField(ReadPlayField(this, "level_1").getFile())

	}
}