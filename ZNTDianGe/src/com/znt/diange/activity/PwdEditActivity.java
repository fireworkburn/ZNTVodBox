package com.znt.diange.activity;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.znt.diange.R;
import com.znt.diange.factory.HttpFactory;
import com.znt.diange.http.HttpMsg;

public class PwdEditActivity extends BaseActivity implements OnClickListener
{

	private EditText etOldPwd = null;
	private EditText etNewPwd = null;
	private EditText etPwdConfirm = null;
	private TextView tvConfirm = null;
	
	private HttpFactory httpFactory = null;
	
	private boolean isRunning = false;
	
	Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == HttpMsg.NO_NET_WORK_CONNECT)
			{
				showToast("无网络连接");
			}
			else if(msg.what == HttpMsg.PWD_RESET_START)
			{
				//修改密码
				tvConfirm.setText("正在处理...");
				isRunning = true;
			}
			else if(msg.what == HttpMsg.PWD_RESET_SUCCESS)
			{
				//修改密码成功
				
				String newPwd = etNewPwd.getText().toString();
				//getLocalData().setUserPwd(newPwd);
				tvConfirm.setText("确认修改");
				finish();
			}
			else if(msg.what == HttpMsg.PWD_RESET_FAIL)
			{
				//修改密码失败
				String error = (String)msg.obj;
				if(TextUtils.isEmpty(error))
					error = "操作失败";
				showToast(error);
				
				tvConfirm.setText("确认修改");
				
				isRunning = false;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_pwd_edit);
		
		etOldPwd = (EditText)findViewById(R.id.et_pwd_edit_old);
		etNewPwd = (EditText)findViewById(R.id.et_pwd_edit_new);
		etPwdConfirm = (EditText)findViewById(R.id.et_pwd_edit_confirm);
		tvConfirm = (TextView)findViewById(R.id.tv_pwd_edit_confirm);
		
		tvConfirm.setOnClickListener(this);
		
		setCenterString("修改密码");
		
		httpFactory = new HttpFactory(getActivity(), handler);
		
		etPwdConfirm.setOnEditorActionListener(new OnEditorActionListener()
		{
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
			{
				if ((actionId == 0 || actionId == 2) && event != null) 
				{
					resetPwd();
				}
				// TODO Auto-generated method stub
				return false;
			}
		});
	}
	
	private void resetPwd()
	{
		if(isRunning)
			return;
		String oldPwd = etOldPwd.getText().toString();
		String newPwd = etNewPwd.getText().toString();
		String newPwdConfirm = etPwdConfirm.getText().toString();
		httpFactory.resetPwd(oldPwd, newPwd, newPwdConfirm);
	}
	
	/**
	*callbacks
	*/
	@Override
	protected void onDestroy() 
	{
		// TODO Auto-generated method stub
		super.onDestroy();
		if(httpFactory != null)
			httpFactory.stopHttp();
	}
	
	@Override
	public void onClick(View v) 
	{
		// TODO Auto-generated method stub
		if(v == tvConfirm)
		{
			resetPwd();
		}
	}
	
}
