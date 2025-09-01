package com.billion47.task.data.model

import java.io.File

class MediaRepository(
    private val jsonLoader: JsonLoader,
    private val store: LocalFileStore,
    private val downloader: DownloadHelper
) {
    private var onDownloadComplete: (() -> Unit)? = null
    
    fun setOnDownloadCompleteListener(listener: () -> Unit) {
        onDownloadComplete = listener
    }
    
    suspend fun prepareMedia(): List<MediaItem> {
        val items = jsonLoader.load()
        
        for (item in items) {
            val outFile = store.fileFor(item)
            
            // Only download if file doesn't exist
            if (!outFile.exists()) {
                val downloadId = downloader.enqueue(item.src, outFile)
                downloader.observe(downloadId).collect { process ->
                    if (process.done) {
                        if (process.success) {
                            // File downloaded successfully, notify listener
                            onDownloadComplete?.invoke()
                        } else {
                            // Download failed, clean up partial file
                            if (outFile.exists()) {
                                outFile.delete()
                            }
                        }

                    }
                }
            }
        }
        
        return items
    }

    fun resolveLocal(item: MediaItem): File = store.fileFor(item)
    
    fun isDownloaded(item: MediaItem): Boolean = store.hasFile(item)
}