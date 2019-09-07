package me.superblaubeere27.jobf.utils;

import java.util.Arrays;

public class PermutationSequence
{
	private String[] dictionary;
	private String[] guess;
	
	public PermutationSequence(String[] characterSet, int guessLength)
	{
		dictionary = characterSet;
		guess = new String[guessLength];
		Arrays.fill(guess, dictionary[0]);
	}
	
	public String increment()
	{
		int index = guess.length - 1;
		while(index >= 0)
		{
			if(guess[index].equals(dictionary[dictionary.length - 1]))
			{
				if(index == 0)
				{
					guess = new String[guess.length + 1];
					Arrays.fill(guess, dictionary[0]);
					break;
				} else
				{
					guess[index] = dictionary[0];
					index--;
				}
			} else
			{
				guess[index] = dictionary[Arrays.binarySearch(dictionary, guess[index]) + 1];
				break;
			}
		}
		
		return String.join("", guess);
	}
}