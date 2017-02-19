package webchatinterface.client.ui.components;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import webchatinterface.client.util.ResourceLoader;
import webchatinterface.util.ClientUser;

/**@author Brandon Richardson
 *@version 1.4.3
 *@since 06/05/2016
  *<p>
  *The {@code StatusBar} class represents a Container with various Components that
  *display the status of the client application.
  *<p>
  *The StatusBar has three JLabels: availabilityStatusIcon which displays an icon showing
  *the availability of the client; availabilityStatus which describes the availability of
  *the client; chatroom which describes what chatroom the client is connected to.
  */

public class StatusBar extends Container
{
	/**Serial Version UID is used as a version control for the class that implements
	 *the serializable interface.*/
	private static final long serialVersionUID = 1326722175903246842L;
	
	/**JLabel that contains the icon that describes the availability of the client*/
	private JLabel availabilityStatusIcon;
	
	/**JLabel that contains a description of the availability of the client*/
	private JLabel availabilityStatus;
	
	/**JLabel that contains a description of the chat room in which the client is connected*/
	private JLabel chatroom;
	
	private ImageIcon availableIcon;
	
	private ImageIcon busyIcon;
	
	private ImageIcon awayIcon;
	
	private ImageIcon appearOfflineIcon;
	
	private ImageIcon offlineIcon;
	
	/**Builds a {@code StatusBar} object. Constructs a new StatusBar with default fields.
	  *By default, the availability is OFFLINE, and the chatroom is "Public Chat Room".*/
	public StatusBar()
	{
		//Build Container and Set Layout Manager
		super();
		super.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		
		//Set Icons
		ResourceLoader rl = ResourceLoader.getInstance();
		this.availableIcon = ResourceLoader.bufferedImageToImageIcon(rl.getStatusAvailableIcon());
		this.busyIcon = ResourceLoader.bufferedImageToImageIcon(rl.getStatusBusyIcon());
		this.awayIcon = ResourceLoader.bufferedImageToImageIcon(rl.getStatusAwayIcon());
		this.appearOfflineIcon = ResourceLoader.bufferedImageToImageIcon(rl.getStatusAppearOfflineIcon());
		this.offlineIcon = ResourceLoader.bufferedImageToImageIcon(rl.getStatusOfflineIcon());
		
		//Construct JLabels and Set Parameters
		this.availabilityStatusIcon = new JLabel(this.offlineIcon);
		this.availabilityStatus = new JLabel(" OFFLINE - ");
		this.availabilityStatus.setFont(new Font("Courier New", Font.PLAIN, 11));
		this.chatroom = new JLabel("Public Chat Room");
		this.chatroom.setFont(new Font("Courier New", Font.PLAIN, 11));
		
		//Add JLabels to Container
		super.add(this.availabilityStatusIcon);
		super.add(this.availabilityStatus);
		super.add(this.chatroom);
	}
	
	/**Set the availability of the user.
	  *@param availability The availability of the client, as described by the static fields in
	  *{@code ClientUser}*/
	public void setAvailability(int availability)
	{
		if(availability == ClientUser.AVAILABLE)
		{
			this.availabilityStatusIcon.setIcon(this.availableIcon);
			this.availabilityStatus.setText(" AVAILABLE - ");
		}
		else if(availability == ClientUser.BUSY)
		{
			this.availabilityStatusIcon.setIcon(this.busyIcon);
			this.availabilityStatus.setText(" BUSY - ");
		}
		else if(availability == ClientUser.AWAY)
		{
			this.availabilityStatusIcon.setIcon(this.awayIcon);
			this.availabilityStatus.setText(" AWAY - ");
		}
		else if(availability == ClientUser.APPEAR_OFFLINE)
		{
			this.availabilityStatusIcon.setIcon(this.appearOfflineIcon);
			this.availabilityStatus.setText(" APPEAR OFFLINE - ");
		}
		else
		{
			this.availabilityStatusIcon.setIcon(this.offlineIcon);
			this.availabilityStatus.setText(" Offline - ");
		}
	}
	
	/**Set the chat room name to display in the StatusBar.
	  *@param chatroomName The name of the chatroom*/
	public void setChatroom(String chatroomName)
	{
		this.chatroom.setText(chatroomName);
	}
}
