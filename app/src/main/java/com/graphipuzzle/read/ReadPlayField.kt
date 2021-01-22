package com.graphipuzzle.read;

import android.content.Context
import android.util.Log
import com.graphipuzzle.data.PlayFieldData
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

/**
 *
 * @param ctx The context where the resources are acquired from.
 * @param playFieldLevel See [PlayFieldLevel]
 * @param fileName The name of the JSON raw resource file, with the extension. See [PlayFieldData] for data representation.
 */
class ReadPlayField(ctx: Context, playFieldLevel: PlayFieldLevel, fileName: String)
{
	private val playFieldData: PlayFieldData

	init
	{
		playFieldData = readFile(ctx, fileName, playFieldLevel)
	}

	fun getPlayFieldData(): PlayFieldData
	{
		return this.playFieldData
	}

	private fun readFile(
		ctx: Context,
		fileName: String,
		playFieldLevel: PlayFieldLevel
	): PlayFieldData
	{
		Log.d(this.toString(), "Reading play field data...")
		val assetManager = ctx.resources.assets
		// FIXME: replace path separator
		val bufferedReader =
			assetManager.open(playFieldLevel.fieldLevelName + "/" + fileName)
				.bufferedReader()
		val resourceContent: String = bufferedReader.readText()
		bufferedReader.close()
		return Json.decodeFromString(resourceContent)
	}
}
