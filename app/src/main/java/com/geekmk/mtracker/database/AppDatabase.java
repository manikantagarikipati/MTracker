package com.geekmk.mtracker.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import com.geekmk.mtracker.database.journey.JourneyDAO;
import com.geekmk.mtracker.database.journey.MJourney;
import com.geekmk.mtracker.database.location.LocationDAO;
import com.geekmk.mtracker.database.location.MLocation;

/**
 * Created by manikanta.garikipati on 14/01/18.
 */

@Database(entities = {MJourney.class, MLocation.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

  public abstract JourneyDAO journeyDAO();

  public abstract LocationDAO locationDAO();

  /**
   * The only instance
   */
  private static AppDatabase sInstance;


  public static synchronized AppDatabase getInstance(Context context) {
    if (sInstance == null) {
      sInstance = Room
          .databaseBuilder(context.getApplicationContext(), AppDatabase.class, "mtracker")
          .build();
    }
    return sInstance;
  }

}
