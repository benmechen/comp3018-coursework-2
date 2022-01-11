package com.psybm7.runningtracker.tracker.dto

import java.io.Serializable

data class Point(val latitude: Double, val longitude: Double, val time: Long) : Serializable {
}