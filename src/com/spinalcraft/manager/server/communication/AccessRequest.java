package com.spinalcraft.manager.server.communication;

import java.io.PrintStream;
import java.security.GeneralSecurityException;
import java.sql.SQLException;

import com.spinalcraft.easycrypt.messenger.MessageReceiver;
import com.spinalcraft.easycrypt.messenger.MessageSender;
import com.spinalcraft.manager.server.Main;

public class AccessRequest {
	private MessageReceiver receiver;
	private PrintStream printer;
	
	public AccessRequest(MessageReceiver receiver, PrintStream printer){
		this.receiver = receiver;
		this.printer = printer;
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
			
			MessageSender sender = new MessageSender(printer);
			sender.addHeader("status", "GOOD");
			sender.addItem("secret", Crypt.getInstance().encode(cipher));
			sender.sendMessage();
			
		} catch (GeneralSecurityException | SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void denyAccess(){
		MessageSender sender  = new MessageSender(printer);
		sender.addHeader("status", "BAD");
		sender.sendMessage();
	}
}
