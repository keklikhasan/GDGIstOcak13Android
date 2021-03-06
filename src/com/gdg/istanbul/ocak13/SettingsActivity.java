package com.gdg.istanbul.ocak13;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.gdg.istanbul.ocak13.utils.Constants;
import com.gdg.istanbul.ocak13.utils.LanguageUtil;
import com.gdg.istanbul.ocak13.utils.MessageUtil;
import com.gdg.istanbul.ocak13.utils.PropertiesUtil;

public class SettingsActivity extends PreferenceActivity {

	private MessageUtil messageUtil = null;
	private Activity mContext = null;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LanguageUtil.setApplicationLanguage(this);
		addPreferencesFromResource(R.xml.settings);
		mContext = this;
		messageUtil = new MessageUtil(mContext);

		findPreference(Constants.PREF_USERNAME).setOnPreferenceChangeListener(
				new OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						boolean result = true;
						String newUserName = (String) newValue;
						if (TextUtils.isEmpty(newUserName)) {
							messageUtil
									.showToastMessage(getString(R.string.error_field_required));
							result = false;
						} else if (newUserName.length() < 3) {
							messageUtil
									.showToastMessage(getString(R.string.error_invalid_short_username));
							result = false;
						} else if (newUserName.length() > 15) {
							messageUtil
									.showToastMessage(getString(R.string.new_record_error_username_long));
							result = false;
						}
						return result;
					}
				});

		findPreference(Constants.PREF_API_URL).setOnPreferenceChangeListener(
				new OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						boolean result = true;
						String newApiUrl = (String) newValue;
						if (TextUtils.isEmpty(newApiUrl)) {
							messageUtil
									.showToastMessage(getString(R.string.error_field_required));
							result = false;
						} else if (!newApiUrl.startsWith("http")) {
							messageUtil
									.showToastMessage(getString(R.string.error_api_url_http_required));
							result = false;
						}
						return result;
					}
				});

		findPreference(Constants.PREF_APP_LANG).setOnPreferenceChangeListener(
				new OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						PropertiesUtil.setString(
								PropertiesUtil.getSharedPreferences(mContext),
								Constants.PREF_APP_LANG, newValue.toString());
						LanguageUtil.setApplicationLanguage(mContext);
						messageUtil
								.showToastMessage(getString(R.string.settings_category_language_suc));
						return true;
					}
				});
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// refresh your views here
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.menu_settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_exit: {
			android.os.Process.killProcess(android.os.Process.myPid());
			finish();
			break;
		}
		case R.id.menu_about: {
			Intent intent = new Intent(mContext, AboutUsActivity.class);
			startActivity(intent);
			break;
		}
		case R.id.menu_default: {
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setMessage(R.string.dialog_title_sure_clear_to_defaults)
					.setPositiveButton(R.string.dialog_ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									if (PropertiesUtil
											.clearToDefaults(mContext)) {
										messageUtil
												.showToastMessage(getString(R.string.suc_on_clear_def));
										finish();
										startActivity(getIntent());
									} else {
										messageUtil
												.showToastMessage(getString(R.string.error_on_clear_def));
									}
								}
							})
					.setNegativeButton(R.string.dialog_cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.dismiss();
								}
							}).show();
			break;
		}
		}
		return true;
	}

}
