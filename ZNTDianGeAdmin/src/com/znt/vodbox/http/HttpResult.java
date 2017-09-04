
package com.znt.vodbox.http; 
/** 
 * @ClassName: HttpResult 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-11-25 下午2:50:47  
 */
public class HttpResult
{
	private boolean bResult = false;
	private Object oResult = null;
	private String strInfor = null;
	private int total = 0;
	
	public void setSuccess(boolean bResult)
	{
		this.bResult = bResult;
	}
	public boolean isSuccess()
	{
		return bResult;
	}
	
	public void setResult(boolean bResult, Object oResult)
	{
		this.bResult = bResult;
		this.oResult = oResult;
	}
	public Object getReuslt()
	{
		return oResult;
	}
	public String getError()
	{
		if(oResult != null && oResult instanceof String)
			return (String)oResult;
		return "";
	}
	
	public void setTotal(int total)
	{
		this.total = total;
	}
	public int getTotal()
	{
		return total;
	}
}
 
