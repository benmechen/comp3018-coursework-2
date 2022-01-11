package com.psybm7.runningtracker

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import androidx.room.Room
import com.psybm7.runningtracker.run.RunDao
import java.lang.IllegalArgumentException

class RunContentProvider : ContentProvider() {
    companion object {
        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

        init {
            uriMatcher.addURI(
                RunContentProviderContract.AUTHORITY,
                RunContentProviderContract.CONTENT_TYPE_MULTIPLE,
                1
            )
            uriMatcher.addURI(
                RunContentProviderContract.AUTHORITY,
                RunContentProviderContract.CONTENT_TYPE_SINGLE,
                2
            )
        }
    }

    private lateinit var database: RunRoomDatabase

    private var runDao: RunDao? = null

    override fun onCreate(): Boolean {
        database = Room.databaseBuilder(context!!, RunRoomDatabase::class.java, "runs").build()

        runDao = database.runDao()

        return true
    }

    override fun getType(uri: Uri): String {
        return when (uriMatcher.match(uri)) {
            1 -> RunContentProviderContract.CONTENT_TYPE_MULTIPLE
            2 -> RunContentProviderContract.CONTENT_TYPE_SINGLE
            else -> throw IllegalArgumentException("Unknown: $uri")
        }
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        val cursor = when (uriMatcher.match(uri)) {
            1 -> runDao?.queryRuns(null)
            2 -> runDao?.findByID(ContentUris.parseId(uri).toInt())
            else -> null
        }

        cursor?.setNotificationUri(context?.contentResolver, uri)
        return cursor
    }

//    Coursework specification was not clear if these needed to be
//    implemented or not. It said "In principle allowing other applications to make use of the data",
//    which I have interpreted as only needing to query the data, not insert, update or delete it
    override fun insert(p0: Uri, p1: ContentValues?): Uri? {
        TODO("Not yet implemented")
    }

    override fun update(p0: Uri, p1: ContentValues?, p2: String?, p3: Array<out String>?): Int {
        TODO("Not yet implemented")
    }

    override fun delete(p0: Uri, p1: String?, p2: Array<out String>?): Int {
        TODO("Not yet implemented")
    }

}