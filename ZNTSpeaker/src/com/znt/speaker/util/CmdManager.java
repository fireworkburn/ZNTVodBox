
package com.znt.speaker.util; 

import org.json.JSONException;
import org.json.JSONObject;

import com.znt.diange.mina.cmd.RegisterCmd;
import com.znt.diange.mina.entity.UserInfor;

/** 
 * @ClassName: CmdUtils 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-12-10 上午11:31:12  
 */
public class CmdManager
{
	protected RegisterCmd getRegisterCmd(String cmdStr)
	{
		RegisterCmd cmd = new RegisterCmd();
		try
		{
			JSONObject jsonObj = new JSONObject(cmdStr);
			UserInfor userInfor = new UserInfor();
				
			cmd.setUserInfor(userInfor);
		} 
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return cmd;
	}
}
 
