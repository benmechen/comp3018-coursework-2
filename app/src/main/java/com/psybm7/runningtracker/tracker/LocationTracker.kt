package com.psybm7.runningtracker.tracker

import android.location.Location
import android.location.LocationListener
import android.util.Log
import com.psybm7.runningtracker.tracker.dto.Track

class LocationTracker: LocationListener {
    val track = Track()

    private val subscribers: MutableList<TrackerSubscriber> = mutableListOf()

    fun addSubscriber(subscriber: TrackerSubscriber) {
        this.subscribers.add(subscriber)
    }

    override fun onLocationChanged(location: Location) {
        Log.d("LocationTracker", "Location changed")
        this.track.addLocation(location)
        this.subscribers.forEach { subscriber -> subscriber.onTrackUpdate(this.track) }
    }

    override fun onProviderEnabled(provider: String) {
        Log.d("LocationTracker", "Provider enabled")
    }

    override fun onProviderDisabled(provider: String) {
        Log.d("LocationTracker", "Provider disabled")
    }
}