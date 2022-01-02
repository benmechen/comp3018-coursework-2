package com.psybm7.runningtracker.run

import android.util.Log
import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

/**
 * Wrapper around RunDao.
 * This separates the ViewModel from Database layer, which would
 * allow the app to be connected to multiple backends in the future (such as a web server)
 */
class RunRepository(private val runDao: RunDao) {
    val runs: Flow<List<Run>> = runDao.getRuns()

    @WorkerThread()
    suspend fun insert(run: Run) {
        runDao.insert(run)
    }

    @WorkerThread()
    suspend fun update(run: Run) {
        Log.d("RunRepository", "Updating run")
        runDao.update(run)
    }
}