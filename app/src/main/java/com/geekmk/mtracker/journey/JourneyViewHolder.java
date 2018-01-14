package com.geekmk.mtracker.journey;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.geekmk.mtracker.R;
import com.geekmk.mtracker.database.journey.MJourney;

/**
 * Created by manikanta.garikipati on 14/01/18.
 */

public class JourneyViewHolder extends BaseViewHolder<JourneyViewIdentifier> {

  private ImageView ivStaticMap;

  private TextView fromLocation, toLocation;


  public JourneyViewHolder(View itemView) {
    super(itemView);
    ivStaticMap = itemView.findViewById(R.id.iv_journey);
    fromLocation = itemView.findViewById(R.id.tv_from_loc);
    toLocation = itemView.findViewById(R.id.tv_to_loc);
  }

  @Override
  public void setViewData(JourneyViewIdentifier bean) {
    MJourney journey = bean.getmJourney();
    fromLocation.setText(journey.getStartLatLng());
    toLocation.setText(journey.getEndLatLng());
  }
}
