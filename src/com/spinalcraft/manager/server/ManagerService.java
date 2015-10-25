package com.spinalcraft.manager.server;

import java.net.Socket;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.spinalcraft.berberos.service.BerberosService;
import com.spinalcraft.easycrypt.EasyCrypt;
import com.spinalcraft.easycrypt.messenger.MessageReceiver;
import com.spinalcraft.easycrypt.messenger.MessageSender;
import com.spinalcraft.manager.server.communication.messenger.Receiver;
import com.spinalcraft.manager.server.communication.messenger.Sender;

public class ManagerService extends BerberosService{

	public ManagerService(EasyCrypt crypt) {
		super(crypt);
	}

	@Override
	protected boolean authenticatorCached(String authenticator) {
		String query = "SELECT * FROM authenticators WHERE authenticator = ?";
		PreparedStatement stmt;
		try {
			stmt = Database.getInstance().prepareStatement(query);
			stmt.setString(1, authenticator);
			ResultSet rs = stmt.executeQuery();
			return rs.first();
		} catch (SQLException e) {
			e.printStackTrace();
			//Default to true for security
			return true;
		}
	}

	@Override
	protected boolean cacheAuthenticator(String authenticator) {
		String query = "INSERT INTO authenticators (authenticator) VALUES (?)";
		try {
			PreparedStatement stmt = Database.getInstance().prepareStatement(query);
			stmt.setString(1, authenticator);
			stmt.execute();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	protected void storeSecretKey(String secretKey) {
		String query = "INSERT INTO masterKey (secretKey) VALUES (?)";
		try {
			PreparedStatement stmt = Database.getInstance().prepareStatement(query);
			stmt.setString(1, secretKey);
			stmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected String retrieveSecretKey() {
		String query = "SELECT secretKey FROM masterKey LIMIT 1";
		try {
			PreparedStatement stmt = Database.getInstance().prepareStatement(query);
			ResultSet rs = stmt.executeQuery();
			if(!rs.first())
				return null;
			return rs.getString("secretKey");
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public MessageSender getSender(Socket socket, EasyCrypt crypt) {
		return new Sender(socket, crypt);
	}

	@Override
	public MessageReceiver getReceiver(Socket socket, EasyCrypt crypt) {
		return new Receiver(socket, crypt);
	}
}
