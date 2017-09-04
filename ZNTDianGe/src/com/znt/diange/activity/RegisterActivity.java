package com.znt.diange.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.znt.diange.R;
import com.znt.diange.factory.HttpFactory;
import com.znt.diange.http.HttpMsg;
import com.znt.diange.mina.entity.UserInfor;

public class RegisterActivity extends BaseActivity 
{

	private Button mBtnRegister;
	private Button mBtnCancel;
	private EditText etPhone;
	private EditText etPwd;
	private EditText etName;
	
	private String phoneNum;
	private UserInfor userInfor = null;
	private HttpFactory httpFactory = null;
	
	private boolean isRunning = false;
	
	private Handler handler = new Handler()
	{
		public void handleMessage(Message msg) 
		{
			if(msg.what == HttpMsg.REGISTER_START)//注册开始
			{
				//tvConfirm.setText("正在注册...");
				showProgressDialog(getActivity(), "正在注册...");
				isRunning = true;
			}
			else if(msg.what == HttpMsg.REGISTER_SUCCESS)//注册成功
			{

				dismissDialog();
				
				Object obj = msg.obj;
				if(obj instanceof String)
				{
					showToast((String)obj);
				}
				else if(obj instanceof UserInfor)
				{
					showToast("注册成功");
					userInfor = (UserInfor)obj;
					Intent intent = new Intent();
					intent.putExtra("phone", userInfor.getAccount());
					intent.putExtra("pwd", userInfor.getPwd());
					setResult(RESULT_OK,intent);
					finish();
				}
				//getLocalData().setUserInfor(userInfor);
				
				//loginByLocalData();
				isRunning = false;
			}
			else if(msg.what == HttpMsg.REGISTER_FAIL)//注册失败
			{
				
				dismissDialog();
				
				String error = (String)msg.obj;
				if(TextUtils.isEmpty(error))
					error = "注册失败";
				showToast(error);
				isRunning = false;
				//tvConfirm.setText("注册");
			}
		};
	};
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		initScrollView();
		initEditText();
		setBtnListener();
		
		httpFactory = new HttpFactory(getActivity(), handler);
	}
	
	private void initScrollView()
	{
		setContentView(R.layout.activity_register);
		setCenterString("用户注册");
		
		Bundle bundle = this.getIntent().getExtras();
		phoneNum = bundle.getString("phone");
		
	}
	
	private void initEditText() 
	{
		etPhone = (EditText) findViewById(R.id.register_phone);
		etPwd = (EditText) findViewById(R.id.register_psw);
		etName = (EditText) findViewById(R.id.register_user_name);
		
		etPhone.setText(phoneNum);
		
		etPwd.requestFocus();
		
		etPwd.setOnEditorActionListener(new OnEditorActionListener()
		{
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
			{
				if ((actionId == 0 || actionId == 2) && event != null) 
				{
					register();
				}
				// TODO Auto-generated method stub
				return false;
			}
		});
		etName.setOnEditorActionListener(new OnEditorActionListener()
		{
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
			{
				if ((actionId == 0 || actionId == 2) && event != null) 
				{
					register();
				}
				// TODO Auto-generated method stub
				return false;
			}
		});
	}

	private void setBtnListener() 
	{
		mBtnRegister = (Button) findViewById(R.id.btn_user_register);
		mBtnRegister.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) 
			{
				register();
			}
		});
		
		mBtnCancel = (Button) findViewById(R.id.btn_cancel);
		mBtnCancel.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) 
			{
				finish();
			}
		});
	}
	
	private void register()
	{
		if(isRunning)
			return;
		String phone = etPhone.getText().toString();
		String pwd = etPwd.getText().toString();
		String name = etName.getText().toString();
		
		if(TextUtils.isEmpty(pwd))
		{
			showToast("请输入登陆密码");
			return;
		}
		if(pwd.length() < 6)
		{
			showToast("密码最少六位");
			return;
		}
		
		if(TextUtils.isEmpty(name))
		{
			showToast("请输入昵称");
			return;
		}
		
		/*if(name.length() < 2)
		{
			showToast("昵称太短啦");
			return;
		}*/
		
		httpFactory.doRegisterByPhone(phone, pwd, name);
		
		/*Intent intent = new Intent();
		intent.putExtra("phone", etPhone.getText().toString());
		intent.putExtra("pwd", pwd);
		setResult(RESULT_OK, intent);
		finish();*/
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
}
