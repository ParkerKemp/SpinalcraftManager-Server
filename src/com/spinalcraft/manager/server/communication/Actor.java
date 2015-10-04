package com.spinalcraft.manager.server.communication;

import java.security.GeneralSecurityException;
import java.security.PublicKey;

import javax.crypto.SecretKey;

public class Actor {
	private Crypt crypt;
	
	public int id;
	public String name;
	public PublicKey publicKey;
	public SecretKey secretKey;
	
	public Actor(){
		crypt = new Crypt();
	}
	
	public String getPublicKeyAsString(){
		try {
			return crypt.stringFromPublicKey(publicKey);
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getSecretKeyAsString(){
		return crypt.stringFromSecretKey(secretKey);
	}
}
