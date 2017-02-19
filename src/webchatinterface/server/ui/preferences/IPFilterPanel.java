package webchatinterface.server.ui.preferences;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class IPFilterPanel extends PreferencePanel
{
	private static final long serialVersionUID = 16172777675754695L;
	
	private JTextArea IPFilterEditor;
	
	private JCheckBox filterAllIPAddressesExceptCheckBox;
	
	private boolean edited;
	
	public IPFilterPanel(String header)
	{
		super(header);
		this.IPFilterEditor = new JTextArea();
		this.filterAllIPAddressesExceptCheckBox = new JCheckBox("Filter All Addresses Except Those Marked With Asterisk (*)");
		this.edited = false;
		this.populatePanel();
		
		this.IPFilterEditor.getDocument().addDocumentListener(new DocumentListener()
		{
			public void changedUpdate(DocumentEvent event)
			{
				IPFilterPanel.this.edited = true;
			}

			public void insertUpdate(DocumentEvent event)
			{
				IPFilterPanel.this.edited = true;
			}

			public void removeUpdate(DocumentEvent event)
			{
				IPFilterPanel.this.edited = true;
			}
		});
		
		JPanel body = new JPanel();
		body.setLayout(new BoxLayout(body, BoxLayout.PAGE_AXIS));
		body.setBorder(BorderFactory.createEmptyBorder());
		body.add(this.buildIPFilterPanel());
		
		super.add(body, BorderLayout.CENTER);
	}
	
	protected JPanel buildIPFilterPanel()
	{
		JPanel IPFilterSettingsPanel = new JPanel();
		IPFilterSettingsPanel.setLayout(new BorderLayout(5,5));
		IPFilterSettingsPanel.setBorder(BorderFactory.createTitledBorder("IP Address Filter"));
		
		String info = "Filter IP Addresses:\nFilter the following list of IP addresses from connecting to the server. Each address "
				+ "must be listed individually, seperated by a whitespace character or on a new line. Invalid or incorrect addresses will be ignored.\n\n"
				+ "If the \"Filter All Addresses\" checkbox below is selected, all addresses will be filtered except those preceeded by an asterisk."
				+ "For example, *xxx.xxx.xxx.xxx";
		IPFilterSettingsPanel.add(super.createInformationPanel(info), BorderLayout.PAGE_START);

		JPanel innerPanel = new JPanel();
		innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.PAGE_AXIS));
		innerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		innerPanel.add(new JScrollPane(this.IPFilterEditor));
		
		IPFilterSettingsPanel.add(innerPanel, BorderLayout.CENTER);
		
		innerPanel = new JPanel();
		innerPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		innerPanel.add(this.filterAllIPAddressesExceptCheckBox);
		
		IPFilterSettingsPanel.add(innerPanel, BorderLayout.PAGE_END);
		
		return IPFilterSettingsPanel;
	}

	public String[] requestChangedFields()
	{
		if(this.edited)
		{
			return new String[]{"IP Address Filter"};
		}
		else
		{
			return new String[0];
		}
	}
	
	public void save(){}

	protected void populatePanel(){}
}
