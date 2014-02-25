package de.mrpixeldream.dreamcode.im.server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import de.mrpixeldream.dreamcode.im.server.io.MessageWrapper;

public class LoginHandler extends Thread
{
	Socket client;
	IMServer parent;
	
	public LoginHandler(IMServer parent, Socket client)
	{
		this.client = client;
		this.parent = parent;
	}
	
	@Override
	public void run()
	{
		try
		{
			// Open needed streams for client-server communication
			ObjectOutputStream objOut = new ObjectOutputStream(client.getOutputStream());
			ObjectInputStream objIn = new ObjectInputStream(client.getInputStream());
			
			// Send the "ACK" flag to make clear that we got the client's connection
			MessageWrapper ackFlag = new MessageWrapper(this.parent.getBasicEncryption().encrypt("ACK"));
			objOut.writeObject(ackFlag);
			objOut.flush();
			ackFlag = null;
			
			// (Hopefully) Read the login command and split it to handle it properly
			MessageWrapper loginCommand = (MessageWrapper) objIn.readObject();
			String[] commandSplit = this.parent.getBasicEncryption().decrypt(loginCommand.getEncryptedMessage()).split(" ");
			loginCommand = null;
			
			// Check the validity of the login command
			if (commandSplit.length != 3)
			{
				// If not valid, send an error code to the client
				MessageWrapper errorCode = new MessageWrapper(this.parent.getBasicEncryption().encrypt("err_login_args"));
				objOut.writeObject(errorCode);
				objOut.flush();
				errorCode = null;
			}
			
			String user = commandSplit[1];
			String password = commandSplit[2];
			
			// Create a new client handler for the client
			ClientHandler handler = new ClientHandler(client, parent, objIn, objOut);
			
			// Log the client in and register him
			String loginResponse = parent.doLogin(client, user, password, handler);
			MessageWrapper loginProc = new MessageWrapper(this.parent.getBasicEncryption().encrypt(loginResponse));
			objOut.writeObject(loginProc);
			
			objOut.flush();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}