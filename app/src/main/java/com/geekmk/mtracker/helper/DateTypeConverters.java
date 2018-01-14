package com.geekmk.mtracker.helper;

import android.arch.persistence.room.TypeConverter;
import java.util.Date;

/**
 * Created by manikanta.garikipati on 14/01/18.
 */

public class DateTypeConverters {

  @TypeConverter
  public Date fromTimestamp(Long value) {
    return value == null ? null : new Date(value);
  }

  @TypeConverter
  public Long dateToTimestamp(Date date) {
    if (date == null) {
      return null;
    } else {
      return date.getTime();
    }
  }

}
