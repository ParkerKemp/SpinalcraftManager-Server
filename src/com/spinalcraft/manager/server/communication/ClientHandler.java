package com.spinalcraft.manager.server.communication;

import java.net.Socket;

import com.google.gson.Gson;
import com.spinalcraft.berberos.service.ServiceAmbassador;
import com.spinalcraft.easycrypt.messenger.MessageReceiver;
import com.spinalcraft.easycrypt.messenger.MessageSender;
import com.spinalcraft.manager.server.ApplicationManager;
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
		MessageSender sender = ambassador.getSender();
		switch(intent){
		case "applicationList":
			
			break;
		case "message":			
			String message = receiver.getItem("message");
			System.out.println("Received Message: " + message);
			sender = ambassador.getSender();
			sender.addItem("message", message + " to you as well");
			ambassador.sendMessage(sender);
			break;
		}
	}
	
	private void sendApplicationList(MessageReceiver receiver, ServiceAmbassador ambassador){
		String filter = receiver.getItem("filter");
		MessageSender sender = ambassador.getSender();
		Gson gson = new Gson();
		String json = gson.toJson(ApplicationManager.getApplications(filter));
		System.out.println(json);
		sender = ambassador.getSender();
		sender.addHeader("status", "good");
		sender.addItem("applications", json);
		ambassador.sendMessage(sender);
	}
}



