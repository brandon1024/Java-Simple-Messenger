package webchatinterface.server.account;

import webchatinterface.AbstractIRC;
import webchatinterface.server.AbstractServer;

import java.io.*;
import java.util.ArrayList;

public class BlacklistManager
{
    public synchronized static void blacklistIPAddress(String address)
    {
        if(address.length() == 0)
            return;

        String[] currentBlacklist = BlacklistManager.getBlacklistedAddresses();
        String[] blacklistedAddresses = new String[currentBlacklist.length + 1];
        System.arraycopy(currentBlacklist, 0, blacklistedAddresses, 0, currentBlacklist.length);
        blacklistedAddresses[blacklistedAddresses.length-1] = address;

        try(FileOutputStream blackListFileOut = new FileOutputStream(AbstractIRC.SERVER_APPLCATION_DIRECTORY + "BLACKLIST.dat", false);
            ObjectOutputStream blackListOut = new ObjectOutputStream(blackListFileOut))
        {
            for(String addressOut : blacklistedAddresses)
                blackListOut.writeObject(addressOut);
        }
        catch (IOException e)
        {
            AbstractServer.logException(e);
        }
    }

    public synchronized static boolean isBlacklisted(String IP)
    {
        //read from blacklist file to determine if user is blacklisted
        try(FileInputStream blackListFileIn = new FileInputStream(AbstractIRC.SERVER_APPLCATION_DIRECTORY + "BLACKLIST.dat");
            ObjectInputStream blackListIn = new ObjectInputStream(blackListFileIn))
        {
            while(true)
            {
                String IPIn;

                try
                {
                    IPIn = (String) blackListIn.readObject();
                }
                catch (EOFException e)
                {
                    AbstractServer.logException(e);
                    return false;
                }

                if(IPIn.charAt(0) == '*')
                    continue;

                if(IPIn.equals(IP))
                    return true;
            }
        }
        catch(Exception e)
        {
            AbstractServer.logException(e);
        }

        return false;
    }

    public synchronized static void clearBlacklistRecord()
    {
        File blacklistFile = new File(AbstractIRC.SERVER_APPLCATION_DIRECTORY + "BLACKLIST.dat");
        blacklistFile.delete();
    }

    public synchronized static String[] getBlacklistedAddresses()
    {
        ArrayList<String> addresses = new ArrayList<String>();

        try(FileInputStream blackListFileIn = new FileInputStream(AbstractIRC.SERVER_APPLCATION_DIRECTORY + "BLACKLIST.dat");
            ObjectInputStream blackListIn = new ObjectInputStream(blackListFileIn))
        {
            while(true)
            {
                String address = (String) blackListIn.readObject();
                addresses.add(address);
            }
        }
        catch(Exception e)
        {
            AbstractServer.logException(e);
        }

        return addresses.toArray(new String[0]);
    }
}
