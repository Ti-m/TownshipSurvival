package com.example.settlers.util

import android.util.Log

interface Logger {
    fun logi(tag: String, text: String)
}

class DefaultLogger : Logger {
    override fun logi(tag: String, text: String) {
        Log.i(tag,text)
    }
}

//Use for tests
class DisabledLogger : Logger {
    override fun logi(tag: String, text: String) {
        //do nothing
    }

}