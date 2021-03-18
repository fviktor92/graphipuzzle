package com.graphipuzzle.fragments

import android.content.Context
import android.widget.TextView
import com.graphipuzzle.R
import com.skydoves.colorpickerview.AlphaTileView
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.flag.FlagView


class CustomFlag(context: Context?, layout: Int) : FlagView(context, layout)
{
	private val textView: TextView = findViewById(R.id.flag_color_code)
	private val alphaTileView: AlphaTileView = findViewById(R.id.flag_color_layout)
	override fun onRefresh(colorEnvelope: ColorEnvelope)
	{
		textView.text = "#" + colorEnvelope.hexCode
		alphaTileView.setPaintColor(colorEnvelope.color)
	}

}