package com.psybm7.runningtracker.dto

import android.location.Location

class Segment(val start: Point, val end: Point) {
    val duration: Long
        get() = this.end.time - this.start.time

    val distance: Float
        get() {
            val results = FloatArray(3)
            Location.distanceBetween(this.start.latitude, this.start.longitude, this.end.latitude, this.end.longitude, results)
            return results[0]
        }
}