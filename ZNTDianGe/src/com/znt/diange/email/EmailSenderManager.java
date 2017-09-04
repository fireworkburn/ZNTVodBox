package com.znt.diange.email; 

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

/** 
 * @ClassName: EmailSenderManager 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-4-20 下午2:37:28  
 */
public class EmailSenderManager
{

	
	
	public void sendEmail(final String title, final String emailContent)
	{
        new Thread(new Runnable() 
        {
    		@Override
    		public void run()
    		{
    			try {
    				
    				/*EmailSender sender = new EmailSender();
    				//设置服务器地址和端口，网上搜的到
    				sender.setProperties("mail.neldtv.org", "25");
    				//分别设置发件人，邮件标题和文本内容
    				sender.setMessage("photocloud@neldtv.org", "庭悦异常_" + DateUtils.getStringDate(), emailContent);
    				//设置收件人
    				sender.setReceiver(new String[]{"yyu@neldtv.org"});
    				//添加附件
    				//这个附件的路径是我手机里的啊，要发你得换成你手机里正确的路径
//    				sender.addAttachment("/sdcard/DCIM/Camera/asd.jpg");
    				//发送邮件
    				sender.sendEmail("mail.neldtv.org", "photocloud@neldtv.org", "tbtad0918");*/
    				
    				EmailSender sender = new EmailSender();
    				//设置服务器地址和端口，网上搜的到
    				sender.setProperties("smtp.sina.com", "25");
    				//分别设置发件人，邮件标题和文本内容
    				sender.setMessage("yuyan19850204@sina.com", title, emailContent);
    				//设置收件人
    				sender.setReceiver(new String[]{"yuyan@zhunit.com"});
    				//添加附件
    				//这个附件的路径是我手机里的啊，要发你得换成你手机里正确的路径
//    				sender.addAttachment("/sdcard/DCIM/Camera/asd.jpg");
    				//发送邮件
    				sender.sendEmail("smtp.sina.com", "yuyan19850204@sina.com", "jinzai520");
    				
    			} catch (AddressException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} catch (MessagingException e) {
    				// TODO Auto-generated catch blockf
    				e.printStackTrace();
    			}
    		}
    	}).start();
	}
}
 
