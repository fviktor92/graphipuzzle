package com.graphipuzzle.read;

import android.content.Context
import android.util.Log
import com.graphipuzzle.data.PlayFieldData
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

val LEVEL_PACKS_FOLDER_NAME = "level_packs"

/**
 *
 * @param ctx The context where the resources are acquired from.
 * @param levelPack See [LevelPack]
 * @param fileName The name of the JSON raw resource file, with the extension. See [PlayFieldData] for data representation.
 */
class ReadPlayField(ctx: Context, levelPack: LevelPack, fileName: String)
{
	private val playFieldData: PlayFieldData

	init
	{
		playFieldData = readFile(ctx, fileName, levelPack)
	}

	fun getPlayFieldData(): PlayFieldData
	{
		return this.playFieldData
	}

	private fun readFile(ctx: Context, fileName: String, levelPack: LevelPack): PlayFieldData
	{
		Log.d(this.toString(), "Reading play field data...")
		val assetManager = ctx.resources.assets
		// FIXME: replace path separator
		val bufferedReader =
			assetManager.open(LEVEL_PACKS_FOLDER_NAME + "/" + levelPack.levelPackFolderName + "/" + fileName)
				.bufferedReader()
		val resource = bufferedReader.readText()
		bufferedReader.close()
		return Json.decodeFromString(resource)
	}
}
