package webchatinterface;

import java.io.File;

/**@author Brandon Richardson
 *@version 1.4.3
 *@since 06/05/2016
 *<p>
 *The AbstractIRC interface is a top level application interface that supplies
 *constants and methods related to the application, such as client and server
 *versions, release dates, temporary application directories, and others.
 *<p>
 *This interface allows these constants to be easily altered with new version
 *releases.
 *<p>
 *Release Notes:
 *<br>Version 1.4.2:
 *<br>	- Implemented File Transfer Feature:
 *<br>		Users are now able to submit files to be broadcasted to all clients connected to
 *<br>		the server. Max file size is 100MB, and all file types are permitted.
 *<br>	- Separated {@code client.ConsoleManager} and {@code client.ConsoleManager} into
 *<br>		console manager and logger classes. The reason for which is to separate standalone
 *<br>		functions into classes.
 *<br>  - Simplified Client GUI class variables by creating a ClientUser class, which represents
 *<br>		all the client user parameters, such as username and user ID.
 *<br>  - Client JMenuItems now disable and re-enable according to the authentication state of the user
 *<br>	- Minor Bug Fixes
 *<br>
 *<br>Version 1.4.3:
 *<br>	- Implemented user accounts. Users can now create a new private account, or log in as a guest.
 *<br>		Passwords are stored by the server using SHA256 hashing with a 64 bit salt which ensures safe
 *<br>		storage of passwords.
 *<br>	- Updated Message and Command timestamps to conform to ISO 8601 format with UTC offset. This
 *<br>		allows messages sent from different timezones to display the timestamp in the civil time
 *<br>		of the client reading the message.
 *<br>	- Implemented availability feature, allowing clients to specify an availability status.
 *<br>  - Added client status bar, used to display the client availability and what chatroom the client
 *<br>		is connected to.
 *<br>  - Updated server 'Show Connected Clients' dialog. Added status icon and status description columns.
 *<br>	- Added new BroadcastHelper class, designed to simply message broadcasting. Also facilitates scheduled
 *<br>		server message broadcasting, and features a redesigned server message broadcast dialog.
 *<br>	- Updated client 'Show Connected Users' function. The list of clients is now displayed in a standalone
 *<br>		dialog. This also allows users to see other users availability. Users with availability OFFLINE or
 *<br>		APPEAR_OFFLINE do not appear in this list.
 *<br>	- Implemented a feature that allows for private and public chatrooms. Now, users can request a 
 *<br>		private chatroom with another client.
 *<br>	- Consolidated TransferBuffer and MultimediaMessage into the single class TransferBuffer. Images
 *<br>		and files are differentiated by the file extension, where files with the extension .JPEG, .JPG, .GIF
 *<br>		and .PNG are displayed as images in the console.
 *<br>	- Image messages may be saved by clicking the image in the popup JDialog.
 *<br>	- To indicate that work is in progress, the wait cursor is displayed when hovering over the application
 *<br>		user interface. This prevents the apparent 'block' in the application runtime.
 *<br>	- Connected users dialog auto will auto refresh every second. Users can now keep the ConnectedUserViewer
 *<br>		window open while using the application to see who is online in real time.
 *<br>	- Redesigned Usage Monitor. Removed non-essential information and added space between columns for
 *<br>		easier reading.
 *<br>	- File chooser dialog for uploading images and files is more user friendly, icons do not take time to buffer
 *<br>	- Minor Bug Fixes
 */

public interface AbstractIRC
{
	/**The version of the client as of the release date*/
	public static final String CLIENT_VERSION = "1.4.3";
	
	/**The version of the server as of the release date*/
	public static final String SERVER_VERSION = "1.4.3";
	
	/**The release date of the WebChatInterface application*/
	public static final String RELEASE_DATE = "07/22/2016";
	
	/**The author of the WebChatInterface application*/
	public static final String AUTHOR = "Brandon Richardson";
	
	/**The temporary directory for configuration, resource and log files for the client application*/
	public static final String CLIENT_APPLCATION_DIRECTORY = System.getProperty("java.io.tmpdir") + "Web Chat Client" + File.separator;
	
	/**The temporary directory for configuration, resource and log files for the server application*/
	public static final String SERVER_APPLCATION_DIRECTORY = System.getProperty("java.io.tmpdir") + "Web Chat Server" + File.separator;
	
	/**The name of the client application*/
	public static final String CLIENT_APPLICATION_NAME = "Web Chat Client Interface";
	
	/**The name of the server application*/
	public static final String SERVER_APPLICATION_NAME = "Web Chat Server Interface";
}
