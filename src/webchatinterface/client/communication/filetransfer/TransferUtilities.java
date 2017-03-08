package webchatinterface.client.communication.filetransfer;

import webchatinterface.helpers.DataHelper;

public class TransferUtilities
{
	public static final int MODE_SEND = 0;
	public static final int MODE_RECEIVE = 1;

	public static int progressPercentageInt(long currentPacket, long totalPackets)
	{
		return (int)((100) * currentPacket / totalPackets);
	}
	
	public static String computeTransferSpeedText(long bufferSize, long timeElapsedMillis)
	{
		long speed = Double.doubleToLongBits(bufferSize / (double)timeElapsedMillis);
		return "Speed: " + DataHelper.formatBytes(speed, 2) + "/s";
	}
	
	public static String computePercentCompletionText(long bytesRead, long bytesTotal)
	{
		int percentage = TransferUtilities.progressPercentageInt(bytesRead, bytesTotal);
		return percentage + "% (" + DataHelper.formatBytes(bytesRead, 2) + ")";
	}
	
	public static String computeProgressText(long bytesRead, long bytesTotal)
	{
		String dataRead = DataHelper.formatBytes(bytesRead, 2);
		String dataTotal = DataHelper.formatBytes(bytesTotal,2);
		return "Progress: " + dataRead + "/" + dataTotal;
	}
}
