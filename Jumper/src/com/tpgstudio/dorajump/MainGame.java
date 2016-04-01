package com.tpgstudio.dorajump;

import java.util.ArrayList;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.MotionEvent;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.InterstitialAd;
import com.google.ads.AdRequest.ErrorCode;
import com.tpgstudio.dorajump.R;
import com.neurondigital.nudge.Button;
import com.neurondigital.nudge.HighScoreManager;
import com.neurondigital.nudge.Instance;
import com.neurondigital.nudge.Screen;
import com.neurondigital.nudge.Sprite;

public class MainGame extends Screen {

	//paints
	Paint background_shader = new Paint();
	Paint Title_Paint = new Paint();
	Paint SubTitle_Paint = new Paint();
	Paint Score_Paint = new Paint();
	Paint Instruction_Paint = new Paint();
	Paint Black_shader = new Paint();
	Paint White_shader = new Paint();
	Paint Yellow_shader = new Paint();

	//constants
	final int X = 0, Y = 1, TYPE = 2, SPECIAL_TYPE = 3;
	final int BAR_NORMAL = 0, BAR_BREAKS = 1, BAR_MOVES = 2;
	final int NOTHING = -1, COIN1 = 0, COIN5 = 1, COIN9 = 2, BOMB = 3, TRAMPOLIN = 4, ROCKET1 = 5, ROCKET2 = 6, BALLOON = 7;

	//background
	Bitmap background;

	//instances
	Character character;
	Sprite bar_sprite[] = new Sprite[3];
	Sprite specialItem_sprite[] = new Sprite[8];
	ArrayList<Instance> bars = new ArrayList<Instance>();
	ArrayList<Instance> specialItems = new ArrayList<Instance>();
	ArrayList<Instance> clouds = new ArrayList<Instance>();
	Sprite cloud_sprite, cloud_sprite2;

	//bars
	int next_bar_to_create = 0;

	//states
	final int MENU = 0, GAMEPLAY = 1, HIGHSCORES = 2, OPTIONS = 3, GAMEOVER = 4;
	int state = MENU;
	boolean pause = false, notstarted = true;

	//menu buttons
	Button btn_Play, btn_Options, btn_Highscores, btn_Exit, btn_Home, btn_facebook, btn_Replay, btn_sound_mute, btn_music_mute, btn_pause;

	//score
	int score = 0;
	HighScoreManager highscoreManager;
	HighScoreManager.Highscore[] highscore_list;
	Sprite score_cup;

	//sound
	SoundPool sp;
	MediaPlayer music;
	int sound_coin, sound_fall, sound_gameover, sound_jump, sound_trampolin, sound_beep;
	boolean sound_muted = false, music_muted = false;
	Sprite sound_on, sound_off, music_on, music_off;

	//gameover
	int gameover_distance = 0;

	//TODO: Speed and score Controls.
	int bar_moving_speed = 1; //the moving bar's left/right speed.
	int gameover_delay = 10; //time before game over
	int initial_score = 0;
	float bounce_speed = 0.03f;
	float trampolin_speed = 0.05f;
	int accelerometer_movement_speed = 9;

	//TODO: Change Bar Positions and Special Item Positions
	final float bar_positions[][] = new float[][] {
			// {x position of bar (0.5f is cener of screen), y position of bar (the larger the further up). 1000 is like 10 seconds playtime, Bar type (BAR_NORMAL, BAR_BREAKS, BAR_MOVES), Special item on bar (NOTHING, COIN1, COIN5, COIN9, BOMB, TRAMPOLIN, ROCKET1, ROCKET2, BALLOON) }
			{ 0.5f, 60, BAR_NORMAL, NOTHING },
			{ 0.2f, 120, BAR_NORMAL, NOTHING },
			{ 0.8f, 150, BAR_NORMAL, NOTHING },
			{ 0.01f, 200, BAR_NORMAL, NOTHING },
			{ 0.6f, 230, BAR_NORMAL, COIN1 },
			{ 0.4f, 310, BAR_NORMAL, NOTHING },
			{ 0.7f, 320, BAR_NORMAL, NOTHING },
			{ 0.1f, 360, BAR_NORMAL, NOTHING },
			{ 0.5f, 390, BAR_NORMAL, COIN1 },
			{ 0.2f, 420, BAR_NORMAL, NOTHING },
			{ 0.8f, 435, BAR_NORMAL, NOTHING },
			{ 0.01f, 480, BAR_NORMAL, NOTHING },
			{ 0.6f, 490, BAR_NORMAL, NOTHING },
			{ 0.4f, 540, BAR_NORMAL, NOTHING },
			{ 0.7f, 580, BAR_NORMAL, COIN1 },
			{ 0.1f, 610, BAR_NORMAL, NOTHING },
			{ 0.5f, 630, BAR_NORMAL, NOTHING },
			//breaking bars introduced
			{ 0.1f, 660, BAR_NORMAL, NOTHING },
			{ 0.5f, 710, BAR_BREAKS, NOTHING },
			{ 0.001f, 730, BAR_NORMAL, NOTHING },
			{ 0.9f, 790, BAR_NORMAL, NOTHING },
			{ 0.1f, 795, BAR_NORMAL, COIN1 },
			{ 0.2f, 860, BAR_BREAKS, NOTHING },
			{ 0.8f, 870, BAR_NORMAL, NOTHING },
			{ 0.01f, 920, BAR_BREAKS, NOTHING },
			{ 0.5f, 960, BAR_NORMAL, NOTHING },
			{ 0.8f, 980, BAR_NORMAL, NOTHING },
			{ 0.1f, 1020, BAR_BREAKS, NOTHING },
			{ 0.6f, 1050, BAR_NORMAL, NOTHING },
			//about 10sec - more separeated bars
			{ 0.01f, 1100, BAR_NORMAL, NOTHING },
			{ 0.9f, 1160, BAR_NORMAL, NOTHING },
			{ 0.4f, 1200, BAR_NORMAL, COIN1 },
			{ 0.1f, 1260, BAR_NORMAL, NOTHING },
			{ 0.2f, 1320, BAR_NORMAL, TRAMPOLIN },
			{ 0.4f, 1400, BAR_NORMAL, NOTHING },
			{ 0.3f, 1500, BAR_NORMAL, NOTHING },
			{ 0.01f, 1580, BAR_NORMAL, NOTHING },
			{ 0.8f, 1620, BAR_BREAKS, NOTHING },
			{ 0.4f, 1730, BAR_NORMAL, NOTHING },
			{ 0.6f, 1820, BAR_NORMAL, NOTHING },
			{ 0.4f, 1950, BAR_NORMAL, TRAMPOLIN },
			{ 0.8f, 2050, BAR_NORMAL, COIN5 },
			//about 20sec playtime
			{ 0.8f, 2180, BAR_NORMAL, NOTHING },
			{ 0.001f, 2280, BAR_NORMAL, NOTHING },
			{ 0.6f, 2300, BAR_NORMAL, NOTHING },
			{ 0.9f, 2400, BAR_NORMAL, COIN5 },
			{ 0.5f, 2520, BAR_NORMAL, NOTHING },
			{ 0.1f, 2610, BAR_NORMAL, NOTHING },
			{ 0.6f, 2780, BAR_NORMAL, NOTHING },
			{ 0.5f, 2900, BAR_NORMAL, NOTHING },
			{ 0.001f, 2940, BAR_NORMAL, NOTHING },
			{ 0.6f, 3030, BAR_NORMAL, COIN5 },
			//about 30sec. -add bombs
			{ 0.1f, 3110, BAR_NORMAL, NOTHING },
			{ 0.001f, 3210, BAR_NORMAL, NOTHING },
			{ 0.8f, 3320, BAR_NORMAL, COIN5 },
			{ 0.5f, 3430, BAR_NORMAL, BOMB },
			{ 0.9f, 3440, BAR_NORMAL, NOTHING },
			{ 0.4f, 3550, BAR_NORMAL, NOTHING },
			{ 0.6f, 3660, BAR_NORMAL, NOTHING },
			{ 0.4f, 3680, BAR_BREAKS, COIN5 },
			{ 0.5f, 3790, BAR_NORMAL, ROCKET1 },
			{ 0.1f, 3900, BAR_NORMAL, NOTHING },
			{ 0.2f, 4000, BAR_NORMAL, COIN9 },
			//about 40sec - add separation
			{ 0.1f, 4200, BAR_NORMAL, NOTHING },
			{ 0.2f, 4400, BAR_NORMAL, NOTHING },
			{ 0.3f, 4600, BAR_NORMAL, NOTHING },
			{ 0.4f, 4800, BAR_NORMAL, COIN9 },
			{ 0.5f, 5000, BAR_NORMAL, NOTHING },
			//about 50sec
			{ 0.6f, 5100, BAR_NORMAL, NOTHING },
			{ 0.7f, 5200, BAR_NORMAL, NOTHING },
			{ 0.8f, 5300, BAR_NORMAL, NOTHING },
			{ 0.9f, 5350, BAR_NORMAL, COIN9 },
			{ 0.1f, 5400, BAR_NORMAL, COIN9 },
			{ 0.3f, 5500, BAR_NORMAL, BALLOON },
			{ 0.8f, 5560, BAR_NORMAL, NOTHING },
			{ 0.1f, 5700, BAR_NORMAL, BOMB },
			{ 0.5f, 5710, BAR_NORMAL, NOTHING },
			{ 0.6f, 5900, BAR_NORMAL, NOTHING },
			{ 0.01f, 6000, BAR_NORMAL, COIN9 },
			//about 60sec -introduce moving bar
			{ 0.5f, 6150, BAR_NORMAL, NOTHING },
			{ 0.1f, 6200, BAR_NORMAL, NOTHING },
			{ 0.6f, 6250, BAR_BREAKS, COIN9 },
			{ 0.6f, 6300, BAR_MOVES, NOTHING },
			{ 0.6f, 6400, BAR_NORMAL, NOTHING },
			{ 0.6f, 6500, BAR_MOVES, NOTHING },
			{ 0.6f, 6600, BAR_NORMAL, NOTHING },
			{ 0.7f, 6700, BAR_NORMAL, NOTHING },
			{ 0.6f, 6800, BAR_MOVES, NOTHING },
			{ 0.6f, 6900, BAR_NORMAL, COIN9 },
			{ 0.9f, 7000, BAR_NORMAL, NOTHING },
			{ 0.1f, 7010, BAR_NORMAL, BOMB },
			//about 70sec
			{ 0.5f, 7200, BAR_NORMAL, NOTHING },
			{ 0.7f, 7400, BAR_NORMAL, COIN9 },
			{ 0.01f, 7400, BAR_NORMAL, NOTHING },
			{ 0.9f, 7600, BAR_NORMAL, COIN9 },
			{ 0.5f, 7700, BAR_BREAKS, COIN9 },
			{ 0.01f, 7800, BAR_NORMAL, NOTHING },
			{ 0.9f, 8000, BAR_NORMAL, COIN9 },
			{ 0.5f, 8100, BAR_NORMAL, BOMB },
			{ 0.01f, 8200, BAR_NORMAL, NOTHING },
			{ 0.9f, 8400, BAR_NORMAL, COIN9 },
			{ 0.01f, 8600, BAR_NORMAL, NOTHING },
			{ 0.9f, 8800, BAR_NORMAL, COIN9 },
			{ 0.01f, 9000, BAR_NORMAL, NOTHING },
			//about 90sec
			{ 0.5f, 9100, BAR_NORMAL, NOTHING },
			{ 0.5f, 9300, BAR_MOVES, NOTHING },
			{ 0.5f, 9500, BAR_NORMAL, NOTHING },
			{ 0.5f, 9700, BAR_MOVES, NOTHING },
			{ 0.5f, 9900, BAR_NORMAL, COIN9 },
			{ 0.01f, 10000, BAR_NORMAL, COIN9 },
			{ 0.9f, 10080, BAR_BREAKS, COIN9 },
			//about 100sec
			{ 0.4f, 10150, BAR_NORMAL, TRAMPOLIN },
			{ 0.6f, 10230, BAR_NORMAL, ROCKET2 },
			{ 0.8f, 10300, BAR_NORMAL, NOTHING },
			{ 0.9f, 10400, BAR_NORMAL, NOTHING },
			{ 0.7f, 10600, BAR_NORMAL, COIN9 },
			{ 0.6f, 10800, BAR_NORMAL, NOTHING },
			{ 0.6f, 10900, BAR_BREAKS, BOMB },
			{ 0.8f, 11000, BAR_NORMAL, COIN9 },
			{ 0.8f, 11100, BAR_MOVES, NOTHING },
			{ 0.1f, 11300, BAR_NORMAL, NOTHING },
			{ 0.2f, 11500, BAR_MOVES, NOTHING },
			{ 0.5f, 11700, BAR_NORMAL, COIN9 },
			{ 0.5f, 11900, BAR_MOVES, NOTHING },
			{ 0.1f, 12100, BAR_MOVES, NOTHING },
			{ 0.2f, 12200, BAR_NORMAL, BOMB },
			{ 0.8f, 12250, BAR_NORMAL, BOMB },
			{ 0.5f, 12300, BAR_NORMAL, BALLOON },
			{ 0.3f, 12300, BAR_NORMAL, NOTHING },
			//about 120sec
			{ 0.4f, 12500, BAR_NORMAL, COIN9 },
			{ 0.2f, 12700, BAR_MOVES, NOTHING },
			{ 0.2f, 12900, BAR_NORMAL, BOMB },
			{ 0.8f, 13100, BAR_NORMAL, BOMB },
			{ 0.2f, 13200, BAR_MOVES, NOTHING },
			{ 0.5f, 13300, BAR_NORMAL, BALLOON },
			{ 0.2f, 13400, BAR_NORMAL, BOMB },
			{ 0.8f, 13600, BAR_NORMAL, BOMB },
			{ 0.2f, 13800, BAR_MOVES, NOTHING },
			{ 0.2f, 14000, BAR_NORMAL, BOMB },
			{ 0.8f, 14200, BAR_NORMAL, BOMB },
			{ 0.2f, 14400, BAR_MOVES, NOTHING },
			{ 0.5f, 14600, BAR_MOVES, NOTHING },
			{ 0.8f, 14700, BAR_BREAKS, COIN5 },
			{ 0.4f, 14800, BAR_MOVES, NOTHING },
			{ 0.2f, 14900, BAR_BREAKS, COIN5 },
			{ 0.3f, 15000, BAR_MOVES, NOTHING },
			//about 150sec
			{ 0.6f, 15100, BAR_NORMAL, TRAMPOLIN },
			{ 0.4f, 15500, BAR_BREAKS, COIN5 },
			{ 0.1f, 15600, BAR_BREAKS, COIN9 },
			{ 0.6f, 15850, BAR_NORMAL, TRAMPOLIN },
			{ 0.8f, 15900, BAR_BREAKS, COIN9 },
			{ 0.6f, 16600, BAR_NORMAL, TRAMPOLIN },
			{ 0.4f, 16800, BAR_BREAKS, BOMB },
			{ 0.3f, 17100, BAR_MOVES, NOTHING },
			{ 0.8f, 17300, BAR_NORMAL, NOTHING },
			{ 0.6f, 17450, BAR_NORMAL, BOMB },
			{ 0.2f, 17500, BAR_NORMAL, ROCKET1 },
			//about 170sec
			{ 0.8f, 18800, BAR_NORMAL, NOTHING },
			{ 0.2f, 19000, BAR_MOVES, NOTHING },
			{ 0.1f, 19100, BAR_BREAKS, BOMB },
			{ 0.2f, 19150, BAR_BREAKS, BOMB },
			{ 0.3f, 19200, BAR_MOVES, NOTHING },
			{ 0.1f, 19250, BAR_BREAKS, BOMB },
			{ 0.3f, 19300, BAR_BREAKS, BOMB },
			{ 0.2f, 19400, BAR_MOVES, NOTHING },
			{ 0.1f, 19450, BAR_BREAKS, BOMB },
			{ 0.7f, 19500, BAR_BREAKS, BOMB },
			{ 0.8f, 19600, BAR_MOVES, NOTHING },
			//about 190sec
			{ 0.1f, 19650, BAR_BREAKS, BOMB },
			{ 0.6f, 19700, BAR_BREAKS, BOMB },
			{ 0.1f, 19750, BAR_BREAKS, BOMB },
			{ 0.7f, 19750, BAR_BREAKS, BOMB },
			{ 0.8f, 19800, BAR_MOVES, NOTHING },
			{ 0.5f, 19900, BAR_NORMAL, ROCKET2 },

	};

	//Colors
	final int BLACK = Color.argb(255, 51, 51, 51);
	final int RED = Color.argb(255, 222, 82, 46);
	final int WHITE = Color.argb(255, 255, 255, 255);
	final int YELLOW = Color.argb(255, 251, 209, 52);

	//ad
	private InterstitialAd interstitial;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		//setDebugMode(true);
		initialiseAccelerometer();

		//highscores
		highscoreManager = new HighScoreManager(this, savedInstanceState, layout);

		// Create the interstitial
		interstitial = new InterstitialAd(this, getResources().getString(R.string.InterstitialAd_unit_id));

	}

	public void openAd() {
		runOnUiThread(new Runnable() {
			public void run() {
				// Create ad request
				AdRequest adRequest = new AdRequest();

				// Begin loading your interstitial
				interstitial.loadAd(adRequest);

				// Set Ad Listener to use the callbacks below
				interstitial.setAdListener(new AdListener() {

					@Override
					public void onReceiveAd(Ad arg0) {
						if (interstitial.isReady()) {
							interstitial.show();
						}
					}

					@Override
					public void onPresentScreen(Ad arg0) {
					}

					@Override
					public void onLeaveApplication(Ad arg0) {
					}

					@Override
					public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
					}

					@Override
					public void onDismissScreen(Ad arg0) {
					}
				});
			}
		});
	}

	@Override
	public void Start() {
		super.Start();
		//fonts
		Typeface SCRIPTBL = Typeface.createFromAsset(getAssets(), "SCRIPTBL.TTF");

		//set paints
		//title
		Title_Paint.setTextSize(dpToPx(70));
		Title_Paint.setAntiAlias(true);
		Title_Paint.setColor(RED);
		Title_Paint.setTypeface(SCRIPTBL);

		//subtitle
		SubTitle_Paint.setTextSize(dpToPx(20));
		SubTitle_Paint.setAntiAlias(true);
		SubTitle_Paint.setColor(BLACK);
		SubTitle_Paint.setTypeface(Typeface.DEFAULT_BOLD);

		//score Paint
		Score_Paint.setTextSize(dpToPx(50));
		Score_Paint.setAntiAlias(true);
		Score_Paint.setColor(BLACK);
		Score_Paint.setTypeface(SCRIPTBL);

		//Instruction Paint
		Instruction_Paint.setTextSize(dpToPx(50));
		Instruction_Paint.setAntiAlias(true);
		Instruction_Paint.setColor(BLACK);
		Instruction_Paint.setTypeface(SCRIPTBL);

		Black_shader.setColor(BLACK);
		White_shader.setColor(WHITE);
		Yellow_shader.setColor(YELLOW);

		//get menu ready
		//play button
		btn_Play = new Button(getResources().getString(R.string.Play), 50, Typeface.DEFAULT_BOLD, BLACK, ScreenWidth() / 2, ScreenHeight() * 0.45f, this, false);
		btn_Play.x = ScreenWidth() / 2 - btn_Play.getWidth() / 2;

		//options button
		btn_Options = new Button(new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.options), ScreenWidth() * 0.25f), ScreenWidth() * 0.1f, ScreenHeight() * 0.63f, this, false);

		//highscores button
		btn_Highscores = new Button(new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.highscores), ScreenWidth() * 0.25f), ScreenWidth() * 0.64f, ScreenHeight() * 0.63f, this, false);

		//exit button
		btn_Exit = new Button(new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.exit), ScreenWidth() * 0.15f), 0, 0, this, false);
		btn_Exit.x = ScreenWidth() - btn_Exit.getWidth() * 1.2f;
		btn_Exit.y = ScreenHeight() - btn_Exit.getHeight() * 1.2f;

		//home button
		btn_Home = new Button(new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.home), ScreenWidth() * 0.15f), 0, 0, this, false);
		btn_Home.x = ScreenWidth() - btn_Home.getWidth() * 1.2f;
		btn_Home.y = ScreenHeight() - btn_Home.getHeight() * 1.2f;

		//replay button
		btn_Replay = new Button(new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.replay), ScreenWidth() * 0.13f), 0, 0, this, false);
		btn_Replay.x = btn_Replay.getWidth() * 0.2f;
		btn_Replay.y = ScreenHeight() - btn_Replay.getHeight() * 1.2f;

		//share on facebook
		btn_facebook = new Button(new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.facebook), ScreenWidth() * 0.3f), 0, ScreenHeight() * 0.05f, this, false);

		//sound buttons
		music_on = new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.music_on), ScreenWidth() * 0.1f);
		music_off = new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.music_off), ScreenWidth() * 0.1f);
		sound_off = new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.sound_off), ScreenWidth() * 0.1f);
		sound_on = new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.sound_on), ScreenWidth() * 0.1f);
		//music mute
		btn_music_mute = new Button(music_on, 0, 0, this, false);
		btn_music_mute.x = ScreenWidth() - btn_music_mute.getWidth() * 1.2f;
		btn_music_mute.y = btn_music_mute.getHeight() * 0.06f;
		//sound mute
		btn_sound_mute = new Button(sound_on, ScreenWidth() - btn_music_mute.getWidth() * 2.5f, btn_music_mute.getHeight() * 0.15f, this, false);

		//pause button
		btn_pause = new Button(new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.pause), ScreenWidth() * 0.08f), ScreenWidth() - btn_music_mute.getWidth() * 4f, btn_music_mute.getHeight() * 0.17f, this, false);

		//set world origin
		setOrigin(BOTTOM_LEFT);

		//render background
		//background = renderBackground();

		//initialise character
		character = new Character(0, 0, this);

		//TODO: You may wish to change the bar sizes from here. Just change the 0.1f.
		//initialise bars
		bar_sprite[BAR_NORMAL] = new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.bar1), ScreenHeight() * 0.1f);
		bar_sprite[BAR_BREAKS] = new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.bar3), ScreenHeight() * 0.1f);
		bar_sprite[BAR_MOVES] = new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.bar2), ScreenHeight() * 0.1f);

		//TODO: You may wish to change the special items sizes from here. Just change the 0.09f.
		//initialise special items
		specialItem_sprite[COIN1] = new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.coin1), ScreenWidth() * 0.09f);
		specialItem_sprite[COIN5] = new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.coin2), ScreenWidth() * 0.09f);
		specialItem_sprite[COIN9] = new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.coin3), ScreenWidth() * 0.09f);
		specialItem_sprite[BOMB] = new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.bomb), ScreenWidth() * 0.09f);
		specialItem_sprite[TRAMPOLIN] = new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.trampolin), ScreenWidth() * 0.09f);
		specialItem_sprite[ROCKET1] = new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.rocket1), ScreenWidth() * 0.09f);
		specialItem_sprite[ROCKET2] = new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.rocket2), ScreenWidth() * 0.09f);
		specialItem_sprite[BALLOON] = new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.balloon), ScreenWidth() * 0.09f);

		//initialise clouds
		cloud_sprite = new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.cloud1), ScreenWidth() * 0.45f);
		cloud_sprite2 = new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.cloud1), ScreenWidth() * 0.7f);

		//initialise score image
		score_cup = new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.score), ScreenWidth() * 0.3f);

		//initialise sound fx
		activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
		sound_coin = sp.load(activity, R.raw.coin, 1);

		//initialise music
		music = MediaPlayer.create(activity, R.raw.music);
		sound_coin = sp.load(activity, R.raw.coin, 1);
		sound_fall = sp.load(activity, R.raw.fall, 1);
		sound_gameover = sp.load(activity, R.raw.gameover, 1);
		sound_jump = sp.load(activity, R.raw.jump, 1);
		sound_trampolin = sp.load(activity, R.raw.trampolin, 1);
		sound_beep = sp.load(activity, R.raw.beep, 1);

	}

	@Override
	synchronized public void Step() {
		super.Step();
		if (state == MENU) {

		} else if (state == GAMEPLAY) {

			//things to pause
			if (!notstarted && !pause) {
				//character movement
				character.Update();
				character.x += getAccelerometer().x * dpToPx(accelerometer_movement_speed) * 0.1f;

				if (character.x < 0)
					character.x = ScreenWidth();
				if (character.x > ScreenWidth())
					character.x = 0;

				//move camera and add score
				if (ScreenY(character.y) < ScreenHeight() / 2) {
					//move cloud with camera
					for (int i = 0; i < clouds.size(); i++)
						clouds.get(i).y += ((ScreenHeight() / 2) - ScreenY(character.y)) * 0.6 * (i + 1);

					cameraY += (ScreenHeight() / 2) - ScreenY(character.y);
					score++;

				}
				if (ScreenY(character.y) > ScreenHeight() * 0.9) {
					//move cloud with camera
					for (int i = 0; i < clouds.size(); i++)
						clouds.get(i).y -= (ScreenY(character.y) - (ScreenHeight() * 0.9)) * 0.6 * (i + 1);

					cameraY -= ScreenY(character.y) - (ScreenHeight() * 0.9);
					gameover_distance++;
				} else {
					gameover_distance = 0;
				}
			}
			//check for game over
			//System.out.println(gameover_distance);
			if (gameover_distance > dpToPx(gameover_delay))
				GameOver();

			//character to special items collisions
			for (int i = specialItems.size() - 1; i >= 0; i--) {
				if (character.CollidedWith(specialItems.get(i))) {

					//special items features
					if (specialItems.get(i).sprite.equals(specialItem_sprite[COIN1])) {
						score += 1;
						specialItems.remove(i);
						if (sound_coin != 0 && !sound_muted)
							sp.play(sound_coin, 1, 1, 0, 0, 1);
					} else if (specialItems.get(i).sprite.equals(specialItem_sprite[COIN5])) {
						score += 5;
						specialItems.remove(i);
						if (sound_coin != 0 && !sound_muted)
							sp.play(sound_coin, 1, 1, 0, 0, 1);
					} else if (specialItems.get(i).sprite.equals(specialItem_sprite[COIN9])) {
						score += 9;
						specialItems.remove(i);
						if (sound_coin != 0 && !sound_muted)
							sp.play(sound_coin, 1, 1, 0, 0, 1);
					} else if (specialItems.get(i).sprite.equals(specialItem_sprite[BOMB])) {
						if (character.power != Character.BALLOON)
							GameOver();
						specialItems.remove(i);
					} else if (specialItems.get(i).sprite.equals(specialItem_sprite[TRAMPOLIN])) {
						if ((character.y > specialItems.get(i).y) && (character.speedy < 0)) {
							character.speedy = ScreenHeight() * trampolin_speed;
							if (sound_trampolin != 0 && !sound_muted)
								sp.play(sound_trampolin, 1, 1, 0, 0, 1);
						}
					} else if (specialItems.get(i).sprite.equals(specialItem_sprite[ROCKET1])) {
						character.setPower(Character.ROCKET1);
						specialItems.remove(i);
					} else if (specialItems.get(i).sprite.equals(specialItem_sprite[ROCKET2])) {
						character.setPower(Character.ROCKET2);
						specialItems.remove(i);
					} else if (specialItems.get(i).sprite.equals(specialItem_sprite[BALLOON])) {
						character.setPower(Character.BALLOON);
						specialItems.remove(i);
					}
				}

			}

			//character to bars collisions and bar movement
			for (int i = bars.size() - 1; i >= 0; i--) {
				//move the Moving bar
				if (bars.get(i).sprite.equals(bar_sprite[BAR_MOVES])) {
					bars.get(i).Update();
					if (bars.get(i).x > ScreenWidth() - bars.get(i).getWidth())
						bars.get(i).speedx = -bars.get(i).speedx;
					else if (bars.get(i).x <= 0)
						bars.get(i).speedx = -bars.get(i).speedx;
				}

				if (character.CollidedWith(bars.get(i))) {
					if ((character.y > bars.get(i).y + (character.getHeight() / 2)) && (character.speedy < 0)) {
						//Normal bar
						if (bars.get(i).sprite.equals(bar_sprite[BAR_NORMAL])) {
							character.speedy = ScreenHeight() * bounce_speed;
							if (sound_jump != 0 && !sound_muted)
								sp.play(sound_jump, 1, 1, 0, 0, 1);
						}
						//Moving bar
						if (bars.get(i).sprite.equals(bar_sprite[BAR_MOVES])) {
							character.speedy = ScreenHeight() * bounce_speed;
							if (sound_jump != 0 && !sound_muted)
								sp.play(sound_jump, 1, 1, 0, 0, 1);
						}
						//the bar that breaks
						if (bars.get(i).sprite.equals(bar_sprite[BAR_BREAKS])) {
							bars.remove(i);
							if (sound_fall != 0 && !sound_muted)
								sp.play(sound_fall, 1, 1, 0, 0, 1);
						}

					}
				}

			}

			//create bars
			while (bar_positions.length > next_bar_to_create) {
				float x = ScreenWidth() * bar_positions[next_bar_to_create][X];
				float y = (int) ScreenHeight() * bar_positions[next_bar_to_create][Y] * 0.002f;
				if (ScreenY(y) > 0) {

					int special_type = (int) (bar_positions[next_bar_to_create][SPECIAL_TYPE]);
					int bar_type = (int) bar_positions[next_bar_to_create][TYPE];
					Instance temp_bar = new Instance(bar_sprite[bar_type], x, y, this, true);
					//move bar if bar moves
					if (temp_bar.sprite.equals(bar_sprite[BAR_MOVES])) {
						temp_bar.speedx = dpToPx(bar_moving_speed);
					}
					bars.add(temp_bar);

					//create special items if available
					if (special_type != -1)
						specialItems.add(new Instance(specialItem_sprite[special_type], x + (bar_sprite[bar_type].getWidth() / 2) - (specialItem_sprite[special_type].getWidth() / 2), (float) (y + specialItem_sprite[special_type].getHeight() - (bar_sprite[bar_type].getHeight() * 0.2)), this, true));
					next_bar_to_create++;
				} else {
					break;
				}
			}

			//destroy bars out ouf screen
			for (int i = bars.size() - 1; i >= 0; i--) {
				if (ScreenY(bars.get(i).y) > ScreenHeight() - bar_sprite[0].getHeight()) {
					bars.remove(i);
				}
			}

			//destroy special items out ouf screen
			for (int i = specialItems.size() - 1; i >= 0; i--) {
				if (specialItems.size() > i) {
					if (ScreenY(specialItems.get(i).y) > ScreenHeight() - bar_sprite[0].getHeight()) {
						specialItems.remove(i);
					}
				}
			}

			//move cloud
			for (int i = clouds.size() - 1; i >= 0; i--) {
				if (clouds.size() > i) {
					if (clouds.get(i).y > ScreenHeight()) {
						clouds.remove(i);
						createCloud();
					}
				}
			}

		}

	}

	@Override
	public synchronized void onAccelerometer(PointF point) {

	}

	@Override
	public synchronized void BackPressed() {
		if (state == GAMEPLAY) {
			StopMusic();
			state = MENU;
		} else if (state == OPTIONS) {
			state = MENU;
		} else if (state == HIGHSCORES) {
			state = MENU;
		} else if (state == MENU) {
			StopMusic();
			Exit();

		} else if (state == GAMEOVER) {
			highscoreManager.newScore(score, getResources().getString(R.string.Default_topscore_name));
			state = MENU;
		}
	}

	@Override
	public synchronized void onTouch(float TouchX, float TouchY, MotionEvent event) {
		//handle constant events like sound buttons
		if (event.getAction() == MotionEvent.ACTION_DOWN) {

			if (btn_sound_mute.isTouched(event)) {
				btn_sound_mute.Highlight(RED);
			}
			if (btn_music_mute.isTouched(event)) {
				btn_music_mute.Highlight(RED);
			}
		}
		if (event.getAction() == MotionEvent.ACTION_UP) {
			//refresh all
			btn_music_mute.LowLight();
			btn_sound_mute.LowLight();

			if (btn_sound_mute.isTouched(event)) {
				toggleSoundFx();
			}
			if (btn_music_mute.isTouched(event)) {
				toggleMusic();
			}
		}

		if (state == MENU) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				if (btn_Play.isTouched(event)) {
					btn_Play.Highlight(RED);
				}
				if (btn_Options.isTouched(event)) {
					btn_Options.Highlight(RED);
				}
				if (btn_Highscores.isTouched(event)) {
					btn_Highscores.Highlight(RED);
				}
				if (btn_Exit.isTouched(event)) {
					btn_Exit.Highlight(RED);
				}
			}
			if (event.getAction() == MotionEvent.ACTION_UP) {
				//refresh all
				btn_Play.LowLight();
				btn_Options.LowLight();
				btn_Highscores.LowLight();
				btn_Exit.LowLight();

				if (btn_Play.isTouched(event)) {
					if (sound_trampolin != 0 && !sound_muted)
						sp.play(sound_trampolin, 1, 1, 0, 0, 1);
					StartGame();
				}
				if (btn_Options.isTouched(event)) {
					if (sound_beep != 0 && !sound_muted)
						sp.play(sound_beep, 1, 1, 0, 0, 1);
					state = OPTIONS;
				}
				if (btn_Highscores.isTouched(event)) {
					if (sound_beep != 0 && !sound_muted)
						sp.play(sound_beep, 1, 1, 0, 0, 1);
					OpenHighscores();
				}
				if (btn_Exit.isTouched(event)) {
					Exit();
				}
			}
			if (event.getAction() == MotionEvent.ACTION_MOVE) {

			}
		} else if (state == OPTIONS) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				if (btn_Home.isTouched(event)) {
					btn_Home.Highlight(RED);
				}
			}
			if (event.getAction() == MotionEvent.ACTION_UP) {
				//refresh all
				btn_Home.LowLight();

				if (btn_Home.isTouched(event)) {
					if (sound_beep != 0 && !sound_muted)
						sp.play(sound_beep, 1, 1, 0, 0, 1);
					state = MENU;
				} else {
					if (sound_beep != 0 && !sound_muted)
						sp.play(sound_beep, 1, 1, 0, 0, 1);
					CalibrateAccelerometer();
				}
			}
			if (event.getAction() == MotionEvent.ACTION_MOVE) {

			}
		} else if (state == HIGHSCORES) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				if (btn_Home.isTouched(event)) {
					btn_Home.Highlight(RED);
				}
			}
			if (event.getAction() == MotionEvent.ACTION_UP) {
				//refresh all
				btn_Home.LowLight();

				if (btn_Home.isTouched(event)) {
					if (sound_beep != 0 && !sound_muted)
						sp.play(sound_beep, 1, 1, 0, 0, 1);
					state = MENU;
				}
			}
			if (event.getAction() == MotionEvent.ACTION_MOVE) {

			}
		} else if (state == GAMEOVER) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				if (btn_Home.isTouched(event)) {
					btn_Home.Highlight(RED);
				}
				if (btn_facebook.isTouched(event)) {
					btn_facebook.Highlight(WHITE);
				}
				if (btn_facebook.isTouched(event)) {
					btn_facebook.Highlight(RED);
				}
			}
			if (event.getAction() == MotionEvent.ACTION_UP) {
				//refresh all
				btn_Home.LowLight();
				btn_facebook.LowLight();
				btn_Replay.LowLight();
				if (btn_Home.isTouched(event)) {
					//show_enter_highscore();
					highscoreManager.newScore(score, getResources().getString(R.string.Default_topscore_name));
					state = MENU;
					if (sound_beep != 0 && !sound_muted)
						sp.play(sound_beep, 1, 1, 0, 0, 1);
				}
				if (btn_facebook.isTouched(event)) {
					//share with facebook
					highscoreManager.postToFacebook("" + score, getResources().getString(R.string.facebook_share_link), getResources().getString(R.string.facebook_share_description), getResources().getString(R.string.Error_no_facebook_app_installed));
					if (sound_beep != 0 && !sound_muted)
						sp.play(sound_beep, 1, 1, 0, 0, 1);
				}
				if (btn_Replay.isTouched(event)) {
					highscoreManager.newScore(score, getResources().getString(R.string.Default_topscore_name));
					StartGame();
					if (sound_beep != 0 && !sound_muted)
						sp.play(sound_beep, 1, 1, 0, 0, 1);

				}
			}
		} else if (state == GAMEPLAY) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				if (btn_pause.isTouched(event)) {
					btn_pause.Highlight(RED);
				}

			}
			if (event.getAction() == MotionEvent.ACTION_UP) {
				//start game
				if (notstarted) {
					notstarted = false;
				}
				btn_pause.LowLight();
				if (btn_pause.isTouched(event)) {
					togglePause();
					if (sound_beep != 0 && !sound_muted)
						sp.play(sound_beep, 1, 1, 0, 0, 1);
				}
			}

		}
	}

	//..................................................Game Functions..................................................................................................................................

	public void StartGame() {
		//refresh score
		score = 0;
		//refresh character
		character.x = ((int) ScreenWidth() * bar_positions[0][X]);
		character.y = ((int) ScreenHeight() * bar_positions[0][Y] * 0.002f) + character.getHeight();
		character.speedy = 0;
		character.speedx = 0;
		character.accelerationy = character.NORMAL_ACCELERATIONY;
		character.setPower(Character.NORMAL);
		//refresh bars
		bars.clear();
		next_bar_to_create = 0;
		//refresh camera
		cameraY = 0;

		//clouds
		clouds.clear();
		createCloud();
		createCloud();

		//refresh game over
		gameover_distance = 0;

		//not started
		notstarted = true;
		state = GAMEPLAY;
		PlayMusic();

		//pause off
		pause = false;
	}

	public synchronized void GameOver() {
		openAd();
		if (sound_gameover != 0 && !sound_muted)
			sp.play(sound_gameover, 1, 1, 0, 0, 1);
		StopMusic();
		state = GAMEOVER;
		highscoreManager.AddName_Editview(((int) (ScreenWidth() / 1.5f) < dpToPx(250)) ? ((int) (ScreenWidth() / 1.5f)) : (dpToPx(250)), getResources().getString(R.string.Highscore_hint), (int) (ScreenHeight() * 0.68f));
	}

	public void OpenHighscores() {
		state = HIGHSCORES;
		highscore_list = highscoreManager.load_localscores();
	}

	public void createCloud() {
		if (Math.random() > 0.5)
			clouds.add(new Instance(cloud_sprite, (float) ((Math.random() * ScreenWidth()) - (cloud_sprite.getWidth() / 2)), (float) (-dpToPx(200) - (Math.random() * dpToPx(300))), this, false));
		else
			clouds.add(new Instance(cloud_sprite2, (float) ((Math.random() * ScreenWidth()) - (cloud_sprite2.getWidth() / 2)), (float) (-dpToPx(200) - (Math.random() * dpToPx(300))), this, false));
	}

	public void PlayMusic() {
		if (!music_muted && state == GAMEPLAY) {
			music = MediaPlayer.create(activity, R.raw.music);
			music.start();
			music.setLooping(true);
		}
	}

	public void StopMusic() {
		music.stop();
	}

	public void toggleMusic() {
		if (music_muted) {

			music_muted = false;
			btn_music_mute.sprite = music_on;
			if (!pause) {
				PlayMusic();
			}
		} else {
			music_muted = true;
			btn_music_mute.sprite = music_off;
			StopMusic();
		}
	}

	public void toggleSoundFx() {
		if (sound_muted) {
			sound_muted = false;
			btn_sound_mute.sprite = sound_on;
		} else {
			sound_muted = true;
			btn_sound_mute.sprite = sound_off;
		}
	}

	public void pause() {
		if (state == GAMEPLAY && !notstarted) {
			pause = true;
			StopMusic();
		}
	}

	public void togglePause() {
		if (state == GAMEPLAY) {
			if (pause) {
				pause = false;
				if (!music_muted)
					PlayMusic();
			} else {
				pause();
			}
		}
	}

	//...................................................Rendering of screen............................................................................................................................
	@Override
	public void Draw(Canvas canvas) {
		//draw background
		//canvas.drawBitmap(background, 0, 0, null);
		renderBackground(canvas);

		if (state == MENU) {
			//draw clouds
			cloud_sprite2.draw(canvas, dpToPx(10), dpToPx(50));
			cloud_sprite.draw(canvas, ScreenWidth() - (cloud_sprite.getWidth() * 0.75f), ScreenHeight() - (cloud_sprite.getHeight() * 0.8f));

			canvas.drawText(getResources().getString(R.string.app_name), (ScreenWidth() / 2) - (Title_Paint.measureText(getResources().getString(R.string.app_name)) / 2), (float) (ScreenHeight() * 0.25), Title_Paint);
			btn_Play.draw(canvas);
			btn_Options.draw(canvas);
			btn_Highscores.draw(canvas);
			btn_Exit.draw(canvas);

		} else if (state == GAMEPLAY) {
			//draw clouds
			for (int i = 0; i < clouds.size(); i++) {
				clouds.get(i).draw(canvas);
			}

			//draw bars
			for (int i = 0; i < bars.size(); i++) {
				bars.get(i).draw(canvas);
			}
			//draw specials
			for (int i = 0; i < specialItems.size(); i++) {
				specialItems.get(i).draw(canvas);
			}

			//draw character
			character.draw(canvas);

			//draw score
			canvas.drawText("" + score, ScreenWidth() * 0.04f, (float) (ScreenHeight() * 0.11f), Title_Paint);

			//before game starts
			if (notstarted) {
				canvas.drawText(getResources().getString(R.string.Tap_to_start), (ScreenWidth() / 2) - (Instruction_Paint.measureText(getResources().getString(R.string.Tap_to_start)) / 2), (float) (ScreenHeight() * 0.5), Instruction_Paint);
			}
			if (pause) {
				canvas.drawText(getResources().getString(R.string.Paused), (ScreenWidth() / 2) - (Instruction_Paint.measureText(getResources().getString(R.string.Paused)) / 2), (float) (ScreenHeight() * 0.5), Instruction_Paint);
			}

			//pause button
			btn_pause.draw(canvas);

		} else if (state == OPTIONS) {
			//draw clouds
			cloud_sprite2.draw(canvas, dpToPx(10), dpToPx(50));
			cloud_sprite.draw(canvas, ScreenWidth() - (cloud_sprite.getWidth() * 0.75f), ScreenHeight() - (cloud_sprite.getHeight() * 0.8f));

			canvas.drawText(getResources().getString(R.string.calibrate), (ScreenWidth() / 2) - (Title_Paint.measureText(getResources().getString(R.string.calibrate)) / 2), (float) (ScreenHeight() * 0.25), Title_Paint);
			canvas.drawText(getResources().getString(R.string.calibrate_instructions), (ScreenWidth() / 2) - (SubTitle_Paint.measureText(getResources().getString(R.string.calibrate_instructions)) / 2), (float) (ScreenHeight() * 0.30), SubTitle_Paint);

			//calibrate tool
			canvas.drawRect((ScreenWidth() / 2) - (ScreenWidth() / 4), (ScreenHeight() / 2) - (ScreenWidth() / 20), (ScreenWidth() / 2) + (ScreenWidth() / 4), (ScreenHeight() / 2) + (ScreenWidth() / 20), Black_shader);
			canvas.drawCircle((ScreenWidth() / 2) - (getAccelerometer().x * 2), (ScreenHeight() / 2), (ScreenWidth() / 30), Yellow_shader);

			btn_Home.draw(canvas);

		} else if (state == HIGHSCORES) {
			//draw clouds
			cloud_sprite2.draw(canvas, dpToPx(10), dpToPx(50));
			cloud_sprite.draw(canvas, ScreenWidth() - (cloud_sprite.getWidth() * 0.75f), ScreenHeight() - (cloud_sprite.getHeight() * 0.8f));

			canvas.drawText(getResources().getString(R.string.Highscores), (ScreenWidth() / 2) - (Title_Paint.measureText(getResources().getString(R.string.Highscores)) / 2), (float) (ScreenHeight() * 0.25), Title_Paint);

			if (highscore_list != null) {
				//hiscores
				for (int i = 0; i < 10; i++) {
					canvas.drawText(highscore_list[i].hiscorename, (ScreenWidth() / 2) - (ScreenWidth() / 4), (ScreenHeight() * 0.35f) + (i * SubTitle_Paint.getTextSize() * 1.5f), SubTitle_Paint);
					canvas.drawText("" + highscore_list[i].highscore, (ScreenWidth() / 2) + (ScreenWidth() / 6), (ScreenHeight() * 0.35f) + (i * SubTitle_Paint.getTextSize() * 1.5f), SubTitle_Paint);
				}
			}

			btn_Home.draw(canvas);
		} else if (state == GAMEOVER) {
			//draw clouds
			cloud_sprite2.draw(canvas, dpToPx(10), dpToPx(50));
			cloud_sprite.draw(canvas, ScreenWidth() - (cloud_sprite.getWidth() * 0.75f), ScreenHeight() - (cloud_sprite.getHeight() * 0.8f));

			canvas.drawText(getResources().getString(R.string.game_over), (ScreenWidth() / 2) - (Title_Paint.measureText(getResources().getString(R.string.game_over)) / 2), (float) (ScreenHeight() * 0.25), Title_Paint);

			score_cup.draw(canvas, (ScreenWidth() / 2) - (score_cup.getWidth() / 2), (float) (ScreenHeight() * 0.30));

			canvas.drawText("" + score, (ScreenWidth() / 2) - (Score_Paint.measureText("" + score) / 2), (float) (ScreenHeight() * 0.55), Score_Paint);

			canvas.drawText(getResources().getString(R.string.Enter_highscore_comment), (ScreenWidth() / 2) - (SubTitle_Paint.measureText(getResources().getString(R.string.Enter_highscore_comment)) / 2), (float) (ScreenHeight() * 0.65), SubTitle_Paint);

			btn_facebook.draw(canvas);
			btn_Home.draw(canvas);
			btn_Replay.draw(canvas);

		}
		//draw sound buttons
		btn_sound_mute.draw(canvas);
		btn_music_mute.draw(canvas);

		super.Draw(canvas);
	}

	//Rendering of background
	public void renderBackground(Canvas canvas) {
		//Bitmap clouds = Bitmap.createBitmap(ScreenWidth(), ScreenHeight(), Bitmap.Config.ARGB_8888);
		//Canvas canvas = new Canvas(clouds);

		canvas.drawColor(Color.rgb(173, 230, 255));

		background_shader.setARGB(255, 123, 212, 245);
		int radius = DrawBackgroundCloud(canvas, (int) (ScreenHeight() / 2.5), 10);
		canvas.drawRect(0, (float) ((ScreenHeight() / 2.7) + radius * 1.5), ScreenWidth(), ScreenHeight(), background_shader);

		background_shader.setARGB(255, 118, 188, 235);
		radius = DrawBackgroundCloud(canvas, (ScreenHeight() / 2), 7);
		canvas.drawRect(0, (float) ((ScreenHeight() / 2.2) + radius * 1.5), ScreenWidth(), ScreenHeight(), background_shader);

		background_shader.setARGB(255, 102, 164, 204);
		radius = DrawBackgroundCloud(canvas, (int) (ScreenHeight() / 1.5), 4);
		canvas.drawRect(0, (float) ((ScreenHeight() / 1.7) + radius * 1.5), ScreenWidth(), ScreenHeight(), background_shader);

		//return clouds;
	}

	public int DrawBackgroundCloud(Canvas canvas, int y, int circles) {
		int radius = (int) (ScreenWidth() / (circles * 1.3));
		for (int i = 0; i < circles; i++) {
			canvas.drawCircle((float) (i * radius * 1.5), (float) (y + radius + (Math.sin(i * circles * y) * radius * 0.35f)), radius, background_shader);
		}
		return radius;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		highscoreManager.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onResume() {
		super.onResume();
		highscoreManager.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		highscoreManager.onSaveInstanceState(outState);
	}

	@Override
	public void onPause() {
		super.onPause();
		pause();
		highscoreManager.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		highscoreManager.onDestroy();
	}
}
