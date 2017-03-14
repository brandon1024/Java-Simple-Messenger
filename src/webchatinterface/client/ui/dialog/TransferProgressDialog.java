package webchatinterface.client.ui.dialog;

import webchatinterface.client.AbstractClient;
import webchatinterface.client.communication.filetransfer.TransferDialogUtilities;
import webchatinterface.client.util.ResourceLoader;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**@author Brandon Richardson
  *@version 1.4.3
  *@since 06/05/2016
  *<p>
  *The {@code TransferProgressDialog} class is used to display information regarding the transfer
  *of a file between the client and the server.
  *<p>
  *Once a new instance of TransferProgressDialog is constructed, the dialog is displayed in a new frame 
  *with default fields. Each field is editable using mutator methods. TransferProgressDialog extends
  *JFrame, thus the frame properties can be altered according to desired specifications.
  */

public class TransferProgressDialog extends JFrame
{
	private static final Color PROGRESS_GREEN = new Color(0,204,0);
	private static final Color PROGRESS_RED = new Color(255,0,0);
	private static final Color PROGRESS_BLUE = new Color(0, 102, 255);

	private JProgressBar progress;
	private JLabel informationLabel;
	private JLabel speedLabel;
	private JLabel progressLabel;
	
	public TransferProgressDialog()
	{
		super("File Transfer");
		super.setIconImage(ResourceLoader.getInstance().getFrameIcon());
		super.setSize(450,78);
		super.setVisible(true);
		super.setResizable(false);
		super.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		this.progress = new JProgressBar(0,100);
		this.progress.setValue(0);
		this.progress.setStringPainted(true);
		this.informationLabel = new JLabel();
		this.speedLabel = new JLabel();
		this.progressLabel = new JLabel();
		
		JPanel informationContainer = new JPanel();
		informationContainer.setBorder(new EmptyBorder(3, 3, 3, 3));
		informationContainer.setLayout(new BorderLayout());
		informationContainer.add(this.informationLabel, BorderLayout.PAGE_START);
		informationContainer.add(this.speedLabel, BorderLayout.LINE_START);
		informationContainer.add(this.progressLabel, BorderLayout.LINE_END);

		super.getContentPane().setLayout(new BorderLayout());
		super.getContentPane().add(this.progress, BorderLayout.PAGE_START);
		super.getContentPane().add(informationContainer, BorderLayout.CENTER);
	}

	public void updateTransferDialog(long bytesRead, long arraySize, long bytesTotal, long timeElapsedMillis, String filename)
	{
		this.setTitle("Filename: " + filename);
		this.informationLabel.setText(filename);
		this.speedLabel.setText(TransferDialogUtilities.computeTransferSpeedText(arraySize, timeElapsedMillis, 1073741824));
		this.progress.setForeground(TransferProgressDialog.PROGRESS_GREEN);
		this.progress.setValue(TransferDialogUtilities.progressPercentage(bytesRead, bytesTotal));
		this.progress.setString(TransferDialogUtilities.computePercentCompletionText(bytesRead, bytesTotal));
		this.progressLabel.setText(TransferDialogUtilities.computeProgressText(bytesRead, bytesTotal));
	}

	public void updateTransferDialogComplete()
	{
		this.setWindowTitleBarText("Complete");
		this.progressLabel.setText("File Transfer Complete");
		this.progress.setValue(100);
		this.progress.setForeground(TransferProgressDialog.PROGRESS_BLUE);

		for(int i = 5; i >= 0; i--)
		{
			this.progress.setString("Dismissed in " + i + " seconds");
			try
			{
				Thread.sleep(1000);
			}
			catch(InterruptedException e)
			{
				AbstractClient.logException(e);
			}
		}

		this.dispose();
	}

	public void updateTransferDialogError()
	{
		this.progress.setForeground(TransferProgressDialog.PROGRESS_RED);
		this.progress.setString("ERROR OCCURRED");
		this.progressLabel.setText("ERROR OCCURRED");
		this.setWindowTitleBarText("ERROR OCCURRED");
	}
	
	private void setWindowTitleBarText(String titleBar)
	{
		this.setTitle("File Transfer - " + titleBar);
	}
}
