package com.psybm7.runningtracker

import android.app.Application
import com.psybm7.runningtracker.run.RunRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class RunsApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())

    private val database by lazy { RunRoomDatabase.getDatabase(this, this.applicationScope) }
    val repository by lazy { RunRepository(database.runDao()) }
}