package com.cube.storm.example;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

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

		Manifest manifest = ContentSettings.getInstance().getBundleBuilder().buildManifest(Uri.parse("cache://manifest.json"));
		long lastUpdate = 0;

		if (manifest != null)
		{
			lastUpdate = manifest.getTimestamp();
		}

		Debug.out("CHECKING FOR UPDATES");
//		ContentSettings.getInstance().getUpdateManager().checkForUpdates(lastUpdate);

		Intent start = UiSettings.getInstance().getIntentFactory().geIntentForPageUri(this, Uri.parse(UiSettings.getInstance().getApp().getVector()));

		if (start != null)
		{
			startActivity(start);
		}
	}
}
