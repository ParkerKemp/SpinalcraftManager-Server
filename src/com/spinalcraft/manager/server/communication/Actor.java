package com.spinalcraft.manager.server.communication;

import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.crypto.SecretKey;

import com.spinalcraft.manager.server.Database;

public class Actor {
	public int id;
	public String name;
	public PublicKey publicKey;
	public SecretKey secretKey;

	public static Actor getFromUnclaimedAccessKey(String key) throws SQLException{
		String query = "SELECT * FROM manager_accessKeys k JOIN manager_actors a ON k.actor_id = a.id "
				+ "WHERE k.accessKey = ?";
		
		PreparedStatement stmt = Database.getInstance().prepareStatement(query);
		
		stmt.setString(1, key);
		
		ResultSet rs = stmt.executeQuery();
		if(!rs.first()){
			return null;
		}
		
		Actor actor = new Actor();
		actor.id = rs.getInt("a.id");
		actor.name = rs.getString("a.name");
		
		return actor;
	}
	
	public static Actor getFromPublicKey(String publicKey) throws SQLException{
		String query = "SELECT * FROM manager_actors WHERE publicKey = ?";
		PreparedStatement stmt = Database.getInstance().prepareStatement(query);
		stmt.setString(1, publicKey);
		ResultSet rs = stmt.executeQuery();
		if(!rs.first()){
			return null;
		}
		Actor actor = new Actor();
		
		actor.id = rs.getInt("id");
		actor.name = rs.getString("name");
		try {
			actor.publicKey = Crypt.getInstance().loadPublicKey(publicKey);
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
			return null;
		}
		actor.secretKey = Crypt.getInstance().loadSecretKey(rs.getString("secretKey"));
		
		return actor;
	}
	
	public String getPublicKeyAsString(){
		try {
			return Crypt.getInstance().stringFromPublicKey(publicKey);
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getSecretKeyAsString(){
		return Crypt.getInstance().stringFromSecretKey(secretKey);
	}
	
	public void updateWithApproval() throws SQLException{
		String query = "UPDATE manager_actors "
				+ "SET publicKey = ?,"
				+ "secretKey = ?"
				+ "WHERE id = ?";
		PreparedStatement stmt = Database.getInstance().prepareStatement(query);
		stmt.setString(1, getPublicKeyAsString());
		stmt.setString(2, getSecretKeyAsString());
		stmt.setInt(3, id);
		stmt.execute();
		
		updateWithClaimedAccessKey();
	}
	
	private void updateWithClaimedAccessKey() throws SQLException{
		String query = "UPDATE manager_accessKeys "
				+ "SET claimed = 1"
				+ "WHERE actor_id = ?";
		PreparedStatement stmt = Database.getInstance().prepareStatement(query);
		stmt.setInt(1, id);
		stmt.execute();
	}
}
