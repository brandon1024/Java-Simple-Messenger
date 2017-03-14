package webchatinterface.server.ui.components.preferences;

import javax.swing.*;
import java.awt.*;

public abstract class PreferencePanel extends JPanel
{
	private String panelID;
	
	protected PreferencePanel(String header)
	{
		super();
		super.setLayout(new BorderLayout(5,5));
		super.add(new PanelHeaderLabel(header), BorderLayout.PAGE_START);
		this.panelID = header;
	}

	protected JComponent createInformationPanel(String text)
	{
		JTextArea information = new JTextArea();
		information.setWrapStyleWord(true);
		information.setLineWrap(true);
		information.setOpaque(false);
		information.setEditable(false);
		information.setFocusable(false);
		information.setFont(new Font("Arial", Font.PLAIN, 11));
		information.setText(text);

		return information;
	}

	public String getID()
	{
		return this.panelID;
	}

	public abstract String[] requestChangedFields();
	
	public abstract void save();
	
	public abstract void populatePanel();
}
