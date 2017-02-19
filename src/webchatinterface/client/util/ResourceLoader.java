package webchatinterface.client.util;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import webchatinterface.client.AbstractClient;

public class ResourceLoader
{
	private BufferedImage FRAME_ICON;
	private BufferedImage STATUS_AVAILABLE_ICON;
	private BufferedImage STATUS_BUSY_ICON;
	private BufferedImage STATUS_AWAY_ICON;
	private BufferedImage STATUS_APPEAR_OFFLINE_ICON;
	private BufferedImage STATUS_OFFLINE_ICON;
	
	private String FRAME_ICON_PATH;
	private String STATUS_AVAILABLE_ICON_PATH;
	private String STATUS_BUSY_ICON_PATH;
	private String STATUS_AWAY_ICON_PATH;
	private String STATUS_APPEAR_OFFLINE_ICON_PATH;
	private String STATUS_OFFLINE_ICON_PATH;
	
	private ResourceLoader()
	{
		String ps = "/";
		String commonPath = ps + "webchatinterface" + ps + "client" + ps + "resources" + ps;
		
		this.FRAME_ICON_PATH = commonPath + "CLIENTICON.png";
		this.STATUS_AVAILABLE_ICON_PATH = commonPath + "AVAILABLE.png";
		this.STATUS_BUSY_ICON_PATH = commonPath + "BUSY.png";
		this.STATUS_AWAY_ICON_PATH = commonPath + "AWAY.png";
		this.STATUS_APPEAR_OFFLINE_ICON_PATH = commonPath + "APPEAROFFLINE.png";
		this.STATUS_OFFLINE_ICON_PATH = commonPath + "OFFLINE.png";
		
		this.FRAME_ICON = null;
		this.STATUS_AVAILABLE_ICON = null;
		this.STATUS_BUSY_ICON = null;
		this.STATUS_AWAY_ICON = null;
		this.STATUS_APPEAR_OFFLINE_ICON = null;
		this.STATUS_OFFLINE_ICON = null;
	}
	
	public boolean loadResources()
	{
		boolean frameIconLoadedSuccess = this.loadFrameIcon();
		boolean statusIconsLoadedSuccess = this.loadStatusIcons();
		
		return frameIconLoadedSuccess && statusIconsLoadedSuccess;
	}
	
	private boolean loadFrameIcon()
	{
		try
		{
			this.FRAME_ICON = ImageIO.read(this.getClass().getResource(this.FRAME_ICON_PATH));
			
			if(this.FRAME_ICON == null)
				throw new IOException("Resource was unable to load; FRAME_ICON");
		}
		catch(IOException | IllegalArgumentException e)
		{
			AbstractClient.logException(e);
			this.FRAME_ICON = null;
		}
		
		if(this.FRAME_ICON == null)
			return false;
		else
			return true;
	}
	
	private boolean loadStatusIcons()
	{
		try
		{
			this.STATUS_AVAILABLE_ICON = ImageIO.read(this.getClass().getResource(this.STATUS_AVAILABLE_ICON_PATH));
			this.STATUS_BUSY_ICON = ImageIO.read(this.getClass().getResource(this.STATUS_BUSY_ICON_PATH));
			this.STATUS_AWAY_ICON = ImageIO.read(this.getClass().getResource(this.STATUS_AWAY_ICON_PATH));
			this.STATUS_APPEAR_OFFLINE_ICON = ImageIO.read(this.getClass().getResource(this.STATUS_APPEAR_OFFLINE_ICON_PATH));
			this.STATUS_OFFLINE_ICON = ImageIO.read(this.getClass().getResource(this.STATUS_OFFLINE_ICON_PATH));
			
			if(this.STATUS_AVAILABLE_ICON == null)
				throw new IOException("Resource was unable to load; STATUS_AVAILABLE_ICON");
			
			if(this.STATUS_BUSY_ICON == null)
				throw new IOException("Resource was unable to load; STATUS_BUSY_ICON");
			
			if(this.STATUS_AWAY_ICON == null)
				throw new IOException("Resource was unable to load; STATUS_AWAY_ICON");
			
			if(this.STATUS_APPEAR_OFFLINE_ICON == null)
				throw new IOException("Resource was unable to load; STATUS_APPEAR_OFFLINE_ICON");
			
			if(this.STATUS_OFFLINE_ICON == null)
				throw new IOException("Resource was unable to load; STATUS_OFFLINE_ICON");
		}
		catch(IOException | IllegalArgumentException e)
		{
			AbstractClient.logException(e);
			this.STATUS_AVAILABLE_ICON = null;
			this.STATUS_BUSY_ICON = null;
			this.STATUS_AWAY_ICON = null;
			this.STATUS_APPEAR_OFFLINE_ICON = null;
			this.STATUS_OFFLINE_ICON = null;
		}
		
		if(this.STATUS_AVAILABLE_ICON == null)
			return false;
		else
			return true;
	}
	
	public BufferedImage getFrameIcon()
	{
		return FRAME_ICON;
	}

	public BufferedImage getStatusAvailableIcon()
	{
		return STATUS_AVAILABLE_ICON;
	}

	public BufferedImage getStatusBusyIcon()
	{
		return STATUS_BUSY_ICON;
	}

	public BufferedImage getStatusAwayIcon()
	{
		return STATUS_AWAY_ICON;
	}

	public BufferedImage getStatusAppearOfflineIcon()
	{
		return STATUS_APPEAR_OFFLINE_ICON;
	}

	public BufferedImage getStatusOfflineIcon()
	{
		return STATUS_OFFLINE_ICON;
	}
	
	public static ImageIcon bufferedImageToImageIcon(BufferedImage image)
	{
		ImageIcon img = null;
		try
		{
			img = new ImageIcon(image);
		}
		catch(NullPointerException e)
		{
			AbstractClient.logException(e);
		}
		
		return img;
	}
	
	public static ResourceLoader getInstance()
	{
		return InstanceHolder.INSTANCE;
	}
	
	private static class InstanceHolder
	{
		/**The single instance of ConsoleManager.*/
		private static final ResourceLoader INSTANCE = new ResourceLoader();
	}
}