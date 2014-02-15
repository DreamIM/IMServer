package de.mrpixeldream.dreamcode.im.server.commands;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import de.mrpixeldream.dreamcode.im.server.ClientHandler;
import de.mrpixeldream.dreamcode.im.server.LogLevel;

public class AdminCommandShutdown implements Command {

	@Override
	public boolean commandAction(String command, CommandHandler handler, Socket sender, ClientHandler clientHandler) {
		
		if (command.toUpperCase().startsWith("SHUTDOWN"))
		{
			boolean isAdmin = false;
			String executor = handler.parent.clientNameByID(clientHandler.getID());
			
			isAdmin = handler.parent.getDBManager().isAdmin(executor);
			
			if (isAdmin)
			{
				handler.parent.doShutdown();
			}
			else
			{
				try
				{
					PrintWriter output = new PrintWriter(sender.getOutputStream());
					output.println("You are not an administrator!");
					output.flush();
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			return true;
		}
		
		return false;
	}

}
