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
import com.graphipuzzle.R
import com.graphipuzzle.read.LevelPack
import com.graphipuzzle.util.SoundPoolUtil

class LevelPacksAdapter(context: Context, levelPacks: Array<LevelPack>) :
	RecyclerView.Adapter<LevelPacksAdapter.ViewHolder>()
{
	private val context: Context = context
	private val levelPacks: Array<LevelPack> = levelPacks

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
	{
		val inflater: LayoutInflater = LayoutInflater.from(this.context)
		val row = inflater.inflate(R.layout.level_pack_row, parent, false)
		return ViewHolder(row)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int)
	{
		holder.levelPackName.text = levelPacks[position].levelPackName
		holder.levelPackImage.setImageResource(R.drawable.ic_launcher_background)
		holder.itemView.setOnClickListener { view ->
			openLevelPackSetOnClickListener(view, levelPacks[position])
		}
	}

	override fun getItemCount(): Int
	{
		return levelPacks.size
	}

	private fun openLevelPackSetOnClickListener(view: View, levelPack: LevelPack)
	{
		SoundPoolUtil.getInstance(this.context).playSound(R.raw.button_sound)
		val bundle = bundleOf(LEVEL_PACK to levelPack.name)
		view.findNavController().navigate(R.id.action_levelPacksFragment_to_levelsFragment, bundle)
	}


	class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
	{
		val levelPackName: TextView = itemView.findViewById(R.id.level_pack_name)
		val levelPackImage: ImageView = itemView.findViewById(R.id.level_pack_image)
	}
}