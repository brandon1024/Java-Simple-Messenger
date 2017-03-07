package webchatinterface.helpers;

public class UsernameHelper
{
    private static String[] disallowedSubstrings;

    static
    {
        UsernameHelper.disallowedSubstrings = new String[]
        {
            "fuck", "sex", "bitch", "cunt", "ass", "cock", "bastard", "shit"
        };
    }

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

        for(String profaneWord : UsernameHelper.disallowedSubstrings)
        {
            if(username.toLowerCase().contains(profaneWord))
                return false;
        }

        return true;
    }
}
