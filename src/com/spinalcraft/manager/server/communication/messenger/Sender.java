package com.spinalcraft.manager.server.communication.messenger;

import java.net.Socket;

import com.spinalcraft.easycrypt.EasyCrypt;
import com.spinalcraft.easycrypt.messenger.MessageSender;

public class Sender extends MessageSender{

	public Sender(Socket socket, EasyCrypt crypt) {
		super(socket, crypt);
	}
	
	@Override
	public void setIdentifier(String id){
		super.setIdentifier(id);
	}
}
