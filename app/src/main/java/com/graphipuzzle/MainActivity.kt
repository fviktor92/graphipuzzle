package com.graphipuzzle

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

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

	/*private fun checkPermissions()
	{
		if (ContextCompat.checkSelfPermission(
				this,
				Manifest.permission.WRITE_EXTERNAL_STORAGE
			)
			== PackageManager.PERMISSION_DENIED
		)
		{
			ActivityCompat.requestPermissions(
				this,
				String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE },
				EXT_STORAGE_PERMISSION_CODE
			);
			Log.d(
				"Check Permissions",
				"After getting permission: " + Manifest.permission.WRITE_EXTERNAL_STORAGE + " " + ContextCompat.checkSelfPermission(
					this,
					Manifest.permission.WRITE_EXTERNAL_STORAGE
				)
			);

		} else
		{
			// We were granted permission already before
			Log.d("Check Permissions", "Already has permission to write to external storage");
		}
	}*/
}