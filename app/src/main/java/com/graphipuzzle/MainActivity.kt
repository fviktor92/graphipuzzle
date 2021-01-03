package com.graphipuzzle

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.graphipuzzle.read.PlayFieldSize
import com.graphipuzzle.read.ReadPlayField
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

const val PLAY_FIELD_MESSAGE = "com.graphipuzzle.MainActivity.PlayField"

class MainActivity : AppCompatActivity()
{
	lateinit var playField: PlayField

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		val startSmallGame: Button = findViewById(R.id.start_small_game)
		val startBigGame: Button = findViewById(R.id.start_big_game)
		startSmallGame.setOnClickListener {
			playField =
				PlayField(ReadPlayField(this, PlayFieldSize.SMALL, "level_1.json").getPlayFieldData())
			startPlayFieldActivity(playField)
		}
		startBigGame.setOnClickListener {
			playField =
				PlayField(ReadPlayField(this, PlayFieldSize.BIG, "level_1.json").getPlayFieldData())
			startPlayFieldActivity(playField)
		}
	}

	private fun startPlayFieldActivity(playField: PlayField)
	{
		Log.d(this.toString(), "Starting play field activity...")
		val playFieldIntent = Intent(this, PlayFieldActivity::class.java).apply {
			putExtra(PLAY_FIELD_MESSAGE, Json.encodeToString(playField))
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