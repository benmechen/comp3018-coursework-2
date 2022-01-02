package com.psybm7.runningtracker

import android.Manifest
import android.content.Context
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.psybm7.runningtracker.databinding.ActivityNewRunBinding
import java.time.Instant

enum class NewRunState {
    RUNNING,
    PAUSED,
    STOPPED
}

class NewRunActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewRunBinding

    private lateinit var start: Instant

    private lateinit var state: NewRunState

    private lateinit var locationManager: LocationManager

    private val locationTracker = LocationTracker()

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { granted ->
            if (granted.all { permission -> permission.value == false }) finish()
        }

    companion object {
        const val EXTRA_REPLY = "com.psybm7.runningtracker.NewRunActivity.NEW_RUN_REPLY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_run)

        this.state = NewRunState.RUNNING
        start = Instant.now()

        this.locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        this.play()
    }

    fun onStopClick(view: View) {
        this.binding.vtTimer.stop()
        this.state = NewRunState.STOPPED
    }

    fun onPlayPauseClick(view: View) {
        if (this.state == NewRunState.RUNNING) {
            this.pause()
        } else {
            this.play()
        }
    }

    private fun play() {
        this.requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
        try {
            this.locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5.toLong(), // minimum time interval between updates
                5.toFloat(), // minimum distance between updates, in metres
                this.locationTracker
            );
        } catch (e: SecurityException) {
            Log.e("NewRunActivity", e.toString())
        }

        this.binding.vtTimer.start()
        this.binding.ibPlayPause.setImageResource(android.R.drawable.ic_media_pause)
        this.state = NewRunState.RUNNING
    }

    private fun pause() {
        this.binding.vtTimer.stop()
        this.binding.ibPlayPause.setImageResource(android.R.drawable.ic_media_play)
        this.state = NewRunState.PAUSED

        Log.d("NewRunActivity", this.locationTracker.getDistance().toString())
    }
}