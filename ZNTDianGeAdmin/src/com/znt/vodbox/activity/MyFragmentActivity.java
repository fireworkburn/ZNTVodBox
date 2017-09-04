
package com.znt.vodbox.activity; 

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View.OnClickListener;
import android.view.WindowManager;

import com.znt.vodbox.R;
import com.znt.vodbox.dialog.MyAlertDialog;
import com.znt.vodbox.dialog.MyProgressDialog;
import com.znt.vodbox.entity.Constant;
import com.znt.vodbox.entity.LocalDataEntity;
import com.znt.vodbox.netset.WifiFactory;
import com.znt.vodbox.utils.MyToast;
import com.znt.vodbox.utils.SystemUtils;

/** 
 * @ClassName: MyFragmentActivity 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-7-29 下午4:51:25  
 */
public class MyFragmentActivity extends FragmentActivity
{

	private MyToast myToast = null;
	private LocalDataEntity localData = null;
	
	private static MyProgressDialog mProgressDialog;
	private static MyAlertDialog myAlertDialog = null;
	
	
	/**
	*callbacks
	*/
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		MyActivityManager.getScreenManager().pushActivity(this);
		localData = new LocalDataEntity(getActivity());
		
		myToast = new MyToast(getApplicationContext());
		
		/*requestWindowFeature(Window.FEATURE_NO_TITLE);
		setTitle(null);*/
	}
	
	/*public boolean isDeviceConnected()
	{
		if(DLNAContainer.getInstance().isSpeakerDeviceFind())
		{
			String localUuid = getLocalData().getDeviceId();
			if(DLNAContainer.getInstance().getSpeakerDeviceUuid().equals(localUuid))
			{
				return true;
			}
		}
		String localUuid = getLocalData().getDeviceId();
		if(DnsContainer.getInstance(getActivity()).isServcerFind())
		{
			if(getDeviceInforFromServer().getId().equals(localUuid))
			{
				return true;
			}
		}
		else if(DLNAContainer.getInstance().isSpeakerDeviceFind())
		{
			if(DLNAContainer.getInstance().getSpeakerDeviceUuid().equals(localUuid))
			{
				return true;
			}
		}
		else if(isWifiApConnect() && MinaClient.getInstance().isConnected())
			return true;
		return false;
	}*/
	
	/*public DeviceInfor getDeviceInforFromServer()
	{
		return DnsContainer.getInstance(getActivity()).getDeviceInfor();
	}*/
	
	public boolean isOnline()
	{
		if(!WifiFactory.newInstance(getActivity()).getWifiAdmin().isWifiEnabled())
			return false;
		
		//0,无网络连接  1，连接的是WIFI热点 2，正常连接
		boolean result = false;
		String curSSID = getCurrentSsid();
		if(TextUtils.isEmpty(curSSID))
		{
			result = false;
			showToast("无网络连接，请先联网!");
		}
		else if(curSSID.equals(getWifiHotName()))
		{
			result = false;
			showToast("当前网络不能访问外部数据~");
		}
		else
			result = true;
		
		return result;
	}
	
	public boolean isWifiApConnect()
	{
		String curSsid = getCurrentSsid();
		if(TextUtils.isEmpty(curSsid))
			return false;
		return curSsid.endsWith(Constant.UUID_TAG);
	}
	
	/**
	*callbacks
	*/
	@Override
	protected void onPause()
	{
		super.onPause();
	}
	/**
	*callbacks
	*/
	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	public boolean isOnline(boolean showInfor)
	{
		if(!WifiFactory.newInstance(getActivity()).getWifiAdmin().isWifiEnabled())
			return false;
		
		//0,无网络连接  1，连接的是WIFI热点 2，正常连接
		boolean result = false;
		String curSSID = getCurrentSsid();
		if(TextUtils.isEmpty(curSSID))
		{
			result = false;
			if(showInfor)
				showToast("无网络连接，请先联网!");
		}
		else if(curSSID.equals(getWifiHotName()))
		{
			result = false;
			if(showInfor)
				showToast("当前网络不能访问外部数据~");
		}
		else
			result = true;
		
		return result;
	}
	public String getWifiHotName()
	{
		return Constant.WIFI_HOT_NAME + localData.getDeviceInfor().getId();
	}
	
	/**
	*callbacks
	*/
	@Override
	protected void onDestroy()
	{
		MyActivityManager.getScreenManager().popActivity(this);
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		if(keyCode == KeyEvent.KEYCODE_BACK) 
		{
			dismissDialog();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public String getCurrentSsid()
	{
		return SystemUtils.getConnectWifiSsid(getActivity());
	}
	
	public static final void showProgressDialog(Context context, 
			String message) 
	{
		if (TextUtils.isEmpty(message)) 
		{
			message = "正在加载...";
		}
		if(mProgressDialog == null)
			mProgressDialog = new MyProgressDialog(context, R.style.Theme_CustomDialog);
		mProgressDialog.setInfor(message);
		
		if(!mProgressDialog.isShowing())
		{
			mProgressDialog.show();
			WindowManager windowManager = ((Activity) context).getWindowManager();
			Display display = windowManager.getDefaultDisplay();
			WindowManager.LayoutParams lp = mProgressDialog.getWindow().getAttributes();
			lp.width = (int)(display.getWidth()); //设置宽度
			lp.height = (int)(display.getHeight()); //设置高度
			mProgressDialog.getWindow().setAttributes(lp);
		}
	}
	
	public final void showProgressDialog(Activity activity, 
			String message, boolean isBackEnable) 
	{
		while (activity.getParent() != null) 
		{  
			activity = activity.getParent();  
		}  
		
		if (TextUtils.isEmpty(message)) 
		{
			message = "正在加载...";
		}
		if(mProgressDialog == null)
			mProgressDialog = new MyProgressDialog(activity, R.style.Theme_CustomDialog);
		mProgressDialog.setInfor(message);
		mProgressDialog.setBackEnable(isBackEnable);
		if(!mProgressDialog.isShowing())
		{
			mProgressDialog.show();
			WindowManager windowManager = (activity).getWindowManager();
			Display display = windowManager.getDefaultDisplay();
			WindowManager.LayoutParams lp = mProgressDialog.getWindow().getAttributes();
			lp.width = (int)(display.getWidth()); //设置宽度
			lp.height = (int)(display.getHeight()); //设置高度
			mProgressDialog.getWindow().setAttributes(lp);
		}
	}
	
	public final void showAlertDialog(Activity activity, OnClickListener listener, String title,
			String message) 
	{
		if (TextUtils.isEmpty(title)) 
		{
			title = "提示";
		}
		
		while (activity.getParent() != null) 
		{  
            activity = activity.getParent();  
        }  
		
		if(myAlertDialog == null || myAlertDialog.isDismissed())
			myAlertDialog = new MyAlertDialog(activity, R.style.Theme_CustomDialog);
		myAlertDialog.setInfor(title, message);
		if(myAlertDialog.isShowing())
			myAlertDialog.dismiss();
		myAlertDialog.show();
		myAlertDialog.setOnClickListener(listener);
		
		WindowManager windowManager = ((Activity) activity).getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = myAlertDialog.getWindow().getAttributes();
		lp.width = (int)(display.getWidth()); //设置宽度
		lp.height = (int)(display.getHeight()); //设置高度
		myAlertDialog.getWindow().setAttributes(lp);
	}
	
	public final void showAlertDialog(Activity activity, String title,
			String message) 
	{
		
		while (activity.getParent() != null) 
		{  
            activity = activity.getParent();  
        }  
		
		if (TextUtils.isEmpty(title)) 
		{
			title = "提示";
		}
		
		
		if(myAlertDialog == null || myAlertDialog.isDismissed())
			myAlertDialog = new MyAlertDialog(activity, R.style.Theme_CustomDialog);
		myAlertDialog.setInfor(title, message);
		if(myAlertDialog.isShowing())
			myAlertDialog.dismiss();
		myAlertDialog.show();
		WindowManager windowManager = ((Activity) activity).getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = myAlertDialog.getWindow().getAttributes();
		lp.width = (int)(display.getWidth()); //设置宽度
		lp.height = (int)(display.getHeight()); //设置高度
		myAlertDialog.getWindow().setAttributes(lp);
	}
	
	public final void showProgressDialog(Context context,
			String message, OnDismissListener listener) 
	{
		if (TextUtils.isEmpty(message)) 
		{
			message = "正在加载...";
		}
		if(mProgressDialog == null)
		{
			mProgressDialog = new MyProgressDialog(context, R.style.Theme_CustomDialog);
			mProgressDialog.setOnDismissListener(listener);
		}
		mProgressDialog.setInfor(message);
		
		if(!mProgressDialog.isShowing())
		{
			mProgressDialog.show();
			WindowManager windowManager = ((Activity) context).getWindowManager();
			Display display = windowManager.getDefaultDisplay();
			WindowManager.LayoutParams lp = mProgressDialog.getWindow().getAttributes();
			lp.width = (int)(display.getWidth()); //设置宽度
			lp.height = (int)(display.getHeight()); //设置高度
			mProgressDialog.getWindow().setAttributes(lp);
		}
	}

	public final void dismissDialog() 
	{
		if(getActivity() == null || getActivity().isFinishing())
			return;
		if (mProgressDialog != null && mProgressDialog.isShowing()) 
		{
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
		if (myAlertDialog != null && myAlertDialog.isShowing()) 
		{
			myAlertDialog.dismiss();
			myAlertDialog = null;
		}
	}
	
	public void closeAllActivity()
	{
		MyActivityManager.getScreenManager().popAllActivity();
	}
	
	public Activity getActivity()
	{
		return this;
	}
	
	public LocalDataEntity getLocalData()
	{
		return localData;
	}
	
	public void showToast(int res)
	{
		myToast.show(res);
	}
	public void showToast(String res)
	{
		myToast.show(res);
	}
	
}
 
