package com.psybm7.runningtracker.tracker.dto

import android.location.Location
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable
import kotlin.math.round

/**
 * Tracked run.
 * Contains a series of segments, which are made up of two points.
 */
class Track : Serializable {
    /**
     * List of location points on this run
     */
    private val segments: MutableList<Segment> = mutableListOf()

    companion object {
        val GSON_TYPE = object : TypeToken<Track>() {}.type

        /**
         * Convert from JSON to a Track object
         */
        fun deserialise(json: String): Track {
            return Gson().fromJson(json, Track.GSON_TYPE)
        }
    }

    /**
     * Total distance of all segments
     */
    val distance: Float
        get() {
            if (this.segments.isEmpty()) return 0.0F
            val distance = this.segments.map { segment -> segment.distance }
            return distance.sum()
        }

    /**
     * Mean pace over all segments
     */
    val pace: Double
        get() {
            if (this.segments.isEmpty()) return 0.0
            val pace = this.segments.map { segment -> segment.pace }
            return pace.average()
        }

    /**
     * Total duration between all segments
     */
    val duration: Long
        get() {
            if (this.segments.isEmpty()) return 0
            val start = this.segments.first().start.time
            val end = this.segments.last().end.time
            return end.minus(start)
        }

    /**
     * List of all start points in the Track
     */
    val points: List<Point>
        get() = this.segments.map { segment -> segment.start }

    /**
     * Add a new segment to the Track.
     * Uses the last point of the last segment as the start point.
     */
    fun addLocation(location: Location) {
        val end = Point(location.latitude, location.longitude, location.time)
        var start = end.copy()
        if (this.segments.size > 0) start = this.segments.last().end

        this.segments.add(Segment(start, end))
    }

    /**
     * Convert Track into JSON
     */
    fun serialise(): String {
        return Gson().toJson(this, Track.GSON_TYPE)
    }
}