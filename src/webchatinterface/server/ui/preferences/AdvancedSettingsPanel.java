package webchatinterface.server.ui.preferences;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;

import webchatinterface.AbstractIRC;
import webchatinterface.server.AbstractServer;

public class AdvancedSettingsPanel extends PreferencePanel
{
	private static final long serialVersionUID = 1000489259244129189L;
	
	private JTextArea configurationEditor;
	
	private JCheckBox editedCheckBox;
	
	public AdvancedSettingsPanel(String header)
	{
		super(header);
		this.configurationEditor = new JTextArea();
		this.configurationEditor.setEditable(false);
		this.configurationEditor.setEnabled(false);
		this.editedCheckBox = new JCheckBox("Edit Configuration File");
		this.editedCheckBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				if(((JCheckBox)event.getSource()).isSelected())
				{
					AdvancedSettingsPanel.this.configurationEditor.setEditable(true);
					AdvancedSettingsPanel.this.configurationEditor.setEnabled(true);
				}
				else
				{
					AdvancedSettingsPanel.this.configurationEditor.setEditable(false);
					AdvancedSettingsPanel.this.configurationEditor.setEnabled(false);
				}
			}
		});
		
		this.editedCheckBox.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent event)
			{
				if(((JCheckBox)event.getSource()).isSelected())
				{
					AdvancedSettingsPanel.this.configurationEditor.setEditable(true);
					AdvancedSettingsPanel.this.configurationEditor.setEnabled(true);
				}
				else
				{
					AdvancedSettingsPanel.this.configurationEditor.setEditable(false);
					AdvancedSettingsPanel.this.configurationEditor.setEnabled(false);
				}
			}
		});
		
		this.configurationEditor.addMouseListener(new MouseListener()
		{
			public void mouseClicked(MouseEvent event)
			{
				try
				{
					String text = AdvancedSettingsPanel.this.configurationEditor.getText();
					int caretPos = AdvancedSettingsPanel.this.configurationEditor.getCaretPosition();
					int linePos = AdvancedSettingsPanel.this.configurationEditor.getLineOfOffset(caretPos);
					int startPos = AdvancedSettingsPanel.this.configurationEditor.getLineStartOffset(linePos);
					int endPos = AdvancedSettingsPanel.this.configurationEditor.getLineEndOffset(linePos) - 1;
					
					String line = text.substring(startPos, endPos);
					int index = line.indexOf('=') + 1;
					
					if(index == -1)
						return;
					
					startPos += index;
					
					int currentSelectionStart = AdvancedSettingsPanel.this.configurationEditor.getSelectionStart();
					int currentSelectionEnd = AdvancedSettingsPanel.this.configurationEditor.getSelectionStart();
					if(currentSelectionStart >= startPos && currentSelectionEnd <= endPos)
						return;
					
					AdvancedSettingsPanel.this.configurationEditor.select(startPos, endPos);
					
				}
				catch (BadLocationException e){}
			}

			public void mouseEntered(MouseEvent arg0){}
			public void mouseExited(MouseEvent arg0){}
			public void mousePressed(MouseEvent arg0){}
			public void mouseReleased(MouseEvent arg0){}
		});
		
		this.populatePanel();
		
		JPanel body = new JPanel();
		body.setLayout(new BoxLayout(body, BoxLayout.PAGE_AXIS));
		body.setBorder(BorderFactory.createEmptyBorder());
		body.add(this.buildAdvancedSettingsPanel());
		
		super.add(body, BorderLayout.CENTER);
	}
	
	private JPanel buildAdvancedSettingsPanel()
	{
		JPanel advancedSettingsPanel = new JPanel();
		advancedSettingsPanel.setLayout(new BoxLayout(advancedSettingsPanel, BoxLayout.PAGE_AXIS));
		advancedSettingsPanel.setBorder(BorderFactory.createTitledBorder("Advanced Settings"));
		
		String info = "Edit the server configuration file:\nThe server settings can also be changed by editing the "
				+ "server configuration file. The ini file, located in the server application directory, represents "
				+ "all saved server preferences.\n\n"
				+ "Once the file has been modified in any way, changes to settings in other settings panes will be "
				+ "ignored and overridden by the new configuration file. Once applied, the server must be restarted "
				+ "before the changes come into effect.";
		
		advancedSettingsPanel.add(super.createInformationPanel(info));
		
		JPanel innerPanel = new JPanel();
		innerPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		innerPanel.add(this.editedCheckBox);
		advancedSettingsPanel.add(innerPanel);
		
		innerPanel = new JPanel();
		innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.PAGE_AXIS));
		innerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		JScrollPane scroll = new JScrollPane(this.configurationEditor);
		scroll.setPreferredSize(new Dimension(0, 300));
		innerPanel.add(scroll);
		advancedSettingsPanel.add(innerPanel);
		
		innerPanel = new JPanel();
		innerPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		innerPanel.add(new JLabel(AbstractIRC.SERVER_APPLCATION_DIRECTORY + "config.ini"));
		advancedSettingsPanel.add(innerPanel);
		
		return advancedSettingsPanel;
	}
	
	public String[] requestChangedFields()
	{
		if(this.editedCheckBox.isSelected())
			return new String[]{"Modified Configuration File"};
		else
			return new String[0];
	}
	
	public void save()
	{
		if(!this.editedCheckBox.isSelected())
			return;
		
		try
		{
			Scanner fileScanner = new Scanner(this.configurationEditor.getText());
			Scanner contentScanner = fileScanner.useDelimiter("\\Z");
			String text = contentScanner.next();
			contentScanner.close();
			fileScanner.close();
			
			String[] lines = text.split("\r\n|\r|\n");
			
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

	protected void populatePanel()
	{
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
			this.configurationEditor.append(line);
	}

	public boolean isEdited()
	{
		return this.editedCheckBox.isSelected();
	}
}
