
package com.znt.vodbox.activity; 

import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;

import com.znt.vodbox.R;
import com.znt.vodbox.dialog.AddressInputDialog;
import com.znt.vodbox.entity.Constant;

/** 
 * @ClassName: AboutActivity 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-10-22 下午3:00:56  
 */
public class AboutActivity extends BaseActivity
{
	private ImageView ivLogo = null;
	private int hitCount = 0;
	/**
	*callbacks
	*/
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_about);
		
		setCenterString("关于我们");
		
		ivLogo = (ImageView)findViewById(R.id.iv_about_logo);
		
		ivLogo.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				hitCount ++;
				if(hitCount >= 10)
				{
					Constant.isInnerVersion = true;
					showToast("内部版本开启");
				}
				if(hitCount >= 15)
				{
					showCreateAlbumDialog();
					showToast("进入调试模式");
				}
			}
		});
	}
	
	private AddressInputDialog dialog = null;
	private void showCreateAlbumDialog()
	{
		if(dialog == null || dialog.isDismissed())
			dialog = new AddressInputDialog(getActivity());
		//playDialog.updateProgress("00:02:18 / 00:05:12");
		if(dialog.isShowing())
			dialog.dismiss();
		dialog.show();
		
		WindowManager windowManager = getActivity().getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		lp.width = (int)(display.getWidth()); //设置宽度
		lp.height = (int)(display.getHeight()); //设置高度
		dialog.getWindow().setAttributes(lp);
	}
}
 
