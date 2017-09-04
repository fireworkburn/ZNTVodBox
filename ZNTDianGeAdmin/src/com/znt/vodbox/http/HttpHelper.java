
package com.znt.vodbox.http; 

import java.util.List;

import org.apache.http.NameValuePair;

import android.content.Context;
import android.os.Handler;

import com.znt.vodbox.utils.SystemUtils;
import com.znt.vodbox.utils.ViewUtils;

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
		else if(type == HttpType.ResetPwd)//修改密码
		{
			ViewUtils.sendMessage(handler, HttpMsg.PWD_RESET_START);
			// TODO Auto-generated method stub
			if(!isStop)
			{
				int requestCount = RETRY_COUNT;
				HttpResult result = null;
				for(int i=0;i<requestCount;i++)
				{
					result = resetPwd(params);
					if(result.isSuccess())
						break;
				}
				if(result.isSuccess())
					ViewUtils.sendMessage(handler, HttpMsg.PWD_RESET_SUCCESS, result.getReuslt());
				else
					ViewUtils.sendMessage(handler, HttpMsg.PWD_RESET_FAIL, result.getError());
			}
			else
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//任务取消
		}
		/*else if(type == HttpType.CoinGet)//获取金币
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
		}*/
		/*else if(type == HttpType.CoinUpload)//上传金币
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
		}*/
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
		else if(type == HttpType.GetSecondLevels)//获二级管理员列表
		{
			ViewUtils.sendMessage(handler, HttpMsg.GET_SECOND_LEVELS_START);
			// TODO Auto-generated method stub
			if(!isStop)
			{
				int requestCount = RETRY_COUNT;
				HttpResult result = null;
				for(int i=0;i<requestCount;i++)
				{
					result = getSecondLevels(params);
					if(result.isSuccess())
						break;
				}
				if(result.isSuccess())
					ViewUtils.sendMessage(handler, HttpMsg.GET_SECOND_LEVELS_SUCCESS, result);
				else
					ViewUtils.sendMessage(handler, HttpMsg.GET_SECOND_LEVELS_FAIL, result.getError());
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
		/*else if(type == HttpType.CoinFreeze)//
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
		}*/
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
					ViewUtils.sendMessage(handler, HttpMsg.GET_BIND_SPEAKERS_SUCCESS, result);					
				}
				else
					ViewUtils.sendMessage(handler, HttpMsg.GET_BIND_SPEAKERS_FAIL, result.getError());
			}
			else
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//任务取消	
		}
		else if(type == HttpType.GetCreateAlbums)//
		{
			ViewUtils.sendMessage(handler, HttpMsg.GET_MUSIC_ALBUM_START);
			if(!isStop)
			{
				int requestCount = RETRY_COUNT;
				HttpResult result = null;
				for(int i=0;i<requestCount;i++)
				{
					result = getCreateAlbums(params);
					if(result.isSuccess())
						break;
				}
				if(result.isSuccess())
				{
					ViewUtils.sendMessage(handler, HttpMsg.GET_MUSIC_ALBUM_SUCCESS, result.getReuslt());					
				}
				else
					ViewUtils.sendMessage(handler, HttpMsg.GET_MUSIC_ALBUM_FAIL, result.getError());
			}
			else
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//任务取消	
		}
		else if(type == HttpType.GetMusicAlbums)//
		{
			ViewUtils.sendMessage(handler, HttpMsg.GET_MUSIC_ALBUM_START);
			if(!isStop)
			{
				int requestCount = RETRY_COUNT;
				HttpResult result = null;
				for(int i=0;i<requestCount;i++)
				{
					result = getMusicAlbums(params);
					if(result.isSuccess())
						break;
				}
				if(result.isSuccess())
				{
					ViewUtils.sendMessage(handler, HttpMsg.GET_MUSIC_ALBUM_SUCCESS, result.getReuslt());					
				}
				else
					ViewUtils.sendMessage(handler, HttpMsg.GET_MUSIC_ALBUM_FAIL, result.getError());
			}
			else
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//任务取消	
		}
		else if(type == HttpType.GetParentMusicAlbums)//
		{
			ViewUtils.sendMessage(handler, HttpMsg.GET_PARENT_MUSIC_ALBUM_START);
			if(!isStop)
			{
				int requestCount = RETRY_COUNT;
				HttpResult result = null;
				for(int i=0;i<requestCount;i++)
				{
					result = getMyParentMusicAlbums(params);
					if(result.isSuccess())
						break;
				}
				if(result.isSuccess())
				{
					ViewUtils.sendMessage(handler, HttpMsg.GET_PARENT_MUSIC_ALBUM_SUCCESS, result.getReuslt());					
				}
				else
					ViewUtils.sendMessage(handler, HttpMsg.GET_PARENT_MUSIC_ALBUM_FAIL, result.getError());
			}
			else
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//任务取消	
		}
		else if(type == HttpType.GetSystemAlbums)//
		{
			ViewUtils.sendMessage(handler, HttpMsg.GET_SYSTEM_ALBUMS_START);
			if(!isStop)
			{
				int requestCount = RETRY_COUNT;
				HttpResult result = null;
				for(int i=0;i<requestCount;i++)
				{
					result = getSystemAlbums(params);
					if(result.isSuccess())
						break;
				}
				if(result.isSuccess())
				{
					ViewUtils.sendMessage(handler, HttpMsg.GET_SYSTEM_ALBUMS_SUCCESS, result.getReuslt());					
				}
				else
					ViewUtils.sendMessage(handler, HttpMsg.GET_SYSTEM_ALBUMS_FAIL, result.getError());
			}
			else
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//任务取消	
		}
		else if(type == HttpType.GetAlbumMusics)//
		{
			ViewUtils.sendMessage(handler, HttpMsg.GET_ALBUM_MUSIC_START);
			if(!isStop)
			{
				int requestCount = RETRY_COUNT;
				HttpResult result = null;
				for(int i=0;i<requestCount;i++)
				{
					result = getAlbumMusics(params);
					if(result.isSuccess())
						break;
				}
				if(result.isSuccess())
				{
					ViewUtils.sendMessage(handler, HttpMsg.GET_ALBUM_MUSIC_SUCCESS, result);					
				}
				else
					ViewUtils.sendMessage(handler, HttpMsg.GET_ALBUM_MUSIC_FAIL, result.getError());
			}
			else
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//任务取消	
		}
		else if(type == HttpType.CreateAlbum)//
		{
			ViewUtils.sendMessage(handler, HttpMsg.CREATE_ALBUM_START);
			if(!isStop)
			{
				int requestCount = RETRY_COUNT;
				HttpResult result = null;
				for(int i=0;i<requestCount;i++)
				{
					result = createAlbum(params);
					if(result.isSuccess())
						break;
				}
				if(result.isSuccess())
				{
					ViewUtils.sendMessage(handler, HttpMsg.CREATE_ALBUM_SUCCESS, result.getReuslt());					
				}
				else
					ViewUtils.sendMessage(handler, HttpMsg.CREATE_ALBUM_FAIL, result.getError());
			}
			else
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//任务取消	
		}
		else if(type == HttpType.EditAlbum)//
		{
			ViewUtils.sendMessage(handler, HttpMsg.EDIT_ALBUM_START);
			if(!isStop)
			{
				int requestCount = RETRY_COUNT;
				HttpResult result = null;
				for(int i=0;i<requestCount;i++)
				{
					result = editAlbum(params);
					if(result.isSuccess())
						break;
				}
				if(result.isSuccess())
				{
					ViewUtils.sendMessage(handler, HttpMsg.EDIT_ALBUM_SUCCESS, result.getReuslt());					
				}
				else
					ViewUtils.sendMessage(handler, HttpMsg.EDIT_ALBUM_FAIL, result.getError());
			}
			else
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//任务取消	
		}
		else if(type == HttpType.DeleteAlbum)//
		{
			ViewUtils.sendMessage(handler, HttpMsg.DELETE_ALBUM_START);
			if(!isStop)
			{
				int requestCount = RETRY_COUNT;
				HttpResult result = null;
				for(int i=0;i<requestCount;i++)
				{
					result = deleteAlbum(params);
					if(result.isSuccess())
						break;
				}
				if(result.isSuccess())
				{
					ViewUtils.sendMessage(handler, HttpMsg.DELETE_ALBUM_SUCCESS, result.getReuslt());					
				}
				else
					ViewUtils.sendMessage(handler, HttpMsg.DELETE_ALBUM_FAIL, result.getError());
			}
			else
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//任务取消	
		}
		else if(type == HttpType.DeleteAlbumMusic)//
		{
			ViewUtils.sendMessage(handler, HttpMsg.DELETE_ALBUM_MUSIC_START);
			if(!isStop)
			{
				int requestCount = RETRY_COUNT;
				HttpResult result = null;
				for(int i=0;i<requestCount;i++)
				{
					result = deleteAlbumMusic(params);
					if(result.isSuccess())
						break;
				}
				if(result.isSuccess())
				{
					ViewUtils.sendMessage(handler, HttpMsg.DELETE_ALBUM_MUSIC_SUCCESS, result.getReuslt());					
				}
				else
					ViewUtils.sendMessage(handler, HttpMsg.DELETE_ALBUM_MUSIC_FAIL, result.getError());
			}
			else
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//任务取消	
		}
		else if(type == HttpType.DeleteAlbumMusics)//
		{
			ViewUtils.sendMessage(handler, HttpMsg.DELETE_ALBUM_MUSICS_START);
			if(!isStop)
			{
				int requestCount = RETRY_COUNT;
				HttpResult result = null;
				for(int i=0;i<requestCount;i++)
				{
					result = deleteAlbumMusics(params);
					if(result.isSuccess())
						break;
				}
				if(result.isSuccess())
				{
					ViewUtils.sendMessage(handler, HttpMsg.DELETE_ALBUM_MUSICS_SUCCESS, result.getReuslt());					
				}
				else
					ViewUtils.sendMessage(handler, HttpMsg.DELETE_ALBUM_MUSICS_FAIL, result.getError());
			}
			else
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//任务取消	
		}
		else if(type == HttpType.GetCurPlan)//
		{
			ViewUtils.sendMessage(handler, HttpMsg.GET_CUR_PLAN_START);
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
					ViewUtils.sendMessage(handler, HttpMsg.GET_CUR_PLAN_SUCCESS, result);					
				}
				else
					ViewUtils.sendMessage(handler, HttpMsg.GET_CUR_PLAN_FAIL, result.getError());
			}
			else
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//任务取消	
		}
		else if(type == HttpType.GetBoxPlan)//
		{
			ViewUtils.sendMessage(handler, HttpMsg.GET_CUR_PLAN_START);
			if(!isStop)
			{
				int requestCount = RETRY_COUNT;
				HttpResult result = null;
				for(int i=0;i<requestCount;i++)
				{
					result = getBoxPlan(params);
					if(result.isSuccess())
						break;
				}
				if(result.isSuccess())
				{
					ViewUtils.sendMessage(handler, HttpMsg.GET_CUR_PLAN_SUCCESS, result);					
				}
				else
					ViewUtils.sendMessage(handler, HttpMsg.GET_CUR_PLAN_FAIL, result.getError());
			}
			else
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//任务取消	
		}
		else if(type == HttpType.AddPlan)//
		{
			ViewUtils.sendMessage(handler, HttpMsg.ADD_PLAN_START);
			if(!isStop)
			{
				int requestCount = RETRY_COUNT;
				HttpResult result = null;
				for(int i=0;i<requestCount;i++)
				{
					result = addPlan(params);
					if(result.isSuccess())
						break;
				}
				if(result.isSuccess())
				{
					ViewUtils.sendMessage(handler, HttpMsg.ADD_PLAN_SUCCESS, result.getReuslt());					
				}
				else
					ViewUtils.sendMessage(handler, HttpMsg.ADD_PLAN_FAIL, result.getError());
			}
			else
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//任务取消	
		}
		else if(type == HttpType.EditPlan)//
		{
			ViewUtils.sendMessage(handler, HttpMsg.EDIT_PLAN_START);
			if(!isStop)
			{
				int requestCount = RETRY_COUNT;
				HttpResult result = null;
				for(int i=0;i<requestCount;i++)
				{
					result = editPlan(params);
					if(result.isSuccess())
						break;
				}
				if(result.isSuccess())
				{
					ViewUtils.sendMessage(handler, HttpMsg.EDIT_PLAN_SUCCESS, result.getReuslt());					
				}
				else
					ViewUtils.sendMessage(handler, HttpMsg.EDIT_PLAN_FAIL, result.getError());
			}
			else
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//任务取消	
		}
		else if(type == HttpType.AddMusicToAlbum)//
		{
			ViewUtils.sendMessage(handler, HttpMsg.ADD_MUSIC_TO_ALBUM_START);
			if(!isStop)
			{
				int requestCount = RETRY_COUNT;
				HttpResult result = null;
				for(int i=0;i<requestCount;i++)
				{
					result = addMusicToAlbum(params);
					if(result.isSuccess())
						break;
				}
				if(result.isSuccess())
				{
					ViewUtils.sendMessage(handler, HttpMsg.ADD_MUSIC_TO_ALBUM_SUCCESS, result.getReuslt());					
				}
				else
					ViewUtils.sendMessage(handler, HttpMsg.ADD_MUSIC_TO_ALBUM_FAIL, result.getError());
			}
			else
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//任务取消	
		}
		else if(type == HttpType.AddSysMusicToAlbum)//
		{
			ViewUtils.sendMessage(handler, HttpMsg.ADD_MUSIC_TO_ALBUM_START);
			if(!isStop)
			{
				int requestCount = RETRY_COUNT;
				HttpResult result = null;
				for(int i=0;i<requestCount;i++)
				{
					result = addSysMusicToAlbum(params);
					if(result.isSuccess())
						break;
				}
				if(result.isSuccess())
				{
					ViewUtils.sendMessage(handler, HttpMsg.ADD_MUSIC_TO_ALBUM_SUCCESS, result.getReuslt());					
				}
				else
					ViewUtils.sendMessage(handler, HttpMsg.ADD_MUSIC_TO_ALBUM_FAIL, result.getError());
			}
			else
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//任务取消	
		}
		else if(type == HttpType.GetPlanMusics)//
		{
			ViewUtils.sendMessage(handler, HttpMsg.GET_SPEAKER_MUSIC_START);
			// TODO Auto-generated method stub
			if(!isStop)
			{
				int requestCount = RETRY_COUNT;
				HttpResult result = null;
				for(int i=0;i<requestCount;i++)
				{
					result = getPlanMusics(params);
					if(result.isSuccess())
						break;
				}
				if(result.isSuccess())
				{
					ViewUtils.sendMessage(handler, HttpMsg.GET_SPEAKER_MUSIC_SUCCESS, result);
				}
				else
					ViewUtils.sendMessage(handler, HttpMsg.GET_SPEAKER_MUSIC_FAIL, result.getError());
			}
			else
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//任务取消
		}
		else if(type == HttpType.CollectAlbum)//
		{
			ViewUtils.sendMessage(handler, HttpMsg.ALBUM_COLLECT_START);
			// TODO Auto-generated method stub
			if(!isStop)
			{
				int requestCount = RETRY_COUNT;
				HttpResult result = null;
				for(int i=0;i<requestCount;i++)
				{
					result = collectAlbum(params);
					if(result.isSuccess())
						break;
				}
				if(result.isSuccess())
				{
					ViewUtils.sendMessage(handler, HttpMsg.ALBUM_COLLECT_SUCCESS, result.getReuslt());
				}
				else
					ViewUtils.sendMessage(handler, HttpMsg.ALBUM_COLLECT_FAIL, result.getError());
			}
			else
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//任务取消
		}
		else if(type == HttpType.PushMusic)//
		{
			ViewUtils.sendMessage(handler, HttpMsg.PUSH_MUSIC_START);
			// TODO Auto-generated method stub
			if(!isStop)
			{
				int requestCount = RETRY_COUNT;
				HttpResult result = null;
				for(int i=0;i<requestCount;i++)
				{
					result = pushMusic(params);
					if(result.isSuccess())
						break;
				}
				if(result.isSuccess())
				{
					ViewUtils.sendMessage(handler, HttpMsg.PUSH_MUSIC_SUCCESS, result.getReuslt());
				}
				else
					ViewUtils.sendMessage(handler, HttpMsg.PUSH_MUSIC_FAIL, result.getError());
			}
			else
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//任务取消
		}
		else if(type == HttpType.GetAllPlanMusic)//
		{
			ViewUtils.sendMessage(handler, HttpMsg.GET_ALBUM_MUSIC_START);
			// TODO Auto-generated method stub
			if(!isStop)
			{
				int requestCount = RETRY_COUNT;
				HttpResult result = null;
				for(int i=0;i<requestCount;i++)
				{
					result = getAllPlanMusics(params);
					if(result.isSuccess())
						break;
				}
				if(result.isSuccess())
				{
					ViewUtils.sendMessage(handler, HttpMsg.GET_ALBUM_MUSIC_SUCCESS, result);
				}
				else
					ViewUtils.sendMessage(handler, HttpMsg.GET_ALBUM_MUSIC_FAIL, result.getError());
			}
			else
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//任务取消
		}
		else if(type == HttpType.GetPushHostoryMusic)//
		{
			ViewUtils.sendMessage(handler, HttpMsg.GET_PUSH_HISTORY_MUSIC_START);
			// TODO Auto-generated method stub
			if(!isStop)
			{
				int requestCount = RETRY_COUNT;
				HttpResult result = null;
				for(int i=0;i<requestCount;i++)
				{
					result = getPushHistoryMusics(params);
					if(result.isSuccess())
						break;
				}
				if(result.isSuccess())
				{
					ViewUtils.sendMessage(handler, HttpMsg.GET_PUSH_HISTORY_MUSIC_SUCCESS, result);
				}
				else
					ViewUtils.sendMessage(handler, HttpMsg.GET_PUSH_HISTORY_MUSIC_FAIL, result.getError());
			}
			else
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//任务取消
		}
		else if(type == HttpType.UpdateSpeakerInfor)//
		{
			ViewUtils.sendMessage(handler, HttpMsg.UPDATE_SPEAKER_INFOR_START);
			// TODO Auto-generated method stub
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
				{
					ViewUtils.sendMessage(handler, HttpMsg.UPDATE_SPEAKER_INFOR_SUCCESS, result);
				}
				else
					ViewUtils.sendMessage(handler, HttpMsg.UPDATE_SPEAKER_INFOR_FAIL, result.getError());
			}
			else
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//任务取消
		}
		else if(type == HttpType.StartOldPlan)//
		{
			ViewUtils.sendMessage(handler, HttpMsg.START_OLDPLAN_START);
			// TODO Auto-generated method stub
			if(!isStop)
			{
				int requestCount = RETRY_COUNT;
				HttpResult result = null;
				for(int i=0;i<requestCount;i++)
				{
					result = startOldPlan(params);
					if(result.isSuccess())
						break;
				}
				if(result.isSuccess())
				{
					ViewUtils.sendMessage(handler, HttpMsg.START_OLDPLAN_SUCCESS, result);
				}
				else
					ViewUtils.sendMessage(handler, HttpMsg.START_OLDPLAN_FAIL, result.getError());
			}
			else
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//任务取消
		}
		else if(type == HttpType.DeleteOldPlan)//
		{
			ViewUtils.sendMessage(handler, HttpMsg.DELETE_OLDPLAN_START);
			// TODO Auto-generated method stub
			if(!isStop)
			{
				int requestCount = RETRY_COUNT;
				HttpResult result = null;
				for(int i=0;i<requestCount;i++)
				{
					result = deleteOldPlan(params);
					if(result.isSuccess())
						break;
				}
				if(result.isSuccess())
				{
					ViewUtils.sendMessage(handler, HttpMsg.DELETE_OLDPLAN_SUCCESS, result);
				}
				else
					ViewUtils.sendMessage(handler, HttpMsg.DELETE_OLDPLAN_FAIL, result.getError());
			}
			else
				ViewUtils.sendMessage(handler, HttpMsg.HTTP_CANCEL);//任务取消
		}
	}
}
 
