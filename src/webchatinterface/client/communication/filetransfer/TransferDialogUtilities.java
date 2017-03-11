package webchatinterface.client.communication.filetransfer;

import webchatinterface.helpers.DataHelper;

public class TransferDialogUtilities
{
	public static String computeTransferSpeedText(long bufferSize, long timeElapsedMillis, long max)
	{
		long speed = Double.doubleToLongBits(bufferSize / (double)timeElapsedMillis);
		speed = speed > max ? max : speed;
		return "Speed: " + DataHelper.formatBytes(speed, 2) + "/s" + (speed == max ? "+" : "");
	}
	
	public static String computePercentCompletionText(long bytesRead, long bytesTotal)
	{
		int percentage = TransferDialogUtilities.progressPercentage(bytesRead, bytesTotal);
		return percentage + "% (" + DataHelper.formatBytes(bytesRead, 2) + ")";
	}
	
	public static String computeProgressText(long bytesRead, long bytesTotal)
	{
		String dataRead = DataHelper.formatBytes(bytesRead, 2);
		String dataTotal = DataHelper.formatBytes(bytesTotal,2);
		return "Progress: " + dataRead + "/" + dataTotal;
	}

	public static int progressPercentage(long numerator, long denominator)
	{
		return (int)((100) * numerator / denominator);
	}
}
