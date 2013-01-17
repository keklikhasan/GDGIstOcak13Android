package com.gdg.istanbul.ocak13.map;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gdg.istanbul.ocak13.R;
import com.gdg.istanbul.ocak13.api.GDGApi;
import com.gdg.istanbul.ocak13.api.Record;
import com.gdg.istanbul.ocak13.utils.ImageUtil;

@SuppressLint("HandlerLeak")
public class MapDialogUtil {

	// show info
	private ImageView imageView;
	private ProgressBar progressBar;
	private DownloadImageTask downloadTask = null;
	private Bitmap image;
	private Context mContext;

	public MapDialogUtil(Context mContext) {
		this.mContext = mContext;
	}

	public class DownloadImageTask extends AsyncTask<String, String, Boolean> {
		@Override
		protected Boolean doInBackground(String... params) {

			try {
				URL url = new URL(GDGApi.getUrl(mContext, params[0]));
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setDoInput(true);
				conn.connect();
				InputStream is = conn.getInputStream();
				image = BitmapFactory.decodeStream(is);
				byte[] data = ImageUtil.codec(image,
						Bitmap.CompressFormat.JPEG, 90);
				image = BitmapFactory.decodeByteArray(data, 0, data.length);
			} catch (Exception e) {
				e.printStackTrace();
			}
			setImageHandler.sendEmptyMessage(-1);
			downloadTask = null;
			return false;
		}
	}

	public Handler setImageHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (image != null) {
				imageView.setImageBitmap(image);
				progressBar.setVisibility(View.GONE);
				imageView.setVisibility(View.VISIBLE);
			} else {
				Toast.makeText(mContext, R.string.error_download_image,
						Toast.LENGTH_SHORT).show();
			}
		}
	};

	public void showDialog(Record rec, View mapInfoView) {
		AlertDialog.Builder mapInfoDialogBuilder = new AlertDialog.Builder(
				mContext);
		mapInfoDialogBuilder.setView(mapInfoView);
		mapInfoDialogBuilder.setCancelable(true);
		final AlertDialog mapInfoDialog = mapInfoDialogBuilder.create();

		// Set Title
		TextView mapsTitleAlert = (TextView) mapInfoView
				.findViewById(R.id.map_title);
		mapsTitleAlert.setText(rec.title);

		// Set user
		TextView mapsUserAlert = (TextView) mapInfoView
				.findViewById(R.id.map_user);
		mapsUserAlert.setText(rec.user+" : ");

		// Set Content
		TextView mapsContentAlert = (TextView) mapInfoView
				.findViewById(R.id.map_content);
		mapsContentAlert.setText(rec.body);

		// Set Date
		TextView mapsDateAlert = (TextView) mapInfoView
				.findViewById(R.id.map_date);
		mapsDateAlert.setText(rec.createdAt);

		imageView = (ImageView) mapInfoView.findViewById(R.id.image_thumbnail);
		progressBar = (ProgressBar) mapInfoView.findViewById(R.id.progressBar);

		downloadTask = new DownloadImageTask();
		String[] params = new String[1];
		params[0] = rec.pic;
		downloadTask.execute(params);
		mapInfoDialog.show();

		mapInfoDialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				downloadTask = null;
				imageView = null;
				progressBar = null;
				image = null;
			}
		});
	}

}
