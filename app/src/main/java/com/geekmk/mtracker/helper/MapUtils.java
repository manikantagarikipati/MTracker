package com.geekmk.mtracker.helper;

import android.location.Location;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by manikanta.garikipati on 14/01/18.
 */
public class MapUtils {


  private MapUtils() {
  }

  public static Marker addMarker(Location location, String title, GoogleMap googleMap) {
    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
    MarkerOptions markerOptions = new MarkerOptions();
    markerOptions.position(latLng);
    markerOptions.title(title);
    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
    Marker marker = googleMap.addMarker(markerOptions);
    //move map camera
    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));

    return marker;
  }


}
