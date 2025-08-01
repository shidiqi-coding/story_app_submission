package com.dicoding.storyapp.view.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.net.toUri
import com.dicoding.storyapp.R

@Suppress("DEPRECATION")
class ImagesBannerWidget : AppWidgetProvider() {

    companion object {
        private const val TOAST_ACTION = "com.dicoding.storyApp.TOAST_ACTION"
        const val EXTRA_ITEM = "com.dicoding.storyApp.EXTRA_ITEM"

//        private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
//            val intent = Intent(context, StackWidgetService::class.java).apply {
//                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
//                data = toUri(Intent.URI_INTENT_SCHEME).toUri()
//            }
//
//            val views = RemoteViews(context.packageName, R.layout.image_banner_widget).apply {
//                setRemoteAdapter(R.id.stack_view, intent)
//                setEmptyView(R.id.stack_view, R.id.empty_view)
//
//                val toastIntent = Intent(context, ImagesBannerWidget::class.java).apply {
//                    action = TOAST_ACTION
//                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
//                }
//
//                val toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent,
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
//                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
//                    else 0
//                )
//                setPendingIntentTemplate(R.id.stack_view, toastPendingIntent)
//            }
//
//            appWidgetManager.updateAppWidget(appWidgetId, views)
//        }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            val intent = Intent(context, StackWidgetService::class.java)
            val views = RemoteViews(context.packageName, R.layout.image_banner_widget)
            views.setRemoteAdapter(R.id.stack_view, intent)
            views.setEmptyView(R.id.stack_view, R.id.empty_view)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == TOAST_ACTION) {
            val viewIndex = intent.getIntExtra(EXTRA_ITEM, 0)
            Toast.makeText(context, "Touched view $viewIndex", Toast.LENGTH_SHORT).show()
        }
    }
}
