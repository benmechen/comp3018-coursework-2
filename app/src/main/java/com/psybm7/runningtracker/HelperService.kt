package com.psybm7.runningtracker

import java.util.concurrent.TimeUnit
import kotlin.math.round

/**
 * Collection of helper functions to use across the application
 * Abstracted out to `HelperService` to allow them to be unit tested
 */
class HelperService {
    companion object {
        /**
         * Calculate the running pace from distance and duration (km / m).
         * `time / distance`
         * @param distance Distance in meters
         * @param duration Duration in minutes
         * @return Pace (km / m)
         */
        fun calculatePace(distance: Float, duration: Long): Double {
            val kilometers = this.metersToKilometers(distance)
            val minutes = this.millisecondsToMinutes(duration)

            if (kilometers == 0.0 || minutes == 0.0) return 0.0

            return round(minutes.div(kilometers))
        }

        /**
         * Convert meters to kilometers.
         * `meters / 1000`
         * @param value Meters
         * @return Kilometers
         */
        private fun metersToKilometers(value: Float): Double {
            return value.toDouble().div(1000)
        }

        /**
         * Convert milliseconds to minutes
         * `milliseconds / 1000 / 60`
         * @param milliseconds Milliseconds
         * @return Minutes
         */
        private fun millisecondsToMinutes(milliseconds: Long): Double {
            return milliseconds.toDouble().div(1000).div(60)
        }

        /**
         * Format meters into a displayable format
         * @param distance Distance in meters
         * @return Meters or Kilometers (string)
         */
        fun formatDistance(distance: Int): String {
            val kilometers = this.metersToKilometers(distance.toFloat())
            if (kilometers > 1) return kilometers.toString().take(4).plus(" km")
            return distance.toString().plus(" m")
        }

        /**
         * Format a duration into a displayable format
         * @param milliseconds Duration
         * @return HH:MM:SS format string
         */
        fun formatDuration(milliseconds: Long): String {
            val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds).toInt() % 60
            val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds).toInt() % 60
            val hours = TimeUnit.MILLISECONDS.toHours(milliseconds).toInt() % 24

            return when {
                hours > 0 -> String.format("%d:%02d:%02d", hours, minutes, seconds)
                minutes > 0 -> String.format("%02d:%02d", minutes, seconds)
                seconds > 0 -> String.format("00:%02d", seconds)
                else -> "00:00"
            }
        }

        /**
         * Format pace into a displayable format
         * @param pace Pace
         * @return pace m/km
         */
        fun formatPace(pace: Double): String {
            return pace.toString().take(4).plus(" m/km")
        }

    }
}