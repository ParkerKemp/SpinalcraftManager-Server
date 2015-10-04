package com.spinalcraft.manager.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.HashMap;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ClientHandler implements Runnable{
	private Socket conn;
	private PrintStream printer;
	
	public ClientHandler(Socket conn){
		this.conn = conn;
	}
	
	@Override
	public void run(){
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			printer = new PrintStream(conn.getOutputStream());
			
//			String request = reader.readLine();
			parseRequest(reader);
//			System.out.println(request);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void parseRequest(BufferedReader reader){
		int numLines;
		try {
			numLines = Integer.parseInt(reader.readLine());
			HashMap<String, String> request = new HashMap<String, String>();
			String line;
			for(int i = 0; i < numLines; i++){
				line = reader.readLine();
				String tokens[] = line.split(":");
				request.put(tokens[0], tokens[1]);
			}
			
			String intent = request.get("intent");
			processRequest(intent, request);
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
	}
	
	private void processRequest(String intent, HashMap<String, String> request){
		switch(intent){
		case "access":
			(new AccessRequest(request, printer)).process();
			break;
		}
	}
}



