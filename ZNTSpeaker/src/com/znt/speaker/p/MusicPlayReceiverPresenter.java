package com.znt.speaker.p;

import com.znt.speaker.center.DMRCenter;
import com.znt.speaker.v.IMusicReceiverView;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.util.Log;

public class MusicPlayReceiverPresenter 
{

	private Activity activity = null;
	private MusicPlayReceiver mpReceiver = null;
	private IMusicReceiverView iMusicReceiverView = null;
	
	private boolean isMpSRegistered = false;
	public MusicPlayReceiverPresenter(Activity activity, IMusicReceiverView iMusicReceiverView)
	{
		this.activity = activity;
		this.iMusicReceiverView = iMusicReceiverView;
		mpReceiver = new MusicPlayReceiver();
	}
	
	public void registerReceiver()
    {
		if(!isMpSRegistered)
		{
			IntentFilter filter = new IntentFilter();
	    	filter.addAction(DMRCenter.GET_NEW_SONG);
	    	filter.addAction(DMRCenter.STOP_SONG);
	    	filter.addAction(DMRCenter.SET_DEVICE);
	    	filter.addAction(DMRCenter.PLAY_NEXT);
	    	filter.addAction(DMRCenter.UPDATE_SYSTEM);
	    	filter.addAction(DMRCenter.UPDATE_RES);
	    	filter.addAction(DMRCenter.UPDATE_PLAY_STATE);
	    	/*filter.addAction(DMSService.CLOSE);
	    	filter.addAction(DMSService.SCAN_MUSIC_FINISH);
	    	filter.addAction(DMSService.SCAN_MUSIC_START);*/
	    	filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
	    	activity.registerReceiver(mpReceiver, filter);
			isMpSRegistered = true;
		}
    }
	public void unregisterReceiver()
    {
    	try {
    		if(mpReceiver != null && isMpSRegistered)
    			activity.unregisterReceiver(mpReceiver);
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("", "unregisterReceiver error-->" + e.getMessage());
		}
    }
    
    class MusicPlayReceiver extends BroadcastReceiver
	{
		/**
		*callbacks
		*/
		@Override
		public void onReceive(Context arg0, Intent intent)
		{
			// TODO Auto-generated method stub
			iMusicReceiverView.onReceive(intent);
		}
	}
}
