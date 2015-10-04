package com.spinalcraft.manager.server;

import java.util.Base64;

import com.spinalcraft.easycrypt.EasyCrypt;

public class Crypt extends EasyCrypt{
	public byte[] decode(String str){
		return Base64.getDecoder().decode(str);
	}
	
	public String encode(byte[] bytes){
		return Base64.getEncoder().encodeToString(bytes);
	}
}
