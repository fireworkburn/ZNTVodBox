/*  
* @Project: ZNTVodBox 
* @User: Administrator 
* @Description: 家庭音乐
* @Author： yan.yu
* @Company：http://www.zhunit.com/
* @Date 2016-9-29 上午12:21:20 
* @Version V1.1   
*/ 

package com.znt.vodbox.activity; 

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.znt.diange.mina.entity.UserInfor;
import com.znt.vodbox.R;
import com.znt.vodbox.entity.Constant;
import com.znt.vodbox.factory.HttpFactory;
import com.znt.vodbox.http.HttpMsg;
import com.znt.vodbox.utils.ViewUtils;

/** 
 * @ClassName: UserRecordActivity 
 * @Description: TODO
 * @author yan.yu 
 * @date 2016-9-29 上午12:21:20  
 */
public class UserRecordActivity extends BaseActivity
{
	private TextView tvRecord1 = null;
	private TextView tvRecord2 = null;
	private TextView tvRecord3 = null;
	
	private UserInfor user1 = null;
	private UserInfor user2 = null;
	private UserInfor user3 = null;
	private UserInfor userInfor = null;
	private HttpFactory httpFactory = null;
	
	Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == HttpMsg.LOGIN_START)//登陆开始
			{
				showProgressDialog(getActivity(), "正在切换账户，请稍后...");
			}
			else if(msg.what == HttpMsg.LOGIN_SUCCESS)//登陆成功
			{
				MyApplication.isLogin = true;
				Constant.isShopUpdated = true;
				Constant.isAlbumUpdated = true;
				UserInfor tempInfor = (UserInfor)msg.obj;
				tempInfor.setAccount(userInfor.getAccount());
				tempInfor.setPwd(userInfor.getPwd());
				
				getLocalData().setUserInfor(tempInfor);
				userInfor = tempInfor;
				dismissDialog();
				setResult(0);
				finish();
			}
			else if(msg.what == HttpMsg.LOGIN_FAIL)//登陆失败
			{
				String error = (String)msg.obj;
				if(TextUtils.isEmpty(error))
					error = "登录失败";
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
		
		
		setContentView(R.layout.activity_user_record);
		
		setCenterString("切换账户");
		
		tvRecord1 = (TextView)findViewById(R.id.tv_user_record_1);
		tvRecord2 = (TextView)findViewById(R.id.tv_user_record_2);
		tvRecord3 = (TextView)findViewById(R.id.tv_user_record_3);
		
		httpFactory = new HttpFactory(getActivity(), handler);
		
		user1 = getLocalData().getUser1();
		user2 = getLocalData().getUser2();
		user3 = getLocalData().getUser3();
		if(!TextUtils.isEmpty(user1.getUserName()))
			tvRecord1.setText(user1.getUserName());
		else
			tvRecord1.setVisibility(View.GONE);
		if(!TextUtils.isEmpty(user2.getUserName()))
			tvRecord2.setText(user2.getUserName());
		else
			tvRecord2.setVisibility(View.GONE);
		if(!TextUtils.isEmpty(user3.getUserName()))
			tvRecord3.setText(user3.getUserName());
		else
			tvRecord3.setVisibility(View.GONE);
		
		tvRecord1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				userInfor = user1;
				if(isCurCanLogin(user1))
					login();
				else
					showToast("该账户已登录");
			}
		});
		tvRecord2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				userInfor = user2;
				if(isCurCanLogin(user2))
					login();
				else
					showToast("该账户已登录");
			}
		});
		tvRecord3.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				userInfor = user3;
				if(isCurCanLogin(user3))
					login();
				else
					showToast("该账户已登录");
			}
		});
		
	}
	
	private void login()
	{
		httpFactory.login(userInfor.getAccount(), userInfor.getPwd());
	}
	
	private boolean isCurCanLogin(UserInfor infor)
	{
		String name = getLocalData().getUserName();
		if(name.equals(infor.getUserName()) && MyApplication.isLogin)
			return false;
		return true;
	}
	
}
 
