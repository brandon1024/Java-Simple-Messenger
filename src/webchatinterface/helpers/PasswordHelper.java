package webchatinterface.helpers;

public class PasswordHelper
{
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
