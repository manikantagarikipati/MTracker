package com.geekmk.mtracker.helper;

import android.arch.persistence.room.TypeConverter;
import java.util.Date;

/**
 * Created by manikanta.garikipati on 14/01/18.
 *
 * A date type convertor used by room persistence to understand the date object and
 * convert it into its equivalent primitives
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
