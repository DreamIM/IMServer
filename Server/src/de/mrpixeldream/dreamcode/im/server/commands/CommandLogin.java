package de.mrpixeldream.dreamcode.im.server.commands;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import de.mrpixeldream.dreamcode.im.server.ClientHandler;

public class CommandLogin implements Command {

	@Override
	public boolean commandAction(String command, CommandHandler handler, Socket sender, ClientHandler clientHandler) {
		
		if (command.toUpperCase().startsWith("LOGIN"))
		{
			try
			{
				PrintWriter output = new PrintWriter(sender.getOutputStream());
				
				String statusMessage = handler.parent.doLogin(sender, command.split(" ")[1], command.split(" ")[2], clientHandler);
				
				if (!statusMessage.toUpperCase().contains("MATCH"))
				{
					output.println(statusMessage);
					output.flush();
					
					clientHandler.setID(handler.parent.getIps().get(sender.getInetAddress()));
				}
				else
				{
					output.println(statusMessage);
					output.flush();
					
					clientHandler.logout();
				}
				
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			return true;
		}
		
		return false;
		
	}

}
