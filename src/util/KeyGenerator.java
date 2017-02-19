package util;

import java.util.Random;

/**@author Brandon Richardson
 *@version 1.4.3
 *@since 06/05/2016
  *<p>
  *The {@code KeyGenerator} class provides methods for generating alphanumeric keys. The methods in
  *the {@code KeyGenerator} class rely on java.util.Random to generate random sequences of numbers
  *and letters of a desired key length. The class is designed to be used for generating unique keys 
  *for representing client connections, or for temporary file naming.
  *<p>
  *Note: {@code KeyGenerator} is not designed to generate secure keys for security purposes. See 
  *javax.crypto.KeyGenerator or java.security.SecureRandom for generating secure random symmetric keys.
  */

public final class KeyGenerator
{
	/**Algorithm used to generate an alphanumeric mixed case key*/
	public static final String ALPHANUMERIC_MIXED_CASE = "ALPHANUMERIC_MIXED_CASE";
	
	/**Algorithm used to generate an alphanumeric lower case key*/
	public static final String ALPHANUMERIC_LOWER_CASE = "ALPHANUMERIC_LOWER_CASE";
	
	/**Algorithm used to generate an alphanumeric upper case key*/
	public static final String ALPHANUMERIC_UPPER_CASE = "ALPHANUMERIC_UPPER_CASE";
	
	/**Algorithm used to generate an numeric case key*/
	public static final String NUMERIC = "NUMERIC";
	
	/**Algorithm used to generate an alphabetic mixed case key*/
	public static final String ALPHABETIC = "ALPHABETIC";
	
	/**Algorithm used to generate an alphabetic mixed case key*/
	private static final String TEST = "TEST";
	
	/**Generates a new key given the desired key length and algorithm.
	  *@param keySize the length of the key to be generated, in bits. The length of the key in characters 
	  *will be n = keySize, where n is an integer.
	  *@param algorithm the algorithm to be used when generating the key, as specified by the static members
	  *of this class. Each algorithm produces a different type of key.
	  *@return a new key of length log2(keySize) using the specified algorithm.*/
	public static String generateKey(int keySize, String algorithm)
	{
		if(keySize < 0)
			throw new IllegalArgumentException("key length must be non negative");
		if(!algorithm.equalsIgnoreCase(ALPHANUMERIC_MIXED_CASE) && 
				!algorithm.equalsIgnoreCase(ALPHANUMERIC_LOWER_CASE) && 
				!algorithm.equalsIgnoreCase(ALPHANUMERIC_UPPER_CASE) && 
				!algorithm.equalsIgnoreCase(NUMERIC) && 
				!algorithm.equalsIgnoreCase(ALPHABETIC) &&
				!algorithm.equalsIgnoreCase(TEST))
			throw new IllegalArgumentException("unrecognized code generator algorithm");
		
		Random charTypeChooser = new Random();
		Random numberChooser = new Random();
		Random characterChooser = new Random();
		Random caseChooser = new Random();
		
		char[] key = new char[keySize];
		
		if(KeyGenerator.ALPHABETIC.equalsIgnoreCase(algorithm))
		{
			for(int pos = 0; pos < keySize; pos++)
			{
				if(caseChooser.nextInt(2) == 0)
					key[pos] = (char)(characterChooser.nextInt(26) + 65);
				else
					key[pos] = (char)(characterChooser.nextInt(26) + 97);
			}
		}
		else if(KeyGenerator.NUMERIC.equalsIgnoreCase(algorithm))
		{
			for(int pos = 0; pos < keySize; pos++)
			{
				key[pos] = (char)(48 + numberChooser.nextInt(10));
			}
		}
		else if(KeyGenerator.ALPHANUMERIC_LOWER_CASE.equalsIgnoreCase(algorithm))
		{
			for(int pos = 0; pos < keySize; pos++)
			{
				if(charTypeChooser.nextInt(2) == 0)
					key[pos] = (char)(characterChooser.nextInt(26) + 97);
				else
					key[pos] = (char)(48 + numberChooser.nextInt(10));
			}
		}
		else if(KeyGenerator.ALPHANUMERIC_UPPER_CASE.equalsIgnoreCase(algorithm))
		{
			for(int pos = 0; pos < keySize; pos++)
			{
				if(charTypeChooser.nextInt(2) == 0)
				{
					key[pos] = (char)(characterChooser.nextInt(26) + 65);
				}
				else
				{
					key[pos] = (char)(48 + numberChooser.nextInt(10));
				}
			}
		}
		else if(KeyGenerator.ALPHANUMERIC_MIXED_CASE.equalsIgnoreCase(algorithm))
		{
			for(int pos = 0; pos < keySize; pos++)
			{
				if(charTypeChooser.nextInt(2) == 0)
				{
					if(caseChooser.nextInt(2) == 0)
						key[pos] = (char)(characterChooser.nextInt(26) + 65);
					else
						key[pos] = (char)(characterChooser.nextInt(26) + 97);
				}
				else
				{
					key[pos] = (char)(48 + numberChooser.nextInt(10));
				}
			}
		}
		else if(KeyGenerator.TEST.equalsIgnoreCase(algorithm))
		{
			return new String("PASS");
		}
		
		return new String(key);
	}
	
	/**Generates a new alphanumeric key of length 16.
	  *@return new alphanumeric key*/
	public static String generateKey16(String algorithm)
	{
		return KeyGenerator.generateKey(16, algorithm);
	}
	
	/**Generates a new alphanumeric key of length 32.
	  *@return new alphanumeric key*/
	public static String generateKey32(String algorithm)
	{
		return KeyGenerator.generateKey(32, algorithm);
	}
	
	/**Generates a new alphanumeric key of length 64.
	  *@return new alphanumeric key*/
	public static String generateKey64(String algorithm)
	{
		return KeyGenerator.generateKey(64, algorithm);
	}
		
	/**Generates a new alphanumeric key of length 128.
	  *@return new alphanumeric key*/
	public static String generateKey128(String algorithm)
	{
		return KeyGenerator.generateKey(128, algorithm);
	}
	
	/**Generates a new alphanumeric key of length 256.
	  *@return new alphanumeric key*/
	public static String generateKey256(String algorithm)
	{
		return KeyGenerator.generateKey(256, algorithm);
	}
	
	/**Generates a new alphanumeric key of length 512.
	  *@return new alphanumeric key*/
	public static String generateKey512(String algorithm)
	{
		return KeyGenerator.generateKey(512, algorithm);
	}
	
	/**Generates a new alphanumeric key of length 1024.
	  *@return new alphanumeric key*/
	public static String generateKey1024(String algorithm)
	{
		return KeyGenerator.generateKey(1024, algorithm);
	}
	
	/**Generates a new alphanumeric key of length 2048.
	  *@return new alphanumeric key*/
	public static String generateKey2048(String algorithm)
	{
		return KeyGenerator.generateKey(2048, algorithm);
	}
	
	/**Generates a new alphanumeric key of length 4096.
	  *@return new alphanumeric key*/
	public static String generateKey4096(String algorithm)
	{
		return KeyGenerator.generateKey(4096, algorithm);
	}
}