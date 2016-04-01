package com.neurondigital.nudge;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;

public class Physics {
	RectF a = null, b = null;

	//collisions
	public boolean intersect(int x, int y, int width, int height, int x2, int y2, int width2, int height2) {
		a = new RectF(x, y, x + width, y + height);
		b = new RectF(x2, y2, x2 + width2, y2 + height2);
		return a.intersect(b);
	}

	public boolean intersect(int x, int y, int width, int height, int pointx, int pointy) {
		a = new RectF(x, y, x + width, y + height);
		if ((pointx > x) && (pointy > y) && (pointx < (x + width)) && (pointy < (y + height))) {
			return (true);
		}
		return false;
	}

	public void drawDebug(Canvas canvas) {
		//draw collisions

		Paint paint = new Paint();
		paint.setColor(Color.RED);
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(5);
		if (a != null)
			canvas.drawRect(a, paint);
		if (b != null)
			canvas.drawRect(b, paint);

	}
}
