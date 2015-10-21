package com.spinalcraft.manager.server.component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.spinalcraft.manager.server.Application;
import com.spinalcraft.manager.server.Database;

public class ApplicationManager {
	public static ArrayList<Application> getApplications(String filter){

		ArrayList<Application> applications = new ArrayList<Application>();

		String query = "SELECT uuid, username, country, year, heard, email, comment, status, staffActor, UNIX_TIMESTAMP(actionTimestamp) as ats, UNIX_TIMESTAMP(timestamp) as ts FROM applications ";
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
				application.uuid = rs.getString("uuid");
				application.username = rs.getString("username");
				application.country = rs.getString("country");
				application.year = rs.getInt("year");
				application.heard = rs.getString("heard");
				application.email = rs.getString("email");
				application.status = rs.getInt("status");
				application.staffActor = rs.getString("staffActor");
				application.actionTimestamp = rs.getInt("ats");
				application.timestamp = rs.getInt("ts");
				application.comment = rs.getString("comment");
				applications.add(application);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return applications;
	}

	public static Application getApplication(String uuid) throws SQLException{
		String query = "SELECT * FROM applications WHERE uuid = ?";
		PreparedStatement stmt = Database.getInstance().prepareStatement(query);
		stmt.setString(1, uuid);
		ResultSet rs = stmt.executeQuery();
		if(!rs.first())
			return null;
		
		Application application = new Application();
		application.uuid = rs.getString("uuid");
		application.username = rs.getString("username");
		application.country = rs.getString("country");
		application.year = rs.getInt("year");
		application.heard = rs.getString("heard");
		application.email = rs.getString("email");
		application.status = rs.getInt("status");
		application.staffActor = rs.getString("staffActor");
		application.actionTimestamp = rs.getInt("ats");
		application.timestamp = rs.getInt("ts");
		application.comment = rs.getString("comment");
		
		return application;
	}
	
	public static Application completeApplication(String uuid, boolean accept, String staffIdentity){

		try {
			lockTable();
			if(!applicationNotComplete(uuid)){
				unlockTable();
				return getApplication(uuid);
			}
			
			updateApplication(uuid, accept, staffIdentity);
			
			unlockTable();
			return getApplication(uuid);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		unlockTable();
		return null;
	}
	
	private static void lockTable() throws SQLException{
		String query = "LOCK TABLES applications WRITE";
		PreparedStatement stmt = Database.getInstance().prepareStatement(query);
		stmt.execute();
	}
	
	private static void unlockTable(){
		String query = "UNLOCK TABLES";
		PreparedStatement stmt;
		try {
			stmt = Database.getInstance().prepareStatement(query);
			stmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static void updateApplication(String uuid, boolean accept, String staffIdentity) throws SQLException{
		String query = "UPDATE applications SET status = ?, staffActor = ?, actionTimestamp = CURRENT_TIMESTAMP WHERE uuid = ?";
		PreparedStatement stmt = Database.getInstance().prepareStatement(query);
		stmt.setInt(1, accept ? 1 : 2);
		stmt.setString(2, staffIdentity);
		stmt.setString(3, uuid);
		stmt.execute();
	}
	
	private static boolean applicationNotComplete(String uuid) throws SQLException{
		String query = "SELECT status FROM applications WHERE uuid = ?";
		PreparedStatement stmt = Database.getInstance().prepareStatement(query);
		stmt.setString(1, uuid);
		
		ResultSet rs = stmt.executeQuery();
		if(!rs.first()){
			System.out.println("Invalid uuid: " + uuid);
			return false;
		}
		
		int status = rs.getInt("status");
		if(status != 0)
			return false;
		return true;
	}
}
