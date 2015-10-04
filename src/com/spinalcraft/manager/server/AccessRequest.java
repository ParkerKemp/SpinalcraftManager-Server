package com.spinalcraft.manager.server;

import java.io.PrintStream;
import java.security.GeneralSecurityException;
import java.security.PublicKey;

import javax.crypto.SecretKey;

import org.apache.commons.lang3.StringEscapeUtils;

import com.google.gson.JsonObject;
import com.spinalcraft.easycrypt.messenger.MessageReceiver;
import com.spinalcraft.easycrypt.messenger.MessageSender;

public class AccessRequest {
	private Crypt crypt;
	private MessageReceiver receiver;
	private PrintStream printer;
	
	public AccessRequest(MessageReceiver receiver, PrintStream printer){
		this.receiver = receiver;
		this.printer = printer;
		crypt = new Crypt();
	}
	
	public void process(){
		String accessKey = receiver.get("accessKey");
		if(accessKey.equals("derp")){
			grantAccess();	
		}
		else{
			printer.println("ACCESS DENIED");
		}
	}
	
	private void grantAccess(){
		try {
			String publicKeyString = StringEscapeUtils.unescapeJava(receiver.get("publicKey"));
			System.out.println(publicKeyString);
			PublicKey publicKey = crypt.loadPublicKey(publicKeyString);
			SecretKey secretKey = crypt.generateSecretKey();
			System.out.println("Generated key: " + crypt.encode(secretKey.getEncoded()));
			byte[] cipher = crypt.encryptKey(publicKey, secretKey);
			System.out.println("Key cipher: " + crypt.encode(cipher));
			
			MessageSender sender = new MessageSender(printer);
			sender.add("status", "GOOD");
			sender.add("secret", crypt.encode(cipher));
			sender.sendMessage();
//			JsonObject obj = new JsonObject();
//			obj.addProperty("status", "GOOD");
//			obj.addProperty("secret", crypt.encode(cipher));
//			
//			printer.println(obj.toString();
			
			
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
	}
}
