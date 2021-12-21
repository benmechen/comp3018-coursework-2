package com.psybm7.runningtracker

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.View
import androidx.databinding.DataBindingUtil
import com.psybm7.runningtracker.databinding.ActivityRunBinding
import com.psybm7.runningtracker.dto.Run
import java.time.Duration

class RunActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRunBinding

    private lateinit var run: Run

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_run)

        val bundle = intent.extras
        this.run = bundle?.getSerializable("run") as Run? ?: throw Error("No run provided")

        binding.tvRunTitle.text = run.name
        binding.txRunName.setText(run.name)
        binding.rbRating.rating = run.rating
        binding.tvCardPace.text = run.pace.toString()
        binding.tvCardDistance.text = run.distance.toString().plus(" km")

        val duration = Duration.between(run.start, run.end)
        binding.tvCardDuration.text = duration.toMinutes().toString().plus(" mins")
    }

    fun onSaveClick(view: View) {
        val bundle = Bundle()
        bundle.putSerializable("run", this.run)

        val intent = Intent()
        intent.putExtras(bundle)

        setResult(Activity.RESULT_OK, intent)

        finish()
    }
}