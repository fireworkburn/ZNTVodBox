package com.way.app;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.way.ftp.Defaults;
import com.way.ftp.FtpServerService;
import com.way.swipeback.SwipeBackActivity;
import com.znt.ftp.R;

public class UserPwdActivity extends SwipeBackActivity implements
		OnClickListener {
	private EditText mPortEditText;
	private EditText mUserNameEditText;
	private EditText mPasswordEditText;
	private Button mConfirmButton;
	private Button mCancleButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_password_port_layout);
		mPortEditText = (EditText) findViewById(R.id.port_edittext);
		mUserNameEditText = (EditText) findViewById(R.id.user_name_edittext);
		mPasswordEditText = (EditText) findViewById(R.id.password_edittext);
		mConfirmButton = (Button) findViewById(R.id.buttonConfirm);
		mCancleButton = (Button) findViewById(R.id.buttonCancle);
		mConfirmButton.setOnClickListener(this);
		mCancleButton.setOnClickListener(this);

		mPortEditText.setText(PreferenceUtils.getPrefInt(this,
				FtpServerService.PORT_KEY, Defaults.portNumber) + "");
		mUserNameEditText.setText(PreferenceUtils.getPrefString(this,
				FtpServerService.USER_NAME_KEY, Defaults.username));
		mPasswordEditText.setText(PreferenceUtils.getPrefString(this,
				FtpServerService.PASSWORD_KEY, Defaults.password));

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonConfirm:
			int port = 0;
			try {
				port = Integer.parseInt(mPortEditText.getText().toString());
			} catch (Exception e) {
			}
			if (port <= 0 || port > 65535) {
				Toast.makeText(this, R.string.port_validation_error,
						Toast.LENGTH_LONG).show();
				return;
			}
			String name = mUserNameEditText.getText().toString();
			if (!name.matches("[a-zA-Z0-9]+")) {
				Toast.makeText(this, R.string.username_validation_error,
						Toast.LENGTH_LONG).show();
				return;
			}
			String password = mPasswordEditText.getText().toString();
			if (TextUtils.isEmpty(password)) {
				Toast.makeText(this, R.string.password_validation_error,
						Toast.LENGTH_LONG).show();
				return;
			}

			PreferenceUtils.setPrefInt(this, FtpServerService.PORT_KEY, port);
			PreferenceUtils.setPrefString(this, FtpServerService.USER_NAME_KEY,
					name);
			PreferenceUtils.setPrefString(this, FtpServerService.PASSWORD_KEY,
					password);
			setResult(1);
			finish();
			break;
		case R.id.buttonCancle:
			finish();
			break;

		default:
			break;
		}
	}

}
