package com.spinalcraft.manager.server;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ApplicationManager {
	public static ArrayList<Application> getApplications(){
		ArrayList<Application> applications = new ArrayList<Application>();
		
		String query = "SELECT * FROM applications";
		try {
			PreparedStatement stmt = Database.getInstance().prepareStatement(query);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				Application application = new Application();
				application.username = rs.getString("username");
				application.country = rs.getString("country");
				application.year = rs.getInt("year");
				application.heard = rs.getString("heard");
				application.email = rs.getString("email");
				application.timestamp = rs.getTimestamp("timestamp");
				applications.add(application);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return applications;
	}
}