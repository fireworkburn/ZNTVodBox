package com.znt.vodbox.factory;

import java.util.List;
import java.util.Stack;

import com.znt.diange.mina.entity.MediaInfor;
import com.znt.vodbox.dlna.mediaserver.util.CommonLog;
import com.znt.vodbox.dlna.mediaserver.util.LogFactory;

public class ContentManager {

	private static final CommonLog log = LogFactory.createLog();
	
	private static ContentManager mInstance = null;
	
	private Stack<List<MediaInfor>> mStack;
	
	public synchronized static ContentManager getInstance(){
		if (mInstance == null){
			mInstance =  new ContentManager();
		}
		
		return mInstance;
	}
	
	private ContentManager()
	{
		mStack = new Stack<List<MediaInfor>>();
	}
	
	public void pushListItem(List<MediaInfor> dataList)
	{
		if (dataList != null)
		{
			log.e("mStack.add data.size = " + dataList.size());
			mStack.add(dataList);
			int t = mStack.size();
		}
	}
	
	public void updateFirst(List<MediaInfor> dataList)
	{
		if (dataList != null && dataList.size() > 0)
		{
			mStack.set(0, dataList);
		}
		
	}
	
	public List<MediaInfor> peekListItem()
	{
		if (mStack.empty()){
			return null;
		}
		
		List<MediaInfor> tempList = mStack.peek();
		int s = mStack.size();
		return tempList;
	}
	
	public List<MediaInfor> popListItem()
	{
		if (mStack.empty()){
			return null;
		}
		
		List<MediaInfor> tempList = mStack.pop();
		
		return tempList;
	}
	
	public int getSize()
	{
		return mStack.size();
	}
	
	public void clear()
	{
		mStack.clear();
	}
	
	
}
