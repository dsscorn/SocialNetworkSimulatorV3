// package TwitterGatherDataFollowers.userRyersonU;
//Average parameters generation
//Generate large amount keep proportions of user tweets and proportion to large amount total

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
import java.util.Random;
import java.util.Scanner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.math3.random.RandomDataGenerator;

public class GenerateTweetsCloseToReal4 {

	//	private static final double DEFAULT_SHAPE2 = 0.48694;
	//	private static final double DEFAULT_SCALE2 = 2.09345E-6;
	//	private static final double DEFAULT_SHAPE2 = 0.18999; //shape from dataset 14k technical report average
	//	private static final double DEFAULT_SCALE2 = 2.19E-06; //scale from dataset 14k technical report average
	private static final double DEFAULT_SHAPE2 = 0.338465; //average shape from two datasets OUTDATED
	private static final double DEFAULT_SCALE2 = 2.14173E-06; //average scale from two datasets OUTDATED
	public static final int MAX_WORDS = 7; //Maximum number of words in tweet after processing
	public static final int MIN_WORDS = 3; //Minimum number of words in tweet after processing
	// private static final int TOTAL_ORIGINAL_WORDS = 1148; //Total number of words from original corpus
	//	private static final int TOTAL_ORIGINAL_WORDS_DEMO = 30506;
	private static final int TOTAL_ORIGINAL_WORDS = 1170; //Total number of words from original corpus 1k OUTDATED
	// private static final int TOTAL_ORIGINAL_WORDS = 18732; //Total number of words from original corpus 14k
	// private static final int TOTAL_ORIGINAL_WORDS = 264501; //Total number of words from original corpus 1k


	// private static final int TOTAL_ORIGINAL_WORDS_DEMO = 18732; //total words from dataset after cleaning 14k
	// private static final int TOTAL_ORIGINAL_WORDS_DEMO = 264501; //total words from dataset after cleaning
	private static final int TOTAL_ORIGINAL_WORDS_DEMO = 1170; //total words from dataset after cleaning 1k OUTDATED
	// private static final double MIN_TFIDF = 0.000169458; //min tf-idf from dataset 
	// private static final double MAX_TFIDF = 0.066244964; //max tf-idf from dataset

	// private static final double MIN_TFIDF = 0.000169458; //min tf-idf from dataset 
	// private static final double MAX_TFIDF = 0.066244964; //max tf-idf from dataset

	private static final double MIN_TFIDF = 0; //min tf-idf from dataset 1k
	private static final double MAX_TFIDF = 0.118414337; //max tf-idf from dataset 1k OUTDATED

	// private static final double MIN_TFIDF = 0; //min tf-idf from dataset 14k
	// private static final double MAX_TFIDF = 0.185485899; //max tf-idf from dataset 14k

	// private static final double MIN_TFIDF = 0; //min tf-idf from dataset 424k
	// private static final double MAX_TFIDF = 0.088432837; //max tf-idf from dataset 424k

	private static ArrayList<String> currentBagWords = new ArrayList<String>(); //words already used in random generation

	private static Random r = new Random(); //should not construct in method, make it static
	private static RandomDataGenerator randomDataGenerator = new RandomDataGenerator(); //should not construct in method, make it static

	private static double[] topicsWordsTfidfDemo = new double[TOTAL_ORIGINAL_WORDS_DEMO];
	private static String[] topicsWordsDemo = new String[TOTAL_ORIGINAL_WORDS_DEMO];
	private static double[] topicsWordsIndexDemo = new double[TOTAL_ORIGINAL_WORDS_DEMO];


	// private static ArrayList<Double> wordsBins = new ArrayList<Double>(); //Bins for groups of tf-idf
	private static LinkedHashMap<Double,ArrayList<String>> tfidfWordsBins = new LinkedHashMap<Double,ArrayList<String>>(); //Words for each bin
	private static ArrayList<Double> wordsBins = new ArrayList<Double>(); //Bins for groups of tf-idf
	
	private static LinkedHashMap<String,Double> userTfidfVector; //each user tfidf vector
	private static LinkedHashMap<String,LinkedHashMap<String,Double>> allUserTfidfVectors = new LinkedHashMap<String,LinkedHashMap<String,Double>>(); //all the users tfidf vectors

	private static LinkedHashMap<String,Double> usersShape = new LinkedHashMap<String,Double>(); //shape parameter for user
	private static LinkedHashMap<String,Double> usersScale = new LinkedHashMap<String,Double>(); //scale parameter for user
		
	
	private static LinkedHashMap<String,LinkedHashMap<Double,ArrayList<String>>> allUserTfidfWordsBins = new LinkedHashMap<String,LinkedHashMap<Double,ArrayList<String>>>(); //All bins for each user
	
	private static LinkedHashMap<String,Double> averageWordsTfidf = new LinkedHashMap<String,Double>();
	private static ArrayList<String> allWordsInSet = new ArrayList<String>();
	private static LinkedHashMap<Double,ArrayList<String>> averageWordBins = new LinkedHashMap<Double,ArrayList<String>>();
	private static double averageUserShape;
	private static double averageUserScale;
	

	private static int totalUsers = 10; //total users from dataset 1k
	// private static int totalUsers = 14; //total users from dataset 14k
	// private static int totalUsers = 256; //total users from dataset

	// private static String pathToWordsText = "words_424k.txt";
	// private static String pathToWordsText = "words_14k.txt";
	private static String pathToWordsText = "words_1k.txt"; //outdated

	// private static String pathToUserParameters = "user_parameters_1k.txt"; //gen3
	// private static String pathToTfidfMatrix = "tfidf_matrix_1k.txt"; //gen3
	
	
	// private static String pathToUserParameters = "user_parameters_RyersonU_SMALL.txt"; //gen3
	// private static String pathToTfidfMatrix = "RyersonU_tfidf_matrix_SMALL.txt"; //gen3
	
	// private static String pathToUserParameters = "user_parameters_TheCatTweeting_SMALL.txt"; //gen3
	// private static String pathToTfidfMatrix = "TheCatTweeting_tfidf_matrix_SMALL.txt"; //gen3
	
	// private static String pathToUserParameters = "user_parameters_TorontoStar_SMALL.txt"; //gen3
	private static String pathToTfidfMatrix = "TorontoStar_tfidf_matrix_SMALL.txt"; //gen3
	
	// private static String pathToUserParameters = "user_parameters_weathernetwork_SMALL.txt"; //gen3
	// private static String pathToTfidfMatrix = "weathernetwork_tfidf_matrix_SMALL.txt"; //gen3
	
	// private static String pathToUserParameters = "user_parameters_jtimberlake_SMALL.txt"; //gen3
	// private static String pathToTfidfMatrix = "jtimberlake_tfidf_matrix_SMALL.txt"; //gen3
	
	// private static String pathToUserParameters = "user_parameters_NASA_SMALL.txt"; //gen3
	// private static String pathToTfidfMatrix = "NASA_tfidf_matrix_SMALL.txt"; //gen3
	
	private static String pathToUserParameters = "user_parameters_CombinedMatrix_avg_SMALL.txt"; //gen3
	// private static String pathToTfidfMatrix = "CombinedMatrix_avg_SMALL.txt"; //gen3

	
// 14357
// 17% ryerson 2423
// 20% cat 2872
// 63% toronto 9062
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// int originalSet = 2423; //the original total from small set ryerson
		// int originalSet = 2872; //the original total from small set cat
		int originalSet = 9062; //the original total from small set torontostar
		int originalTotal = 14357;
		
		int newTotal = 1200000; //the total in the end combining all sets
		double percentageSetFromOriginalTotal = (double) originalSet/originalTotal;
		int newSetAmount = (int) (newTotal * percentageSetFromOriginalTotal);
		int iterationsToMatchNewAmount = newSetAmount/originalSet;
		
		System.out.println("percentageSetFromOriginalTotal: "+percentageSetFromOriginalTotal+ " newSetAmount: "+ newSetAmount + " iterationsToMatchNewAmount: "+iterationsToMatchNewAmount);
		
		try {
			
			readInGeneratingParameters4();
			readInTfidfMatrix();
			// averageParameters();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// setGeneratedWordBins5();
		setGeneratedWordBins4();

		// String fileName = "RyersonU_SMALL_cleaned";
		// String fileName = "TheCatTweeting_SMALL_cleaned";
		String fileName = "TorontoStar_SMALL_cleaned";
		
		// String fileName = "weathernetwork_SMALL_cleaned";
		// String fileName = "jtimberlake_SMALL_cleaned";
		// String fileName = "NASA_SMALL_cleaned";
		
		// String fileName = "CombinedTweets_avg_SMALL";
		String outputType = ".txt";
		
		// File textFile = new File("demoFolder_Data_cleaned.txt"); //read in cleaned dataset 14k
		// File textFile = new File("demoTweetsVerification1000_Tweets_cleaned.txt"); //read in cleaned dataset 1k
		// File textFile = new File("SelectedTweets_Total_Tweets_cleaned.txt"); //read in cleaned dataset 424k
		
		File textFile = new File(fileName+outputType); //read in cleaned RyersonU_SMALL_cleaned
		
		
		
		
		
		for (int i = 0; i < iterationsToMatchNewAmount; i++)
		{
			try {
				final char END_OF_TWEET = '\r';
				int character;
				StringBuffer lineBuffer = new StringBuffer(1024);
				FileInputStream fileInput = new FileInputStream(textFile);
				BufferedInputStream bufferedInput = new BufferedInputStream(fileInput);

				String referenceUser;
				Long tweetId;
				Long userId;
				String tweetDate;
				String currentUserName;
				String tweetText;
				Tweet currentTweet;

				String generatedTweetText;

				while((character=bufferedInput.read())!=-1) {
					//FileWriter writer = new FileWriter("dummy_out.txt", true); //append
					//BufferedWriter bufferedWriter = new BufferedWriter(writer);
					//System.out.println("character: "+character);
					if (character==END_OF_TWEET) {
						character = bufferedInput.read();
						if (character!=-1 && character !='\n')
						{
							lineBuffer.append((char)character);
						}
						else if (character!=-1 && character == '\n')
						{
							// Here is where something is done with each line
							//System.out.println("lineBuffer: "+lineBuffer);
							//bufferedWriter.write("lineBuffer: "+lineBuffer);
							//bufferedWriter.newLine();
							//bufferedWriter.close();
							//linecount++;
							String info[] = lineBuffer.toString().split("\t",6);
							lineBuffer.setLength(0);

							referenceUser = info[0];
							tweetId = Long.valueOf(info[1]);
							tweetDate = info[2];
							userId = Long.valueOf(info[3]);
							currentUserName = info[4];
							tweetText = info[5];

							if (currentUserName.contains("generated"))
							{
								tweetId+=2;
							}
							else if (currentUserName.contains("simulated"))
							{
								tweetId++;
							}
							
							tweetId+=i;

							currentTweet = new Tweet(tweetText,tweetId,tweetDate,currentUserName);


							System.out.println(referenceUser+"\t"+currentTweet.getTweetId()+"\t"+currentTweet.getDateString()+"\t"+userId+"\t"+currentTweet.getUser()+"\t"+currentTweet.getTweetText());
							//						generatedTweetText = generateTweetText2();
							//						generatedTweetText = generateTweetText3();
							// generatedTweetText = generateTweetText5(currentUserName);
							generatedTweetText = generateTweetText4(currentUserName);

							if (currentUserName.contains("generated"))
							{
								tweetId+=2;
							}
							else if (currentUserName.contains("simulated"))
							{
								tweetId++;
							}

							// System.out.println(referenceUser+"_generated\t"+currentTweet.getTweetId()+"\t"+currentTweet.getDateString()+"\t"+userId+"\t"+currentTweet.getUser()+"_generated\t"+generatedTweetText);
							System.out.println(referenceUser+"\t"+currentTweet.getTweetId()+"\t"+currentTweet.getDateString()+"\t"+userId+"\t"+currentTweet.getUser()+"\t"+generatedTweetText);
							try {
								// FileWriter writer = new FileWriter("demoFolder_Data_cleaned_generated_final.txt", true); //append 14k
								// FileWriter writer = new FileWriter("demoTweetsVerification1000_Tweets_cleaned_test_indiv_test.txt", true); //append 1k
								// FileWriter writer = new FileWriter("SelectedTweets_Total_Tweets_cleaned_generated_final.txt", true); //append 424k
								// FileWriter writer = new FileWriter(fileName+"_generated"+outputType, true);
								FileWriter writer = new FileWriter(fileName+"_LARGE_avg_generated_split"+outputType, true);
								BufferedWriter bufferedWriter = new BufferedWriter(writer);

								// bufferedWriter.write(referenceUser+"_generated\t"+currentTweet.getTweetId()+"\t"+currentTweet.getDateString()+"\t"+userId+"\t"+currentTweet.getUser()+"_generated\t"+generatedTweetText);
								bufferedWriter.write(referenceUser+"\t"+currentTweet.getTweetId()+"\t"+currentTweet.getDateString()+"\t"+userId+"\t"+currentTweet.getUser()+"\t"+generatedTweetText);
								bufferedWriter.newLine();

								bufferedWriter.close();
							} catch (IOException e) {
								e.printStackTrace();
							}

						}


					} else {
						lineBuffer.append((char) character);
					}
				}

				bufferedInput.close();
				fileInput.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	
	//Read in individual user's shape scale
	public static void readInGeneratingParameters4() throws FileNotFoundException
	{
		// Read in the scale and shape of each user
		Scanner sc = new Scanner(new File(pathToUserParameters)).useDelimiter("[\t\n]");
		
		int numUsers = 0;
		
		while (sc.hasNext())
		{	
			String userName = sc.next();
			Double scaleInput = Double.parseDouble(sc.next());
			Double shapeInput = Double.parseDouble(sc.next());
			usersScale.put(userName,scaleInput);
			usersShape.put(userName,shapeInput);
			
			numUsers++;
			averageUserScale += scaleInput;
			averageUserShape += shapeInput;
			//			System.out.println(userName+" "+scaleInput+" "+shapeInput);
		}
		
		averageUserScale /= numUsers;
		averageUserShape /= numUsers;
		
		sc.close();
	}

	
	
	//read in the tf-idf matrix
	public static void readInTfidfMatrix() throws FileNotFoundException
	{
		Scanner sc = new Scanner(new File(pathToTfidfMatrix)).useDelimiter("[\t\n]");
		ArrayList<String> userNamesIndex = new ArrayList<String>();
		// for (int i = 0; i < usersShape.keySet().size(); i++)
		int numInSet = 4; //for separate groups
		for (int i = 0; i < numInSet; i++)
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

				allWordsInSet.add(wordInMatrix);
				
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

		// System.out.println("OK ALL USER TFIDF VECTORS: "+allUserTfidfVectors);
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

	//averageTfidf
	public static void averageParameters()
	{
		double currentAverageTfidf = 0.0;
		double currentSumTfidf = 0.0;
		int numOfUsers = allUserTfidfVectors.keySet().size();
		
		for (String currentWord : allWordsInSet)
		{
			for (String currUser : allUserTfidfVectors.keySet())
			{
				numOfUsers++;
				currentSumTfidf += allUserTfidfVectors.get(currUser).get(currentWord);
			}
			
			currentAverageTfidf = currentSumTfidf / numOfUsers;
			
			averageWordsTfidf.put(currentWord,currentAverageTfidf);
			
			currentAverageTfidf = 0.0;
			currentSumTfidf = 0.0;
		}
		
		
	}

	//Word bins for average words tfidf
	public static void setGeneratedWordBins5()
	{
		double currentValue = MIN_TFIDF;
	
		for (String currWord : averageWordsTfidf.keySet())
		{
			
			ArrayList<String> wordsInBin = new ArrayList<String>();	
			
			currentValue = averageWordsTfidf.get(currWord);
				
//			System.out.println(currWord);
			
			if (!averageWordBins.keySet().contains(currentValue))
			{
//				System.out.println("I ENTERED HERE "+currentValue+" "+currWord);
				wordsInBin = new ArrayList<String>();
				wordsInBin.add(currWord);
				averageWordBins.put(currentValue, wordsInBin);
			}
			else
			{
//				System.out.println("I OTHERED HERE "+currentValue+" "+currWord);
				wordsInBin = averageWordBins.get(currentValue);
				wordsInBin.add(currWord);
				averageWordBins.put(currentValue,wordsInBin);
			}
		}
		
//		System.out.println("HELLO "+allUserTfidfWordsBins);
	}

	//generate tweets based on individual users
	public static String generateTweetText5(String tweetUserName)
	{

		String generatedTweetText = "";
		double userShape = averageUserShape; //Average shape parameter from original corpus
		double userScale = averageUserScale; //Average scale parameter from original corpus

		int wordsInTweet = r.nextInt((MAX_WORDS - MIN_WORDS) + 1) + MIN_WORDS;		

		for (int j = 0; j < wordsInTweet; j++) {

			String word = "";


			word = generateWord5(tweetUserName,userShape, userScale);
			System.out.println("word: "+word);
			//Checks if generated word is already used
			//			if (currentBagWords.size() < TOTAL_ORIGINAL_WORDS_DEMO)
			//			{
			//				//Generate word until it is a word that has not been used
			////				while(currentBagWords.contains(word))
			////				{
			////					word = generateWord2(averageShape, averageScale);
			////					System.out.println("word: "+word);
			////				}
			//				
			//							
			//				word = generateWord2(averageShape, averageScale);
			//				System.out.println("word: "+word);
			//				
			//			}
			//			//Clear bag of words if all words are in the bag
			//			else
			//			{
			//				currentBagWords.clear();
			//			}

			//Add newly generated word to bag of words
			//			currentBagWords.add(word);

			//			System.out.println("bin = "+indexOfWord);

			generatedTweetText += word + " ";
		}

		//		System.out.println("generatedTweet: "+ generatedTweetText);

		return generatedTweetText;
	}

	//generate a word based on individual user tfidf
	public static String generateWord5(String currTweetUserName, double currUserShape, double currUserScale)
	{
		String generatedWord = "";
		ArrayList<String> bagOfWordsFound = new ArrayList<String>();

		double x = randomDataGenerator.nextWeibull(currUserShape, currUserScale);
		//		System.out.println(x);
		//		while (x < topicsMinMax[userTopic][0] || x > topicsMinMax[userTopic][1])
		//		{
		//			x = weibull2(averageShape,averageScale);
		//		x = randomDataGenerator.nextWeibull(averageShape, averageScale) + 4.91893e-6;
		//		x = randomDataGenerator.nextWeibull(averageShape, averageScale);
		x = randomDataGenerator.nextWeibull(currUserShape, currUserScale) + MIN_TFIDF;
		System.out.println("new x: "+x);
		//		}
		//		System.out.println(x + " min: " + topicsMinMax[userTopic][0] + " max: " + topicsMinMax[userTopic][1]);

		int indexOfWord = 0;
		int indexClosest = 0;

		LinkedHashMap<Double,ArrayList<String>> currUserTfidfWordsBins = averageWordBins;
		
		ArrayList<Double> currTfidfValues = new ArrayList<Double>(averageWordBins.keySet());
		Collections.sort(currTfidfValues);
		
//		for (Double d: currTfidfValues)
//		{
//			System.out.print(d+" ");
//		}
//		System.out.println();
		
		
		// for (Double currTfidf : currTfidfValues)
		// {
			// if (currTfidf >= x)
			// {
				// closestTfidfValue = currTfidf;
				// break;
			// }
		// }
		
		double closestTfidfDiff = Math.abs(x-currTfidfValues.get(0));
		double currTfidfDiff = 0.0;
		double closestTfidfValue = currTfidfValues.get(0);
		
		for (int i = 1; i < currTfidfValues.size(); i++)
		{
			currTfidfDiff = Math.abs(x-currTfidfValues.get(i));
			if (closestTfidfDiff > currTfidfDiff)
			{
				closestTfidfDiff = currTfidfDiff;
				closestTfidfValue = currTfidfValues.get(i);
			}
		}
	
		ArrayList<String> wordsFromBin = currUserTfidfWordsBins.get(closestTfidfValue);

		indexOfWord = r.nextInt(wordsFromBin.size());

		generatedWord = wordsFromBin.get(indexOfWord);


		return generatedWord;
	}
	
	
		//Word bins for each individual users
	public static void setGeneratedWordBins4()
	{
		double currentValue = MIN_TFIDF;

		for (String currUser : allUserTfidfVectors.keySet())
		{
			ArrayList<String> wordsInBin = new ArrayList<String>();

			LinkedHashMap<String,Double> currUserTfidfVector = allUserTfidfVectors.get(currUser);
			LinkedHashMap<Double,ArrayList<String>> userTfidfWordsBins = new LinkedHashMap<Double,ArrayList<String>>();
			
//System.out.println(currUser+"--------------");
			
			for (String currWord : currUserTfidfVector.keySet())
			{
				currentValue = currUserTfidfVector.get(currWord);
				
//				System.out.println(currWord);
				
				if (!userTfidfWordsBins.keySet().contains(currentValue))
				{
//					System.out.println("I ENTERED HERE "+currentValue+" "+currWord);
					wordsInBin = new ArrayList<String>();
					wordsInBin.add(currWord);
					userTfidfWordsBins.put(currentValue, wordsInBin);
				}
				else
				{
//					System.out.println("I OTHERED HERE "+currentValue+" "+currWord);
					wordsInBin = userTfidfWordsBins.get(currentValue);
					wordsInBin.add(currWord);
					userTfidfWordsBins.put(currentValue,wordsInBin);
				}

			}
			
			allUserTfidfWordsBins.put(currUser, userTfidfWordsBins);
			
//			private static LinkedHashMap<Double,ArrayList<String>> userTfidfWordsBins 
//			private static LinkedHashMap<String,LinkedHashMap<Double,ArrayList<String>>> allUserTfidfWordsBins
//			put in all userwordbin
		}
		
//		System.out.println("HELLO "+allUserTfidfWordsBins);
	}

	//generate tweets based on individual users
	public static String generateTweetText4(String tweetUserName)
	{

		String generatedTweetText = "";
		double userShape = usersShape.get(tweetUserName); //Average shape parameter from original corpus
		double userScale = usersScale.get(tweetUserName); //Average scale parameter from original corpus

		int wordsInTweet = r.nextInt((MAX_WORDS - MIN_WORDS) + 1) + MIN_WORDS;		

		for (int j = 0; j < wordsInTweet; j++) {

			String word = "";


			word = generateWord4(tweetUserName,userShape, userScale);
			System.out.println("word: "+word);
			//Checks if generated word is already used
			//			if (currentBagWords.size() < TOTAL_ORIGINAL_WORDS_DEMO)
			//			{
			//				//Generate word until it is a word that has not been used
			////				while(currentBagWords.contains(word))
			////				{
			////					word = generateWord2(averageShape, averageScale);
			////					System.out.println("word: "+word);
			////				}
			//				
			//							
			//				word = generateWord2(averageShape, averageScale);
			//				System.out.println("word: "+word);
			//				
			//			}
			//			//Clear bag of words if all words are in the bag
			//			else
			//			{
			//				currentBagWords.clear();
			//			}

			//Add newly generated word to bag of words
			//			currentBagWords.add(word);

			//			System.out.println("bin = "+indexOfWord);

			generatedTweetText += word + " ";
		}

		//		System.out.println("generatedTweet: "+ generatedTweetText);

		return generatedTweetText;
	}

	//generate a word based on individual user tfidf
	public static String generateWord4(String currTweetUserName, double currUserShape, double currUserScale)
	{
		String generatedWord = "";
		ArrayList<String> bagOfWordsFound = new ArrayList<String>();

		double x = randomDataGenerator.nextWeibull(currUserShape, currUserScale);
		//		System.out.println(x);
		//		while (x < topicsMinMax[userTopic][0] || x > topicsMinMax[userTopic][1])
		//		{
		//			x = weibull2(averageShape,averageScale);
		//		x = randomDataGenerator.nextWeibull(averageShape, averageScale) + 4.91893e-6;
		//		x = randomDataGenerator.nextWeibull(averageShape, averageScale);
		x = randomDataGenerator.nextWeibull(currUserShape, currUserScale) + MIN_TFIDF;
		System.out.println("new x: "+x);
		//		}
		//		System.out.println(x + " min: " + topicsMinMax[userTopic][0] + " max: " + topicsMinMax[userTopic][1]);

		int indexOfWord = 0;
		int indexClosest = 0;

		LinkedHashMap<Double,ArrayList<String>> currUserTfidfWordsBins = allUserTfidfWordsBins.get(currTweetUserName); 
		
		ArrayList<Double> currTfidfValues = new ArrayList<Double>(currUserTfidfWordsBins.keySet());
		Collections.sort(currTfidfValues);
		
//		for (Double d: currTfidfValues)
//		{
//			System.out.print(d+" ");
//		}
//		System.out.println();
		
		
		// for (Double currTfidf : currTfidfValues)
		// {
			// if (currTfidf >= x)
			// {
				// closestTfidfValue = currTfidf;
				// break;
			// }
		// }
		
		double closestTfidfDiff = Math.abs(x-currTfidfValues.get(0));
		double currTfidfDiff = 0.0;
		double closestTfidfValue = currTfidfValues.get(0);
		
		for (int i = 1; i < currTfidfValues.size(); i++)
		{
			currTfidfDiff = Math.abs(x-currTfidfValues.get(i));
			if (closestTfidfDiff > currTfidfDiff)
			{
				closestTfidfDiff = currTfidfDiff;
				closestTfidfValue = currTfidfValues.get(i);
			}
		}
	
		ArrayList<String> wordsFromBin = currUserTfidfWordsBins.get(closestTfidfValue);

		indexOfWord = r.nextInt(wordsFromBin.size());

		generatedWord = wordsFromBin.get(indexOfWord);


		return generatedWord;
	}
	
	
}





class Tweet implements Comparable<Tweet> {

	private String tweetText;
	private long tweetId;
	private Date date;
	private String username;
	private String dateString;

	public Tweet()
	{
		tweetText = "";
		tweetId = 0;
		date = new Date();
		username = "";
	}

	public Tweet(String text, long id, Date date, String username)
	{
		tweetText = text;
		tweetId = id;
		this.date = date;
		this.username = username;
	}
	
	public Tweet(String text, long id, String date, String username)
	{
		dateString = date;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		tweetText = text;
		tweetId = id;
		try {
			this.date = sdf.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.username = username;
	}

	
	
	public String getTweetText()
	{
		return tweetText;
	}

	public void setTweetText(String text)
	{
		tweetText = text;
	}

	public long getTweetId()
	{
		return tweetId;
	}

	public void setTweetId(long tweetId)
	{
		this.tweetId = tweetId;
	}

	public Date getDate()
	{
		return date;
	}

	public void setDate(Date date)
	{
		this.date = date;
	}
	
	public String getDateString()
	{
		return dateString;
	}

	public String getUser()
	{
		return username;
	}

	public void setUser(String user)
	{
		username = user;
	}

	public int compareTo(Tweet otherTweet) {
		
		if (tweetId < otherTweet.getTweetId())
			return -1;
		else if (tweetId > otherTweet.getTweetId())
			return 1;
		else
			return 0;
	}
}

