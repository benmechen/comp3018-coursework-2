package com.psybm7.runningtracker.tracker.dto

import java.io.Serializable

/**
 * Individual point on a segment.
 * Holds a position and the time at which the user was there
 */
data class Point(val latitude: Double, val longitude: Double, val time: Long) : Serializable {
}