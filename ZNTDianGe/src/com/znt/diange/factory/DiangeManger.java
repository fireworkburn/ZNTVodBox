
package com.znt.diange.factory; 

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Toast;

import com.znt.diange.R;
import com.znt.diange.activity.AccountActivity;
import com.znt.diange.activity.MyApplication;
import com.znt.diange.activity.NetDeviceActivity;
import com.znt.diange.dialog.MyAlertDialog;
import com.znt.diange.dialog.MyProgressDialog;
import com.znt.diange.entity.Constant;
import com.znt.diange.entity.LocalDataEntity;
import com.znt.diange.mina.client.MinaClient;
import com.znt.diange.mina.cmd.DeviceInfor;
import com.znt.diange.netset.WifiFactory;
import com.znt.diange.utils.SystemUtils;
import com.znt.diange.utils.ViewUtils;

/** 
 * @ClassName: DiangeManger 
 * @Description: TODO
 * @author yan.yu 
 * @date 2016-3-21 下午11:07:16  
 */
public class DiangeManger 
{
	private Activity activity = null;
	
	public DiangeManger(Activity activity)
	{
		this.activity = activity;
	}
	
	private Activity getActivity()
	{
		return activity;
	}
	
	public boolean isDiangeEnable()
	{
		return isWifiConnect() && isDeviceFind(true) && isServerConnect() && isLogin();
	}
	
	public boolean isDeviceFind(boolean showAlert)
	{
		String localUuid = LocalDataEntity.newInstance(getActivity()).getDeviceId();
		if(MinaClient.getInstance().getDeviceInfor() != null)
		{
			if(TextUtils.isEmpty(localUuid) || 
					MinaClient.getInstance().getDeviceInfor().getId().equals(localUuid))
			{
				return true;
			}
			else 
				showAlert(showAlert);
		}
		else 
			showAlert(showAlert);
		return false;
	}
	
	private void showAlert(boolean showAlert)
	{
		if(showAlert)
			showAlertDialog(AlertType.Device);
	}
	
	public DeviceInfor getDeviceInfor()
	{
		return MinaClient.getInstance().getDeviceInfor();
	}
	
	public String getDeviceName()
	{
		if(getDeviceInfor() != null)
			return getDeviceInfor().getName();
		return "";
	}
	
	private boolean isWifiConnect()
	{
		String curSsid = getCurrentSsid();
		if(TextUtils.isEmpty(curSsid) || curSsid.contains("unknown"))
		{
			Toast.makeText(getActivity(), "请先连接店内WIFI网络", 0).show();
			return false;
		}
			
		return true;
	}
	
	private boolean isServerConnect()
	{
		
		if(MinaClient.getInstance().isConnected())
			return true;
		else
			showAlertDialog(AlertType.Server);
		
		return false;
	}
	
	private String getCurrentSsid()
	{
		if(!WifiFactory.newInstance(getActivity()).getWifiAdmin().isWifiEnabled())
			return "";
		String ssid = SystemUtils.getConnectWifiSsid(getActivity());
		if(TextUtils.isEmpty(ssid))
			return "";
		return ssid.replace("\"","");
	}
	
	private boolean isLogin()
	{
		if(MyApplication.isLogin)
			return true;
		else
			showAlertDialog(AlertType.Login);
		return false;
	}
	
	private MyAlertDialog myAlertDialog = null;
	public final void showAlertDialog(final AlertType alertType) 
	{
		if(myAlertDialog == null || myAlertDialog.isDismissed())
			myAlertDialog = new MyAlertDialog(activity, R.style.Theme_CustomDialog);
		if(alertType == AlertType.Device)
		{
			myAlertDialog.setInfor("提示", "未连接设备，请先选择一个设备");
		}
		else if(alertType == AlertType.Server)
		{
			myAlertDialog.setInfor("提示", "服务器断开连接，请先连接服务器");
		}
		else if(alertType == AlertType.Login)
		{
			myAlertDialog.setInfor("提示", "未登陆，请先登陆");
		}
		
		while (activity.getParent() != null) 
		{  
            activity = activity.getParent();  
        }  
		
		if(myAlertDialog.isShowing())
			myAlertDialog.dismiss();
		myAlertDialog.show();
		myAlertDialog.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				// TODO Auto-generated method stub
				if(alertType == AlertType.Device)
				{
					ViewUtils.startActivity(getActivity(), NetDeviceActivity.class, null);
				}
				else if(alertType == AlertType.Server)
				{
					MinaClient.getInstance().reConnect(getActivity());
					/*MinaClient.getInstance().setOnConnectListener(new OnConnectListener() {
						
						@Override
						public void onConnectting(final int connectTime) {
							// TODO Auto-generated method stub
							activity.runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									// TODO Auto-generated method stub
									showProgressDialog("正在连接服务器：" + connectTime + " 次");
								}
							});
						}
						
						@Override
						public void onConnectted() {
							// TODO Auto-generated method stub
							activity.runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									// TODO Auto-generated method stub
									dismissDialog();
									Toast.makeText(getActivity(), "连接成功，请重新操作", 0).show();
								}
							});
						}
						
						@Override
						public void onConnectFail(boolean isDeviceRemove) {
							// TODO Auto-generated method stub
							activity.runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									// TODO Auto-generated method stub
									dismissDialog();
									Toast.makeText(getActivity(), "连接失败", 0).show();
								}
							});
						}
					});*/
				}
				else if(alertType == AlertType.Login)
				{
					Bundle bundle = new Bundle();
					bundle.putBoolean("INIT", false);
					ViewUtils.startActivity(getActivity(), AccountActivity.class, bundle, 1);
				}
				dismissDialog();
			}
		});
		
		WindowManager windowManager = ((Activity) activity).getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = myAlertDialog.getWindow().getAttributes();
		lp.width = (int)(display.getWidth()); //设置宽度
		lp.height = (int)(display.getHeight()); //设置高度
		myAlertDialog.getWindow().setAttributes(lp);
	}
	private MyProgressDialog mProgressDialog = null;
	public final void showProgressDialog(String message) 
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
	
	private void dismissDialog()
	{
		if(myAlertDialog != null && myAlertDialog.isShowing())
			myAlertDialog.dismiss();
		if(mProgressDialog != null && mProgressDialog.isShowing())
			mProgressDialog.dismiss();
	}
	
	
	private enum AlertType
	{
		Device,
		Server,
		Login
	}
}
 
