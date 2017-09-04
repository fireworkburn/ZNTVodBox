package com.znt.dnsdiscover;

import java.util.ArrayList;
import java.util.List;

import javax.jmdns.ServiceEvent;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.znt.speaker.jmdns.DnsDiscoverManager;

public class MainActivity extends Activity 
{

	private Button btnRefresh = null;
	private Button btnStart = null;
	private View loadingView = null;
	private TextView tvHint = null;
	private ProgressBar pbLoading = null;
	private ListView listView = null;
	private List<ServiceEvent> devices = new ArrayList<ServiceEvent>();
	private DnsDiscoverManager dnsDiscoverManager = null;
	private DeviceAdapter adapter = null;
	
	private boolean isBackClick = false;
	public final static int MSG_DEVICE_ADD = 1;
	public final static int MSG_DEVICE_REMOVE = 2;
	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == MSG_DEVICE_ADD)
			{
				ServiceEvent sev = (ServiceEvent)msg.obj;
				devices.add(sev);
				adapter.notifyDataSetChanged();
			}
			else if(msg.what == MSG_DEVICE_REMOVE)
			{
				ServiceEvent sev = (ServiceEvent)msg.obj;
				removeDevice(sev);
			}
			else if(msg.what == DnsDiscoverManager.OPEN_DNS_START)
			{
				showLoadingView("正在开启DNS服务...");
			}
			else if(msg.what == DnsDiscoverManager.OPEN_DNS_SUCCESS)
			{
				showHintView("DNS服务正在运行");
			}
			else if(msg.what == DnsDiscoverManager.OPEN_DNS_FAIL)
			{
				showHintView("DNS服务开启失败");
			}
			else if(msg.what == DnsDiscoverManager.CLOSE_DNS_START)
			{
				showLoadingView("正在关闭DNS服务...");
			}
			else if(msg.what == DnsDiscoverManager.CLOSE_DNS_FINISH)
			{
				showHintView("DNS服务关闭完成");
				if(isBackClick)
					finish();
			}
		};
	};
	
	private void removeDevice(ServiceEvent sev)
	{
		int size = devices.size();
		for(int i=0;i<size;i++)
		{
			ServiceEvent tempDev = devices.get(i);
			if(tempDev.getInfo().getName().equals(sev.getInfo().getName()))
			{
				devices.remove(i);
				break;
			}
		}
		adapter.notifyDataSetChanged();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		loadingView = findViewById(R.id.view_loading);
		tvHint = (TextView)findViewById(R.id.tv_loading);
		btnRefresh = (Button)findViewById(R.id.btn_refresh);
		btnStart = (Button)findViewById(R.id.btn_start);
		pbLoading = (ProgressBar)findViewById(R.id.pb_loading);
		listView = (ListView)findViewById(R.id.lv_devices);
		
		adapter = new DeviceAdapter();
		listView.setAdapter(adapter);
		
		dnsDiscoverManager = new DnsDiscoverManager(this, handler);
		
		btnRefresh.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				devices.clear();
				adapter.notifyDataSetChanged();
				
				if(dnsDiscoverManager != null)
				{
					if(dnsDiscoverManager.isRunning())
						dnsDiscoverManager.stopDns();
					if(!dnsDiscoverManager.isRunning())
						dnsDiscoverManager.openDns("neldtv dns test refresh", true);
				}
			}
		});
		btnStart.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				devices.clear();
				adapter.notifyDataSetChanged();
				
				if(dnsDiscoverManager != null)
				{
					if(dnsDiscoverManager.isRunning())
						dnsDiscoverManager.stopDns();
					if(!dnsDiscoverManager.isRunning())
						dnsDiscoverManager.openDns("neldtv dns test start", false);
				}
			}
		});
		
	}
	
	private void showLoadingView(String hint)
	{
		loadingView.setVisibility(View.VISIBLE);
		pbLoading.setVisibility(View.VISIBLE);
		tvHint.setText(hint);
	}
	private void showHintView(String hint)
	{
		loadingView.setVisibility(View.VISIBLE);
		pbLoading.setVisibility(View.GONE);
		tvHint.setText(hint);
	}
	private void hideLoadingView()
	{
		loadingView.setVisibility(View.GONE);
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

	/**
	*callbacks
	*/
	@Override
	public void onBackPressed()
	{
		if(dnsDiscoverManager.isRunning())
		{
			isBackClick = true;
			dnsDiscoverManager.stopDns();
		}
		else
			super.onBackPressed();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	class DeviceAdapter extends BaseAdapter
	{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return devices.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return devices.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int pos, View convertView, ViewGroup arg2) 
		{
			// TODO Auto-generated method stub
			
			TextView textView = new TextView(MainActivity.this);
			ServiceEvent device = devices.get(pos);
			
			String iaddr = device.getInfo().getAddress().getHostAddress();
			String name = device.getInfo().getName();
			String textStr = device.getInfo().getTextString();
			/*String Key = device.getInfo().getKey();
			String NiceTextString = device.getInfo().getNiceTextString();
			String QualifiedName = device.getInfo().getQualifiedName();
			Map<Fields, String> nameMap = device.getInfo().getQualifiedNameMap();
			String url = device.getInfo().getURL();*/
			
			textView.setText("名称：" + name + "\n" + "ip：" + iaddr +  "\n" + "内容：" + textStr);
			return textView;
		}
	}

}
