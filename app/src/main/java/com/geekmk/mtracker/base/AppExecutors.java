package com.geekmk.mtracker.base;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Mani on 19/09/17.
 *
 * Global executor pools for the whole application.
 *
 * <p>
 * Grouping tasks like this avoids the effects of task starvation (e.g. disk reads don't wait behind
 * webservice requests).
 */

public class AppExecutors {

  private static int THREAD_COUNT = 1;

  private final Executor diskIO;

  private final Executor networkIO;

  private final Executor mainThread;

  private AppExecutors(Executor diskIO, Executor networkIO, Executor mainThread) {
    this.diskIO = diskIO;
    this.networkIO = networkIO;
    this.mainThread = mainThread;
  }

  public AppExecutors() {
    this(Executors.newSingleThreadExecutor(), Executors.newFixedThreadPool(THREAD_COUNT),
        new MainThreadExecutor());
  }

  public Executor diskIO() {
    return diskIO;
  }

  public Executor networkIO() {
    return networkIO;
  }

  public Executor mainThread() {
    return mainThread;
  }


  private static class MainThreadExecutor implements Executor {

    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    @Override
    public void execute(@NonNull Runnable runnableCommand) {
      mainThreadHandler.post(runnableCommand);
    }
  }
}
