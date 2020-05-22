package com.example.settlers

import android.os.Handler

object RepeatHelper {
    fun repeatDelayed(delay: Long, todo: () -> Unit) {
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                todo()
                handler.postDelayed(this, delay)
            }
        }, delay)
    }
}