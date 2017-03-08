package util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**@author Brandon Richardson
 *@version 1.4.3
 *@since 06/05/2016
 *<p>
 *The {@code Logger} class manages logging information regarding the state of an application, system,
 *or components, be it simple messages or information regarding exceptions thrown.
 *<p>
 *The main purpose of the {@code Logger} class is to aid in application troubleshooting and
 *record keeping. Log files are stored in a desired directory, in plain text files.
 */

public class Logger
{
	private String logFileDirectory;
	private FileWriter fileOutput;
	private boolean isClosed;
	
	public Logger(String logFileDirectory)
	{
		if(logFileDirectory == null)
			throw new NullPointerException("null file directory");
		
		this.logFileDirectory = logFileDirectory;
		this.isClosed = false;
		
		if(!(new File(logFileDirectory)).isDirectory())
			(new File(logFileDirectory)).mkdir();
		
		//Attempt to Build Log File and File Writer
		File logFile = new File(logFileDirectory + "LOG " + Logger.getSystemTimestamp() + ".txt");
		
		try
		{
			if(logFile.exists())
				throw new RuntimeException(logFile.getName() + " already exists");
			else
				logFile.createNewFile();
			
			this.fileOutput = new FileWriter(logFile);
		}
		catch (IOException e)
		{
			this.isClosed = true;
		}
	}
	
	public synchronized void logException(Exception e)
	{
		if(this.isClosed)
			return;
		
		try
		{
			String stackTrace = "";
			for(StackTraceElement element : e.getStackTrace())
				stackTrace += "\t" + element.toString() + "\n";
			stackTrace += "\n";
			
			this.fileOutput.write(Logger.getSystemTimestamp() + ": EXCEPTION THROWN" + "\n");
			this.fileOutput.write(e.toString() + "\n" + stackTrace);
			this.fileOutput.flush();
		}
		catch(Exception ex)
		{
			this.isClosed = true;
		}
	}
	
	public synchronized void logString(String str)
	{
		if(this.isClosed)
			return;
		
		try
		{
			this.fileOutput.write(Logger.getSystemTimestamp() + ": " + str);
			this.fileOutput.flush();
		}
		catch(Exception e)
		{
			this.isClosed = true;
		}
	}
	
	public void clearLogs()
	{
		File[] files =  (new File(this.logFileDirectory)).listFiles();

		if(files == null)
			return;

		for(File f : files)
		{
			if(f.getName().startsWith("LOG"))
				f.delete();
		}
	}
	
	public void close()
	{
		try
		{
			this.fileOutput.close();
		}
		catch (IOException e){}
		this.isClosed = true;
	}
	
	private static String getSystemTimestamp()
	{
		return new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
	}
}
