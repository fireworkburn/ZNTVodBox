
package com.znt.diange.activity; 

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.znt.diange.R;
import com.znt.diange.dialog.CommonHintDialog;
import com.znt.diange.dialog.SongCoinSelectDialog;
import com.znt.diange.dialog.SongHintDialog;
import com.znt.diange.dlna.mediaserver.util.LogFactory;
import com.znt.diange.dmc.engine.OnConnectHandler;
import com.znt.diange.entity.Constant;
import com.znt.diange.entity.LocalDataEntity;
import com.znt.diange.factory.DiangeManger;
import com.znt.diange.factory.HttpFactory;
import com.znt.diange.http.HttpMsg;
import com.znt.diange.mina.client.ClientHandler;
import com.znt.diange.mina.client.MinaClient;
import com.znt.diange.mina.cmd.PlayCmd;
import com.znt.diange.mina.entity.MediaInfor;
import com.znt.diange.mina.entity.ResoureType;
import com.znt.diange.mina.entity.SongInfor;
import com.znt.diange.player.PlayFactory;
import com.znt.diange.timer.NetStatusTimer;
import com.znt.diange.utils.DownHelper;
import com.znt.diange.utils.StringUtils;
import com.znt.diange.utils.SystemUtils;
import com.znt.diange.utils.TimerUtils;
import com.znt.diange.utils.ViewUtils;
import com.znt.diange.utils.TimerUtils.OnTimeOverListner;
import com.znt.diange.view.ItemTextView;

/** 
 * @ClassName: SongPrepareActivity 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-7-20 下午2:15:58  
 */
public class SongPrepareActivity extends BaseActivity implements OnClickListener
{

	private TextView tvMusicName = null;
	private TextView tvMusicArtist = null;
	private TextView tvCurCoin = null;
	private TextView tvHint = null;
	private TextView tvCofirm = null;
	private EditText etMsg = null;
	private ProgressBar pbLoading = null;
	private ItemTextView itvCoin = null;
	
	private DiangeManger mDiangeManger = null;
	private PlayFactory playFactory = null;
	private DownHelper downHelper = null;
	private MediaInfor musicInfor = null;
	private SongInfor songInfor = new SongInfor();
	private TimerUtils timerUtils = null;
	private NetStatusTimer netStatusTimer = null;
	
	private HttpFactory httpFactory = null;
	
	private boolean isRunning = false;
	private boolean isCheckFail = false;
	private boolean isClickEnable = true;
	private long duration = -1;
	private final int MAX_DURATION = 10 * 60;
	private final int MIN_DURATION = 60;
	private int coinFreeze = 0;
	private boolean isCheck = true;
	private int sendType = 0;
	
	private final int NET_STATUS = 10;
	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == PlayFactory.GET_DURATION)
			{
				 duration = (Long)msg.obj;
				 duration = (int) Math.ceil((float)(duration / 1000));
				 showPrepareSuccess();
			}
			else if(msg.what == PlayFactory.PLAY_ERROR)
			{
				showPrepareFail();
			}
			else if(msg.what == ClientHandler.RECV_PLAY_RESULT)
			{
				
				dismissDialog();
				
				getDBManager().insertSong(songInfor);
				
				PlayCmd playCmd = (PlayCmd)msg.obj;
				String result  = playCmd.getResult();
				
				if(result.equals("0"))
				{
					showToast("点播成功");
					
					/*int totalCoin = getLocalData().getCoin();
					String coinStr = itvCoin.getSecondView().getText().toString().trim();
					int subCoin = 0;
					if(!TextUtils.isEmpty(coinStr))
						subCoin = Integer.parseInt(coinStr);
					getLocalData().setCoin(totalCoin - subCoin);*/
					
					Constant.isSongUpdate = true;
					Constant.isSongSended = true;
					//直接显示主页的点播列表
					MyActivityManager.getScreenManager().popActivity(LocalMusicActivity1.class);
					MyActivityManager.getScreenManager().popActivity(KuwoMusicActivity.class);
					MyActivityManager.getScreenManager().popActivity(KuwoCategoryActivity.class);
					MyActivityManager.getScreenManager().popActivity(SearchMusicActivity.class);
					MyActivityManager.getScreenManager().popActivity(KuwoSecondCategoryActivity.class);
					
					finish();
				}
				else if(result.equals("1"))
					showToast("点播失败，请重试");
				else if(result.equals("3"))//已经有点播了，需要提示更新
				{
					showAlertDialog(getActivity(), new OnClickListener()
					{
						@Override
						public void onClick(View arg0)
						{
							// TODO Auto-generated method stub
							dismissDialog();
							sendType = 1;
							sendPlayCmd();
						}
					},"重复点播提示", "您已经点播过，重复点播会覆盖上一次的歌曲，确认覆盖吗？");
				}
				else if(result.equals("4"))//该音响不允许点播第三方歌曲
				{
					showPlayPermissionDialog();
				}
				else if(result.equals("5"))//该音响禁止点歌
				{
					showPlayFobidenDialog();
				}
				
				isRunning = false;
			}
			else if(msg.what == ClientHandler.TIME_OUT)
			{
				dismissDialog();
				isRunning = false;
				showToast(getResources().getString(R.string.request_time_out));
			}
			else if(msg.what == ClientHandler.RECV_ERROR)
			{
				dismissDialog();
				String error = (String)msg.obj;
				if(error != null)
					showToast(error);
			}
			else if(msg.what == NET_STATUS)
			{
				String netStatus = showNetSpeed();
				LogFactory.createLog().e("******网络速度-->"+netStatus);
			}
			else if(msg.what == OnConnectHandler.ON_NETWORK_RECONNECTED_SUCCESS)
			{
				startPlay();
			}
			else if(msg.what == HttpMsg.CONIN_FREEZE_START)
			{
				
			}
			else if(msg.what == HttpMsg.CONIN_FREEZE_SUCCESS)
			{
				//获取金币处理的额交易id
				String trandId = (String)msg.obj;
				songInfor.setTrandId(trandId);
				getLocalData().removeCoin(coinFreeze);
				sendPlayCmd();
			}
			else if(msg.what == HttpMsg.CONIN_FREEZE_START)
			{
				showToast("操作失败");
			}
			else if(msg.what == HttpMsg.NO_NET_WORK_CONNECT)
			{
				showToast("无网络连接");
			}
		};
	};
	
	private long lastTotalRxBytes = 0;
	private long lastTimeStamp = 0;
	private String showNetSpeed() 
	{
	    long nowTotalRxBytes = getTotalRxBytes();
	    long nowTimeStamp = System.currentTimeMillis();
	    long timeSp = (nowTimeStamp - lastTimeStamp);
	    if(timeSp <= 0)
	    	return "";
	    long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / timeSp);//毫秒转换
	 
	    lastTimeStamp = nowTimeStamp;
	    lastTotalRxBytes = nowTotalRxBytes;
	    return String.valueOf(speed) + " kb/s";
	}
	private long getTotalRxBytes() 
	{
	    return TrafficStats.getUidRxBytes(getApplicationInfo().uid)==TrafficStats.UNSUPPORTED ? 0 :(TrafficStats.getTotalRxBytes()/1024);//转为KB
	}
	 
	private void showPlayPermissionDialog()
	{
		showAlertDialog(getActivity(), new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Bundle bundle = new Bundle();
				bundle.putBoolean("IS_LOCAL", false);
				bundle.putSerializable("DeviceInfor", mDiangeManger.getDeviceInfor());
				getLocalData().setDeviceInfor(mDiangeManger.getDeviceInfor());
				ViewUtils.startActivity(getActivity(), LocalMusicActivity1.class, bundle);
				
				dismissDialog();
				finish();
			}
		}, "点播权限提示", "当前设备只允许点播音响内置歌曲，确定去音响曲库吗？");
	}
	private void showPlayFobidenDialog()
	{
		CommonHintDialog commonHintDialog = new CommonHintDialog(getActivity());
		commonHintDialog.setTitle("点播权限提示");
		commonHintDialog.setInfor("当前音响禁止点歌，请联系管理员");
		
		commonHintDialog.show();
		
		WindowManager windowManager = getActivity().getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = commonHintDialog.getWindow().getAttributes();
		lp.width = (int)(display.getWidth()); 
		lp.height = (int)(display.getHeight()); 
		commonHintDialog.getWindow().setAttributes(lp);
	}
	
	/**
	*callbacks
	*/
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_song_prepare);
		
		setCenterString("歌曲点播");
		
		tvMusicName = (TextView)findViewById(R.id.tv_song_prepare_music_name);
		tvMusicArtist = (TextView)findViewById(R.id.tv_song_prepare_music_artist);
		tvCurCoin = (TextView)findViewById(R.id.tv_song_prepare_conin);
		tvHint = (TextView)findViewById(R.id.tv_song_prepare_hint);
		tvCofirm = (TextView)findViewById(R.id.tv_song_prepare_confirm);
		itvCoin = (ItemTextView)findViewById(R.id.itv_song_prepare_coin);
		etMsg = (EditText)findViewById(R.id.et_song_prepare_msg);
		pbLoading = (ProgressBar)findViewById(R.id.pb_song_prepare_loading);
		
		itvCoin.getFirstView().setText("插播金币：");
		itvCoin.showMoreButton(true);
		itvCoin.getIconView().setImageResource(R.drawable.icon_jinbi);
		itvCoin.setIconSize(26);
		itvCoin.setOnClick(this);
		itvCoin.showBottomLine(false);
		itvCoin.hideIocn();

		isCheck = getIntent().getBooleanExtra("IS_CHECK", isCheck);
		musicInfor = (MediaInfor)getIntent().getSerializableExtra("MediaInfor");
		
		songInfor = new SongInfor(musicInfor);
		
		httpFactory = new HttpFactory(getActivity(), handler);
		
		/*songInfor.setMediaName(musicInfor.getMediaName());
		songInfor.setMediaName(musicInfor.getMediaName());
		songInfor.setMediaType(musicInfor.getMediaType());
		songInfor.setMediaUrl(musicInfor.getMediaUrl());
		songInfor.setAlbumName(musicInfor.getAlbumName());
		songInfor.setAlbumUrl(musicInfor.getAlbumUrl());
		songInfor.setArtist(musicInfor.getArtist());
		songInfor.setMediaSize(musicInfor.getMediaSize());*/
		
		initViews();
		
		downHelper = new DownHelper();
		playFactory = new PlayFactory(handler);
		
		mDiangeManger = new DiangeManger(getActivity());
		
		timerUtils = new TimerUtils(new OnTimeOverListner()
		{
			@Override
			public void OnTimeOver()
			{
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable()
				{
					public void run()
					{
						showPrepareFail();
					}
				});
			}
		});
		
		startNetStatus();
		
		checkMediaInfor();
	}
	
	private void checkMediaInfor()
	{
		String type = musicInfor.getMediaType();
		
		if(type.equals(MediaInfor.MEDIA_TYPE_PHONE) || !isCheck)//手机本地歌曲
			handleLocalMusic();
		else if(type.equals(MediaInfor.MEDIA_TYPE_SPEAKER))//音响本地歌曲直接点播
			handleSpeakerMusic();
		else if(type.equals(MediaInfor.MEDIA_TYPE_NET))//网络歌曲
			handleOnlineMusic();
		
	}
	
	private void startNetStatus()
	{
		netStatusTimer = new NetStatusTimer(getActivity());
		netStatusTimer.setHandler(handler, NET_STATUS);
		netStatusTimer.startTimer();
	}
	private void stopNetStatus()
	{
		if(netStatusTimer != null)
			netStatusTimer.stopTimer();
	}
	
	private void handleSpeakerMusic()
	{
		isCheck = false;
		//转换播放地址
		/*String linkUrl = UrlUtils.getMediaPlayUrl(songInfor.getMediaUrl(), false);
		songInfor.setMediaUrl(linkUrl);*/
		showPrepareSuccess();
	}
	private void handleLocalMusic()
	{
		if(isCheck)
		{
			String tag = "";
			String url = songInfor.getMediaUrl();
			if(url.contains("."))
				tag = url.substring(url.lastIndexOf("."));
			long size = songInfor.getMediaSize();
			if(isLossless(tag) && size <= 50 * 1024 * 1024)
			{
				//是无损音乐，并且小于50M，不检查
				isCheck = false;
				showPrepareSuccess();
			}
			else
			{
				showPreparing();
				playFactory.startGetDuration(url);
			}
		}
		else
			showPrepareSuccess();
	}
	private void handleOnlineMusic()
	{
		showPreparing();
		if(!isCheckFail)
			getMusicUrl(songInfor);
		else
			playFactory.startGetDuration(songInfor.getMediaUrl());
	}
	
	private void showPreparing()
	{
		timerUtils.delayTime(30);//超时检查开始
		tvCofirm.setText("正在检查歌曲信息...");
		pbLoading.setVisibility(View.VISIBLE);
		tvHint.setText("");
		tvCofirm.setEnabled(false);
		enableInput(false);
	}
	private void showPrepareFail()
	{
		isRunning = false;
		
		timerUtils.cancel();
		
		isCheckFail = true;
		
		if(!getActivity().isFinishing())
		{
			tvHint.setTextColor(getResources().getColor(R.color.red));
			tvHint.setText("歌曲信息检查失败，请重试 \n 可能是该歌曲过大或者网络不稳定");
			tvCofirm.setText("重试");
			tvCofirm.setEnabled(true);
			pbLoading.setVisibility(View.GONE);
			enableInput(true);
		}
	}
	private void showPrepareSuccess()
	{
		isRunning = false;
		
		timerUtils.cancel();
		
		isCheckFail = false;
		
		pbLoading.setVisibility(View.GONE);
		tvCofirm.setEnabled(true);
		tvCofirm.setText("确认点播");
		
		if(isCheck)
		{
			if(duration > MAX_DURATION)
			{
				tvHint.setTextColor(getResources().getColor(R.color.red));
				//tvHint.setText("歌曲不能超过10分钟，请选择其他歌曲~ \n 当前歌曲时长：" + StringUtils.getStrTime((int)duration));
				tvHint.setText("该歌曲不支持点播哦,请选择其他歌曲~");
				
				isClickEnable = false;
				
			}
			else if(duration > 0 && duration < MIN_DURATION)
			{
				tvHint.setTextColor(getResources().getColor(R.color.red));
				//tvHint.setText("歌曲太短了哦，请选择其他歌曲~ \n 当前歌曲时长：" + StringUtils.getStrTime((int)duration));
				tvHint.setText("该歌曲不支持点播哦,请选择其他歌曲~");
				isClickEnable = false;
			}
			else 
			{
				tvHint.setTextColor(getResources().getColor(R.color.main_bg));
				tvHint.setText("当前歌曲时长：" + StringUtils.getStrTime((int)duration));
				enableInput(true);
				
				isClickEnable = true;
			}
		}
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
		playFactory.stopPlay();
		stopNetStatus();
		timerUtils.cancel();
		MinaClient.getInstance().setConnectStop();
		// TODO Auto-generated method stub
		super.onPause();
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
	
	private void initViews()
	{
		
		//showHintDialog();
		
		tvMusicName.setText(musicInfor.getMediaName());
		tvMusicArtist.setText(musicInfor.getArtist());
		tvCurCoin.setText("剩余金币: " + getLocalData().getCoin());
/*		tvCurCoin.setText(StringUtils.setColorText("剩余金币: " + getLocalData().getCoin()
				, " " + getLocalData().getCoin(), (float) 1.2, getResources().getColor(R.color.text_blue_off)));
*/		
		tvCofirm.setOnClickListener(this);
		
		//etCoin.setLable("插播金币：");
		itvCoin.getSecondView().setText("0");
		itvCoin.getSecondView().setSingleLine(true);
		itvCoin.getSecondView().setTextColor(getResources().getColor(R.color.text_blue_on));
		itvCoin.getSecondView().setTextSize(StringUtils.dip2px(getActivity(), 10));
		//etCoin.getEditText().setTextSize(StringUtils.dip2px(getActivity(), 12));
		
		etMsg.setInputType(InputType.TYPE_CLASS_TEXT |InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		
		SystemUtils.hideInputView(getActivity());
	}
	
	private void enableInput(boolean isEnable)
	{
		itvCoin.setFocusable(isEnable);
		itvCoin.setFocusableInTouchMode(isEnable);
		etMsg.setFocusable(isEnable);
		etMsg.setFocusableInTouchMode(isEnable);
	}
	
	private void showHintDialog()
	{
		
		if(getLocalData().getSongHintShow())
		{
			SongHintDialog hintDialog = new SongHintDialog(getActivity());
			
			hintDialog.show();
			
			WindowManager windowManager = getActivity().getWindowManager();
			Display display = windowManager.getDefaultDisplay();
			WindowManager.LayoutParams lp = hintDialog.getWindow().getAttributes();
			lp.width = (int)(display.getWidth()); //设置宽度
			lp.height = (int)(display.getHeight()); //设置高度
			hintDialog.getWindow().setAttributes(lp);
		}
	}
	
	private void getMusicUrl(final SongInfor songInfor)
	{
		
		if(isRunning)
			return;
		isRunning = true;
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				if(musicInfor.getMediaType().equals(MediaInfor.MEDIA_TYPE_NET))
				{
					if(musicInfor.getResourceType() == ResoureType.KUWO)
					{
						//酷我网络音乐要重新获取链接
						String musicUrl = downHelper.getDlAndPath(musicInfor.getMediaId());
						songInfor.setMediaUrl(musicUrl);
					}
				}
				
				playFactory.startGetDuration(songInfor.getMediaUrl());
				
				isRunning = false;
				
			}
		}).start();
	}
	
	private void startPlay()
	{
		if(musicInfor.getMediaType().equals(MediaInfor.MEDIA_TYPE_NET))
		{
			if(!isOnline())
				return;
		}
		
		if(isCheckFail)
		{
			checkMediaInfor();
			return;
		}
		
		String coin = itvCoin.getSecondView().getText().toString().trim();
		if(TextUtils.isEmpty(coin))
			coin = "0";
		
		coinFreeze = Integer.parseInt(coin);
		int total = getLocalData().getCoin();
		
		if(total < coinFreeze)
		{
			showToast("金币不够拉~");
			return;
		}
		
		/*if(musicInfor.getMediaType().equals(MediaInfor.MEDIA_TYPE_PHONE))
		{
			//转换播放地址
			String linkUrl = UrlUtils.getMediaPlayUrl(songInfor.getMediaUrl(), true);
			songInfor.setMediaUrl(linkUrl);
		}*/
		
		songInfor.setPlayMsg(etMsg.getText().toString().trim());
		songInfor.setCoin(coinFreeze);
		songInfor.setUserInfor(LocalDataEntity.newInstance(getActivity()).getUserInfor());
		
		sendType = 0;
		if(isWifiApConnect() || TextUtils.isEmpty(coin) || coin.equals("0"))
		{
			sendPlayCmd();
		}
		else
			httpFactory.coinFreeze(coin);
	}
	private void sendPlayCmd()
	{
		if(isRunning)
			return;
		isRunning = true;
		
		showProgressDialog(getActivity(), "正在处理...", true);
		
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				final boolean result = MinaClient.getInstance().sendPlay(getActivity(), sendType, songInfor);
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						// TODO Auto-generated method stub
						if(!result)
						{
							showToast("点播失败，请重试");
							dismissDialog();
							isRunning = false;
						}
					}
				});
			}
		}).start();
	}

	/**
	*callbacks
	*/
	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		if(v == tvCofirm)
		{
			if(!mDiangeManger.isDiangeEnable())
			{
				return;
			}
			
			if(!isClickEnable)
				return;
			
			startPlay();
			
		}
		else if(v == itvCoin.getBgView())
		{
			showCoinSelectDialog();
		}
	}
	
	private void showCoinSelectDialog()
	{
		
		showActivityAnim();
		
		final SongCoinSelectDialog songCoinSelectDialog = new SongCoinSelectDialog(getActivity());
		
		songCoinSelectDialog.show();
		
		WindowManager windowManager = getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = songCoinSelectDialog.getWindow().getAttributes();
		lp.width = (int)(display.getWidth()); //设置宽度
		lp.height = (int)(display.getHeight()); //设置高度
		songCoinSelectDialog.getWindow().setAttributes(lp);
		songCoinSelectDialog.setOnDismissListener(new OnDismissListener()
		{
			@Override
			public void onDismiss(DialogInterface arg0)
			{
				// TODO Auto-generated method stub
				if(songCoinSelectDialog.isUpdate())
				{
					int coin = songCoinSelectDialog.getCurrentCoin();
					itvCoin.getSecondView().setText("" + coin);
				}
				songCoinSelectDialog.stopHttp();
				MinaClient.getInstance().setHandler(getActivity(), handler);
				clearActivityAnim();
			}
		});
	}

	private boolean isOnline()
	{
		
		if(!SystemUtils.isNetConnected(getActivity()))
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
			showToast("当前网络只能播放本地歌曲哦~");
		}
		else
			result = true;
		
		return result;
	}
	
	private boolean isLossless(String tag)
	{
		if(TextUtils.isEmpty(tag))
			return false;
		return tag.toLowerCase().equals(".flac") || 
				tag.toLowerCase().equals(".ape");
	}
}
 
