package webchatinterface.client.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import webchatinterface.client.AbstractClient;

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
	/**Serial Version UID is used as a version control for the class that implements 
	  *the serializable interface.*/
	private static final long serialVersionUID = 2625912269846450722L;
	
	/**Default Color object for the JProgressBar foreground. Used to describe a transfer
	  *in progress.*/
	public static final Color PROGRESS_GREEN = new Color(0,204,0);
	
	/**Default Color object for the JProgressBar foreground. Used to describe an unsuccesssful
	  *transfer.*/
	public static final Color PROGRESS_RED = new Color(255,0,0);
	
	/**Default Color object for the JProgressBar foreground. Used to describe a paused transfer.*/
	public static final Color PROGRESS_BLUE = new Color(0, 102, 255);

	/**Master container for the dialog. Content pane for superclass. Contains the dialog components.*/
	private Container masterPane;
	
	/**The JProgressBar, used to display the progress of the transfer.*/
	private JProgressBar progress;
	
	/**Label used to display information regarding the transfer*/
	private JLabel informationLabel;
	
	/**Label used to display the speed of the transfer.*/
	private JLabel speedLabel;
	
	/**Label used to display the progress of the transfer.*/
	private JLabel progressLabel;
	
	/**Sole constructor. Constructs a new default TransferProgressDialog.
	 *By default, the JProgressBar range is 0-100, and the value is 0. The informationLabel,
	 *speedLabel and progressLabel are empty.*/
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
		
		this.masterPane = super.getContentPane();
		this.masterPane.setLayout(new BorderLayout());
		
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
		
		this.masterPane.add(this.progress, BorderLayout.PAGE_START);
		this.masterPane.add(informationContainer, BorderLayout.CENTER);
	}
	
	/**Mutator method for the JProgressBar progress value.
	  *@param value the new progress bar value*/
	public void setProgressValue(int value)
	{
		this.progress.setValue(value);
	}
	
	/**Mutator method for the minimum JProgressBar progress value.
	  *@param minValue minmimum value for the progress bar*/
	public void setProgressMin(int minValue)
	{
		this.progress.setMinimum(minValue);
	}
	
	/**Mutator method for the maximum JProgressBar progress value.
	  *@param maxValue maximum value for the progress bar*/
	public void setProgressMax(int maxValue)
	{
		this.progress.setMinimum(maxValue);
	}
	
	/**Mutator method for the JProgressBar string value.
	  *@param str the string to be displayed in the JProgressBar*/
	public void setProgressString(String str)
	{
		this.progress.setString(str);
	}
	
	/**Mutator method for the foreground color of the JProgressBar
	  *@color color the new foreground color of the JProgressBar*/
	public void setProgressColor(Color color)
	{
		this.progress.setForeground(color);
	}
	
	/**Mutator method for the information JLabel text.
	  *@param important transfer information*/
	public void setInformationLabelText(String information)
	{
		this.informationLabel.setText(information);
	}
	
	/**Mutator method for the speed JLabel text.
	  *@param speed new speedLabel text*/
	public void setSpeedLabelText(String speed)
	{
		this.speedLabel.setText(speed);
	}
	
	/**Mutator method for the speed JLabel text.
	  *@param progress new progressLabel text*/
	public void setProgressLabelText(String progress)
	{
		this.progressLabel.setText(progress);
	}
	
	/**Set a descriptive window title. The title bar text is
	  *appended to the default window title.
	  *<p>
	  *{@code super.setTitle("File Transfer - " + titleBar)}
	  *@param titlebar description of transfer to be displayed in the window
	  *title*/
	public void setWindowTitleBarText(String titleBar)
	{
		this.setTitle("File Transfer - " + titleBar);
	}
}
