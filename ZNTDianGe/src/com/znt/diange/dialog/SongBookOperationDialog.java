
package com.znt.diange.dialog; 

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.znt.diange.R;
import com.znt.diange.activity.SongPrepareActivity;
import com.znt.diange.db.DBManager;
import com.znt.diange.factory.HttpFactory;
import com.znt.diange.http.HttpMsg;
import com.znt.diange.mina.entity.MediaInfor;
import com.znt.diange.mina.entity.ResoureType;
import com.znt.diange.mina.entity.SongInfor;
import com.znt.diange.utils.DownHelper;
import com.znt.diange.utils.ViewUtils;

/** 
 * @ClassName: PlayListDialog 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-9-19 下午3:49:41  
 */
public class SongBookOperationDialog extends Dialog implements OnItemClickListener
{

	private View parentView = null;
	private Activity activity = null;
	private TextView tvPlay = null;
	private TextView tvDianBo = null;
	private TextView tvDelete = null;
	private TextView tvCancel = null;
	private ProgressBar progressBar = null;
	private View viewOperation = null;
	
	private DownHelper downHelper = null;
	private HttpFactory httpFactory = null;
	
	private MediaInfor mediaInfor = null;
	private boolean isDeleteSuccess = false;
	private boolean isRunning = false;
	private boolean isSpeakerMusic = false;
	
	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == 0)
			{
				String musicUrl = (String)msg.obj;
				if(!TextUtils.isEmpty(musicUrl))
				{
					mediaInfor.setMediaUrl(musicUrl);
					SongInfor songInfor = new SongInfor(mediaInfor);
					DBManager.newInstance(activity).insertSong(songInfor);
					
					Toast.makeText(activity, "添加成功", 0).show();
					dismiss();
				}
				else
					Toast.makeText(activity, "歌曲添加失败", 0).show();
			}
			else if(msg.what == HttpMsg.DELETE_SPEAKER_MUSIC_START)
			{
				isRunning = true;
				showLoadingView(true);
			}
			else if(msg.what == HttpMsg.DELETE_SPEAKER_MUSIC_SUCCESS)
			{
				isDeleteSuccess = true;
				isRunning = false;
				Toast.makeText(activity, "删除成功", 0).show();
				dismiss();
				showLoadingView(false);
			}
			else if(msg.what == HttpMsg.DELETE_SPEAKER_MUSIC_FAIL)
			{
				isDeleteSuccess = false;
				isRunning = false;
				String error = (String)msg.obj;
				if(TextUtils.isEmpty(error))
					error = "添加失败";
				Toast.makeText(activity, error, 0).show();
				dismiss();
				showLoadingView(false);
			}
			
		};
	};
	
	public SongBookOperationDialog(Activity activity, MediaInfor mediaInfor) 
	{
		super(activity, R.style.MMTheme_DataSheet);
		// TODO Auto-generated constructor stub
		this.activity = activity;
		this.mediaInfor = mediaInfor;
		httpFactory = new HttpFactory(activity, handler);
	}
	
	public void setSpeakerMusic(boolean isSpeakerMusic)
	{
		this.isSpeakerMusic = isSpeakerMusic;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.dialog_song_book_operation);
		
		parentView = findViewById(R.id.view_song_book_operation_dialog_bg);
		
		tvCancel = (TextView)findViewById(R.id.tv_song_book_operation_cancel);
		tvPlay = (TextView)findViewById(R.id.tv_song_book_operation_play);
		tvDianBo = (TextView)findViewById(R.id.tv_song_book_operation_dianbo);
		tvDelete = (TextView)findViewById(R.id.tv_song_book_operation_delete);
		progressBar = (ProgressBar)findViewById(R.id.pb_song_book_operation);
		viewOperation = findViewById(R.id.view_song_book_operation);
		
		downHelper = new DownHelper();
		
		parentView.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) 
			{
				// TODO Auto-generated method stub
				dismiss();
				return false;
			}
		});
		tvCancel.setOnClickListener(new android.view.View.OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				dismiss();
			}
		});
		tvPlay.setOnClickListener(new android.view.View.OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				showPlayDialog(mediaInfor);
				dismiss();
			}
		});
		tvDianBo.setOnClickListener(new android.view.View.OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				Bundle bundle = new Bundle();
				if(isSpeakerMusic)
					mediaInfor.setMediaType(MediaInfor.MEDIA_TYPE_SPEAKER);
				bundle.putSerializable("MediaInfor", mediaInfor);
				ViewUtils.startActivity(activity, SongPrepareActivity.class, bundle);
				dismiss();
			}
		});
		tvDelete.setOnClickListener(new android.view.View.OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				//getMusicUrl();
				/*SongInfor songInfor = new SongInfor(mediaInfor);
				DBManager.newInstance(activity).insertSong(songInfor);
				
				Toast.makeText(activity, "添加成功", 0).show();*/
				if(isRunning)
					return;
				httpFactory.deleteSpeakerMusic(mediaInfor);
			}
		});
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
	
	public boolean isDeleteSuccess()
	{
		return isDeleteSuccess;
	}
	
	private void getMusicUrl()
    {
    	new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				String musicUrl = null;
				if(mediaInfor.getMediaType().equals(MediaInfor.MEDIA_TYPE_NET))
				{
					//网络音乐要重新获取链接
					if(mediaInfor.getResourceType() == ResoureType.KUWO)
						musicUrl = downHelper.getDlAndPath(mediaInfor.getMediaId());
					else if(mediaInfor.getResourceType() == ResoureType.WANGYI)
						musicUrl = mediaInfor.getMediaUrl();
				}
				ViewUtils.sendMessage(handler, 0, musicUrl);
			}
		}).start();
    }

	/**
	*callbacks
	*/
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{
		// TODO Auto-generated method stub
		
		/*MediaInfor musicInfor = songList.get(arg2);
		Bundle bundle = new Bundle();
		bundle.putBoolean("IS_CHECK", false);
		bundle.putSerializable("MediaInfor", musicInfor);
		ViewUtils.startActivity(context.getActivity(), SongPrepareActivity.class, bundle);*/
		
	}
	
	private void showPlayDialog(final MediaInfor infor)
	{
		final MusicPlayDialog playDialog = new MusicPlayDialog(activity, R.style.Theme_CustomDialog);
		playDialog.setInfor(infor);
		//playDialog.updateProgress("00:02:18 / 00:05:12");
		if(playDialog.isShowing())
			playDialog.dismiss();
		playDialog.show();
		playDialog.setOnClickListener(new android.view.View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				playDialog.dismiss();
				Bundle bundle = new Bundle();
				bundle.putSerializable("MediaInfor", infor);
				ViewUtils.startActivity(activity, SongPrepareActivity.class, bundle);
			}
		});
		
		WindowManager windowManager = ((Activity) activity).getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = playDialog.getWindow().getAttributes();
		lp.width = (int)(display.getWidth()); //设置宽度
		lp.height = (int)(display.getHeight()); //设置高度
		playDialog.getWindow().setAttributes(lp);
	}

}