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
	public static void main(String[] args) throws Exception
	{
		HashMap<String, Integer> idMap = getIDMap();
		HashMap<Integer, Holder> holderMap = getHolderMap(idMap);
		
		String testString = 
				"UTC_Log - 11-21-2019 19.15.35.log:[6531] [UnityCrossThreadLogger]<== Draft.DraftStatus {\"id\":90,\"payload\":{\"DraftId\":\"3EF1FFCA28D42BB0:QuickDraft_ELD_20191011:Draft\",\"DraftStatus\":\"Draft.PickNext\",\"PackNumber\":0,\"PickNumber\":3,\"DraftPack\":[\"70395\",\"70311\",\"70172\",\"70281\",\"70219\",\"70325\",\"70368\",\"70181\",\"70293\",\"70320\",\"70224\"],\"PickedCards\":[\"70173\",\"70254\",\"70237\"]}}";
		
		List<Integer> inPack =  getCardsInPack(testString);
		
		for( Integer i : inPack)
		{
			Holder h = holderMap.get(i);
			System.out.println(h.cardName + " " + h.avgSeenAt + " " + h.avgTakenAt);
		}
		
		List<Integer> pickedCards =getPickedCards(testString);
		
		for( Integer i : pickedCards)
		{
			Holder h = holderMap.get(i);
			System.out.println(h.cardName + " " + h.avgSeenAt + " " + h.avgTakenAt);
		}
		
	}
	
	private static List<Integer> getPickedCards(String s )
	{
		List<Integer> list = new ArrayList<>();
		s= s.substring( s.indexOf("\"PickedCards\":[\"") + 15);
		s = s.replace("]}}", "");
		
		System.out.println(s);
		
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
		double avgSeenAt;
		double avgTakenAt;
	}
	
	public static HashMap<Integer, Holder> getHolderMap(HashMap<String, Integer> idMap ) throws Exception
	{
		HashMap<Integer, Holder> map = new HashMap<>();
		
		BufferedReader reader = new BufferedReader(new FileReader(
				"C:\\mtga_Arena_tool\\ranks.txt"));
		
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
			map.put(i, h);
		}
		
		
		return map;
	}
	
	public static HashMap<String, Integer> getIDMap() throws Exception
	{
		HashMap<String, Integer> map = new HashMap<>();
		
		BufferedReader reader =new BufferedReader(new FileReader(new File(
				"C:\\mtga_Arena_tool\\MTG-Arena-Tool-master\\src\\resources\\database.json")));
		
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
