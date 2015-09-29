package com.spinalcraft.manager.server;

public class Main {

	public static void main(String[] args){
//		Listener listener = new Listener();
		(new Thread(new Listener())).start();
		System.out.println("started listener");
	}
}
