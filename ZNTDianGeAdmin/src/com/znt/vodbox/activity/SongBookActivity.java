/*  
* @Project: ZNTDianGe 
* @User: Administrator 
* @Description: 店铺音乐
* @Author： yan.yu
* @Company：http://www.zhunit.com/
* @Date 2016-4-15 下午11:32:48 
* @Version V1.1   
*/ 

package com.znt.vodbox.activity; 

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.znt.vodbox.R;
import com.znt.diange.mina.cmd.DeviceInfor;
import com.znt.diange.mina.entity.MediaInfor;
import com.znt.vodbox.dmc.engine.OnConnectHandler;
import com.znt.vodbox.factory.HttpFactory;
import com.znt.vodbox.http.HttpMsg;
import com.znt.vodbox.mina.client.ClientHandler;
import com.znt.vodbox.mina.client.MinaClient;
import com.znt.vodbox.utils.ViewUtils;
import com.znt.vodbox.view.SongBookAdapter;
import com.znt.vodbox.view.listview.LJListView;
import com.znt.vodbox.view.listview.LJListView.IXListViewListener;

/** 
 * @ClassName: SongBookActivity 
 * @Description: TODO
 * @author yan.yu 
 * @date 2016-4-15 下午11:32:48  	
 */
public class SongBookActivity extends BaseActivity implements IXListViewListener, OnItemClickListener
{

	private LJListView mListView;
	private View viewBottom = null;
	private View viewUpdate = null;
	private View viewAddMusic = null;
	private SongBookAdapter adapter = null;
	private HttpFactory httpFactory = null;
	
	private DeviceInfor deviceInfor = null;
	private boolean isRunning = false;
	private boolean isUpdateRequring = false;
	private int pageNum = 1;
	
	private List<MediaInfor> mediaList = new ArrayList<MediaInfor>();
	
	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == HttpMsg.GET_SPEAKER_MUSIC_START)
			{
				showLoadingView(true);
				isRunning = true;
			}
			else if(msg.what == HttpMsg.GET_SPEAKER_MUSIC_SUCCESS)
			{
				pageNum ++;
				List<MediaInfor> tempList = (List<MediaInfor>)msg.obj;
				if(tempList.size() == 0)
				{
					if(isAdminDevice())
						showNoDataView("该音响还没有网络音乐" + "\n赶紧去添加吧~");
					else
						showNoDataView("该音响还没有网络音乐哦~");
				}
				else
				{
					mediaList.clear();
					mediaList.addAll(tempList);
					adapter.notifyDataSetChanged();
					hideHintView();
				}
				showLoadingView(false);
				isRunning = false;
				onLoad(0);
			}
			else if(msg.what == HttpMsg.GET_SPEAKER_MUSIC_FAIL)
			{
				isRunning = false;
				onLoad(0);
			}
			else if(msg.what == ClientHandler.RECV_PALY_RES_UPDATE)
			{
				showToast("更新成功");
				isUpdateRequring = false;
			}
			else if(msg.what == OnConnectHandler.ON_NETWORK_RECONNECTED_SUCCESS)
			{
				startUpdateSpeakerMusic();
			}
			else if(msg.what == ClientHandler.TIME_OUT)
			{
				isUpdateRequring = false;
				Toast.makeText(getActivity(), "请求超时", 0).show();
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
		
		setContentView(R.layout.activity_song_book);
		
		viewBottom = findViewById(R.id.view_song_book_bottom);
		viewUpdate = findViewById(R.id.view_song_book_update);
		viewAddMusic = findViewById(R.id.view_song_book_add);
		mListView = (LJListView)findViewById(R.id.ptrl_song_book);
		mListView.getListView().setDivider(getResources().getDrawable(R.color.transparent));
		mListView.getListView().setDividerHeight(1);
		mListView.setPullLoadEnable(true,"共5条数据"); //如果不想让脚标显示数据可以mListView.setPullLoadEnable(false,null)或者mListView.setPullLoadEnable(false,"")
		mListView.setPullRefreshEnable(true);
		mListView.setIsAnimation(true); 
		mListView.setXListViewListener(this);
		mListView.showFootView(false);
		mListView.setRefreshTime();
		mListView.setOnItemClickListener(this);
		
		deviceInfor = (DeviceInfor)getIntent().getSerializableExtra("DEVICE_INFOR");
		setCenterString(deviceInfor.getName());
		
		adapter = new SongBookAdapter(this, mediaList);
		mListView.setAdapter(adapter);
		
		if(isAdminDevice())
			viewBottom.setVisibility(View.VISIBLE);
		else
			viewBottom.setVisibility(View.GONE);
		
		httpFactory = new HttpFactory(getActivity(), handler);
		
		MinaClient.getInstance().setOnConnectListener(getActivity(), handler);
		
		viewUpdate.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0)
			{
				String localDev = getLocalData().getDeviceId();
				if(TextUtils.isEmpty(localDev) || !deviceInfor.getId().equals(localDev))
				{
					showToast("未连接当前设备，请连接该设备后操作");
				}
				else
					startUpdateSpeakerMusic();
			}
		});
		viewAddMusic.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0)
			{
				ViewUtils.startActivity(getActivity(), SearchMusicActivity.class, null);
			}
		});
	}
	
	/**
	*callbacks
	*/
	@Override
	protected void onResume() 
	{
	
		mListView.onFresh();
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	private void startUpdateSpeakerMusic()
	{
		if(isUpdateRequring)
			return;
		if(MinaClient.getInstance().isConnected())
		{
			isUpdateRequring = true;
		}
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				MinaClient.getInstance().sendUpdateSpeakerMusic();
			}
		}).start();
	}
	
	private void onLoad(int updateCount) 
	{
		mListView.setCount(updateCount);
		mListView.stopLoadMore();
		mListView.stopRefresh();
		mListView.setRefreshTime();
	}
	
	private void getPlanMusics()
	{
		httpFactory.getSpeakerMusic(deviceInfor.getCode(), pageNum);
	}
	
	/**
	*callbacks
	*/
	@Override
	protected void onDestroy() 
	{
		// TODO Auto-generated method stub
		super.onDestroy();
		if(httpFactory != null)
			httpFactory.stopHttp();
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
		getPlanMusics();
	}

	/**
	*callbacks
	*/
	@Override
	public void onLoadMore() 
	{
		// TODO Auto-generated method stub
		getPlanMusics();
	}

	/**
	*callbacks
	*/
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
	{
		// TODO Auto-generated method stub
		if(mediaList.size() == 0)
			return;
		
		if(position > 0)
			position = position - 1;
		
		MediaInfor item = mediaList.get(position);
		
	}
}
 
