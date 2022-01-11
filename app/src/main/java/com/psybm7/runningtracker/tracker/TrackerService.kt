package com.psybm7.runningtracker.tracker

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.MutableLiveData
import com.psybm7.runningtracker.HelperService
import com.psybm7.runningtracker.NewRunState
import com.psybm7.runningtracker.R
import com.psybm7.runningtracker.tracker.dto.Track
import kotlin.math.round

class TrackerService : Service(), TrackerSubscriber {

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "runningtracker"
        private const val CHANNEL_NAME = "Running Tracker"
    }

    /**
     * Allow Activities to bind to this service for updates
     */
    inner class TrackerBinder: Binder() {
        val service: TrackerService
            get() = this@TrackerService
    }

    private val binder: TrackerBinder = TrackerBinder()

    val state: MutableLiveData<NewRunState> by lazy {
        MutableLiveData(NewRunState.PAUSED)
    }

    val distance: MutableLiveData<Float> by lazy {
        MutableLiveData(0f)
    }

    val pace: MutableLiveData<Double> by lazy {
        MutableLiveData(0.0)
    }

    val locationTracker = LocationTracker()

    private lateinit var locationManager: LocationManager

    private lateinit var notificationManager: NotificationManager

    override fun onBind(intent: Intent): IBinder {
        return this.binder
    }

    override fun onCreate() {
        super.onCreate()

//        Start tracking
        this.locationTracker.addSubscriber(this)
        this.locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        this.notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        this.buildChannel()
        val notification = this.buildNotification(HelperService.formatDistance(0), HelperService.formatDuration(0))

        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

//        Stop tracking
        this.stop()
    }

    override fun onTrackUpdate(track: Track) {
        this.distance.value = track.distance
        this.pace.value = track.pace

        val notification = this.buildNotification(
            HelperService.formatDistance(track.distance.toInt()),
            HelperService.formatDuration(track.duration)
        )

        this.notificationManager.notify(NOTIFICATION_ID, notification)
    }

    @SuppressLint("MissingPermission")
    fun play() {
        try {
            this.locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                2,
                2f,
                this.locationTracker
            );
        } catch (e: SecurityException) {
            Log.e("NewRunActivity", e.toString())
        }
        this.state.value = NewRunState.RUNNING
    }

    fun pause() {
        this.locationManager.removeUpdates(this.locationTracker)
        this.state.value = NewRunState.PAUSED
    }

    fun stop() {
        this.locationManager.removeUpdates(this.locationTracker)
        this.state.value = NewRunState.STOPPED
    }

    /**
     * Build a notification to show the current media title
     * as a foreground notification
     */
    private fun buildNotification(distance: String, duration: String): Notification {
        return Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("Tracking run")
            .setContentText(distance.plus(", ").plus(duration))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .build()
    }

    /**
     * Build a notification channel for the foreground notification
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun buildChannel(): NotificationChannel {
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
        channel.setSound(null, null)
        this.notificationManager.createNotificationChannel(channel)

        return channel
    }}