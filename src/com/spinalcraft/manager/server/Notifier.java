package com.spinalcraft.manager.server;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.spinalcraft.manager.server.component.ApplicationManager;

public class Notifier implements Runnable{

	@Override
	public void run() {
		while(true){			
			try {
				Thread.sleep(5000);
				ArrayList<Application> applications = getNewApplications();
				for(Application application : applications){
					PushNotification notification = new PushNotification();
					Gson gson = new Gson();
					notification.message = gson.toJson(application);
					notification.send();
				}
			} catch (InterruptedException | IOException | SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	private ArrayList<Application> getNewApplications() throws SQLException{
		String query;
		PreparedStatement stmt;
//		query = "DELETE FROM pendingNotification WHERE seen = 1";
//		stmt = Database.getInstance().prepareStatement(query);
//		stmt.execute();
		
		query = "UPDATE pendingNotification SET seen = 1";
		stmt = Database.getInstance().prepareStatement(query);
		stmt.execute();
		
		query = "SELECT * FROM pendingNotification WHERE seen = 1";
		stmt = Database.getInstance().prepareStatement(query);
		ResultSet rs = stmt.executeQuery();
		ArrayList<Application> applications = new ArrayList<Application>();
		while(rs.next()){
			applications.add(ApplicationManager.getApplication(rs.getString("uuid")));
		}
		return applications;
	}
}
