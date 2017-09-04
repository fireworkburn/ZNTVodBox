package com.znt.speaker.http.callback;

import com.znt.speaker.entity.CurPlanInfor;

public interface IGetCurPllanCallBack 
{
	public void requestStart(int requestId);
	public void requestFail(int requestId);
	public void requestSuccess(CurPlanInfor curPlanInfor, int requestId);
	
}
