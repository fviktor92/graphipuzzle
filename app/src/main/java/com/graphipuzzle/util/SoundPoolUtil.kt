package com.graphipuzzle.util

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.preference.PreferenceManager
import com.graphipuzzle.fragments.KEY_SOUND

/**
 * Singleton convenience class for playing sounds, using the [SoundPool] class.
 */
class SoundPoolUtil private constructor(private var context: Context)
{
	private val soundPool: SoundPool

	init
	{
		var attributes = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME)
			.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()
		this.soundPool = SoundPool.Builder().setAudioAttributes(attributes).build()
	}

	companion object
	{
		@Volatile
		private var INSTANCE: SoundPoolUtil? = null

		fun getInstance(context: Context): SoundPoolUtil =
			INSTANCE ?: synchronized(this) {
				INSTANCE ?: SoundPoolUtil(context).also { INSTANCE = it }
			}
	}

	fun playSound(resid: Int)
	{
		if (PreferenceManager.getDefaultSharedPreferences(this.context).getBoolean(KEY_SOUND, true))
		{
			var soundId = this.soundPool.load(this.context, resid, 1)
			this.soundPool.setOnLoadCompleteListener { _, _, _ ->
				this.soundPool.play(soundId, 0.2F, 0.2F, 1, 0, 1F)
			}
		}
	}
}