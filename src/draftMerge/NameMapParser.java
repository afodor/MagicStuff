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
		HashMap<Integer, String> map = getMap();
		
		BufferedWriter writer = new BufferedWriter(new FileWriter("c:\\temp\\test.txt"));
		
		writer.write( "id\tname\n" );
		
		for(Integer i : map.keySet())
			writer.write(i + "\t" + map.get(i) + "\n");
		
		writer.flush(); writer.close();
	}
	
	public static HashMap<Integer, String> getMap() throws Exception
	{
		HashMap<Integer, String> map = new HashMap<>();
		
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
			map.put(key,val);
		}
		
		return map;
	}
}
