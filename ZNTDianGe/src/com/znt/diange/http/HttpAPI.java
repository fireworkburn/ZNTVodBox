
package com.znt.diange.http; 


/** 
 * @ClassName: HttpBaseConnect 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-4-22 4:02:09  
 */
public class HttpAPI
{
	public static final String SERVER_ADDRESS = "http://www.zhunit.com/";
	//protected final String SERVER_ADDRESS = "http://192.168.1.117:8080/vodbox/";
	
	public final String CHECK_UPDATE = SERVER_ADDRESS + "mobinf/softVersionAction!getLastVersion.do?";
	public final String USER_CONNECT_COUNT = SERVER_ADDRESS + "mobinf/terminalCountAction!addConnCount.do";
	public final String USER_REGISTER = SERVER_ADDRESS + "mobinf/memberAction!register.do";
	//用户登陆
	public final String USER_LOGIN = SERVER_ADDRESS + "mobinf/memberAction!signIn.do";
	public final String USER_LOGIN_BY_TOKEN = SERVER_ADDRESS + "mobinf/memberAction!checkToken.do";
	//用户资料修改
	public final String USER_INFOR_EDIT = SERVER_ADDRESS + "/mobinf/memberAction!editInfo.do";
	//修改密码
	public final String RESET_PWD = SERVER_ADDRESS + "mobinf/memberAction!editPwd.do";
	
	//上传金币
	public final String COIN_UPLOAD = SERVER_ADDRESS + "mobinf/memberAccountAction!goldAccountAdd.do";
	//获取金币
	public final String COIN_GET = SERVER_ADDRESS + "mobinf/memberAccountAction!getMemberGold.do";
	//冻结金币
	public final String COIN_FREEZE = SERVER_ADDRESS + "mobinf/memberAccountAction!goldConsumeAuth.do";
	//扣除金币
	public final String COIN_REMOVE = SERVER_ADDRESS + "mobinf/memberAccountAction!goldConsumeAuthComplete.do";
	//取消冻结的金币
	public final String COIN_FREEZE_CANCEL = SERVER_ADDRESS + "mobinf/memberAccountAction!goldConsumeAuthCancel.do";
	
	//获取所有的音响
	public final String GET_ALL_SPEAKERS = SERVER_ADDRESS + "mobinf/terminalAction!getAllTerminal.do";
	public final String GET_NEAR_BY_SPEAKERS = SERVER_ADDRESS + "mobinf/terminalAction!getNearbyTerminal.do";
	//获取音响信息
	public final String GET_SPEAKER_INFOR = SERVER_ADDRESS + "mobinf/terminalAction!getTerminal.do";
	//申请管理员权限
	public final String BIND_SPEAKER = SERVER_ADDRESS + "mobinf/memberAction!applyForAdmin.do";
	public final String ADMIN_APPLY = SERVER_ADDRESS + "mobinf/memberAction!addMerchantMember.do";
	
	
	//获取绑定的音响列表
	public final String GET_BIND_SPEAKERS = SERVER_ADDRESS + "mobinf/memberAction!getAllTerminal.do";
	//添加音乐到音响
	public final String ADD_MUSIC_TO_SPEAKER = SERVER_ADDRESS + "mobinf/terminalMusicAction!addTerminalMusic.do";
	//从音响中删除歌曲
	public final String DELETE_MUSIC_FROM_SPEAKER = SERVER_ADDRESS + "mobinf/terminalMusicAction!delTerminalMusic.do";
	//获取音响的曲库列表
	public final String GET_SPEAKER_MUSIC = SERVER_ADDRESS + "mobinf/terminalMusicAction!getTerminalMusicList.do";
		
	
	public final String GET_WX_USER_INFOR = "https://api.weixin.qq.com/sns/userinfo";
	public final String GET_WX_INFOR = "https://api.weixin.qq.com/sns/oauth2/access_token";
}
 
