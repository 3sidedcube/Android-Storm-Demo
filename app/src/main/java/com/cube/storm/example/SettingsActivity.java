package com.cube.storm.example;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.cube.storm.ContentSettings;
import com.cube.storm.content.lib.Environment;
import com.cube.storm.content.model.Manifest;
import com.cube.storm.content.model.Manifest.FileDescriptor;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.callumtaylor.asynchttp.AsyncHttpClient;
import net.callumtaylor.asynchttp.obj.entity.JsonEntity;
import net.callumtaylor.asynchttp.response.JsonResponseHandler;

import java.io.File;
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

	private boolean inDevelopMode = false;
	private int selectedLocaleOption = -1;

	@Override protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		inDevelopMode = prefs.getBoolean("developer_mode", false);

		addPreferencesFromResource(R.xml.preferences);

		findPreference("developer_mode").setTitle(inDevelopMode ? "Exit developer mode" : "Enter developer mode");
		findPreference("locale").setOnPreferenceClickListener(this);
		findPreference("developer_mode").setOnPreferenceClickListener(this);
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

		else if (preference.getKey().equals("developer_mode"))
		{
			if (inDevelopMode)
			{
				inDevelopMode = false;

				MainApplication.getContentSettings().setAuthorizationToken(null);
				MainApplication.getContentSettings().setContentEnvironment(Environment.LIVE);

				clearCache();

				prefs.edit()
					.remove("developer_token")
					.remove("developer_mode")
					.remove("developer_timeout")
					.apply();

				prefs.edit().remove("developer_mode").apply();
				Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
			else
			{
				final AlertDialog loginDialog = new AlertDialog.Builder(this)
					.setTitle("Developer mode")
					.setView(LayoutInflater.from(this).inflate(R.layout.developer_login_view, null))
					.setPositiveButton("Login", null)
					.setNegativeButton("Cancel", null)
					.create();

				final AsyncHttpClient loginClient = new AsyncHttpClient("http://auth.cubeapis.com/v1.4/");

				final ProgressDialog progress = new ProgressDialog(this);
				progress.setCanceledOnTouchOutside(false);
				progress.setMessage("Logging in...");

				final JsonResponseHandler response = new JsonResponseHandler()
				{
					private String token;
					private long timeout = 0L;

					@Override public void onSend()
					{
						progress.show();
					}

					@Override public void onSuccess()
					{
						JsonElement resp = getContent();
						if (resp != null)
						{
							token = resp.getAsJsonObject().get("token").getAsString();
							timeout = resp.getAsJsonObject().get("expires").getAsJsonObject().get("timeout").getAsInt() * 1000L;
						}
					}

					@Override public void onFinish(boolean failed)
					{
						progress.dismiss();
						loginDialog.dismiss();

						if (!failed && !TextUtils.isEmpty(token) && !isFinishing())
						{
							prefs.edit()
								.putString("developer_token", token)
								.putBoolean("developer_mode", true)
								.putLong("developer_timeout", timeout)
								.apply();

							MainApplication.getContentSettings().setAuthorizationToken(token);
							MainApplication.getContentSettings().setContentEnvironment(Environment.TEST);

							clearCache();

							Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
							intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(intent);
						}
						else if (failed)
						{
							Toast.makeText(getBaseContext(), "Invalid username/password combination", Toast.LENGTH_LONG).show();
						}
					}
				};

				loginDialog.show();
				loginDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
				{
					@Override public void onClick(View v)
					{
						try
						{
							EditText username = (EditText)loginDialog.findViewById(R.id.username);
							EditText password = (EditText)loginDialog.findViewById(R.id.password);

							JsonObject data = new JsonObject();
							data.addProperty("username", username.getText().toString());
							data.addProperty("password", password.getText().toString());

							JsonEntity loginData = new JsonEntity(data);
							loginClient.post("authentication", loginData, response);
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				});
			}

			return true;
		}

		return false;
	}

	public void clearCache()
	{
		String path = ContentSettings.getInstance().getStoragePath();
		deleteRecursive(new File(path, "pages/"));
		deleteRecursive(new File(path, "data/"));
		deleteRecursive(new File(path, "content/"));
		deleteRecursive(new File(path, "languages/"));
		deleteRecursive(new File(path, "app.json"));
		deleteRecursive(new File(path, "manifest.json"));
	}

	private void deleteRecursive(File fileOrDirectory)
	{
		if (fileOrDirectory.isDirectory())
		{
			File[] files = fileOrDirectory.listFiles();

			if (files != null)
			{
				for (File child : files)
				{
					deleteRecursive(child);
				}
			}
		}

		fileOrDirectory.delete();
	}
}
