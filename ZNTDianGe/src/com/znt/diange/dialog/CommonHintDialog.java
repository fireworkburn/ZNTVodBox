
package com.znt.diange.dialog; 

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.znt.diange.R;

public class CommonHintDialog extends Dialog 
{

	private TextView tvVonfirm = null;
	private TextView tvTitle = null;
	private TextView tvInfor = null;
	
	private String title = null;
	private String hintInfor = null;
	
	/** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param context 
	*/
	public CommonHintDialog(Context context)
	{
		super(context, R.style.MMTheme_DataSheet);
		// TODO Auto-generated constructor stub
	}

    @Override
	protected void onCreate(Bundle savedInstanceState) 
    {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.dialog_common_hint);
	    setScreenBrightness();
	    
	    Window window = getWindow();
		window.setWindowAnimations(R.style.AnimAlph);
	    
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
    	tvVonfirm = (TextView) this.findViewById(R.id.tv_common_hint_confirm);
    	tvTitle = (TextView) this.findViewById(R.id.tv_common_hint_title);
    	tvInfor = (TextView) this.findViewById(R.id.tv_common_hint_infor);
        
        setCanceledOnTouchOutside(false);
        
        tvVonfirm.setOnClickListener(new android.view.View.OnClickListener()
        {
        	@Override
        	public void onClick(View arg0)
        	{
        		// TODO Auto-generated method stub
        		dismiss();
        	}
        });
        
        if(TextUtils.isEmpty(title))
        	title = "提示";
        if(TextUtils.isEmpty(hintInfor))
        	hintInfor = "";
        
        tvTitle.setText(title);
        tvInfor.setText(hintInfor);
        
    }
    
    public void setTitle(String title)
    {
    	this.title = title;
    }
    public void setInfor(String hintInfor)
    {
    	this.hintInfor = hintInfor;
    }
    
    private void setScreenBrightness() 
    {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.dimAmount = 0;
        window.setAttributes(lp);
    }
}