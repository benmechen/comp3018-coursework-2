package com.psybm7.runningtracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.psybm7.runningtracker.databinding.ActivityMainBinding
import com.psybm7.runningtracker.dto.Run
import java.time.Instant
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val dummyData = listOf(
            Run(Instant.parse("2021-08-08T09:00:00.00Z"), 5),
            Run(Instant.parse("2021-08-10T18:00:00.00Z"), 6),
            Run(Instant.parse("2021-08-12T08:30:00.00Z"), 4)
        )

        val adapter = RunListAdapter(dummyData)
        binding.rvRunList.adapter = adapter
        binding.rvRunList.layoutManager = GridLayoutManager(this, 2)
    }
}