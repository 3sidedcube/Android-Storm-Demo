package com.cube.storm.example;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import com.cube.storm.LanguageSettings;
import com.cube.storm.ui.activity.StormActivity;

/**
 * // TODO: Add class description
 *
 * @author Alan Le Fournis
 * @project Storm
 */
public class MainActivity extends StormActivity
{
	@Override public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.main, menu);
		menu.getItem(0).setTitle(LanguageSettings.getInstance().getLanguageManager().getValue(this, "_TITLE_SETTINGS"));

		return super.onCreateOptionsMenu(menu);
	}

	@Override public boolean onOptionsItemSelected(MenuItem item)
	{
		if (item.getItemId() == R.id.settings)
		{
			Intent settings = new Intent(this, SettingsActivity.class);
			startActivity(settings);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
