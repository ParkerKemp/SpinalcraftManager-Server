package com.spinalcraft.manager.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.JsonObject;

public class Notifier implements Runnable{

	@Override
	public void run() {
		while(true){			
			try {
				Thread.sleep(5000);
				HttpURLConnection connection = createConnection();
				JsonObject data = new JsonObject();
				data.addProperty("message", "Test Message");
				JsonObject obj = new JsonObject();
				obj.addProperty("to", "/topics/applications");
				obj.add("data", data);
				writeBody(connection, obj.toString());
				
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String response = reader.readLine();
				System.out.println("Response: " + response);
				
			} catch (InterruptedException | IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void writeBody(HttpURLConnection connection, String body) throws IOException {
		OutputStream stream = connection.getOutputStream();
		stream.write(body.getBytes());
		stream.flush();
		stream.close();
	}

	private static HttpURLConnection createConnection() throws IOException {
		URL url = new URL("https://gcm-http.googleapis.com/gcm/send");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestProperty("Authorization:", "key=AIzaSyDWU3jB2LidRRoD4mj-BVfcD_r7DDHg93k");
//		connection.setUseCaches(false);
		connection.setDoInput(true);
		connection.setDoOutput(true);
		return connection;
	}
}
