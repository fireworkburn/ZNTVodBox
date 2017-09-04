package com.znt.diange.utils;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

/**
* @ClassName: MyToast 
* @Description: 实时Toast消息
* @author yan.yu 
* @date 2013-6-8 下午4:12:37 
*
 */
public class MyToast extends View
{
	private  Toast toast = null;
	private static MyToast instance = null;
	
	/**
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param context
	 */
	 
	public MyToast(Context context) 
	{
		super(context);
		// TODO Auto-generated constructor stub
        toast = Toast.makeText(context, "", 0);
        //toast.setGravity(Gravity.BOTTOM, 0, 100);
        //toast.setGravity(Gravity.CENTER, 0, 0);
	}
	
	/**
	* @Description: 获取Toast实例
	* @param @param context
	* @param @return   
	* @return MyToast 
	* @throws
	 */
	public static MyToast getInstance(Context context)
	{
		instance = new MyToast(context);
		return instance;
	}
	
	/**
	* @Description: 显示提示信息
	* @param @param infor
	* @param @param time   
	* @return void 
	* @throws
	 */
	public void show(String infor) 
	{
		if(infor == null)
			infor = "";
		toast.setText(infor);
		toast.show();
	}
	
	public void show(int res) 
	{
		toast.setText(res);
		toast.show();
	}
}
