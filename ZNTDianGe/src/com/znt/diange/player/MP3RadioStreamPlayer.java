package com.znt.diange.player;

import java.io.IOException;
import java.nio.ByteBuffer;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.znt.diange.dlna.mediaserver.util.LogFactory;


public class MP3RadioStreamPlayer 
{
	
	public final String LOG_TAG = "MP3RadioStreamPlayer";
	
	protected MediaExtractor extractor;
	protected MediaCodec codec;
	protected AudioTrack audioTrack;
	protected MediaFormat format = null;
	
	protected int inputBufIndex;
	protected int bufIndexCheck;
	protected int lastInputBufIndex;
	
	protected Boolean doStop = false;
	protected Boolean palyEnable = true;
	/*
	 * Delegate to receive notifications
	 */
	protected OnStreamPlayListener onStreamPlayListener;
	
	public interface OnStreamPlayListener
	{
		public void onStreamPlayStarted();
		public void onStreamPlayStoped();
		public void onStreamPlayError();
		public void onStreamPlayProgress(int progress);
		public void onGetDuration(long duration);
		
	}
	
	/**
 	 * Set the delegate for this instance. The delegate will receive notifications about the player's status
 	 * 
 	 * @param mDelegate
 	 */
 	public void setOnStreamPlayListener(OnStreamPlayListener onStreamPlayListener)
 	{
		this.onStreamPlayListener = onStreamPlayListener;
	}
 	
	 // indicates the state our service:
 	public enum State {
        Retrieving, // retrieving music (filling buffer)
        Stopped,    // player is stopped and not prepared to play
        Playing,    // playback active 
    };
    
    /**
     * Current player state
     */
    State mState = State.Retrieving;
    
    /**
     * Getter for mState
     */
    public State getState()
    {
    	return mState;
    }
    
	/**
	 * String The url of the radio stream
	 */
	private String mUrlString;
	
	public void setUrlString(String mUrlString) {
		this.mUrlString = mUrlString;
	}
	/**
	 * mUrlString getter
	 */
	public String getUrlString() {
		return mUrlString;
	}
	
	/**
	 * Constructor
	 * 
	 * @param url String The url of the radio stream
	 */
	public MP3RadioStreamPlayer(){
		mState = State.Stopped;
	}
	
	/**
	 * Attempts to fetch mp3 data from the mUrlString location, decode it and feed it to an AudioTrack instance
	 * 
	 * @return void
	 * @throws IOException 
	 */
	public void play(boolean palyEnable) throws IOException
	{
		this.palyEnable = palyEnable;
		
		mState = State.Retrieving;
		
		doStop = false;
		bufIndexCheck = 0;
		lastInputBufIndex = -1;
		
		new DecodeOperation().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		
	}
	
	public void seekToPosition(long timeUs)
	{
		extractor.seekTo(timeUs, MediaExtractor.SEEK_TO_NEXT_SYNC);
	}
	 
	/**
	 * @throws IOException 
	 * 
	 * 
	 */
	private void decodeLoop()
	{
		
        ByteBuffer[] codecInputBuffers;
        ByteBuffer[] codecOutputBuffers;
        
        // extractor gets information about the stream
        extractor = new MediaExtractor();
        String mime = "";
        try 
        {
        	//String path = Environment.getExternalStorageDirectory() + "/sdcard/Music/bandry.ape";
			extractor.setDataSource(mUrlString);
			
			format = extractor.getTrackFormat(0);
	        mime = format.getString(MediaFormat.KEY_MIME);
	        long duration = format.getLong(MediaFormat.KEY_DURATION) / 1000;
	        
	        onStreamPlayListener.onGetDuration(duration);
			
		} 
        catch (Exception e) 
		{
			onStreamPlayListener.onStreamPlayError();
			return;
		}
        
        if(format == null || TextUtils.isEmpty(mime))
        {
			onStreamPlayListener.onStreamPlayError();
			return;
		}
        
        if(!palyEnable)
        	return;
        
        // the actual decoder
        codec = MediaCodec.createDecoderByType(mime);
        try
		{
        	codec.configure(format, null /* surface */, null /* crypto */, 0 /* flags */);
            codec.start();
		} 
        catch (Exception e)
		{
			// TODO: handle exception
        	LogFactory.createLog().e("codec.configure error-->" + e.getMessage());
        	onStreamPlayListener.onStreamPlayError();
			return;
		}
        codecInputBuffers = codec.getInputBuffers();
        codecOutputBuffers = codec.getOutputBuffers();
        // get the sample rate to configure AudioTrack
		int sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE);
		
		/*Log.i(LOG_TAG,"mime "+mime);
		Log.i(LOG_TAG,"sampleRate "+sampleRate);*/
        
		// create our AudioTrack instance
        audioTrack = new AudioTrack(
				AudioManager.STREAM_MUSIC, 
				sampleRate, 
				AudioFormat.CHANNEL_OUT_STEREO, 
				AudioFormat.ENCODING_PCM_16BIT, 
				AudioTrack.getMinBufferSize (
						sampleRate, 
						AudioFormat.CHANNEL_OUT_STEREO, 
						AudioFormat.ENCODING_PCM_16BIT
						), 
				AudioTrack.MODE_STREAM
				);
        
        // start playing, we will feed you later
        audioTrack.play();
        extractor.selectTrack(0);
        
        // start decoding
        final long kTimeOutUs = 10000;
        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
        boolean sawInputEOS = false;
        boolean sawOutputEOS = false;
        int noOutputCounter = 0;
        int noOutputCounterLimit = 50;
        
        while (!sawOutputEOS && noOutputCounter < noOutputCounterLimit && !doStop)
        {
        	//Log.i(LOG_TAG, "loop ");
        	noOutputCounter++;
            if (!sawInputEOS) 
            {
            	
                inputBufIndex = codec.dequeueInputBuffer(kTimeOutUs);
                bufIndexCheck++;
               // Log.d(LOG_TAG, " bufIndexCheck " + bufIndexCheck);
                if (inputBufIndex >= 0)
                {
                    ByteBuffer dstBuf = codecInputBuffers[inputBufIndex];

                    int sampleSize = extractor.readSampleData(dstBuf, 0 /* offset */);

                    long presentationTimeUs = 0;

                    if (sampleSize < 0) 
                    {
                        Log.d(LOG_TAG, "saw input EOS.");
                        sawInputEOS = true;
                        sampleSize = 0;
                    }
                    else 
                    {
                        presentationTimeUs = extractor.getSampleTime();
                    }
                    // can throw illegal state exception (???)
   
                	codec.queueInputBuffer(
                            inputBufIndex,
                            0 /* offset */,
                            sampleSize,
                            presentationTimeUs,
                            sawInputEOS ? MediaCodec.BUFFER_FLAG_END_OF_STREAM : 0);

                    if (!sawInputEOS) 
                    {
                        extractor.advance();
                    }
                }
                else
                {
                	Log.e(LOG_TAG, "inputBufIndex " +inputBufIndex);
                }
            }
            /*//long duration = extractor.getCachedDuration();
            long duration = extractor.getSampleTime();
            String d = StringUtils.getFormatTime(duration);*/
            
            int res = codec.dequeueOutputBuffer(info, kTimeOutUs);
            if (res >= 0) 
            {
                //Log.d(LOG_TAG, "got frame, size " + info.size + "/" + info.presentationTimeUs);
                if (info.size > 0) 
                {
                    noOutputCounter = 0;
                }

                int outputBufIndex = res;
                ByteBuffer buf = codecOutputBuffers[outputBufIndex];
                final byte[] chunk = new byte[info.size];
                buf.get(chunk);
                buf.clear();
                if(chunk.length > 0)
                {        	
                	audioTrack.write(chunk,0,chunk.length);
                	if(this.mState != State.Playing)
                	{
                		onStreamPlayListener.onStreamPlayStarted();
                	}
                	this.mState = State.Playing;
                }
                
                int progress = (int) Math.ceil((float)(info.presentationTimeUs / (1000 * 1000)));
                if(progress > 0)
                	onStreamPlayListener.onStreamPlayProgress(progress);
                
                codec.releaseOutputBuffer(outputBufIndex, false /* render */);
                if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) 
                {
                    Log.d(LOG_TAG, "saw output EOS.");
                    sawOutputEOS = true;
                }
            } 
            else if (res == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED)
            {
                codecOutputBuffers = codec.getOutputBuffers();

                Log.d(LOG_TAG, "output buffers have changed.");
            } 
            else if (res == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) 
            {
                MediaFormat oformat = codec.getOutputFormat();

                Log.d(LOG_TAG, "output format has changed to " + oformat);
            } 
            else 
            {
                Log.d(LOG_TAG, "dequeueOutputBuffer returned " + res);
            }
        }
        
        Log.d(LOG_TAG, "stopping...");
        
        relaxResources(true);
        
        this.mState = State.Stopped;
        doStop = true;
        
    	// attempt reconnect
        if(sawOutputEOS)
        {
        	try 
        	{
				MP3RadioStreamPlayer.this.play(palyEnable);
				return;
			} 
        	catch (IOException e) 
        	{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }  
        
        if(noOutputCounter >= noOutputCounterLimit)
        {
        	onStreamPlayListener.onStreamPlayError();
        }
        else
        {
        	onStreamPlayListener.onStreamPlayStoped();
        }
	}
	
	public void release()
	{
		stop();
		relaxResources(false);
	}
	
	private void relaxResources(Boolean release)
	{
		if(codec != null)
		{
			if(release)
			{
				codec.stop();
				codec.release();
				codec = null;
			}
		    
		}
	    if(audioTrack != null)
	    {
	    	audioTrack.flush();
		    audioTrack.release();
		    audioTrack = null;	
	    }
	}
	
	/**
	 * Stops playback
	 * 
	 * @return void
	 */
	public void stop()
	{
		doStop = true;
	}
	
	/**
	 * AsyncTask that takes care of running the decode/playback loop
	 *
	 */
	private class DecodeOperation extends AsyncTask<Void, Void, Void> 
	{
       
		@Override
        protected Void doInBackground(Void... values) 
		{	
			MP3RadioStreamPlayer.this.decodeLoop();
        	return null;
        }
        @Override
        protected void onPreExecute()
        {
        	
        }
        @Override
        protected void onProgressUpdate(Void... values) 
        {
        	
        }
    }
}
