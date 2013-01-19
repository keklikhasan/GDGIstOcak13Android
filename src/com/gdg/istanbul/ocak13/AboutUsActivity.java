package com.gdg.istanbul.ocak13;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class AboutUsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_us);

		findViewById(R.id.tw).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String url = "https://twitter.com/keklikhasan";
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
		});

		findViewById(R.id.gdg).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String url = "http://gdgistanbul.com";
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
		});

		findViewById(R.id.email).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent emailIntent = new Intent(
						android.content.Intent.ACTION_SEND);
				emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
						new String[] { "keklikhasan@gmail.com" });
				emailIntent.setType("plain/text");
				startActivity(Intent.createChooser(emailIntent,
						"Contact Hasan Keklik"));
			}
		});
	}

}
