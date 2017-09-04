package com.znt.speaker.center;

import org.apache.mina.core.session.IoSession;
import org.json.JSONObject;


public interface IDMRAction 
{
	public void recvRegister(IoSession session, JSONObject obj);
	public void recvGetPlayList(IoSession session, JSONObject obj);
	public void recvGetPlayRecord(IoSession session, JSONObject obj);
	public void recvPlay(IoSession session, JSONObject obj);
	public void recvGetPlayInfor(IoSession session, JSONObject obj);
	public void recvDeleteSong(IoSession session, JSONObject obj);
	public void recvStopSong(IoSession session, JSONObject obj);
	public void recvSetDevice(IoSession session, JSONObject obj);
	public void recvSetVolume(IoSession session, JSONObject obj);
	public void recvGetVolume(IoSession session, JSONObject obj);
	public void recvGetPlayState(IoSession session, JSONObject obj);
	public void recvSetPlayState(IoSession session, JSONObject obj);
	public void recvPlayNext(IoSession session, JSONObject obj);
	public void recvSpeakerMusic(IoSession session, JSONObject obj);
	public void recvSystemUpdate(IoSession session, JSONObject obj);
	public void recvPlayPermission(IoSession session, JSONObject obj);
	public void recvSetPlayRes(IoSession session, JSONObject obj);
	public void recvGetPlayRes(IoSession session, JSONObject obj);
	public void recvResUpdate(IoSession session, JSONObject obj);
	public void recvGetPlayPermission(IoSession session, JSONObject obj);
	public void recvGetWifiList(IoSession session, JSONObject obj);
}

