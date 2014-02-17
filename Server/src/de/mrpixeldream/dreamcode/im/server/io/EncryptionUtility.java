package de.mrpixeldream.dreamcode.im.server.io;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.Socket;

import de.mrpixeldream.dreamcode.im.server.thirdparty.ReadWriteAES;

public class EncryptionUtility {

	String password = "";
	
	public EncryptionUtility(String masterPassword) {
		
		this.password = masterPassword;
		
	}
	
	public void sendEncrypted(String message, Socket target) {
		
		try
		{
			ReadWriteAES.encode(message.getBytes(), target.getOutputStream(), password);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public String receiveEncrypted(Socket target) {
		
		String decryptedMessage = "";
		try
		{
			BufferedInputStream inputStream = new BufferedInputStream(target.getInputStream());
			byte[] encryptionBuffer = ReadWriteAES.decode(inputStream, password);
			decryptedMessage = encryptionBuffer.toString();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return decryptedMessage;
	}
	
}
