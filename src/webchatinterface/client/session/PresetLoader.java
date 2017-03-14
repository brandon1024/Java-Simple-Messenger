package webchatinterface.client.session;

import webchatinterface.AbstractIRC;
import webchatinterface.client.AbstractClient;

import java.io.*;

public class PresetLoader
{
	public static Preset loadPreset()
	{
		File presetFile = new File(AbstractIRC.CLIENT_APPLCATION_DIRECTORY + "PRESET.dat");
		Preset loadedPreset = null;

		//---ATTEMPT TO READ PRESET FROM FILE---//
		try(ObjectInputStream presetIn = new ObjectInputStream(new FileInputStream(presetFile)))
		{
			loadedPreset = (Preset)presetIn.readObject();
		}
		catch(IOException | ClassNotFoundException e)
		{
			AbstractClient.logException(e);
		}

		return loadedPreset;
	}

	public static boolean savePreset(Preset preset)
	{
		boolean success = false;
		File presetFile = new File(AbstractIRC.CLIENT_APPLCATION_DIRECTORY + "PRESET.dat");

		//---ATTEMPT TO SAVE PRESET TO FILE---//
		try(ObjectOutputStream presetOut = new ObjectOutputStream(new FileOutputStream(presetFile)))
		{
			presetOut.writeObject(preset);
			success = true;
		}
		catch(IOException e)
		{
			AbstractClient.logException(e);
		}

		return success;
	}

	public static Session renderSession(Preset preset)
	{
		Session session = new Session();
		session.emailAddress = preset.getEmailAddress();
		session.username = preset.getUsername();
		session.password = preset.getPassword();
		session.hostAddress = preset.getHostAddress();
		session.portNumber = preset.getPort();
		session.guest = preset.isGuest();

		return session;
	}

	public static Preset renderPreset(Session session)
	{
		return new Preset(session.emailAddress, session.username, session.password, session.hostAddress, session.portNumber, session.guest);
	}
}
