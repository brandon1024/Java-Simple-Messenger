package webchatinterface.server.ui.dialog;

import webchatinterface.server.network.NetworkManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NetworkManagerDialog extends JFrame implements ActionListener
{
	private NetworkManager netManager;
	private JMenuBar menuBar;
	private JMenu manageConnectedUsers;
	private JMenu manageActiveRooms;
	private JMenu manageUserDatabase;
	private JMenu manageBlacklist;
	
	public NetworkManagerDialog(NetworkManager netManager)
	{
		super("Network Manager");
		this.netManager = netManager;
		
		this.menuBar = new JMenuBar();
		this.manageConnectedUsers = new JMenu("Manage Connected Users");
		this.manageActiveRooms = new JMenu("Manage Active Rooms");
		this.manageUserDatabase = new JMenu("Manage User Database");
		this.manageBlacklist = new JMenu("Manage Blacklist");
		
		this.menuBar.add(this.manageConnectedUsers);
	}

	public void actionPerformed(ActionEvent event)
	{
		
	}
}
