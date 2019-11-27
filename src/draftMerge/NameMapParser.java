package draftMerge;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NameMapParser
{
	private static File JSON_FILE = new File(
			"C:\\mtgArenaTools\\MTG-Arena-Tool-master\\src\\resources\\database.json");
	
	private static File LOG_FILE = new File(
			"C:\\Program Files (x86)\\Wizards of the Coast\\MTGA\\MTGA_Data\\Logs\\Logs\\UTC_Log - 11-27-2019 03.00.18.log");
	
	private static String DRAFT_ID = "3EF1FFCA28D42BB0:QuickDraft_ELD_20191011:Draft";
	
	public static List<String> getDraftLines() throws Exception
	{
		List<String> list = new ArrayList<>();
		
		BufferedReader reader = new BufferedReader(new FileReader(LOG_FILE));
		
		for(String s= reader.readLine(); s != null; s = reader.readLine())
		{
			if( s.indexOf(DRAFT_ID) != - 1 && s.indexOf("PickNumber") != -1)
				list.add(s);
		}
		
		reader.close();
		return list;
	}
	
	private static void writeSummaryFile(List<String> draftLines,
			HashMap<Integer, Holder> holderMap) throws Exception
	{
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
			"c:\\magicStuff\\aDraft_Nov27.txt"	)));
		
		int pack =1;
		int pick =1;
		
		for(String s : draftLines)
		{
			//System.out.println("LINE " + s);
			
			if( pack <3 || pick < 14)
			{
				List<Integer> inPack =  getCardsInPack(s);
				
				writer.write("pack " + pack + " pick " + pick + "\n");
				
				for( Integer i : inPack)
				{
					Holder h = holderMap.get(i);
					writer.write(h.cardName + "\t" + h.color + "\t" +  h.avgSeenAt + "\t" + h.avgTakenAt + "\n");
				}
				
				/*
				List<Integer> pickedCards =getPickedCards(s);
				
				for( Integer i : pickedCards)
				{
					Holder h = holderMap.get(i);
					System.out.println(h.cardName + " " + h.avgSeenAt + " " + h.avgTakenAt);
				}
				*/
				
				System.out.println( "Pack " + pack + " Pick " + pick );
				
				pick++;
				
				if(pick == 15)
				{
					pick =1;
					pack++;
				}
				
				writer.write("\n\n");
			}
			
		}
		
		writer.flush();  writer.close();
	}
	
	public static void main(String[] args) throws Exception
	{
		HashMap<String, Integer> idMap = getIDMap();
		HashMap<Integer, Holder> holderMap = getHolderMap(idMap);
		
		List<String> draftLines = getDraftLines();
		
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
				"C:\\magicStuff\\ranks.txt" ));
		
		reader.readLine();
		
		for(String s = reader.readLine(); s != null; s= reader.readLine())
		{
			String[] splits = s.split("\t");
			
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
