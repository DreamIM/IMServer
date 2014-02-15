package de.mrpixeldream.dreamcode.im.server.commands;

import java.net.Socket;

import de.mrpixeldream.dreamcode.im.server.ClientHandler;

public class CommandBroadcast implements Command {

	@Override
	public boolean commandAction(String command, CommandHandler handler, Socket sender, ClientHandler clientHandler) {
		
		if (command.toUpperCase().startsWith("BROADCAST"))
		{
			String send = handler.parent.clientNameByID(clientHandler.getID()) + ": ";
            
            for (int i = 1; i < command.split(" ").length; i++)
            {
                    send += command.split(" ")[i] + " ";
            }
            
            handler.parent.broadcastMessage(send);
            
            return true;
		}
		
		return false;
		
	}

}
