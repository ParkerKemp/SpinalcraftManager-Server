package com.spinalcraft.manager.server.communication;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

import org.newsclub.net.unix.AFUNIXSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;

import com.google.gson.Gson;
import com.spinalcraft.berberos.service.ServiceAmbassador;
import com.spinalcraft.easycrypt.messenger.MessageReceiver;
import com.spinalcraft.easycrypt.messenger.MessageSender;
import com.spinalcraft.manager.server.Application;
import com.spinalcraft.manager.server.ManagerService;
import com.spinalcraft.manager.server.component.ApplicationManager;

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
			sendApplicationList(receiver, ambassador);
			break;
		case "applicationAnswer":
			processApplicationAnswerRequest(receiver, ambassador);
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
		sender = ambassador.getSender();
		sender.addHeader("status", "good");
		sender.addItem("applications", json);
		ambassador.sendMessage(sender);
	}
	
	private void processApplicationAnswerRequest(MessageReceiver receiver, ServiceAmbassador ambassador){
		String identity = receiver.getHeader("identity");
		String uuid = receiver.getItem("uuid");
		boolean accept = Boolean.parseBoolean(receiver.getItem("accept"));
		MessageSender sender = ambassador.getSender();
		Application application = ApplicationManager.completeApplication(uuid, accept, identity);
		if(application != null){
			if(accept && whitelistAdd(application.username))
				notifyApplicant(application.email, application.username);
			sender.addHeader("status", "good");
			Gson gson = new Gson();
			sender.addItem("application", gson.toJson(application));
			ambassador.sendMessage(sender);
		}
		else{
			sender.addHeader("status", "bad");
			ambassador.sendMessage(sender);
		}
	}
	
	private void notifyApplicant(String email, String username){
		Runtime rt = Runtime.getRuntime();
		try {
			rt.exec("/usr/local/sbin/notifyAcceptedApplication " + email + " " + username);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private boolean whitelistAdd(String username){
		File socketFile = new File("/home/minecraft/server/dev/plugins/Spinalpack/sockets/command.sock");
		try {
			AFUNIXSocket socket = AFUNIXSocket.newInstance();
			socket.connect(new AFUNIXSocketAddress(socketFile));
			PrintStream printer = new PrintStream(socket.getOutputStream());
			printer.print("whitelist add " + username);
			socket.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}


