package webchatinterface.server.ui.components.preferences;

import webchatinterface.AbstractIRC;
import webchatinterface.server.AbstractServer;
import webchatinterface.server.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.HashMap;

public class LoggingPanel extends PreferencePanel
{
	private JCheckBox enableLoggingCheckBox;
	private JRadioButton logOnlyWarningsExceptionsRadioButton;
	private JRadioButton logOnlyServerActivityRadioButton;
	private JRadioButton logAllRadioButton;
	private JCheckBox logAllToSingleFileCheckBox;
	private JCheckBox logFileFormatCheckBox;
	private JCheckBox limitFileSizeCheckBox;
	private JCheckBox deleteOldLogFilesCheckBox;
	private JCheckBox showTimestampsCheckBox;
	private JTextField limitFileSizeField;
	private JTextField deleteOldLogFilesField;
	private JTextField logFileFormatField;
	private JButton showLogFolderButton;
	private JButton deleteAllLogFilesButton;
	
	public LoggingPanel(String header)
	{
		super(header);
		this.enableLoggingCheckBox = new JCheckBox("Enable Logging to File");
		this.logOnlyWarningsExceptionsRadioButton = new JRadioButton("Log Only Warnings and Exceptions");
		this.logOnlyServerActivityRadioButton = new JRadioButton("Log Only Server Activity");
		this.logAllRadioButton = new JRadioButton("Log All Activity");
		this.logAllToSingleFileCheckBox = new JCheckBox("Log All to Single File \'WebChatServer.LOG\'");
		this.logFileFormatCheckBox = new JCheckBox("Use File Format: ");
		this.limitFileSizeCheckBox = new JCheckBox("Limit Log File Size");
		this.deleteOldLogFilesCheckBox = new JCheckBox("Delete Old Log Files");
		this.showTimestampsCheckBox = new JCheckBox("Show Timestamps In Log Files");
		this.limitFileSizeField = new JTextField(15);
		this.deleteOldLogFilesField = new JTextField(15);
		this.logFileFormatField = new JTextField(15);
		this.showLogFolderButton = new JButton("Open Logs in Windows Explorer");
		this.deleteAllLogFilesButton = new JButton("Delete All Logs");
		
		this.showLogFolderButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				try
				{
					if(Desktop.isDesktopSupported())
						Desktop.getDesktop().open(new File(AbstractIRC.SERVER_APPLCATION_DIRECTORY + "LOGS"));
					else
					{
						JTextArea info = (JTextArea)LoggingPanel.this.createInformationPanel("This feature is not supported on this platform.\n"
								+ "Use Windows Explorer to open " + AbstractIRC.SERVER_APPLCATION_DIRECTORY + "LOGS");
						info.setFocusable(true);
						info.setRows(3);
						info.setColumns(50);
						JOptionPane.showMessageDialog(LoggingPanel.this, info);
					}
				}
				catch(Exception e){}
			}
		});
		
		this.deleteAllLogFilesButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				AbstractServer.clearLogs();
			}
		});
		
		ButtonGroup group = new ButtonGroup();
		group.add(this.logOnlyWarningsExceptionsRadioButton);
		group.add(this.logOnlyServerActivityRadioButton);
		group.add(this.logAllRadioButton);
		
		ActionListener action = new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				if(((JCheckBox)event.getSource()).isSelected())
				{
					LoggingPanel.this.logOnlyWarningsExceptionsRadioButton.setEnabled(true);
					LoggingPanel.this.logOnlyServerActivityRadioButton.setEnabled(true);
					LoggingPanel.this.logAllRadioButton.setEnabled(true);
					LoggingPanel.this.logAllToSingleFileCheckBox.setEnabled(true);
					LoggingPanel.this.logFileFormatCheckBox.setEnabled(true);
					LoggingPanel.this.limitFileSizeCheckBox.setEnabled(true);
					LoggingPanel.this.deleteOldLogFilesCheckBox.setEnabled(true);
					LoggingPanel.this.showTimestampsCheckBox.setEnabled(true);
					
					LoggingPanel.this.limitFileSizeField.setEnabled(true);
					LoggingPanel.this.limitFileSizeField.setEditable(true);
					
					LoggingPanel.this.deleteOldLogFilesField.setEnabled(true);
					LoggingPanel.this.deleteOldLogFilesField.setEditable(true);
					
					LoggingPanel.this.logFileFormatField.setEnabled(true);
					LoggingPanel.this.logFileFormatField.setEditable(true);
				}
				else
				{
					LoggingPanel.this.logOnlyWarningsExceptionsRadioButton.setEnabled(false);
					LoggingPanel.this.logOnlyServerActivityRadioButton.setEnabled(false);
					LoggingPanel.this.logAllRadioButton.setEnabled(false);
					LoggingPanel.this.logAllToSingleFileCheckBox.setEnabled(false);
					LoggingPanel.this.logFileFormatCheckBox.setEnabled(false);
					LoggingPanel.this.limitFileSizeCheckBox.setEnabled(false);
					LoggingPanel.this.deleteOldLogFilesCheckBox.setEnabled(false);
					LoggingPanel.this.showTimestampsCheckBox.setEnabled(false);
					
					LoggingPanel.this.limitFileSizeField.setEnabled(false);
					LoggingPanel.this.limitFileSizeField.setEditable(false);
					
					LoggingPanel.this.deleteOldLogFilesField.setEnabled(false);
					LoggingPanel.this.deleteOldLogFilesField.setEditable(false);
					
					LoggingPanel.this.logFileFormatField.setEnabled(false);
					LoggingPanel.this.logFileFormatField.setEditable(false);
				}
			}
		};
		
		ItemListener item = new ItemListener()
		{
			public void itemStateChanged(ItemEvent event)
			{
				if(event.getStateChange() == ItemEvent.SELECTED)
				{
					LoggingPanel.this.logOnlyWarningsExceptionsRadioButton.setEnabled(true);
					LoggingPanel.this.logOnlyServerActivityRadioButton.setEnabled(true);
					LoggingPanel.this.logAllRadioButton.setEnabled(true);
					LoggingPanel.this.logAllToSingleFileCheckBox.setEnabled(true);
					LoggingPanel.this.logFileFormatCheckBox.setEnabled(true);
					LoggingPanel.this.limitFileSizeCheckBox.setEnabled(true);
					LoggingPanel.this.deleteOldLogFilesCheckBox.setEnabled(true);
					LoggingPanel.this.showTimestampsCheckBox.setEnabled(true);
					
					LoggingPanel.this.limitFileSizeField.setEnabled(true);
					LoggingPanel.this.limitFileSizeField.setEditable(true);
					
					LoggingPanel.this.deleteOldLogFilesField.setEnabled(true);
					LoggingPanel.this.deleteOldLogFilesField.setEditable(true);
					
					LoggingPanel.this.logFileFormatField.setEnabled(true);
					LoggingPanel.this.logFileFormatField.setEditable(true);
				}
				else
				{
					LoggingPanel.this.logOnlyWarningsExceptionsRadioButton.setEnabled(false);
					LoggingPanel.this.logOnlyServerActivityRadioButton.setEnabled(false);
					LoggingPanel.this.logAllRadioButton.setEnabled(false);
					LoggingPanel.this.logAllToSingleFileCheckBox.setEnabled(false);
					LoggingPanel.this.logFileFormatCheckBox.setEnabled(false);
					LoggingPanel.this.limitFileSizeCheckBox.setEnabled(false);
					LoggingPanel.this.deleteOldLogFilesCheckBox.setEnabled(false);
					LoggingPanel.this.showTimestampsCheckBox.setEnabled(false);
					
					LoggingPanel.this.limitFileSizeField.setEnabled(false);
					LoggingPanel.this.limitFileSizeField.setEditable(false);
					
					LoggingPanel.this.deleteOldLogFilesField.setEnabled(false);
					LoggingPanel.this.deleteOldLogFilesField.setEditable(false);
					
					LoggingPanel.this.logFileFormatField.setEnabled(false);
					LoggingPanel.this.logFileFormatField.setEditable(false);
				}
			}
		};
		
		this.enableLoggingCheckBox.addActionListener(action);
		this.enableLoggingCheckBox.addItemListener(item);
		
		action = new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				if(((JCheckBox)event.getSource()).isSelected())
				{
					LoggingPanel.this.logFileFormatField.setEnabled(true);
					LoggingPanel.this.logFileFormatField.setEditable(true);
				}
				else
				{
					LoggingPanel.this.logFileFormatField.setEnabled(false);
					LoggingPanel.this.logFileFormatField.setEditable(false);
				}
			}
		};
		
		item = new ItemListener()
		{
			public void itemStateChanged(ItemEvent event)
			{
				if(((JCheckBox)event.getSource()).isSelected())
				{
					LoggingPanel.this.logFileFormatField.setEnabled(true);
					LoggingPanel.this.logFileFormatField.setEditable(true);
				}
				else
				{
					LoggingPanel.this.logFileFormatField.setEnabled(false);
					LoggingPanel.this.logFileFormatField.setEditable(false);
				}
			}
		};
		this.logFileFormatCheckBox.addActionListener(action);
		this.logFileFormatCheckBox.addItemListener(item);
		
		action = new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				if(((JCheckBox)event.getSource()).isSelected())
				{
					LoggingPanel.this.limitFileSizeField.setEnabled(true);
					LoggingPanel.this.limitFileSizeField.setEditable(true);
				}
				else
				{
					LoggingPanel.this.limitFileSizeField.setEnabled(false);
					LoggingPanel.this.limitFileSizeField.setEditable(false);
				}
			}
		};
		
		item = new ItemListener()
		{
			public void itemStateChanged(ItemEvent event)
			{
				if(((JCheckBox)event.getSource()).isSelected())
				{
					LoggingPanel.this.limitFileSizeField.setEnabled(true);
					LoggingPanel.this.limitFileSizeField.setEditable(true);
				}
				else
				{
					LoggingPanel.this.limitFileSizeField.setEnabled(false);
					LoggingPanel.this.limitFileSizeField.setEditable(false);
				}
			}
		};
		this.limitFileSizeCheckBox.addActionListener(action);
		this.limitFileSizeCheckBox.addItemListener(item);
		
		action = new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				if(((JCheckBox)event.getSource()).isSelected())
				{
					LoggingPanel.this.deleteOldLogFilesField.setEnabled(true);
					LoggingPanel.this.deleteOldLogFilesField.setEditable(true);
				}
				else
				{
					LoggingPanel.this.deleteOldLogFilesField.setEnabled(false);
					LoggingPanel.this.deleteOldLogFilesField.setEditable(false);
				}
			}
		};
		
		item = new ItemListener()
		{
			public void itemStateChanged(ItemEvent event)
			{
				if(((JCheckBox)event.getSource()).isSelected())
				{
					LoggingPanel.this.deleteOldLogFilesField.setEnabled(true);
					LoggingPanel.this.deleteOldLogFilesField.setEditable(true);
				}
				else
				{
					LoggingPanel.this.deleteOldLogFilesField.setEnabled(false);
					LoggingPanel.this.deleteOldLogFilesField.setEditable(false);
				}
			}
		};

		this.deleteOldLogFilesCheckBox.addActionListener(action);
		this.deleteOldLogFilesCheckBox.addItemListener(item);
		
		JPanel body = new JPanel();
		body.setLayout(new BoxLayout(body, BoxLayout.PAGE_AXIS));
		body.setBorder(BorderFactory.createEmptyBorder());
		body.add(this.buildLogSettingsPanel());
		body.add(this.buildFileManagementPanel());
		
		super.add(body, BorderLayout.CENTER);
		super.add(Box.createRigidArea(new Dimension(0,15)), BorderLayout.PAGE_END);
	}

	private JPanel buildLogSettingsPanel()
	{
		JPanel logSettings = new JPanel();
		logSettings.setLayout(new BoxLayout(logSettings, BoxLayout.PAGE_AXIS));
		logSettings.setBorder(BorderFactory.createTitledBorder("Logging"));
		
		String info = "Change Server Logging Settings:\nEnabling server logging will allow you to examine the server "
				+ "activity for suspicious client activity or troubleshooting issues with the server.";
		logSettings.add(super.createInformationPanel(info));
		
		JPanel innerPanel = new JPanel();
		innerPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		innerPanel.add(this.enableLoggingCheckBox);
		logSettings.add(innerPanel);
		
		innerPanel = new JPanel();
		innerPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		innerPanel.add(Box.createRigidArea(new Dimension(20,0)));
		innerPanel.add(this.logOnlyWarningsExceptionsRadioButton);
		innerPanel.add(this.logOnlyServerActivityRadioButton);
		innerPanel.add(this.logAllRadioButton);
		logSettings.add(innerPanel);
		
		innerPanel = new JPanel();
		innerPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		innerPanel.add(Box.createRigidArea(new Dimension(20,0)));
		innerPanel.add(this.logAllToSingleFileCheckBox);
		logSettings.add(innerPanel);
		
		innerPanel = new JPanel();
		innerPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		innerPanel.add(Box.createRigidArea(new Dimension(20,0)));
		innerPanel.add(this.logFileFormatCheckBox);
		innerPanel.add(this.logFileFormatField);
		logSettings.add(innerPanel);
		
		innerPanel = new JPanel();
		innerPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		innerPanel.add(Box.createRigidArea(new Dimension(20,0)));
		innerPanel.add(this.limitFileSizeCheckBox);
		innerPanel.add(new JLabel("Limit: "));
		innerPanel.add(this.limitFileSizeField);
		innerPanel.add(new JLabel("KB"));
		logSettings.add(innerPanel);
		
		innerPanel = new JPanel();
		innerPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		innerPanel.add(Box.createRigidArea(new Dimension(20,0)));
		innerPanel.add(this.deleteOldLogFilesCheckBox);
		innerPanel.add(new JLabel("Delete After "));
		innerPanel.add(this.deleteOldLogFilesField);
		innerPanel.add(new JLabel("Sessions"));
		logSettings.add(innerPanel);
		
		innerPanel = new JPanel();
		innerPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		innerPanel.add(Box.createRigidArea(new Dimension(20,0)));
		innerPanel.add(this.showTimestampsCheckBox);
		logSettings.add(innerPanel);
		
		innerPanel = new JPanel();
		innerPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		innerPanel.add(new JLabel(AbstractIRC.SERVER_APPLCATION_DIRECTORY + "LOGS"));
		logSettings.add(innerPanel);
		
		return logSettings;
	}
	
	private JPanel buildFileManagementPanel()
	{
		JPanel manageLogFilesPanel = new JPanel();
		manageLogFilesPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		manageLogFilesPanel.setBorder(BorderFactory.createTitledBorder("Manage Log Files"));
		
		manageLogFilesPanel.add(this.showLogFolderButton);
		manageLogFilesPanel.add(this.deleteAllLogFilesButton);
		
		return manageLogFilesPanel;
	}

	public HashMap<String, Pair<Object, Boolean>> getModifiedPreferences()
	{
		//TODO:
		return null;
	}

	public void setPreferences(HashMap<String, Pair<Object, Boolean>> preferences)
	{
		//TODO:
	}
}
