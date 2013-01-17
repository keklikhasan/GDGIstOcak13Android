package com.gdg.istanbul.ocak13.map;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;

import com.gdg.istanbul.ocak13.R;
import com.gdg.istanbul.ocak13.api.Record;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

@SuppressLint("HandlerLeak")
public class CustomItemizedOverlay extends ItemizedOverlay<OverlayItem> {

	private ArrayList<OverlayItem> mapOverlays = new ArrayList<OverlayItem>();
	public Record record;
	private Context mContext;

	public CustomItemizedOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
	}

	public CustomItemizedOverlay(Drawable defaultMarker, Context context) {
		this(defaultMarker);
		this.mContext = context;
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mapOverlays.get(i);
	}

	@Override
	public int size() {
		return mapOverlays.size();
	}

	public void addOverlay(OverlayItem overlay) {
		mapOverlays.add(overlay);
		this.populate();
	}

	public void addRecord(Record record) {
		this.record = record;
		this.populate();
	}

	@Override
	protected boolean onTap(int index) {

		if (record != null) {
			LayoutInflater li = LayoutInflater.from(mContext);
			View mapInfoView = li.inflate(R.layout.maps_dialog, null);

			MapDialogUtil dialogUtil = new MapDialogUtil(mContext);
			dialogUtil.showDialog(record, mapInfoView);

		} else {
			OverlayItem item = mapOverlays.get(index);
			AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
			dialog.setTitle(item.getTitle());
			dialog.setCancelable(true);
			dialog.setMessage(item.getSnippet());
			dialog.show();
		}
		return true;
	}

}