package com.sam_chordas.android.stockhawk.ui;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import java.util.ArrayList;
import java.util.List;

import static com.sam_chordas.android.stockhawk.R.id.chart;


public class StockDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int DETAIL_CURSOR_ID = 10;
    public static final String SELECTED_SYMBOL = "SELECTED_SYMBOL";
    private LineChart lineChartView;

    private static final String[] STOCK_COLUMNS = {
        QuoteColumns.CREATED, QuoteColumns.BIDPRICE
    };

    public static final int CREATED = 0;
    public static final int BID_PRICE = 1;

    private String selectedSymbol;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);
        lineChartView = (LineChart) findViewById(chart);
        Intent intent = getIntent();
        selectedSymbol = intent.getStringExtra(SELECTED_SYMBOL);
        getLoaderManager().initLoader(DETAIL_CURSOR_ID, null, this);
    }

    private void loadData(@NonNull Cursor cursor) {
        List<Entry> entries = new ArrayList<Entry>();
        float x = 1f;
        float bufferBidPrice = 0f;
        final ArrayList<Pair<Float, String>> list = new ArrayList<>();

        while (cursor.moveToNext()){
            if (cursor.getCount() > 10){
                cursor.moveToPosition(cursor.getCount() - 10);
            }
            if (bufferBidPrice != cursor.getFloat(BID_PRICE)){
                x += 1f;
                bufferBidPrice = cursor.getFloat(BID_PRICE);
                entries.add(new Entry(x, bufferBidPrice));
                list.add(new Pair<>(x, cursor.getString(CREATED)));
            }
        }

        LineDataSet dataSet = new LineDataSet(entries, "Label");
        LineData lineData = new LineData(dataSet);
        lineChartView.setData(lineData);
        lineData.setDrawValues(false);
        YAxis yAxis = lineChartView.getAxisLeft();
        yAxis.setTextColor(Color.BLACK);
        yAxis.setTextSize(12f);
        lineChartView.getAxisRight().setEnabled(false);
        XAxis xAxis = lineChartView.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setTextSize(12f);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new AxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                for (Pair<Float, String> pair :
                        list) {
                    if (pair.first == value) {
                        return pair.second;
                    }
                }
                return "";
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });
        lineChartView.setDescription(getString(R.string.text_stock_over_time, selectedSymbol));
        lineChartView.invalidate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(DETAIL_CURSOR_ID, null, this);
    }

    @Override
    public Loader onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this, QuoteProvider.Quotes.CONTENT_URI,
                STOCK_COLUMNS,
                QuoteColumns.SYMBOL + " = ?",
                new String[]{ selectedSymbol },
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0){
            loadData(cursor);
        }
    }



    @Override
    public void onLoaderReset(Loader loader) {

    }


    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(R.string.text_value_over_time);
        actionBar.setSubtitle(selectedSymbol);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        restoreActionBar();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
           case(android.R.id.home):
               finish();
               return true;
        }
        return false;
    }
}
