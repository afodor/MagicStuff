package draftMerge;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;

public class NameMapParser
{
	private static File JSON_FILE = new File(
			"C:\\mtgArenaTools\\MTG-Arena-Tool-master\\src\\resources\\database.json");
	
	private static File LOG_FILE = new File(
			"C:\\Program Files (x86)\\Wizards of the Coast\\MTGA\\MTGA_Data\\Logs\\Logs");
	
	private static String DRAFT_ID ="3EF1FFCA28D42BB0:QuickDraft_WAR_20191220";
	//private static String DRAFT_ID ="3EF1FFCA28D42BB0:QuickDraft_ELD_20191011:Draft";
	//private static String DRAFT_ID ="3EF1FFCA28D42BB0:QuickDraft_M20_20191206:Draft";
	
	private static int getInitialNumber(String s)
	{
		s = s.substring(1);
		s = s.substring(0, s.indexOf("]"));
		return Integer.parseInt(s);
	}
	
	private static int getField(String inString, String key) throws Exception
	{
		String[] splits = inString.split(",");
		for(String s : splits)
		{
			s = s.replaceAll("\"", "");
			
			if( s.startsWith(key))
			{
				StringTokenizer sToken = new StringTokenizer(s,":");
				
				sToken.nextToken();
				
				return Integer.parseInt(sToken.nextToken()) + 1;
			}
		}
		
		throw new Exception("Could not find " + key +  " "  + inString);
	}
	
	public static List<String> getDraftLines() throws Exception
	{
		String[] dir = LOG_FILE.list();
		
		List<String> list = new ArrayList<>();
		
		
		for(String name : dir )
		{
			File inFile =new File(LOG_FILE.getAbsolutePath() + File.separator + name);
			
			BufferedReader reader = new BufferedReader(new FileReader(inFile));
			
			for(String s= reader.readLine(); s != null; s = reader.readLine())
			{
				if( s.indexOf(DRAFT_ID) != -1 
							&& s.indexOf("PackNumber") != -1 
								/*getInitialNumber(s) >= 741608*/ ) 
					list.add(s);
			}
			
			reader.close();
		}
		
		
		
		return list;
	}
	
	private static void writeSummaryFile(List<String> draftLines,
			HashMap<Integer, Holder> holderMap) throws Exception
	{
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
			"c:\\magicStuff\\draft3_Nov28.txt"	)));
		
		HashSet<String> inSet = new HashSet<>();
		
		for(String s : draftLines)
		{

			int pack =getField(s, "PackNumber");
			int pick =getField(s, "PickNumber");
			
			String key = pack + "@" + pick;
			
			//System.out.println("LINE " + s);
			
			if( ! inSet.contains(key))
			if( pack <3 || pick < 14)
			{
				List<Integer> inPack =  getCardsInPack(s);
				
				writer.write("pack " + pack + " pick " + pick + "\n");
				
				writer.write("name\tcolor\tseenAt\tdiff\ttakenAt\n");
				for( Integer i : inPack)
				{
					//System.out.println(i);
					Holder h = holderMap.get(i);
					
					if( h!= null)
						writer.write(h.cardName + "\t" + h.color + "\t" +  h.avgSeenAt + "\t" + (h.avgSeenAt-pick ) + "\t" +  h.avgTakenAt + "\n");
					else
						System.out.println("Skipping " + i );
				}
				
				/*
				 * 
				List<Integer> pickedCards =getPickedCards(s);
				
				for( Integer i : pickedCards)
				{
					Holder h = holderMap.get(i);
					System.out.println(h.cardName + " " + h.avgSeenAt + " " + h.avgTakenAt);
				}
				*/
				
				System.out.println( "Pack " + pack + " Pick " + pick );
				
				writer.write("\n\n");
				inSet.add(key);
			}
			
		}
		
		writer.flush();  writer.close();
	}
	
	public static void main(String[] args) throws Exception
	{
		HashMap<String, Integer> idMap = getIDMap();
	
		HashMap<Integer, Holder> holderMap = getHolderMap(idMap);
		
		List<String> draftLines = getDraftLines();
		
		System.out.println("Got list size " + draftLines.size());
		
		writeSummaryFile(draftLines, holderMap);
		
		/*
		String testString = 
				"UTC_Log - 11-21-2019 19.15.35.log:[6531] [UnityCrossThreadLogger]<== Draft.DraftStatus {\"id\":90,\"payload\":{\"DraftId\":\"3EF1FFCA28D42BB0:QuickDraft_ELD_20191011:Draft\",\"DraftStatus\":\"Draft.PickNext\",\"PackNumber\":0,\"PickNumber\":3,\"DraftPack\":[\"70395\",\"70311\",\"70172\",\"70281\",\"70219\",\"70325\",\"70368\",\"70181\",\"70293\",\"70320\",\"70224\"],\"PickedCards\":[\"70173\",\"70254\",\"70237\"]}}";
		
		
		*/
		
	}
	
	private static List<Integer> getPickedCards(String s )
	{
		List<Integer> list = new ArrayList<>();
		s= s.substring( s.indexOf("\"PickedCards\":[\"") + 15);
		s = s.replace("]}}", "");
		
		//System.out.println(s);
		
		String[] splits = s.split("\\,");
		
		for( int x=0; x < splits.length; x++)
		{
			Integer i = Integer.parseInt(splits[x].replaceAll("\"", ""));
			list.add(i);
		}
		
		//System.out.println(s);
		return list;
	}
	
	private static List<Integer> getCardsInPack(String s)
	{
		List<Integer> list = new ArrayList<>();
		s= s.substring( s.indexOf("\"DraftPack\":[") + 13);
		s=s.substring(0, s.indexOf("\"PickedCards\":")-2);
		
		String[] splits = s.split("\\,");
		
		for( int x=0; x < splits.length; x++)
		{
			Integer i = Integer.parseInt(splits[x].replaceAll("\"", ""));
			list.add(i);
		}
		
		//System.out.println(s);
		return list;
	}
	
	
	private static class Holder
	{
		String cardName;
		String color;
		double avgSeenAt;
		double avgTakenAt;
	}
	
	public static HashMap<Integer, Holder> getHolderMap(HashMap<String, Integer> idMap ) throws Exception
	{
		
		HashMap<Integer, Holder> map = new HashMap<>();
		
		BufferedReader reader = new BufferedReader(new FileReader(
				"C:\\magicStuff\\ranks_war.txt" ));
		
		reader.readLine();
		
		for(String s = reader.readLine(); s != null; s= reader.readLine())
			if( s.trim().length() > 0 )
		{
			String[] splits = s.split("\t");
			//System.out.println(s);
			String key = splits[0].replaceAll("\"", "");
			Integer i = idMap.get(key);
			
			if( i == null)
				System.out.println("Could not find " + splits[0]);
			
			Holder h = new Holder();
			h.cardName =key;
			h.avgSeenAt = Double.parseDouble(splits[4].replaceAll("\"", ""));
			h.avgTakenAt = Double.parseDouble(splits[6].replaceAll("\"", ""));
			h.color = splits[1].replaceAll("\"", "");
			map.put(i, h);
		}
		
		
		return map;
	}
	
	public static HashMap<String, Integer> getIDMap() throws Exception
	{
		HashMap<String, Integer> map = new HashMap<>();
		
		BufferedReader reader =new BufferedReader(new FileReader(
				JSON_FILE ));
		
		String aString = reader.readLine();
		
		String[] splits = aString.split("\"id\"");
		
		for( int x=1; x < splits.length; x++)
		{
			//System.out.println(splits[x]);

			Integer key = Integer.parseInt(splits[x].substring(1,splits[x].indexOf(",") ));
			int endPos = splits[x].indexOf( "\"set\"");
			String val =splits[x].substring(splits[x].indexOf(",")+8, endPos-2).replaceAll("\"", ""); 
			map.put(val,key);
		}
		
		return map;
	}
}
