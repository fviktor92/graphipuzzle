package com.graphipuzzle;

import android.content.Context
import android.util.Log
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

class ReadPlayField(ctx: Context, fileName: String)
{
	private val fieldValues: MutableList<MutableList<Int>>

	init
	{
		fieldValues = readFile(ctx, fileName)
	}

	fun getFile(): MutableList<MutableList<Int>>
	{
		return this.fieldValues
	}

	private fun readFile(ctx: Context, fileName: String): MutableList<MutableList<Int>>
	{
		Log.d(this.toString(), "Reading play field...")
		val resId: Int = ctx.resources.getIdentifier(fileName, "raw", ctx.packageName)
		val inputStream: InputStream = ctx.resources.openRawResource(resId)
		val inputReader = InputStreamReader(inputStream)
		val bufferedReader = BufferedReader(inputReader)
		val lines: List<String> = bufferedReader.readLines();
		val size = lines.size
		var playField: MutableList<MutableList<Int>> = ArrayList(size)

		for ((row, line) in lines.withIndex())
		{
			var values = line.trim().split(',')
			for (col in lines.indices)
			{
				playField.add(ArrayList(size))
				playField[row].add(Integer.parseInt(values[col]))
			}
		}

		return playField;
	}
}
