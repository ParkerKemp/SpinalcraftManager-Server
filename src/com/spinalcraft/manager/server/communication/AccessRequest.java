package com.spinalcraft.manager.server.communication;

import java.net.Socket;
import java.security.GeneralSecurityException;
import java.sql.SQLException;

import com.spinalcraft.easycrypt.messenger.MessageReceiver;
import com.spinalcraft.manager.server.Main;
import com.spinalcraft.manager.server.communication.messenger.Sender;

public class AccessRequest {
	private MessageReceiver receiver;
	private Socket socket;
	
	public AccessRequest(MessageReceiver receiver, Socket socket){
		this.receiver = receiver;
		this.socket = socket;
	}
	
	public void process(){
		String accessKey = receiver.getItem("accessKey");
		try {
			Actor actor = Actor.getFromUnclaimedAccessKey(accessKey);
			if(actor != null)
				grantAccess(actor);
			else
				denyAccess();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void grantAccess(Actor actor){
		try {
			String publicKeyString = receiver.getHeader("publicKey");
			actor.publicKey = Crypt.getInstance().loadPublicKey(publicKeyString);
			actor.secretKey = Crypt.getInstance().generateSecretKey();
			actor.updateWithApproval();
			
			Main.debug("Granting access for actor: " + actor.name);
			byte[] cipher = Crypt.getInstance().encryptKey(actor.publicKey, actor.secretKey);
			
			Sender sender = new Sender(socket, Crypt.getInstance());
			sender.addHeader("status", "GOOD");
			sender.addItem("secret", Crypt.getInstance().encode(cipher));
			sender.sendMessage();
			
		} catch (GeneralSecurityException | SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void denyAccess(){
		Sender sender  = new Sender(socket, Crypt.getInstance());
		sender.addHeader("status", "BAD");
		sender.sendMessage();
	}
}
