package com.geekmk.mtracker.database.location;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import com.geekmk.mtracker.database.DataBaseConstants;
import com.geekmk.mtracker.database.DataBaseConstants.LocationColumn;


/**
 * Created by manikanta.garikipati on 13/01/18.
 */

@Entity(tableName = DataBaseConstants.TABLE_LOCATION)
public class MLocation {

  @ColumnInfo(name = LocationColumn.JOURNEY_ID)
  private long journeyId;

  @ColumnInfo(name = LocationColumn.LATITUDE)
  private double latitude;

  @ColumnInfo(name = LocationColumn.LONGITUDE)
  private double longitude;

  @ColumnInfo(name = LocationColumn.TIME_STAMP)
  private long time;

  @ColumnInfo(name = LocationColumn.PLACE_INFO)
  private String placeInfo;

  @ColumnInfo(name = LocationColumn.BATTERY_LEVEL)
  private float batteryLevel;

  public long getJourneyId() {
    return journeyId;
  }

  public void setJourneyId(long journeyId) {
    this.journeyId = journeyId;
  }

  public double getLatitude() {
    return latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public long getTime() {
    return time;
  }

  public void setTime(long time) {
    this.time = time;
  }

  public String getPlaceInfo() {
    return placeInfo;
  }

  public void setPlaceInfo(String placeInfo) {
    this.placeInfo = placeInfo;
  }

  public float getBatteryLevel() {
    return batteryLevel;
  }

  public void setBatteryLevel(float batteryLevel) {
    this.batteryLevel = batteryLevel;
  }
}
