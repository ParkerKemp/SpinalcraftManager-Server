package com.spinalcraft.manager.server;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.newsclub.net.unix.AFUNIXSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;

import com.google.gson.Gson;
import com.spinalcraft.berberos.service.BerberosService;
import com.spinalcraft.berberos.service.ServiceAmbassador;
import com.spinalcraft.easycrypt.EasyCrypt;
import com.spinalcraft.easycrypt.messenger.MessageReceiver;
import com.spinalcraft.easycrypt.messenger.MessageSender;
import com.spinalcraft.manager.server.communication.messenger.Receiver;
import com.spinalcraft.manager.server.communication.messenger.Sender;
import com.spinalcraft.manager.server.component.ApplicationManager;

public class ManagerService extends BerberosService{

	public ManagerService(EasyCrypt crypt) {
		super("auth.spinalcraft.com", 9494, crypt);
	}

	@Override
	protected void onAuthenticated(ServiceAmbassador ambassador) {
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
			sendApplicationList(ambassador, receiver);
			break;
		case "applicationAnswer":
			processApplicationAnswerRequest(ambassador, receiver);
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
	
	private void sendApplicationList(ServiceAmbassador ambassador, MessageReceiver receiver){
		String filter = receiver.getItem("filter");
		MessageSender sender = ambassador.getSender();
		Gson gson = new Gson();
		String json = gson.toJson(ApplicationManager.getApplications(filter));
		sender = ambassador.getSender();
		sender.addHeader("status", "good");
		sender.addItem("applications", json);
		ambassador.sendMessage(sender);
	}
	
	private void processApplicationAnswerRequest(ServiceAmbassador ambassador, MessageReceiver receiver){
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

	@Override
	public boolean authenticatorCached(String authenticator) {
		String query = "SELECT * FROM authenticators WHERE authenticator = ?";
		PreparedStatement stmt;
		try {
			stmt = Database.getInstance().prepareStatement(query);
			stmt.setString(1, authenticator);
			ResultSet rs = stmt.executeQuery();
			return rs.first();
		} catch (SQLException e) {
			e.printStackTrace();
			//Default to true for security?
			return true;
		}
	}

	@Override
	public boolean cacheAuthenticator(String authenticator) {
		String query = "INSERT INTO authenticators (authenticator) VALUES (?)";
		try {
			PreparedStatement stmt = Database.getInstance().prepareStatement(query);
			stmt.setString(1, authenticator);
			stmt.execute();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	protected void storeSecretKey(String secretKey) {
		String query = "INSERT INTO masterKey (secretKey) VALUES (?)";
		try {
			PreparedStatement stmt = Database.getInstance().prepareStatement(query);
			stmt.setString(1, secretKey);
			stmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected String retrieveSecretKey() {
		String query = "SELECT secretKey FROM masterKey LIMIT 1";
		try {
			PreparedStatement stmt = Database.getInstance().prepareStatement(query);
			ResultSet rs = stmt.executeQuery();
			if(!rs.first())
				return null;
			return rs.getString("secretKey");
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public MessageSender getSender(Socket socket, EasyCrypt crypt) {
		return new Sender(socket, crypt);
	}

	@Override
	public MessageReceiver getReceiver(Socket socket, EasyCrypt crypt) {
		return new Receiver(socket, crypt);
	}
}
