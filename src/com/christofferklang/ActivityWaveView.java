package com.christofferklang;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class ActivityWaveView extends Activity {
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_wave_view);

    final WaveView small = (WaveView) findViewById(R.id.waveSmall);
    small.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        if(small.isRunning()) {
          small.stop();
        } else {
          small.start();
        }
      }
    });

    final WaveView large = (WaveView) findViewById(R.id.waveLarge);
    large.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        if(large.isRunning()) {
          large.stop();
        } else {
          large.start();
        }
      }
    });
  }
}
