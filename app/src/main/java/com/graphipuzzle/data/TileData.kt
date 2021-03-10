package com.graphipuzzle.data

import kotlinx.serialization.Serializable

/**
 * Contains data about a tile that is located in a [com.graphipuzzle.data.PlayFieldData]
 * @property isPaintable Whether the tile can be painted to black or not
 * @property hexColorCode The color code of the tile in HEX
 */
@Serializable
data class TileData(var isPaintable: Boolean, var hexColorCode: String)