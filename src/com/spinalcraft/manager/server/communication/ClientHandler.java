package com.spinalcraft.manager.server.communication;

import java.net.Socket;

import com.spinalcraft.berberos.service.ServiceAmbassador;
import com.spinalcraft.easycrypt.messenger.MessageReceiver;
import com.spinalcraft.easycrypt.messenger.MessageSender;
import com.spinalcraft.manager.server.ManagerService;

public class ClientHandler implements Runnable{
	private Socket conn;
	private ManagerService service;
	
	public ClientHandler(Socket conn, ManagerService service){
		this.conn = conn;
		this.service = service;
	}
	
	@Override
	public void run(){
		ServiceAmbassador ambassador = service.getAmbassador(conn);
		if(ambassador == null){
			System.err.println("Failed to authenticate user.");
			return;
		}
		
//		MessageReceiver receiver = ambassador.receiveMessage();
//		if(receiver == null)
//			return;
		processRequest(ambassador);
	}
	
	private void processRequest(ServiceAmbassador ambassador){
		MessageReceiver receiver = ambassador.receiveMessage();
		if(receiver == null)
			return;
		String intent = receiver.getHeader("intent");
		switch(intent){
		case "access":
			(new AccessRequest(receiver, conn)).process();
			break;
		case "message":			
			String message = receiver.getItem("message");
			System.out.println("Received Message: " + message);
			MessageSender sender = ambassador.getSender();
			sender.addItem("message", message + " to you as well");
			ambassador.sendMessage(sender);
			break;
		}
	}
}



