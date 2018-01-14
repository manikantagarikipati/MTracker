package com.geekmk.mtracker.helper;

import android.location.Location;
import com.geekmk.mtracker.database.location.MLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by manikanta.garikipati on 14/01/18.
 */
public class MapUtils {

  public static final String STATIC_MAPS_BASE_URL = "https://maps.google.com/maps/api/staticmap?";
  public static final String STATIC_MAPS_PARAM_CENTER = "center";
  public static final String STATIC_MAPS_PARAM_ZOOM = "zoom";
  public static final String STATIC_MAPS_PARAM_SIZE = "size";
  public static final String STATIC_MAPS_PARAM_CROSS = "x";
  public static final String STATIC_MAPS_PARAM_MAP_TYPE = "maptype";
  public static final String STATIC_MAPS_PARAM_VALUE_ROADMAP = "roadmap";
  public static final String STATIC_MAPS_PARAM_MARKERS = "markers";
  public static final String STATIC_MAPS_PARAM_VALUE_COLOR = "color:red";
  public static final String STATIC_MAPS_PARAM_VALUE_LABEL = "label:DEFAULT";
  public static final String STATIC_MAPS_PARAM_VALUE_SEPARATOR = "%7C";
  public static final String VALUE_EMPTY = "";
  private MapUtils() {
  }

  public static Marker addMarker(Location location, String title, GoogleMap googleMap) {
    return addMarker(location.getLatitude(), location.getLongitude(), googleMap, title);
  }

  /**
   * Add marker to the map and return it
   *
   * @param latitude latitude
   * @param longitude longitude
   * @param googleMap map where the marker need to be added
   */
  public static Marker addMarker(double latitude, double longitude, GoogleMap googleMap,
      String title) {
    LatLng latLng = new LatLng(latitude, longitude);
    MarkerOptions markerOptions = new MarkerOptions();
    markerOptions.position(latLng);
    markerOptions.title(title);
    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
    Marker marker = googleMap.addMarker(markerOptions);
    //move map camera
    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));

    return marker;
  }


  /**
   * @return the static map url for a given latitude, longitude, zoom level, width and height
   */
  public static String getStaticMapUrl(double latitude, double longitude, int width,
      int height) {
//        sample url
//        String url = "https://maps.google.com/maps/api/staticmap?center=12.97,77.59&zoom=16&size=400x150&maptype=roadmap&markers=color:red%7Clabel:BA%7C12.97,77.59";
    String url = STATIC_MAPS_BASE_URL;
    url = url + STATIC_MAPS_PARAM_CENTER + "=" + latitude + "," + longitude + "&";
    url = url + STATIC_MAPS_PARAM_ZOOM + "=" + 10 + VALUE_EMPTY
        + "&";
    url = url + STATIC_MAPS_PARAM_SIZE + "=" + width
        + STATIC_MAPS_PARAM_CROSS + height + "&";
    url = url + STATIC_MAPS_PARAM_MAP_TYPE + "="
        + STATIC_MAPS_PARAM_VALUE_ROADMAP + "&";
    url = url + STATIC_MAPS_PARAM_MARKERS + "="
        + STATIC_MAPS_PARAM_VALUE_SEPARATOR
        + STATIC_MAPS_PARAM_VALUE_COLOR
        + STATIC_MAPS_PARAM_VALUE_SEPARATOR
        + STATIC_MAPS_PARAM_VALUE_LABEL
        + STATIC_MAPS_PARAM_VALUE_SEPARATOR + latitude + "," + longitude;
    return url;
  }

  /**
   * Display polyline path for a single location with rounded start and end cap settings
   * @param googleMap GoogleMap where the polyline needs to be implemented
   * @param locationList list of locations which is added to the polyline path
   * @param polylinecolor color of the polyline path
   * */
  public static Polyline displayPathInfo(GoogleMap googleMap, List<MLocation> locationList,
      int polylinecolor) {
    List<LatLng> latLngList = new ArrayList<>();
    for (MLocation mLocation : locationList) {
      latLngList.add(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()));
    }
    PolylineOptions polylineOptions = new PolylineOptions();
    polylineOptions.addAll(latLngList);
    polylineOptions.width(18);
    polylineOptions.color(polylinecolor);
    Polyline polyline = googleMap.addPolyline(polylineOptions);
    polyline.setStartCap(new RoundCap());
    polyline.setEndCap(new RoundCap());
    polyline.setJointType(JointType.ROUND);
    return polyline;
  }

  public static Polyline displayExistingPathInfo(List<MLocation> locationList,
      int polylinecolor, Polyline polyline) {
    List<LatLng> latLngList = new ArrayList<>();
    for (MLocation mLocation : locationList) {
      latLngList.add(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()));
    }
    PolylineOptions polylineOptions = new PolylineOptions();
    polylineOptions.addAll(latLngList);
    polylineOptions.width(18);
    polylineOptions.color(polylinecolor);
    polyline.setStartCap(new RoundCap());
    polyline.setEndCap(new RoundCap());
    polyline.setJointType(JointType.ROUND);
    return polyline;
  }

  /**
   * Display polyline path for a single location with rounded start and end cap settings
   * @param googleMap GoogleMap where the polyline needs to be implemented
   * @param mLocation location to which we need to add polyline
   * @param polylinecolor color of the polyline path
   * */
  public static Polyline displayPathInfo(GoogleMap googleMap, Location mLocation,
      int polylinecolor) {
    PolylineOptions polylineOptions = new PolylineOptions();
    polylineOptions.add(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()));
    polylineOptions.width(18);
    polylineOptions.color(polylinecolor);
    Polyline polyline = googleMap.addPolyline(polylineOptions);
    polyline.setStartCap(new RoundCap());
    polyline.setEndCap(new RoundCap());
    polyline.setJointType(JointType.ROUND);
    return polyline;
  }
}
