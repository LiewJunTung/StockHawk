package com.sam_chordas.android.stockhawk.widget;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

/**
 * Popular Movie App
 * Created by jtlie on 8/25/2016.
 */

public class StockWidgetRemoteViewService extends RemoteViewsService {
    private static final String[] MAIN_COLUMNS = {
            QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
            QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP
    };
    private final static int ID_COLUMN = 0;
    private final static int SYMBOL_COLUMN = 1;
    private final static int BIDPRICE_COLUMN = 2;
    private final static int PERCENT_CHANGE_COLUMN = 3;
    private final static int CHANGE_COLUMN = 4;
    private final static int ISUP_COLUMN = 5;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {

            }

            @Override
            public void onDataSetChanged() {
                if (data != null){
                    data.close();
                }
                final long identityToken = Binder.clearCallingIdentity();

                //get the most updated
                data = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                        MAIN_COLUMNS,
                        QuoteColumns.ISCURRENT + " = ?",
                        new String[]{"1"},
                        null);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_view);
                //set remote view
                views.setTextViewText(R.id.stock_symbol, data.getString(SYMBOL_COLUMN));
                views.setTextViewText(R.id.bid_price, data.getString(BIDPRICE_COLUMN));
                if (data.getInt(ISUP_COLUMN) == 1){
                    views.setInt(R.id.change, "setBackgroundColor", getResources().getDrawable(R.drawable.percent_change_pill_green));
                } else {

                }
                views.setTextViewText(R.id.change, data.getString(PERCENT_CHANGE_COLUMN));

                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return null;
            }

            @Override
            public int getViewTypeCount() {
                return 0;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }

        };
    }
}
