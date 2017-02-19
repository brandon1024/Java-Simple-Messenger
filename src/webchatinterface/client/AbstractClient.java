package webchatinterface.client;

import util.Logger;
import webchatinterface.AbstractIRC;
import webchatinterface.util.ClientUser;

public abstract class AbstractClient
{
	private static Logger logger;
	
	private static ClientUser client;
	
	static
	{
		AbstractClient.logger = new Logger(AbstractIRC.CLIENT_APPLCATION_DIRECTORY);
		AbstractClient.client = new ClientUser();
	}
	
	public synchronized static void logException(Exception e)
	{
		AbstractClient.logger.logException(e);
	}
	
	public static ClientUser getClientUser()
	{
		return AbstractClient.client;
	}
}