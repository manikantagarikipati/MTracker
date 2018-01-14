package com.geekmk.mtracker.database.journey;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import com.geekmk.mtracker.database.DataBaseConstants;
import com.geekmk.mtracker.database.DataBaseConstants.JourneyColumn;
import java.util.List;

/**
 * Created by manikanta.garikipati on 14/01/18.
 */

@Dao
public interface JourneyDAO {

  @Query("SELECT * FROM " + DataBaseConstants.TABLE_JOURNEY)
  LiveData<List<MJourney>> getAll();

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  long insertJourney(MJourney journey);

  @Query("SELECT * FROM " + DataBaseConstants.TABLE_JOURNEY + " WHERE "
      + JourneyColumn.JOURNEY_ID + " LIKE :journeyId LIMIT 1")
  LiveData<MJourney> fetchJourney(long journeyId);
}
