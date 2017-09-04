
package com.znt.vodbox.utils; 

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/** 
 * @ClassName: UrlUtils 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-12-27 下午3:45:00  
 */
public class UrlUtils
{

	
	public static String encodeUrl(String srcUrl)
	{
		try 
		{
			srcUrl = URLEncoder.encode(srcUrl, "utf-8");
		} 
		catch (UnsupportedEncodingException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return srcUrl;
		
		/*URL url;
		try 
		{
			url = new URL(srcUrl);
			URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
			url = uri.toURL();
			srcUrl = url.toString();
		}
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return srcUrl;*/
	}
	
	public static String decodeUrl(String srcUrl)
	{
		
		try 
		{
			srcUrl = URLDecoder.decode(srcUrl, "utf-8");
		} 
		catch (UnsupportedEncodingException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return srcUrl;
	}
	
	public static String getMediaPlayUrl(String mediaUrl, boolean isLocal)
	{
		String url = "";
		/*String[] tags = mediaUrl.split("/");
		int tagLen = tags.length;
		if(tagLen > 1)
		{
			url = tags[tagLen - 2] + "/" + tags[tagLen - 1];
		}
		else
			url = tags[tagLen];
		
		//urlEnd = UrlUtils.convertUrl(urlEnd);
		//urlEnd = UrlUtils.decodeUrl(urlEnd);
		if(isLocal)
			url = DLNAContainer.getInstance().getAddrFromPhone() + "/%25/" + url;
		else
			url = DLNAContainer.getInstance().getAddrFromSpeaker() + "/%25/" + url;*/
		
		return url;
	}
	
	/*public static String convertUrl(String resUrl)
	{
		String url = "";
		try
		{
			url = URLEncoder.encode(resUrl,"utf-8").replaceAll("\\+", "%20");
			url = url.replaceAll("%3A", ":").replaceAll("%2F", "/");
		}
		catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return url;
	}*/
	
}
 
