package com.spinalcraft.manager.server.communication;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.spinalcraft.manager.server.ManagerService;

public class Listener implements Runnable {
	
	@Override
	public void run(){
		ServerSocket socket = null;
		Socket conn = null;
		
		ManagerService service = (ManagerService)new ManagerService(Crypt.getInstance())
				.setIdentity("manager")
				.setServiceAddress("mc.spinalcraft.com")
				.setPort(9495);
		if(!service.init("auth.spinalcraft.com", 9494, "1234")){
			System.err.println("Failed to register with authentication server! Exiting...");
			return;
		}
		try {
			socket = new ServerSocket(9495);
			while(true){
				conn = socket.accept();
				new Thread(new ClientHandler(conn, service)).start();
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
