package webchatinterface.server.ui.components.preferences;

import webchatinterface.server.util.Pair;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public abstract class PreferencePanel extends JPanel
{
	protected PreferencePanel(String header)
	{
		super(new BorderLayout(5,5));
		super.add(new PanelHeaderLabel(header), BorderLayout.PAGE_START);
	}

	protected JComponent createInformationPanel(String text)
	{
		JTextArea information = new JTextArea(text);
		information.setWrapStyleWord(true);
		information.setLineWrap(true);
		information.setOpaque(false);
		information.setEditable(false);
		information.setFocusable(false);
		information.setFont(new Font("Arial", Font.PLAIN, 11));
		return information;
	}

	public abstract HashMap<String, Pair<Object, Boolean>> getModifiedPreferences();

	public abstract void setPreferences(HashMap<String, Pair<Object, Boolean>> preferences);
}