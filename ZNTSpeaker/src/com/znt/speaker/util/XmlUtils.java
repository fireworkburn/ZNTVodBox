package com.znt.speaker.util;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

public class XmlUtils 
{

	
	/*public static String xml2JSON(String xml) 
	{
		return new XMLSerializer().read(xml).toString();
		
	}*/
	public static String xml2JSON(String xml) 
	{
		try 
		{
		
			JSONObject obj = XML.toJSONObject(xml);
		
			return obj.toString();
		
		} 
		catch (JSONException e) 
		{
		
			System.err.println("xml->json失败" + e.getLocalizedMessage());
		
			return "";
		
		}
	}
	
}
