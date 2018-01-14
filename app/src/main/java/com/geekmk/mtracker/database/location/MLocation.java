package com.geekmk.mtracker.database.location;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import com.geekmk.mtracker.database.DataBaseConstants;
import com.geekmk.mtracker.database.DataBaseConstants.LocationColumn;
import com.geekmk.mtracker.helper.DateTypeConverters;
import java.util.Date;


/**
 * Created by manikanta.garikipati on 13/01/18.
 */

@Entity(tableName = DataBaseConstants.TABLE_LOCATION)
@TypeConverters({DateTypeConverters.class})
public class MLocation {

  @PrimaryKey(autoGenerate = true)
  private long locationId;

  @ColumnInfo(name = LocationColumn.JOURNEY_ID)
  private long journeyId;

  @ColumnInfo(name = LocationColumn.LATITUDE)
  private double latitude;

  @ColumnInfo(name = LocationColumn.LONGITUDE)
  private double longitude;

  public Date getTime() {
    return time;
  }

  public void setTime(Date time) {
    this.time = time;
  }

  @ColumnInfo(name = LocationColumn.TIME_STAMP)
  private Date time;

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

  public long getLocationId() {
    return locationId;
  }

  public void setLocationId(long locationId) {
    this.locationId = locationId;
  }

}
