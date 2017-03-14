package webchatinterface.server.ui.components.preferences;

import javax.swing.*;
import java.awt.*;

public class PanelHeaderLabel extends JLabel
{
	public PanelHeaderLabel(String text)
	{
		super(" " + text);
		this.setForeground(Color.WHITE);
		this.setFont(new Font("Courier New", Font.PLAIN, 16));
	}
	
	public void paintComponent(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		GradientPaint grad = new GradientPaint(0,0,Color.BLACK, this.getWidth(), this.getHeight(), Color.WHITE);
		g2d.setPaint(grad);
		g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
		super.paintComponent(g);
	}
}
