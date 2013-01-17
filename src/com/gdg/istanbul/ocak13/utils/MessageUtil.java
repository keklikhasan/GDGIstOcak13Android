package com.gdg.istanbul.ocak13.utils;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.gdg.istanbul.ocak13.R;

@SuppressLint("HandlerLeak")
public class MessageUtil {

	private ProgressDialog progressDialog = null;
	private Context mContext;

	public MessageUtil(Context context) {
		mContext = context;
	}

	public boolean isProgress() {
		if (progressDialog != null) {
			return progressDialog.isShowing();
		}
		return false;
	}

	public void showProgress(boolean show, String message) {
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putBoolean(Constants.EXTRA_SHOW, show);
		bundle.putString(Constants.EXTRA_MESSAGE, message);
		msg.setData(bundle);
		progressHandler.sendMessage(msg);
	}

	public void showToastMessage(String message) {
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putString(Constants.EXTRA_MESSAGE, message);
		msg.setData(bundle);
		toastMessageHandler.sendMessage(msg);
	}

	private Handler progressHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			boolean show = false;
			String message = null;
			try {
				show = msg.getData().getBoolean(Constants.EXTRA_SHOW);
				message = msg.getData().getString(Constants.EXTRA_MESSAGE);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (TextUtils.isEmpty(message)) {
				message = mContext.getString(R.string.maps_progress_wait);
			}

			if (show) {
				if (progressDialog == null) {
					progressDialog = ProgressDialog.show(mContext, "", message,
							true);
				} else {
					progressDialog.dismiss();
					progressDialog = null;
					progressDialog = ProgressDialog.show(mContext, "", message,
							true);
				}
			} else {
				if (progressDialog != null) {
					progressDialog.dismiss();
					progressDialog = null;
				}
			}
		}
	};

	private Handler toastMessageHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Toast.makeText(mContext,
					msg.getData().getString(Constants.EXTRA_MESSAGE),
					Toast.LENGTH_SHORT).show();
		}
	};
}
