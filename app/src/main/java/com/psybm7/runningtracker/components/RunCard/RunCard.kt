package com.psybm7.runningtracker.components.RunCard

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.psybm7.runningtracker.R

class RunCard : Fragment() {

    companion object {
        fun newInstance() = RunCard()
    }

    private lateinit var viewModel: RunCardViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.run_card, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(RunCardViewModel::class.java)
        // TODO: Use the ViewModel
    }

}