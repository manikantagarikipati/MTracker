package com.geekmk.mtracker;

import android.app.Application;
import com.geekmk.mtracker.base.MRepo;

/**
 * Created by manikanta.garikipati on 14/01/18.
 */

public class MApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    MRepo.initialize(this);
  }
}
