package webchatinterface.client.ui.dialog;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**@author Brandon Richardson
  *@version 1.4.3
  *@since 06/05/2016
  *<p>
  *The HelpDialog class is designed to display a small dialog to give the user a way to contact the
  *developer if in need of help with the use of the application.
  */

public class HelpDialog
{
	public static void showHelpDialog()
	{
		//display help dialog
		JPanel dialogPanel = new JPanel();
		String help = "Need help?"
		+ "\nContact the developer:"
		+ "\n\tBrandon Richardson"
		+ "\n\tEmail: brandon1024.br@gmail.com";

		JOptionPane.showMessageDialog(dialogPanel, help,
			"Get Help", JOptionPane.INFORMATION_MESSAGE);
	}
}
