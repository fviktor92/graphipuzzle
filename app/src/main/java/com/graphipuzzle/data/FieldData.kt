package com.graphipuzzle.data

import kotlinx.serialization.Serializable

/**
 * Contains data about a field that is located in a [com.graphipuzzle.data.PlayFieldData]
 * @property isPaintable Whether the field can be painted to black or not
 * @property hexColorCode The color code of the field in HEX
 */
@Serializable
data class FieldData(val isPaintable: Boolean, val hexColorCode: String)