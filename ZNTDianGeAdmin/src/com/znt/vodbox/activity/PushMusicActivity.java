/*  
* @Project: ZNTVodBox 
* @User: Administrator 
* @Description: 家庭音乐
* @Author： yan.yu
* @Company：http://www.zhunit.com/
* @Date 2016-7-5 下午11:02:49 
* @Version V1.1   
*/ 

package com.znt.vodbox.activity; 

import android.os.Bundle;

import com.znt.vodbox.R;

/** 
 * @ClassName: PushMusicActivity 
 * @Description: TODO
 * @author yan.yu 
 * @date 2016-7-5 下午11:02:49  
 */
public class PushMusicActivity extends BaseActivity
{

	
	/**
	*callbacks
	*/
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_push_music);
		
		setCenterString("自助点歌");
		
		
	}
	
}
 
