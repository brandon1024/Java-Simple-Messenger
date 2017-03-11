package webchatinterface.helpers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailHelper
{
    public static boolean isValidEmailAddress(String emailAddress)
    {
        //---Preliminary Check---//
        if(emailAddress == null)
            return false;
        if(emailAddress.length() == 0)
            return false;
        if(emailAddress.length() > 320)
            return false;
        if(!emailAddress.contains("@"))
            return false;
        if(!emailAddress.matches("\\A\\p{ASCII}*\\z"))
            return false;

        String addressWithQuotedMarkers = emailAddress.replaceAll("\\\".*\\\"", "[QUOTED]");
        String addressWithoutQuotedMarkers = emailAddress.replaceAll("\\\".*\\\"", "");
        int removedChars = emailAddress.length() - addressWithoutQuotedMarkers.length();

        //---Ensure Only One Unquoted @ Symbol---//
        if(!addressWithoutQuotedMarkers.contains("@"))
            return false;
        if(addressWithoutQuotedMarkers.length() - addressWithoutQuotedMarkers.replace("@", "").length() > 1)
            return false;

        //---Ensure Only Valid Characters---//
        String[] invalidChars = {",", ";", "<", ">", " "};
        for(String character : invalidChars)
        {
            if(addressWithoutQuotedMarkers.contains(character))
                return false;
        }

        String localPartWithQuotedMarkers = addressWithQuotedMarkers.substring(0, addressWithQuotedMarkers.indexOf('@'));
        String localPartWithoutQuotedMarkers = addressWithoutQuotedMarkers.substring(0, addressWithoutQuotedMarkers.indexOf('@'));
        String domainPartWithQuotedMarkers = addressWithQuotedMarkers.substring(addressWithQuotedMarkers.indexOf('@')+1);

        //---Ensure Local Text Quoted Text Left Anchored, Commented Text Left/Right Anchored, No Leading/Trailing Dot---//
        String[] localPatterns = {".\\[QUOTED\\]", ".\\(.*\\).", "^\\..|.\\.$"};
        for(String pattern : localPatterns)
        {
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(localPartWithQuotedMarkers);
            if(m.find())
                return false;
        }

        //---Ensure Domain Text No Quoted Text, No Double Dot, No Leading Dash---//
        String[] domainPatterns = {	"\\[QUOTED\\]", "\\.\\.", "^-|-$"};
        for(String pattern : domainPatterns)
        {
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(domainPartWithQuotedMarkers);
            if(m.find())
                return false;
        }

        //---Ensure Valid Character Lengths---//
        if(localPartWithQuotedMarkers.length() + removedChars > 64)
            return false;
        if(localPartWithoutQuotedMarkers.length() == 0 && removedChars == 0)
            return false;
        if(domainPartWithQuotedMarkers.length() > 255)
            return false;

        //---Ensure Each DNS Label Has Valid Length---//
        String[] DNSLabel = domainPartWithQuotedMarkers.split("\\.");
        for(String label : DNSLabel)
        {
            if(label.length() >= 64)
                return false;
        }

        return true;
    }
}
