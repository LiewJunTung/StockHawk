package com.sam_chordas.android.stockhawk.rest;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.util.Log;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by sam_chordas on 10/8/15.
 */
public class Utils {

    public static boolean showPercent = true;
    private static String LOG_TAG = Utils.class.getSimpleName();


    public static ArrayList quoteJsonToContentVals(String JSON, Context context) {
        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();
        JSONObject jsonObject = null;
        JSONArray resultsArray = null;
        Log.d(LOG_TAG, JSON);
        try {
            jsonObject = new JSONObject(JSON);
            if (jsonObject.length() != 0) {
                jsonObject = jsonObject.getJSONObject(context.getString(R.string.json_query));
                int count = Integer.parseInt(jsonObject.getString(context.getString(R.string.json_count)));
                LocalDateTime dateTime = LocalDateTime.parse(jsonObject.getString(context.getString(R.string.json_created)), DateTimeFormatter.ISO_DATE_TIME);
                String date = dateTime.format(DateTimeFormatter.ofPattern(context.getString(R.string.json_time_format)));
                if (count == 1) {
                    jsonObject = jsonObject.getJSONObject(context.getString(R.string.json_results)).getJSONObject(context.getString(R.string.json_quote));
                    if (!jsonObject.isNull(context.getString(R.string.json_ask))){
                        batchOperations.add(buildBatchOperation(jsonObject, date, context));
                    }
                } else {
                    resultsArray = jsonObject.getJSONObject(context.getString(R.string.json_results)).getJSONArray(context.getString(R.string.json_quote));
                    if (resultsArray != null && resultsArray.length() != 0) {
                        for (int i = 0; i < resultsArray.length(); i++) {
                            jsonObject = resultsArray.getJSONObject(i);
                            if (!jsonObject.isNull(context.getString(R.string.json_ask))){
                                batchOperations.add(buildBatchOperation(jsonObject, date, context));
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "String to JSON failed: " + e);
        }
        return batchOperations;
    }

    public static String truncateBidPrice(String bidPrice, Context context) {
        bidPrice = String.format(Locale.getDefault(), context.getString(R.string.decimal_format), Float.parseFloat(bidPrice));
        return bidPrice;
    }

    public static String truncateChange(String change, boolean isPercentChange, Context context) {
        String weight = change.substring(0, 1);
        String ampersand = "";
        if (isPercentChange) {
            ampersand = change.substring(change.length() - 1, change.length());
            change = change.substring(0, change.length() - 1);
        }
        change = change.substring(1, change.length());
        double round = (double) Math.round(Double.parseDouble(change) * 100) / 100;
        change = context.getString(R.string.decimal_format, round);
        StringBuffer changeBuffer = new StringBuffer(change);
        changeBuffer.insert(0, weight);
        changeBuffer.append(ampersand);
        change = changeBuffer.toString();
        return change;
    }

    public static ContentProviderOperation buildBatchOperation(JSONObject jsonObject, String date, Context context) {
        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
                QuoteProvider.Quotes.CONTENT_URI);
        Log.d(LOG_TAG, jsonObject.toString());
        try {
            String change = jsonObject.getString(context.getString(R.string.json_change));
            builder.withValue(QuoteColumns.SYMBOL, jsonObject.getString(context.getString(R.string.json_symbol)));
            builder.withValue(QuoteColumns.BIDPRICE, truncateBidPrice(jsonObject.getString(context.getString(R.string.json_bid)), context));
            builder.withValue(QuoteColumns.PERCENT_CHANGE, truncateChange(
                    jsonObject.getString(context.getString(R.string.json_change_in_percent)), true, context));
            builder.withValue(QuoteColumns.CREATED, date);
            builder.withValue(QuoteColumns.CHANGE, truncateChange(change, false, context));
            builder.withValue(QuoteColumns.ISCURRENT, 1);
            if (change.charAt(0) == '-') {
                builder.withValue(QuoteColumns.ISUP, 0);
            } else {
                builder.withValue(QuoteColumns.ISUP, 1);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return builder.build();
    }
}
