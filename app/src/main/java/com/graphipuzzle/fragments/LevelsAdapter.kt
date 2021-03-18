package com.graphipuzzle.fragments

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.graphipuzzle.PlayField
import com.graphipuzzle.R
import com.graphipuzzle.read.LEVEL_PACKS_FOLDER_NAME
import com.graphipuzzle.read.LevelPack
import com.graphipuzzle.read.ReadPlayField
import com.graphipuzzle.util.SoundPoolUtil
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class LevelsAdapter(context: Context, levelPack: LevelPack) :
	RecyclerView.Adapter<LevelsAdapter.ViewHolder>()
{
	private val context: Context = context
	private val levelPack: LevelPack = levelPack
	private val levels: Array<String> = getListOfLevels()

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
	{
		val inflater: LayoutInflater = LayoutInflater.from(this.context)
		val item = inflater.inflate(R.layout.level_item, parent, false)
		return ViewHolder(item)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int)
	{
		holder.levelNumber.text = (position + 1).toString()
		holder.levelImage.setImageResource(R.drawable.ic_launcher_background)
		holder.itemView.setOnClickListener { view ->
			startLevelSetOnClickListener(view, levels[position])
		}
	}

	override fun getItemCount(): Int
	{
		return this.levels.size
	}

	private fun getListOfLevels(): Array<String>
	{
		return this.context.resources.assets.list("$LEVEL_PACKS_FOLDER_NAME/${this.levelPack.levelPackFolderName}")!!
	}

	private fun startLevelSetOnClickListener(view: View, playFieldFileName: String)
	{
		SoundPoolUtil.getInstance(this.context).playSound(R.raw.button_sound)
		val playFieldJson =
			Json.encodeToString(PlayField(ReadPlayField(this.context, this.levelPack, playFieldFileName).getPlayFieldData()))
		val bundle = bundleOf(PLAY_FIELD to playFieldJson)
		view.findNavController().navigate(R.id.action_levelsFragment_to_playFieldFragment, bundle)
	}

	class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
	{
		val levelNumber: TextView = itemView.findViewById(R.id.level_number)
		val levelImage: ImageView = itemView.findViewById(R.id.level_image)
	}
}