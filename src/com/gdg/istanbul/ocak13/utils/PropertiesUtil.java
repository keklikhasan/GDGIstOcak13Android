package com.gdg.istanbul.ocak13.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.preference.PreferenceManager;

@SuppressLint("CommitPrefEdits")
public class PropertiesUtil {

	public static SharedPreferences getSharedPreferences(Context mContext) {
		return PreferenceManager.getDefaultSharedPreferences(mContext);

	}

	public static boolean clearToDefaults(Context mContext) {
		Editor editor = getSharedPreferences(mContext).edit().clear();
		return commitEditor(editor);
	}

	@SuppressLint("CommitPrefEdits")
	public static void setString(SharedPreferences mPreferences, String key,
			String value) {
		Editor editor = mPreferences.edit();
		editor.putString(key, value);
		commitEditor(editor);
	}

	@SuppressLint("NewApi")
	public static boolean commitEditor(SharedPreferences.Editor editor) {
		if (editor != null) {
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
				return editor.commit();
			} else {
				editor.apply();
				return editor.commit();
			}
		}
		return false;
	}

}
