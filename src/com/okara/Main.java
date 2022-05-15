package com.okara;

import com.okara.common.MyPrint;

public class Main {
		
	public static void main(String[] args) {
		System.setOut(new MyPrint(System.out));
		
		String configPath = "../conf/config.properties";
		String configLogPath = "../logs/forward.log";
		GlobalConfig.config(configPath);
		GlobalConfig.configLog(configLogPath);
		
		String ports = GlobalConfig.get("ports");
		String[] portArr = ports.split(",");
		
		for(String port: portArr) {
			listen(port);
		}
	}
	
	private static void listen(String port) {
		String remotePortKey = port + "_port";
		String remoteIpKey = port + "_ip";
		
		int remotePort = GlobalConfig.getInt(remotePortKey, 0);
		String remoteIp = GlobalConfig.get(remoteIpKey);
		
		System.out.println("Listen at port: " + port);
		System.out.println("Forward to address: " + remoteIp + ":" + remotePort);
		new HttpListen(Integer.parseInt(port), remoteIp, remotePort);
	}
	
    static {
    	System.out.println("======>Start");
    }
}
