package com.example.sodukov;

import java.lang.reflect.Method;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class CanvasActivity extends Activity {
	private static int realWidth;
	private static int realHeight;
	private static int min_radix;

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Display display = getWindowManager().getDefaultDisplay();

		if ((realWidth == 0) || (realHeight == 0)) {
			if (Build.VERSION.SDK_INT >= 17) {
				// new pleasant way to get real metrics
				DisplayMetrics realMetrics = new DisplayMetrics();
				display.getRealMetrics(realMetrics);
				realWidth = realMetrics.widthPixels;
				realHeight = realMetrics.heightPixels;

			} else if (Build.VERSION.SDK_INT >= 14) {
				// reflection for this weird in-between time
				try {
					Method mGetRawH = Display.class.getMethod("getRawHeight");
					Method mGetRawW = Display.class.getMethod("getRawWidth");
					realWidth = (Integer) mGetRawW.invoke(display);
					realHeight = (Integer) mGetRawH.invoke(display);
				} catch (Exception e) {
					// this may not be 100% accurate, but it's all we've got
					realWidth = display.getWidth();
					realHeight = display.getHeight();
				}

			} else {
				// This should be close, as lower API devices should not have
				// window
				// navigation bars
				realWidth = display.getWidth();
				realHeight = display.getHeight();
			}

			min_radix = realWidth > realHeight ? realHeight : realWidth;
			min_radix /= 2;
		}

		// setContentView(R.layout.activity_canvas);
		setContentView(new GraphicsView(this));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.canvas, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	static public class GraphicsView extends View {
		private Path circle;
		private Paint cPaint;
		private Paint tPaint;
		private static final String QUOTE = "Now is the time for all "
				+ "good men to come to the aid of their country.";

		public GraphicsView(Context context) {
			super(context);

			circle = new Path();
			circle.addCircle(realWidth / 2, realHeight / 2, min_radix - 20,
					Direction.CW);

			cPaint = new Paint();
			tPaint = new Paint();
			cPaint.setColor(Color.GRAY);
			tPaint.setColor(Color.RED);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			// Drawing commands go here
			canvas.drawPath(circle, cPaint);
			canvas.drawTextOnPath(QUOTE, circle, 0, 20, tPaint);
		}
	}
}
