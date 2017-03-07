package webchatinterface.server.ui.components.preferences;

import webchatinterface.server.AbstractServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

public class WelcomeMessagePanel extends PreferencePanel
{
	private static final long serialVersionUID = -2242780715491172094L;
	
	private JTextArea newMemberMessageEditor;
	
	private JTextArea returningMemberMessageEditor;
	
	private JCheckBox alwaysSendWelcomeMessageCheckBox;
	
	public WelcomeMessagePanel(String header)
	{
		super(header);
		this.newMemberMessageEditor = new JTextArea();
		this.returningMemberMessageEditor = new JTextArea();
		this.alwaysSendWelcomeMessageCheckBox = new JCheckBox("Always Send Welcome Message");	
		ActionListener action = new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				if(((JCheckBox)event.getSource()).isSelected())
				{
					WelcomeMessagePanel.this.returningMemberMessageEditor.setEnabled(false);
					WelcomeMessagePanel.this.returningMemberMessageEditor.setEditable(false);
				}
				else
				{
					WelcomeMessagePanel.this.returningMemberMessageEditor.setEnabled(true);
					WelcomeMessagePanel.this.returningMemberMessageEditor.setEditable(true);
				}
			}
		};
		
		ItemListener item = new ItemListener()
		{
			public void itemStateChanged(ItemEvent event)
			{
				if(event.getStateChange() == ItemEvent.SELECTED)
				{
					WelcomeMessagePanel.this.returningMemberMessageEditor.setEnabled(false);
					WelcomeMessagePanel.this.returningMemberMessageEditor.setEditable(false);
			    }
				else
				{
					WelcomeMessagePanel.this.returningMemberMessageEditor.setEnabled(true);
					WelcomeMessagePanel.this.returningMemberMessageEditor.setEditable(true);
			    }
			}
		};
		
		this.alwaysSendWelcomeMessageCheckBox.addActionListener(action);
		this.alwaysSendWelcomeMessageCheckBox.addItemListener(item);
		
		this.populatePanel();
		
		JPanel body = new JPanel();
		body.setLayout(new BoxLayout(body, BoxLayout.PAGE_AXIS));
		body.setBorder(BorderFactory.createTitledBorder("Modify Welcome Message"));
		body.add(this.buildWelcomeMessageEditorPanel());
		
		super.add(body, BorderLayout.CENTER);
	}
	
	private JPanel buildWelcomeMessageEditorPanel()
	{
		JPanel welcomeMessageEditorPanel = new JPanel();
		welcomeMessageEditorPanel.setLayout(new BorderLayout(5,5));

		String info = "Modify Server Welcome Message:\nThe specified welcome message will be sent to clients "
				+ "upon successful authentication. You may specifify that only new members and guest members "
				+ "receive the welcome message.\n\n"
				+ "The welcome message must not exceed 400 characters in length. Additional characters will be removed. "
				+ "Empty lines at the top or bottom of the message will also be removed.";
		welcomeMessageEditorPanel.add(super.createInformationPanel(info), BorderLayout.PAGE_START);
		
		JPanel innerPanel = new JPanel();
		innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.PAGE_AXIS));
		innerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		innerPanel.add(new JLabel("New Member and Guest Welcome Message"));
		innerPanel.add(new JScrollPane(this.newMemberMessageEditor));
		innerPanel.add(Box.createRigidArea(new Dimension(0,10)));
		innerPanel.add(new JLabel("Returning Member Welcome Message"));
		innerPanel.add(new JScrollPane(this.returningMemberMessageEditor));
		
		welcomeMessageEditorPanel.add(innerPanel, BorderLayout.CENTER);
		
		innerPanel = new JPanel();
		innerPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		innerPanel.add(this.alwaysSendWelcomeMessageCheckBox);
		
		welcomeMessageEditorPanel.add(innerPanel, BorderLayout.PAGE_END);
		
		return welcomeMessageEditorPanel;
	}

	public String[] requestChangedFields()
	{
		ArrayList<String> changedFields = new ArrayList<String>();
		
		if(!this.newMemberMessageEditor.getText().equals(AbstractServer.newMemberGuestWelcomeMessage))
			changedFields.add("New Member Welcome Message");
		
		if(!this.returningMemberMessageEditor.getText().equals(AbstractServer.returningMemberWelcomeMessage))
			changedFields.add("Returning Member Welcome Message");
		
		if(this.alwaysSendWelcomeMessageCheckBox.isSelected() != AbstractServer.alwaysSendWelcomeMessage)
			changedFields.add("Enable/Disable Always Send Welcome Message");
		
		return changedFields.toArray(new String[0]);
	}
	
	public void save()
	{
		String newMemberWelcomeMessage = this.newMemberMessageEditor.getText();
		newMemberWelcomeMessage = newMemberWelcomeMessage.trim();
		newMemberWelcomeMessage = (newMemberWelcomeMessage.length() >= 400) ? newMemberWelcomeMessage.substring(0,400) : newMemberWelcomeMessage;

		String returningMemberWelcomeMessage = this.returningMemberMessageEditor.getText();
		returningMemberWelcomeMessage = returningMemberWelcomeMessage.trim();
		returningMemberWelcomeMessage = (returningMemberWelcomeMessage.length() >= 400) ? returningMemberWelcomeMessage.substring(0,400) : returningMemberWelcomeMessage;

		AbstractServer.newMemberGuestWelcomeMessage = newMemberWelcomeMessage;
		AbstractServer.returningMemberWelcomeMessage = returningMemberWelcomeMessage;
		AbstractServer.alwaysSendWelcomeMessage = this.alwaysSendWelcomeMessageCheckBox.isSelected();
	}

	public void populatePanel()
	{
		this.newMemberMessageEditor.setText(AbstractServer.newMemberGuestWelcomeMessage);
		this.returningMemberMessageEditor.setText(AbstractServer.returningMemberWelcomeMessage);
		this.alwaysSendWelcomeMessageCheckBox.setSelected(AbstractServer.alwaysSendWelcomeMessage);
	}
}
