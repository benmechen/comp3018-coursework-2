package com.psybm7.runningtracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.psybm7.runningtracker.run.Run
import java.util.*

class RunListAdapter(private val runs: List<Run>, private val onItemClicked: (Run) -> Unit) :
    RecyclerView.Adapter<RunListAdapter.ViewHolder>() {
    class ViewHolder(view: View, private val onItemClicked: (Int) -> Unit) :
        RecyclerView.ViewHolder(view) {
        private val tvRunName: TextView = view.findViewById(R.id.tvRunName)
        private val tvRunDistance: TextView = view.findViewById(R.id.tvRunDistance)

        init {
            itemView.setOnClickListener {
                onItemClicked(adapterPosition)
            }
        }

        fun setRun(run: Run) {
            this.tvRunName.text = run.name
            this.tvRunDistance.text = HelperService.formatDistance(run.distance)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val runCardView =
            LayoutInflater.from(parent.context).inflate(R.layout.run_card, parent, false)

        return ViewHolder(runCardView) {
            onItemClicked(runs[it])
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val run = this.runs[position]
        holder.setRun(run)
    }

    override fun getItemCount(): Int {
        return this.runs.size
    }
}