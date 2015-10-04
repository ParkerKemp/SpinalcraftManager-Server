package com.spinalcraft.manager.server;

import java.sql.Connection;
import java.sql.DriverManager;
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
		} catch (SQLException e) {
			e.printStackTrace();
		}
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
	
	private void connect() throws SQLException{
		conn = DriverManager.getConnection("jdbc:mysql://localhost", "root", "password");
		Statement stmt = conn.createStatement();
		String query = "USE " + dbName;
		stmt.execute(query);
	}
}
