
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class ClearWhiteSpacesLeaveWords
{
	public static void main(String[] args) throws Exception
	{
		String fileNameRead = "clustersWords.txt";
		String fileNameOutput = "clustersWords_cleaned.txt";
		
		Scanner sc = new Scanner(new File(fileNameRead));
		
		FileWriter writer = new FileWriter(fileNameOutput, true); //append
		BufferedWriter bufferedWriter = new BufferedWriter(writer);
		
		while (sc.hasNextLine())
		{
			String text = sc.nextLine();
			if (text.contains(":"))
			{
				int colonIndex = text.indexOf(":");
				// System.out.println("colonIndex: "+colonIndex);
				String clusterName = text.substring(0,colonIndex+1);
				String remainingText = text.substring(colonIndex+1);
				remainingText = remainingText.replaceAll("\\s+",",");
				remainingText = remainingText.substring(1);		
				System.out.println("clusterName: "+clusterName+" remainingText: "+remainingText);
				bufferedWriter.write(clusterName+" "+remainingText);
				bufferedWriter.newLine();
			}
			else
			{
				System.out.println(text);
				bufferedWriter.write(text);
				bufferedWriter.newLine();
			}
					
		}
		sc.close();
		bufferedWriter.close();
		writer.close();
	}
}