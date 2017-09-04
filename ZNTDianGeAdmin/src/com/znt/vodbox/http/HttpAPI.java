
package com.znt.vodbox.http; 


/** 
 * @ClassName: HttpBaseConnect 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-4-22 4:02:09  
 */
public class HttpAPI
{
	public static String SERVER_ADDRESS = "https://www.zhunit.com/";
	//protected final String SERVER_ADDRESS = "http://192.168.1.117:8080/";
	//protected final String SERVER_ADDRESS = "http://192.168.1.118:8080/vodbox/";
	//protected final String SERVER_ADDRESS = "http://192.168.1.113:8080/vodbox/";
	
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
	public final String GET_SECOND_LEVEL = SERVER_ADDRESS + "/mobinf/memberAction!getSecondLevel.do";
	//获取音响信息
	public final String GET_SPEAKER_INFOR = SERVER_ADDRESS + "mobinf/terminalAction!getTerminal.do";
	//申请管理员权限
	public final String ADMIN_APPLY = SERVER_ADDRESS + "mobinf/memberAction!applyForAdmin.do";
	
	
	//获取绑定的音响列表
	public final String GET_BIND_SPEAKERS = SERVER_ADDRESS + "/mobinf/terminalAction!getMyTerminal.do";
	//添加音乐到音响
	public final String ADD_MUSIC_TO_SPEAKER = SERVER_ADDRESS + "mobinf/terminalMusicAction!addTerminalMusic.do";
	//从音响中删除歌曲
	public final String DELETE_MUSIC_FROM_SPEAKER = SERVER_ADDRESS + "mobinf/terminalMusicAction!delTerminalMusic.do";
	//获取音响的曲库列表
	public final String GET_SPEAKER_MUSIC = SERVER_ADDRESS + "mobinf/terminalMusicAction!getTerminalMusicList.do";
	public final String GET_PLAN_MUSICS = SERVER_ADDRESS + "/mobinf/terminalAction!getPlanMusicList.do";
	//获取歌单列表
	public final String GET_MUSIC_ALBUM = SERVER_ADDRESS + "/mobinf/musicCategoryAction!getMyMusicCategory.do";
	public final String GET_CREATE_AND_COLLECT = SERVER_ADDRESS + "/mobinf/musicCategoryAction!getMyCreateAndCollectCategory.do";
	//获取歌单列表
	public final String GET_ALBUM_MUSIC = SERVER_ADDRESS + "/mobinf/musicAction!getMyCategoryMusic.do";
	//
	public final String CREATE_ALBUM = SERVER_ADDRESS + "/mobinf/musicCategoryAction!createMusicCategory.do";
	public final String EDIT_ALBUM = SERVER_ADDRESS + "/mobinf/musicCategoryAction!updateMyMusicCategory.do";
	public final String DELETE_ALBUM = SERVER_ADDRESS + "/mobinf/musicCategoryAction!delMyMusicCategory.do";
	public final String DELETE_ALBUM_MUSIC = SERVER_ADDRESS + "/mobinf/musicAction!delMusicFromMyCategoryOrLike.do";
	public final String DELETE_ALBUM_MUSICS = SERVER_ADDRESS + "/mobinf/musicAction!delMyMusics.do";
	public final String GET_CREATE_ALBUMS = SERVER_ADDRESS + "/mobinf/musicCategoryAction!getMyMusicCategory.do";
	public final String GET_SYSTEM_ALBUMS = SERVER_ADDRESS + "/mobinf/musicCategoryAction!getSysCategory.do";
	public final String CLOLLECT_ALBUM = SERVER_ADDRESS + "/mobinf/musicCategoryAction!addCollectCategory.do";
	public final String ADD_SYS_MUSICS_TO_MY_ALBUM = SERVER_ADDRESS + "/mobinf/musicAction!addSysMusicsToMyCategory.do";
	
	//获取当前播放计划
	public final String GET_CUR_PLAN = SERVER_ADDRESS + "/mobinf/planAction!getPlanList.do";
	public final String GET_BOX_PLAN = SERVER_ADDRESS + "/mobinf/planAction!getTerminalPlan.do";
	//public final String GET_CUR_PLAN = SERVER_ADDRESS + "/mobinf/planAction!getPlayPlan.do";
	//添加播放计划
	public final String ADD_PLAN = SERVER_ADDRESS + "/mobinf/planAction!addPlan.do";
	//修改播放计划
	public final String EDIT_PLAN = SERVER_ADDRESS + "/mobinf/planAction!updatePlan.do";
	//修改播放计划
	public final String ADD_MUSIC_TO_ALBUM = SERVER_ADDRESS + "/mobinf/musicAction!addMusicToMyCategoryOrLike.do";
	public final String PUSH_MUSIC = SERVER_ADDRESS + "/mobinf/terminalMusicAction!pushMusic.do";
	public final String GET_ALL_PLAN_MUSICS = SERVER_ADDRESS + "/mobinf/terminalAction!getAllPlanMusicList.do";
	public final String UPDATE_SPEAKER_INFOR = SERVER_ADDRESS + "mobinf/terminalAction!updateTerminal.do?";
	public final String GET_PUSH_HISTORY_MUSICS = SERVER_ADDRESS + "/mobinf/terminalMusicAction!getPushHistory.do";
	
	public final String PLAN_START = SERVER_ADDRESS + "/mobinf/planAction!usePlan.do?";
	public final String PLAN_DELETE = SERVER_ADDRESS + "/mobinf/planAction!delPlan.do";
	public final String GET_MY_PARENT_MUSIC_CATEGORY = SERVER_ADDRESS + "/mobinf/musicCategoryAction!getMyParentMusicCategory.do";
	
	public final String GET_WX_USER_INFOR = "https://api.weixin.qq.com/sns/userinfo";
	public final String GET_WX_INFOR = "https://api.weixin.qq.com/sns/oauth2/access_token";
}
 
