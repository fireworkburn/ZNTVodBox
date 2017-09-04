
package com.znt.vodbox.dialog; 

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.znt.vodbox.R;
import com.znt.vodbox.http.HttpAPI;

/** 
 * @ClassName: MusicPlayDialog 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-7-20 下午3:30:43  
 */
public class AddressInputDialog extends Dialog
{

	private TextView textTitle = null;
	private Button btnLeft = null;
	private Button btnRight = null;
	private EditText etInput = null;
	
	private Activity context = null;
	private boolean isDismissed = false;
	
	private final int PREPARE_FINISH = 0;
	private final int PREPARE_FAIL = 1;
	
	/** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param context 
	*/
	public AddressInputDialog(Activity context)
	{
		super(context, R.style.MMTheme_DataSheet);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

    /** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param context
	* @param themeCustomdialog 
	*/
	public AddressInputDialog(Activity context, int themeCustomdialog)
	{
		super(context, themeCustomdialog);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	public String getContent()
	{
		String content = etInput.getText().toString().trim();
		if(content == null)
			content = "";
		return content;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
    {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.dialog_name_edit);
	    setScreenBrightness();
	    
	    Window window = getWindow();
		window.setWindowAnimations(R.style.AnimAlph);
	    
		btnRight = (Button) AddressInputDialog.this.findViewById(R.id.btn_dialog_name_edit_right);
    	btnLeft = (Button) AddressInputDialog.this.findViewById(R.id.btn_dialog_name_edit_left);
    	textTitle = (TextView) AddressInputDialog.this.findViewById(R.id.tv_dialog_name_edit_title);
    	etInput = (EditText) AddressInputDialog.this.findViewById(R.id.et_name_edit);
	    this.setOnShowListener(new OnShowListener()
	    {
            @Override
            public void onShow(DialogInterface dialog)
            {
            	initViews();
            }
        });
	    this.setOnDismissListener(new OnDismissListener()
		{
			
			@Override
			public void onDismiss(DialogInterface arg0)
			{
				// TODO Auto-generated method stub
				
			}
		});
	    
	}
	
    private void initViews()
    {
        
        setCanceledOnTouchOutside(false);
        
        textTitle.setText("请输入地址");
        etInput.setText("http://192.168.1.122:8080");
        btnLeft.setText("还原");
        btnRight.setText("调试");
        btnLeft.setOnClickListener(new android.view.View.OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				// TODO Auto-generated method stub
				HttpAPI.SERVER_ADDRESS = "http://www.zhunit.com/";
				Toast.makeText(getContext(), "已还原正式模式", 0).show();
				dismiss();
			}
		});
        btnRight.setOnClickListener(new android.view.View.OnClickListener()
        {
        	@Override
        	public void onClick(View arg0)
        	{
        		// TODO Auto-generated method stub
        		String text = etInput.getText().toString().trim();
        		if(!TextUtils.isEmpty(text))
        		{
        			HttpAPI.SERVER_ADDRESS = text;
        			dismiss();
        			Toast.makeText(getContext(), "进入调试模式", 0).show();
        		}
        		else
        		{
        			Toast.makeText(getContext(), "请输入调试地址", 0).show();
        		}
        	}
        });
        
    }
    
    public boolean isDismissed()
    {
    	return isDismissed;
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