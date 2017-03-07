package webchatinterface.server.util;

import webchatinterface.server.AbstractServer;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class FrameUtilities
{
    public static boolean setFrameIcon(JFrame frame, BufferedImage icon)
    {
        try
        {
            frame.setIconImage(icon);
        }
        catch(IllegalArgumentException e)
        {
            AbstractServer.logException(e);
            return false;
        }

        return true;
    }
}