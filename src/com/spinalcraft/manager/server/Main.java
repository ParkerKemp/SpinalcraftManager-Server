package com.spinalcraft.manager.server;

import com.spinalcraft.easycrypt.messenger.Messenger;

public class Main {

	public static void main(String[] args){
		Messenger.shouldShowDebug = true;
		(new Thread(new Listener())).start();
		System.out.println("Waiting...");
	}
}
