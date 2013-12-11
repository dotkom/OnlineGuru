package no.ntnu.online.onlineguru.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Timer extends Thread {
	
	private Object caller;
	private String methodName;
	private long waitTime;
	private boolean runForever = false;
	private boolean running = false;
	private Object[] arguments;
	
	public Timer(Object caller, String methodName, long waitTime, boolean runForever) {
		this.caller = caller;
		this.methodName = methodName;
		this.waitTime = waitTime;
		this.runForever = runForever;
	}
	
	public Timer(Object caller, String methodName, long waitTime, boolean runForever, Object[] arguments) {
		this.caller = caller;
		this.methodName = methodName;
		this.waitTime = waitTime;
		this.runForever = runForever;
		this.arguments = arguments;
	}
	
	public void run() {
		running = true;
		while(running) {
			try {
				sleep(waitTime);
				if(!running) break;
				
				if(arguments != null) {
                    Method method = caller.getClass().getMethod(methodName, Object[].class);
					method.invoke(caller, (Object)arguments);
				} else {
				    Method method = caller.getClass().getMethod(methodName);
					method.invoke(caller);
				}
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(!runForever) {
				running = false;
			}
		}
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public void stopTimer() {
		running = false;
	}
	
	public void startTimer() {
		if(running != true) {
			running = true;
			new Thread(this).start();
		}
	}
}
