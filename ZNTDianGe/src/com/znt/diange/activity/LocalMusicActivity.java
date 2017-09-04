/*
package com.znt.diange.activity;

import java.util.ArrayList;
import java.util.List;

import org.cybergarage.upnp.Device;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.znt.diange.R;
import com.znt.diange.dlna.mediaserver.server.center.ContentManager;
import com.znt.diange.dlna.mediaserver.server.center.ControlRequestProxy;
import com.znt.diange.dlna.mediaserver.server.center.ControlRequestProxy.ControlRequestCallback;
import com.znt.diange.dmc.engine.DLNAContainer;
import com.znt.diange.dmc.engine.DLNAContainer.DeviceChangeListener;
import com.znt.diange.mina.entity.MediaInfor;
import com.znt.diange.service.DMSService;
import com.znt.diange.utils.SystemUtils;
import com.znt.diange.utils.TimerUtils;
import com.znt.diange.utils.TimerUtils.OnTimeOverListner;
import com.znt.diange.utils.UpnpUtil;
import com.znt.diange.utils.ViewUtils;
import com.znt.diange.view.SortAdapter;
import com.znt.diange.view.listview.LJListView;
import com.znt.diange.view.listview.LJListView.IXListViewListener;

*//** 
* @ClassName: LocalMusicActivity 
* @Description: TODO
* @author yan.yu 
* @date 2015年7月19日 下午4:43:29  
*//*
public class LocalMusicActivity extends BaseActivity implements OnItemClickListener, 
ControlRequestCallback, OnClickListener, IXListViewListener
{
	
	private TextView tvHint = null;
	private TextView tvRefresh = null;
    private View viewReload = null;
    private LJListView mListView;
    private SortAdapter mAdaptor;
	
	private ContentManager mContentManager;
	private TimerUtils timerUtils = null;
	
	private List<MediaInfor> musicList = new ArrayList<MediaInfor>();
	
	private boolean isLocal = true;
	private boolean isInit = true;
	private boolean isScannFinish = false;
	private boolean isReload = false;
	
	private String curDir = "";
	
	private final int SCAN_MUSIC = 1;
	private final int SCAN_MUSIC_FINISH = 2;
	private final int DEVICE_CHANGED = 3;
	
	Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == SCAN_MUSIC)
			{
				if(!isScannFinish)
				{
					showLoadingView(true);
					mListView.setVisibility(View.INVISIBLE);
				}
				timerUtils.delayTime(30);//30秒后还未加载完成就取消加载
			}
			else if(msg.what == SCAN_MUSIC_FINISH)
			{
				
				timerUtils.cancel();
				
				if(!isScannFinish)
					clearWebMedias();
				
				List<MediaInfor> tempList = (ArrayList<MediaInfor>)msg.obj;
				if(tempList != null && tempList.size() > 0)
				{
					if(!isScannFinish)
					{
						mContentManager.pushListItem(tempList);	
						musicList.addAll(tempList);
						mAdaptor.updateListView(tempList);
						
						hideHintView();
						showLoadingView(false);
						mListView.showFootView(false);
						mListView.setVisibility(View.VISIBLE);
					}
					else
					{
						isScannFinish = false;
						mContentManager.updateFirst(tempList);	
						musicList.addAll(mContentManager.peekListItem());
					}
				}
				else
				{
					if(tempList == null)
					{
						if(viewReload != null)
						{
							showLoadingView(false);
							hideHintView();
							viewReload.setVisibility(View.VISIBLE);
						}
					}
					else if(tempList.size() == 0)
						showNoDataView("没有找到任何歌曲哦~");
				}
				
				onLoad(0);
				
				dismissDialog();
			}
			else if(msg.what == DEVICE_CHANGED)
			{
				showHintView();
			}
		};
	};
	
	*//**
	*callbacks
	*//*
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_local_music);
		
		mListView = (LJListView)findViewById(R.id.phlv_local_music);
		tvHint = (TextView) findViewById(R.id.tv_local_music_hint);
		tvRefresh = (TextView) findViewById(R.id.tv_local_music_refresh);
		viewReload = (View) findViewById(R.id.view_local_music_hint);
		
		isLocal = getIntent().getBooleanExtra("IS_LOCAL", true);
		
		mListView.getListView().setDividerHeight(0);
		
		mListView.setPullRefreshEnable(false);
		mListView.setRefreshTime();
		
		initViews();
		showHintView();
		
		timerUtils = new TimerUtils(new OnTimeOverListner()
		{
			@Override
			public void OnTimeOver()
			{
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						// TODO Auto-generated method stub
						hideHintView();
						showLoadingView(false);
						viewReload.setVisibility(View.VISIBLE);
					}
				});
			}
		});
		
		DLNAContainer.getInstance().setOnDeviceChangeListener(new DeviceChangeListener()
		{
			@Override
			public void onDeviceChange(Device device, boolean isAdd)
			{
				// TODO Auto-generated method stub
				if(isAdd && isReload)
					ViewUtils.sendMessage(handler, DEVICE_CHANGED);
			}
		});
	}
	
	private Device getDevice()
    {
    	if(isLocal)
    		return DLNAContainer.getInstance().getPhoneDms();
    	else
    		return DLNAContainer.getInstance().getSpeakerDms();
    }
	private void resetDevice()
	{
		if(isLocal)
			DLNAContainer.getInstance().setPhoneDms(null);
		else
			DLNAContainer.getInstance().setSpeakerDms(null);
	}
	
	public void stop()
	{
		handler.removeMessages(SCAN_MUSIC);
		handler.removeMessages(SCAN_MUSIC_FINISH);
	}
	
	private void restartDMS()
	{
		Intent intent = new Intent(this, DMSService.class);
		intent.setAction(DMSService.RESTART_SERVER_ENGINE);
		Bundle bundle = new Bundle();
		bundle.putString("UUID", SystemUtils.getDeviceId(this));
		bundle.putString("DEVICE_NAME", new Build().MODEL);
		intent.putExtras(bundle);
		startService(intent);
	}
	
	*//**
	*callbacks
	*//*
	@Override
	protected void onResume()
	{
	
		if(isLocal)
			registerReceiver();
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	*//**
	*callbacks
	*//*
	@Override
	public void onPause()
	{
		stop();
		
		if(isLocal)
			unregisterReceiver();
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	*//**
	*callbacks
	*//*
	@Override
	protected void onDestroy()
	{
		DLNAContainer.getInstance().setOnDeviceChangeListener(null);
		mContentManager.clear();
		timerUtils.cancel();
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	public void updateLocalMusic()
	{
		isInit = true;
		
		showHintView();
	}
	
	private void showHintView()
	{
		if(getDevice() == null)
		{
			if(viewReload != null)
			{
				showLoadingView(false);
				hideHintView();
				viewReload.setVisibility(View.VISIBLE);
			}
		}
		else
		{
			if(isInit)
			{
				isInit = false;
				getWebMedias();
			}
			if(viewReload != null)
				viewReload.setVisibility(View.GONE);
		}
	}
	
	private void initViews()
	{
		mContentManager = ContentManager.getInstance();
		
		if(isLocal)
		{
			setCenterString("手机本地歌曲");
			tvHint.setText(R.string.local_music_hint_phone);
			getRightView().setOnClickListener(this);
		}
		else
		{
			setCenterString("音箱本地歌曲");
			tvHint.setText(R.string.local_music_hint_speacker);
		}
		
		mListView.setXListViewListener(this);
		mListView.setOnItemClickListener(this);
		
		tvRefresh.setOnClickListener(this);
		findViewById(R.id.view_title_back).setOnClickListener(this);
		
		mAdaptor = new SortAdapter(getActivity(), musicList);
		mAdaptor.setIsLocalMusic(true);
		mListView.setAdapter(mAdaptor);
		
	}
	
	private void setContentlist(List<MediaInfor> list)
	{	
		if (list != null)
		{
			musicList.clear();
			musicList.addAll(list);
			mAdaptor.updateListView(list);
		}
	}
	
	private void back()
	{
		mContentManager.popListItem();
		List<MediaInfor> list = mContentManager.peekListItem();
		if (list == null)
		{
			super.onBackPressed();
		}
		else
		{
			setContentlist(list);
		}	
	}

	@Override
	public void onBackPressed() 
	{
		// TODO Auto-generated method stub
		back();
	}
	
	public void getWebMedias()
	{
		requestDirectory();
	}
	public void clearWebMedias()
	{
		if(musicList != null && musicList.size() > 0)
			musicList.clear();
	}
	
	public List<MediaInfor> getMusics()
	{
		return musicList;
	}
	
	@Override
	public void onGetItems(final List<MediaInfor> list) 
	{
		// TODO Auto-generated method stub
		isReload = false;
		ViewUtils.sendMessage(handler, SCAN_MUSIC_FINISH, list);
	}
	
	private void requestDirectory()
    {
		if(!SystemUtils.isNetConnected(getActivity()))
		{
			showToast("无网络连接");
			return ;
		}
			
    	if (getDevice() == null)
    	{
    		showToast("设备初始化中，请稍后...");
    		restartDMS();
    		return ;
    	}
    	
    	ViewUtils.sendMessage(handler, SCAN_MUSIC);
    	
    	ControlRequestProxy.syncGetDirectory(getActivity(), getDevice(), this);
    }
	
	private void onLoad(int updateCount) 
	{
		mListView.setCount(updateCount);
		mListView.stopLoadMore();
		mListView.stopRefresh();
		mListView.setRefreshTime();
	}
	
	*//**
	*callbacks
	*//*
	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3)
	{
		// TODO Auto-generated method stub
		
		if(musicList.size() == 0)
			return;
		
		if(position > 0)
			position = position - 1;
		
		MediaInfor item = musicList.get(position);
		if (UpnpUtil.isAudioItem(item) || TextUtils.isEmpty(item.getchildCount())) 
		{
			Bundle bundle = new Bundle();
			bundle.putSerializable("MediaInfor", item);
			String mediaType = "";
			if(isLocal)
				mediaType = MediaInfor.MEDIA_TYPE_PHONE;
			else
				mediaType = MediaInfor.MEDIA_TYPE_SPEAKER;
			item.setMediaType(mediaType);
			ViewUtils.startActivity(this, SongPrepareActivity.class, bundle);
		}
		else if (UpnpUtil.isVideoItem(item))
		{
			//goVideoPlayerActivity(position, item);
		}
		else if (UpnpUtil.isPictureItem(item))
		{
			//goPicturePlayerActivity(position, item);
		}
		else
		{
			if(!TextUtils.isEmpty(item.getchildCount()))
			{
				ViewUtils.sendMessage(handler, SCAN_MUSIC);
				ControlRequestProxy.syncGetItems(LocalMusicActivity.this, item.getMediaId(), getDevice(), LocalMusicActivity.this);
			}
			else
				showToast("不支持的文件格式~");
		}
	}
	
	*//**
	*callbacks
	*//*
	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		if(v == tvRefresh)
		{
			if(isLocal)
			{
				restartDMS();
			}
			
			isInit = true;
			isReload = true;
			restartDLNAService();
			resetDevice();
			showLoadingView(true);
		}
		else if(v.getId() == R.id.view_title_back)
		{
			back();
		}
		else if(v.getId() == R.id.tv_view_top_right)
		{
			
			if(isScanning)
				return;
			
			isScanning = true;
			
			mContentManager.clear();
			musicList.clear();
			showLoadingView(true);
			
			restartDMS();
		}
	}


	*//**
	*callbacks
	*//*
	@Override
	public void onRefresh()
	{
		// TODO Auto-generated method stub
		
	}


	*//**
	*callbacks
	*//*
	@Override
	public void onLoadMore()
	{
		// TODO Auto-generated method stub
		
	}
	
	private MediaScanReceiver mediaScanReceiver = new MediaScanReceiver();
	private void registerReceiver()
    {
		IntentFilter filter = new IntentFilter();
    	filter.addAction(DMSService.SCAN_MUSIC_START);
    	filter.addAction(DMSService.SCAN_MUSIC_FINISH);
		registerReceiver(mediaScanReceiver, filter);
    }
    private void unregisterReceiver()
    {
    	unregisterReceiver(mediaScanReceiver);
    }
	class MediaScanReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context arg0, Intent intent)
		{
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if(action.equals(DMSService.SCAN_MUSIC_START))//扫描开始
			{
				if(isLocal)
					showProgressView(true);
			}
			else if(action.equals(DMSService.SCAN_MUSIC_FINISH))//扫描完成
			{
				if(musicList != null)
					musicList.clear();
				if(mContentManager != null)
					mContentManager.clear();
				isScannFinish = true;
				getWebMedias();
				
			}
		}
	}
}
 
*/