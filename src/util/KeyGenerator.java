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
	
	/**Generates a new key given the desired key length and algorithm.
	  *@param keySize the length of the key to be generated, in bits. The length of the key in characters 
	  *will be n = keySize/8, where n is an integer.
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
				!algorithm.equalsIgnoreCase(ALPHABETIC))
			throw new IllegalArgumentException("unrecognized code generator algorithm");
		
		Random charTypeChooser = new Random();
		Random numberChooser = new Random();
		Random characterChooser = new Random();
		Random caseChooser = new Random();
		
		char[] key = new char[keySize/8];
		
		if(KeyGenerator.ALPHABETIC.equalsIgnoreCase(algorithm))
		{
			for(int pos = 0; pos < keySize/8; pos++)
			{
				if(caseChooser.nextInt(2) == 0)
					key[pos] = (char)(characterChooser.nextInt(26) + 65);
				else
					key[pos] = (char)(characterChooser.nextInt(26) + 97);
			}
		}
		else if(KeyGenerator.NUMERIC.equalsIgnoreCase(algorithm))
		{
			for(int pos = 0; pos < keySize/8; pos++)
			{
				key[pos] = (char)(48 + numberChooser.nextInt(10));
			}
		}
		else if(KeyGenerator.ALPHANUMERIC_LOWER_CASE.equalsIgnoreCase(algorithm))
		{
			for(int pos = 0; pos < keySize/8; pos++)
			{
				if(charTypeChooser.nextInt(2) == 0)
					key[pos] = (char)(characterChooser.nextInt(26) + 97);
				else
					key[pos] = (char)(48 + numberChooser.nextInt(10));
			}
		}
		else if(KeyGenerator.ALPHANUMERIC_UPPER_CASE.equalsIgnoreCase(algorithm))
		{
			for(int pos = 0; pos < keySize/8; pos++)
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
			for(int pos = 0; pos < keySize/8; pos++)
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
		
		return new String(key);
	}
	
	/**Generates a new 16-bit alphanumeric key.
	  *@return new 16-bit alphanumeric key*/
	public static String generate16bitKey(String algorithm)
	{
		return KeyGenerator.generateKey(16, algorithm);
	}
	
	/**Generates a new 32-bit alphanumeric key.
	  *@return new 32-bit alphanumeric key*/
	public static String generate32bitKey(String algorithm)
	{
		return KeyGenerator.generateKey(32, algorithm);
	}
	
	/**Generates a new 64-bit alphanumeric key.
	  *@return new 64-bit alphanumeric key*/
	public static String generate64bitKey(String algorithm)
	{
		return KeyGenerator.generateKey(64, algorithm);
	}
		
	/**Generates a new 128-bit alphanumeric key.
	  *@return new 128-bit alphanumeric key*/
	public static String generate128bitKey(String algorithm)
	{
		return KeyGenerator.generateKey(128, algorithm);
	}
	
	/**Generates a new 256-bit alphanumeric key.
	  *@return new 256-bit alphanumeric key*/
	public static String generate256bitKey(String algorithm)
	{
		return KeyGenerator.generateKey(256, algorithm);
	}
	
	/**Generates a new 512-bit alphanumeric key.
	  *@return new 512-bit alphanumeric key*/
	public static String generate512bitKey(String algorithm)
	{
		return KeyGenerator.generateKey(512, algorithm);
	}
	
	/**Generates a new 1024-bit alphanumeric key.
	  *@return new 1024-bit alphanumeric key*/
	public static String generate1024bitKey(String algorithm)
	{
		return KeyGenerator.generateKey(1024, algorithm);
	}
	
	/**Generates a new 2048-bit alphanumeric key.
	  *@return new 2048-bit alphanumeric key*/
	public static String generate2048bitKey(String algorithm)
	{
		return KeyGenerator.generateKey(2048, algorithm);
	}
	
	/**Generates a new 4096-bit alphanumeric key.
	  *@return new 4096-bit alphanumeric key*/
	public static String generate4096bitKey(String algorithm)
	{
		return KeyGenerator.generateKey(4096, algorithm);
	}
}