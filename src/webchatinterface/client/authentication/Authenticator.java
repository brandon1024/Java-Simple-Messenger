package webchatinterface.client.authentication;

import webchatinterface.AbstractIRC;
import webchatinterface.client.AbstractClient;
import webchatinterface.client.ui.WebChatClientGUI;
import webchatinterface.client.ui.dialog.AuthenticationDialog;
import webchatinterface.client.ui.dialog.NewAccountDialog;
import webchatinterface.client.util.Preset;
import webchatinterface.helpers.EmailHelper;
import webchatinterface.helpers.UsernameHelper;

import java.io.*;
import java.util.Arrays;

/**@author Brandon Richardson
 *@version 1.4.3
 *@since 06/05/2016
  *<p>
  *The {@code Authenticator} class represents an object that is able to gather information from the
  *user to authenticate for a client-server socket connection. The class is designed to do so by
  *displaying a dialog with fields for gathering from the user the desired username, server host 
  *address and port number. The authenticator also assigns a generated 256-bit user ID key.
  *<p>
  *The {@code Authenticator} also handles loading and saving previously saved authentication presets.
  */

public class Authenticator
{
	private WebChatClientGUI parent;

	private boolean guest;
	private boolean newAccount;
	private String emailAddress;
	private String username;
	private byte[] password;
	private String hostAddress;
	private Integer portNumber;

	/**Constructor for the {@code Authenticator} object.*/
	public Authenticator(WebChatClientGUI parent)
	{
		this.parent = parent;
	}
	
	/**Attempt accelerated authentication by gathering username, host address and port number from
	  *preset file. If preset file not found, normal authentication is executed; i.e. 
	  *{@code showDialog()} is invoked.
	  *@throws AuthenticationException if preset file was not found and showDialog() method
	  *threw an exception due to invalid entered information*/
	public void quickAuthenticate() throws AuthenticationException
	{
		//Attempt to Load Previous Settings
		try
		{
			this.loadPreset();
		}
		catch(FileNotFoundException e)
		{
			AbstractClient.logException(e);
			this.showAuthenticationDialog();
		}
	}
	
	/**Display the authentication dialog to gather the user specified username, password, host address and port
	  *number. Once the appropriate information is entered, the {@code Authenticator} object fields
	  *are updated, and may be accessed using accessor methods.
	  *@throws AuthenticationException if information entered by the user is invalid
	  *@throws AuthenticationAbortedException if the authentication was aborted by the user*/
	public void showNewAccountDialog() throws AuthenticationException
	{
		this.guest = false;
		this.newAccount = true;

		//---REQUEST SETTINGS FROM USER---//
		NewAccountDialog nad = new NewAccountDialog(parent);
		if(nad.showDialog() == 1)
		{
			try
			{
				this.hostAddress = nad.getHostAddress();
				this.portNumber = Integer.valueOf(nad.getHostPort());
			}
			catch (NumberFormatException e)
			{
				AbstractClient.logException(e);
				throw new AuthenticationException("Invalid Input");
			}

			this.emailAddress = nad.getEmailAddress();
			if(!EmailHelper.isValidEmailAddress(this.emailAddress))
				throw new AuthenticationException("Invalid Email Address");

			this.username = nad.getUsername();
			if(!UsernameHelper.isValidUsername(this.username))
				throw new AuthenticationException("Invalid Username");

			char[] pass1 = nad.getPassword();
			char[] pass2 = nad.getConfirmPassword();

			if(!Arrays.equals(pass1, pass2))
				throw new AuthenticationException("Passwords Don't Match");

			Arrays.fill(pass2, Character.MIN_VALUE);

			this.password = new byte[pass1.length];

			if(pass1.length < 6)
				throw new AuthenticationException("Your password is too short. Password must be a minimum of 6 characters.");

			for(char character : pass1)
			{
				if(character > Byte.MAX_VALUE)
					throw new AuthenticationException("Invalid Password; ASCII characters only: a-z, A-Z, 0-9, or any !\"#$%&\'()*+,-./:;<=>?@[\\]^_`{|}~");
			}

			for(int i = 0; i < this.password.length; i++)
				this.password[i] = (byte)pass1[i];

			Arrays.fill(pass1, Character.MIN_VALUE);
		}
		else
			throw new AuthenticationAbortedException("Authentication Aborted by User");

		this.savePreset();
	}

	public void showAuthenticationDialog() throws AuthenticationException
	{
		//Attempt to Load Previous Settings
		try
		{
			this.loadPreset();
		}
		catch(FileNotFoundException e)
		{
			AbstractClient.logException(e);
		}

		//---REQUEST SETTINGS FROM USER---//
		AuthenticationDialog auth = new AuthenticationDialog(this.parent, this.username, this.password, this.hostAddress, this.portNumber);

		if(auth.showDialog() == 1)
		{
			try
			{
				this.hostAddress = auth.getHostAddress();
				this.portNumber = Integer.valueOf(auth.getPortNumber());
			}
			catch (NumberFormatException e)
			{
				AbstractClient.logException(e);
				throw new AuthenticationException("Invalid Input");
			}

			if (auth.getIsGuest())
			{
				this.guest = true;
				this.newAccount = false;
				this.username = null;
				this.password = null;
			}
			else
			{
				this.guest = false;
				this.newAccount = false;
				this.username = auth.getUsername();

				char[] pass = auth.getPassword();
				this.password = new byte[pass.length];

				if (pass.length < 6)
					throw new AuthenticationException("Your password is too short. Password must be a minimum of 6 characters.");

				for (char character : pass)
				{
					if (character > Byte.MAX_VALUE)
						throw new AuthenticationException("Invalid Password; ASCII characters only: a-z, A-Z, 0-9, or any !\"#$%&\'()*+,-./:;<=>?@[\\]^_`{|}~");
				}

				for (int i = 0; i < this.password.length; i++)
					this.password[i] = (byte) pass[i];

				Arrays.fill(pass, Character.MIN_VALUE);
			}
		}
		else
			throw new AuthenticationAbortedException("Authentication Aborted by User");

		if(auth.getSavePreset())
			this.savePreset();
	}
	
	/**Attempt to load saved preset from application temporary directory. Assigns preset settings to
	  *{@code Authenticator} object fields.
	  *@throws FileNotFoundException if the preset file could not be found*/
	private void loadPreset() throws FileNotFoundException
	{
		File presetFile = new File(AbstractIRC.CLIENT_APPLCATION_DIRECTORY + "PRESET.dat");
		
		//---ATTEMPT TO READ PRESET FROM FILE---//
		try(ObjectInputStream presetIn = new ObjectInputStream(new FileInputStream(presetFile)))
		{
			Preset savedPreset = (Preset)presetIn.readObject();
			
			if(savedPreset != null)
			{
				this.username = savedPreset.getUsername();
				this.password = savedPreset.getPassword();
				this.hostAddress = savedPreset.getHostAddress();
				this.portNumber = savedPreset.getPort();
			}
		}
		catch(IOException | ClassNotFoundException e)
		{
			AbstractClient.logException(e);
		}
	}
	
	/**Attempt to save current settings to preset file in application temporary directory.*/
	private void savePreset()
	{
		File presetFile = new File(AbstractIRC.CLIENT_APPLCATION_DIRECTORY + "PRESET.dat");
		if(!presetFile.exists())
		{
			try
			{
				presetFile.createNewFile();
			}
			catch(IOException e)
			{
				AbstractClient.logException(e);
			}	
		}
		
		//---ATTEMPT TO SAVE PRESET TO FILE---//
		try(ObjectOutputStream presetOut = new ObjectOutputStream(new FileOutputStream(presetFile)))
		{
			Preset savedPreset = new Preset(this.username, this.password, this.hostAddress, this.portNumber);
			presetOut.writeObject(savedPreset);
		}
		catch(IOException e)
		{
			AbstractClient.logException(e);
		}
	}
	
	public boolean isGuest()
	{
		return this.guest;
	}
	
	public boolean isNewMember()
	{
		return this.newAccount;
	}
	
	public String getEmailAddress()
	{
		return this.emailAddress;
	}
	
	/**Accessor method for the username field of the {@code Authenticator} object.
	  *@return the username specified by the user*/
	public String getUsername()
	{
		return this.username;
	}
	
	/**Accessor method for the user password field of the {@code Authenticator} object.
	  *@return the account password*/
	public byte[] getPassword()
	{
		return this.password;
	}
	
	/**Accessor method for the host address field of the {@code Authenticator} object.
	  *@return the server host address specified by the user*/
	public String getHostAddress()
	{
		return this.hostAddress;
	}
	
	/**Accessor method for the port number field of the {@code Authenticator} object.
	  *@return the server port number specified by the user*/
	public int getPortNumber()
	{
		return this.portNumber;
	}
	
	/***/
	public void removeSensitiveInformation()
	{
		Arrays.fill(this.password, Byte.MIN_VALUE);
	}
}