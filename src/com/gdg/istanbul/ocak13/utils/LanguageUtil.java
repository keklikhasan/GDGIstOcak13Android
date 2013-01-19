package com.gdg.istanbul.ocak13.utils;

import java.util.Locale;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;

public class LanguageUtil {
	public static String LANG_TR = "tr_TR";
	public static String LANG_EN = "en_US";

	public static void setApplicationLanguage(Activity activity) {
		try {
			String appLanguage = PropertiesUtil.getSharedPreferences(activity)
					.getString(Constants.PREF_APP_LANG, "");
			Locale locale = null;
			if (appLanguage.equals(LANG_TR)) {
				locale = new Locale("tr", "TR");
			} else if (appLanguage.equals(LANG_EN)) {
				locale = new Locale("en", "US");
			} else {
				locale = new Locale(
						Resources.getSystem().getConfiguration().locale
								.getLanguage());
			}
			Locale.setDefault(locale);
			Configuration config = new Configuration();
			config.locale = locale;
			activity.getBaseContext().getResources()
					.updateConfiguration(config, null);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
