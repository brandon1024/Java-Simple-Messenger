package webchatinterface.server.ui.dialog;

import webchatinterface.server.util.ResourceLoader;
import webchatinterface.server.communication.ScheduledServerMessage;
import webchatinterface.server.ui.WebChatServerGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class BroadcastMessageDialog extends JDialog
{
	private JComboBox<ScheduledServerMessage> automatedMessages;
	private ScheduledServerMessage defaultMessage;
	private JTextArea messageField;
	private JTextField customEveryMinutes;
	private JTextField customDailyMinutes;
	private JTextField customDailyHours;
	private JRadioButton oneTime;
	private JRadioButton every1Min;
	private JRadioButton every15Min;
	private JRadioButton every1Hour;
	private JRadioButton custom;
	private int exitCode;

	public BroadcastMessageDialog(WebChatServerGUI parent, ScheduledServerMessage[] scheduledMessages)
	{
		super(parent, "Schedule Server Message", Dialog.ModalityType.DOCUMENT_MODAL);
		super.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		super.setSize(560, 285);
		super.setResizable(false);
		super.setLocationRelativeTo(parent);
		super.setIconImage(ResourceLoader.getInstance().getFrameIcon());
		this.init(scheduledMessages);
		this.exitCode = 0;
	}

	private void init(ScheduledServerMessage[] scheduledMessages)
	{
		//Build JComboBox and Populate with AutomatedServerMessages
		this.automatedMessages = new JComboBox<ScheduledServerMessage>();
		this.defaultMessage = new ScheduledServerMessage("New Message", 0);
		this.automatedMessages.addItem(this.defaultMessage);

		synchronized(this)
		{
			for(ScheduledServerMessage message : scheduledMessages)
				this.automatedMessages.addItem(message);
		}
		this.automatedMessages.setSelectedIndex(0);

		//Build Message JTextArea and Make Scrollable
		this.messageField = new JTextArea(3,50);
		this.messageField.setWrapStyleWord(true);
		JScrollPane scroll = new JScrollPane(this.messageField);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		//Add to Message Panel
		JPanel messagePanel = new JPanel();
		messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.PAGE_AXIS));
		messagePanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		messagePanel.add(new JLabel("Enter Message to Broadcast:"));
		messagePanel.add(scroll);

		//Build Custom Frequency TextFields
		this.customEveryMinutes = new JTextField();
		this.customEveryMinutes.setEnabled(false);
		this.customDailyMinutes = new JTextField();
		this.customDailyMinutes.setEnabled(false);
		this.customDailyHours = new JTextField();
		this.customDailyHours.setEnabled(false);

		//Build RadioButton Frequency Options
		this.oneTime = new JRadioButton("One Time", true);
		this.every1Min = new JRadioButton("Every 1 Minute");
		this.every15Min = new JRadioButton("Every 15 Minute");
		this.every1Hour = new JRadioButton("Every 1 Hour");
		this.custom = new JRadioButton("Custom:");

		//Hide Custom Frequency TextFields when Custom Radio Button Not Selected
		this.custom.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				if (e.getStateChange() == ItemEvent.SELECTED)
				{
					BroadcastMessageDialog.this.customEveryMinutes.setEnabled(true);
					BroadcastMessageDialog.this.customDailyMinutes.setEnabled(true);
					BroadcastMessageDialog.this.customDailyHours.setEnabled(true);
				}
				else if (e.getStateChange() == ItemEvent.DESELECTED)
				{
					BroadcastMessageDialog.this.customEveryMinutes.setEnabled(false);
					BroadcastMessageDialog.this.customDailyMinutes.setEnabled(false);
					BroadcastMessageDialog.this.customDailyHours.setEnabled(false);
				}
			}
		});

		//Add RadioButtons to Group
		ButtonGroup group = new ButtonGroup();
		group.add(this.oneTime);
		group.add(this.every1Min);
		group.add(this.every15Min);
		group.add(this.every1Hour);
		group.add(this.custom);

		//Build and Populate Inner Panel
		JPanel customEveryMinutesPanel = new JPanel();
		customEveryMinutesPanel.setLayout(new BoxLayout(customEveryMinutesPanel, BoxLayout.LINE_AXIS));
		customEveryMinutesPanel.add(this.customEveryMinutes);
		customEveryMinutesPanel.add(new JLabel(" minutes"));

		//Build and Populate Inner Panel
		JPanel customDailyPanel = new JPanel();
		customDailyPanel.setLayout(new BoxLayout(customDailyPanel, BoxLayout.LINE_AXIS));
		customDailyPanel.add(this.customDailyHours);
		customDailyPanel.add(new JLabel(" : "));
		customDailyPanel.add(this.customDailyMinutes);
		customDailyPanel.add(new JLabel(" daily"));
		customEveryMinutesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		customDailyPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		//Set Component Alignment
		this.oneTime.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.every1Min.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.every15Min.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.every1Hour.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.custom.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.customEveryMinutes.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.customDailyHours.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.customDailyMinutes.setAlignmentX(Component.LEFT_ALIGNMENT);

		JButton submitButton = new JButton("OK");
		JButton deleteButton = new JButton("Delete");
		JButton clearAllButton = new JButton("Clear All");
		JButton cancelButton = new JButton("Cancel");

		submitButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				try
				{
					if(!BroadcastMessageDialog.this.customEveryMinutes.getText().isEmpty())
						Integer.parseInt(BroadcastMessageDialog.this.customEveryMinutes.getText());
				}
				catch(Exception e)
				{
					JOptionPane.showMessageDialog(null, "Invalid field: custom every minutes", "Invalid Field", JOptionPane.INFORMATION_MESSAGE);
					return;
				}

				try
				{
					if(!BroadcastMessageDialog.this.customDailyHours.getText().isEmpty())
						Integer.parseInt(BroadcastMessageDialog.this.customDailyHours.getText());
				}
				catch(Exception e)
				{
					JOptionPane.showMessageDialog(null, "Invalid field:", "Invalid Field", JOptionPane.INFORMATION_MESSAGE);
					return;
				}

				try
				{
					if(!BroadcastMessageDialog.this.customDailyMinutes.getText().isEmpty())
						Integer.parseInt(BroadcastMessageDialog.this.customDailyMinutes.getText());
				}
				catch(Exception e)
				{
					JOptionPane.showMessageDialog(null, "Invalid field:", "Invalid Field", JOptionPane.INFORMATION_MESSAGE);
					return;
				}

				BroadcastMessageDialog.this.exitCode = 1;
				BroadcastMessageDialog.this.dispose();
			}
		});

		deleteButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				BroadcastMessageDialog.this.exitCode = 2;
				BroadcastMessageDialog.this.dispose();
			}
		});

		clearAllButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				BroadcastMessageDialog.this.exitCode = 3;
				BroadcastMessageDialog.this.dispose();
			}
		});

		cancelButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				BroadcastMessageDialog.this.exitCode = 4;
				BroadcastMessageDialog.this.dispose();
			}
		});

		JPanel buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.add(submitButton);
		buttonPanel.add(deleteButton);
		buttonPanel.add(clearAllButton);
		buttonPanel.add(cancelButton);

		//Populate Frequency Panel
		JPanel frequencyPanel = new JPanel();
		frequencyPanel.setLayout(new BoxLayout(frequencyPanel, BoxLayout.PAGE_AXIS));
		frequencyPanel.setBorder(BorderFactory.createEmptyBorder(5,0,5,5));
		frequencyPanel.add(new JLabel(" Frequency:"));
		frequencyPanel.add(this.oneTime);
		frequencyPanel.add(this.every1Min);
		frequencyPanel.add(this.every15Min);
		frequencyPanel.add(this.every1Hour);
		frequencyPanel.add(this.custom);
		frequencyPanel.add(customEveryMinutesPanel);
		frequencyPanel.add(new JLabel("or"));
		frequencyPanel.add(customDailyPanel);

		//Populate Master Panel
		super.getContentPane().setLayout(new BorderLayout(5,5));
		super.getContentPane().add(this.automatedMessages, BorderLayout.PAGE_START);
		super.getContentPane().add(messagePanel, BorderLayout.CENTER);
		super.getContentPane().add(frequencyPanel, BorderLayout.LINE_END);
		super.getContentPane().add(buttonPanel, BorderLayout.PAGE_END);
	}

	public int showDialog()
	{
		this.setVisible(true);
		return exitCode;
	}

	public ScheduledServerMessage getScheduledMessage()
	{
		ScheduledServerMessage message = null;

		if(this.oneTime.isSelected() && !this.messageField.getText().isEmpty())
			message = new ScheduledServerMessage(this.messageField.getText(), 0);
		else if(this.every1Min.isSelected())
			message = new ScheduledServerMessage(this.messageField.getText(), 1);
		else if(this.every15Min.isSelected())
			message = new ScheduledServerMessage(this.messageField.getText(), 15);
		else if(this.every1Hour.isSelected())
			message = new ScheduledServerMessage(this.messageField.getText(), 60);
		else if(this.custom.isSelected())
		{
			if(!this.customEveryMinutes.getText().isEmpty())
			{
				int min = Integer.parseInt(this.customEveryMinutes.getText());
				message = new ScheduledServerMessage(this.messageField.getText(), min);
			}
			else
			{
				int dailyHour = Integer.parseInt(this.customDailyHours.getText());
				int dailyMin = Integer.parseInt(this.customDailyMinutes.getText());
				message = new ScheduledServerMessage(this.messageField.getText(), dailyHour, dailyMin);
			}
		}

		return message;
	}

	public ScheduledServerMessage getSelectedScheduledMessage()
	{
		ScheduledServerMessage message = (ScheduledServerMessage)automatedMessages.getSelectedItem();
		if(message.equals(this.defaultMessage))
			return null;

		return message;
	}
}
