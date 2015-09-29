package com.spinalcraft.manager.server;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Listener implements Runnable {
	
	@Override
	public void run(){
		ServerSocket socket = null;
		Socket conn = null;
		Database db = new Database("Development");
		try {
			socket = new ServerSocket(9494);
			while(true){
				conn = socket.accept();
				PrintStream printer = new PrintStream(conn.getOutputStream());
				ArrayList<String> authors = db.getRecords();
				printer.println(authors.get(0));
//				printer.println("Hello from the server!");
				System.out.println("Got a connection!");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
