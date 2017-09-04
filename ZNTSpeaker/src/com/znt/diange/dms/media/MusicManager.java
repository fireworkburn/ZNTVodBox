
package com.znt.diange.dms.media; 

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.text.TextUtils;

import com.znt.diange.dms.media.MediaStoreCenter.SourceType;
import com.znt.diange.mina.entity.MediaInfor;
import com.znt.diange.mina.entity.SongInfor;

/** 
 * @ClassName: MusicManager 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-10-14 下午1:46:10  
 */
public class MusicManager
{
	private Map<String, File> musicList = new HashMap<String, File>();
	private Map<String, String> dirList = new HashMap<String, String>();
	private Map<String, Integer> childSizeList = new HashMap<String, Integer>();
	private List<SongInfor> songList = new ArrayList<SongInfor>();
	private static MusicManager INSTANCE = null;
	
	
	public MusicManager()
	{
		
	}
	
	public static MusicManager getInstance()
	{
		if(INSTANCE == null)
			INSTANCE = new MusicManager();
		return INSTANCE;
	}
	
	public Map<String, File> getMusicList()
	{
		return musicList;
	}
	public Map<String, String> getDirList()
	{
		return dirList;
	}
	public List<SongInfor> getSongList()
	{
		return songList;
	}
	
	public int getDirTotal()
	{
		return dirList.size();
	}
	public int getMusicTotal()
	{
		return musicList.size();
	}
	
	public void addMusic(File file, SourceType sourceType)
	{
		/*String key = file.getAbsolutePath();
		if(!musicList.containsKey(key))
		{
			musicList.put(key, file);
			addFileToSongList(file);
			
			if(!dirList.containsKey(key))
			{
				String[] subStrs = key.split("/");
				int tempSize = subStrs.length;
				if(tempSize > 1)
				{
					String dir = subStrs[tempSize  - 2];;
					key = file.getParent();
					dirList.put(key, dir);
				}
			}
			
			if(childSizeList.containsKey(key))
			{
				int count = childSizeList.get(key);
				count = count + 1;
				childSizeList.put(key, count);
			}
			else
				childSizeList.put(key, 1);
		}*/
		
		//if(sourceType == SourceType.Speaker)
		/*{
			if(!dirList.containsKey(key))
			{
				String[] subStrs = key.split("/");
				int tempSize = subStrs.length;
				if(tempSize > 1)
				{
					String dir = subStrs[tempSize  - 2];;
					key = file.getParent();
					dirList.put(key, dir);
				}
			}
		}*/
	}
	
	private void addFileToSongList(File f)
	{
		SongInfor tempInfor = new SongInfor();
		tempInfor.setMediaUrl(f.getAbsolutePath());
		String tempName = f.getName();
		if(tempName.contains("."))
			tempName = tempName.substring(0, tempName.lastIndexOf("."));
		tempInfor.setMediaName(tempName);
		tempInfor.setMediaType(MediaInfor.MEDIA_TYPE_SPEAKER);
		songList.add(tempInfor);
	}
	
	public List<MediaInfor> getMusicInforDir()
	{
		List<MediaInfor> dirs = new ArrayList<MediaInfor>();
		
    	if(dirList.size() == 0)
    		return dirs;
    	if(dirs.size() > 0)
    		dirs.clear();
    	
    	Iterator iterator = dirList.keySet().iterator(); 
    	while(iterator.hasNext())
    	{
    		String key = (String) iterator.next();
    		String name = dirList.get(key);
    		MediaInfor musicInfor = new MediaInfor();
    		musicInfor.setMediaName(name);
    		musicInfor.setMediaUrl(key);
    		musicInfor.setDir(true);
    		if(childSizeList.containsKey(key))
    		{
    			int childCount = childSizeList.get(key);
    			musicInfor.setchildCount(childCount + "");
    		}
    		
    		dirs.add(musicInfor);
    	}
		
		return dirs;
	}
	
	public List<MediaInfor> getMusicInforList(String key)
	{
		List<MediaInfor> tempList = new ArrayList<MediaInfor>();
		
    	if(musicList.size() == 0 || TextUtils.isEmpty(key))
    		return tempList;
    	if(tempList.size() > 0)
    		tempList.clear();
    	
    	Iterator iterator = musicList.keySet().iterator(); 
    	while(iterator.hasNext())
    	{
    		File f = musicList.get(iterator.next());
    		if(f.getParent().equals(key))
    		{
    			MediaInfor tempInfor = new MediaInfor();
    			tempInfor.setMediaUrl(f.getAbsolutePath());
        		String tempName = f.getName();
        		if(tempName.contains("."))
        			tempName = tempName.substring(0, tempName.lastIndexOf("."));
        		String artist = "";
        		if(tempName.contains("-"))
        		{
        			artist = tempName.substring(0, tempName.indexOf("-"));
        		}
        		tempInfor.setArtist(artist);
        		tempInfor.setMediaName(tempName);
        		tempInfor.setDir(false);
        		tempList.add(tempInfor);
    		}
    	}
		
		return tempList;
	}
	
    /**
     * 根据接收到的音乐路径获取需要点播的歌曲信息
     * @param musicUrl
     * @return
     */
	public SongInfor getLocalSongInfor(String musicUrl)
    {
    	SongInfor tempSongInfor = new SongInfor();
    	
    	Map<String, File> list = MusicManager.getInstance().getMusicList();
    	File f = list.get(musicUrl);
    	tempSongInfor.setMediaUrl(musicUrl);
		String tempName = f.getName();
		if(tempName.contains("."))
			tempName = tempName.substring(0, tempName.lastIndexOf("."));
		tempSongInfor.setMediaName(tempName);
    	
    	return tempSongInfor;
    }
	
	public void clearMusic()
	{
		musicList.clear();
		dirList.clear();
		songList.clear();
		childSizeList.clear();
	}
}
 
