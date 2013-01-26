package com.christofferklang;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AndroidScratchPad extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

      Button buttonWaveView = (Button) findViewById(R.id.buttonWaveView);
      buttonWaveView.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          startWaveView();
        }
      });

      Button backgroundService = (Button) findViewById(R.id.buttonBackground);
      backgroundService.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          startBackgroundServiceActivity();
        }
      });
    }
  private void startBackgroundServiceActivity() {
    Intent startBackgroundActivity = new Intent(this, ActivityBackground.class);
    startActivity(startBackgroundActivity);
  }

  private void startWaveView() {
    Intent startWaveView = new Intent(this, ActivityWaveView.class);
    startActivity(startWaveView);
  }
}
