package webchatinterface.client.session;

import webchatinterface.AbstractIRC;
import webchatinterface.client.AbstractClient;
import webchatinterface.client.util.Preset;

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
}
