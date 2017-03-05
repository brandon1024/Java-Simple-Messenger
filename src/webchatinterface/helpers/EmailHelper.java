package webchatinterface.helpers;

public class EmailHelper
{
    //Rules:
    //1-Must Contain '@'
    //2-Local part must contain at least character
    //3-Domain part must contain at least three character
    //4-Domain part must contain '.'
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
