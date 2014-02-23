package de.mrpixeldream.dreamcode.im.server.commands;

import java.net.Socket;

import de.mrpixeldream.dreamcode.im.server.ClientHandler;

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
				clientHandler.getEncryptionUtility().sendEncrypted("You are not an administrator!", sender);
			}
			
			return true;
		}
		
		return false;
	}

}
