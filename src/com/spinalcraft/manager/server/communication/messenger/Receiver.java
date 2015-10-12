package com.spinalcraft.manager.server.communication.messenger;

import java.net.Socket;

import com.spinalcraft.easycrypt.EasyCrypt;
import com.spinalcraft.easycrypt.messenger.MessageReceiver;

public class Receiver extends MessageReceiver{

	public Receiver(Socket socket, EasyCrypt crypt) {
		super(socket, crypt);
	}
}
