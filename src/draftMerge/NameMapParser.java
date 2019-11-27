package draftMerge;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;

public class NameMapParser
{
	public static void main(String[] args) throws Exception
	{
		HashMap<String, Integer> idMap = getIDMap();
		HashMap<Integer, Holder> holderMap = getHolderMap(idMap);
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
