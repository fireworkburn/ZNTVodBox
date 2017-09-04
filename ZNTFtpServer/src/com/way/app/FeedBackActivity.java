package com.way.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.way.swipeback.SwipeBackActivity;
import com.znt.ftp.R;

public class FeedBackActivity extends SwipeBackActivity {
	private EditText mFeedBackEditText;
	private Button mSendFeedBackButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedback_layout);
		mFeedBackEditText = (EditText) findViewById(R.id.fee_back_edit);
		mSendFeedBackButton = (Button) findViewById(R.id.feed_back_btn);
		mSendFeedBackButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String content = mFeedBackEditText.getText().toString();
				if (!TextUtils.isEmpty(content)) {
					Intent intent = new Intent(Intent.ACTION_SENDTO);
					intent.setType("text/plain");
					intent.putExtra(Intent.EXTRA_SUBJECT, "FTP服务器 - 信息反馈");
					intent.putExtra(Intent.EXTRA_TEXT, content);
					intent.setData(Uri.parse("mailto:way.ping.li@gmail.com"));
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					FeedBackActivity.this.startActivity(intent);
				} else {
					Toast.makeText(getApplicationContext(), "请输入一点点内容嘛！",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
}
