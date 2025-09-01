package com.billion47.task.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.media3.common.MediaItem as ExoMediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.billion47.task.databinding.ActivityMainBinding
import com.billion47.task.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val vm: MainViewModel by viewModels()
    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.lifecycleOwner = this
        binding.vm = vm
        
        setupVideoPlayer()
        setupObservers()
    }

    private fun setupVideoPlayer() {
        player = ExoPlayer.Builder(this).build()
        binding.playerView.player = player
        binding.playerView.useController = true // Enable controls for better UX
    }

    private fun setupObservers() {
        vm.videos.observe(this) { videoFiles ->
            if (videoFiles.isNotEmpty()) {
                setupVideoPlaylist(videoFiles)
            }
        }

        // Observe video scale changes
        vm.videoScale.observe(this) { scale ->
            // The binding adapter will handle the scale change
        }

        // Observe right top image
        vm.rightTop.observe(this) { file ->
            // The binding adapter will handle the image display
        }

        // Observe right bottom image
        vm.rightBottom.observe(this) { file ->
            // The binding adapter will handle the image display
        }
    }

    private fun setupVideoPlaylist(videoFiles: List<java.io.File>) {
        player?.let { exoPlayer ->
            exoPlayer.clearMediaItems()
            
            if (videoFiles.isNotEmpty()) {
                val mediaItems = videoFiles.map { file ->
                    ExoMediaItem.fromUri(file.toUri())
                }
                
                exoPlayer.addMediaItems(mediaItems)
                exoPlayer.repeatMode = ExoPlayer.REPEAT_MODE_ALL
                exoPlayer.prepare()
                exoPlayer.playWhenReady = true
            }
        }
    }

    // Method to manually refresh media (can be called from UI if needed)
    fun refreshMedia() {
        vm.refreshMedia()
    }

    override fun onResume() {
        super.onResume()
        player?.play()
    }

    override fun onPause() {
        super.onPause()
        player?.pause()
    }

    override fun onDestroy() {
        player?.release()
        player = null
        super.onDestroy()
    }
}


