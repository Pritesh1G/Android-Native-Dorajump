package com.neurondigital.nudge;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class Particles {

	Bitmap particle_img;
	int number = 100;//number of particles per smoke TODO: change for denser smoke
	float[] Particle_x = new float[number], Particle_y = new float[number], direction = new float[number], life = new float[number], alpha = new float[number];
	Paint paint = new Paint();
	int counter = 0, delay = 2, particle_direction = -1, speed = 1, particle_life;
	int initialx_offset = 70, initialy_offset = 40;

	public Particles(Bitmap particle_img, int delay, int speed, int particle_direction, int initialx_offset, int initialy_offset, int particle_life, int numberOfParticles) {
		this.particle_img = particle_img;
		this.delay = delay;
		this.speed = speed;
		this.particle_direction = particle_direction;
		this.initialx_offset = initialx_offset;
		this.initialy_offset = initialy_offset;
		this.particle_life = particle_life;
		this.number = numberOfParticles;

		for (int i = 0; i < number; i++) {
			Particle_x[i] = (float) ((Math.random() * initialx_offset) - (Math.random() * initialx_offset));//initial particle x-------CHANGE ME for larger initial cloud
			Particle_y[i] = (float) ((Math.random() * initialy_offset) - (Math.random() * initialy_offset));//initial particle y-------CHANGE ME for larger initial cloud
			if (particle_direction == -1)
				direction[i] = (int) (Math.random() * 360);//direction of each particle-------CHANGE ME to change direction of particle
			else
				direction[i] = (float) ((int) (Math.random() * 20) - (Math.random() * 20) + particle_direction);
			life[i] = particle_life + (int) (Math.random() * particle_life);//life of each particle-------CHANGE ME for growing clouds
			alpha[i] = (int) (Math.random() * 200);//alpha of each particle-------CHANGE ME to change transparency
		}
	}

	public void update() {
		counter++;
		if (counter > delay) {
			counter = 0;
			for (int i = 0; i < number; i++) {
				life[i]--;
				if (life[i] > 0) {
					Particle_x[i] = (float) (Particle_x[i] + (Math.cos(-((direction[i] * Math.PI / 180) - (Math.PI / 2))) * (Math.random() * speed)));
					Particle_y[i] = (float) (Particle_y[i] - (Math.sin(-((direction[i] * Math.PI / 180) - (Math.PI / 2))) * (Math.random() * speed)));
				} else {
					Particle_x[i] = (float) ((Math.random() * initialx_offset) - (Math.random() * initialx_offset));
					Particle_y[i] = (float) ((Math.random() * initialy_offset) - (Math.random() * initialy_offset));
					if (particle_direction == -1)
						direction[i] = (int) (Math.random() * 360);
					else
						direction[i] = (float) ((int) (Math.random() * 15) - (Math.random() * 15) + particle_direction);
					life[i] = particle_life + (int) (Math.random() * particle_life);
				}
			}
		}
	}

	public void setDirection(int Direction) {
		particle_direction = Direction;
	}

	public void draw(Canvas canvas, float x, float y) {
		//draw cloud
		for (int i = 0; i < number; i++) {
			paint.setAlpha((int) life[i]);
			canvas.drawBitmap(particle_img, x + Particle_x[i], y + Particle_y[i], paint);
		}
	}
}
