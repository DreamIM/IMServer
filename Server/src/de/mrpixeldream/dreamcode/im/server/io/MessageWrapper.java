package de.mrpixeldream.dreamcode.im.server.io;

import java.io.Serializable;

public class MessageWrapper implements Serializable
{
	private static final long serialVersionUID = 1L;
	private String encryptedMessage;
	
	public MessageWrapper(String encryptedMessage)
	{
		this.encryptedMessage = encryptedMessage;
	}
	
	public void setEncryptedMessage(String encryptedMessage)
	{
		this.encryptedMessage = encryptedMessage;
	}
	
	public String getEncryptedMessage()
	{
		return this.encryptedMessage;
	}
}
