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
  private TimerService mService = null;

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
    startService(serviceIntent);
    bindService(serviceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
  }

  @Override protected void onStop() {
    super.onStop();
    Log.d(TAG, "onStop()");
    if(mService == null) {
      Log.d(TAG, "No service currently bound.");
    } else {
      Log.d(TAG, "Unbinding from service.");

      if(!mService.isRunning()) {
        mService.stopSelf();
      }

      unbindService(mServiceConnection);
      mService = null;
    }
  }

  @SuppressWarnings("UnusedParameters")
  public void onStartClicked(View button) {
    Log.d(TAG, "onStartClicked()");
    if(mService != null) {
      mService.startTimer();
      textStatus.setText("Elapsed: " + mService.getElapsed());
    }
  }

  @SuppressWarnings("UnusedParameters")
  public void onStopClicked(View button) {
    Log.d(TAG, "onStopClicked()");
    if(mService != null) {
      final long elapsed = mService.stopTimer();
      textStatus.setText("Ended at: " + elapsed);
    }
  }

  // Callback handler for the connection to the service
  private ServiceConnection mServiceConnection = new ServiceConnection() {
    @Override public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
      Log.d(TAG, "onServiceConnected()");
      TimerService.LocalBinder binder = (TimerService.LocalBinder) iBinder;
      // Set the connected service
      mService = binder.getService();

      textStatus.setText("Connected");
    }

    @Override public void onServiceDisconnected(ComponentName componentName) {
      Log.d(TAG, "onServiceDisconnected()");
      // No longer connected to the service
      mService = null;
      textStatus.setText("Disconnected");
    }
  };
}
