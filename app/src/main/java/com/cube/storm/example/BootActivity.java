package com.cube.storm.example;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.cube.storm.ContentSettings;
import com.cube.storm.UiSettings;
import com.cube.storm.content.model.Manifest;
import com.cube.storm.util.lib.debug.Debug;

/**
 * Entry point of the app.
 *
 * @author Callum Taylor
 * @project StormExample
 */
public class BootActivity extends Activity
{
	@Override protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		boolean developerMode = prefs.getBoolean("developer_mode", false);
		Manifest manifest = ContentSettings.getInstance().getBundleBuilder().buildManifest(Uri.parse("cache://manifest.json"));
		long lastUpdate = 0;

		if (manifest != null)
		{
			lastUpdate = manifest.getTimestamp();
		}

		if (developerMode && !ContentSettings.getInstance().getFileManager().fileExists(ContentSettings.getInstance().getStoragePath() + "/manifest.json"))
		{
			Debug.out("DOWNLOADING TEST BUNDLE");
			ContentSettings.getInstance().getUpdateManager().checkForBundle();
		}
		else
		{
			Debug.out("CHECKING FOR UPDATES");
			ContentSettings.getInstance().getUpdateManager().checkForUpdates(lastUpdate);
		}

		Intent start = UiSettings.getInstance().getIntentFactory().geIntentForPageUri(this, Uri.parse(UiSettings.getInstance().getApp().getVector()));

		if (start != null)
		{
			startActivity(start);
			finish();
		}
	}
}
