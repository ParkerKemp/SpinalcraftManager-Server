package com.spinalcraft.manager.server.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import com.spinalcraft.easycrypt.messenger.MessageReceiver;

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
		String intent = receiver.get("intent");
		switch(intent){
		case "access":
			(new AccessRequest(receiver, printer)).process();
			break;
		}
	}
}



