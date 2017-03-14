package webchatinterface.server.ui.components.preferences;

import webchatinterface.server.AbstractServer;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ConnectionSettingsPanel extends PreferencePanel
{
	private JTextField portNumberField;
	private JTextField bindIPField;
	private JTextField maxConnectionsField;
	private JTextField loginTimeoutField;
	
	public ConnectionSettingsPanel(String header)
	{
		super(header);
		this.portNumberField = new JTextField(15);
		this.bindIPField = new JTextField(15);
		this.maxConnectionsField = new JTextField(15);
		this.loginTimeoutField = new JTextField(15);
		this.populatePanel();
		
		JPanel body = new JPanel();
		body.setLayout(new BoxLayout(body, BoxLayout.PAGE_AXIS));
		body.setBorder(BorderFactory.createEmptyBorder());
		body.add(this.buildConnectionSettingsPanel());
		body.add(this.buildTimeoutSettingsPanel());
		
		super.add(body, BorderLayout.CENTER);
		super.add(Box.createRigidArea(new Dimension(0,45)), BorderLayout.PAGE_END);
	}
	
	private JPanel buildConnectionSettingsPanel()
	{
		JPanel connectionSettings = new JPanel();
		connectionSettings.setLayout(new BoxLayout(connectionSettings, BoxLayout.PAGE_AXIS));
		connectionSettings.setBorder(BorderFactory.createTitledBorder("Connection Settings"));
		
		JPanel bindIP = new JPanel();
		bindIP.setLayout(new BoxLayout(bindIP, BoxLayout.PAGE_AXIS));
		String info = "Specify Server Bind IP:\nBinding the server to an IP will allow the server to accept " + 
				"connections from a single local address. Leaving the server unbound will " + 
				"default accepting connections on any or all local addresses.";
		bindIP.add(super.createInformationPanel(info));
		JPanel innerPanel = new JPanel();
		innerPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		innerPanel.add(new JLabel("Bind Server to IP: "));
		innerPanel.add(this.bindIPField);
		innerPanel.add(new JLabel("(Default address: default)"));
		bindIP.add(innerPanel);
		connectionSettings.add(bindIP);
		connectionSettings.add(Box.createRigidArea(new Dimension(0,15)));
		
		JPanel portNumber = new JPanel();
		portNumber.setLayout(new BoxLayout(portNumber, BoxLayout.PAGE_AXIS));
		info = "Specify Server Port Number:\nThe port must be between 0 and 65535, inclusive. " + 
				"A port number of 0 means that the port number is automatically allocated, typically from an " + 
				"ephemeral port range.";
		portNumber.add(super.createInformationPanel(info));
		innerPanel = new JPanel();
		innerPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		innerPanel.add(new JLabel("Listen on Port: "));
		innerPanel.add(this.portNumberField);
		innerPanel.add(new JLabel("(Default: 5100, 0-65535)"));
		portNumber.add(innerPanel);
		connectionSettings.add(portNumber);
		connectionSettings.add(Box.createRigidArea(new Dimension(0,15)));
		
		JPanel maxConnections = new JPanel();
		maxConnections.setLayout(new BoxLayout(maxConnections, BoxLayout.PAGE_AXIS));
		info = "Specify Maximum Number of Client Connections:\nPrevent users from connecting " + 
				"to the server when the server is at maximum capacity.";
		maxConnections.add(super.createInformationPanel(info));
		innerPanel = new JPanel();
		innerPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		innerPanel.add(new JLabel("Maximum Number of Connected Users: "));
		innerPanel.add(this.maxConnectionsField);
		innerPanel.add(new JLabel("(Default: 200, 0 for infinity)"));
		maxConnections.add(innerPanel);
		connectionSettings.add(maxConnections);
		
		return connectionSettings;
	}
	
	private JPanel buildTimeoutSettingsPanel()
	{
		JPanel timeoutSettings = new JPanel();
		timeoutSettings.setLayout(new BoxLayout(timeoutSettings, BoxLayout.PAGE_AXIS));
		timeoutSettings.setBorder(BorderFactory.createTitledBorder("Timeout Settings"));
		String info = "Specify Login Timeout:\nAbort user authentication after specified number of seconds without a response.";
		timeoutSettings.add(super.createInformationPanel(info));
		JPanel innerPanel = new JPanel();
		innerPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		innerPanel.add(new JLabel("Login Timeout: "));
		innerPanel.add(this.loginTimeoutField);
		innerPanel.add(new JLabel("seconds (Default: 60, 0 for infinity)"));
		timeoutSettings.add(innerPanel);
		
		return timeoutSettings;
	}
	
	public String[] requestChangedFields()
	{
		ArrayList<String> changedFields = new ArrayList<String>();
		
		if(Integer.parseInt(this.portNumberField.getText()) != AbstractServer.serverPortNumber)
			changedFields.add("Port Number");
		
		if(!this.bindIPField.getText().equals(AbstractServer.serverBindIPAddress))
			changedFields.add("Bind IP Address");
		
		if(Integer.parseInt(this.maxConnectionsField.getText()) != AbstractServer.maxConnectedUsers)
			changedFields.add("Maximum Number of Connected Users");
		
		if(Integer.parseInt(this.loginTimeoutField.getText()) != AbstractServer.loginTimeoutSeconds)
			changedFields.add("Login Timeout Seconds");
		
		return changedFields.toArray(new String[0]);
	}
	
	public void save()
	{
		AbstractServer.serverPortNumber = Integer.parseInt(this.portNumberField.getText());
		AbstractServer.serverBindIPAddress = this.bindIPField.getText();
		AbstractServer.maxConnectedUsers = Integer.parseInt(this.maxConnectionsField.getText());
		AbstractServer.loginTimeoutSeconds = Integer.parseInt(this.loginTimeoutField.getText());
	}

	public void populatePanel()
	{
		this.portNumberField.setText(Integer.toString(AbstractServer.serverPortNumber));
		this.bindIPField.setText(AbstractServer.serverBindIPAddress);
		this.maxConnectionsField.setText(Integer.toString(AbstractServer.maxConnectedUsers));
		this.loginTimeoutField.setText(Integer.toString(AbstractServer.loginTimeoutSeconds));
	}
}
