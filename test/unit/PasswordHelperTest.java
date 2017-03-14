package unit;

import org.junit.Test;
import webchatinterface.helpers.PasswordHelper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PasswordHelperTest
{
	@Test
	public void isValidPassword() throws Exception
	{
		String[] validPasswords = {"testPassword", "123Test123", "+-*/?.>,<;:'\"[]{}\\|+_()*&^%$#@!~`)]", "123456"};
		String[] invalidPasswords = {"", "12345", " ", "あinvalidcharacter"};

		assertFalse("passing null argument should return false", PasswordHelper.isValidPassword(null));

		for(String password : validPasswords)
			assertTrue("should be a valid password: " + password, PasswordHelper.isValidPassword(password.toCharArray()));

		for(String password : invalidPasswords)
			assertFalse("should be an invalid password: " + password, PasswordHelper.isValidPassword(password.toCharArray()));
	}

	@Test
	public void passwordStrength() throws Exception
	{
		assertEquals("", 3, PasswordHelper.passwordStrength("0".toCharArray()));
		assertEquals("", 6, PasswordHelper.passwordStrength("00".toCharArray()));
		assertEquals("", 9, PasswordHelper.passwordStrength("000".toCharArray()));
		assertEquals("", 2, PasswordHelper.passwordStrength("a".toCharArray()));
		assertEquals("", 4, PasswordHelper.passwordStrength("aa".toCharArray()));
		assertEquals("", 6, PasswordHelper.passwordStrength("aaa".toCharArray()));
		assertEquals("", 3, PasswordHelper.passwordStrength("A".toCharArray()));
		assertEquals("", 6, PasswordHelper.passwordStrength("AA".toCharArray()));
		assertEquals("", 9, PasswordHelper.passwordStrength("AAA".toCharArray()));
		assertEquals("", 4, PasswordHelper.passwordStrength(".".toCharArray()));
		assertEquals("", 8, PasswordHelper.passwordStrength("..".toCharArray()));
		assertEquals("", 12, PasswordHelper.passwordStrength("...".toCharArray()));
	}
}