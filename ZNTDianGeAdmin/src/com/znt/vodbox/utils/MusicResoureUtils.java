
package com.znt.vodbox.utils; 

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.text.TextUtils;

import com.znt.diange.mina.entity.MediaInfor;
import com.znt.vodbox.dlna.mediaserver.util.LogFactory;
import com.znt.vodbox.http.HttpResult;

/** 
 * @ClassName: MusicResoureUtils 
 * @Description: TODO
 * @author yan.yu 
 * @date 2016-3-1 下午3:33:16  
 */
public class MusicResoureUtils
{

	/**
	 * 
	* @Description: 酷我音乐搜索（测试版）
	* @param @param name
	* @param @param pageNum
	* @param @param total
	* @param @return   
	* @return List<MediaInfor> 
	* @throws
	 */
	public List<MediaInfor> searchMusicByKuwo1(String name, int pageNum, int total)
	{
		List<MediaInfor> tempList = new ArrayList<MediaInfor>();
		String url = null;
		try
		{
			url = "http://search.kuwo.cn/r.s?all=" + name + 
					"&ft=music&itemset=web_2013&client=kt&pn=" + pageNum + "&rn=20&rformat=json&encoding=utf8";
			//url = "http://search.kuwo.cn" + "type=1&s='" + name + "'&limit=50&offset=" + pageNum;
			//url = "http://sou.kuwo.cn/ws/NSearch?key=" + URLEncoder.encode(name, "UTF-8") + "&type=music&pn=" + pageNum;
			HttpResult httpResult = connect(url);
			if(httpResult.isSuccess())
			{
				JSONObject jsonObj = (JSONObject) httpResult.getReuslt();
				String t = getInforFromJason(jsonObj, "TOTAL");
				if(!TextUtils.isEmpty(t))
					total = Integer.parseInt(t);
				//String ARTISTPIC = getInforFromJason(jsonObj, "ARTISTPIC");
				String abslist = getInforFromJason(jsonObj, "abslist");
				JSONArray array = new JSONArray(abslist);
				int len = array.length();
				for(int i=0;i<len;i++)
				{
					JSONObject json1 = array.getJSONObject(i);
					String MUSICRID = getInforFromJason(json1, "MUSICRID");
					String SONGNAME = getInforFromJason(json1, "SONGNAME");
					String ARTIST = getInforFromJason(json1, "ARTIST");
					/*String ARTISTID = getInforFromJason(json1, "ARTISTID");
					String ALBUM = getInforFromJason(json1, "ALBUM");
					String ALBUMID = getInforFromJason(json1, "ALBUMID");
					String FORMATS = getInforFromJason(json1, "FORMATS");
					String SCORE100 = getInforFromJason(json1, "SCORE100");
					String NSIG1 = getInforFromJason(json1, "NSIG1");
					String NSIG2 = getInforFromJason(json1, "NSIG2");
					String MP3NSIG1 = getInforFromJason(json1, "MP3NSIG1");
					String MP3NSIG2 = getInforFromJason(json1, "MP3NSIG2");
					String MP3RID = getInforFromJason(json1, "MP3RID");
					String MP3_3536634 = getInforFromJason(json1, "MP3_3536634");
					String MKVNSIG1 = getInforFromJason(json1, "MKVNSIG1");*/
					
					MediaInfor infor = new MediaInfor();
					//infor.setAlbumName(albumName);
					//infor.setAlbumUrl(albumImg);
					infor.setArtist(ARTIST);
					//infor.setMediaCover(artistImg);
					infor.setMediaId(MUSICRID);
					infor.setMediaName(SONGNAME);
					infor.setMediaSize(0);
					infor.setMediaResType("1");
					//infor.setMediaType(MediaT);
					//infor.setMediaUrl(audio);
					
					tempList.add(infor);
					
				}
			}
			else
				return null;
			return tempList;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	* @Description: 酷我音乐搜索
	* @param @param name
	* @param @param pageNum
	* @param @param total
	* @param @return   
	* @return List<MediaInfor> 
	* @throws
	 */
	public List<MediaInfor> searchMusicByKuwo(String name, int pageNum, int total)
    {
		List<MediaInfor> tempList = new ArrayList<MediaInfor>();
		Document doc = null;
        String url = null;
        try
        {
            url = "http://sou.kuwo.cn/ws/NSearch?key=" + URLEncoder.encode(name, "UTF-8") + "&type=music&pn=" + pageNum;
            for(int i=0;i<3;i++)
    		{
    			try 
    			{
    				doc = Jsoup.parse(new URL(url), 5000);
    			} 
    			catch (MalformedURLException e1) 
    			{
    				e1.printStackTrace();
    				doc = null;
    			} 
    			catch (IOException e1) 
    			{
    				e1.printStackTrace();
    				doc = null;
    			}
    			if(doc != null)
    				break;
    			try
    			{
    				Thread.sleep(500);
    			} 
    			catch (InterruptedException e)
    			{
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    		}
            if(doc == null)
            	return null;
            
            if(total == 0)
            {
            	String title = doc.getElementsByClass("m_list").get(0).getElementsByClass("title").get(0).getElementsByTag("h1").text();
            	
            	String regEx="[^0-9]";   
            	Pattern p = Pattern.compile(regEx);   
            	Matcher m = p.matcher(title);   
            	String num = m.replaceAll("").trim();
            	if(!TextUtils.isEmpty(num))
            		total = Integer.parseInt(num);
            }
            
            Elements es = doc.getElementsByClass("m_list").get(0).getElementsByClass("list").get(0).getElementsByTag("ul");
            for (Element e : es) 
            {
            	Elements elements = e.getElementsByTag("li");//分类列表
    			int size = elements.size();
    			for(int i=0;i<size;i++)
    			{
    				String mid = elements.get(i).getElementsByClass("number").get(0).getElementsByTag("input").attr("mid");
    				String m_name = elements.get(i).getElementsByClass("m_name").get(0).getElementsByTag("a").attr("title");
    				String m_url = elements.get(i).getElementsByClass("m_name").get(0).getElementsByTag("a").attr("href");
    				//String m_url = elements.get(i).getElementsByClass("m_name").get(0).getElementsByTag("a").attr("href");//视频地址
    				
    				String a_name = elements.get(i).getElementsByClass("a_name").get(0).getElementsByTag("a").attr("title");
    				String a_url = elements.get(i).getElementsByClass("a_name").get(0).getElementsByTag("a").attr("href");
    				
    				String s_name = elements.get(i).getElementsByClass("s_name").get(0).getElementsByTag("a").attr("title");
    				/*String s_url = elements.get(i).getElementsByClass("s_name").get(0).getElementsByTag("a").attr("href");
    				
    				String listen_name = elements.get(i).getElementsByClass("listen").get(0).getElementsByTag("a").attr("title");
    				String listen_url = elements.get(i).getElementsByClass("listen").get(0).getElementsByTag("a").attr("href");
    				
    				String video_name = elements.get(i).getElementsByClass("video").get(0).getElementsByTag("a").attr("title");
    				String video_url = elements.get(i).getElementsByClass("video").get(0).getElementsByTag("a").attr("href");
    				*/
    				//if(isUrlValid(m_url))
    				{
    					MediaInfor infor = new MediaInfor();
        				infor.setMediaId(mid);
        				infor.setMediaName(m_name);
        				infor.setMediaUrl(m_url);
        				infor.setArtist(s_name);
        				infor.setAlbumName(a_name);
        				infor.setAlbumUrl(a_url);
        				infor.setMediaResType("1");
        				infor.setMediaType(MediaInfor.MEDIA_TYPE_NET);
        				tempList.add(infor);
    				}
    				
    			}
            }
            return tempList;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return null;
    }
	private boolean isUrlValid(String url)
	{
		String temp = "";
		if(url.contains("/"))
		{
			temp = url.substring(url.lastIndexOf("/") + 1, url.length());
			if(temp.contains("."))
				temp = temp.substring(0, temp.lastIndexOf("."));
			if(temp.equals("0"))
				return false;
		}
		return true;
	}
	
	/**
     * 网易音乐搜索API
     * http://s.music.163.com/search/get/
     * 获取方式：GET
     * 参数：
     * src: lofter //可为空
     * type: 1
     * filterDj: true|false //可为空
     * s: //关键词
     * limit: 10 //限制返回结果数
     * offset: 0 //偏移
     * callback: //为空时返回json，反之返回jsonp callback
     */
	public List<MediaInfor> searchMusicByWY(String name, int pageNum, int total)
	{
		List<MediaInfor> tempList = new ArrayList<MediaInfor>();
		String url = null;
		try
		{
			url = "http://s.music.163.com/search/get/?" + "type=1&s='" + name + "'&limit=50&offset=" + pageNum;
			//url = "http://sou.kuwo.cn/ws/NSearch?key=" + URLEncoder.encode(name, "UTF-8") + "&type=music&pn=" + pageNum;
			HttpResult httpResult = connect(url);
			if(httpResult.isSuccess())
			{
				JSONObject jsonObj = (JSONObject) httpResult.getReuslt();
				String result = getInforFromJason(jsonObj, "result");
				JSONObject json = new JSONObject(result);
				String songCount = getInforFromJason(json, "songCount");
				
				if(!TextUtils.isEmpty(songCount))
					total = Integer.parseInt(songCount);
				
				String songs = getInforFromJason(json, "songs");
				JSONArray array = new JSONArray(songs);
				int size = array.length();
				for(int i=0;i<size;i++)
				{
					JSONObject json1 = array.getJSONObject(i);
					String id = getInforFromJason(json1, "id");
					String artists = getInforFromJason(json1, "artists");
					String djProgramId = getInforFromJason(json1, "djProgramId");
					String audio = getInforFromJason(json1, "audio");
					String album = getInforFromJason(json1, "album");
					String mediaName = getInforFromJason(json1, "name");
					
					JSONObject json2 = new JSONObject(album);
					String albumId = getInforFromJason(json2, "id");
					String albumImg = getInforFromJason(json2, "picUrl");
					String artist = getInforFromJason(json2, "artist");
					String albumName = getInforFromJason(json2, "name");
					
					JSONArray array1 = new JSONArray(artists);
					int len = array1.length();
					String artistName = "";
					String artistImg = "";
					for(int j=0;j<len;j++)
					{
						JSONObject json3 = array1.getJSONObject(j);
						String artistId = getInforFromJason(json3, "id");
						if(TextUtils.isEmpty(artistImg))
							artistImg = getInforFromJason(json3, "picUrl");
						artistName += " " + getInforFromJason(json3, "name");
					}
					
					MediaInfor infor = new MediaInfor();
					infor.setAlbumName(albumName);
					infor.setAlbumUrl(albumImg);
					infor.setArtist(artistName);
					infor.setMediaCover(artistImg);
					infor.setMediaId(id);
					infor.setMediaName(mediaName);
					infor.setMediaSize(0);
					//infor.setMediaType(MediaT);
					infor.setMediaUrl(audio);
					
					tempList.add(infor);
					
				}
				/*String artist_offset = getInforFromJason(jsonObj, "artist_offset");
				String total_albums = getInforFromJason(jsonObj, "total_albums");
				String artists = getInforFromJason(jsonObj, "artists");
				String album_offset = getInforFromJason(jsonObj, "album_offset");
				String albums = getInforFromJason(jsonObj, "albums");*/
				
			}
			else
				return null;
			return tempList;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	public List<MediaInfor> searchMusicByYun(String name, int pageNum, int pageSize) throws JSONException{
//		String url = "http://s.music.163.com/search/get/?" + "type=1&s='" + name + "'&limit=50&offset=" + pageNum; 
		String url = "http://music.163.com/api/search/pc"; 	//?s="+name+"&offset="+pageNum+"&limit=20&type=1"
		int total = 0 ;
		int offset = (pageNum-1) * pageSize;
		int limit = pageSize * pageNum;
		try {
			Map<String,String> params = new HashMap<String,String>();
			params.put("s", name);
			params.put("offset", String.valueOf(offset));
			params.put("limit", String.valueOf(limit));
			params.put("type", String.valueOf(1));
			Document doc = Jsoup.connect(url).data(params)
				.userAgent("Mozilla/5.0 (Windows NT 6.1; rv:22.0) Gecko/20100101 Firefox/22.0")
				.cookie("Cookie", "appver=1.5.0.75771;")
				.referrer("http://music.163.com/")
				.header("Content-type", "application/x-www-form-urlencoded")
				.header("Content-length", "")
				.postDataCharset("utf8")
				.ignoreContentType(true)
				.timeout(30000)
				.post();
//			System.out.println(doc.toString());
//			System.out.println(doc.text());
			JSONObject json = new JSONObject(doc.text());
			String code = String.valueOf(json.get("code"));
			System.out.println(code);
			if(code.equals("200"))
			{
				JSONArray songs = json.getJSONObject("result").getJSONArray("songs");
				total = json.getJSONObject("result").getInt("songCount");
				List<MediaInfor> templist = new ArrayList<MediaInfor>();
				for(int i=0;i<songs.length();i++)
				{
					JSONObject song = (JSONObject)songs.get(i);
					String musicUrl = getInforFromJason(song, "mp3Url");
					MediaInfor music = new MediaInfor() ;
					music.setMediaUrl(musicUrl);
					music.setMediaId(getInforFromJason(song, "id"));
					music.setMediaName(getInforFromJason(song, "name"));
					if(song.has("duration"))
						music.setMediaSize(song.getLong("duration"));
					if(song.has("album"))
					{
						JSONObject js = song.getJSONObject("album");
						if(js.has("name"))
							music.setAlbumName(js.getString("name"));
						if(js.has("id"))
							music.setAlbumUrl(js.getString("id"));
						if(js.has("picUrl"))
							music.setMediaCover(js.getString("picUrl"));
					}
					
					if(song.has("artists"))
					{
						JSONArray jsArray = song.getJSONArray("artists");
						int tempSize = jsArray.length();
						String singName = "";
						for(int j=0;j<tempSize;j++)
						{
							JSONObject artist = (JSONObject) jsArray.get(j);
							if(artist.has("name"))
								singName += artist.getString("name") + " ";
						}
						music.setArtist(singName);
					}
					
					music.setMediaResType("2");
					music.setAvailable(isUrlValid(musicUrl));
					//System.out.println(music.getMediaName()+"--"+music.getAlbumName()+"--"+total);
					templist.add(music);
				}
				
				return templist;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		 return null;
	}
	
	public List<MediaInfor> searchMusicDuomi(String name, int pageNum, int total)
	{
		List<MediaInfor> tempList = new ArrayList<MediaInfor>();
		Document doc = null;
		String url = null;
		try
		{
			url = "http://v5.pc.duomi.com/search-ajaxsearch-searchall?kw=" + name + "&pi=" +pageNum + "&pz=10";
			//url = "http://sou.kuwo.cn/ws/NSearch?key=" + URLEncoder.encode(name, "UTF-8") + "&type=music&pn=" + pageNum;
			HttpResult httpResult = connect(url);
			if(httpResult.isSuccess())
			{
				JSONObject jsonObj = (JSONObject) httpResult.getReuslt();
				String track_offset = getInforFromJason(jsonObj, "track_offset");
				String artist_offset = getInforFromJason(jsonObj, "artist_offset");
				String total_albums = getInforFromJason(jsonObj, "total_albums");
				String artists = getInforFromJason(jsonObj, "artists");
				String album_offset = getInforFromJason(jsonObj, "album_offset");
				String albums = getInforFromJason(jsonObj, "albums");
				
			}
			else
				return null;
			return tempList;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	public List<MediaInfor> searchMusicBaidu(String name, int pageNum, int total)
	{
		List<MediaInfor> tempList = new ArrayList<MediaInfor>();
		String url = null;
		try
		{
			url = "http://sug.music.baidu.com/info/suggestion?format=json&version=2&from=0&word=" + name + "&_=1405404358299";
			HttpResult httpResult = connect(url);
			if(httpResult.isSuccess())
			{
				JSONObject jsonObj = (JSONObject) httpResult.getReuslt();
				String data = getInforFromJason(jsonObj, "data");
				JSONObject jsonObj1 = new JSONObject(data);
				String song = getInforFromJason(jsonObj1, "song");
				
				String artistImg = "";
				String albumName = "";
				String albumImg = "";
				
				String album = getInforFromJason(jsonObj1, "album");
				JSONArray jsonArray = new JSONArray(album);
				int len = jsonArray.length();
				for(int i=0;i<len;i++)
				{
					JSONObject json = jsonArray.getJSONObject(i);
					String albumname = getInforFromJason(json, "albumname");
					String artistpic = getInforFromJason(json, "artistpic");
					String bitrate_fee = getInforFromJason(json, "bitrate_fee");
					String albumid = getInforFromJason(json, "albumid");
					String artistname = getInforFromJason(json, "artistname");
					
					if(TextUtils.isEmpty(artistImg))
						artistImg = artistpic;
					if(TextUtils.isEmpty(albumName))
						albumName = albumname;
					if(TextUtils.isEmpty(albumImg))
						albumImg = artistpic;
				}
				
				JSONArray array = new JSONArray(song);
				int size = array.length();
				for(int i=0;i<size;i++)
				{
					JSONObject json = array.getJSONObject(i);
					String bitrate_fee = getInforFromJason(json, "bitrate_fee");
					String yyr_artist = getInforFromJason(json, "yyr_artist");
					String songname = getInforFromJason(json, "songname");
					String artistname = getInforFromJason(json, "artistname");
					String songid = getInforFromJason(json, "songid");
					String has_mv = getInforFromJason(json, "has_mv");
					String encrypted_songid = getInforFromJason(json, "encrypted_songid");
					
					String mediaUrl = "http://ting.baidu.com/data/music/links?songIds=" + songid + "&format=json";
					
					MediaInfor infor = new MediaInfor();
					infor.setAlbumName(albumName);
					infor.setAlbumUrl(albumImg);
					infor.setArtist(artistname);
					infor.setMediaCover(artistImg);
					infor.setMediaId(songid);
					infor.setMediaName(songname);
					//infor.setMediaType(MediaT);
					infor.setMediaUrl(mediaUrl);
					
					tempList.add(infor);
				}
				
				
			}
			else
				return null;
			return tempList;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	* @Description: QQ音乐搜索
	* @param @param name
	* @param @param pageNum
	* @param @param total
	* @param @return   
	* @return List<MediaInfor> 
	* @throws
	 */
	public List<MediaInfor> searchMusicByQQ(String name, int pageNum, int total)
	{
		List<MediaInfor> tempList = new ArrayList<MediaInfor>();
		String url = null;
		try
		{
			url = "http://s.music.qq.com/fcgi-bin/music_search_new_platform?t=0&n=20&aggr=1&cr=1&loginUin=0&format=json&inCharset=GB2312&outCharset=utf-8&notice=0&platform=jqminiframe.json&needNewCode=0&p="
					+ pageNum +"&catZhida=0&remoteplace=sizer.newclient.next_song&w=" + name;
			HttpResult httpResult = connect(url);
			if(httpResult.isSuccess())
			{
				JSONObject jsonObj = (JSONObject) httpResult.getReuslt();
				String data = getInforFromJason(jsonObj, "data");
				JSONObject json1 = new JSONObject(data);
				String semantic = getInforFromJason(json1, "semantic");
				JSONObject json2 = new JSONObject(semantic);
				String totalnum = getInforFromJason(json2, "totalnum");
				String curpage = getInforFromJason(json2, "curpage");
				String list = getInforFromJason(json2, "list");
				JSONArray array = new JSONArray(list);
				int len = array.length();
				for(int i=0;i<len;i++)
				{
					JSONObject json3 = array.getJSONObject(i);
					String pubTime = getInforFromJason(json3, "pubTime");
					String nt = getInforFromJason(json3, "nt");
					String singerName_hilight = getInforFromJason(json3, "singerName_hilight");
					String tag = getInforFromJason(json3, "tag");
					String isupload = getInforFromJason(json3, "isupload");
					String songName_hilight = getInforFromJason(json3, "songName_hilight");
					String singerid = getInforFromJason(json3, "singerid");
					String chinesesinger = getInforFromJason(json3, "chinesesinger");
					String ver = getInforFromJason(json3, "ver");
					String fsinger2 = getInforFromJason(json3, "fsinger2");
					String lyric_hilight = getInforFromJason(json3, "lyric_hilight");
					String docid = getInforFromJason(json3, "docid");
					String albumName_hilight = getInforFromJason(json3, "albumName_hilight");
					String singerMID = getInforFromJason(json3, "singerMID");
					String only = getInforFromJason(json3, "only");
					String f = getInforFromJason(json3, "f");
					String singerid2 = getInforFromJason(json3, "singerid2");
					String fiurl = getInforFromJason(json3, "fiurl");
					String mv = getInforFromJason(json3, "mv");
					String singerMID2 = getInforFromJason(json3, "singerMID2");
					String singerName2_hilight = getInforFromJason(json3, "singerName2_hilight");
					String pure = getInforFromJason(json3, "pure");
					String lyric = getInforFromJason(json3, "lyric");
					String fsinger = getInforFromJason(json3, "fsinger");
					String fsong = getInforFromJason(json3, "fsong");
					
					MediaInfor infor = new MediaInfor();
					//infor.setAlbumName(albumName);
					//infor.setAlbumUrl(albumImg);
					infor.setArtist(singerName_hilight);
					//infor.setMediaCover(artistImg);
					//infor.setMediaId(MUSICRID);
					infor.setMediaName(songName_hilight);
					infor.setMediaSize(0);
					//infor.setMediaType(MediaT);
					//infor.setMediaUrl(audio);
					
					tempList.add(infor);
				}
			}
			else
				return null;
			return tempList;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	private String getInforFromJason(JSONObject json, String key)
	{
		if(json == null || key == null)
			return "";
		if(json.has(key))
		{
			try
			{
				String result = json.getString(key);
				if(result.equals("null"))
					result = "";
				return result;
				//return StringUtils.decodeStr(result);
			} 
			catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "";
	}
	
	protected int HTTP_CONN_TIMEOUT = 3000;
	protected int HTTP_SOCKET_TIMEOUT = 5000;
	/**
	* @Description: post方式访问
	* @param @param params
	* @param @return   
	* @return JSONObject 
	* @throws
	 */
	protected HttpResult connect(String url)
	{
		
		HttpResult httpResult = new HttpResult();
		if(url.contains(" "))
			url = url.replace(" ", "");
		HttpPost httpRequest = new HttpPost(url);
		try
		{
			HttpParams httpParameters = new BasicHttpParams();
	        HttpConnectionParams.setConnectionTimeout(httpParameters, HTTP_CONN_TIMEOUT);
	        HttpConnectionParams.setSoTimeout(httpParameters, HTTP_SOCKET_TIMEOUT);
	        HttpResponse httpResponse = new DefaultHttpClient(httpParameters).execute(httpRequest);
	        
	        httpResult.setResult(false, null);
	        
	        if (httpResponse.getStatusLine().getStatusCode() == 200)
	        {
	            String strResult = EntityUtils.toString(httpResponse.getEntity());
	            httpResult.setResult(true, new JSONObject(strResult));
	        } 
	        else
	        {
	        	 httpResult.setResult(false, httpResponse.getStatusLine().toString());
	        	 LogFactory.createLog().e("network error: "+ httpResponse.getStatusLine().toString());
	        }
	      
		} 
		catch (UnsupportedEncodingException e)
		{
			httpResult.setResult(false, e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (ClientProtocolException e)
		{
			httpResult.setResult(false, e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e)
		{
			httpResult.setResult(false, e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (JSONException e)
		{
			httpResult.setResult(false, e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return httpResult;
	}
}
 
