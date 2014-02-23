package de.mrpixeldream.dreamcode.im.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import de.mrpixeldream.dreamcode.im.server.io.EncryptionUtility;

public class ClientHandler extends Thread {

	Socket client;
    String id;
    Scanner input;
    PrintWriter output;
    
    EncryptionUtility encryptionUtil;
    
    IMServer parent;
    
    public ClientHandler(Socket client, IMServer parent)
    {
    	this.client = client;
    	this.parent = parent;
    	
    	try
        {
                this.input = new Scanner(client.getInputStream());
                this.output = new PrintWriter(client.getOutputStream());
        }
        catch (IOException e)
        {
                e.printStackTrace();
        }
    }
    
    public void setEncryptionUtility(EncryptionUtility encUtil)
    {
    	this.encryptionUtil = encUtil;
    }
    
    @Override
    public void run()
    {
    	String msg = "";
    	
    	do
    	{
    		msg = "";
    		
    		try
            {
                msg = encryptionUtil.receiveEncrypted(client); 
                System.out.println(msg);
                this.parent.getCommandHandler().handleCommand(msg, client, this);
            }
            catch (Exception e)
            {
                this.parent.log("Failed to read line from client! Logging out...", LogLevel.ERROR);
            	e.printStackTrace();
                msg = "LOGOUT";
            }
    	} while (!msg.equalsIgnoreCase("LOGOUT"));
    	
    	this.logout();
    }
    
    public void setID(String id)
    {
    	this.id = id;
    }
    
    public String getID()
    {
    	return this.id;
    }
    
    public EncryptionUtility getEncryptionUtility()
    {
    	return this.encryptionUtil;
    }
    
    public void logout()
    {
    	//this.output.println("Logging you out!");
    	this.parent.doLogout(client.getInetAddress());
    }
	
}