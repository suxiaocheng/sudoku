package com.example.sodukov;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

public class Music {
	private static MediaPlayer mp = null;
	private static int currentPosition;

	/** Stop old song and start new one */
	public static void play(Context context, int resource) {
		stop(context);

		// Start music only if not disabled in preferences
		if (Prefs.getMusic(context)) {
			mp = MediaPlayer.create(context, resource);
			mp.setLooping(true);
			mp.start();
		}
	}
	
	public static void play(Context context, String name) {
		stop(context);

		// Start music only if not disabled in preferences
		if (Prefs.getMusic(context)) {
			mp = MediaPlayer.create(context, Uri.parse(name));
			mp.setLooping(true);
			mp.start();
		}
	}

	/** Stop the music */
	public static void stop(Context context) {
		if (mp != null) {
			mp.stop();
			mp.release();
			mp = null;
		}
	}
	
	public static void pause(Context context){
		if(mp != null){
			currentPosition = mp.getCurrentPosition();
		}
	}
}
