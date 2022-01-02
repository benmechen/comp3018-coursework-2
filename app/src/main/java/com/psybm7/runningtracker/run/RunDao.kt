package com.psybm7.runningtracker.run

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao()
interface RunDao {
    @Query("SELECT * FROM runs ORDER BY start DESC")
    fun getRuns(): Flow<List<Run>>

    @Insert()
    suspend fun insert(run: Run)

    @Update()
    suspend fun update(run: Run)

    @Query("DELETE FROM runs")
    fun deleteAll()
}