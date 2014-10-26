package com.example.sodukov;

import java.io.IOException;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

public class Music implements MediaPlayer.OnPreparedListener {
	private static final String TAG = "Music";
	private MediaPlayer mp = null;
	private int currentPosition;

	public Music() {
		currentPosition = 0;
	}

	/** Stop old song and start new one */
	public void play(Context context, int resource) {
		stop(context);

		mp = new MediaPlayer();
		mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mp.setLooping(true);
		try {
			mp.setDataSource(
					context,
					Uri.parse("android.resource://" + context.getPackageName()
							+ "/" + resource));
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mp.setOnPreparedListener(this);
		mp.prepareAsync();
	}

	public void play(Context context, String name) {
		stop(context);

		mp = new MediaPlayer();
		mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mp.setLooping(true);
		try {
			mp.setDataSource(name);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mp.setOnPreparedListener(this);
		mp.prepareAsync();
	}

	/** Stop the music */
	public boolean stop(Context context) {
		if (mp != null) {
			currentPosition = mp.getCurrentPosition();
			// Log.d(TAG, "stop->currentPosition:" + currentPosition);
			mp.stop();
			mp.release();
			mp = null;

			return true;
		}
		return false;
	}

	/** Called when MediaPlayer is ready */
	public void onPrepared(MediaPlayer player) {
		// Log.d(TAG, "resume->currentPosition:" + currentPosition);
		player.seekTo(currentPosition);
		player.start();
	}
}
