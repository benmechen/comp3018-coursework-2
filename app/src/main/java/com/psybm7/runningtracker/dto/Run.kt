package com.psybm7.runningtracker.dto

import java.io.Serializable
import java.time.Instant

data class Run(
    val name: String,
    val start: Instant,
    val end: Instant,
    val distance: Int,
    val pace: Double,
    val rating: Float) : Serializable {
}