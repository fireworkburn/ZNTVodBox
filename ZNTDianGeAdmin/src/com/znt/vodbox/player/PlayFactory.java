/*  
* @Project: ZNTDianGe 
* @User: Administrator 
* @Description: 家庭音乐
* @Author： yan.yu
* @Company：http://www.zhunit.com/
* @Date 2015年10月20日 下午10:16:19 
* @Version V1.1   
*/ 

package com.znt.vodbox.player;

import java.io.IOException;

import android.os.Handler;

import com.znt.vodbox.player.MP3RadioStreamPlayer.OnStreamPlayListener;
import com.znt.vodbox.utils.ViewUtils;

/** 
* @ClassName: PlayFactory 
* @Description: TODO
* @author yan.yu 
* @date 2015年10月20日 下午10:16:19  
*/
public class PlayFactory implements OnStreamPlayListener
{
	private MP3RadioStreamPlayer player;
	private Handler handler = null;
	
	public static final int GET_DURATION = 90;
	public static final int PLAY_ERROR = 91;
	public static final int PLAY_PROGRESS = 92;
	
	public PlayFactory(Handler handler)
	{
		this.handler = handler;
	}
	
	public void startPlay(String url)
	{
		stopPlay();
		
		player = new MP3RadioStreamPlayer();
		player.setUrlString(url);
		player.setOnStreamPlayListener(this);
		
		try
		{
			player.play(true);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	public void startGetDuration(String url)
	{
		stopPlay();
		
		player = new MP3RadioStreamPlayer();
		player.setUrlString(url);
		player.setOnStreamPlayListener(this);
		
		try
		{
			player.play(false);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void seekToPosition(long timeUs)
	{
		player.seekToPosition(timeUs);
	}
	
	public void stopPlay()
	{
		if(player != null)
		{
			player.stop();
			player.release();
			player = null;
		}
	}

	/**
	*callbacks
	*/
	@Override
	public void onStreamPlayStarted()
	{
		// TODO Auto-generated method stub
		
	}
	/**
	*callbacks
	*/
	@Override
	public void onStreamPlayStoped()
	{
		// TODO Auto-generated method stub
		
	}
	/**
	*callbacks
	*/
	@Override
	public void onStreamPlayError()
	{
		// TODO Auto-generated method stub
		if(handler != null)
			ViewUtils.sendMessage(handler, PLAY_ERROR);
	}

	/**
	*callbacks
	*/
	@Override
	public void onStreamPlayProgress(int progreee)
	{
		// TODO Auto-generated method stub
		if(handler != null)
			ViewUtils.sendMessage(handler, PLAY_PROGRESS, progreee);
	}

	/**
	*callbacks
	*/
	@Override
	public void onGetDuration(long duration)
	{
		// TODO Auto-generated method stub
		if(handler != null)
			ViewUtils.sendMessage(handler, GET_DURATION, duration);
	}

}
 
