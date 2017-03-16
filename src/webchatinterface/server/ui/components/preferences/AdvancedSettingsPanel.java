package webchatinterface.server.ui.components.preferences;

import webchatinterface.AbstractIRC;
import webchatinterface.server.util.Pair;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

public class AdvancedSettingsPanel extends PreferencePanel
{
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
					//Reference to Editor
					JTextArea editor = AdvancedSettingsPanel.this.configurationEditor;

					int caretPos = editor.getCaretPosition();
					int lineIndex = editor.getLineOfOffset(caretPos);
					int lineStartIndex = editor.getLineStartOffset(lineIndex);
					int lineEndIndex = editor.getLineEndOffset(lineIndex) - 1;

					if(lineIndex == editor.getLineCount()-1)
						lineEndIndex++;

					String line = editor.getText().substring(lineStartIndex, lineEndIndex);
					int index = line.indexOf('=') + 1;
					
					if(index == -1)
						return;
					
					int propertyStartIndex = lineStartIndex + index;

					int currentSelectionStart = editor.getSelectionStart();
					int currentSelectionEnd = editor.getSelectionStart();
					if(currentSelectionStart >= propertyStartIndex && currentSelectionEnd <= lineEndIndex)
						return;

					editor.select(propertyStartIndex, lineEndIndex);
				}
				catch (BadLocationException e){}
			}

			public void mouseEntered(MouseEvent arg0){}
			public void mouseExited(MouseEvent arg0){}
			public void mousePressed(MouseEvent arg0){}
			public void mouseReleased(MouseEvent arg0){}
		});
		
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
