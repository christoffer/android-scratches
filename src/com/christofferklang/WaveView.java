package com.christofferklang;

import android.content.Context;
import android.graphics.*;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class WaveView extends View {
  private static final String TAG = WaveView.class.getName();
  private static final long PIXELS_PER_SECOND = 25;
  private static final long FPS = 20;
  private static final long UPDATE_INTERVAL = 1000 / FPS;
  private static final double SWINGS_PER_LENGTH = 2;

  private float offset;

  private Handler handler = new Handler();

  private float[] waveform = new float[0];
  private float waveformLengthScale = 1.0f;

  private Paint primaryPaint;
  private Paint secondaryPaint;

  private long timestamp;

  private Bitmap bitmap;
  private Paint gradientPaint;
  private boolean isRunning = false;

  public WaveView(Context context) {
    super(context);
    init();
  }

  public WaveView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  @Override protected void onDraw(Canvas canvas) {
    final int width = getWidth();
    final int height = getHeight();
    if(bitmap == null) {
      createBitmap();
    }
    if(gradientPaint == null) {
      createGradientPaint();
    }
    canvas.drawBitmap(bitmap, -offset, 0, null);
    canvas.drawRect(0, 0, getWidth(), getHeight(), gradientPaint);
  }

  private void createGradientPaint() {
    gradientPaint = new Paint();
//    LinearGradient gradient = new LinearGradient(getWidth(), 0, getWidth() * 0.13f, 0, Color.argb(0, 0, 0, 0), Color.argb(255, 0, 0, 0), Shader.TileMode.CLAMP);
    int fill = Color.argb(255, 0, 0, 0);
    int transparent = Color.argb(0, 0, 0, 0);
    int[] colors = new int[] { fill,  transparent, transparent, fill };
    float[] positions = new float[] { 0.2f, 0.8f, 0.9f, 0.98f };
    LinearGradient gradient = new LinearGradient(0, 0, getWidth(), 0, colors, positions, Shader.TileMode.CLAMP);
    gradientPaint.setShader(gradient);
  }

  private void createBitmap() {
    generateWaveform(20, 0.5f);
    int w = getWaveformWidth();
    bitmap = Bitmap.createBitmap(w * 2, getHeight(), Bitmap.Config.RGB_565);
    Canvas bitmapCanvas = new Canvas(bitmap);
    drawWaveformPath(bitmapCanvas, 0, w, getHeight(), primaryPaint);
    drawWaveformPath(bitmapCanvas, -w, w, getHeight(), primaryPaint);

    generateWaveform(20, 0.5f);
    drawWaveformPath(bitmapCanvas, 0, w, getHeight(), secondaryPaint);
    drawWaveformPath(bitmapCanvas, -w, w, getHeight(), secondaryPaint);

    generateWaveform(20, 0.5f);
    drawWaveformPath(bitmapCanvas, 0, w, getHeight(), secondaryPaint);
    drawWaveformPath(bitmapCanvas, -w, w, getHeight(), secondaryPaint);
  }

  public void start() {
    isRunning = true;
    handler.removeCallbacks(animationTimer);
    handler.postDelayed(animationTimer, UPDATE_INTERVAL);
  }

  public void stop() {
    isRunning = false;
    handler.removeCallbacks(animationTimer);
  }

  public boolean isRunning() {
    return isRunning;
  }

  private Runnable animationTimer = new Runnable() {
    @Override public void run() {
      long now = System.currentTimeMillis();
      long elapsed = now - timestamp;
      float delta = PIXELS_PER_SECOND * ((float) elapsed / 1000.0f);
      timestamp = now;
      offset = (offset + delta) % getWaveformWidth();
      invalidate();
      handler.postDelayed(animationTimer, UPDATE_INTERVAL);
    }
  };

  private void init() {
    Log.d(TAG, "Initializing");
    primaryPaint = new Paint() {{
      setColor(Color.argb(188, 67, 128, 198));
      setStyle(Paint.Style.STROKE);
      setStrokeWidth(3.0f);
      setAntiAlias(true);
      setPathEffect(new CornerPathEffect(45));
    }};

    secondaryPaint = new Paint() {{
      setColor(Color.argb(30, 255, 228, 220));
      setStyle(Paint.Style.STROKE);
      setStrokeWidth(2.0f);
      setAntiAlias(true);
      setPathEffect(new CornerPathEffect(20));
      setXfermode(new PorterDuffXfermode(PorterDuff.Mode.LIGHTEN));
    }};
  }

  /**
   * Generates points on a wave form to render.
   * The generated points are uniform to the coordinate system [-1.0, 1.0]
   *
   * @param numPoints   number of points on the waveform to generate
   * @param lengthScale scale the length of the waveform (shorter width = denser ripples)
   */
  private void generateWaveform(int numPoints, float lengthScale) {
    if(numPoints < 0) {
      throw new IllegalArgumentException("Need at least some points");
    }
    waveform = new float[numPoints];
    for(int i = 0; i < numPoints; i++) {
      final float offsetFromCenter = 0.2f + (float) Math.random() * 0.2f;
      waveform[i] = i % 2 == 0 ? offsetFromCenter : -offsetFromCenter;
    }
    waveformLengthScale = (numPoints - 1) * lengthScale;
  }

  private int getWaveformWidth() {
    int width = getWidth();
    return Math.max(width, (int) (width * waveformLengthScale));
  }

  private void drawWaveformPath(Canvas canvas, final float offset, final int width, final int height, Paint paint) {
    canvas.save();
    canvas.translate(-offset, 0);

    // Initialize cursor
    Path path = new Path();

    final int centerY = height / 2;
    final float scaleY = 1.0f;

    final int numPoints = waveform.length;
    final float halfNumPoints = numPoints * 0.5f;

    final float scaledHeight = height * scaleY;
    final float halfHeight = 0.5f * scaledHeight;
    final int scaledWidth = getWaveformWidth();
    final float wobbleSpeedConstant = (float) (SWINGS_PER_LENGTH * Math.PI);

    final float stepSize = scaledWidth / numPoints;
    final float halfStepSize = stepSize * 0.5f;

    int prevY = 0;
    for(int i = 0; i < numPoints + 1; i++) {
      final int x = (int) stepSize * i;
      final int handleX = (int) (x - halfStepSize);

      final float offsetProgression = x / width;
      final int index = i < numPoints ? i : 0;
      final float waveformValue = waveform[index];
      final float sine = (float) Math.sin(wobbleSpeedConstant * offsetProgression + Math.abs(halfNumPoints - i));
      final int y = (int) (centerY + halfHeight * waveformValue * sine);

      if(i == 0) {
        path.moveTo(0, y);
      } else {
        path.cubicTo(
            handleX, prevY,
            handleX, y,
            x, y);
      }
      prevY = y;
    }
    canvas.drawPath(path, paint);
    canvas.restore();
  }
}
