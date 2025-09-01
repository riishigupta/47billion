package com.billion47.task.data.model

import android.content.Context
import com.google.gson.JsonObject
import org.json.JSONObject

class JsonLoader(private val context: Context) {
    fun load(): List<MediaItem> {
        val text = context.assets.open("download.json").bufferedReader().use { it.readText() }
        val root = JSONObject(text)
        val arr = root.getJSONArray("downloadable")
        val list = mutableListOf<MediaItem>()
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            val id = obj.getString("id")
            val type = obj.getString("type")
            val name = obj.getString("name")
            val src = obj.getString("src")
            val scale = obj.getString("scale")
            list.add(
                MediaItem(
                    id = id,
                    type = type,
                    name = name,
                    src = src,
                    scale = scale
                )
            )

        }
        return list
    }
}