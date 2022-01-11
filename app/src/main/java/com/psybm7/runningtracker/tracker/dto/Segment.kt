package com.psybm7.runningtracker.tracker.dto

import android.location.Location
import com.psybm7.runningtracker.HelperService
import java.io.Serializable

class Segment(val start: Point, val end: Point) : Serializable {
    val duration: Long
        get() = this.end.time - this.start.time

    val distance: Float
        get() {
            val results = FloatArray(3)
            Location.distanceBetween(this.start.latitude, this.start.longitude, this.end.latitude, this.end.longitude, results)
            return results[0]
        }

    val pace: Double
        get() = HelperService.calculatePace(this.distance, this.duration)
}