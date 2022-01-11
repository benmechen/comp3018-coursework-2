package com.psybm7.runningtracker.run

import android.database.Cursor
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao()
interface RunDao {
    @Query("SELECT * FROM runs ORDER BY start DESC")
    fun getRuns(): Flow<List<Run>>

    @Query("SELECT * FROM runs WHERE id = :id")
    fun findByID(id: Int): Cursor

    @Query("SELECT * FROM runs WHERE name LIKE :query")
    fun queryRuns(query: String?): Cursor

    @Insert()
    suspend fun insert(run: Run)

    @Update()
    suspend fun update(run: Run)

    @Query("DELETE FROM runs")
    fun deleteAll()
}