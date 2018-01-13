package com.geekmk.mtracker.tracker;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import com.geekmk.mtracker.R;
import com.geekmk.mtracker.helper.AppConstants;
import com.geekmk.mtracker.helper.AppUtils;
import com.geekmk.mtracker.map.MapsActivity;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by manikanta.garikipati on 13/01/18.
 */

public class TrackerService extends Service implements LocationListener {

  private static final String TAG = TrackerService.class.getSimpleName();
  private GoogleApiClient mGoogleApiClient;
  //  private PowerManager.WakeLock mWakelock;
  private SharedPreferences mPrefs;
  private NotificationManager mNotificationManager;
  private NotificationCompat.Builder mNotificationBuilder;
  private LinkedList<Map<String, Object>> mTrackingStatus = new LinkedList<>();

  private static final int FOREGROUND_SERVICE_ID = 1;
  private static final int NOTIFICATION_ID = 1;

  public TrackerService() {
  }

  @Override
  public void onCreate() {
    super.onCreate();
    //displaying notification on what is happening behind the hood
    buildNotification();
    setStatusMessage(R.string.msg_service_started);
    startLocationTracking();
  }


  @Override
  public void onDestroy() {
    setStatusMessage(R.string.msg_service_destroyed);
    // Stop the persistent notification.
    mNotificationManager.cancel(NOTIFICATION_ID);

    // Stop receiving location updates.
    if (mGoogleApiClient != null) {
      LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,
          TrackerService.this);
    }

//    // Release the wakelock
//    if (mWakelock != null) {
//      mWakelock.release();
//    }
    super.onDestroy();
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return mBinder;
  }

  private GoogleApiClient.ConnectionCallbacks mLocationRequestCallback = new GoogleApiClient
      .ConnectionCallbacks() {

    @SuppressLint("MissingPermission")
    @Override
    public void onConnected(Bundle bundle) {
      setStatusMessage(R.string.msg_tracking_started);
      reportCurrentLocation(LocationServices.
          FusedLocationApi.getLastLocation(mGoogleApiClient));
      LocationRequest request = new LocationRequest();
      request.setInterval(AppConstants.LOCATION_REQUEST_INTERVAL);
      request.setFastestInterval(AppConstants.LOCATION_REQUEST_INTERVAL_FASTEST);
      request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
      LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
          request, TrackerService.this);

//      // Hold a partial wake lock to keep CPU awake when the we're tracking location.
//      PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
//      mWakelock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakelockTag");
//      mWakelock.acquire(10*60*1000L /*10 minutes*/);
    }

    @Override
    public void onConnectionSuspended(int reason) {
      // TODO: Handle gracefully
      setStatusMessage(R.string.msg_tracking_suspended);
    }
  };

  /**
   * Starts location tracking by creating a Google API client, and
   * requesting location updates.
   */
  private void startLocationTracking() {
    mGoogleApiClient = new GoogleApiClient.Builder(this)
        .addConnectionCallbacks(mLocationRequestCallback)
        .addApi(LocationServices.API)
        .build();
    mGoogleApiClient.connect();
  }

  @Override
  public void onLocationChanged(Location location) {

    Map<String, Object> trackStatus = new HashMap<>();
    trackStatus.put("lat", location.getLatitude());
    trackStatus.put("lng", location.getLongitude());
    trackStatus.put("time", new Date().getTime());
    trackStatus.put("power", AppUtils.getBatteryLevel(this));

    if (AppUtils.locationIsAtStatus(location, 1, mTrackingStatus)
        && AppUtils.locationIsAtStatus(location, 0, mTrackingStatus)) {
      // If the most recent two statuses are approximately at ,the same
      // location as the new current location, rather than adding the new
      // location, we update the latest status with the current. Two statuses
      // are kept when the locations are the same, the earlier representing
      // the time the location was arrived at, and the latest representing the
      // current time.
      mTrackingStatus.set(0, trackStatus);
      // Only need to update 0th status, so we can save bandwidth.
//      mFirebaseTransportRef.child("0").setValue(transportStatus);
    } else {
      // Maintain a fixed number of previous statuses.
      while (mTrackingStatus.size() >= AppConstants.MAX_STATUSES) {
        mTrackingStatus.removeLast();
      }
      mTrackingStatus.addFirst(trackStatus);
      // We push the entire list at once since each key/index changes, to
      // minimize network requests.
      //todo insert into db
//      mFirebaseTransportRef.setValue(mTransportStatuses);a
    }

    reportCurrentLocation(location);
  }


  private void buildNotification() {
    mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,
        new Intent(this, MapsActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
    mNotificationBuilder = new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.ic_track_changes_white_48dp)
        .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
        .setContentTitle(getString(R.string.app_name))
        .setOngoing(true)
        .setContentIntent(resultPendingIntent);
    startForeground(FOREGROUND_SERVICE_ID, mNotificationBuilder.build());
  }

  /**
   * Sets the current status message (connecting/tracking/not tracking).
   */
  private void setStatusMessage(int stringId) {

    mNotificationBuilder.setContentText(getString(stringId));
    mNotificationManager.notify(NOTIFICATION_ID, mNotificationBuilder.build());

//    // Also display the status message in the activity.
//    Intent intent = new Intent(STATUS_INTENT);
//    intent.putExtra(getString(R.string.status), stringId);
//    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
  }

  private void reportCurrentLocation(Location location) {
    Intent intent = new Intent();
    intent.setAction(AppConstants.LOCATION_INTENT);
//    intent.setType(AppConstants.INTENT_TYPE_CURRENT_LOCATION);
    intent.putExtra(AppConstants.EXTRA_CUR_LOC, location);
    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
  }

  private final IBinder mBinder = new LocalBinder();

  public class LocalBinder extends Binder {

    public TrackerService getService() {
      return TrackerService.this;
    }
  }

  @SuppressLint("MissingPermission")
  public Location getCurrentLocation() {
    if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
      return LocationServices.
          FusedLocationApi.getLastLocation(mGoogleApiClient);
    }
    return null;
  }
}
