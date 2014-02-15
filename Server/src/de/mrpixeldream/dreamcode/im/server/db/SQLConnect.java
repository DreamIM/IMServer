package de.mrpixeldream.dreamcode.im.server.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * A simple MySQL Connection class to maintain easy access to databases which are MySQL based
 *
 * @author Niklas Schöllhorn (MrPixelDream)
 * @version 1.0
 *
 */
public class SQLConnect
{
	Statement stmt;
	Connection con;
	
	/**
	 * The standard constructor, should not be used in programs implementing this API
	 *
	 * @deprecated Only for being loaded by some classloaders like the Bukkit class loader
	 *
	 */
	@Deprecated
	public SQLConnect()
	{
		System.out.println("SQLConnect API loading...");
		System.out.println("Don't use this constructor for connecting to a server!");
	}
	
	/**
	 * The main constructor to create a connection to a database
	 * @param server Server to connect to
	 * @param user User for the MySQL server
	 * @param password Password for the MySQL user
	 * @param database MySQL database name to be used
	 * @param port MySQL server port (default: 3306)
	 */
	public SQLConnect(String server, String user, String password, String database, int port)
	{
		String connectionString = "";
		if (!server.startsWith("jdbc:mysql://"))
		{
			connectionString = ( "jdbc:mysql://" + server + ":" + port + "/" + database);
		}
		else
		{
			connectionString = server + ":" + port + "/" + database;
		}
		try
		{
			con = DriverManager.getConnection(connectionString, user, password);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			if (e.getMessage().contains("denied"))
			{
				System.err.println("[MySQL-Reader] CAN'T CONNECT! WRONG USER AND/OR PASSWORD!");
				return;
			}
			return;
		}
		try
		{
			stmt = con.createStatement();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			System.err.println("[MySQL-Reader] AN UNKNOWN ERROR OCCURRED!");
			return;
		}
		return;
	}
	
	/**
	 * Executes the given query on the database and returns a ResultSet or null
	 * @param query The query to be executed in the database
	 * @return The result of the given query, null if the query was a database update
	 */
	public ResultSet query(String query)
	{
		if (con == null)
		{
			System.err.println("No connection found, check credentials!");
			return null;
		}
		if (query.toUpperCase().startsWith("SELECT"))
		{
			try
			{
				return stmt.executeQuery(query);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			try
			{
				stmt.executeUpdate(query);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			return null;
		}
		return null;
	}
	
	/**
	 * Disconnects from the database and cleans up all used objects
	 */
	public void disconnect()
	{
		try
		{
			//con.commit();
			con.close();
			stmt.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		con = null;
		stmt = null;
	}
}