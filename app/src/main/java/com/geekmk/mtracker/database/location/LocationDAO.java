package com.geekmk.mtracker.database.location;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import com.geekmk.mtracker.database.DataBaseConstants;
import com.geekmk.mtracker.database.DataBaseConstants.LocationColumn;
import java.util.List;

/**
 * Created by manikanta.garikipati on 13/01/18.
 */

@Dao
public interface LocationDAO {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insertAll(List<MLocation> locationList);

  @Query("SELECT * FROM " + DataBaseConstants.TABLE_LOCATION + " WHERE "
      + LocationColumn.JOURNEY_ID + " = :journeyId")
  LiveData<List<MLocation>> fetchLocationForJourney(long journeyId);

  @Query("SELECT * FROM " + DataBaseConstants.TABLE_LOCATION + " WHERE "
      + LocationColumn.JOURNEY_ID + " = :journeyId")
  List<MLocation> fetchLocation(long journeyId);

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insertLocation(MLocation location);

}
