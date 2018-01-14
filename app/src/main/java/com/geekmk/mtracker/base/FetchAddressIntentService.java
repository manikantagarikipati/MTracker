package com.geekmk.mtracker.base;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.Nullable;
import com.geekmk.mtracker.database.journey.JourneyFetchCB;
import com.geekmk.mtracker.database.journey.MJourney;
import com.geekmk.mtracker.helper.CollectionUtils;
import java.util.List;
import java.util.Locale;

/**
 * Created by manikanta.garikipati on 14/01/18.
 *
 * Performs geocoding given the latitude and longitude
 */

public class FetchAddressIntentService extends IntentService {

  public static final String EXTRA_LATITUDE = "latitude";

  public static final String EXTRA_LONGITUDE = "longitude";

  public static final String JOURNEY_ID = "JID";

  public static final String IS_START = "isStart";


  /**
   * Creates an IntentService.  Invoked by your subclass's constructor.
   *
   * @param name Used to name the worker thread, important only for debugging.
   */
  public FetchAddressIntentService(String name) {
    super(name);
  }

  public FetchAddressIntentService() {
    super("FetchAddress");
  }

  @Override
  protected void onHandleIntent(@Nullable Intent intent) {

    if (intent != null) {
      Geocoder geocoder = new Geocoder(this, Locale.getDefault());

      double latitude = intent.getDoubleExtra(FetchAddressIntentService.EXTRA_LATITUDE, 0);
      double longitude = intent.getDoubleExtra(FetchAddressIntentService.EXTRA_LONGITUDE, 0);

      long journeyId = intent.getLongExtra(FetchAddressIntentService.JOURNEY_ID, 0);
      final boolean isStart = intent.getBooleanExtra(FetchAddressIntentService.IS_START, false);

      List<Address> addressList = null;
      try {
        addressList = geocoder.getFromLocation(latitude, longitude, 1);
      } catch (Exception e) {
      }

      if (CollectionUtils.isNotEmpty(addressList)) {
        final List<Address> finalAddressList = addressList;
        MRepo.fetchJourney(journeyId, new JourneyFetchCB() {
          @Override
          public void onJourneyLoaded(MJourney mJourney) {
            Address address = finalAddressList.get(0);
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
              stringBuilder.append(address.getAddressLine(i));
              stringBuilder.append(" ");
            }
            if (isStart) {
              mJourney.setStartPlaceName(stringBuilder.toString());
            } else {
              mJourney.setEndPlaceName(stringBuilder.toString());
            }
            MRepo.addJourney(mJourney, null);
          }

          @Override
          public void onError() {
          }
        });
      }
    }

  }
}
