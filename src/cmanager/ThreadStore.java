package cmanager;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;

public class ThreadStore implements UncaughtExceptionHandler
{
    private ArrayList<Thread> threads = new ArrayList<>();
    private Throwable exception = null;

    public void addAndRun(Thread t)
    {
        t.setUncaughtExceptionHandler(this);
        threads.add(t);
        t.start();
    }

    public void joinAndThrow() throws Throwable
    {
        for (Thread t : threads)
            try
            {
                t.join();
            }
            catch (InterruptedException e1)
            {
            }

        if (exception != null)
            throw exception;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e)
    {
        exception = e;
    }

    public int getCores(int maximum)
    {
        int cores = Runtime.getRuntime().availableProcessors();
        if (cores > maximum)
            cores = maximum;
        return cores;
    }
}
