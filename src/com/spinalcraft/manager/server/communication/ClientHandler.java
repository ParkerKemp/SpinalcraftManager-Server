package com.spinalcraft.manager.server.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.sql.SQLException;

import com.spinalcraft.easycrypt.messenger.MessageReceiver;
import com.spinalcraft.easycrypt.messenger.MessageSender;

public class ClientHandler implements Runnable{
	private Socket conn;
	private PrintStream printer;
	
	public ClientHandler(Socket conn){
		this.conn = conn;
	}
	
	@Override
	public void run(){
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			printer = new PrintStream(conn.getOutputStream());
			MessageReceiver receiver = new MessageReceiver(reader);
			receiver.receiveMessage();
			processRequest(receiver);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void processRequest(MessageReceiver receiver){
		String intent = receiver.getHeader("intent");
		switch(intent){
		case "access":
			(new AccessRequest(receiver, printer)).process();
			break;
		case "message":
			String publicKey = receiver.getHeader("publicKey");
			System.out.println("Got Public Key: " + publicKey);
			Actor actor;
			try {
				actor = Actor.getFromPublicKey(publicKey);
				if(receiver.needsSecretKey()){
					receiver.decrypt(actor.secretKey, Crypt.getInstance());
				}
				String message = receiver.getItem("message");
				System.out.println("Received Message: " + message);
				MessageSender sender = new MessageSender(printer);
				sender.addItem("message", message + " to you as well");
				sender.sendEncrypted(actor.secretKey, Crypt.getInstance());
			} catch (SQLException e) {
				e.printStackTrace();
			}
			break;
		}
	}
}



