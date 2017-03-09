package webchatinterface.client.ui.dialog;

import webchatinterface.client.ui.WebChatClientGUI;
import webchatinterface.client.util.ResourceLoader;

import javax.swing.JDialog;
import javax.swing.WindowConstants;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import java.awt.Dialog;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class AuthenticationDialog extends JDialog
{
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField hostAddressField;
    private JTextField portField;
    private JCheckBox savePresetCheck;
    private JCheckBox loginAsGuest;
    private int exitCode;

    public AuthenticationDialog(WebChatClientGUI parent, String username, byte[] password, String hostAddress, Integer port)
    {
        super(parent, "Client Authentication", Dialog.ModalityType.DOCUMENT_MODAL);
        super.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        super.setSize(350, 160);
        super.setResizable(false);
        super.setLocationRelativeTo(parent);
        super.setIconImage(ResourceLoader.getInstance().getFrameIcon());
        this.init(username, password, hostAddress, port);
    }

    private void init(String username, byte[] password, String hostAddress, Integer port)
    {
        this.usernameField = new JTextField(15);
        this.passwordField = new JPasswordField(15);
        this.hostAddressField = new JTextField(15);
        this.portField = new JTextField(5);
        this.savePresetCheck = new JCheckBox("Save this Preset");
        this.loginAsGuest = new JCheckBox("Login as Guest");
        this.exitCode = 0;

        this.usernameField.setText((username == null) ? "" : username);
        this.passwordField.setText((password == null) ? "" : new String(password));
        this.hostAddressField.setText((hostAddress == null) ? "" : hostAddress);
        this.portField.setText(port == null ? "" : port.toString());
        this.savePresetCheck.setSelected(password == null);

        this.usernameField.addKeyListener(new KeyListener()
        {
            public void keyPressed(KeyEvent arg0){}
            public void keyReleased(KeyEvent arg0)
            {
                AuthenticationDialog.this.savePresetCheck.setSelected(true);
            }
            public void keyTyped(KeyEvent arg0){}
        });

        this.loginAsGuest.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                if(AuthenticationDialog.this.loginAsGuest.isSelected())
                {
                    AuthenticationDialog.this.usernameField.setEditable(false);
                    AuthenticationDialog.this.passwordField.setEditable(false);
                }
                else
                {
                    AuthenticationDialog.this. usernameField.setEditable(true);
                    AuthenticationDialog.this. passwordField.setEditable(true);
                }
            }
        });

        JButton submit = new JButton("OK");
        JButton cancel = new JButton("Cancel");

        submit.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                AuthenticationDialog.this.exitCode = 1;
                AuthenticationDialog.this.dispose();
            }
        });

        cancel.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                AuthenticationDialog.this.exitCode = 3;
                AuthenticationDialog.this.dispose();
            }
        });

        JPanel hostInfoPanel = new JPanel();
        hostInfoPanel.setLayout(new BoxLayout(hostInfoPanel, BoxLayout.PAGE_AXIS));
        hostInfoPanel.add(new JLabel("Host Address:"));
        hostInfoPanel.add(this.hostAddressField);
        hostInfoPanel.add(new JLabel("Port:"));
        hostInfoPanel.add(this.portField);
        hostInfoPanel.add(this.savePresetCheck);

        JPanel userInfoPanel = new JPanel();
        userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.PAGE_AXIS));
        userInfoPanel.add(new JLabel("Enter Username:"));
        userInfoPanel.add(this.usernameField);
        userInfoPanel.add(new JLabel("Enter Password:"));
        userInfoPanel.add(this.passwordField);
        userInfoPanel.add(this.loginAsGuest);

        JPanel optionPanel = new JPanel();
        optionPanel.setLayout(new FlowLayout());
        optionPanel.add(submit);
        optionPanel.add(cancel);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(1,2, 10, 0));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        inputPanel.add(hostInfoPanel);
        inputPanel.add(userInfoPanel);

        (this.getContentPane()).setLayout(new BorderLayout());
        (this.getContentPane()).add(inputPanel, BorderLayout.CENTER);
        (this.getContentPane()).add(optionPanel, BorderLayout.PAGE_END);
    }

    public int showDialog()
    {
        this.setVisible(true);
        return exitCode;
    }

    public String getUsername()
    {
        return this.usernameField.getText();
    }

    public char[] getPassword()
    {
        return this.passwordField.getPassword();
    }

    public String getHostAddress()
    {
        return this.hostAddressField.getText();
    }

    public String getPortNumber()
    {
        return this.portField.getText();
    }

    public boolean getSavePreset()
    {
        return this.savePresetCheck.isSelected();
    }

    public boolean getIsGuest()
    {
        return this.loginAsGuest.isSelected();
    }
}
