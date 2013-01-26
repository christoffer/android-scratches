package com.christofferklang;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class ActivityBackground extends Activity {
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_background);
  }
}
