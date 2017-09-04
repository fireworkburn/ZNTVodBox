
package com.znt.vodbox.activity; 

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.znt.vodbox.R;
import com.znt.vodbox.utils.MyWebViewClient;

/** 
 * @ClassName: WebViewActivity 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-11-5 上午10:04:57  
 */
public class WebViewActivity extends BaseActivity implements OnClickListener
{

	private WebView webView = null;
	
	private MyWebViewClient client = null;
	private String webUrl = null;
	private String title = null;
	/**
	*callbacks
	*/
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_webview);
		
		showRightImageView(false);
		setRightText("刷新");
		getRightView().setOnClickListener(this);
		
		webUrl = getIntent().getStringExtra("URL");
		title = getIntent().getStringExtra("TITLE");
		if(TextUtils.isEmpty(title))
			title = "网页";
		
		webView = (WebView)findViewById(R.id.webview);
		
		client = new MyWebViewClient(webView);
		client.shouldOverrideUrlLoading(webView, webUrl);
		//加载进度
		webView.setWebChromeClient(new WebChromeClient()
	    {
	    	@Override
	    	public void onProgressChanged(WebView view, int newProgress) 
	    	{
	    	    if(newProgress==100)
	    	    {   
	    	    	// 这里是设置activity的标题， 也可以根据自己的需求做一些其他的操作
	    	    	setCenterString(title);
	    	    }
	    	    else
	    	    {
	    	    	setCenterString("加载中......." + newProgress + "%");
	    	    }
	    	}
	    });
	}
	
	public boolean onKeyDown(int keyCoder,KeyEvent event)
   	{  
        if(webView.canGoBack() && keyCoder == KeyEvent.KEYCODE_BACK)
        {  
        	//goBack()表示返回webView的上一页面   
        	webView.goBack();   
            return true;  
        }  
        finish();
        return false;  
    }

	/**
	*callbacks
	*/
	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		if(v == getRightView())
		{
			webView.stopLoading();
			client.shouldOverrideUrlLoading(webView, webUrl);
		}
	}  
}
 
