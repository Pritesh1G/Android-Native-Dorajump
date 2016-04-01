package com.neurondigital.nudge;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.MotionEvent;

public class Button extends Instance {
	public final int TEXT_BTN = 0, SPRITE_BTN = 1;
	public int type;
	public Paint textPaint = new Paint();
	public String text;

	/**
	 * Create new sprite button
	 * 
	 * @param sprite
	 *            sprite to bisplay on button
	 * @param x
	 *            x-coordinate to draw button
	 * @param y
	 *            y-coordinate to draw button
	 * @param screen
	 *            A reference to the main nudge engine screen instance
	 * @param world
	 *            true if you wish to draw the button relative to the camera or false if you wish to draw it relative to screen
	 */
	public Button(Sprite sprite, float x, float y, Screen screen, boolean world) {
		super(sprite, x, y, screen, world);
		type = SPRITE_BTN;
	}

	/**
	 * Create new text button
	 * 
	 * @param text
	 *            text to bisplay on button
	 * @param dpSize
	 *            size of text in dp
	 * @param font
	 *            Typface of text
	 * @param color
	 *            Color to use for text
	 * @param x
	 *            x-coordinate to draw button
	 * @param y
	 *            y-coordinate to draw button
	 * @param screen
	 *            A reference to the main nudge engine screen instance
	 * @param world
	 *            true if you wish to draw the button relative to the camera or false if you wish to draw it relative to screen
	 */
	public Button(String text, int dpSize, Typeface font, int color, float x, float y, Screen screen, boolean world) {
		super(null, x, y, screen, world);
		type = TEXT_BTN;
		textPaint = new Paint();
		textPaint.setTextSize(screen.dpToPx(dpSize));
		textPaint.setAntiAlias(true);
		textPaint.setColor(color);
		textPaint.setTypeface(font);
		this.text = text;
	}

	public void Highlight(int color) {
		ColorFilter filter = new LightingColorFilter(1, color);
		if (type == SPRITE_BTN)
			sprite.imagePaint.setColorFilter(filter);
		else
			textPaint.setColorFilter(filter);
	}

	public void LowLight() {
		ColorFilter filter = null;
		if (type == SPRITE_BTN)
			sprite.imagePaint.setColorFilter(filter);
		else
			textPaint.setColorFilter(filter);
	}

	@Override
	public int getWidth() {
		if (type == SPRITE_BTN)
			return super.getWidth();
		else {
			Rect bounds = new Rect();
			textPaint.getTextBounds(text, 0, text.length(), bounds);
			return bounds.width();
		}
	}

	@Override
	public int getHeight() {
		if (type == SPRITE_BTN)
			return super.getHeight();
		else {
			Rect bounds = new Rect();
			textPaint.getTextBounds(text, 0, text.length(), bounds);
			return bounds.height();
		}
	}

	//draw the sprite to screen
	@Override
	public void draw(Canvas canvas) {
		if (type == SPRITE_BTN)
			super.draw(canvas);
		else {
			canvas.drawText(text, x, y + getHeight(), textPaint);
			if (screen.debug_mode)
				physics.drawDebug(canvas);
		}

	}

	@Override
	public boolean isTouched(MotionEvent event) {
		if (world)
			return physics.intersect(screen.ScreenX((int) x), screen.ScreenY((int) y), getWidth(), (int) getHeight(), (int) event.getX(), (int) event.getY());
		else
			return physics.intersect((int) x, (int) y, getWidth(), (int) getHeight(), (int) event.getX(), (int) event.getY());

	}

}
