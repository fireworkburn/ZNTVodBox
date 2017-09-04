package com.znt.speaker.dialog;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.znt.speaker.R;

public class WifiListDialog extends Dialog
{
	private Context context;
	private View parentView = null;
	private ListView lv_wifilist;
	private TextView tvCancel = null;
	
	private String curSsid = "";
	
    List<ScanResult> mWifiList = new ArrayList<ScanResult>();
	
	public WifiListDialog(Context context)
	{
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		parentView = inflater.inflate(R.layout.dialog_wifi_list, null);
		
	    setScreenBrightness();
	    Window window = getWindow();
		window.setWindowAnimations(R.style.MMTheme_DataSheet);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}
	
	public void showWifiDialog(List<ScanResult> list, String curSsid, OnItemClickListener listener)
	{
		this.curSsid = curSsid;
		
        lv_wifilist = (ListView)parentView. findViewById(R.id.lv_wifilist);
        tvCancel = (TextView)parentView. findViewById(R.id.tv_dialog_wifi_cancel);
        
        mWifiList.clear();
        mWifiList.addAll(list);
		
        lv_wifilist.setAdapter(new MyAdapter());
        lv_wifilist.setOnItemClickListener(listener);
        
        Window w = getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		
		final int cFullFillWidth = 10000;
		parentView.setMinimumWidth(cFullFillWidth);
		
		lp.x = 0;
		final int cMakeBottom = -1000;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.BOTTOM;
		onWindowAttributesChanged(lp);
		setCanceledOnTouchOutside(true);
		setContentView(parentView);
		show();
		
		tvCancel.setOnClickListener(new android.view.View.OnClickListener()
		{
			
			@Override
			public void onClick(View arg0)
			{
				// TODO Auto-generated method stub
				dismiss();
			}
		});
	}
	
	class MyAdapter extends BaseAdapter
	{

		@Override
		public int getCount()
		{
			// TODO Auto-generated method stub
			if (mWifiList != null)
				return mWifiList.size();
			return 0;
		}

		@Override
		public Object getItem(int position)
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position)
		{
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			// TODO Auto-generated method stub
			
			convertView = (View)LayoutInflater.from(context).inflate(R.layout.dialog_wifi_item_view, null);
			
			TextView ssid = (TextView)convertView.findViewById(R.id.tv_ssid);
			ImageView ivLock = (ImageView)convertView.findViewById(R.id.iv_wifi_item_lock);
			ImageView ivLevel = (ImageView)convertView.findViewById(R.id.img_level);
			ImageView ivSelect = (ImageView)convertView.findViewById(R.id.iv_wifi_item_select);
			
			ScanResult result = mWifiList.get(position);
			
			ssid.setText(" " + result.SSID);
			ivLevel.setImageResource(getLevelImage(result.level));
			String cap = result.capabilities;
			
			if(hasPwd(cap))
				ivLock.setVisibility(View.VISIBLE);
			else
				ivLock.setVisibility(View.INVISIBLE);
			if(result.SSID.equals(curSsid))
				ivSelect.setVisibility(View.VISIBLE);
			else
				ivSelect.setVisibility(View.INVISIBLE);
			
			return convertView;
		}
		
		private boolean hasPwd(String capabilities )
		{
			boolean result = false;
			if (!TextUtils.isEmpty(capabilities))
            {
                if (capabilities.contains("WPA") || capabilities.contains("wpa"))
                {
                    Log.i("hefeng", "wpa");
                    result = true;
                } 
                else if (capabilities.contains("WEP") || capabilities.contains("wep")) 
                {
                    Log.i("hefeng", "wep");
                    result = true;
                } 
                else
                {
                    Log.i("hefeng", "no");
                    result = false;
                }
            }
			return result;
		}
		
		class ViewHolder
		{
			ImageView ivLock = null;
			ImageView ivLevel = null;
			ImageView ivSelect = null;
		}
		
		private int getLevelImage(int level)
		{
			int numberOfLevels=5;
			level = WifiManager.calculateSignalLevel(level, numberOfLevels);
			
			switch (level)
			{
			case 1:
				return R.drawable.wifi_signal_1;
			case 2:
				return R.drawable.wifi_signal_2;
			case 3:
				return R.drawable.wifi_signal_3;
			case 4:
				return R.drawable.wifi_signal_4;
			default:
				return R.drawable.wifi_signal_1;
			}
		}
	}
	
	private void setScreenBrightness() 
    {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        /**
        *  此处设置亮度值。dimAmount代表黑暗数量，也就是昏暗的多少，设置为0则代表完全明亮。
        *  范围是0.0到1.0
        */
        lp.dimAmount = 0;
        window.setAttributes(lp);
    }
}
