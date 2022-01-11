package com.psybm7.runningtracker.tracker.dto

import android.location.Location
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable
import kotlin.math.round

class Track : Serializable {
    private val segments: MutableList<Segment> = mutableListOf()

    companion object {
        val GSON_TYPE = object : TypeToken<Track>() {}.type

        fun deserialise(json: String): Track {
            return Gson().fromJson(json, Track.GSON_TYPE)
        }
    }

    val distance: Float
        get() {
            if (this.segments.isEmpty()) return 0.0F
            val distance = this.segments.map { segment -> segment.distance }
            return distance.sum()
        }

    val pace: Double
        get() {
            if (this.segments.isEmpty()) return 0.0
            val pace = this.segments.map { segment -> segment.pace }
            Log.d("Pace", pace.toString())
            return pace.average()
        }

    val duration: Long
        get() {
            if (this.segments.isEmpty()) return 0
            val start = this.segments.first().start.time
            val end = this.segments.last().end.time
            return end.minus(start)
        }

    val points: List<Point>
        get() = this.segments.map { segment -> segment.start }

    fun addLocation(location: Location) {
        val end = Point(location.latitude, location.longitude, location.time)
        var start = end.copy()
        if (this.segments.size > 0) start = this.segments.last().end

        this.segments.add(Segment(start, end))
    }

    fun serialise(): String {
        return Gson().toJson(this, Track.GSON_TYPE)
    }
}