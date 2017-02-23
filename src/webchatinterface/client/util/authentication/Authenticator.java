package webchatinterface.client.util.authentication;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import webchatinterface.AbstractIRC;
import webchatinterface.client.AbstractClient;
import webchatinterface.client.util.Preset;

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
	/***/
	private boolean guest;
	
	/***/
	private boolean newAccount;
	
	/***/
	private String emailAddress;
	
	/**The username specified by the user*/
	private String username;
	
	/***/
	private byte[] password;
	
	/**The server host address specified by the user*/
	private String hostAddress;
	
	/**The server port number specified by the user*/
	private Integer portNumber;
	
	/**The directory for the saved preset file in the application temporary directory*/
	private String presetFileLocation;
	
	/**Constructor for the {@code Authenticator} object.*/
	public Authenticator()
	{
		this.guest = false;
		this.newAccount = false;
		this.emailAddress = null;
		this.username = null;
		this.password = null;
		this.hostAddress = null;
		this.portNumber = null;
		this.presetFileLocation = AbstractIRC.CLIENT_APPLCATION_DIRECTORY;
	}
	
	/**Attempt accelerated authentication by gathering username, host address and port number from
	  *preset file. If preset file not found, normal authentication is executed; i.e. 
	  *{@code showDialog()} is invoked.
	  *@see webchatinterface.client.util.authentication.Authenticator#loadPreset()
	  *@see webchatinterface.client.util.authentication.Authenticator#showDialog()
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
			this.showDialog();
		}
	}
	
	/**Display the authentication dialog to gather the user specified username, password, host address and port
	  *number. Once the appropriate information is entered, the {@code Authenticator} object fields
	  *are updated, and may be accessed using accessor methods.
	  *@throws AuthenticationException if information entered by the user is invalid
	  *@throws AuthenticationAbortedException if the authentication was aborted by the user*/
	public void showDialog() throws AuthenticationException
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
		boolean saveSettings;

		JPanel dialogPanel = new JPanel();
		dialogPanel.setLayout(new GridLayout(1,2, 10, 0));
		JPanel hostInfoPanel = new JPanel();
		hostInfoPanel.setLayout(new BoxLayout(hostInfoPanel, BoxLayout.PAGE_AXIS));
		JPanel userInfoPanel = new JPanel();
		userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.PAGE_AXIS));
		
		JTextField usernameField = new JTextField(15);
		JPasswordField passwordField = new JPasswordField(15);
		JTextField hostAddressField = new JTextField(15);
		JTextField portField = new JTextField(5);
		JCheckBox savePresetCheck = new JCheckBox("Save this Preset");
		JCheckBox loginAsGuest = new JCheckBox("Login as Guest");
		
		usernameField.setText(this.username);
		passwordField.setText((this.password == null) ? "" : new String(this.password));
		hostAddressField.setText(this.hostAddress);
		portField.setText(this.portNumber == null ? null : this.portNumber.toString());
		
		savePresetCheck.setSelected(this.password == null);
		
		usernameField.addKeyListener(new KeyListener()
		{
			public void keyPressed(KeyEvent arg0){}
			public void keyReleased(KeyEvent arg0)
			{
				savePresetCheck.setSelected(true);
			}
			public void keyTyped(KeyEvent arg0){}
		});
		
		loginAsGuest.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				if(loginAsGuest.isSelected())
				{
					usernameField.setEditable(false);
					passwordField.setEditable(false);
				}
				else
				{
					usernameField.setEditable(true);
					passwordField.setEditable(true);
				}
			}
		});
		
		hostInfoPanel.add(new JLabel("Host Address:"));
		hostInfoPanel.add(hostAddressField);
		hostInfoPanel.add(new JLabel("Port:"));
		hostInfoPanel.add(portField);
		hostInfoPanel.add(savePresetCheck);
		
		userInfoPanel.add(new JLabel("Enter Username:"));
		userInfoPanel.add(usernameField);
		userInfoPanel.add(new JLabel("Enter Password:"));
		userInfoPanel.add(passwordField);
		userInfoPanel.add(loginAsGuest);

		dialogPanel.add(hostInfoPanel);
		dialogPanel.add(userInfoPanel);
		
		String[] options = {"OK", "New Account", "Cancel"};
		int returnValue = JOptionPane.showOptionDialog(null, dialogPanel, 
				"Client Authentication", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

		try
		{
			saveSettings = savePresetCheck.isSelected();
			this.hostAddress = hostAddressField.getText();
			this.portNumber = Integer.valueOf(portField.getText());
		}
		catch (NumberFormatException e)
		{
			AbstractClient.logException(e);
			throw new AuthenticationException("Invalid Input");
		}
		
		if (returnValue == 0)
		{
			if(loginAsGuest.isSelected())
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
				this.username = usernameField.getText();
				
				char[] pass = passwordField.getPassword();
				this.password = new byte[pass.length];
				
				if(pass.length < 6)
					throw new AuthenticationException("Your password is too short. Password must be a minimum of 6 characters.");
				
				for(char character : pass)
				{
					if(character > Byte.MAX_VALUE)
						throw new AuthenticationException("Invalid Password; ASCII characters only: a-z, A-Z, 0-9, or any !\"#$%&\'()*+,-./:;<=>?@[\\]^_`{|}~");
				}
				
				for(int i = 0; i < this.password.length; i++)
					this.password[i] = (byte)pass[i];
				
				Arrays.fill(pass, Character.MIN_VALUE);
				passwordField.setText("");
			}
		}
		else if(returnValue == 1)
		{
			this.guest = false;
			this.newAccount = true;
			
			JPanel newAccountDialog = new JPanel();
			newAccountDialog.setLayout(new BorderLayout(10,0));
			JPanel inputPane = new JPanel();
			inputPane.setLayout(new BoxLayout(inputPane, BoxLayout.PAGE_AXIS));
			JPanel informationPolicy = new JPanel();
			informationPolicy.setLayout(new BorderLayout());
			informationPolicy.setBorder(BorderFactory.createTitledBorder("Account Store Policy"));
			
			JTextField hAddr = new JTextField(15);
			JTextField hPort = new JTextField(15);
			JTextField email = new JTextField(15);
			JTextField username = new JTextField(15);
			JPasswordField password = new JPasswordField(15);
			JPasswordField confirmPassword = new JPasswordField(15);
			JLabel passwordStrength = new JLabel();
			passwordStrength.setText("Strength: Poor");
			passwordStrength.setForeground(Color.RED);
			passwordStrength.setToolTipText("Password must be a minimum of 6 characters. Valid characters include a-z, A-Z, 0-9, or any !\"#$%&\'()*+,-./:;<=>?@[\\]^_`{|}~");
			
			inputPane.add(new JLabel("Host Address:"));
			inputPane.add(hAddr);
			inputPane.add(new JLabel("Port Number:"));
			inputPane.add(hPort);
			inputPane.add(new JLabel("Email Address:"));
			inputPane.add(email);
			inputPane.add(new JLabel("New Username:"));
			inputPane.add(username);
			inputPane.add(new JLabel("New Password:"));
			inputPane.add(password);
			inputPane.add(new JLabel("Confirm Password:"));
			inputPane.add(confirmPassword);
			inputPane.add(passwordStrength);
			
			password.addKeyListener(new KeyListener()
			{
				public void keyPressed(KeyEvent arg0){}
				public void keyReleased(KeyEvent arg0)
				{
					if(password.getPassword().length <= 6)
					{
						passwordStrength.setText("Strength: Poor");
						passwordStrength.setForeground(Color.RED);
					}
					else if(password.getPassword().length <= 8)
					{
						passwordStrength.setText("Strength: Moderate");
						passwordStrength.setForeground(Color.BLUE);
					}
					else if(password.getPassword().length <= 10)
					{
						passwordStrength.setText("Strength: Good");
						passwordStrength.setForeground(Color.GREEN);
					}
					else if(password.getPassword().length > 10)
					{
						passwordStrength.setText("Strength: Great");
						passwordStrength.setForeground(Color.GREEN);
					}
				}
				public void keyTyped(KeyEvent arg0){}
			});
			
			confirmPassword.addKeyListener(new KeyListener()
			{
				public void keyPressed(KeyEvent arg0){}
				public void keyReleased(KeyEvent arg0)
				{
					if(Arrays.equals(password.getPassword(), confirmPassword.getPassword()))
					{
						passwordStrength.setText("Passwords Match");
						passwordStrength.setForeground(Color.GREEN);
					}
					else
					{
						passwordStrength.setText("Passwords do not Match");
						passwordStrength.setForeground(Color.RED);
					}
				}
				public void keyTyped(KeyEvent arg0){}
			});
			
			JTextArea policy = new JTextArea(6,25);
			policy.setWrapStyleWord(true);
			policy.setLineWrap(true);
			policy.setOpaque(false);
			policy.setEditable(false);
			policy.setFocusable(false);
			policy.setFont(new Font("Arial", Font.PLAIN, 11));
			
			String policyText = "Account passwords are stored using secure salted password hashing with SHA-256 level cryptography. "
					+ "This means passwords are never stored in plain text. Passwords are hashed with 256-bit salts, which prevent "
					+ "attackers from dictionary and brute-force attacks.";
			
			policy.setText(policyText);
			informationPolicy.add(policy, BorderLayout.CENTER);
			
			newAccountDialog.add(inputPane, BorderLayout.CENTER);
			newAccountDialog.add(informationPolicy, BorderLayout.LINE_END);
			
			String[] dialogOptions = {"OK", "Cancel"};
			int option = JOptionPane.showOptionDialog(null, newAccountDialog, 
					"New Account", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, dialogOptions, dialogOptions[0]);
			
			if(option == 0)
			{
				try
				{
					this.hostAddress = hostAddressField.getText();
					this.portNumber = Integer.valueOf(portField.getText());
				}
				catch (NumberFormatException e)
				{
					AbstractClient.logException(e);
					throw new AuthenticationException("Invalid Input");
				}
				
				this.emailAddress = email.getText();
				this.username = username.getText();
				
				char[] pass = password.getPassword();
				this.password = new byte[pass.length];
				
				if(pass.length < 6)
					throw new AuthenticationException("Your password is too short. Password must be a minimum of 6 characters.");
				
				for(char character : pass)
				{
					if(character > Byte.MAX_VALUE)
						throw new AuthenticationException("Invalid Password; ASCII characters only: a-z, A-Z, 0-9, or any !\"#$%&\'()*+,-./:;<=>?@[\\]^_`{|}~");
				}
				
				for(int i = 0; i < this.password.length; i++)
					this.password[i] = (byte)pass[i];
				
				Arrays.fill(pass, Character.MIN_VALUE);
				password.setText("");
				confirmPassword.setText("");
			}
			else
				throw new AuthenticationAbortedException("Authentication Aborted by User");
		}
		else
			throw new AuthenticationAbortedException("Authentication Aborted by User");
			
		if(saveSettings)
			this.savePreset();
	}
	
	/**Attempt to load saved preset from application temporary directory. Assigns preset settings to
	  *{@code Authenticator} object fields.
	  *@throws FileNotFoundException if the preset file could not be found*/
	private void loadPreset() throws FileNotFoundException
	{
		File presetFile = new File(this.presetFileLocation + "PRESET.dat");
		
		//---ATTEMPT TO READ PRESET FROM FILE---//
		try
		{
			ObjectInputStream presetIn = new ObjectInputStream(new FileInputStream(presetFile));
			Preset savedPreset = (Preset)presetIn.readObject();
			
			if(savedPreset != null)
			{
				this.username = savedPreset.getUsername();
				this.password = savedPreset.getPassword();
				this.hostAddress = savedPreset.getHostAddress();;
				this.portNumber = savedPreset.getPort();
			}
			
			presetIn.close();
		}
		catch(IOException | ClassNotFoundException e)
		{
			AbstractClient.logException(e);
		}
	}
	
	/**Attempt to save current settings to preset file in application temporary directory.*/
	private void savePreset()
	{
		File presetFile = new File(this.presetFileLocation + "PRESET.dat");
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
		try
		{
			Preset savedPreset = new Preset(this.username, this.password, this.hostAddress, this.portNumber);
			ObjectOutputStream presetOut = new ObjectOutputStream(new FileOutputStream(presetFile));
			presetOut.writeObject(savedPreset);
			presetOut.close();
		}
		catch(IOException e)
		{
			AbstractClient.logException(e);
		}
	}
	
	/***/
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
		return this.portNumber.intValue();
	}
	
	/***/
	public void removeSensitiveInformation()
	{
		Arrays.fill(this.password, Byte.MIN_VALUE);
	}
}