package cmanager;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;

public class ThreadStore implements UncaughtExceptionHandler
{
	private ArrayList<Thread> threads = new ArrayList<>();
	private Throwable exception = null;
	
	public void add(Thread t)
	{
		t.setUncaughtExceptionHandler(this);
		threads.add(t);
	}
	
	public void join() throws Throwable
	{
		for(Thread t : threads)
			try {
				t.join();
			} catch (InterruptedException e1) {	}
		
		if( exception != null )
			throw exception;
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) 
	{
		exception = e;
	}

}
