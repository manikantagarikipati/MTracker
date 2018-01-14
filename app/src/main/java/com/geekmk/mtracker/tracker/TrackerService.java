package com.geekmk.mtracker.tracker;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.arch.lifecycle.LifecycleService;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import com.geekmk.mtracker.R;
import com.geekmk.mtracker.database.AppDatabase;
import com.geekmk.mtracker.database.location.MLocation;
import com.geekmk.mtracker.helper.AppConstants;
import com.geekmk.mtracker.helper.AppPreferences;
import com.geekmk.mtracker.helper.AppUtils;
import com.geekmk.mtracker.map.LocationLiveData;
import com.geekmk.mtracker.map.MapsActivity;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by manikanta.garikipati on 13/01/18.
 */

public class TrackerService extends LifecycleService {

  private static final String TAG = TrackerService.class.getSimpleName();
  private static final int MSG_TYPE_LOCATION = 12;
  private PowerManager.WakeLock mWakelock;
  private NotificationManager mNotificationManager;
  private NotificationCompat.Builder mNotificationBuilder;
  private LinkedList<Map<String, Object>> mTrackingStatus = new LinkedList<>();

  private static final int FOREGROUND_SERVICE_ID = 1;
  private static final int NOTIFICATION_ID = 1;

  private Looper mServiceLooper;
  private ServiceHandler mServiceHandler;

  // Handler that receives messages from the thread
  private final class ServiceHandler extends Handler {

    public ServiceHandler(Looper looper) {
      super(looper);
    }

    @Override
    public void handleMessage(Message msg) {
      if (msg.arg1 == MSG_TYPE_LOCATION) {
        Map<String, Object> info = (Map<String, Object>) msg.obj;
        MLocation mLocation = new MLocation();
        mLocation
            .setJourneyId(AppPreferences.getCurrentJourneyId(getApplication().getBaseContext()));
        mLocation.setBatteryLevel((float) info.get("power"));
        mLocation.setTime((Date) info.get("time"));
        mLocation.setLatitude((Double) info.get("lat"));
        mLocation.setLongitude((Double) info.get("lng"));

        AppDatabase.getInstance(getApplication().getBaseContext()).locationDAO()
            .insertLocation(mLocation);
      }
    }
  }

  public TrackerService() {
  }

  private Observer<Location> locationObserver;

  @Override
  public void onCreate() {
    super.onCreate();
    HandlerThread thread = new HandlerThread("ServiceStartArguments",
        Process.THREAD_PRIORITY_BACKGROUND);
    thread.start();

    // Get the HandlerThread's Looper and use it for our Handler
    mServiceLooper = thread.getLooper();
    mServiceHandler = new ServiceHandler(mServiceLooper);

    //displaying notification on what is happening behind the hood
    buildNotification();
    setStatusMessage(R.string.msg_service_started);

    locationObserver = new Observer<Location>() {
      @Override
      public void onChanged(@Nullable Location location) {
        handleLocation(location);
      }
    };

    LocationLiveData.getInstance(this).observe(TrackerService.this, locationObserver);
// Hold a partial wake lock to keep CPU awake when the we're tracking location.
    PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
    mWakelock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakelockTag");
    mWakelock.acquire();
  }


  private void handleLocation(Location location) {
    if (location != null) {
      Map<String, Object> trackStatus = new HashMap<>();
      trackStatus.put("lat", location.getLatitude());
      trackStatus.put("lng", location.getLongitude());
      trackStatus.put("time", new Date());
      trackStatus.put("power", AppUtils.getBatteryLevel(TrackerService.this));

      if (AppUtils.locationIsAtStatus(location, 1, mTrackingStatus)
          && AppUtils.locationIsAtStatus(location, 0, mTrackingStatus)) {
        // If the most recent two statuses are approximately at ,the same
        // location as the new current location, rather than adding the new
        // location, we update the latest status with the current. Two statuses
        // are kept when the locations are the same, the earlier representing
        // the time the location was arrived at, and the latest representing the
        // current time.
        mTrackingStatus.set(0, trackStatus);
      } else {
        // Maintain a fixed number of previous statuses.
        while (mTrackingStatus.size() >= AppConstants.MAX_STATUSES) {
          mTrackingStatus.removeLast();
        }
        mTrackingStatus.addFirst(trackStatus);
        // We push the entire list at once since each key/index changes, to
        // minimize network requests.
        //todo insert into db
        Message message = mServiceHandler.obtainMessage();
        message.arg1 = MSG_TYPE_LOCATION;
        message.obj = mTrackingStatus.getFirst();
        mServiceHandler.sendMessage(message);
        Log.d(TrackerService.TAG, location.toString());
      }
    }
  }


  @Override
  public void onDestroy() {
    setStatusMessage(R.string.msg_service_destroyed);
    // Stop the persistent notification.
    mNotificationManager.cancel(NOTIFICATION_ID);
    LocationLiveData.getInstance(this).removeObserver(locationObserver);
    // Release the wakelock
    if (mWakelock != null) {
      mWakelock.release();
    }
    super.onDestroy();
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    super.onBind(intent);
    return null;
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
  }
}
