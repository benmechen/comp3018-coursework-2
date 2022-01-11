package com.psybm7.runningtracker.tracker

import com.psybm7.runningtracker.tracker.dto.Track

interface TrackerSubscriber {
    fun onTrackUpdate(track: Track)
}