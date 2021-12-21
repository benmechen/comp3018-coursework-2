package com.psybm7.runningtracker

import android.app.Activity
import android.content.Intent
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.psybm7.runningtracker.databinding.ActivityMainBinding
import com.psybm7.runningtracker.dto.Run
import java.time.Instant

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    /**
     * Launch `BrushActivity` and provide a handler for the
     * resulting value.
     *
     * When a result is returned, update the view model with the new brush config.
     */
    private val runLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//        if (result.resultCode == Activity.RESULT_OK) {
//            val data: Intent? = result.data
//            val bundle = data?.extras
//            val size = bundle?.getInt("width")
//            val cap = bundle?.get("cap")
//
//            if (size != null && cap != null) {
//                // Cast cap to the enum value - this is dangerous but is the only way to pass enum values through intents?
//                this.viewModel.setBrush(Brush(size, cap as Paint.Cap))
//            }
//        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val dummyData = listOf(
            Run(
                "2021-08-08",
                Instant.parse("2021-08-08T09:00:00.00Z"),
                Instant.parse("2021-08-08T10:00:00.00Z"),
                5,
                6.5,
                3F
            ),
            Run(
                "Evening Run",
                Instant.parse("2021-08-10T18:00:00.00Z"),
                Instant.parse("2021-08-10T18:30:00.00Z"),
                6,
                7.0,
                5F
            ),
            Run(
                "2021-08-12",
                Instant.parse("2021-08-12T08:30:00.00Z"),
                Instant.parse("2021-08-12T09:00:00.00Z"),
                3,
                7.4,
                3F
            ),        )

        val adapter = RunListAdapter(dummyData) {
            this.onRunClick(it)
        }
        binding.rvRunList.adapter = adapter
        binding.rvRunList.layoutManager = GridLayoutManager(this, 2)
    }

    fun onNewRunClick(view: View) {
        val intent = Intent(this, NewRun::class.java)

        runLauncher.launch(intent)
    }

    private fun onRunClick(run: Run) {
        val intent = Intent(this, RunActivity::class.java)

        val bundle = Bundle()
        bundle.putSerializable("run", run)

        intent.putExtras(bundle)

        runLauncher.launch(intent)
    }
}