package com.psybm7.runningtracker

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.psybm7.runningtracker.databinding.ActivityMainBinding
import com.psybm7.runningtracker.run.Run
import com.psybm7.runningtracker.run.RunViewModel
import com.psybm7.runningtracker.run.RunViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val runViewModel: RunViewModel by viewModels {
        RunViewModelFactory((application as RunsApplication).repository)
    }

    /**
     * Launch `RunActivity` and provide a handler for the
     * resulting value.
     *
     * When a result is returned, update the view model with the new brush config.
     */
    private val newRunLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
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
    private val runLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Update existing run
            val data: Intent? = result.data
            val run = data?.extras?.getSerializable(RunActivity.RUN) as? Run?

            if (run != null) {
                this.runViewModel.update(run)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        runViewModel.runs.observe(this, Observer { runs ->
            val adapter = RunListAdapter(runs) {
                this.onRunClick(it)
            }
            binding.rvRunList.adapter = adapter
            binding.rvRunList.layoutManager = GridLayoutManager(this, 2)

            binding.tvCardAveragePace.text = HelperService.formatPace(this.calculateAveragePace(runs))
            binding.tvCardTotalDistance.text = HelperService.formatDistance(this.calculateTotalDistance(runs))
            binding.tvCardTotalDuration.text = HelperService.formatDuration(this.calculateTotalDuration(runs))
        })
    }

    fun onNewRunClick(view: View) {
        val intent = Intent(this, NewRunActivity::class.java)

        newRunLauncher.launch(intent)
    }

    private fun onRunClick(run: Run) {
        val intent = Intent(this, RunActivity::class.java)

        val bundle = Bundle()
        bundle.putSerializable("run", run)

        intent.putExtras(bundle)

        runLauncher.launch(intent)
    }

    private fun calculateTotalDistance(runs: List<Run>): Int {
        val distances = runs.map { run -> run.distance }
        return distances.sum();
    }

    private fun calculateTotalDuration(runs: List<Run>): Long {
        val durations = runs.map { run -> run.duration }
        return durations.sum();
    }

    private fun calculateAveragePace(runs: List<Run>): Double {
        val paces = runs.mapNotNull { if (it.pace.isInfinite() || it.pace.isNaN()) null else it.pace }
        return paces.average();
    }
}