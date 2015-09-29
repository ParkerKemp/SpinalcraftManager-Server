package com.spinalcraft.manager.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Listener implements Runnable {
	
	@Override
	public void run(){
		ServerSocket socket = null;
		Socket conn = null;
		try {
			socket = new ServerSocket(9494);
			while(true){
				conn = socket.accept();
				System.out.println("Got a connection!");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
