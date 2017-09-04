package com.znt.diange.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;

import com.znt.diange.R;

public class ClearEditText extends EditText implements  
        OnFocusChangeListener, TextWatcher { 
	/**
	 * 删锟斤拷钮锟斤拷锟斤拷锟斤拷
	 */
    private Drawable mClearDrawable; 
 
    public ClearEditText(Context context) { 
    	this(context, null); 
    } 
 
    public ClearEditText(Context context, AttributeSet attrs) { 
    	//锟斤拷锟斤构锟届方锟斤拷也锟斤拷锟斤拷要锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷芏锟斤拷锟斤拷圆锟斤拷锟斤拷锟�ML锟斤拷锟芥定锟斤拷
    	this(context, attrs, android.R.attr.editTextStyle); 
    } 
    
    public ClearEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    
    
    private void init() { 
    	//锟斤拷取EditText锟斤拷DrawableRight,锟斤拷锟斤拷没锟斤拷锟斤拷锟斤拷锟斤拷锟角撅拷使锟斤拷默锟较碉拷图片
    	mClearDrawable = getCompoundDrawables()[2]; 
        if (mClearDrawable == null) { 
        	mClearDrawable = getResources() 
                    .getDrawable(R.drawable.emotionstore_progresscancelbtn); 
        } 
        mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(), mClearDrawable.getIntrinsicHeight()); 
        setClearIconVisible(false); 
        setOnFocusChangeListener(this); 
        addTextChangedListener(this); 
    } 
 
 
    /**
     * 锟斤拷为锟斤拷锟角诧拷锟斤拷直锟接革拷EditText锟斤拷锟矫碉拷锟斤拷录锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷眉锟阶★拷锟斤拷前锟斤拷碌锟轿伙拷锟斤拷锟侥ｏ拷锟斤拷锟斤拷录锟�
     * 锟斤拷锟斤拷锟角帮拷锟铰碉拷位锟斤拷 锟斤拷  EditText锟侥匡拷锟�- 图锟疥到锟截硷拷锟揭边的硷拷锟�- 图锟斤拷目锟斤拷  锟斤拷
     * EditText锟侥匡拷锟�- 图锟疥到锟截硷拷锟揭边的硷拷锟街�拷锟斤拷锟斤拷蔷锟斤拷锟斤拷锟斤拷锟酵硷拷辏�拷锟街憋拷锟斤拷锟矫伙拷锌锟斤拷锟�
     */
    @Override 
    public boolean onTouchEvent(MotionEvent event) { 
        if (getCompoundDrawables()[2] != null) { 
            if (event.getAction() == MotionEvent.ACTION_UP) { 
            	boolean touchable = event.getX() > (getWidth() 
                        - getPaddingRight() - mClearDrawable.getIntrinsicWidth()) 
                        && (event.getX() < ((getWidth() - getPaddingRight())));
                if (touchable) { 
                    this.setText(""); 
                } 
            } 
        } 
 
        return super.onTouchEvent(event); 
    } 
 
    /**
     * 锟斤拷ClearEditText锟斤拷锟姐发锟斤拷浠�拷锟绞憋拷锟斤拷卸锟斤拷锟斤拷锟斤拷址锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷图锟斤拷锟斤拷锟绞撅拷锟斤拷锟斤拷锟�
     */
    @Override 
    public void onFocusChange(View v, boolean hasFocus) { 
        if (hasFocus) { 
            setClearIconVisible(getText().length() > 0); 
        } else { 
            setClearIconVisible(false); 
        } 
    } 
 
 
    /**
     * 锟斤拷锟斤拷锟斤拷锟酵硷拷锟斤拷锟斤拷示锟斤拷锟斤拷锟截ｏ拷锟斤拷锟斤拷setCompoundDrawables为EditText锟斤拷锟斤拷锟斤拷去
     * @param visible
     */
    protected void setClearIconVisible(boolean visible) { 
        Drawable right = visible ? mClearDrawable : null; 
        setCompoundDrawables(getCompoundDrawables()[0], 
                getCompoundDrawables()[1], right, getCompoundDrawables()[3]); 
    } 
     
    
    /**
     * 锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷莘锟斤拷锟戒化锟斤拷时锟斤拷氐锟斤拷姆锟斤拷锟�
     */
    @Override 
    public void onTextChanged(CharSequence s, int start, int count, 
            int after) { 
        setClearIconVisible(s.length() > 0); 
    } 
 
    @Override 
    public void beforeTextChanged(CharSequence s, int start, int count, 
            int after) { 
         
    } 
 
    @Override 
    public void afterTextChanged(Editable s) { 
         
    } 
    
   
    /**
     * 锟斤拷锟矫晃讹拷锟斤拷锟斤拷
     */
    public void setShakeAnimation(){
    	this.setAnimation(shakeAnimation(5));
    }
    
    
    /**
     * 锟轿讹拷锟斤拷锟斤拷
     * @param counts 1锟斤拷锟接晃讹拷锟斤拷锟斤拷锟斤拷
     * @return
     */
    public static Animation shakeAnimation(int counts){
    	Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
    	translateAnimation.setInterpolator(new CycleInterpolator(counts));
    	translateAnimation.setDuration(1000);
    	return translateAnimation;
    }
 
 
}
