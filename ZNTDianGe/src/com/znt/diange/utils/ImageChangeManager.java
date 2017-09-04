
package com.znt.diange.utils; 

import java.util.List;

import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.znt.diange.R;

/** 
 * @ClassName: ImageChangeManager 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-10-10 下午2:27:51  
 */
public class ImageChangeManager
{
	private Activity activity = null;
	private Animation operatingAnim = null;
	
	private List<String> imgList = null;
	private ImageView imageView = null;
	private int position = 0;
	private int time = 0;
	
	public ImageChangeManager(Activity activity, ImageView imageView)
	{
		this.imageView = imageView;
		this.activity = activity;
		
		operatingAnim = AnimationUtils.loadAnimation(activity, R.anim.rotate_indefinitely); 
	}
	
	public void startChange()
	{
		handler.postDelayed(task, 1000);
		startAnim();
	}
	public void stopChange()
	{
		time = 0;
		handler.removeCallbacks(task);
		stopAnim();
	}
	
	public void setImageList(List<String> imgList)
	{
		this.imgList = imgList;
		showImage();
	}
	
	private void startAnim()
	{
		LinearInterpolator lin = new LinearInterpolator(); 
		operatingAnim.setInterpolator(lin); 
		imageView.startAnimation(operatingAnim);
	}
	private void stopAnim()
	{
		imageView.clearAnimation();
	}
	
	private void showImage()
	{
		if(imgList == null || imgList.size() == 0)
		{
			imageView.setVisibility(View.GONE);
			return;
		}
		imageView.setVisibility(View.VISIBLE);
		
		if(position >= imgList.size())
		{
			position = 0;
		}
		Picasso.with(activity).load(imgList.get(position)).into(imageView);
		
		position ++;
	}
	
	private Handler handler = new Handler();
	Runnable task = new Runnable()
	{
		@Override
		public void run()
		{
			// TODO Auto-generated method stub
			showImage();
			handler.postDelayed(task, 30000);
		}
	};
}
 
