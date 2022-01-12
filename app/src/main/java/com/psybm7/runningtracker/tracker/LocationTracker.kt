package com.psybm7.runningtracker.tracker

import android.location.Location
import android.location.LocationListener
import android.util.Log
import com.psybm7.runningtracker.tracker.dto.Track

/**
 * Listener for location updates
 */
class LocationTracker: LocationListener {
    /**
     * Track containing points tracked to
     */
    val track = Track()

    /**
     * Track update subscribers
     */
    private val subscribers: MutableList<TrackerSubscriber> = mutableListOf()

    /**
     * Add a new subscriber to track updates
     */
    fun addSubscriber(subscriber: TrackerSubscriber) {
        this.subscribers.add(subscriber)
    }

    override fun onLocationChanged(location: Location) {
        Log.d("LocationTracker", "Location changed")
        this.track.addLocation(location)
        this.subscribers.forEach { subscriber -> subscriber.onTrackUpdate(this.track) }
    }
}