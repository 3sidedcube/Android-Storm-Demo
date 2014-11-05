package com.cube.storm.example;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.cube.storm.ContentSettings;
import com.cube.storm.content.model.Manifest;
import com.cube.storm.content.model.Manifest.FileDescriptor;

import java.util.ArrayList;
import java.util.Locale;

/**
 * // TODO: Add class description
 *
 * @author Alan Le Fournis
 * @project Storm
 */
public class SettingsActivity extends PreferenceActivity implements OnPreferenceClickListener
{
	public static final String FILE_MANIFEST = "manifest.json";
	public static final String PREFS_LOCALE = "locale";
	public static final String PREFS_LOCALE_STRING = "locale_string";

	private SharedPreferences prefs;

	private int selectedLocaleOption = -1;

	@Override protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.preferences);
		findPreference("locale").setOnPreferenceClickListener(this);

		prefs = PreferenceManager.getDefaultSharedPreferences(this);
	}

	@Override public boolean onPreferenceClick(Preference preference)
	{
		if (preference.getKey().equals("locale"))
		{
			Manifest manifest = ContentSettings.getInstance().getBundleBuilder().buildManifest(Uri.parse("cache://" + FILE_MANIFEST));

			if (manifest != null)
			{
				final ArrayList<Locale> locales = new ArrayList<Locale>(manifest.getLanguages().size());
				final ArrayList<String> options = new ArrayList<String>(manifest.getLanguages().size() + 1);
				options.add("Automatic");

				String currentLocale = prefs.getString(PREFS_LOCALE, "");
				int selectedLocale = 0;

				int index = 0;
				for (FileDescriptor language : manifest.getLanguages())
				{
					String languageSrc = language.getSrc();
					String[] languageSpec = languageSrc.replace(".json", "").split("_");
					Locale locale = new Locale(languageSpec[1], languageSpec[0]);

					locales.add(locale);
					options.add(locale.getDisplayCountry(locale) + " - " + locale.getDisplayLanguage(locale));

					if (currentLocale.equals(locale.getISO3Country() + "_" + locale.getLanguage()))
					{
						selectedLocale = index + 1;
					}

					index++;
				}

				new AlertDialog.Builder(this)
					.setTitle("Select")
					.setSingleChoiceItems(options.toArray(new String[options.size()]), selectedLocale, new DialogInterface.OnClickListener()
					{
						@Override public void onClick(DialogInterface dialog, int which)
						{
							selectedLocaleOption = which;
						}
					})
					.setPositiveButton("Change", new DialogInterface.OnClickListener()
					{
						@Override public void onClick(DialogInterface dialog, int which)
						{
							SharedPreferences.Editor editor = prefs.edit();

							if (selectedLocaleOption - 1 < 0)
							{
								editor.remove(PREFS_LOCALE);
								editor.remove(PREFS_LOCALE_STRING);
							}
							else
							{
								editor.putString(PREFS_LOCALE, locales.get(selectedLocaleOption - 1).getISO3Country() + "_" + locales.get(selectedLocaleOption - 1).getLanguage());
								editor.putString(PREFS_LOCALE_STRING, options.get(selectedLocaleOption));
							}

							editor.putBoolean("language_card", true);
							editor.apply();

							finish();

							Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(intent);
						}
					})
					.setNegativeButton("Close", null)
					.show();
			}
		}

		return false;
	}
}
