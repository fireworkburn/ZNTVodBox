
package com.znt.speaker.dialog; 

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.znt.speaker.R;

/** 
 * @ClassName: MyDialog 
 * @Description: TODO
 * @author yan.yu 
 * @date 2013-7-12 上午11:39:59  
 */
public class MyProgressDialog extends ProgressDialog 
{

	private TextView textTitle = null;
	private TextView textInfor = null;
	private ImageView imageView = null;
	private String title = null;
	private String message = null;
	
	/** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param context 
	*/
	public MyProgressDialog(Context context, int theme)
	{
		super(context, theme);
		// TODO Auto-generated constructor stub
	}

    @Override
	protected void onCreate(Bundle savedInstanceState) 
    {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.progress_dialog);
	    setScreenBrightness();
	    
	    this.setOnShowListener(new OnShowListener()
	    {
            @Override
            public void onShow(DialogInterface dialog)
            {
            	initViews();
        	    startAnim();
            }
        });
	}
    
    /**
	*callbacks
	*/
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0)
        {
			return true;
        }
        else
        {
        	return false;
        }
		// TODO Auto-generated method stub
	}
    
    private void initViews()
    {
    	imageView = (ImageView) MyProgressDialog.this.findViewById(R.id.iv_progress_dialog_img);
    	textTitle = (TextView) MyProgressDialog.this.findViewById(R.id.tv_progress_dialog_title);
        textInfor = (TextView) MyProgressDialog.this.findViewById(R.id.tv_progress_dialog_infor);
        
        setCanceledOnTouchOutside(false);
        
        textTitle.setText(title);
        textInfor.setText(message);
    }
    private void startAnim()
    {
    	Animation anim = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setRepeatCount(Animation.INFINITE); // 设置INFINITE，对应值-1，代表重复次数为无穷次
        anim.setDuration(1000);                  // 设置该动画的持续时间，毫秒单位
        anim.setInterpolator(new LinearInterpolator());	// 设置一个插入器，或叫补间器，用于完成从动画的一个起始到结束中间的补间部分
        imageView.startAnimation(anim);
    }
    
    public void setInfor(String title, String infor)
    {
    	/*setTitle(title);
    	setInfor(infor);*/
    	this.title = title;
    	this.message = infor;
    	if(textTitle != null)
    		textTitle.setText(title);
    	if(textInfor != null)
    		textInfor.setText(message);
    }
   /* public void showDialog(int title, int infor)
    {
    	setTitle(title);
    	setInfor(infor);
    	show();
    }

    public void setTitle(String text)
    {
    	textTitle.setText(text);
    }
    public void setTitle(int text)
    {
    	textTitle.setText(text);
    }
    
    public void setInfor(String text)
    {
    	textInfor.setText(text);
    }
    public void setInfor(int text)
    {
    	textInfor.setText(text);
    }*/
    
    private void setScreenBrightness() 
    {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        /**
        *  此处设置亮度值。dimAmount代表黑暗数量，也就是昏暗的多少，设置为0则代表完全明亮。
        *  范围是0.0到1.0
        */
        lp.dimAmount = 0;
        window.setAttributes(lp);
    }
}