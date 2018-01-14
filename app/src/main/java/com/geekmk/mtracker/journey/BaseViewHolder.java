package com.geekmk.mtracker.journey;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by manikanta.garikipati on 14/01/18.
 */

public abstract class BaseViewHolder<T extends ViewIdentifier> extends RecyclerView.ViewHolder {

  public BaseViewHolder(View itemView) {
    super(itemView);
  }

  abstract public void setViewData(T bean);
}