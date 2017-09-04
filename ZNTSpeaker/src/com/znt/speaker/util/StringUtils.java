
package com.znt.speaker.util; 

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.mina.util.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;


/** 
 * @ClassName: StringUtils 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-2-11 上午11:54:03  
 */
public class StringUtils
{

	private static long TIMEMSUNIT = 1000;
	private static long TIMEUNIT = 60;
	
	private static long STOREUNIT = 1024;
	
	/**
	* @Description: 字符串空判断
	* @param @param value
	* @param @return   
	* @return boolean 
	* @throws
	 */
	public static boolean isEmpty(String value) 
	{
		return value == null || value.equals("");
	}

	/**
	* @Description: 根据tag将字符串分解
	* @param @param url
	* @param @return   
	* @return String[] 
	* @throws
	 */
	public static String[] splitUrls(String url, String tag) 
	{
		String[] urls = url.split(tag);
		return urls;
	}

	/**
	* @Description: 全角半角转换
	* @param @param input
	* @param @return   
	* @return String 
	* @throws
	 */
	public static String toDBC(String input) 
	{          
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) 
        {              
	        if (c[i] == 12288) 
	        {                 
		        c[i] = (char) 32;                  
		        continue;
	        }
	         if (c[i] > 65280 && c[i] < 65375)
	            c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }  
	
	/**
	* @Description: 字符过滤
	* @param @param str
	* @param @return   
	* @return String 
	* @throws
	 */
	public static String stringFilter(String str) 
	{
		str = str.replaceAll("【", "[").replaceAll("】", "]")
				.replaceAll("！", "!").replaceAll("：", ":");// 替换中文标号
		String regEx = "[『』]"; // 清除掉特殊字符
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}
	
	/**
    * @Description: 字符串转整形
    * @param @param str
    * @param @return   
    * @return int 
    * @throws
     */
    public static int stringToInt(String str)
    {
    	if(str == null)
    		return -1;
    	return Integer.parseInt(str);
    }
	
	public static String getHeadByTag(String tag, String body)
	{
		if(body == null || tag == null || tag.length() == 0 || body.length() == 0)
			return "";
		String tempStr = null;
		if(body.endsWith(tag))
			body = body.substring(0, body.lastIndexOf(tag));
		if(body.contains(tag))
		{
			tempStr = body.substring(0, body.lastIndexOf(tag));
		}
		if(tempStr == null)
			tempStr = "";
		return tempStr;
	}
	
	public static String getLastByTag(String tag, String body)
	{
		if(body == null || tag == null || tag.length() == 0 || body.length() == 0)
			return "";
		String tempStr = null;
		if(body.endsWith(tag))
			body = body.substring(0, body.lastIndexOf(tag));
		if(body.contains(tag))
		{
			tempStr = body.substring(body.lastIndexOf(tag) + 1);
		}
		if(tempStr == null)
			tempStr = "";
		return tempStr;
	}
	
	/**
	* @Description: 将字符串转为以endStr结尾
	* @param @param tag  
	* @param @param endStr
	* @param @param body
	* @param @return   
	* @return String 
	* @throws
	 */
	public static String getStringEndsWithStr(String tag, String endStr, String body)
	{
		String resulStr = body;
		if(!resulStr.endsWith(endStr))
		{
			resulStr = getHeadByTag(tag, body) + endStr;
		}
		return resulStr;
	}
	
	 /**
	* @Description: Base64加密  依赖commons-codec-1.6.jar包
	* @param @param plainText
	* @param @return   
	* @return String 
	* @throws
	 */
	public static String encodeStr(String plainText)
	{
		byte[] b=null;
		String s= null;
		try 
		{
			b = plainText.getBytes("UTF-8");
			Base64 base64 = new Base64();
			b = base64.encode(b);
			s = new String(b,"UTF-8");
		}
		catch (UnsupportedEncodingException e) 
		{
			e.printStackTrace();
		}
		return s;
	}
	public static String encodeStr(byte[] b)
	{
		String s= null;
		try 
		{
			Base64 base64 = new Base64();
			b = base64.encode(b);
			s = new String(b,"UTF-8");
		}
		catch (UnsupportedEncodingException e) 
		{
			e.printStackTrace();
		}
		return s;
	}
	/**
	* @Description: Base64解密 依赖commons-codec-1.6.jar包
	* @param @param encodeStr
	* @param @return   
	* @return String 
	* @throws
	 */
	public static String decodeStr(String encodeStr)
	{
		byte[] b =null;
		String s=null;
		try
		{
			b = encodeStr.getBytes("UTF-8");
			Base64 base64 = new Base64();
			b = base64.decode(b);
			s = new String(b,"UTF-8");
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return s;
	}
	
	/**
    * @Description: 获取百分百
    * @param @param progress
    * @param @param total
    * @param @return   
    * @return int 
    * @throws
     */
    public static int getProgress(long progress, long total)
    {
    	float f = (float)progress/total;
    	if(f > 0)
    	{
    		BigDecimal bd = new BigDecimal(f);
    		bd = bd.setScale(2,BigDecimal.ROUND_UP);//取3.1415926小数点后面二位
    		float f1 = Float.parseFloat(bd+"");
    		float result = f1*100;
    		int l = (int)result;
    		return l;
    	}
		return 0;
    }
    public static int getProgress(int progress, int total)
    {
    	float f = (float)progress/total;
    	BigDecimal bd = new BigDecimal(f);
    	bd = bd.setScale(2,BigDecimal.ROUND_UP);//取3.1415926小数点后面二位
    	float f1 = Float.parseFloat(bd+"");
    	float result = f1*100;
    	int l = (int)result;
    	return l;
    }
    
    /**
	* @Description: 设置字符串颜色 如：
	* exam1:editText2.setText(Html.fromHtml(  "<font color=#E61A6B>红色代码</font> "+ "<i><font color=#1111EE>蓝色斜体代码</font></i>"
　　　　　　　　　　　　　　　　　　　　　　　　+"<u><i><font color=#1111EE>蓝色斜体加粗体下划线代码</font></i></u>"));
	* exam2:String temp = "name:<br /><font color=\"teal\">hello<small>title<b>activeBalance</b></small></font>"; 
	* 说明：<br />：表示换行，和“\n”一样。
			<small>content</small>：表示小字体。
			<font color=\"teal\">content</font>：设置颜色，teal是青色。
			<b>content</b>：表示粗体
			<u>content</u>：表示下横线
	* @param @param content  内容
	* @param @param color 颜色
	* @param @param bold 是否粗体
	* @param @param italic 是否斜体
	* @param @param underline 是否下划线
	* @param @param size 大小  0:small 1: big  
	* @param @return   
	* @return String  字符串的格式  通过Html.fromHtml(inforStyle)解析后再显示
	* @throws
	 */
	public static  String setTextStyle(String content, String color, boolean bold, boolean italic, boolean underline, int sizeType)
	{
		String result = "<font color=" + color + ">" + content + "</font>";
		if(bold)
			result = packageStrings("<b>", result, "</b>");
		if(italic)
			result = packageStrings("<i>", result, "</i>");
		if(underline)
			result = packageStrings("<u>", result, "</u>");
		if(sizeType == 0)
			result = packageStrings("<small>", result, "</small>");
		else if(sizeType == 1)
			result = packageStrings("<big>", result, "</big>");
		return result;
	}
	
	/**
     * 设置子字符串为红色
     * @param text
     * @param colorText
     * @return
     */
    public static SpannableString setColorText(String text, String colorText)
    {
        //创建一个 SpannableString对象    
        SpannableString msp = new SpannableString(text);
        Pattern p=Pattern.compile(colorText);
        Matcher matcher=p.matcher(text);
        while(matcher.find())
        {
            msp.setSpan(new ForegroundColorSpan(Color.RED), 
                    matcher.start(), matcher.end(), 
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return msp;
    }
    
    /**
     * 设置子字符串为红色和字体大小
     * @param text
     * @param colorText
     * @param colorTextSize
     * @return
     */
    public static SpannableString setColorText(String text, String colorText, float colorTextSize)
    {
        //创建一个 SpannableString对象    
        SpannableString msp = new SpannableString(text);
        Pattern p = Pattern.compile(colorText);
        Matcher matcher = p.matcher(text);
        while(matcher.find())
        {
            msp.setSpan(new ForegroundColorSpan(Color.RED), 
                    matcher.start(), matcher.end(), 
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            msp.setSpan(new RelativeSizeSpan((colorTextSize)), 
                    matcher.start(), matcher.end(), 
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return msp;
    }
    /**
    * @Description: 设置指定字符串颜色和大小
    * @param @param text
    * @param @param colorText
    * @param @param colorTextSize
    * @param @param color
    * @param @return   
    * @return SpannableString 
    * @throws
     */
    public static SpannableString setColorText(String text, String colorText, float colorTextSize, int color)
    {
    	//创建一个 SpannableString对象    
    	SpannableString msp = new SpannableString(text);
    	Pattern p = Pattern.compile(colorText);
    	Matcher matcher = p.matcher(text);
    	while(matcher.find())
    	{
    		msp.setSpan(new ForegroundColorSpan(color), 
    				matcher.start(), matcher.end(), 
    				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    		
    		msp.setSpan(new RelativeSizeSpan((colorTextSize)), 
    				matcher.start(), matcher.end(), 
    				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    	}
    	return msp;
    }
	
	public static String packageStrings(String str1, String str, String str2)
	{
		if(str1 == null && str2 != null)
			return str + str2;
		if(str1 != null && str2 == null)
			return str1 + str;
		if(str1 == null && str2 == null)
			return null;
		return str1 + str + str2;
	}
	
	/**
    * @Description: 获取时间格式
    * @param @param time
    * @param @return   
    * @return String 
    * @throws
     */
    public static String getFormatTime(long time) 
    {
        double second = (double) time / TIMEMSUNIT;
        if (second < 1) 
        {
            return time + " MS";
        }

        double minute = second / TIMEUNIT;
        if (minute < 1) 
        {
            BigDecimal result = new BigDecimal(Double.toString(second));
            return result.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " SEC";
        }

        double hour = minute / TIMEUNIT;
        if (hour < 1) 
        {
            BigDecimal result = new BigDecimal(Double.toString(minute));
            return result.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " MIN";
        }

        BigDecimal result = new BigDecimal(Double.toString(hour));
        return result.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " H";
    }
    
    /**
	* @Description: 获取磁盘空间大小格式
	* @param @param size
	* @param @return   
	* @return String 
	* @throws
	 */
	public static String getFormatSize(double size) 
	{
        double kiloByte = size / STOREUNIT;
        if (kiloByte < 1) 
        {
            return size + " Byte";
        }

        double megaByte = kiloByte / STOREUNIT;
        if (megaByte < 1)
        {
            BigDecimal result = new BigDecimal(Double.toString(kiloByte));
            return result.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " KB";
        }

        double gigaByte = megaByte / STOREUNIT;
        if (gigaByte < 1) 
        {
            BigDecimal result = new BigDecimal(Double.toString(megaByte));
            return result.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " MB";
        }

        double teraBytes = gigaByte / STOREUNIT;
        if (teraBytes < 1)
        {
            BigDecimal result = new BigDecimal(Double.toString(gigaByte));
            return result.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " GB";
        }
        BigDecimal result = new BigDecimal(teraBytes);
        return result.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " TB";
    }
    
    /**
	  * 判断是否是邮箱
	  * @param param
	  * @return
	  */
	 public static boolean isEmail(String param)
	 {
		 if(param == null)
			 return false;
		 if(param.length() <= 0)
			 return false;
		 boolean flag=false;
		 Pattern p = Pattern.compile("^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(\\.([a-zA-Z0-9_-])+)+$");
		 Matcher m = p.matcher(param);
		 flag=m.matches();
		 return flag;
	 } 
	 
	 /**
	 * 验证手机格式
	* @Description: TODO
	* @param @param mobiles
	* @param @return   
	* @return boolean 
	* @throws
	 */
	public static boolean isMobileNO(String mobiles) 
	{
		/*
		移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
		联通：130、131、132、152、155、156、185、186
		电信：133、153、180、189、（1349卫通）
		总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
		*/
		String telRegex = "[1][358]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
		if (TextUtils.isEmpty(mobiles)) 
			return false;
		else 
			return mobiles.matches(telRegex);
    }
	 
	
	/** 
	   *  获取时间戳
	   * @return 
	   */ 
	public static String generateTimeStamp() 
	{ 
		return String.valueOf(System.currentTimeMillis() / 1000); 
	} 
	  /** 
	   * 单次产生值
	   * @param is32 
	   *            32 
	   * @return 
	   */ 
	public static String generateNonce(boolean is32) 
	{ 
		Random random = new Random(); 
		// 1234009999999 
		String result = String.valueOf(random.nextInt(9876599) + 123400); 
		if (is32) 
		{ 
			// MD5 
			try 
			{ 
				MessageDigest md = MessageDigest.getInstance("MD5"); 
				md.update(result.getBytes()); 
		        byte b[] = md.digest(); 
		        int i; 
		        StringBuffer buf = new StringBuffer(""); 
		        for (int offset = 0; offset < b.length; offset++) 
		        { 
		        	i = b[offset]; 
		        	if (i < 0) 
		        		i += 256; 
		        	if (i < 16) 
		        		buf.append("0"); 
		        	buf.append(Integer.toHexString(i)); 
		        } 
		        result = buf.toString(); 
			} 
			catch (NoSuchAlgorithmException e) 
			{ 
				e.printStackTrace(); 
			} 
		} 
		return result; 
	} 
	
      
	private static String byteToHexString(byte ib)
	{  
	      char[] Digit={  '0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f' };  
	      char[] ob=new char[2];  
	      ob[0]=Digit[(ib>>>4)& 0X0f];  
	      ob[1]=Digit[ib & 0X0F];  
	      String s=new String(ob);  
	      return s;           
	}       
	
	public static int dip2px(Activity activity, float dipValue) 
	{  
		float scale = activity.getResources().getDisplayMetrics().density; 
		return (int) (dipValue * scale + 0.5f); 
	}   
	public static int px2dip(Activity activity, float pxValue) 
	{  
		float scale = activity.getResources().getDisplayMetrics().density; 
		return (int) (pxValue / scale + 0.5f); 
	}
	public static int dip2px(Context activity, float dipValue) 
	{  
		float scale = activity.getResources().getDisplayMetrics().density; 
		return (int) (dipValue * scale + 0.5f); 
	}   
	public static int px2dip(Context activity, float pxValue) 
	{  
		float scale = activity.getResources().getDisplayMetrics().density; 
		return (int) (pxValue / scale + 0.5f); 
	}
	
	/**
	 * 播放时间转换
	 */
	public static String durationToTime(int time) {

		time /= 1000;
		int minute = time / 60;
		int second = time % 60;
		minute %= 60;
		return String.format("%02d:%02d", minute, second);
	}
	
	public static int formatDurationString(String durationString){
		int duration = 0;
		if (durationString == null || durationString.length() == 0){
			return duration;
		}
		
		double a = 3.2;
		int b = (int) a;
		try {
			String sArray[] = durationString.split(":");
			double hour = Double.valueOf(sArray[0]);
			double minute = Double.valueOf(sArray[1]);
			double second = Double.valueOf(sArray[2]);		
			
			return (int) (((hour * 60 + minute) * 60 + second) * 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return duration;
	}
	
	public static String secToTime(int time) 
	{
		String timeStr = null;
		int hour = 0;
		int minute = 0;
		int second = 0;
		if (time <= 0)
			return "00:00:00";
		else 
		{
			minute = time / 60;
			if (minute < 60) 
			{
				second = time % 60;
				timeStr = "00:" + unitFormat(minute) + ":" + unitFormat(second);
			} 
			else
			{
				hour = minute / 60;
				if (hour > 99)
					return "99:59:59";
				minute = minute % 60;
				second = time - hour * 3600 - minute * 60;
				timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":"
						+ unitFormat(second);
			}
		}
		return timeStr;
	}

	public static String unitFormat(int i)
	{
		String retStr = null;
		if (i >= 0 && i < 10)
			retStr = "0" + Integer.toString(i);
		else if (i >= 10 && i <= 60) 
		{
			retStr = "" + i;
		} 
		else 
		{
			retStr = "00";
		}
		return retStr;
	}
	/**
	 * Convert time from "00:00:00" to seconds.
	 * 
	 * @param length
	 *            00:00:00或者00:00
	 * @return The length in seconds.
	 */
	public static int getIntLength(String length) 
	{
		if (TextUtils.isEmpty(length))
		{
			return -1;
		}
		String[] split = length.split(":");
		int count = -1;
		try 
		{
			if (split.length == 3) 
			{
				count += (Integer.parseInt(split[0])) * 60 * 60;
				count += Integer.parseInt(split[1]) * 60;
				count += Integer.parseInt(split[2]);
			} 
			else if (split.length == 2) 
			{
				count += Integer.parseInt(split[0]) * 60;
				count += Integer.parseInt(split[1]);
			}
		} 
		catch (NumberFormatException e)
		{
			e.printStackTrace();
		}
		return count;
	}
	
	public static List<String> stringsToList(String[] strs)
	{
		List<String> list = new ArrayList<String>();
		int size = strs.length;
		for(int i=0;i<size;i++)
		{
			list.add(strs[i]);
		}
		
		return list;
	}
	
	public static String getInforFromJason(JSONObject json, String key)
	{
		if(json == null || key == null)
			return "";
		if(json.has(key))
		{
			try
			{
				String result = json.getString(key);
				if(result.equals("null"))
					result = "";
				return result;
				//return StringUtils.decodeStr(result);
			} 
			catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "";
	}
	
	public static String getPlanTimeShow(String endTime)
	{
		String[] ts = endTime.split(":");
		String hour = null;
		String min = null;
		String sec = null;
		if(ts.length > 0)
			hour = ts[0];
		if(ts.length > 1)
			min = ts[1];
		if(ts.length > 2)
			sec = ts[2];
		if(!TextUtils.isEmpty(sec) && !TextUtils.isEmpty(min))
		{
			
			int secInt = Integer.parseInt(sec);
			int minInt = Integer.parseInt(min);
			int hourInt = Integer.parseInt(hour);
			if(secInt > 0)
				minInt += 1;
			if(minInt == 60)
			{
				min = "00";
				hourInt = hourInt + 1;
				if(hourInt < 10)
					hour = "0" + hourInt;
				else
					hour = "" + hourInt;
			}
			else if(minInt < 10)
				min = "0" + minInt;
			else
				min = "" + minInt;
		}
		return hour + ":" + min;
	}
}
 
