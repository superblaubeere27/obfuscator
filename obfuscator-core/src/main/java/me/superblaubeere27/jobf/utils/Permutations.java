package me.superblaubeere27.jobf.utils;

import java.util.ArrayList;
import java.util.Comparator;

public class Permutations
{
	private String[] dict;
	private int num;
	
	private ArrayList<String> current;
	
	public Permutations(String[] dict, int num)
	{
		this.dict = dict;
		this.num = num;
	}
	
	public ArrayList<String> get()
	{
		current = new ArrayList<>();
		
		get(dict, "", dict.length, num);
		
		current.sort(AlphabeticComparator);
		current.sort(StrLengthComparator);
		return current;
	}
	
	private void get(String[] set, String prefix, int n, int k)
	{
		if (k == 0)
		{
			current.add(prefix);
			return;
		}
		
		for (int i = 0; i < n; ++i)
		{
			String newPrefix = prefix + set[i];
			
			get(set, newPrefix, n, k - 1);
		}
	}
	
	private static Comparator<String> StrLengthComparator = new Comparator<String>()
	{
		public int compare(String h1, String h2) { return Integer.compare(h1.length(), h2.length()); }
	};
	
	private static Comparator<String> AlphabeticComparator = new Comparator<String>()
	{
		public int compare(String h1, String h2)
		{
			return h1.compareTo(h2);
		}
	};
}