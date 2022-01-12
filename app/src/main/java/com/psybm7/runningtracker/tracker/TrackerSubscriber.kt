package com.psybm7.runningtracker.tracker

import com.psybm7.runningtracker.tracker.dto.Track

/**
 * Subscriber to track change updates
 */
interface TrackerSubscriber {
    fun onTrackUpdate(track: Track)
}