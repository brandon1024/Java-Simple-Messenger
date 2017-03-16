package webchatinterface.server.ui.components.preferences;

import webchatinterface.server.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class SecuritySettingsPanel extends PreferencePanel
{
	private JTextField hashingAlgorithmField;
	private JTextField keyGeneratorAlgorithmField;
	private JComboBox<Object> saltLengthComboBox;
	private JComboBox<Object> userIDKeyLengthComboBox;
	private JComboBox<Object> supportedUserIDKeyAlgorithmsComboBox;
	private JCheckBox blacklistInconsistentUserIDCheckBox;
	
	public SecuritySettingsPanel(String header)
	{
		super(header);
		this.hashingAlgorithmField = new JTextField(15);
		this.keyGeneratorAlgorithmField = new JTextField(15);
		this.blacklistInconsistentUserIDCheckBox = new JCheckBox("Blacklist Account and IP Address if User ID Inconsistent");

		ComboBoxItem[] security = new ComboBoxItem[8];
		security[0] = new ComboBoxItem("32-bit Security", new Integer(32));
		security[1] = new ComboBoxItem("64-bit Security", new Integer(64));
		security[2] = new ComboBoxItem("128-bit Security", new Integer(128));
		security[3] = new ComboBoxItem("256-bit Security", new Integer(256));
		security[4] = new ComboBoxItem("512-bit Security", new Integer(512));
		security[5] = new ComboBoxItem("1024-bit Security", new Integer(1024));
		security[6] = new ComboBoxItem("2048-bit Security", new Integer(2048));
		security[7] = new ComboBoxItem("4096-bit Security", new Integer(4096));
		
		this.saltLengthComboBox = new JComboBox<Object>(security);
		this.userIDKeyLengthComboBox = new JComboBox<Object>(security);

		ComboBoxItem[] supportedUserIDKeyAlgorithms = new ComboBoxItem[5];
		supportedUserIDKeyAlgorithms[0] = new ComboBoxItem("ALPHANUMERIC_MIXED_CASE", "ALPHANUMERIC_MIXED_CASE");
		supportedUserIDKeyAlgorithms[1] = new ComboBoxItem("ALPHANUMERIC_LOWER_CASE", "ALPHANUMERIC_LOWER_CASE");
		supportedUserIDKeyAlgorithms[2] = new ComboBoxItem("ALPHANUMERIC_UPPER_CASE", "ALPHANUMERIC_UPPER_CASE");
		supportedUserIDKeyAlgorithms[3] = new ComboBoxItem("NUMERIC", "NUMERIC");
		supportedUserIDKeyAlgorithms[4] = new ComboBoxItem("ALPHABETIC", "ALPHABETIC");
		
		this.supportedUserIDKeyAlgorithmsComboBox = new JComboBox<Object>(supportedUserIDKeyAlgorithms);
		
		JPanel body = new JPanel();
		body.setLayout(new BoxLayout(body, BoxLayout.PAGE_AXIS));
		body.setBorder(BorderFactory.createEmptyBorder());
		body.add(this.buildAccountStorePanel());
		body.add(this.buildUserIDSettingsPanel());
		body.setPreferredSize(new Dimension(100, 585));
		super.add(new JScrollPane(body, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);
	}
	
	private JPanel buildAccountStorePanel()
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
				+ "For a list of supported hashing algorithms, see \"Message Digest Algorithms\" specified in the Java� "
				+ "Cryptography Architecture Standard Algorithm Name Documentation. http://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#MessageDigest\n\n"
				+ "For a list of supported salt generation algorithms, see \"SecureRandom Number Generation (RNG) Algorithms\" specified in the Java� "
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
	
	private JPanel buildUserIDSettingsPanel()
	{
		JPanel userIDPanel = new JPanel();
		userIDPanel.setLayout(new BoxLayout(userIDPanel, BoxLayout.PAGE_AXIS));
		userIDPanel.setBorder(BorderFactory.createTitledBorder("User Identification and Validation"));
		
		String info = "Connection Security:\nEach client connected to the server is assigned a unique "
				+ "user identification key. This key is used to distinguish between users, and used as connection "
				+ "control and message validation. Each message received by a client is verified against the user "
				+ "identification key on record. If the user identification key does not match, the client will be "
				+ "disconnected immediately. An inconsistent ID suggests suspicious behavior on the part of the client.";
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

	public HashMap<String, Pair<Object, Boolean>> getModifiedPreferences()
	{
		//TODO:
		return null;
	}

	public void setPreferences(HashMap<String, Pair<Object, Boolean>> preferences)
	{
		//TODO:
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
