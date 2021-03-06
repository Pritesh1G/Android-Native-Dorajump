package com.tpgstudio.dorajump;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import com.tpgstudio.dorajump.R;
import com.neurondigital.nudge.Instance;
import com.neurondigital.nudge.Particles;
import com.neurondigital.nudge.Screen;
import com.neurondigital.nudge.Sprite;

public class Character extends Instance {
	public static final int NORMAL = 0, ROCKET1 = 1, ROCKET2 = 2, BALLOON = 3;
	private Sprite[] power_images = new Sprite[3];
	private Particles smoke;
	public int power = NORMAL;
	Screen screen;
	private int power_timout = 0;
	private final int[] POWER_TIME = new int[] { 40, 60, 180 };//TODO: you may wish to change powers time on character
	public float NORMAL_ACCELERATIONY;

	public Character(float x, float y, Screen screen) {
		super(new Sprite(BitmapFactory.decodeResource(screen.getResources(), R.drawable.character), screen.ScreenWidth() * 0.12f), x, y, screen, true);
		power_images[0] = new Sprite(BitmapFactory.decodeResource(screen.getResources(), R.drawable.rocket1), screen.ScreenWidth() * 0.17f);
		power_images[1] = new Sprite(BitmapFactory.decodeResource(screen.getResources(), R.drawable.rocket2), screen.ScreenWidth() * 0.37f);
		power_images[2] = new Sprite(BitmapFactory.decodeResource(screen.getResources(), R.drawable.balloon), screen.ScreenWidth() * 0.22f);

		Bitmap smoke_img = Sprite.Scale(BitmapFactory.decodeResource(screen.getResources(), R.drawable.cloud), screen.ScreenWidth() * 0.05f);
		smoke = new Particles(smoke_img, 1, 80, 180, 0, 0, 5, 60);
		NORMAL_ACCELERATIONY = -screen.ScreenHeight() * 0.00095f ;//TODO: you may wish to change character gravity
		accelerationy = NORMAL_ACCELERATIONY;
		this.screen = screen;
	}

	@Override
	public void Update() {
		super.Update();
		if (power == ROCKET1 || power == ROCKET2)
			smoke.update();

		if (power_timout > 0)
			power_timout--;
		else if (power != NORMAL) {
			power = NORMAL;
			accelerationy = NORMAL_ACCELERATIONY;
		}

	}

	public void setPower(int power) {
		this.power = power;
		if (power == NORMAL) {
			power = NORMAL;
			accelerationy = NORMAL_ACCELERATIONY;
		} else {
			power_timout = POWER_TIME[power - 1];
			if (power == ROCKET1) {
				accelerationy = 0;
				speedy = screen.ScreenHeight() * 0.05f;
			}
			if (power == ROCKET2) {
				accelerationy = 0;
				speedy = screen.ScreenHeight() * 0.05f;
			}
		}
	}

	public void draw(Canvas canvas) {
		if (power == ROCKET1) {
			smoke.draw(canvas, screen.ScreenX(x + getWidth() * 0.9f), screen.ScreenY(y - getHeight() * 0.8f));
			smoke.draw(canvas, screen.ScreenX(x - getWidth() * 0.2f), screen.ScreenY(y - getHeight() * 0.8f));
			power_images[ROCKET1 - 1].draw(canvas, screen.ScreenX(x + getWidth() / 2 - power_images[ROCKET1 - 1].getWidth() / 2), screen.ScreenY(y - getHeight() + power_images[ROCKET1 - 1].getHeight()));

		}
		super.draw(canvas);

		if (power == ROCKET2) {
			smoke.draw(canvas, screen.ScreenX(x + getWidth() * 0.4f), screen.ScreenY(y - getHeight() * 0.8f));
			power_images[ROCKET2 - 1].draw(canvas, screen.ScreenX(x + getWidth() / 2 - power_images[ROCKET2 - 1].getWidth() / 2), screen.ScreenY(y + getHeight() * 1.8f));
		}
		if (power == BALLOON) {
			power_images[BALLOON - 1].draw(canvas, screen.ScreenX(x + getWidth() / 2 - power_images[BALLOON - 1].getWidth() / 2), screen.ScreenY(y - getHeight() / 2 + power_images[BALLOON - 1].getHeight() / 2));
		}
	}
}
