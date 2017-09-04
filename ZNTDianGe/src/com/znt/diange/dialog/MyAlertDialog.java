package com.znt.diange.dialog;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.znt.diange.R;

/** 
 * @ClassName: MyAlertDialog 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-9-18 下午3:16:04  
 */
public class MyAlertDialog extends Dialog
{

	private View parentView = null;
	private TextView textTitle = null;
	private TextView textInfor = null;
	private Button btnLeft = null;
	private Button btnRight = null;
	private String title = null;
	private String message = null;
	private android.view.View.OnClickListener listener = null;
	
	private Context context = null;
	private boolean isDismissed = false;
	
	/** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param context 
	*/
	public MyAlertDialog(Context context)
	{
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

    /** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param context
	* @param themeCustomdialog 
	*/
	public MyAlertDialog(Context context, int themeCustomdialog)
	{
		super(context, themeCustomdialog);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) 
    {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.dialog_my_alert);
	    setScreenBrightness();
	    
	    Window window = getWindow();
		window.setWindowAnimations(R.style.AnimAlph);
	    
		parentView = MyAlertDialog.this.findViewById(R.id.my_alert_dialog_bg);
		btnRight = (Button) MyAlertDialog.this.findViewById(R.id.btn_my_alert_right);
    	btnLeft = (Button) MyAlertDialog.this.findViewById(R.id.btn_my_alert_left);
    	textTitle = (TextView) MyAlertDialog.this.findViewById(R.id.tv_my_alert_title);
        textInfor = (TextView) MyAlertDialog.this.findViewById(R.id.tv_my_alert_infor);
		
        //ViewUtils.setViewParams(context, parentView, context.getResources().getDimension(R.dimen.dialog_width), 0);
        //ViewUtils.setViewParams(context, parentView, 300, 0);
        
	    this.setOnShowListener(new OnShowListener()
	    {
            @Override
            public void onShow(DialogInterface dialog)
            {
            	initViews();
            }
        });
	}
    
    private void initViews()
    {
        
        setCanceledOnTouchOutside(false);
        
        textTitle.setText(title);
        textInfor.setText(message);
        
        if(listener != null)
        	btnRight.setOnClickListener(listener);
        
        btnLeft.setOnClickListener(new android.view.View.OnClickListener()
		{
			
			@Override
			public void onClick(View arg0)
			{
				// TODO Auto-generated method stub
				isDismissed = true;
				dismiss();
			}
		});
    }
    
    public boolean isDismissed()
    {
    	return isDismissed;
    }
    
    public void setOnClickListener(android.view.View.OnClickListener listener)
    {
    	this.listener  = listener;
    }
    
    public void setInfor(String title, String infor)
    {
    	this.title = title;
    	this.message = infor;
    }
    
    public Button getLeftButton()
    {
    	return btnLeft;
    }
    public Button getRightButton()
    {
    	return btnRight;
    }
    
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