
package com.znt.diange.http; 
/** 
 * @ClassName: HttpMsg 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-5-15 下午5:36:01  
 */
public class HttpMsg
{
	public static final int HTTP_CANCEL = -2;//任务取消
	public static final int NO_NET_WORK_CONNECT = -1;//无网络连接
	
	public static final int CHECK_UPDATE_START = 0x1001;//检测升级开始
	public static final int CHECK_UPDATE_SUCCESS = 0x1002;//检测升级成功
	public static final int CHECK_UPDATE_FAIL = 0x1003;//检测升级失败
	
	
	public static final int LOGIN_START = 0x2001;//登陆开始
	public static final int LOGIN_SUCCESS = 0x2002;//登陆成功
	public static final int SET_USER_HEAD = 0x2003;//设置头像
	public static final int SET_USER_HEAD_SUCCESS = 0x2004;//头像设置成功
	public static final int SET_USER_HEAD_FAIL = 0x2005;//设置头像失败
	public static final int REGISTER_START = 0x2006;//注册
	public static final int REGISTER_SUCCESS = 0x2007;//注册成功
	public static final int REGISTER_FAIL = 0x2008;//注册失败
	public static final int NEW_PWD_BY_PHONE = 0x2009;//找回密码
	public static final int NEW_PWD_BY_PHONE_SUCCESS = 0x2010;//找回密码成功
	public static final int NEW_PWD_BY_PHONE_FAIL = 0x2011;//找回密码失败
	public static final int LOGIN_FAIL = 0x2012;//注册失败
	public static final int USER_INFOR_EDIT_START = 0x2013;//修改昵称开始
	public static final int USER_INFOR_EDIT_SUCCESS = 0x2014;//修改昵称成功
	public static final int USER_INFOR_EDIT_FAIL = 0x2015;//修改昵称失败
	public static final int PWD_RESET_START = 0x2016;//修改密码开始
	public static final int PWD_RESET_SUCCESS = 0x2017;//修改密码成功
	public static final int PWD_RESET_FAIL = 0x2018;//修改密码失败
	
	public static final int CONIN_GET_START = 0x2019;//获取金币开始
	public static final int CONIN_GET_SUCCESS = 0x2020;//获取金币成功
	public static final int CONIN_GET_FAIL = 0x2021;//获取金币失败
	public static final int CONIN_UPLOAD_START = 0x2022;//上传金币开始
	public static final int CONIN_UPLOAD_SUCCESS = 0x2023;//上传金币成功
	public static final int CONIN_UPLOAD_FAIL = 0x2024;//上传金币失败
	public static final int CONIN_REMOVE_START = 0x2025;//扣除金币开始
	public static final int CONIN_REMOVE_SUCCESS = 0x2026;//扣除金币成功
	public static final int CONIN_REMOVE_FAIL = 0x2027;//扣除金币失败
	public static final int CONIN_FREEZE_START = 0x2028;//冻结金币开始
	public static final int CONIN_FREEZE_SUCCESS = 0x2029;//冻结金币成功
	public static final int CONIN_FREEZE_FAIL = 0x2030;//冻结金币失败
	public static final int CONIN_FREEZE_CANCEL_START = 0x2031;//冻结金币开始
	public static final int CONIN_FREEZE_CANCEL_SUCCESS = 0x2032;//冻结金币成功
	public static final int CONIN_FREEZE_CANCEL_FAIL = 0x2033;//冻结金币失败
	
	public static final int GET_ALL_SPEAKER_START = 0x3001;//获取设备列表开始
	public static final int GET_ALL_SPEAKER_SUCCESS = 0x3002;//获取设备列表成功
	public static final int GET_ALL_SPEAKER_FAIL = 0x3003;//获取设备列表失败
	public static final int GET_NEAR_BY_SPEAKER_START = 0x3004;//获取设备列表开始
	public static final int GET_NEAR_BY_SPEAKER_SUCCESS = 0x3005;//获取设备列表成功
	public static final int GET_NEAR_BY_SPEAKER_FAIL = 0x3006;//获取设备列表失败
	public static final int ADMIN_APPLY_START = 0x3007;//申请管理员权限开始
	public static final int ADMIN_APPLY_SUCCESS = 0x3008;//申请管理员权限成功
	public static final int ADMIN_APPLY_FAIL = 0x3009;//申请管理员权限失败
	
	public static final int GET_WX_INFOR_START = 0x313;//获取微信信息开始
	public static final int GET_WX_INFOR_SUCCESS = 0x314;//获取微信信息成功
	public static final int GET_WX_INFOR_FAIL = 0x315;//获取微信信息失败
	
	public static final int ADD_MUSIC_SPEAKER_START = 0x3020;//添加音乐到音响开始
	public static final int ADD_MUSIC_SPEAKER_SUCCESS = 0x3021;//添加音乐到音响成功
	public static final int ADD_MUSIC_SPEAKER_FAIL = 0x3022;//添加音乐到音响失败
	public static final int DELETE_SPEAKER_MUSIC_START = 0x3022;//删除音响歌曲开始
	public static final int DELETE_SPEAKER_MUSIC_SUCCESS = 0x3023;//删除音响歌曲成功
	public static final int DELETE_SPEAKER_MUSIC_FAIL = 0x3024;//删除音响歌曲失败
	public static final int GET_SPEAKER_MUSIC_START = 0x3022;//获取音响歌曲开始
	public static final int GET_SPEAKER_MUSIC_SUCCESS = 0x3023;//获取音响歌曲成功
	public static final int GET_SPEAKER_MUSIC_FAIL = 0x3024;//获取音响歌曲失败
	public static final int GET_BIND_SPEAKERS_START = 0x3025;//获取绑定的音响列表开始
	public static final int GET_BIND_SPEAKERS_SUCCESS = 0x3026;//获取绑定的音响列表成功
	public static final int GET_BIND_SPEAKERS_FAIL = 0x3027;//获取绑定的音响列表失败
	
	
	
}
 
