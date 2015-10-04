package com.spinalcraft.manager.server;

import com.spinalcraft.easycrypt.messenger.Messenger;
import com.spinalcraft.manager.server.communication.Listener;

public class Main {
	public final static String dbName = "Development";

	public static void main(String[] args){
		Messenger.shouldShowDebug = true;
		Database.getInstance().init(dbName);
		(new Thread(new Listener())).start();
		System.out.println("Waiting...");
	}
}
