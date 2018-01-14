package com.geekmk.mtracker.journey;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.geekmk.mtracker.R;
import com.geekmk.mtracker.database.journey.MJourney;
import com.geekmk.mtracker.helper.AppConstants;
import com.geekmk.mtracker.helper.AppUtils;
import com.geekmk.mtracker.helper.MapUtils;
import com.geekmk.mtracker.journeydetail.JourneyDetailActivity;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

/**
 * Created by manikanta.garikipati on 14/01/18.
 */

public class JourneyViewHolder extends BaseViewHolder<JourneyViewIdentifier> implements
    OnClickListener {

  private ImageView ivStaticMap;

  private TextView fromLocation, toLocation;

  private MJourney journey;

  private Context context;

  public JourneyViewHolder(View itemView) {
    super(itemView);
    context = itemView.getContext();
    ivStaticMap = itemView.findViewById(R.id.iv_journey);
    fromLocation = itemView.findViewById(R.id.tv_from_loc);
    toLocation = itemView.findViewById(R.id.tv_to_loc);
    itemView.setOnClickListener(this);
  }

  @Override
  public void setViewData(JourneyViewIdentifier bean) {
    journey = bean.getmJourney();
    LatLng latLng = AppUtils.getLatLng(journey.getStartLatLng());
    Float height = TypedValue.applyDimension
        (TypedValue.COMPLEX_UNIT_DIP, 148,
            context.getResources().getDisplayMetrics());
    if (latLng != null) {
      String imageURL = MapUtils.getStaticMapUrl(latLng.latitude, latLng.longitude,
          context.getResources().getDisplayMetrics().widthPixels, height.intValue());
      Picasso.with(context).load(imageURL).fit().centerCrop().into(ivStaticMap);
    }
    String fromLoc;

    if (!TextUtils.isEmpty(journey.getStartPlaceName())) {
      fromLoc = context.getString(R.string.msg_frm_loc, journey.getStartPlaceName());
    } else {
      fromLoc = context.getString(R.string.msg_frm_loc, journey.getStartLatLng());
    }
    fromLocation.setText(fromLoc);
    String toLoc;
    if (!TextUtils.isEmpty(journey.getEndPlaceName())) {
      toLoc = context.getString(R.string.msg_to_loc, journey.getEndPlaceName());
    } else {
      toLoc = context.getString(R.string.msg_to_loc, journey.getEndLatLng());
    }
    toLocation.setText(toLoc);
  }

  @Override
  public void onClick(View v) {
    Intent intent = new Intent(context, JourneyDetailActivity.class);
    intent.putExtra(AppConstants.EXTRA_JOURNEY_ID, journey.getJourneyId());
    context.startActivity(intent);
  }
}
