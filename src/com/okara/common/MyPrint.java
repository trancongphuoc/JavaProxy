package com.okara.common;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

public class MyPrint extends PrintStream {

	public MyPrint(OutputStream out) {
		super(out);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void println(Object x) {
		super.println(x);
		if(x instanceof Exception) {
			StringWriter errors = new StringWriter();
			((Throwable) x).printStackTrace(new PrintWriter(errors));
			NetWork.addConsoleLogToFirebase(errors.toString());
		}
		
	}
	
	

	@Override
	public void println(String x) {
		super.println(x);
		NetWork.addConsoleLogToFirebase(x);
	}

}
