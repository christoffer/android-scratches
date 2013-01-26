package com.christofferklang;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class ActivityBackground extends Activity {
  private static final String TAG = ActivityBackground.class.getName();
  private TimerService mConnectedTimerService = null;

  private static TextView textStatus;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_background);
    textStatus = (TextView) findViewById(R.id.textStatus);
  }

  @Override protected void onStart() {
    super.onStart();
    Log.d(TAG, "onStart()");
    Log.d(TAG, "(re-)Binding to service");
    Intent serviceIntent = new Intent(this, TimerService.class);
    bindService(serviceIntent, mTimerServiceConnection, Context.BIND_AUTO_CREATE);
  }

  @Override protected void onStop() {
    super.onStop();
    Log.d(TAG, "onStop()");
    if(mConnectedTimerService == null) {
      Log.d(TAG, "No service currently bound.");
    } else {
      Log.d(TAG, "Disconnecting current bound service.");
      unbindService(mTimerServiceConnection);
      mTimerServiceConnection = null;
    }
  }

  @SuppressWarnings("UnusedParameters")
  public void onStartClicked(View button) {
    Log.d(TAG, "onStartClicked()");
    if(mConnectedTimerService != null) {
      mConnectedTimerService.startTimer();
      textStatus.setText("Elapsed: " + mConnectedTimerService.getElapsed());
    }
  }

  @SuppressWarnings("UnusedParameters")
  public void onStopClicked(View button) {
    Log.d(TAG, "onStopClicked()");
    if(mConnectedTimerService != null) {
      final long elapsed = mConnectedTimerService.stopTimer();
      textStatus.setText("Ended at: " + elapsed);
    }
  }

  // Callback handler for the connection to the service
  private ServiceConnection mTimerServiceConnection = new ServiceConnection() {
    @Override public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
      Log.d(TAG, "onServiceConnected()");
      TimerService.LocalBinder binder = (TimerService.LocalBinder) iBinder;
      // Set the connected service
      mConnectedTimerService = binder.getService();

      textStatus.setText("Connected");
    }

    @Override public void onServiceDisconnected(ComponentName componentName) {
      Log.d(TAG, "onServiceDisconnected()");
      // No longer connected to the service
      mConnectedTimerService = null;
      textStatus.setText("Disconnected");
    }
  };
}
