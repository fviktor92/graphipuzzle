package com.graphipuzzle

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PlayFieldActivity : AppCompatActivity()
{
	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_play_field)
		var textView: TextView = findViewById(R.id.play_field_text)
		val playField = PlayField(ReadPlayField(this, "level_1").getFile())
		textView.text = playField.getFieldRows().toString()
	}
}