package com.graphipuzzle.read

enum class LevelPack(val position: Int, val levelPackFolderName: String, val levelPackName: String)
{
	ANIMALS(0, "animals", "Animals"),
	FOOD(1, "food", "Food"),
	OBJECTS(2, "objects", "Objects"),
	NATURE(3, "nature", "Nature"),
	HOLIDAYS(4, "holidays", "Holidays"),
	VEHICLES(5, "vehicles", "Vehicles"),
	SPORTS(6, "sports", "Sports"),
	SPACE(7, "space", "Space"),
	MYTHOLOGY(8, "mythology", "Mythology"),
	WORLD(9, "world", "World")
}