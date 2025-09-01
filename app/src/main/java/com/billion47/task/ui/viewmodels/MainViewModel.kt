package com.billion47.task.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.billion47.task.data.model.DownloadHelper
import com.billion47.task.data.model.JsonLoader
import com.billion47.task.data.model.LocalFileStore
import com.billion47.task.data.model.MediaItem
import com.billion47.task.data.model.MediaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = MediaRepository(
        jsonLoader = JsonLoader(application),
        store = LocalFileStore(application),
        downloader = DownloadHelper(application)
    )

    private val _allItem = MutableLiveData<List<MediaItem>>()
    val allItem: LiveData<List<MediaItem>> = _allItem

    private val _videos = MutableLiveData<List<File>>()
    val videos: LiveData<List<File>> = _videos

    private val _videoScale = MutableLiveData<String>("fit")
    val videoScale: LiveData<String> = _videoScale

    private val _rightTop = MutableLiveData<File?>()
    val rightTop: LiveData<File?> = _rightTop

    private val _rightBottom = MutableLiveData<File?>()
    val rightBottom: LiveData<File?> = _rightBottom

    private val _rightTopScale = MutableLiveData<String>("fit")
    val rightTopScale: LiveData<String> = _rightTopScale

    private val _rightBottomScale = MutableLiveData<String>("fit")
    val rightBottomScale: LiveData<String> = _rightBottomScale

    private val _downloadProgress = MutableLiveData<Map<String, Int>>()
    val downloadProgress: LiveData<Map<String, Int>> = _downloadProgress

    private var imageFileWithScale: List<Pair<File, String>> = emptyList()
    private var isInitialized = false

    init {
        // Set up download complete listener
        repo.setOnDownloadCompleteListener {
            refreshMedia()
        }
        
        initializeMedia()
    }

    private fun initializeMedia() {
        if (isInitialized) return
        
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val items = repo.prepareMedia()
                _allItem.postValue(items)

                // Handle videos
                val videoItems = items.filter { it.type == "video" }
                if (videoItems.isNotEmpty()) {
                    val firstVideo = videoItems.first()
                    _videoScale.postValue(firstVideo.scale)
                    
                    val videoFiles = videoItems.mapNotNull { item ->
                        if (repo.isDownloaded(item)) {
                            repo.resolveLocal(item)
                        } else {
                            null
                        }
                    }
                    _videos.postValue(videoFiles)
                }

                // Handle images
                val imageItems = items.filter { it.type == "image" }
                imageFileWithScale = imageItems.mapNotNull { item ->
                    if (repo.isDownloaded(item)) {
                        repo.resolveLocal(item) to item.scale
                    } else {
                        null
                    }
                }

                // Start image ticker if we have images
                if (imageFileWithScale.isNotEmpty()) {
                    startImageTicker()
                }
                
                isInitialized = true
            } catch (e: Exception) {
                // Handle error
                e.printStackTrace()
            }
        }
    }

    private fun startImageTicker() {
        viewModelScope.launch(Dispatchers.Default) {
            if (imageFileWithScale.isEmpty()) return@launch
            
            val imgs = imageFileWithScale
            var idx = 0
            
            while (true) {
                val first = imgs[idx % imgs.size]
                val second = imgs[(idx + 1) % imgs.size]
                
                _rightTop.postValue(first.first)
                _rightTopScale.postValue(first.second)
                _rightBottom.postValue(second.first)
                _rightBottomScale.postValue(second.second)
                
                idx = (idx + 2) % imgs.size
                delay(3000L)
            }
        }
    }

    // Method to refresh media when new downloads complete
    fun refreshMedia() {
        if (isInitialized) {
            viewModelScope.launch(Dispatchers.IO) {
                val items = _allItem.value ?: return@launch
                
                // Update videos
                val videoItems = items.filter { it.type == "video" }
                val videoFiles = videoItems.mapNotNull { item ->
                    if (repo.isDownloaded(item)) {
                        repo.resolveLocal(item)
                    } else {
                        null
                    }
                }
                _videos.postValue(videoFiles)
                
                // Update images
                val imageItems = items.filter { it.type == "image" }
                imageFileWithScale = imageItems.mapNotNull { item ->
                    if (repo.isDownloaded(item)) {
                        repo.resolveLocal(item) to item.scale
                    } else {
                        null
                    }
                }
                
                // Restart image ticker if needed
                if (imageFileWithScale.isNotEmpty() && _rightTop.value == null) {
                    startImageTicker()
                }
            }
        }
    }
}