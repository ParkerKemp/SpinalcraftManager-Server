package com.spinalcraft.manager.server;

import com.spinalcraft.easycrypt.messenger.Messenger;
import com.spinalcraft.manager.server.communication.Crypt;
import com.spinalcraft.manager.server.component.RefreshUsernames;

public class Main {
	public final static String dbName = "Manager";
	private final static boolean shouldShowDebug = true;
	
	private static ManagerService service;

	public static void main(String[] args){
		Messenger.shouldShowDebug = shouldShowDebug;
		Database.getInstance().init(dbName);
		(new Thread(new RefreshUsernames())).start();
//		(new Thread(new Listener())).start();
		(new Thread(new Notifier())).start();
		
		initService();
		service.serve();
	}
	
	private static void initService(){
		service = (ManagerService)new ManagerService(Crypt.getInstance())
				.setIdentity("manager")
				.setServiceAddress("mc.spinalcraft.com")
				.setPort(9495);
		
		if(!service.init("373pprp6m0r2aqkd765u1suqdo")){
			System.err.println("Failed to register with authentication server! Exiting...");
			return;
		}
	}
	
	public static void debug(String string){
		if(shouldShowDebug){
			System.out.println(string);
		}
	}
}
