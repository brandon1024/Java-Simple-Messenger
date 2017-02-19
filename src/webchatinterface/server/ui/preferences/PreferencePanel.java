package webchatinterface.server.ui.preferences;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public abstract class PreferencePanel extends JPanel
{
	private static final long serialVersionUID = 7574662138707249779L;

	private String panelID;
	
	protected PreferencePanel(String header)
	{
		super();
		super.setLayout(new BorderLayout(5,5));
		super.add(new PanelHeaderLabel(header), BorderLayout.PAGE_START);
		this.panelID = header;
	}
	
	public abstract String[] requestChangedFields();
	
	public abstract void save();
	
	protected abstract void populatePanel();
	
	public String getID()
	{
		return this.panelID;
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
}
