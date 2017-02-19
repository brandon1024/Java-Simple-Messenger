package webchatinterface.server.ui.preferences;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import webchatinterface.server.AbstractServer;

public class StylePreferencesPanel extends PreferencePanel
{
	private static final long serialVersionUID = 7958241337977537948L;
	
	private Color backgroundColor;
	
	private Color foregroundColor;
	
	private Font textFont;
	
	private JButton foregroundColorButton;
	
	private JButton backgroundColorButton;
	
	private JComboBox<String> fontFamilyComboBox;
	
	private JComboBox<Integer> fontSizeComboBox;
	
	private JRadioButton fontBoldRadioButton;
	
	private JRadioButton fontItalicRadioButton;
	
	private JRadioButton fontPlainRadioButton;
	
	private JRadioButton fontBoldItalicRadioButton;
	
	private JTextArea previewPane;
	
	private String[] supportedFonts;
	
	private Integer[] supportedSizes;
	
	public StylePreferencesPanel(String header)
	{
		super(header);
		this.foregroundColorButton = new JButton("TRANSPARENCY");
		this.foregroundColorButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.foregroundColorButton.setContentAreaFilled(false);
		this.foregroundColorButton.setOpaque(true);
		this.backgroundColorButton = new JButton("TRANSPARENCY");
		this.backgroundColorButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.backgroundColorButton.setContentAreaFilled(false);
		this.backgroundColorButton.setOpaque(true);
		
		this.previewPane = new JTextArea();
		this.previewPane.setRows(10);
		this.previewPane.setText("20160804_093316: 20160804_093316: Initializing Server\n" +
				"20160804_093316: 20160804_093316: Using Server IP default\n" +
				"20160804_093316: 20160804_093316: Using Port 5100\n" +
				"20160804_093316: 20160804_093316: Opening Socket on Port 5100\n" +
				"20160804_093316: 20160804_093316: Sucessfully Opened Socket on Port 5100\n" +
				"20160804_093316: 20160804_093316: Awaiting Client Connection...");
		this.previewPane.setFont(this.textFont);
		this.previewPane.setWrapStyleWord(true);
		this.previewPane.setLineWrap(true);
		
		this.backgroundColorButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				Color color = JColorChooser.showDialog(StylePreferencesPanel.this, "Choose Background Color", Color.WHITE);
				if(color != null)
				{
					StylePreferencesPanel.this.backgroundColor = color;
					StylePreferencesPanel.this.backgroundColorButton.setBackground(StylePreferencesPanel.this.backgroundColor);
					StylePreferencesPanel.this.backgroundColorButton.setForeground(StylePreferencesPanel.this.backgroundColor);
					StylePreferencesPanel.this.previewPane.setForeground(StylePreferencesPanel.this.foregroundColor);
					StylePreferencesPanel.this.previewPane.setBackground(StylePreferencesPanel.this.backgroundColor);
				}
			}
		});
		
		this.foregroundColorButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				Color color = JColorChooser.showDialog(StylePreferencesPanel.this, "Choose Foreground Color", Color.WHITE);
				if(color != null)
				{
					StylePreferencesPanel.this.foregroundColor = color;
					StylePreferencesPanel.this.foregroundColorButton.setBackground(StylePreferencesPanel.this.foregroundColor);
					StylePreferencesPanel.this.foregroundColorButton.setForeground(StylePreferencesPanel.this.foregroundColor);
					StylePreferencesPanel.this.previewPane.setForeground(StylePreferencesPanel.this.foregroundColor);
					StylePreferencesPanel.this.previewPane.setBackground(StylePreferencesPanel.this.backgroundColor);
				}
			}
		});
		
		this.supportedFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		this.fontFamilyComboBox = new JComboBox<String>(this.supportedFonts);
		
		this.supportedSizes = new Integer[16];
		for(int i = 5; i <= 20; i++)
		{
			this.supportedSizes[i-5] = new Integer(i);
		}
		this.fontSizeComboBox = new JComboBox<Integer>(this.supportedSizes);
		
		this.fontBoldRadioButton = new JRadioButton("Bold");
		this.fontItalicRadioButton = new JRadioButton("Italic");
		this.fontBoldItalicRadioButton = new JRadioButton("Bold Italic");
		this.fontPlainRadioButton = new JRadioButton("Plain");
		
		ButtonGroup group = new ButtonGroup();
		group.add(this.fontBoldRadioButton);
		group.add(this.fontItalicRadioButton);
		group.add(this.fontBoldItalicRadioButton);
		group.add(this.fontPlainRadioButton);
		
		ActionListener listener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int constval;
				if(StylePreferencesPanel.this.fontBoldRadioButton.isSelected())
				{
					constval = Font.BOLD;
				}
				else if(StylePreferencesPanel.this.fontItalicRadioButton.isSelected())
				{
					constval = Font.ITALIC;
				}
				else if(StylePreferencesPanel.this.fontBoldItalicRadioButton.isSelected())
				{
					constval = Font.ITALIC | Font.BOLD;
				}
				else
				{
					constval = Font.PLAIN;
				}
				Font font = new Font((String)fontFamilyComboBox.getSelectedItem(), constval, (int)fontSizeComboBox.getSelectedItem());
				StylePreferencesPanel.this.previewPane.setFont(font);
				StylePreferencesPanel.this.textFont = font;
			}
		};
		
		this.fontFamilyComboBox.addActionListener(listener);
		this.fontSizeComboBox.addActionListener(listener);
		this.fontBoldRadioButton.addActionListener(listener);
		this.fontItalicRadioButton.addActionListener(listener);
		this.fontBoldItalicRadioButton.addActionListener(listener);
		this.fontPlainRadioButton.addActionListener(listener);
		
		this.populatePanel();
		JPanel body = new JPanel();
		body.setLayout(new BoxLayout(body, BoxLayout.PAGE_AXIS));
		body.setBorder(BorderFactory.createEmptyBorder());
		body.add(this.buildColorSettingsPanel());
		body.add(this.buildFontSettingsPanel());
		body.add(this.buildPreviewPanel());
		
		super.add(body, BorderLayout.CENTER);
	}
	
	protected JPanel buildColorSettingsPanel()
	{
		JPanel colorSettingsPanel = new JPanel();
		colorSettingsPanel.setLayout(new BoxLayout(colorSettingsPanel, BoxLayout.PAGE_AXIS));
		colorSettingsPanel.setBorder(BorderFactory.createTitledBorder("Colors"));
		
		JPanel innerPanel = new JPanel();
		innerPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		innerPanel.add(new JLabel("Foreground Color: "));
		innerPanel.add(this.foregroundColorButton);
		colorSettingsPanel.add(innerPanel);
		
		innerPanel = new JPanel();
		innerPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		innerPanel.add(new JLabel("Background Color: "));
		innerPanel.add(this.backgroundColorButton);
		colorSettingsPanel.add(innerPanel);
		
		return colorSettingsPanel;
	}
	
	protected JPanel buildFontSettingsPanel()
	{
		JPanel fontSettingsPanel = new JPanel();
		fontSettingsPanel.setLayout(new BoxLayout(fontSettingsPanel, BoxLayout.PAGE_AXIS));
		fontSettingsPanel.setBorder(BorderFactory.createTitledBorder("Font"));
		
		JPanel innerPanel = new JPanel();
		innerPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		innerPanel.add(new JLabel("Font Name: "));
		innerPanel.add(this.fontFamilyComboBox);
		fontSettingsPanel.add(innerPanel);
		
		innerPanel = new JPanel();
		innerPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		innerPanel.add(new JLabel("Font Size: "));
		innerPanel.add(this.fontSizeComboBox);
		fontSettingsPanel.add(innerPanel);
		
		innerPanel = new JPanel();
		innerPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		innerPanel.add(this.fontBoldRadioButton);
		innerPanel.add(this.fontItalicRadioButton);
		innerPanel.add(this.fontBoldItalicRadioButton);
		innerPanel.add(this.fontPlainRadioButton);
		fontSettingsPanel.add(innerPanel);
		
		return fontSettingsPanel;
	}
	
	protected JPanel buildPreviewPanel()
	{
		JPanel previewPanel = new JPanel();
		previewPanel.setLayout(new BoxLayout(previewPanel, BoxLayout.PAGE_AXIS));
		previewPanel.setBorder(BorderFactory.createTitledBorder("Preview"));
		previewPanel.add(new JScrollPane(this.previewPane));
		
		return previewPanel;
	}

	public String[] requestChangedFields()
	{
		ArrayList<String> changedFields = new ArrayList<String>();
		
		if(!this.backgroundColor.equals(AbstractServer.backgroundColor))
		{
			changedFields.add("Background Color");
		}
		
		if(!this.foregroundColor.equals(AbstractServer.foregroundColor))
		{
			changedFields.add("Foreground Color");
		}
		
		if(!this.textFont.equals(AbstractServer.textFont))
		{
			changedFields.add("Text Font");
		}
		
		return changedFields.toArray(new String[0]);
	}
	
	public void save()
	{
		AbstractServer.backgroundColor = this.backgroundColor;
		AbstractServer.textFont = this.textFont;
	}

	protected void populatePanel()
	{
		this.backgroundColor = AbstractServer.backgroundColor;
		this.foregroundColor = AbstractServer.foregroundColor;
		this.foregroundColorButton.setBackground(this.foregroundColor);
		this.foregroundColorButton.setForeground(this.foregroundColor);
		this.backgroundColorButton.setBackground(this.backgroundColor);
		this.backgroundColorButton.setForeground(this.backgroundColor);
		
		this.previewPane.setForeground(this.foregroundColor);
		this.previewPane.setBackground(this.backgroundColor);
		
		this.textFont = AbstractServer.textFont;
		
		for(String font : this.supportedFonts)
		{
			if(font.equals(AbstractServer.textFont.getFamily()))
			{
				this.fontFamilyComboBox.setSelectedItem(font);
			}
		}
		
		for(Integer size : this.supportedSizes)
		{
			if(size.intValue() == AbstractServer.textFont.getSize())
			{
				this.fontSizeComboBox.setSelectedItem(size);
			}
		}
		
		if(AbstractServer.textFont.isBold())
		{
			this.fontBoldRadioButton.setSelected(true);
		}
		else if(AbstractServer.textFont.isItalic())
		{
			this.fontItalicRadioButton.setSelected(true);
		}
		else if(AbstractServer.textFont.isPlain())
		{
			this.fontPlainRadioButton.setSelected(true);
		}
		else
		{
			this.fontBoldItalicRadioButton.setSelected(true);
		}
	}
}
