package com.znt.speaker.player;

import com.znt.diange.mina.entity.SongInfor;



public interface PlayerEngineListener {
	
	public void onTrackPlay(SongInfor itemInfo); 

	public void onTrackStop(SongInfor itemInfo);
	
	public void onTrackPause(SongInfor itemInfo);	

	public void onTrackPrepareSync(SongInfor itemInfo);
	
	public void onTrackPrepareComplete(SongInfor itemInfo);
	
	public void onTrackStreamError(SongInfor itemInfo);
	
	public void onTrackPlayComplete(SongInfor itemInfo);
}
