package com.spinalcraft.manager.server.communication.messenger;

import java.net.Socket;
import java.sql.SQLException;

import javax.crypto.SecretKey;

import com.spinalcraft.easycrypt.messenger.MessageReceiver;
import com.spinalcraft.manager.server.communication.Actor;
import com.spinalcraft.manager.server.communication.Crypt;

public class Receiver extends MessageReceiver{

	public Receiver(Socket socket, Crypt crypt) {
		super(socket, crypt);
	}

	@Override
	protected long getLastTransmitTimeForIdentifier(String identifier) {
		try {
			Actor actor = Actor.getFromPublicKey(identifier);
			if(actor == null){
				return -1;
			}
			return actor.lastTransmit;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	@Override
	protected SecretKey getSecretKeyForIdentifier(String identifier) {
		try {
			Actor actor = Actor.getFromPublicKey(identifier);
			if(actor == null)
				return null;
			return actor.secretKey;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
