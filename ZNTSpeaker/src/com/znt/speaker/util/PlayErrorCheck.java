
package com.znt.speaker.util; 

import java.io.Serializable;

import javax.jmdns.ServiceListener;

import com.znt.diange.mina.entity.SongInfor;

/** 
 * @ClassName: PlayErrorCheck 
 * @Description: TODO
 * @author yan.yu 
 * @date 2016-1-6 下午3:10:09  
 */
public class PlayErrorCheck implements Serializable
{

	/** 
	* @Fields serialVersionUID : TODO
	*/ 
	private static final long serialVersionUID = 1L;
	
	private long startTime = 0;
	private long endTime = 0;
	
	private SongInfor startSong = null;
	private SongInfor endSong = null;
	
	
	public void setStart(long startTime, SongInfor startSong)
	{
		this.startTime = startTime;
		this.startSong = startSong;
	}
	
	public boolean isPlayError(SongInfor endSong)
	{
		this.endSong = endSong;
		endTime = System.currentTimeMillis();
		
		if(startSong == null)
			return false;
		if(endSong == null)
			return false;
		if(startTime == 0)
			return false;
		
		if(startSong.getMediaUrl().equals(endSong.getMediaUrl()))
		{
			long interTime = endTime - startTime;
			LogFactory.createLog().e("********play interTime-->"+interTime);
			return interTime <= 15*1000;
		}
		
		return false;
	}
	
	public void clear()
	{
		startSong = null;
		endSong = null;
		startTime = 0;
		endTime = 0;
	}
}
 
