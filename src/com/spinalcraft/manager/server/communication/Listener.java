package com.spinalcraft.manager.server.communication;

import com.spinalcraft.berberos.service.ServiceAmbassador;
import com.spinalcraft.manager.server.ManagerService;

public class Listener implements Runnable {
	
	@Override
	public void run(){
		
		ManagerService service = (ManagerService)new ManagerService(Crypt.getInstance())
				.setIdentity("manager")
				.setServiceAddress("mc.spinalcraft.com")
				.setPort(9495);
		
		if(!service.init("373pprp6m0r2aqkd765u1suqdo")){
			System.err.println("Failed to register with authentication server! Exiting...");
			return;
		}
		
		while(true){
			System.out.println("Waiting for ambassador");
			ServiceAmbassador ambassador = service.getAmbassador();
			System.out.println("Got ambassador");
			if(ambassador != null)
				new Thread(new ClientHandler(ambassador)).start();
		}
	}
}
