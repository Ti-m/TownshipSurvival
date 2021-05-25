package com.example.settlers.ui

import android.graphics.Color
import android.graphics.Paint
import com.example.settlers.GroundType

object ColorHelper {

    fun getGroundPaint(type: GroundType): Paint {
        val base = Paint().apply {
            this.style = Paint.Style.FILL_AND_STROKE
            this.strokeWidth = 1.0f
        }
        return when (type) {
            GroundType.Water -> base.apply {
                this.color = Color.BLUE
            }
            GroundType.Grass -> base.apply {
                this.color = Color.GREEN
            }
            GroundType.Desert -> base.apply {
                this.color = Color.YELLOW
            }
            GroundType.Mountain -> base.apply {
                this.color = Color.GRAY
            }
        }
    }

    fun getFlagPaint(): Paint {
        return Paint().apply {
            this.color = Color.LTGRAY
            this.style = Paint.Style.FILL
            this.textSize = 100.0f
        }
    }

    fun getBuildingProgressPaint() : Paint {
        return Paint().apply {
            this.color = Color.RED
            this.style = Paint.Style.FILL
            this.strokeWidth = 10.0f
            this.textAlign = Paint.Align.CENTER
        }
    }

    fun getTextPaint() : Paint {
        return Paint().apply {
            this.color = Color.BLACK
            this.textAlign = Paint.Align.CENTER
            this.textSize =  10.0f
            this.style = Paint.Style.FILL
        }
    }

    fun getBuildingPaint() : Paint {
        return Paint().apply {
            this.color = Color.LTGRAY
            this.style = Paint.Style.FILL_AND_STROKE
            this.strokeWidth = 1.0f
        }
    }

    fun getStoppedPaint() : Paint {
        return Paint().apply {
            this.color = Color.MAGENTA
            this.alpha = 150 //Make this less bright
            this.style = Paint.Style.FILL_AND_STROKE
            this.strokeWidth = 1.0f
        }
    }

    fun getOverLayPaintAssigned() : Paint {
        return Paint().apply {
            this.color = Color.BLUE
            this.alpha = 200 //Make this less bright
            this.style = Paint.Style.FILL_AND_STROKE
            this.strokeWidth = 1.0f
        }
    }

    fun getOverLayPaintClicked() : Paint {
        return Paint().apply {
            this.color = Color.GREEN
            this.alpha = 200 //Make this less bright
            this.style = Paint.Style.FILL_AND_STROKE
            this.strokeWidth = 1.0f
        }
    }
}