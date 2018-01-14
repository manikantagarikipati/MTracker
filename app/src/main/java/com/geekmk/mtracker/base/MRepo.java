package com.geekmk.mtracker.base;

import android.content.Context;
import com.geekmk.mtracker.database.journey.JourneyFetchCB;
import com.geekmk.mtracker.database.journey.JourneyInsertCB;
import com.geekmk.mtracker.database.journey.MJourney;

/**
 * Created by manikanta.garikipati on 14/01/18.
 */

public final class MRepo {

  private AppExecutors appExecutors;

  private static MRepo INSTANCE;

  private DataSourceInf dataSource;

  public static void initialize(Context context) {

    if (INSTANCE == null) {
      synchronized (MRepo.class) {
        if (INSTANCE == null) {
          INSTANCE = new MRepo(new AppExecutors(), DataSourceImpl.getINSTANCE(context));
        }
      }
    }
  }

  private MRepo(AppExecutors executors, DataSourceInf dataSource) {
    this.appExecutors = executors;
    this.dataSource = dataSource;
  }

  public static void addJourney(final MJourney mJourney, final JourneyInsertCB journeyInsertCB) {

    INSTANCE.appExecutors.diskIO().execute(new Runnable() {
      @Override
      public void run() {
        final long id = INSTANCE.dataSource.saveJourney(mJourney);

        INSTANCE.appExecutors.mainThread().execute(new Runnable() {
          @Override
          public void run() {
            if (journeyInsertCB != null) {
              journeyInsertCB.onInsertSuccess(id);
            }
          }
        });
      }
    });
  }


  public static void fetchJourney(final long journeyId, final JourneyFetchCB journeyFetchCB) {
    INSTANCE.appExecutors.diskIO().execute(new Runnable() {
      @Override
      public void run() {
        final MJourney journey = INSTANCE.dataSource.fetchJourney(journeyId);

        INSTANCE.appExecutors.mainThread().execute(new Runnable() {
          @Override
          public void run() {
            if (journeyFetchCB != null) {
              if (journey != null) {
                journeyFetchCB.onJourneyLoaded(journey);
              } else {
                journeyFetchCB.onError();
              }
            }
          }
        });
      }
    });
  }

}
