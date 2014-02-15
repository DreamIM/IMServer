package de.mrpixeldream.dreamcode.im.server.commands;

import java.net.Socket;

import de.mrpixeldream.dreamcode.im.server.ClientHandler;

public interface Command {
	
	public boolean commandAction(String command, CommandHandler handler, Socket sender, ClientHandler clientHandler);

}
