
package com.znt.diange.activity; 

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.znt.diange.R;
import com.znt.diange.http.HttpHelper;
import com.znt.diange.http.HttpMsg;
import com.znt.diange.http.HttpType;
import com.znt.diange.mina.cmd.DeviceInfor;

/** 
 * @ClassName: DeviceEditActivity 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-8-7 下午5:49:30  
 */
public class AddAdminActivity extends BaseActivity implements OnClickListener
{
	private EditText etPhoneNum = null;
	private TextView tvConfirm = null;
	
	private HttpHelper httpHelper = null;
	private DeviceInfor deviceInfor = null;
	private final int CHECK_SSID = 1;
	private final int CHECK_DEVICE = 2;
	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == HttpMsg.ADMIN_APPLY_START)
			{
				showProgressDialog(getActivity(), "正在申请管理员权限...");
			}
			else if(msg.what == HttpMsg.ADMIN_APPLY_SUCCESS)
			{
				showToast("操作成功");
				dismissDialog();
				finish();
			}
			else if(msg.what == HttpMsg.ADMIN_APPLY_FAIL)
			{
				String error = (String) msg.obj;
				if(TextUtils.isEmpty(error))
					error = "管理员权限申请失败";
				showToast(error);
				dismissDialog();
			}
		};
	};
	
	/**
	*callbacks
	*/
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_add_admin);
		
		setCenterString("管理员配置");
		
		etPhoneNum = (EditText)findViewById(R.id.et_add_admin_num);
		tvConfirm = (TextView)findViewById(R.id.btn_add_admin_confirm);
		
		tvConfirm.setOnClickListener(this);
		
		deviceInfor = (DeviceInfor)getIntent().getSerializableExtra("DEVICE_INFOR");
		
	}
	
	private void startAdminApply(String terminalId)
	{
		String id = getLocalData().getUserInfor().getUserId();
		String phone = etPhoneNum.getText().toString().trim();
		
		if(TextUtils.isEmpty(phone))
			return;
		/*if(TextUtils.isEmpty(uid))
			return;*/
		httpHelper = new HttpHelper(handler, getActivity());
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("terminalId", terminalId));
        params.add(new BasicNameValuePair("phone", phone ));
        params.add(new BasicNameValuePair("id", id));
        
        httpHelper.startHttp(HttpType.AdminApply, params);
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
			startAdminApply(deviceInfor.getCode());
		}
	}
}
 
