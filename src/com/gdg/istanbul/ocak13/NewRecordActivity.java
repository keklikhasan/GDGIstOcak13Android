package com.gdg.istanbul.ocak13;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gdg.istanbul.ocak13.R;
import com.gdg.istanbul.ocak13.api.GDGApi;
import com.gdg.istanbul.ocak13.utils.ConnectionUtil;
import com.gdg.istanbul.ocak13.utils.Constants;
import com.gdg.istanbul.ocak13.utils.ImageUtil;
import com.gdg.istanbul.ocak13.utils.LanguageUtil;
import com.gdg.istanbul.ocak13.utils.LocationUtil;
import com.gdg.istanbul.ocak13.utils.MessageUtil;
import com.gdg.istanbul.ocak13.utils.PropertiesUtil;

@SuppressLint("HandlerLeak")
public class NewRecordActivity extends Activity {

	// tasks
	private SendTask mSendTask = null;
	private Activity mContext;
	private static MessageUtil messageUtil;
	private SharedPreferences mPreferences = null;

	//
	private static String mUserName;
	private static String mTitleText;
	private static String mContentText;
	private static Bitmap mBitmap;
	private static byte[] bitmapdata;
	private static String mLocationString;

	// UI references.
	private TextView mLabelUserName;
	private RelativeLayout mUsernameContainer;
	private ImageView mImage;
	private RelativeLayout mImageContainer;
	private Button mButtonAddImage;
	private EditText mTitle;
	private EditText mContent;
	private Button mButtonSend;
	private static boolean lifeCyleStatus;
	private static boolean waitImage;

	// ACTION CODES
	private static final int ACTION_TAKE_PHOTO = 1;
	private static final int ACTION_CHOOSE_PHOTO = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LanguageUtil.setApplicationLanguage(this);
		setContentView(R.layout.activity_new_record);
		mContext = this;
		mPreferences = PropertiesUtil.getSharedPreferences(mContext);
		messageUtil = new MessageUtil(mContext);

		mUserName = mPreferences.getString(Constants.PREF_USERNAME, null);

		mLabelUserName = (TextView) findViewById(R.id.lable_username);
		mImage = (ImageView) findViewById(R.id.image_thumbnail);
		mButtonAddImage = (Button) findViewById(R.id.button_add_image);
		mTitle = (EditText) findViewById(R.id.title);
		mContent = (EditText) findViewById(R.id.content);
		mButtonSend = (Button) findViewById(R.id.button_send);
		mUsernameContainer = (RelativeLayout) findViewById(R.id.username_container);
		mImageContainer = (RelativeLayout) findViewById(R.id.image_container);

		lifeCyleStatus = false;
		waitImage = false;
		// set value
		mLabelUserName.setText(mUserName);
		if (mBitmap != null) {
			mImage.setImageBitmap(mBitmap);
		}
		mContent.setText(mContentText);
		mTitle.setText(mTitleText);
		if (mBitmap != null) {
			mImage.setImageBitmap(mBitmap);
		}

		mButtonAddImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				addImage();
			}
		});
		mUsernameContainer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				changeName();
			}
		});
		mImageContainer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				addImage();
			}
		});
		mButtonSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				attemptSend();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!waitImage) {
			if (lifeCyleStatus) {
				lifeCyleStatus = false;
				finish();
				startActivity(getIntent());
			}
		} else {
			waitImage=false;
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		lifeCyleStatus = true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.menu_new, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_exit: {
			finish();
			System.exit(0);
			break;
		}
		case R.id.menu_settings: {
			Intent intent = new Intent(mContext, SettingsActivity.class);
			startActivity(intent);
			break;
		}
		case R.id.menu_map: {
			/*
			 * to choose which maps api version
			 */
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setMessage(R.string.new_record_dialog_choose_maps)
					.setPositiveButton(
							R.string.new_record_dialog_choose_mapsv2,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									startmapActivityv2();
								}
							})
					.setNegativeButton(
							R.string.new_record_dialog_choose_mapsv1,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									startmapActivityv1();
								}
							}).show();
			break;
		}
		}
		return true;
	}

	public void changeName() {

		LayoutInflater li = LayoutInflater.from(mContext);
		View usernameView = li.inflate(R.layout.input_dialog, null);
		AlertDialog.Builder usernameDialogBuilder = new AlertDialog.Builder(
				mContext);
		usernameDialogBuilder.setView(usernameView);

		// set dialog message
		usernameDialogBuilder.setCancelable(true);

		// create alert dialog
		final AlertDialog usernameDialog = usernameDialogBuilder.create();

		// input_dialog_title
		TextView userInputTitle = (TextView) usernameView
				.findViewById(R.id.input_dialog_title);
		userInputTitle.setText(R.string.dialog_title_username);

		// input dialog edit text
		final EditText userInput = (EditText) usernameView
				.findViewById(R.id.input_dialog_edittext);
		userInput.setError(null);
		userInput.setText(mUserName);
		userInput.setHint(R.string.prompt_username);

		// input dialog ok button
		Button buttonOK = (Button) usernameView.findViewById(R.id.dialog_ok);
		buttonOK.setText(R.string.dialog_ok);
		buttonOK.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				boolean cancel = false;
				String tmpUserName = userInput.getText().toString();
				View focusView = null;

				if (TextUtils.isEmpty(tmpUserName)) {
					userInput
							.setError(getString(R.string.error_field_required));
					focusView = userInput;
					cancel = true;
				} else if (tmpUserName.length() < 3) {
					userInput
							.setError(getString(R.string.error_invalid_short_username));
					focusView = userInput;
					cancel = true;
				} else if (mUserName.length() > 15) {
					userInput
							.setError(getString(R.string.new_record_error_username_long));
					focusView = userInput;
					cancel = true;
				}

				if (cancel) {
					focusView.requestFocus();
				} else {
					mUserName = tmpUserName;
					PropertiesUtil.setString(mPreferences,
							Constants.PREF_USERNAME, mUserName);
					mLabelUserName.setText(mUserName);
					usernameDialog.dismiss();
				}
			}
		});
		// input dialog cancel button
		Button buttonCancel = (Button) usernameView
				.findViewById(R.id.dialog_cancel);
		buttonCancel.setText(R.string.dialog_cancel);
		buttonCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				usernameDialog.dismiss();
			}
		});

		// show it
		usernameDialog.show();
	}

	public void startmapActivityv1() {
		Intent intent = new Intent(mContext, MapsActivityv1.class);
		intent.putExtra(Constants.EXTRA_USERNAME, mUserName);
		startActivity(intent);
		finish();
	}

	public void startmapActivityv2() {
		Intent intent = new Intent(mContext, MapsActivityv2.class);
		intent.putExtra(Constants.EXTRA_USERNAME, mUserName);
		startActivity(intent);
		finish();
	}

	public void attemptSend() {

		if (mSendTask != null) {
			return;
		}

		mTitle.setError(null);
		mContent.setError(null);

		mTitleText = mTitle.getText().toString();
		mContentText = mContent.getText().toString();

		boolean cancel = false;
		View focusView = null;

		if (mBitmap == null || mBitmap.getHeight() < 1) {
			messageUtil
					.showToastMessage(getString(R.string.new_record_error_img_required));
			focusView = mImage;
			cancel = true;
		}
		/*
		 * check locationn services
		 */

		getLocation();
		/*
		 * Test
		 */
		if (TextUtils.isEmpty(mLocationString)) {
			mLocationString = "41.110100,29.031714";
		}
		/*
		 * Test
		 */

		if (TextUtils.isEmpty(mLocationString)) {
			messageUtil
					.showToastMessage(getString(R.string.new_record_error_not_find_loc));
			cancel = true;
		}

		if (TextUtils.isEmpty(mUserName)) {
			messageUtil
					.showToastMessage(getString(R.string.new_record_error_username_required));
			focusView = mLabelUserName;
			cancel = true;
		} else if (mUserName.length() < 3) {
			messageUtil
					.showToastMessage(getString(R.string.new_record_error_username_short));
			focusView = mLabelUserName;
			cancel = true;
		}

		if (TextUtils.isEmpty(mContentText)) {
			mContent.setError(getString(R.string.new_record_error_content_required));
			focusView = mContent;
			cancel = true;
		}

		if (TextUtils.isEmpty(mTitleText)) {
			mTitle.setError(getString(R.string.new_record_error_title_required));
			focusView = mTitle;
			cancel = true;
		} else if (mTitleText.length() < 3) {
			mTitle.setError(getString(R.string.new_record_error_title_short));
			focusView = mTitle;
			cancel = true;
		} else if (mTitleText.length() > 50) {
			mTitle.setError(getString(R.string.new_record_error_title_long));
			focusView = mTitle;
			cancel = true;
		}

		if (cancel) {
			if (focusView != null)
				focusView.requestFocus();
		} else {
			messageUtil.showProgress(true,
					getString(R.string.new_record_progress_sending));
			mSendTask = new SendTask();
			mSendTask.execute((Void) null);
		}
	}

	public void addImage() {
		/*
		 * choose camera or galery
		 */
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setMessage(R.string.new_record_label_image)
				.setPositiveButton(R.string.new_record_dialog_choose_photo,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dispatchChoosePictureIntent();
							}
						})
				.setNegativeButton(R.string.new_record_dialog_take_photo,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								if (!getPackageManager().hasSystemFeature(
										PackageManager.FEATURE_CAMERA)) {
									Toast.makeText(
											mContext,
											getResources()
													.getString(
															R.string.new_record_error_no_camera),
											Toast.LENGTH_SHORT).show();
								} else {
									dispatchTakePictureIntent();
								}
							}
						}).show();
	}

	private void dispatchTakePictureIntent() {
		waitImage = true;
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(takePictureIntent, ACTION_TAKE_PHOTO);
	}

	private void dispatchChoosePictureIntent() {
		waitImage = true;
		Intent choosePictureIntent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(choosePictureIntent, ACTION_CHOOSE_PHOTO);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case ACTION_TAKE_PHOTO: {
			if (resultCode == RESULT_OK) {
				handleCameraPhoto(data);
			}
			break;
		}
		case ACTION_CHOOSE_PHOTO: {
			if (resultCode == RESULT_OK) {
				handleGalaryPhoto(data);
			}
			break;
		}
		}
	}

	private void handleCameraPhoto(Intent intent) {
		Bundle extras = intent.getExtras();
		mBitmap = (Bitmap) extras.get("data");
		bitmapdata = ImageUtil.codec(mBitmap, Bitmap.CompressFormat.JPEG, 50);
		mBitmap = BitmapFactory.decodeByteArray(bitmapdata, 0,
				bitmapdata.length);
		mImage.setImageBitmap(mBitmap);
	}

	private void handleGalaryPhoto(Intent intent) {
		Uri selectedImage = intent.getData();
		String[] filePathColumn = { MediaStore.Images.Media.DATA };

		Cursor cursor = getContentResolver().query(selectedImage,
				filePathColumn, null, null, null);
		cursor.moveToFirst();

		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		String picturePath = cursor.getString(columnIndex);
		cursor.close();
		mBitmap = BitmapFactory.decodeFile(picturePath);
		bitmapdata = ImageUtil.codec(mBitmap, Bitmap.CompressFormat.JPEG, 50);
		mBitmap = BitmapFactory.decodeByteArray(bitmapdata, 0,
				bitmapdata.length);
		mImage.setImageBitmap(mBitmap);
	}

	public class SendTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {

			if (!ConnectionUtil.checkInternetWM(mContext)) {
				messageUtil
						.showToastMessage(getString(R.string.error_no_connection));
				return false;
			}
			if (GDGApi.newRecord(mContext, messageUtil, mTitleText,
					mContentText, mUserName, mLocationString, bitmapdata)) {
				resetViewhandler.sendEmptyMessage(-1);
				return true;
			}
			return false;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mSendTask = null;
			messageUtil.showProgress(false, null);
		}

		@Override
		protected void onCancelled() {
			mSendTask = null;
			messageUtil.showProgress(false, null);
		}
	}

	/*
	 * Location Services
	 */

	public void getLocation() {
		Location loc = LocationUtil.getLastBestLocation(mContext);
		if (loc != null) {
			mLocationString = loc.getLatitude() + "," + loc.getLongitude();
		} else {
			messageUtil
					.showToastMessage(getString(R.string.new_record_error_not_find_loc));
		}
	}

	/*
	 * Reset views
	 */
	private Handler resetViewhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mBitmap = null;
			bitmapdata = null;
			mContentText = null;
			mTitleText = null;
			mImage.setImageDrawable(getResources().getDrawable(
					R.drawable.ic_launcher));
			mContent.setText(mContentText);
			mTitle.setText(mTitleText);
		}
	};

}
