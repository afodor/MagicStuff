package scratch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class WhenManaScrew
{
	private static final Random r = new Random();
	public static void main(String[] args)
	{
		List<Integer> list = new ArrayList<Integer>();
		
		// -1 repesents source of the needed mana
		for( int x=0; x < 8; x++)
			list.add(-1);
		
		// -2 represents main source of mana
		for( int x=0; x < 11; x++)
			list.add(-2);
		
		// represent cards that require the needed mana
		list.add(1); list.add(1);list.add(1);list.add(1);list.add(1);
		
		while( list.size() < 40)
			list.add(0);
		
		System.out.println(list.size());
		
		double numTrials = 100000;
		double badDraw=0;
		
		for( int x=0; x < numTrials; x++)
			if( isBadDraw(list))
				badDraw++;
		
		System.out.println(badDraw/ numTrials);
	}
	
	private static boolean isBadDraw(List<Integer> list)
	{
		Collections.shuffle(list,r);
		
		List<Integer> hand =new ArrayList<Integer>();
		
		for( int x=0; x < 7; x++)
			hand.add(list.get(x));
		
		int numLands =0;
		
		for( Integer i : hand)
			if( i< 0 )
				numLands++;
		
		if( numLands < 2 )
			return true;
		
		if( ! hand.contains(-2))
			return true;
		
		int handIndex = 7;
		
		// on draw
		if( r.nextDouble() >= 0.5)
		{
			hand.add(list.get(handIndex));
			handIndex++;
		}
		
		while(handIndex < list.size())
		{
			// we have the mana
			if( hand.contains(-1) )
				return false;
			
			// we have a card we can't cast
			if( hand.contains(1))
				return true;
			
			hand.add(list.get(handIndex));
			handIndex++;
		}
		
		throw new RuntimeException("Logic error");
	}
}
