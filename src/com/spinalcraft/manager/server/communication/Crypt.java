package com.spinalcraft.manager.server.communication;

import java.util.Base64;

import com.spinalcraft.easycrypt.EasyCrypt;

public class Crypt extends EasyCrypt{
	@Override
	public byte[] decode(String str){
		return Base64.getDecoder().decode(str);
	}
	
	@Override
	public String encode(byte[] bytes){
		return Base64.getEncoder().encodeToString(bytes);
	}
}
