package de.mrpixeldream.dreamcode.im.server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import de.mrpixeldream.dreamcode.im.server.io.EncryptionUtility;

public class ClientHandler extends Thread {

	Socket client;
    String id;
    ObjectInputStream input;
    ObjectOutputStream output;
    
    EncryptionUtility encryptionUtil;
    
    IMServer parent;
    
    public ClientHandler(Socket client, IMServer parent, ObjectInputStream in, ObjectOutputStream out)
    {
    	this.client = client;
    	this.parent = parent;
    	
    	try
        {
                this.input = in;
                this.output = out;
        }
        catch (Exception e)
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
                msg = encryptionUtil.receiveEncrypted(input); 
                //System.out.println(msg);
    			//msg = input.nextLine();
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
    
    public ObjectOutputStream getOutput()
    {
    	return this.output;
    }
    
    public ObjectInputStream getInput()
    {
    	return this.input;
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