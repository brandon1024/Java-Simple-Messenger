package webchatinterface.client.ui.dialog;

import webchatinterface.client.ui.WebChatClientGUI;
import webchatinterface.client.util.ResourceLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;

public class NewAccountDialog extends JDialog
{
    private JTextField hostAddressField;
    private JTextField portField;
    private JTextField emailAddressField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JLabel passwordStrengthLabel;
    private int exitCode;

    public NewAccountDialog(WebChatClientGUI parent)
    {
        super(parent, "Create New Account", Dialog.ModalityType.DOCUMENT_MODAL);
        super.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        super.setSize(350, 300);
        super.setResizable(false);
        super.setLocationRelativeTo(parent);
        super.setIconImage(ResourceLoader.getInstance().getFrameIcon());
        this.init();
    }

    private void init()
    {
        this.hostAddressField = new JTextField(25);
        this.portField = new JTextField(25);
        this.emailAddressField = new JTextField(25);
        this.usernameField = new JTextField(25);
        this.passwordField = new JPasswordField(25);
        this.confirmPasswordField = new JPasswordField(15);
        this.passwordStrengthLabel = new JLabel();

        this.passwordStrengthLabel.setText("Strength: Poor");
        this.passwordStrengthLabel.setForeground(Color.RED);
        this.passwordStrengthLabel.setToolTipText("Password must be a minimum of 6 characters. Valid characters include a-z, A-Z, 0-9, or any !\"#$%&\'()*+,-./:;<=>?@[\\]^_`{|}~");


        JPanel inputPane = new JPanel();
        inputPane.setLayout(new BoxLayout(inputPane, BoxLayout.PAGE_AXIS));

        inputPane.add(new JLabel("Host Address:"));
        inputPane.add(this.hostAddressField);
        inputPane.add(new JLabel("Port Number:"));
        inputPane.add(this.portField);
        inputPane.add(new JLabel("Email Address:"));
        inputPane.add(this.emailAddressField);
        inputPane.add(new JLabel("New Username:"));
        inputPane.add(this.usernameField);
        inputPane.add(new JLabel("New Password:"));
        inputPane.add(this.passwordField);
        inputPane.add(new JLabel("Confirm Password:"));
        inputPane.add(this.confirmPasswordField);
        inputPane.add(this.passwordStrengthLabel);

        this.passwordField.addKeyListener(new KeyListener()
        {
            public void keyPressed(KeyEvent arg0){}
            public void keyReleased(KeyEvent arg0)
            {
                if(NewAccountDialog.this.passwordField.getPassword().length <= 6)
                {
                    NewAccountDialog.this.passwordStrengthLabel.setText("Strength: Poor");
                    NewAccountDialog.this.passwordStrengthLabel.setForeground(Color.RED);
                }
                else if(NewAccountDialog.this.passwordField.getPassword().length <= 8)
                {
                    NewAccountDialog.this.passwordStrengthLabel.setText("Strength: Moderate");
                    NewAccountDialog.this.passwordStrengthLabel.setForeground(Color.BLUE);
                }
                else if(NewAccountDialog.this.passwordField.getPassword().length <= 10)
                {
                    NewAccountDialog.this.passwordStrengthLabel.setText("Strength: Good");
                    NewAccountDialog.this.passwordStrengthLabel.setForeground(Color.GREEN);
                }
                else if(NewAccountDialog.this.passwordField.getPassword().length > 10)
                {
                    NewAccountDialog.this.passwordStrengthLabel.setText("Strength: Great");
                    NewAccountDialog.this.passwordStrengthLabel.setForeground(Color.GREEN);
                }
            }
            public void keyTyped(KeyEvent arg0){}
        });

        this.confirmPasswordField.addKeyListener(new KeyListener()
        {
            public void keyPressed(KeyEvent arg0){}
            public void keyReleased(KeyEvent arg0)
            {
                if(Arrays.equals(NewAccountDialog.this.passwordField.getPassword(), NewAccountDialog.this.confirmPasswordField.getPassword()))
                {
                    NewAccountDialog.this.passwordStrengthLabel.setText("Passwords Match");
                    NewAccountDialog.this.passwordStrengthLabel.setForeground(Color.GREEN);
                }
                else
                {
                    NewAccountDialog.this.passwordStrengthLabel.setText("Passwords do not Match");
                    NewAccountDialog.this.passwordStrengthLabel.setForeground(Color.RED);
                }
            }
            public void keyTyped(KeyEvent arg0){}
        });

        JButton submit = new JButton("OK");
        JButton cancel = new JButton("Cancel");

        submit.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                NewAccountDialog.this.exitCode = 1;
                NewAccountDialog.this.dispose();
            }
        });

        cancel.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                NewAccountDialog.this.exitCode = 2;
                NewAccountDialog.this.dispose();
            }
        });

        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new FlowLayout());
        optionsPanel.add(submit);
        optionsPanel.add(cancel);

        JTextArea policy = new JTextArea(6,25);
        policy.setWrapStyleWord(true);
        policy.setLineWrap(true);
        policy.setOpaque(false);
        policy.setEditable(false);
        policy.setFocusable(false);
        policy.setFont(new Font("Arial", Font.PLAIN, 11));

        String policyText = "Account passwords are stored using secure salted password hashing with SHA-256 level cryptography. "
                + "This means passwords are never stored in plain text. Passwords are hashed with 256-bit salts, which prevent "
                + "attackers from dictionary and brute-force attacks.";

        policy.setText(policyText);

        JPanel informationPolicy = new JPanel();
        informationPolicy.setLayout(new BorderLayout());
        informationPolicy.setPreferredSize(new Dimension(125,200));
        informationPolicy.setBorder(BorderFactory.createTitledBorder("Account Store Policy"));
        informationPolicy.add(policy, BorderLayout.CENTER);

        JPanel newAccountDialog = new JPanel();
        newAccountDialog.setLayout(new BorderLayout(10,0));
        newAccountDialog.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        newAccountDialog.add(inputPane, BorderLayout.CENTER);
        newAccountDialog.add(informationPolicy, BorderLayout.LINE_END);
        newAccountDialog.add(optionsPanel, BorderLayout.PAGE_END);

        (this.getContentPane()).add(newAccountDialog);
    }

    public int showDialog()
    {
        this.setVisible(true);
        return exitCode;
    }

    public String getHostAddress()
    {
        return this.hostAddressField.getText();
    }

    public String getPortNumber()
    {
        return this.portField.getText();
    }

    public String getUsername()
    {
        return this.usernameField.getText();
    }

    public char[] getPassword()
    {
        return this.passwordField.getPassword();
    }

    public char[] getConfirmPassword()
    {
        return this.confirmPasswordField.getPassword();
    }

    public String getEmailAddress()
    {
        return this.emailAddressField.getText();
    }
}
