
package com.znt.diange.dialog; 

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.znt.diange.R;
import com.znt.diange.entity.LocalDataEntity;

/** 
 * @ClassName: HintDialog 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-10-19 下午1:53:23  
 */
public class SongHintDialog extends Dialog
{

	private View parentView = null;
	private TextView tvCancel = null;
	private CheckBox checkBox = null;
	
	private Context context = null;
	
	/** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param context 
	*/
	public SongHintDialog(Context context)
	{
		super(context, R.style.MMTheme_DataSheet);
		
		this.context = context;
		// TODO Auto-generated constructor stub
	}
	
	/**
	*callbacks
	*/
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.dialog_song_hint);
		
		parentView = findViewById(R.id.view_hint_dialog_bg);
		tvCancel = (TextView)findViewById(R.id.tv_dialog_hint_cancel);
		checkBox = (CheckBox)findViewById(R.id.cb_dialog_hint);
		
		tvCancel.setOnClickListener(new android.view.View.OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				dismiss();
			}
		});
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton v, boolean arg1)
			{
				// TODO Auto-generated method stub
				LocalDataEntity.newInstance(context).setSongHintShow(!v.isChecked());
			}
		});
	}
	
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
 
