package com.znt.diange.email;

import java.io.File;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class EmailSender 
{
	private Properties properties;
	private Session session;
	private Message message;
	private MimeMultipart multipart;

	public EmailSender() {
		super();
		this.properties = new Properties();
	}
	public void setProperties(String host,String post){
		//��ַ
		this.properties.put("mail.smtp.host",host);
		//�˿ں�
		this.properties.put("mail.smtp.post",post);
		//�Ƿ���֤
		this.properties.put("mail.smtp.auth",true);
		this.session=Session.getInstance(properties);
		this.message = new MimeMessage(session);
		this.multipart = new MimeMultipart("mixed");
	}
	/**
	 * �����ռ���
	 * @param receiver
	 * @throws MessagingException
	 */
	public void setReceiver(String[] receiver) throws MessagingException{
		Address[] address = new InternetAddress[receiver.length];
		for(int i=0;i<receiver.length;i++){
			address[i] = new InternetAddress(receiver[i]);
		}
		this.message.setRecipients(Message.RecipientType.TO, address);
	}
	/**
	 * �����ʼ�
	 * @param from ��Դ
	 * @param title ����
	 * @param content ����
	 * @throws AddressException
	 * @throws MessagingException
	 */
	public void setMessage(String from,String title,String content) throws AddressException, MessagingException{
		this.message.setFrom(new InternetAddress(from));
		this.message.setSubject(title);
		//���ı��Ļ���setText()���У������и�������ʾ������������
		MimeBodyPart textBody = new MimeBodyPart();
		textBody.setContent(content,"text/html;charset=gbk");
		this.multipart.addBodyPart(textBody);
	}
	/**
	 * ��Ӹ���
	 * @param filePath �ļ�·��
	 * @throws MessagingException
	 */
	public void addAttachment(String filePath) throws MessagingException{
		FileDataSource fileDataSource = new FileDataSource(new File(filePath));
		DataHandler dataHandler = new DataHandler(fileDataSource);
		MimeBodyPart mimeBodyPart = new MimeBodyPart();
		mimeBodyPart.setDataHandler(dataHandler);
		mimeBodyPart.setFileName(fileDataSource.getName());
		this.multipart.addBodyPart(mimeBodyPart);
	}
	/**
	 * �����ʼ�
	 * @param host ��ַ
	 * @param account �˻���
	 * @param pwd ����
	 * @throws MessagingException
	 */
	public void sendEmail(String host,String account,String pwd) throws MessagingException{
		//����ʱ��
		this.message.setSentDate(new Date());
		//���͵����ݣ��ı��͸���
		this.message.setContent(this.multipart);
		this.message.saveChanges();
		//�����ʼ����Ͷ��󣬲�ָ����ʹ��SMTPЭ�鷢���ʼ�  
		Transport transport=session.getTransport("smtp");  
		//��¼����  
		transport.connect(host,account,pwd);  
		//�����ʼ�
		transport.sendMessage(message, message.getAllRecipients());
		//�ر�����
		transport.close();
	}
}