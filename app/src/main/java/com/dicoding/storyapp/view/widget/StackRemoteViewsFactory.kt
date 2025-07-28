package com.dicoding.storyapp.view.widget



import com.dicoding.storyapp.R
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService.RemoteViewsFactory
import com.bumptech.glide.Glide
import com.dicoding.storyapp.data.response.ListStoryItem
import com.dicoding.storyapp.data.retrofit.ApiConfig
import com.dicoding.storyapp.view.setting.SettingPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class StackRemoteViewsFactory(private val context: Context) : RemoteViewsFactory {


    private val storyList = mutableListOf<ListStoryItem>()

    override fun onCreate() {}

    override fun onDestroy() {
        storyList.clear()
    }

    override fun getCount(): Int = storyList.size

    override fun onDataSetChanged() {
        Log.d("RemoteViewsFactory", "onDataSetChanged: dipanggil")

        try {
            val pref = SettingPreferences.getInstance(context)
            val token = runBlocking {
                pref.getToken().first()
            }
            Log.d("RemoteViewsFactory", "Token: $token")

            val apiService = ApiConfig.getApiService()
            val response = runBlocking { apiService.getStories("Bearer $token") }

            Log.d("RemoteViewsFactory", "Jumlah cerita diterima: ${response.listStory?.size ?: 0}")



            storyList.clear()
            storyList.addAll(response.listStory ?: emptyList())
        } catch (e: Exception) {
            Log.e("RemoteViewsFactory", "Gagal memuat cerita", e)
        }
    }

    override fun getViewAt(position: Int): RemoteViews {
        val story = storyList[position]
        val views = RemoteViews(context.packageName, R.layout.item_widget)

        val bitmap = getBitmapFromUrl(story.photoUrl)
        views.setImageViewBitmap(R.id.image_view, bitmap)

        val fillInIntent = Intent()
        fillInIntent.putExtra(ImageBannerWidget.EXTRA_ITEM, position)
        views.setOnClickFillInIntent(R.id.image_view, fillInIntent)

        return views
    }

    override fun getLoadingView(): RemoteViews? = null
    override fun getViewTypeCount(): Int = 1
    override fun getItemId(position: Int): Long = position.toLong()
    override fun hasStableIds(): Boolean = true

    private fun getBitmapFromUrl(url: String?): Bitmap? {
        return try {
            Glide.with(context)
                .asBitmap()
                .load(url)
                .submit()
                .get()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
