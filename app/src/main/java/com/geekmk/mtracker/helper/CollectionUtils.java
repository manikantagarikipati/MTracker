package com.geekmk.mtracker.helper;

import java.util.Collection;

/**
 * Created by manikanta.garikipati on 14/01/18.
 */

public class CollectionUtils {

  /**
   * Hide the default constructor. Instantiating utility classes does not make sense.
   */
  private CollectionUtils() {

  }

  public static boolean isNotEmpty(Collection input) {
    return input != null && !input.isEmpty();
  }

  public static boolean isEmpty(Collection input) {
    return input == null || input.isEmpty();
  }


}
