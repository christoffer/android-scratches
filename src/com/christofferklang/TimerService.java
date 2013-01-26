package com.christofferklang;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class TimerService extends Service {
  private static final String TAG = TimerService.class.getName();

  // timestamp when the timing was started
  private long mTimestampStarted = 0;
  private boolean mIsRunning = false;

  // Binder object given to clients
  private final IBinder binder = new LocalBinder();

  @Override public IBinder onBind(Intent intent) {
    return binder;
  }

  // Binding interface between the client and the service
  public class LocalBinder extends Binder {
    TimerService getService() {
      // Since the client and the service are known to be in the same process
      // we can safely just return the instance here.
      return TimerService.this;
    }
  }

  /**
   * Resets and starts the timer unless already started.
   */
  public void startTimer() {
    if(!mIsRunning) {
      Log.d(TAG, "startTimer(): starting timer");
      resetTimer();
      mIsRunning = true;
    } else {
      Log.d(TAG, "startTimer(): already running");
    }
  }

  /**
   * Stops the timer.
   *
   * @return elapsed milliseconds
   */
  public long stopTimer() {
    Log.d(TAG, "stopTimer()");
    final long elapsed = getElapsed();
    mIsRunning = false;
    return elapsed;
  }

  /**
   * Resets the timer.
   */
  public void resetTimer() {
    Log.d(TAG, "resetTimer()");
    mTimestampStarted = System.currentTimeMillis();
  }

  /**
   * Gets the number of milliseconds elapsed since timer was started.
   * Returns -1 if the timer is stopped.
   *
   * @return milliseconds since timer was started
   */
  public long getElapsed() {
    if(mIsRunning) {
      final long elapsed = System.currentTimeMillis() - mTimestampStarted;
      Log.d(TAG, "getElapsed(): " + elapsed);
      return elapsed;
    } else {
      Log.d(TAG, "getElapsed(): Not running");
      return -1;
    }
  }
}
