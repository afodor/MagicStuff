package rankedDraftWalk;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import utils.Avevar;

public class RankedDraftWalk
{
	public final static String[] RANKS = { "GOLD" , "PLAT" , "DIAMOND" };
	private final static int[] RANK_SIZES = {5,5,5};
	
	// 0 == gold, 1 == plat, 2 == Diamond
	private static final int START_RANK = 2;	
	
	private static final int NUM_PERMUTATION = 100000;
	
	private static Random random = new Random();
	
	private static class Holder
	{
		List<Integer> numGamesToMythic = new ArrayList<>();
		List<Integer> numDrafsToMythic = new ArrayList<>();
	}
	
	private static Holder runASim(double winRate)
	{
		Holder h = new Holder();
		
		for( int x=0; x < NUM_PERMUTATION; x++)
		{
			int numGames =0;
			int numDrafts = 1;
			int protection =0;
			int currentLevel = 4;
			int currentWinsInLevel = 0;
			int currentDraftWins =0;
			int currentDraftLoses =0;
			int currentRank =START_RANK;
			
			while( currentRank <= 2)
			{
				numGames++;
				
				if( random.nextDouble() <= winRate)
				{
					currentDraftWins++;
					
					if( currentDraftWins == 7 )
					{
						numDrafts++;
						currentDraftLoses =0;
						currentDraftWins =0;
					}
					
					currentWinsInLevel++;
					
					if( currentWinsInLevel >= RANK_SIZES[currentRank] )
					{
						currentWinsInLevel=0;
						currentLevel--;
						protection=3;
						
						if( currentLevel ==0)
						{
							currentLevel = 4;
							currentRank++;
						}
					}
					//System.out.println("WIN " + currentRank + " " + currentLevel + " " + currentWinsInLevel);
					
				}
				else // loss
				{
					
					currentDraftLoses++;
					
					if( currentDraftLoses == 3)
					{
						numDrafts++;
						currentDraftLoses =0;
						currentDraftWins =0;
					}
					
					boolean atBottom = (currentLevel == 4 & currentWinsInLevel == 0);
					
					if( ! atBottom)
					{
						if( protection >0)
						{
							protection--;
						}
						else
						{
							currentWinsInLevel--;
							
							if( currentWinsInLevel < 0)
							{
								currentWinsInLevel =0;
								currentLevel++;
							}
						}
					}
					
					//System.out.println("LOSS " + currentRank + " " + currentLevel + " " + currentWinsInLevel);
				}
			}
			
			h.numDrafsToMythic.add(numDrafts);
			h.numGamesToMythic.add(numGames);
			
		}
		
		return h;
	}
	
	public static void main(String[] args) throws Exception
	{
		double[] winRates = {0.48, 0.5,0.52,0.54,0.55, 0.56,0.58,0.6,0.62,0.64,0.65,0.70};
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
				"c:\\MagicStuff\\walks.txt")));
		
		writer.write("winRate\tnumGamesAvg\tnumGamesSD\tnumDraftsAvg\tnumDraftsSD\n");
		
		for( double d : winRates)
		{
			Holder h = runASim(d);

			writer.write(d + "\t" + new Avevar(h.numGamesToMythic).getAve() + "\t" + 
					new Avevar(h.numGamesToMythic).getSD() + "\t" + 
					new Avevar(h.numDrafsToMythic).getAve() + "\t" + 
					new Avevar(h.numGamesToMythic).getSD() + "\n"	);
			
			System.out.println(d);
		}
		
		writer.flush();  writer.close();
	}
}
