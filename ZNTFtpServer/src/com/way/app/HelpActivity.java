package com.way.app;

import android.os.Bundle;
import android.text.util.Linkify;
import android.widget.TextView;

import com.way.swipeback.SwipeBackActivity;
import com.znt.ftp.R;

public class HelpActivity extends SwipeBackActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help_layout);
		TextView tv = (TextView) findViewById(R.id.text);
		tv.setText(R.string.help_dlg_message);
		Linkify.addLinks(tv, Linkify.ALL);
	}
}
