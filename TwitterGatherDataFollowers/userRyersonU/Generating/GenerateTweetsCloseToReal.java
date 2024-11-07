

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.Scanner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.math3.random.RandomDataGenerator;

//Generate based on each user's own scale shape parameter

public class GenerateTweetsCloseToReal {

	//	private static final double DEFAULT_SHAPE2 = 0.48694;
	//	private static final double DEFAULT_SCALE2 = 2.09345E-6;
//	private static final double DEFAULT_SHAPE2 = 0.18999; //shape from dataset 14k technical report average
//	private static final double DEFAULT_SCALE2 = 2.19E-06; //scale from dataset 14k technical report average
	private static final double DEFAULT_SHAPE2 = 0.338465; //average shape from two datasets
	private static final double DEFAULT_SCALE2 = 2.14173E-06; //average scale from two datasets
	public static final int MAX_WORDS = 7; //Maximum number of words in tweet after processing
	public static final int MIN_WORDS = 3; //Minimum number of words in tweet after processing
		// private static final int TOTAL_ORIGINAL_WORDS = 1148; //Total number of words from original corpus
	//	private static final int TOTAL_ORIGINAL_WORDS_DEMO = 30506;
	private static final int TOTAL_ORIGINAL_WORDS = 1170; //Total number of words from original corpus 1k
	// private static final int TOTAL_ORIGINAL_WORDS = 18732; //Total number of words from original corpus 14k
	// private static final int TOTAL_ORIGINAL_WORDS = 264501; //Total number of words from original corpus 1k
	
	
	// private static final int TOTAL_ORIGINAL_WORDS_DEMO = 18732; //total words from dataset after cleaning 14k
	// private static final int TOTAL_ORIGINAL_WORDS_DEMO = 264501; //total words from dataset after cleaning
	private static final int TOTAL_ORIGINAL_WORDS_DEMO = 1170; //total words from dataset after cleaning 1k
	// private static final double MIN_TFIDF = 0.000169458; //min tf-idf from dataset 
	// private static final double MAX_TFIDF = 0.066244964; //max tf-idf from dataset
	
	// private static final double MIN_TFIDF = 0.000169458; //min tf-idf from dataset 
	// private static final double MAX_TFIDF = 0.066244964; //max tf-idf from dataset
	
	private static final double MIN_TFIDF = 0; //min tf-idf from dataset 1k
	private static final double MAX_TFIDF = 0.118414337; //max tf-idf from dataset 1k
		
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
	
	
	private static ArrayList<Double> wordsBins = new ArrayList<Double>(); //Bins for groups of tf-idf
	private static LinkedHashMap<Double,ArrayList<String>> tfidfWordsBins = new LinkedHashMap<Double,ArrayList<String>>(); //Words for each bin

	private static int totalUsers = 10; //total users from dataset 1k
	// private static int totalUsers = 14; //total users from dataset 14k
	// private static int totalUsers = 256; //total users from dataset
	
	// private static String pathToWordsText = "words_424k.txt";
	// private static String pathToWordsText = "words_14k.txt";
	private static String pathToWordsText = "words_1k.txt";
			
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {
			//			readInGeneratingParameters();
			readInGeneratingParameters2();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		//		setGeneratedWordBins();
//		setGeneratedWordBins2();
		setGeneratedWordBins3();


		// File textFile = new File("demoFolder_Data_cleaned.txt"); //read in cleaned dataset 14k
		File textFile = new File("demoTweetsVerification1000_Tweets_cleaned.txt"); //read in cleaned dataset 1k
		// File textFile = new File("SelectedTweets_Total_Tweets_cleaned.txt"); //read in cleaned dataset 424k
		
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

						currentTweet = new Tweet(tweetText,tweetId,tweetDate,currentUserName);


						System.out.println(referenceUser+"\t"+currentTweet.getTweetId()+"\t"+currentTweet.getDateString()+"\t"+userId+"\t"+currentTweet.getUser()+"\t"+currentTweet.getTweetText());
//						generatedTweetText = generateTweetText2();
						generatedTweetText = generateTweetText3();

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
							FileWriter writer = new FileWriter("demoTweetsVerification1000_Tweets_cleaned_test4.txt", true); //append 1k
							// FileWriter writer = new FileWriter("SelectedTweets_Total_Tweets_cleaned_generated_final.txt", true); //append 424k

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

	public static String generateTweetText2()
	{

		String generatedTweetText = "";
		double averageShape = DEFAULT_SHAPE2; //Average shape parameter from original corpus
		double averageScale = DEFAULT_SCALE2; //Average scale parameter from original corpus

		int wordsInTweet = r.nextInt((MAX_WORDS - MIN_WORDS) + 1) + MIN_WORDS;		

		for (int j = 0; j < wordsInTweet; j++) {

			String word = "";


			word = generateWord2(averageShape, averageScale);
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
			currentBagWords.add(word);

			//			System.out.println("bin = "+indexOfWord);

			generatedTweetText += word + " ";
		}

		//		System.out.println("generatedTweet: "+ generatedTweetText);

		return generatedTweetText;
	}

	public static String generateWord2(double averageShape, double averageScale)
	{
		String generatedWord = "";
		ArrayList<String> bagOfWordsFound = new ArrayList<String>();

		double x = randomDataGenerator.nextWeibull(averageShape, averageScale);
		//		System.out.println(x);
		//		while (x < topicsMinMax[userTopic][0] || x > topicsMinMax[userTopic][1])
		//		{
		//			x = weibull2(averageShape,averageScale);
		//		x = randomDataGenerator.nextWeibull(averageShape, averageScale) + 4.91893e-6;
//		x = randomDataGenerator.nextWeibull(averageShape, averageScale);
		x = randomDataGenerator.nextWeibull(averageShape, averageScale) + MIN_TFIDF;
		System.out.println("new x: "+x);
		//		}
		//		System.out.println(x + " min: " + topicsMinMax[userTopic][0] + " max: " + topicsMinMax[userTopic][1]);

		int indexOfWord = 0;
		int indexClosest = 0;
		
		for (int i = 0; i < TOTAL_ORIGINAL_WORDS_DEMO; i++)
		{
			if (topicsWordsTfidfDemo[i] >= x)
//			if (topicsWordsTfidfDemo[i] > x)
			{
				indexClosest = i;
				bagOfWordsFound.add(topicsWordsDemo[i]);
			}
		}
//		if (bagOfWordsFound.size() < 1)
//		{
//
//			//indexOfWord = r.nextInt((topicsWords - MIN_WORDS) + 1) + MIN_WORDS;	
//		}
//		else
//		{
//			indexOfWord = r.nextInt(bagOfWordsFound.size());
//		}

		indexOfWord = indexClosest;


		//		for (int i = 0; i < TOTAL_ORIGINAL_WORDS_DEMO; i++)
		//		{
		//			if (topicsWordsIndexDemo[i] >= x || x > topicsWordsIndexDemo[TOTAL_ORIGINAL_WORDS-1])
		//			{
		//				indexOfWord = i;
		//				break;
		//			}
		//		}

		//		while (x > topicsWordsIndex[userTopic][indexOfWord] && indexOfWord < TOTAL_ORIGINAL_WORDS-1)
		//		{
		//			indexOfWord++;
		//		}

		generatedWord = topicsWordsDemo[indexOfWord];


		return generatedWord;
	}

	public static String generateTweetText3()
	{

		String generatedTweetText = "";
		double averageShape = DEFAULT_SHAPE2; //Average shape parameter from original corpus
		double averageScale = DEFAULT_SCALE2; //Average scale parameter from original corpus

		int wordsInTweet = r.nextInt((MAX_WORDS - MIN_WORDS) + 1) + MIN_WORDS;		

		for (int j = 0; j < wordsInTweet; j++) {

			String word = "";


			word = generateWord3(averageShape, averageScale);
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
			currentBagWords.add(word);

			//			System.out.println("bin = "+indexOfWord);

			generatedTweetText += word + " ";
		}

		//		System.out.println("generatedTweet: "+ generatedTweetText);

		return generatedTweetText;
	}

	public static String generateWord3(double averageShape, double averageScale)
	{
		String generatedWord = "";
		ArrayList<String> bagOfWordsFound = new ArrayList<String>();

		double x = randomDataGenerator.nextWeibull(averageShape, averageScale);
		//		System.out.println(x);
		//		while (x < topicsMinMax[userTopic][0] || x > topicsMinMax[userTopic][1])
		//		{
		//			x = weibull2(averageShape,averageScale);
		//		x = randomDataGenerator.nextWeibull(averageShape, averageScale) + 4.91893e-6;
//		x = randomDataGenerator.nextWeibull(averageShape, averageScale);
		x = randomDataGenerator.nextWeibull(averageShape, averageScale) + MIN_TFIDF;
		System.out.println("new x: "+x);
		//		}
		//		System.out.println(x + " min: " + topicsMinMax[userTopic][0] + " max: " + topicsMinMax[userTopic][1]);

		int indexOfWord = 0;
		int indexClosest = 0;
		
		for (int i = 0; i < wordsBins.size(); i++)
		{
			if (wordsBins.get(i) >= x)
//			if (topicsWordsTfidfDemo[i] > x)
			{
				indexClosest = i;
				break;
			}
		}
//		if (bagOfWordsFound.size() < 1)
//		{
//
//			//indexOfWord = r.nextInt((topicsWords - MIN_WORDS) + 1) + MIN_WORDS;	
//		}
//		else
//		{
//			indexOfWord = r.nextInt(bagOfWordsFound.size());
//		}

		ArrayList<String> wordsFromBin = tfidfWordsBins.get(wordsBins.get(indexClosest));
		
		indexOfWord = r.nextInt(wordsFromBin.size());


		//		for (int i = 0; i < TOTAL_ORIGINAL_WORDS_DEMO; i++)
		//		{
		//			if (topicsWordsIndexDemo[i] >= x || x > topicsWordsIndexDemo[TOTAL_ORIGINAL_WORDS-1])
		//			{
		//				indexOfWord = i;
		//				break;
		//			}
		//		}

		//		while (x > topicsWordsIndex[userTopic][indexOfWord] && indexOfWord < TOTAL_ORIGINAL_WORDS-1)
		//		{
		//			indexOfWord++;
		//		}

		generatedWord = wordsFromBin.get(indexOfWord);


		return generatedWord;
	}
	
	
	public static void readInGeneratingParameters2() throws FileNotFoundException
	{
		// Read in the tf-idf of each word for each dataset; format: word tf-idf (in ascending order)
		//		Scanner sc = new Scanner(new File("words_100k.txt")).useDelimiter("[\t\n]");
		Scanner sc = new Scanner(new File(pathToWordsText)).useDelimiter("[\t\n]");
		for (int i = 0; i < TOTAL_ORIGINAL_WORDS_DEMO; i++) {
			String s = sc.next();
			topicsWordsDemo[i] = s;
			Double d = Double.parseDouble(sc.next());
			topicsWordsTfidfDemo[i] = d;
			System.out.println(s+" "+d);
		}
		sc.close();
	}

	// Sets the bins for generating random indices for words
	public static void setGeneratedWordBins2()
	{
		//for (int i = 0; i < totalUsers; i++) {

			//			double dist = (1.55907e-5 - 4.91893e-6) / TOTAL_ORIGINAL_WORDS_DEMO;
			//			double currentValue = 4.91893e-6;
			double dist = (MAX_TFIDF - MIN_TFIDF) / TOTAL_ORIGINAL_WORDS_DEMO;
			double currentValue = MIN_TFIDF;

			for (int j = 0; j < TOTAL_ORIGINAL_WORDS; j++) {
				topicsWordsIndexDemo[j] = currentValue;
				currentValue += dist;
				//					System.out.println(currentValue);
			}
		//}
	}
	
	public static void setGeneratedWordBins3()
	{
		

			//			double dist = (1.55907e-5 - 4.91893e-6) / TOTAL_ORIGINAL_WORDS_DEMO;
			//			double currentValue = 4.91893e-6;
			double currentValue = MIN_TFIDF;
			
			ArrayList<String> wordsInBin = new ArrayList<String>();
			for (int j = 0; j < TOTAL_ORIGINAL_WORDS_DEMO; j++) {
				
				
				currentValue = topicsWordsIndexDemo[j];
				
				if (!wordsBins.contains(currentValue))
				{
					wordsBins.add(currentValue);
					wordsInBin = new ArrayList<String>();
					wordsInBin.add(topicsWordsDemo[j]);
					//wordsInBin.add(topicsWordsIndexDemo[j]);
					tfidfWordsBins.put(currentValue, wordsInBin);
				}
				else
				{
					wordsInBin = tfidfWordsBins.get(currentValue);
					wordsInBin.add(topicsWordsDemo[j]);
					tfidfWordsBins.put(currentValue,wordsInBin);
				}
				
				//					System.out.println(currentValue);
			}
		
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

