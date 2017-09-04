package com.znt.vodbox.utils;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MyWebViewClient extends WebViewClient
{
	
	private WebView webView = null;
	
	public MyWebViewClient(WebView webView)
	{
		this.webView = webView;
		
		initWebView();
	}
	
	/*public boolean loadUrl(String url)
	{
		if(url.indexOf("tel:")<0)
	    {
	    	//ҳ���������ֻᵼ�����ӵ绰   
			webView.loadUrl(url);  
		}  
		else
			return false;
		return true;
	}*/
	
	/**
	*callbacks
	*/
	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url)
	{
		 //��д�˷������������ҳ��������ӻ����ڵ�ǰ��webview����ת������������Ǳ�?
		// TODO Auto-generated method stub
		if(url.indexOf("tel:")<0)
	    {
	    	//ҳ���������ֻᵼ�����ӵ绰   
			webView.loadUrl(url);  
		}  
		else
			return false;
		return true;
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onReceivedSslError(WebView view, SslErrorHandler handler,
			SslError error)
	{
		// ��д�˷���������webview����https����
		// TODO Auto-generated method stub
		super.onReceivedSslError(view, handler, error);
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onPageFinished(WebView view, String url)
	{
		// TODO Auto-generated method stub
		super.onPageFinished(view, url);
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon)
	{
		// TODO Auto-generated method stub
		super.onPageStarted(view, url, favicon);
	}
	
	private void initWebView()
	{
		webView.setFocusable(true);
		webView.requestFocus();
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setSupportZoom(true);//��������
		webView.getSettings().setBuiltInZoomControls(true);
		webView.getSettings().setUseWideViewPort(true);//���ô����ԣ�����������������š�?
		webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		webView.setWebViewClient(this);
	}
}

/* 
 * //���ؽ��?
	webView.setWebChromeClient(new WebChromeClient()
    {
    	@Override
    	public void onProgressChanged(WebView view, int newProgress) 
    	{
    	    if(newProgress==100)
    	    {   
    	    	// ����������activity�ı��⣬ Ҳ���Ը���Լ���������һЩ����Ĳ���
    	    	//start.setText("�������?);
    	    }
    	    else
    	    {
    	    	//start.setText("������......." + newProgress);
    	    }
    	}
    });
 * 
 * 
 * �����ؼ��˻���һ��ҳ�棬��û����һ��ҳ��ʱ���˳���ǰactivitiy
 * public boolean onKeyDown(int keyCoder,KeyEvent event)
   	{  
        if(wv.canGoBack() && keyCoder == KeyEvent.KEYCODE_BACK)
        {  
        	//goBack()��ʾ����webView����һҳ��   
        	wv.goBack();   
            return true;  
        }  
        finish();
        return false;  
    }  
 * 
 * 
 * */
