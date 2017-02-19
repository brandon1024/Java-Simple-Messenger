package webchatinterface.server.ui.preferences;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.WindowConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import webchatinterface.server.AbstractServer;
import webchatinterface.server.ui.WebChatServerGUI;

public class PreferencesDialog extends JDialog implements ActionListener, WindowListener, TreeSelectionListener
{
	private static final long serialVersionUID = 6276982860422704135L;
	
	private WebChatServerGUI userInterface;
	
	private JPanel masterPane;
	
	private PreferencePanel currentPanel;
	
	private PreferencePanel[] panels;
	
	private JTree settingTree;
	
	private JButton applyButton;
	
	private JButton okButton;
	
	private JButton cancelButton;

	private JButton helpButton;
	
	public PreferencesDialog(WebChatServerGUI userInterface)
	{
		super(userInterface, "Server Preferences", true);
		super.setSize(700, 500);
		super.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		super.setVisible(false);
		super.setResizable(false);
		
		this.userInterface = userInterface;
		this.masterPane = (JPanel)super.getContentPane();
		this.masterPane.setLayout(new BorderLayout(5,5));
		this.masterPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		this.settingTree = this.buildTree();
		this.buildSettingsPanels();
		this.currentPanel = this.panels[0];
		
		this.masterPane.add(this.settingTree, BorderLayout.LINE_START);
		this.masterPane.add(this.currentPanel);
		this.masterPane.add(this.buildButtonPanel(), BorderLayout.PAGE_END);
	}
	
	public void showDialog()
	{
		super.setVisible(true);
	}
	
	public void close(boolean save)
	{
		if(!save)
		{
			super.dispose();
			return;
		}
		
		if(((AdvancedSettingsPanel)this.panels[8]).isEdited())
		{
			JPanel dialogPanel = new JPanel();
			dialogPanel.setLayout(new BorderLayout(5,5));
			
			JTable table = new JTable(new DefaultTableModel(new String[]{"Panel", "Property"}, 0));
			DefaultTableModel model = (DefaultTableModel) table.getModel();
			for(String mod : (this.panels[8]).requestChangedFields())
			{
				model.addRow(new String[]{this.panels[8].getID(), mod});
			}
			JScrollPane scroll = new JScrollPane(table);
			
			dialogPanel.add(new JLabel("Confirm Modified Properties?"), BorderLayout.PAGE_START);
			dialogPanel.add(scroll, BorderLayout.CENTER);
			dialogPanel.add(new JLabel("This modified property requires the server to restart."), BorderLayout.PAGE_END);
			
			String[] options = {"Confirm and Restart", "Confirm and Restart Later", "Cancel"};
			
			int returnValue = JOptionPane.showOptionDialog(this, dialogPanel, 
					"Modified Preferences", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
			
			if(returnValue == 0)
			{
				try
				{
					this.panels[8].save();
				}
				catch(RuntimeException e){}
			}
			if(returnValue == 1)
			{
				try
				{
					this.panels[8].save();
				}
				catch(RuntimeException e){}
			}
		}
		else
		{
			boolean showConfirmDialog = false;
			for(PreferencePanel panel : this.panels)
			{
				if(panel.requestChangedFields().length != 0)
				{
					showConfirmDialog = true;
					break;
				}
			}
			
			if(showConfirmDialog)
			{
				JPanel dialogPanel = new JPanel();
				dialogPanel.setLayout(new BorderLayout(5,5));
				
				JTable table = new JTable(new DefaultTableModel(new String[]{"Panel", "Property"}, 0));
				DefaultTableModel model = (DefaultTableModel) table.getModel();
				
				boolean requiresRestart = false;
				for(PreferencePanel panel : this.panels)
				{
					for(String mod : panel.requestChangedFields())
					{
						model.addRow(new String[]{panel.getID(), mod});
						
						if(panel.getID().equals("Connection Settings") || panel.getID().equals("Security Settings") || panel.getID().equals("Logging"))
						{
							requiresRestart = true;
						}
					}
				}
				
				JScrollPane scroll = new JScrollPane(table);
				
				dialogPanel.add(new JLabel("Confirm Modified Properties?"), BorderLayout.PAGE_START);
				dialogPanel.add(scroll, BorderLayout.CENTER);
				
				if(requiresRestart)
				{
					dialogPanel.add(new JLabel("This modified property requires the server to restart."), BorderLayout.PAGE_END);
				}
				
				String[] options = {"Confirm and Restart", "Confirm and Restart Later", "Cancel"};
				int returnValue = JOptionPane.showOptionDialog(this, dialogPanel, 
						"Modified Preferences", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
				
				if(returnValue == 0)
				{
					for(PreferencePanel panel : this.panels)
					{
						try
						{
							panel.save();
						}
						catch(RuntimeException e){}
					}
					
					this.userInterface.restartServer();
				}
				else if(returnValue == 1)
				{
					for(PreferencePanel panel : this.panels)
					{
						try
						{
							panel.save();
						}
						catch(RuntimeException e){}
					}
					
					this.userInterface.validateSettings();
				}
			}
			else
			{
				for(PreferencePanel panel : this.panels)
				{
					try
					{
						panel.save();
					}
					catch(RuntimeException e){}
				}
				
				this.userInterface.validateSettings();
			}
		}
		
		super.dispose();
	}
	
	public void restoreSavedSettings()
	{
		for(PreferencePanel panel : panels)
		{
			panel.populatePanel();
		}
	}
	
	public void restoreGlobalSettings()
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
		
		for(PreferencePanel panel : panels)
		{
			panel.populatePanel();
		}
	}
	
	private JTree buildTree()
	{
		DefaultMutableTreeNode parentNode = new DefaultMutableTreeNode();
		JTree tree = new JTree(parentNode, false);
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		tree.setBorder(BorderFactory.createLineBorder(Color.black));
		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		renderer.setLeafIcon(null);
		renderer.setClosedIcon(null);
		renderer.setOpenIcon(null);
		tree.setCellRenderer(renderer);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addTreeSelectionListener(this);
		
		DefaultMutableTreeNode node = new DefaultMutableTreeNode("General Server Settings", true);
		node.add(new DefaultMutableTreeNode("Welcome Message", false));
		node.add(new DefaultMutableTreeNode("Connection Settings", false));
		node.add(new DefaultMutableTreeNode("IP Filter", false));
		parentNode.add(node);
		
		parentNode.add(new DefaultMutableTreeNode("File Transfer Settings"));
		parentNode.add(new DefaultMutableTreeNode("Security Settings", false));
		parentNode.add(new DefaultMutableTreeNode("Logging", false));
		parentNode.add(new DefaultMutableTreeNode("Style Preferences", false));
		parentNode.add(new DefaultMutableTreeNode("Advanced Settings", false));
		
		tree.expandPath(new TreePath(parentNode.getPath()));
		for(int i = 0; i < tree.getRowCount(); i++)
		{
		    tree.expandRow(i);
		}
		
		return tree;
	}
	
	private JPanel buildButtonPanel()
	{
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new GridLayout(1,5,10,10));
		buttonPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		this.applyButton = new JButton("Apply");
		this.okButton = new JButton("OK");
		this.cancelButton = new JButton("Cancel");
		this.helpButton = new JButton("Help");
		this.applyButton.addActionListener(this);
		this.okButton.addActionListener(this);
		this.cancelButton.addActionListener(this);
		this.helpButton.addActionListener(this);
		
		buttonPane.add(this.applyButton);
		buttonPane.add(this.okButton);
		buttonPane.add(this.cancelButton);
		buttonPane.add(new JLabel());
		buttonPane.add(this.helpButton);
		
		return buttonPane;
	}
	
	private void buildSettingsPanels()
	{
		this.panels = new PreferencePanel[9];
		this.panels[0] = new GeneralSettingsPanel("General Server Settings", this);
		this.panels[1] = new WelcomeMessagePanel("Welcome Message");
		this.panels[2] = new ConnectionSettingsPanel("Connection Settings");
		this.panels[3] = new IPFilterPanel("IP Filter");
		this.panels[4] = new FileTransferSettingsPanel("File Transfer Settings");
		this.panels[5] = new SecuritySettingsPanel("Security Settings");
		this.panels[6] = new LoggingPanel("Logging");
		this.panels[7] = new StylePreferencesPanel("Style Preferences");
		this.panels[8] = new AdvancedSettingsPanel("Advanced Settings");
	}

	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == this.applyButton)
		{
			this.close(true);
		}
		else if(e.getSource() == this.okButton)
		{
			this.close(true);
		}
		else if(e.getSource() == this.cancelButton)
		{
			this.close(false);
		}
		else if(e.getSource() == this.helpButton)
		{
			
		}
	}
	
	public void valueChanged(TreeSelectionEvent e)
	{
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)this.settingTree.getLastSelectedPathComponent();
		
		if(node == null)
		{
			return;
		}
		else
		{
			Object nodeIdentifier = node.getUserObject();
			
			for(PreferencePanel panel : this.panels)
			{
				if(panel.getID().equals(nodeIdentifier))
				{
					this.masterPane.remove(this.currentPanel);
					this.currentPanel = panel;
					this.masterPane.add(this.currentPanel, BorderLayout.CENTER);
					this.revalidate();
		            this.repaint();
					break;
				}
			}
		}
	}
	
	public void windowActivated(WindowEvent arg0){}
	public void windowClosed(WindowEvent arg0){}
	public void windowClosing(WindowEvent arg0){}
	public void windowDeactivated(WindowEvent arg0){}
	public void windowDeiconified(WindowEvent arg0){}
	public void windowIconified(WindowEvent arg0){}
	public void windowOpened(WindowEvent arg0){}
}