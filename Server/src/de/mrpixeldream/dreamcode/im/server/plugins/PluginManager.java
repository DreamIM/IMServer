package de.mrpixeldream.dreamcode.im.server.plugins;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

import de.mrpixeldream.dreamcode.im.server.IMServer;
import de.mrpixeldream.dreamcode.im.server.LogLevel;

public class PluginManager
{

	IMServer	parent;

	public PluginManager(IMServer parent)
	{
		this.parent = parent;
	}

	public void unloadPlugins(ArrayList<ServerPlugin> plugins)
	{
		for (ServerPlugin plugin : plugins)
		{
			plugin.onUnload();
		}
	}
	
	public ArrayList<ServerPlugin> loadPlugins()
	{
		ArrayList<ServerPlugin> plugins = new ArrayList<>();

		File pluginDir = new File("plugins");

		this.parent.log("Plugin Folder: " + pluginDir.getAbsolutePath(), LogLevel.INFO);

		File[] jars = pluginDir.listFiles(new FileFilter()
		{
			public boolean accept(File pathname)
			{
				return pathname.getName().endsWith(".jar");
			}
		});

		try
		{
			for (File f : jars)
			{
				String pluginName = f.getName().split("\\.")[0];
				this.parent.log("Enabling " + pluginName, LogLevel.INFO);

				URLClassLoader loader = URLClassLoader.newInstance(new URL[] { f.toURI().toURL() });
				ResourceBundle props = ResourceBundle.getBundle("service", Locale.getDefault(), loader);
				final String pluginClassName = props.getString("dreamcode.serverplugin.Name");

				ServerPlugin plugin = (ServerPlugin) loader.loadClass(pluginClassName).newInstance();
				
				plugin.onLoad();
				
				plugins.add(plugin);

			}
		}
		catch (Exception ex)
		{
			this.parent.log("Unknown error in plugin system: \n", LogLevel.ERROR);
			ex.printStackTrace();
		}

		return plugins;
	}

}
