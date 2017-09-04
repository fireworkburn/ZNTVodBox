package com.znt.speaker.http; 

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.znt.diange.mina.entity.SongInfor;
import com.znt.diange.mina.entity.UpdateInfor;
import com.znt.speaker.db.DBManager;
import com.znt.speaker.entity.CurPlanInfor;
import com.znt.speaker.entity.CurPlanSubInfor;
import com.znt.speaker.entity.DeviceStatusInfor;
import com.znt.speaker.entity.LocalDataEntity;
import com.znt.speaker.util.LogFactory;
import com.znt.speaker.util.StringUtils;
import com.znt.speaker.util.UrlUtils;

/** 
 * @ClassName: HttpManager 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-7-31 下午5:36:02  
 */
public class HttpManager extends HttpAPI
{
	protected boolean isStop = false;
	
	protected int HTTP_CONN_TIMEOUT = 15*1000;
	protected int HTTP_SOCKET_TIMEOUT = 15*1000;
	
	private String RESULT_INFO = "info";
	private String RESULT_OK = "result";
	private Context context = null;
	public void setActivity(Context context)
	{
		this.context = context;
	}
	/**
	 * 检测升级
	 * @param params
	 * @return
	 */
	protected HttpResult checkUpdate(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(CHECK_UPDATE, params);
		
		 if(httpResult.isSuccess() && !isStop)
         {
        	JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
        	 try
             {
                 int result = jsonObject.getInt(RESULT_OK);
                 if(result == 0)
                 {
                	 JSONObject json = jsonObject.getJSONObject(RESULT_INFO);
                	 UpdateInfor updateInfor = new UpdateInfor();
                	 String versionName = getInforFromJason(json, "version");
                	 String versionNum = getInforFromJason(json, "versionNum");
                	 String apkUrl = getInforFromJason(json, "url");
                	 String updateType  = getInforFromJason(json, "updateType");
                	 updateInfor.setApkUrl(apkUrl);
                	 updateInfor.setUpdateType(updateType);
                	 updateInfor.setVersionNum(versionNum);
                	 updateInfor.setVersionName(versionName);
                	 httpResult.setResult(true, updateInfor);
                 }
                 else
                 {
                	 httpResult.setResult(false, jsonObject.getString(RESULT_INFO));
                 }
             } 
             catch (JSONException e)
             {
            	 httpResult.setResult(false, e.getMessage());
                 // TODO Auto-generated catch block
                 e.printStackTrace();
             }
         }
		 else
			httpResult.setSuccess(false);
		
		 return httpResult;
    }
	
	/**
	 * 注册
	 * @param params
	 * @return
	 */
	protected HttpResult register(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(REGISTER, params);
		
		if(httpResult.isSuccess() && !isStop)
		{
			JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
			try
			{
				int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					String code = getInforFromJason(jsonObject, RESULT_INFO);
					httpResult.setResult(true, code);
				}
				else
				{
					httpResult.setResult(false, jsonObject.getString(RESULT_INFO));
				}
			} 
			catch (JSONException e)
			{
				httpResult.setResult(false, e.getMessage());
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
			httpResult.setSuccess(false);
		
		return httpResult;
	}
	
	/**
	 * 更新音响信息
	 * @param params
	 * @return
	 */
	protected HttpResult updateSpeakerInfor(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(UPDATE_SPEAKER_INFOR, params);
		
		if(httpResult.isSuccess() && !isStop)
		{
			JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
			try
			{
				int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					String code = getInforFromJason(jsonObject, RESULT_INFO);
					httpResult.setResult(true, code);
				}
				else
				{
					httpResult.setResult(false, jsonObject.getString(RESULT_INFO));
				}
			} 
			catch (JSONException e)
			{
				httpResult.setResult(false, e.getMessage());
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
			httpResult.setSuccess(false);
		
		return httpResult;
	}
	
	/**
	 * @Description: 扣除金币
	 * @param @param params
	 * @param @return   
	 * @return HttpResult 
	 * @throws
	 */
	protected HttpResult coinRemove(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(COIN_REMOVE, params);
		
		if(httpResult.isSuccess() && !isStop)
		{
			JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
			try
			{
				int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					httpResult.setResult(true, null);
				}
				else
				{
					httpResult.setResult(false, jsonObject.getString(RESULT_INFO));
				}
			} 
			catch (JSONException e)
			{
				httpResult.setResult(false, e.getMessage());
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
			httpResult.setSuccess(false);
		
		return httpResult;
	}
	
	/**
	 * @Description: 冻结金币取消
	 * @param @param params
	 * @param @return   
	 * @return HttpResult 
	 * @throws
	 */
	protected HttpResult coinFreezeCancel(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(COIN_FREEZE_CANCEL, params);
		
		if(httpResult.isSuccess() && !isStop)
		{
			JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
			try
			{
				int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					httpResult.setResult(true, null);
				}
				else
				{
					httpResult.setResult(false, jsonObject.getString(RESULT_INFO));
				}
			} 
			catch (JSONException e)
			{
				httpResult.setResult(false, e.getMessage());
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
			httpResult.setSuccess(false);
		
		return httpResult;
	}
	
	/**
	 * @Description: 获取播放列表
	 * @param @param params
	 * @param @return   
	 * @return HttpResult 
	 * @throws
	 */
	protected HttpResult getPlayList(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(GET_PLAY_LIST, params);
		
		if(httpResult.isSuccess() && !isStop)
		{
			JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
			try
			{
				int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					String info = getInforFromJason(jsonObject, RESULT_INFO);
					JSONArray jsonArray = new JSONArray(info);
					int size = jsonArray.length();
					List<SongInfor> tempList = new ArrayList<SongInfor>();
					if(size > 0)
					{
						for(int i=0;i<size;i++)
						{
							JSONObject json = jsonArray.getJSONObject(i);
							String terminalId = getInforFromJason(json, "terminalId");
							String musicDuration = getInforFromJason(json, "musicDuration");
							String id = getInforFromJason(json, "id");
							//String status = getInforFromJason(json, "status");
							String musicAuther = getInforFromJason(json, "musicAuther");
							String musicId = getInforFromJason(json, "musicId");
							String musicUrl = getInforFromJason(json, "musicUrl");
							String musicName = getInforFromJason(json, "musicName");
							String musicSing = getInforFromJason(json, "musicSing");
							
							SongInfor tempInfor = new SongInfor();
							if(!TextUtils.isEmpty(musicDuration))
								tempInfor.setMediaDuration(Integer.parseInt(musicDuration));
							String musicInfoId = getInforFromJason(json, "musicInfoId");
							tempInfor.setMediaId(musicInfoId);
							tempInfor.setResId(musicId);
							tempInfor.setMediaName(musicName);
							if(!TextUtils.isEmpty(musicAuther))
								tempInfor.setResourceType(Integer.parseInt(musicAuther));
							tempInfor.setMediaUrl(UrlUtils.decodeUrl(musicUrl));
							tempInfor.setArtist(musicSing);
							tempInfor.setTerminalId(terminalId);
							tempList.add(tempInfor);
						}
					}
					
					httpResult.setResult(true, tempList);
				}
				else
				{
					httpResult.setResult(false, jsonObject.getString(RESULT_INFO));
				}
			} 
			catch (JSONException e)
			{
				httpResult.setResult(false, e.getMessage());
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
			httpResult.setSuccess(false);
		
		return httpResult;
	}
	
	/**
	 * @Description:获取设备状态
	 * @param @param 返回值 vodFlag--0-不允许点播 1-点播盒子内置歌曲 2-可点播第三方歌曲
						lastMusicUpdate --曲库更新时间
						lastBootTime --盒子最后启动时间
	 * @param @return   
	 * @return HttpResult 
	 * @throws
	 */
	protected HttpResult getDeviceStatus(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(GET_DEVICE_STATUS, params);
		
		if(httpResult.isSuccess() && !isStop)
		{
			JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
			try
			{
				int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					String info = getInforFromJason(jsonObject, RESULT_INFO);
					JSONObject json = new JSONObject(info);
					DeviceStatusInfor deviceStatusInfor  = new DeviceStatusInfor();
					String vodFlag = getInforFromJason(json, "vodFlag");
					String planId = getInforFromJason(json, "planId");
					String planTime = getInforFromJason(json, "planTime");
					String playStatus   = getInforFromJason(json, "playStatus  ");
					String lastMusicUpdate  = getInforFromJason(json, "lastMusicUpdate");
					String sysLastVersionNum  = getInforFromJason(json, "sysLastVersionNum");
					String pushStatus  = getInforFromJason(json, "pushStatus");
					String volume  = getInforFromJason(json, "volume");
					String downloadFlag  = getInforFromJason(json, "downloadFlag");
					String videoWhirl  = getInforFromJason(json, "videoWhirl");
					//String playingPos  = getInforFromJason(json, "playingPos");
					deviceStatusInfor.setLastVersionNum(sysLastVersionNum);
					deviceStatusInfor.setVodFlag(vodFlag);
					deviceStatusInfor.setMusicLastUpdate(lastMusicUpdate);
					deviceStatusInfor.setPlanId(planId);
					deviceStatusInfor.setPlanTime(planTime);
					deviceStatusInfor.setPlayStatus(playStatus);
					deviceStatusInfor.setPushStatus(pushStatus);
					//deviceStatusInfor.setPlayingPos(playingPos);
					deviceStatusInfor.setVolume(volume);
					deviceStatusInfor.setDownloadFlag(downloadFlag);
					deviceStatusInfor.setVideoWhirl(videoWhirl);
					/*if(!TextUtils.isEmpty(playingPos))
					{
						//int curIndex = LocalDataEntity.newInstance(context).getMusicIndex();
						int serverIndex = Integer.parseInt(playingPos);
						if(curIndex < serverIndex)
							LocalDataEntity.newInstance(context).setMusicIndex(serverIndex);
					}*/
					
					httpResult.setResult(true, deviceStatusInfor);
				}
				else
				{
					httpResult.setResult(false, jsonObject.getString(RESULT_INFO));
				}
			} 
			catch (JSONException e)
			{
				httpResult.setResult(false, e.getMessage());
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
			httpResult.setSuccess(false);
		
		return httpResult;
	}
	
	/**
	 * @Description:上传点播的歌曲记录
	 * @param @param params
	 * @param @return   
	 * @return HttpResult 
	 * @throws
	 */
	protected HttpResult uploadSongRecord(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(UPLOAD_SONG_RECORD, params);
		
		if(httpResult.isSuccess() && !isStop)
		{
			JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
			try
			{
				int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					httpResult.setResult(true, null);
				}
				else
				{
					httpResult.setResult(false, jsonObject.getString(RESULT_INFO));
				}
			} 
			catch (JSONException e)
			{
				httpResult.setResult(false, e.getMessage());
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
			httpResult.setSuccess(false);
		
		return httpResult;
	}
	
	/**
	 * @Description:绑定音响
	 * @param @param params
	 * @param @return   
	 * @return HttpResult 
	 * @throws
	 */
	protected HttpResult bindSpeaker(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(BIND_SPEAKER, params);
		
		if(httpResult.isSuccess() && !isStop)
		{
			JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
			try
			{
				int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					httpResult.setResult(true, null);
				}
				else
				{
					httpResult.setResult(false, jsonObject.getString(RESULT_INFO));
				}
			} 
			catch (JSONException e)
			{
				httpResult.setResult(false, e.getMessage());
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
			httpResult.setSuccess(false);
		
		return httpResult;
	}
	/**
	 * @Description:获取当前时间
	 * @param @param params
	 * @param @return   
	 * @return HttpResult 
	 * @throws
	 */
	protected HttpResult getCurTime(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(GET_CUR_TIME, params);
		
		if(httpResult.isSuccess() && !isStop)
		{
			JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
			try
			{
				int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					String time = getInforFromJason(jsonObject, RESULT_INFO);
					httpResult.setResult(true, time);
				}
				else
				{
					httpResult.setResult(false, jsonObject.getString(RESULT_INFO));
				}
			} 
			catch (JSONException e)
			{
				httpResult.setResult(false, e.getMessage());
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
			httpResult.setSuccess(false);
		
		return httpResult;
	}
	/**
	 * @Description:获取当前时间
	 * @param @param params
	 * @param @return   
	 * @return HttpResult 
	 * @throws
	 */
	protected HttpResult getCurMusicPos(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(GET_CUR_MUSIC_POS, params);
		
		if(httpResult.isSuccess() && !isStop)
		{
			JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
			try
			{
				int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					String index = getInforFromJason(jsonObject, RESULT_INFO);
					JSONObject jsonObj = new JSONObject(index);
					String playingPos = getInforFromJason(jsonObj, "playingPos");
					httpResult.setResult(true, playingPos);
				}
				else
				{
					httpResult.setResult(false, jsonObject.getString(RESULT_INFO));
				}
			} 
			catch (JSONException e)
			{
				httpResult.setResult(false, e.getMessage());
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
			httpResult.setSuccess(false);
		
		return httpResult;
	}
	/**
	 * @Description:获取当前时间
	 * @param @param params
	 * @param @return   
	 * @return HttpResult 
	 * @throws
	 */
	protected HttpResult initTerminal(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(INIT_TERMINAL, params);
		
		if(httpResult.isSuccess() && !isStop)
		{
			JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
			try
			{
				int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					String infor = getInforFromJason(jsonObject, RESULT_INFO);
					JSONObject json1 = new JSONObject(infor);
					
					String systemTime = getInforFromJason(json1, "systemTime");
					String playingPos = getInforFromJason(json1, "playingPos");
					if(!TextUtils.isEmpty(playingPos))
					{
						int pos = Integer.parseInt(playingPos);
						if(pos > 0)
							pos = pos - 1;
						LocalDataEntity.newInstance(context).setMusicIndex(pos);
					}
					
					
					//String playingPos = getInforFromJason(jsonObj, "playingPos");
					httpResult.setResult(true, systemTime);
				}
				else
				{
					httpResult.setResult(false, jsonObject.getString(RESULT_INFO));
				}
			} 
			catch (JSONException e)
			{
				httpResult.setResult(false, e.getMessage());
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
			httpResult.setSuccess(false);
		
		return httpResult;
	}
	/**
	 * @Description:获取推送的歌曲列表
	 * @param @param params
	 * @param @return   
	 * @return HttpResult 
	 * @throws
	 */
	protected HttpResult getPushMusics(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(GET_PUSH_MUSIC, params);
		
		if(httpResult.isSuccess() && !isStop)
		{
			JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
			try
			{
				int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					String info = getInforFromJason(jsonObject, "info");
					JSONArray jasonArray = new JSONArray(info);
					int len = jasonArray.length();
					List<SongInfor> tempList = new ArrayList<SongInfor>();
					for(int i=0;i<len;i++)
					{
						JSONObject json = (JSONObject) jasonArray.get(i);
						String terminalId = getInforFromJason(json, "terminalId");
						String musicSource = getInforFromJason(json, "musicSource");
						String musicDuration = getInforFromJason(json, "musicDuration");
						String id = getInforFromJason(json, "id");
						String status = getInforFromJason(json, "status");
						String musicAuther = getInforFromJason(json, "musicAuther");
						String musicId = getInforFromJason(json, "musicId");
						String musicUrl = getInforFromJason(json, "musicUrl");
						String musicName = getInforFromJason(json, "musicName");
						String musicCategoryId = getInforFromJason(json, "musicCategoryId");
						String musicAlbum = getInforFromJason(json, "musicAlbum");
						String musicSing = getInforFromJason(json, "musicSing");
						String addTime = getInforFromJason(json, "musicAlbum");
						
						SongInfor infor = new SongInfor();
						String musicInfoId = getInforFromJason(json, "musicInfoId");
						infor.setMediaId(musicInfoId);
						infor.setResId(musicId);
						infor.setMediaName(musicName);
						infor.setMediaResType(musicAuther);
						infor.setMediaUrl(UrlUtils.decodeUrl(musicUrl));
						infor.setAlbumName(musicAlbum);
						infor.setArtist(musicSing);
						tempList.add(0,infor);
						//DBManager.newInstance(context).insertSong(infor);
					}
					/*String time = getInforFromJason(jsonObject, RESULT_INFO);
					httpResult.setResult(true, time);
					int songCount = DBManager.newInstance(context).getSongCount();
					List<SongInfor> tempList = DBManager.newInstance(context).getSongList(0, 0);*/
					if(tempList.size() > 0)
					{
						for(int i=0;i<tempList.size();i++)
						{
							DBManager.newInstance(context).insertSong(tempList.get(i));
						}
					}
					httpResult.setResult(true, "获取数据成功");
					
				}
				else
				{
					httpResult.setResult(false, jsonObject.getString(RESULT_INFO));
				}
			} 
			catch (JSONException e)
			{
				httpResult.setResult(false, e.getMessage());
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
			httpResult.setSuccess(false);
		
		return httpResult;
	}
	/**
	 * @Description:获取当前播放计划的歌曲列表
	 * @param @param params
	 * @param @return   
	 * @return HttpResult 
	 * @throws
	 */
	protected HttpResult getPlanMusics(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(GET_PLAN_MUSICS, params);
		
		if(httpResult.isSuccess() && !isStop)
		{
			JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
			try
			{
				int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					String info = getInforFromJason(jsonObject, RESULT_INFO);
					JSONObject jsonObject1 = new JSONObject(info);
					String total = getInforFromJason(jsonObject1, "listSize");
					int totalInt = 0;
					if(!TextUtils.isEmpty(total))
						totalInt = Integer.parseInt(total);
					httpResult.setTotal(totalInt);
					
					String playingPos = getInforFromJason(jsonObject1, "playingPos");
					if(!TextUtils.isEmpty(playingPos))
					{
						int pos = Integer.parseInt(playingPos);
						/*if(pos >= 0)
							pos = pos - 1;*/
						LocalDataEntity.newInstance(context).setMusicIndex(pos);
					}
					String seekPos = getInforFromJason(jsonObject1, "playSeek");
					if(!TextUtils.isEmpty(seekPos))
					{
						int seekPosInt = Integer.parseInt(seekPos);
						if(seekPosInt >= 0)
							LocalDataEntity.newInstance(context).setSeekPos(seekPosInt);
					}
					String lastUpdateTime = getInforFromJason(jsonObject1, "lastUpdateTime");
					if(!TextUtils.isEmpty(lastUpdateTime))
					{
						long lastUpdateTimeInt = Long.parseLong(lastUpdateTime);
						if(lastUpdateTimeInt > 0)
							LocalDataEntity.newInstance(context).setCurLastUpdateTime(lastUpdateTimeInt);
					}
					
					String listInfo = getInforFromJason(jsonObject1, "infoList");
					JSONArray jsonArray = new JSONArray(listInfo);
					int size = jsonArray.length();
					List<SongInfor> tempList = new ArrayList<SongInfor>();
					if(size > 0)
					{
						for(int i=0;i<size;i++)
						{
							JSONObject json = jsonArray.getJSONObject(i);
							String musicAlbum = getInforFromJason(json, "musicAlbum");
							String musicId = getInforFromJason(json, "musicId");
							String musicUrl = getInforFromJason(json, "musicUrl");
							String musicName = getInforFromJason(json, "musicName");
							String musicSing = getInforFromJason(json, "musicSing");
							
							if(!TextUtils.isEmpty(musicUrl))
								musicUrl = UrlUtils.decodeUrl(musicUrl);
							SongInfor tempInfor = new SongInfor();
							String musicInfoId = getInforFromJason(json, "musicInfoId");
							tempInfor.setMediaId(musicInfoId);
							tempInfor.setResId(musicId);
							tempInfor.setMediaName(musicName);
							tempInfor.setMediaUrl(musicUrl);
							tempInfor.setArtist(musicSing);
							tempInfor.setAlbumName(musicAlbum);
							tempList.add(tempInfor);
						}
					}
					
					
					httpResult.setResult(true, tempList);
				}
				else
				{
					httpResult.setResult(false, jsonObject.getString(RESULT_INFO));
				}
			} 
			catch (JSONException e)
			{
				httpResult.setResult(false, e.getMessage());
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
			httpResult.setSuccess(false);
		
		return httpResult;
	}
	/**
	 * @Description:获取当前播放计划的歌曲列表
	 * @param @param params
	 * @param @return   
	 * @return HttpResult 
	 * @throws
	 */
	protected HttpResult getScheduleMusics(List<NameValuePair> params, String planScheId) 
	{
		
		HttpResult httpResult = connect(GET_SCHEDULE_MUSICS, params);
		
		if(httpResult.isSuccess() && !isStop)
		{
			JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
			try
			{
				int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					String info = getInforFromJason(jsonObject, RESULT_INFO);
					JSONObject jsonObject1 = new JSONObject(info);
					String total = getInforFromJason(jsonObject1, "listSize");
					int totalInt = 0;
					if(!TextUtils.isEmpty(total))
						totalInt = Integer.parseInt(total);
					httpResult.setTotal(totalInt);
					String listInfo = getInforFromJason(jsonObject1, "infoList");
					JSONArray jsonArray = new JSONArray(listInfo);
					int size = jsonArray.length();
					List<SongInfor> tempList = new ArrayList<SongInfor>();
					for(int i=0;i<size;i++)
					{
						JSONObject json = jsonArray.getJSONObject(i);
						String musicAlbum = getInforFromJason(json, "musicAlbum");
						String musicId = getInforFromJason(json, "musicId");
						String musicUrl = getInforFromJason(json, "musicUrl");
						String musicName = getInforFromJason(json, "musicName");
						String musicSing = getInforFromJason(json, "musicSing");
						if(!TextUtils.isEmpty(musicUrl))
							musicUrl = UrlUtils.decodeUrl(musicUrl);
						SongInfor tempInfor = new SongInfor();
						String musicInfoId = getInforFromJason(json, "musicInfoId");
						tempInfor.setMediaId(musicInfoId);
						tempInfor.setResId(musicId);
						tempInfor.setMediaName(musicName);
						tempInfor.setMediaUrl(musicUrl);
						tempInfor.setArtist(musicSing);
						tempInfor.setAlbumName(musicAlbum);
						tempList.add(tempInfor);
						
						DBManager.newInstance(context).addCurPlanMusic(tempInfor, planScheId);
						
					}
					
					httpResult.setResult(true, tempList);
				}
				else
				{
					httpResult.setResult(false, jsonObject.getString(RESULT_INFO));
				}
			} 
			catch (JSONException e)
			{
				httpResult.setResult(false, e.getMessage());
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
			httpResult.setSuccess(false);
		
		return httpResult;
	}
	
	protected List<SongInfor> getScheduleMusics(String planScheId)
	{
		int pagSize = 300;
		int pageNo = 1;
		List<SongInfor> songList = new ArrayList<SongInfor>(); 
		while(true)
		{
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("planScheId", planScheId));
			params.add(new BasicNameValuePair("pageSize", pagSize + ""));
			params.add(new BasicNameValuePair("pageNo", pageNo + ""));
			
			HttpResult httpResult = new HttpResult();
			httpResult = getScheduleMusics(params, planScheId);
			if(httpResult != null && httpResult.isSuccess())
			{
				List<SongInfor> tempList = (List<SongInfor>) httpResult.getReuslt();
				songList.addAll(tempList);
				if(tempList.size() < pagSize)
				{
					break;
				}
			}
			else
				break;
			pageNo ++;
		}
		return songList;
	}
	
	/**
	 * 获取当前播放计划
	* @Description: TODO
	* @param @param params
	* @param @return   
	* @return HttpResult 
	* @throws
	 */
	protected HttpResult getCurPlan(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(GET_CUR_PLAN, params);
	
		if(httpResult.isSuccess() && !isStop)
		{
			JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
			try
			{
				int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					CurPlanInfor curPlanInfor = new CurPlanInfor();
					
					String info = jsonObject.getString(RESULT_INFO);
					if(TextUtils.isEmpty(info))
					{
						httpResult.setResult(true, curPlanInfor);
						return httpResult;
					}
					
					DBManager.newInstance(context).deleteAllPlan();
					
					JSONObject json = new JSONObject(info);
					/*String total = getInforFromJason(json, "total");
					if(!TextUtils.isEmpty(total))
						httpResult.setTotal(Integer.parseInt(total));*/
					//String cycleList = getInforFromJason(json, "cycleList");
					String endDate = getInforFromJason(json, "endDate");
					String id = getInforFromJason(json, "id");
					//String memberId = getInforFromJason(json, "memberId");
					//String planFlag = getInforFromJason(json, "planFlag");
					String planName = getInforFromJason(json, "planName");
					//String planType = getInforFromJason(json, "planType");
					//String publishTime = getInforFromJason(json, "publishTime");
					String startDate = getInforFromJason(json, "startDate");
					//String status = getInforFromJason(json, "status");
					//String terminalList = getInforFromJason(json, "terminalList");
					//String tlist = getInforFromJason(json, "tlist");
					
					if(!TextUtils.isEmpty(startDate))
					{
						//long dateLong = Long.parseLong(startDate);
						curPlanInfor.setStartDate(startDate);
						//planInfor.setStartDate(DateUtils.getStringTimeHead(dateLong));
					}
					if(!TextUtils.isEmpty(endDate))
					{
						//long dateLong = Long.parseLong(endDate);
						curPlanInfor.setEndDate(endDate);
						//planInfor.setEndDate(DateUtils.getStringTimeHead(dateLong));
					}
					curPlanInfor.setPlanName(planName);
					curPlanInfor.setPlanId(id);
					
					
					String pslist = getInforFromJason(json, "pslist");
					JSONArray jsonArray = new JSONArray(pslist);
					int len = jsonArray.length();
					for(int i=0;i<len;i++)
					{
						CurPlanSubInfor curSubPlanInfor = new CurPlanSubInfor();
						
						JSONObject json1 = (JSONObject) jsonArray.get(i);
						//String cycleType = getInforFromJason(json1, "cycleType");
						String endTime = getInforFromJason(json1, "endTime");
						String id1 = getInforFromJason(json1, "id");
						//String musicCategoryList = getInforFromJason(json1, "musicCategoryList");
						//String publishId = getInforFromJason(json1, "publishId");
						String startTime = getInforFromJason(json1, "startTime");
						
						if(!TextUtils.isEmpty(startTime) && startTime.contains(":"))
						{
							//int tempS = DateUtils.timeToInt(startTime, ":");
							curSubPlanInfor.setStartTime(startTime);
						}
						if(!TextUtils.isEmpty(endTime) && endTime.contains(":"))
						{
							//int tempE = DateUtils.timeToInt(endTime, ":");
							curSubPlanInfor.setEndTime(endTime);
						}
						/*curSubPlanInfor.setStartTime(startTime);
						curSubPlanInfor.setEndTime(endTime);*/
						curSubPlanInfor.setPlanId(id1);
						DBManager.newInstance(context).addCurPlanSub(curSubPlanInfor);
						
						List<SongInfor> tempList = getScheduleMusics(id1);
						curSubPlanInfor.setSongList(tempList);
						curPlanInfor.addSubPlanInfor(curSubPlanInfor);
						/*String mclist = getInforFromJason(json1, "mclist");
						JSONArray jsonArray1 = new JSONArray(mclist);
						int len1 = jsonArray1.length();
						for(int j=0;j<len1;j++)
						{
							JSONObject json2 = (JSONObject) jsonArray1.get(j);
							String addTime = getInforFromJason(json2, "addTime");
							String description = getInforFromJason(json2, "description");
							String id2 = getInforFromJason(json2, "id");
							String imageUrl = getInforFromJason(json2, "imageUrl");
							String lastUpdateTime = getInforFromJason(json2, "lastUpdateTime");
							String memberId1 = getInforFromJason(json2, "memberId");
							String name = getInforFromJason(json2, "name");
							String ordinal = getInforFromJason(json2, "ordinal");
							String remark = getInforFromJason(json2, "remark");
							String status1 = getInforFromJason(json2, "status");
							String sysUserId = getInforFromJason(json2, "sysUserId");
							String type = getInforFromJason(json2, "type");
						}*/
					}
					httpResult.setResult(true, curPlanInfor);
				}
				else
				{
					httpResult.setResult(false, jsonObject.getString(RESULT_INFO));
				}
			} 
			catch (JSONException e)
			{
				httpResult.setResult(false, e.getMessage());
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
			httpResult.setSuccess(false);
		
		return httpResult;
	}
	
	private String getInforFromJason(JSONObject jsonObject, String key, boolean decode)
    {
        if(jsonObject == null || key == null)
            return null;
        if(jsonObject.has(key))
        {
            if(isStop)
                return "";
            try
            {
                String result = jsonObject.getString(key);
                if(result.equals("null"))
                    result = "";
                return decode ? StringUtils.decodeStr(result): result;
            } 
            catch (JSONException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    }
	
	
	private String getInforFromJason(JSONObject json, String key)
	{
		if(json == null || key == null)
			return "";
		if(json.has(key))
		{
			if(isStop)
				return "";
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
	
	/**
	* @Description: post方式访问
	* @param @param params
	* @param @return   
	* @return JSONObject 
	* @throws
	 */
	protected HttpResult connect(String url, List<NameValuePair> params)
	{
		
		if(url.startsWith("https") || url.startsWith("http"))
			return initSSLAllWithHttpClient(url, params);
		
		HttpResult httpResult = new HttpResult();
		
		if(url.contains(" "))
			url = url.replace(" ", "");
		HttpPost httpRequest = new HttpPost(url);
		try
		{
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpParams httpParameters = new BasicHttpParams();
	        HttpConnectionParams.setConnectionTimeout(httpParameters, HTTP_CONN_TIMEOUT);
	        HttpConnectionParams.setSoTimeout(httpParameters, HTTP_SOCKET_TIMEOUT);
	        HttpResponse httpResponse = new DefaultHttpClient(httpParameters).execute(httpRequest);
	        
	        if(isStop)
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
	
	/** 
     * HttpClient方式实现，支持所有Https免验证方式链接 
     *  
     * @throws ClientProtocolException 
     * @throws IOException 
     */  
    public HttpResult initSSLAllWithHttpClient(String url, List<NameValuePair> params)
    {  
    	HttpResult httpResult = new HttpResult();
        HttpParams param = new BasicHttpParams();  
        HttpConnectionParams.setConnectionTimeout(param, HTTP_CONN_TIMEOUT);  
        HttpConnectionParams.setSoTimeout(param, HTTP_SOCKET_TIMEOUT);  
        HttpConnectionParams.setTcpNoDelay(param, true);  
  
        SchemeRegistry registry = new SchemeRegistry();  
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));  
        registry.register(new Scheme("https", TrustAllSSLSocketFactory.getDefault(), 443));  
        ClientConnectionManager manager = new ThreadSafeClientConnManager(param, registry);  
        DefaultHttpClient client = new DefaultHttpClient(manager, param);  
  
        HttpPost request = new HttpPost(url);  
        //HttpGet request = new HttpGet("https://certs.cac.washington.edu/CAtest/");  
        // HttpGet request = new HttpGet("https://www.alipay.com/");
        try
		{
	        request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
	        HttpResponse response = client.execute(request);  
	        if(isStop)
	        	httpResult.setResult(false, null);
	        
	        if (response.getStatusLine().getStatusCode() == 200)
	        {
	            String strResult = EntityUtils.toString(response.getEntity());
	            httpResult.setResult(true, new JSONObject(strResult));
	        } 
	        else
	        {
	        	 httpResult.setResult(false, response.getStatusLine().toString());
	        	 LogFactory.createLog().e("network error: "+ response.getStatusLine().toString());
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
 
