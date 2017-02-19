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
	/**Static variable used to describe the user as available*/
	public static final int AVAILABLE = 0;
	
	/**Static variable used to describe the user as busy*/
	public static final int BUSY = 1;
	
	/**Static variable used to describe the user as away*/
	public static final int AWAY = 2;
	
	/**Static variable used to describe the user as appearing offline*/
	public static final int APPEAR_OFFLINE = 3;
	
	/**Static variable used to describe the user as offline*/
	public static final int OFFLINE = 4;
	
	/**The username defined by the user*/
	private String username;
	
	/**The unique user identification key associated with this instance of ClientUser*/
	private String userID;
	
	/**The availability of the client user, as described by the static class constants */
	private int availability;
	
	/**Default ClientUser constructor. Constructs ClientUser object with null fields.
	  *To initialize object, invoke {@code signIn()} method.*/
	public ClientUser(){}
	
	/**ClientUser Constructor. Constructs and initializes ClientUser object with a given
	  *username and unique UserID. Sets the availability to AVAILABLE.
	  *@param username The username representing the client user
	  *@param userID the unique user identification key associated with this client*/
	public ClientUser(String username, String userID)
	{
		this.username = username;
		this.userID = userID;
		this.availability = ClientUser.AVAILABLE;
	}
	
	/**Initializes the ClientUser object with a given username and unique UserID. Sets 
	  *the availability to AVAILABLE.
	  *@param username The username representing the client user
	  *@param userID the unique user identification key associated with this client*/
	public void signIn(String username, String userID)
	{
		this.username = username;
		this.userID = userID;
		this.availability = ClientUser.AVAILABLE;
	}
	
	/**Get the state of the client user. If the client user is signed in, {@code getUsername()} and
	  *{@code getUserID()} return object fields. If the client user is signed out, these methods
	  *return null.
	  *@return true if the ClientUser objects fields exist (client is signed in), false otherwise.*/
	public boolean isSignedIn()
	{
		if(this.availability == ClientUser.OFFLINE || this.userID == null || this.username == null)
		{
			return false;
		}
		
		return true;
	}
	
	/**Removes references to username and userID, and sets the availability to APPEAR_OFFLINE.*/
	public void signOut()
	{
		this.username = null;
		this.userID = null;
		this.availability = ClientUser.OFFLINE;
	}
	
	/**Accessor method for the username represented by this ClientUser object. 
	  *@return the client username if signed in, or null if the user is signed out*/
	public String getUsername()
	{
		return this.username;
	}

	/**Mutator method for the username of this ClientUser object. 
	  *@param username the new client username*/
	public void setUsername(String username)
	{
		this.username = username;
	}

	/**Accessor method for the userID represented by this ClientUser object. 
	  *@return the client userID if signed in, or null if the user is signed out*/
	public String getUserID()
	{
		return userID;
	}
	
	/**Set the unique user identification key associated with this instance of the client.
	  *Note: Changing the unique ID will result in a verification error by the server, and
	  *the client will be disconnected.
	  *@param userID the new unique user identification key*/
	public void setUserID(String userID)
	{
		this.userID = userID;
	}
	
	/**Accessor method for the availability of the client user.
	  *@return the availability of the client user*/
	public int getAvailability()
	{
		return this.availability;
	}
	
	/**Mutator method for the availability of the client user. 
	  *@param availability the new client availability*/
	public void setAvailability(int availability)
	{
		this.availability = availability;
	}
}
