package com.psybm7.runningtracker

import androidx.room.TypeConverter
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

}