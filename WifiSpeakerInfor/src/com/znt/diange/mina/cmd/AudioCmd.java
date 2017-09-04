
package com.znt.diange.mina.cmd; 

import java.io.Serializable;

/** 
 * @ClassName: AudioCmd 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-8-20 下午3:41:19  
 */
public class AudioCmd extends BaseCmd implements Serializable
{

	/** 
	* @Fields serialVersionUID : TODO
	*/ 
	private static final long serialVersionUID = 1L;
	
	private int len = 0;
	private byte[] pkg = null;
	private long timestamp = 0;
	
	public AudioCmd(int len)
	{
		this.len = len;
		pkg = new byte[len];
	}
	
	public void setPkg(byte[] data)
	{
		this.pkg = data;
		//System.arraycopy(data, 0, pkg, 0, len);
	}
	public byte[] getPkg()
	{
		return pkg;
	}
	
	public void setLen(int len)
	{
		this.len = len;
	}
	public int getLen()
	{
		return len;
	}
	
	public void setTimeStamp(long timestamp)
	{
		this.timestamp = timestamp;
	}
	public long getTimeStamp()
	{
		return timestamp;
	}

}
 
