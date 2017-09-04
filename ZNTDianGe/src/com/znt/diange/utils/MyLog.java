package com.znt.diange.utils;

import android.content.Context;
import android.util.Log;
/**
 * 
* @ClassName: MyLog 
* @Description: 打印信息
* @author yan.yu 
* @date 2013-6-8 下午1:50:57 
*
 */
public class MyLog
{
	private static String TAG = "MyLog";
	private static boolean isOpen = true;
	
	/**
	* @Description: 打开log信息
	* @param    无
	* @return void   无
	* @throws
	 */
	public static void enable()
	{
		isOpen = true;
	}
	/**
	* @Description: 关闭log信息
	* @param    
	* @return void 
	* @throws
	 */
	public static void disable()
	{
		isOpen = false;
	}
	
	/**
	* @Description: error信息，级别最高
	* @param @param context
	* @param @param msg   
	* @return void 
	* @throws
	 */
	public static void e(Context context, Object msg)
	{
		if(isOpen)
			Log.e(getClassName(context.getClass().toString()), msg.toString());
	}
	public static void e(Class<?> cls, Object msg)
	{
		if(isOpen)
			Log.e(getClassName(cls.toString()), msg.toString());
	}
	public static void e(String tag, Object msg)
	{
		if(isOpen)
			Log.e(tag, msg.toString());
	}
	public static void e(Object msg)
	{
		if(isOpen)
			Log.e(TAG, msg.toString());
	}
	
	/**
	* @Description: system打印信息
	* @param @param obj   
	* @return void 
	* @throws
	 */
	public static void print(Object obj)
	{
		if(isOpen)
			System.out.print(obj);
	}
	
	/**
	* @Description: debug信息
	* @param @param context
	* @param @param msg   
	* @return void 
	* @throws
	 */
	public static void d(Context context, Object msg)
	{
		if(isOpen)
			Log.d(getClassName(context.getClass().toString()), msg.toString());
	}
	public static void d(Class<?> cls, Object msg)
	{
		if(isOpen)
			Log.d(getClassName(cls.toString()), msg.toString());
	}
	public static void d(String tag, Object msg)
	{
		if(isOpen)
			Log.d(tag, msg.toString());
	}
	public static void d(Object msg)
	{
		if(isOpen)
			Log.d(TAG, msg.toString());
	}
	
	/**
	* @Description: info信息
	* @param @param context
	* @param @param msg   
	* @return void 
	* @throws
	 */
	public static void i(Context context, Object msg)
	{
		if(isOpen)
			Log.i(getClassName(context.getClass().toString()), msg.toString());
	}
	public static void i(Class<?> cls, Object msg)
	{
		if(isOpen)
			Log.i(getClassName(cls.toString()), msg.toString());
	}
	public static void i(String tag, Object msg)
	{
		if(isOpen)
			Log.i(tag, msg.toString());
	}
	public static void i(Object msg)
	{
		if(isOpen)
			Log.i(TAG, msg.toString());
	}
	
	public static String getClassName(String result)
	{
		int start = result.lastIndexOf(".");
		result = result.substring(start);
		return result;
	}
}
