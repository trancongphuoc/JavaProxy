package com.okara.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetWork {
	
	public static void addConsoleLogToFirebase(String text) {
		new Thread() {
			public void run() {
				try {
					URL u = new URL("http://203.162.76.17:9201/writelog");
					HttpURLConnection conn = (HttpURLConnection) u.openConnection();
					conn.setDoOutput(true);
					conn.setDoInput(true);
					conn.setConnectTimeout(10000);
					conn.setReadTimeout(10000);
					conn.setRequestMethod("POST");
					conn.setRequestProperty("Content-Type", "application/json");

					OutputStream os = conn.getOutputStream();
					BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
					writer.write(text);
					writer.flush();
					writer.close();
					int response = conn.getResponseCode();
					BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
					String line;
					while ((line = reader.readLine()) != null) {
						// System.out.println(line);
					}
					reader.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
	}
	
}
