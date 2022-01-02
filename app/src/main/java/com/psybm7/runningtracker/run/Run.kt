package com.psybm7.runningtracker.run

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.time.Instant

@Entity(tableName = "runs")
data class Run(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val start: Instant,
    val end: Instant,
    val distance: Int,
    val pace: Double,
    val rating: Float) : Serializable {
}