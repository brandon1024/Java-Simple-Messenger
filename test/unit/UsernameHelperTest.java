package unit;

import org.junit.Test;
import webchatinterface.helpers.UsernameHelper;
import static org.junit.Assert.*;

public class UsernameHelperTest
{
    @Test
    public void testIsValidUsername()
    {
        String[] validUsername = {"brandon1024br",
                "guest12321",
                "JohnDoeIsAwesome",
                "xxUserNameCanBeUpTo64CharactersLongSoThisUsernameShouldBeValidxx",
                "xxDestinationxx",
                "randomUser",
                "ValidChars-+*/!@#$%^&*().<>=_:;[]{}|`~\\"};

        String[] invalidUsername = {"testfuckTest",
                "testbastardsTest",
                "testsexyTest",
                "testbitchtest",
                "testcuntTest",
                "testAssTest",
                "testCocktest",
                "testShitTest",
                "xxxUserNameCanBeUpTo64CharactersLongSoThisUsernameShouldBeValidxx",
                "",
                " "};

        for(String username : validUsername)
        {
            assertTrue("Username should be valid: " + username, UsernameHelper.isValidUsername(username));
        }

        for(String username : invalidUsername)
        {
            assertFalse("Username should be invalid: " + username, UsernameHelper.isValidUsername(username));
        }
    }
}
