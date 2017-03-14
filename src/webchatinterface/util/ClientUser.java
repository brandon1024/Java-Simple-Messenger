package webchatinterface.util;

/**@author Brandon Richardson
 *@version 1.4.3
 *@since 06/05/2016
 *<p>
 *The {@code ClientUser} class represents the end user of the client application.
 *The purpose of the ClientUser class is to manage the state and fields associated
 *with the user.
 */

public class ClientUser
{
	public static final int AVAILABLE = 0;
	public static final int BUSY = 1;
	public static final int AWAY = 2;
	public static final int APPEAR_OFFLINE = 3;
	public static final int OFFLINE = 4;

	private String username;
	private String userID;
	private int availability;
	
	public ClientUser(){}
	
	public ClientUser(String username, String userID)
	{
		this.username = username;
		this.userID = userID;
		this.availability = ClientUser.AVAILABLE;
	}
	
	public void signIn(String username, String userID)
	{
		this.username = username;
		this.userID = userID;
		this.availability = ClientUser.AVAILABLE;
	}
	
	public boolean isSignedIn()
	{
		return this.availability != ClientUser.OFFLINE && this.userID != null && this.username != null;
	}
	
	public void signOut()
	{
		this.username = null;
		this.userID = null;
		this.availability = ClientUser.OFFLINE;
	}
	
	public String getUsername()
	{
		return this.username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getUserID()
	{
		return userID;
	}
	
	public void setUserID(String userID)
	{
		this.userID = userID;
	}
	
	public int getAvailability()
	{
		return this.availability;
	}
	
	public void setAvailability(int availability)
	{
		this.availability = availability;
	}
}
