package com.spinalcraft.manager.server;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ApplicationManager {
	public static ArrayList<Application> getApplications(String filter){
		ArrayList<Application> applications = new ArrayList<Application>();

		String query = "SELECT username, country, year, heard, email, comment, UNIX_TIMESTAMP(timestamp) as ts FROM applications ";
		String where = "";
		switch(filter){
		case "all":
			break;
		case "pending":
			where = "WHERE status = 0";
			break;
		case "accepted":
			where = "WHERE status = 1";
			break;
		case "declined":
			where = "WHERE status = 2";
			break;
		default:
			return applications;
		}
		
		try {
			PreparedStatement stmt = Database.getInstance().prepareStatement(query + where);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				Application application = new Application();
				application.username = rs.getString("username");
				application.country = rs.getString("country");
				application.year = rs.getInt("year");
				application.heard = rs.getString("heard");
				application.email = rs.getString("email");
				application.timestamp = rs.getInt("ts");
				application.comment = rs.getString("comment");
				applications.add(application);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return applications;
	}
}
