package com.psybm7.runningtracker

class RunContentProviderContract {
    companion object {
        val AUTHORITY = "com.psybm7.runningtracker.RunContentProvider"

        val CONTENT_TYPE_SINGLE = "vnd.android.cursor.item/RunContentProvider.data.text"

        val CONTENT_TYPE_MULTIPLE = "vnd.android.cursor.dir/RunContentProvider.data.text"
    }
}