package com.geekmk.mtracker.journey;

import com.geekmk.mtracker.R;

/**
 * Created by manikanta.garikipati on 14/01/18.
 */

public class EmptyJourneyView implements ViewIdentifier {

  @Override
  public int getResourceType() {
    return R.layout.item_empty_journeys;
  }
}
