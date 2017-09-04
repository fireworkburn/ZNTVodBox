
package com.znt.diange.activity; 

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.znt.diange.R;
import com.znt.diange.email.EmailSenderManager;
import com.znt.diange.utils.DateUtils;
import com.znt.diange.utils.SystemUtils;
import com.znt.diange.view.EditTextView;

/** 
 * @ClassName: FeedBackActivity 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-4-22 下午5:16:31  
 */
public class FeedBackActivity extends BaseActivity implements OnClickListener
{

	private TextView tvConfirm = null;
	private EditTextView etvContent = null;
	private EditTextView etvContact = null;
	
	private EmailSenderManager emailManager = null;
	
	/**
	*callbacks
	*/
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_feed_back);
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		
		tvConfirm = (TextView)findViewById(R.id.tv_feed_back_confirm);
		etvContent = (EditTextView)findViewById(R.id.et_feed_back_content);
		etvContact = (EditTextView)findViewById(R.id.et_feed_back_contact);
		
		setCenterString("用户反馈");
		
		initViews();
		
		emailManager = new EmailSenderManager();
		
	}
	
	private void initViews()
	{
		tvConfirm.setOnClickListener(this);
		
		etvContent.setLable("反馈信息：");
		etvContent.setHint("十分感谢您的意见与建议，请输入您的反馈信息。");
		etvContent.setInputType(InputType.TYPE_CLASS_TEXT |InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		etvContent.setMaxCount(120);
		etvContent.showTopLine(false);
		etvContent.showBottomLine(false);
		etvContent.setMinLines(6);
		
		etvContact.setLable("联系方式：");
		etvContact.setHint("请输入您的手机或邮箱");
		etvContact.setInputType(InputType.TYPE_CLASS_TEXT |InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		etvContact.setMaxCount(30);
		etvContact.showTopLine(false);
		etvContact.showBottomLine(false);
		etvContact.setMinLines(2);
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		if(v == tvConfirm)
		{
			if(!SystemUtils.isNetConnected(getActivity()))
			{
				showToast("请先连接网络");
				return;
			}
			
			if(SystemUtils.isNetConnected(getActivity())
					&& !TextUtils.isEmpty(SystemUtils.getWifiName(getActivity())))
			{
				String content = etvContent.getText().toString();
				String contact = etvContact.getText().toString();
				if(TextUtils.isEmpty(content))
				{
					showToast("请输入反馈信息");
					return;
				}
				String infor = content;
				if(!TextUtils.isEmpty(contact))
					infor = content + "/n 联系方式：" + contact;
				
				emailManager.sendEmail("人人点歌反馈_" + DateUtils.getStringDate(), infor);
				
				showToast("您的反馈信息已发送~");
				
				finish();
				
			}
			else
				showToast("未联网，请设置您的网络");
			
		}
	}
}
 
