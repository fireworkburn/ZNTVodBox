
package com.znt.diange.mina.cmd; 

import java.io.Serializable;

/** 
 * @ClassName: CmdType 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-8-3 上午11:07:16  
 */
public class CmdType implements Serializable
{

	/** 
	* @Fields serialVersionUID : TODO
	*/ 
	private static final long serialVersionUID = 1L;
	
	public static final String REGISTER = "00";//注册
	public static final String REGISTER_FB = "01";//注册反馈
	public static final String GET_PLAY_MUSIC_INFOR = "02";//获取播放的歌曲信息
	public static final String GET_PLAY_MUSIC_INFOR_FB = "03";//获取播放的歌曲信息反馈
	public static final String GET_PLAY_LIST = "04";//获取播放列表
	public static final String GET_PLAY_LIST_FB = "05";//获取播放列表反馈
	public static final String PLAY = "06";//点播
	public static final String PLAY_FB = "07";//点播反馈
	public static final String DELETE_SONG = "08";//删除歌曲
	public static final String DELETE_SONG_FB = "09";//删除反馈
	public static final String UPDATE_INFOR = "10";//更新广播
	public static final String STOP = "11";//播放停止
	public static final String STOP_FB = "12";//播放停止反馈
	public static final String SET_DEVICE = "13";//设备编辑设置
	public static final String SET_DEVICE_FB = "14";//设备编辑反馈
	public static final String AUDIO = "15";//音频流
	public static final String GET_DEVICE_INFOR = "16";//获取设备信息
	public static final String SET_DEVICE_VOLUM = "17";//设置设备音量
	public static final String SET_DEVICE_VOLUM_FB = "18";//设置设备音量反馈
	public static final String GET_DEVICE_VOLUM = "19";//获取设备音量
	public static final String GET_PLAY_STATE = "20";//获取播放状态
	public static final String SET_PLAY_STATE = "21";//设置播放状态
	public static final String PLAY_NEXT = "22";//切歌
	public static final String PLAY_NEXT_FB = "23";//切歌反馈
	public static final String SPEAKER_MUSIC = "24";//获取音响音乐列表
	public static final String SPEAKER_MUSIC_FB = "25";//获取音响音乐列表反馈
	public static final String SYSTEM_UPDATE = "26";//音响系统更新
	public static final String SYSTEM_UPDATE_FB = "27";//音响系统更新反馈
	public static final String PLAY_ERROR = "28";//播放失败
	public static final String PLAY_PERMISSION = "29";//播放权限
	public static final String ERROR = "30";//播放错误
	public static final String GET_PLAY_RECORD = "31";//获取播放记录
	public static final String GET_PLAY_RECORD_FB = "32";//获取播放记录
	public static final String SET_PLAY_RES = "33";//设置播放音乐源
	public static final String SET_PLAY_RES_FB = "34";//设置播放音乐源反馈
	public static final String RES_UPDATE = "35";//更新曲库
	public static final String GET_PLAY_RES = "36";//获取播放的曲库类别
	public static final String GET_PLAY_PERMISSION = "37";//获取点播权限
	public static final String GET_WIFI_LIST = "38";//获取点播权限
	

}
 
