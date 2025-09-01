package com.billion47.task.data.model

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import com.google.android.play.core.ktx.AppUpdateResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File

@SuppressLint("ServiceCast")
class DownloadHelper(private val context: Context) {
    private val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    data class Progress(val id: Long, val downloaded: Long, val done:Boolean, val success:Boolean)

    fun enqueue(url:String,outFile: File):Long{
        val request = DownloadManager.Request(Uri.parse(url))
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationUri(Uri.fromFile(outFile))

        return downloadManager.enqueue(request)

    }
    fun observe(id: Long):Flow<Progress> = flow{
        var finished = false
        while (!finished) {
            val query = DownloadManager.Query().setFilterById(id)
            val cursor = downloadManager.query(query)
            cursor.use {
                if (it.moveToFirst()) {
                    val downloaded =
                        it.getLong(it.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                    val total =
                        it.getLong(it.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                    val status = it.getInt(it.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                    val reasonIndex = it.getColumnIndex(DownloadManager.COLUMN_REASON)
                    val done =
                        status == DownloadManager.STATUS_SUCCESSFUL || status == DownloadManager.STATUS_FAILED
                    val success = status == DownloadManager.STATUS_SUCCESSFUL
                    finished = done
                    emit(Progress(id, downloaded, done, success))
                }
            }
            delay(200L)
        }

    }
}