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

        for(String profaneWord : UsernameHelper.disallowedSubstrings)
        {
            if(username.toLowerCase().contains(profaneWord))
                return false;
        }

        return true;
    }
}
