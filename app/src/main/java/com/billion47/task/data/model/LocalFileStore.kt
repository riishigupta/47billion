package com.billion47.task.data.model

import android.content.Context
import java.io.File

class LocalFileStore(private val context: Context) {
    private val root: File by lazy {
        File(context.getExternalFilesDir(null), "media").apply { if (!exists()) mkdirs() }
    }


    fun fileFor(item: MediaItem): File {
        val filename = if (item.name.isNotBlank()) {
            item.name
        } else {
            "${item.id}_${item.src.hashCode()}"
        }
        return File(root, filename)
    }

    fun hasFile(item: MediaItem): Boolean = fileFor(item).exists()
}