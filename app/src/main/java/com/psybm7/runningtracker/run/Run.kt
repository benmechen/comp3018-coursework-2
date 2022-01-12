package com.psybm7.runningtracker.run

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.psybm7.runningtracker.tracker.dto.Track
import java.io.Serializable
import java.time.Instant
import kotlin.math.round

/**
 * Run entity.
 * Saves Runs in a local table
 */
@Entity(tableName = "runs")
class Run(
    var name: String,
    val start: Instant,
    val duration: Long,
    val distance: Int,
    var rating: Float?,
    var track: Track?
) : Serializable {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var pace: Double = 0.0

    init {
        if (track != null)
            this.pace = track!!.pace
    }
}