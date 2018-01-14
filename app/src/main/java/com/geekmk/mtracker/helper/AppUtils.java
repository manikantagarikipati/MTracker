package com.geekmk.mtracker.helper;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.BatteryManager;
import android.util.Log;
import android.widget.Toast;
import com.geekmk.mtracker.base.FetchAddressIntentService;
import com.geekmk.mtracker.journeydetail.JourneyDetailActivity;
import com.google.android.gms.maps.model.LatLng;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by manikanta.garikipati on 13/01/18.
 */

public class AppUtils {

  private AppUtils() {
  }

  public static float getBatteryLevel(Context context) {
    Intent batteryStatus = context.registerReceiver(null,
        new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    int batteryLevel = -1;
    int batteryScale = 1;
    if (batteryStatus != null) {
      batteryLevel = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, batteryLevel);
      batteryScale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, batteryScale);
    }
    return batteryLevel / (float) batteryScale * 100;
  }


  /**
   * Determines if the current location is approximately the same as the location
   * for a particular status. Used to check if we'll add a new status, or
   * update the most recent status of we're stationary.
   */
  public static boolean locationIsAtStatus(Location location,
      int statusIndex, LinkedList<Map<String, Object>> mTrackingStatus) {
    if (mTrackingStatus.size() <= statusIndex) {
      return false;
    }
    Map<String, Object> status = mTrackingStatus.get(statusIndex);
    Location locationForStatus = new Location("");
    locationForStatus.setLatitude((double) status.get("lat"));
    locationForStatus.setLongitude((double) status.get("lng"));
    float distance = location.distanceTo(locationForStatus);
    Log.d("Location Log", String.format("Distance from status %s is %sm", statusIndex, distance));
    return distance < AppConstants.LOCATION_MIN_DISTANCE_CHANGED;
  }

  public static boolean isServiceRunning(Class<?> serviceClass, Context context) {
    ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    for (ActivityManager.RunningServiceInfo service : manager
        .getRunningServices(Integer.MAX_VALUE)) {
      if (serviceClass.getName().equals(service.service.getClassName())) {
        return true;
      }
    }
    return false;
  }

  public static String buildLatLngString(LatLng position) {

    return position.latitude + AppConstants.LatLngSeparator + position.longitude;
  }

  public static LatLng getLatLng(String startLatLng) {
    try {
      String[] info = startLatLng.split(AppConstants.LatLngSeparator);
      LatLng latLng = new LatLng(Double.valueOf(info[0]), Double.valueOf(info[1]));
      return latLng;
    } catch (Exception e) {
    }
    return null;
  }

  public static void showToast(Context context, String msg) {
    if (context != null) {
      Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
  }

  public static void showToast(Context context, int resId) {
    if (context != null) {
      Toast.makeText(context, context.getString(resId), Toast.LENGTH_SHORT).show();
    }
  }

  public static void displayJourneyDetail(Context context, long journeyId) {
    Intent intent = new Intent(context, JourneyDetailActivity.class);
    intent.putExtra(AppConstants.EXTRA_JOURNEY_ID, journeyId);
    context.startActivity(intent);
  }

  public static void geoCode(Context context, long journeyId, double lat, double lng,
      boolean isStart) {
    Intent intent = new Intent(context, FetchAddressIntentService.class);
    intent.putExtra(FetchAddressIntentService.IS_START, isStart);
    intent.putExtra(FetchAddressIntentService.JOURNEY_ID, journeyId);
    intent.putExtra(FetchAddressIntentService.EXTRA_LATITUDE, lat);
    intent.putExtra(FetchAddressIntentService.EXTRA_LONGITUDE, lng);
    context.startService(intent);
  }
}
