
package com.znt.vodbox.player; 

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;

import com.znt.vodbox.utils.MyLog;

/** 
 * @ClassName: PlayerHelper 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-5-26 上午10:03:18  
 */
public class PlayerHelper
{
	private MediaPlayer mediaPlayer = null;
	private OnMusicPrepareListener listener = null;
	
	public interface OnMusicPrepareListener
	{
		public void onPrepareFinish(MediaPlayer mp);
		public void onPrepareFail(String error);
	}
	
	public PlayerHelper()
	{
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnPreparedListener(new MyPreparedListener());
		mediaPlayer.setOnErrorListener(new OnErrorListener()
		{
			@Override
			public boolean onError(MediaPlayer arg0, int arg1, int arg2)
			{
				// TODO Auto-generated method stub
				if(listener != null)
					listener.onPrepareFail(null);
				return false;
			}
		});
	}
	
	public void setOnPrepareListener(OnMusicPrepareListener listener)
	{
		this.listener = listener;
	}
	
	public void startInitPlayer(String mediaUrl)
	{
		try
		{
			if(mediaPlayer.isPlaying())
				mediaPlayer.stop();
			mediaPlayer.reset();// 把各项参数恢复到初始状态
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			/**
			 * 通过MediaPlayer.setDataSource()
			 * 的方法,将URL或文件路径以字符串的方式传入.使用setDataSource ()方法时,要注意以下三点:
			 * 1.构建完成的MediaPlayer 必须实现Null 对像的检查.
			 * 2.必须实现接收IllegalArgumentException 与IOException
			 * 等异常,在很多情况下,你所用的文件当下并不存在. 
			 * 3.若使用URL 来播放在线媒体文件,该文件应该要能支持pragressive
			 * 下载.
			 */
			mediaPlayer.setDataSource(mediaUrl);
			mediaPlayer.prepareAsync();// 进行缓冲
			
		} 
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			if(listener != null)
				listener.onPrepareFail(e.getMessage());
			MyLog.e("******MediaPlayer 初始化失败-->"+e.getMessage());
		} 
		//closeMediaPlayer();
	}
	
	public MediaPlayer getMediaPlayer()
	{
		return mediaPlayer;
	}
	
	public void startPlay()
	{
		if(mediaPlayer != null)
			mediaPlayer.start();
	}
	
	public void closeMediaPlayer()
	{
		if(mediaPlayer != null)
		{
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					// TODO Auto-generated method stub
					mediaPlayer.stop();
					mediaPlayer.release();
					mediaPlayer = null;
				}
			}).start();
		}
	}
	
	private final class MyPreparedListener implements android.media.MediaPlayer.OnPreparedListener 
	{
		public MyPreparedListener()
		{
			
		}
		
		@Override
		public void onPrepared(MediaPlayer mp) 
		{
			if(listener != null)
				listener.onPrepareFinish(mp);
		}
	}
}
 
