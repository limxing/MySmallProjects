package com.example.limxing.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class MusicService extends Service {
	public String path;
	private MediaPlayer mediaPlayer;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		mediaPlayer = new MediaPlayer();
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		path = intent.getStringExtra("name");
		if (path.equals("0")) {
			pause();

		} else if (path.equals("1")) {
			play();
		} else {
			if (mediaPlayer.isPlaying()) {
//				mediaPlayer.stop();
//				mediaPlayer = new MediaPlayer();
			}
			try {
				mediaPlayer.stop();
				mediaPlayer = new MediaPlayer();
				mediaPlayer.setDataSource(path);
				mediaPlayer.setLooping(false);
				mediaPlayer.prepare();// 准备资源
			} catch (Exception e) {
				e.printStackTrace();
			}
			play();

		}

		return super.onStartCommand(intent, flags, startId);
	}

	public void play() {
		mediaPlayer.start();// 开始播放
	}

	public void pause() {
		if (mediaPlayer.isPlaying()) {
			mediaPlayer.pause();
		}
	}

	@Override
	public void onDestroy() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.seekTo(0);
			mediaPlayer.release();// 释放资源
		}
		super.onDestroy();
	}
}
