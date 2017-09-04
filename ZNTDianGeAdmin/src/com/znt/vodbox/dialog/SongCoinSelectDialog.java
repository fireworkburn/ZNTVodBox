
package com.znt.vodbox.dialog; 

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.znt.vodbox.R;
import com.znt.diange.mina.cmd.PlayCmd;
import com.znt.diange.mina.entity.SongInfor;
import com.znt.vodbox.entity.Constant;
import com.znt.vodbox.entity.LocalDataEntity;
import com.znt.vodbox.factory.HttpFactory;
import com.znt.vodbox.http.HttpMsg;
import com.znt.vodbox.mina.client.ClientHandler;
import com.znt.vodbox.mina.client.MinaClient;
import com.znt.vodbox.mina.client.ClientHandler.MinaErrorType;
import com.znt.vodbox.utils.MyToast;
import com.znt.vodbox.utils.SystemUtils;

/** 
 * @ClassName: HintDialog 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-10-19 下午1:53:23  
 */
public class SongCoinSelectDialog extends Dialog implements android.view.View.OnClickListener
{

	private View parentView = null;
	private TextView tvCancel = null;
	private TextView tvConfirm = null;
	private TextView tvCoinTotal = null;
	private TextView tvCoin1 = null;
	private TextView tvCoin2 = null;
	private TextView tvCoin3 = null;
	private TextView tvCoin4 = null;
	private TextView tvCoin5 = null;
	private TextView tvCoin6 = null;
	private TextView tvCoin7 = null;
	private TextView tvCoin8 = null;
	private TextView tvCoin9 = null;
	private ProgressBar progressBar = null;
	private List<TextView> tvConins = new ArrayList<TextView>();
	
	private Activity activity = null;
	
	private HttpFactory httpFactory = null;
	private SongInfor songInfor = null;
	private boolean isUpdate = false;
	private boolean isRunning = false;
	private int coinOld = 0;
	private int curCoin = 0;
	
	private MyToast myToast = null;
	
	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == ClientHandler.RECV_PLAY_RESULT)
			{
				PlayCmd cmdInfor = (PlayCmd)msg.obj;
				String result = ((PlayCmd)cmdInfor).getResult();
				if(result.equals("0"))
				{
					showToast("更新成功~");
					dismiss();
					isUpdate = true;
				}
				else if(result.equals("1"))
				{
					showToast("更新失败，请重新操作~");
				}
				else if(result.equals("2"))
				{
					showToast("歌曲不存在或者该歌曲已经在播放~");
				}
				showLoadingView(false);
			}
			else if(msg.what == ClientHandler.MINA_CONNECT_ERROR)
			{
				MinaErrorType type = (MinaErrorType)msg.obj;
				
				if(type == MinaErrorType.CLOSED)
					showToast("提示：服务器断开连接");
				else if(type == MinaErrorType.EXCEPTION)
					showToast("操作失败，服务器异常");
				else if(type == MinaErrorType.IDLE)
					showToast("操作失败");
				showLoadingView(false);
			}
			else if(msg.what == ClientHandler.TIME_OUT)
			{
				showToast(activity.getResources().getString(R.string.request_time_out));
				showLoadingView(false);
			}
			else if(msg.what == HttpMsg.CONIN_FREEZE_START)
			{
				showLoadingView(true);
			}
			else if(msg.what == HttpMsg.CONIN_FREEZE_SUCCESS)
			{
				//获取金币处理的额交易id
				String trandId = (String)msg.obj;
				songInfor.setTrandId(trandId);

				LocalDataEntity.newInstance(activity).removeCoin(curCoin);
				sendUpdateCmd();
			}
			else if(msg.what == HttpMsg.CONIN_FREEZE_FAIL)
			{
				showToast("操作失败");
				showLoadingView(false);
			}
			else if(msg.what == HttpMsg.NO_NET_WORK_CONNECT)
			{
				showToast("无网络连接");
			}
			else if(msg.what == HttpMsg.CONIN_FREEZE_CANCEL_SUCCESS)
			{
				if(songInfor != null)
					songInfor.setCoin(curCoin);
				if(curCoin > 0)
					httpFactory.coinFreeze(curCoin + "");
				else
					sendUpdateCmd();
			}
		};
	};
	/** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param context 
	*/
	public SongCoinSelectDialog(Activity activity)
	{
		super(activity, R.style.MMTheme_DataSheet);
		
		this.activity = activity;
		myToast = new MyToast(activity);
		
		MinaClient.getInstance().setHandler(activity, handler);
		httpFactory = new HttpFactory(activity, handler);
		
		// TODO Auto-generated constructor stub
	}
	
	private void showToast(String infor)
	{
		myToast.show(infor);
	}
	
	/**
	*callbacks
	*/
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.dialog_song_coin_select);
		
		parentView = findViewById(R.id.view_song_coin_select_dialog_bg);
		tvCancel = (TextView)findViewById(R.id.tv_song_coin_select_cancel);
		tvConfirm = (TextView)findViewById(R.id.tv_song_coin_select_confirm);
		tvCoinTotal = (TextView)findViewById(R.id.tv_song_coin_select_total);
		tvCoin1 = (TextView)findViewById(R.id.tv_coin_select_one);
		tvCoin2 = (TextView)findViewById(R.id.tv_coin_select_two);
		tvCoin3 = (TextView)findViewById(R.id.tv_coin_select_three);
		tvCoin4 = (TextView)findViewById(R.id.tv_coin_select_four);
		tvCoin5 = (TextView)findViewById(R.id.tv_coin_select_five);
		tvCoin6 = (TextView)findViewById(R.id.tv_coin_select_six);
		tvCoin7 = (TextView)findViewById(R.id.tv_coin_select_seven);
		tvCoin8 = (TextView)findViewById(R.id.tv_coin_select_eight);
		tvCoin9 = (TextView)findViewById(R.id.tv_coin_select_nine);
		progressBar = (ProgressBar)findViewById(R.id.pb_coin_select_operation);
		
		tvCoin1.setTag(10);
		tvCoin2.setTag(20);
		tvCoin3.setTag(50);
		tvCoin4.setTag(100);
		tvCoin5.setTag(200);
		tvCoin6.setTag(300);
		tvCoin7.setTag(500);
		tvCoin8.setTag(800);
		tvCoin9.setTag(1000);
		
		tvCoin1.setOnClickListener(this);
		tvCoin2.setOnClickListener(this);
		tvCoin3.setOnClickListener(this);
		tvCoin4.setOnClickListener(this);
		tvCoin5.setOnClickListener(this);
		tvCoin6.setOnClickListener(this);
		tvCoin7.setOnClickListener(this);
		tvCoin8.setOnClickListener(this);
		tvCoin9.setOnClickListener(this);
		
		tvConins.add(tvCoin1);
		tvConins.add(tvCoin2);
		tvConins.add(tvCoin3);
		tvConins.add(tvCoin4);
		tvConins.add(tvCoin5);
		tvConins.add(tvCoin6);
		tvConins.add(tvCoin7);
		tvConins.add(tvCoin8);
		tvConins.add(tvCoin9);
		
		tvCoinTotal.setText("剩余金币:" + LocalDataEntity.newInstance(activity).getCoin());
		
		tvCancel.setOnClickListener(this);
		tvConfirm.setOnClickListener(this);
	}
	
	private void showLoadingView(boolean isShow)
	{
		if(isShow)
		{
			progressBar.setVisibility(View.VISIBLE);
			//viewOperation.setVisibility(View.INVISIBLE);
		}
		else
		{
			progressBar.setVisibility(View.GONE);
			//viewOperation.setVisibility(View.VISIBLE);
		}
	}
	
	public void stopHttp()
	{
		if(httpFactory != null)
			httpFactory.stopHttp();
	}
	
	public void setSongInfor(SongInfor songInfor)
	{
		this.songInfor = songInfor;
		
		coinOld = songInfor.getCoin();
		curCoin = 0;
		
		int size = tvConins.size();
		for(int i=0;i<size;i++)
		{
			TextView textView = tvConins.get(i);
			int tempCoin = (Integer) textView.getTag();
			if(coinOld == tempCoin)
			{
				textView.setSelected(true);
				textView.setTextColor(activity.getResources().getColor(R.color.white));
			}
			else
			{
				textView.setTextColor(activity.getResources().getColor(R.color.text_blue_on));
				textView.setSelected(false);
			}
		}
	}
	
	public boolean isUpdate()
	{
		return isUpdate;
	}
	
	public int getCurrentCoin()
	{
		return curCoin;
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onBackPressed()
	{
		// TODO Auto-generated method stub
		super.onBackPressed();
	}
	
	public boolean isWifiApConnect()
	{
		String curSsid = SystemUtils.getConnectWifiSsid(activity);
		if(TextUtils.isEmpty(curSsid))
			return false;
		return curSsid.endsWith(Constant.UUID_TAG);
	}
	
	private void sendUpdateCmd()
	{
		
		if(!MinaClient.getInstance().isConnected())
		{
			showLoadingView(false);
			showToast("服务器断开，正在重新连接");
			return;
		}
		showLoadingView(true);
		
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				final boolean result = MinaClient.getInstance().sendPlay(activity, 1, songInfor);
				activity.runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						// TODO Auto-generated method stub
						if(!result)
						{
							dismiss();
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
		if(v == tvCancel)
		{
			isUpdate = false;
			dismiss();
		}
		else if(v == tvConfirm)
		{
			
			if(isRunning)
				return;
			
			isRunning = true;
			
			if(songInfor != null)
			{
				if(curCoin == 0)
				{
					dismiss();
					return;
				}
				if(coinOld == curCoin)
				{
					dismiss();
					return;
				}
				
				if((LocalDataEntity.newInstance(activity).getCoin() + coinOld) - curCoin < 0)
				{
					showToast("金币不够了哦~");
					return;
				}
				if(isWifiApConnect())
				{
					songInfor.setCoin(curCoin);
					sendUpdateCmd();
					return;
				}
				
				showLoadingView(true);
				
				if(coinOld > 0)
				{
					//先取消前面冻结的金币
					httpFactory.coinFreezeCancel(songInfor);
				}
				else
				{
					if(curCoin > 0)
					{
						songInfor.setCoin(curCoin);
						httpFactory.coinFreeze(curCoin + "");
					}
					else
						sendUpdateCmd();
				}
				//curCoin = Integer.parseInt(coin);
			}
			else
			{
				isUpdate = true;
				dismiss();
			}
		}
		else
		{
			int size = tvConins.size();
			for(int i=0;i<size;i++)
			{
				TextView textView = tvConins.get(i);
				if(v == textView)
				{
					textView.setSelected(true);
					textView.setTextColor(activity.getResources().getColor(R.color.white));
					curCoin = (Integer) textView.getTag();
				}
				else
				{
					textView.setTextColor(activity.getResources().getColor(R.color.text_blue_on));
					textView.setSelected(false);
				}
			}
		}
	}
}
 
