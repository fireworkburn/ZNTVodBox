
package com.znt.diange.http; 

import java.util.List;

import org.apache.http.NameValuePair;

import android.content.Context;
import android.os.Handler;

import com.znt.diange.utils.SystemUtils;
import com.znt.diange.utils.ViewUtils;

/** 
 * @ClassName: HttpHelper 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-7-31 下午5:35:32  
 */
public class HttpHelper extends HttpManager implements Runnable
{
	private Context context = null;
	private Handler handler = null;
	private HttpType type = null;
	private List<NameValuePair> params = null;
	private List<List<NameValuePair>> paramsList = null;
	private String fileUrl = null;
	
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

	/**
	*callbacks
	*/
	@Override
	public void run()
	{
		// TODO Auto-generated method stub
		if(isStop)
			return;
		
		if(!SystemUtils.isNetConnected(context))//无网络链接
		{
			ViewUtils.sendMessage(handler, HttpMsg.NO_NET_WORK_CONNECT);
			return ;
		}
		
		if(type == HttpType.CheckUpdate)//检测升级
		{
			ViewUtils.sendMessage(handler, HttpMsg.CHECK_UPDATE_START);
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
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//任务取消
		}
		else if(type == HttpType.UserConnetCount)//检测升级
		{
			ViewUtils.sendMessage(handler, HttpMsg.CHECK_UPDATE_START);
			// TODO Auto-generated method stub
			if(!isStop)
			{
				int requestCount = RETRY_COUNT;
				HttpResult result = null;
				for(int i=0;i<requestCount;i++)
				{
					result = userConnectInfor(params);
					if(result.isSuccess())
						break;
				}
				if(result.isSuccess())
					ViewUtils.sendMessage(handler, HttpMsg.CHECK_UPDATE_SUCCESS, result.getReuslt());
				else
					ViewUtils.sendMessage(handler, HttpMsg.CHECK_UPDATE_FAIL, result.getError());
			}
			else
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//任务取消
		}
		else if(type == HttpType.Register)//
		{
			ViewUtils.sendMessage(handler, HttpMsg.REGISTER_START);
			// TODO Auto-generated method stub
			if(!isStop)
			{
				int requestCount = RETRY_COUNT;
				HttpResult result = null;
				for(int i=0;i<requestCount;i++)
				{
					result = register(params);
					if(result.isSuccess())
						break;
				}
				if(result.isSuccess())
					ViewUtils.sendMessage(handler, HttpMsg.REGISTER_SUCCESS, result.getReuslt());
				else
					ViewUtils.sendMessage(handler, HttpMsg.REGISTER_FAIL, result.getError());
			}
			else
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//任务取消
		}
		else if(type == HttpType.Login)//
		{
			ViewUtils.sendMessage(handler, HttpMsg.LOGIN_START);
			// TODO Auto-generated method stub
			if(!isStop)
			{
				int requestCount = RETRY_COUNT;
				HttpResult result = null;
				for(int i=0;i<requestCount;i++)
				{
					result = login(params);
					if(result.isSuccess())
						break;
				}
				if(result.isSuccess())
					ViewUtils.sendMessage(handler, HttpMsg.LOGIN_SUCCESS, result.getReuslt());
				else
					ViewUtils.sendMessage(handler, HttpMsg.LOGIN_FAIL, result.getError());
			}
			else
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//任务取消
		}
		else if(type == HttpType.UserInforEdit)//修改昵称
		{
			ViewUtils.sendMessage(handler, HttpMsg.USER_INFOR_EDIT_START);
			// TODO Auto-generated method stub
			if(!isStop)
			{
				int requestCount = RETRY_COUNT;
				HttpResult result = null;
				for(int i=0;i<requestCount;i++)
				{
					result = userInforEdit(params);
					if(result.isSuccess())
						break;
				}
				if(result.isSuccess())
					ViewUtils.sendMessage(handler, HttpMsg.USER_INFOR_EDIT_SUCCESS, result.getReuslt());
				else
					ViewUtils.sendMessage(handler, HttpMsg.USER_INFOR_EDIT_FAIL, result.getError());
			}
			else
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//任务取消
		}
		else if(type == HttpType.CoinGet)//获取金币
		{
			ViewUtils.sendMessage(handler, HttpMsg.CONIN_GET_START);
			// TODO Auto-generated method stub
			if(!isStop)
			{
				int requestCount = RETRY_COUNT;
				HttpResult result = null;
				for(int i=0;i<requestCount;i++)
				{
					result = coinGet(params);
					if(result.isSuccess())
						break;
				}
				if(result.isSuccess())
					ViewUtils.sendMessage(handler, HttpMsg.CONIN_GET_SUCCESS, result.getReuslt());
				else
					ViewUtils.sendMessage(handler, HttpMsg.CONIN_GET_FAIL, result.getError());
			}
			else
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//任务取消
		}
		else if(type == HttpType.CoinUpload)//上传金币
		{
			ViewUtils.sendMessage(handler, HttpMsg.CONIN_UPLOAD_START);
			// TODO Auto-generated method stub
			if(!isStop)
			{
				int requestCount = RETRY_COUNT;
				HttpResult result = null;
				for(int i=0;i<requestCount;i++)
				{
					result = coinUpload(params);
					if(result.isSuccess())
						break;
				}
				if(result.isSuccess())
					ViewUtils.sendMessage(handler, HttpMsg.CONIN_UPLOAD_SUCCESS, result.getReuslt());
				else
					ViewUtils.sendMessage(handler, HttpMsg.CONIN_UPLOAD_FAIL, result.getError());
			}
			else
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//任务取消
		}
		else if(type == HttpType.GetAllSpeaker)//获取全部音响
		{
			ViewUtils.sendMessage(handler, HttpMsg.GET_ALL_SPEAKER_START);
			// TODO Auto-generated method stub
			if(!isStop)
			{
				int requestCount = RETRY_COUNT;
				HttpResult result = null;
				for(int i=0;i<requestCount;i++)
				{
					result = getAllSpeakers(params);
					if(result.isSuccess())
						break;
				}
				if(result.isSuccess())
					ViewUtils.sendMessage(handler, HttpMsg.GET_ALL_SPEAKER_SUCCESS, result.getReuslt());
				else
					ViewUtils.sendMessage(handler, HttpMsg.GET_ALL_SPEAKER_FAIL, result.getError());
			}
			else
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//任务取消
		}
		else if(type == HttpType.GetNearBySpeaker)//获取附近音响
		{
			ViewUtils.sendMessage(handler, HttpMsg.GET_NEAR_BY_SPEAKER_START);
			// TODO Auto-generated method stub
			if(!isStop)
			{
				int requestCount = RETRY_COUNT;
				HttpResult result = null;
				for(int i=0;i<requestCount;i++)
				{
					result = getNearBySpeakers(params);
					if(result.isSuccess())
						break;
				}
				if(result.isSuccess())
					ViewUtils.sendMessage(handler, HttpMsg.GET_NEAR_BY_SPEAKER_SUCCESS, result.getReuslt());
				else
					ViewUtils.sendMessage(handler, HttpMsg.GET_NEAR_BY_SPEAKER_FAIL, result.getError());
			}
			else
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//任务取消
		}
		else if(type == HttpType.GetSpeakerInfor)//
		{
			//ViewUtils.sendMessage(handler, HttpMsg.CONIN_UPLOAD_START);
			// TODO Auto-generated method stub
			if(!isStop)
			{
				int requestCount = RETRY_COUNT;
				HttpResult result = null;
				for(int i=0;i<requestCount;i++)
				{
					result = getSpeakerInfor(params);
					if(result.isSuccess())
						break;
				}
				/*if(result.isSuccess())
					ViewUtils.sendMessage(handler, HttpMsg.CONIN_UPLOAD_SUCCESS, result.getReuslt());
				else
					ViewUtils.sendMessage(handler, HttpMsg.CONIN_UPLOAD_FAIL, result.getError());*/
			}
			else
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//任务取消
		}
		else if(type == HttpType.CoinFreeze)//
		{
			ViewUtils.sendMessage(handler, HttpMsg.CONIN_FREEZE_START);
			// TODO Auto-generated method stub
			if(!isStop)
			{
				int requestCount = RETRY_COUNT;
				HttpResult result = null;
				for(int i=0;i<requestCount;i++)
				{
					result = coinFreeze(params);
					if(result.isSuccess())
						break;
				}
				if(result.isSuccess())
					ViewUtils.sendMessage(handler, HttpMsg.CONIN_FREEZE_SUCCESS, result.getReuslt());
				else
					ViewUtils.sendMessage(handler, HttpMsg.CONIN_FREEZE_FAIL, result.getError());
			}
			else
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//任务取消
		}
		else if(type == HttpType.CoinFreezeCancel)//
		{
			ViewUtils.sendMessage(handler, HttpMsg.CONIN_FREEZE_CANCEL_START);
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
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//任务取消
		}
		else if(type == HttpType.CoinRemove)//
		{
			ViewUtils.sendMessage(handler, HttpMsg.CONIN_REMOVE_START);
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
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//任务取消
		}
		else if(type == HttpType.AdminApply)//申请管理员权限
		{
			ViewUtils.sendMessage(handler, HttpMsg.ADMIN_APPLY_START);
			// TODO Auto-generated method stub
			if(!isStop)
			{
				int requestCount = RETRY_COUNT;
				HttpResult result = null;
				for(int i=0;i<requestCount;i++)
				{
					result = adminApply(params);
					if(result.isSuccess())
						break;
				}
				if(result.isSuccess())
					ViewUtils.sendMessage(handler, HttpMsg.ADMIN_APPLY_SUCCESS, result.getReuslt());
				else
					ViewUtils.sendMessage(handler, HttpMsg.ADMIN_APPLY_FAIL, result.getError());
			}
			else
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//任务取消
		}
		else if(type == HttpType.GetWXInfor)//获取微信的信息
		{
			ViewUtils.sendMessage(handler, HttpMsg.GET_WX_INFOR_START);
			if(!isStop)
			{
				int requestCount = RETRY_COUNT;
				HttpResult result = null;
				for(int i=0;i<requestCount;i++)
				{
					result = getWXInfor(params);
					if(result.isSuccess())
						break;
				}
				if(result.isSuccess())
				{
					ViewUtils.sendMessage(handler, HttpMsg.GET_WX_INFOR_SUCCESS, result.getReuslt());					
				}
				else
					ViewUtils.sendMessage(handler, HttpMsg.GET_WX_INFOR_FAIL, result.getError());
			}
			else
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//任务取消	
		}
		else if(type == HttpType.AddMusicToSpeaker)//添加音乐到音响
		{
			ViewUtils.sendMessage(handler, HttpMsg.ADD_MUSIC_SPEAKER_START);
			if(!isStop)
			{
				int requestCount = RETRY_COUNT;
				HttpResult result = null;
				for(int i=0;i<requestCount;i++)
				{
					result = addMusicToSpeaker(params);
					if(result.isSuccess())
						break;
				}
				if(result.isSuccess())
				{
					ViewUtils.sendMessage(handler, HttpMsg.ADD_MUSIC_SPEAKER_SUCCESS, result.getReuslt());					
				}
				else
					ViewUtils.sendMessage(handler, HttpMsg.ADD_MUSIC_SPEAKER_FAIL, result.getError());
			}
			else
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//任务取消	
		}
		else if(type == HttpType.DeleteSpeakerMusic)//删除音响音乐
		{
			ViewUtils.sendMessage(handler, HttpMsg.DELETE_SPEAKER_MUSIC_START);
			if(!isStop)
			{
				int requestCount = RETRY_COUNT;
				HttpResult result = null;
				for(int i=0;i<requestCount;i++)
				{
					result = deleteSpeakerMusic(params);
					if(result.isSuccess())
						break;
				}
				if(result.isSuccess())
				{
					ViewUtils.sendMessage(handler, HttpMsg.DELETE_SPEAKER_MUSIC_SUCCESS, result.getReuslt());					
				}
				else
					ViewUtils.sendMessage(handler, HttpMsg.DELETE_SPEAKER_MUSIC_FAIL, result.getError());
			}
			else
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//任务取消	
		}
		else if(type == HttpType.GetSpeakerMusic)//获取音响音乐
		{
			ViewUtils.sendMessage(handler, HttpMsg.GET_SPEAKER_MUSIC_START);
			if(!isStop)
			{
				int requestCount = RETRY_COUNT;
				HttpResult result = null;
				for(int i=0;i<requestCount;i++)
				{
					result = getSpeakerMusic(params);
					if(result.isSuccess())
						break;
				}
				if(result.isSuccess())
				{
					ViewUtils.sendMessage(handler, HttpMsg.GET_SPEAKER_MUSIC_SUCCESS, result.getReuslt());					
				}
				else
					ViewUtils.sendMessage(handler, HttpMsg.GET_SPEAKER_MUSIC_FAIL, result.getError());
			}
			else
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//任务取消	
		}
		else if(type == HttpType.GetBindSpeakers)//获取绑定的音响列表
		{
			ViewUtils.sendMessage(handler, HttpMsg.GET_BIND_SPEAKERS_START);
			if(!isStop)
			{
				int requestCount = RETRY_COUNT;
				HttpResult result = null;
				for(int i=0;i<requestCount;i++)
				{
					result = getBindSpeakers(params);
					if(result.isSuccess())
						break;
				}
				if(result.isSuccess())
				{
					ViewUtils.sendMessage(handler, HttpMsg.GET_BIND_SPEAKERS_SUCCESS, result.getReuslt());					
				}
				else
					ViewUtils.sendMessage(handler, HttpMsg.GET_BIND_SPEAKERS_FAIL, result.getError());
			}
			else
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//任务取消	
		}
	}
}
 
