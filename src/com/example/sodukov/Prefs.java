package com.example.sodukov;

import java.io.File;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class Prefs extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {

	private static final String TAG = "Prefs";
	// Option names and default values
	private static final String OPT_MUSIC = "Play_background_music";
	private static final boolean OPT_MUSIC_DEF = true;
	private static final String OPT_HINTS = "Hints";
	private static final boolean OPT_HINTS_DEF = true;

	private static final String OPT_MUSIC_TYPE = "Music Location Selection";
	private static final String OPT_MUSIC_NAME = "Select Music to play";

	private static final int CHOOSE_FILE = 0xff;
	private static String selectMusicStr;

	private static final String music_ext[] = { "mp3", "wav" };

	private Preference MusicOnPref;
	private ListPreference MusicTypePref;
	private Preference MusicNamePref;
	private Preference HintsOnPref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_prefs);
		addPreferencesFromResource(R.layout.activity_prefs);

		/* Update the default value from database */
		MusicOnPref = findPreference(getResources().getString(
				R.string.music_title));
		MusicTypePref = (ListPreference) findPreference(getResources()
				.getString(R.string.background_music_type_title));
		MusicNamePref = findPreference(getResources().getString(
				R.string.preference_music_select));
		HintsOnPref = findPreference(getResources().getString(
				R.string.hints_title));

		MusicOnPref.setDefaultValue(PreferenceManager
				.getDefaultSharedPreferences(this).getBoolean(OPT_HINTS,
						OPT_HINTS_DEF));

		MusicTypePref.setDefaultValue(PreferenceManager
				.getDefaultSharedPreferences(this).getString(OPT_MUSIC_TYPE,
						"0"));

		selectMusicStr = PreferenceManager.getDefaultSharedPreferences(this)
				.getString(OPT_MUSIC_NAME, null);

		if (checkMusicValid(selectMusicStr, this) == false) {
			selectMusicStr = null;
			MusicTypePref.setValueIndex(0);
		} else {
			MusicNamePref.setDefaultValue(selectMusicStr);
			// MusicTypePref.setValueIndex(1);
		}

		HintsOnPref.setDefaultValue(PreferenceManager
				.getDefaultSharedPreferences(this).getBoolean(OPT_HINTS,
						OPT_HINTS_DEF));

		MusicNamePref = findPreference(getResources().getString(
				R.string.preference_music_select));

		OnPreferenceClickListener onPreferenceClick = new Preference.OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				if (preference == MusicNamePref) {
					Intent i = new Intent();
					i.setAction(i.ACTION_GET_CONTENT);
					i.setType("*/*");
					MusicNamePref.setIntent(i);
					startActivityForResult(i, CHOOSE_FILE);
				}
				return true;
			}
		};

		MusicNamePref.setOnPreferenceClickListener(onPreferenceClick);

		setUpActionBar();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.prefs, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		/*
		 * if (id == R.id.action_settings) { return true; }
		 */
		return super.onOptionsItemSelected(item);
	}

	/** Get the current value of the music option */
	public static boolean getMusic(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getBoolean(OPT_MUSIC, OPT_MUSIC_DEF);
	}

	/** Get the current value of the hints option */
	public static boolean getHints(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getBoolean(OPT_HINTS, OPT_HINTS_DEF);
	}

	public static String getMusicType(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getString(OPT_MUSIC_TYPE, "0");
	}

	public static String getMusicName(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getString(OPT_MUSIC_NAME, null);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setUpActionBar() {
		// Make sure we're running on Honeycomb or higher to use ActionBar APIs
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals(getResources().getString(
				R.string.preference_music_select))) {
			// Preference connectionPref = findPreference(key);
			// Set summary to be the user-description for the selected value
			// connectionPref.setSummary(sharedPreferences.getString(key, ""));
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case CHOOSE_FILE: {
			Uri uri;
			String ext;
			String prefix;
			int i;
			if (data != null) {
				uri = data.getData();
				selectMusicStr = uri.toString();
				selectMusicStr = Uri.decode(selectMusicStr);
				ext = selectMusicStr.substring(
						selectMusicStr.lastIndexOf('.') + 1,
						selectMusicStr.length());
				prefix = selectMusicStr.substring(0, 7);
				for (i = 0; i < music_ext.length; i++) {
					if (ext.compareToIgnoreCase(music_ext[i]) == 0) {
						break;
					}
				}
				if (i == music_ext.length) {
					Toast.makeText(this,
							"invalid file select\n" + selectMusicStr,
							Toast.LENGTH_SHORT).show();
					selectMusicStr = null;
				} else {
					/* delete the prefix */
					if (prefix.compareToIgnoreCase("file://") == 0) {
						selectMusicStr = selectMusicStr.substring(7,
								selectMusicStr.length());
					}
				}

				if (checkMusicValid(selectMusicStr, this) == false) {
					selectMusicStr = null;
					MusicTypePref.setValueIndex(0);
				} else {
					MusicNamePref.setDefaultValue(selectMusicStr);
					MusicTypePref.setValueIndex(1);
				}

				Log.d(TAG, "select music:" + selectMusicStr);
			}
		}
		}

	}

	private static boolean checkMusicValid(String name, Context context) {
		boolean valid;
		if (name == null)
			return false;
		File music_file = new File(name);

		valid = music_file.isFile();
		if (valid == false) {
			Toast.makeText(context, "file didn't exist\n" + name,
					Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(context, "file valid\n" + name, Toast.LENGTH_LONG)
					.show();
		}

		return valid;
	}

	public static boolean dumpinfo(String tag, Context context) {
		Log.d(tag, "Enable:" + getMusic(context));
		Log.d(tag, "Type:" + getMusicType(context));
		Log.d(tag, "Name:" + getMusicName(context));
		Log.d(tag, "Hints:" + getHints(context));
		return true;
	}

	public static boolean updatePreferenceValue(Context context) {
		String str;

		PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
				OPT_HINTS, OPT_HINTS_DEF);

		str = PreferenceManager.getDefaultSharedPreferences(context).getString(
				OPT_MUSIC_NAME, null);

		if (checkMusicValid(str, context) == false) {
			str = null;
			
		}else{
			PreferenceManager.getDefaultSharedPreferences(context).getString(
					OPT_MUSIC_TYPE, "0");
		}

		PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
				OPT_HINTS, OPT_HINTS_DEF);
		return true;
	}
}
