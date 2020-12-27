package com.graphipuzzle.data

import kotlinx.serialization.Serializable

/**
 * Contains the data of a play field. It is supposed to be an n*n list of [FieldData], that must be dividable by 5.
 */
@Serializable
data class PlayFieldData(val fieldValues: MutableList<MutableList<FieldData>>)
