package com.okara;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Handle implements Runnable {

	private static final Logger log = Logger.getLogger(Handle.class.getName());

	private Socket sClient;
	private Socket sServer;
	private final String SERVER_URL;
	private final int SERVER_PORT;
	private final int TIME_SLEEP = 5000;

	static {
		if (GlobalConfig.handlerLog != null) {
			log.addHandler(GlobalConfig.handlerLog);
		}
	}

	Handle(Socket sClient, String ServerUrl, int ServerPort) {
		this.SERVER_URL = ServerUrl;
		this.SERVER_PORT = ServerPort;
		this.sClient = sClient;
		Thread t = new Thread(this);
		t.start();
	}

	@Override
	public void run() {
		try {
			sClient.setKeepAlive(true);
			sClient.setSoTimeout(10000);
			BufferedReader brClient = new BufferedReader(new InputStreamReader(sClient.getInputStream()));
			PrintWriter pwClient = new PrintWriter(new OutputStreamWriter(sClient.getOutputStream()), true);
			try {
				System.out.println("=======>" + sClient.getInetAddress().toString() + ":" + sClient.getLocalPort()
						+ " start connect " + SERVER_URL + ":" + SERVER_PORT + "...");
				sServer = new Socket(SERVER_URL, SERVER_PORT);
				System.out.println("=======>" + sClient.getInetAddress().toString() + ":" + sClient.getLocalPort()
						+ " connected to " + SERVER_URL + ":" + SERVER_PORT);
			} catch (IOException e) {
				PrintWriter out = new PrintWriter(new OutputStreamWriter(sClient.getOutputStream()));
				out.write("HTTP/1.1 404 Not Found\r\n");
				out.write("Content-Type: text/plain\r\n\r\n");
				out.write("404");
				out.flush();
				sClient.close();

				System.out.println("Couldn't connect to " + SERVER_URL + ":" + SERVER_PORT);
				System.out.println("Closed connect to client");
				throw new RuntimeException(e);
			}

			BufferedReader brServer = new BufferedReader(new InputStreamReader(sServer.getInputStream()));
			PrintWriter pwServer = new PrintWriter(sServer.getOutputStream(), true);

			// forward request
			StringBuilder req = new StringBuilder();
			while (brClient.ready()) {
				char c = (char) brClient.read();
				req.append(c);
			}
			pwServer.write(req.toString());
			pwServer.flush();

			System.out.println("Request: " + req.toString());

			// waiting response from server
			while(!brServer.ready()) {
				Thread.sleep(100);
			}
//			// get response
			StringBuilder resp = new StringBuilder();
			while (brServer.ready()) {
				char c = (char) brServer.read();
				resp.append(c);
			}

			String result = resp.toString();
			if (result == null || result.trim().equals("")) {
				pwClient.write("HTTP/1.1 200 Error\r\n");
				pwClient.write("Content-Type: application/xml\r\n");
				pwClient.write("Accept: application/soap+xml\r\n\r\n");
				pwClient.write("<?xml version=\"1.0\"?>\r\n"
						+ "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" >\r\n" + "<S:Body>\r\n"
						+ "<subRequestResponse xmlns=\"http://contentws/xsd\">\r\n" + "<return>1</return>\r\n"
						+ "</subRequestResponse>\r\n" + "</S:Body>\r\n" + "</S:Envelope>");
				System.out.println("Couldn't get response from " + SERVER_URL + ":" + SERVER_PORT);
			}
			pwClient.write(result);
			pwClient.flush();
			System.out.println("Response: " + result);

			brServer.close();
			pwServer.close();
			sServer.close();
			System.out.println("Closed connect to server");

			brClient.close();
			pwClient.close();
			sClient.close();
			System.out.println("Closed connect to client");

		} catch (Exception e) {
			System.out.println(e);
		}
	}

}
