package com.sam_chordas.android.stockhawk.utils;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.support.annotation.StringDef;

/**
 * Popular Movie App
 * Created by jtlie on 8/25/2016.
 */

public class StockUtils {

    @StringDef({ERROR_TYPE_PREF})
    public @interface PrefErrorTypeName {}
    public static final String ERROR_TYPE_PREF = "ErrorType";

    @IntDef({NO_ERROR, INVALID_STOCKS})
    public @interface ErrorType {}
    public static final int NO_ERROR = 0;
    public static final int INVALID_STOCKS = 1;

    public static void setPreferenceError(Context context, @ErrorType int errorType){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(ERROR_TYPE_PREF, errorType)
                .apply();
    }
}
