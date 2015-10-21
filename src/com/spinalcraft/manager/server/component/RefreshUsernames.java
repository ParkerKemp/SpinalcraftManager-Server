package com.spinalcraft.manager.server.component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import com.spinalcraft.manager.server.Database;
import com.spinalcraft.usernamehistory.UHistory;
import com.spinalcraft.usernamehistory.UName;
import com.spinalcraft.usernamehistory.UUIDFetcher;
import com.spinalcraft.usernamehistory.UsernameHistory;

public class RefreshUsernames implements Runnable{

	@Override
	public void run() {
		String query = "SELECT uuid, username FROM applications";
		try {
			PreparedStatement stmt = Database.getInstance().prepareStatement(query);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				checkAndUpdate(rs.getString("uuid"), rs.getString("username"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void checkAndUpdate(String uuidString, String knownUsername) throws SQLException{
		UUID uuid = UUIDFetcher.getUUIDFromString(uuidString);
		UHistory history = UsernameHistory.getHistoryFromUuid(uuid);
		UName[] names = history.getOldUsernames();
		String currentUsername = names[names.length - 1].getName();
		if(currentUsername != knownUsername){
			updateUsername(uuidString, currentUsername);
		}
	}
	
	private void updateUsername(String uuidString, String newUsername) throws SQLException{
		String query = "UPDATE applications SET username = ? WHERE uuid = ?";
		PreparedStatement stmt = Database.getInstance().prepareStatement(query);
		stmt.setString(1, newUsername);
		stmt.setString(2, uuidString);
		stmt.execute();
	}
}
