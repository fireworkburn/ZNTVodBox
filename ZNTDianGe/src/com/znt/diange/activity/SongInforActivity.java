
package com.znt.diange.activity;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.znt.diange.R;
import com.znt.diange.dialog.SongCoinSelectDialog;
import com.znt.diange.dmc.engine.OnConnectHandler;
import com.znt.diange.factory.DiangeManger;
import com.znt.diange.mina.client.ClientHandler;
import com.znt.diange.mina.client.ClientHandler.MinaErrorType;
import com.znt.diange.mina.client.MinaClient;
import com.znt.diange.mina.cmd.DeleteSongCmd;
import com.znt.diange.mina.cmd.PlayCmd;
import com.znt.diange.mina.cmd.StopCmd;
import com.znt.diange.mina.entity.SongInfor;
import com.znt.diange.utils.StringUtils;
import com.znt.diange.view.EditTextView;
import com.znt.diange.view.ItemTextView;

/** 
* @ClassName: MySongActivity 
* @Description: TODO
* @author yan.yu 
* @date 2015年7月19日 下午9:20:44  
*/
public class SongInforActivity extends BaseActivity implements OnClickListener
{

	private TextView tvName = null;
	private TextView tvArtist = null;
	private TextView tvDelete = null;
	private TextView tvStop = null;
	private TextView tvConfirm = null;
	private TextView tvCoinTotal = null;
	private TextView tvUserName = null;
	private EditTextView etMessage = null;
	private ItemTextView itvCoin = null;
	private View viewBottom = null;
	private View viewSpace1 = null;
	private View viewSpace2 = null;
	private View viewSpace3 = null;
	
	private DiangeManger mDiangeManger = null;
	
	private SongInfor songInfor = null;
	private boolean isRunning = false;
	private int coinOld = 0;
	private int curCoin = 0;
	private String oldMsg = "";
	
	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == ClientHandler.RECV_PLAY_RESULT)
			{
				dismissDialog();
				
				PlayCmd cmdInfor = (PlayCmd)msg.obj;
				String result = ((PlayCmd)cmdInfor).getResult();
				if(result.equals("0"))
				{
					getLocalData().setCoin((getLocalData().getCoin() + coinOld) - curCoin);
					
					showToast("更新成功~");
					
					setResult(RESULT_OK);
					finish();
				}
				else if(result.equals("1"))
				{
					showToast("更新失败，请重新操作~");
				}
				else if(result.equals("2"))
				{
					showToast("歌曲不存在或者该歌曲已经在播放~");
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
					setResult(RESULT_OK);
					finish();
				}
				else
					showToast("删除失败");
			}
			else if(msg.what == ClientHandler.RECV_STOP_RESULT)
			{
				dismissDialog();
				StopCmd stopCmd = (StopCmd)msg.obj;
				String result = stopCmd.getResult();
				if(result.equals("0"))
				{
					showToast("停止成功");
					setResult(RESULT_OK);
					finish();
				}
				else
					showToast("操作失败");
			}
			else if(msg.what == ClientHandler.MINA_CONNECT_ERROR)
			{
				MinaErrorType type = (MinaErrorType)msg.obj;
				
				isRunning = false;
				dismissDialog();
				if(type == MinaErrorType.CLOSED)
					showToast("提示：服务器断开连接");
				else if(type == MinaErrorType.EXCEPTION)
					showToast("操作失败，服务器异常");
				else if(type == MinaErrorType.IDLE)
					showToast("操作失败");
			}
			else if(msg.what == ClientHandler.TIME_OUT)
			{
				dismissDialog();
				isRunning = false;
				showToast(getResources().getString(R.string.request_time_out));
			}
			else if(msg.what == OnConnectHandler.ON_NETWORK_RECONNECTED_SUCCESS)
			{
				sendUpdateCmd(songInfor);
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
		
		setContentView(R.layout.activity_song_infor);
		
		tvName = (TextView)findViewById(R.id.tv_song_infor_name);
		tvArtist = (TextView)findViewById(R.id.tv_song_infor_artist);
		tvDelete = (TextView)findViewById(R.id.tv_song_infor_delete);
		tvStop = (TextView)findViewById(R.id.tv_song_infor_stop);
		tvConfirm = (TextView)findViewById(R.id.tv_song_infor_confirm);
		tvCoinTotal = (TextView)findViewById(R.id.tv_song_infor_coin);
		tvUserName = (TextView)findViewById(R.id.tv_song_infor_user_name);
		itvCoin = (ItemTextView)findViewById(R.id.itv_song_infor_coin);
		etMessage = (EditTextView)findViewById(R.id.et_song_infor_message);
		viewSpace1 = findViewById(R.id.view_song_infor_space1);
		viewSpace2 = findViewById(R.id.view_song_infor_space2);
		viewSpace3 = findViewById(R.id.view_song_infor_space3);
		viewBottom = findViewById(R.id.view_song_infor_bottom);
		
		itvCoin.getFirstView().setText("插播金币：");
		itvCoin.showMoreButton(true);
		itvCoin.getIconView().setImageResource(R.drawable.icon_jinbi);
		itvCoin.setIconSize(26);
		itvCoin.setOnClick(this);
		itvCoin.showBottomLine(false);
		itvCoin.hideIocn();
		
		mDiangeManger = new DiangeManger(getActivity());
		
		songInfor = (SongInfor)getIntent().getSerializableExtra("SongInfor");
		coinOld = songInfor.getCoin();
		oldMsg = songInfor.getPlayMsg();
		
		if(TextUtils.isEmpty(oldMsg))
			oldMsg = getResources().getString(R.string.song_message_default);
		
		initViews();
		
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
	
	/**
	*callbacks
	*/
	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		
		super.onDestroy();
	}
	
	private void initViews()
	{
		tvDelete.setOnClickListener(this);
		tvConfirm.setOnClickListener(this);
		tvStop.setOnClickListener(this);
		
		tvName.setText(songInfor.getMediaName());
		tvArtist.setText(songInfor.getArtist());
		
		if(songInfor.getUserInfor() != null)
		{
			if(isMySong())
				showMySongViews();
			else
				showOtherViews();
		}
		else
			showDefaultViews();
		
		//etCoin.setLable("插播金币：");
		itvCoin.getSecondView().setText(songInfor.getCoin() + "");
		itvCoin.getSecondView().setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED |InputType.TYPE_CLASS_NUMBER);
		//etCoin.getLable().setTextColor(getResources().getColor(R.color.text_blue_on));
		itvCoin.getSecondView().setTextColor(getResources().getColor(R.color.text_blue_on));
		itvCoin.getSecondView().setTextSize(StringUtils.dip2px(getActivity(), 10));
		
		etMessage.setLable("附加信息：");
		etMessage.setHint("说点什么吧......");
		etMessage.setInputType(InputType.TYPE_CLASS_TEXT |InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		etMessage.setMaxCount(80);
		etMessage.showTopLine(false);
		etMessage.showBottomLine(false);
		etMessage.setMinLines(6);
		if(TextUtils.isEmpty(songInfor.getPlayMsg()))
			etMessage.setText(R.string.song_message_default);
		else
			etMessage.setText(songInfor.getPlayMsg());
		
	}

	private boolean isMySong()
	{
		return songInfor.getUserInfor().getUserId().equals(getLocalData().getUserInfor().getUserId());
	}
	
	private void showMySongViews()
	{
		if(songInfor.getPlayState() == 0)
		{
			tvCoinTotal.setVisibility(View.VISIBLE);
			viewBottom.setVisibility(View.VISIBLE);
			tvStop.setVisibility(View.GONE);
			tvUserName.setVisibility(View.GONE);
			
			itvCoin.getBgView().setClickable(false);
			etMessage.setInputEnable(true); 
		}
		else
		{
			viewBottom.setVisibility(View.VISIBLE);
			tvConfirm.setVisibility(View.GONE);
			tvDelete.setVisibility(View.GONE);
			tvStop.setVisibility(View.VISIBLE);
			tvCoinTotal.setVisibility(View.GONE);
			
			tvUserName.setVisibility(View.GONE);
			
			itvCoin.getBgView().setClickable(false);
			etMessage.setInputEnable(false); 
		}
		
		setCenterString("我的点播");
		showMyCoins();
		
	}
	private void showDefaultViews()
	{
		tvCoinTotal.setVisibility(View.VISIBLE);
		viewBottom.setVisibility(View.GONE);
		itvCoin.setVisibility(View.GONE);
		etMessage.setVisibility(View.GONE);
		viewSpace1.setVisibility(View.GONE);
		viewSpace2.setVisibility(View.GONE);
		viewSpace3.setVisibility(View.GONE);
		
		/*etCoin.setFocusable(false); 
		etCoin.setFocusableInTouchMode(false); 
		etMessage.setFocusable(false); 
		etMessage.setFocusableInTouchMode(false); */
		
		setCenterString("系统歌曲");
		showMyCoins();
		
	}
	private void showOtherViews()
	{
		tvCoinTotal.setVisibility(View.GONE);
		viewBottom.setVisibility(View.GONE);
		tvUserName.setVisibility(View.VISIBLE);
		
		itvCoin.getBgView().setClickable(false); 
		etMessage.setInputEnable(false); 
		etMessage.setVisibility(View.GONE);
		findViewById(R.id.view_song_infor_space2).setVisibility(View.GONE);
		findViewById(R.id.view_song_infor_space3).setVisibility(View.GONE);
		
		setCenterString("点播信息");
		
		tvUserName.setText(StringUtils.setColorText("点播人:" + songInfor.getUserInfor().getUserName()
				, songInfor.getUserInfor().getUserName(), (float) 1.0, getResources().getColor(R.color.text_blue_off)));
		
		
	}
	
	private void showMyCoins()
	{
		tvCoinTotal.setText(StringUtils.setColorText("我的金币： " + getLocalData().getCoin()
				, " " + getLocalData().getCoin(), (float) 1.2, getResources().getColor(R.color.text_blue_off)));
	}
	
	private void sendUpdateCmd(final SongInfor songInfor)
	{
		String coinStr = itvCoin.getSecondView().getText().toString().trim();
		String message = etMessage.getText().toString().trim();
		if(TextUtils.isEmpty(coinStr))
			coinStr = "0";
		curCoin = Integer.parseInt(coinStr);
		if(curCoin == coinOld && message.equals(oldMsg))
		{
			showToast("信息未改变");
			return;
		}
		if((getLocalData().getCoin() + coinOld) - curCoin < 0)
		{
			showToast("金币不够哦~");
			return;
		}
		
		if(isRunning)
			return;
		isRunning = true;
		
		songInfor.setCoin(curCoin);
		songInfor.setPlayMsg(message);
		
		showProgressDialog(getActivity(), "正在处理...");
		
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				final boolean result = MinaClient.getInstance().sendPlay(getActivity(), 1, songInfor);
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						// TODO Auto-generated method stub
						if(!result)
						{
							dismissDialog();
							showToast("更新失败");
						}
					}
				});
				isRunning = false;
			}
		}).start();
	}
	private void sendDeleteCmd(final SongInfor songInfor)
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
	
	private void sendStopCmd()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				
				final boolean result = MinaClient.getInstance().sendStop(getActivity(), songInfor);
				runOnUiThread(new Runnable()
				{
					
					@Override
					public void run()
					{
						// TODO Auto-generated method stub
						if(!result)
						{
							showToast("操作失败");
							dismissDialog();
						}
					}
				});
				isRunning = false;
			}
		}).start();
	}
	
	private void showStopDialog()
	{
		showAlertDialog(getActivity(), new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				// TODO Auto-generated method stub
				dismissDialog();
				
				showProgressDialog(getActivity(), "正在处理...");
				sendStopCmd();
			}
		}, null, "当前歌曲将会停止播放，确定停止吗？");
	}
	private void showDeleteDialog()
	{
		showAlertDialog(getActivity(), new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				// TODO Auto-generated method stub
				dismissDialog();
				if(mDiangeManger.isDeviceFind(true))
				{
					showProgressDialog(getActivity(), "正在处理...");
					
					sendDeleteCmd(songInfor);
				}
			}
		}, null, "确定删除当前歌曲吗？");
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		if(v == tvDelete)
		{
			showDeleteDialog();
		}
		else if(v == tvConfirm)
		{
			if(mDiangeManger.isDeviceFind(true))
				sendUpdateCmd(songInfor);
		}
		else if(v == tvStop)
		{
			if(mDiangeManger.isDeviceFind(true))
				showStopDialog();
		}
		else if(v == itvCoin.getBgView())
		{
			showCoinSelectDialog();
		}
	}
	
	private void showCoinSelectDialog()
	{
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
			}
		});
	}
}
 
