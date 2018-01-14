package com.geekmk.mtracker.database;

/**
 * Created by manikanta.garikipati on 13/01/18.
 */

public class DataBaseConstants {

  private DataBaseConstants() {
  }

  public static final String TABLE_LOCATION = "Location";

  public static final String TABLE_JOURNEY = "Journey";


  public static final class JourneyColumn {

    public static final String JOURNEY_ID = "id";

    public static final String STATUS = "status";

    public static final String START = "startLocation";

    public static final String END = "endLocation";

    public static final String START_NAME = "startPlace";

    public static final String END_NAME = "endName";

    public static final String START_TIME = "startTime";

    public static final String END_TIME = "endTime";
  }


  public static final class LocationColumn {

    public static final String LATITUDE = "lat";
    public static final String LONGITUDE = "lng";
    public static final String BATTERY_LEVEL = "batterylevel";
    public static final String JOURNEY_ID = "id";
    public static final String PLACE_INFO = "placeinfo";
    public static final String TIME_STAMP = "ts";
  }

}
