// SPDX-License-Identifier: GPL-3.0-or-later

package io.github.muntashirakon.AppManager.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import io.github.muntashirakon.AppManager.R
import io.github.muntashirakon.AppManager.main.MainActivity
import io.github.muntashirakon.AppManager.settings.QuickExportComposeActivity

/**
 * DeviceGuard Pro Quick Actions Widget
 * Provides quick access to common app management tasks
 */
class QuickActionsWidget : AppWidgetProvider() {
    
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // Update each widget instance
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    // Create RemoteViews for the widget layout
    val views = RemoteViews(context.packageName, R.layout.widget_quick_actions)

    // Set up click handlers for each action button
    setupClickHandler(
        context, views, R.id.widget_open_app,
        Intent(context, MainActivity::class.java),
        REQUEST_CODE_OPEN_APP
    )

    setupClickHandler(
        context, views, R.id.widget_quick_export,
        Intent(context, QuickExportComposeActivity::class.java),
        REQUEST_CODE_QUICK_EXPORT
    )

    // Update widget title with DeviceGuard Pro branding
    views.setTextViewText(R.id.widget_title, "DeviceGuard Pro")

    // Update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}

private fun setupClickHandler(
    context: Context,
    views: RemoteViews,
    viewId: Int,
    intent: Intent,
    requestCode: Int
) {
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    val pendingIntent = PendingIntent.getActivity(
        context, requestCode, intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    views.setOnClickPendingIntent(viewId, pendingIntent)
}

private const val REQUEST_CODE_OPEN_APP = 1001
private const val REQUEST_CODE_QUICK_EXPORT = 1002