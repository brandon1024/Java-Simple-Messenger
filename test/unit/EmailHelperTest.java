package unit;

import org.junit.Test;
import webchatinterface.helpers.EmailHelper;
import static org.junit.Assert.*;

public class EmailHelperTest
{
	@Test
	public void isValidEmailAddress() throws Exception
	{
		String[] validEmailAddresses = {
		"prettyandsimple@example.com",
		"(comment)prettyandsimple@example.com",
		"prettyandsimple(comment)@example.com",
		"very.common@example.com",
		"disposable.style.email.with+symbol@example.com",
		"other.email-with-dash@example.com",
		"x@example.com",
		"\"much.more unusual\"@example.com",
		"\"very.unusual.@.unusual.com\"@example.com",
		"\"very.(),:;<>[]\".VERY.\"very@\\ \"very\".unusual\"@strange.example.com",
		"example-indeed@strange-example.com",
		"admin@mailserver1",
		"#!$%&'*+-/=?^_`{}|~@example.org",
		"\"()<>[]:,;@\\\"!#$%&'-/=?^_`{}| ~.a\"@example.org",
		"\" \"@example.org",
		"example@localhost",
		"example@s.solutions",
		"user@localserver",
		"user@tt",
		"user@[IPv6:2001:DB8::1]",
		"email@domain.com",
		"firstname.lastname@domain.com",
		"email@123.123.123.123",
		"email@[123.123.123.123]",
		"\"email\"@domain.com",
		"1234567890@domain.com",
		"email@domain-one.com",
		"_______@domain.com",
		"email@domain.name",
		"email@domain.co.jp",
		"email@(comment)domain.com",
		"email@domain.com(comment)",
		"firstname-lastname@domain.com",
		"\"a\"1111111111111111111111111111111111111111111111111111111111111@test.com"};

		String[] invalidEmailAddresses = {
		"ABC.example.com",
		"A@b@c@example.com",
		"a\"b(c)d,e:f;g<h>i[j\\k]l@example.com",
		"just\"not\"right@example.com",
		"this is\"not\\allowed@example.com",
		"this\\ still\\\"not\\\\allowed@example.com",
		"1234567890123456789012345678901234567890123456789012345678901234+x@example.com",
		"john.doe@example..com",
		" prettyandsimple@example.com",
		"prettyandsimple@example.com ",
		"plainaddress",
		"#@%^%#$@#$@#.com",
		"@domain.com",
		"Joe Smith <email@domain.com>",
		"email.domain.com",
		"email@domain@domain.com",
		".email@domain.com",
		"email.@domain.com",
		"あいうえお@domain.com",
		"email@domain.com (Joe Smith)",
		"email@-domain.com",
		"email@domain.com-",
		"email@domain..com"};

		assertFalse("passing null argument should return false", EmailHelper.isValidEmailAddress(null));
		assertFalse("passing empty string argument should return false", EmailHelper.isValidEmailAddress(""));

		for(String address : validEmailAddresses)
			assertTrue("should be a valid address: " + address, EmailHelper.isValidEmailAddress(address));

		for(String address : invalidEmailAddresses)
			assertFalse("should be an invalid address: " + address, EmailHelper.isValidEmailAddress(address));
	}
}