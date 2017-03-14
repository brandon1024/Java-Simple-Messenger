package webchatinterface.helpers;

public class PasswordHelper
{
	public static boolean isValidPassword(char[] password)
	{
		if(password == null)
			return false;
		if(password.length == 0)
			return false;
		if(password.length < 6)
			return false;
		if(!new String(password).matches("\\A\\p{ASCII}*\\z"))
			return false;

		return true;
	}

	public static int passwordStrength(char[] password)
	{
		int score = 0;
		score += password.length;

		for(char character : password)
		{
			if(Character.isDigit(character))
				score += 2;
			else if(Character.isLowerCase(character))
				score += 1;
			else if(Character.isUpperCase(character))
				score += 2;
			else
				score += 3;
		}

		return score;
	}
}
