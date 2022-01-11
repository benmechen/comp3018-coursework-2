package com.psybm7.runningtracker

import androidx.room.TypeConverter
import com.psybm7.runningtracker.tracker.dto.Track
import java.time.Instant

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long): Instant {
        return value.let { Instant.ofEpochMilli(it) }
    }

    @TypeConverter
    fun toTimestamp(value: Instant): Long {
        return value.toEpochMilli()
    }

    @TypeConverter
    fun fromTrack(value: Track): String {
        return value.let { it.serialise() }
    }

    @TypeConverter
    fun toTrack(value: String): Track {
        return Track.deserialise(value)
    }

}