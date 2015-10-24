package com.spinalcraft.manager.server;

import java.io.IOException;

public class Notifier implements Runnable{

	@Override
	public void run() {
		while(true){			
			try {
				Thread.sleep(15000);
				PushNotification notification = new PushNotification();
				notification.message = "TEST";
				notification.send();
				
			} catch (InterruptedException | IOException e) {
				e.printStackTrace();
			}
		}
	}
}
