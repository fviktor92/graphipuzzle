package com.graphipuzzle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    lateinit var playField: PlayField

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val textView: TextView = findViewById(R.id.textview)
        playField = PlayField(ReadPlayField(this, "level_1").getFile())
        textView.text = playField.getFieldValues().contentDeepToString()
    }
}