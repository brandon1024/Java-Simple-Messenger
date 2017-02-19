package webchatinterface.client.util.filetransfer;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import webchatinterface.client.AbstractClient;

public class TransferUtilities
{
	public static int getSystemTimestamp()
	{
		String time = new SimpleDateFormat("HHmmssSSS").format(Calendar.getInstance().getTime());
		int milliseconds = Integer.parseInt(time.substring(0,2)) * 3600000;
		milliseconds += Integer.parseInt(time.substring(2,4)) * 60000;
		milliseconds += Integer.parseInt(time.substring(4,6)) * 1000;
		milliseconds += Integer.parseInt(time.substring(6));
		
		return milliseconds;
	}
	
	public static int progressPercentageInt(long currentPacket, long totalPackets, int offset) throws IllegalArgumentException
	{
		if(offset < 0 || offset > 100)
		{
			IllegalArgumentException e = new IllegalArgumentException("Invalid offset: " + offset);
			AbstractClient.logException(e);
			throw e;
		}
		
		if(totalPackets < 0)
			return 0;
		
		return (int)((100 - offset) * currentPacket / totalPackets);
	}
	
	public static String computeTransferSpeedText(long bufferSize, long timeElapsedMillis)
	{
		String text;
		double bytesTransfered = bufferSize;
		double speed = bytesTransfered / (double)timeElapsedMillis;
		
		if(speed > 1073741824.0)
		{
			bytesTransfered = bytesTransfered / 1073741824.0;
			speed = bytesTransfered / timeElapsedMillis;
			speed = Math.round(speed*100.0)/100.0;
			
			if(speed < 500)
				text = "Speed: " + speed + "GB/s";
			else
				text = "Speed: 500+GB/s";
		}
		else if(speed > 1048576.0)
		{
			bytesTransfered = bytesTransfered / 1048576.0;
			speed = bytesTransfered / timeElapsedMillis;
			speed = Math.round(speed*100.0)/100.0;
			text = "Speed: " + speed + "MB/s";
		}
		else if(speed > 1024.0)
		{
			bytesTransfered = bytesTransfered / 1024.0;
			speed = bytesTransfered / timeElapsedMillis;
			speed = Math.round(speed*100.0)/100.0;
			text = "Speed: " + speed + "kB/s";
		}
		else
		{
			text = "Speed: " + speed + "B/s";
		}
		
		return text;
	}
	
	public static String computePercentCompletionText(long bytesRead, long bytesTotal)
	{
		String text;
		
		if(bytesRead > 1073741824)
		{
			double dataRead = Math.round(((double)bytesRead / 107374182.0)*100.0)/100.0;
			int percentage = TransferUtilities.progressPercentageInt(bytesRead, bytesTotal, 0);
			text = percentage + "% (" + dataRead + "GB)";
		}
		else if(bytesRead > 1048576)
		{
			double dataRead = Math.round(((double)bytesRead / 1048576.0)*100.0)/100.0;
			int percentage = TransferUtilities.progressPercentageInt(bytesRead, bytesTotal, 0);
			text = percentage + "% (" + dataRead + "MB)";
		}
		else if(bytesRead > 1024)
		{
			double dataRead = Math.round(((double)bytesRead / 1024.0)*100.0)/100.0;
			int percentage = TransferUtilities.progressPercentageInt(bytesRead, bytesTotal, 0);
			text = percentage + "% (" + dataRead + "kB)";
		}
		else
		{
			int percentage = TransferUtilities.progressPercentageInt(bytesRead, bytesTotal, 0);
			text = percentage + "% (" + bytesRead + "B)";
		}
		
		return text ;
	}
	
	public static String computeProgressText(long bytesRead, long bytesTotal)
	{
		String text;
		
		if(bytesRead > 1073741824)
		{
			double dataRead = Math.round(((double)bytesRead / 107374182.0)*100.0)/100.0;
			double dataTotal = Math.round(((double)bytesTotal / 107374182.0)*100.0)/100.0;
			text = "Progress: " + dataRead + "GB/" + dataTotal + "GB";
		}
		else if(bytesRead > 1048576)
		{
			double dataRead = Math.round(((double)bytesRead / 1048576.0)*100.0)/100.0;
			double dataTotal = Math.round(((double)bytesTotal / 1048576.0)*100.0)/100.0;
			text = "Progress: " + dataRead + "MB/" + dataTotal + "MB";
		}
		else if(bytesRead > 1024)
		{
			double dataRead = Math.round(((double)bytesRead / 1024.0)*100.0)/100.0;
			double dataTotal = Math.round(((double)bytesTotal / 1024.0)*100.0)/100.0;
			text = "Progress: " + dataRead + "kB/" + dataTotal + "kB";
		}
		else
		{
			text = "Progress: " + bytesRead + "B/" + bytesTotal + "B";
		}
		
		return text;
	}
}
