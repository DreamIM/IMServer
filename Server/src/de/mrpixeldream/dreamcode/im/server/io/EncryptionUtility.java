package de.mrpixeldream.dreamcode.im.server.io;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import de.mrpixeldream.dreamcode.im.server.thirdparty.ReadWriteAES;
import de.mrpixeldream.dreamcode.im.server.thirdparty.ReadWriteDES;

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
	
	public String receiveEncrypted(Socket target) throws Exception {
		
		String decryptedMessage = "";

		BufferedReader reader = new BufferedReader(new InputStreamReader(target.getInputStream()));
		String encryptedMessage = reader.readLine();
		ByteArrayInputStream encryptedBytes = new ByteArrayInputStream(encryptedMessage.getBytes());
		byte[] encryptionBuffer = ReadWriteAES.decode(encryptedBytes, password);
		
		System.out.println(encryptionBuffer);
		decryptedMessage = new String(encryptionBuffer);
		System.out.println("msg_de: " + decryptedMessage);
		System.out.println("msg_en: " + encryptedMessage);
		
		return decryptedMessage;
	}
	
}
