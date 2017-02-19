package unit;

import static org.junit.Assert.*;

import org.junit.Test;

import util.KeyGenerator;

public class KeyGeneratorTest
{
	@Test
	public void testGenerateKey()
	{
		String ALPHANUMERIC_MIXED_CASE = "ALPHANUMERIC_MIXED_CASE";
		String ALPHANUMERIC_LOWER_CASE = "ALPHANUMERIC_LOWER_CASE";
		String ALPHANUMERIC_UPPER_CASE = "ALPHANUMERIC_UPPER_CASE";
		String NUMERIC = "NUMERIC";
		String ALPHABETIC = "ALPHABETIC";
		
		assertEquals("alphanumeric mixed case algorithm string does not match", ALPHANUMERIC_MIXED_CASE, KeyGenerator.ALPHANUMERIC_MIXED_CASE);
		assertEquals("alphanumeric lower case algorithm string does not match", ALPHANUMERIC_LOWER_CASE, KeyGenerator.ALPHANUMERIC_LOWER_CASE);
		assertEquals("alphanumeric upper case algorithm string does not match", ALPHANUMERIC_UPPER_CASE, KeyGenerator.ALPHANUMERIC_UPPER_CASE);
		assertEquals("numeric algorithm string does not match", NUMERIC, KeyGenerator.NUMERIC);
		assertEquals("alphabetic mixed case algorithm string does not match", ALPHABETIC, KeyGenerator.ALPHABETIC);
		
		try
		{
			KeyGenerator.generateKey(-1, KeyGenerator.ALPHANUMERIC_LOWER_CASE);
			fail("invalid parameter was accepted");
		}
		catch(IllegalArgumentException e){}
		catch(Exception e)
		{
			fail("an unexpected exception was thrown");
		}
		
		try
		{
			KeyGenerator.generateKey(8, "invalid algorithm");
			fail("invalid parameter was accepted");
		}
		catch(IllegalArgumentException e){}
		catch(Exception e)
		{
			fail("an unexpected exception was thrown");
		}
		
		assertTrue("test algorithm did not return the correct value", "PASS".equals(KeyGenerator.generateKey(8, "TEST")));
		
		testAlphanumericMixedCaseAlgorithm();
		testAlphanumericLowerCaseAlgorithm();
		testAlphanumericUpperCaseAlgorithm();
		testnumericAlgorithm();
		testAlphabeticAlgorithm();
	}
	
	@Test
	public void testAlphanumericMixedCaseAlgorithm()
	{
		String key1 = KeyGenerator.generateKey(256, KeyGenerator.ALPHANUMERIC_MIXED_CASE);
		String key2 = KeyGenerator.generateKey(256, KeyGenerator.ALPHANUMERIC_MIXED_CASE);
		assertTrue("two of the same key returned", !key1.equals(key2));
		assertTrue("invalid key lengths", key1.length() == 256 && key2.length() == 256);
		
		int lowercase = 0, uppercase = 0, digit = 0;
		for(int index = 0; index < key1.length(); index++)
		{
			if(Character.isDigit(key1.charAt(index)))
				digit++;
			else if(Character.isLetter(key1.charAt(index)))
			{
				if(Character.isUpperCase(key1.charAt(index)))
					uppercase++;
				else if(Character.isLowerCase(key1.charAt(index)))
					lowercase++;
				else
					fail("invalid character: " + key1.charAt(index));
			}
			else
				fail("invalid character: " + key1.charAt(index));
		}
		
		assertTrue("no digits found in key", digit > 0);
		assertTrue("no lowercase letters found in key: " + key1, lowercase > 0);
		assertTrue("no uppercase letters found in key: " + key1, uppercase > 0);
		
		for(int index = 0; index < key2.length(); index++)
		{
			if(Character.isDigit(key2.charAt(index)))
				digit++;
			else if(Character.isLetter(key2.charAt(index)))
			{
				if(Character.isUpperCase(key2.charAt(index)))
					uppercase++;
				else if(Character.isLowerCase(key2.charAt(index)))
					lowercase++;
				else
					fail("invalid character: " + key2.charAt(index));
			}
			else
				fail("invalid character: " + key2.charAt(index));
		}
		
		assertTrue("total number of characters incorrect", digit+lowercase+uppercase == 512);
		assertTrue("number of digits in key does not satisfy acceptable ranges", digit > 512/4 || digit < 512/2);
		assertTrue("number of lowercase letters in key does not satisfy acceptable ranges", lowercase > 512/4 || lowercase < 512/2);
		assertTrue("number of uppercase letters in key does not satisfy acceptable ranges", uppercase > 512/4 || uppercase < 512/2);
	}
	
	@Test
	public void testAlphanumericLowerCaseAlgorithm()
	{
		String key1 = KeyGenerator.generateKey(256, KeyGenerator.ALPHANUMERIC_LOWER_CASE);
		String key2 = KeyGenerator.generateKey(256, KeyGenerator.ALPHANUMERIC_LOWER_CASE);
		assertTrue("two of the same key returned", !key1.equals(key2));
		assertTrue("invalid key lengths", key1.length() == 256 && key2.length() == 256);
		
		int lowercase = 0, digit = 0;
		for(int index = 0; index < key1.length(); index++)
		{
			if(Character.isDigit(key1.charAt(index)))
				digit++;
			else if(Character.isLetter(key1.charAt(index)))
			{
				if(Character.isLowerCase(key1.charAt(index)))
					lowercase++;
				else
					fail("invalid character: " + key1.charAt(index));
			}
			else
				fail("invalid character: " + key1.charAt(index));
		}
		
		assertTrue("no digits found in key", digit > 0);
		assertTrue("no lowercase letters found in key: " + key1, lowercase > 0);
		
		for(int index = 0; index < key2.length(); index++)
		{
			if(Character.isDigit(key2.charAt(index)))
				digit++;
			else if(Character.isLetter(key2.charAt(index)))
			{
				if(Character.isLowerCase(key2.charAt(index)))
					lowercase++;
				else
					fail("invalid character: " + key2.charAt(index));
			}
			else
				fail("invalid character: " + key2.charAt(index));
		}
		
		assertTrue("total number of characters incorrect", digit+lowercase == 512);
		assertTrue("number of digits in key does not satisfy acceptable ranges", digit > 512/4 || digit < 512/2);
		assertTrue("number of lowercase letters in key does not satisfy acceptable ranges", lowercase > 512/4 || lowercase < 512/2);
	}
	
	@Test
	public void testAlphanumericUpperCaseAlgorithm()
	{
		String key1 = KeyGenerator.generateKey(256, KeyGenerator.ALPHANUMERIC_UPPER_CASE);
		String key2 = KeyGenerator.generateKey(256, KeyGenerator.ALPHANUMERIC_UPPER_CASE);
		assertTrue("two of the same key returned", !key1.equals(key2));
		assertTrue("invalid key lengths", key1.length() == 256 && key2.length() == 256);
		
		int uppercase = 0, digit = 0;
		for(int index = 0; index < key1.length(); index++)
		{
			if(Character.isDigit(key1.charAt(index)))
				digit++;
			else if(Character.isLetter(key1.charAt(index)))
			{
				if(Character.isUpperCase(key1.charAt(index)))
					uppercase++;
				else
					fail("invalid character: " + key1.charAt(index));
			}
			else
				fail("invalid character: " + key1.charAt(index));
		}
		
		assertTrue("no digits found in key", digit > 0);
		assertTrue("no uppercase letters found in key: " + key1, uppercase > 0);
		
		for(int index = 0; index < key2.length(); index++)
		{
			if(Character.isDigit(key2.charAt(index)))
				digit++;
			else if(Character.isLetter(key2.charAt(index)))
			{
				if(Character.isUpperCase(key2.charAt(index)))
					uppercase++;
				else
					fail("invalid character: " + key2.charAt(index));
			}
			else
				fail("invalid character: " + key2.charAt(index));
		}
		
		assertTrue("total number of characters incorrect", digit+uppercase == 512);
		assertTrue("number of digits in key does not satisfy acceptable ranges", digit > 512/4 || digit < 512/2);
		assertTrue("number of uppercase letters in key does not satisfy acceptable ranges", uppercase > 512/4 || uppercase < 512/2);
	}
	
	@Test
	public void testnumericAlgorithm()
	{
		String key1 = KeyGenerator.generateKey(256, KeyGenerator.NUMERIC);
		String key2 = KeyGenerator.generateKey(256, KeyGenerator.NUMERIC);
		assertTrue("two of the same key returned", !key1.equals(key2));
		assertTrue("invalid key lengths", key1.length() == 256 && key2.length() == 256);
		
		int digit = 0;
		for(int index = 0; index < key1.length(); index++)
		{
			if(Character.isDigit(key1.charAt(index)))
				digit++;
			else
				fail("invalid character: " + key1.charAt(index));
		}
		
		assertTrue("no digits found in key", digit > 0);
		
		for(int index = 0; index < key2.length(); index++)
		{
			if(Character.isDigit(key2.charAt(index)))
				digit++;
			else
				fail("invalid character: " + key2.charAt(index));
		}
		
		assertTrue("total number of characters incorrect", digit == 512);
		assertTrue("number of digits in key does not satisfy acceptable ranges", digit > 512/4 || digit < 512/2);
	}
	
	@Test
	public void testAlphabeticAlgorithm()
	{
		String key1 = KeyGenerator.generateKey(256, KeyGenerator.ALPHABETIC);
		String key2 = KeyGenerator.generateKey(256, KeyGenerator.ALPHABETIC);
		assertTrue("two of the same key returned", !key1.equals(key2));
		assertTrue("invalid key lengths", key1.length() == 256 && key2.length() == 256);
		
		int lowercase = 0, uppercase = 0;
		for(int index = 0; index < key1.length(); index++)
		{
			if(Character.isLetter(key1.charAt(index)))
			{
				if(Character.isUpperCase(key1.charAt(index)))
					uppercase++;
				else if(Character.isLowerCase(key1.charAt(index)))
					lowercase++;
				else
					fail("invalid character: " + key1.charAt(index));
			}
			else
				fail("invalid character: " + key1.charAt(index));
		}
		
		assertTrue("no lowercase letters found in key: " + key1, lowercase > 0);
		assertTrue("no uppercase letters found in key: " + key1, uppercase > 0);
		
		for(int index = 0; index < key2.length(); index++)
		{
			if(Character.isLetter(key2.charAt(index)))
			{
				if(Character.isUpperCase(key2.charAt(index)))
					uppercase++;
				else if(Character.isLowerCase(key2.charAt(index)))
					lowercase++;
				else
					fail("invalid character: " + key2.charAt(index));
			}
			else
				fail("invalid character: " + key2.charAt(index));
		}
		
		assertTrue("total number of characters incorrect", lowercase+uppercase == 512);
		assertTrue("number of lowercase letters in key does not satisfy acceptable ranges", lowercase > 512/4 || lowercase < 512/2);
		assertTrue("number of uppercase letters in key does not satisfy acceptable ranges", uppercase > 512/4 || uppercase < 512/2);
	}

	public boolean helperIsValidKey(String algorithm, String key)
	{
		int lowercase = 0, uppercase = 0, digit = 0;
		for(int index = 0; index < key.length(); index++)
		{
			if(Character.isDigit(key.charAt(index)))
				digit++;
			else if(Character.isLetter(key.charAt(index)))
			{
				if(Character.isUpperCase(key.charAt(index)))
					uppercase++;
				else if(Character.isLowerCase(key.charAt(index)))
					lowercase++;
				else
					return false;
			}
			else
				return false;
		}
		
		switch(algorithm)
		{
			case KeyGenerator.ALPHANUMERIC_MIXED_CASE:
				if(digit > 0 && lowercase > 0 && uppercase > 0)
					return true;
				else
					return false;
			case KeyGenerator.ALPHANUMERIC_LOWER_CASE:
				if(digit > 0 && lowercase > 0 && uppercase == 0)
					return true;
				else
					return false;
			case KeyGenerator.ALPHANUMERIC_UPPER_CASE:
				if(digit > 0 && lowercase == 0 && uppercase > 0)
					return true;
				else
					return false;
			case KeyGenerator.ALPHABETIC:
				if(digit == 0 && lowercase > 0 && uppercase > 0)
					return true;
				else
					return false;
			case KeyGenerator.NUMERIC:
				if(digit > 0 && lowercase == 0 && uppercase == 0)
					return true;
				else
					return false;
		}
		
		return false;
	}
	
	@Test
	public void testGenerate16bitKey()
	{
		helperIsValidKey(KeyGenerator.ALPHANUMERIC_MIXED_CASE, KeyGenerator.generateKey16(KeyGenerator.ALPHANUMERIC_MIXED_CASE));
		helperIsValidKey(KeyGenerator.ALPHANUMERIC_LOWER_CASE, KeyGenerator.generateKey16(KeyGenerator.ALPHANUMERIC_LOWER_CASE));
		helperIsValidKey(KeyGenerator.ALPHANUMERIC_UPPER_CASE, KeyGenerator.generateKey16(KeyGenerator.ALPHANUMERIC_UPPER_CASE));
		helperIsValidKey(KeyGenerator.NUMERIC, KeyGenerator.generateKey16(KeyGenerator.NUMERIC));
		helperIsValidKey(KeyGenerator.ALPHABETIC, KeyGenerator.generateKey16(KeyGenerator.ALPHABETIC));
	}

	@Test
	public void testGenerate32bitKey()
	{
		helperIsValidKey(KeyGenerator.ALPHANUMERIC_MIXED_CASE, KeyGenerator.generateKey32(KeyGenerator.ALPHANUMERIC_MIXED_CASE));
		helperIsValidKey(KeyGenerator.ALPHANUMERIC_LOWER_CASE, KeyGenerator.generateKey32(KeyGenerator.ALPHANUMERIC_LOWER_CASE));
		helperIsValidKey(KeyGenerator.ALPHANUMERIC_UPPER_CASE, KeyGenerator.generateKey32(KeyGenerator.ALPHANUMERIC_UPPER_CASE));
		helperIsValidKey(KeyGenerator.NUMERIC, KeyGenerator.generateKey32(KeyGenerator.NUMERIC));
		helperIsValidKey(KeyGenerator.ALPHABETIC, KeyGenerator.generateKey32(KeyGenerator.ALPHABETIC));
	}

	@Test
	public void testGenerate64bitKey()
	{
		helperIsValidKey(KeyGenerator.ALPHANUMERIC_MIXED_CASE, KeyGenerator.generateKey64(KeyGenerator.ALPHANUMERIC_MIXED_CASE));
		helperIsValidKey(KeyGenerator.ALPHANUMERIC_LOWER_CASE, KeyGenerator.generateKey64(KeyGenerator.ALPHANUMERIC_LOWER_CASE));
		helperIsValidKey(KeyGenerator.ALPHANUMERIC_UPPER_CASE, KeyGenerator.generateKey64(KeyGenerator.ALPHANUMERIC_UPPER_CASE));
		helperIsValidKey(KeyGenerator.NUMERIC, KeyGenerator.generateKey64(KeyGenerator.NUMERIC));
		helperIsValidKey(KeyGenerator.ALPHABETIC, KeyGenerator.generateKey64(KeyGenerator.ALPHABETIC));
	}

	@Test
	public void testGenerate128bitKey()
	{
		helperIsValidKey(KeyGenerator.ALPHANUMERIC_MIXED_CASE, KeyGenerator.generateKey128(KeyGenerator.ALPHANUMERIC_MIXED_CASE));
		helperIsValidKey(KeyGenerator.ALPHANUMERIC_LOWER_CASE, KeyGenerator.generateKey128(KeyGenerator.ALPHANUMERIC_LOWER_CASE));
		helperIsValidKey(KeyGenerator.ALPHANUMERIC_UPPER_CASE, KeyGenerator.generateKey128(KeyGenerator.ALPHANUMERIC_UPPER_CASE));
		helperIsValidKey(KeyGenerator.NUMERIC, KeyGenerator.generateKey128(KeyGenerator.NUMERIC));
		helperIsValidKey(KeyGenerator.ALPHABETIC, KeyGenerator.generateKey128(KeyGenerator.ALPHABETIC));
	}

	@Test
	public void testGenerate256bitKey()
	{
		helperIsValidKey(KeyGenerator.ALPHANUMERIC_MIXED_CASE, KeyGenerator.generateKey256(KeyGenerator.ALPHANUMERIC_MIXED_CASE));
		helperIsValidKey(KeyGenerator.ALPHANUMERIC_LOWER_CASE, KeyGenerator.generateKey256(KeyGenerator.ALPHANUMERIC_LOWER_CASE));
		helperIsValidKey(KeyGenerator.ALPHANUMERIC_UPPER_CASE, KeyGenerator.generateKey256(KeyGenerator.ALPHANUMERIC_UPPER_CASE));
		helperIsValidKey(KeyGenerator.NUMERIC, KeyGenerator.generateKey256(KeyGenerator.NUMERIC));
		helperIsValidKey(KeyGenerator.ALPHABETIC, KeyGenerator.generateKey256(KeyGenerator.ALPHABETIC));
	}

	@Test
	public void testGenerate512bitKey()
	{
		helperIsValidKey(KeyGenerator.ALPHANUMERIC_MIXED_CASE, KeyGenerator.generateKey512(KeyGenerator.ALPHANUMERIC_MIXED_CASE));
		helperIsValidKey(KeyGenerator.ALPHANUMERIC_LOWER_CASE, KeyGenerator.generateKey512(KeyGenerator.ALPHANUMERIC_LOWER_CASE));
		helperIsValidKey(KeyGenerator.ALPHANUMERIC_UPPER_CASE, KeyGenerator.generateKey512(KeyGenerator.ALPHANUMERIC_UPPER_CASE));
		helperIsValidKey(KeyGenerator.NUMERIC, KeyGenerator.generateKey512(KeyGenerator.NUMERIC));
		helperIsValidKey(KeyGenerator.ALPHABETIC, KeyGenerator.generateKey512(KeyGenerator.ALPHABETIC));
	}

	@Test
	public void testGenerate1024bitKey()
	{
		helperIsValidKey(KeyGenerator.ALPHANUMERIC_MIXED_CASE, KeyGenerator.generateKey1024(KeyGenerator.ALPHANUMERIC_MIXED_CASE));
		helperIsValidKey(KeyGenerator.ALPHANUMERIC_LOWER_CASE, KeyGenerator.generateKey1024(KeyGenerator.ALPHANUMERIC_LOWER_CASE));
		helperIsValidKey(KeyGenerator.ALPHANUMERIC_UPPER_CASE, KeyGenerator.generateKey1024(KeyGenerator.ALPHANUMERIC_UPPER_CASE));
		helperIsValidKey(KeyGenerator.NUMERIC, KeyGenerator.generateKey1024(KeyGenerator.NUMERIC));
		helperIsValidKey(KeyGenerator.ALPHABETIC, KeyGenerator.generateKey1024(KeyGenerator.ALPHABETIC));
	}

	@Test
	public void testGenerate2048bitKey()
	{
		helperIsValidKey(KeyGenerator.ALPHANUMERIC_MIXED_CASE, KeyGenerator.generateKey2048(KeyGenerator.ALPHANUMERIC_MIXED_CASE));
		helperIsValidKey(KeyGenerator.ALPHANUMERIC_LOWER_CASE, KeyGenerator.generateKey2048(KeyGenerator.ALPHANUMERIC_LOWER_CASE));
		helperIsValidKey(KeyGenerator.ALPHANUMERIC_UPPER_CASE, KeyGenerator.generateKey2048(KeyGenerator.ALPHANUMERIC_UPPER_CASE));
		helperIsValidKey(KeyGenerator.NUMERIC, KeyGenerator.generateKey2048(KeyGenerator.NUMERIC));
		helperIsValidKey(KeyGenerator.ALPHABETIC, KeyGenerator.generateKey2048(KeyGenerator.ALPHABETIC));
	}

	@Test
	public void testGenerate4096bitKey()
	{
		helperIsValidKey(KeyGenerator.ALPHANUMERIC_MIXED_CASE, KeyGenerator.generateKey4096(KeyGenerator.ALPHANUMERIC_MIXED_CASE));
		helperIsValidKey(KeyGenerator.ALPHANUMERIC_LOWER_CASE, KeyGenerator.generateKey4096(KeyGenerator.ALPHANUMERIC_LOWER_CASE));
		helperIsValidKey(KeyGenerator.ALPHANUMERIC_UPPER_CASE, KeyGenerator.generateKey4096(KeyGenerator.ALPHANUMERIC_UPPER_CASE));
		helperIsValidKey(KeyGenerator.NUMERIC, KeyGenerator.generateKey4096(KeyGenerator.NUMERIC));
		helperIsValidKey(KeyGenerator.ALPHABETIC, KeyGenerator.generateKey4096(KeyGenerator.ALPHABETIC));
	}
}