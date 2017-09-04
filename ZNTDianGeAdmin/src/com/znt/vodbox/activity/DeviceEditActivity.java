
package com.znt.vodbox.activity; 

import java.io.File;
import java.util.List;

import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.znt.vodbox.R;
import com.znt.diange.mina.cmd.CmdType;
import com.znt.diange.mina.cmd.DeviceInfor;
import com.znt.diange.mina.cmd.DeviceSetCmd;
import com.znt.vodbox.db.DBManager;
import com.znt.vodbox.dialog.WifiListDialog;
import com.znt.vodbox.dmc.engine.OnConnectHandler;
import com.znt.vodbox.entity.Constant;
import com.znt.vodbox.entity.LocalDataEntity;
import com.znt.vodbox.factory.DiangeManger;
import com.znt.vodbox.factory.HttpFactory;
import com.znt.vodbox.http.HttpMsg;
import com.znt.vodbox.mina.client.ClientHandler;
import com.znt.vodbox.mina.client.MinaClient;
import com.znt.vodbox.mina.client.ClientHandler.MinaErrorType;
import com.znt.vodbox.netset.WifiFactory;
import com.znt.vodbox.timer.CheckSsidTimer;
import com.znt.vodbox.timer.SearchDeviceTimer;
import com.znt.vodbox.utils.FileUtils;
import com.znt.vodbox.utils.ViewUtils;

/** 
 * @ClassName: DeviceEditActivity 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-8-7 下午5:49:30  
 */
public class DeviceEditActivity extends BaseActivity implements OnClickListener
{
	private EditText etDeviceName = null;
	private EditText etDeviceId = null;
	private EditText etWifiName = null;
	private EditText etWifiPwd = null;
	private EditText etActcode = null;
	
	private TextView tvFtpAddr = null;
	
	private TextView btnConfirm = null;
	private TextView tvHint = null;
	
	private HttpFactory httpFactory = null;
	
	private WifiListDialog mWifiListDialog;
	
	private List<ScanResult> mWifiList = null;
	private ScanResult mSelectedScanResult;
	
	private DeviceInfor localDeviceInfor = null;
	private CheckSsidTimer checkSsidTimer = null;
	private SearchDeviceTimer searchDeviceTimer = null;
	private DiangeManger mDiangeManger = null;
	
	private int searchTime = 0;
	private int adminAplayCount = 0;
	private boolean isRunning = false;
	private boolean isWifiSetted = false;
	private String connectSSID = "";
	
	private String curSSID = "";
	private String curPwd = "";
	private String curName = "";
	private String oldSSID = "";
	private String oldPwd = "";
	private String oldName = "";
	private String terminalCode = "";
	
	private final int CHECK_SSID = 1;
	private final int CHECK_DEVICE = 2;
	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == ClientHandler.RECV_DEVICE_EDIT_RESULT)
			{
				DeviceSetCmd deviceSetCmd = (DeviceSetCmd)msg.obj;
				
				if(deviceSetCmd.getCmdType().equals(CmdType.SET_DEVICE_FB))
				{
					connectCurWifi();
				}
			}
			else if(msg.what == ClientHandler.MINA_CONNECT_ERROR)
			{
				MinaErrorType type = (MinaErrorType)msg.obj;
				isRunning = false;
			}
			else if(msg.what == OnConnectHandler.ON_NETWORK_RECONNECTED_SUCCESS)
			{
				isRunning = false;
				sendDeviceSetCmd();
			}
			else if(msg.what == OnConnectHandler.ON_NETWORK_RECONNETC_FAIL)
			{
				isRunning = false;
				dismissDialog();
				
				boolean isDeviceRemove = (Boolean)msg.obj;
				if(isDeviceRemove)
				{
					showToast("设备断开连接，请重新选择设备");
				}
				else
					showToast("连接服务器失败，请重试");
			}
			else if(msg.what == ClientHandler.TIME_OUT)
			{
				dismissDialog();
				isRunning = false;
				showToast(getResources().getString(R.string.request_time_out));
			}
			else if(msg.what == CHECK_SSID)
			{
				if(checkSsidTimer.isOver())
				{
					doNetworkConnectFail();
					searchTime = 0;
				}
				else
				{
					connectSSID = getCurrentSsid();
					if(curSSID.equals(connectSSID))
					{
						stopCheckSsid();
						
						doNetworkConnectSuccess();

						//startCheckDevice();
					}
				}
			}
			else if(msg.what == CHECK_DEVICE)
			{
				if(searchDeviceTimer.isOver())
				{
					showToast("搜索设备失败，请检查网络是否配置正确");
					dismissDialog();
				}
				else
				{
					if(mDiangeManger.isDeviceFind(false))
					{
						doSearchDeviceSuccess();
					}
					else
					{
						searchTime ++;
						if(searchTime % 20 == 0)
						{
							//restartConnectDeviceService();
						}
					}
				}
			}
			else if(msg.what == ClientHandler.RECV_ERROR)
			{
				dismissDialog();
				String error = (String)msg.obj;
				if(error != null)
					showToast(error);
			}
			else if(msg.what == HttpMsg.ADMIN_APPLY_START)
			{
				
			}
			else if(msg.what == HttpMsg.ADMIN_APPLY_SUCCESS)
			{
				applyDeviceSuccess();
			}
			else if(msg.what == HttpMsg.ADMIN_APPLY_FAIL)
			{
				reapply();
			}
			else if(msg.what == HttpMsg.NO_NET_WORK_CONNECT)
			{
				reapply();
			}
		};
	};
	
	private void reapply()
	{
		adminAplayCount ++;
		if(adminAplayCount >= 15)
		{
			adminAplayCount = 0;
			doNetworkConnectFail();
		}
		else
		{
			handler.postDelayed(new Runnable() 
			{
				@Override
				public void run() 
				{
					// TODO Auto-generated method stub
					startApplyAdmin();
				}
			}, 3000);
		}
	}
	
	private void doNetworkConnectSuccess()
	{
		isWifiSetted = true;
		
		DeviceInfor tempDevice = getDeviceInforByEdit();
		getLocalData().setWifiInfor(tempDevice.getWifiName(), tempDevice.getWifiPwd());
		getLocalData().setDeviceInfor(tempDevice);
		DBManager.newInstance(getActivity()).insertDevice(tempDevice);
		
		if(TextUtils.isEmpty(etActcode.getText().toString()))
			startApplyAdmin();
		else
			applyDeviceSuccess();
		//resetDevices();
	}
	private void doNetworkConnectFail()
	{
		dismissDialog();
		showToast("设备配置失败，请重试");
	}
	private void applyDeviceSuccess()
	{
		Constant.isShopUpdated = true;
		Constant.isAlbumUpdated = true;
		showToast("配置成功");
		dismissDialog();
		setResult(RESULT_OK);
		finish();
	}
	/**
	*callbacks
	*/
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_device_edit);
		
		setCenterString("设备配置");
		
		etDeviceName = (EditText)findViewById(R.id.et_device_edit_name);
		etDeviceId = (EditText)findViewById(R.id.et_device_edit_id);
		etWifiName = (EditText)findViewById(R.id.et_device_edit_wifi_ssid);
		etWifiPwd = (EditText)findViewById(R.id.et_device_edit_wifi_pwd);
		etActcode = (EditText)findViewById(R.id.et_device_edit_actcode);
		tvFtpAddr = (TextView)findViewById(R.id.tv_device_ftp_addr);
		btnConfirm = (TextView)findViewById(R.id.btn_device_edit_confirm);
		tvHint = (TextView)findViewById(R.id.tv_device_edit_hint);
		btnConfirm.setVisibility(View.GONE);
		btnConfirm.setOnClickListener(this);
		etWifiName.setOnClickListener(this);
		
		showRightView(true);
		setRightText("确定");
		showRightImageView(false);
		getRightView().setOnClickListener(this);
		
		mDiangeManger = new DiangeManger(getActivity());
		
		checkSsidTimer = new CheckSsidTimer(getActivity());
		checkSsidTimer.setHandler(handler, CHECK_SSID);
		
		searchDeviceTimer = new SearchDeviceTimer(getActivity());
		searchDeviceTimer.setHandler(handler, CHECK_DEVICE);
		
		String ftpAddr = "";
		String ip = "";
		if(!isWifiApConnect())
			ip = getLocalData().getDeviceIp();
		else
			ip = "192.168.43.1";
		ftpAddr = "ftp://" + ip + ":2121";
		tvFtpAddr.setText(ftpAddr);
		saveFtpExplorer(ip);
		initData();
		
	}
	
	private void startApplyAdmin()
	{
		if(httpFactory == null)
			httpFactory = new HttpFactory(getActivity(), handler);
		if(terminalCode.endsWith(Constant.UUID_TAG))
			httpFactory.startAdminApply(terminalCode);
		else
			ViewUtils.sendMessage(handler, HttpMsg.ADMIN_APPLY_FAIL);
	}
	
	private void saveFtpExplorer(String ip)
	{
		String ftpFilePath = Environment.getExternalStorageDirectory() + Constant.WORK_DIR;
		File ftpFile = new File(ftpFilePath, "人人点歌.bat");
		if(ftpFile.exists())
			ftpFile.delete();
		String content = "explorer ftp://znt:znt@" + ip + ":2121";
		FileUtils.writeDataToFile(ftpFile.getAbsolutePath(), content);
	}
	
	private void doSearchDeviceSuccess()
	{
		stopCheckDevice();
		
		MinaClient.getInstance().startClient();
		
		getLocalData().setDeviceInfor(devInfroEdit);
		
		dismissDialog();
		setResult(RESULT_OK);
		finish();
	}
	
	private void startCheckDevice()
	{
		showProgressDialog(getActivity(), "正在连接设备...");
		
		searchDeviceTimer.setMaxTime(60);
		searchDeviceTimer.startTimer();
	}
	private void stopCheckDevice()
	{
		if(searchDeviceTimer != null)
			searchDeviceTimer.stopTimer();
	}
	
	private void showHint(String hint)
	{
		tvHint.setText(hint);
	}
	
	/**
	*callbacks
	*/
	@Override
	protected void onResume()
	{
		MinaClient.getInstance().setOnConnectListener(getActivity(), handler);
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	/**
	*callbacks
	*/
	@Override
	protected void onPause()
	{
		
		MinaClient.getInstance().setConnectStop();
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	private void initData()
	{
		localDeviceInfor = LocalDataEntity.newInstance(this).getDeviceInfor();
		
		etDeviceName.setText(localDeviceInfor.getName());
		String ssidTemp = getCurrentSsid();
		if(TextUtils.isEmpty(ssidTemp))
			ssidTemp = localDeviceInfor.getWifiName();
		etWifiName.setText(ssidTemp);
		etWifiPwd.setText(localDeviceInfor.getWifiPwd());
		etActcode.setText(localDeviceInfor.getActCode());
		
		etDeviceId.setText(localDeviceInfor.getId());
		
		oldSSID = ssidTemp;
		oldPwd = localDeviceInfor.getWifiPwd();
		oldName = localDeviceInfor.getName();
		
	}
	
	private void startCheckSSID()
	{
		Constant.deviceInfor = null;
		getLocalData().clearDeviceInfor();
		
		checkSsidTimer.setMaxTime(30);
		checkSsidTimer.startTimer();
		searchTime = 0;
	}
	private void stopCheckSsid()
	{
		if(checkSsidTimer != null)
			checkSsidTimer.stopTimer();
	}
	
	/**
	*callbacks
	*/
	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	
	private void scanWifi()
	{
		WifiFactory.newInstance(getActivity()).getWifiAdmin().startScan();
		mWifiList = WifiFactory.newInstance(getActivity()).getWifiAdmin().getWifiList();
	}
	
	private void showWifiListDialog()
	{
		scanWifi();
		
		if(mWifiList != null && mWifiList.size() > 0)
		{
			if(mWifiListDialog != null && mWifiListDialog.isShowing())
			{
				mWifiListDialog.dismiss();
				mWifiListDialog = null;
			}
			mWifiListDialog = new WifiListDialog(this);
			mWifiListDialog.showWifiDialog( mWifiList, curSSID, mOnItemClickListener);
		}
		else
		{
			showToast("未检测到网络，请先打开WIFI");
			ViewUtils.startNetWorkSet(getActivity());
		}
	}
	
	private OnItemClickListener mOnItemClickListener = new OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id)
		{
			// TODO Auto-generated method stub
			mWifiListDialog.dismiss();
			mSelectedScanResult = mWifiList.get(position);
			curSSID = mSelectedScanResult.SSID;
			etWifiName.setText(curSSID);
			if(!curSSID.equals(oldSSID))
				etWifiPwd.setText("");
			else
				etWifiPwd.setText(oldPwd);
		}
	};
	
	private DeviceInfor getDeviceInforByEdit()
	{
		
		String devName = etDeviceName.getText().toString().trim();
		if(devName.contains(" "))
			devName = devName.replace(" ", "");
		String devId = etDeviceId.getText().toString().trim();
		String wifiName = etWifiName.getText().toString().trim();
		String wifiPwd = etWifiPwd.getText().toString().trim();
		String actCode = etActcode.getText().toString().trim();
		
		DeviceInfor deviceInfor = new DeviceInfor();
		deviceInfor.setName(devName);
		deviceInfor.setId(devId);
		deviceInfor.setWifiName(wifiName);
		deviceInfor.setWifiPwd(wifiPwd);
		deviceInfor.setActCode(actCode);
		return deviceInfor;
	}
	
	private DeviceInfor devInfroEdit = null; 
	private void doSystemSet()
	{
		
		curSSID = etWifiName.getText().toString().trim();
		curPwd = etWifiPwd.getText().toString().trim();
		curName = etDeviceName.getText().toString().trim();
		
		if(curPwd.contains(" "))
			curPwd.replace(" ", "");
		
		if(oldName.equals(curName) && oldSSID.equals(curSSID) && oldPwd.equals(curPwd))
		{
			showToast("信息未更改");
			return;
		}
		
		//已经连接当前网络
		devInfroEdit = getDeviceInforByEdit();
		devInfroEdit.setWifiName(curSSID);
		devInfroEdit.setWifiPwd(curPwd);
		
		if(isRunning)
			return;
		
		if(!MinaClient.getInstance().isConnected())
		{
			//showToast("未连接服务器，正在重连");
			MinaClient.getInstance().reConnect(getActivity());
			return;
		}
		else
		{
			isRunning = true;
			sendDeviceSetCmd();
		}
	}
	
	private void sendDeviceSetCmd()
	{
		showProgressDialog(getActivity(), "正在配置...");
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				boolean result = MinaClient.getInstance().sendDeviceSet(getActivity(), devInfroEdit);
				if(result)
				{
					runOnUiThread(new Runnable()
					{
						@Override
						public void run()
						{
							// TODO Auto-generated method stub
						}
					});
				}
			}
		}).start();
	}
	
	private void sendGetRecordCmd()
	{
		final int pageNum = 0;
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				boolean result = MinaClient.getInstance().sendGetRecordList(getActivity(), pageNum);
				if(result)
				{
					runOnUiThread(new Runnable()
					{
						@Override
						public void run()
						{
							// TODO Auto-generated method stub
							
						}
					});
				}
			}
		}).start();
	}
	
	private void connectCurWifi()
	{
		
		String curConnectSsid = getCurrentSsid();
		//连接当前网络
		if(TextUtils.isEmpty(curConnectSsid) || !curConnectSsid.equals(curSSID))
		{
			showProgressDialog(getActivity(), "正在配置设备，请耐心等待...");
			WifiFactory.newInstance(getActivity()).connectWifi(curSSID, curPwd);
			startCheckSSID();
		}
		else
		{
			doSearchDeviceSuccess();
		}
	}
	
	private void showWifiSetDialog()
	{
		showAlertDialog(getActivity(), new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
			}
		}, null, "请先连接设备的网络重试");
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		if(v == btnConfirm)
		{
			if(!isWifiSetted)
			{
				if(getCurrentSsid().endsWith(Constant.UUID_TAG))
				{
					terminalCode = getCurrentSsid();
					doSystemSet();
				}
				/*else
					showAlertDialog(getActivity(), listener, null, "请先连接设备的网络重试");*/
			}
			else
				startApplyAdmin();
			
			//sendConfig();
			//sendGetRecordCmd();
		}
		else if(v == getRightView())
		{
			if(!isWifiSetted)
			{
				if(!WifiFactory.newInstance(getActivity()).getWifiAdmin().isWifiEnabled())
				{
					showAlertDialog(getActivity(), new OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							ViewUtils.startNetWorkSet(getActivity());
							dismissDialog();
						}
					}, null, "WIFI未打开，去开启WIFI吗？");
					return;
				}
				if(getCurrentSsid().endsWith(Constant.UUID_TAG))
				{
					terminalCode = getCurrentSsid();
					doSystemSet();
				}
				else
				{
					//有WIFI热点
					String wifiApSsid = WifiFactory.newInstance(getActivity()).getWifiAdmin().ifHasWifiAp();
					if(!TextUtils.isEmpty(wifiApSsid))
					{
						startConnectHot(wifiApSsid);
					}
					else
						showHint("未检测到设备wifi，请检查设备是否已经连接wifi网络，或者重启设备");
				}
			}
			else
				startApplyAdmin();
		}
		else if(v == etWifiName)
		{
			showWifiListDialog();
		}
	}
	
	private void startConnectHot(final String wifiApSsid)
	{
		//当前连接的是热点就不提示
		if(getCurrentSsid().endsWith(Constant.UUID_TAG))
			return;
		showAlertDialog(getActivity(), new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				// TODO Auto-generated method stub
				DeviceInfor tempInfor = new DeviceInfor();
				tempInfor.setAvailable(true);
				tempInfor.setId(wifiApSsid);
				tempInfor.setWifiName(wifiApSsid);
				tempInfor.setWifiPwd("00000000");
				Bundle bundle = new Bundle();
				bundle.putSerializable("DEVICE_INFOR", tempInfor);
				ViewUtils.startActivity(getActivity(), NetWorkChangeActivity.class, bundle);
				dismissDialog();
				finish();
			}
		}, "设备网络发现提示", "发现设备网络，确认连接该设备吗？");
	}
}
 
