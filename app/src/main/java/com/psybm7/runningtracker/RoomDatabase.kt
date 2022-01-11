package com.psybm7.runningtracker

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.psybm7.runningtracker.run.Run
import com.psybm7.runningtracker.run.RunDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.Instant

@Database(
    entities = [Run::class],
    version = 6,
//    autoMigrations = [
//        AutoMigration(from = 1, to = 2)
//    ]
)
@TypeConverters(Converters::class)
abstract class RunRoomDatabase : RoomDatabase() {
    abstract fun runDao(): RunDao

    companion object {
        @Volatile
        private var INSTANCE: RunRoomDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): RunRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RunRoomDatabase::class.java,
                    "runDatabase"
                )
                    .addCallback(RunRoomDatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                this.INSTANCE = instance

                instance
            }
        }
    }

    private class RunRoomDatabaseCallback(private val scope: CoroutineScope) :
        RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populate(database.runDao())
                }
            }
        }

        suspend fun populate(runDao: RunDao) {
            runDao.deleteAll()

            // Insert example runs
//            var run = Run("Test Run", Instant.now(), Instant.now(), 5, 7.6, 4f)
//            runDao.insert(run)
//            run = Run("Test Run #2", Instant.now(), Instant.now(), 4, 6.5, 3f)
//            runDao.insert(run)
        }
    }
}