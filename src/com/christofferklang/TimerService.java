package com.christofferklang;

import android.*;
import android.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class TimerService extends Service {
  private static final String TAG = TimerService.class.getName();

  private static final int NOTIFICATION_ID = 1;

  private Notification mNotification;
  private static NotificationManager mNotificationManager;

  // timestamp when the timing was started
  private long mTimestampStarted = 0;
  private boolean mIsRunning = false;

  // Binder object given to clients
  private final IBinder binder = new LocalBinder();

  @Override public IBinder onBind(Intent intent) {
    return binder;
  }

  @Override public void onCreate() {
    Log.d(TAG, "onCreate()");
    super.onCreate();

    mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    mNotification = new Notification(R.drawable.btn_star, "Timer started", System.currentTimeMillis());
    mNotification.flags |= Notification.FLAG_NO_CLEAR;
  }

  @Override public void onStart(Intent intent, int startId) {
    Log.d(TAG, "onStart()");
    super.onStart(intent, startId);
  }

  @Override public void onRebind(Intent intent) {
    Log.d(TAG, "onRebind()");
    super.onRebind(intent);
  }

  @Override public boolean onUnbind(Intent intent) {
    Log.d(TAG, "onUnbind()");
    return super.onUnbind(intent);
  }

  @Override public void onDestroy() {
    Log.d(TAG, "onDestroy()");
    super.onDestroy();
    mNotificationManager.cancel(NOTIFICATION_ID);
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
      startForeground(NOTIFICATION_ID, mNotification);
    } else {
      Log.d(TAG, "startTimer(): already running");
    }

    updateTicker();
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
    updateTicker();
    return elapsed;
  }

  /**
   * Resets the timer.
   */
  public void resetTimer() {
    Log.d(TAG, "resetTimer()");
    mTimestampStarted = System.currentTimeMillis();
    updateTicker();
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

  /**
   * Indicate whether or not the timer is currently active.
   *
   * @return true if active, false otherwise
   */
  public boolean isRunning() {
    return mIsRunning;
  }

  /**
   * Update the ticker with the current state of the Timer.
   */
  private void updateTicker() {
    Intent intent = new Intent(getApplicationContext(), ActivityBackground.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    String content = "-";
    if(isRunning()) {
      content = "Elapsed: " + getElapsed();
    }
    mNotification.setLatestEventInfo(this, "Timer: " + (isRunning() ? "Running" : "Stopped"), content, pendingIntent);
    mNotificationManager.notify(NOTIFICATION_ID, mNotification);
  }
}
