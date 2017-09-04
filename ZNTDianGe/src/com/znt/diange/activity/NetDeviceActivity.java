
package com.znt.diange.activity; 

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.znt.diange.R;
import com.znt.diange.dmc.engine.OnConnectHandler;
import com.znt.diange.entity.Constant;
import com.znt.diange.factory.HttpFactory;
import com.znt.diange.factory.LocationFactory;
import com.znt.diange.http.HttpMsg;
import com.znt.diange.mina.client.MinaClient;
import com.znt.diange.mina.cmd.DeviceInfor;
import com.znt.diange.netset.WifiFactory;
import com.znt.diange.utils.SystemUtils;
import com.znt.diange.utils.ViewUtils;
import com.znt.diange.view.DeviceAdapter;
import com.znt.diange.view.listview.LJListView;
import com.znt.diange.view.listview.LJListView.IXListViewListener;

/** 
 * @ClassName: NetDeviceActivity 
 * @Description: TODO
 * @author yan.yu 
 * @date 2016-3-23 上午11:44:57  
 */
public class NetDeviceActivity extends BaseActivity implements IXListViewListener, OnItemClickListener
{

	private LJListView listView = null;
	private TextView tvHint = null;
	
	private DeviceAdapter deviceAdapter = null;
	private List<DeviceInfor> deviceList = new ArrayList<DeviceInfor>();
	private HttpFactory httpFactory = null;
	private LocationFactory locationFactory = null;
	
	private DeviceInfor oldDeviceInfor = null;
	
	private boolean isAddAdmin = false;
	private boolean isRunning = false;
	private boolean isChangeDevice = false;
	
	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == HttpMsg.GET_ALL_SPEAKER_START)
			{
				isRunning = true;
			}
			else if(msg.what == HttpMsg.GET_ALL_SPEAKER_SUCCESS)
			{
				List<DeviceInfor> tempList = (List<DeviceInfor>)msg.obj;
				
				if(tempList.size() == 0)
				{
					showHint("您所在的区域没有设备");
					onLoad(0);
				}
				else
				{
					hideHint();
					deviceList.clear();
					deviceList.addAll(tempList);
					deviceAdapter.notifyDataSetChanged();
					onLoad(0);
				}
				isRunning = false;
			}
			else if(msg.what == HttpMsg.GET_ALL_SPEAKER_FAIL)
			{
				isRunning = false;
				onLoad(0);
				showHint("获取数据失败");
				showToast("获取数据失败，请下拉刷新");
			}
			if(msg.what == HttpMsg.GET_NEAR_BY_SPEAKER_START)
			{
				isRunning = true;
			}
			else if(msg.what == HttpMsg.GET_NEAR_BY_SPEAKER_SUCCESS)
			{
				List<DeviceInfor> tempList = (List<DeviceInfor>)msg.obj;
				
				if(tempList.size() == 0)
				{
					showHint("您所在的区域没有设备");
					onLoad(0);
				}
				else
				{
					hideHint();
					deviceList.clear();
					deviceList.addAll(tempList);
					deviceAdapter.notifyDataSetChanged();
					onLoad(0);
				}
				isRunning = false;
			}
			else if(msg.what == HttpMsg.GET_NEAR_BY_SPEAKER_FAIL)
			{
				isRunning = false;
				onLoad(0);
				showHint("获取设备列表失败");
				//showToast("获取数据失败，请下拉刷新");
			}
			else if(msg.what == OnConnectHandler.ON_NETWORK_RECONNECTED_SUCCESS)
			{
				if(isWifiApConnect())//如果是热点，就连接固定ip
					getLocalData().setWifiInfor(getCurrentSsid(), Constant.WIFI_HOT_PWD);
				Constant.isSongUpdate = true;
				setResult(RESULT_OK);
				finish();
			}
			else if(msg.what == OnConnectHandler.ON_NETWORK_RECONNETC_FAIL)
			{
				//boolean isDeviceRemove = (Boolean)msg.obj;
				showToast("连接失败，请确保与该设备在同一局域网");
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
		
		setContentView(R.layout.activity_net_device);
		
		tvHint = (TextView)findViewById(R.id.tv_net_device_hint);
		listView = (LJListView)findViewById(R.id.ptrl_net_devices);
		listView.getListView().setDivider(getResources().getDrawable(R.color.transparent));
		listView.getListView().setDividerHeight(1);
		listView.setPullLoadEnable(true,"共5条数据"); //如果不想让脚标显示数据可以mListView.setPullLoadEnable(false,null)或者mListView.setPullLoadEnable(false,"")
		listView.setPullRefreshEnable(true);
		listView.setIsAnimation(true); 
		listView.setXListViewListener(this);
		listView.showFootView(false);
		listView.setRefreshTime();
		listView.setOnItemClickListener(this);
		deviceAdapter = new DeviceAdapter(getActivity(), deviceList, false);
		listView.setAdapter(deviceAdapter);
		
		httpFactory = new HttpFactory(getActivity(), handler);
		
		listView.onFresh();
		
		setCenterString("在线设备");
		
		isAddAdmin = getIntent().getBooleanExtra("ADD_ADMIN", false);
		
		oldDeviceInfor = getLocalData().getDeviceInfor();
		
		String lat = getLocalData().getLat();
		if(TextUtils.isEmpty(lat))
		{
			locationFactory = new LocationFactory(getActivity());
			locationFactory.startLocation();
		}
	}
	
	private void onLoad(int updateCount) 
	{
		listView.setCount(updateCount);
		listView.stopLoadMore();
		listView.stopRefresh();
		listView.setRefreshTime();
	}
	
	private void showHint(String text)
	{
		tvHint.setVisibility(View.VISIBLE);
		tvHint.setText(text);
	}
	private void hideHint()
	{
		tvHint.setVisibility(View.GONE);
	}
	
	/**
	*callbacks
	*/
	@Override
	protected void onResume() 
	{
		// TODO Auto-generated method stub
		super.onResume();
		MinaClient.getInstance().setOnConnectListener(getActivity(), handler);
	}
	
	/**
	*callbacks
	*/
	@Override
	protected void onPause() 
	{
		// TODO Auto-generated method stub
		super.onPause();
		MinaClient.getInstance().setOnConnectListener(getActivity(), null);
		MinaClient.getInstance().stopConnect();
	}
	
	/**
	*callbacks
	*/
	@Override
	protected void onDestroy()
	{
	
		if(httpFactory != null)
			httpFactory.stopHttp();
		
		if(isChangeDevice && !MinaClient.getInstance().isConnected())
		{
			if(oldDeviceInfor != null)
			{
				getLocalData().setDeviceInfor(oldDeviceInfor);
				
				MinaClient.getInstance().stopConnect();
				MinaClient.getInstance().close();
				MinaClient.getInstance().startClient();
			}
		}
		if(locationFactory != null)
			locationFactory.stopLocation();
		listView.stopRefresh();
		
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	/**
	*callbacks
	*/
	@Override
	public void onRefresh() 
	{
		// TODO Auto-generated method stub
		if(isRunning)
			return;
		if(httpFactory != null)
			httpFactory.getNearBySpeakers();
	}

	/**
	*callbacks
	*/
	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		
	}

	private void connectNewDevice(DeviceInfor deviceInfor)
	{
		getLocalData().setDeviceInfor(deviceInfor);
		if(MinaClient.getInstance().isConnected() && MinaClient.getInstance().isSameAddr(deviceInfor.getIp()))
		{
			//socket已经连接，并且当前设备的ip与连接的ip一样就直接退出
			finish();
		}
		else
		{
			//当前选择的设备与当前连接的设备不一样，断开socket
			MinaClient.getInstance().stopConnect();
			MinaClient.getInstance().close();
			MinaClient.getInstance().startClient();
		}
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int pos, long arg3) 
	{
		// TODO Auto-generated method stub
		if(pos > 0)
			pos = pos - 1;
		DeviceInfor deviceInfor = deviceList.get(pos);
		if(isAddAdmin)
		{
			Bundle bundle = new Bundle();
			bundle.putSerializable("DEVICE_INFOR", deviceInfor);
			ViewUtils.startActivity(getActivity(), AddAdminActivity.class, bundle);
		}
		else
		{
			/*if(!deviceInfor.isAvailable())
			{
				showToast("请连接店铺的wifi网络(" + deviceInfor.getWifiName() + ")");
				return;
			}*/
			/*if(!deviceInfor.isSelected())//当前设备未被选中
			{
				if(deviceInfor.isAvailable())//当前网络内有该设备
				{
					//连接该设备
					isChangeDevice = true;
					connectNewDevice(deviceInfor);
				}
				else if(WifiFactory.newInstance(getActivity()).getWifiAdmin().ifHasWifi(deviceInfor.getWifiName()))
				{
					//当前区域有该网络,就进入网络切换页面
					if(deviceInfor.isAvailable())
					{
						String wifiName = deviceInfor.getWifiName();
						if(wifiName.endsWith(Constant.UUID_TAG))//wifi热点
						{
							deviceInfor.setWifiPwd(Constant.WIFI_HOT_PWD);//设置wifi热点默认密码
							deviceInfor.setAvailable(false);//如果是wifi热点，并且当前连接的网络为其他网络，将设备状态设置为false，让其正常连接扫描
						}
					}
					getDBManager().insertDevice(deviceInfor);
					Bundle bundle = new Bundle();
					bundle.putSerializable("DEVICE_INFOR", deviceInfor);
					ViewUtils.startActivity(getActivity(), NetWorkChangeActivity.class, bundle);
					finish();
				}
				else 
				{
					//当前区域没有该网络就提示用户
					showToast("请连接店铺的wifi网络(" + deviceInfor.getWifiName() + ")");
					return;
				}
			}
			else
			{
				connectNewDevice(deviceInfor);
			}*/
			if(WifiFactory.newInstance(getActivity()).getWifiAdmin().isWifiEnabled() && !TextUtils.isEmpty(getCurrentSsid()))
			{
				connectNewDevice(deviceInfor);
			}
			else
				showToast("请先连接店内WIFI网络");
		}
	}
}
 
