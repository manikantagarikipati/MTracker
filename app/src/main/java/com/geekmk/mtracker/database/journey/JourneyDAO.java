package com.geekmk.mtracker.database.journey;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import com.geekmk.mtracker.database.DataBaseConstants;
import java.util.List;

/**
 * Created by manikanta.garikipati on 14/01/18.
 */

@Dao
public interface JourneyDAO {

  @Query("SELECT * FROM " + DataBaseConstants.TABLE_JOURNEY)
  LiveData<List<MJourney>> getAll();

}
