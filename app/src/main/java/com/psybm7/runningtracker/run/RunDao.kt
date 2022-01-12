package com.psybm7.runningtracker.run

import android.database.Cursor
import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * DAO to interface with the local database
 */
@Dao()
interface RunDao {
    /**
     * Get all runs ordered by start date (descending)
     */
    @Query("SELECT * FROM runs ORDER BY start DESC")
    fun getRuns(): Flow<List<Run>>

    /**
     * Find a Run by its ID
     */
    @Query("SELECT * FROM runs WHERE id = :id")
    fun findByID(id: Int): Cursor

    /**
     * Get a cursor of all runs
     */
    @Query("SELECT * FROM runs WHERE name LIKE :query")
    fun queryRuns(query: String?): Cursor

    /**
     * Create a new Run
     */
    @Insert()
    suspend fun insert(run: Run)

    /**
     * Update an existing run
     */
    @Update()
    suspend fun update(run: Run)

    /**
     * Clear local table
     */
    @Query("DELETE FROM runs")
    fun deleteAll()
}