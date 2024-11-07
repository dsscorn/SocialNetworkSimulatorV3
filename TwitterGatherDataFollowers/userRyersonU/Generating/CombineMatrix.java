// package TwitterGatherDataFollowers.userRyersonU;
//Average parameters generation


import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Scanner;

public class CombineMatrix {

	

		
	private static LinkedHashMap<String,Double> userTfidfVector; //each user tfidf vector
	private static LinkedHashMap<String,LinkedHashMap<String,Double>> allUserTfidfVectors = new LinkedHashMap<String,LinkedHashMap<String,Double>>(); //all the users tfidf vectors

	private static LinkedHashMap<String,Double> usersShape = new LinkedHashMap<String,Double>(); //shape parameter for user
	private static LinkedHashMap<String,Double> usersScale = new LinkedHashMap<String,Double>(); //scale parameter for user
		
	
	private static LinkedHashMap<String,Double> averageWordsTfidf = new LinkedHashMap<String,Double>();
	
	
	private static ArrayList<String> allWordsInSet = new ArrayList<String>();
	private static ArrayList<String> allUsers = new ArrayList<String>();
	private static ArrayList<Integer> usersInMatrix = new ArrayList<Integer>();
	
	private static LinkedHashMap<Double,ArrayList<String>> averageWordBins = new LinkedHashMap<Double,ArrayList<String>>();
	private static double averageUserShape;
	private static double averageUserScale;
	
	// private static String pathToWordsText = "words_424k.txt";
	// private static String pathToWordsText = "words_14k.txt";
	private static String pathToWordsText = "words_1k.txt"; //outdated

	// private static String pathToUserParameters = "user_parameters_1k.txt"; //gen3
	// private static String pathToTfidfMatrix = "tfidf_matrix_1k.txt"; //gen3
	
	
	// private static String pathToUserParameters = "user_parameters_RyersonU_SMALL.txt"; //gen3
	// private static String pathToTfidfMatrix = "RyersonU_tfidf_matrix_SMALL.txt"; //gen3
	
	// private static String pathToUserParameters = "user_parameters_TheCatTweeting_SMALL.txt"; //gen3
	// private static String pathToTfidfMatrix = "TheCatTweeting_tfidf_matrix_SMALL.txt"; //gen3
	
	private static String[] pathToUserParameters = {"user_parameters_TorontoStar_SMALL.txt","user_parameters_RyersonU_SMALL.txt","user_parameters_TheCatTweeting_SMALL.txt"}; //gen3
	private static String[] pathToTfidfMatrix = {"TorontoStar_tfidf_matrix_SMALL.txt","RyersonU_tfidf_matrix_SMALL.txt","TheCatTweeting_tfidf_matrix_SMALL.txt"}; //gen3
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {
			
			readInGeneratingParameters();
			readInAllPossibleWords(); //Read all words from matrices
			createZeroTfidfVectors();//Create tf-idf vectors for all users containing all words with tf-idf 0 as default
			readInTfidfMatrix(); //fill in tf-idf vectors

		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


		// String fileName = "RyersonU_SMALL_cleaned";
		// String fileName = "TheCatTweeting_SMALL_cleaned";
		// String fileName = "TorontoStar_SMALL_cleaned";
		
		// String outputType = ".txt";
		
		// File textFile = new File("demoFolder_Data_cleaned.txt"); //read in cleaned dataset 14k
		// File textFile = new File("demoTweetsVerification1000_Tweets_cleaned.txt"); //read in cleaned dataset 1k
		// File textFile = new File("SelectedTweets_Total_Tweets_cleaned.txt"); //read in cleaned dataset 424k
		
		// File textFile = new File(fileName+outputType); //read in cleaned RyersonU_SMALL_cleaned
		
	}

	
	//Read in individual user's shape scale
	public static void readInGeneratingParameters() throws FileNotFoundException
	{	
		int numUsers = 0;
		int numInMatrix = 0;
		for (int i = 0; i < pathToUserParameters.length; i++)
		{
			Scanner sc = new Scanner(new File(pathToUserParameters[i])).useDelimiter("[\t\n]");	
		
			while (sc.hasNext())
			{	
				String userName = sc.next();
				allUsers.add(userName);
				Double scaleInput = Double.parseDouble(sc.next());
				Double shapeInput = Double.parseDouble(sc.next());
				usersShape.put(userName,scaleInput);
				usersScale.put(userName,shapeInput);
				
				numInMatrix++;
				numUsers++;
				averageUserScale += scaleInput;
				averageUserShape += shapeInput;
				//			System.out.println(userName+" "+scaleInput+" "+shapeInput);
			}
			
			usersInMatrix.add(numInMatrix);
			numInMatrix = 0;
			
			sc.close();
		}
		
		averageUserScale /= numUsers;
		averageUserShape /= numUsers;
		
		System.out.println("averageUserScale: "+averageUserScale+" averageUserShape: "+averageUserShape);
		System.out.println("number of users: "+usersInMatrix);
		
	}

	public static void readInAllPossibleWords() throws FileNotFoundException
	{
		for (int i = 0; i < pathToTfidfMatrix.length; i++)
		{
			Scanner sc = new Scanner(new File(pathToTfidfMatrix[i])).useDelimiter("[\t\n]");
			//skip first line
			for (int j = 0; j < usersInMatrix.get(i); j++)
			{
				String s = sc.next();
			}

			while (sc.hasNext())
			{
				String wordInMatrix;
				sc.next(); //blank tab
				if (sc.hasNext())
				{
					wordInMatrix = sc.next();
					//				System.out.println("wordInMatrix: "+wordInMatrix);

					if (!allWordsInSet.contains(wordInMatrix))
						allWordsInSet.add(wordInMatrix);
					
					for (int k = 0; k < usersInMatrix.get(i); k++)
					{
						sc.next();
					}
					//				System.out.println();
				}
			}
			sc.close();
		}
		
		// try {
			// FileWriter writer = new FileWriter("testCombinedMatrixWords.txt", true);
			// BufferedWriter bufferedWriter = new BufferedWriter(writer);
			
			// for (String word : allWordsInSet)
			// {
				// bufferedWriter.write(word);
				// bufferedWriter.newLine();
			// }

			// bufferedWriter.close();
		// } catch (IOException e) {
			// e.printStackTrace();
		// }

		
	}
	
	public static void createZeroTfidfVectors()
	{
		double DEFAULT_ZERO = 0.0;
		for (String currentUser : allUsers)
		{
				LinkedHashMap<String,Double> userTfidfVector = new LinkedHashMap<String,Double>();
				
				for (String currentWord : allWordsInSet)
				{
					userTfidfVector.put(currentWord,DEFAULT_ZERO);
				}
				allUserTfidfVectors.put(currentUser,userTfidfVector);
		}
		
		// try {
			// FileWriter writer = new FileWriter("testCombinedMatrixVectors.txt", true);
			// BufferedWriter bufferedWriter = new BufferedWriter(writer);
			
			// for (String user: allUserTfidfVectors.keySet())
			// {
				// bufferedWriter.write("User: "+user+"\t");
				// for (String word: allUserTfidfVectors.get(user).keySet())
				// {
					// bufferedWriter.write(word+": "+allUserTfidfVectors.get(user).get(word)+" ");
				// }
				// bufferedWriter.newLine();
			// }
			
			// bufferedWriter.close();

		// } catch (IOException e) {
			// e.printStackTrace();
		// }
	}
	
	//read in the tf-idf matrix
	public static void readInTfidfMatrix() throws FileNotFoundException
	{
		for (int i = 0; i < pathToTfidfMatrix.length; i++)
		{
			Scanner sc = new Scanner(new File(pathToTfidfMatrix[i])).useDelimiter("[\t\n]");
			ArrayList<String> userNamesIndex = new ArrayList<String>();
			for (int j = 0; j < usersInMatrix.get(i); j++)
			{
				String s = sc.next();
				userNamesIndex.add(s);
				//			System.out.println("s: "+s);
			}

			while (sc.hasNext())
			{
				String wordInMatrix;
				sc.next(); //blank tab
				if (sc.hasNext())
				{
					wordInMatrix = sc.next();
					//				System.out.println("wordInMatrix: "+wordInMatrix);
					
					for (String currUser : userNamesIndex)
					{
						Double tfidfValue = Double.parseDouble(sc.next());
						//					System.out.print(tfidfValue+"\t");

						if (allUserTfidfVectors.containsKey(currUser))
						{
							userTfidfVector = allUserTfidfVectors.get(currUser);
						}
						else
						{
							userTfidfVector = new LinkedHashMap<String,Double>(); 
						}

						userTfidfVector.put(wordInMatrix,tfidfValue);
						allUserTfidfVectors.put(currUser, userTfidfVector);
					}
					//				System.out.println();
				}
			}
			sc.close();
		}
		
		// System.out.println("OK ALL USER TFIDF VECTORS: "+allUserTfidfVectors);
		
		try {
			FileWriter writer = new FileWriter("testCombinedMatrix_avg_SMALL.txt", true);
			BufferedWriter bufferedWriter = new BufferedWriter(writer);
			
			bufferedWriter.write("\t");
			for (String user : allUserTfidfVectors.keySet())
			{
				bufferedWriter.write(user+"\t");
			}
			bufferedWriter.newLine();
			
			for (String uniqueTerm: allWordsInSet)
			{
				bufferedWriter.write(uniqueTerm+"\t");
				for (String userNames : allUserTfidfVectors.keySet())
				{
					double tfidfValue = 0.0;
					if (allUserTfidfVectors.get(userNames).containsKey(uniqueTerm))
						tfidfValue = allUserTfidfVectors.get(userNames).get(uniqueTerm);
					bufferedWriter.write(tfidfValue+"\t");
				}
				bufferedWriter.newLine();
			}
			
			bufferedWriter.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//		System.out.println(userNamesIndex);
		//		
		//		for (String currWord : allUserTfidfVectors.get(userNamesIndex.get(0)).keySet())
		//		{
		//			for (int i = 0; i < userNamesIndex.size(); i++)
		//			{
		//				if (i == 0)
		//					System.out.print(currWord+"\t");
		//				
		//				System.out.print(allUserTfidfVectors.get(userNamesIndex.get(i)).get(currWord)+"\t");
		//			}
		//			System.out.println();
		//		}
	}
}

