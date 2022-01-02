package com.psybm7.runningtracker

import android.location.Location
import android.location.LocationListener
import android.util.Log
import com.psybm7.runningtracker.dto.Point
import com.psybm7.runningtracker.dto.Segment

class LocationTracker: LocationListener {
    private val track: MutableList<Segment> = mutableListOf()

    override fun onLocationChanged(location: Location) {
        Log.d("LocationTracker", "Location changed")

        val end = Point(location.latitude, location.longitude, location.time)
        var start = end.copy()
        if (this.track.size > 0) start = this.track.last().end

        this.track.add(Segment(start, end))
    }

    override fun onProviderEnabled(provider: String) {
        Log.d("LocationTracker", "Provider enabled")
    }

    override fun onProviderDisabled(provider: String) {
        Log.d("LocationTracker", "Provider disabled")
    }

    fun getDistance(): Float {
        if (this.track.size == 0) return 0.0F
        val distance = track.map { segment -> segment.distance }
        return distance.sum()
    }
}