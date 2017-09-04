
package com.znt.diange.activity; 

import android.os.Bundle;

import com.znt.diange.R;

/** 
 * @ClassName: AboutActivity 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-10-22 下午3:00:56  
 */
public class AboutActivity extends BaseActivity
{
	
	/**
	*callbacks
	*/
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_about);
		
		setCenterString("关于我们");
	}
}
 
