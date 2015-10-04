package com.spinalcraft.manager.server;

import java.io.PrintStream;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.HashMap;

import javax.crypto.SecretKey;

import com.google.gson.JsonObject;

public class AccessRequest {
	Crypt crypt;
	HashMap<String, String> request;
	PrintStream printer;
	
	public AccessRequest(HashMap<String, String> request, PrintStream printer){
		this.request = request;
		this.printer = printer;
		crypt = new Crypt();
	}
	
	public void process(){
		String accessKey = request.get("accessKey");
		if(accessKey.equals("derp")){
			grantAccess();
			
		}
		else{
			printer.println("ACCESS DENIED");
		}
	}
	
	private void grantAccess(){
		try {
			String publicKeyString = request.get("publicKey");
			System.out.println(publicKeyString);
			PublicKey publicKey = crypt.loadPublicKey(publicKeyString);
//			printer.println("Access granted");
			SecretKey secretKey = crypt.generateSecretKey();
			System.out.println("Generated key: " + crypt.encode(secretKey.getEncoded()));
			byte[] cipher = crypt.encryptKey(publicKey, secretKey);
			System.out.println("Key cipher: " + crypt.encode(cipher));
			
			JsonObject obj = new JsonObject();
			obj.addProperty("status", "GOOD");
			obj.addProperty("secret", crypt.encode(cipher));
			
			printer.println(obj.toString());
			
			
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
	}
}
