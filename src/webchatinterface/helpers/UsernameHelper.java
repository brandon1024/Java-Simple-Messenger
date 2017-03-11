package webchatinterface.helpers;

public class UsernameHelper
{
    public static boolean isValidUsername(String username)
    {
        if(username.length() == 0)
            return false;
        if(username.length() > 64)
            return false;

        for(char c : username.toCharArray())
        {
            if(c <= 32 || c >= 127)
                return false;
        }

        String[] disallowedSubstrings = {"fuck", "sex", "bitch", "cunt", "ass", "cock", "bastard", "shit"};
        for(String profaneWord : disallowedSubstrings)
        {
            if(username.toLowerCase().contains(profaneWord))
                return false;
        }

        return true;
    }
}
