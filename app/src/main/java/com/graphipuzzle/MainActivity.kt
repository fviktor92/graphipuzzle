package com.graphipuzzle

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.graphipuzzle.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity()
{
	private lateinit var mainBinding: ActivityMainBinding
	private lateinit var navController: NavController

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		this.mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
		setContentView(this.mainBinding.root)

		navController = this.findNavController(R.id.navHostFragment)
		NavigationUI.setupActionBarWithNavController(this, navController)
		supportActionBar?.title = ""
	}

	override fun onSupportNavigateUp(): Boolean
	{
		return navController.navigateUp()
	}

	override fun onResume()
	{
		super.onResume()
		hideSystemNavBar()
	}

	override fun onWindowFocusChanged(hasFocus: Boolean)
	{
		super.onWindowFocusChanged(hasFocus)
		if (hasFocus) hideSystemNavBar()
	}

	private fun hideSystemNavBar()
	{
		window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
	}

	override fun onBackPressed()
	{
		
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