package server;

import java.io.*;

/**
 * OutputDuplicator highjacks the standard output stream, and prints a copy in an AdminConsole. 
 * 
 * @author Sebastian Hoggard, Rasmus Svedin
 *
 */
public class OutputDuplicator extends PrintStream {
	private AdminConsole console;
	
	public OutputDuplicator(AdminConsole console) {
		super((OutputStream)System.out);
		System.setOut(this);
		this.console = console;
	}
	
	@Override
	public void print(String x) {
		super.print(x);
		console.setText(x);
	}
	
	@Override
	public void println(String x) {
		super.println(x);
		console.setText("\n");
	}
	
	@Override
	public void print(int x) {
		super.print(x);
		console.setText(String.valueOf(x));
	}
	
	@Override
	public void println(int x) {
		super.println(x);
		console.setText("\n");
	}
	
	@Override
	public void print(char x) {
		super.print(x);
		console.setText(String.valueOf(x));
	}
	
	@Override
	public void println(char x) {
		super.println(x);
		console.setText("\n");
	}
	@Override
	public void print(double x) {
		super.print(x);
		console.setText(String.valueOf(x));
	}
	
	@Override
	public void println(double x) {
		super.println(x);
		console.setText("\n");
	}
	
	@Override
	public void print(float x) {
		super.print(x);
		console.setText(String.valueOf(x));
	}
	
	@Override
	public void println(float x) {
		super.println(x);
		console.setText("\n");
	}
	
	@Override
	public void print(long x) {
		super.print(x);
		console.setText(String.valueOf(x));
	}
	
	@Override
	public void println(long x) {
		super.println(x);
		console.setText("\n");
	}
	
	@Override
	public void print(boolean x) {
		super.print(x);
		console.setText(String.valueOf(x));
	}
	
	@Override
	public void println(boolean x) {
		super.println(x);
		console.setText("\n");
	}
	
	@Override
	public void print(char[] x) {
		super.print(x);
		console.setText(String.valueOf(x));
	}
	
	@Override
	public void println(char[] x) {
		super.println(x);
		console.setText("\n");
	}
	
	@Override
	public void print(Object x) {
		super.print(x);
		console.setText(String.valueOf(x));
	}
	
	@Override
	public void println(Object x) {
		super.println(x);
		console.setText("\n");
	}
}
