package webchatinterface.server.ui.dialog;

import webchatinterface.server.ui.components.preferences.*;
import webchatinterface.server.util.Pair;
import webchatinterface.server.util.ResourceLoader;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

public class PreferencesDialog extends JDialog implements TreeSelectionListener
{
	private HashMap<String, Pair<Object, Boolean>> preferences;
	private PreferencePanel[] panels;
	private PreferencePanel currentPanel;
	private JTree settingTree;
	private int exitCode;

	public PreferencesDialog(JFrame parent)
	{
		super(parent, "Server Preferences", ModalityType.DOCUMENT_MODAL);
		super.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		super.setSize(700, 500);
		super.setResizable(false);
		super.setLocationRelativeTo(parent);
		super.setIconImage(ResourceLoader.getInstance().getFrameIcon());

		this.buildSettingsPanels();
		this.buildSettingsTree();
		this.currentPanel = this.panels[0];
		this.init();
		this.preferences = new HashMap<String, Pair<Object, Boolean>>(32);
		this.exitCode = 0;
	}

	private void init()
	{
		JButton okButton = new JButton("OK");
		JButton applyButton = new JButton("Apply");
		JButton cancelButton = new JButton("Cancel");
		JButton helpButton = new JButton("Help");

		okButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				PreferencesDialog.this.saveSettings();
			}
		});

		applyButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				PreferencesDialog.this.exitCode = 1;
				PreferencesDialog.this.saveSettings();
				PreferencesDialog.this.dispose();
			}
		});

		cancelButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				PreferencesDialog.this.exitCode = 2;
				PreferencesDialog.this.dispose();
			}
		});

		helpButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				//Open GitHub, Do Not Dispose
			}
		});

		JPanel buttonPanel = new JPanel(new GridLayout(1,5,10,10));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		buttonPanel.add(okButton);
		buttonPanel.add(applyButton);
		buttonPanel.add(cancelButton);
		buttonPanel.add(new JLabel());
		buttonPanel.add(helpButton);

		JPanel contentPane = (JPanel)this.getContentPane();
		contentPane.setLayout(new BorderLayout(5,5));
		contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		contentPane.add(this.settingTree, BorderLayout.LINE_START);
		contentPane.add(this.currentPanel);
		contentPane.add(buttonPanel, BorderLayout.PAGE_END);
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

	private void buildSettingsTree()
	{
		DefaultMutableTreeNode parentNode = new DefaultMutableTreeNode();
		this.settingTree = new JTree(parentNode, false);
		settingTree.setRootVisible(false);
		settingTree.setShowsRootHandles(true);
		settingTree.setBorder(BorderFactory.createLineBorder(Color.black));
		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		renderer.setLeafIcon(null);
		renderer.setClosedIcon(null);
		renderer.setOpenIcon(null);
		settingTree.setCellRenderer(renderer);
		settingTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		settingTree.addTreeSelectionListener(this);

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

		settingTree.expandPath(new TreePath(parentNode.getPath()));
		for(int i = 0; i < settingTree.getRowCount(); i++)
			settingTree.expandRow(i);
	}

	private void saveSettings()
	{
		for(PreferencePanel panel : PreferencesDialog.this.panels)
		{
			HashMap<String, Pair<Object, Boolean>> preferences = panel.getModifiedPreferences();
			for(String key : preferences.keySet())
				PreferencesDialog.this.preferences.put(key, preferences.get(key));
		}
	}

	public int showDialog()
	{
		this.setVisible(true);
		return exitCode;
	}

	public HashMap<String, Pair<Object, Boolean>> getModifiedPreferences()
	{
		return this.preferences;
	}

	public void setPreferences(HashMap<String, Pair<Object, Boolean>> preferences)
	{
		//TODO:
		//Send appropriate preferences to corresponding panels
	}

	public void valueChanged(TreeSelectionEvent e)
	{
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)this.settingTree.getLastSelectedPathComponent();
		Object nodeIdentifier = node.getUserObject();

		for(PreferencePanel panel : this.panels)
		{
			if(panel.getID().equals(nodeIdentifier))
			{
				this.getContentPane().remove(this.currentPanel);
				this.currentPanel = panel;
				this.getContentPane().add(this.currentPanel, BorderLayout.CENTER);
				this.revalidate();
				this.repaint();
				break;
			}
		}
	}
}