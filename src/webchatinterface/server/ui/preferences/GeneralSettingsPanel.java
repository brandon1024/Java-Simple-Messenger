package webchatinterface.server.ui.preferences;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import webchatinterface.server.AbstractServer;

public class GeneralSettingsPanel extends PreferencePanel
{
	private static final long serialVersionUID = 507661870194073394L;

	private JCheckBox startServerWhenOpened;
	
	private JCheckBox startServerMinimized;
	
	private JCheckBox showResourceMonitor;
	
	private JButton restoreDefaultSettings;
	
	private JButton restoreSavedSettings;
	
	public GeneralSettingsPanel(String header, PreferencesDialog dialog)
	{
		super(header);
		this.startServerWhenOpened = new JCheckBox("Start Server Immediately When Application Starts");
		this.startServerMinimized = new JCheckBox("Open Server in Minimized Window");
		this.showResourceMonitor = new JCheckBox("Show Resource Monitor");
		this.restoreDefaultSettings = new JButton("Restore Global Default Settings");
		this.restoreSavedSettings = new JButton("Restore Saved Settings");
		this.restoreDefaultSettings.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				dialog.restoreGlobalSettings();
			}
		});
		this.restoreSavedSettings.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				dialog.restoreSavedSettings();
			}
		});
		this.populatePanel();
		
		JPanel body = new JPanel();
		body.setLayout(new BoxLayout(body, BoxLayout.PAGE_AXIS));
		body.setBorder(BorderFactory.createEmptyBorder());
		body.add(this.buildWindowSettingsPanel());
		body.add(this.buildRestoreSettingsPanel());
		
		super.add(body, BorderLayout.CENTER);
	}
	
	protected JPanel buildWindowSettingsPanel()
	{
		JPanel generalSettingsPanel = new JPanel();
		generalSettingsPanel.setLayout(new BoxLayout(generalSettingsPanel, BoxLayout.PAGE_AXIS));
		generalSettingsPanel.setBorder(BorderFactory.createTitledBorder("Window Settings"));
		
		String info = "Start Server Immediately When Application Starts:\nAllows the server to start immediately "
				+ "once the application is executed. Otherwise, the server must be started manually after each execution.";
		generalSettingsPanel.add(super.createInformationPanel(info));
		
		JPanel innerPanel = new JPanel();
		innerPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		innerPanel.add(this.startServerWhenOpened);
		generalSettingsPanel.add(innerPanel);
		generalSettingsPanel.add(Box.createRigidArea(new Dimension(0,15)));
		
		info = "Open Minimized:\nThe server will open in a minimized window once the application is executed. "
				+ "Otherwise, the application window will open normally.";
		generalSettingsPanel.add(super.createInformationPanel(info));
		
		innerPanel = new JPanel();
		innerPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		innerPanel.add(this.startServerMinimized);
		generalSettingsPanel.add(innerPanel);
		generalSettingsPanel.add(Box.createRigidArea(new Dimension(0,15)));
		
		info = "Show Resource Monitor:\nThe resource monitor is a panel located at the bottom of the application "
				+ "window that displays critical information with regards to the Java Virtual Machine (JVM), server activity "
				+ "and server usage. The resource monitor allows the server host to monitor the state of the server, and take action "
				+ "in the event the server experiences abnormal memory allocation, significant message or file transfer bandwidth,"
				+ "or large number of connected clients.";
		generalSettingsPanel.add(super.createInformationPanel(info));
		
		innerPanel = new JPanel();
		innerPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		innerPanel.add(this.showResourceMonitor);
		generalSettingsPanel.add(innerPanel);
		
		return generalSettingsPanel;
	}
	
	protected JPanel buildRestoreSettingsPanel()
	{
		JPanel restoreSettingsPanel = new JPanel();
		restoreSettingsPanel.setLayout(new BoxLayout(restoreSettingsPanel, BoxLayout.PAGE_AXIS));
		restoreSettingsPanel.setBorder(BorderFactory.createTitledBorder("Restore Settings"));
		
		JPanel innerPanel = new JPanel();
		innerPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		innerPanel.add(this.restoreDefaultSettings);
		innerPanel.add(this.restoreSavedSettings);
		restoreSettingsPanel.add(innerPanel);
		
		return restoreSettingsPanel;
	}
	
	public String[] requestChangedFields()
	{
		ArrayList<String> changedFields = new ArrayList<String>();
		
		if(this.startServerWhenOpened.isSelected() != AbstractServer.startServerWhenApplicationStarts)
		{
			changedFields.add("Start Server When Application Starts");
		}
		
		if(this.startServerMinimized.isSelected() != AbstractServer.openMinimized)
		{
			changedFields.add("Start Application Minimized");
		}
		
		if(this.showResourceMonitor.isSelected() != AbstractServer.showResourceMonitor)
		{
			changedFields.add("Show Resource Monitor");
		}
		
		return changedFields.toArray(new String[0]);
	}
	
	public void save()
	{
		AbstractServer.startServerWhenApplicationStarts = this.startServerWhenOpened.isSelected();
		AbstractServer.openMinimized = this.startServerMinimized.isSelected();
		AbstractServer.showResourceMonitor = this.showResourceMonitor.isSelected();
	}

	protected void populatePanel()
	{
		this.startServerWhenOpened.setSelected(AbstractServer.startServerWhenApplicationStarts);
		this.startServerMinimized.setSelected(AbstractServer.openMinimized);
		this.showResourceMonitor.setSelected(AbstractServer.showResourceMonitor);
	}
}
