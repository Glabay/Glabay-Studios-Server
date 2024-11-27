package io.xeros;

import io.xeros.content.wogw.Wogw;

/**
 * A thread which will be started when the server is being shut down. Although in most cases the Thread will be started, it cannot be guaranteed.
 * 
 * @author Emiel
 *
 */
public class ShutdownHook extends Thread {

	public ShutdownHook() {
		setName("shutdown-hook");
	}

	public void run() {
		System.out.println("Successfully executed ShutdownHook");
		Wogw.save();
	}
}
