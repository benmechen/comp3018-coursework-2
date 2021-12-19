package com.psybm7.runningtracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.psybm7.runningtracker.dto.Run
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

class RunListAdapter(private val runs: List<Run>): RecyclerView.Adapter<RunListAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvRunDate: TextView = view.findViewById(R.id.tvRunDate)
        private val tvRunDistance: TextView = view.findViewById(R.id.tvRunDistance)

        fun setRun(run: Run) {
            this.tvRunDistance.text = run.distance.toString().plus(" km")

            val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                .withLocale(Locale.UK)
                .withZone(ZoneId.systemDefault())

            this.tvRunDate.text = formatter.format(run.date)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val runCardView = LayoutInflater.from(parent.context).inflate(R.layout.run_card, parent, false)

        return ViewHolder(runCardView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val run = this.runs[position]
        holder.setRun(run)
    }

    override fun getItemCount(): Int {
        return this.runs.size
    }
}