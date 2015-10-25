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
		
		if(!service.init("u5ho6n4vlukd9dsh55shqn42d6")){
			System.err.println("Failed to register with authentication server! Exiting...");
			return;
		}
		
		while(true){
			ServiceAmbassador ambassador = service.getAmbassador();
			if(ambassador != null)
				new Thread(new ClientHandler(ambassador)).start();
		}
	}
}
