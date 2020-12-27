package com.graphipuzzle;

import android.content.Context
import android.content.res.Resources
import android.util.Log
import com.graphipuzzle.data.PlayFieldData
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

/**
 *
 * @param ctx The context where the resources are acquired from.
 * @param fileName The name of the JSON raw resource file, without the extension. See [PlayFieldData] for data representation.
 */
class ReadPlayField(ctx: Context, fileName: String)
{
	private val playFieldData: PlayFieldData

	init
	{
		playFieldData = readFile(ctx, fileName)
	}

	fun getPlayFieldData(): PlayFieldData
	{
		return this.playFieldData
	}

	fun readFile(ctx: Context, fileName: String): PlayFieldData
	{
		Log.d(this.toString(), "Reading play field data...")
		val resId: Int = ctx.resources.getIdentifier(fileName, "raw", ctx.packageName)
		try
		{
			val bufferedReader = ctx.resources.openRawResource(resId).bufferedReader()
			val resourceContent: String = bufferedReader.readText()
			bufferedReader.close()
			return Json.decodeFromString(resourceContent)
		} catch (nfe: Resources.NotFoundException)
		{
			Log.e(this.toString(), "Could not find a raw resource with filename: '$fileName'")
			throw nfe;
		}
	}
}
