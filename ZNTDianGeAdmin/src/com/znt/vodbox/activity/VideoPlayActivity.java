/*  
* @Project: ZNTVodBox 
* @User: Administrator 
* @Description: 家庭音乐
* @Author： yan.yu
* @Company：http://www.zhunit.com/
* @Date 2016-12-11 下午10:31:11 
* @Version V1.1   
*/ 

package com.znt.vodbox.activity; 

import java.io.IOException;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.os.Bundle;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.znt.diange.mina.entity.MediaInfor;
import com.znt.vodbox.R;
import com.znt.vodbox.dialog.ShopListDialog;

/** 
 * @ClassName: VideoPlayActivity 
 * @Description: TODO
 * @author yan.yu 
 * @date 2016-12-11 下午10:31:11  
 */
public class VideoPlayActivity  extends BaseActivity implements OnCompletionListener,OnErrorListener,OnInfoListener,    
OnPreparedListener, OnSeekCompleteListener,OnVideoSizeChangedListener,SurfaceHolder.Callback
{    
	private Display currDisplay;    
	private SurfaceView surfaceView;    
	private SurfaceHolder holder;    
	private MediaPlayer player;    
	private ProgressBar progressBar = null;
	private int vWidth,vHeight;   
	private MediaInfor musicInfor = null;
	private boolean isTopViewShow = true;
	private TextView tvOritation = null;
	//private boolean readyToPlay = false;    
	        
	public void onCreate(Bundle savedInstanceState)
	{    
	    super.onCreate(savedInstanceState);  
	    
	    this.setContentView(R.layout.video_surface);    
	    
	    surfaceView = (SurfaceView)this.findViewById(R.id.surfaceView);   
	    progressBar = (ProgressBar)this.findViewById(R.id.progressBar);
	    tvOritation = (TextView)this.findViewById(R.id.tv_video_orientation);
	    //给SurfaceView添加CallBack监听    
	    holder = surfaceView.getHolder();    
	    holder.setKeepScreenOn(true);
	    holder.addCallback(this);    
	    //为了可以播放视频或者使用Camera预览，我们需要指定其Buffer类型    
	    holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);    
	    
	    showRightView(true);
	    showRightImageView(false);
	    setRightText("插播");
	    getRightView().setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				showShopListDialog(musicInfor);
				releasePlayer();
			}
		});
	    tvOritation.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				if(getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
				{
					  setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				}
				else
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}
		});
	    surfaceView.setOnClickListener(new OnClickListener() 
	    {
			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				if(isTopViewShow)
				{
					showTopView(false);
					isTopViewShow = false;
				}
				else
				{
					showTopView(true);
					isTopViewShow = true;
				}
			}
		});
	        
	    //下面开始实例化MediaPlayer对象    
	    player = new MediaPlayer();    
	    player.setOnCompletionListener(this);    
	    player.setOnErrorListener(this);    
	    player.setOnInfoListener(this);    
	    player.setOnPreparedListener(this);    
	    player.setOnSeekCompleteListener(this);    
	    player.setOnVideoSizeChangedListener(this);    
	    //然后指定需要播放文件的路径，初始化MediaPlayer    
	    musicInfor = (MediaInfor)getIntent().getSerializableExtra("MEDIA_INFOR");
	    String videoName = musicInfor.getMediaName();
	    if(videoName == null)
	    	videoName = "视频播放";
	    setCenterString(videoName);
	    String dataPath = musicInfor.getMediaUrl();    
	    try 
	    {    
	        player.setDataSource(dataPath);    
	    } 
	    catch (IllegalArgumentException e) 
	    {    
	        e.printStackTrace();    
	    } 
	    catch (IllegalStateException e) 
	    {    
	        e.printStackTrace();    
	    } 
	    catch (IOException e) 
	    {    
	        e.printStackTrace();    
	    }    
	    //然后，我们取得当前Display对象    
	    currDisplay = this.getWindowManager().getDefaultDisplay();    
	    
	} 
	
	@Override    
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) 
	{    
	    // 当Surface尺寸等参数改变时触发    
	        
	}    
	@Override    
	public void surfaceCreated(SurfaceHolder holder)
	{    
	    // 当SurfaceView中的Surface被创建的时候被调用    
	    //在这里我们指定MediaPlayer在当前的Surface中进行播放    
	    player.setDisplay(holder);    
	    //在指定了MediaPlayer播放的容器后，我们就可以使用prepare或者prepareAsync来准备播放了    
	    player.prepareAsync();    
	        
	}    
	@Override    
	public void surfaceDestroyed(SurfaceHolder holder)
	{    
	        
	}    
	@Override    
	public void onVideoSizeChanged(MediaPlayer arg0, int arg1, int arg2)
	{    
	    // 当video大小改变时触发    
	    //这个方法在设置player的source后至少触发一次    
	        
	}    
	@Override    
	public void onSeekComplete(MediaPlayer arg0) 
	{    
	    // seek操作完成时触发    
	        
	}    
	@Override    
	public void onPrepared(MediaPlayer player)
	{    
	    // 当prepare完成后，该方法触发，在这里我们播放视频    
	        
	    //首先取得video的宽和高    
	    /*vWidth = player.getVideoWidth();    
	    vHeight = player.getVideoHeight();    
	    
	    if(vWidth > currDisplay.getWidth() || vHeight > currDisplay.getHeight())
	    {    
	        //如果video的宽或者高超出了当前屏幕的大小，则要进行缩放    
	        float wRatio = (float)vWidth/(float)currDisplay.getWidth();    
	        float hRatio = (float)vHeight/(float)currDisplay.getHeight();    
	            
	        //选择大的一个进行缩放    
	        float ratio = Math.max(wRatio, hRatio);    
	            
	        vWidth = (int)Math.ceil((float)vWidth/ratio);    
	        vHeight = (int)Math.ceil((float)vHeight/ratio);    
	            
	    }  */  
	    //设置surfaceView的布局参数    
        //surfaceView.setLayoutParams(new FrameLayout.LayoutParams(vWidth, vHeight));    
            
        //然后开始播放视频    
	    progressBar.setVisibility(View.GONE);
        player.start(); 
	}    
	@Override    
	public boolean onInfo(MediaPlayer player, int whatInfo, int extra) 
	{    
	    // 当一些特定信息出现或者警告时触发    
	    switch(whatInfo){    
	    case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:    
	        break;    
	    case MediaPlayer.MEDIA_INFO_METADATA_UPDATE:      
	        break;    
	    case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:    
	        break;    
	    case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE:     
	        break;    
	    }    
	    return false;    
	}    
	@Override    
	public boolean onError(MediaPlayer player, int whatError, int extra) 
	{    
	    switch (whatError) 
	    {    
	    case MediaPlayer.MEDIA_ERROR_SERVER_DIED:    
	        break;    
	    case MediaPlayer.MEDIA_ERROR_UNKNOWN:    
	        break;    
	    default:    
	        break;    
	    }    
	    return false;    
	}    
	@Override    
	public void onCompletion(MediaPlayer player) 
	{    
	    // 当MediaPlayer播放完成后触发    
	    this.finish();    
	}  
	
	private void releasePlayer()
	{
		if(player != null)
		{
			if (player.isPlaying())  
				player.stop();
			player.release();  
			player = null;
		}
	}
	
	/**
	*callbacks
	*/
	@Override
	protected void onDestroy() 
	{
		releasePlayer();
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	private void showShopListDialog(MediaInfor musicInfor)
	{
		final ShopListDialog playDialog = new ShopListDialog(getActivity(), musicInfor);
		//playDialog.updateProgress("00:02:18 / 00:05:12");
		if(playDialog.isShowing())
			playDialog.dismiss();
		playDialog.show();
		
		WindowManager windowManager = (getActivity()).getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = playDialog.getWindow().getAttributes();
		lp.width = (int)(display.getWidth()); //设置宽度
		lp.height = (int)(display.getHeight()); //设置高度
		playDialog.getWindow().setAttributes(lp);
	}
}    
