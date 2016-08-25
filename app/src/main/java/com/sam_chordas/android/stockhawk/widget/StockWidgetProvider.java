package com.sam_chordas.android.stockhawk.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.ui.StockDetailActivity;

/**
 * Popular Movie App
 * Created by jtlie on 8/25/2016.
 */

public class StockWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId: appWidgetIds){
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_view);

            // Create an Intent to launch MainActivity
            Intent intent = new Intent(context, StockDetailActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            views.setRemoteAdapter(R.id.widget_list, new Intent(context, StockDetailActivity.class));
            Intent listIntent = new Intent(context, StockDetailActivity.class);
        }
    }
}
