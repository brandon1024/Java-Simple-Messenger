package webchatinterface.helpers;

public class EmailHelper
{
    public static boolean isValidEmailAddress(String emailAddress)
    {
        if(!emailAddress.contains("@"))
            return false;

        String local = emailAddress.substring(0, emailAddress.indexOf("@"));
        if(local.length() == 0)
            return false;

        String domain = emailAddress.substring(emailAddress.indexOf("@")+1);
        if(domain.length() < 3 || !domain.contains("."))
            return false;

        return true;
    }
}
