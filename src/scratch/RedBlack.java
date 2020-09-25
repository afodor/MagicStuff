package scratch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RedBlack
{
	public static void main(String[] args)
	{
		List<String> list = new ArrayList<String>();
		
		for( int x=0; x < 8; x++)
			list.add("RED");
		
		for( int x=0; x < 8; x++)
			list.add("BLACK");
		
		while(list.size() < 40)
			list.add("NO");
		
		System.out.println(list.size());
		
		double numTrials = 100000;
		double success =0;
		
		for( int x=0; x < numTrials; x++)
			if( hasBoth(list))
				success++;
		
		System.out.println(success / numTrials);
	}
	
	private static boolean hasBoth( List<String> list ) 
	{
		Collections.shuffle(list);
		
		boolean hasRed = false;
		boolean hasBlack = false;
		
		for( int x=0; x< 7; x++)
		{
			if(list.get(x).equals("RED"))
				hasRed=true;
			
			if(list.get(x).equals("BLACK"))
				hasBlack=true;
		}
		
		return hasRed && hasBlack;
	}
}
