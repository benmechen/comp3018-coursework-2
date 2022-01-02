package com.psybm7.runningtracker

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
            val run = data?.extras?.getSerializable(NewRunActivity.EXTRA_REPLY) as? Run?

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
}