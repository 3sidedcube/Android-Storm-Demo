package com.cube.storm.example;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.cube.storm.ContentSettings;
import com.cube.storm.LanguageSettings;
import com.cube.storm.MessageSettings;
import com.cube.storm.UiSettings;
import com.cube.storm.content.lib.Environment;
import com.cube.storm.content.lib.listener.UpdateListener;
import com.cube.storm.language.lib.processor.LanguageTextProcessor;
import com.cube.storm.lib.factory.OverrideIntentFactory;
import com.cube.storm.message.lib.listener.RegisterListener;
import com.cube.storm.message.lib.receiver.GCMReceiver;
import com.cube.storm.ui.model.App;
import com.cube.storm.util.lib.debug.Debug;
import com.google.gson.JsonObject;

import net.callumtaylor.asynchttp.AsyncHttpClient;
import net.callumtaylor.asynchttp.obj.entity.JsonEntity;

/**
 * Entry application for the example.
 *
 * Loads the modules for bundle content
 *
 * @author Callum Taylor
 * @project StormExample
 */
public class MainApplication extends Application
{
	private static ContentSettings contentSettings;
	private static UiSettings uiSettings;
	private static LanguageSettings languageSettings;
	private static MessageSettings messageSettings;

	@Override public void onCreate()
	{
		super.onCreate();

		Debug.DEBUG = true;

		contentSettings = new ContentSettings.Builder(this)
			.appId("3SC_STORM-1-1")
			.contentBaseUrl("http://api.stormcorp.co/")
			.contentVersion("latest")
			.contentEnvironment(Environment.TEST)
			.updateListener(new UpdateListener()
			{
				@Override public void onUpdateDownloaded()
				{
					loadApp();

					// Reload the language
					languageSettings.reloadLanguage(MainApplication.this);
				}
			})
			.build();

		languageSettings = new LanguageSettings.Builder(this)
			.registerUriResolver("cache", ContentSettings.getInstance().getUriResolvers().get("cache"))
			.defaultLanguage(Uri.parse("cache://languages/gbr_en.json"))
			.build();

		uiSettings = new UiSettings.Builder(this)
			.registerUriResolver("cache", ContentSettings.getInstance().getUriResolvers().get("cache"))
			.intentFactory(new OverrideIntentFactory())
			.textProcessor(new LanguageTextProcessor())
//			.textProcessor(new OverrideLanguageTextProcessor())
			.build();

		messageSettings = new MessageSettings.Builder(this)
			.projectNumber("87842985303")
			.registerListener(new RegisterListener()
			{
				@Override public void onDeviceRegistered(@NonNull Context context, @Nullable String token)
				{
					Debug.out(token);

					if (!TextUtils.isEmpty(token))
					{
						AsyncHttpClient client = new AsyncHttpClient(contentSettings.getContentBaseUrl());

						try
						{
							String appId = contentSettings.getAppId();
							String[] appIdParts = appId.split("-");

							JsonObject jsonData = new JsonObject();
							jsonData.addProperty("appId", appIdParts[2]);
							jsonData.addProperty("token", token);
							jsonData.addProperty("idiom", "android");

							JsonEntity postData = new JsonEntity(jsonData);

							client.post(contentSettings.getContentVersion() + "/push/token", postData, null);
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				}
			})
			.build();

		// register for gcm
		new GCMReceiver().register(this);

		// Loading app json
		loadApp();
	}

	private void loadApp()
	{
		String appUri = "cache://app.json";
		App app = UiSettings.getInstance().getViewBuilder().buildApp(Uri.parse(appUri));

		if (app != null)
		{
			UiSettings.getInstance().setApp(app);
		}
	}
}
