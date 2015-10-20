package com.spinalcraft.manager.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Database {
	private String dbName;
	private Connection conn;
	private static Database instance;
	
	private Database(){
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static Database getInstance(){
		if(instance == null){
			instance = new Database();
		}
		return instance;
	}
	
	public void init(String dbName){
		this.dbName = dbName;
		try {
			connect();
			createTables();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public PreparedStatement prepareStatement(String query) throws SQLException{
		return conn.prepareStatement(query);
	}
	
	public ArrayList<String> getRecords(){
		ArrayList<String> authors = new ArrayList<String>();
		String query = "SELECT * FROM Applications WHERE unread = 1 AND discarded = 0";
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			authors.add(rs.getString("author"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return authors;
	}
	
	public ArrayList<String> getUnclaimedAccessKeys() throws SQLException{
		ArrayList<String> keys = new ArrayList<String>();
		String query = "SELECT * FROM manager_accessKeys WHERE claimed = 0";

		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		keys.add(rs.getString("accessKey"));

		return keys;
	}
	
	private void connect() throws SQLException{
		conn = DriverManager.getConnection("jdbc:mysql://localhost", "root", "password");
		
		Statement stmt = conn.createStatement();
		String query = "CREATE DATABASE IF NOT EXISTS " + dbName;
		stmt.execute(query);
		
		stmt = conn.createStatement();
		query = "USE " + dbName;
		stmt.execute(query);
	}
	
	private void createTables() throws SQLException{
		String query = "CREATE TABLE IF NOT EXISTS actors ("
				+ "id INT PRIMARY KEY AUTO_INCREMENT, "
				+ "name VARCHAR(32) NOT NULL, "
				+ "publicKey TINYTEXT, "
				+ "secretKey VARCHAR(32) UNIQUE, "
				+ "lastTransmit INT)";
		
		Statement stmt = conn.createStatement();
		stmt.execute(query);
		
		query = "CREATE TABLE IF NOT EXISTS authenticators (id INT PRIMARY KEY AUTO_INCREMENT, "
				+ "authenticator TINYTEXT)";
		stmt = conn.createStatement();
		stmt.execute(query);
		
		query = "CREATE TABLE IF NOT EXISTS masterKey (id INT PRIMARY KEY AUTO_INCREMENT, "
				+ "secretKey TINYTEXT)";
		stmt = conn.createStatement();
		stmt.execute(query);
		
		query = "CREATE TABLE IF NOT EXISTS applications ("
				+ "uuid VARCHAR(32) PRIMARY KEY, "
				+ "username VARCHAR(32), "
				+ "country VARCHAR(64), "
				+ "year INT, "
				+ "heard VARCHAR(32), "
				+ "email VARCHAR(64), "
				+ "comment MEDIUMTEXT, "
				+ "status INT DEFAULT 0, "
				+ "staffActor VARCHAR(32), "
				+ "actionTimestamp TIMESTAMP DEFAULT NULL, "
				+ "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
		stmt = conn.createStatement();
		stmt.execute(query);
	}
}




