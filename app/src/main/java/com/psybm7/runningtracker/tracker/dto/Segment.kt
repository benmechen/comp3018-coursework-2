package com.psybm7.runningtracker.tracker.dto

import android.location.Location
import com.psybm7.runningtracker.HelperService
import java.io.Serializable

/**
 * Segment of a larger Track.
 * Contains a start and end point.
 */
class Segment(val start: Point, val end: Point) : Serializable {
    /**
     * Total duration from start point to end point
     */
    val duration: Long
        get() = this.end.time - this.start.time

    /**
     * Total distance from start point to end point
     */
    val distance: Float
        get() {
            val results = FloatArray(3)
            Location.distanceBetween(
                this.start.latitude,
                this.start.longitude,
                this.end.latitude,
                this.end.longitude,
                results
            )
            return results[0]
        }

    /**
     * Pace between the two points
     */
    val pace: Double
        get() = HelperService.calculatePace(this.distance, this.duration)
}