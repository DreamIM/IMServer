package de.mrpixeldream.dreamcode.im.server.commands;

import java.net.Socket;

import de.mrpixeldream.dreamcode.im.server.ClientHandler;

public class CommandSend implements Command {

	@Override
	public boolean commandAction(String command, CommandHandler handler, Socket sender, ClientHandler clientHandler) {
		
		if (command.toUpperCase().startsWith("SEND"))
		{
			String id = command.split(" ")[1];
            String send = "";
            
            for (int i = 2; i < command.split(" ").length; i++)
            {
                    send += command.split(" ")[i] + " ";
            }
            
            try
            {
                    Integer.parseInt(id);
                    handler.parent.sendMessage(id, send);
            }
            catch (NumberFormatException e)
            {
                    handler.parent.sendMessage(handler.parent.idByName(id), send);
            }
			
			return true;
		}
		
		return false;
		
	}

}
