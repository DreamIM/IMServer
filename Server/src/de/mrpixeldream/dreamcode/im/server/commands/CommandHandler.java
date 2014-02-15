package de.mrpixeldream.dreamcode.im.server.commands;

import java.net.Socket;
import java.util.ArrayList;

import de.mrpixeldream.dreamcode.im.server.ClientHandler;
import de.mrpixeldream.dreamcode.im.server.IMServer;
import de.mrpixeldream.dreamcode.im.server.LogLevel;

public class CommandHandler {

	IMServer parent;
	ArrayList<Command> commands = new ArrayList<>();
	
	public CommandHandler(IMServer parent)
	{
		this.parent = parent;
	}
	
	public void addCommand(Command cmd)
	{
		if (!this.commands.contains(cmd))
		{
			this.commands.add(cmd);
		}
		else
		{
			this.parent.log("Command already exists", LogLevel.ERROR);
		}
	}
	
	public void removeCommand(Command cmd)
	{
		if (this.commands.contains(cmd))
		{
			this.commands.remove(this.commands.indexOf(cmd));
		}
		else
		{
			this.parent.log("Command to remove not found", LogLevel.ERROR);
		}
	}
	
	public void handleCommand(String command, Socket sender, ClientHandler handler)
	{
		boolean handlerFound = false;
		for (Command cmd : commands)
		{
			if (cmd.commandAction(command, this, sender, handler))
			{
				handlerFound = true;
			}	
		}
		
		if (!handlerFound)
		{
			this.parent.log("No handler found for command \"" + command.split(" ")[0] + "\"", LogLevel.ERROR);
		}
	}
	
	public IMServer getParent()
	{
		return this.parent;
	}
	
}
