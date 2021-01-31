package com.graphipuzzle

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.graphipuzzle.databinding.ActivityMainBinding
import com.graphipuzzle.fragments.LEVEL_CHOOSER_FRAGMENT
import com.graphipuzzle.fragments.LevelChooserFragment

class MainActivity : AppCompatActivity()
{
	private lateinit var mainBinding: ActivityMainBinding

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