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
import com.sam_chordas.android.stockhawk.rest.Utils;
import com.sam_chordas.android.stockhawk.ui.StockDetailActivity;

/**
 * Popular Movie App
 * Created by jtlie on 8/25/2016.
 */

public class StockWidgetRemoteViewService extends RemoteViewsService {
    public static final String LOG_TAG = StockWidgetRemoteViewService.class.getSimpleName();

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
                        R.layout.list_item_quote);
                //set remote view
                views.setTextViewText(R.id.stock_symbol, data.getString(SYMBOL_COLUMN));
                views.setTextViewText(R.id.bid_price, data.getString(BIDPRICE_COLUMN));
                if (Utils.showPercent){
                    views.setTextViewText(R.id.change, data.getString(PERCENT_CHANGE_COLUMN));
                } else {
                    views.setTextViewText(R.id.change, data.getString(CHANGE_COLUMN));
                }
                if (data.getInt(ISUP_COLUMN) == 1){
                    views.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
                } else {
                    views.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);
                }
                Intent clickIntent = new Intent();
                clickIntent.putExtra(StockDetailActivity.SELECTED_SYMBOL, data.getString(SYMBOL_COLUMN));
                views.setOnClickFillInIntent(R.id.stock_list_item, clickIntent);
                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_view);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position)){
                    return data.getInt(ID_COLUMN);
                }
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }

        };
    }
}
