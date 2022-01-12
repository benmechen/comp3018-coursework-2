package com.psybm7.runningtracker.tracker

import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Icon
import android.location.LocationManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import com.psybm7.runningtracker.HelperService
import com.psybm7.runningtracker.NewRunState
import com.psybm7.runningtracker.R
import com.psybm7.runningtracker.tracker.dto.Track
import java.security.InvalidParameterException

/**
 * Track activity as a foreground service,
 * showing a notification to the user.
 * Bound service for Activities to receive updates and control the tracking
 */
class TrackerService : Service(), TrackerSubscriber {
    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "runningtracker"
        private const val CHANNEL_NAME = "Running Tracker"
        private const val TRACKER_RECEIVER_PLAY =
            "com.psybm7.runningtracker.TrackerReceiver.PLAY"
        private const val TRACKER_RECEIVER_PAUSE =
            "com.psybm7.runningtracker.TrackerReceiver.PAUSE"
    }

    /**
     * Allow Activities to bind to this service for updates
     */
    inner class TrackerBinder : Binder() {
        val service: TrackerService
            get() = this@TrackerService
    }

    /**
     * Binding instance
     */
    private val binder: TrackerBinder = TrackerBinder()

    /**
     * Broadcast Receiver to receive notification button actions
     */
    inner class TrackerReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                TRACKER_RECEIVER_PLAY -> play()
                TRACKER_RECEIVER_PAUSE -> pause()
                else -> throw InvalidParameterException("Unrecognised action")
            }
        }
    }

    /**
     * Broadcast Receiver instance
     */
    private val receiver = TrackerReceiver()

    /**
     * Current tracking state
     */
    val state: MutableLiveData<NewRunState> by lazy {
        MutableLiveData(NewRunState.PAUSED)
    }

    /**
     * Current distance tracked
     */
    val distance: MutableLiveData<Float> by lazy {
        MutableLiveData(0f)
    }

    /**
     * Current pace
     */
    val pace: MutableLiveData<Double> by lazy {
        MutableLiveData(0.0)
    }

    /**
     * [LocationTracker] instance for handling [Track]s
     */
    val locationTracker = LocationTracker()

    /**
     * Location manager instance
     */
    private lateinit var locationManager: LocationManager

    /**
     * Notification manager instance
     */
    private lateinit var notificationManager: NotificationManager

    override fun onBind(intent: Intent): IBinder {
        return this.binder
    }

    /**
     * 1. Start location tracking and register [LocationTracker] as a subscriber
     * 2. Register the BroadcastReceiver for start/pause actions
     * 3. Create a notification channel and display to user
     */
    override fun onCreate() {
        super.onCreate()

//        Start tracking
        this.locationTracker.addSubscriber(this)
        this.locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        this.notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val filter = IntentFilter().apply {
            addAction(TRACKER_RECEIVER_PLAY)
            addAction(TRACKER_RECEIVER_PAUSE)
        }
        registerReceiver(receiver, filter)

        this.buildChannel()
        val notification =
            this.buildNotification(HelperService.formatDistance(0), HelperService.formatDuration(0))

        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    /**
     * Stop tracking and remove Broadcast Receiver
     */
    override fun onDestroy() {
        super.onDestroy()

//        Stop tracking
        this.stop()

        unregisterReceiver(receiver)
    }

    /**
     * Track was updated, update Activity if open and notification
     */
    override fun onTrackUpdate(track: Track) {
        this.distance.value = track.distance
        this.pace.value = track.pace
        this.updateNotification()
    }

    /**
     * Start location updates and update the notification state
     */
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
        updateNotification()
    }

    /**
     * Pause updates and update notification state
     */
    fun pause() {
        this.locationManager.removeUpdates(this.locationTracker)
        this.state.value = NewRunState.PAUSED
        updateNotification()
    }

    /**
     * Stop location updates and update notification state
     */
    fun stop() {
        this.locationManager.removeUpdates(this.locationTracker)
        this.state.value = NewRunState.STOPPED
        updateNotification()
    }

    /**
     * Update the current notification with the distance and state
     */
    private fun updateNotification() {
        val notification = this.buildNotification(
            HelperService.formatDistance(locationTracker.track.distance.toInt()),
            HelperService.formatDuration(locationTracker.track.duration)
        )

        this.notificationManager.notify(NOTIFICATION_ID, notification)
    }

    /**
     * Build a notification to show the current running statistics
     * as a foreground notification.
     * Add a Play or Pause button depending on the state
     */
    private fun buildNotification(distance: String, duration: String): Notification {
        val action = if (state.value == NewRunState.RUNNING) TRACKER_RECEIVER_PAUSE else TRACKER_RECEIVER_PLAY
        val buttonText = if (state.value == NewRunState.RUNNING) R.string.notification_pause else R.string.notification_play

        val playPauseIntent = Intent(action)
        val playPausePendingIntent = PendingIntent.getBroadcast(this, 0, playPauseIntent, 0)

        val playPauseAction = Notification.Action.Builder(
            Icon.createWithResource(
                this,
                android.R.drawable.ic_media_play
            ), getString(buttonText), playPausePendingIntent
        ).build()

        return Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("Tracking run")
            .setContentText(distance.plus(", ").plus(duration))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .addAction(playPauseAction)
            .build()
    }

    /**
     * Build a notification channel for the foreground notification
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun buildChannel(): NotificationChannel {
        val channel =
            NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
        channel.setSound(null, null)
        this.notificationManager.createNotificationChannel(channel)

        return channel
    }
}