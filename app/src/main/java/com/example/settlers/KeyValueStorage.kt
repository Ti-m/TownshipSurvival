package com.example.settlers

import android.content.SharedPreferences

interface KeyValueStorage {
    fun setString(key: String, value: String)
    fun getString(key: String): String
}

class DefaultKeyValueStorage(private val sharedPreferences: SharedPreferences) : KeyValueStorage {
    override fun setString(key: String, value: String) {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    override fun getString(key: String): String = sharedPreferences.getString(key, "")!!
}

class TestDoubleKeyValueStorage : KeyValueStorage {

    private val map: MutableMap<String, Any> = mutableMapOf()

    override fun setString(key: String, value: String) {
        map[key] = value
    }

    override fun getString(key: String): String = map[key] as String

}