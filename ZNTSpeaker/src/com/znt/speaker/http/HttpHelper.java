
package com.znt.speaker.http; 

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;

import com.znt.diange.mina.cmd.DeviceInfor;
import com.znt.diange.mina.entity.SongInfor;
import com.znt.speaker.db.DBManager;
import com.znt.speaker.entity.Constant;
import com.znt.speaker.entity.LocalDataEntity;
import com.znt.speaker.util.SystemUtils;
import com.znt.speaker.util.ViewUtils;

/** 
 * @ClassName: HttpHelper 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-7-31 涓嬪崍5:35:32  
 */
public class HttpHelper extends HttpManager implements Runnable
{
	private Context context = null;
	private Handler handler = null;
	private HttpType type = null;
	private List<NameValuePair> params = null;
	private List<List<NameValuePair>> paramsList = null;
	private String fileUrl = null;
	private final int SLEEP_TIME_WHEN_NO_NET = 30*1000;
	
	private final int RETRY_COUNT = 3;
	
	public HttpHelper(Handler handler, Context context)
	{
		this.handler = handler;
		this.context = context;
		setActivity(context);
	}
	
	public void stop()
	{
		isStop = true;
	}
	public void startHttp(HttpType type, List<NameValuePair> params)
    {
        isStop = false;
        this.type = type;
        this.params = params;
        new Thread(this).start();
    }
	public void startHttps(HttpType type, List<List<NameValuePair>> paramsList)
	{
		isStop = false;
		this.type = type;
		this.paramsList = paramsList;
		new Thread(this).start();
	}
	/*public void startHttp(HttpType type, MyMultipartEntity mpEntity, String fileUrl)
	{
		isStop = false;
		this.type = type;
		this.mpEntity = mpEntity;
		this.fileUrl = fileUrl;
		new Thread(this).start();
	}*/
	private int getCurrentVolume()
	{
		AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE); 
		//int max = mAudioManager.getStreamMaxVolume( AudioManager.STREAM_MUSIC ); 
		return mAudioManager.getStreamVolume( AudioManager.STREAM_MUSIC ); 
	}
	/**
	*callbacks
	*/
	@Override
	public void run()
	{
		// TODO Auto-generated method stub
		if(isStop)
			return;
		
		/*if(!SystemUtils.isNetConnected(context))//鏃犵綉缁滈摼鎺�
		{
			ViewUtils.sendMessage(handler, HttpMsg.NO_NET_WORK_CONNECT);
			return ;
		}*/
		
		if(type == HttpType.CheckUpdate)//妫�娴嬪崌绾�
		{
			ViewUtils.sendMessage(handler, HttpMsg.CHECK_UPDATE_START);
			if(!SystemUtils.isNetConnected(context))//鏃犵綉缁滈摼鎺�
			{
				ViewUtils.sendMessage(handler, HttpMsg.CHECK_UPDATE_FAIL, "鏃犵綉缁滆繛鎺�");
				return;
			}
			// TODO Auto-generated method stub
			if(!isStop)
			{
				int requestCount = RETRY_COUNT;
				HttpResult result = null;
				for(int i=0;i<requestCount;i++)
				{
					result = checkUpdate(params);
					if(result.isSuccess())
						break;
				}
				if(result.isSuccess())
					ViewUtils.sendMessage(handler, HttpMsg.CHECK_UPDATE_SUCCESS, result.getReuslt());
				else
					ViewUtils.sendMessage(handler, HttpMsg.CHECK_UPDATE_FAIL, result.getError());
			}
			else
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//浠诲姟鍙栨秷
		}
		else if(type == HttpType.GetDeviceStatus)//鑾峰彇璁惧鐘舵��
		{
			ViewUtils.sendMessage(handler, HttpMsg.GET_DEVICE_STATUS_START);
			if(!SystemUtils.isNetConnected(context))//鏃犵綉缁滈摼鎺�
			{
				/*try {
					Thread.sleep(SLEEP_TIME_WHEN_NO_NET);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				ViewUtils.sendMessage(handler, HttpMsg.GET_DEVICE_STATUS_FAIL, "鏃犵綉缁滆繛鎺�");
				return;
			}
			// TODO Auto-generated method stub
			if(!isStop)
			{
				int requestCount = RETRY_COUNT;
				HttpResult result = null;
				for(int i=0;i<requestCount;i++)
				{
					result = getDeviceStatus(params);
					if(result.isSuccess())
						break;
				}
				if(result.isSuccess())
					ViewUtils.sendMessage(handler, HttpMsg.GET_DEVICE_STATUS_SUCCESS, result.getReuslt());
				else
					ViewUtils.sendMessage(handler, HttpMsg.GET_DEVICE_STATUS_FAIL, result.getError());
			}
			else
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//浠诲姟鍙栨秷
		}
		else if(type == HttpType.Register)//闊冲搷娉ㄥ唽
		{
			ViewUtils.sendMessage(handler, HttpMsg.REGISTER_START);
			if(!SystemUtils.isNetConnected(context))//鏃犵綉缁滈摼鎺�
			{
				ViewUtils.sendMessage(handler, HttpMsg.REGISTER_FAIL, "鏃犵綉缁滆繛鎺�");
				return;
			}
			
			if(params == null)
				params = new ArrayList<NameValuePair>();
			
			DeviceInfor infor = LocalDataEntity.newInstance(context).getDeviceInfor();
			String name = infor.getName();
			String code = infor.getId();
			String wifiName = infor.getWifiName();
			String wifiPassword = infor.getWifiPwd();
			String longitude = LocalDataEntity.newInstance(context).getDeviceLon();
			String latitude = LocalDataEntity.newInstance(context).getDeviceLat();
			String addr = LocalDataEntity.newInstance(context).getDeviceAddr();
			
			//Toast.makeText(activity, "寮�濮嬫敞鍐�-->"+longitude + "  " + latitude, 0).show();
			try 
			{
				params.add(new BasicNameValuePair("softVersion", SystemUtils.getPkgInfo(context).versionCode + ""));
			} 
			catch (Exception e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//params.add(new BasicNameValuePair("hardVersion", Build.VERSION.RELEASE));
			params.add(new BasicNameValuePair("volume", getCurrentVolume() + ""));
			if(!TextUtils.isEmpty(name))
				params.add(new BasicNameValuePair("name", name));
			params.add(new BasicNameValuePair("code", code));
			if(!TextUtils.isEmpty(infor.getActCode()))
				params.add(new BasicNameValuePair("activateCode", infor.getActCode()));
			params.add(new BasicNameValuePair("ip", SystemUtils.getIP()));
			params.add(new BasicNameValuePair("wifiMac", SystemUtils.getWifiBSsid(context)));
			if(!TextUtils.isEmpty(wifiName))
				params.add(new BasicNameValuePair("wifiName", wifiName));
			//if(!TextUtils.isEmpty(wifiPassword))
				params.add(new BasicNameValuePair("wifiPassword", wifiPassword));
			if(!TextUtils.isEmpty(longitude))
				params.add(new BasicNameValuePair("longitude", longitude));
			if(!TextUtils.isEmpty(latitude))
				params.add(new BasicNameValuePair("latitude", latitude));
			/*params.add(new BasicNameValuePair("longitude", "114.01666"));
			params.add(new BasicNameValuePair("latitude", "22.538146"));*/
			if(!TextUtils.isEmpty(addr))
				params.add(new BasicNameValuePair("address", addr));
			
			// TODO Auto-generated method stub
			if(!isStop)
			{
				int requestCount = RETRY_COUNT;
				HttpResult result = null;
				for(int i=0;i<requestCount;i++)
				{
					String devCode = LocalDataEntity.newInstance(context).getDeviceCode();
					if(TextUtils.isEmpty(devCode))
					{
						result = register(params);
					}
					else
					{
						params.add(new BasicNameValuePair("id", devCode));
						result = updateSpeakerInfor(params);
						if(!result.isSuccess())
							result = register(params);
					}
					
					if(result.isSuccess())
						break;
				}
				if(result.isSuccess())
				{
					LocalDataEntity.newInstance(context).setDeviceAddr("");
					LocalDataEntity.newInstance(context).setDeviceName("");
					LocalDataEntity.newInstance(context).setDeviceLocation("", "");
					ViewUtils.sendMessage(handler, HttpMsg.REGISTER_SUCCESS, result.getReuslt());
				}
				else
					ViewUtils.sendMessage(handler, HttpMsg.REGISTER_FAIL, result.getError());
			}
			else
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//浠诲姟鍙栨秷
		}
		else if(type == HttpType.CoinRemove)//
		{
			ViewUtils.sendMessage(handler, HttpMsg.CONIN_REMOVE_START);
			if(!SystemUtils.isNetConnected(context))//鏃犵綉缁滈摼鎺�
			{
				ViewUtils.sendMessage(handler, HttpMsg.CONIN_REMOVE_FAIL, "鏃犵綉缁滆繛鎺�");
				return;
			}
			// TODO Auto-generated method stub
			if(!isStop)
			{
				int requestCount = RETRY_COUNT;
				HttpResult result = null;
				for(int i=0;i<requestCount;i++)
				{
					result = coinRemove(params);
					if(result.isSuccess())
						break;
				}
				if(result.isSuccess())
					ViewUtils.sendMessage(handler, HttpMsg.CONIN_REMOVE_SUCCESS, result.getReuslt());
				else
					ViewUtils.sendMessage(handler, HttpMsg.CONIN_REMOVE_FAIL, result.getError());
			}
			else
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//浠诲姟鍙栨秷
		}
		else if(type == HttpType.CoinFreezeCancel)//
		{
			ViewUtils.sendMessage(handler, HttpMsg.CONIN_FREEZE_CANCEL_START);
			if(!SystemUtils.isNetConnected(context))//鏃犵綉缁滈摼鎺�
			{
				ViewUtils.sendMessage(handler, HttpMsg.CONIN_FREEZE_CANCEL_FAIL, "鏃犵綉缁滆繛鎺�");
				return;
			}
			// TODO Auto-generated method stub
			if(!isStop)
			{
				int requestCount = RETRY_COUNT;
				HttpResult result = null;
				for(int i=0;i<requestCount;i++)
				{
					result = coinFreezeCancel(params);
					if(result.isSuccess())
						break;
				}
				if(result.isSuccess())
					ViewUtils.sendMessage(handler, HttpMsg.CONIN_FREEZE_CANCEL_SUCCESS, result.getReuslt());
				else
					ViewUtils.sendMessage(handler, HttpMsg.CONIN_FREEZE_CANCEL_FAIL, result.getError());
			}
			else
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//浠诲姟鍙栨秷
		}
		else if(type == HttpType.GetPlayList)//
		{
			ViewUtils.sendMessage(handler, HttpMsg.GET_PLAY_LIST_START);
			if(!SystemUtils.isNetConnected(context))//鏃犵綉缁滈摼鎺�
			{
				ViewUtils.sendMessage(handler, HttpMsg.GET_PLAY_LIST_FAIL, "鏃犵綉缁滆繛鎺�");
				return;
			}
			// TODO Auto-generated method stub
			if(!isStop)
			{
				int requestCount = RETRY_COUNT;
				HttpResult result = null;
				for(int i=0;i<requestCount;i++)
				{
					result = getPlayList(params);
					if(result.isSuccess())
						break;
				}
				if(result.isSuccess())
					ViewUtils.sendMessage(handler, HttpMsg.GET_PLAY_LIST_SUCCESS, result.getReuslt());
				else
					ViewUtils.sendMessage(handler, HttpMsg.GET_PLAY_LIST_FAIL, result.getError());
			}
			else
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//浠诲姟鍙栨秷
		}
		else if(type == HttpType.UploadSongRecord)//
		{
			ViewUtils.sendMessage(handler, HttpMsg.UPLOAD_SONG_RECORD_START);
			if(!SystemUtils.isNetConnected(context))//鏃犵綉缁滈摼鎺�
			{
				ViewUtils.sendMessage(handler, HttpMsg.UPLOAD_SONG_RECORD_FAIL, "鏃犵綉缁滆繛鎺�");
				return;
			}
			// TODO Auto-generated method stub
			if(!isStop)
			{
				int requestCount = RETRY_COUNT;
				HttpResult result = null;
				for(int i=0;i<requestCount;i++)
				{
					result = uploadSongRecord(params);
					if(result.isSuccess())
						break;
				}
				if(result.isSuccess())
					ViewUtils.sendMessage(handler, HttpMsg.UPLOAD_SONG_RECORD_SUCCESS, result.getReuslt());
				else
					ViewUtils.sendMessage(handler, HttpMsg.UPLOAD_SONG_RECORD_FAIL, result.getError());
			}
			else
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//浠诲姟鍙栨秷
		}
		else if(type == HttpType.InitTerminal)//
		{
			// TODO Auto-generated method stub
			if(!SystemUtils.isNetConnected(context))//鏃犵綉缁滈摼鎺�
			{
				ViewUtils.sendMessage(handler, HttpMsg.INIT_TERMINAL_FAIL, "鏃犵綉缁滆繛鎺�");
				return;
			}
			if(!isStop)
			{
				int requestCount = RETRY_COUNT;
				HttpResult result = null;
				for(int i=0;i<requestCount;i++)
				{
					result = initTerminal(params);
					if(result.isSuccess())
						break;
				}
				if(result.isSuccess())
					ViewUtils.sendMessage(handler, HttpMsg.INIT_TERMINAL_SUCCESS, result.getReuslt());
				else
					ViewUtils.sendMessage(handler, HttpMsg.INIT_TERMINAL_FAIL, result.getError());
			}
			else
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//浠诲姟鍙栨秷
		}
		else if(type == HttpType.GetCurTime)//
		{
			// TODO Auto-generated method stub
			if(!SystemUtils.isNetConnected(context))//鏃犵綉缁滈摼鎺�
			{
				ViewUtils.sendMessage(handler, HttpMsg.GET_CUR_TIME_FAIL, "鏃犵綉缁滆繛鎺�");
				return;
			}
			if(!isStop)
			{
				int requestCount = RETRY_COUNT;
				HttpResult result = null;
				for(int i=0;i<requestCount;i++)
				{
					result = getCurTime(params);
					if(result.isSuccess())
						break;
				}
				if(result.isSuccess())
					ViewUtils.sendMessage(handler, HttpMsg.GET_CUR_TIME_SUCCESS, result.getReuslt());
				else
					ViewUtils.sendMessage(handler, HttpMsg.GET_CUR_TIME_FAIL, result.getError());
			}
			else
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//浠诲姟鍙栨秷
		}
		else if(type == HttpType.GetCurPos)//
		{
			// TODO Auto-generated method stub
			ViewUtils.sendMessage(handler, HttpMsg.GET_CUR_POS_START);
			if(!SystemUtils.isNetConnected(context))//鏃犵綉缁滈摼鎺�
			{
				ViewUtils.sendMessage(handler, HttpMsg.GET_CUR_POS_FAIL, "鏃犵綉缁滆繛鎺�");
				return;
			}
			if(!isStop)
			{
				int requestCount = RETRY_COUNT;
				HttpResult result = null;
				for(int i=0;i<requestCount;i++)
				{
					result = getCurMusicPos(params);
					if(result.isSuccess())
						break;
				}
				if(result.isSuccess())
					ViewUtils.sendMessage(handler, HttpMsg.GET_CUR_POS_SUCCESS, result.getReuslt());
				else
					ViewUtils.sendMessage(handler, HttpMsg.GET_CUR_POS_FAIL, result.getError());
			}
			else
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//浠诲姟鍙栨秷
		}
		else if(type == HttpType.UpdateCurPos)//
		{
			// TODO Auto-generated method stub
			ViewUtils.sendMessage(handler, HttpMsg.UPDATE_CUR_POS_START);
			if(!SystemUtils.isNetConnected(context))//鏃犵綉缁滈摼鎺�
			{
				ViewUtils.sendMessage(handler, HttpMsg.UPDATE_CUR_POS_FAIL, "鏃犵綉缁滆繛鎺�");
				return;
			}
			if(!isStop)
			{
				int requestCount = RETRY_COUNT;
				HttpResult result = null;
				for(int i=0;i<requestCount;i++)
				{
					result = updateSpeakerInfor(params);
					if(result.isSuccess())
						break;
				}
				if(result.isSuccess())
					ViewUtils.sendMessage(handler, HttpMsg.UPDATE_CUR_POS_SUCCESS, result.getReuslt());
				else
					ViewUtils.sendMessage(handler, HttpMsg.UPDATE_CUR_POS_FAIL, result.getError());
			}
			else
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//浠诲姟鍙栨秷
		}
		else if(type == HttpType.BindSpeaker)//
		{
			//ViewUtils.sendMessage(handler, HttpMsg.UPLOAD_SONG_RECORD_START);
			// TODO Auto-generated method stub
			if(!SystemUtils.isNetConnected(context))//鏃犵綉缁滈摼鎺�
			{
				return;
			}
			if(!isStop)
			{
				int requestCount = RETRY_COUNT;
				HttpResult result = null;
				for(int i=0;i<requestCount;i++)
				{
					result = bindSpeaker(params);
					if(result.isSuccess())
						break;
				}
				/*if(result.isSuccess())
					ViewUtils.sendMessage(handler, HttpMsg.UPLOAD_SONG_RECORD_SUCCESS, result.getReuslt());
				else
					ViewUtils.sendMessage(handler, HttpMsg.UPLOAD_SONG_RECORD_FAIL, result.getError());*/
			}
			else
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//浠诲姟鍙栨秷
		}
		else if(type == HttpType.GetPushMusics)//
		{
			ViewUtils.sendMessage(handler, HttpMsg.GET_PUSH_MUSICS_STATR);
			if(!SystemUtils.isNetConnected(context))//鏃犵綉缁滈摼鎺�
			{
				ViewUtils.sendMessage(handler, HttpMsg.GET_PUSH_MUSICS_FAIL, "鏃犵綉缁滆繛鎺�");
				return;
			}
			// TODO Auto-generated method stub
			if(!isStop)
			{
				int requestCount = RETRY_COUNT;
				HttpResult result = null;
				for(int i=0;i<requestCount;i++)
				{
					result = getPushMusics(params);
					if(result.isSuccess())
						break;
				}
				if(result.isSuccess())
					ViewUtils.sendMessage(handler, HttpMsg.GET_PUSH_MUSICS_SUCCESS, result.getReuslt());
				else
					ViewUtils.sendMessage(handler, HttpMsg.GET_PUSH_MUSICS_FAIL, result.getError());
			}
			else
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//浠诲姟鍙栨秷
		}
		else if(type == HttpType.GetPlanMusics)//
		{
			ViewUtils.sendMessage(handler, HttpMsg.GET_PLAY_LIST_START);
			if(!SystemUtils.isNetConnected(context))//鏃犵綉缁滈摼鎺�
			{
				ViewUtils.sendMessage(handler, HttpMsg.GET_PLAY_LIST_FAIL, "鏃犵綉缁滆繛鎺�");
				return;
			}
			int total = 0;
			int pageNo = 1;
			int pagSize = 300;
			boolean requestResult = false;
			List<SongInfor> musicList = new ArrayList<SongInfor>();
			// TODO Auto-generated method stub
			if(!isStop)
			{
				//int requestCount = RETRY_COUNT;
				HttpResult result = null;
				params.add(new BasicNameValuePair("pageSize", pagSize + ""));
				params.add(new BasicNameValuePair("pageNo", pageNo + ""));
				while(true)
				{
					result = getPlanMusics(params);
					if(result.isSuccess())
					{
						List<SongInfor> tempList = (List<SongInfor>)result.getReuslt();
						total = result.getTotal();
						musicList.addAll(tempList);
						requestResult = true;
						if(tempList.size() < pagSize)
							break;
					}
					else
						break;
					pageNo ++;
					if(params.size() > 1)
						params.set(params.size() - 1, new BasicNameValuePair("pageNo", pageNo + ""));
				}
				if(requestResult)
				{
					if(musicList.size() > 1)
					{
						//DBManager.newInstance(context).deleteAllRecord();
						//List<SongInfor> tempList1 = DBManager.newInstance(context).getSongRecord(0, 1000);
						List<SongInfor> tempList = new ArrayList<SongInfor>();
						if(musicList.size() >= 100)
							tempList.addAll(musicList.subList(0, 100));
						else
							tempList.addAll(musicList);
						/*for(int i=0;i<tempList.size();i++)
						{
							SongInfor tempInfor = tempList.get(i);
							DBManager.newInstance(context).insertSongRecord(tempInfor);
						}*/
						//List<SongInfor> tempList2 = DBManager.newInstance(context).getSongRecord(0, 1000);
					}
					ViewUtils.sendMessage(handler, HttpMsg.GET_PLAY_LIST_SUCCESS, musicList);
				}
				else
					ViewUtils.sendMessage(handler, HttpMsg.GET_PLAY_LIST_FAIL, result.getError());
			}
			else
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//浠诲姟鍙栨秷
		}
		else if(type == HttpType.GetCurPlan)//
		{
			ViewUtils.sendMessage(handler, HttpMsg.GET_CUR_PLAN_START);
			if(!SystemUtils.isNetConnected(context))//鏃犵綉缁滈摼鎺�
			{
				ViewUtils.sendMessage(handler, HttpMsg.GET_CUR_PLAN_FAIL, "鏃犵綉缁滆繛鎺�");
				return;
			}
			if(!isStop)
			{
				int requestCount = RETRY_COUNT;
				HttpResult result = null;
				for(int i=0;i<requestCount;i++)
				{
					result = getCurPlan(params);
					if(result.isSuccess())
						break;
				}
				if(result.isSuccess())
				{
					ViewUtils.sendMessage(handler, HttpMsg.GET_CUR_PLAN_SUCCESS, result.getReuslt());					
				}
				else
					ViewUtils.sendMessage(handler, HttpMsg.GET_CUR_PLAN_FAIL, result.getError());
			}
			else
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//浠诲姟鍙栨秷	
		}
	}
}
 
