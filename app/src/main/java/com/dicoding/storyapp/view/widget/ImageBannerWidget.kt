package com.dicoding.storyapp.view.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import com.dicoding.storyapp.R
import androidx.core.net.toUri
import com.dicoding.storyapp.view.widget.ImageBannerWidget.Companion.EXTRA_ITEM

/**
 * Implementation of App Widget functionality.
 */
class ImageBannerWidget : AppWidgetProvider() {

    companion object {
        private const val TOAST_ACTION = "com.dicoding.storyapp.TOAST_ACTION"
        const val EXTRA_ITEM = "com.dicoding.storyapp.EXTRA_ITEM"
    }

    @Suppress("DEPRECATION")
    private fun updateAppWidget(
        context: Context ,
        appWidgetManager: AppWidgetManager ,
        appWidgetId: Int
    ) {
        val intent = Intent(context , StackWidgetService::class.java)
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID , appWidgetId)
        intent.data = intent.toUri(Intent.URI_INTENT_SCHEME).toUri()

        val views = RemoteViews(context.packageName , R.layout.image_banner_widget)
        views.setRemoteAdapter(R.id.stack_view , intent)
        views.setEmptyView(R.id.stack_view , R.id.empty_text)

        val toastIntent = Intent(context , ImageBannerWidget::class.java)
        toastIntent.action = TOAST_ACTION
        toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID , appWidgetId)


        val toastPendingIntent = PendingIntent.getBroadcast(
            context , 0 , toastIntent ,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            else 0
        )
        views.setPendingIntentTemplate(R.id.stack_view , toastPendingIntent)

        appWidgetManager.updateAppWidget(appWidgetId , views)
    }


    override fun onUpdate(
        context: Context ,
        appWidgetManager: AppWidgetManager ,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context , appWidgetManager , appWidgetId)
        }
//    }
//
//    override fun onEnabled(context: Context) {
//        // Enter relevant functionality for when the first widget is created
//    }
//
//    override fun onDisabled(context: Context) {
//        // Enter relevant functionality for when the last widget is disabled
//    }
    }

    override fun onReceive(context: Context , intent: Intent) {
        super.onReceive(context , intent)
        if (intent.action == TOAST_ACTION) {
            val index = intent.getIntExtra(EXTRA_ITEM , 0)
            // Optional: Toast or other action when clicked
            // Toast.makeText(context, "Clicked item $index", Toast.LENGTH_SHORT).show()
        }
    }
}

//@Suppress("DEPRECATION")
//internal fun updateAppWidget(
//    context: Context ,
//    appWidgetManager: AppWidgetManager ,
//    appWidgetId: Int
//) {
//    //val widgetText = context.getString(R.string.appwidget_text)
//    // Construct the RemoteViews object
//    val views = RemoteViews(context.packageName , R.layout.image_banner_widget)
//
//    val intent = Intent(context, StackWidgetService::class.java)
//    views.setRemoteAdapter(R.id.stack_view, intent)
//
//    views.setEmptyView(R.id.stack_view, R.id.empty_text)
//
//
//    // Instruct the widget manager to update the widget
//    appWidgetManager.updateAppWidget(appWidgetId , views)
//}