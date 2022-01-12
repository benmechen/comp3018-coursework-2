package com.psybm7.runningtracker

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.psybm7.runningtracker.databinding.ActivityRunBinding
import com.psybm7.runningtracker.run.Run
import java.time.Duration
import java.time.temporal.TemporalUnit
import java.util.concurrent.TimeUnit

/**
 * RunActivity to display a recorded Run
 */
class RunActivity : AppCompatActivity(), OnMapReadyCallback {
    /**
     * Binding to update the UI
     */
    private lateinit var binding: ActivityRunBinding

    /**
     * Run to display
     */
    private lateinit var run: Run

    companion object {
        const val RUN = "com.psybm7.runningtracker.RunActivity.UPDATE_RUN_REPLY"
    }

    /**
     * 1. Set up Data Binding
     * 2. Deserialise Run
     * 3. Set UI components to Run values
     * 4. Connect map to this Activity
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_run)

        val bundle = intent.extras
        this.run = bundle?.getSerializable("run") as Run? ?: throw Error("No run provided")

        binding.tvRunTitle.text = run.name
        binding.txRunName.setText(run.name)
        binding.rbRating.rating = run.rating ?: 0f
        binding.tvCardPace.text = HelperService.formatPace(run.pace)
        binding.tvCardDistance.text = HelperService.formatDistance(run.distance)
        binding.tvCardDuration.text = HelperService.formatDuration(run.duration)
        val mapFragment = supportFragmentManager.findFragmentById(binding.mvRoute.id) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    /**
     * When map has initialised, draw the start
     * point for each segment on the map
     */
    override fun onMapReady(map: GoogleMap) {
        if (this.run.track?.points?.isEmpty()!!) return

        val builder = LatLngBounds.Builder()

        val points = this.run.track?.points?.map { point -> {}
            val latLng = LatLng(point.latitude, point.longitude)
            builder.include(latLng)

            latLng
        }
            ?: return

        val polyline =
            PolylineOptions()
                .clickable(true).addAll(points)

        map.addPolyline(polyline)
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 50))
    }

    /**
     * When saving a Run, get the current values
     * and pass to parent Activity for saving
     */
    fun onSaveClick(view: View) {
        run.rating = binding.rbRating.rating
        run.name = binding.txRunName.text.toString()

        val bundle = Bundle()
        bundle.putSerializable(RUN, this.run)

        val intent = Intent()
        intent.putExtras(bundle)

        setResult(Activity.RESULT_OK, intent)

        finish()
    }
}