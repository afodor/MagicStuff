package draftMerge;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class IDtoNames
{
	public static HashMap<Integer,String> getIdToNameMap() throws Exception
	{
		HashMap<Integer,String> map = new LinkedHashMap<Integer, String>();
		
		BufferedReader reader = new BufferedReader(new FileReader(new File( 
			"C:\\Program Files (x86)\\Wizards of the Coast\\MTGA\\MTGA_Data\\Downloads\\Data\\data_loc_3b2cf3c5472bf8418661b4a12155d3e8.mtga"	)));
		
		String s= reader.readLine();
		
		while( s!= null)
		{
			s =s.trim().replaceAll("\"", "");
					
			if( s.startsWith("id"))
			{
				s = s.replace("id:", "").trim();
				
				s = s.substring(0, s.length()-1);
				Integer anInt = Integer.parseInt(s);
			
				if( ! map.containsKey(anInt))
				{
					String text = reader.readLine().replaceAll("\"", "").trim();
					text = text.replace("text:", "");
					map.put(anInt, text);
					
				}
				
			}
			
			s= reader.readLine();
		}
		
		reader.close();
		return map;
	}
	
	public static void main(String[] args) throws Exception
	{
		HashMap<Integer,String> aMap = getIdToNameMap();
		
		for(Integer i : aMap.keySet())
			System.out.println(i + " " + aMap.get(i));
	}
}
