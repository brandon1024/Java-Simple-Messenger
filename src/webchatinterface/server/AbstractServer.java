package webchatinterface.server;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import util.Logger;
import webchatinterface.AbstractIRC;
import webchatinterface.util.ClientUser;

public abstract class AbstractServer
{
	private static Logger logger;
	private static ClientUser server;
	public static boolean startServerWhenApplicationStarts;
	public static boolean openMinimized;
	public static boolean showResourceMonitor;
	public static boolean alwaysSendWelcomeMessage;
	public static String newMemberGuestWelcomeMessage;
	public static String returningMemberWelcomeMessage;
	public static String serverBindIPAddress;
	public static int serverPortNumber;
	public static int maxConnectedUsers;
	public static int loginTimeoutSeconds;
	public static int fileTransferBufferSize;
	public static long fileTransferSizeLimit;
	public static String messageDigestHashingAlgorithm;
	public static String secureRandomSaltAlgorithm;
	public static int secureRandomSaltLength;
	public static int userIDKeyLength;
	public static String userIDAlgorithm;
	public static boolean blacklistAccountIPInconsistentUserID;
	public static boolean loggingEnabled;
	public static boolean logOnlyWarningsExceptions;
	public static boolean logOnlyServerActivity;
	public static boolean logAllActivity;
	public static boolean logAllToSingleFile;
	public static String logFileFormat;
	public static int logFileSizeLimit;
	public static int deleteLogAfterSessions;
	public static boolean showTimestampsInLogFiles;
	public static Color foregroundColor;
	public static Color backgroundColor;
	public static Font textFont;
	
	static
	{
		AbstractServer.logger = new Logger(AbstractIRC.SERVER_APPLCATION_DIRECTORY + "LOGS" + File.separator);
		AbstractServer.server = new ClientUser("SERVER", "0");
		
		//Attempt to Load Saved Settings, Use Default Settings if Unsuccessful
		try
		{
			AbstractServer.loadState();
		}
		catch (Exception e1)
		{
			AbstractServer.startServerWhenApplicationStarts = false;
			AbstractServer.openMinimized = false;
			AbstractServer.showResourceMonitor = true;
			AbstractServer.alwaysSendWelcomeMessage = false;
			AbstractServer.newMemberGuestWelcomeMessage = "";
			AbstractServer.returningMemberWelcomeMessage = "";
			AbstractServer.serverBindIPAddress = "default";
			AbstractServer.serverPortNumber = 5100;
			AbstractServer.maxConnectedUsers = 200;
			AbstractServer.loginTimeoutSeconds = 0;
			AbstractServer.fileTransferBufferSize = 4096;
			AbstractServer.fileTransferSizeLimit = 104857600;
			AbstractServer.messageDigestHashingAlgorithm = "SHA-256";
			AbstractServer.secureRandomSaltAlgorithm = "SHA1PRNG";
			AbstractServer.secureRandomSaltLength = 32;
			AbstractServer.userIDKeyLength = 256;
			AbstractServer.userIDAlgorithm = "ALPHANUMERIC_MIXED_CASE";
			AbstractServer.blacklistAccountIPInconsistentUserID = true;
			AbstractServer.loggingEnabled = true;
			AbstractServer.logOnlyWarningsExceptions = false;
			AbstractServer.logOnlyServerActivity = false;
			AbstractServer.logAllActivity = true;
			AbstractServer.logAllToSingleFile = false;
			AbstractServer.logFileFormat = "LOG";
			AbstractServer.logFileSizeLimit = 0;
			AbstractServer.deleteLogAfterSessions = 0;
			AbstractServer.showTimestampsInLogFiles = true;
			AbstractServer.foregroundColor = Color.BLACK;
			AbstractServer.backgroundColor = Color.WHITE;
			AbstractServer.textFont = new Font("Courier New", Font.PLAIN, 12);
			
			//Save Default Settings
			try
			{
				AbstractServer.saveState();
			}
			catch(Exception e2){}
		}
	}
	
	/**Load saved settings from configuration file config.ini in application directory.*/
	public static void loadState() throws IOException
	{
		File configFile = new File(AbstractIRC.SERVER_APPLCATION_DIRECTORY + "config.ini");
		
		if(!configFile.exists())
		{
			throw new IOException("configuration file does not exist");
		}
		
		try
		{
			//Retrieve Body of Configuration File as String
			Scanner fileScanner = new Scanner(configFile);
			Scanner contentScanner = fileScanner.useDelimiter("\\Z");
			String text = contentScanner.next();
			contentScanner.close();
			fileScanner.close();
			
			//Split String into Array of Lines
			String[] lines = text.split("\r\n|\r|\n");
			
			//Verify Configuration File
			String temp = lines[0].replace("[", "");
			temp = temp.replace("]", "");
			if(!temp.equals(AbstractIRC.SERVER_APPLICATION_NAME))
				throw new Exception();
			
			temp = lines[1].substring(lines[1].indexOf('=')+1);
			if(!temp.equals(AbstractIRC.SERVER_VERSION))
				throw new Exception();
			
			temp = lines[2].substring(lines[2].indexOf('=')+1);
			if(!temp.equals(AbstractIRC.RELEASE_DATE))
				throw new Exception();
			
			temp = lines[3].substring(lines[3].indexOf('=')+1);
			if(!temp.equals(AbstractIRC.AUTHOR))
				throw new Exception();
			
			temp = lines[4].substring(lines[4].indexOf('=')+1);
			if(!temp.equals(AbstractIRC.SERVER_APPLCATION_DIRECTORY))
				throw new Exception();
			
			//Parse Configuration Preferences
			temp = lines[6].substring(lines[6].indexOf('=')+1);
			AbstractServer.startServerWhenApplicationStarts = Boolean.parseBoolean(temp);
			
			temp = lines[7].substring(lines[7].indexOf('=')+1);
			AbstractServer.openMinimized = Boolean.parseBoolean(temp);
			
			temp = lines[8].substring(lines[8].indexOf('=')+1);
			AbstractServer.showResourceMonitor = Boolean.parseBoolean(temp);
			
			temp = lines[10].substring(lines[10].indexOf('=')+1);
			AbstractServer.alwaysSendWelcomeMessage = Boolean.parseBoolean(temp);
			
			temp = lines[11].substring(lines[11].indexOf('=')+1);
			AbstractServer.newMemberGuestWelcomeMessage = temp;
			
			temp = lines[12].substring(lines[12].indexOf('=')+1);
			AbstractServer.returningMemberWelcomeMessage = temp;
			
			temp = lines[14].substring(lines[14].indexOf('=')+1);
			AbstractServer.serverBindIPAddress = temp;
			
			temp = lines[15].substring(lines[15].indexOf('=')+1);
			AbstractServer.serverPortNumber = Integer.parseInt(temp);
			
			temp = lines[16].substring(lines[16].indexOf('=')+1);
			AbstractServer.maxConnectedUsers = Integer.parseInt(temp);
			
			temp = lines[17].substring(lines[17].indexOf('=')+1);
			AbstractServer.loginTimeoutSeconds = Integer.parseInt(temp);
			
			temp = lines[19].substring(lines[19].indexOf('=')+1);
			AbstractServer.fileTransferBufferSize = Integer.parseInt(temp);
			
			temp = lines[20].substring(lines[20].indexOf('=')+1);
			AbstractServer.fileTransferSizeLimit = Long.parseLong(temp);
			
			temp = lines[22].substring(lines[22].indexOf('=')+1);
			AbstractServer.messageDigestHashingAlgorithm = temp;
			
			temp = lines[23].substring(lines[23].indexOf('=')+1);
			AbstractServer.secureRandomSaltAlgorithm = temp;
			
			temp = lines[24].substring(lines[24].indexOf('=')+1);
			AbstractServer.secureRandomSaltLength = Integer.parseInt(temp);
			
			temp = lines[25].substring(lines[25].indexOf('=')+1);
			AbstractServer.userIDKeyLength = Integer.parseInt(temp);
			
			temp = lines[26].substring(lines[26].indexOf('=')+1);
			AbstractServer.userIDAlgorithm = temp;
			
			temp = lines[27].substring(lines[27].indexOf('=')+1);
			AbstractServer.blacklistAccountIPInconsistentUserID = Boolean.parseBoolean(temp);
			
			temp = lines[29].substring(lines[29].indexOf('=')+1);
			AbstractServer.loggingEnabled = Boolean.parseBoolean(temp);
			
			temp = lines[30].substring(lines[30].indexOf('=')+1);
			AbstractServer.logOnlyWarningsExceptions = Boolean.parseBoolean(temp);
			
			temp = lines[31].substring(lines[31].indexOf('=')+1);
			AbstractServer.logOnlyServerActivity = Boolean.parseBoolean(temp);
			
			temp = lines[32].substring(lines[32].indexOf('=')+1);
			AbstractServer.logAllActivity = Boolean.parseBoolean(temp);
			
			temp = lines[33].substring(lines[33].indexOf('=')+1);
			AbstractServer.logAllToSingleFile = Boolean.parseBoolean(temp);
			
			temp = lines[34].substring(lines[34].indexOf('=')+1);
			AbstractServer.logFileFormat = temp;
			
			temp = lines[35].substring(lines[35].indexOf('=')+1);
			AbstractServer.logFileSizeLimit = Integer.parseInt(temp);
			
			temp = lines[36].substring(lines[36].indexOf('=')+1);
			AbstractServer.deleteLogAfterSessions = Integer.parseInt(temp);
			
			temp = lines[37].substring(lines[37].indexOf('=')+1);
			AbstractServer.showTimestampsInLogFiles = Boolean.parseBoolean(temp);
			
			temp = lines[39].substring(lines[39].indexOf('=')+1);
			String[] values = temp.split(",");
			int red = Integer.parseInt(values[0]);
			int green = Integer.parseInt(values[1]);
			int blue = Integer.parseInt(values[2]);
			int alpha = Integer.parseInt(values[3]);
			AbstractServer.foregroundColor = new Color(red, green, blue, alpha);
			
			temp = lines[40].substring(lines[40].indexOf('=')+1);
			values = temp.split(",");
			red = Integer.parseInt(values[0]);
			green = Integer.parseInt(values[1]);
			blue = Integer.parseInt(values[2]);
			alpha = Integer.parseInt(values[3]);
			AbstractServer.backgroundColor = new Color(red, green, blue, alpha);
			
			temp = lines[41].substring(lines[41].indexOf('=')+1);
			String fontName = temp;
			
			temp = lines[42].substring(lines[42].indexOf('=')+1);
			int fontSize = Integer.parseInt(temp);
			
			temp = lines[43].substring(lines[43].indexOf('=')+1);
			boolean bold = Boolean.parseBoolean(temp);
			
			temp = lines[44].substring(lines[44].indexOf('=')+1);
			boolean italic = Boolean.parseBoolean(temp);
			
			temp = lines[45].substring(lines[45].indexOf('=')+1);
			boolean boldItalic = Boolean.parseBoolean(temp);
			
			temp = lines[46].substring(lines[46].indexOf('=')+1);
			boolean plain = Boolean.parseBoolean(temp);
			
			if(bold)
				AbstractServer.textFont = new Font(fontName, Font.BOLD, fontSize);
			else if(italic)
				AbstractServer.textFont = new Font(fontName, Font.ITALIC, fontSize);
			else if(boldItalic)
				AbstractServer.textFont = new Font(fontName, Font.ITALIC | Font.BOLD, fontSize);
			else if(plain)
				AbstractServer.textFont = new Font(fontName, Font.PLAIN, fontSize);
		}
		catch(Exception e)
		{
			throw new RuntimeException("Invalid Configuration File Format");
		}
	}
	
	/**Save current settings to configuration file config.ini in application directory*/
	public static void saveState() throws IOException
	{
		File configFile = new File(AbstractIRC.SERVER_APPLCATION_DIRECTORY + "config.ini");
		
		if(!configFile.exists())
		{
			configFile.createNewFile();
		}
		
		FileWriter fileWriter = new FileWriter(configFile, false);
		String[] lines = new String[47];
		
		lines[0] = "[" + AbstractIRC.SERVER_APPLICATION_NAME + "]" + "\n";
		lines[1] = "Version=" + AbstractIRC.SERVER_VERSION + "\n";
		lines[2] = "ReleaseDate=" + AbstractIRC.RELEASE_DATE + "\n";
		lines[3] = "Author=" + AbstractIRC.AUTHOR + "\n";
		lines[4] = "ApplicationDirectory=" + AbstractIRC.SERVER_APPLCATION_DIRECTORY + "\n";
		lines[5] = "\n";
		lines[6] = "StartServerWhenApplicationStarts=" + AbstractServer.startServerWhenApplicationStarts + "\n";
		lines[7] = "OpenServerMinimized=" + AbstractServer.openMinimized + "\n";
		lines[8] = "ShowResourceMonitor=" + AbstractServer.showResourceMonitor + "\n";
		lines[9] = "\n";
		lines[10] = "AlwaysSendWelcomeMessage=" + AbstractServer.alwaysSendWelcomeMessage + "\n";
		lines[11] = "NewMemberGuestWelcomeMessage=" + AbstractServer.newMemberGuestWelcomeMessage + "\n";
		lines[12] = "ReturningMemberWelcomeMessage=" + AbstractServer.returningMemberWelcomeMessage + "\n";
		lines[13] = "\n";
		lines[14] = "ServerBindIP=" + AbstractServer.serverBindIPAddress + "\n";
		lines[15] = "ServerPortNumber=" + AbstractServer.serverPortNumber + "\n";
		lines[16] = "MaximumConnectedUsers=" + AbstractServer.maxConnectedUsers + "\n";
		lines[17] = "LoginTimeoutSeconds=" + AbstractServer.loginTimeoutSeconds + "\n";
		lines[18] = "\n";
		lines[19] = "FileTransferBufferSizeBytes=" + AbstractServer.fileTransferBufferSize + "\n";
		lines[20] = "FileTransferSizeLimit=" + AbstractServer.fileTransferSizeLimit + "\n";
		lines[21] = "\n";
		lines[22] = "MessageDigestHashingAlgorithm=" + AbstractServer.messageDigestHashingAlgorithm + "\n";
		lines[23] = "SecureRandomSaltAlgorithm=" + AbstractServer.secureRandomSaltAlgorithm + "\n";
		lines[24] = "SecureRandomSaltLength=" + AbstractServer.secureRandomSaltLength + "\n";
		lines[25] = "UserIDKeyLength=" + AbstractServer.userIDKeyLength + "\n";
		lines[26] = "UserIdentificationKeyAlgorithm=" + AbstractServer.userIDAlgorithm + "\n";
		lines[27] = "BlacklistInconsistentAccountAndIPAddress=" + AbstractServer.blacklistAccountIPInconsistentUserID + "\n";
		lines[28] = "\n";
		lines[29] = "ServerLoggingEnabled=" + AbstractServer.loggingEnabled + "\n";
		lines[30] = "LogOnlyWarningsAndExceptions=" + AbstractServer.logOnlyWarningsExceptions + "\n";
		lines[31] = "LogOnlyServerActivity=" + AbstractServer.logOnlyServerActivity + "\n";
		lines[32] = "LogAllActivity=" + AbstractServer.logAllActivity + "\n";
		lines[33] = "LogAllToSingleFile=" + AbstractServer.logAllToSingleFile + "\n";
		lines[34] = "UseCustomLogFileFormat=" + AbstractServer.logFileFormat + "\n";
		lines[35] = "LimitLogFileSize=" + AbstractServer.logFileSizeLimit + "\n";
		lines[36] = "DeleteOldLogFilesAfterSessions=" + AbstractServer.deleteLogAfterSessions + "\n";
		lines[37] = "ShowTimestampsInLogs=" + AbstractServer.showTimestampsInLogFiles + "\n";
		lines[38] = "\n";
		lines[39] = "ConsoleForegroundColor=" + AbstractServer.foregroundColor.getRed() + "," + AbstractServer.foregroundColor.getGreen() + "," + AbstractServer.foregroundColor.getBlue() + "," + AbstractServer.foregroundColor.getAlpha() + "\n";
		lines[40] = "ConsoleBackgroundColor=" + AbstractServer.backgroundColor.getRed() + "," + AbstractServer.backgroundColor.getGreen() + "," + AbstractServer.backgroundColor.getBlue() + "," + AbstractServer.backgroundColor.getAlpha() + "\n";
		lines[41] = "ConsoleFontName=" + AbstractServer.textFont.getFontName() + "\n";
		lines[42] = "ConsoleFontSize=" + AbstractServer.textFont.getSize() + "\n";
		lines[43] = "ConsoleFontBold=" + (AbstractServer.textFont.getStyle() == Font.BOLD) + "\n";
		lines[44] = "ConsoleFontItalic=" + (AbstractServer.textFont.getStyle() == Font.ITALIC) + "\n";
		lines[45] = "ConsoleFontBoldItalic=" + (AbstractServer.textFont.getStyle() == (Font.BOLD | Font.ITALIC)) + "\n";
		lines[46] = "ConsoleFontPlain=" + (AbstractServer.textFont.getStyle() == Font.PLAIN);
		
		for(String line : lines)
		{
			fileWriter.write(line);
		}

		fileWriter.close();
	}
	
	/**Load all default settings.*/
	public static void loadDefaultSettings()
	{
		AbstractServer.startServerWhenApplicationStarts = false;
		AbstractServer.openMinimized = false;
		AbstractServer.showResourceMonitor = true;
		AbstractServer.alwaysSendWelcomeMessage = false;
		AbstractServer.newMemberGuestWelcomeMessage = "";
		AbstractServer.returningMemberWelcomeMessage = "";
		AbstractServer.serverBindIPAddress = "default";
		AbstractServer.serverPortNumber = 5100;
		AbstractServer.maxConnectedUsers = 200;
		AbstractServer.loginTimeoutSeconds = 0;
		AbstractServer.fileTransferBufferSize = 4096;
		AbstractServer.fileTransferSizeLimit = 104857600;
		AbstractServer.messageDigestHashingAlgorithm = "SHA-256";
		AbstractServer.secureRandomSaltAlgorithm = "SHA1PRNG";
		AbstractServer.secureRandomSaltLength = 32;
		AbstractServer.userIDKeyLength = 256;
		AbstractServer.userIDAlgorithm = "ALPHANUMERIC_MIXED_CASE";
		AbstractServer.blacklistAccountIPInconsistentUserID = true;
		AbstractServer.loggingEnabled = true;
		AbstractServer.logOnlyWarningsExceptions = false;
		AbstractServer.logOnlyServerActivity = false;
		AbstractServer.logAllActivity = true;
		AbstractServer.logAllToSingleFile = false;
		AbstractServer.logFileFormat = "LOG";
		AbstractServer.logFileSizeLimit = 0;
		AbstractServer.deleteLogAfterSessions = 0;
		AbstractServer.showTimestampsInLogFiles = true;
		AbstractServer.foregroundColor = Color.BLACK;
		AbstractServer.backgroundColor = Color.WHITE;
		AbstractServer.textFont = new Font("Courier New", Font.PLAIN, 12);
	}
	
	/**Log an exception to the log file.*/
	public synchronized static void logException(Exception e)
	{
		AbstractServer.logger.logException(e);
	}
	
	/**Log a String to the log file*/
	public synchronized static void logString(String str)
	{
		AbstractServer.logger.logString(str);
	}
	
	/**Clear all server logs.*/
	public synchronized static void clearLogs()
	{
		AbstractServer.logger.clearLogs();
	}
	
	/**Retrieve a reference to the server user*/
	public static ClientUser getServerUser()
	{
		return AbstractServer.server;
	}
}