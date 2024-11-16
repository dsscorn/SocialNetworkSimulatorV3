package TwitterGatherDataFollowers.userRyersonU;
 

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Collections;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.TreeSet;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Random;

import twitter4j.IDs;
import twitter4j.Paging;
import twitter4j.RateLimitStatus;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.domain.AMSService;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.util.CombinatoricsUtils;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.commons.math3.stat.descriptive.summary.Sum;
import org.apache.commons.math3.util.Precision;

public class UserGenSimAgent extends Agent 
{
	private static final long serialVersionUID = 1L;
	private static final int MIN_WORDS = 3;

	private Agent thisAgent = this;

	//My Own Variables 
	String[] stopWordsArray = {"a","able","about","above","abst","accordance","according","accordingly","across","act",
			"actually","added","adj","affected","affecting","affects","after","afterwards","again","against",
			"ah","all","almost","alone","along","already","also","although","always","am",
			"among","amongst","an","and","announce","another","any","anybody","anyhow","anymore",
			"anyone","anything","anyway","anyways","anywhere","apparently","approximately","are","aren","arent",
			"arise","around","as","aside","ask","asking","at","auth","available","away",
			"awfully","b","back","be","became","because","become","becomes","becoming","been",
			"before","beforehand","begin","beginning","beginnings","begins","behind","being","believe","below",
			"beside","besides","between","beyond","biol","both","brief","briefly","but","by",
			"c","ca","came","can","cannot","cant","cause","causes","certain","certainly",
			"co","com","come","comes","contain","containing","contains","could","couldnt","d",
			"da","date","did","didnt","different","do","does","doesnt","doing","done",
			"dont","down","downwards","due","during","e","each","ed","edu","effect",
			"eg","eight","eighty","either","else","elsewhere","end","ending","enough","especially",
			"et","et-al","etc","even","ever","every","everybody","everyone","everything","everywhere",
			"ex","except","f","far","few","ff","fifth","first","five","fix",
			"followed","following","follows","for","former","formerly","forth","found","four","from",
			"further","furthermore","g","gave","get","gets","getting","give","given","gives",
			"giving","go","goes","gone","got","gotten","h","had","happens","hardly",
			"has","hasnt","have","havent","having","he","hed","hence","her","here",
			"hereafter","hereby","herein","heres","hereupon","hers","herself","hes","hi","hid",
			"him","himself","his","hither","home","how","howbeit","however","hundred","i",
			"id","idk","ie","if","ill","im","immediate","immediately","importance","important","in",
			"inc","indeed","index","information","instead","into","invention","inward","is","isnt",
			"it","itd","itll","its","itself","ive","j","just","k","keep",
			"keeps","kept","kg","km","know","known","knows","l","largely","last",
			"lately","later","latter","latterly","least","less","lest","let","lets","like",
			"liked","likely","line","little","ll","look","looking","looks","ltd","m",
			"made","mainly","make","makes","many","may","maybe","me","mean","means",
			"meantime","meanwhile","merely","mg","might","million","miss","ml","more","moreover",
			"most","mostly","mr","mrs","much","mug","must","my","myself","n",
			"na","name","namely","nay","nd","near","nearly","necessarily","necessary","need",
			"needs","neither","never","nevertheless","next","new","nine","ninety","no","nobody",
			"non","none","nonetheless","noone","nor","normally","nos","not","noted","nothing",
			"now","nowhere","o","obtain","obtained","obviously","of","off","often","oh",
			"ok","okay","old","omitted","on","once","one","ones","only","onto",
			"or","ord","other","others","otherwise","ought","our","ours","ourselves","out",
			"outside","over","overall","owing","own","p","page","pages","part","particular",
			"particularly","past","per","perhaps","placed","please","plus","poorly","possible","possibly",
			"potentially","pp","predominantly","present","previously","primarily","probably","promptly","proud","provides",
			"put","q","que","quickly","quite","qv","r","ran","rather","rd",
			"re","readily","really","recent","recently","ref","refs","regarding","regardless","regards",
			"related","relatively","research","respectively","resulted","resulting","results","right","rt","run","s",
			"said","same","saw","say","saying","says","sec","section","see","seeing",
			"seem","seemed","seeming","seems","seen","self","selves","sent","seven","several",
			"shall","she","shed","shell","shes","should","shouldnt","show","showed","shown",
			"showns","shows","significant","significantly","similar","similarly","since","six","slightly","so",
			"some","somebody","somehow","someone","somethan","something","sometime","sometimes","somewhat","somewhere",
			"soon","sorry","specifically","specified","specify","specifying","still","stop","strongly","sub",
			"substantially","successfully","such","sufficiently","suggest","sup","sure","t","take","taken",
			"taking","tell","tends","th","than","thank","thanks","thanx","that","thatll",
			"thats","thatve","the","their","theirs","them","themselves","then","thence","there",
			"thereafter","thereby","thered","therefore","therein","therell","thereof","therere","theres","thereto",
			"thereupon","thereve","these","they","theyd","theyll","theyre","theyve","think","this",
			"those","thou","though","thoughh","thousand","throug","through","throughout","thru","thus",
			"til","tip","to","together","too","took","toward","towards","tried","tries",
			"truly","try","trying","ts","twice","two","ty","u","ull","ull",
			"un","under","unfortunately","unless","unlike","unlikely","until","unto","up","upon",
			"ups","ur","us","use","used","useful","usefully","usefulness","uses","using",
			"usually","v","value","various","ve","very","via","viz","vol","vols",
			"vs","w","want","wants","was","wasnt","way","we","wed","welcome",
			"well","went","were","werent","weve","what","whatever","whatll","whats","when",
			"whence","whenever","where","whereafter","whereas","whereby","wherein","wheres","whereupon","wherever",
			"whether","which","while","whim","whither","who","whod","whoever","whole","wholl",
			"whom","whomever","whos","whose","why","widely","willing","wish","with","within",
			"without","wont","words","world","would","wouldnt","www","x","y","yes",
			"yet","yo","you","youd","youll","your","youre","yours","yourself","yourselves","youve",
			"z","zero"};
	
	private InMemoryDb localDb; //Stores all tweets from corpusGenFile
	
	private int numArtificialTweets;
	private int totalTweetsToGenerate;
	private File corpusGenFile;
	
	transient protected ControllerAgentGui myGui;
	
	private Map<String,Long> userTwitterId; //Users and their UserId on Twitter
	private Map<String,List<Long>> followeeCorpusTweetIds;
	private Map<Long,String> tweetIdFolloweeName; //Map a tweet id to its followee
	private Map<String,Integer> maxWordsFolloweeGroup; //The maximum number of words in a tweet for a followee group
	private Map<String,Double> followeeShapeParameters; //The average shape parameters for a followee group
	private Map<String,Double> followeeScaleParameters; //The average scale parameters for a followee group
	private Map<String,Map<Double,List<String>>> allUserTfidfBins; //Store user tfidf into bins with words
	
	private static Random r = new Random(); //should not construct in method, make it static
	private static RandomDataGenerator randomDataGenerator = new RandomDataGenerator(); //should not construct in method, make it static

	protected void setup() 
	{
		Object[] args = getArguments();
		
		numArtificialTweets = (Integer) args[0]; 	
		corpusGenFile = (File) args[1];
		myGui = (ControllerAgentGui) args[2];
		
		localDb = new InMemoryDb();


		System.out.println(getLocalName()+" numArtificialTweets: "+numArtificialTweets);
		
		try {
			DFAgentDescription dfd = new DFAgentDescription();
			dfd.setName(getAID());
			ServiceDescription sd = new ServiceDescription();
			sd.setName("Distributed Recommender System");
			sd.setType("User Generation Simulation Agent");
			dfd.addServices(sd);
			DFService.register(this, dfd);
			System.out.println(getLocalName()+" REGISTERED WITH THE DF");
		} catch (FIPAException e) {
			e.printStackTrace();
		}		

		System.out.println("Hello! I am " + getAID().getName()+ " and is setup properly.");

		UserGenSimBehaviour userGenSimBehaviour = new UserGenSimBehaviour(this);
		
		addBehaviour(userGenSimBehaviour);
		
		setQueueSize(0);
	}

	private class UserGenSimBehaviour extends OneShotBehaviour {		
		private static final long serialVersionUID = 1L;

		public UserGenSimBehaviour(Agent a) {
			super(a);
		}

		public void action() {	
			
			double observationsExample[] = {0.0,0.661872677,1.103638324,0.230972299,0.004025552,3.07008E-06,5.07473E-11,8.80808E-18,7.69829E-27,1.61608E-38};
			// MLE Shape: 0.054859261474858256 Scale: 2.621674356811457E-6
			// Regression Shape: 0.04070180242132002 Scale: 1.1940218998091645E-5
			
			System.out.println("*****");
			for (int i = 0; i < 10; i++)
			{
					double x = randomDataGenerator.nextWeibull(0.054859261474858256, 2.621674356811457E-6);
					System.out.println(x);
			}
			System.out.println("*****");

			double[] exampleMLEShapeScale = calculateWeibullShapeScaleParameter(observationsExample);	
			double[] exampleRegressionShapeScale = calculateRegressionWeibullShapeScaleParameter2(observationsExample);
			
			System.out.println("MLE Shape: "+exampleMLEShapeScale[0]+" Scale: "+exampleMLEShapeScale[1]);
			System.out.println("Regression Shape: "+exampleRegressionShapeScale[0]+" Scale: "+exampleRegressionShapeScale[1]);

			doDelete();
			
			//read textfile from corpusGenFile;
			readFromTextFile();
			
			List<Tweet> allTweetsInOriginalCorpus = localDb.getTweets();
			List<Tweet> processedTweetsCorpus = new ArrayList<Tweet>();
			List<Long> tweetIdsToRemove = new ArrayList<Long>();
			
			myGui.appendUserGenTweetsResult("Reading from: "+corpusGenFile.getName());		
			
			//Process all the tweets
			myGui.appendUserGenTweetsResult("Processing original tweets corpus...");
			
			for (Tweet currentTweet : allTweetsInOriginalCorpus)
			{
				String currentText = currentTweet.getTweetText();

				//pad out spaces before and after word for parsing 
				currentText = String.format(" %s ",currentText);

				//System.out.println("Original text: "+currentText);

				//Remove Photo: tweets
				if (currentText.contains("Photo:"))
					currentText = currentText.substring(0,currentText.indexOf("Photo:"));

				//Remove Photoset: tweets
				if (currentText.contains("Photoset:"))
					currentText = currentText.substring(0,currentText.indexOf("Photoset:"));

				//Remove all retweets if flagged
				Matcher matcher; //a matcher
				if (currentText.contains("RT @"))
					currentText="";
				else
				{
					//Remove RT @, conserve the text from retweets
					Pattern retweet = Pattern.compile("RT @");

					matcher = retweet.matcher(currentText);
					currentText = matcher.replaceAll("RT ");
				}
				//System.out.println("After RT @: " + currentText);

				//Remove punctuations
				Pattern punctuations = Pattern.compile("[\\p{P}]");
				matcher = punctuations.matcher(currentText);
				currentText = matcher.replaceAll("");

				//System.out.println("After punctuations: "+ currentText);

				//Remove url links
				Pattern links = Pattern.compile("http[a-zA-Z0-9]*|bitly[a-zA-Z0-9]*|www[a-zA-Z0-9]*");
				matcher = links.matcher(currentText);
				currentText = matcher.replaceAll(" ");

				//System.out.println("After url links: "+ currentText);

				//Remove special characters including hash tags if flagged

				Pattern specialCharacters = Pattern.compile("[^a-zA-Z\\p{Z}]");
				matcher = specialCharacters.matcher(currentText);
				currentText = matcher.replaceAll(" ");

				//System.out.println("After special characters: " + currentText);

				//Remove stop words if flagged
				for (String stopWord : stopWordsArray){	
					if (currentText.toLowerCase().contains(" "+stopWord+" "))
						//System.out.println("FOUND STOPWORD: "+stopWord);
						currentText = currentText.toLowerCase().replaceAll(" "+stopWord+" "," ");
				}
				
				//Change all text to lowercase
				currentText=currentText.toLowerCase();

				//Trim leading and ending white space
				currentText=currentText.trim();
				//Remove any non-alphabetical characters
				currentText=currentText.replaceAll("[^a-zA-Z ]","");
				//Shorten any spaces to just 1 single space
				currentText=currentText.replaceAll(" +", " ");
				//currentText=currentText.trim().replaceAll(" +", " ");

				//System.out.println(getLocalName()+" Removed junk: "+currentText);
				
				int wordCount = 0;
				wordCount = currentText.split("\\s+").length;

				long currTweetId = currentTweet.getTweetId();
				
				//System.out.println("currentText: "+ currentText);
				//If processed text is a blank line with 1 single space or less than 3 words
				if (wordCount < 3)
				{
					//System.out.println("DO NOT ADD");
					tweetIdsToRemove.add(currTweetId);
				}
				else
				{
					String[] processedText = currentText.split("\\s+");
					String neatProcessedText = "";
					for (int i = 0; i < processedText.length; i++)
					{
						neatProcessedText += processedText[i]+" ";
					}
					// System.out.println("neatProcessedText: "+neatProcessedText);
					Tweet processedTweet = new Tweet(neatProcessedText,currentTweet.getTweetId(),currentTweet.getDate(),currentTweet.getUser());
					processedTweetsCorpus.add(processedTweet);
				}
			}
					
			//Remove tweets that did not meet criteria after processing from followee tweet id lists
			myGui.appendUserGenTweetsResult("Removing tweets that did not meet processing criteria...");
			
			for (Long tweetIdToRemove : tweetIdsToRemove)
			{
				for (String referenceUser : followeeCorpusTweetIds.keySet())
				{
					List<Long> referenceUserTweetIds = followeeCorpusTweetIds.get(referenceUser);
					if (referenceUserTweetIds.contains(tweetIdToRemove))
					{
						referenceUserTweetIds.remove(tweetIdToRemove);
						followeeCorpusTweetIds.put(referenceUser,referenceUserTweetIds);
					}
				}
				
				if (tweetIdFolloweeName.containsKey(tweetIdToRemove))
					tweetIdFolloweeName.remove(tweetIdToRemove);
			}
			
			System.out.println("original corpus size: "+allTweetsInOriginalCorpus.size());
			System.out.println("tweets to remove: "+tweetIdsToRemove.size());
			System.out.println("final size: "+(allTweetsInOriginalCorpus.size()-tweetIdsToRemove.size()));
			System.out.println("processedTweetsCorpus size: "+processedTweetsCorpus.size());
			
			int countRemainingIds = 0;
			for (String referenceUser : followeeCorpusTweetIds.keySet())
			{
				List<Long> referenceUserTweetIds = followeeCorpusTweetIds.get(referenceUser);
				countRemainingIds+=referenceUserTweetIds.size();
			}
			
			System.out.println("followeeCorpusTweetIds total size: "+countRemainingIds);
			System.out.println("tweetIdFolloweeName total size: "+tweetIdFolloweeName.keySet().size());
			
			
			//Aggregate user tweets into a single document and keep list of followers for each followee (includes itself as follower)
			Map<String,List<String>> followeeFollowerNames = new LinkedHashMap<String,List<String>>();
			Map<String,Tweet> aggregatedTweetsCorpus = new LinkedHashMap<String,Tweet>();
			Map<String,Integer> userTweetCounts = new LinkedHashMap<String,Integer>();
			Map<String,Integer> followeeWordCounts = new LinkedHashMap<String,Integer>(); //Sum of the number of words from all tweets in a followee group
			
			myGui.appendUserGenTweetsResult("Aggregating user tweets into a single document...");
			
			for (Tweet currTweet : processedTweetsCorpus)
			{
				long processedTweetId = currTweet.getTweetId();
				String currProcessedTweetUserName = currTweet.getUser();
				String currReferenceUser = tweetIdFolloweeName.get(processedTweetId);
				List<String> currFollowerNames;
				Tweet currAggregatedTweet;
				int tweetCount = 0;
				
				//Fill in followee and follower names
				if (followeeFollowerNames.containsKey(currReferenceUser))
				{
					currFollowerNames = followeeFollowerNames.get(currReferenceUser);
				}
				else
				{
					currFollowerNames = new ArrayList<String>();
				}
				
				//Only add user to follower names if not present in list already
				if (!currFollowerNames.contains(currProcessedTweetUserName))
				{
					currFollowerNames.add(currProcessedTweetUserName);
					followeeFollowerNames.put(currReferenceUser,currFollowerNames);
				}
				
				//Sum the word counts of each tweet from each followee group
				int numWordsInCurrTweet = currTweet.getTweetText().split("\\s+").length;
				int currFolloweeWordCount = 0;
				if (followeeWordCounts.keySet().contains(currReferenceUser))
					currFolloweeWordCount = followeeWordCounts.get(currReferenceUser);
				followeeWordCounts.put(currReferenceUser,currFolloweeWordCount+numWordsInCurrTweet);
				
				//Add tweet count of user
				if (userTweetCounts.containsKey(currProcessedTweetUserName))
					tweetCount = userTweetCounts.get(currProcessedTweetUserName);
				
				tweetCount++;
				userTweetCounts.put(currProcessedTweetUserName,tweetCount);
				
				//Create tweet to hold aggregated tweet if not already made
				if (aggregatedTweetsCorpus.containsKey(currProcessedTweetUserName))
				{
					currAggregatedTweet = aggregatedTweetsCorpus.get(currProcessedTweetUserName);
				}
				else
				{
					currAggregatedTweet = new Tweet("",currTweet.getTweetId(),currTweet.getDate(),currTweet.getUser());
				}
				
				currAggregatedTweet.setTweetText(currAggregatedTweet.getTweetText()+currTweet.getTweetText());
				aggregatedTweetsCorpus.put(currProcessedTweetUserName,currAggregatedTweet);
			}
			
			maxWordsFolloweeGroup = new LinkedHashMap<String,Integer>();
			//Find maximum number of words in a followee group for generation by taking average of word counts
			for (String followeeName : followeeCorpusTweetIds.keySet())
			{
				int numTweetsFollowee = followeeCorpusTweetIds.get(followeeName).size();
				int totalWordsFollowee = followeeWordCounts.get(followeeName);
				int maxWordsToGenerate = (int) Math.ceil( (double)totalWordsFollowee / numTweetsFollowee);
				maxWordsFolloweeGroup.put(followeeName,maxWordsToGenerate);
			}
			
			
			// try {
				// FileWriter writer;
				// writer = new FileWriter("processedTweetsCorpus.txt", true); //append
				// BufferedWriter bufferedWriter = new BufferedWriter(writer);
				// for (String user : aggregatedTweetsCorpus.keySet())
				// {
					// String tweetText = aggregatedTweetsCorpus.get(user).getTweetText();
					// bufferedWriter.write("UserX: "+user);
					// bufferedWriter.newLine();
					// bufferedWriter.write("TweetX: "+tweetText);
					// bufferedWriter.newLine();
				// }
				
				// bufferedWriter.close();
			// } catch (IOException e) {
				/* // TODO Auto-generated catch block */
				// e.printStackTrace();
			// }
			
			
			// System.out.println("aggregatedTweetsCorpus keysize: "+aggregatedTweetsCorpus.keySet().size());
			// for (String followee : followeeFollowerNames.keySet())
			// {
				// List<String> tempFollowerNames = followeeFollowerNames.get(followee);
				// System.out.print("Followee: "+followee+" Followers: ");
				// for (String follower : tempFollowerNames)
				// {
					// System.out.print(follower + " ");
				// }
				// System.out.println();
			// }
				
			//Put aggregated tweets into corresponding followee corpus
			Map<String,List<Tweet>> followeeCorpusAggregatedTweets = new LinkedHashMap<String,List<Tweet>>();
			
			myGui.appendUserGenTweetsResult("Putting aggregated tweets into corresponding followee corpus...");
			
			for (String followeeName : followeeFollowerNames.keySet())
			{
				List<Tweet> followerAggregatedTweets = new ArrayList<Tweet>();
				
				for (String followerName: followeeFollowerNames.get(followeeName))
				{
					followerAggregatedTweets.add(aggregatedTweetsCorpus.get(followerName));
				}
				
				followeeCorpusAggregatedTweets.put(followeeName,followerAggregatedTweets);
			}
			
			//Find unique words of each followee Corpus
					
			myGui.appendUserGenTweetsResult("Finding unique terms for each followee corpus...");
			
			Map<String,Set<String>> followeeUniqueTerms = findUniqueWordsFolloweeCorpus(followeeCorpusAggregatedTweets);
			
			//Calculate the word vectors for each followee corpus separately
			//Create blank term vectors for each followee corpus
			Map<String,Map<String,Double>> followeeTermVectors = new LinkedHashMap<String,Map<String,Double>>();
						
			for (String followeeName: followeeUniqueTerms.keySet())
			{
				followeeTermVectors.put(followeeName,createBlankTermVector(followeeUniqueTerms.get(followeeName)));
			}
			
			// for (String followeeName: followeeTermVectors.keySet())
			// {
				// System.out.println("Term Vector Followee: "+followeeName);
				// for (String term: followeeTermVectors.get(followeeName).keySet())
				// {
					// System.out.print(term+":"+followeeTermVectors.get(followeeName).get(term)+",");
				// }
				// System.out.println();
			// }
			
			//Calculate the term frequency of each user
			Map<String,Map<String,Double>> allUserTermVectors = new LinkedHashMap<String,Map<String,Double>>();
			
			myGui.appendUserGenTweetsResult("Calculating term frequency of user documents...");
			
			for (String followeeName: followeeCorpusAggregatedTweets.keySet())
			{
				List<Tweet> followerTweets = followeeCorpusAggregatedTweets.get(followeeName);
				for (Tweet currFollowerTweet : followerTweets)
				{
					String currFollowerName = currFollowerTweet.getUser();
					Map<String,Double> userTermVector = calculateTermFrequency(currFollowerTweet,followeeUniqueTerms.get(followeeName));
					allUserTermVectors.put(currFollowerName,userTermVector);
				}
			}
			
			// for (String user : allUserTermVectors.keySet())
			// {
				// System.out.println("Term Freq User: "+user);
				// for (String term : allUserTermVectors.get(user).keySet())
				// {
					// System.out.print(term+":"+allUserTermVectors.get(user).get(term)+",");
				// }
				// System.out.println();
			// }
			
			//Calculate document frequency of terms
			Map<String,Map<String,Double>> followeeDocumentFreqVectors = new LinkedHashMap<String,Map<String,Double>>();
			
			myGui.appendUserGenTweetsResult("Calculating document frequency of followee corpus...");
			
			for (String followeeName : followeeCorpusAggregatedTweets.keySet())
			{
				Map<String,Double> followeeDocFreqVector = createBlankTermVector(followeeUniqueTerms.get(followeeName));
				
				for (String followerName : followeeFollowerNames.get(followeeName))
				{
					followeeDocFreqVector = calculateDocFrequency(followeeDocFreqVector,allUserTermVectors.get(followerName));
				}
				
				followeeDocumentFreqVectors.put(followeeName,followeeDocFreqVector);
			}
			
			// for (String followee : followeeDocumentFreqVectors.keySet())
			// {
				// System.out.println("Doc Freq followee: "+followee);
				// for (String term : followeeDocumentFreqVectors.get(followee).keySet())
				// {
					// System.out.print(term+":"+followeeDocumentFreqVectors.get(followee).get(term)+",");
				// }
				// System.out.println();
			// }
			
			
			//Calculate tf-idf of users for each corpus
			Map<String,Map<String,Double>> allUserTfidfVectors = new LinkedHashMap<String,Map<String,Double>>();
			
			myGui.appendUserGenTweetsResult("Calculating tf-idf of each user in each followee corpus...");
			
			for (String followeeName : followeeFollowerNames.keySet())
			{
				Map<String,Double> currDocFreq = followeeDocumentFreqVectors.get(followeeName);
				for (String followerName : followeeFollowerNames.get(followeeName))
				{
					Map<String,Double> userTfidfVector = copyTermVector(allUserTermVectors.get(followerName));
					int totalDocuments = followeeFollowerNames.get(followeeName).size();
					double vectorMagnitude = 0.0;
					
					for (String term : userTfidfVector.keySet())
					{
						double idf = Math.log10(totalDocuments/currDocFreq.get(term)) / Math.log10(2);
						double tfidf = userTfidfVector.get(term)*idf;
						userTfidfVector.put(term,tfidf);
						vectorMagnitude+=tfidf * tfidf;
					}
					
					vectorMagnitude = Math.sqrt(vectorMagnitude);
					
					//precalculated the magnitude of vectors and element-wise division of documents to the magnitude x./|x| ie. normalize the document to unit vectors
					for (String term : userTfidfVector.keySet())
					{
						double tfidf = userTfidfVector.get(term);
						tfidf = tfidf / vectorMagnitude;
						userTfidfVector.put(term,tfidf);				
					}
					
					allUserTfidfVectors.put(followerName,userTfidfVector);
				}
				
			}
			
			// for (String user : allUserTfidfVectors.keySet())
			// {
				// Map<String,Double> tfidfVector = allUserTfidfVectors.get(user);
				
				// try {
					// FileWriter writer;
					// writer = new FileWriter("userGenTfidfVectors.txt", true); //append
					// BufferedWriter bufferedWriter = new BufferedWriter(writer);
					
					// String tweetText = aggregatedTweetsCorpus.get(user).getTweetText();
					// bufferedWriter.write(user+",");
					// for (String term : tfidfVector.keySet())
					// {
						// bufferedWriter.write(tfidfVector.get(term)+",");
					// }
					// bufferedWriter.newLine();
					// bufferedWriter.close();
				// } catch (IOException e) {
					/* // TODO Auto-generated catch block */
					// e.printStackTrace();
				// }
			// }
			
			//Store user tfidf into bins with words
			allUserTfidfBins = new LinkedHashMap<String,Map<Double,List<String>>>();
			
			myGui.appendUserGenTweetsResult("Creating tfidf word bins for each user...");
			
			for (String userName : allUserTfidfVectors.keySet())
			{
				Map<String,Double> currUserTfidf = allUserTfidfVectors.get(userName);
				Map<Double,List<String>> currUserTfidfBins = createTfidfBins(currUserTfidf);
				allUserTfidfBins.put(userName,currUserTfidfBins);
			}
			
			//************Calculate scale and shape parameter of weibull distribution, store them for generation**********
			Map<String,Double> allUserShapes = new LinkedHashMap<String,Double>();
			Map<String,Double> allUserScales = new LinkedHashMap<String,Double>();
			
			myGui.appendUserGenTweetsResult("Calculating shape and scale parameter for each user in each followee corpus...");
			
			for (String userName : allUserTfidfVectors.keySet())
			{
				System.out.println(userName);
				Map<String,Double> currUserTfidf = allUserTfidfVectors.get(userName);
				double[] tfidfArray = createTfidfArray(currUserTfidf);
				double[] parametersShapeScale = calculateWeibullShapeScaleParameter(tfidfArray);
				// double[] parametersShapeScale = calculateShapeScaleParameter(tfidfArray);
				// double[] parametersShapeScale1 = calculateWeibullShapeScaleParameter(tfidfArray);
				// double[] parametersShapeScale2 = calculateRegressionWeibullShapeScaleParameter1(tfidfArray);
				double[] parametersShapeScale3 = calculateRegressionWeibullShapeScaleParameter2(tfidfArray);
				// System.out.println("parametersShapeScale1 shape: "+parametersShapeScale1[0]+" scale: "+parametersShapeScale1[1]);
				// System.out.println("parametersShapeScale2 shape: "+parametersShapeScale2[0]+" scale: "+parametersShapeScale2[1]);
				System.out.println("parametersShapeScale3 shape: "+parametersShapeScale3[0]+" scale: "+parametersShapeScale3[1]);
				double currShape = parametersShapeScale[0];
				double currScale = parametersShapeScale[1];
				if (currShape < 0 || Double.isNaN(currShape) || currScale < 0 || Double.isNaN(currScale))
				{
					currShape = parametersShapeScale3[0];
					currScale = parametersShapeScale3[1];
					myGui.appendUserGenTweetsResult("Calculated shape and scale parameter for "+userName+" using Regressional Analysis...");
				}
				else
				{
					myGui.appendUserGenTweetsResult("Calculated shape and scale parameter for "+userName+" using Maximum Likelihood Estimate (MLE)...");
					
				}
				myGui.appendUserGenTweetsResult("Shape: "+currShape+"\tScale: "+currScale);
				
				allUserShapes.put(userName,currShape);
				allUserScales.put(userName,currScale);
			}
			
			//shape: 3.74567204, scale: 692.0880379
			// double[] values = {509.0,660.0,386.0,753.0,811.0,613.0,848.0,725.0,315.0,872.0,487.0,512.0};
			// double[] parametersShapeScale4 = calculateShapeScaleParameter(values);
			// double[] parametersShapeScale5 = calculateWeibullShapeScaleParameter(values);
			// double[] parametersShapeScale6 = calculateRegressionWeibullShapeScaleParameter1(values);
			// double[] parametersShapeScale7 = calculateRegressionWeibullShapeScaleParameter2(values);
			// System.out.println("parametersShapeScale4 shape: "+parametersShapeScale4[0]+" scale: "+parametersShapeScale4[1]);
			// System.out.println("parametersShapeScale5 shape: "+parametersShapeScale5[0]+" scale: "+parametersShapeScale5[1]);
			// System.out.println("parametersShapeScale6 shape: "+parametersShapeScale6[0]+" scale: "+parametersShapeScale6[1]);
			// System.out.println("parametersShapeScale7 shape: "+parametersShapeScale7[0]+" scale: "+parametersShapeScale7[1]);
			
			
			followeeShapeParameters = new LinkedHashMap<String,Double>(); //The average shape parameters for a followee group
			followeeScaleParameters = new LinkedHashMap<String,Double>(); //The average scale parameters for a followee group
			
			//Get the average shape and scale parameter for each followee group for generation
			for (String followeeName : followeeFollowerNames.keySet())
			{
				double sumShapeParameters = 0.0;
				double sumScaleParameters = 0.0;
				for (String followerName : followeeFollowerNames.get(followeeName))
				{
					sumShapeParameters+= allUserShapes.get(followerName);
					sumScaleParameters+= allUserScales.get(followerName);
				}
				
				followeeShapeParameters.put(followeeName,sumShapeParameters/followeeFollowerNames.get(followeeName).size());
				followeeScaleParameters.put(followeeName,sumScaleParameters/followeeFollowerNames.get(followeeName).size());
				
			}
			
			//Generate Tweets based on processedTweetsCorpus if no need to change total amount of generated tweets
			List<Tweet> generatedTweetsCorpus = new ArrayList<Tweet>();
			Set<String> generatedUniqueWords = new TreeSet<String>(); //Set of unique generated words for making tfidf vectors
			
			myGui.appendUserGenTweetsResult("************GENERATING USER TWEETS...************");
			
			DateFormat dateFormatName = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
			Date currentDate = new Date();
			String uniqueTime = dateFormatName.format(currentDate);
			
			String generatedFileName = corpusGenFile.getName().split("\\.(?=[^\\.]+$)")[0]+"_"+uniqueTime+"_GENERATED.txt";
			String generatedFilePath = "Dataset/TwitterObtained/GENERATED/"+ generatedFileName;
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			try {
				File generatedFile = new File(generatedFilePath);
				generatedFile.getParentFile().mkdirs();
				FileWriter genWriter = new FileWriter(generatedFile, true);
				BufferedWriter bufferedGenWriter = new BufferedWriter(genWriter);
				
				if (numArtificialTweets == 0 || numArtificialTweets < processedTweetsCorpus.size())
				{
					System.out.println("ENTERED HERE SAME GENERATED SIZE");
					myGui.appendUserGenTweetsResult("GENERATING same size as original real processed tweets...");
					myGui.appendUserGenTweetsResult(processedTweetsCorpus.size()+" tweets to be GENERATED...");
					
					for (Tweet currProcessedTweet : processedTweetsCorpus)
					{
						long currProcessedTweetId = currProcessedTweet.getTweetId();
						Date currProcessedTweetDate = currProcessedTweet.getDate();
						String currProcessedTweetUser = currProcessedTweet.getUser();
						String generatedTweetText = generateTweetText4(currProcessedTweetUser,findFolloweeName(currProcessedTweetUser,followeeFollowerNames));
						String[] generatedWords = generatedTweetText.split("\\s+");
						for (String word : generatedWords)
							generatedUniqueWords.add(word);
						
						myGui.appendUserGenTweetsResult("Username: "+currProcessedTweetUser+" GENERATED tweet: "+ generatedTweetText);
						Tweet generatedTweet = new Tweet(generatedTweetText,currProcessedTweetId,currProcessedTweetDate,currProcessedTweetUser);
						generatedTweetsCorpus.add(generatedTweet);

						bufferedGenWriter.write(findFolloweeName(currProcessedTweetUser,followeeFollowerNames));
						bufferedGenWriter.write("\t"+Long.toString(currProcessedTweetId));
						bufferedGenWriter.write("\t"+dateFormat.format(currProcessedTweet.getDate()));
						bufferedGenWriter.write("\t"+Long.toString(userTwitterId.get(currProcessedTweetUser)));
						bufferedGenWriter.write("\t"+currProcessedTweetUser);
						bufferedGenWriter.write("\t"+generatedTweetText);	
						bufferedGenWriter.newLine();
			
					}
				}
				else //Generate larger amount of tweets based on proportion of original tweet counts
				{
					System.out.println("ENTERED HERE GENERATED LARGER");
					myGui.appendUserGenTweetsResult("GENERATING larger size as original real processed tweets...");
					myGui.appendUserGenTweetsResult(numArtificialTweets+" tweets to be GENERATED...");
					
					double totalOriginalTweets = processedTweetsCorpus.size();
					long generatedTweetIndex = 1L;
					// DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					Date fakeDate = new Date();
					Calendar c = Calendar.getInstance(); 
					c.setTime(fakeDate); 
					c.add(Calendar.DATE, -1);
					fakeDate = c.getTime();
					
					String generatedDate = dateFormat.format(fakeDate);
					for (String userName : userTweetCounts.keySet())
					{
						double proportionTweets = userTweetCounts.get(userName)/totalOriginalTweets;
						int newTweetCount = (int) Math.round(numArtificialTweets*proportionTweets);
						System.out.println(userName+" originalTweetCount: "+userTweetCounts.get(userName)+" proportion: "+proportionTweets+" newTweetCount: "+newTweetCount);
						for (int i = 0; i < newTweetCount; i++ )
						{
							String generatedTweetText = generateTweetText4(userName,findFolloweeName(userName,followeeFollowerNames));
							Tweet generatedTweet = new Tweet(generatedTweetText,generatedTweetIndex,generatedDate,userName);
							String[] generatedWords = generatedTweetText.split("\\s+");
							for (String word : generatedWords)
								generatedUniqueWords.add(word);
							
							myGui.appendUserGenTweetsResult("Username: "+userName+" GENERATED tweet: "+ generatedTweetText);
							generatedTweetsCorpus.add(generatedTweet);
							generatedTweetIndex++;
							
							bufferedGenWriter.write(findFolloweeName(userName,followeeFollowerNames));
							bufferedGenWriter.write("\t"+Long.toString(generatedTweetIndex-1));
							bufferedGenWriter.write("\t"+dateFormat.format(fakeDate));
							bufferedGenWriter.write("\t"+Long.toString(userTwitterId.get(userName)));
							bufferedGenWriter.write("\t"+userName);
							bufferedGenWriter.write("\t"+generatedTweetText);	
							bufferedGenWriter.newLine();							
							
						}
					}			
					
				}
				bufferedGenWriter.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			} 
			
			
			
			System.out.println("generatedUniqueWords.size(): "+generatedUniqueWords.size());
			System.out.println("generatedTweetsCorpus.size(): "+generatedTweetsCorpus.size());
			
			// for (Tweet t : generatedTweetsCorpus)
			// {
				// System.out.println(t.getTweetText());
			// }
			
			//Aggregate generated tweets
			Map<String,Tweet> generatedAggregatedTweetsCorpus = new LinkedHashMap<String,Tweet>();
			
			myGui.appendUserGenTweetsResult("Aggregating GENERATED user tweets into a single document...");
			
			for (Tweet currTweet : generatedTweetsCorpus)
			{
				long generatedTweetId = currTweet.getTweetId();
				String currGeneratedTweetUserName = currTweet.getUser();
				Tweet currGeneratedAggregatedTweet;
								
				//Create tweet to hold aggregated tweet if not already made
				if (generatedAggregatedTweetsCorpus.containsKey(currGeneratedTweetUserName))
				{
					currGeneratedAggregatedTweet = generatedAggregatedTweetsCorpus.get(currGeneratedTweetUserName);
				}
				else
				{
					currGeneratedAggregatedTweet = new Tweet("",currTweet.getTweetId(),currTweet.getDate(),currTweet.getUser());
				}
				
				currGeneratedAggregatedTweet.setTweetText(currGeneratedAggregatedTweet.getTweetText()+currTweet.getTweetText());
				generatedAggregatedTweetsCorpus.put(currGeneratedTweetUserName,currGeneratedAggregatedTweet);
			}
			
			// for (String name : generatedAggregatedTweetsCorpus.keySet())
			// {
				// System.out.println(generatedAggregatedTweetsCorpus.get(name).getTweetText());
			// }
			
			//Calculate the term frequency of each generated user
			Map<String,Map<String,Double>> allGeneratedUserTermVectors = new LinkedHashMap<String,Map<String,Double>>();
			
			myGui.appendUserGenTweetsResult("Calculating term frequency of GENERATED user documents...");
			
			for (String generatedUserName : generatedAggregatedTweetsCorpus.keySet())
			{
				Tweet generatedUserTweet = generatedAggregatedTweetsCorpus.get(generatedUserName);
				Map<String,Double> generatedUserTermVector = calculateTermFrequency(generatedUserTweet,generatedUniqueWords);
				allGeneratedUserTermVectors.put(generatedUserName,generatedUserTermVector);
			}
			
			
			//Calculate df with generated tweets
				
			myGui.appendUserGenTweetsResult("Calculating document frequency of GENERATED tweets corpus...");
			
			Map<String,Double> generatedDocumentFreqVector = createBlankTermVector(generatedUniqueWords);
				
			for (String generatedUserName : allGeneratedUserTermVectors.keySet())
			{
				generatedDocumentFreqVector = calculateDocFrequency(generatedDocumentFreqVector,allGeneratedUserTermVectors.get(generatedUserName));
			}
							
			//Calcualte tf-idf with generated tweets
			
			Map<String,Map<String,Double>> allGeneratedUserTfidfVectors = new LinkedHashMap<String,Map<String,Double>>();
			
			myGui.appendUserGenTweetsResult("Calculating tf-idf of each GENERATED user in generated tweets corpus...");
			
			int totalDocuments = allGeneratedUserTermVectors.keySet().size();
			for (String generatedUserName : allGeneratedUserTermVectors.keySet())
			{
				Map<String,Double> generatedUserTfidfVector = copyTermVector(allGeneratedUserTermVectors.get(generatedUserName));
				double vectorMagnitude = 0.0;
				for (String term : generatedUserTfidfVector.keySet())
				{
					double idf = Math.log10(totalDocuments/generatedDocumentFreqVector.get(term)) / Math.log10(2);
					double tfidf = generatedUserTfidfVector.get(term)*idf;
					generatedUserTfidfVector.put(term,tfidf);
					vectorMagnitude+=tfidf * tfidf;
				}
					
				vectorMagnitude = Math.sqrt(vectorMagnitude);
					
				//precalculated the magnitude of vectors and element-wise division of documents to the magnitude x./|x| ie. normalize the document to unit vectors
				for (String term : generatedUserTfidfVector.keySet())
				{
					double tfidf = generatedUserTfidfVector.get(term);
					tfidf = tfidf / vectorMagnitude;
					generatedUserTfidfVector.put(term,tfidf);				
				}
				
				allGeneratedUserTfidfVectors.put(generatedUserName,generatedUserTfidfVector);
				
			}
						
			//Cluster with generated tweets
			
			//k-means on aggregated generated tweets as document vectors
			int kClusters = 3; //number of k clusters
			boolean convergence = false; //documents remain in the same clusters
			int maxIterations = 10;

			List<Cluster> allClusters = new ArrayList<Cluster>();
			List<String> allUserNames = new ArrayList<String>(allGeneratedUserTfidfVectors.keySet());
			List<String> remainingUserNames = new ArrayList<String>(allGeneratedUserTfidfVectors.keySet()); //Remaining usernames after initial usernames chosen for seeds

			Map<String,Double> centroidTFIDF = new LinkedHashMap<String,Double>();
			Map<String,Double> baseCentroidTFIDF = new LinkedHashMap<String,Double>(); //tfidf of 0.0 for all unique doc terms
			List<List<Point>> prevListPoints = new ArrayList<List<Point>>();

			if (kClusters > allGeneratedUserTfidfVectors.keySet().size())
				kClusters = allGeneratedUserTfidfVectors.keySet().size();
			
			baseCentroidTFIDF = createBlankTermVector(generatedUniqueWords);
			
			System.out.println("Calculating k-means");
			myGui.appendUserGenTweetsResult("k-means Clustering...");

			//Choose random initial points for cluster centroid
			for (int i = 0; i < kClusters; i++)
			{
				System.out.println("remainingUserNames: "+remainingUserNames);
				String initialUserName;
				Collections.shuffle(remainingUserNames);
				System.out.println("shuffled remainingUserNames: "+remainingUserNames);
				
				if (remainingUserNames.contains("RyersonU"))
				{
					initialUserName = "RyersonU";
					remainingUserNames.remove(initialUserName);
				}
				else if (remainingUserNames.contains("TheCatTweeting"))
				{
					initialUserName = "TheCatTweeting";
					remainingUserNames.remove(initialUserName);
				}
				else if (remainingUserNames.contains("weathernetwork"))
				{
					initialUserName = "weathernetwork";
					remainingUserNames.remove(initialUserName);
				}
				else
				{
					initialUserName = remainingUserNames.get(0);
					remainingUserNames.remove(0);
				}
				
				System.out.println("initialUserName: "+initialUserName);
				
				Cluster cluster = new Cluster(i);
				Map<String,Double> currTFIDF = allGeneratedUserTfidfVectors.get(initialUserName);
				Point initialPoint = new Point(initialUserName,currTFIDF);

				initialPoint.setCluster(i);
				cluster.addPoint(initialPoint);

				centroidTFIDF = new LinkedHashMap<String,Double>(baseCentroidTFIDF);

				for (String term : currTFIDF.keySet())
				{
					centroidTFIDF.put(term,currTFIDF.get(term));
				}

				Point centroid = new Point(-1,centroidTFIDF);
				cluster.setCentroid(centroid);

				allClusters.add(cluster);
			}



			//Create the initial clusters
			// for (Cluster currCluster: allClusters)
			// {
				// List<Point> points = currCluster.getPoints();
				// System.out.println(currCluster.getPoints());
					// for (Point p : points)
					// {
						// System.out.println(p.getTweetId());
					// }
					// System.out.println("currCluster centroid: "+currCluster.getCentroid());

			// }

			// for (int i = 0; i < kClusters; i++) {
					// Cluster c = allClusters.get(i);
					// c.plotClusterTweets();
				// }


			//Assign remaining points to the closest cluster
			//assignCluster();
			double highestCosSim = 0.0; 
			int cluster = 0;                 
			double cosSim = 0.0; 

			for(String currUserName : remainingUserNames) 
			{
				Map<String,Double> currTFIDF = allGeneratedUserTfidfVectors.get(currUserName);
				Point currPoint = new Point(currUserName,currTFIDF);

				Cluster c;

				for(int i = 0; i < kClusters; i++) 
				{
					c = allClusters.get(i);
					cosSim = currPoint.cosSimDistance(c.getCentroid());
					if(cosSim > highestCosSim)
					{
						highestCosSim = cosSim;
						cluster = i;
					}
				}
				currPoint.setCluster(cluster);
				allClusters.get(cluster).addPoint(currPoint);

				highestCosSim = 0.0;
			}


			//Calculate new centroids.
			//calculateCentroids();
			for(Cluster clusterI : allClusters) {

				List<Point> listOfPoints = clusterI.getPoints();
				int numPoints = listOfPoints.size();

				Point centroid = clusterI.getCentroid();
				if(numPoints > 0) {
					Map<String,Double> newCentroidTFIDF = new LinkedHashMap<String,Double>(baseCentroidTFIDF);
					for (Point p : listOfPoints)
					{
						Map<String,Double> currTFIDF = p.getTfidf_or_Tf();
						for (String term : currTFIDF.keySet())
						{
							double pointTFIDFValue = 0.0;
							double centroidTFIDFValue = 0.0;
							pointTFIDFValue = currTFIDF.get(term);
							centroidTFIDFValue = newCentroidTFIDF.get(term);
							newCentroidTFIDF.put(term, pointTFIDFValue+centroidTFIDFValue);
						}
					}

					double newClusterMag = 0.0;
					double currTermTFIDF = 0.0;

					for (String term : newCentroidTFIDF.keySet())
					{
						currTermTFIDF = 0.0;
						currTermTFIDF = newCentroidTFIDF.get(term);
						
						if (currTermTFIDF > 0.0)
						{
							currTermTFIDF = currTermTFIDF / numPoints;
							newClusterMag += currTermTFIDF * currTermTFIDF;
							newCentroidTFIDF.put(term, currTermTFIDF);
						}

					}

					//Normalize the new cluster centroid as it might not be normalized

					newClusterMag = Math.sqrt(newClusterMag);
					for (String term : newCentroidTFIDF.keySet())
					{
						currTermTFIDF = newCentroidTFIDF.get(term);
						
						if (currTermTFIDF > 0.0)
						{
							currTermTFIDF = currTermTFIDF / newClusterMag;
							newCentroidTFIDF.put(term, currTermTFIDF);
						}

					}

					centroid.setTfidf_or_Tf(newCentroidTFIDF);
					clusterI.setCentroid(centroid);
				}
			}

//			System.out.println("~~~~~~~~~~~~~~~~~~~TESTING IF CENTROID UNIT VECTOR~~~~~~~~~~~~~~~~~~~~~~~");
			Double sumOfMags;
			Point cent;
			for(Cluster clusterI : allClusters) {
				sumOfMags = 0.0;
				cent = clusterI.getCentroid();
				Map<String,Double> centtfidf = cent.getTfidf_or_Tf();
				for (String term : centtfidf.keySet())
				{
					sumOfMags += centtfidf.get(term)*centtfidf.get(term);
				}
				sumOfMags = Math.sqrt(sumOfMags);
//				System.out.println("cluster #"+clusterI.getId()+" sumOfMags: "+sumOfMags);
//				System.out.println(cent.getTfidf_or_Tf());

			}


			System.out.println("#################");
			System.out.println("Iteration: " + 0);


			for (int i = 0; i < kClusters; i++) {
				Cluster c = allClusters.get(i);
				//c.plotClusterTweets();
				prevListPoints.add(c.getPoints());
				//System.out.println("prevListPoints: "+prevListPoints.get(i));
			}


			//Calculate k-means calculate()
			int iteration = 0;

			//Iterate k-means ********************************************************************************
			while(!convergence && iteration < maxIterations) {

				//Clear cluster state
				for(Cluster clusterK : allClusters) {
					clusterK.clear();
					//System.out.println("clusterK: "+clusterK.getPoints());
				}


				//getCentroids()
				List centroids = new ArrayList(kClusters);
				for(Cluster clusterH : allClusters) {
					Point currCentroid = clusterH.getCentroid();
					Point point = new Point(currCentroid.getTweetId(),currCentroid.getTfidf_or_Tf());
					centroids.add(point);
				}
				List<Point> lastCentroids = centroids;

				//Assign points to the closer cluster
				//assignCluster();
				highestCosSim = 0.0; 
				cluster = 0;                 
				cosSim = 0.0; 

				for(String currUserName : allUserNames) {

					Map<String,Double> currTFIDF = allGeneratedUserTfidfVectors.get(currUserName);
					Point currPoint = new Point(currUserName,currTFIDF);

					for(int i = 0; i < kClusters; i++) {
						Cluster c = allClusters.get(i);
						cosSim = currPoint.cosSimDistance(c.getCentroid());
						if(cosSim >= highestCosSim){
							highestCosSim = cosSim;
							cluster = i;
						}
					}
					currPoint.setCluster(cluster);
					allClusters.get(cluster).addPoint(currPoint);

					highestCosSim = 0.0;
				}


				//Get the current list of points
				List<List<Point>> currListPoints = new ArrayList<List<Point>>();

				//Calculate new centroids.
				//calculateCentroids();

				for(Cluster clusterI : allClusters) {

					List<Point> listOfPoints = clusterI.getPoints();
					currListPoints.add(listOfPoints);

					int numPoints = listOfPoints.size();

					Point centroid = clusterI.getCentroid();
					if(numPoints > 0) {
						Map<String,Double> newCentroidTFIDF = new LinkedHashMap<String,Double>(baseCentroidTFIDF);
						for (Point p : listOfPoints)
						{
							Map<String,Double> currTFIDF = p.getTfidf_or_Tf();

							for (String term : currTFIDF.keySet())
							{
								double pointTFIDFValue = 0.0;
								double centroidTFIDFValue = 0.0;
								pointTFIDFValue = currTFIDF.get(term);
								centroidTFIDFValue = newCentroidTFIDF.get(term);
								newCentroidTFIDF.put(term, pointTFIDFValue+centroidTFIDFValue);
							}
						}

						double newClusterMag = 0.0;
						double currTermTFIDF = 0.0;

						for (String term : newCentroidTFIDF.keySet())
						{
							currTermTFIDF = 0.0;
							currTermTFIDF = newCentroidTFIDF.get(term);
							if (currTermTFIDF > 0.0)
							{
								currTermTFIDF = currTermTFIDF / numPoints;
								newClusterMag += currTermTFIDF * currTermTFIDF;
								newCentroidTFIDF.put(term, currTermTFIDF);
							}

						}

						//Normalize the new cluster centroid as it might not be normalized

						newClusterMag = Math.sqrt(newClusterMag);
						for (String term : newCentroidTFIDF.keySet())
						{
							currTermTFIDF = newCentroidTFIDF.get(term);

							if (currTermTFIDF > 0.0)
							{
								currTermTFIDF = currTermTFIDF / newClusterMag;
								newCentroidTFIDF.put(term, currTermTFIDF);
							}

						}

						centroid.setTfidf_or_Tf(newCentroidTFIDF);
						clusterI.setCentroid(centroid);
					}
				}


				System.out.println("~~~~~~~~~~~~~~~~~~~TESTING IF CENTROID UNIT VECTOR AFTER ITERATION "+iteration+"~~~~~~~~~~~~~~~~~~~~~~~");

				for(Cluster clusterI : allClusters) {
					sumOfMags = 0.0;
					cent = clusterI.getCentroid();
					Map<String,Double> centtfidf = cent.getTfidf_or_Tf();
					for (String term : centtfidf.keySet())
					{
						sumOfMags += centtfidf.get(term)*centtfidf.get(term);
					}
					sumOfMags = Math.sqrt(sumOfMags);
//							System.out.println("cluster #"+clusterI.getId()+" sumOfMags: "+sumOfMags);
//							System.out.println(cent.getTfidf_or_Tf());
				}

				iteration++;


				//Check if convergence
				convergence = true;

				for (int i = 0; i < kClusters; i++) {
					List<Point> prevList = prevListPoints.get(i);
					List<Point> currList = currListPoints.get(i);
					//System.out.println("prevList "+prevList);
					//System.out.println("currList "+currList);
					for (Point p : prevList)
					{

						if (!currList.contains(p))
						{
							convergence = false;
							break;
						}
					}
					if (convergence == false)
						break;

				}

				if (convergence == false)
					prevListPoints = currListPoints;

//						System.out.println("#################");
//						System.out.println("Iteration: " + iteration);


				//	for (int i = 0; i < kClusters; i++) {
				//		Cluster c = allClusters.get(i);
				//		c.plotClusterTweets();
				//	}

			}

			//Output the clusters and the tweets
			System.out.println("THE FINAL CLUSTERS "+ getLocalName());
			myGui.appendClustersResult("************THE FINAL CLUSTERS************");
			for (Cluster c : allClusters)
			{
				String resultText = c.getClusterResultAreaText();
				myGui.appendClustersResult(resultText);
			}
		
			//Show cluster results
		
			myGui.appendUserGenTweetsResult("FINISHED USER GENERATED SIMULATION");
			myGui.appendUserGenTweetsResult("=======================================================================================");
			myGui.appendUserGenTweetsResult("");
			
			myGui.enableUserGenSimButtons();
			myGui.showMessageBox("finished user generated simulation");
			
			System.out.println(getLocalName()+" Finished User Generated Simulation");
			
			doDelete();	
				
		}
	}	

	public Map<String,Double> copyTermVector(Map<String,Double> vectorToCopy)
	{
		Map<String,Double> copyVector = new LinkedHashMap<String,Double>();
		for (String term : vectorToCopy.keySet())
		{
			copyVector.put(term,vectorToCopy.get(term));
		}
		return copyVector;
	}
	
	public Map<String,Double> createBlankTermVector(Set<String> uniqueTerms)
	{
		Map<String,Double> blankTermVector = new LinkedHashMap<String,Double>();
		for (String term: uniqueTerms)
		{
			blankTermVector.put(term,0.0);
		}
		return blankTermVector;
	}
	
	public Map<String,Double> calculateTermFrequency(Tweet document,Set<String> uniqueTerms)
	{
		Map<String,Double> termVector = new LinkedHashMap<String,Double>();
		String[] terms = document.getTweetText().split("\\s+");
		// System.out.println("uniqueTerms.size(): "+uniqueTerms.size());
		
		for (String term : uniqueTerms)
		{
			double termFreq = 0.0;
			termVector.put(term,termFreq);
			// System.out.println("term: "+term+" termFreq: "+termFreq);
		}
		
		
		for (String term : terms)
		{
			double termFreq = termVector.get(term);
			termFreq++;
			termVector.put(term,termFreq);
		}
		
		// Normalize document
		// for (String term : terms)
		// {
			// double termFreq = termVector.get(term)/terms.length;
			// termVector.put(term,termFreq);
		// }
		
		return termVector;
	}
	
	public Map<String,Double> calculateDocFrequency(Map<String,Double> currDocFreqVector, Map<String,Double> userTermVector)
	{
		for (String term : currDocFreqVector.keySet())
		{
			if (userTermVector.get(term) > 0.0)
			{
				double currDocFreq = currDocFreqVector.get(term);
				currDocFreq++;
				currDocFreqVector.put(term,currDocFreq);
			}
		}
		return currDocFreqVector;
	}
	
	public Map<String,Set<String>> findUniqueWordsFolloweeCorpus(Map<String,List<Tweet>> followeeCorpusAggregatedTweets)
	{
		Map<String,Set<String>> followeeUniqueTerms = new LinkedHashMap<String,Set<String>>();
		for (String followeeName : followeeCorpusAggregatedTweets.keySet())
		{
			Set<String> uniqueTerms = new TreeSet<String>();
			List<Tweet> followerTweets = followeeCorpusAggregatedTweets.get(followeeName);
			
			for (Tweet currFollowerTweets : followerTweets)
			{
				String[] termsInTweet = currFollowerTweets.getTweetText().split("\\s+");
				
				for (String term : termsInTweet)
				{
					uniqueTerms.add(term);
				}
			}
			
			followeeUniqueTerms.put(followeeName,uniqueTerms);
		}
		return followeeUniqueTerms;
	}
	
	public Map<Double,List<String>> createTfidfBins (Map<String,Double> tfidfVector)
	{
		Map<Double,List<String>> tfidfBins = new LinkedHashMap<Double,List<String>>();
		for (String term : tfidfVector.keySet())
		{
			double tfidfValue = tfidfVector.get(term);
			List<String> wordsInBin;
			
			//Get list of words from bin if it exists otherwise create new list
			if (tfidfBins.containsKey(tfidfValue))
			{
				wordsInBin = tfidfBins.get(tfidfValue);
			}
			else
			{
				wordsInBin = new ArrayList<String>();
			}
			
			//Only add word to bin if it is not in list already
			if (!wordsInBin.contains(term))
			{
				wordsInBin.add(term);
				tfidfBins.put(tfidfValue,wordsInBin);
			}
				
		}
		return tfidfBins;
	}

	public double[] createTfidfArray (Map<String,Double> tfidfVector)
	{
		double[] tfidfArray = new double[tfidfVector.keySet().size()];
		int index = 0;
		for (String term : tfidfVector.keySet())
		{
			tfidfArray[index] = tfidfVector.get(term);
			index++;
		}
		return tfidfArray;
	}
	
	//Calculate shape and scale parameter for weibull distribution
	public double[] calculateShapeScaleParameter (double[] observations)
	{	
		double shape = 0.0;
		//1. Calculate the average
		Mean mean = new Mean();
		double average = mean.evaluate(observations,0,observations.length);
		//2. Calculate the sample standard deviation
		StandardDeviation sampleSd = new StandardDeviation(true);
		double sd = sampleSd.evaluate(observations,average);
		//3. Find signal-to-noise ratio (average/sd)
		double signalToNoiseRatio = average/sd;
		//4. Estimate shape parameter, m, by using the following formula: m = 1.2785 (average/sd) - 0.5004
		shape = Math.abs(1.2785 * signalToNoiseRatio - 0.5004);
		
		//5.Estimate scale, A, from sample average and estimate of m in step 4 by using the following formula: A = average/(G(1+1/m)), where G(x) is the gamma function G(x)= (x-1)!
		int x = (int) Math.round(1+1/shape);
		// System.out.println("average: "+average+" sd: "+sd+" signal-to-noise: "+signalToNoiseRatio+" shape: "+shape+ " x: "+x);
		double scale = 0.0;
		scale = average / CombinatoricsUtils.factorialDouble(x-1);
		System.out.println("average: "+average+" sd: "+sd+" signal-to-noise: "+signalToNoiseRatio+" shape: "+shape+ " x: "+x+" scale: "+scale);
		
		//average: 0.002503089102286171 sd: 0.013791191690198433 signal-to-noise: 0.18149911613984357 shape: -0.2683533800152099 x: -3
		
		double[] shapeScale = new double[2];
		shapeScale[0] = shape;
		shapeScale[1] = scale;
		return shapeScale;
	}
	
	//Simulation book, alpha is shape, beta is scale
	public double[] calculateWeibullShapeScaleParameter (double[] observations)
	{
		//Eq 1. (Sum(X^shape * ln X) / Sum(X^shape)) - (1/shape)
		//Eq 2. Sum(ln X) / n
		//Shape is found by newton's method so that Eq1 = Eq2 must be satisfied
		//Scale is ( Sum(X ^ shape) / n ) ^ (1/shape)
		//shape0 = ( ((6/Math.pi^2)*( Sum((ln X)^2) - ( ((Sum(ln X))^2) / n )))/(n-1))^ (-1/2)
		Sum sumHelper = new Sum();
		int n = observations.length;
		boolean convergence = false;
		
		for (int i = 0; i < observations.length; i++)
		{
			if (observations[i] == 0.0)
				observations[i] = 1E-8;
		}
		
		
		double[] naturalLogData = naturalLogDataArray(observations);
		double initialShapeEstimate = Math.pow( ( (6/Math.pow(Math.PI,2))*( sumHelper.evaluate(powerDataArray(naturalLogData,2),0,n) - ( Math.pow(sumHelper.evaluate(naturalLogData,0,n),2) / n ) ) ) / (n-1) , -1.0/2.0 ) ;
		double prevShapeEstimate = initialShapeEstimate;		
		double currShapeEstimate = initialShapeEstimate;
		System.out.println("initialShapeEstimate: "+initialShapeEstimate + "initialScaleParameter: "+Math.pow(sumHelper.evaluate(powerDataArray(observations,initialShapeEstimate),0,n)/n,(1/initialShapeEstimate)));
		
		
		int iteration = 0;
		int maxIteration = 20;
		while (!convergence && iteration < maxIteration)
		{
			double functionA = sumHelper.evaluate(naturalLogData,0,n) / n;
			double functionB = sumHelper.evaluate(powerDataArray(observations,prevShapeEstimate),0,n);
			double functionC = sumHelper.evaluate(productDataArray(powerDataArray(observations,prevShapeEstimate),naturalLogData),0,n);
			double functionH = sumHelper.evaluate(productDataArray(powerDataArray(observations,prevShapeEstimate),powerDataArray(naturalLogData,2)),0,n);	
			System.out.println("functionA: "+functionA);
			System.out.println("functionB: "+functionB);
			System.out.println("functionC: "+functionC);
			System.out.println("functionH: "+functionH);
			
			currShapeEstimate = prevShapeEstimate + ((functionA + (1/prevShapeEstimate) - (functionC/functionB)) / ( (1/Math.pow(prevShapeEstimate,2)) + ((functionB * functionH - Math.pow(functionC,2)) / Math.pow(functionB,2) ) ));
			
			System.out.println("currShapeEstimate: "+currShapeEstimate + " currScaleEstimate: "+Math.pow(sumHelper.evaluate(powerDataArray(observations,currShapeEstimate),0,n)/n,(1/currShapeEstimate)));
			
			if (Precision.equals(currShapeEstimate,prevShapeEstimate,1E-8))
			{
				convergence = true;
			}
			// else if (currShapeEstimate < 0)
			// {
				// currShapeEstimate = initialShapeEstimate;
				// convergence = true;
			// }
			else
			{
				prevShapeEstimate = currShapeEstimate;
			}
			
			iteration++;
		}
		
		System.out.println("iteration: "+iteration);
		
		double shape = currShapeEstimate;
		double scale = Math.pow(sumHelper.evaluate(powerDataArray(observations,shape),0,n)/n,(1/shape));
		System.out.println("shape: "+shape+" scale: "+scale);
		
		double[] shapeScale = new double[2];
		shapeScale[0] = shape;
		shapeScale[1] = scale;
		return shapeScale;
	}
		
	//helper to take natural log of values in array
	public double[] naturalLogDataArray (double[] observations)
	{
		double[] naturalLogData = new double[observations.length];
		for (int i = 0; i < naturalLogData.length; i++)
		{
			naturalLogData[i] = Math.log(observations[i]);
		}
		return naturalLogData;
	}
	
	//helper to take the values in an array to the power given
	public double[] powerDataArray (double[] observations,double power)
	{
		double[] poweredData = new double[observations.length];
		for (int i = 0; i < poweredData.length; i++)
		{
			poweredData[i] = Math.pow(observations[i],power);
		}
		return poweredData;
	}
	
	//helper to multiply the values of two arrays
	public double[] productDataArray(double[] observations1, double[] observations2)
	{
		double[] productData = new double[observations1.length];
		for (int i = 0; i < productData.length; i++)
		{
			productData[i] = observations1[i] * observations2[i];
		}
		return productData;
	}
	
	//Wrong implementation XXX Bad
	//https://www.researchgate.net/post/How_can_I_determine_weibull_parameters_from_data
	public double[] calculateRegressionWeibullShapeScaleParameter1 (double[] observations)
	{
		
		//1. Sort data into ascending order
		double[] sortedAscendedData = Arrays.copyOf(observations,observations.length);
		Arrays.sort(sortedAscendedData);
		for (int i = 0; i < sortedAscendedData.length; i++)
		{
			if (sortedAscendedData[i] == 0.0)
				sortedAscendedData[i] = 1E-8;
		}
		//2. Assign them a rank, such that the lowest data point is 1, second lowest is 2, etc.
		double[] rankData = new double[sortedAscendedData.length];
		for (int i = 0; i < rankData.length; i++)
		{
			rankData[i] = i+1.0;
		}
		//3. Assign each data point a probability. For beginners, i recommend (i-0.5)/n, where i and n are rank and sample size, respectively.
		double[] dataProbability = new double[sortedAscendedData.length];
		for (int i = 0; i < dataProbability.length; i++)
		{
			dataProbability[i] = (rankData[i]-0.5)/dataProbability.length;
		}
		//4. Take natural log of data.
		double[] naturalLogData = new double[dataProbability.length];
		for (int i = 0; i < naturalLogData.length; i++)
		{
			naturalLogData[i] = Math.log(dataProbability[i]);
		}
		//5. Calculate ln (-ln (1-P)) for every data, where P is probabiliyy calculated in step 3.
		double[] regressionData = new double[naturalLogData.length];
		for (int i = 0; i < regressionData.length; i++)
		{
			regressionData[i] = Math.log(-Math.log(1-naturalLogData[i]));
		}
		
		SimpleRegression regression = new SimpleRegression();
		for (int i = 0; i < regressionData.length; i++)
		{
			regression.addData(naturalLogData[i],regressionData[i]);
		}
		
		double shape = regression.getSlope();
		double intercept = regression.getIntercept();
		//The intercept is the negative of the product of shape parameter and natural log of scale parameter.
		double scale = Math.exp(-intercept/shape);
		
		double[] shapeScale = new double[2];
		shapeScale[0] = shape;
		shapeScale[1] = scale;
		return shapeScale;
	}
	
	//Works
	//http://www.real-statistics.com/distribution-fitting/fitting-weibull-regression/
	public double[] calculateRegressionWeibullShapeScaleParameter2 (double[] observations)
	{
		
		//1. Sort data into ascending order
		double[] sortedAscendedData = Arrays.copyOf(observations,observations.length);
		Arrays.sort(sortedAscendedData);
		for (int i = 0; i < sortedAscendedData.length; i++)
		{
			if (sortedAscendedData[i] == 0.0)
				sortedAscendedData[i] = 1E-8;
		}
		//2. Assign them a rank, such that the lowest data point is 1, second lowest is 2, etc.
		double[] rankData = new double[sortedAscendedData.length];
		for (int i = 0; i < rankData.length; i++)
		{
			rankData[i] = i+1.0;
		}
		//3. Take natural log of data.
		double[] naturalLogData = new double[sortedAscendedData.length];
		for (int i = 0; i < naturalLogData.length; i++)
		{
			naturalLogData[i] = Math.log(sortedAscendedData[i]);
		}
		//4. Calculate F(x)
		double[] estimatedFData = new double[naturalLogData.length];
		for (int i = 0; i < estimatedFData.length; i++)
		{
			estimatedFData[i] = (rankData[i]-0.5)/rankData.length;
		}
		//5. Calculate ln (-ln (1-F(x)) )
		double[] regressionData = new double[naturalLogData.length];
		for (int i = 0; i < regressionData.length; i++)
		{
			regressionData[i] = Math.log(-Math.log(1-estimatedFData[i]));
		}
		
		SimpleRegression regression = new SimpleRegression();
		for (int i = 0; i < regressionData.length; i++)
		{
			regression.addData(naturalLogData[i],regressionData[i]);
		}
		
		//6. Calculate Slope (shape)
		double shape = regression.getSlope();
		
		//7. - Shape ln (Scale) from intercept. The intercept is the negative of the product of shape parameter and natural log of scale parameter.
		double intercept = regression.getIntercept();
		
		//8. Scale = EXP ( - Shape ln (Scale) / Shape)
		double scale = Math.exp(-intercept/shape);
		
		double[] shapeScale = new double[2];
		shapeScale[0] = shape;
		shapeScale[1] = scale;
		return shapeScale;
	}
	
	
	//generate tweets based on individual users
	public String generateTweetText4(String tweetUserName, String followeeName)
	{

		String generatedTweetText = "";
		double userShape = followeeShapeParameters.get(followeeName); //Average shape parameter from original corpus
		double userScale = followeeScaleParameters.get(followeeName); //Average scale parameter from original corpus

		int wordsInTweet = r.nextInt((maxWordsFolloweeGroup.get(followeeName) - MIN_WORDS) + 1) + MIN_WORDS;		

		for (int j = 0; j < wordsInTweet; j++) {

			String word = "";

			word = generateWord4(tweetUserName,userShape, userScale);
			generatedTweetText += word + " ";
		}

		return generatedTweetText;
	}
	
	//generate a word based on individual user tfidf
	public String generateWord4(String currTweetUserName, double currUserShape, double currUserScale)
	{
		String generatedWord = "";
		List<String> bagOfWordsFound = new ArrayList<String>();

		double x = randomDataGenerator.nextWeibull(currUserShape, currUserScale);

		x = randomDataGenerator.nextWeibull(currUserShape, currUserScale);

		int indexOfWord = 0;
		int indexClosest = 0;

		Map<Double,List<String>> currUserTfidfWordsBins = allUserTfidfBins.get(currTweetUserName); 
		
		List<Double> currTfidfValues = new ArrayList<Double>(currUserTfidfWordsBins.keySet());
		Collections.sort(currTfidfValues);
		
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
	
		List<String> wordsFromBin = currUserTfidfWordsBins.get(closestTfidfValue);

		indexOfWord = r.nextInt(wordsFromBin.size());

		generatedWord = wordsFromBin.get(indexOfWord);

		return generatedWord;
	}
	
	public String findFolloweeName(String followerName, Map<String,List<String>> followeeFollowerNames)
	{
		String followerFolloweeName = "";
		for (String followeeName : followeeFollowerNames.keySet())
		{
			List<String> followerNames = followeeFollowerNames.get(followeeName);
			if (followerNames.contains(followerName))
			{
				followerFolloweeName = followeeName;
				break;
			}
		}
		return followerFolloweeName;
	}
	
	//Read text file of tweets and store into InMemoryDb
	public void readFromTextFile()
	{
		try {
			final char END_OF_TWEET = '\r';
			int character;
			StringBuffer lineBuffer = new StringBuffer(1024);
			FileInputStream fileInput = new FileInputStream(corpusGenFile);
			BufferedInputStream bufferedInput = new BufferedInputStream(fileInput);
			

			Long tweetId;
			Long userId;
			String tweetDate;
			String currentUserName;
			String tweetText;
			String referenceUser;
			boolean dateRangeValid;
			Tweet currentTweet;
			
			
			int tweetsReadCount = 0;
			followeeCorpusTweetIds = new LinkedHashMap<String,List<Long>>();
			tweetIdFolloweeName = new LinkedHashMap<Long,String>();
			List<Long> currentFolloweeTweetIds;
			userTwitterId = new LinkedHashMap<String,Long>();
			
			while((character=bufferedInput.read())!=-1) {

				if (character==END_OF_TWEET) {
					character = bufferedInput.read();
					if (character!=-1 && character !='\n')
					{
						lineBuffer.append((char)character);
					}
					else if (character!=-1 && character == '\n')
					{

						String info[] = lineBuffer.toString().split("\t",6);
						lineBuffer.setLength(0);

						referenceUser = info[0];
						tweetId = Long.valueOf(info[1]);
						tweetDate = info[2];
						userId = Long.valueOf(info[3]);
						currentUserName = info[4];
						tweetText = info[5];
						
						currentTweet = new Tweet(tweetText,tweetId,tweetDate,currentUserName);
						
						if (!userTwitterId.containsKey(currentUserName))
							userTwitterId.put(currentUserName,userId);
						
						if (followeeCorpusTweetIds.keySet().contains(referenceUser))
						{
							currentFolloweeTweetIds = followeeCorpusTweetIds.get(referenceUser);
						}
						else
						{
							currentFolloweeTweetIds = new ArrayList<Long>();
						}
						
						currentFolloweeTweetIds.add(tweetId);
						followeeCorpusTweetIds.put(referenceUser,currentFolloweeTweetIds);
						
						tweetIdFolloweeName.put(tweetId,referenceUser);
						
						tweetsReadCount++;
						
						localDb.addTweet(currentTweet);

					}
				} else {
					lineBuffer.append((char) character);
				}
			}

			bufferedInput.close();
			fileInput.close();

			System.out.println(getLocalName()+" finished reading textfile lines: "+ tweetsReadCount);
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
		
	protected void takeDown() {
		try {
			DFService.deregister(this);
			System.out.println(getLocalName()+" DEREGISTERED WITH THE DF");
			//doDelete();
		} catch (FIPAException e) {
			e.printStackTrace();
		}

	}


}
