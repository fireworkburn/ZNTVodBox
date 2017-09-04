
package com.znt.diange.dialog; 

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.znt.diange.R;
import com.znt.diange.mina.entity.MediaInfor;
import com.znt.diange.mina.entity.ResoureType;
import com.znt.diange.player.PlayerHelper;
import com.znt.diange.player.PlayerHelper.OnMusicPrepareListener;
import com.znt.diange.utils.DownHelper;
import com.znt.diange.utils.StringUtils;
import com.znt.diange.utils.UrlUtils;
import com.znt.diange.utils.ViewUtils;

/** 
 * @ClassName: MusicPlayDialog 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-7-20 下午3:30:43  
 */
public class MusicPlayDialog extends Dialog implements OnMusicPrepareListener
{

	private TextView textTitle = null;
	private TextView textInfor = null;
	private SeekBar seekBar = null;
	private Button btnLeft = null;
	private Button btnRight = null;
	private android.view.View.OnClickListener listener = null;
	
	private PlayerHelper playerHelper = null;
	private DownHelper downHelper = null;
	
	private MediaInfor musicInfor = null;
	private Activity context = null;
	private boolean isDismissed = false;
	private boolean isStop = false;
	private int duration = 0;
	
	private final int PREPARE_FINISH = 0;
	private final int PREPARE_FAIL = 1;
	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == PREPARE_FINISH)
			{
				seekBar.setMax(duration);
				playerHelper.startPlay();
				
				startPlayPosition();
			}
			else if(msg.what == PREPARE_FAIL)
			{
				textTitle.setText("播放失败，请重试");
			}
		};
	};
	
	/** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param context 
	*/
	public MusicPlayDialog(Activity context)
	{
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

    /** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param context
	* @param themeCustomdialog 
	*/
	public MusicPlayDialog(Activity context, int themeCustomdialog)
	{
		super(context, themeCustomdialog);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) 
    {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.dialog_music_play);
	    setScreenBrightness();
	    
	    Window window = getWindow();
		window.setWindowAnimations(R.style.AnimAlph);
	    
		seekBar = (SeekBar) MusicPlayDialog.this.findViewById(R.id.sb_dialog_music_play_progress);
		btnRight = (Button) MusicPlayDialog.this.findViewById(R.id.btn_dialog_music_play_right);
    	btnLeft = (Button) MusicPlayDialog.this.findViewById(R.id.btn_dialog_music_play_left);
    	textTitle = (TextView) MusicPlayDialog.this.findViewById(R.id.tv_dialog_music_play_title);
        textInfor = (TextView) MusicPlayDialog.this.findViewById(R.id.tv_dialog_music_play_progress);
		
        downHelper = new DownHelper();
        playerHelper = new PlayerHelper();
        playerHelper.setOnPrepareListener(this);
        
        if(musicInfor.getMediaType().equals(MediaInfor.MEDIA_TYPE_PHONE))
        {
        	btnRight.setText("去搜索");
        }
        else
        {
        	btnRight.setText("点播");
        }
        
	    this.setOnShowListener(new OnShowListener()
	    {
            @Override
            public void onShow(DialogInterface dialog)
            {
            	initViews();
            }
        });
	    this.setOnDismissListener(new OnDismissListener()
		{
			
			@Override
			public void onDismiss(DialogInterface arg0)
			{
				// TODO Auto-generated method stub
				if(playerHelper != null)
					playerHelper.closeMediaPlayer();
				stopPlayPosition();
			}
		});
	    
	    seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
		{
			@Override
			public void onStopTrackingTouch(SeekBar sb)
			{
				// TODO Auto-generated method stub
				if(playerHelper != null)
				{
					int temp = (int) ((duration * 1000) * ((float)sb.getProgress() / sb.getMax()));
					playerHelper.getMediaPlayer().seekTo(temp);
					isStop = false;
					//MyLog.e("onProgressChanged temp-->"+temp);
				}
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar arg0)
			{
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar arg0, int position, boolean fromUser)
			{
				// TODO Auto-generated method stub
				if(fromUser)
				{
					//MyLog.e("onProgressChanged position-->"+position);
					isStop = true;
				}
			}
		});
	}
	
    private void initViews()
    {
        
        setCanceledOnTouchOutside(false);
        
        textTitle.setText(musicInfor.getMediaName());
        
        if(listener != null)
        	btnRight.setOnClickListener(listener);
        
        btnLeft.setOnClickListener(new android.view.View.OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				// TODO Auto-generated method stub
				isDismissed = true;
				dismiss();
			}
		});
        
        getMusicUrl();
        
    }
    
    public boolean isDismissed()
    {
    	return isDismissed;
    }
    
    public void setOnClickListener(android.view.View.OnClickListener listener)
    {
    	this.listener  = listener;
    }
    
    public void setInfor(MediaInfor musicInfor)
    {
    	this.musicInfor = musicInfor;
    }
    
    public void updateProgress(String text)
    {
    	this.textInfor.setText(text);
    }
    
    public Button getLeftButton()
    {
    	return btnLeft;
    }
    public Button getRightButton()
    {
    	return btnRight;
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
    
    private void getMusicUrl()
    {
    	new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				//网络音乐要重新获取链接
				String musicUrl = null;
				if(musicInfor.getMediaType().equals(MediaInfor.MEDIA_TYPE_NET))
				{
					if(musicInfor.getResourceType() == ResoureType.KUWO)
						musicUrl = downHelper.getDlAndPath(musicInfor.getMediaId());
					else if(musicInfor.getResourceType() == ResoureType.WANGYI)
						musicUrl = musicInfor.getMediaUrl();
				}
				else
				{
					musicUrl = musicInfor.getMediaUrl();
					if(musicInfor.getMediaType().equals(MediaInfor.MEDIA_TYPE_SPEAKER))
					{
						//如果是音响本地歌曲，就转换播放地址
						musicUrl = UrlUtils.getMediaPlayUrl(musicUrl, false);
					}
				}
					
				if(!isDismissed)
				{
					playerHelper.startInitPlayer(musicUrl);
				}
			}
		}).start();
    }
    
    Runnable tast = new Runnable()
	{
		@Override
		public void run()
		{
			// TODO Auto-generated method stub
			
			if(!isDismissed && playerHelper != null && !isStop)
			{
				int progress = playerHelper.getMediaPlayer().getCurrentPosition() / 1000;
				seekBar.setProgress(progress);
				textInfor.setText(StringUtils.secToTime(progress) + " / " + StringUtils.secToTime(duration));
			}
			handler.postDelayed(tast, 800);
		}
	};
    private void startPlayPosition()
    {
    	handler.postDelayed(tast, 800);
    }
    private void stopPlayPosition()
    {
    	handler.removeCallbacks(tast);
    }

	/**
	*callbacks
	*/
	@Override
	public void onPrepareFinish(MediaPlayer mp)
	{
		// TODO Auto-generated method stub
		duration = (int) Math.ceil((float)(mp.getDuration() / 1000));
		ViewUtils.sendMessage(handler, PREPARE_FINISH);
	}

	/**
	*callbacks
	*/
	@Override
	public void onPrepareFail(String error)
	{
		// TODO Auto-generated method stub
		ViewUtils.sendMessage(handler, PREPARE_FAIL);
	}
}