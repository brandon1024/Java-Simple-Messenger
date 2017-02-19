package webchatinterface.server.ui.preferences;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import webchatinterface.server.AbstractServer;

public class SecuritySettingsPanel extends PreferencePanel
{
	private static final long serialVersionUID = 6119231329123738660L;

	private JTextField hashingAlgorithmField;
	
	private JTextField keyGeneratorAlgorithmField;
	
	private JComboBox<Object> saltLengthComboBox;
	
	private JComboBox<Object> userIDKeyLengthComboBox;
	
	private JComboBox<Object> supportedUserIDKeyAlgorithmsComboBox;
	
	private JCheckBox blacklistInconsistentUserIDCheckBox;
	
	private ComboBoxItem[] security;
	
	private ComboBoxItem[] supportedUserIDKeyAlgorithms;
	
	public SecuritySettingsPanel(String header)
	{
		super(header);
		this.hashingAlgorithmField = new JTextField(15);
		this.keyGeneratorAlgorithmField = new JTextField(15);
		this.blacklistInconsistentUserIDCheckBox = new JCheckBox("Blacklist Account and IP Address if User ID Inconsistent");
		
		this.security = new ComboBoxItem[8];
		this.security[0] = new ComboBoxItem("32-bit Security", new Integer(32));
		this.security[1] = new ComboBoxItem("64-bit Security", new Integer(64));
		this.security[2] = new ComboBoxItem("128-bit Security", new Integer(128));
		this.security[3] = new ComboBoxItem("256-bit Security", new Integer(256));
		this.security[4] = new ComboBoxItem("512-bit Security", new Integer(512));
		this.security[5] = new ComboBoxItem("1024-bit Security", new Integer(1024));
		this.security[6] = new ComboBoxItem("2048-bit Security", new Integer(2048));
		this.security[7] = new ComboBoxItem("4096-bit Security", new Integer(4096));
		
		this.saltLengthComboBox = new JComboBox<Object>(this.security);
		this.userIDKeyLengthComboBox = new JComboBox<Object>(this.security);
		
		this.supportedUserIDKeyAlgorithms = new ComboBoxItem[5];
		this.supportedUserIDKeyAlgorithms[0] = new ComboBoxItem("ALPHANUMERIC_MIXED_CASE", "ALPHANUMERIC_MIXED_CASE");
		this.supportedUserIDKeyAlgorithms[1] = new ComboBoxItem("ALPHANUMERIC_LOWER_CASE", "ALPHANUMERIC_LOWER_CASE");
		this.supportedUserIDKeyAlgorithms[2] = new ComboBoxItem("ALPHANUMERIC_UPPER_CASE", "ALPHANUMERIC_UPPER_CASE");
		this.supportedUserIDKeyAlgorithms[3] = new ComboBoxItem("NUMERIC", "NUMERIC");
		this.supportedUserIDKeyAlgorithms[4] = new ComboBoxItem("ALPHABETIC", "ALPHABETIC");
		
		this.supportedUserIDKeyAlgorithmsComboBox = new JComboBox<Object>(this.supportedUserIDKeyAlgorithms);
		
		this.populatePanel();
		
		JPanel body = new JPanel();
		body.setLayout(new BoxLayout(body, BoxLayout.PAGE_AXIS));
		body.setBorder(BorderFactory.createEmptyBorder());
		body.add(this.buildAccountStorePanel());
		body.add(this.buildUserIDPanel());
		body.setPreferredSize(new Dimension(100, 585));
		super.add(new JScrollPane(body, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);
	}
	
	protected JPanel buildAccountStorePanel()
	{
		JPanel accountStorePanel = new JPanel();
		accountStorePanel.setLayout(new BoxLayout(accountStorePanel, BoxLayout.PAGE_AXIS));
		accountStorePanel.setBorder(BorderFactory.createTitledBorder("User Account Security"));
		
		String info = "Confidential User Information Hashing:\nClient account passwords are never stored or displayed in "
				+ "plain text. Passwords are stored by the server using industry standard "
				+ "salted-hash algorithms, which prevents attackers from using dictionaries, brute force, "
				+ "lookup tables, reverse lookup tables, and rainbow tables as methods of obtaining client passwords.\n\n"
				+ "Changing the confidential user information settings may introduce vulnerabilities into the user account "
				+ "store. It is recommended to use recommended settings.\n\n"
				+ "For a list of supported hashing algorithms, see \"Message Digest Algorithms\" specified in the Java™ "
				+ "Cryptography Architecture Standard Algorithm Name Documentation. http://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#MessageDigest\n\n"
				+ "For a list of supported salt generation algorithms, see \"SecureRandom Number Generation (RNG) Algorithms\" specified in the Java™ "
				+ "Cryptography Architecture Standard Algorithm Name Documentation. http://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#SecureRandom";
		accountStorePanel.add(super.createInformationPanel(info));
		
		JPanel innerPanel = new JPanel();
		innerPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		innerPanel.add(new JLabel("MessageDigest Hashing Algorithm: "));
		innerPanel.add(this.hashingAlgorithmField);
		innerPanel.add(new JLabel("(Recommended: SHA-256)"));
		accountStorePanel.add(innerPanel);
		
		innerPanel = new JPanel();
		innerPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		innerPanel.add(new JLabel("SecureRandom Salt Algorithm: "));
		innerPanel.add(this.keyGeneratorAlgorithmField);
		innerPanel.add(new JLabel("(Recommended: SHA1PRNG)"));
		accountStorePanel.add(innerPanel);
		
		innerPanel = new JPanel();
		innerPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		innerPanel.add(new JLabel("SecureRandom Salt Length: "));
		innerPanel.add(this.saltLengthComboBox);
		innerPanel.add(new JLabel("(Default: 32-bit)"));
		accountStorePanel.add(innerPanel);
		
		return accountStorePanel;
	}
	
	protected JPanel buildUserIDPanel()
	{
		JPanel userIDPanel = new JPanel();
		userIDPanel.setLayout(new BoxLayout(userIDPanel, BoxLayout.PAGE_AXIS));
		userIDPanel.setBorder(BorderFactory.createTitledBorder("User Identification and Validation"));
		
		String info = "Connection Security:\nEach client connected to the server is assigned a unique "
				+ "user identification key. This key is used to distinguish between users, and used as connection "
				+ "control and message validation. Each message received by a client is verified against the user "
				+ "idenfitication key on record. If the user identification key does not match, the client will be "
				+ "disconnected immediately. An inconsistent ID suggests suspicous behavior on the part of the client.";
		userIDPanel.add(super.createInformationPanel(info));
		
		JPanel innerPanel = new JPanel();
		innerPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		innerPanel.add(new JLabel("User Identification Key Length: "));
		innerPanel.add(this.userIDKeyLengthComboBox);
		innerPanel.add(new JLabel("(Default: 256-bit)"));
		userIDPanel.add(innerPanel);
		
		innerPanel = new JPanel();
		innerPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		innerPanel.add(new JLabel("User Identification Key Algorithm: "));
		innerPanel.add(this.supportedUserIDKeyAlgorithmsComboBox);
		innerPanel.add(new JLabel("(Default: ALPHANUMERIC_MIXED_CASE)"));
		userIDPanel.add(innerPanel);
		
		innerPanel = new JPanel();
		innerPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		innerPanel.add(this.blacklistInconsistentUserIDCheckBox);
		userIDPanel.add(innerPanel);
		
		return userIDPanel;
	}
	
	public String[] requestChangedFields()
	{
		ArrayList<String> changedFields = new ArrayList<String>();
		
		if(!this.hashingAlgorithmField.getText().equals(AbstractServer.messageDigestHashingAlgorithm))
		{
			changedFields.add("Message Digest Hashing Algorithm");
		}
		
		if(!this.keyGeneratorAlgorithmField.getText().equals(AbstractServer.secureRandomSaltAlgorithm))
		{
			changedFields.add("Secure Random Salt Generator");
		}
		
		ComboBoxItem item1 = (ComboBoxItem)(this.saltLengthComboBox.getSelectedItem());
		Integer value1 = (Integer)(item1.getValue());
		if(value1.intValue() != AbstractServer.secureRandomSaltLength)
		{
			changedFields.add("Secure Random Salt Length");
		}
		
		ComboBoxItem item2 = (ComboBoxItem)(this.userIDKeyLengthComboBox.getSelectedItem());
		Integer value2 = (Integer)(item2.getValue());
		if(value2.intValue() != AbstractServer.userIDKeyLength)
		{
			changedFields.add("User ID Key Length");
		}
		
		ComboBoxItem item3 = (ComboBoxItem)(this.supportedUserIDKeyAlgorithmsComboBox.getSelectedItem());
		String value3 = (String)(item3.getValue());
		if(!value3.equals(AbstractServer.userIDAlgorithm))
		{
			changedFields.add("User ID Key Generator Algorithm");
		}
		
		if(blacklistInconsistentUserIDCheckBox.isSelected() != AbstractServer.blacklistAccountIPInconsistentUserID)
		{
			changedFields.add("Enable/Disable Blacklist Account/IP On Inconsistent User ID");
		}
		
		return changedFields.toArray(new String[0]);
	}
	
	public void save()
	{
		AbstractServer.messageDigestHashingAlgorithm = this.hashingAlgorithmField.getText();
		AbstractServer.secureRandomSaltAlgorithm = this.keyGeneratorAlgorithmField.getText();
		AbstractServer.secureRandomSaltLength = (int)((ComboBoxItem)(this.saltLengthComboBox.getSelectedItem())).getValue();
		AbstractServer.userIDKeyLength = (int)((ComboBoxItem)(this.userIDKeyLengthComboBox.getSelectedItem())).getValue();
		AbstractServer.userIDAlgorithm = (String)((ComboBoxItem)(this.supportedUserIDKeyAlgorithmsComboBox.getSelectedItem())).getValue();
		AbstractServer.blacklistAccountIPInconsistentUserID = this.blacklistInconsistentUserIDCheckBox.isSelected();
	}

	protected void populatePanel()
	{
		this.hashingAlgorithmField.setText(AbstractServer.messageDigestHashingAlgorithm);
		this.keyGeneratorAlgorithmField.setText(AbstractServer.secureRandomSaltAlgorithm);
		this.blacklistInconsistentUserIDCheckBox.setSelected(AbstractServer.blacklistAccountIPInconsistentUserID);
		
		switch(AbstractServer.secureRandomSaltLength)
		{
			case 32:
				this.saltLengthComboBox.setSelectedIndex(0);
				break;
			case 64:
				this.saltLengthComboBox.setSelectedIndex(1);
				break;
			case 128:
				this.saltLengthComboBox.setSelectedIndex(2);
				break;
			case 256:
				this.saltLengthComboBox.setSelectedIndex(3);
				break;
			case 512:
				this.saltLengthComboBox.setSelectedIndex(4);
				break;
			case 1024:
				this.saltLengthComboBox.setSelectedIndex(5);
				break;
			case 2048:
				this.saltLengthComboBox.setSelectedIndex(6);
				break;
			case 4096:
				this.saltLengthComboBox.setSelectedIndex(7);
				break;
		}
		
		switch(AbstractServer.userIDKeyLength)
		{
			case 32:
				this.userIDKeyLengthComboBox.setSelectedIndex(0);
				break;
			case 64:
				this.userIDKeyLengthComboBox.setSelectedIndex(1);
				break;
			case 128:
				this.userIDKeyLengthComboBox.setSelectedIndex(2);
				break;
			case 256:
				this.userIDKeyLengthComboBox.setSelectedIndex(3);
				break;
			case 512:
				this.userIDKeyLengthComboBox.setSelectedIndex(4);
				break;
			case 1024:
				this.userIDKeyLengthComboBox.setSelectedIndex(5);
				break;
			case 2048:
				this.userIDKeyLengthComboBox.setSelectedIndex(6);
				break;
			case 4096:
				this.userIDKeyLengthComboBox.setSelectedIndex(7);
				break;
		}
		
		switch(AbstractServer.userIDAlgorithm)
		{
			case "ALPHANUMERIC_MIXED_CASE":
				this.supportedUserIDKeyAlgorithmsComboBox.setSelectedIndex(0);
				break;
			case "ALPHANUMERIC_LOWER_CASE":
				this.supportedUserIDKeyAlgorithmsComboBox.setSelectedIndex(1);
				break;
			case "ALPHANUMERIC_UPPER_CASE":
				this.supportedUserIDKeyAlgorithmsComboBox.setSelectedIndex(2);
				break;
			case "NUMERIC":
				this.supportedUserIDKeyAlgorithmsComboBox.setSelectedIndex(3);
				break;
			case "ALPHABETIC":
				this.supportedUserIDKeyAlgorithmsComboBox.setSelectedIndex(4);
				break;
		}
	}
	
	private class ComboBoxItem
	{
		private Object value;
		
		private Object name;
		
		public ComboBoxItem(Object name, Object value)
		{
			this.name = name;
			this.value = value;
		}

		public Object getValue()
		{
			return value;
		}

		public Object getName()
		{
			return name;
		}
		
		public String toString()
		{
			return this.getName().toString();
		}
	}
}
