package de.mrpixeldream.dreamcode.im.server.plugins;

import de.mrpixeldream.dreamcode.im.server.commands.Command;

public interface ServerPlugin extends Command {

	public void onLoad();
	public void onUnload();
	
}
