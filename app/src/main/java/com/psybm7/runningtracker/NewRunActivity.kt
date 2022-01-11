package com.psybm7.runningtracker

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.psybm7.runningtracker.databinding.ActivityNewRunBinding
import com.psybm7.runningtracker.run.Run
import com.psybm7.runningtracker.tracker.TrackerService
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import kotlin.math.round

enum class NewRunState {
    RUNNING,
    PAUSED,
    STOPPED
}

class NewRunActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewRunBinding

    private lateinit var start: Instant

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { granted ->
            if (granted.all { permission -> permission.value == false }) finish()
        }


    private var service: TrackerService? = null

    /**
     * Has the activity been bound to the service?
     */
    private var bound: Boolean = false

    /**
     * Connection to get the service instance
     * and handle disconnecting
     */
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, iBinder: IBinder?) {
            val binder = iBinder as TrackerService.TrackerBinder
            service = binder.service
            bound = true
            onBound()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            bound = false
        }
    }

    companion object {
        const val RUN = "com.psybm7.runningtracker.NewRunActivity.NEW_RUN_REPLY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_run)

        start = Instant.now()

        this.requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    override fun onStart() {
        super.onStart()
        this.bindService()
    }

    override fun onStop() {
        super.onStop()
        this.unbindService()
    }

    /**
     * Start the MP3Service as a Foreground Service
     * Bind to it to receive updates
     */
    private fun bindService() {
        val intent = Intent(this, TrackerService::class.java)
        startForegroundService(intent)
        bindService(intent, this.serviceConnection, Context.BIND_AUTO_CREATE)
    }

    /**
     * Unbind from service,
     * but keep it running in the background so music keeps playing
     */
    private fun unbindService() {
        Intent(this, TrackerService::class.java).also {
            unbindService(serviceConnection)
        }
    }

    /**
     * When bound to the service,
     * register observers on it's states
     */
    private fun onBound() {
        this.service?.state?.value = NewRunState.PAUSED
        this.service?.state?.observe(this, { state ->
            Log.d("State Update", state.toString())
            when (state) {
                NewRunState.RUNNING -> this.onRunPlay()
                NewRunState.PAUSED -> this.onRunPause()
                NewRunState.STOPPED -> this.onRunStop()
            }
        })
        this.service?.distance?.observe(this, { distance ->
            this.binding.tvRunDistance.text = HelperService.formatDistance(distance.toInt())
        })
        this.service?.pace?.observe(this, { pace ->
            this.binding.tvRunPace.text = HelperService.formatPace(pace)
        })

//        Start run
        this.service?.play();
    }

//    Button handlers
    fun onStopClick(view: View) {
        this.service?.stop()
    }

    fun onPlayPauseClick(view: View) {
        if (this.service?.state?.value == NewRunState.RUNNING) {
            this.service?.pause()
        } else {
            this.service?.play()
        }
    }

//    Callbacks
    private fun onRunPlay() {
        this.binding.vtTimer.start()
        this.binding.ibPlayPause.setImageResource(android.R.drawable.ic_media_pause)
    }

    private fun onRunPause() {
        this.binding.vtTimer.stop()
        this.binding.ibPlayPause.setImageResource(android.R.drawable.ic_media_play)
    }

    private fun onRunStop() {
        this.binding.vtTimer.stop()

        val duration = this.getElapsedMilliseconds(this.binding.vtTimer.text.toString())

        val run = Run(
            this.generateName(this.start),
            this.start,
            duration,
            this.service?.locationTracker?.track?.distance?.toInt() ?: 0,
            null,
            this.service?.locationTracker?.track
        )

        val bundle = Bundle()
        bundle.putSerializable(NewRunActivity.RUN, run)

        val intent = Intent()
        intent.putExtras(bundle)

        setResult(Activity.RESULT_OK, intent)

        finish()
    }

//    Helpers
    private fun generateName(date: Instant): String {
        val formatter = DateTimeFormatter
            .ofLocalizedDateTime(FormatStyle.MEDIUM)
            .withZone(ZoneId.systemDefault())
            .withLocale(Locale.UK)

        return formatter.format(date)
    }

    private fun getElapsedMilliseconds(duration: String): Long {
        val parts = duration.split(":")

        if (parts.size < 2 || parts.size > 3) return 0

        var seconds: Long
        var minutes: Long
        var hours: Long = 0

        try {
            if (parts.size == 2) {
                seconds = parts[1].toLong()
                minutes = parts[0].toLong()
            } else {
                seconds = parts[2].toLong()
                minutes = parts[1].toLong()
                hours = parts[0].toLong()
            }
        } catch (e: NumberFormatException) {
            seconds = 0
            minutes = 0
        }

        return (seconds * 1000) + (minutes * 60000) + (hours * 3600000)
    }
}