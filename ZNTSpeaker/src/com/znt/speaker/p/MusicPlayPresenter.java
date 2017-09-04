package com.znt.speaker.p;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.znt.diange.mina.entity.SongInfor;
import com.znt.speaker.db.DBManager;
import com.znt.speaker.entity.DownloadFileInfo;
import com.znt.speaker.entity.LocalDataEntity;
import com.znt.speaker.factory.UIManager;
import com.znt.speaker.player.AbstractTimer;
import com.znt.speaker.player.MusicPlayEngineImpl;
import com.znt.speaker.player.PlayerEngineListener;
import com.znt.speaker.player.SingleSecondTimer;
import com.znt.speaker.util.FileDownLoadUtil;
import com.znt.speaker.util.PlayErrorCheck;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.os.Environment;
import android.os.Handler;

public class MusicPlayPresenter implements OnBufferingUpdateListener, OnSeekCompleteListener,OnErrorListener
{
	private Activity activity = null;
	private UIManager mUIManager = null;
	private MusicPlayEngineImpl mPlayerEngineImpl;
	private MusicPlayEngineListener mPlayEngineListener;
	private HttpPresenter httpPresenter = null;
	
	private AbstractTimer mPlayPosTimer;
	
	private SongInfor songInforPlay = null;
	private List<SongInfor> playList = new ArrayList<SongInfor>();
	
	private final int REFRESH_CURPOS = 0;
	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == REFRESH_CURPOS)
			{
				if(mPlayerEngineImpl != null && mPlayerEngineImpl.isPlaying())
					refreshCurPos();
			}
		};
	};
	
	public MusicPlayPresenter(Activity activity, UIManager mUIManager, HttpPresenter httpPresenter)
	{
		this.activity = activity;
		this.mUIManager = mUIManager;
		this.httpPresenter = httpPresenter;
		init();
	}
	
	private void init()
	{
		mPlayerEngineImpl = new MusicPlayEngineImpl(activity);
		mPlayerEngineImpl.setOnBuffUpdateListener(this);
		mPlayerEngineImpl.setOnSeekCompleteListener(this);
		mPlayEngineListener = new MusicPlayEngineListener();
		mPlayerEngineImpl.setPlayerListener(mPlayEngineListener);
		mPlayerEngineImpl.setUiManager(mUIManager);
		
		startRefreshPosition();
	}
	
	public void closePlayer()
	{
		mPlayerEngineImpl.exit();
		mPlayPosTimer.stopTimer();
	}
	
	private void startRefreshPosition()
	{
		mPlayPosTimer = new SingleSecondTimer(activity);
		mPlayPosTimer.setHandler(handler, REFRESH_CURPOS);
		mPlayPosTimer.startTimer();
	}
	
	private void restartTimer()
	{
		mPlayPosTimer.stopTimer();
		mPlayPosTimer.startTimer();
		
		mUIManager.stopTimer();
		mUIManager.startTimer();
	}
	
	private void refreshCurPos()
	{
		int pos = mPlayerEngineImpl.getCurPosition();
		mUIManager.setSeekbarProgress(pos);
		
		if(mUIManager.getMaxProgress() == 0)
		{
			int duration = mPlayerEngineImpl.getDuration();
			mUIManager.setSeekbarMax(duration);
			mUIManager.setTotalTime(duration);
		}
	}
	
	public int getCurPosition()
	{
		if(mPlayerEngineImpl == null)
			return 0;
		return mPlayerEngineImpl.getCurPosition();
	}
	public SongInfor getCurPlaySong()
	{
		if(mPlayerEngineImpl == null)
			return null;
		return mPlayerEngineImpl.getSongInfor();
	}
	public String getCurPlaySongName()
	{
		SongInfor tempInfor = getCurPlaySong();
		if(tempInfor == null)
			return "";
		return tempInfor.getMediaName();
	}
	public int getCurPlaySongType()
	{
		SongInfor tempInfor = getCurPlaySong();
		if(tempInfor == null)
			return 0;
		return tempInfor.getMusicPlayType();
	}
	
	public void checkDelay()
	{
		if(mPlayerEngineImpl == null)
			return;
		
		int pos = getCurPosition();
		boolean ret = isDelay(pos);
		if (ret)
		{
			mPlayerEngineImpl.setLoaded();
			if(playList.size() > 0)
			{
				startPlaySong();
			}
		}
		setPos(pos);
	}
	
	private int lastPos = 0;
	public void setPos(int pos)
	{
		lastPos = pos;
	}
	
	public boolean isDelay(int pos)
	{
		if (pos != lastPos)
		{
			return false;
		}
/*		if (pos == 0 || pos != lastPos)
		{
			return false;
		}
*/		
		return true;
	}
	
	private void doPrepare()
	{
		//String mediaName = itemInfo.getMediaName();
		playErrorCheck.clear();
		playErrorCheck.setStart(System.currentTimeMillis(), songInforPlay);
	}
	
	public void playMusicWhenInitFinish()
	{
		if(playList.size() > 0 && mPlayerEngineImpl != null && !mPlayerEngineImpl.isPlaying())
		{
			startPlaySong();
		}
	}
	public void addPlayList(List<SongInfor> tempList)
	{
		if(tempList != null)
		{
			if(playList == null)
				playList = new ArrayList<SongInfor>();
			playList.clear();
			playList.addAll(tempList);
		}
	}
	public void clearPlayList()
	{
		if(playList != null)
			playList.clear();
	}
	public boolean isPlayListNone()
	{
		return playList == null || playList.size() == 0;
	}
	public boolean isPlayPushMusic()
	{
		if(songInforPlay != null && songInforPlay.isPushMusic())
			return true;
		return false;
	}
	
	public void handleProcessWhenNonePlayTime()
	{
		if(!isPlayPushMusic())
		{
			clearPlayList();
			if(mPlayerEngineImpl != null && mPlayerEngineImpl.isPlaying())
			{
				mPlayerEngineImpl.stop();
				mUIManager.showPrepareLoadView(true);
			}
		}
	}
	
	public void handlerLocalPlayListDo(List<SongInfor> tempList)
	{
		if(tempList != null && tempList.size() > 0)
		{
			addPlayList(tempList);
			LocalDataEntity.newInstance(activity).setMusicIndex(0);
			if(playList.size() > 0 && !mPlayerEngineImpl.isPlaying())
				startPlaySong();
			else if(playList.size() == 0 && mPlayerEngineImpl.isPlaying())
				mPlayerEngineImpl.stop();
		}
	}
	public synchronized void startPlaySong()
	{
		if(isPlayEnable())
		{
			List<SongInfor> songList = DBManager.newInstance(activity).getSongList(0, 200);
			if(songList.size() > 0)
			{
				startPlayDiangboMusic(songList);
			}
			else if(!mPlayerEngineImpl.isPlaying()) //如果当前没有歌曲在播放，就播放计划歌曲
			{
				startPlaySpeakerMusic(playList);
			}
		}
	}
	private boolean isPlayEnable()
	{
		if(mPlayerEngineImpl == null || mPlayerEngineImpl.isLoading())
			return false;
		
		if(mPlayerEngineImpl.isPause())//如果是暂停状态，就不接受任何播放
			return false;
		if(songInforPlay != null && mPlayerEngineImpl.isPlaying())//如果正在播放
		{
			if(songInforPlay.isPushMusic())//如果当前播放的歌曲为点播的，则要等待该歌曲播放完成
				return false;
		}
		return true;
	}
	private void startPlayDiangboMusic(List<SongInfor> playList)
	{
		songInforPlay = playList.get(0);
		DBManager.newInstance(activity).deleteSongByUrl(songInforPlay);
		
		mPlayerEngineImpl.playMedia(songInforPlay);
		updatePlayView();
	}
	private void startPlaySpeakerMusic(List<SongInfor> playList)
	{
		if(playList != null && playList.size() > 0)
		{
			//Random  random = new Random();
			//musicIndex = random.nextInt(playList.size() + 1);//getLocalData().getMusicIndex();
			int musicIndex = LocalDataEntity.newInstance(activity).getMusicIndex();
			if(musicIndex >= playList.size() || musicIndex < 0)
				musicIndex = 0;
			songInforPlay = playList.get(musicIndex);
			
			musicIndex ++;
			LocalDataEntity.newInstance(activity).setMusicIndex(musicIndex);
			
			mPlayerEngineImpl.playMedia(songInforPlay);
			
			updatePlayView();
		}
		else
		{
			songInforPlay = null;
			mPlayerEngineImpl.resetPlayInfor();
		}
	}
	
	private void updatePlayView()
	{
		activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mUIManager.updateMediaInfoView(mPlayerEngineImpl.getSongInfor());
				mUIManager.showControlView(true);
			}
		});
	}
	
	private boolean isFileExsit(SongInfor songInfor)
	{
		String url = songInfor.getMediaUrl();
		File file1 = new File(getMusicLocalPath(url));
		File file2 = new File(getMusicLocalPath(url) + ".temp");
		return file1.exists() && file2.exists();
	}
	private String getMusicLocalPath(String url)
	{
		String fileName = "";
		if(url.contains("."))
			fileName = url.substring(url.lastIndexOf("/") + 1, url.length());
		String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + fileName;
		
		return filePath;
	}
	
	private boolean isUpdateCurPosRunning = false;
	private void updateCurPos(SongInfor itemInfo)
	{
		if(!isUpdateCurPosRunning)
		{
			int index = LocalDataEntity.newInstance(activity).getMusicIndex();
			if(index > 0)
				index = index - 1;
			httpPresenter.updateCurPos(index, itemInfo.getMediaId());
		}
	}
	
	private PlayErrorCheck playErrorCheck = new PlayErrorCheck();
	private class MusicPlayEngineListener implements PlayerEngineListener
	{
		@Override
		public void onTrackPlay(final SongInfor itemInfo) 
		{
			mUIManager.showPlay(false);
			mUIManager.showControlView(true);
			
			if(!isFileExsit(songInforPlay))
			{
				DownloadFileInfo downloadFileInfo = new DownloadFileInfo(songInforPlay);
				if(!downloadFileInfo.isFileExist())
					FileDownLoadUtil.getInstance(activity).addFirstFile(songInforPlay);
			}
			updateCurPos(itemInfo);
		}

		@Override
		public void onTrackStop(final SongInfor itemInfo) 
		{
			mUIManager.showPlay(true);
			mUIManager.updateMediaInfoView(itemInfo);
			mUIManager.isSeekComplete = true;
		}

		@Override
		public void onTrackPause(SongInfor itemInfo) 
		{
			mUIManager.showPlay(true);
		}

		@Override
		public void onTrackPrepareSync(final SongInfor itemInfo) 
		{
			doPrepare();
		}

		@Override
		public void onTrackPrepareComplete(SongInfor itemInfo) 
		{
			int duration = mPlayerEngineImpl.getDuration();
			
			if(duration > 0)
			{
				mUIManager.setSeekbarMax(duration);
				mUIManager.setTotalTime(duration);
			}
		}
		
		@Override
		public void onTrackStreamError(SongInfor itemInfo) 
		{
			
		}

		@Override
		public void onTrackPlayComplete(SongInfor itemInfo) 
		{
			mPlayerEngineImpl.stop();
			
			restartTimer();
			String curPlayUrl = "";
			if(songInforPlay != null)
				curPlayUrl = songInforPlay.getMediaUrl();
			if(itemInfo == null || itemInfo.getMediaUrl() .equals(curPlayUrl))
			{
				startPlaySong();
			}
		}
	}
	
	@Override
	public boolean onError(MediaPlayer mp, int what, int extra)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onSeekComplete(MediaPlayer mp) 
	{
		// TODO Auto-generated method stub
		mUIManager.isSeekComplete = true;
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) 
	{
		// TODO Auto-generated method stub
		int tempDuration = mPlayerEngineImpl.getDuration();
		int time = tempDuration * percent / 100;
		mUIManager.setSeekbarSecondProgress(time);
	}
}
