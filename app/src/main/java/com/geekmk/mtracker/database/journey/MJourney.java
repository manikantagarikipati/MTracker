package com.geekmk.mtracker.database.journey;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import com.geekmk.mtracker.database.DataBaseConstants;
import com.geekmk.mtracker.database.DataBaseConstants.JourneyColumn;
import com.geekmk.mtracker.helper.DateTypeConverters;
import java.util.Date;

/**
 * Created by manikanta.garikipati on 14/01/18.
 */

@Entity(tableName = DataBaseConstants.TABLE_JOURNEY)
@TypeConverters({DateTypeConverters.class})
public class MJourney {

  @ColumnInfo(name = JourneyColumn.JOURNEY_ID)
  @PrimaryKey(autoGenerate = true)
  private long journeyId;

  @ColumnInfo(name = JourneyColumn.STATUS)
  private int status;

  @ColumnInfo(name = JourneyColumn.START)
  private String startLatLng;

  @ColumnInfo(name = JourneyColumn.END)
  private String endLatLng;

  @ColumnInfo(name = JourneyColumn.START_NAME)
  private String startPlaceName;

  @ColumnInfo(name = JourneyColumn.END_NAME)
  private String endPlaceName;

  @ColumnInfo(name = JourneyColumn.START_TIME)
  private Date startTime;

  @ColumnInfo(name = JourneyColumn.END_TIME)
  private Date endTime;

  public String getEndPlaceName() {
    return endPlaceName;
  }

  public Date getStartTime() {
    return startTime;
  }

  public void setStartTime(Date startTime) {
    this.startTime = startTime;
  }

  public Date getEndTime() {
    return endTime;
  }

  public void setEndTime(Date endTime) {
    this.endTime = endTime;
  }

  public void setEndPlaceName(String endPlaceName) {
    this.endPlaceName = endPlaceName;
  }

  public long getJourneyId() {
    return journeyId;
  }

  public void setJourneyId(long journeyId) {
    this.journeyId = journeyId;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getStartLatLng() {
    return startLatLng;
  }

  public void setStartLatLng(String startLatLng) {
    this.startLatLng = startLatLng;
  }

  public String getEndLatLng() {
    return endLatLng;
  }

  public void setEndLatLng(String endLatLng) {
    this.endLatLng = endLatLng;
  }

  public String getStartPlaceName() {
    return startPlaceName;
  }

  public void setStartPlaceName(String startPlaceName) {
    this.startPlaceName = startPlaceName;
  }
}
