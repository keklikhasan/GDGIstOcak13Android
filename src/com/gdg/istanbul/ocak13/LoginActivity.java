package com.gdg.istanbul.ocak13;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gdg.istanbul.ocak13.tw.TwitterUtils;
import com.gdg.istanbul.ocak13.utils.Constants;
import com.gdg.istanbul.ocak13.utils.LanguageUtil;
import com.gdg.istanbul.ocak13.utils.PropertiesUtil;
import com.twitter.android.Twitter;
import com.twitter.android.Twitter.TweetDialogListener;
import com.twitter.android.TwitterError;

@SuppressLint("HandlerLeak")
public class LoginActivity extends Activity {

	// activity context
	private Activity mContext;
	private SharedPreferences mPreferences = null;
	private Handler handleEvent = null;

	// username
	private static String mUserName;

	// UI references.
	private EditText mUserNameView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LanguageUtil.setApplicationLanguage(this);
		setContentView(R.layout.activity_login);
		mContext = this;
		mPreferences = PropertiesUtil.getSharedPreferences(mContext);
		mUserName = mPreferences.getString(Constants.PREF_USERNAME, null);
		if (!TextUtils.isEmpty(mUserName)) {
			Intent intent = new Intent(mContext, NewRecordActivity.class);
			intent.putExtra(Constants.EXTRA_USERNAME, mUserName);
			startActivity(intent);
			finish();
		}

		mUserNameView = (EditText) findViewById(R.id.username);
		mUserNameView.setText(mUserName);

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						// hide keyboard
						((InputMethodManager) mContext
								.getSystemService(Context.INPUT_METHOD_SERVICE))
								.hideSoftInputFromWindow(
										mUserNameView.getWindowToken(), 0);
						attemptLogin();
					}
				});

		findViewById(R.id.sign_in_button_tw).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						loginTwitter();
					}
				});
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.menu_login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_exit: {
			finish();
			android.os.Process.killProcess(android.os.Process.myPid());
			break;
		}
		case R.id.menu_about: {
			Intent intent = new Intent(mContext, AboutUsActivity.class);
			startActivity(intent);
			break;
		}
		}
		return true;
	}

	public void attemptLogin() {
		mUserNameView.setError(null);
		mUserName = mUserNameView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		if (TextUtils.isEmpty(mUserName)) {
			mUserNameView.setError(getString(R.string.error_field_required));
			focusView = mUserNameView;
			cancel = true;
		} else if (mUserName.length() < 3) {
			mUserNameView
					.setError(getString(R.string.error_invalid_short_username));
			focusView = mUserNameView;
			cancel = true;
		} else if (mUserName.length() > 15) {
			mUserNameView
					.setError(getString(R.string.new_record_error_username_long));
			focusView = mUserNameView;
			cancel = true;
		}

		if (cancel) {
			focusView.requestFocus();
		} else {
			PropertiesUtil.setString(mPreferences, Constants.PREF_USERNAME,
					mUserName);
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			Intent intent = new Intent(mContext, NewRecordActivity.class);
			startActivity(intent);
			finish();
		}
	}

	private void loginTwitter() {
		handleEvent = new Handler();
		final Twitter twitter = new Twitter(R.drawable.tw);

		twitter.authorize(mContext, handleEvent,
				getString(R.string.tw_consumer_key),
				getString(R.string.tw_consumer_secret),
				new TweetDialogListener() {

					public void onTwitterError(TwitterError e) {
						e.printStackTrace();
					}

					public void onError(com.twitter.android.DialogError e) {
						e.printStackTrace();
					}

					public void onComplete(Bundle values) {
						try {
							PropertiesUtil.setString(mPreferences,
									Constants.PREF_TWITTER_AUTH_TOKEN,
									twitter.getAccessToken());
							PropertiesUtil.setString(mPreferences,
									Constants.PREF_TWITTER_AUTH_SECRET,
									twitter.getSecretToken());
							mUserName = TwitterUtils.getUserName(mContext,
									mPreferences);
							PropertiesUtil.setString(mPreferences,
									Constants.PREF_USERNAME, mUserName);
							mLoginStatusMessageView
									.setText(R.string.login_progress_signing_in);
							Intent intent = new Intent(mContext,
									NewRecordActivity.class);
							startActivity(intent);
							finish();
						} catch (Exception e) {
							Message msg = new Message();
							Bundle bundle = new Bundle();
							bundle.putString(
									Constants.EXTRA_MESSAGE,
									getString(R.string.error_api_tw)
											+ e.toString());
							msg.setData(bundle);
							toastMessageHandler.sendMessage(msg);
						}

					}

					public void onCancel() {
						System.err.println("cancel");
					}
				});

	}

	private Handler toastMessageHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Toast.makeText(mContext,
					msg.getData().getString(Constants.EXTRA_MESSAGE),
					Toast.LENGTH_SHORT).show();
		}
	};

	private void showProgress(boolean show) {
		mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
		mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
	}

}
