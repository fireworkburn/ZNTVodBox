package com.znt.vodbox.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.znt.vodbox.R;
import com.znt.vodbox.factory.HttpFactory;
import com.znt.vodbox.http.HttpMsg;
import com.znt.vodbox.utils.SystemUtils;

public class UserInforEditAct extends BaseActivity implements OnClickListener
{

	
	private TextView tvLabel = null;
	private EditText etContent = null;
	private TextView tvConfirm = null;
	private View bgView = null;
	
	private HttpFactory httpFactory = null;
	
	private String content = "";
	
	Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == HttpMsg.NO_NET_WORK_CONNECT)
			{
				showToast("无网络连接");
			}
			else if(msg.what == HttpMsg.USER_INFOR_EDIT_START)
			{
				//修改昵称
			}
			else if(msg.what == HttpMsg.USER_INFOR_EDIT_SUCCESS)
			{
				//修改昵称成功
				
				SystemUtils.hideInputView(getActivity());
				
				showToast("修改成功");
				
				Intent intent = new Intent();
				intent.putExtra("NICK_NAME", etContent.getText().toString().trim());
				setResult(-1, intent);
				finish();
				
			}
			else if(msg.what == HttpMsg.USER_INFOR_EDIT_FAIL)
			{
				//修改昵称失败
				String error = (String)msg.obj;
				if(TextUtils.isEmpty(error))
					error = "修改失败";
				showToast(error);
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_user_infor_edit);
		
		tvLabel = (TextView)findViewById(R.id.tv_user_infor_edit_label);
		etContent = (EditText)findViewById(R.id.et_user_infor_edit_content);
		tvConfirm = (TextView)findViewById(R.id.tv_dialog_user_infor_edit_confirm);
		bgView = findViewById(R.id.view_user_infor_edit_bg);
		
		tvConfirm.setOnClickListener(this);
		bgView.setOnClickListener(this);
		
		content = getIntent().getStringExtra("CONTENT");
		etContent.setText(content);
		etContent.setSelection(etContent.length());
		
		setCenterString("修改昵称");
		
		httpFactory = new HttpFactory(getActivity(), handler);
		
		etContent.setOnEditorActionListener(new OnEditorActionListener()
		{
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
			{
				if ((actionId == 0 || actionId == 2) && event != null) 
				{
					doNameEdit();
				}
				// TODO Auto-generated method stub
				return false;
			}
		});
		
	}
	
	private void doNameEdit()
	{
		String tempStr = etContent.getText().toString().trim();
		
		if(TextUtils.isEmpty(tempStr))
		{
			showToast("请输入昵称~");
			return;
		}
		
		if(content.equals(tempStr))
		{
			showToast("昵称未改变~");
		}
		else
			httpFactory.editUserInfor(tempStr);
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
	
	/**
	*callbacks
	*/
	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		if(v == tvConfirm)
		{
			doNameEdit();
		}
		else if(v == bgView)
		{
			SystemUtils.hideInputView(getActivity());
		}
	}
	
	
}
