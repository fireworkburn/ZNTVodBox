package com.znt.diange.db; 

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.znt.diange.entity.Constant;
import com.znt.diange.entity.LocalDataEntity;
import com.znt.diange.mina.cmd.DeviceInfor;
import com.znt.diange.mina.entity.MediaInfor;
import com.znt.diange.mina.entity.SongInfor;
import com.znt.diange.mina.entity.UserInfor;

/** 
 * @ClassName: DBManager 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-8-14 上午10:25:36  
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
			INSTANCE = new DBManager(c);
		return INSTANCE;
	}
	
     public void deleteAllMusic()
     {
    	 Cursor cur = query(TBL_MUSIC);
      	if(cur != null)
      	{
      		while(cur.moveToNext())
      		{
      			String music_id = cur.getString(cur.getColumnIndex("music_id"));
      			delete("music_id", music_id, TBL_MUSIC);
      		}
      	}
      	cur.close();
     }
     public void deleteLocalMusic()
     {
    	 Cursor cur = query(TBL_MUSIC);
    	 if(cur != null)
    	 {
    		 while(cur.moveToNext())
    		 {
    			 String music_type = cur.getString(cur.getColumnIndex("music_type"));
    			 if(music_type.equals(MediaInfor.MEDIA_TYPE_PHONE))
    				 delete("music_type", music_type, TBL_MUSIC);
    		 }
    	 }
    	 cur.close();
     }
     
     /**
      * 删除音乐
      * @param id
      * @return
      */
     public int deleteMusicByUrl(MediaInfor infor)
     {
    	 return delete("music_url", infor.getMediaUrl(), TBL_MUSIC);
     }
     
     
     
     public long insertDevice(DeviceInfor deviceInfor)
     {
    	/*if(isDeviceExist(deviceInfor))
     	{
     		return updateDevice(deviceInfor);
     	}
    	
     	ContentValues values = new ContentValues();
     	
     	String deviceName = deviceInfor.getName();
     	String deviceId = deviceInfor.getId();
     	String wifiName = deviceInfor.getWifiName();
     	String wifiPwd = deviceInfor.getWifiPwd();
     	if(wifiName.contains(Constant.UUID_TAG))
     		wifiPwd = Constant.WIFI_HOT_PWD;
     	String device_version = deviceInfor.getVersion();
     	
     	values.put("device_name", deviceName);
     	values.put("device_id", deviceId);
     	values.put("wifi_name", wifiName);
     	values.put("wifi_pwd", wifiPwd);
     	values.put("device_version", device_version);
     	values.put("modify_time", System.currentTimeMillis());
 		return insert(values, TBL_DEVICE);*/
    	 return 1;
     }
     public boolean isDeviceExist(DeviceInfor deviceInfor)
     {
    	 boolean  result = false;
     	
     	 Cursor cur = query(TBL_DEVICE);
      	 if(cur != null)
      	 {
      		while(cur.moveToNext())
      		{
      			String device_id = cur.getString(cur.getColumnIndex("device_id"));
      			//String wifi_name = cur.getString(cur.getColumnIndex("wifi_name"));
      			if(device_id != null && device_id.equals(deviceInfor.getId()))
      			{
      				result = true;
      				break;
      			}
      		}
      	 }
      	cur.close();
     	 return result;
     }
     public int updateDevice(DeviceInfor deviceInfor)
     {
    	 ContentValues values = new ContentValues();
      	
      	 String deviceName = deviceInfor.getName();
      	 String deviceId = deviceInfor.getId();
      	 String wifiName = deviceInfor.getWifiName();
      	 String wifiPwd = deviceInfor.getWifiPwd();
      	 String device_version = deviceInfor.getVersion();
      	
      	 if(!TextUtils.isEmpty(deviceName))
      		 values.put("device_name", deviceName);
      	 if(!TextUtils.isEmpty(deviceId))
      		values.put("device_id", deviceId);
      	 if(!TextUtils.isEmpty(wifiName))
      		 values.put("wifi_name", wifiName);
      	 if(!TextUtils.isEmpty(wifiPwd))
      		values.put("wifi_pwd", wifiPwd);
      	if(!TextUtils.isEmpty(device_version))
      		values.put("device_version", "device_version");
      	 values.put("modify_time", System.currentTimeMillis());
  		 return edit(TBL_DEVICE, "device_id", deviceInfor.getId(), values);
     }
     public int updateDeviceName(DeviceInfor deviceInfor)
     {
    	 ContentValues values = new ContentValues();
    	 String deviceName = deviceInfor.getName();
    	 if(!TextUtils.isEmpty(deviceName))
    		 values.put("device_name", deviceName);
    	 values.put("modify_time", System.currentTimeMillis());
    	 return edit(TBL_DEVICE, "device_id", deviceInfor.getId(), values);
     }
     public int updateDeviceVersion(String device_version)
     {
    	 String deviceId = LocalDataEntity.newInstance(context).getDeviceId();
    	 ContentValues values = new ContentValues();
    	 if(!TextUtils.isEmpty(device_version))
    		 values.put("device_version", device_version);
    	 values.put("modify_time", System.currentTimeMillis());
    	 return edit(TBL_DEVICE, "device_id", deviceId, values);
     }
     public List<DeviceInfor> getDeviceList()
     {
    	 List<DeviceInfor> tempList = new ArrayList<DeviceInfor>();
      	 Cursor cur = query(TBL_DEVICE);
      	 if(cur.getCount() <= 0)
  		 {
  			cur.close();
  			return tempList;
  		 }
      	 if(cur != null)
      	 {
      		while(cur.moveToNext())
      		{
      			DeviceInfor tempInfor = new DeviceInfor();
  				String device_name = cur.getString(cur.getColumnIndex("device_name"));
  				String device_id = cur.getString(cur.getColumnIndex("device_id"));
  				String wifi_name = cur.getString(cur.getColumnIndex("wifi_name"));
  				String wifi_pwd = cur.getString(cur.getColumnIndex("wifi_pwd"));
  				String is_admin = cur.getString(cur.getColumnIndex("is_admin"));
  				String device_version = cur.getString(cur.getColumnIndex("device_version"));
  				int modify_time = cur.getInt(cur.getColumnIndex("modify_time"));
  				
  				tempInfor.setName(device_name);
  				tempInfor.setId(device_id);
  				tempInfor.setWifiName(wifi_name);
  				tempInfor.setWifiPwd(wifi_pwd);
  				tempInfor.setVersion(device_version);
  				
      			tempList.add(0, tempInfor);
      		}
      	 }
      	cur.close();
      	
      	 return tempList;
     }
     public int deleteDevice(DeviceInfor deviceInfor)
     {
    	 return delete("device_id", deviceInfor.getId(), TBL_DEVICE);
     }
     
     
     /**
      * @Description: 插入音乐
      * @param infor   
      * @return void 
      * @throws
       */
      public synchronized long insertSong(SongInfor infor)
      {
      	if(infor == null)
      		return -1;
      	
      	if(isSongExist(infor))
      	{
      		updateSong(infor);
      		return -1;
      	}
      	
      	ContentValues values = new ContentValues();
      	
      	String musicName = infor.getMediaName();
      	String music_id = infor.getMediaId();
      	String artist = infor.getArtist();
      	/*if(TextUtils.isEmpty(artist) 
      			&& infor.getMediaType() == SongInfor.MEDIA_TYPE_LOCAL)//本地音乐分离歌手和歌曲名
      	{
      		if(musicName.contains("-"))
      			artist = musicName.substring(0, musicName.indexOf("-"));
      	}*/
      		
      	if(!TextUtils.isEmpty(musicName))
      		values.put("music_name", musicName);
      	if(!TextUtils.isEmpty(music_id))
      		values.put("music_id", music_id);
      	if(!TextUtils.isEmpty(infor.getMediaUrl()))
      		values.put("music_url", infor.getMediaUrl());
      	if(!TextUtils.isEmpty(infor.getAlbumName()))
      		values.put("music_album", infor.getAlbumName());
      	if(!TextUtils.isEmpty(infor.getAlbumUrl()))
      		values.put("music_album_url", infor.getAlbumUrl());
      	if(!TextUtils.isEmpty(artist))
      		values.put("music_artist", artist);
      	if(!TextUtils.isEmpty(infor.getMediaType()))
      		values.put("music_type", infor.getMediaType());
      	values.put("resource_type", infor.getResourceType());
      	
      	values.put("play_state", infor.getPlayState());
      	values.put("play_time", System.currentTimeMillis() + "");
      	values.put("music_coin", infor.getCoin());
      	if(infor.getUserInfor() != null)
      	{
      		values.put("user_id", infor.getUserInfor().getUserId());
          	values.put("user_name", infor.getUserInfor().getUserName());
          	values.put("play_message", infor.getPlayMsg());
      	}
      	
  		return insert(values, TBL_SONG_LIST);
      }
      
      /**
       * @Description: 判断音乐是否存在
       * @param @param music
       * @param @return   
       * @return boolean 
       * @throws
        */
       public synchronized boolean isSongExist(SongInfor music)
       {
	       	boolean  result = false;
	       	
	       	Cursor cur = query(TBL_SONG_LIST);
        	if(cur != null)
        	{
        		while(cur.moveToNext())
        		{
        			String music_url = cur.getString(cur.getColumnIndex("music_url"));
        			if(music_url != null && music_url.equals(music.getMediaUrl()))
        			{
        				result = true;
        				break;
        			}
        		}
        	}
	       	cur.close();
	       	return result;
       }
      
      /**
       * 更新音乐信息
       * @param infor
       * @return
       */
      public synchronized int updateSong(SongInfor infor)
      {
      	ContentValues values = new ContentValues();
      	
      	String musicName = infor.getMediaName();
      	String music_id = infor.getMediaId();
      	String artist = infor.getArtist();
      	if(!TextUtils.isEmpty(musicName))
      		values.put("music_name", musicName);
      	if(!TextUtils.isEmpty(music_id))
      		values.put("music_id", music_id);
      	if(!TextUtils.isEmpty(infor.getMediaUrl()))
      		values.put("music_url", infor.getMediaUrl());
      	if(!TextUtils.isEmpty(infor.getAlbumName()))
      		values.put("music_album", infor.getAlbumName());
      	if(!TextUtils.isEmpty(infor.getAlbumUrl()))
      		values.put("music_album_url", infor.getAlbumUrl());
      	if(!TextUtils.isEmpty(artist))
      		values.put("music_artist", artist);
      	if(!TextUtils.isEmpty(infor.getMediaType()))
      		values.put("music_type", infor.getMediaType());
      	values.put("resource_type", infor.getResourceType());
      	
      	values.put("play_state", infor.getPlayState());
      	//values.put("play_time", System.currentTimeMillis());
      	values.put("music_coin", infor.getCoin());
      	values.put("play_message", infor.getPlayMsg());
      	//values.put("user_id", infor.getUserId());
      	
      	return edit(TBL_SONG_LIST, infor.getUserInfor().getUserId(), "0", values);
      }
      
     /**
      * @Description: 获取音乐列表
      * @param @return   
      * @return List<GoodsInfor> 
      * @throws
       */
      public synchronized List<SongInfor> getSongList(int pageNum, int pageSize)
      {
      	List<SongInfor> tempList = new ArrayList<SongInfor>();
      	Cursor cur = query(TBL_SONG_LIST);
      	if(cur.getCount() == 0)
      	{
      		cur.close();
      		return tempList;
      	}
      	if(cur != null)
      	{
      		cur.moveToPosition(pageNum * pageSize);
      		for(int i = pageNum * pageSize;i < (pageNum + 1) * pageSize;i++)
      		{
      			if(cur.moveToPosition(i))
      			{
      				SongInfor tempInfor = new SongInfor();
      				String music_name = cur.getString(cur.getColumnIndex("music_name"));
      				String music_id = cur.getString(cur.getColumnIndex("music_id"));
      				String music_artist = cur.getString(cur.getColumnIndex("music_artist"));
      				String music_album = cur.getString(cur.getColumnIndex("music_album"));
      				String music_album_url = cur.getString(cur.getColumnIndex("music_album_url"));
      				int play_state = cur.getInt(cur.getColumnIndex("play_state"));
      				String play_time = cur.getString(cur.getColumnIndex("play_time"));
      				int music_coin = cur.getInt(cur.getColumnIndex("music_coin"));
      				String user_id = cur.getString(cur.getColumnIndex("user_id"));
      				String user_name = cur.getString(cur.getColumnIndex("user_name"));
      				String play_message = cur.getString(cur.getColumnIndex("play_message"));
      				//String music_lyric = cur.getString(cur.getColumnIndex("music_lyric"));
      				String music_url = cur.getString(cur.getColumnIndex("music_url"));
      				String music_type = cur.getString(cur.getColumnIndex("music_type"));
      				int resourceType = cur.getInt(cur.getColumnIndex("resource_type"));
      				
      				/*if(music_type.equals(MediaInfor.MEDIA_TYPE_PHONE))
      				{
      					File file = new File(music_url);
      					if(!file.exists())//手机本地的歌曲不存在就不显示
      						continue;
      				}*/
      				
      				tempInfor.setMediaName(music_name);
      				tempInfor.setMediaId(music_id);
      				tempInfor.setArtist(music_artist);
      				tempInfor.setAlbumName(music_album);
      				tempInfor.setAlbumUrl(music_album_url);
      				tempInfor.setPlayState(play_state);
      				tempInfor.setPlayTime(play_time);
      				tempInfor.setCoin(music_coin);
      				tempInfor.setMediaType(music_type);
      				tempInfor.setResourceType(resourceType);
      				
      				UserInfor userInfor = new UserInfor();
      				userInfor.setUserId(user_id);
      				userInfor.setUserName(user_name);
      				tempInfor.setUserInfor(userInfor);
      				tempInfor.setPlayMsg(play_message);
      				tempInfor.setMediaUrl(music_url);
      				
          			tempList.add(0, tempInfor);
      			}
      		}
      	}
      	cur.close();
      	return tempList;
      }
      public synchronized int getSongCount()
      {
     	 Cursor cur = query(TBL_SONG_LIST);
     	 return cur.getCount();
      }
      public synchronized void deleteAllSong()
      {
     	 Cursor cur = query(TBL_SONG_LIST);
       	if(cur != null)
       	{
       		while(cur.moveToNext())
       		{
       			String music_id = cur.getString(cur.getColumnIndex("music_url"));
       			delete("music_url", music_id, TBL_SONG_LIST);
       		}
       	}
       	cur.close();
      }
      public synchronized void deleteLocalSong()
      {
    	  Cursor cur = query(TBL_SONG_LIST);
    	  if(cur != null)
    	  {
    		  while(cur.moveToNext())
    		  {
    			  String music_type = cur.getString(cur.getColumnIndex("music_type"));
    			  if(music_type.equals(MediaInfor.MEDIA_TYPE_PHONE))
    			  {
    				  String music_id = cur.getString(cur.getColumnIndex("music_url"));
        			  delete("music_url", music_id, TBL_SONG_LIST);
    			  }
    		  }
    	  }
    	  cur.close();
      }
      /**
       * 删除音乐
       * @param id
       * @return
       */
      public synchronized int deleteSongByUrl(SongInfor infor)
      {
     	 return delete("music_url", infor.getMediaUrl(), TBL_SONG_LIST);
      }
      public synchronized int deleteSongByUrl(String music_url)
      {
    	  return delete("music_url", music_url, TBL_SONG_LIST);
      }
      
      /************搜索记录*************/
      public int getSearchRecordCount()
      {
     	 Cursor cur = query(TBL_SEARCH_RECORD);
     	 return cur.getCount();
      }
      public List<String> getSearchRecordList()
      {
     	 	List<String> tempList = new ArrayList<String>();
 	      	Cursor cur = query(TBL_SEARCH_RECORD);
 	      	if(cur.getCount() == 0)
 	      		 return tempList;
 	      	if(cur != null)
 	      	{
 	      		while(cur.moveToNext())
 	      		{
 	  				String name = cur.getString(cur.getColumnIndex("name"));
 	  				
 	      			tempList.add(name);
 	      		}
 	      	}
 	      	cur.close();
 	      	return tempList;
      }
      public long setSearchRecord(String key)
      {
     	 if(!isSearchRecordExist(key))
     	 {
     		deleteEndSearchRecord();
     		 
     		 ContentValues values = new ContentValues();
          	 values.put("name", key);
          	 values.put("modify_time", System.currentTimeMillis());
         	 return insert(values, TBL_SEARCH_RECORD);
     	 }
     	 return -1; 
      }
      private void deleteEndSearchRecord()
      {
     	 Cursor cur = query(TBL_SEARCH_RECORD);
       	 if(cur != null)
       	 {
       		if(cur.getCount() >= 5 && cur.moveToLast())
       		{
   				String name = cur.getString(cur.getColumnIndex("name"));
   				delete("name", name, TBL_SEARCH_RECORD);
       		}
       	 }
       	cur.close();
      }
      private boolean isSearchRecordExist(String key)
      {
     	 boolean  result = false;
       	 Cursor cur = query(TBL_SEARCH_RECORD);
    	 if(cur != null)
    	 {
    		while(cur.moveToNext())
    		{
    			String name = cur.getString(cur.getColumnIndex("name"));
    			if(name != null && name.equals(key))
    			{
    				result = true;
    				break;
    			}
    		}
    	 }
    	 cur.close();
       	 return result;
      }
      public void clearSearchRecord()
      {
     	 Cursor cur = query(TBL_SEARCH_RECORD);
    	 if(cur != null)
    	 {
    		while(cur.moveToNext())
    		{
    			String name = cur.getString(cur.getColumnIndex("name"));
    			delete("name", name, TBL_SEARCH_RECORD);
    		}
    	 }
    	 cur.close();
      }
     
}
 
