
package com.znt.speaker.factory; 

import com.znt.diange.mina.entity.SongInfor;
import com.znt.speaker.R;
import com.znt.speaker.entity.LocalDataEntity;
import com.znt.speaker.player.AbstractTimer;
import com.znt.speaker.player.SingleSecondTimer;
import com.znt.speaker.util.DateUtils;
import com.znt.speaker.util.DlnaUtils;
import com.znt.speaker.util.FileUtils;

import android.app.Activity;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

/** 
 * @ClassName: UIManager 
 * @Description: TODO
 * @author yan.yu 
 * @date 2016-3-21 涓嬪崍5:00:37  
 */
public class UIManager implements OnClickListener, OnSeekBarChangeListener
{
	
	private Activity activity = null;
	
	public TextView mTVPrepareSpeed;
	
	public TextView mTVLoadSpeed;
	
	public View mControlView;	
	public TextView mTVSongName;
	public TextView mTVArtist;
	public TextView mTVAlbum;
	
	public ImageButton mBtnPlay;
	public ImageButton mBtnPause;
	public SeekBar mSeekBar;
	public TextView mTVCurTime;
	public TextView mTVTotalTime;
	public ImageView mIVAlbum; 
	
	public TextView tvCurTime = null;
	
	private TextureView textureView;
	private Surface surface = null;
	private View viewMusicPlayBg = null;
	private View viewVideoPlayView = null;
	//private View viewLoading = null;
	
	public TranslateAnimation mHideDownTransformation;
	public AlphaAnimation mAlphaHideTransformation;
	private AbstractTimer updateTimer;
	
	public View mSongInfoView;
	public boolean lrcShow = false;
	private long curTime = 0;
	
	private final int UPDATE_TIME = 1;
	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == UPDATE_TIME)
			{
				showCurTime();
			}
		};
	};
	
	public UIManager(Activity activity)
	{
		this.activity = activity;
		initView();
	}

	public void initView()
	{
		
		mTVPrepareSpeed = (TextView) activity.findViewById(R.id.tv_prepare_speed);
		
		mTVLoadSpeed = (TextView) activity.findViewById(R.id.tv_speed);
		
		mControlView = activity.findViewById(R.id.control_panel);	
		mTVSongName = (TextView) activity.findViewById(R.id.tv_title);
		mTVArtist = (TextView) activity.findViewById(R.id.tv_artist);
		mTVAlbum = (TextView) activity.findViewById(R.id.tv_album);
		
		mBtnPlay = (ImageButton) activity.findViewById(R.id.btn_play);
		mBtnPause = (ImageButton) activity.findViewById(R.id.btn_pause);
		mBtnPlay.setOnClickListener(this);
		mBtnPause.setOnClickListener(this);	
		mSeekBar = (SeekBar) activity.findViewById(R.id.playback_seeker);
		mTVCurTime = (TextView) activity.findViewById(R.id.tv_curTime);
		mTVTotalTime = (TextView) activity.findViewById(R.id.tv_totalTime);
		mIVAlbum = (ImageView) activity.findViewById(R.id.iv_album);
		
		tvCurTime = (TextView) activity.findViewById(R.id.tv_cur_time);
		updateTimer = new SingleSecondTimer(activity);
		updateTimer.setHandler(handler, UPDATE_TIME);
		startTimer();
		showCurTime();
		
		//viewLoading = activity.findViewById(R.id.prepare_panel);
		viewMusicPlayBg = activity.findViewById(R.id.view_player_bg_default);
		viewVideoPlayView = activity.findViewById(R.id.view_video_sufaceview);
		
		setSeekbarListener(this);
		
    	mSongInfoView = activity.findViewById(R.id.song_info_view);
	    
		mHideDownTransformation = new TranslateAnimation(0.0f, 0.0f,0.0f,200.0f);  
    	mHideDownTransformation.setDuration(1000);
    	
    	mAlphaHideTransformation = new AlphaAnimation(1, 0);
    	mAlphaHideTransformation.setDuration(1000);
    	
	}
	
	private void showCurTime()
	{
		if(curTime > 0)
		{
			curTime += 1000;
			String time = DateUtils.getDateFromLong(curTime);
			//String time = DateUtils.getEndDateFromLong(curTime);
			tvCurTime.setText(time);
		}
	}
	public void setCurTime(String time)
	{
		if(!TextUtils.isEmpty(time))
			curTime = Long.parseLong(time);
	}
	public void setCurTime(long time)
	{
		this.curTime = time;
	}
	public long getCurTime()
	{
		return curTime;
	}
	
	public void setSurfaceViewOritation(String degree)
	{
		/*DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		ViewUtils.setViewParams(activity, textureView,dm.heightPixels , dm.widthPixels);*/
		if(textureView != null)
		{
			float curDegree = getDegree(LocalDataEntity.newInstance(activity).getVideoWhirl());
			if(curDegree > 0)
			{
				curDegree = -curDegree;
				textureView.setRotation(curDegree);
			}
			textureView.setRotation(getDegree(degree));
		}
	}
	private float getDegree(String degree)
	{
		if(degree.equals("0"))//
			return 0;
		else if(degree.equals("1"))//
			return 90;
		else if(degree.equals("2"))//
			return -90;
		else if(degree.equals("3"))//
			return 180;
		return 0;
	}
	
	public void startTimer()
	{
		updateTimer.startTimer();
	}
	public void stopTimer()
	{
		if(updateTimer != null)
			updateTimer.stopTimer();
	}
	
	public void initTextureView()
	{
		textureView = (TextureView) activity.findViewById(R.id.surfaceView);
		textureView.setLayerType(TextureView.LAYER_TYPE_SOFTWARE, null);
		
		setSurfaceViewOritation(LocalDataEntity.newInstance(activity).getVideoWhirl());
	}
	public void releaseTextureView()
	{
		if(textureView != null)
		{
			textureView.destroyDrawingCache();
			textureView = null;
		}
	}
	public TextureView getTextureView()
	{
		return textureView;
	}
	public Surface getSurface()
	{
		return surface;
	}
	public void showDefaultView(SongInfor mediaInfor)
	{
		String url = mediaInfor.getMediaUrl();
		
		if(FileUtils.isMusic(url))
		{
			showPrepareLoadView(true);
		}
		else
		{
			showPrepareLoadView(false);
		}
		/*surfaceView.setVisibility(View.GONE);
		viewMusicPlayBg.setVisibility(View.GONE);*/
	}
	
	public void unInit(){
		
	}
	
	public void showDeviceInfor()
	{
		/*if(tvHint != null && activity != null)
		{
			DeviceInfor deviceInfor = LocalDataEntity.newInstance(activity).getDeviceInfor();
			String playRes = LocalDataEntity.newInstance(activity).getPlayRes();
			if(playRes.equals(PlayRes.LOCAL))
				playRes = "本地歌曲";
			else if(playRes.equals(PlayRes.ONLINE))
				playRes = "在线歌曲";
			tvHint.setText("ssid:  " + SystemUtils.getConnectWifiSsid(activity)
					+ "\n ip:  " + SystemUtils.getIP()
					 + "\n 播放模式:  " + playRes
					 + "\n 设备id:  " + deviceInfor.getId()
					 + "\n 设备code:  " + deviceInfor.getCode()
					 + "\n 设备名称:  " + deviceInfor.getName());
		}*/
	}
	
	public void showPrepareLoadView(boolean isShow)
	{
		if (isShow)
		{
			viewMusicPlayBg.setVisibility(View.VISIBLE);
			mSongInfoView.setVisibility(View.VISIBLE);
			if(textureView != null)
				textureView.setVisibility(View.GONE);
			viewVideoPlayView.setVisibility(View.GONE);
		}
		else
		{
			viewMusicPlayBg.setVisibility(View.GONE);
			mSongInfoView.setVisibility(View.GONE);
			if(textureView != null)
				textureView.setVisibility(View.VISIBLE);
			viewVideoPlayView.setVisibility(View.VISIBLE);
		}
	}
	
	public void showControlView(boolean show)
	{
		/*if (show)
		{
			mControlView.setVisibility(View.VISIBLE);
		}
		else
		{
			mControlView.setVisibility(View.GONE);
		}*/
	}
	
	public void play()
	{
		//MusicActivity.mPlayerEngineImpl.play();
	}
	
	public void pause()
	{
		//MusicActivity.mPlayerEngineImpl.pause();
	}
	
	public void stop()
	{
		stopTimer();
		//MusicActivity.mPlayerEngineImpl.stop();
	}
	
	private boolean isSeekbarTouch = false;	

	@Override
	public void onClick(View v) 
	{
		switch(v.getId())
		{
			case R.id.btn_play:
				play();
				break;
			case R.id.btn_pause:
				pause();
				break;
		}
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) 
	{
		setcurTime(progress);
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) 
	{
		isSeekbarTouch = true;
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) 
	{
		isSeekbarTouch = false;			
		seek(seekBar.getProgress());
	}
	
	public boolean isSeekComplete = false;
	public void seek(int pos)
	{
		/*isSeekComplete = false;
		MusicActivity.mPlayerEngineImpl.skipTo(pos);
		setSeekbarProgress(pos);*/
	}
	
	public void showPlay(boolean bShow)
	{
		if (bShow)
		{
			mBtnPlay.setVisibility(View.VISIBLE);
			mBtnPause.setVisibility(View.INVISIBLE);
		}
		else
		{
			mBtnPlay.setVisibility(View.INVISIBLE);
			mBtnPause.setVisibility(View.VISIBLE);
		}
	}
	
	public void togglePlayPause()
	{
		if (mBtnPlay.isShown())
		{
			play();
		}
		else
		{
			pause();
		}
	}
	
	public void setSeekbarProgress(int time)
	{
		if (!isSeekbarTouch)
		{
			mSeekBar.setProgress(time);	
		}
	}
	public int getMaxProgress()
	{
		return mSeekBar.getMax();
	}
	
	public boolean isConinCanRemove()
	{
		int progress = mSeekBar.getProgress();
		int max = mSeekBar.getMax();
		if(progress > 0 && max > 0)
		{
			if(((float)progress / max) * 100 > 10)
				return true;
		}
		return false;
	}
	public boolean isNearFinish()
	{
		int progress = mSeekBar.getProgress();
		int max = mSeekBar.getMax();
		if(progress > 0 && max > 0)
		{
			float curPro = ((float)progress / max) * 100;
			if(curPro == 80)
				return true;
		}
		return false;
	}
	
	public void setSeekbarSecondProgress(int time)
	{
		mSeekBar.setSecondaryProgress(time);	
	}
	
	public void setSeekbarMax(int max){
		mSeekBar.setMax(max);
	}
	
	public void setcurTime(int curTime)
	{
		final String timeString = DlnaUtils.formateTime(curTime);
		activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mTVCurTime.setText(timeString);
			}
		});
		
	}
	
	public void setTotalTime(int totalTime){
		String timeString = DlnaUtils.formateTime(totalTime);
		mTVTotalTime.setText(timeString);
	}
	
	public void updateMediaInfoView(SongInfor mSongInfor){
		
		if(mSongInfor == null)
			return;
		setcurTime(0);
		setTotalTime(0);
		setSeekbarMax(0);
		setSeekbarProgress(0);
		
		showDefaultView(mSongInfor);

		mTVSongName.setText(mSongInfor.getMediaName());
		mTVArtist.setText(mSongInfor.getArtist());
		mTVAlbum.setText(mSongInfor.getAlbumName());
	}
	
	public void setLoadingHint(String hint){
		//String showString = (int)speed + "KB/" + activity.getResources().getString(R.string.second);
		mTVPrepareSpeed.setText(hint);
		//mTVLoadSpeed.setText(hint);
	}
	

	public void setSeekbarListener(OnSeekBarChangeListener listener)
	{
		mSeekBar.setOnSeekBarChangeListener(listener);
	}

	public boolean isControlViewShow(){
		return mControlView.getVisibility() == View.VISIBLE ? true : false;
	}
	
	public boolean isLoadViewShow(){
		/*if (mLoadView.getVisibility() == View.VISIBLE || 
				mPrepareView.getVisibility() == View.VISIBLE){
			return true;
		}*/
		
		return false;
	}
	
}
 
