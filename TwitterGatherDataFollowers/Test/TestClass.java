import java.util.Map;
import java.util.LinkedHashMap;

public class TestClass {

	public static void main(String[] args)
	{
		Map<String,Double> baseMap = new LinkedHashMap<String,Double>();
		
		baseMap.put("Hello",1.0);
		baseMap.put("Good",2.0);
		baseMap.put("Bye",3.0);
		
		Map<String,Double> mapTest1 = new LinkedHashMap<String,Double>(baseMap);
		Map<String,Double> mapTest2 = new LinkedHashMap<String,Double>(baseMap);
		
		System.out.println("INITIAL baseMap");
		for (String key : baseMap.keySet())
		{
			System.out.print(key+":"+baseMap.get(key)+",");
		}
		System.out.println();
		
		System.out.println("INITIAL mapTest1");
		for (String key : mapTest1.keySet())
		{
			System.out.print(key+":"+mapTest1.get(key)+",");
		}
		System.out.println();
		
		System.out.println("INITIAL mapTest2");
		for (String key : mapTest2.keySet())
		{
			System.out.print(key+":"+mapTest2.get(key)+",");
		}
		System.out.println();
		
		
		
		mapTest1.put("Hello",4.0);
		mapTest2.put("Hello",5.0);
		mapTest2.put("Day",6.0);
		
		System.out.println("baseMap");
		for (String key : baseMap.keySet())
		{
			System.out.print(key+":"+baseMap.get(key)+",");
		}
		System.out.println();
		
		System.out.println("mapTest1");
		for (String key : mapTest1.keySet())
		{
			System.out.print(key+":"+mapTest1.get(key)+",");
		}
		System.out.println();
		
		System.out.println("mapTest2");
		for (String key : mapTest2.keySet())
		{
			System.out.print(key+":"+mapTest2.get(key)+",");
		}
		System.out.println();
		
		System.out.println("baseMap 2");
		for (String key : baseMap.keySet())
		{
			System.out.print(key+":"+baseMap.get(key)+",");
		}
		System.out.println();
		
		System.out.println("mapTest1 2");
		for (String key : mapTest1.keySet())
		{
			System.out.print(key+":"+mapTest1.get(key)+",");
		}
		System.out.println();
		
		
		
	}

	
}