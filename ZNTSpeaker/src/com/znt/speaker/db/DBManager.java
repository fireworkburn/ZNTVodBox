package com.znt.speaker.db; 

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.text.TextUtils;

import com.znt.diange.mina.entity.SongInfor;
import com.znt.diange.mina.entity.UserInfor;
import com.znt.speaker.entity.CurPlanSubInfor;
import com.znt.speaker.entity.LocalDataEntity;
import com.znt.speaker.util.DateUtils;

/** 
 * @ClassName: DBManager 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-8-14 涓婂崍10:25:36  
 */
public class DBManager extends MyDbHelper
{

	private static DBManager INSTANCE = null;
	private Context context = null;
	
	/** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param c 
	*/
	public DBManager(Context c)
	{
		super(c);
		this.context = c;
		// TODO Auto-generated constructor stub
	}

	public static DBManager newInstance(Context c)
	{
		if(INSTANCE == null)
		{
			synchronized (DBManager.class)
			{
				if(INSTANCE == null)
					INSTANCE = new DBManager(c);	
			}
		}
		return INSTANCE;
	}
	
	/**
    * @Description: 鎻掑叆闊充箰
    * @param infor   
    * @return void 
    * @throws
     */
    public synchronized long insertSong(SongInfor infor)
    {
    	if(infor == null)
    		return -1;
    	
    	//濡傛灉鐐规挱闃熷垪涓湁璇ユ瓕鏇插氨鏇存柊璇ユ瓕鏇�
    	if(isSongExist(infor))
    	{
    		updateSong(infor);
    		return -1;
    	}
    	
    	ContentValues values = new ContentValues();
    	
    	String musicName = infor.getMediaName();
    	String artist = infor.getArtist();
    	/*if(TextUtils.isEmpty(artist) 
    			&& infor.getMediaType() == SongInfor.MEDIA_TYPE_LOCAL)//鏈湴闊充箰鍒嗙姝屾墜鍜屾瓕鏇插悕
    	{
    		if(musicName.contains("-"))
    			artist = musicName.substring(0, musicName.indexOf("-"));
    	}*/
    		
    	if(!TextUtils.isEmpty(musicName))
    		values.put("music_name", musicName);
    	if(!TextUtils.isEmpty(infor.getMediaUrl()))
    		values.put("music_url", infor.getMediaUrl());
    	if(!TextUtils.isEmpty(infor.getAlbumName()))
    		values.put("music_album", infor.getAlbumName());
    	if(!TextUtils.isEmpty(infor.getAlbumUrl()))
    		values.put("music_album_url", infor.getMediaId());
    	if(!TextUtils.isEmpty(artist))
    		values.put("music_artist", artist);
    	
    	values.put("play_state", infor.getPlayState());
    	values.put("play_time", System.currentTimeMillis() + "");
    	values.put("music_coin", infor.getCoin());
    	values.put("trand_id", infor.getMediaId());
    	if(infor.getUserInfor() != null)
    	{
    		values.put("user_id", infor.getUserInfor().getUserId());
        	values.put("user_name", infor.getUserInfor().getUserName());
    	}
    	values.put("play_message", infor.getPlayMsg());
    	
		return insert(values, TBL_SONG_LIST);
    }
    public synchronized long insertSongAdmin(SongInfor infor)
    {
    	if(infor == null)
    		return -1;
    	
    	//濡傛灉鐐规挱闃熷垪涓湁璇ユ瓕鏇插氨鏇存柊璇ユ瓕鏇�
    	if(isSongExist(infor))
    	{
    		updateSong(infor);
    		return -1;
    	}
    	
    	ContentValues values = new ContentValues();
    	
    	String musicName = infor.getMediaName();
    	String artist = infor.getArtist();
    	/*if(TextUtils.isEmpty(artist) 
    			&& infor.getMediaType() == SongInfor.MEDIA_TYPE_LOCAL)//鏈湴闊充箰鍒嗙姝屾墜鍜屾瓕鏇插悕
    	{
    		if(musicName.contains("-"))
    			artist = musicName.substring(0, musicName.indexOf("-"));
    	}*/
    	
    	if(!TextUtils.isEmpty(musicName))
    		values.put("music_name", musicName);
    	if(!TextUtils.isEmpty(infor.getMediaUrl()))
    		values.put("music_url", infor.getMediaUrl());
    	if(!TextUtils.isEmpty(infor.getAlbumName()))
    		values.put("music_album", infor.getAlbumName());
    	/*if(!TextUtils.isEmpty(infor.getAlbumUrl()))
    		values.put("music_album_url", infor.getAlbumUrl());*/
    	if(!TextUtils.isEmpty(artist))
    		values.put("music_artist", artist);
    	
    	values.put("play_state", infor.getPlayState());
    	values.put("play_time", System.currentTimeMillis() + "");
    	values.put("music_coin", infor.getCoin());
    	values.put("trand_id", infor.getMediaId());
    	values.put("user_id", infor.getUserInfor().getUserId());
    	values.put("user_name", infor.getUserInfor().getUserName());
    	values.put("play_message", infor.getPlayMsg());
    	
    	return insert(values, TBL_SONG_LIST_ADMIN);
    }
    
    public synchronized long insertSongRecord(SongInfor infor, long modify_time)
    {
    	if(infor == null)
    		return -1;
    	if(isRecorExist(infor))
    	{
    		if(modify_time <= 0)
    			updateSongRecord(infor, modify_time);
    		return -1;
    	}
    	
    	ContentValues values = new ContentValues();
    	
    	String musicName = infor.getMediaName();
    	if(!TextUtils.isEmpty(musicName))
    		values.put("music_name", musicName);
    	if(!TextUtils.isEmpty(infor.getMediaUrl()))
    		values.put("music_url", infor.getMediaUrl());
    	if(modify_time <= 0)
    		modify_time = System.currentTimeMillis();
    	values.put("modify_time", System.currentTimeMillis());
    	
    	return insert(values, TBL_SONG_RECORD);
    }
    /**
     * 鏇存柊闊充箰淇℃伅
     * @param infor
     * @return
     */
    public synchronized int updateSongRecord(SongInfor infor, long modify_time)
    {
    	if(infor == null)
   		 return -1;
    	ContentValues values = new ContentValues();
    	String musicName = infor.getMediaName();
    	if(!TextUtils.isEmpty(musicName))
    		values.put("music_name", musicName);
    	if(!TextUtils.isEmpty(infor.getMediaUrl()))
    		values.put("music_url", infor.getMediaUrl());
    	if(modify_time <= 0)
    		modify_time = System.currentTimeMillis();
    	values.put("modify_time", System.currentTimeMillis());
    	
    	return edit(TBL_SONG_RECORD, infor.getUserInfor().getUserId(), "0", values);
    }
    
    /**
     * @Description: 鍒ゆ柇闊充箰鏄惁瀛樺湪
     * @param @param music
     * @param @return   
     * @return boolean 
     * @throws
      */
     public synchronized boolean isRecorExist(SongInfor music)
     {
     	if(music == null)
    		 return false;
     	boolean  result = false;
     	
     	Cursor cur = query(TBL_SONG_RECORD);
      	if(cur != null && cur.getCount() > 0)
      	{
      		while(cur.moveToNext())
      		{
      			String music_url = cur.getString(cur.getColumnIndex("music_url"));
      			if(music_url == null)
  				{
      				result = false;
      				break;
  				}
      			if(music.getMediaUrl().equals(music_url))
      			{
      				result = true;
      				break;
      			}
      		}
      	}
      	if(cur != null )
      		cur.close();
     	return result;
     }
    
    /**
     * 鏇存柊闊充箰淇℃伅
     * @param infor
     * @return
     */
    public synchronized int updateSong(SongInfor infor)
    {
    	if(infor == null)
   		 return -1;
    	ContentValues values = new ContentValues();
    	
    	String musicName = infor.getMediaName();
    	String artist = infor.getArtist();
    	if(!TextUtils.isEmpty(musicName))
    		values.put("music_name", musicName);
    	if(!TextUtils.isEmpty(infor.getMediaUrl()))
    		values.put("music_url", infor.getMediaUrl());
    	if(!TextUtils.isEmpty(infor.getAlbumName()))
    		values.put("music_album", infor.getAlbumName());
    	if(!TextUtils.isEmpty(infor.getAlbumUrl()))
    		values.put("music_album_url", infor.getMediaId());
    	if(!TextUtils.isEmpty(artist))
    		values.put("music_artist", artist);
    	if(infor.getUserInfor() != null)
    	{
    		values.put("user_id", infor.getUserInfor().getUserId());
        	values.put("user_name", infor.getUserInfor().getUserName());
    	}
    	values.put("play_state", infor.getPlayState());
    	//values.put("play_time", System.currentTimeMillis());
    	values.put("music_coin", infor.getCoin());
    	values.put("trand_id", infor.getMediaId());
    	values.put("play_message", infor.getPlayMsg());
    	//values.put("user_id", infor.getUserId());
    	
    	return edit(TBL_SONG_LIST, infor.getUserInfor().getUserId(), "0", values);
    }
    
    public synchronized int updateSongState(SongInfor infor)
    {
    	if(infor == null)
   		 return -1;
    	ContentValues values = new ContentValues();
    	values.put("play_state", infor.getPlayState());
    	return edit(TBL_SONG_RECORD, infor.getUserInfor().getUserId(), values);
    }
    
    /**
    * @Description: 鍒ゆ柇闊充箰鏄惁瀛樺湪
    * @param @param music
    * @param @return   
    * @return boolean 
    * @throws
     */
    public synchronized boolean isSongExist(SongInfor music)
    {
    	if(music == null)
   		 return false;
    	boolean  result = false;
    	
    	Cursor cur = query(TBL_SONG_LIST);
     	if(cur != null && cur.getCount() > 0)
     	{
     		while(cur.moveToNext())
     		{
     			if(music.getUserInfor() == null)
 				{
     				result = false;
     				break;
 				}
     			//鏍规嵁鐢ㄦ埛id瀵规瘮鐨勶紝鍚屼竴涓敤鎴峰彧鑳界偣鎾竴棣栨瓕鏇�
     			/*String user_id = cur.getString(cur.getColumnIndex("user_id"));
     			int play_state = cur.getInt(cur.getColumnIndex("play_state"));
     			if(user_id != null && user_id.equals(music.getUserInfor().getUserId())
     					&& play_state == 0)
     			{
     				result = true;
     				break;
     			}*/
     			String musicUrl = cur.getString(cur.getColumnIndex("music_url"));
     			if(musicUrl != null && musicUrl.equals(music.getMediaUrl()))
     			{
     				result = true;
     				break;
     			}
     		}
     	}
     	if(cur != null )
     		cur.close();
    	return result;
    }
    public synchronized boolean isSongExist(SongInfor music, String key)
    {
    	if(music == null)
      		 return false;
    	boolean  result = false;
    	Cursor cur = query(TBL_SONG_LIST);
    	if(cur != null && cur.getCount() > 0)
    	{
    		while(cur.moveToNext())
    		{
    			String music_name = cur.getString(cur.getColumnIndex(key));
    			if(music_name != null && music_name.trim().equals(music.getMediaName().trim()))
    			{
    				result = true;
    				//updateMusic(music);
    				break;
    			}
    		}
    	}
    	if(cur != null )
     		cur.close();
    	return result;
    }
    
    /**
     * @Description: 鑾峰彇闊充箰鍒楄〃
     * @param @return   
     * @return List<GoodsInfor> 
     * @throws
      */
     public synchronized List<SongInfor> getSongList(int pageNum, int pageSize)
     {
     	List<SongInfor> tempList = new ArrayList<SongInfor>();
     	Cursor cur = query(TBL_SONG_LIST);
     	if(cur == null || cur.getCount() == 0)
     		 return tempList;
     	if(cur != null)
     	{
     		cur.moveToPosition(pageNum * pageSize);
     		for(int i = pageNum * pageSize;i < (pageNum + 1) * pageSize;i++)
     		{
     			if(cur.moveToPosition(i))
     			{
     				SongInfor tempInfor = new SongInfor();
     				String music_name = cur.getString(cur.getColumnIndex("music_name"));
     				String music_artist = cur.getString(cur.getColumnIndex("music_artist"));
     				//String music_album = cur.getString(cur.getColumnIndex("music_album"));
     				String music_album_url = cur.getString(cur.getColumnIndex("music_album_url"));
     				int play_state = cur.getInt(cur.getColumnIndex("play_state"));
     				String play_time = cur.getString(cur.getColumnIndex("play_time"));
     				int music_coin = cur.getInt(cur.getColumnIndex("music_coin"));
     				String trand_id = cur.getString(cur.getColumnIndex("trand_id"));
     				String user_id = cur.getString(cur.getColumnIndex("user_id"));
     				String user_name = cur.getString(cur.getColumnIndex("user_name"));
     				String play_message = cur.getString(cur.getColumnIndex("play_message"));
     				//String music_lyric = cur.getString(cur.getColumnIndex("music_lyric"));
     				String music_url = cur.getString(cur.getColumnIndex("music_url"));
     				
     				tempInfor.setMediaName(music_name);
     				tempInfor.setArtist(music_artist);
     				//tempInfor.setAlbumName(music_album);
     				tempInfor.setMediaId(trand_id);
     				tempInfor.setPlayState(play_state);
     				tempInfor.setPlayTime(play_time);
     				tempInfor.setCoin(music_coin);
     				tempInfor.setTrandId(trand_id);
     				UserInfor userInfor = new UserInfor();
     				userInfor.setUserId(user_id);
     				userInfor.setUserName(user_name);
     				tempInfor.setUserInfor(userInfor);
     				tempInfor.setPlayMsg(play_message);
     				tempInfor.setMediaUrl(music_url);
     				tempInfor.setMusicPlayType(SongInfor.MUSIC_PLAY_TYPE_PUSH);;
         			tempList.add(tempInfor);
     			}
     		}
     	}
     	if(cur != null )
     		cur.close();
     	return tempList;
     }
     
     /**
      * @Description: 鑾峰彇闊充箰鎾斁璁板綍
      * @param @return   
      * @return List<GoodsInfor> 
      * @throws
      */
     public synchronized List<SongInfor> getSongRecord(int pageNum, int pageSize)
     {
    	 List<SongInfor> tempList = new ArrayList<SongInfor>();
    	 Cursor cur = query(TBL_SONG_RECORD);
    	 if(cur == null || cur.getCount() == 0)
    		 return tempList;
    	 if(cur != null)
    	 {
    		 cur.moveToPosition(pageNum * pageSize);
    		 for(int i = pageNum * pageSize;i < (pageNum + 1) * pageSize;i++)
    		 {
    			 if(cur.moveToPosition(i))
    			 {
    				 SongInfor tempInfor = new SongInfor();
    				 String music_name = cur.getString(cur.getColumnIndex("music_name"));
    				 String music_artist = cur.getString(cur.getColumnIndex("music_artist"));
    				 //String music_album = cur.getString(cur.getColumnIndex("music_album"));
    				 //String music_album_url = cur.getString(cur.getColumnIndex("music_album_url"));
    				 int play_state = cur.getInt(cur.getColumnIndex("play_state"));
    				 //String play_time = cur.getString(cur.getColumnIndex("play_time"));
    				 //int music_coin = cur.getInt(cur.getColumnIndex("music_coin"));
    				 //String trand_id = cur.getString(cur.getColumnIndex("trand_id"));
    				 //String user_id = cur.getString(cur.getColumnIndex("user_id"));
    				 //String user_name = cur.getString(cur.getColumnIndex("user_name"));
    				 //String play_message = cur.getString(cur.getColumnIndex("play_message"));
    				 //String music_lyric = cur.getString(cur.getColumnIndex("music_lyric"));
    				 String music_url = cur.getString(cur.getColumnIndex("music_url"));
    				 if(!TextUtils.isEmpty(music_url))
    				 {
    					 File file = new File(music_url);
        				 if(file.exists())
        				 {
        					 tempInfor.setMediaName(music_name);
            				 tempInfor.setArtist(music_artist);
            				 //tempInfor.setAlbumName(music_album);
            				 //tempInfor.setAlbumUrl(music_album_url);
            				 tempInfor.setPlayState(play_state);
            				 //tempInfor.setPlayTime(play_time);
            				 //tempInfor.setCoin(music_coin);
            				 //tempInfor.setTrandId(trand_id);
            				 UserInfor userInfor = new UserInfor();
            				 //userInfor.setUserId(user_id);
            				 //userInfor.setUserName(user_name);
            				 tempInfor.setUserInfor(userInfor);
            				 //tempInfor.setPlayMsg(play_message);
            				 tempInfor.setMediaUrl(music_url);
            				 
            				 tempList.add(tempInfor);
        				 }
        				 else
        					 deleteSongRecordByUrl(music_url);
    				 }
    			 }
    		 }
    	 }
    	 if(cur != null )
    		 cur.close();
    	 return tempList;
     }
     
     /**
      * 杩囨护鏈湴鏃犵敤鐨勬枃浠�
      */
     public synchronized void filterInvalidFile()
     {
    	 Cursor cur = query(TBL_SONG_RECORD);
    	 if(cur == null || cur.getCount() == 0)
    		 return ;
    	 if(cur != null)
    	 {
    		 while(cur.moveToNext())
			 {
				 String music_url = cur.getString(cur.getColumnIndex("music_url"));
				 if(!TextUtils.isEmpty(music_url))
				 {
					 File file = new File(music_url);
					 if(!file.exists())
					 {
						 deleteSongRecordByUrl(music_url);
					 }
				 }
			 }
    	 }
    	 if(cur != null )
    		 cur.close();
     }
     
     public synchronized SongInfor getSongByUser(String userId)
     {
    	 Cursor cur = query(TBL_SONG_RECORD);
    	 SongInfor tempInfor = new SongInfor();
    	 if(cur != null && cur.getCount() > 0)
    	 {
    		 while(cur.moveToNext())
    		 {
    			 int play_state = cur.getInt(cur.getColumnIndex("play_state"));
    			 if(play_state == 1)//褰撳墠姝ｅ湪鎾斁鐘舵��
    			 {
	      				String music_name = cur.getString(cur.getColumnIndex("music_name"));
	      				String music_artist = cur.getString(cur.getColumnIndex("music_artist"));
	      				String music_album = cur.getString(cur.getColumnIndex("music_album"));
	      				String music_album_url = cur.getString(cur.getColumnIndex("music_album_url"));
	      				String play_time = cur.getString(cur.getColumnIndex("play_time"));
	      				int music_coin = cur.getInt(cur.getColumnIndex("music_coin"));
	      				String trand_id = cur.getString(cur.getColumnIndex("trand_id"));
	      				String user_id = cur.getString(cur.getColumnIndex("user_id"));
	      				String user_name = cur.getString(cur.getColumnIndex("user_name"));
	      				String play_message = cur.getString(cur.getColumnIndex("play_message"));
	      				//String music_lyric = cur.getString(cur.getColumnIndex("music_lyric"));
	      				String music_url = cur.getString(cur.getColumnIndex("music_url"));
	      				
	      				tempInfor.setMediaName(music_name);
	      				tempInfor.setArtist(music_artist);
	      				tempInfor.setAlbumName(music_album);
	      				tempInfor.setAlbumUrl(music_album_url);
	      				tempInfor.setPlayState(play_state);
	      				tempInfor.setPlayTime(play_time);
	      				tempInfor.setCoin(music_coin);
	      				tempInfor.setTrandId(trand_id);
	      				UserInfor userInfor = new UserInfor();
	      				userInfor.setUserId(user_id);
	      				userInfor.setUserName(user_name);
	      				tempInfor.setUserInfor(userInfor);
	      				tempInfor.setPlayMsg(play_message);
	      				tempInfor.setMediaUrl(music_url);
	      				break;
    			 }
    		 }
    	 }
    	 if(cur != null )
      		cur.close();
    	 return tempInfor;
     }
     
     public synchronized int getSongCount()
     {
    	 Cursor cur = query(TBL_SONG_LIST);
    	 if(cur == null)
    		 return 0;
    	 return cur.getCount();
     }
     
     public synchronized int getRecordCount()
     {
    	 Cursor cur = query(TBL_SONG_RECORD);
    	 if(cur == null)
    		 return 0;
    	 return cur.getCount();
     }
     
     public synchronized void deleteAllSong()
     {
    	 /*Cursor cur = query(TBL_SONG_LIST);
      	if(cur != null && cur.getCount() > 0)
      	{
      		while(cur.moveToNext())
      		{
      			String music_id = cur.getString(cur.getColumnIndex("music_url"));
      			delete("music_url", music_id, TBL_SONG_LIST);
      		}
      	}
      	if(cur != null )
     		cur.close();*/
    	 deleteAll(TBL_SONG_LIST);
     }
     public synchronized void deleteSongRecordByUrl(String music_url)
     {
    	 /*Cursor cur = query(TBL_SONG_LIST);
    	 if(cur != null && cur.getCount() > 0)
    	 {
    		 while(cur.moveToNext())
    		 {
    			 String music_url = cur.getString(cur.getColumnIndex("music_url"));
    			 if(music_url.equals(musicUrl))
    			 {
    				 delete("music_url", music_url, TBL_SONG_RECORD);
    				 break;
    			 }
    		 }
    	 }
    	 if(cur != null )
    		 cur.close();*/
    	 delete("music_url", music_url, TBL_SONG_RECORD);
     }
     public synchronized void deleteLastSongRecordByIndex(int size)
     {
    	 Cursor cur = query(TBL_SONG_RECORD);
    	 int deleteCount = 0;
    	 if(cur != null && cur.getCount() > size)
    	 {
    		 while(cur.moveToLast())
    		 {
    			 //String music_name = cur.getString(cur.getColumnIndex("music_name"));
    			 String music_url = cur.getString(cur.getColumnIndex("music_url"));
				 delete("music_url", music_url, TBL_SONG_RECORD);
				 File file = new File(music_url);
				 if(file.exists())
					 file.delete();
				 else
					 deleteSongRecordByUrl(music_url);
				 deleteCount ++;
				 if(deleteCount >= size)
					 break;
    		 }
    	 }
    	 if(cur != null )
    		 cur.close();
     }
     public synchronized void deleteLastSongRecordBySize(long deleteSize)
     {
    	 Cursor cur = query(TBL_SONG_RECORD);
    	 long deletedSize = 0;
    	 if(cur != null && deleteSize > 0)
    	 {
    		 while(cur.moveToNext())
    		 {
    			 //String music_name = cur.getString(cur.getColumnIndex("music_name"));
    			 String music_url = cur.getString(cur.getColumnIndex("music_url"));
    			 //delete("music_url", music_url, TBL_SONG_RECORD);
    			 File file = new File(music_url);
    			 if(file.exists())
    			 {
    				 deletedSize += file.length();
    				 if(file.delete())
    					 deleteSongRecordByUrl(music_url);
    				 if(deletedSize >= deleteSize)
        				 break;
    			 }
    			 else
    				 deleteSongRecordByUrl(music_url);
    		 }
    	 }
    	 if(cur != null )
    		 cur.close();
     }
     public synchronized void deleteAllRecord()
     {
    	 /*Cursor cur = query(TBL_SONG_LIST);
    	 if(cur != null && cur.getCount() > 0)
    	 {
    		 while(cur.moveToNext())
    		 {
    			 String music_id = cur.getString(cur.getColumnIndex("music_url"));
    			 delete("music_url", music_id, TBL_SONG_LIST);
    		 }
    	 }
    	 if(cur != null )
    		 cur.close();*/
    	 deleteAll(TBL_SONG_RECORD);
     }
     /**
      * 鍒犻櫎闊充箰
      * @param id
      * @return
      */
     public synchronized int deleteSongByUrl(SongInfor infor)
     {
    	 if(infor == null)
    		 return -1;
    	 return delete("music_url", infor.getMediaUrl(), TBL_SONG_LIST);
     }
     public synchronized int deleteSongByUserId(String userId)
     {
    	 return delete("user_id", userId, TBL_SONG_LIST);
     }
     public synchronized int deleteSongByUrl(String music_url)
     {
   	  	 return delete("music_url", music_url, TBL_SONG_LIST);
     }
     
     /**************绠＄悊鍛樻搷浣�**************/
     public synchronized long insertAdmin(UserInfor infor)
     {
     	if(infor == null)
     		return -1;
     	
     	ContentValues values = new ContentValues();
     	
     	String name = infor.getUserName();
     	String id = infor.getUserId();
     	
     	if(!TextUtils.isEmpty(name))
     		values.put("user_name", name);
     	if(!TextUtils.isEmpty(id))
     		values.put("user_id", id);
     	values.put("modify_time", System.currentTimeMillis());
     	
     	return insert(values, TBL_ADMIN);
     }
     public synchronized boolean isAdminExist(String userId)
     {
     	boolean  result = false;
     	Cursor cur = query(TBL_ADMIN);
     	if(cur != null && cur.getCount() > 0)
     	{
     		while(cur.moveToNext())
     		{
     			String user_id = cur.getString(cur.getColumnIndex("user_id"));
     			if(user_id != null && user_id.trim().equals(userId.trim()))
     			{
     				result = true;
     				//updateMusic(music);
     				break;
     			}
     		}
     	}
     	if(cur != null )
     		cur.close();
     	return result;
     }
     
     /**************WIFI鎿嶄綔*****************/
     public synchronized long insertWifi(String wifiName, String wifiPwd)
     {
     	if(TextUtils.isEmpty(wifiName) || TextUtils.isEmpty(wifiPwd))
     		return -1;
     	
     	if(isWifiExist(wifiName))
     	{
     		updateWifi(wifiName, wifiPwd);
     		return -1;
     	}
     	
     	ContentValues values = new ContentValues();
     	
     	if(!TextUtils.isEmpty(wifiName))
     		values.put("wifi_name", wifiName);
     	if(!TextUtils.isEmpty(wifiPwd))
     		values.put("wifi_pwd", wifiPwd);
     	values.put("modify_time", System.currentTimeMillis());
     	
     	return insert(values, TBL_WIFI);
     }
     public String getWifiPwdByName(String wifiName)
     {
    	 String wifiPwd = "";
      	 Cursor cur = query(TBL_WIFI);
      	 if(cur != null && cur.getCount() > 0)
      	 {
      		while(cur.moveToNext())
      		{
      			String wifi_name = cur.getString(cur.getColumnIndex("wifi_name"));
      			if(wifi_name != null && wifi_name.trim().equals(wifiName.trim()))
      			{
      				wifiPwd = cur.getString(cur.getColumnIndex("wifi_pwd"));
      				break;
      			}
      		}
      	 }
      	if(cur != null )
     		cur.close();
      	 return wifiPwd;
     }
     public synchronized boolean isWifiExist(String wifiName)
     {
     	boolean  result = false;
     	Cursor cur = query(TBL_WIFI);
     	if(cur != null && cur.getCount() > 0)
     	{
     		while(cur.moveToNext())
     		{
     			String wifi_name = cur.getString(cur.getColumnIndex("wifi_name"));
     			if(wifi_name != null && wifi_name.trim().equals(wifiName.trim()))
     			{
     				result = true;
     				//updateMusic(music);
     				break;
     			}
     		}
     	}
     	if(cur != null )
     		cur.close();
     	return result;
     }
     
     public synchronized int updateWifi(String wifiName, String wifiPwd)
     {
     	ContentValues values = new ContentValues();
     	
     	if(!TextUtils.isEmpty(wifiName))
     		values.put("wifi_name", wifiName);
     	if(!TextUtils.isEmpty(wifiPwd))
     		values.put("wifi_pwd", wifiPwd);
     	
     	values.put("modify_time", System.currentTimeMillis());
     	
     	return editWifi(TBL_WIFI, wifiName, values);
     }
     
     
     /************* 鎾斁璁″垝鐨勬搷浣� ************/
     public synchronized void deleteAllPlan()
     {
    	 deleteAll(TBL_CUR_PLAN_LIST);
    	 deleteAll(TBL_CUR_PLAN_MUSIC);
     }
     public synchronized void setCurPlanSub(List<CurPlanSubInfor> planList)
     {
    	 int len = planList.size();
    	 for(int i=0;i<len;i++)
    	 {
    		 CurPlanSubInfor tempPlanInfor = planList.get(i);
    		 addCurPlanSub(tempPlanInfor);
    		 List<SongInfor> tempSongList = tempPlanInfor.getSongList();
    		 int count = tempSongList.size();
    		 for(int j=0;j<count;j++)
    		 {
    			 SongInfor tempSongInfor = tempSongList.get(j);
    			 addCurPlanMusic(tempSongInfor, tempPlanInfor.getPlanId());
    		 }
    	 }
     }
     public synchronized long addCurPlanSub(CurPlanSubInfor curPlanSubInfor)
     {
     	ContentValues values = new ContentValues();
     	
     	if(!TextUtils.isEmpty(curPlanSubInfor.getPlanId()))
     		values.put("id", curPlanSubInfor.getPlanId());
     	if(!TextUtils.isEmpty(curPlanSubInfor.getStartTime()))
     		values.put("startTime", curPlanSubInfor.getStartTime());
     	if(!TextUtils.isEmpty(curPlanSubInfor.getEndTime()))
     		values.put("endTime", curPlanSubInfor.getEndTime());
     	
     	values.put("modify_time", System.currentTimeMillis());
     	
     	return insert(values, TBL_CUR_PLAN_LIST);
     }
     public synchronized long addCurPlanMusic(SongInfor songInfor, String planId)
     {
    	 ContentValues values = new ContentValues();
    	 
    	 if(!TextUtils.isEmpty(planId))
    		 values.put("planId", planId);
    	 if(!TextUtils.isEmpty(songInfor.getMediaId()))
    		 values.put("musicId", songInfor.getMediaId());
    	 if(!TextUtils.isEmpty(songInfor.getMediaName()))
    		 values.put("musicName", songInfor.getMediaName());
    	 if(!TextUtils.isEmpty(songInfor.getAlbumName()))
    		 values.put("musicAlbum", songInfor.getAlbumName());
    	 if(!TextUtils.isEmpty(songInfor.getArtist()))
    		 values.put("musicSing", songInfor.getArtist());
    	 if(!TextUtils.isEmpty(songInfor.getMediaUrl()))
    		 values.put("musicUrl", songInfor.getMediaUrl());
    	 
    	 values.put("modify_time", System.currentTimeMillis());
    	 
    	 return insert(values, TBL_CUR_PLAN_MUSIC);
     }
     
     public synchronized List<SongInfor> getSongListByPlanId(String planId, boolean isDownload)
     {
     	List<SongInfor> tempList = new ArrayList<SongInfor>();
     	Cursor cur = queryAsc(TBL_CUR_PLAN_MUSIC);
     	if(cur == null || cur.getCount() == 0)
     		 return tempList;
     	if(cur != null && cur.getCount() > 0)
     	{
	   		 while(cur.moveToNext())
	   		 {
	   			String tempId = cur.getString(cur.getColumnIndex("planId"));
	   			if(tempId.equals(planId))
	   			{
	   				SongInfor tempInfor = new SongInfor();
	   				
	   				
	  				String musicUrl = cur.getString(cur.getColumnIndex("musicUrl"));
	  				
	  				if(isDownload)//涓嬭浇鏃讹紝鏈湴鏈夌殑灏变笉娣诲姞
	  				{
	  					if(!isFileExsit(musicUrl))
	  					{
	  						String musicId = cur.getString(cur.getColumnIndex("musicId"));
	  		  				String musicName = cur.getString(cur.getColumnIndex("musicName"));
	  		  				String musicAlbum = cur.getString(cur.getColumnIndex("musicAlbum"));
	  		  				String musicSing = cur.getString(cur.getColumnIndex("musicSing"));
	  		  				tempInfor.setMediaId(musicId);
	  		  				tempInfor.setMediaName(musicName);
	  		  				tempInfor.setAlbumName(musicAlbum);
	  		  				tempInfor.setArtist(musicSing);
	  		  				tempInfor.setMediaUrl(musicUrl);
	  		  				
	  						tempList.add(tempInfor);
	  					}
	  				}
	  				else //鎾斁鐨勶紝鏈湴娌℃湁鐨勫氨涓嶆坊鍔�
	  				{
	  					if(isFileExsit(musicUrl))
	  					{
	  						String musicId = cur.getString(cur.getColumnIndex("musicId"));
	  		  				String musicName = cur.getString(cur.getColumnIndex("musicName"));
	  		  				String musicAlbum = cur.getString(cur.getColumnIndex("musicAlbum"));
	  		  				String musicSing = cur.getString(cur.getColumnIndex("musicSing"));
	  		  				tempInfor.setMediaId(musicId);
	  		  				tempInfor.setMediaName(musicName);
	  		  				tempInfor.setAlbumName(musicAlbum);
	  		  				tempInfor.setArtist(musicSing);
	  		  				tempInfor.setMediaUrl(musicUrl);
	  		  				
	  						tempList.add(tempInfor);
	  					}
	  					else
	  					{
	  						deleteCurPlanMusic(musicUrl);
	  					}
	  				}
	   			}
	   		 }
     	}
     	if(cur != null )
     		cur.close();
     	return tempList;
     }
     
     private void deleteCurPlanMusic(String music_url)
     {
    	 delete("music_url", music_url, TBL_CUR_PLAN_MUSIC);
     }
     
     public synchronized List<SongInfor> getCurPlanMusics(long curTime)
     {
    	 List<SongInfor> songList = new ArrayList<SongInfor>();
    	 Cursor cur = queryNormal(TBL_CUR_PLAN_LIST);
      	 if(cur == null || cur.getCount() == 0 || curTime == 0)
      		 return null;
      	 String curPlanTime = LocalDataEntity.newInstance(context).getPlayTime();
      	 String curTimeShort = DateUtils.getEndDateFromLong(curTime);
      	 long curTimeShortLong = DateUtils.timeToInt(curTimeShort, ":");
      	 
      	 if(cur != null && cur.getCount() > 0)
      	 {
      		while(cur.moveToNext())
      		{
      			String tempId = cur.getString(cur.getColumnIndex("id"));
      			String startTime = cur.getString(cur.getColumnIndex("startTime"));
      			String endTime = cur.getString(cur.getColumnIndex("endTime"));
      			if(!TextUtils.isEmpty(startTime) && !TextUtils.isEmpty(endTime))
      			{
      				long sLong = DateUtils.timeToInt(startTime, ":");
      				long eLong = DateUtils.timeToInt(endTime, ":");
      				if(isTimeOverlap(sLong, eLong, curTimeShortLong))
      				{
      					if(!curPlanTime.equals(startTime))
      					{
      						songList = getSongListByPlanId(tempId, false);
      						
      						LocalDataEntity.newInstance(context).setPlanTime(startTime);
      					}
      					else//褰撳墠鎾斁鍒楄〃涓庢湰鍦板垪琛ㄤ竴鑷达紝涓嶆洿鏂版暟鎹�
      					{
      						songList = null;
      						//LogFactory.createLog().e("*&&&*&*&*&*&*&*&-->curPlanTime" + curPlanTime + "  startTime-->"+startTime);
      						//showToast("*&&&*&*&*&*&*&*&-->curPlanTime" + curPlanTime + "  startTime-->"+startTime);
      					}
      					break;
      				}
      				else
      				{
      					//songList = new ArrayList<SongInfor>();
      					//LogFactory.createLog().e("*&&&*&*&*&*&*&*&-->sLong" + sLong + "  eLong-->"+eLong + "  curTimeShortLong-->"+curTimeShortLong);
      					//showToast("*&&&*&*&*&*&*&*&-->sLong" + sLong + "  eLong-->"+eLong + "  curTimeShortLong-->"+curTimeShortLong);
      				}
      			}
      			else
      			{
      				//songList = new ArrayList<SongInfor>();
      				//showToast("*&&&*&*&*&*&*&*&-->startTime" + startTime + "  endTime-->"+endTime);
      			}
      		}
      	 }
      	if(cur != null )
     		cur.close();
    	 return songList;
     }
     public synchronized List<SongInfor> getAllPlanMusics()
     {
    	 List<SongInfor> songList = new ArrayList<SongInfor>();
    	 Cursor cur = queryNormal(TBL_CUR_PLAN_LIST);
    	 if(cur == null || cur.getCount() == 0)
    		 return songList;
    	 
    	 if(cur != null && cur.getCount() > 0)
    	 {
    		 while(cur.moveToNext())
    		 {
    			 String tempId = cur.getString(cur.getColumnIndex("id"));
    			 String startTime = cur.getString(cur.getColumnIndex("startTime"));
    			 String endTime = cur.getString(cur.getColumnIndex("endTime"));
    			 if(!TextUtils.isEmpty(startTime) && !TextUtils.isEmpty(endTime))
    			 {
    				 songList.addAll(getSongListByPlanId(tempId, true));
    			 }
    		 }
    	 }
    	 if(cur != null )
    		 cur.close();
    	 return songList;
     }
     
     /**
      * 鑾峰彇鏈湴璁″垝鐨勭涓�涓紑濮嬫椂闂�
      * @return
      */
     public synchronized long getFirstPlanTime()
     {
    	 long time = 0;
    	 Cursor cur = queryNormal(TBL_CUR_PLAN_LIST);
    	 if(cur == null || cur.getCount() == 0)
    		 return time;
    	 
    	 if(cur != null && cur.getCount() > 0)
    	 {
    		 while(cur.moveToLast())
    		 {
    			 String startTime = cur.getString(cur.getColumnIndex("startTime"));
    			 if(!TextUtils.isEmpty(startTime))
    			 {//"yyyy-MM-dd HH:mm:ss"
    				 startTime = "2016-10-1 " + startTime;
    				 //time = DateUtils.timeToInt(startTime, ":") ;
    				 SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    				 try {
						time = formatter.parse(startTime).getTime() + 3 * 60 * 1000;
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    				 break;
    			 }
    			 //String endTime = cur.getString(cur.getColumnIndex("endTime"));
    		 }
    	 }
    	 if(cur != null )
    		 cur.close();
    	 return time;
     }
     public synchronized boolean isLocalHasPlan()
     {
    	 Cursor cur = queryNormal(TBL_CUR_PLAN_LIST);
    	 if(cur == null || cur.getCount() == 0)
    		 return false;
    	 return true;
     }
     
     private boolean isTimeOverlap(long start, long end, long dest)
 	 {
 		if(start > end)//璧峰鏃堕棿澶т簬缁撴潫鏃堕棿锛岄偅涔堢粨鏉熸椂闂村姞24灏忔椂
 		{
 			end = end + 24 * 60 * 60;
 			if(dest > start)//鐩爣鏃堕棿澶т簬璧峰鏃堕棿锛岄偅涔堢洰鏍囨椂闂存病鏈夎繃24
 			{
 				if(dest < end)
 					return true;
 			}
 			else//鐩爣鏃堕棿灏忎簬璧峰鏃堕棿锛岀洰鏍囨椂闂村凡缁忚繃浜�24锛岃鍔犱笂24灏忔椂
 			{
 				dest = dest + 24 * 60 * 60;
 				if(dest < end)
 					return true;
 			}
 		}
 		else
 		{
 			if(dest > start && dest < end)
 				return true;
 		}
 		
 		return false;
 	}
     
    /* private void showToast(final String text)
     {
    	 ViewUtils.sendMessage(handler, 0, text);
     }*/
     
     /*private Handler handler = new Handler()
     {
    	 public void handleMessage(android.os.Message msg) 
    	 {
    		 String infor = (String) msg.obj;
    		 Toast.makeText(context, infor, 0).show();
    	 };
     };*/
     public boolean isFileExsit(String url)
 	{
 		File file = new File(getMusicLocalPath(url));
 		return file.exists();
 	}
     public boolean isFileExsit(SongInfor songInfor)
     {
    	 String url = songInfor.getMediaUrl();
    	 File file = new File(getMusicLocalPath(url));
    	 return file.exists();
     }
 	private String getMusicLocalPath(String url)
 	{
 		String fileName = "";
 		if(url.contains("."))
 			fileName = url.substring(url.lastIndexOf("/") + 1, url.length());
 		String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + fileName;
 		
 		return filePath;
 	}
     
}
 
