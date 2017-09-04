package com.znt.speaker.player;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.audiofx.Visualizer;
import android.media.audiofx.Visualizer.OnDataCaptureListener;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;

import com.znt.diange.mina.entity.PlayState;
import com.znt.diange.mina.entity.SongInfor;
import com.znt.speaker.entity.LocalDataEntity;
import com.znt.speaker.factory.UIManager;
import com.znt.speaker.util.CommonLog;
import com.znt.speaker.util.FileUtils;
import com.znt.speaker.util.LogFactory;
import com.znt.speaker.util.ViewUtils;

public class MusicPlayEngineImpl extends AbstractMediaPlayEngine implements SurfaceTextureListener
{

	private final CommonLog log = LogFactory.createLog();	
	
	private OnSeekCompleteListener mSeekCompleteListener;
	private OnDataCaptureListener mDataCaptureListener;
	
	private Visualizer mVisualizer;
	
	private Activity activity = null;
	private boolean isPreparing = false;
	private boolean isPlayFile = false;
	private long loadStartTime = 0;
	private Surface surface = null;
	
	public  MusicPlayEngineImpl(Activity context) 
	{
		super(context);
		this.activity = context;
	}
	
	public void setOnBuffUpdateListener(OnBufferingUpdateListener listener)
	{
		mBufferingUpdateListener = listener;
	}
	
	public void setOnSeekCompleteListener(OnSeekCompleteListener listener)
	{
		mSeekCompleteListener = listener;
	}
	
	public void setDataCaptureListener(OnDataCaptureListener listener)
	{
		mDataCaptureListener = listener;
	}
	
	public boolean reInitVisualizer(int sID)
	{
		releaseVisualizer();
		
		final int maxCR = Visualizer.getMaxCaptureRate();
		mVisualizer = new Visualizer(sID);
		mVisualizer.setCaptureSize(256);
		if (mDataCaptureListener != null)
		{
			mVisualizer.setDataCaptureListener(mDataCaptureListener, maxCR/2, false, true);
		}
		
		return true;
	}
	
	public void releaseVisualizer()
	{
		if (mVisualizer != null) 
		{
			mVisualizer.setEnabled(false);
			mVisualizer.release();
			mVisualizer = null;
		}
	}
	
	public void enableVisualizer(boolean flag)
	{
		/*if (mVisualizer != null)
		{
			mVisualizer.setEnabled(flag);
		}*/
	}
	
	public boolean isLoading()
	{
		return isPreparing;
	}
	public void setLoaded()
	{
		if(mMediaPlayer != null)
		{
			mMediaPlayer.stop();
			mMediaPlayer.reset();
		}
		isPreparing = false;
	}
	
	public boolean isPlayFile()
	{
		return isPlayFile;
	}
	
	@Override
	public void play() 
	{
		super.play();
		enableVisualizer(true);
	}

	@Override
	public void pause() 
	{
		super.pause();
		enableVisualizer(false);
	}

	@Override
	public void stop() 
	{	
		super.stop();
		enableVisualizer(false);
	}
	
	private OnBufferingUpdateListener mBufferingUpdateListener;

	@Override
	public void exit()
	{
		super.exit();
		releaseVisualizer();
	}
	
	@Override
	protected boolean prepareSelf()
	{
	
		loadStartTime = System.currentTimeMillis();
		isPreparing = true;
		/*if(mMediaPlayer.isPlaying())
			mMediaPlayer.stop();*/
		mMediaPlayer.reset();
		try 
		{
			String urlPlay = getMusicPlayUrL(mMediaInfo);
			//String urlPlay = "http://m2.music.126.net/lsqs1Sy15_wMdCsJmiJ_yw==/6658642418688884.mp3";
			mMediaPlayer.setDataSource(urlPlay);
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);  
			if (mBufferingUpdateListener != null)
			{
				mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
			}
			if(FileUtils.isVideo(urlPlay))
			{
				if(mUimanager.getTextureView() == null)
				{
					mUimanager.initTextureView();
					mUimanager.getTextureView().setSurfaceTextureListener(this);
				}
			}
			else//销毁视频播放
			{
				if(surface != null)
				{
					surface.release();
					surface = null;
				}
				mUimanager.releaseTextureView();
			}
			mMediaPlayer.prepareAsync();
			mPlayState = PlayState.MPS_PARESYNC;
			performPlayListener(mPlayState);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			isPreparing = false;
			mPlayState = PlayState.MPS_INVALID;
			setSongInforPlay(mMediaInfo);
			performPlayListener(mPlayState);
			log.e("prepareSelf Exception-->" + e.getMessage());
			
			return false;
		}
		
		return true;
	}
	
	@Override
	protected boolean prepareComplete(MediaPlayer mp) 
	{
		
		isPreparing = false;
		
		log.e("prepareComplete");	
		//int duration = mp.getDuration();
		
		setSongInforPlay(mMediaInfo);
		seekCurPos();
		
		mPlayState = PlayState.MPS_PARECOMPLETE;
		if (mPlayerEngineListener != null)
		{
			mPlayerEngineListener.onTrackPrepareComplete(mMediaInfo);
		}
		mMediaPlayer.start();
		
		mPlayState = PlayState.MPS_PLAYING;
		performPlayListener(mPlayState);
		reInitVisualizer(mMediaPlayer.getAudioSessionId());
		enableVisualizer(true);
		return true;
	}
	
	private String getMusicPlayUrL(SongInfor songInfor)
	{
		String url = songInfor.getMediaUrl();
		if(url.startsWith("http://") || url.startsWith("https://"))
		{
			String fileName = "";
			if(url.contains("."))
				fileName = url.substring(url.lastIndexOf("/") + 1, url.length());
			String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + fileName;
			File file = new File(filePath);
			if(file.exists())
			{
				isPlayFile = true;
				return filePath;
			}
			else
			{
				isPlayFile = false;
			}
		}
		else
			isPlayFile = true;
		return songInfor.getMediaUrl();
	}
	
	private UIManager mUimanager = null;
	public void setUiManager(UIManager mUimanager)
	{
		this.mUimanager = mUimanager;
	}
	private void seekCurPos()
	{
		int seekPos = LocalDataEntity.newInstance(mContext).getSeekPos();
		if(seekPos > 0)
		{
			long curUpdateTime = LocalDataEntity.newInstance(mContext).getCurLastUpdateTime();
			long curSystime = 0;
			int delayTime = 0;
			if(mUimanager != null)
				curSystime = mUimanager.getCurTime();
			if(curSystime > 0 && curUpdateTime > 0)
			{
				delayTime = (int) (curSystime - curUpdateTime);
			}
			
			int duration = mMediaPlayer.getDuration();
			if(seekPos <= duration)
			{
				int loadTime = (int) (System.currentTimeMillis() - loadStartTime);
				
				int tempSeek = seekPos + delayTime + loadTime;
				if(tempSeek > 0 && tempSeek < duration)
					mMediaPlayer.seekTo(tempSeek);
				else if(tempSeek == duration)
				{
					mMediaPlayer.seekTo(duration - 1000);
				}
				else
					mMediaPlayer.seekTo(seekPos);
			}
			
			LocalDataEntity.newInstance(mContext).setSeekPos(0);
		}
	}
	
	private boolean isUrlValiable(String url)
	{
		boolean value = false;
		HttpURLConnection conn;
		try
		{
			conn = (HttpURLConnection)new URL(url).openConnection();
			int code=conn.getResponseCode();
			if(code!=200)
			{
				value=false;
			}
			else
			{
				value=true;
			}
		} 
		catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return value;
	}
	@Override
	public void onSurfaceTextureAvailable(SurfaceTexture arg0, int arg1,
			int arg2) {
		// TODO Auto-generated method stub
		surface = new Surface(arg0);
		mMediaPlayer.setSurface(surface);
		
	}

	@Override
	public boolean onSurfaceTextureDestroyed(SurfaceTexture arg0) {
		// TODO Auto-generated method stub
		Log.d("", "onSurfaceTextureDestroyed");
		return false;
	}

	@Override
	public void onSurfaceTextureSizeChanged(SurfaceTexture arg0, int arg1,
			int arg2) {
		// TODO Auto-generated method stub
		Log.d("", "onSurfaceTextureSizeChanged");
	}

	@Override
	public void onSurfaceTextureUpdated(SurfaceTexture arg0) {
		// TODO Auto-generated method stub
		Log.d("", "onSurfaceTextureUpdated");
	}
}
