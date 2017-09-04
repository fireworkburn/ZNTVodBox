
package com.znt.diange.dmc.engine; 

import android.app.Activity;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Toast;

import com.znt.diange.R;
import com.znt.diange.activity.NetWorkChangeActivity;
import com.znt.diange.dialog.MyAlertDialog;
import com.znt.diange.dialog.MyProgressDialog;
import com.znt.diange.dlna.mediaserver.util.LogFactory;
import com.znt.diange.entity.LocalDataEntity;
import com.znt.diange.mina.client.MinaClient;
import com.znt.diange.mina.client.MinaClient.OnConnectListener;
import com.znt.diange.netset.WifiFactory;
import com.znt.diange.utils.SystemUtils;
import com.znt.diange.utils.ViewUtils;

/** 
 * @ClassName: OnConnectHandler 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-9-11 下午4:17:03  
 */
public class OnConnectHandler implements OnConnectListener
{
	private Activity activity = null;
	private Handler handler = null;
	
	public final static int ON_NETWORK_RECONNECTED_SUCCESS = 3001;
	public final static int ON_NETWORK_RECONNETC_FAIL = 3002;
	
	public OnConnectHandler(Activity activity, Handler handler)
	{
		this.activity = activity;
		this.handler = handler;
	}
	
	public Handler getHandler()
	{
		return handler;
	}
	
	private boolean isWifiNameError()
	{
		boolean isWifiNameError = false;
		
		String ssid = getCurWifissid();
		String localSsid = getLocalssid();
		
		boolean isHasLocalWifi = WifiFactory.newInstance(getActivity()).getWifiAdmin().ifHasWifi(localSsid);
		
		if(!TextUtils.isEmpty(localSsid) && ssid.equals(localSsid) && isHasLocalWifi)
		{
			isWifiNameError = false;
		}
		return isWifiNameError;
	}
	
	private String getCurWifissid()
	{
		String ssid = SystemUtils.getConnectWifiSsid(getActivity());
		
		return ssid;
	}
	private String getLocalssid()
	{
		return LocalDataEntity.newInstance(getActivity()).getWifiName();
	}
	
	private void showWorkErrorAlertDialog()
	{
		MinaClient.getInstance().stopConnect();
		if(handler != null)
			ViewUtils.sendMessage(handler, ON_NETWORK_RECONNETC_FAIL);
		
		showAlertDialog(activity, new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				// TODO Auto-generated method stub
				if(isWifiNameError())
				{
					if(WifiFactory.newInstance(getActivity()).getWifiAdmin().ifHasWifi(getLocalssid()))
					{
						//当前网络列表中有该网络就进入网络配置页面
						gotoNetworkChangePage();
					}
					else //提示用户没有该网络
					{
						showToast("未检测到该网络，请确认网络已经打开");
					}
				}
				else
					dismisDialog();
			}
		}, null, "手机与音响网络不一致，请切换网络");
	}
	
	private void gotoNetworkChangePage()
	{
		ViewUtils.startActivity(activity, NetWorkChangeActivity.class, null);
	}
	
	private void dismisDialog()
	{
		if(myAlertDialog != null)
			myAlertDialog.dismiss();
		else if(mProgressDialog != null)
			mProgressDialog.dismiss();
	}
	
	private void showToast(String infor)
	{
		Toast.makeText(getActivity(), infor, 0).show();
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onConnectting(final int connectTime)
	{
		// TODO Auto-generated method stub
		LogFactory.createLog().e(activity.getResources().getString(R.string.connect_server_doing) + connectTime +"  次");
		/*activity.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				if(isWifiNameError())
				{
					//显示网络错误对话框
					dismisDialog();
					showWorkErrorAlertDialog();
				}
				else
					activity.showProgressDialog(activity, 
							activity.getResources().getString(R.string.connect_server_doing) + connectTime +"  次");
			}
		});*/
	}

	/**
	*callbacks
	*/
	@Override
	public void onConnectted()
	{
		// TODO Auto-generated method stub
		activity.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				dismisDialog();
				if(handler != null)
					ViewUtils.sendMessage(handler, ON_NETWORK_RECONNECTED_SUCCESS);
			}
		});
	}

	/**
	*callbacks
	*/
	@Override
	public void onConnectFail(final boolean isDeviceRemove)
	{
		// TODO Auto-generated method stub
		activity.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				dismisDialog();
				if(handler != null)
					ViewUtils.sendMessage(handler, ON_NETWORK_RECONNETC_FAIL, isDeviceRemove);
				if(isDeviceRemove)
				{
					showToast(activity.getResources().getString(R.string.device_disconnected));
				}
				else
					showToast(activity.getResources().getString(R.string.connect_server_fail));
			}
		});
	}
	
	private Activity getActivity()
	{
		return activity;
	}
	/**
	*callbacks
	*/
	@Override
	public void onConnectStart()
	{
		// TODO Auto-generated method stub
		activity.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				if(isWifiNameError())
				{
					//显示网络错误对话框
					dismisDialog();
					showWorkErrorAlertDialog();
				}
				else
					showProgressDialog(activity, 
							activity.getResources().getString(R.string.connect_server_doing));
			}
		});
	}
	
	private MyAlertDialog myAlertDialog = null;
	private final void showAlertDialog(Activity activity, OnClickListener listener, String title,
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
	
	private MyProgressDialog mProgressDialog = null;
	private final void showProgressDialog(Activity activity,
			String message) 
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
}
 
