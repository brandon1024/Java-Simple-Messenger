package webchatinterface.server.account;

import webchatinterface.AbstractIRC;
import webchatinterface.server.AbstractServer;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;

public final class AccountManager
{
	private static File accountStore;
	private static File tempAccountStore;
	
	static
	{
		AccountManager.accountStore = new File(AbstractIRC.SERVER_APPLCATION_DIRECTORY + "AccountStore.db");
		AccountManager.tempAccountStore = new File(AbstractIRC.SERVER_APPLCATION_DIRECTORY + "temp.db");
		
		if(!AccountManager.accountStore.exists())
		{
			try
			{
				AccountManager.accountStore.createNewFile();
			}
			catch (IOException e)
			{
				AbstractServer.logException(e);
			}
		}
	}
	
	public static boolean createNewAccount(byte[] emailAddress, byte[] username, byte[] password) throws NoSuchAlgorithmException, FileNotFoundException, IOException, ClassNotFoundException
	{
		//Generate Salt of Length 32
		SecureRandom saltGenerator = SecureRandom.getInstance("SHA1PRNG");
		byte[] salt = new byte[32];
		saltGenerator.nextBytes(salt);
		
		//Create saltedPassword array
		byte[] saltedPassword = new byte[salt.length + password.length];
		System.arraycopy(password, 0, saltedPassword, 0, password.length);
		System.arraycopy(salt, 0, saltedPassword, password.length, salt.length);
		
		//Remove Sensitive Information
		Arrays.fill(password, Byte.MIN_VALUE);
		password = null;
		
		//Hash Salted Password
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.digest(saltedPassword);
		UserFile accountInformation = new UserFile(emailAddress, username, salt, saltedPassword);
		
		//Write New User File
		return AccountManager.writeUserFile(accountInformation);
	}

	public static boolean verifyCredentials(byte[] username, byte[] password) throws NoSuchAlgorithmException, FileNotFoundException, IOException, ClassNotFoundException
	{
		//Search accountStore for User File
		UserFile readFile = null;
		synchronized(AccountManager.class)
		{
			readFile = AccountManager.readUserFile(username, null);
		}
		
		//If User File Not Found
		if(readFile == null)
			return false;
		
		return AccountManager.verifyCredentials(readFile, password);
	}
	
	private static boolean verifyCredentials(UserFile account, byte[] password) throws NoSuchAlgorithmException
	{
		//Read Account Information
		byte[] readSalt = account.getSalt();
		byte[] readSaltedHashPassword = account.getSaltedHashPassword();
		
		//Hash User-Provided Password Using Salt From User File
		byte[] saltedHashPassword = new byte[readSalt.length + password.length];
		System.arraycopy(password, 0, saltedHashPassword, 0, password.length);
		System.arraycopy(readSalt, 0, saltedHashPassword, password.length, readSalt.length);
		
		//Remove Sensitive Information
		Arrays.fill(password, Byte.MIN_VALUE);
		password = null;
		
		//Hash Salted Password
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.digest(saltedHashPassword);
		
		//Compare User-Provided Salted Hash with Account Salted Hash
		return MessageDigest.isEqual(saltedHashPassword, readSaltedHashPassword);
	}
	
	private synchronized static boolean writeUserFile(UserFile accountInformation) throws IOException, ClassNotFoundException
	{
		AccountManager.tempAccountStore.createNewFile();
		
		//Check if account already exists with matching username or email address in accountStore
		if(readUserFile(accountInformation.getUsername(), accountInformation.getEmailAddress()) != null)
			return false;
		
		//Write entire accountStore to temporary disk location
		try(ObjectInputStream final_OIS = new ObjectInputStream(new FileInputStream(AccountManager.accountStore));
			ObjectOutputStream temp_OOS = new ObjectOutputStream(new FileOutputStream(AccountManager.tempAccountStore)))
		{
			while(true)
			{
				UserFile readFile = (UserFile)final_OIS.readObject();
				temp_OOS.writeObject(readFile);
			}
		}
		catch(EOFException e){}
		
		//Rewrite temporary accountStore to the main accountStore with new user account appended
		ObjectOutputStream final_OOS = new ObjectOutputStream(new FileOutputStream(AccountManager.accountStore));
		try(ObjectInputStream temp_OIS = new ObjectInputStream(new FileInputStream(AccountManager.tempAccountStore)))
		{
			while(true)
			{
				UserFile readFile = (UserFile)temp_OIS.readObject();
				final_OOS.writeObject(readFile);
			}
		}
		catch(EOFException e)
		{
			AbstractServer.logException(e);
		}
		
		final_OOS.writeObject(accountInformation);
		final_OOS.close();
		AccountManager.tempAccountStore.delete();
		
		return true;
	}
	
	private synchronized static UserFile readUserFile(byte[] username, byte[] emailAddress) throws FileNotFoundException, IOException, ClassNotFoundException
	{
		//Search for account in the accountStore, return userFile object if found
		try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(AccountManager.accountStore)))
		{
			//Unable to Search If No Information Provided
			if(username == null && emailAddress == null)
			{
				throw new NullPointerException("both username and emailaddress arguments are null");
			}
			
			//Search by Username Only
			if(username == null)
			{
				while(true)
				{
					UserFile readFile = (UserFile)ois.readObject();
					if(Arrays.equals(emailAddress, readFile.getEmailAddress()))
						return readFile;
				}
			}
			//Search by Email Address Only
			else if(emailAddress == null)
			{
				while(true)
				{
					UserFile readFile = (UserFile)ois.readObject();
					if(Arrays.equals(username, readFile.getUsername()))
						return readFile;
				}
			}
			//Search by Username or Email Address
			else
			{
				while(true)
				{
					UserFile readFile = (UserFile)ois.readObject();
					if(Arrays.equals(username, readFile.getUsername()))
						return readFile;
					if(Arrays.equals(emailAddress, readFile.getEmailAddress()))
						return readFile;
				}
			}
		}
		catch(EOFException e){}
		
		return null;
	}
	
	public synchronized static boolean modifyAccountUsername(byte[] oldUsername, byte[] newUsername, byte[] password) throws IOException, NoSuchAlgorithmException, ClassNotFoundException
	{
		//Verify that a user file with the desired username does not exist
		if(readUserFile(newUsername, null) != null)
			return false;
		
		//Write entire accountStore to temporary disk location
		//If desired account is encountered, verify credentials and assign to temp object reference
		UserFile userAccount = null;
		try(ObjectInputStream final_OIS = new ObjectInputStream(new FileInputStream(AccountManager.accountStore));
			ObjectOutputStream temp_OOS = new ObjectOutputStream(new FileOutputStream(AccountManager.tempAccountStore)))
		{
			while(true)
			{
				UserFile readFile = (UserFile)final_OIS.readObject();
				
				if(Arrays.equals(readFile.getUsername(), oldUsername))
				{
					userAccount = readFile;
					if(!verifyCredentials(userAccount, password))
						return false;
				}
				else
				{
					temp_OOS.writeObject(readFile);
				}
			}
		}
		catch(EOFException e){}
		
		//Return If Account Not Found
		if(userAccount == null)
			return false;
		
		userAccount.setUsername(newUsername);
		
		//Rewrite temporary accountStore to the main accountStore with new user account appended
		ObjectOutputStream final_OOS = new ObjectOutputStream(new FileOutputStream(AccountManager.accountStore));
		try(ObjectInputStream temp_OIS = new ObjectInputStream(new FileInputStream(AccountManager.tempAccountStore)))
		{
			while(true)
			{
				UserFile readFile = (UserFile)temp_OIS.readObject();
				final_OOS.writeObject(readFile);
			}
		}
		catch(EOFException e)
		{
			AbstractServer.logException(e);
		}
		final_OOS.writeObject(userAccount);
		final_OOS.close();
		AccountManager.tempAccountStore.delete();
		
		return true;
	}
	
	public synchronized static boolean modifyAccountEmailAddress(byte[] oldEmailAddress, byte[] newEmailAddress, byte[] password) throws FileNotFoundException, IOException, NoSuchAlgorithmException, ClassNotFoundException
	{
		//Verify that a user file with the desired username does not exist
		if(readUserFile(null, newEmailAddress) != null)
			return false;
		
		//Write entire accountStore to temporary disk location
		//If desired account is encountered, verify credentials and assign to temp object reference
		UserFile userAccount = null;
		try(ObjectInputStream final_OIS = new ObjectInputStream(new FileInputStream(AccountManager.accountStore));
			ObjectOutputStream temp_OOS = new ObjectOutputStream(new FileOutputStream(AccountManager.tempAccountStore)))
		{
			while(true)
			{
				UserFile readFile = (UserFile)final_OIS.readObject();
				
				if(Arrays.equals(readFile.getEmailAddress(), oldEmailAddress))
				{
					userAccount = readFile;
					if(!verifyCredentials(userAccount, password))
						return false;
				}
				else
				{
					temp_OOS.writeObject(readFile);
				}
			}
		}
		catch(EOFException e){}
		
		//Return If Account Not Found
		if(userAccount == null)
			return false;
		
		userAccount.setEmailAddress(newEmailAddress);
		
		//Rewrite temporary accountStore to the main accountStore with new user account appended
		ObjectOutputStream final_OOS = new ObjectOutputStream(new FileOutputStream(AccountManager.accountStore));
		try(ObjectInputStream temp_OIS = new ObjectInputStream(new FileInputStream(AccountManager.tempAccountStore)))
		{
			while(true)
			{
				UserFile readFile = (UserFile)temp_OIS.readObject();
				final_OOS.writeObject(readFile);
			}
		}
		catch(EOFException e)
		{
			AbstractServer.logException(e);
		}
		final_OOS.writeObject(userAccount);
		final_OOS.close();
		AccountManager.tempAccountStore.delete();
		
		return true;
	}
	
	public synchronized static boolean modifyAccountPassword(byte[] username, byte[] oldPassword, byte[] newPassword) throws IOException, ClassNotFoundException, NoSuchAlgorithmException
	{
		//Write entire accountStore to temporary disk location
		//If desired account is encountered, verify credentials and assign to temp object reference
		UserFile userAccount = null;
		try(ObjectInputStream final_OIS = new ObjectInputStream(new FileInputStream(AccountManager.accountStore));
			ObjectOutputStream temp_OOS = new ObjectOutputStream(new FileOutputStream(AccountManager.tempAccountStore)))
		{
			while(true)
			{
				UserFile readFile = (UserFile)final_OIS.readObject();
				
				if(Arrays.equals(readFile.getUsername(), username))
				{
					userAccount = readFile;
					if(!verifyCredentials(userAccount, oldPassword))
						return false;
				}
				else
				{
					temp_OOS.writeObject(readFile);
				}
			}
		}
		catch(EOFException e){}
		
		//Return If Account Not Found
		if(userAccount == null)
			return false;
		
		//Generate Salt of Length 32
		SecureRandom saltGenerator = SecureRandom.getInstance("SHA1PRNG");
		byte[] salt = new byte[32];
		saltGenerator.nextBytes(salt);
		
		//Create saltedPassword array
		byte[] saltedPassword = new byte[salt.length + newPassword.length];
		System.arraycopy(newPassword, 0, saltedPassword, 0, newPassword.length);
		System.arraycopy(salt, 0, saltedPassword, newPassword.length, salt.length);
		
		//Remove Sensitive Information
		Arrays.fill(newPassword, Byte.MIN_VALUE);
		newPassword = null;
		
		//Hash Salted Password
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.digest(saltedPassword);
		
		userAccount.setSalt(salt);
		userAccount.setSaltedHashPassword(saltedPassword);
		
		//Rewrite temporary accountStore to the main accountStore with new user account appended
		ObjectOutputStream final_OOS = new ObjectOutputStream(new FileOutputStream(AccountManager.accountStore));
		try(ObjectInputStream temp_OIS = new ObjectInputStream(new FileInputStream(AccountManager.tempAccountStore)))
		{
			while(true)
			{
				UserFile readFile = (UserFile)temp_OIS.readObject();
				final_OOS.writeObject(readFile);
			}
		}
		catch(EOFException e)
		{
			AbstractServer.logException(e);
		}
		final_OOS.writeObject(userAccount);
		final_OOS.close();
		AccountManager.tempAccountStore.delete();
		
		return true;
	}
	
	public synchronized static UserFile removeAccount(byte[] username, byte[] password) throws FileNotFoundException, IOException, NoSuchAlgorithmException, ClassNotFoundException
	{
		//Write entire accountStore to temporary disk location
		//If desired account is encountered, verify credentials and assign to temp object reference
		UserFile userAccount = null;
		try(ObjectInputStream final_OIS = new ObjectInputStream(new FileInputStream(AccountManager.accountStore));
			ObjectOutputStream temp_OOS = new ObjectOutputStream(new FileOutputStream(AccountManager.tempAccountStore)))
		{
			while(true)
			{
				UserFile readFile = (UserFile)final_OIS.readObject();
				
				if(Arrays.equals(readFile.getUsername(), username))
				{
					userAccount = readFile;
					if(!verifyCredentials(userAccount, password))
						return null;
				}
				else
				{
					temp_OOS.writeObject(readFile);
				}
			}
		}
		catch(EOFException e){}
		
		//Return If Account Not Found
		if(userAccount == null)
			return null;
		
		//Rewrite temporary accountStore to the main accountStore with new user account appended
		ObjectOutputStream final_OOS = new ObjectOutputStream(new FileOutputStream(AccountManager.accountStore));
		try(ObjectInputStream temp_OIS = new ObjectInputStream(new FileInputStream(AccountManager.tempAccountStore)))
		{
			while(true)
			{
				UserFile readFile = (UserFile)temp_OIS.readObject();
				final_OOS.writeObject(readFile);
			}
		}
		catch(EOFException e)
		{
			AbstractServer.logException(e);
		}
		
		final_OOS.close();
		AccountManager.tempAccountStore.delete();
		
		return userAccount;
	}
	
	public synchronized static String[][] retrieveBasicAccountList() throws FileNotFoundException, IOException, ClassNotFoundException
	{
		//Instantiate ArrayList for Username and Email Address
		@SuppressWarnings("unchecked")
		ArrayList<String>[] list = new ArrayList[2];
		list[0] = new ArrayList<String>(); //username
		list[1] = new ArrayList<String>(); //email address
		
		//Read User Information From Account Store
		try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(AccountManager.accountStore)))
		{
			while(true)
			{
				UserFile readFile = (UserFile)ois.readObject();
				list[0].add(new String(readFile.getUsername()));
				list[1].add(new String(readFile.getEmailAddress()));
			}
		}
		catch(EOFException e){}
		
		//Return 2D Array
		return new String[][] {list[0].toArray(new String[0]), list[1].toArray(new String[0])};
	}
}