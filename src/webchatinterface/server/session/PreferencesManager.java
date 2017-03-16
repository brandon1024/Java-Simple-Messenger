package webchatinterface.server.session;

import webchatinterface.server.ui.dialog.PreferencesDialog;
import webchatinterface.server.util.Pair;

import javax.swing.*;
import java.util.HashMap;

public class PreferencesManager
{
	public PreferencesManager()
	{
		//TODO
	}

	public void showPreferencesDialog(JFrame parent)
	{
		PreferencesDialog dialog = new PreferencesDialog(parent);
		dialog.setPreferences(this.getPreferences());

		if(dialog.showDialog() == 1)
		{
			HashMap<String, Pair<Object, Boolean>> modifiedPreferences = dialog.getModifiedPreferences();
			this.savePreferences(modifiedPreferences);
		}
	}

	private void savePreferences(HashMap<String, Pair<Object, Boolean>> preferences)
	{
		//TODO
		//Save preferences to AbstractServer,
		//Show confirmation dialog if server needs to restart
		//Restart Server
	}

	private HashMap<String, Pair<Object, Boolean>> getPreferences()
	{
		//TODO
		//Build Preferences Hashmap from Abstract Server
		return null;
	}

	private int showConfirmDialog()
	{
		//TODO
		//Show simple confirmation dialog
		return 0;
	}
}
