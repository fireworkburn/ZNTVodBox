
package com.znt.diange.wxapi; 
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendAuth;
import com.tencent.mm.sdk.openapi.SendAuth.Resp;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.znt.diange.entity.Constant;
import com.znt.diange.entity.LocalDataEntity;
import com.znt.diange.entity.WXInfor;
import com.znt.diange.http.HttpHelper;
import com.znt.diange.http.HttpMsg;
import com.znt.diange.http.HttpType;

/** 
 * @ClassName: WXEntryActivity 
 * @Description: TODO
 * @author yan.yu 
 * @date 2016-1-25 下午5:35:25  
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler
{
	private IWXAPI api;  
	
	private String nickName = "";
	private String headUrl = "";
	
	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == HttpMsg.LOGIN_START)
			{
				
			}
			else if(msg.what == HttpMsg.LOGIN_FAIL)
			{
				
				String error = (String)msg.obj;
				if(TextUtils.isEmpty(error))
					error = "登陆失败";
				Toast.makeText(WXEntryActivity.this, error, 0).show();
				finish();
			}
			else if(msg.what == HttpMsg.LOGIN_SUCCESS)
			{
				
				if(!TextUtils.isEmpty(nickName))
				{
					LocalDataEntity.newInstance(WXEntryActivity.this).setUserName(nickName);
				}
				if(!TextUtils.isEmpty(headUrl))
				{
					//LocalDataEntity.newInstance(WXEntryActivity.this).setUserHead(headUrl);
				}
				
				setResult(RESULT_OK);
				finish();
			}
			else if(msg.what == HttpMsg.GET_WX_INFOR_START)
			{
				
			}
			else if(msg.what == HttpMsg.GET_WX_INFOR_SUCCESS)
			{
				WXInfor wxInfo = (WXInfor)msg.obj;
				nickName = wxInfo.nickname;
				headUrl = wxInfo.headimgurl;
				//StringUtils.md5(uid);
				//loginByToken(wxInfo.unionid);
				Toast.makeText(WXEntryActivity.this, "微信信息-->" + nickName, 0).show();
			}
			else if(msg.what == HttpMsg.GET_WX_INFOR_FAIL)
			{
				Toast.makeText(WXEntryActivity.this, "获取微信信息失败", 0).show();
				finish();
			}
		};
	};
	
    @Override  
    protected void onCreate(Bundle savedInstanceState) 
    {  
        // TODO Auto-generated method stub  
        super.onCreate(savedInstanceState);  
        //setContentView(R.layout.flash_activity);
        api = WXAPIFactory.createWXAPI(this, Constant.WX_APP_ID, false);  
        api.handleIntent(getIntent(), this);
    }  
    
    private void getWXInfor(String code)
	{
		HttpHelper httpHelper = new HttpHelper(handler, WXEntryActivity.this);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("appid", Constant.WX_APP_ID));
		params.add(new BasicNameValuePair("secret", Constant.WX_APP_SECRET));
		params.add(new BasicNameValuePair("code", code));
		params.add(new BasicNameValuePair("grant_type", "authorization_code"));
        httpHelper.startHttp(HttpType.GetWXInfor, params);
	}
    
    private void loginByToken(String openid)
	{
		HttpHelper httpHelper = new HttpHelper(handler, WXEntryActivity.this);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("action", "externalLogin"));
		//params.add(new BasicNameValuePair("action", "externalinfo"));
		params.add(new BasicNameValuePair("openid", openid));
		httpHelper.startHttp(HttpType.LoginByToken, params);
	}
      
    @Override  
    public void onReq(BaseReq arg0)
    {  
        // TODO Auto-generated method stub  
    	
    }  
  
    @Override  
    public void onResp(BaseResp resp) 
    {  
        Bundle bundle = new Bundle();  
        switch (resp.errCode) {  
        case BaseResp.ErrCode.ERR_OK:  
    	/*if(resp instanceof SendAuth.Resp)
    	{
    		String code = ((SendAuth.Resp) resp).code;  
        	getWXInfor(code);
    	}
    	else
    	{
    		Resp sp = new Resp(bundle);  
    		String code = sp.openId;
    		finish();
    	}*/
        //上面的code就是接入指南里要拿到的code  
        break;  
  
        default: 
        	finish();
            break;  
        }  
    }  
}
 
