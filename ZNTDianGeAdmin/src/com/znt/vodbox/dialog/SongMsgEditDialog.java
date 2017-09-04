
package com.znt.vodbox.dialog; 

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.znt.vodbox.R;
import com.znt.diange.mina.cmd.PlayCmd;
import com.znt.diange.mina.entity.SongInfor;
import com.znt.vodbox.dmc.engine.OnConnectHandler;
import com.znt.vodbox.factory.DiangeManger;
import com.znt.vodbox.mina.client.ClientHandler;
import com.znt.vodbox.mina.client.MinaClient;
import com.znt.vodbox.mina.client.ClientHandler.MinaErrorType;
import com.znt.vodbox.utils.MyToast;

/** 
 * @ClassName: HintDialog 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-10-19 下午1:53:23  
 */
public class SongMsgEditDialog extends Dialog
{

	private View parentView = null;
	private TextView tvCancel = null;
	private TextView tvConfirm = null;
	private EditText etContent = null;
	
	private Activity activity = null;
	private DiangeManger mDiangeManger = null;
	
	private SongInfor songInfor = null;
	private boolean isUpdate = false;
	
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
					/*int localConin = LocalDataEntity.newInstance(activity).getCoin();
					LocalDataEntity.newInstance(activity).setCoin((localConin + coinOld) - curCoin);*/
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
			}
			else if(msg.what == ClientHandler.TIME_OUT)
			{
				showToast(activity.getResources().getString(R.string.request_time_out));
			}
			else if(msg.what == OnConnectHandler.ON_NETWORK_RECONNECTED_SUCCESS)
			{
				sendUpdateCmd();
			}
		};
	};
	
	/** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param context 
	*/
	public SongMsgEditDialog(Activity activity)
	{
		super(activity, R.style.MMTheme_DataSheet);
		
		this.activity = activity;
		
		myToast = new MyToast(activity);
		mDiangeManger = new DiangeManger(activity);
		
		MinaClient.getInstance().setHandler(activity, handler);
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
		
		setContentView(R.layout.dialog_song_msg_edit);
		
		parentView = findViewById(R.id.view_song_msg_edit_dialog_bg);
		tvCancel = (TextView)findViewById(R.id.tv_song_msg_edit_cancel);
		tvConfirm = (TextView)findViewById(R.id.tv_song_msg_edit_confirm);
		etContent = (EditText)findViewById(R.id.et_dialog_song_msg_edit);
		
		tvCancel.setOnClickListener(new android.view.View.OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				dismiss();
			}
		});
		tvConfirm.setOnClickListener(new android.view.View.OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				sendUpdateCmd();
			}
		});
	}
	
	public void setSongInfor(SongInfor songInfor)
	{
		this.songInfor = songInfor;
		etContent.setText(songInfor.getPlayMsg());
	}
	
	public boolean isUpdate()
	{
		return isUpdate;
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
	
	private void sendUpdateCmd()
	{
		String message = etContent.getText().toString().trim();
		if(TextUtils.isEmpty(message))
		{
			showToast("请输入留言信息");
			return;
		}
		if(message.equals(songInfor.getPlayMsg()))
		{
			showToast("信息未更改");
			return;
		}
		
		songInfor.setPlayMsg(message);
		
		if(!mDiangeManger.isDeviceFind(true))
			return;
		
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
}
 
