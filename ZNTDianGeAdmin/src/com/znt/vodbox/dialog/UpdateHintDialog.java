
package com.znt.vodbox.dialog; 

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.znt.vodbox.R;

/** 
 * @ClassName: HintDialog 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-10-19 下午1:53:23  
 */
public class UpdateHintDialog extends Dialog
{

	private View parentView = null;
	private TextView tvInfor = null;
	private TextView tvConfirm = null;
	
	private Context context = null;
	
	/** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param context 
	*/
	public UpdateHintDialog(Context context)
	{
		super(context, R.style.MMTheme_DataSheet);
		
		this.context = context;
		// TODO Auto-generated constructor stub
	}
	
	public interface OnButtonClickListener
	{
		public void onButtonCLicked();
	}
	
	private OnButtonClickListener listsner = null;
	
	/**
	*callbacks
	*/
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.dialog_update_hint);
		
		parentView = findViewById(R.id.view_hint_dialog_bg);
		tvInfor = (TextView)findViewById(R.id.tv_dialog_update_hint_infor);
		tvConfirm = (TextView)findViewById(R.id.tv_dialog_update_hint_confirm);
		
		tvConfirm.setOnClickListener(new android.view.View.OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				/*if(listsner != null)
					listsner.onButtonCLicked();*/
				dismiss();
			}
		});
	}
	
	public void setInfor(String text)
	{
		if(TextUtils.isEmpty(text))
			text = "";
		tvInfor.setText(text);
	}
	
	/*public void OnButtonClickListener(OnButtonClickListener listsner)
	{
		this.listsner = listsner;
	}*/
	
	/**
	*callbacks
	*/
	@Override
	public void onBackPressed()
	{
		// TODO Auto-generated method stub
		//super.onBackPressed();
	}
}
 
