package webchatinterface.server.account;

import webchatinterface.AbstractIRC;
import webchatinterface.server.AbstractServer;

import java.io.*;
import java.util.ArrayList;

public class BlacklistManager
{
    public static void blacklistIPAddress(String address)
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

    /**Determines whether a given IP address is blacklisted. It tests this by comparing
     *the string parameter to the blacklist configuration file found in the temporary directory.
     *@param IP The IP address to check against the blacklist configuration file.
     *@return Returns true if the given IP address is blacklisted, false otherwise.*/
    public static boolean isBlacklisted(String IP)
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

    /**Removes all records of blacklisted IP addresses by appending an empty String to the blacklist
     *configuration file from the server temporary directory.*/
    public static void clearBlacklistRecord()
    {
        File blacklistFile = new File(AbstractIRC.SERVER_APPLCATION_DIRECTORY + "BLACKLIST.dat");
        blacklistFile.delete();
    }

    public static String[] getBlacklistedAddresses()
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
