package com.znt.vodbox.view;

import android.content.Context;
import android.graphics.PointF;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ChildViewPager extends ViewPager
{
   
	private PointF downP = new PointF();
	private  PointF curP = new PointF();
	private float xDistance = 0;
	private float yDistance = 0;
	private int childCount = 0;
	private int index = 0;
	private int updateTime = 0;
	private int changeTime = 2000;
	private OnSingleTouchListener onSingleTouchListener;
 
    public ChildViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }
 
    public ChildViewPager(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }
 
    /*@Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        // TODO Auto-generated method stub
        //当拦截触摸事件到达此位置的时候，返回true，
        //说明将onTouch拦截在此控件，进而执行此控件的onTouchEvent
        return true;
    }*/
 
    public void setTotalPageNum(int childCount)
    {
    	this.childCount = childCount;
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent arg0) 
    {
        // TODO Auto-generated method stub
        //每次进行onTouch事件都记录当前的按下的坐标
        curP.x = arg0.getX();
        curP.y = arg0.getY();
        
        updateTime = 0;
        
        if(arg0.getAction() == MotionEvent.ACTION_DOWN)
        {
            //记录按下时候的坐标
            //切记不可用 downP = curP ，这样在改变curP的时候，downP也会改变
            downP.x = arg0.getX();
            downP.y = arg0.getY();
            //此句代码是为了通知他的父ViewPager现在进行的是本控件的操作，不要对我的操作进行干扰
            getParent().requestDisallowInterceptTouchEvent(true);
        }
 
        if(arg0.getAction() == MotionEvent.ACTION_MOVE)
        {
            
        	xDistance = Math.abs(Math.abs(curP.x) - Math.abs(downP.x));//X轴上的滑动距离
        	yDistance = Math.abs(Math.abs(curP.y) - Math.abs(downP.y));//Y轴上的滑动距离
        	
        	if(xDistance > yDistance)//左右滑动
        	{
        		if((Math.abs(curP.x) <= Math.abs(downP.x)))//向左滑动
            	{
            		if(getCurrentItem() == childCount-1)//最后一页
            			getParent().requestDisallowInterceptTouchEvent(false);
            	}
            	else//不是最后一页，并且是向右侧滑动
            	{
            		//此句代码是为了通知他的父ViewPager现在进行的是本控件的操作，不要对我的操作进行干扰
            		getParent().requestDisallowInterceptTouchEvent(true);
            	}
        	}
        	else//上下滑动，释放触摸事件
        		getParent().requestDisallowInterceptTouchEvent(true);
        }
 
        if(arg0.getAction() == MotionEvent.ACTION_UP)
        {
            //在up时判断是否按下和松手的坐标为一个点
            //如果是一个点，将执行点击事件，这是我自己写的点击事件，而不是onclick
            if(downP.x==curP.x && downP.y==curP.y)
            {
                onSingleTouch();
                return true;
            }
        }
 
        return super.onTouchEvent(arg0);
    }
 
       
    public void onSingleTouch()
    {
        if (onSingleTouchListener!= null) 
        {
 
            onSingleTouchListener.onSingleTouch();
        }
    }
 
   
    public interface OnSingleTouchListener 
    {
        public void onSingleTouch();
    }
 
    public void setOnSingleTouchListener(OnSingleTouchListener onSingleTouchListener) 
    {
        this.onSingleTouchListener = onSingleTouchListener;
    }
    
    public void startAutoScroll()
    {
    	index = getCurrentItem();
    	updateTime = 0;
    	handler.postDelayed(task, changeTime);
    }
    public void stopAutoScroll()
    {
    	updateTime = 0;
    	handler.removeCallbacks(task);
    }
    
    Handler handler = new Handler();
    Runnable task = new Runnable() 
	{
		@Override
		public void run() 
		{
			// TODO Auto-generated method stub
			
			updateTime++;
			
			if(updateTime >= 2)
			{
				updateTime = 0;
				++ index ;
				if(index >= childCount)
					index = 0;
				setCurrentItem(index);
			}
			
			handler.postDelayed(task, changeTime);//5秒更新一次
		}
	};
}