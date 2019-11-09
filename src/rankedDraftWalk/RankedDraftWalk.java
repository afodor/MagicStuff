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
	private final static int[] REWARDS = { 50,100,200,300,450,650, 850,950};
	
	// 0 == gold, 1 == plat, 2 == Diamond
	private static final int START_RANK = 1;	
	
	private static final int NUM_PERMUTATION = 100000;
	
	private static Random random = new Random();
	
	private static class Holder
	{
		List<Integer> numGamesToMythic = new ArrayList<>();
		List<Integer> numDrafsToMythic = new ArrayList<>();
		List<Double> dust = new ArrayList<>();
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
			int goldDraft = 7;
			int dust =0;
			
			while( currentRank <= 2)
			{
				if( currentDraftWins == 7 || currentDraftLoses == 3)
				{
					dust = dust + REWARDS[currentDraftWins]+60;
					goldDraft--;
					
					if( goldDraft ==0)
					{
						goldDraft = 7;
					}
					else
					{
						dust = dust - 750;
					}
					
					numDrafts++;
					currentDraftLoses =0;
					currentDraftWins =0;
					
					goldDraft--;
					
					currentDraftLoses =0;
					currentDraftWins =0;
				}
				
				numGames++;
				
				if( random.nextDouble() <= winRate)
				{
					currentDraftWins++;
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
			h.dust.add(dust/200.0);
			
		}
		
		return h;
	}
	
	public static void main(String[] args) throws Exception
	{
		double[] winRates = {0.48, 0.5,0.52,0.54,0.55, 0.56,0.58,0.6,0.62,0.64,0.65,0.70};
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
				"c:\\MagicStuff\\walks.txt")));
		
		writer.write("winRate\tnumGamesAvg\tnumGamesSD\tnumDraftsAvg\tnumDraftsSD\tdustAvg\tdustSD\n");
		
		for( double d : winRates)
		{
			Holder h = runASim(d);

			writer.write(d + "\t" + new Avevar(h.numGamesToMythic).getAve() + "\t" + 
					new Avevar(h.numGamesToMythic).getSD() + "\t" + 
					new Avevar(h.numDrafsToMythic).getAve() + "\t" + 
					new Avevar(h.numDrafsToMythic).getSD() + "\t" + 
					new Avevar(h.dust).getAve() + "\t" + 
					new Avevar(h.dust).getSD() + "\t" + "\n"
					);
			
			System.out.println(d);
		}
		
		writer.flush();  writer.close();
	}
}
