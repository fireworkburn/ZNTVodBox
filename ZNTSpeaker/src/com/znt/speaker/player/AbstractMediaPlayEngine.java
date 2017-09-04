package com.znt.speaker.player;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;

import com.znt.diange.mina.entity.PlayState;
import com.znt.diange.mina.entity.SongInfor;
import com.znt.speaker.util.CommonLog;
import com.znt.speaker.util.FileUtils;
import com.znt.speaker.util.LogFactory;

public abstract class AbstractMediaPlayEngine implements IBasePlayEngine, OnCompletionListener, 
												OnErrorListener
												{
	
	private static final CommonLog log = LogFactory.createLog();
	
	protected MediaPlayer   mMediaPlayer;					
	protected SongInfor mMediaInfo;							   								
	protected Activity 		mContext;
	protected int 			mPlayState;   
	
	protected PlayerEngineListener mPlayerEngineListener;
	
	protected abstract boolean prepareSelf();
	protected abstract boolean prepareComplete(MediaPlayer mp);
	
	protected  void defaultParam()
	{
		mMediaPlayer = new MediaPlayer();		
		mMediaPlayer.setOnCompletionListener(this);	
		mMediaPlayer.setOnPreparedListener(new MyPreparedListener());
		mMediaPlayer.setOnErrorListener(this);
		mMediaInfo = null;
		mPlayState = PlayState.MPS_NOFILE;
	}
	
	public AbstractMediaPlayEngine(Activity context)
	{
		mContext = context;
		defaultParam();	
	}
	
	public SongInfor getSongInfor()
	{
		return mMediaInfo;
	}
	
	public void setPlayerListener(PlayerEngineListener listener)
	{
		mPlayerEngineListener = listener;
	}
		
	@Override
	public void play() 
	{
		mContext.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				switch (mPlayState) 
				{
				case PlayState.MPS_PAUSE:
					mMediaPlayer.start();
					mPlayState = PlayState.MPS_PLAYING;
					performPlayListener(mPlayState);
					break;
				case PlayState.MPS_STOP:
					prepareSelf();
					break;
				default:
					break;
				}
			}
		});
	}

	@Override
	public void pause() 
	{
		
		mContext.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				switch (mPlayState) 
				{
				case PlayState.MPS_PLAYING:			
					mMediaPlayer.pause();
					mPlayState = PlayState.MPS_PAUSE;
					performPlayListener(mPlayState);
					break;
				default:
					break;
				}
			}
		});
	}

	@Override
	public void stop()
	{
		mContext.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				if (mPlayState != PlayState.MPS_NOFILE)
				{
					mMediaPlayer.reset();
					mPlayState = PlayState.MPS_STOP;
					performPlayListener(mPlayState);
				}
			}
		});
	}
	
	@Override
	public void skipTo(int time) 
	{
		
		switch (mPlayState) 
		{
			case PlayState.MPS_PLAYING:
			case PlayState.MPS_PAUSE:				
				int time2 = reviceSeekValue(time);
				mMediaPlayer.seekTo(time2);
				break;
			default:
				break;
		}
	
	}
	
	public void exit()
	{
		stop();
		mMediaPlayer.release();
		mMediaInfo = null;
		mPlayState = PlayState.MPS_NOFILE;
	}
	
	private SongInfor songInforPlay = null;
	public void setSongInforPlay(SongInfor songInforPlay)
	{
		this.songInforPlay = songInforPlay;
	}

	@Override
	public void onCompletion(MediaPlayer mp) 
	{
		log.e("onCompletion...");
		if (mPlayerEngineListener != null)
		{
			if(songInforPlay != null)
				mPlayerEngineListener.onTrackPlayComplete(songInforPlay);
		}
	}

	public boolean isPlaying() 
	{
		return mPlayState == PlayState.MPS_PLAYING;
	}
	
	public boolean isPrepareing() 
	{
		return mPlayState == PlayState.MPS_PARESYNC;
	}
	
	public boolean isPlayError() 
	{
		return mPlayState == PlayState.MPS_INVALID;
	}

	public boolean isPause()
	{
		return mPlayState == PlayState.MPS_PAUSE;
	}
	
	public void playMedia(SongInfor mediaInfo)
	{
		//String name = mediaInfo.getMediaName();
		if (mediaInfo != null)
		{
			mMediaInfo = mediaInfo;
			prepareSelf();
		}
	}
	
	public void resetPlayInfor()
	{
		mMediaInfo = null;
	}
		
	public int getCurPosition()
	{
		if (mPlayState == PlayState.MPS_PLAYING
				|| mPlayState == PlayState.MPS_PAUSE
			|| mPlayState == PlayState.MPS_PARECOMPLETE)
		{
			return mMediaPlayer.getCurrentPosition();
		}
			
		return 0;
		//return mMediaPlayer.getCurrentPosition();
	}
	
	public MediaPlayer getMediaPlayer()
	{
		return mMediaPlayer;
	}
	
	public int getDuration()
	{
		
		switch(mPlayState)
		{
			case PlayState.MPS_PLAYING:
			case PlayState.MPS_PAUSE:
			case PlayState.MPS_PARECOMPLETE:
				return mMediaPlayer.getDuration();
		}
	
		return 0;
		//return mMediaPlayer.getDuration();
	}
	
	public int getPlayState()
	{
		return mPlayState;
	}

	protected void performPlayListener(int playState)
	{
		if (mPlayerEngineListener != null)
		{
			switch(playState)
			{
				case PlayState.MPS_INVALID:
					mPlayerEngineListener.onTrackStreamError(songInforPlay);
					break;
				case PlayState.MPS_STOP:
					mPlayerEngineListener.onTrackStop(songInforPlay);
					break;
				case PlayState.MPS_PLAYING:
					mPlayerEngineListener.onTrackPlay(songInforPlay);
					break;
				case PlayState.MPS_PAUSE:
					mPlayerEngineListener.onTrackPause(songInforPlay);
					break;
				case PlayState.MPS_PARESYNC:
					mPlayerEngineListener.onTrackPrepareSync(mMediaInfo);
					break;
			}
		}
	}	
	
	private int reviceSeekValue(int value)
	{
		if (value < 0)
		{
			value = 0;
		}
		
		if (value > mMediaPlayer.getDuration())
		{
			value = mMediaPlayer.getDuration();
		}
		
		return value;
	}
	
	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) 
	{
		return false;
	}
	
	private final class MyPreparedListener implements android.media.MediaPlayer.OnPreparedListener 
	{
		public MyPreparedListener()
		{
			
		}
		
		@Override
		public void onPrepared(MediaPlayer mp) 
		{
			prepareComplete(mp);
		}
	}
}
