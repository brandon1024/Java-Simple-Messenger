package webchatinterface.server.ui;

import java.awt.Container;
import java.awt.Dimension;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

import webchatinterface.server.AbstractServer;
import webchatinterface.server.util.AccountManager;

public class AccountListDialog
{
	public final static void displayAccountList()
	{
		JFrame accountDialog = new JFrame();
		accountDialog.setTitle("User Accounts");
		accountDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		accountDialog.setResizable(true);
		accountDialog.setSize(600,150);
		
		try
		{
			accountDialog.setIconImage(ImageIO.read(AccountManager.class.getResource("/webchatinterface/server/resources/SERVERICON.png")));
		}
		catch(IOException | IllegalArgumentException e)
		{
			AbstractServer.logException(e);
		}
		
		Container masterPane = accountDialog.getContentPane();
		
		DefaultTableModel tableModel = new DefaultTableModel();
		tableModel.addColumn("Username");
		tableModel.addColumn("Email Address");
		
		JTable userTable = new JTable(tableModel);
		userTable.setPreferredScrollableViewportSize(new Dimension(600,150));
        
        JScrollPane scrollPane = new JScrollPane(userTable);
		masterPane.add(scrollPane);
			
		userTable.setColumnSelectionAllowed(false);
		userTable.setRowSelectionAllowed(true);
		
		
		try
		{
			
			String[][] accountList = AccountManager.retrieveBasicAccountList();
			for(int n = 0; n < accountList[0].length; n++)
			{
				String[] row = new String[2];
				row[0] = new String(accountList[0][n]);
				row[1] = new String(accountList[1][n]);
				
				tableModel.addRow(row);
			}
		}
		catch (FileNotFoundException e)
		{
			
		}
		catch (ClassNotFoundException e)
		{
			
		}
		catch (IOException e)
		{
			
		}
		
		accountDialog.setVisible(true);
	}
}
