package webchatinterface.client.util;

import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import webchatinterface.client.AbstractClient;

public class FrameUtilities
{
	public static boolean setFrameIcon(JFrame frame, BufferedImage icon)
	{
		try
		{
			frame.setIconImage(icon);
		}
		catch(IllegalArgumentException e)
		{
			AbstractClient.logException(e);
			return false;
		}
		
		return true;
	}
}