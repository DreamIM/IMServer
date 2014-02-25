package de.mrpixeldream.dreamcode.im.server.io;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.jasypt.util.text.StrongTextEncryptor;

public class EncryptionUtility {

	String password = "";
	StrongTextEncryptor encryptor;
	
	public EncryptionUtility(String masterPassword) {
		
		this.password = masterPassword;
		this.encryptor = new StrongTextEncryptor();
		this.encryptor.setPassword(password);
		
	}
	
	public void sendEncrypted(String message, ObjectOutputStream out) {
		
		String encryptedMessage = encryptor.encrypt(message);
		
		try
		{
			MessageWrapper messageWrapper = new MessageWrapper(encryptedMessage);
			out.writeObject(messageWrapper);
			out.flush();
		}
		catch (Exception ex)
		{
			System.out.println("Unkown error in EncryptionUtility (encrypt): ");
			ex.printStackTrace();
		}
		
	}
	
	public String receiveEncrypted(ObjectInputStream in) throws Exception {
		
		try
		{
			MessageWrapper message = (MessageWrapper) in.readObject();
			
			String decryptedMessage = encryptor.decrypt(message.getEncryptedMessage());
			
			return decryptedMessage;
		}
		catch (Exception ex)
		{
			System.out.println("Unknown error in EncryptionUtility (decrypt): ");
			ex.printStackTrace();
		}
		
		return null;
	}
	
}
