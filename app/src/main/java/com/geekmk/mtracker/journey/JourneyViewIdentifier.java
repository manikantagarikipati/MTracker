package com.geekmk.mtracker.journey;

import com.geekmk.mtracker.R;
import com.geekmk.mtracker.database.journey.MJourney;

/**
 * Created by manikanta.garikipati on 14/01/18.
 */

public class JourneyViewIdentifier implements ViewIdentifier {

  private MJourney mJourney;

  public JourneyViewIdentifier(MJourney mJourney) {
    this.mJourney = mJourney;
  }
  public MJourney getmJourney() {
    return mJourney;
  }

  public void setmJourney(MJourney mJourney) {
    this.mJourney = mJourney;
  }

  @Override
  public int getResourceType() {
    return R.layout.item_journey;
  }
}
