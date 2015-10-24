package com.spinalcraft.manager.server;

import com.spinalcraft.easycrypt.messenger.Messenger;
import com.spinalcraft.manager.server.communication.Listener;
import com.spinalcraft.manager.server.component.RefreshUsernames;

public class Main {
	public final static String dbName = "Manager";
	private final static boolean shouldShowDebug = true;

	public static void main(String[] args){
		Messenger.shouldShowDebug = shouldShowDebug;
		Database.getInstance().init(dbName);
		(new Thread(new RefreshUsernames())).start();
		(new Thread(new Listener())).start();
		(new Thread(new Notifier())).start();
	}
	
	public static void debug(String string){
		if(shouldShowDebug){
			System.out.println(string);
		}
	}
}
