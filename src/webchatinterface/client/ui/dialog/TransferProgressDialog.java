package webchatinterface.client.ui.dialog;

import webchatinterface.client.AbstractClient;
import webchatinterface.client.communication.filetransfer.TransferDialogUtilities;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;

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
		
		try
		{
			super.setIconImage(ImageIO.read(TransferProgressDialog.class.getResource("/webchatinterface/client/resources/CLIENTICON.png")));
		}
		catch(IOException | IllegalArgumentException e)
		{
			AbstractClient.logException(e);
		}
		
		super.setSize(450,78);
		super.setVisible(true);
		super.setResizable(false);
		super.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		Container masterPane = super.getContentPane();
		masterPane.setLayout(new BorderLayout());
		
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
		
		masterPane.add(this.progress, BorderLayout.PAGE_START);
		masterPane.add(informationContainer, BorderLayout.CENTER);
	}

	public void updateTransferDialog(long bytesRead, long arraySize, long bytesTotal, long timeElapsedMillis, String filename)
	{
		this.setProgressColor(TransferProgressDialog.PROGRESS_GREEN);
		this.setTitle("Filename: " + filename);
		this.setInformationLabelText(filename);
		this.setSpeedLabelText(TransferDialogUtilities.computeTransferSpeedText(arraySize, timeElapsedMillis));
		this.setProgressValue(TransferDialogUtilities.progressPercentage(bytesRead, bytesTotal));
		this.setProgressString(TransferDialogUtilities.computePercentCompletionText(bytesRead, bytesTotal));
		this.setProgressLabelText(TransferDialogUtilities.computeProgressText(bytesRead, bytesTotal));
	}

	public void updateTransferDialogComplete()
	{
		this.setWindowTitleBarText("Complete");
		this.setProgressLabelText("File Transfer Complete");
		this.setProgressValue(100);
		this.setProgressColor(TransferProgressDialog.PROGRESS_BLUE);

		for(int i = 5; i >= 0; i--)
		{
			this.setProgressString("Dismissed in " + i + "seconds");
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
		this.setProgressColor(TransferProgressDialog.PROGRESS_RED);
		this.setProgressString("ERROR OCCURRED");
		this.setProgressLabelText("ERROR OCCURRED");
		this.setWindowTitleBarText("ERROR OCCURRED");
	}
	
	private void setProgressValue(int value)
	{
		this.progress.setValue(value);
	}
	
	private void setProgressString(String str)
	{
		this.progress.setString(str);
	}
	
	private void setProgressColor(Color color)
	{
		this.progress.setForeground(color);
	}
	
	private void setInformationLabelText(String information)
	{
		this.informationLabel.setText(information);
	}
	
	private void setSpeedLabelText(String speed)
	{
		this.speedLabel.setText(speed);
	}
	
	private void setProgressLabelText(String progress)
	{
		this.progressLabel.setText(progress);
	}
	
	private void setWindowTitleBarText(String titleBar)
	{
		this.setTitle("File Transfer - " + titleBar);
	}
}
