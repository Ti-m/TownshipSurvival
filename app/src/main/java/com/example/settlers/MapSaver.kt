package com.example.settlers

import com.example.settlers.terrain.MapGenerator
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class MapSaver(
    private val cells: MutableMap<Coordinates, Cell>,
    private val mapGenerator: MapGenerator
    ) {

    //TODO Replace with SharedPreferences
    private var storage = ""

    fun newGame() {
        cells.clear()
        cells.putAll(mapGenerator.createMap(MainActivity.tileGridSize))
    }

    fun save() {
        storage = serializeCells(cells)
    }

    fun load() {
        cells.clear()
        cells.putAll(deserializeCells(storage))
    }

    private val json = Json {
        allowStructuredMapKeys = true
        //serializersModule = module
    }

    private fun serializeCells(cells: Map<Coordinates, Cell>): String {
//        val module = SerializersModule {
//            polymorphic(GameObject::class) {
//                polymorphic(Resource::class) {
//                    subclass(Wood::class)
//                }
//            }
//        }

        return json.encodeToString(cells)
    }

    private fun deserializeCells(string: String): Map<Coordinates, Cell> {
        return json.decodeFromString<Map<Coordinates, Cell>>(string)
    }
}
