package com.geekmk.mtracker.journey;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.geekmk.mtracker.R;
import com.geekmk.mtracker.helper.CollectionUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by manikanta.garikipati on 14/01/18.
 */

public class JourneyAdapter extends RecyclerView.Adapter<BaseViewHolder> {

  private List<ViewIdentifier> viewIdentifierList;

  public JourneyAdapter() {
    viewIdentifierList = new ArrayList<>();

  }

  public void resetView(List<ViewIdentifier> viewIdentifierList) {
    if (CollectionUtils.isNotEmpty(viewIdentifierList)) {
      viewIdentifierList.clear();
      this.viewIdentifierList.addAll(viewIdentifierList);
      notifyDataSetChanged();
    }
  }

  @Override
  public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
    switch (viewType) {
      case R.layout.item_journey:
        return new JourneyViewHolder(view);
      case R.layout.item_empty_journeys:
        return new EmptyJourneysViewHolder(view);
    }
    return null;
  }

  @Override
  public void onBindViewHolder(BaseViewHolder holder, int position) {

  }

  @Override
  public int getItemCount() {
    return CollectionUtils.isEmpty(viewIdentifierList) ? 0 : viewIdentifierList.size();
  }
}
