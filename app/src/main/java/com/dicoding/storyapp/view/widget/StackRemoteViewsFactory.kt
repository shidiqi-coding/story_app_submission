package com.dicoding.storyapp.view.widget



import com.dicoding.storyapp.R
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService.RemoteViewsFactory
import com.bumptech.glide.Glide
import com.dicoding.storyapp.data.response.ListStoryItem
import com.dicoding.storyapp.data.retrofit.ApiConfig
import com.dicoding.storyapp.data.pref.UserPreference
import com.dicoding.storyapp.view.setting.SettingPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class StackRemoteViewsFactory(private val context: Context) : RemoteViewsFactory {

    private var storyList = ArrayList<ListStoryItem>()

    override fun onCreate() {}

    override fun onDataSetChanged() {
        try {
            val pref = UserPreference.getInstance(context)
            val token = runBlocking {
                pref.getSession().first().token
            }
            val apiService = ApiConfig.getApiService()
            val response = runBlocking { apiService.getStories("Bearer $token") }

            storyList.clear()
            storyList.addAll(response.listStory ?: emptyList())
        } catch (e: Exception) {
            Log.e("RemoteViewsFactory", "Failed to load stories", e)
        }
    }

    override fun onDestroy() {
        storyList.clear()
    }

    override fun getCount(): Int = storyList.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(context.packageName, R.layout.item_widget)

        val photoUrl = storyList[position].photoUrl
        if (!photoUrl.isNullOrEmpty()) {
            val bitmap = getBitmapFromUrl(photoUrl)
            if (bitmap != null) {
                rv.setImageViewBitmap(R.id.image_view, bitmap)
            } else {
                Log.e("Widget", "Failed to decode bitmap from $photoUrl")
            }
        }

        val fillInIntent = Intent().apply {
            putExtra(ImagesBannerWidget.EXTRA_ITEM, position)
        }
        rv.setOnClickFillInIntent(R.id.image_view, fillInIntent)

        return rv
    }

    override fun getLoadingView(): RemoteViews? = null
    override fun getViewTypeCount(): Int = 1
    override fun getItemId(position: Int): Long = position.toLong()
    override fun hasStableIds(): Boolean = true

    private fun getBitmapFromUrl(url: String?): Bitmap? {
        return try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.inputStream.buffered()
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}
