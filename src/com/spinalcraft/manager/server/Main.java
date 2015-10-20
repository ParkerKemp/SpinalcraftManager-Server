package com.spinalcraft.manager.server;

import com.spinalcraft.easycrypt.messenger.Messenger;
import com.spinalcraft.manager.server.communication.Listener;

public class Main {
	public final static String dbName = "Manager";
	private final static boolean shouldShowDebug = true;

	public static void main(String[] args){
		Messenger.shouldShowDebug = shouldShowDebug;
		Database.getInstance().init(dbName);
		(new Thread(new RefreshUsernames())).start();
		(new Thread(new Listener())).start();
		System.out.println("Waiting...");
	}
	
	public static void debug(String string){
		if(shouldShowDebug){
			System.out.println(string);
		}
	}
}
