package com.psybm7.runningtracker

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.psybm7.runningtracker.databinding.ActivityMainBinding
import com.psybm7.runningtracker.run.Run
import com.psybm7.runningtracker.run.RunViewModel
import com.psybm7.runningtracker.run.RunViewModelFactory

class MainActivity : AppCompatActivity() {
    /**
     * Binding allows the UI to be updated easily
     */
    private lateinit var binding: ActivityMainBinding

    /**
     * View Model fetches Runs and converts List to LiveData
     */
    private val runViewModel: RunViewModel by viewModels {
        RunViewModelFactory((application as RunsApplication).repository)
    }

    /**
     * Launch `RunActivity` and provide a handler for the
     * resulting value.
     *
     * When a result is returned, update the view model with the new brush config.
     */
    private val newRunLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Save new run
                val data: Intent? = result.data
                val run = data?.extras?.getSerializable(NewRunActivity.RUN) as? Run?

                if (run != null) {
                    this.runViewModel.insert(run)
                }
            }
        }

    /**
     * Launch `RunActivity` and provide a handler for the
     * resulting value.
     *
     * When a result is returned, update the view model with the new brush config.
     */
    private val runLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Update existing run
                val data: Intent? = result.data
                val run = data?.extras?.getSerializable(RunActivity.RUN) as? Run?

                if (run != null) {
                    this.runViewModel.update(run)
                }
            }
        }

    /**
     * Launch permissions request window
     * If permissions are granted, start tracking new run
     */
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { granted ->
            if (granted.all { permission -> permission.value == false }) {
                finish()
            } else {
                navigateToNewRunActivity()
            }
        }

    /**
     * 1. Set up DataBinding
     * 2. Observe any changes to the list of Runs
     * 2.1. Set up RecyclerView
     * 2.2. Set main card details
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        runViewModel.runs.observe(this, Observer { runs ->
            val adapter = RunListAdapter(runs) {
                this.onRunClick(it)
            }
            binding.rvRunList.adapter = adapter
            binding.rvRunList.layoutManager = GridLayoutManager(this, 2)

            binding.tvCardAveragePace.text =
                HelperService.formatPace(this.calculateAveragePace(runs))
            binding.tvCardTotalDistance.text =
                HelperService.formatDistance(this.calculateTotalDistance(runs))
            binding.tvCardTotalDuration.text =
                HelperService.formatDuration(this.calculateTotalDuration(runs))
        })
    }

    /**
     * Called when user clicks "+" button
     * Check permissions, navigate if already granted or show popup if not
     */
    fun onNewRunClick(view: View) {
        val fineLocationGranted =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarseLocationGranted =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)

        // If we don't already have permissions, request from user and navigate once granted
        if (fineLocationGranted != PackageManager.PERMISSION_GRANTED || coarseLocationGranted != PackageManager.PERMISSION_GRANTED) {
            return this.requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }

        // Otherwise navigate immediately
        navigateToNewRunActivity()
    }

    private fun navigateToNewRunActivity() {
        val intent = Intent(this, NewRunActivity::class.java)

        newRunLauncher.launch(intent)
    }

    /**
     * Called when user clicks on a previously recorded run
     * Launch RunActivity and pass Run
     */
    private fun onRunClick(run: Run) {
        val intent = Intent(this, RunActivity::class.java)

        val bundle = Bundle()
        bundle.putSerializable("run", run)

        intent.putExtras(bundle)

        runLauncher.launch(intent)
    }

    /**
     * Calculate the total distance from a list of runs
     * @param runs List of run objects
     * @return Total Distance
     */
    private fun calculateTotalDistance(runs: List<Run>): Int {
        val distances = runs.map { run -> run.distance }
        return distances.sum()
    }

    /**
     * Calculate the total duration from a list of runs
     * @param runs List of run objects
     * @return Total Duration
     */
    private fun calculateTotalDuration(runs: List<Run>): Long {
        val durations = runs.map { run -> run.duration }
        return durations.sum()
    }

    /**
     * Calculate the average pace from a list of runs
     * Filters out any corrupted runs without a pace
     * @param runs List of run objects
     * @return Average Pace
     */
    private fun calculateAveragePace(runs: List<Run>): Double {
        val paces =
            runs.mapNotNull { if (it.pace.isInfinite() || it.pace.isNaN()) null else it.pace }
        return paces.average()
    }
}