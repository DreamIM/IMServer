package de.mrpixeldream.dreamcode.im.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

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
			BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
			
			// Send the "ACK" flag to make clear that we got the client's connection
			writer.write("ACK\n");
			writer.flush();
			
			// (Hopefully) Read the login command and split it to handle it properly
			String loginCommand = reader.readLine();
			String[] commandSplit = loginCommand.split(" ");
			
			// Check the validity of the login command
			if (commandSplit.length != 3)
			{
				// If not valid, send an error code to the client
				writer.write("err_login_args");
				writer.newLine();
				writer.flush();
			}
			
			String user = commandSplit[1];
			String password = commandSplit[2];
			
			// Create a new client handler for the client
			ClientHandler handler = new ClientHandler(client, parent);
			
			// Log the client in and register him
			writer.write(parent.doLogin(client, user, password, handler) + "\n");
			writer.flush();
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}
}