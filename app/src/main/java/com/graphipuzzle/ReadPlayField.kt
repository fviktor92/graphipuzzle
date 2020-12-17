package com.graphipuzzle;

import android.content.Context
import android.util.Log
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

class ReadPlayField(ctx: Context, fileName: String)
{
	private val fieldValues: Array<IntArray>

	init
	{
		fieldValues = readFile(ctx, fileName)
	}

	fun getFile(): Array<IntArray>
	{
		return this.fieldValues
	}

	private fun readFile(ctx: Context, fileName: String): Array<IntArray>
	{
		Log.d("ReadPlayField", "Reading play field...")

		val resId: Int = ctx.resources.getIdentifier(fileName, "raw", ctx.packageName)
		val inputStream: InputStream = ctx.resources.openRawResource(resId)
		val inputReader: InputStreamReader = InputStreamReader(inputStream)
		val bufferedReader: BufferedReader = BufferedReader(inputReader)
		val lines: List<String> = bufferedReader.readLines();
		val size: Int = lines.size
		var array: Array<IntArray> = Array(size) { IntArray(size) }

		for ((row, line) in lines.withIndex())
		{
			var values = line.trim().split(',')
			for (col in 0 until size)
			{
				array[row][col] = Integer.parseInt(values[col])
			}
		}

		return array;
	}
}
