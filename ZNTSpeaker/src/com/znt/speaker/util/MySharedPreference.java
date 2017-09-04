package com.znt.speaker.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
* @ClassName: MySharedPreference 
* @Description: 本地数据操作类
* @author yan.yu 
* @date 2013-6-8 下午2:18:05 
 */
public class MySharedPreference
{

	private SharedPreferences sp = null;
	private Context context = null;
	private Editor edit = null;
	
	/**
	* <p>Title: </p> 
	* <p>Description: 构造函数
	* @param context 上下文
	* @param sharedPreferencesName 文件名字,默认为 
	 */
	public MySharedPreference(Context context, String sharedPreferencesName)
	{
		this.context = context;
		if((sharedPreferencesName == null) || (sharedPreferencesName.length() <= 0))
			sharedPreferencesName = "wifi_speaker";
		sp = context.getSharedPreferences(sharedPreferencesName, context.MODE_PRIVATE);
		edit = sp.edit();
	}
	
	public static MySharedPreference newInstance(Context context)
	{
		return new MySharedPreference(context, null);
	}
	
	
	/**
	* @Description: 存储数据
	* @param @param key
	* @param @param value   
	* @return void 
	* @throws
	 */
	public void setData(String key, Object value)
	{
		if(value instanceof String)
			edit.putString(key, (String)value);
		else if(value instanceof Boolean)
			edit.putBoolean(key, (Boolean)value);
		else if(value instanceof Long)
			edit.putLong(key, (Long)value);
		else if(value instanceof Integer)
			edit.putInt(key, (Integer)value);
		/*else if(value instanceof Float)
			edit.putFloat(key, (Float)value);*/
		edit.commit();
	}
	
	/**
	* @Description: 获取数据
	* @param @param key
	* @param @param defValue
	* @param @return   
	* @return String , boolean int
	* @throws
	 */
	public String getData(String key, String defValue)
	{
		return sp.getString(key, defValue);
	}
	public boolean getData(String key, boolean defValue)
	{
		return sp.getBoolean(key, defValue);
	}
	public int getData(String key, int defValue)
	{
		return sp.getInt(key, 0);
	}
	public long getDataLong(String key, long defValue)
	{
		return sp.getLong(key, defValue);
	}
}
