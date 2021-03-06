package de.mrpixeldream.dreamcode.im.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.jasypt.util.text.StrongTextEncryptor;

import de.mrpixeldream.dreamcode.im.server.commands.AdminCommandShutdown;
import de.mrpixeldream.dreamcode.im.server.commands.CommandBroadcast;
import de.mrpixeldream.dreamcode.im.server.commands.CommandHandler;
import de.mrpixeldream.dreamcode.im.server.commands.CommandSend;
import de.mrpixeldream.dreamcode.im.server.commands.CommandShow;
import de.mrpixeldream.dreamcode.im.server.db.DBManager;
import de.mrpixeldream.dreamcode.im.server.io.EncryptionUtility;
import de.mrpixeldream.dreamcode.im.server.plugins.PluginManager;
import de.mrpixeldream.dreamcode.im.server.plugins.ServerPlugin;

public class IMServer
{
	int								port;

	FileWriter						logger;

	// Default Commands
	CommandShow						cmdShow			= new CommandShow();
	CommandSend						cmdSend			= new CommandSend();
	CommandBroadcast				cmdBroadcast	= new CommandBroadcast();
	AdminCommandShutdown			cmdShutdown		= new AdminCommandShutdown();
	CommandHandler					cmdHandler;

	DBManager						dbManager		= new DBManager(this);

	HashMap<String, Socket>			clients;
	HashMap<String, String>			names;
	HashMap<InetAddress, String>	ips;
	HashMap<String, String>			ids;
	HashMap<String, String>			passwords;
	HashMap<String, ClientHandler>	clientHandlers;

	ArrayList<ServerPlugin>			plugins;

	PluginManager					pluginManager;
	
	StrongTextEncryptor				basicEncryption	= new StrongTextEncryptor();

	Random							idGenerator;

	ServerSocket					server;

	boolean							logEnabled		= false;
	boolean							isRunning		= true;

	public static void main(String[] args)
	{
		// Setting standard port
		int port = 22558;

		// Check whether a custom port is given or not
		if (args.length == 1 && !args[0].isEmpty())
		{
			try
			{
				// Set the port to the custom port
				port = Integer.parseInt(args[0]);
			}
			catch (NumberFormatException ex)
			{
				System.err.println("An error occured while parsing port number, must be a number");
			}
		}

		// Create server instance and start the server
		IMServer server = new IMServer(port);
		server.startServer();
	}

	public IMServer(int port)
	{
		this.port = port;
		return;
	}

	public void startServer()
	{
		if (this.port != 0)
		{
			// Initialization code
			File logfile = new File("chatbase.log");

			try
			{
				this.logger = new FileWriter(logfile, true);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			this.logEnabled = true;

			log("Setting up variables...", LogLevel.INFO);

			clients = new HashMap<String, Socket>();
			names = new HashMap<String, String>();
			ips = new HashMap<InetAddress, String>();
			ids = new HashMap<String, String>();
			passwords = new HashMap<>();
			pluginManager = new PluginManager(this);
			clientHandlers = new HashMap<>();
			cmdHandler = new CommandHandler(this);
			
			log("Initializing default encryption...", LogLevel.INFO);
			basicEncryption.setPassword("Gummiball");

			log("Creating socket...", LogLevel.INFO);
			try
			{
				log("Adding default commands...", LogLevel.INFO);

				this.cmdHandler.addCommand(cmdShow);
				this.cmdHandler.addCommand(cmdSend);
				this.cmdHandler.addCommand(cmdBroadcast);
				this.cmdHandler.addCommand(cmdShutdown);

				log("Loading custom features...", LogLevel.INFO);

				this.plugins = this.pluginManager.loadPlugins();
				
				for (ServerPlugin plugin : this.plugins)
				{
					this.cmdHandler.addCommand(plugin);
				}

				server = new ServerSocket(port);

				log("Server now listening for new connections...", LogLevel.INFO);

				while (isRunning)
				{
					Socket client;
					client = server.accept();
					log("Connected from " + client.getInetAddress(), LogLevel.INFO);
					LoginHandler loginHandler = new LoginHandler(this, client);
					loginHandler.start();
				}

				log("Received shutdown signal. Bye!", LogLevel.INFO);
			}
			catch (IOException e)
			{
				log("Got error while creating socket:", LogLevel.ERROR);
				log(e.getMessage(), LogLevel.ERROR);
			}
		}
	}

	public void log(String message, LogLevel level)
	{
		if (this.logEnabled)
		{
			try
			{
				if (level == LogLevel.INFO)
				{
					System.out.println("INFO: " + message);
					logger.append("[INFO] " + this.datePrefix() + message + "\n");
				}
				else if (level == LogLevel.ERROR)
				{
					System.err.println("ERROR: " + message);
					logger.append("[ERROR] " + this.datePrefix() + message + "\n");
				}
				logger.flush();
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
		else
		{
			System.err.println("Logging is not enabled yet!");
		}
	}

	public String doLogin(Socket client, String name, String password, ClientHandler handler)
	{
		String id;
		if (!clients.containsValue(client.getInetAddress()) && !names.containsValue(name) && !ips.containsKey(client.getInetAddress()) && !dbManager.isUserOnline(name))
		{
			if (dbManager.checkPassword(name, password))
			{
				do
				{
					id = makeId();
				} while (clients.containsKey(id));
				clients.put(id, client);
				names.put(id, name);
				ips.put(client.getInetAddress(), id);
				ids.put(name, id);
				passwords.put(id, password);
				clientHandlers.put(id, handler);
				dbManager.setOnlineStatus(name, true);
				handler.setEncryptionUtility(new EncryptionUtility(password));
				handler.setID(id);
				handler.start();
				log("Logged in from " + client.getInetAddress() + " with ID " + id, LogLevel.INFO);
				return "succ_login " + id;
			}
			else
			{
				return "err_login_credentials";
			}
		}
		else if (names.containsValue(name) || dbManager.isUserOnline(name))
		{
			log("User already online, rejecting login.", LogLevel.INFO);
			return "err_login_user";
		}
		else if (ips.containsKey(client.getInetAddress()))
		{
			log("IP already connected, rejecting login.", LogLevel.INFO);
			return "err_login_ip";
		}
		else
		{
			log("Unkown error in login process!", LogLevel.ERROR);
			return "err_login_unkown";
		}
	}

	public void doShutdown()
	{
		broadcastMessage("Server shutdown, all logging out!");
		cmdHandler.removeCommand(cmdBroadcast);
		cmdHandler.removeCommand(cmdSend);
		cmdHandler.removeCommand(cmdShow);
		cmdHandler.removeCommand(cmdShutdown);
		dbManager.shutdown();
		for (ClientHandler elem : clientHandlers.values())
		{
			elem.logout();
		}
		clientHandlers.clear();
		clientHandlers = null;
		isRunning = false;
		log("Shutdown complete.", LogLevel.INFO);
	}

	public void doLogout(InetAddress addr)
	{
		String clientID = ips.get(addr);
		String name = clientNameByID(clientID);
		sendMessage(clientID, "prog_logout");
		Socket sock = clients.get(clientID);
		ids.remove(names.get(clientID));
		names.remove(clientID);
		ips.remove(addr);
		dbManager.setOnlineStatus(name, false);
		clients.remove(clientID);
		try
		{
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
			writer.write("succ_logout");
			writer.flush();
			sock.close();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String makeId()
	{
		idGenerator = new Random(System.currentTimeMillis() | 5);
		String id = String.valueOf(idGenerator.nextInt(9998) + 1);
		while (id.length() < 4)
		{
			String tmp = "0";
			id = tmp + id;
		}
		return id;
	}

	public Object[] listClients()
	{
		return names.keySet().toArray();
	}

	public String clientNameByID(String id)
	{
		return names.get(id);
	}
	
	public StrongTextEncryptor getBasicEncryption()
	{
		return this.basicEncryption;
	}

	public String idByName(String name)
	{
		if (ids.containsKey(name))
		{
			return ids.get(name);
		}
		else
		{
			return "No client with this ID found!";
		}
	}

	public void sendMessage(String id, String message)
	{
		ClientHandler handler = clientHandlers.get(id);
		handler.getEncryptionUtility().sendEncrypted(message, handler.output);
	}

	public void broadcastMessage(String message)
	{
		for (ClientHandler handler : clientHandlers.values())
		{
			handler.getEncryptionUtility().sendEncrypted(message, handler.output);
		}
	}

	private String datePrefix()
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd | HH:mm:ss");
		Date currentTime = new Date(System.currentTimeMillis());
		return "[" + format.format(currentTime) + "] ";
	}

	// GETTER & SETTER
	public HashMap<String, Socket> getClients()
	{
		return clients;
	}

	public void setClients(HashMap<String, Socket> clients)
	{
		this.clients = clients;
	}

	public HashMap<String, String> getNames()
	{
		return names;
	}

	public void setNames(HashMap<String, String> names)
	{
		this.names = names;
	}

	public HashMap<InetAddress, String> getIps()
	{
		return ips;
	}

	public void setIps(HashMap<InetAddress, String> ips)
	{
		this.ips = ips;
	}

	public HashMap<String, String> getIds()
	{
		return ids;
	}

	public void setIds(HashMap<String, String> ids)
	{
		this.ids = ids;
	}

	public CommandHandler getCommandHandler()
	{
		return cmdHandler;
	}

	public DBManager getDBManager()
	{
		return dbManager;
	}
	// ----------------------------------------------------
}
