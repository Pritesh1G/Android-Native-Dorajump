package com.neurondigital.nudge;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.SystemClock;

public class Sprite {
	Bitmap unrotated_image_sequence[], image_sequence[];
	int current_image = 0;
	private float direction = 0;
	long start_time = SystemClock.uptimeMillis();
	int animation_speed;
	public Paint imagePaint = new Paint();

	/**
	 * @param image
	 *            The bitmap image to attach to Sprite
	 * @param scale
	 *            scale factor of image. Eg: ScreenWidth * 0.15f
	 */
	public Sprite(Bitmap image, float scale) {
		image_sequence = new Bitmap[1];

		unrotated_image_sequence = new Bitmap[1];
		image_sequence[0] = unrotated_image_sequence[0] = Bitmap.createScaledBitmap(image, (int) ((float) (scale)), (int) (((float) (scale) / image.getWidth()) * image.getHeight()), true);

	}

	public Sprite(Bitmap sprite_sheet, float scale, int itemsX, int length, int animation_speed) {
		this.animation_speed = animation_speed;
		unrotated_image_sequence = convert(sprite_sheet, itemsX, length);
		image_sequence = new Bitmap[length];
		//scale images
		for (int count = 0; count < length; count++) {
			image_sequence[count] = unrotated_image_sequence[count] = Bitmap.createScaledBitmap(unrotated_image_sequence[count], (int) ((float) (scale)), (int) (((float) (scale) / unrotated_image_sequence[count].getWidth()) * unrotated_image_sequence[count].getHeight()), true);
		}
		start_time = (long) (SystemClock.uptimeMillis() + (Math.random() * 400));
	}

	public int getWidth() {
		return image_sequence[0].getWidth();
	}

	public int getHeight() {
		return image_sequence[0].getHeight();
	}

	public float getDirection() {
		return direction;
	}

	//draw the sprite to screen
	public void draw(Canvas canvas, float x, float y) {
		//draw image
		canvas.drawBitmap(image_sequence[current_image], x, y, imagePaint);
		//update to next image
		if (image_sequence.length > 1) {
			long now = SystemClock.uptimeMillis();
			if (now > start_time + (500 - animation_speed)) {
				start_time = SystemClock.uptimeMillis();
				current_image++;
				if (current_image + 1 > image_sequence.length)
					current_image = 0;
			}
		}
	}

	//convert from sprite sheet to image array
	private Bitmap[] convert(Bitmap sprite_sheet, int itemsX, int length) {
		int itemsY = (int) Math.ceil(length / itemsX);
		int tile_height = (int) (sprite_sheet.getHeight() / itemsY);
		int tile_width = (int) (sprite_sheet.getWidth() / itemsX);
		Bitmap image_sequence[] = new Bitmap[length];

		for (int y = 0; y < itemsY; y++) {
			for (int x = 0; x < itemsX; x++) {
				if ((x + (itemsX * y)) < length)
					image_sequence[x + (itemsX * y)] = Bitmap.createBitmap(sprite_sheet, (x * tile_width), (y * tile_height), tile_width, tile_height);
			}
		}
		return image_sequence;
	}

	/**
	 * Rotates the sprite by a specified angle. 0 means Top.
	 * 
	 * @param direction
	 *            The new direction of the sprite in degrees
	 */
	public void rotate(float direction) {
		float angle = direction;
		this.direction = direction;

		Paint paint = new Paint();
		paint.setAntiAlias(true);
		Bitmap m = Bitmap.createBitmap((int) 5, 5, Bitmap.Config.ARGB_8888);

		//int height = (int) (unrotated_image_sequence[0].getWidth() * Math.abs(Math.sin(direction * (Math.PI / 180)))) + unrotated_image_sequence[0].getHeight();
		//int width = (int) (unrotated_image_sequence[0].getWidth() * Math.abs(Math.cos(direction * (Math.PI / 180)))) + unrotated_image_sequence[0].getHeight();
		RectF a = new RectF(0, 0, unrotated_image_sequence[0].getWidth(), unrotated_image_sequence[0].getHeight());
		Matrix mat = new Matrix();
		mat.setRotate(direction, (unrotated_image_sequence[0].getWidth() / 2), (unrotated_image_sequence[0].getHeight() / 2));
		mat.mapRect(a);

		for (int i = 0; i < image_sequence.length; i++) {
			image_sequence[i] = Bitmap.createScaledBitmap(m, (int) a.width(), (int) a.height(), true);
			Canvas canvas = new Canvas(image_sequence[i]);
			canvas.drawColor(Color.BLUE);
			canvas.rotate(angle, image_sequence[i].getWidth() / 2, image_sequence[i].getHeight() / 2);
			canvas.drawBitmap(unrotated_image_sequence[i], (image_sequence[i].getWidth() / 2) - (unrotated_image_sequence[i].getWidth() / 2), (image_sequence[i].getHeight() / 2) - (unrotated_image_sequence[i].getHeight() / 2), paint);
			canvas.rotate(-angle, image_sequence[i].getWidth() / 2, image_sequence[i].getHeight() / 2);
		}

	}

	public static Bitmap Scale(Bitmap unscaled, float scale) {
		return Bitmap.createScaledBitmap(unscaled, (int) ((float) (scale)), (int) (((float) (scale) / unscaled.getWidth()) * unscaled.getHeight()), true);
	}
}
