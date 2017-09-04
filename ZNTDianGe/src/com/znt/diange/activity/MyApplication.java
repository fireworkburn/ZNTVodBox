
package com.znt.diange.activity; 

import android.app.Application;

import com.znt.diange.exception.MyExceptionHandler;
import com.znt.diange.utils.SystemUtils;

/** 
 * @ClassName: MyApplication 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-7-18 上午11:27:21  
 */
public class MyApplication extends Application
{

	public static boolean isLogin = false;
	
	private static MyApplication mInstance = null;
	
	/**
	*callbacks
	*/
	@Override
	public void onCreate()
	{
		// TODO Auto-generated method stub
		super.onCreate();
		
		MyExceptionHandler excetionHandler = MyExceptionHandler.getInstance();
		excetionHandler.init(getApplicationContext());
		
		int size[] = SystemUtils.getScreenSize(getApplicationContext());
		/*ScreenSize.WIDTH = StringUtils.px2dip(getApplicationContext(), size[0]);
		ScreenSize.HEIGHT = StringUtils.px2dip(getApplicationContext(), size[1]);;
		ScreenSize.SCALE = ScreenSize.HEIGHT * 1.0 / ScreenSize.WIDTH;*/
		
		mInstance = this;
		
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onTerminate()
	{
		// TODO Auto-generated method stub
		super.onTerminate();
		 /*for (Activity activity : activities) 
		 {  
	         activity.finish();  
		 }  */
	    System.exit(0);  
	}
	
	public static MyApplication getInstance()
	{
		return mInstance;
	}
}
 
