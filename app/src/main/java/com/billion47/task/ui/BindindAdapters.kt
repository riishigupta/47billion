package com.billion47.task.ui

import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import java.io.File

@BindingAdapter("localImageFile")
fun bindLocalImageFile(view: ImageView, file: File?) {
    if (file == null || !file.exists()) {
        view.visibility = View.INVISIBLE
        return
    }
    
    view.visibility = View.VISIBLE
    Glide.with(view.context)
        .load(Uri.fromFile(file))
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(view)
}

@BindingAdapter("scaleMode")
fun bindScaleMode(view: ImageView, mode: String?) {
    when (mode) {
        "crop" -> view.scaleType = ImageView.ScaleType.CENTER_CROP
        "stretch" -> view.scaleType = ImageView.ScaleType.FIT_XY
        else -> view.scaleType = ImageView.ScaleType.CENTER_INSIDE
    }
}

@BindingAdapter("playerResize")
fun bindPlayerResize(playerView: PlayerView, mode: String?) {
    playerView.resizeMode = when (mode) {
        "crop" -> AspectRatioFrameLayout.RESIZE_MODE_ZOOM
        "stretch" -> AspectRatioFrameLayout.RESIZE_MODE_FILL
        else -> AspectRatioFrameLayout.RESIZE_MODE_FIT
    }
}

@BindingAdapter("downloadProgress")
fun bindDownloadProgress(overlay: ConstraintLayout, progress: Map<String, Int>?) {
    if (progress == null || progress.isEmpty()) {
        overlay.visibility = View.GONE
        return
    }
    
    // Show overlay if any downloads are in progress
    val hasActiveDownloads = progress.values.any { it < 100 }
    overlay.visibility = if (hasActiveDownloads) View.VISIBLE else View.GONE
}