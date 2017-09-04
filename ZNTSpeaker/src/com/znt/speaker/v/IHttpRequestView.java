package com.znt.speaker.v;

public interface IHttpRequestView 
{
	public void requestStart(int requestId);
	public void requestError(int requestId);
	public void requestSuccess(Object obj, int requestId);
	public void requestNetWorkError();
}
