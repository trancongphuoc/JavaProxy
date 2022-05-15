package com.okara;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpListen implements Runnable{
	
	private static final Logger log = Logger.getLogger(HttpListen.class.getName());
	
	private final String remoteHost;
	private final int remotePort;
	private final int port;
	
	static {
		if(GlobalConfig.handlerLog != null) {
			log.addHandler(GlobalConfig.handlerLog);
		}
	}
	

	HttpListen(int port,String ServerUrl, int ServerPort) {
		this.remoteHost = ServerUrl;
		this.remotePort = ServerPort;
		this.port = port;
		
		Thread t = new Thread(this);
		t.start();
	}


	@Override
	public void run() {
		try {
			ServerSocket server = new ServerSocket(port);
			System.out.println("Accepting connections on port: " + port + "...");
			int count = 1;
			while(true) {
				System.out.println("=======> " + count++ + ": Listening at port " + port);
				server.setSoTimeout(0);
				new Handle(server.accept(), remoteHost, remotePort);
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

}
