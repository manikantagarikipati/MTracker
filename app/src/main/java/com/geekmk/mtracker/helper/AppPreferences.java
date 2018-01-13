package com.geekmk.mtracker.helper;

import android.content.Context;
import android.content.SharedPreferences;
import com.geekmk.mtracker.R;

/**
 * Created by manikanta.garikipati on 13/01/18.
 */

public class AppPreferences {

  public static long getCurrentJourneyId(Context context) {
    SharedPreferences mPrefs = context
        .getSharedPreferences(context.getString(R.string.prefs), Context.MODE_PRIVATE);
    return mPrefs.getLong(context.getString(R.string.val_current_journey), 0L);
  }
}
