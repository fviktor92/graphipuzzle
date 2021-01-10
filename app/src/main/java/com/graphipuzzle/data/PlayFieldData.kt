package com.graphipuzzle.data

import kotlinx.serialization.Serializable

/**
 * Contains the data of a play field. It is supposed to be an n*n list of [TileData], that must be dividable by 5.
 */
@Serializable
data class PlayFieldData(val tileValues: ArrayList<ArrayList<TileData>>)
