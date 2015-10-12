package com.spinalcraft.manager.server.communication;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

import com.spinalcraft.berberos.service.ServiceAmbassador;
import com.spinalcraft.easycrypt.messenger.MessageReceiver;
import com.spinalcraft.easycrypt.messenger.MessageSender;
import com.spinalcraft.manager.server.ManagerService;
import com.spinalcraft.manager.server.communication.messenger.Sender;

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
		if(ambassador == null)
			return;
		
		MessageReceiver receiver = ambassador.receiveMessage();
		if(receiver == null)
			return;
		processRequest(receiver);
	}
	
	private void processRequest(MessageReceiver receiver){
		String intent = receiver.getHeader("intent");
		switch(intent){
		case "access":
			(new AccessRequest(receiver, conn)).process();
			break;
		case "message":
			String publicKey = receiver.getHeader("publicKey");
			System.out.println("Got Public Key: " + publicKey);
			Actor actor;
			try {
				actor = Actor.getFromPublicKey(publicKey);
				String message = receiver.getItem("message");
				System.out.println("Received Message: " + message);
				MessageSender sender = new Sender(conn, Crypt.getInstance());
				sender.addItem("message", message + " to you as well");
				sender.sendEncrypted(actor.secretKey);
			} catch (SQLException | IOException e) {
				e.printStackTrace();
			}
			break;
		}
	}
}



