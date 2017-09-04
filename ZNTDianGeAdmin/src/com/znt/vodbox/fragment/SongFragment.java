
package com.znt.vodbox.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.znt.diange.mina.cmd.CmdType;
import com.znt.diange.mina.cmd.DeleteSongCmd;
import com.znt.diange.mina.cmd.DeviceSetCmd;
import com.znt.diange.mina.cmd.GetSongInforCmd;
import com.znt.diange.mina.cmd.GetSongListCmd;
import com.znt.diange.mina.cmd.PlayErrorCmd;
import com.znt.diange.mina.cmd.UpdateCmd;
import com.znt.diange.mina.entity.SongInfor;
import com.znt.vodbox.R;
import com.znt.vodbox.activity.MainActivity;
import com.znt.vodbox.activity.SongInforActivity;
import com.znt.vodbox.dialog.PlayListDialog;
import com.znt.vodbox.dmc.engine.OnConnectHandler;
import com.znt.vodbox.entity.Constant;
import com.znt.vodbox.factory.DiangeManger;
import com.znt.vodbox.factory.HttpFactory;
import com.znt.vodbox.mina.client.ClientHandler;
import com.znt.vodbox.mina.client.ClientHandler.MinaErrorType;
import com.znt.vodbox.mina.client.MinaClient;
import com.znt.vodbox.utils.ViewUtils;
import com.znt.vodbox.view.CircleImageView;
import com.znt.vodbox.view.MarqueeTextView;
import com.znt.vodbox.view.SongAdapter;
import com.znt.vodbox.view.pulltozoom.view.PullToZoomBase.OnPullZoomListener;
import com.znt.vodbox.view.pulltozoom.view.PullToZoomListViewEx;
import com.znt.vodbox.view.pulltozoom.view.PullToZoomListViewEx.OnListScrollListener;

/** 
* @ClassName: SongFragment 
* @Description: TODO
* @author yan.yu 
* @date 2015年7月15日 下午11:15:29  
*/
public class SongFragment extends BaseFragment implements OnPullZoomListener, OnListScrollListener, OnClickListener
{

	private View parentView = null;
	private View viewHeaderBottom = null;
	private View viewHeaderTop = null;
	private PullToZoomListViewEx mListView;
	private MarqueeTextView marqueeTextView = null;
	private TextView tvRefresh = null;
	private TextView tvDeviceSelect = null;
	private TextView tvDeviceName = null;
	private TextView tvMusicName = null;
	private ImageView ivStatus = null;
	private TextView tvMusicNameTitle = null;
	private TextView tvCount = null;
	private View viewList = null;
	private TextView tvHint = null;
	private TextView tvDianbo = null;
	private ImageView ivRefresh = null;
	private ImageView ivHeaderBg = null;
	private CircleImageView civAlbumDefault = null;
	private CircleImageView civAlbum = null;
	private View footerView = null;
	private ProgressBar pbFooterView = null;
	private TextView tvFooterView = null;
	
	private DiangeManger mDiangeManger = null;
	private SongAdapter songAdapter = null;
	private HttpFactory httpFactory = null;
	
	  /**旋转动画的时间*/
	private final int ROTATION_ANIMATION_DURATION = 1200;
    /**动画插值*/
	private final Interpolator ANIMATION_INTERPOLATOR = new LinearInterpolator();
    /**旋转的动画*/
    private Animation mRotateAnimation;
	
	private SongInfor curSongInfor = null;
	private SongInfor deleteSongInfor = null;
	private List<SongInfor> songList = new ArrayList<SongInfor>();
	private List<String> musicImgList = new ArrayList<String>();
	private boolean isRefresh = false;
	private boolean isRunning = false;
	private boolean isSearching = false;
	private boolean isReconnect = false;
	private int total = 0;
	private int pageNum = 0;
	private int lastItem;
	
	private final int GET_MUSIC_IMAGE = 3;
	private final int GET_MUSIC_IMAGE_FAIL = 4;
	private final int GET_MUSIC_IMAGE_SUCCESS = 5;
	private Handler handler = new Handler(new Callback()
	{
		@SuppressWarnings("unchecked")
		@Override
		public boolean handleMessage(Message msg)
		{
			// TODO Auto-generated method stub
			
			if(msg.what == ClientHandler.RECV_GET_PLAY_LIST)
			{
				Constant.isSongUpdate = false;
				
				GetSongListCmd cmdInfor = (GetSongListCmd)msg.obj;
				total = Integer.parseInt(cmdInfor.getTotal());
				if(pageNum == 0)
					songList.clear();
				
				if(cmdInfor.getSongList() != null && total > 0)
				{
					songList.addAll(cmdInfor.getSongList());
					hideHintView();
					tvHint.setVisibility(View.GONE);
					tvDianbo.setVisibility(View.GONE);
				}
				else
				{
					if(pageNum == 0)
					{
						showHintDianboView();
						mListView.showFooterView(false);
					}
				}
				songAdapter.resetMoreView();
				songAdapter.notifyDataSetChanged();
				
				if(!isHasMore())
					showFooterNoMore();
				
				showSongCount();
				getCurPlaySong();
				refreshFinish();
				
				isRunning = false;
			}
			else if(msg.what == ClientHandler.RECV_PLAY_MUSIC_INFOR)
			{
				Constant.isPlayUpdate = false;
				
				if(msg.obj != null)
				{
					GetSongInforCmd cmdInfor = (GetSongInforCmd)msg.obj;
					updatePlayInfor(cmdInfor.getSongInfor());
				}
				else
				{
					updatePlayInfor(null);
				}
			}
			else if(msg.what == ClientHandler.RECV_UPDATE_INFOR)
			{
				if(msg.obj != null)
				{
					UpdateCmd cmdInfor = (UpdateCmd)msg.obj;
					String type = cmdInfor.getUpdateType();
					if(type.equals(UpdateCmd.SongList))
					{
						updateSongList();
					}
					else if(type.equals(UpdateCmd.PlayInfor))
					{
						getCurPlaySong();
					}
					else if(type.equals(UpdateCmd.All))
					{
						updateSongList();
						//getCurPlaySong();
					}
				}
			}
			else if(msg.what == GET_MUSIC_IMAGE)
			{
				//showDefaultAlbum(false);
			}
			else if(msg.what == GET_MUSIC_IMAGE_FAIL)
			{
				//showDefaultAlbum(true);
			}
			else if(msg.what == GET_MUSIC_IMAGE_SUCCESS)
			{
				if(musicImgList != null)
					musicImgList.clear();
				musicImgList.addAll((List<String>)msg.obj);
				
				//showDefaultAlbum(false);
			}
			else if(msg.what == ClientHandler.RECV_PLAY_ERROR)
			{
				PlayErrorCmd playErrorCmd = (PlayErrorCmd)msg.obj;
				showSongErrorDialog(getActivity(), "点播失败", playErrorCmd.getMediaName(), "该文件格式不支持，请重试或者搜索该歌曲");
				//getPlayList(0);
			}
			else if(msg.what == OnConnectHandler.ON_NETWORK_RECONNECTED_SUCCESS)
			{
				isRunning = false;
				updateSongList();
			}
			else if(msg.what == OnConnectHandler.ON_NETWORK_RECONNETC_FAIL)
			{
				isRunning = false;
				refreshFinish();
				showFooterLoadFail();
			}
			else if(msg.what == ClientHandler.MINA_CONNECT_ERROR)
			{
				MinaErrorType type = (MinaErrorType)msg.obj;
				
				dismissDialog();
				refreshFinish();
				showFooterLoadFail();
				if(type == MinaErrorType.CLOSED)
				{
					showHintRefreshView();
					//showToast("提示：服务器断开连接");
				}
				else if(type == MinaErrorType.EXCEPTION)
					showToast("操作失败，服务器异常");
				else if(type == MinaErrorType.IDLE)
					showToast("操作失败");
				
				//MinaClient.getInstance().reConnect(getActivity());
				
				isRunning = false;
			}
			else if(msg.what == ClientHandler.TIME_OUT)
			{
				dismissDialog();
				refreshFinish();
				showFooterLoadFail();
				isRunning = false;
				showToast(getResources().getString(R.string.request_time_out));
				
				if(pageNum > 0)
					showFooterLoadFail();
			}
			else if(msg.what == ClientHandler.RECV_DEVICE_EDIT_RESULT)
			{
				DeviceSetCmd deviceSetCmd = (DeviceSetCmd)msg.obj;
				
				if(deviceSetCmd.getCmdType().equals(CmdType.SET_DEVICE_FB))
				{
					/*DeviceInfor tempInfor = deviceSetCmd.getDeviceInfor();
					tempInfor.setAuthority(getLocalData().getDeviceInfor().getAuthority());
					getLocalData().setDeviceInfor(deviceSetCmd.getDeviceInfor());
					DBManager.newInstance(getActivity()).insertDevice(deviceSetCmd.getDeviceInfor());*/
					showDeviceName();
				}
			}
			else if(msg.what == ClientHandler.RECV_DELETE_SONG)
			{
				dismissDialog();
				DeleteSongCmd cmdInfor = (DeleteSongCmd)msg.obj;
				String result = ((DeleteSongCmd)cmdInfor).getResult();
				if(result.equals("0"))
				{
					showToast("删除成功");
					httpFactory.coinFreezeCancel(deleteSongInfor);
					updateSongList();
				}
				else
					showToast("删除失败");
			}
			else if(msg.what == ClientHandler.RECV_ERROR)
			{
				dismissDialog();
				String error = (String)msg.obj;
				if(error != null)
					showToast(error);
			}
			return false;
		}
	});
	
	public SongFragment()
	{
		
	}
	public SongFragment newInstance()
	{
		return new SongFragment();
	}
	
	
	/**
	*callbacks
	*/
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	/**
	*callbacks
	*/
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		
		if(parentView == null) 
		{
			parentView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_song, null);
			mListView = (PullToZoomListViewEx)parentView.findViewById(R.id.ptzl_song_queues);
			marqueeTextView = (MarqueeTextView)parentView.findViewById(R.id.tv_song_header_bg_explain);
			tvRefresh = (TextView)parentView.findViewById(R.id.tv_song_header_refresh);
			tvDeviceSelect = (TextView)parentView.findViewById(R.id.tv_song_device_select);
			tvDeviceName = (TextView)parentView.findViewById(R.id.tv_song_header_device_name);
			tvMusicName = (TextView)parentView.findViewById(R.id.tv_song_header_music_name);
			ivStatus = (ImageView)parentView.findViewById(R.id.iv_song_header_status);
			tvMusicNameTitle = (TextView)parentView.findViewById(R.id.tv_song_music_name);
			tvCount = (TextView)parentView.findViewById(R.id.tv_song_header_bg_song_count);
			viewList = parentView.findViewById(R.id.view_song_header_my_list);
			tvHint = (TextView)parentView.findViewById(R.id.tv_song_hint);
			tvDianbo = (TextView)parentView.findViewById(R.id.tv_song_dianbo);
			viewHeaderBottom = parentView.findViewById(R.id.view_song_header_bottom);
			viewHeaderTop = parentView.findViewById(R.id.view_song_head_bg);
			ivRefresh = (ImageView)parentView.findViewById(R.id.iv_song_header_refresh);
			ivHeaderBg = (ImageView)parentView.findViewById(R.id.iv_song_header_bg);
			civAlbumDefault = (CircleImageView)parentView.findViewById(R.id.civ_song_header_album_default);
			civAlbum = (CircleImageView)parentView.findViewById(R.id.civ_song_header_album);
			footerView = LayoutInflater.from(getActivity()).inflate(R.layout.view_pulltozoom_footer, null);
			pbFooterView = (ProgressBar)footerView.findViewById(R.id.pb_footer_loading);
			tvFooterView = (TextView)footerView.findViewById(R.id.tv_footer_loading);
			
			mListView.setDividerHeight(0);
			mListView.setFooterView(footerView);
			mListView.setOnListScrollListener(this);
			mListView.setOnPullZoomListener(this);
			
			viewList.setOnClickListener(this);
			
			mListView.setHideView(viewHeaderTop);
			
			civAlbumDefault.setBorderWidth(2);
			civAlbumDefault.setBorderColor(getResources().getColor(R.color.white));
			civAlbum.setBorderWidth(2);
			civAlbum.setBorderColor(getResources().getColor(R.color.white));
			
			marqueeTextView.setTextColor(getResources().getColor(R.color.white));
			
			DisplayMetrics localDisplayMetrics = new DisplayMetrics();
	        getActivity().getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
	        int mScreenHeight = localDisplayMetrics.heightPixels;
	        int mScreenWidth = localDisplayMetrics.widthPixels;
	        AbsListView.LayoutParams localObject = new AbsListView.LayoutParams(mScreenWidth, (int) (3.2F * (mScreenWidth / 5.0F)));
	        mListView.setHeaderLayoutParams(localObject);
	        songAdapter  = new SongAdapter(this, songList);
			mListView.setAdapter(songAdapter);
			
			viewHeaderBottom.setOnClickListener(this);
			viewHeaderTop.setOnClickListener(this);
			tvDeviceName.setOnClickListener(this);
			tvDianbo.setOnClickListener(this);
			tvDeviceSelect.setOnClickListener(this);
			
			mDiangeManger = new DiangeManger(getActivity());
			httpFactory = new HttpFactory(getActivity(), handler);
			
			setActivityView(parentView);
			
        }
		else
        {
            ViewGroup parent = (ViewGroup) parentView.getParent();
            if(parent != null) 
            {
                parent.removeView(parentView);
            }
        }
		
		// TODO Auto-generated method stub
		return parentView;
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		initAnimation();
		
		//showDeviceListDialog();
		
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}
	
	private void startTextScroll()
	{
		if(marqueeTextView != null)
		{
			marqueeTextView.stopScroll();
			marqueeTextView.startScroll();
		}
	}
	
	private void startAlbumAnim()
	{
		Animation operatingAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_indefinitely); 
		LinearInterpolator lin = new LinearInterpolator(); 
		operatingAnim.setInterpolator(lin); 
		civAlbumDefault.startAnimation(operatingAnim);
	}
	private void stopAlbumAnim()
	{
		civAlbumDefault.clearAnimation();
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onResume()
	{
		
		MinaClient.getInstance().setOnConnectListener(getActivity(), handler);
		
		startAlbumAnim();
		
		showDeviceName();
		
		getDataFromSp();
		
		isRunning = false;
		refreshFinish();
		showFooterNoMore();
		
		showDeviceStatusView();
		
		resetHeaderView();
		
		startTextScroll();
		
		//DLNAContainer.getInstance().setOnDeviceChangeListener(this);
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onPause()
	{
		stopAlbumAnim();
		
		MinaClient.getInstance().setConnectStop();
		
		marqueeTextView.stopScroll();
		super.onPause();
	}
	
	private void startMinaConnect()
	{
		if(mDiangeManger.isDeviceFind(false) && !MinaClient.getInstance().isConnected())
		{
			MinaClient.getInstance().startClient();
		}
	}
	
	private void getDataFromSp()
	{
		if(mDiangeManger.isDeviceFind(false))
		{
			if(Constant.isSongUpdate)
			{
				Constant.isSongUpdate = false;
				updateSongList();
			}
			else if(Constant.isPlayUpdate)
				getCurPlaySong();
		}
	}
	
	public void showDeviceName()
	{
		String deviceName = mDiangeManger.getDeviceName();
		if(!TextUtils.isEmpty(deviceName))
			tvDeviceName.setText(deviceName);
		else
			tvDeviceName.setText("设备未连接");
	}
	
	private void showDeviceStatusView()
	{
		if(!mDiangeManger.isDeviceFind(false))
		{
			isReconnect = false;
			tvHint.setText("");
			tvHint.setTextColor(getResources().getColor(R.color.text_black_on));
			tvHint.setVisibility(View.VISIBLE);
			tvDianbo.setVisibility(View.GONE);
			tvDeviceSelect.setVisibility(View.VISIBLE);
			tvFooterView.setText("");
		}
		else
			startMinaConnect();
	}
	
	private void showHintDianboView()
	{
		isReconnect = false;
		tvHint.setText("暂时没有点播信息~");
		tvHint.setTextColor(getResources().getColor(R.color.text_black_on));
		tvHint.setVisibility(View.VISIBLE);
		tvDianbo.setVisibility(View.VISIBLE);
		tvDeviceSelect.setVisibility(View.GONE);
		tvDianbo.setText("开始点播");
		tvDianbo.setTextColor(getResources().getColor(R.color.text_black_on));
	}
	private void showHintRefreshView()
	{
		isReconnect = true;
		tvHint.setText("服务器已断开连接，请重试~");
		tvHint.setTextColor(getResources().getColor(R.color.text_blue_on));
		tvHint.setVisibility(View.VISIBLE);
		tvDianbo.setVisibility(View.VISIBLE);
		tvDeviceSelect.setVisibility(View.GONE);
		tvDianbo.setText("重新连接");
		tvDianbo.setTextColor(getResources().getColor(R.color.text_blue_on));
		
	}
	private void showWifiErrorView()
	{
		isReconnect = false;
		tvHint.setText("未连接wifi网络，请先连接");
		tvHint.setTextColor(getResources().getColor(R.color.text_black_on));
		tvHint.setVisibility(View.VISIBLE);
		tvDianbo.setVisibility(View.VISIBLE);
		tvDeviceSelect.setVisibility(View.GONE);
		tvDianbo.setText("扫描设备");
		tvDianbo.setTextColor(getResources().getColor(R.color.text_black_on));
	}
	
	private void resetHeaderView()
	{
		mListView.resetView();
	}
	
	public void showSongCount()
	{
		if(total == 0)
			tvCount.setText("");
		else
			tvCount.setText("总共：" + total);
	}
	
	private void updatePlayInfor(SongInfor cmdInfor)
	{
		curSongInfor = cmdInfor;
		if(curSongInfor != null)
		{
			tvMusicName.setText("正在播放：" + curSongInfor.getMediaName());
			tvMusicNameTitle.setText("正在播放：" + curSongInfor.getMediaName());
			ivStatus.setImageDrawable(getResources().getDrawable(R.drawable.icon_play_status_stop));
			
			String msg = curSongInfor.getPlayMsg();
			if(TextUtils.isEmpty(msg))
				msg = getActivity().getResources().getString(R.string.song_message_default);
			
			marqueeTextView.clear();
			marqueeTextView.setText(msg);
		}
		else
		{
			tvMusicName.setText("等待点播...");
			tvMusicNameTitle.setText("等待点播...");
			ivStatus.setImageDrawable(getResources().getDrawable(R.drawable.icon_play_status_wait));
			
			//marqueeTextView.setText("人人点歌公测版正式上线，下载就送9999金币，欢迎大家体验！");
			
			//startTextScroll();
		}
		//startTextScroll();
	}
	
	public void updateSongList()
	{
		pageNum = 0;
		getPlayList(pageNum);
	}
	
	public void updateHandler()
	{
		MinaClient.getInstance().setOnConnectListener(getActivity(), handler);
	}
	
	private synchronized void getPlayList(final int pageNum)
	{
		if(isRunning)
			return;
		isRunning = true;
		
		tvHint.setVisibility(View.GONE);
		tvDianbo.setVisibility(View.GONE);
		tvDeviceSelect.setVisibility(View.GONE);
		
		if(pageNum == 0)
			showFooterLoading();
		
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				final boolean resut = MinaClient.getInstance().sendGetPlayList(getActivity(), pageNum);
			}
		}).start();
	}
	public void getCurPlaySong()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				final boolean resut = MinaClient.getInstance().sendGetPlayMusic(getActivity());
			}
		}).start();
	}
	
	private boolean isHasMore()
	{
		return songList.size() < total;
	}
	
	private void initAnimation()
	{
		float pivotValue = 0.5f;    // SUPPRESS CHECKSTYLE
        float toDegree = 720.0f;    // SUPPRESS CHECKSTYLE
        mRotateAnimation = new RotateAnimation(0.0f, toDegree, Animation.RELATIVE_TO_SELF, pivotValue,
                Animation.RELATIVE_TO_SELF, pivotValue);
        mRotateAnimation.setFillAfter(true);
        mRotateAnimation.setInterpolator(ANIMATION_INTERPOLATOR);
        mRotateAnimation.setDuration(ROTATION_ANIMATION_DURATION);
        mRotateAnimation.setRepeatCount(Animation.INFINITE);
        mRotateAnimation.setRepeatMode(Animation.RESTART);
	}
	private void refreshPrepare()
	{
		ivRefresh.setVisibility(View.VISIBLE);
		tvRefresh.setVisibility(View.VISIBLE);
		ivRefresh.clearAnimation();
		tvRefresh.setText("继续下拉刷新...");
	}
	private void refreshStart()
	{
		ivRefresh.setVisibility(View.VISIBLE);
		tvRefresh.setVisibility(View.VISIBLE);
		ivRefresh.clearAnimation();
		tvRefresh.setText("松开刷新数据");
	}
	private void refreshIng()
	{
		ivRefresh.setVisibility(View.VISIBLE);
		tvRefresh.setVisibility(View.VISIBLE);
		tvRefresh.setText("数据加载中...");
		ivRefresh.startAnimation(mRotateAnimation);
	}
	private void refreshFinish()
	{
		ivRefresh.setVisibility(View.INVISIBLE);
		tvRefresh.setVisibility(View.INVISIBLE);
		ivRefresh.clearAnimation();
	}
	
	private void showFooterLoading()
	{
		if(mDiangeManger.isDeviceFind(false))
		{
			mListView.showFooterView(true);
			pbFooterView.setVisibility(View.VISIBLE);
			tvFooterView.setText("正在加载...");
		}
	}
	private void showFooterNoMore()
	{
		if(mDiangeManger.isDeviceFind(false))
		{
			pbFooterView.setVisibility(View.INVISIBLE);
			tvFooterView.setText("没有更多数据了");
		}
	}
	private void showFooterLoadFail()
	{
		if(mDiangeManger.isDeviceFind(false))
		{
			pbFooterView.setVisibility(View.INVISIBLE);
			tvFooterView.setText("加载数据失败，请重试");
		}
	}
	
	private void showPlayListDialog()
	{
		
		showActivityAnim();
		
		PlayListDialog playListDialog = new PlayListDialog(this);
		
		playListDialog.show();
		
		WindowManager windowManager = getActivity().getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = playListDialog.getWindow().getAttributes();
		lp.width = (int)(display.getWidth()); //设置宽度
		lp.height = (int)(display.getHeight()); //设置高度
		playListDialog.getWindow().setAttributes(lp);
		playListDialog.setOnDismissListener(new OnDismissListener()
		{
			@Override
			public void onDismiss(DialogInterface arg0)
			{
				// TODO Auto-generated method stub
				clearActivityAnim();
			}
		});
	}
	
	public void showSongDetails(int pos)
	{
		if(pos < songList.size())
		{
			Bundle bundle = new Bundle();
			bundle.putSerializable("SongInfor", songList.get(pos));
			ViewUtils.startActivity(getActivity(), SongInforActivity.class, bundle, 3);
		}
	}
	public void deleteSong(int pos)
	{
		showProgressDialog(getActivity(), "正在处理...");
		sendDeleteCmd(songList.get(pos));
	}
	private void sendDeleteCmd(final SongInfor songInfor)
	{
		if(isRunning)
			return;
		isRunning = true;
		this.deleteSongInfor = songInfor;
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				final boolean result = MinaClient.getInstance().sendDelete(getActivity(), songInfor);
				if(!result)
				{
					showToast("操作失败");
					dismissDialog();
				}
				isRunning = false;
			}
		}).start();
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onPullZooming(int newScrollValue)
	{
		// TODO Auto-generated method stub
		//MyLog.e("newScrollValue-->"+newScrollValue);
		if(Math.abs(newScrollValue) >= 130)
		{
			isRefresh = true;
			refreshStart();
		}
		else
		{
			isRefresh = false;
			refreshPrepare();
		}
	}
	/**
	*callbacks
	*/
	@Override
	public void onPullZoomEnd()
	{
		// TODO Auto-generated method stub
		if(isRefresh)
		{
			pageNum = 0;
			getPlayList(pageNum);
			refreshIng();
		}
		else
			refreshFinish();
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState)
	{
		// TODO Auto-generated method stub
		//下拉到空闲是，且最后一个item的数等于数据的总数时，进行更新
		if(scrollState == PullToZoomListViewEx.SCROLL_STATE_IDLE)
		{
			if(lastItem == Constant.ONE_PAGE_SIZE * (pageNum + 1) && isHasMore())
	        { 
				showFooterLoading();
	        	++ pageNum;
	        	getPlayList(pageNum);
	        }
		}
	}
	/**
	*callbacks
	*/
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount)
	{
		// TODO Auto-generated method stub
        lastItem = firstVisibleItem + visibleItemCount - 2;  //减2是因为增加了header和footer
	}
	/**
	*callbacks
	*/
	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		if(v == viewHeaderBottom)
		{
			if(curSongInfor == null)
			{
				showToast("当前没有歌曲播放~");
				return;
			}
			Bundle bundle = new Bundle();
			bundle.putSerializable("SongInfor", curSongInfor);
			ViewUtils.startActivity(getActivity(), SongInforActivity.class, bundle, 3);
		}
		else if(v == viewHeaderTop)
		{
			if(curSongInfor == null)
			{
				showToast("当前没有歌曲播放~");
				return;
			}
			Bundle bundle = new Bundle();
			bundle.putSerializable("SongInfor", curSongInfor);
			ViewUtils.startActivity(getActivity(), SongInforActivity.class, bundle, 3);
		}
		else if(v == tvDeviceName)
		{
			//ViewUtils.startActivity(getActivity(), NetDeviceActivity.class, null);
		}
		else if(v == tvDianbo)
		{
			if(isReconnect)
				getPlayList(pageNum);
			else
				((MainActivity)getActivity()).loadShopPage();
		}
		else if(v == tvDeviceSelect)
		{
			//ViewUtils.startActivity(getActivity(), NetDeviceActivity.class, null);
		}
		else if(v == viewList)
		{
			//我的点播列表
			if(getDBManager().getSongCount() == 0)
				showToast("列表中没有数据哦~");
			else 
				showPlayListDialog();
		}
	}
	/**
	*callbacks
	*/
	@Override
	protected void lazyLoad()
	{
		// TODO Auto-generated method stub
		
	}
}
 
