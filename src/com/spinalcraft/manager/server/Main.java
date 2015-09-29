package com.spinalcraft.manager.server;

public class Main {

	public static void main(String[] args){
		(new Thread(new Listener())).start();
		System.out.println("Waiting...");
	}
}
