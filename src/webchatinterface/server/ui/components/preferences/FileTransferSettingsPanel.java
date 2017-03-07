package webchatinterface.server.ui.components.preferences;

import webchatinterface.server.AbstractServer;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class FileTransferSettingsPanel extends PreferencePanel
{
	private static final long serialVersionUID = -3316390765717795160L;

	private JTextField transferBufferSizeField;
	
	private JTextField fileTransferSizeLimitField;
	
	public FileTransferSettingsPanel(String header)
	{
		super(header);
		this.transferBufferSizeField = new JTextField(15);
		this.fileTransferSizeLimitField = new JTextField(15);
		this.populatePanel();
		
		JPanel body = new JPanel();
		body.setLayout(new BoxLayout(body, BoxLayout.PAGE_AXIS));
		body.setBorder(BorderFactory.createEmptyBorder());
		body.add(this.buildTransferSettingsPanel());
		
		super.add(body, BorderLayout.CENTER);
		super.add(Box.createRigidArea(new Dimension(0,120)), BorderLayout.PAGE_END);
	}
	
	private JPanel buildTransferSettingsPanel()
	{
		JPanel transferSettingsPanel = new JPanel();
		transferSettingsPanel.setLayout(new BoxLayout(transferSettingsPanel, BoxLayout.PAGE_AXIS));
		transferSettingsPanel.setBorder(BorderFactory.createTitledBorder("Transfer Settings"));
		
		String info = "Specify File Transfer Buffer Size:\nA file transfer buffer is the smallest piece of data sent between the client "
				+ "and the server during a file transfer. Specifiying a buffer size will change the transfer speed."
				+ "\n\nUsing a value that is too large or two small can dramatically impact the data throughput via the server. "
				+ "A buffer size that is too large can cause the server to crash due to a memory overflow. It is recommended to only "
				+ "change this value if the clients are experiencing slow transfer rates.";
		transferSettingsPanel.add(super.createInformationPanel(info));
		JPanel innerPanel = new JPanel();
		innerPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		innerPanel.add(new JLabel("File Transfer Buffer Size: "));
		innerPanel.add(this.transferBufferSizeField);
		innerPanel.add(new JLabel("bytes (Default: 4096, recommended < 65536)"));
		transferSettingsPanel.add(innerPanel);
		transferSettingsPanel.add(Box.createRigidArea(new Dimension(0,15)));
		
		info = "Specify File Transfer Size Limit:\nSpecifying a file transfer size limit will limit the clients ability to send very large "
				+ "files, which effectively reduces server data throughput. This limit should be used only to prevent large file transfer, and not "
				+ "to limit the regular use of the server. Adjust this value based on the mean daily connection usage, i.e. use a smaller limit if "
				+ "the server sees significant daily traffic.";
		transferSettingsPanel.add(super.createInformationPanel(info));
		innerPanel = new JPanel();
		innerPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		innerPanel.add(new JLabel("File Transfer Size Limit: "));
		innerPanel.add(this.fileTransferSizeLimitField);
		innerPanel.add(new JLabel("bytes (Default: 104857600, 0 for no limit)"));
		transferSettingsPanel.add(innerPanel);
		
		return transferSettingsPanel;
	}

	public void populatePanel()
	{
		this.transferBufferSizeField.setText(Integer.toString(AbstractServer.fileTransferBufferSize));
		this.fileTransferSizeLimitField.setText(Long.toString(AbstractServer.fileTransferSizeLimit));
	}

	public String[] requestChangedFields()
	{
		ArrayList<String> changedFields = new ArrayList<String>();
		
		if(Integer.parseInt(this.transferBufferSizeField.getText()) != AbstractServer.fileTransferBufferSize)
			changedFields.add("File Transfer Buffer Size");
		
		if(Integer.parseInt(this.fileTransferSizeLimitField.getText()) != AbstractServer.fileTransferSizeLimit)
			changedFields.add("File Transfer Size Limit");
		
		return changedFields.toArray(new String[0]);
	}
	
	public void save()
	{
		AbstractServer.fileTransferBufferSize = Integer.parseInt(this.transferBufferSizeField.getText());
		AbstractServer.fileTransferSizeLimit = Integer.parseInt(this.fileTransferSizeLimitField.getText());
	}
}