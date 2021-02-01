package com.graphipuzzle

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.graphipuzzle.databinding.ActivityMainBinding
import com.graphipuzzle.fragments.LEVEL_CHOOSER_FRAGMENT
import com.graphipuzzle.fragments.LevelChooserFragment

class MainActivity : AppCompatActivity()
{
	private val SYSTEM_UI_MESSAGE = Message().apply { "SYSTEM_UI_MESSAGE" }
	private lateinit var mainBinding: ActivityMainBinding
	private val handler = Handler(Looper.myLooper()!!)

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		this.mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
		supportFragmentManager.popBackStack(
			LEVEL_CHOOSER_FRAGMENT,
			FragmentManager.POP_BACK_STACK_INCLUSIVE
		);
		if (savedInstanceState == null)
		{
			supportFragmentManager.commit {
				setReorderingAllowed(true)
				add(
					R.id.content_fragment_container_view,
					LevelChooserFragment::class.java,
					null
				)
			}
		}

		val toolbar = this.mainBinding.topToolbar as Toolbar
		setSupportActionBar(toolbar)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		supportActionBar?.setDisplayShowHomeEnabled(true)
		toolbar.setNavigationOnClickListener {
			startActivity(Intent(applicationContext, MainActivity::class.java))
		}
	}

	override fun onResume()
	{
		super.onResume()
		hideSystemUI()
	}

	override fun onWindowFocusChanged(hasFocus: Boolean) {
		super.onWindowFocusChanged(hasFocus)
		if (hasFocus) hideSystemUI()
	}

	private fun hideSystemUI() {

		window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
				// Set the content to appear under the system bars so that the
				// content doesn't resize when the system bars hide and show.
				or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
				or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
				// Hide the nav bar and status bar
				or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				or View.SYSTEM_UI_FLAG_FULLSCREEN)
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