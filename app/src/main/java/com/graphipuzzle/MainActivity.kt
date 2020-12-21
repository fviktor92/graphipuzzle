package com.graphipuzzle

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

const val EXTRA_MESSAGE = "com.graphipuzzle.MainActivity"

class MainActivity : AppCompatActivity()
{
	lateinit var playField: PlayField

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		val startGame: Button = findViewById(R.id.start_game)
		startGame.setOnClickListener { startPlayFieldActivity() }
	}

	private fun startPlayFieldActivity()
	{
		Log.d(this.toString(), "Starting play field activity...")
		val playFieldIntent = Intent(this, PlayFieldActivity::class.java).apply {
			putExtra(EXTRA_MESSAGE, this.toString())
		}
		startActivity(playFieldIntent)
	}
}