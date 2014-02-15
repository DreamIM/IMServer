package de.mrpixeldream.dreamcode.im.server.commands;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import de.mrpixeldream.dreamcode.im.server.ClientHandler;

public class CommandShow implements Command {

	@Override
	public boolean commandAction(String command, CommandHandler handler, Socket sender, ClientHandler clientHandler) {
		
		if (command.toUpperCase().startsWith("SHOW"))
		{
			try
			{
				PrintWriter output = new PrintWriter(sender.getOutputStream());
				
				for (Object now : handler.parent.listClients())
				{
					output.println(now);
				}
				
				output.flush();
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
