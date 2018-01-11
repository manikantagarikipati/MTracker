package com.geekmk.mtracker.base;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import com.geekmk.mtracker.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by manikanta.garikipati on 11/01/18.
 */

public class BaseMapActivity extends FragmentActivity {

  public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;


  /**
   * generic method that helps in checking permissions needed for tracking for our app
   * instead of code duplication in every activity better we wrap it out to a base
   */
  public boolean checkLocationRequestPermissions() {
    int fineLocationPermission = ContextCompat
        .checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
    int coarseLocationPermission = ContextCompat
        .checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

    List<String> listPermissionsNeeded = new ArrayList<>();

    if (fineLocationPermission != PackageManager.PERMISSION_GRANTED) {
      listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
    }
    if (coarseLocationPermission != PackageManager.PERMISSION_GRANTED) {
      listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
    }

    if (!listPermissionsNeeded.isEmpty()) {
      ActivityCompat.requestPermissions(this,
          listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
          REQUEST_ID_MULTIPLE_PERMISSIONS);
      return false;
    }
    return true;
  }

  public boolean checkLocationRequestProvidedStatus(String[] permissions, int[] grantResults) {
    Map<String, Integer> perms = new HashMap<>();
    perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
    perms.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);

    if (grantResults.length > 0) {
      for (int i = 0; i < permissions.length; i++) {
        perms.put(permissions[i], grantResults[i]);
      }

      boolean allPermissionsAdded = true;

      //check if all entries in the map has permissions granted.
      for (Object o : perms.entrySet()) {
        Map.Entry pair = (Map.Entry) o;
        allPermissionsAdded =
            allPermissionsAdded && (Integer) pair.getValue() == PackageManager.PERMISSION_GRANTED;
      }

      return allPermissionsAdded;
    }

    return false;
  }

  public void handleLocationPermissionCallBack(
      final MapPermissionsProvidedCB mapPermissionsProvidedCB) {
    //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
    //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
    if (ActivityCompat
        .shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
        || ActivityCompat
        .shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
      showDialogOK("Permissions are required to perform tracking",
          new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              switch (which) {
                default:
                case DialogInterface.BUTTON_POSITIVE:
                  checkLocationRequestPermissions();
                  break;
                case DialogInterface.BUTTON_NEGATIVE:
                  // proceed with logic by disabling the related features or quit the app.
                  mapPermissionsProvidedCB.onMapPermissionsDenied();
                  break;
              }
            }
          });
    }
    //permission is denied (and never ask again is  checked)
    //shouldShowRequestPermissionRationale will return false
    else {
      explain(getResources().getString(R.string.msg_nav_app_settings), mapPermissionsProvidedCB);
    }
  }

  private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
    new AlertDialog.Builder(this)
        .setMessage(message)
        .setPositiveButton(getResources().getString(R.string.ok), okListener)
        .setNegativeButton(getResources().getString(R.string.cancel), okListener)
        .create()
        .show();
  }

  private void explain(String msg, final MapPermissionsProvidedCB appSyncStatusCallBack) {
    new android.support.v7.app.AlertDialog.Builder(this).setMessage(msg)
        .setPositiveButton(getResources().getString(R.string.yes),
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                startActivity(
                    new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri
                        .parse("package:" + getApplicationContext().getPackageName())));
              }
            })
        .setNegativeButton(getResources().getString(R.string.cancel),
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                appSyncStatusCallBack.onMapPermissionsDenied();
              }
            }).show();
  }

  public interface MapPermissionsProvidedCB {

    void onMapPermissionsProvided();

    void onMapPermissionsDenied();
  }

}
