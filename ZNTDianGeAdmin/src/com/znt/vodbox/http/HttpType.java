package com.znt.vodbox.http;

public enum HttpType
{
	CheckUpdate,//检查升级
	UserConnetCount,//统计
	Login,//登陆
	Logout,//注销
	LoginByToken,//登陆
	Register,
	NewPwdByPhone,
	UserInforEdit,
	ResetPwd,
	AdminApply,
	
	CoinUpload,//上传金币
	CoinGet,//获取金币
	CoinRemove,//扣除金币
	CoinFreeze,//冻结金币
	CoinFreezeCancel,//冻结金币
	
	AddMusicToSpeaker,//添加音乐到音响
	DeleteSpeakerMusic,//删除音响音乐
	GetSpeakerMusic,//获取音响音乐
	GetPlanMusics,//获取音响音乐
	GetBindSpeakers,//获取绑定的音响列表
	GetMusicAlbums,//
	GetParentMusicAlbums,//
	GetCreateAlbums,//
	GetSystemAlbums,//
	GetAlbumMusics,//
	CreateAlbum,//
	EditAlbum,//
	DeleteAlbum,//
	DeleteAlbumMusic,//
	DeleteAlbumMusics,//
	GetCurPlan,//
	GetBoxPlan,//
	AddPlan,
	EditPlan,
	AddMusicToAlbum,
	AddSysMusicToAlbum,
	CollectAlbum,
	GetAllPlanMusic,
	GetPushHostoryMusic,
	PushMusic,
	UpdateSpeakerInfor,
	StartOldPlan,
	DeleteOldPlan,
	
	GetAllSpeaker,
	GetNearBySpeaker,
	GetSecondLevels,
	GetSpeakerInfor,
	GetWXInfor
	
}
