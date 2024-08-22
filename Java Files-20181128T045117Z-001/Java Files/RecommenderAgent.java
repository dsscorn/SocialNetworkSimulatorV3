package TwitterGatherDataFollowers.userRyersonU;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.StringJoiner;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.functions.SMO;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.FastVector;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.Remove;


import java.awt.AWTException;
import java.awt.Robot;


public class RecommenderAgent extends Agent 
{	
	private final long serialVersionUID = 1L;
	private static final int HASH_TAGS = 1;
	private static final int RE_TWEETS = 1;
	private static final int STOP_WORDS = 1;
	public static final int COS_SIM = 0;
	public static final int K_MEANS = 1;
	public static final int SVM = 2;
	private static final double TEST_SET_PERCENT = 0.30;
	private static final double TRAIN_SET_PERCENT = 0.70;

	private AID controllerAgent;

	private String referenceUser; 
	private String tweetNum_temp2;
	private String dc1string_temp;
	private String dc2string_temp;
	private int    hashtags_temp;

	private int    retweetedby_temp;
	private int    stopWordFlag_temp;
	private int    stemFlag_temp;
	private int    numberofAgents_temp;	  
	private int    numberofAgents_tempint;
	private int    temp=0;


	private int connectedtoTfidfservernumber_temp;	  
	private int connectedtoTfidfservernumber;
	private int connectedtoRecservernumber_temp;	  
	private int connectedtoRecservernumber;

	private AID[] allRecommenderAgents;	
	private AID[] allUserAgentConnectedtoThisServer;		
	private AID   AID_agent_name;
	private String agentName;



	private int tweetCount = 0; //Number of tweets currently received from user agents
	private int tweetsToReceive = 100; //Total number of tweets the recommender is supposed to receive from user agents

	private int numRecAgents =0;

	static String serverName = "127.0.0.1";
	static String portNumber = "3306";
	static String sid = "testmysql";

	private Connection con;
	private Statement stmt = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;

	static String user = "root";
	static String pass = "Asdf1234";


	private ArrayList<String> textprocessing_wb_or_tfidf_Data = new ArrayList<String>();		

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


	private String nodeNumber ="";

	//@Jason added algorithmRec
	private int algorithmRec = 0;

	//@Jason added convId
	private String convId = "";

	//@Jason added boolean to only calculate once
	private boolean calculateAlready=false;

	//@Jason added list of completed users for recommendation
	private ArrayList<String> completedUsers = new ArrayList<String>();
	private int countUsersCosim = 0;
	private int countScores = 0;
	private long firstTweetTime;
	

	//Necessary @Jason
	//private LinkedHashMap<String,ArrayList<String>> allUserDocuments = new LinkedHashMap<String,ArrayList<String>>();
	private LinkedHashMap<String,Double> aggregatedUserTweets = new LinkedHashMap<String,Double>();
	private LinkedHashMap<String,LinkedHashMap<String,Double>> allUserDocuments = new LinkedHashMap<String,LinkedHashMap<String,Double>>();
	private LinkedHashMap<String,Integer> allTermsDocumentFreq = new LinkedHashMap<String,Integer>();
	private TreeSet<String> allUniqueDocTerms = new TreeSet<String>();
	private int totalUsers=0,totalWords=0,totalDocuments=0;
	private ArrayList<LinkedHashMap<String,Double>> userDocumentVectorsList = new ArrayList<LinkedHashMap<String,Double>>();
	private LinkedHashMap<String,ArrayList<LinkedHashMap<String,Double>>> allUserDocumentVectors = new LinkedHashMap<String,ArrayList<LinkedHashMap<String,Double>>>();

	private long startTimeTextProcessing,endTimeTextProcessing,completionTimeTextProcessing;
	private long startTimeTFIDF,endTimeTFIDF,completionTimeTFIDF;
	private long startTimeAlgorithm,endTimeAlgorithm,completionTimeAlgorithm;

	private LinkedHashMap<Long,String> tweetIdUser = new LinkedHashMap<Long,String>();
	private LinkedHashMap<Long,String> tweetIdText = new LinkedHashMap<Long,String>();
	private LinkedHashMap<Long,LinkedHashMap<String,Double>> tweetIdDocumentVector = new LinkedHashMap<Long,LinkedHashMap<String,Double>>();
	// private LinkedHashMap<String,ArrayList<Long>> usersTweetIdsList = new LinkedHashMap<String,ArrayList<Long>>();
	
	private ArrayList<String> userRegisteredInRecAgent = new ArrayList<String>();
	private ArrayList<String> usersRec; //Users to be given recommendations
	private int[] usersRecTweetCountsReceived;
	
	private Map<String,TreeMap<String,Double>> allUserScores = new TreeMap<String,TreeMap<String,Double>>();

	transient protected ControllerAgentGui myGui;
	private boolean getUserRecList;
	
	private int totalMessageBytes = 0;

	private long systemTimeName;
	
	private Map<String,String> userFollowee = new LinkedHashMap<String,String>(); //list of users and their followeeNames before processing (may have extra since processing can remove users) 
	private Map<String,Integer> followeeFollowerCounts = new LinkedHashMap<String,Integer>(); //number of followers for each followee/class
	private Map<String,List<String>> followeeFollowers = new LinkedHashMap<String,List<String>>(); //list of followees and their followers
	private List<String> testSetUsers; //list of users in test set
	private List<String> trainSetUsers; //list of users in training set
	
	private Classifier trainedCentralSVM;
	private TreeSet<String> centralUniqueDocTerms = new TreeSet<String>();
	
	protected void setup() 
	{


		Object[] args = getArguments();
		controllerAgent = (AID) args[0];

		referenceUser = (String) args[1]; 
		tweetNum_temp2    = (String) args[2];
		dc1string_temp    = (String) args[3];
		dc2string_temp    = (String) args[4];
		hashtags_temp     = Integer.parseInt(args[5].toString());

		retweetedby_temp  = (Integer) args[7];
		stopWordFlag_temp = (Integer) args[8];


		connectedtoTfidfservernumber = (Integer) args[11];	  
		connectedtoRecservernumber   = (Integer) args[12];

		tweetsToReceive		 = (Integer) args[13];		

		System.out.println(getLocalName()+" tweetsToReceive: "+tweetsToReceive);

		numRecAgents = (Integer) args[15];

		//@Jason added algorithmRec argument
		algorithmRec = (Integer) args[16];

		//usersRec = (ArrayList<String>) args[17];

		myGui = (ControllerAgentGui) args[18];

		if (algorithmRec == SVM && numRecAgents > 1)
		{
			trainedCentralSVM = (Classifier) args[19];
			centralUniqueDocTerms = (TreeSet<String>) args[20];
		}
			
		
		try {
			DFAgentDescription dfd = new DFAgentDescription();
			dfd.setName(getAID());
			ServiceDescription sd = new ServiceDescription();
			sd.setName("Distributed Recommender System");
			sd.setType("Recommender Agent");
			dfd.addServices(sd);
			DFService.register(this, dfd);
			RecommenderServiceBehaviour RecommenderServiceBehaviour = new RecommenderServiceBehaviour(this);
			addBehaviour(RecommenderServiceBehaviour);

			System.out.println(getLocalName()+" REGISTERED WITH THE DF");
		} catch (FIPAException e) {
			e.printStackTrace();
		}		
		agentName = getLocalName();
		AID_agent_name = getAID();

		//@Jason checking AID_agent_name
		System.out.println(this.getLocalName()+" AID_agent_name: "+AID_agent_name);


		nodeNumber = agentName.split("ServiceAgent", 2)[1].trim();

		System.out.println("Hello! I am " + getAID().getLocalName()+ " and is setup properly.");

		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Recommender Agent");
		template.addServices(sd);
		try {
			DFAgentDescription[] result = DFService.search(this, template);
			allRecommenderAgents = new AID[result.length];
			for (int i = 0; i < result.length; ++i) {
				allRecommenderAgents[i] = result[i].getName();
			}
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}

		setQueueSize(0);
		getUserRecList = false;
		
		systemTimeName = System.currentTimeMillis();
		
//		usersRec = myGui.getUsersRec();
//		System.out.println(getLocalName()+" usersRec: "+usersRec);
//		usersRecTweetCountsReceived = new int[usersRec.size()];
		
	}

	private class RecommenderServiceBehaviour extends CyclicBehaviour {	
		private static final long serialVersionUID = 1L;

		public RecommenderServiceBehaviour(Agent a) {
			super(a);
		}

		public void action() {
			
			if (!getUserRecList)
			{
				myGui.reselectRecommendee();
				usersRec = myGui.getUsersRec();
				System.out.println(getLocalName()+" usersRec: "+usersRec);
				usersRecTweetCountsReceived = new int[usersRec.size()];
				getUserRecList = true;
			}
			
			ACLMessage msg= myAgent.receive();
			
			if (msg!=null && msg.getOntology() == "Update Connected UserAgent List for this Rec Server" && msg.getPerformative() == ACLMessage.REQUEST)
			{
				DFAgentDescription template = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setName("Distributed Recommender System");
				sd.setType("User-Agent");
				sd.setOwnership(nodeNumber);
				template.addServices(sd);
				try {
					DFAgentDescription[] result = DFService.search(myAgent, template);

					//@Jason checking how many connected agents to recommender agents
					System.out.println(myAgent.getLocalName()+" RESULT LENGTH: "+result.length);

					allUserAgentConnectedtoThisServer = new AID[result.length];
					for (int i = 0; i < result.length; ++i) {
						allUserAgentConnectedtoThisServer[i] = result[i].getName();
						// System.out.println(getLocalName()+" user: "+allUserAgentConnectedtoThisServer[i].getLocalName());
					}
				}
				catch (FIPAException fe) {
					fe.printStackTrace();
				}
			}

			if (msg!=null && msg.getOntology() == "Tweet From User Agent")
			{
				tweetCount++;
				if (tweetCount == 1)
					firstTweetTime = System.nanoTime();
				
				ArrayList<String> currUserDocuments;
				ArrayList<Long> currUserTweetIdList;
				String tweetReceived;
				String tweetUserReceived;
				long tweetIdReceived;
				String tweetTextReceived;
				int totalTweetFromUser;
				final byte[] utf16MessageBytes;
				String tweetFolloweeName;
				
				tweetReceived = msg.getContent();
				// tweetUserReceived = tweetReceived.split(" ",4)[1];
				// tweetIdReceived = Long.valueOf(tweetReceived.split(" ",4)[2]);
				// tweetTextReceived = tweetReceived.split(" ",4)[3];
				// totalTweetFromUser = Integer.parseInt(tweetReceived.split(" ",4)[0]);
				tweetUserReceived = tweetReceived.split(" ",5)[1];
				tweetIdReceived = Long.valueOf(tweetReceived.split(" ",5)[2]);
				tweetTextReceived = tweetReceived.split(" ",5)[4];
				totalTweetFromUser = Integer.parseInt(tweetReceived.split(" ",5)[0]);
				tweetFolloweeName = tweetReceived.split(" ",5)[3];
				
				// System.out.println("1tweetReceived:" +tweetReceived);
				// System.out.println("2tweetReceived:" +totalTweetFromUser+","+tweetUserReceived+","+tweetIdReceived+","+tweetTextReceived+","+tweetFolloweeName);
				
				if (tweetUserReceived.equals("sageryereson"))
					System.out.println("sageryerson: "+tweetReceived);
				
				if (!userFollowee.containsKey(tweetUserReceived))
					userFollowee.put(tweetUserReceived,tweetFolloweeName);
							
				try{
					utf16MessageBytes= tweetReceived.getBytes("UTF-16BE");
				} catch (UnsupportedEncodingException e) {
					throw new AssertionError("UTF-16BE not supported");
					
				}
				totalMessageBytes += utf16MessageBytes.length;
				// System.out.println("totalMessageBytes: "+totalMessageBytes);
				
				// if (tweetUserReceived.equals("TetraRyerson"))
				// {
					// System.out.println("From TetraRyerson: "+tweetTextReceived);
				// }
				
				// try {
					// FileWriter writer = new FileWriter("tweetsReceived"+String.valueOf(systemTimeName)+".txt", true); //append

					// BufferedWriter bufferedWriter = new BufferedWriter(writer);
		
					// bufferedWriter.write(tweetUserReceived + "\t" + tweetIdReceived + "\t" + tweetTextReceived);
					// bufferedWriter.newLine();

					// bufferedWriter.close();
				// } catch (IOException e) {
					// e.printStackTrace();
				// }
				
				if (usersRec.contains(tweetUserReceived))
				{
					int userIndex = usersRec.indexOf(tweetUserReceived);
					usersRecTweetCountsReceived[userIndex]++;
					
					if (usersRecTweetCountsReceived[userIndex] == totalTweetFromUser)
					{
						ACLMessage msgLastTweetFromRecUser = new ACLMessage( ACLMessage.INFORM );
						msgLastTweetFromRecUser.addReceiver( new AID(tweetUserReceived+"-UserAgent", AID.ISLOCALNAME) );
						msgLastTweetFromRecUser.setPerformative( ACLMessage.INFORM );
						msgLastTweetFromRecUser.setContent("Received Last Tweet");
						msgLastTweetFromRecUser.setOntology("Last Tweet Received From Rec Agent");
						send(msgLastTweetFromRecUser);
					}
					
				}
				
				/*if (!allUserDocuments.containsKey(tweetUserReceived))
				{
					currUserDocuments = new ArrayList<String>();
					userRegisteredInRecAgent.add(tweetUserReceived);
				}
				else
					currUserDocuments = allUserDocuments.get(tweetUserReceived);

				currUserDocuments.add(tweetTextReceived);
				allUserDocuments.put(tweetUserReceived, currUserDocuments);
				 */
				
				if (!userRegisteredInRecAgent.contains(tweetUserReceived))
				{
					userRegisteredInRecAgent.add(tweetUserReceived);
				}
				
				tweetIdText.put(tweetIdReceived, tweetTextReceived);
				tweetIdUser.put(tweetIdReceived, tweetUserReceived);

				//				if (!usersTweetIdsList.containsKey(tweetUserReceived))
				//					currUserTweetIdList = new ArrayList<Long>();
				//				else	
				//					currUserTweetIdList = usersTweetIdsList.get(tweetUserReceived);
				//
				//				currUserTweetIdList.add(tweetIdReceived);
				//				usersTweetIdsList.put(tweetUserReceived, currUserTweetIdList);

				//@Jason see tweets before processing
				/*try {
					FileWriter writer = new FileWriter("tweetsRec.txt", true); //append

					BufferedWriter bufferedWriter = new BufferedWriter(writer);

					bufferedWriter.write(myAgent.getLocalName()+" "+msg.getContent()+" tweetCount: "+tweetCount);
					bufferedWriter.newLine();

					bufferedWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}*/

//				System.out.println(myAgent.getLocalName()+" "+msg.getContent()+" tweetCount: "+tweetCount+"/"+tweetsToReceive);
//				System.out.println(myAgent.getLocalName()+" tweetCount: "+tweetCount+"/"+tweetsToReceive);

				if(tweetCount == tweetsToReceive)
				{
					long lastTweetTime = System.nanoTime();
					/*System.out.println("tweetIdText: "+tweetIdText);
					System.out.println("tweetIdUser: "+tweetIdText);
					System.out.println("usersTweetIdsList: "+usersTweetIdsList);
					 */
					 
					ACLMessage msgMessagePassing = new ACLMessage( ACLMessage.INFORM );
					msgMessagePassing.addReceiver( new AID("Starter Agent", AID.ISLOCALNAME) );
					msgMessagePassing.setPerformative( ACLMessage.INFORM );
					
					msgMessagePassing.setContent(String.valueOf(totalMessageBytes));
					msgMessagePassing.setOntology("Message Passing Cost");
					send(msgMessagePassing);
					
					msgMessagePassing.setContent(String.valueOf(convertMs(lastTweetTime-firstTweetTime)));
					msgMessagePassing.setOntology("Message Passing Time");
					send(msgMessagePassing);
					

					System.out.println(convertMs(lastTweetTime-firstTweetTime) + " ms");
					System.out.println("############### tweetCount: "+tweetCount);
					System.out.println("@@@@@@@@@@@@@@@ tweetIdText.size(): "+ tweetIdText.size());

					ArrayList<Long> tweetIdsToRemove = new ArrayList<Long>(); //tweetIdsToRemove because no useful info

					startTimeTextProcessing = System.nanoTime();

					for (Long currTweetId : tweetIdText.keySet())
					{
						LinkedHashMap<String,Double> tweetDocumentVector = new LinkedHashMap<String,Double>();
						String currentText = tweetIdText.get(currTweetId);

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
						if (currentText.contains("RT @") && retweetedby_temp == RE_TWEETS)
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
						if (hashtags_temp == HASH_TAGS)
						{
							Pattern specialCharacters = Pattern.compile("[^a-zA-Z\\p{Z}]");
							matcher = specialCharacters.matcher(currentText);
							currentText = matcher.replaceAll(" ");
						}
						//System.out.println("After special characters: " + currentText);

						//Remove stop words if flagged
						if (stopWordFlag_temp == STOP_WORDS)
						{
							for (String stopWord : stopWordsArray){	
								if (currentText.toLowerCase().contains(" "+stopWord+" "))
									//System.out.println("FOUND STOPWORD: "+stopWord);
									currentText = currentText.toLowerCase().replaceAll(" "+stopWord+" "," ");
							}
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

						//******@Begin making vectors*************************************************
						//Add processed texts to a list
						Scanner sc = new Scanner(currentText);
						//List<String> list = new ArrayList<String>();
						String stringToken;
						double wordFreq = 0.0;
						int wordCount = 0;
						wordCount = currentText.split("\\s+").length;

						//System.out.println("currentText: "+ currentText);
						//If processed text is a blank line with 1 single space or less than 3 words
						if (wordCount < 3)
						{
							//System.out.println("DO NOT ADD");
							tweetIdsToRemove.add(currTweetId);
						}
						else
						{
//							try {
//								FileWriter writer = new FileWriter("demoTweetsVerification1000_tweet_words_users.txt", true); //append	
//								BufferedWriter bufferedWriter = new BufferedWriter(writer);
//								bufferedWriter.write(tweetIdUser.get(currTweetId)+": "+currentText);					
//								bufferedWriter.newLine();
//								bufferedWriter.close();
//							} catch (IOException e) {
//								e.printStackTrace();
//							}
							
							while (sc.hasNext()){
								stringToken = sc.next();
								//list.add(stringToken);

								//Add all unique terms to allUniqueDocTerms
								allUniqueDocTerms.add(stringToken);

								//Count frequency of words in a document
								if (tweetDocumentVector.get(stringToken)!=null) //already exists in vector
									wordFreq = tweetDocumentVector.get(stringToken)+1;
								else //does not exist in vector yet
									wordFreq = 1.0;

								tweetDocumentVector.put(stringToken, wordFreq);
							}
							/*for (String s : list){
									System.out.print(s+" ");
								}
								System.out.println();*/
							sc.close();
							//System.out.println();
							//System.out.println("The length of string: "+currentText.length());

							tweetIdDocumentVector.put(currTweetId, tweetDocumentVector);

						}

					} //end for (Long currTweetId : usersTweetIdsList.get(curName))					

					//					for (String curName: usersTweetIdsList.keySet())
					//					{
					//						userDocumentVectorsList = new ArrayList<LinkedHashMap<String,Double>>();
					//
					//						for (Long currTweetId : usersTweetIdsList.get(curName))
					//						{
					//							LinkedHashMap<String,Double> userDocumentVector = new LinkedHashMap<String,Double>();
					//							String currentText = tweetIdText.get(currTweetId);
					//
					//							//pad out spaces before and after word for parsing 
					//							currentText = String.format(" %s ",currentText);
					//
					//							//System.out.println("Original text: "+currentText);
					//
					//
					//							//Remove Photo: tweets
					//							if (currentText.contains("Photo:"))
					//								currentText = currentText.substring(0,currentText.indexOf("Photo:"));
					//
					//							//Remove Photoset: tweets
					//							if (currentText.contains("Photoset:"))
					//								currentText = currentText.substring(0,currentText.indexOf("Photoset:"));
					//
					//							//Remove all retweets if flagged
					//							Matcher matcher; //a matcher
					//							if (currentText.contains("RT @") && retweetedby_temp == RE_TWEETS)
					//								currentText="";
					//							else
					//							{
					//								//Remove RT @, conserve the text from retweets
					//								Pattern retweet = Pattern.compile("RT @");
					//
					//								matcher = retweet.matcher(currentText);
					//								currentText = matcher.replaceAll("RT ");
					//							}
					//							//System.out.println("After RT @: " + currentText);
					//
					//							//Remove punctuations
					//							Pattern punctuations = Pattern.compile("[\\p{P}]");
					//							matcher = punctuations.matcher(currentText);
					//							currentText = matcher.replaceAll("");
					//
					//							//System.out.println("After punctuations: "+ currentText);
					//
					//							//Remove url links
					//							Pattern links = Pattern.compile("http[a-zA-Z0-9]*|bitly[a-zA-Z0-9]*|www[a-zA-Z0-9]*");
					//							matcher = links.matcher(currentText);
					//							currentText = matcher.replaceAll(" ");
					//
					//							//System.out.println("After url links: "+ currentText);
					//
					//							//Remove special characters including hash tags if flagged
					//							if (hashtags_temp == HASH_TAGS)
					//							{
					//								Pattern specialCharacters = Pattern.compile("[^a-zA-Z\\p{Z}]");
					//								matcher = specialCharacters.matcher(currentText);
					//								currentText = matcher.replaceAll(" ");
					//							}
					//							//System.out.println("After special characters: " + currentText);
					//
					//							//Remove stop words if flagged
					//							if (stopWordFlag_temp == STOP_WORDS)
					//							{
					//								for (String stopWord : stopWordsArray){	
					//									if (currentText.toLowerCase().contains(" "+stopWord+" "))
					//										//System.out.println("FOUND STOPWORD: "+stopWord);
					//										currentText = currentText.toLowerCase().replaceAll(" "+stopWord+" "," ");
					//								}
					//							}
					//							//Change all text to lowercase
					//							currentText=currentText.toLowerCase();
					//
					//							//Trim leading and ending white space
					//							currentText=currentText.trim();
					//							//Remove any non-alphabetical characters
					//							currentText=currentText.replaceAll("[^a-zA-Z ]","");
					//							//Shorten any spaces to just 1 single space
					//							currentText=currentText.replaceAll(" +", " ");
					//							//currentText=currentText.trim().replaceAll(" +", " ");
					//
					//							//System.out.println(getLocalName()+" Removed junk: "+currentText);
					//
					//							//******@Begin making vectors*************************************************
					//							//Add processed texts to a list
					//							Scanner sc = new Scanner(currentText);
					//							List<String> list = new ArrayList<String>();
					//							String stringToken;
					//							double wordFreq = 0.0;
					//							int wordCount = 0;
					//							wordCount = currentText.split("\\s+").length;
					//
					//							//System.out.println("currentText: "+ currentText);
					//							//If processed text is a blank line with 1 single space or less than 3 words
					//							if (wordCount < 3)
					//							{
					//								//System.out.println("DO NOT ADD");
					//								tweetIdsToRemove.add(currTweetId);
					//							}
					//							else
					//							{
					//								while (sc.hasNext()){
					//									stringToken = sc.next();
					//									list.add(stringToken);
					//
					//									//Add all unique terms to allUniqueDocTerms
					//									allUniqueDocTerms.add(stringToken);
					//
					//									//Count frequency of words in a document
					//									if (userDocumentVector.get(stringToken)!=null) //already exists in vector
					//										wordFreq = userDocumentVector.get(stringToken)+1;
					//									else //does not exist in vector yet
					//										wordFreq = 1.0;
					//
					//									userDocumentVector.put(stringToken, wordFreq);
					//								}
					//								/*for (String s : list){
					//									System.out.print(s+" ");
					//								}
					//								System.out.println();*/
					//								sc.close();
					//								//System.out.println();
					//								//System.out.println("The length of string: "+currentText.length());
					//
					//								tweetIdDocumentVector.put(currTweetId, userDocumentVector);
					//								userDocumentVectorsList.add(userDocumentVector);
					//								//System.out.println("currTweetId: "+currTweetId);
					//								//System.out.println(userDocumentVector);
					//							}
					//
					//						} //end for (Long currTweetId : usersTweetIdsList.get(curName))
					//						//Case where after processing, some users may have no more useful words left in every document, only add > 0
					//						if (userDocumentVectorsList.size() > 0)
					//							allUserDocumentVectors.put(curName,userDocumentVectorsList);	
					//
					//					} //end for (String curName: usersTweetIdsList.keySet())

					//System.out.println("tweetIdText.size(): "+tweetIdText.size());

					//Remove all tweetIds that are not useful							
					for (Long tweetIdToRemove : tweetIdsToRemove)
					{
						if (tweetIdDocumentVector.containsKey(tweetIdToRemove))
							tweetIdDocumentVector.remove(tweetIdToRemove);
						if (tweetIdText.containsKey(tweetIdToRemove))
							tweetIdText.remove(tweetIdToRemove);
						if (tweetIdUser.containsKey(tweetIdToRemove))
							tweetIdUser.remove(tweetIdToRemove);

						//						Iterator<Map.Entry<String,ArrayList<Long>>> iterator = usersTweetIdsList.entrySet().iterator();
						//						while(iterator.hasNext()){
						//							Map.Entry<String,ArrayList<Long>> entry = iterator.next();
						//							for (int i = 0; i < entry.getValue().size(); i++)
						//							{
						//								if (entry.getValue().get(i) == tweetIdToRemove)
						//								{
						//									entry.getValue().remove(i);
						//								}
						//							}    
						//							if (entry.getValue().size() == 0)
						//								iterator.remove();
						//						}


					}

					System.out.println("XXXXXXXXXXXXX tweetIdText.size(): " + tweetIdText.size());

					long beforeAggregateTime = System.nanoTime();
					//Aggregate each users' tweets into one document
					for (Long currTweetId : tweetIdDocumentVector.keySet())
					{
						String currUserName = tweetIdUser.get(currTweetId);
						LinkedHashMap<String,Double> currTweetIdDocumentVector = tweetIdDocumentVector.get(currTweetId);

						if (!allUserDocuments.containsKey(currUserName))
							aggregatedUserTweets = new LinkedHashMap<String,Double>();
						else
							aggregatedUserTweets = allUserDocuments.get(currUserName);

						for (String currTerm : currTweetIdDocumentVector.keySet())
						{
							double termFreq = 0.0;
							if (aggregatedUserTweets.containsKey(currTerm))
							{
								termFreq = aggregatedUserTweets.get(currTerm);
							}

							termFreq += currTweetIdDocumentVector.get(currTerm);
							aggregatedUserTweets.put(currTerm,termFreq);
						}
						allUserDocuments.put(currUserName, aggregatedUserTweets);
					}
					
					for (String u : allUserDocuments.keySet())
					{
						System.out.println(getLocalName()+" u: "+u);
					}
					
					
					int followerCount = 0;
					String followeeName;
					List<String> followerNames;
					
					//adds only usable users to follower list					
					for (String currUser : allUserDocuments.keySet())
					{
						followeeName = userFollowee.get(currUser);
						// System.out.println("followeeName: "+followeeName);
						if (!followeeFollowers.containsKey(followeeName))
						{
							followerNames = new ArrayList<String>();
							// System.out.println("Entered first time: "+followeeName);
						}
						else
						{
							followerNames = followeeFollowers.get(followeeName);
							// System.out.println("Entered NOT first time: "+followeeName);
						}
						
						followerNames.add(currUser);
						followeeFollowers.put(followeeName,followerNames);
						
						if (followeeFollowerCounts.containsKey(followeeName))
							followerCount = followeeFollowerCounts.get(followeeName) + 1;
						else
							followerCount = 1;
						
						followeeFollowerCounts.put(followeeName,followerCount);
					}
					
					for (String f: followeeFollowers.keySet())
					{
						List<String> fNames = followeeFollowers.get(f);
						System.out.println(f+": "+followeeFollowerCounts.get(f));
						System.out.println(fNames);
					}
					
					//-------------PRINTING OUT TF TO FILE***********************
					// FileWriter writer;
					// try {
						// writer = new FileWriter("tf_matrix_1000.txt", true); //append
						// BufferedWriter bufferedWriter = new BufferedWriter(writer);
						// bufferedWriter.write("\t\t");
						// for (String userNames : allUserDocuments.keySet())
						// {
							// bufferedWriter.write(userNames+"\t");
						// }
						// bufferedWriter.newLine();
						// for (String uniqueTerm: allUniqueDocTerms)
						// {
							// bufferedWriter.write(uniqueTerm+"\t\t");
							// for (String userNames : allUserDocuments.keySet())
							// {
								// double tfValue = 0.0;
								// if (allUserDocuments.get(userNames).containsKey(uniqueTerm))
									// tfValue = allUserDocuments.get(userNames).get(uniqueTerm);
								// bufferedWriter.write(tfValue+"\t");
							// }
							// bufferedWriter.newLine();
						// }
						// bufferedWriter.close();
					// } catch (IOException e) {
						// TODO Auto-generated catch block
						// e.printStackTrace();
					// }

					//					System.out.println("usersTweetIdsList.keySet(): "+usersTweetIdsList.keySet());
					//					for (String currentUser: usersTweetIdsList.keySet())
					//					{
					//						aggregatedUserTweets = new LinkedHashMap<String,Double>();
					//						for (long currTweetId: usersTweetIdsList.get(currentUser))
					//						{
					//							LinkedHashMap<String,Double> currTweetIdDocumentVector = tweetIdDocumentVector.get(currTweetId);
					//							for (String currTerm : currTweetIdDocumentVector.keySet())
					//							{
					//								double termFreq = 0.0;
					//								if (aggregatedUserTweets.containsKey(currTerm))
					//								{
					//									termFreq = aggregatedUserTweets.get(currTerm);
					//								}
					//
					//								termFreq += currTweetIdDocumentVector.get(currTerm);
					//								aggregatedUserTweets.put(currTerm,termFreq);
					//							}
					//						}
					//						allUserDocuments.put(currentUser, aggregatedUserTweets);
					//					}

					/*FileWriter writer;
					try {
						writer = new FileWriter("processedVectors.txt", true); //append
						BufferedWriter bufferedWriter = new BufferedWriter(writer);
						for (String currentUser: usersTweetIdsList.keySet())
						{
							bufferedWriter.write("---------------"+currentUser+"---------------");
							bufferedWriter.newLine();
							for (long currTweetId: usersTweetIdsList.get(currentUser))
							{
								LinkedHashMap<String,Double> currTweetIdDocumentVector = tweetIdDocumentVector.get(currTweetId);
								bufferedWriter.write(currTweetIdDocumentVector.toString());
								bufferedWriter.newLine();
							}
						}
						bufferedWriter.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}*/

					long aggregatedTime = System.nanoTime();
					System.out.println("##################################allUserDocuments*******************************");
					System.out.println(convertMs(aggregatedTime-beforeAggregateTime)+" ms");
//					for (String user: allUserDocuments.keySet())
//					{
//						System.out.print("user: "+user+" ");
//						System.out.println(allUserDocuments.get(user));
//						try {
//							FileWriter writer = new FileWriter("verification_docs/"+user+".txt", true); //append
//							BufferedWriter bufferedWriter = new BufferedWriter(writer);
//							for (String terms : allUserDocuments.get(user).keySet())
//							{
//								for (int i = 0; i < allUserDocuments.get(user).get(terms); i++)
//								{
//									bufferedWriter.write(terms);
//									bufferedWriter.newLine();
//								}
//							}
//							bufferedWriter.close();
//						} catch (IOException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					}

					//					try {
					//						FileWriter writer = new FileWriter("all_unique_terms.txt", true); //append
					//						BufferedWriter bufferedWriter = new BufferedWriter(writer);
					//						for (String terms : allUniqueDocTerms)
					//						{
					//							bufferedWriter.write(terms);
					//							bufferedWriter.newLine();
					//						}
					//						bufferedWriter.close();
					//					} catch (IOException e) {
					//						// TODO Auto-generated catch block
					//						e.printStackTrace();
					//					}
					//					System.out.println("tweetIdDocumentVector");
					//					System.out.println(tweetIdDocumentVector);


					//					int countDb2 = 0;
					//   					FileWriter writer;
					//					try {
					//						writer = new FileWriter("myOwnDBText.txt", true); //append
					//						BufferedWriter bufferedWriter = new BufferedWriter(writer);
					//						bufferedWriter.write("tweetIdText.size(): "+tweetIdText.size());
					//						bufferedWriter.newLine();
					//						for (Long l: tweetIdText.keySet())
					//						{
					//							countDb2++;
					//							bufferedWriter.write("TweetId: "+l);
					//							bufferedWriter.write("\t Text: "+tweetIdText.get(l));
					//							bufferedWriter.newLine();
					//						}
					//
					//						bufferedWriter.write("countDb2: "+countDb2);
					//						bufferedWriter.newLine();
					//						bufferedWriter.close();
					//					} catch (IOException e) {
					//						// TODO Auto-generated catch block
					//						e.printStackTrace();
					//					}


					endTimeTextProcessing = System.nanoTime();
					completionTimeTextProcessing = endTimeTextProcessing - startTimeTextProcessing;
					System.out.println(getLocalName()+" completionTimeTextProcessing: "+convertMs(completionTimeTextProcessing)+" ms");
					System.out.println(getLocalName()+ " After processing, tweets: "+ tweetIdText.size());

					myGui.appendResult(getLocalName()+"completionTimeTextProcessing: "+convertMs(completionTimeTextProcessing)+" ms");
					myGui.appendResult(getLocalName()+ "After processing, tweets: "+ tweetIdText.size());
					
					//@Jason added code to deny querying for any user who have no tweet in database after text processing and tweeting simulation is complete 					

					//System.out.println(getLocalName()+" userRegisteredInRecAgent: "+userRegisteredInRecAgent);

					ArrayList<String> usersToRemove = new ArrayList<String>();
					
					//System.out.println(getLocalName()+" allUserDocuments.keySet: "+allUserDocuments.keySet());
					//System.out.println(getLocalName()+" userRegisteredinRecAgent: "+userRegisteredInRecAgent);
					
					for(String currUser : userRegisteredInRecAgent){

						if (!allUserDocuments.containsKey(currUser)) {
							System.out.println(currUser+" DOES NOT EXIST IN DB");
							usersToRemove.add(currUser);

							String userAgent = currUser+"-UserAgent";
							System.out.println("userAgent: "+userAgent);
							ACLMessage stopUserQueryMsg = new ACLMessage (ACLMessage.REQUEST);
							stopUserQueryMsg.addReceiver(new AID(userAgent,AID.ISLOCALNAME));
							stopUserQueryMsg.setPerformative(ACLMessage.REQUEST);
							stopUserQueryMsg.setContent("Denied Querying");
							stopUserQueryMsg.setOntology("Denied Querying");
							send(stopUserQueryMsg);
							System.out.println(getLocalName()+" Sent out Denied Querying for "+userAgent);
						}
					}

					
					try {
						FileWriter writer10;
						writer10 = new FileWriter("numTextProcessed.txt", true); //append
						BufferedWriter bufferedWriter = new BufferedWriter(writer10);
						bufferedWriter.write(getLocalName()+ " Tweets After Processing: "+ tweetIdText.size()+" Tweets Before Processing: "+ tweetsToReceive);
						bufferedWriter.newLine();
						bufferedWriter.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
					
					
					
					//   					int countDb = 0;
					//   					FileWriter writer2;
					//					try {
					//						writer2 = new FileWriter("myOwnDB.txt", true); //append
					//						BufferedWriter bufferedWriter = new BufferedWriter(writer2);
					//						bufferedWriter.write("usersTweetIdsList.size(): "+usersTweetIdsList.size());
					//						bufferedWriter.write("\tusersTweetIdsList.keySet(): "+usersTweetIdsList.keySet().size());
					//						bufferedWriter.newLine();
					//						for (String curUsername: usersTweetIdsList.keySet())
					//						{
					//							bufferedWriter.write("username: "+curUsername);
					//							bufferedWriter.write("\t tweetIds: "+usersTweetIdsList.get(curUsername).size());
					//							bufferedWriter.newLine();
					//							for (Long l : usersTweetIdsList.get(curUsername))
					//							{
					//								bufferedWriter.write("\ttweetId: "+l);
					//								bufferedWriter.newLine();
					//								countDb++;
					//							}
					//						}
					//
					//						bufferedWriter.write("countDb: "+countDb);
					//						bufferedWriter.newLine();
					//						bufferedWriter.close();
					//					} catch (IOException e) {
					//						// TODO Auto-generated catch block
					//						e.printStackTrace();
					//					} 

					//@Jason remove users from Starter Agent's counter
					//Only send remove message if there is a user to remove

					System.out.println(getLocalName()+" usersToRemove: "+usersToRemove);
					
					if (usersToRemove.size() > 0)
					{

						ACLMessage removeUsersMessage = new ACLMessage( ACLMessage.INFORM );
						removeUsersMessage.addReceiver(new AID("Starter Agent",AID.ISLOCALNAME));
						removeUsersMessage.setPerformative(ACLMessage.INFORM);
						try {
							removeUsersMessage.setContentObject(usersToRemove);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						removeUsersMessage.setOntology("Remove Users From Total");
						send(removeUsersMessage);
						
						System.out.println(getLocalName()+" sent removeUserMessage");

					}
					//					Sent each user individually instead of the arraylist, more proper to agent fipa
					//					if (usersToRemove.size() > 0)
					//					{
					//
					//	   					ACLMessage removeUsersMessage = new ACLMessage( ACLMessage.INFORM );
					//	   					removeUsersMessage.addReceiver(new AID("Starter Agent",AID.ISLOCALNAME));
					//	   					removeUsersMessage.setPerformative(ACLMessage.INFORM);
					//	   					removeUsersMessage.setContent(String.valueOf(usersToRemove.size()));
					//	   					removeUsersMessage.setOntology("Remove Users From Total");
					//
					//
					//
					//   						send(removeUsersMessage);
					//
					//   					//@Jason remove users from allUserAgents list in Starter Agent
					//	   					removeUsersMessage.setOntology("Remove This User From List of Agents");
					//	   					for (String currentUser : usersToRemove){
					//	   						System.out.println(myAgent.getLocalName()+" send to Starter Agent to remove: "+currentUser);
					//	   						removeUsersMessage.setContent(currentUser+"-UserAgent");
					//	   						send(removeUsersMessage);
					//	   					}
					//					}  					


					//@Jason tell Starter Agent recommender agent is done text processing and only valid tweets available
					ACLMessage textProcessMessage = new ACLMessage( ACLMessage.INFORM );
					textProcessMessage.addReceiver(new AID("Starter Agent",AID.ISLOCALNAME));
					textProcessMessage.setPerformative(ACLMessage.INFORM);
					textProcessMessage.setContent("Text Processing Complete");
					textProcessMessage.setOntology("Text Processing Complete");
					send(textProcessMessage);

					System.out.println(getLocalName()+ " Text Processing Complete");

				}
			}

			//@Jason added new message to start recommending
			if (msg!=null && msg.getOntology()=="Start Recommend Algorithms" && msg.getPerformative()==ACLMessage.REQUEST && calculateAlready==false)
			{

				calculateAlready=true;

				System.out.println(myAgent.getLocalName()+" Starting Algorithm: "+algorithmRec);

				System.out.println(myAgent.getLocalName()+" received Start Recommend Algorithms message");


				/*//@Jason see users in local db
					int c=0;
					for(Entry<String,Double> currentUser : User_in_Server.entrySet()){
   						String currentUserID = currentUser.getKey();
   						c++;
   						System.out.println(myAgent.getLocalName()+" user "+c+": "+currentUserID);
					}
				 */

				startTimeTFIDF = System.nanoTime();

				System.out.println(getLocalName()+" Get Document Freq of Terms");
				int docFreq=0;
				//Get the document frequency of terms
				
				//Make all unique terms from central training when SVM and num nodes > 1; Creates proper test files with correct num of attributes
				if (algorithmRec == SVM  && numRecAgents > 1)
				{
					allUniqueDocTerms.clear();
					allUniqueDocTerms.addAll(centralUniqueDocTerms);
				}
				
				for (String term : allUniqueDocTerms)
				{
					//Initialize allTermsDocumentFreq to 0
					allTermsDocumentFreq.put(term, 0);
				}
				
				for (String curName : allUserDocuments.keySet())
				{
					LinkedHashMap<String,Double> curDoc = allUserDocuments.get(curName);
					for (String docTerm : curDoc.keySet())
					{
						docFreq = allTermsDocumentFreq.get(docTerm);
						docFreq++;
						allTermsDocumentFreq.put(docTerm,docFreq);
					}
				}
				
								// FileWriter writer11;
				// try {
					// writer11 = new FileWriter("df_freq.txt", true); //append
					// BufferedWriter bufferedWriter = new BufferedWriter(writer11);
						// for (String docTerm : allTermsDocumentFreq.keySet())
					// {
						// bufferedWriter.write(docTerm+"\t"+allTermsDocumentFreq.get(docTerm));
						// bufferedWriter.newLine();
					// }
					// bufferedWriter.close();
				// } catch (IOException e) {
					// e.printStackTrace();
				// }
				
				
				
//				for (String term : allUniqueDocTerms)
//				{
//					//Initialize allTermsDocumentFreq to 0
//					allTermsDocumentFreq.put(term, 0);
//
//					//for (String curName: allUserDocumentVectors.keySet())
//					for (String curName: allUserDocuments.keySet())
//					{
//						//for (LinkedHashMap<String,Double> curDoc : allUserDocumentVectors.get(curName))
//						//{
//						LinkedHashMap<String,Double> curDoc = allUserDocuments.get(curName);
//						for (String docTerm : curDoc.keySet())
//						{
//							if (term.equals(docTerm))
//							{
//								docFreq++;
//								break;
//							}
//
//						}
//						//}
//					}
//
//					allTermsDocumentFreq.put(term,docFreq);
//					docFreq=0;
//
//				}

//				System.out.println(getLocalName()+ " allTermsDocumentFreq: "+allTermsDocumentFreq);

				//*******************************@CALCULATE THE TF-IDF ************************************
				//tf-idf = tf * idf
				//tf log normalization= allUserDocumentVectors ; tf raw frequency = allUserDocumentVectors
				//idf smooth = log((totalDocuments/df)+1) //adjust for zero log(1); idf = log(totalDocuments/df)
				//df = allTermsDocumentFreq

				//Put tf-idf weights into documents
				double tf,df,idf,tfidf;
				//LinkedHashMap<String,ArrayList<LinkedHashMap<String,Double>>> allUserDocumentsTFIDF = new LinkedHashMap<String,ArrayList<LinkedHashMap<String,Double>>>();
				LinkedHashMap<String,LinkedHashMap<String,Double>> allUserDocumentsTFIDF = new LinkedHashMap<String,LinkedHashMap<String,Double>>();
				LinkedHashMap<String,LinkedHashMap<String,Double>> allUserDocumentsTF = new LinkedHashMap<String,LinkedHashMap<String,Double>>();
				ArrayList<LinkedHashMap<String,Double>> userDocumentsTFIDFList = new ArrayList<LinkedHashMap<String,Double>>();
				ArrayList<LinkedHashMap<String,Double>> userDocumentsTFList = new ArrayList<LinkedHashMap<String,Double>>();
				//totalDocuments = tweetIdDocumentVector.size();
				totalDocuments = allUserDocuments.size();
				System.out.println(getLocalName()+" Calculating TF-IDF");		

				double vectorMagnitude=0.0;
				double vectorMagnitudeTF=0.0;
				/*
				//TF-IDF for each individual tweets
				for (String curName : usersTweetIdsList.keySet())
				{

					userDocumentsTFIDFList = new ArrayList<LinkedHashMap<String,Double>>();


					for (Long currTweetId: usersTweetIdsList.get(curName))
					{
						vectorMagnitude = 0.0;
						LinkedHashMap<String,Double> tweetIdDoc = tweetIdDocumentVector.get(currTweetId);

						LinkedHashMap<String,Double> userDocumentTFIDF = new LinkedHashMap<String,Double>();
						for (String docTerm : tweetIdDoc.keySet())
						{
							//tf=1+Math.log10(tweetIdDoc.get(docTerm)); //tf log normalization
							tf=tweetIdDoc.get(docTerm); //tf raw frequency
							df=allTermsDocumentFreq.get(docTerm);
							//idf=Math.log10((totalDocuments/df)+1); //idf smooth, adjust for zero log(1)
							//idf=Math.log10(totalDocuments/df); //log base 10
							idf=(double)Math.log(totalDocuments/df) / Math.log(2); //log base 2
							tfidf=tf*idf;
							userDocumentTFIDF.put(docTerm, tfidf);
							//System.out.println("docTerm: "+docTerm+"\ttf: "+tf+"\tdf: "+df+"\tidf: "+idf+"\ttfidf: "+tfidf);					
							vectorMagnitude+=tfidf*tfidf;
						}

						//System.out.println("tweetId: "+currTweetId+","+userDocumentTFIDF);

						vectorMagnitude = Math.sqrt(vectorMagnitude);

						//precalculate the magnitude of vectors and element-wise division of documents to the magnitude x./|x| ie. normalize the document to unit vectors
						double docSumMag = 0.0;
						for (String docTerm : tweetIdDoc.keySet())
						{
							tfidf = userDocumentTFIDF.get(docTerm);
							tfidf = tfidf / vectorMagnitude;
							userDocumentTFIDF.put(docTerm,tfidf);

							docSumMag += tfidf*tfidf;
						}

						//System.out.println("Normalized_tweetId: "+currTweetId+","+userDocumentTFIDF);

						//System.out.println(Math.sqrt(docSumMag));

						userDocumentsTFIDFList.add(userDocumentTFIDF);

						tweetIdTFIDF.put(currTweetId, userDocumentTFIDF);

					} //end for (Long currTweetId: usersTweetIdsList.get(curName)) 

					allUserDocumentsTFIDF.put(curName,userDocumentsTFIDFList);
				} //end for (String curName : usersTweetIdsList.keySet())
				 */
				//Tf-idf for aggregated tweets
				for (String curName : allUserDocuments.keySet())
				{
					vectorMagnitude = 0.0;
					vectorMagnitudeTF = 0.0;
					LinkedHashMap<String,Double> userDoc = allUserDocuments.get(curName);
					LinkedHashMap<String,Double> userDocumentTFIDF = new LinkedHashMap<String,Double>();
					LinkedHashMap<String,Double> userDocumentTF = new LinkedHashMap<String,Double>();

					for (String docTerm : userDoc.keySet())
					{
						//tf=1+Math.log10(tweetIdDoc.get(docTerm)); //tf log normalization
						tf=userDoc.get(docTerm); //tf raw frequency
						//tf = tf/userDoc.keySet().size(); //tf normalized by document
						df=allTermsDocumentFreq.get(docTerm);
						//idf=Math.log10((totalDocuments/df)+1); //idf smooth, adjust for zero log(1)
						//idf=Math.log10(totalDocuments/df); //log base 10
						idf=(double)Math.log10(totalDocuments/df) / Math.log10(2); //log base 2
						tfidf=tf*idf;
						
						//Case for distributed SVM
						if (df == 0 || Double.isNaN(tfidf))
							tfidf=0.0;
						
						userDocumentTFIDF.put(docTerm, tfidf);
						userDocumentTF.put(docTerm, tf);
//						System.out.println("docTerm: "+docTerm+"\ttf: "+tf+"\tdf: "+df+"\tidf: "+idf+"\ttfidf: "+tfidf);					
						vectorMagnitude+=tfidf*tfidf;
						vectorMagnitudeTF+=tf*tf;
					}

					//System.out.println("tweetId: "+currTweetId+","+userDocumentTFIDF);

					vectorMagnitude = Math.sqrt(vectorMagnitude);
					vectorMagnitudeTF = Math.sqrt(vectorMagnitudeTF);

					// FileWriter writer20;
					// try {
						// writer20 = new FileWriter("tfidf_before_norm.txt",true);
						// BufferedWriter bufferedWriter = new BufferedWriter(writer20);
						// bufferedWriter.write(curName+"\t");
						// for (String docTerm : userDoc.keySet())
						// {
							// bufferedWriter.write(docTerm+" "+userDocumentTFIDF.get(docTerm)+"\t");
						// }
						// bufferedWriter.newLine();
						// bufferedWriter.close();
					// }	catch (IOException e) {
							// e.printStackTrace();
					// }
					
					//precalculate the magnitude of vectors and element-wise division of documents to the magnitude x./|x| ie. normalize the document to unit vectors
					double docSumMag = 0.0;
					double docSumMagTF = 0.0;
					for (String docTerm : userDoc.keySet())
					{
						tfidf = userDocumentTFIDF.get(docTerm);
						tfidf = tfidf / vectorMagnitude;
						if (Double.isNaN(tfidf))
							tfidf = 0.0;
						
						userDocumentTFIDF.put(docTerm,tfidf);
						docSumMag += tfidf*tfidf;
						
						tf = userDocumentTF.get(docTerm);
						tf = tf/vectorMagnitude;
						if (Double.isNaN(tf))
							tf = 0.0;
						userDocumentTF.put(docTerm,tf);
						docSumMagTF += tf*tf;
					}

					//System.out.println("Normalized_tweetId: "+currTweetId+","+userDocumentTFIDF);

					//System.out.println(Math.sqrt(docSumMag));

					allUserDocumentsTFIDF.put(curName,userDocumentTFIDF);
					allUserDocumentsTF.put(curName, userDocumentTF);
				// } //end for (String curName : usersTweetIdsList.keySet())
				} //String curName : allUserDocuments.keySet()
				System.out.println();

				endTimeTFIDF = System.nanoTime();
				completionTimeTFIDF = endTimeTFIDF - startTimeTFIDF;
				System.out.println(getLocalName()+" completionTimeTFIDF: "+convertMs(completionTimeTFIDF)+" ms");
				myGui.appendResult(getLocalName()+" completionTimeTFIDF: "+convertMs(completionTimeTFIDF)+" ms");

				for (String userHere: allUserDocumentsTFIDF.keySet())
				{
					System.out.println(getLocalName()+" userHere: "+userHere+" "+allUserDocumentsTFIDF.get(userHere).size());
				}
				
				
				//-------------PRINTING OUT TF-IDF TO FILE***********************
				

				
				// FileWriter writer11;
				// try {
					// writer11 = new FileWriter("tfidf_matrix.txt", true); //append
					// BufferedWriter bufferedWriter = new BufferedWriter(writer11);
					// bufferedWriter.write(totalDocuments+"\t");
					// for (String userNames : allUserDocumentsTFIDF.keySet())
					// {
						// bufferedWriter.write(userNames+"\t");
					// }
					// bufferedWriter.newLine();
					// for (String uniqueTerm: allUniqueDocTerms)
					// {
						// bufferedWriter.write(uniqueTerm+"\t");
						// for (String userNames : allUserDocumentsTFIDF.keySet())
						// {
							// double tfidfValue = 0.0;
							// if (allUserDocumentsTFIDF.get(userNames).containsKey(uniqueTerm))
								// tfidfValue = allUserDocumentsTFIDF.get(userNames).get(uniqueTerm);
							// bufferedWriter.write(tfidfValue+"\t");
						// }
						// bufferedWriter.newLine();
					// }
					// bufferedWriter.close();
				// } catch (IOException e) {
					// e.printStackTrace();
				// }
				
				//-------------PRINTING OUT TF TO FILE***********************
				
//				try {
//					writer = new FileWriter("tf_matrix.txt", true); //append
//					BufferedWriter bufferedWriter = new BufferedWriter(writer);
//					bufferedWriter.write("\t\t");
//					for (String userNames : allUserDocumentsTF.keySet())
//					{
//						bufferedWriter.write(userNames+"\t");
//					}
//					bufferedWriter.newLine();
//					for (String uniqueTerm: allUniqueDocTerms)
//					{
//						bufferedWriter.write(uniqueTerm+"\t\t");
//						for (String userNames : allUserDocumentsTF.keySet())
//						{
//							double tfValue = 0.0;
//							if (allUserDocumentsTF.get(userNames).containsKey(uniqueTerm))
//								tfValue = allUserDocumentsTF.get(userNames).get(uniqueTerm);
//							bufferedWriter.write(tfValue+"\t");
//						}
//						bufferedWriter.newLine();
//					}
//					bufferedWriter.close();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}




//-----------------------------------Original spot for usersRec----------------------------------------
//				usersRec = myGui.getUsersRec();
//				System.out.println(getLocalName()+" usersRec: "+usersRec);

				//System.out.println(getLocalName()+" allUserDocumentsTFIDF: "+allUserDocumentsTFIDF);

				//@Jason added conditions to determine what algorithms to use for recommender

				//Only used for classifiers
				String trainSetFilePath = "";
				String testSetFilePath = "";
				String recommendSetFilePath = "";
				
				
				//Only determineTrainingTestSet when it is a single node and algorithmRec == SVM
				if (algorithmRec == SVM)
				{
					String arffDirName = "Dataset/Arff_files/";
					File arffDir = new File(arffDirName);
					if (!arffDir.exists())
					{
							arffDir.mkdirs();
					}
					
					if (numRecAgents < 2)
					{
						determineTrainingTestSet();
						System.out.println(getLocalName()+" test set "+ testSetUsers.size() +": "+testSetUsers);
						System.out.println(getLocalName()+" train set "+ trainSetUsers.size() +": "+trainSetUsers);
												
						//Create the arff training file attributes
						FileWriter trainWriter;			
						BufferedWriter bufferedWriterTrain;
						
						int uniqueWordCount = 0;
						trainSetFilePath = arffDirName + "train_set_rec"+nodeNumber+".txt";
										
						try {
								trainWriter = new FileWriter(trainSetFilePath, false);	
								bufferedWriterTrain = new BufferedWriter(trainWriter);
											
								bufferedWriterTrain.write("@relation trainingSet");
								bufferedWriterTrain.newLine();
								bufferedWriterTrain.newLine();
															
								for (String uniqueWord: allUniqueDocTerms)
								{
									bufferedWriterTrain.write("@attribute word"+ uniqueWordCount +" numeric");
									bufferedWriterTrain.newLine();	
									uniqueWordCount++;
								}
								
								String attributeClass = "@attribute result ";
								StringJoiner classJoiner = new StringJoiner(",","{","}");
								for (String className: followeeFollowers.keySet())
								{
									classJoiner.add(className);
								}
								
								attributeClass += classJoiner.toString();
								
								bufferedWriterTrain.write(attributeClass);
								bufferedWriterTrain.newLine();
								bufferedWriterTrain.newLine();
								bufferedWriterTrain.newLine();
								bufferedWriterTrain.write("@data");
								bufferedWriterTrain.newLine();	
								
								bufferedWriterTrain.close();
								
						}
						catch (IOException e) {
							e.printStackTrace();
						}
						
						//Write the vector data of each user for training, test, recommend set to arff files
						try {
								trainWriter = new FileWriter(trainSetFilePath, true);
								bufferedWriterTrain = new BufferedWriter(trainWriter);

								StringJoiner tfidfJoiner;
								double currTfidf;
																
								for (String currUser : trainSetUsers)
								{
									// tfidfJoiner = new StringJoiner(",");
									Map<String,Double> currDocTfidf = allUserDocumentsTFIDF.get(currUser);
									
									// for (String uniqueWord: allUniqueDocTerms)
									// {
										// if (currDocTfidf.keySet().contains(uniqueWord))
											// currTfidf = currDocTfidf.get(uniqueWord);
										// else
											// currTfidf = 0.0;
										
										// tfidfJoiner.add(String.valueOf(currTfidf));
									// }
									tfidfJoiner = vectorArffFormat(currDocTfidf,allUniqueDocTerms);
									
									bufferedWriterTrain.write(tfidfJoiner.toString() + "," + userFollowee.get(currUser));
									bufferedWriterTrain.newLine();
									
								}
								bufferedWriterTrain.close();	

						}
						catch (IOException e) {
							e.printStackTrace();
						}
					}
					else
					{
						trainSetFilePath = arffDirName + "train_set_controller.txt";
						testSetUsers = new ArrayList<String>();
						// for (String testUser : allUserDocumentsTFIDF.keySet())
						for (AID testUser : allUserAgentConnectedtoThisServer)
						{
							String testUserName = testUser.getLocalName().split("-")[0];
							testSetUsers.add(testUserName);
							System.out.println(getLocalName()+" testUser: "+testUserName);
						}
						//testset is list of original users connected to rec agent (does not include duplicated recommended user)
						
						System.out.println(getLocalName()+" testSetUsers.size: "+testSetUsers.size());
					}
					
					
					FileWriter testWriter;
					FileWriter recommendWriter;
					BufferedWriter bufferedWriterTest;
					BufferedWriter bufferedWriterRecommend;
					int uniqueWordCount = 0;
					testSetFilePath = arffDirName + "test_set_rec"+nodeNumber+".txt";
					recommendSetFilePath = arffDirName + "recommend_set_rec"+nodeNumber+".txt";
					try{
						testWriter = new FileWriter(testSetFilePath, false);
						recommendWriter = new FileWriter(recommendSetFilePath, false);
						
						bufferedWriterTest = new BufferedWriter(testWriter);
						bufferedWriterTest.write("@relation testSet");
						bufferedWriterTest.newLine();
						bufferedWriterTest.newLine();
						
						bufferedWriterRecommend = new BufferedWriter(recommendWriter);
						bufferedWriterRecommend.write("@relation recommendSet");
						bufferedWriterRecommend.newLine();
						bufferedWriterRecommend.newLine();
						
						for (String uniqueWord: allUniqueDocTerms)
						{
							bufferedWriterTest.write("@attribute word"+ uniqueWordCount +" numeric");
							bufferedWriterTest.newLine();
							bufferedWriterRecommend.write("@attribute word"+ uniqueWordCount +" numeric");
							bufferedWriterRecommend.newLine();		
							uniqueWordCount++;
						}
						
						String attributeClass = "@attribute result ";
						StringJoiner classJoiner = new StringJoiner(",","{","}");
						for (String className: followeeFollowers.keySet())
						{
							classJoiner.add(className);
						}
						
						attributeClass += classJoiner.toString();
						
						
						bufferedWriterTest.write(attributeClass);
						bufferedWriterTest.newLine();
						bufferedWriterTest.newLine();
						bufferedWriterTest.newLine();
						bufferedWriterTest.write("@data");
						bufferedWriterTest.newLine();
						
						bufferedWriterRecommend.write(attributeClass);
						bufferedWriterRecommend.newLine();
						bufferedWriterRecommend.newLine();
						bufferedWriterRecommend.newLine();
						bufferedWriterRecommend.write("@data");
						bufferedWriterRecommend.newLine();
						
						bufferedWriterTest.close();
						bufferedWriterRecommend.close();
						
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
					
					//Write the vector data of each user for training, test, recommend set to arff files
					try {
						testWriter = new FileWriter(testSetFilePath, true);
						recommendWriter = new FileWriter(recommendSetFilePath, true);
						bufferedWriterTest = new BufferedWriter(testWriter);
						bufferedWriterRecommend = new BufferedWriter(recommendWriter);

						StringJoiner tfidfJoiner;
						double currTfidf;
						
						for (String currUser : testSetUsers)
						{
							System.out.println(getLocalName()+" currUser: "+currUser);
							Map<String,Double> currDocTfidf = allUserDocumentsTFIDF.get(currUser);
							// System.out.println(getLocalName()+" currUser tfidf: ");
							// StringJoiner tfidfString = new StringJoiner(",");
							// for (String wordTfidf: currDocTfidf.keySet())
							// {
								// tfidfString.add(String.valueOf(currDocTfidf.get(wordTfidf)));
							// }
							// System.out.println(tfidfString.toString());
							
							tfidfJoiner = vectorArffFormat(currDocTfidf,allUniqueDocTerms);
								
							bufferedWriterTest.write(tfidfJoiner.toString() + "," + userFollowee.get(currUser));
							bufferedWriterTest.newLine();	
						}
						bufferedWriterTest.close();

						for (String currUser : usersRec)
						{
							Map<String,Double> currDocTfidf = allUserDocumentsTFIDF.get(currUser);							
							tfidfJoiner = vectorArffFormat(currDocTfidf,allUniqueDocTerms);
							
							bufferedWriterRecommend.write(tfidfJoiner.toString() + "," + userFollowee.get(currUser));
							bufferedWriterRecommend.newLine();
						}
						bufferedWriterRecommend.close();	
					}
					catch (IOException e) {
						e.printStackTrace();
					}
					
				}
				
				
				
				
				
				//Cosine Similarity
				if (algorithmRec == COS_SIM)
				{
					//@Jason prevent sleep from windows
					/*try {
					        new Robot().mouseMove(new Random().nextInt(1920),new Random().nextInt(1080));		         
					    } catch (AWTException e) {
					        e.printStackTrace();
					    }
					 */
					//-----------------------------CALCULATE COS-SIM SCORES-----------------------------

					startTimeAlgorithm = System.nanoTime();

					//setup array of users
					String[] users = new String[allUserDocumentsTFIDF.keySet().size()];
					String[] usersForRec = new String[usersRec.size()];
					allUserDocumentsTFIDF.keySet().toArray(users);
					usersRec.toArray(usersForRec);

					allUserScores = new TreeMap<String,TreeMap<String,Double>>();
					Map<String,Double> userScore1 = new TreeMap<String,Double>();
					Map<String,Double> userScore2 = new TreeMap<String,Double>();
					double magnitudeVector1=0.0,magnitudeVector2=0.0; //magnitude of vectors
					double dpVectors=0.0; //dot product of vectors
					double score=0.0,prevScore=0.0,newScore=0.0;
					Set<String> lowerTermsVector; 
					Set<String> higherTermsVector;		
					int docTermCount=0;
					int higherTermsUserIndex, higherTermsUserDocIndex, lowerTermsUserIndex, lowerTermsUserDocIndex;

					//initialize scores to 0.0
					System.out.println(getLocalName()+ "Initialized Scores to 0.0");

					for (int i = 0; i < usersForRec.length; i++)
					{
						for (int j = 0; j < users.length; j++)
						{
							if (!usersForRec[i].equals(users[j]))
							{
								userScore1.put(users[j], 0.0);
								allUserScores.put(usersForRec[i],(TreeMap<String,Double>) userScore1);
							}
						}
						userScore1 = new TreeMap<String,Double>();
					}

					/*
						for (int i = 0; i < users.length; i++)
						{
							for (int j = 0; j < users.length; j++)
							{
								if (!users[i].equals(users[j]))
								{
									userScore1.put(users[j], 0.0);
									allUserScores.put(users[i],(TreeMap<String,Double>) userScore1);
								}
							}
							//userScore1 = new LinkedHashMap<String,Double>();
							userScore1 = new TreeMap<String,Double>();
						}
					 */

					//System.out.println(getLocalName()+" allUserScores: "+allUserScores);


					System.out.println(getLocalName()+ "CALCULATING COS-SIM SCORES");
					//System.out.println();

					/*
					//cos Sim with each individual tweets
					for (int i = 0; i < usersForRec.length; i++)
					{
						for (int j = 0; j < users.length; j++)
						{
							if (!usersForRec[i].equals(users[j]))
							{
								int size1 = allUserDocumentsTFIDF.get(usersForRec[i]).size();
								int size2 = allUserDocumentsTFIDF.get(users[j]).size();
								ArrayList<LinkedHashMap<String, Double>> doc1 = allUserDocumentsTFIDF.get(usersForRec[i]);
								ArrayList<LinkedHashMap<String, Double>> doc2 = allUserDocumentsTFIDF.get(users[j]);

								//get documents from users[i]
								for (int k=0; k < size1; k++)
								{
									//get documents from users[j]				
									for (int l=0; l < size2; l++)
									{
										//System.out.println("COSSIM: "+allUserDocumentsTFIDF.get(users[i]).get(k)+"\t"+allUserDocumentsTFIDF.get(users[j]).get(l));

										Set<String> terms1 = doc1.get(k).keySet();
										Set<String> terms2 = doc2.get(l).keySet();
										LinkedHashMap<String,Double> docTerms1 = doc1.get(k);
										LinkedHashMap<String,Double> docTerms2 = doc2.get(l);

										for (String termUser1 : terms1)
										{
											//keeps count of when document k has gone through all its terms
											docTermCount++;
											for (String termUser2 : terms2)
											{

												if (termUser1.equals(termUser2))
												{
													//System.out.print("SAME TERMS "+termUser1+" "+termUser2+" ");
													//System.out.print("dp: "+allUserDocumentsTFIDF.get(users[j]).get(l).get(termUser2)+"*"+allUserDocumentsTFIDF.get(users[i]).get(k).get(termUser1)+"\t");
													dpVectors+=docTerms2.get(termUser2)*docTerms1.get(termUser1);
												}
											}
										}

										score=dpVectors;

										userScore1 = allUserScores.get(usersForRec[i]);
										//userScore2 = allUserScores.get(users[j]);
										if (userScore1.containsKey(users[j]))
										{
											prevScore = userScore1.get(users[j]);
											newScore = prevScore + score;
											userScore1.put(users[j], newScore);
										}
										else if (!userScore1.containsKey(users[j]))
										{
											userScore1.put(users[j], score);
										}
										/*
											if (userScore2.containsKey(users[i]))
											{
												prevScore = userScore2.get(users[i]);
												newScore = prevScore + score;
												userScore2.put(users[i], newScore);
											}
											else if (!userScore2.containsKey(users[i]))
											{
												userScore1.put(users[i], score);
											}
					 */
					//System.out.println("score: "+score);
					/*			dpVectors=0.0;
										docTermCount=0;
										score=0.0;
									}
									allUserScores.put(usersForRec[i],(TreeMap<String,Double>)userScore1);
									//allUserScores.put(users[j],(TreeMap<String,Double>)userScore2);
								}
							}
						} //end for users.length
					} //end for usersForRec.length
					 */
					//cosSim for aggregated tweets
					for (int i = 0; i < usersForRec.length; i++)
					{
						for (int j = 0; j < users.length; j++)
						{
							if (!usersForRec[i].equals(users[j]))
							{
								LinkedHashMap<String, Double> doc1 = allUserDocumentsTFIDF.get(usersForRec[i]);
								LinkedHashMap<String, Double> doc2 = allUserDocumentsTFIDF.get(users[j]);

								//System.out.println("COSSIM: "+allUserDocumentsTFIDF.get(usersForRec[i])+"\t"+allUserDocumentsTFIDF.get(users[j]));

								Set<String> terms1 = doc1.keySet();
								Set<String> terms2 = doc2.keySet();

								for (String termUser1 : terms1)
								{
									for (String termUser2 : terms2)
									{

										if (termUser1.equals(termUser2))
										{
											//System.out.print("SAME TERMS "+termUser1+" "+termUser2+" ");
											//System.out.print("dp: "+allUserDocumentsTFIDF.get(users[j]).get(termUser2)+"*"+allUserDocumentsTFIDF.get(usersForRec[i]).get(termUser1)+"\t");
											dpVectors+=doc2.get(termUser2)*doc1.get(termUser1);
										}
									}
								}

								score=dpVectors;

								userScore1 = allUserScores.get(usersForRec[i]);
								//userScore2 = allUserScores.get(users[j]);
								if (userScore1.containsKey(users[j]))
								{
									prevScore = userScore1.get(users[j]);
									newScore = prevScore + score;
									userScore1.put(users[j], newScore);
								}
								else if (!userScore1.containsKey(users[j]))
								{
									userScore1.put(users[j], score);
								}
								/*
											if (userScore2.containsKey(users[i]))
											{
												prevScore = userScore2.get(users[i]);
												newScore = prevScore + score;
												userScore2.put(users[i], newScore);
											}
											else if (!userScore2.containsKey(users[i]))
											{
												userScore1.put(users[i], score);
											}
								 */
								//System.out.println("score: "+score);
								dpVectors=0.0;
								score=0.0;

								allUserScores.put(usersForRec[i],(TreeMap<String,Double>)userScore1);
								//allUserScores.put(users[j],(TreeMap<String,Double>)userScore2);

							}
						} //end for users.length
					} //end for usersForRec.length


					// System.out.println(getLocalName()+" After COS SIM scores: "+allUserScores);

					endTimeAlgorithm = System.nanoTime();
					completionTimeAlgorithm = endTimeAlgorithm - startTimeAlgorithm;

					//Output for cosSIM
					textprocessing_wb_or_tfidf_Data.add("CosSim=TP+TFIDF+CosSim" + "\t" + agentName + "\t" + tweetCount + "\t" + completionTimeTextProcessing + "\t" + completionTimeTFIDF    + "\t" + completionTimeAlgorithm + "\t" + System.getProperty("line.separator"));
					System.out.println(agentName+"- Total Tweets Processed: " + tweetCount + " TP: " + convertMs(completionTimeTextProcessing) + " TFIDF: " + convertMs(completionTimeTFIDF) + " CosSim: " + convertMs(completionTimeAlgorithm) + " Total: " + convertMs(completionTimeTextProcessing+completionTimeTFIDF+completionTimeAlgorithm));
//					myGui.appendResult(agentName+"\nTotal Tweets Processed: " + tweetCount + " TP:" + round(completionTimeTextProcessing/1000000.00,2) + "ms TFIDF:" + round(completionTimeTFIDF/1000000.00,2) + "ms CosSim:" + round(completionTimeAlgorithm/1000000.00,2) + "ms Total:" + round((completionTimeTextProcessing+completionTimeTFIDF+completionTimeAlgorithm)/1000000.00,2)+"ms");
					myGui.appendResult(agentName+"\nTotal Tweets Processed: " + tweetCount + " TP: " + convertMs(completionTimeTextProcessing) + " ms TFIDF: " + convertMs(completionTimeTFIDF) + " ms CosSim: " + convertMs(completionTimeAlgorithm) + " ms Total: " + convertMs(completionTimeTextProcessing+completionTimeTFIDF+completionTimeAlgorithm)+" ms");
				}

				//K-Means 
				else if (algorithmRec == K_MEANS)
				{
					//@Jason prevent sleep from windows
					/*try {
					        new Robot().mouseMove(new Random().nextInt(1920),new Random().nextInt(1080));		         
					    } catch (AWTException e) {
					        e.printStackTrace();
					    }
					 */
					//**************CALCULATE K-Means SCORES******************
					/*
					//k-means on every individual tweets 
					int kClusters = 3; //number of k clusters
					long initialTweetId;
					boolean notDuplicateInitialTweetId = false; 
					boolean convergence = false; //documents remain in the same clusters
					int maxIterations = 10;
					ArrayList<Long> initialTweetIds = new ArrayList<Long>();
					List<Long> allTweetIds = new ArrayList<Long>(tweetIdDocumentVector.keySet());
					List<Long> usableTweetIds = new ArrayList<Long>(allTweetIds); //Tweet ids excluding initial tweet ids
					List<Cluster> allClusters = new ArrayList<Cluster>();
					LinkedHashMap<String,Double> centroidTFIDF = new LinkedHashMap<String,Double>();
					LinkedHashMap<String,Double> baseCentroidTFIDF = new LinkedHashMap<String,Double>(); //tfidf of 0.0 for all unique doc terms
					List<List<Point>> prevListPoints = new ArrayList<List<Point>>();

					if (kClusters > allTweetIds.size())
						kClusters = allTweetIds.size();
					if (kClusters < 2)
						kClusters = 2;

					for (String term : allUniqueDocTerms)
					{
						baseCentroidTFIDF.put(term, 0.0);
					}


					startTimeAlgorithm = System.nanoTime();

					System.out.println("Calculating k-means");

					//Choose random initial points for cluster centroid
					for (int i = 0; i < kClusters; i++)
					{
						Collections.shuffle(allTweetIds);
						initialTweetId = allTweetIds.get(0);
						if (initialTweetIds.contains(initialTweetId))
						{
							notDuplicateInitialTweetId = true;
							while (notDuplicateInitialTweetId)
							{
								System.out.println("Duplicated");
								Collections.shuffle(allTweetIds);
								initialTweetId = allTweetIds.get(0);
								if (!initialTweetIds.contains(initialTweetId))
									notDuplicateInitialTweetId = false;
							}
						}

						initialTweetIds.add(initialTweetId);

						Cluster cluster = new Cluster(i);
						LinkedHashMap<String,Double> currTFIDF = tweetIdTFIDF.get(initialTweetId);
						Point initialPoint = new Point(initialTweetId,currTFIDF);
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

					//remove initial tweet ids from remaining tweet ids
					usableTweetIds.removeAll(initialTweetIds);

					//	System.out.println("allTweetIds: "+allTweetIds);
					//	System.out.println("Initial Tweet Ids: "+initialTweetIds);
					//	System.out.println("usableTweetIds: "+usableTweetIds);

					//Create the initial clusters
					for (Cluster currCluster: allClusters)
					{
						List<Point> points = currCluster.getPoints();
						//System.out.println(currCluster.getPoints());
						//	for (Point p : points)
						//	{
						//		System.out.println(p.getTweetId());
						//	}
						//	System.out.println("currCluster centroid: "+currCluster.getCentroid());

					}

					//for (int i = 0; i < kClusters; i++) {
				    //		Cluster c = allClusters.get(i);
				    //		c.plotClusterTweets();
				    //	}


					//Assign remaining points to the closest cluster
					//assignCluster();
					double highestCosSim = 0.0; 
					int cluster = 0;                 
					double cosSim = 0.0; 

					for(Long currTweetId : usableTweetIds) {

						LinkedHashMap<String,Double> currTFIDF = tweetIdTFIDF.get(currTweetId);
						Point currPoint = new Point(currTweetId,currTFIDF);
						Cluster c;

						for(int i = 0; i < kClusters; i++) {
							c = allClusters.get(i);
							cosSim = Point.cosSimDistance(currPoint, c.getCentroid());
							if(cosSim >= highestCosSim){
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
							LinkedHashMap<String,Double> newCentroidTFIDF = new LinkedHashMap<String,Double>(baseCentroidTFIDF);
							for (Point p : listOfPoints)
							{
								LinkedHashMap<String,Double> currTFIDF = p.getTfidf();
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

							centroid.setTfidf(newCentroidTFIDF);
							clusterI.setCentroid(centroid);
						}
					}

					System.out.println("~~~~~~~~~~~~~~~~~~~TESTING IF CENTROID UNIT VECTOR~~~~~~~~~~~~~~~~~~~~~~~");
					Double sumOfMags;
					Point cent;
					for(Cluster clusterI : allClusters) {
						sumOfMags = 0.0;
						cent = clusterI.getCentroid();
						LinkedHashMap<String,Double> centtfidf = cent.getTfidf();
						for (String term : centtfidf.keySet())
						{
							sumOfMags += centtfidf.get(term)*centtfidf.get(term);
						}
						sumOfMags = Math.sqrt(sumOfMags);
						System.out.println("cluster #"+clusterI.getId()+" sumOfMags: "+sumOfMags);
						System.out.println(cent.getTfidf());
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
						//clearClusters();
						for(Cluster clusterK : allClusters) {
							clusterK.clear();
							//System.out.println("clusterK: "+clusterK.getPoints());
						}


						//getCentroids()
						List centroids = new ArrayList(kClusters);
						for(Cluster clusterH : allClusters) {
							Point currCentroid = clusterH.getCentroid();
							Point point = new Point(currCentroid.getTweetId(),currCentroid.getTfidf());
							centroids.add(point);
						}
						List<Point> lastCentroids = centroids;

						//Assign points to the closer cluster
						//assignCluster();
						highestCosSim = 0.0; 
						cluster = 0;                 
						cosSim = 0.0; 

						for(Long currTweetId : allTweetIds) {

							LinkedHashMap<String,Double> currTFIDF = tweetIdTFIDF.get(currTweetId);
							Point currPoint = new Point(currTweetId,currTFIDF);

							for(int i = 0; i < kClusters; i++) {
								Cluster c = allClusters.get(i);
								cosSim = Point.cosSimDistance(currPoint, c.getCentroid());
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
								LinkedHashMap<String,Double> newCentroidTFIDF = new LinkedHashMap<String,Double>(baseCentroidTFIDF);
								for (Point p : listOfPoints)
								{
									LinkedHashMap<String,Double> currTFIDF = p.getTfidf();
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

								centroid.setTfidf(newCentroidTFIDF);
								clusterI.setCentroid(centroid);
							}
						}


						System.out.println("~~~~~~~~~~~~~~~~~~~TESTING IF CENTROID UNIT VECTOR AFTER ITERATION "+iteration+"~~~~~~~~~~~~~~~~~~~~~~~");

						for(Cluster clusterI : allClusters) {
							sumOfMags = 0.0;
							cent = clusterI.getCentroid();
							LinkedHashMap<String,Double> centtfidf = cent.getTfidf();
							for (String term : centtfidf.keySet())
							{
								sumOfMags += centtfidf.get(term)*centtfidf.get(term);
							}
							sumOfMags = Math.sqrt(sumOfMags);
							System.out.println("cluster #"+clusterI.getId()+" sumOfMags: "+sumOfMags);
							System.out.println(cent.getTfidf());
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

						System.out.println("#################");
						System.out.println("Iteration: " + iteration);


						//	for (int i = 0; i < kClusters; i++) {
					    //		Cluster c = allClusters.get(i);
					    //		c.plotClusterTweets();
					    //	}

					}

					//Output the clusters and the tweets
					System.out.println("THE CLUSTERS");
					for (Cluster c : allClusters)
					{
						c.plotClusterTweets(tweetIdUser);
					}




					String[] users = new String[allUserDocumentsTFIDF.keySet().size()];
					allUserDocumentsTFIDF.keySet().toArray(users);
					allUserScores = new TreeMap<String,TreeMap<String,Double>>();
					Map<String,Double> userScore1 = new TreeMap<String,Double>();
					Map<String,Double> userScore2 = new TreeMap<String,Double>();
					double dpVectors = 0.0, score = 0.0, prevScore = 0.0;

					System.out.println("Initialized Scores to 0.0");
					for (int i = 0; i < users.length; i++)
					{
						for (int j = 0; j < users.length; j++)
						{
							if (!users[i].equals(users[j]))
							{
								userScore1.put(users[j], 0.0);
								allUserScores.put(users[i],(TreeMap<String,Double>) userScore1);
							}
						}
						//userScore1 = new LinkedHashMap<String,Double>();
						userScore1 = new TreeMap<String,Double>();
					}


					//System.out.println("allUserScores:" +allUserScores);

					for (int i = 0; i < allClusters.size(); i++)
					{
						//Not comparing where clusters have a size of 1 or 0
						List<Point> pointsInCluster = allClusters.get(i).getPoints();
						if (pointsInCluster.size() > 1)
						{
							for (int j = 0; j < pointsInCluster.size()-1; j++)
							{
								long tweetId1 = pointsInCluster.get(j).getTweetId();
								LinkedHashMap<String,Double> tweetId1Tfidf = pointsInCluster.get(j).getTfidf();
								Set<String> terms1 = tweetId1Tfidf.keySet();

								for (int k = j+1; k < pointsInCluster.size(); k++)
								{
									long tweetId2 = pointsInCluster.get(k).getTweetId();
									LinkedHashMap<String,Double> tweetId2Tfidf = pointsInCluster.get(k).getTfidf();
									Set<String> terms2 = tweetId2Tfidf.keySet();

									String user1, user2;
									user1 = tweetIdUser.get(tweetId1);
									user2 = tweetIdUser.get(tweetId2);

									if (!user1.equals(user2))
									{
										for (String term1 : terms1)
										{
											for (String term2 : terms2)
											{

												if (term1.equals(term2))
												{
													dpVectors+=tweetId1Tfidf.get(term1)*tweetId2Tfidf.get(term2);
												}
											}
										}

										//System.out.println("user1: "+user1+" user2: "+user2);
										//Update the scores of the users
										//System.out.println(allUserScores.get(user1));

										//prevScore = allUserScores.get(user1).get(user2);
					        			//	score = prevScore + dpVectors;
					        			//	userScore1 = allUserScores.get(user1);
					        			//	userScore1.put(user2, score);
					        			//	allUserScores.put(user1,(TreeMap<String,Double>)userScore1);
					        			//	userScore2 = allUserScores.get(user2);
					        			//	userScore2.put(user1, score);
					        			//	allUserScores.put(user2,(TreeMap<String,Double>)userScore2);

										prevScore = allUserScores.get(user1).get(user2);
										score = prevScore + dpVectors;
										allUserScores.get(user1).put(user2, score);
										allUserScores.get(user2).put(user1, score);

									}
									dpVectors=0.0;
									score=0.0;
								} //end for (int k = j+1; k < pointsInCluster.size(); k++)
							} //end for (int j = 0; j < pointsInCluster.size()-1; j++)
						} //end if (pointsInCluster.size() > 1)
					} //for (int i = 0; i < allClusters.size(); i++)

					//for (String s : allUserScores.keySet())
					//	{
					//		System.out.print("user: "+s+"\t");
					//		System.out.println(allUserScores.get(s));
					//	}
					 */
					//k-means on aggregated tweets as document vectors
					int kClusters = 3; //number of k clusters
					boolean convergence = false; //documents remain in the same clusters
					int maxIterations = 10;

					List<Cluster> allClusters = new ArrayList<Cluster>();
					ArrayList<String> allUserNames = new ArrayList<String>(allUserDocumentsTFIDF.keySet());
					ArrayList<String> remainingUserNames = new ArrayList<String>(allUserDocumentsTFIDF.keySet()); //Remaining usernames after initial usernames chosen for seeds

					LinkedHashMap<String,Double> centroidTFIDF = new LinkedHashMap<String,Double>();
					LinkedHashMap<String,Double> baseCentroidTFIDF = new LinkedHashMap<String,Double>(); //tfidf of 0.0 for all unique doc terms
					List<List<Point>> prevListPoints = new ArrayList<List<Point>>();
					
					LinkedHashMap<String,Double> centroidTF = new LinkedHashMap<String,Double>();
					LinkedHashMap<String,Double> baseCentroidTF = new LinkedHashMap<String,Double>(); //tf of 0.0 for all unique doc terms



//					if (kClusters > allUserDocumentsTF.size())
//						kClusters = allUserDocumentsTF.size();
					if (kClusters > allUserDocumentsTFIDF.size())
						kClusters = allUserDocumentsTFIDF.size();
					if (kClusters < 2)
						kClusters = 2;

					for (String term : allUniqueDocTerms)
					{
						baseCentroidTFIDF.put(term, 0.0);
//						baseCentroidTF.put(term, 0.0);
					}


					startTimeAlgorithm = System.nanoTime();

					System.out.println("Calculating k-means");

					//Choose random initial points for cluster centroid
					for (int i = 0; i < kClusters; i++)
					{
						System.out.println("remainingUserNames: "+remainingUserNames);
						String initialUserName;
						Collections.shuffle(remainingUserNames);
						System.out.println("shuffled remainingUserNames: "+remainingUserNames);
//						initialUserName = remainingUserNames.get(0);
//						remainingUserNames.remove(0);
						// if (remainingUserNames.contains("Simon_Pella"))
						// {
							// initialUserName = "Simon_Pella";
							// remainingUserNames.remove(initialUserName);
						// }
						// else if (remainingUserNames.contains("Simon_Pella_generated"))
						// {
							// initialUserName = "Simon_Pella_generated";
							// remainingUserNames.remove(initialUserName);
						// }
						// else if (remainingUserNames.contains("styleofthesix"))
						// {
							// initialUserName = "styleofthesix";
							// remainingUserNames.remove(initialUserName);
						// }
						// else if (remainingUserNames.contains("styleofthesix_generated"))
						// {
							// initialUserName = "styleofthesix_generated";
							// remainingUserNames.remove(initialUserName);
						// }
						// else if (remainingUserNames.contains("AHKru"))
						// {
							// initialUserName = "AHKru";
							// remainingUserNames.remove(initialUserName);
						// }
						// else if (remainingUserNames.contains("AHKru_generated"))
						// {
							// initialUserName = "AHKru_generated";
							// remainingUserNames.remove(initialUserName);
						// }
						// else if (remainingUserNames.contains("Steve"))
						// {
							// initialUserName = "Steve";
							// remainingUserNames.remove(initialUserName);
						// }
						// else if (remainingUserNames.contains("Alice"))
						// {
							// initialUserName = "Alice";
							// remainingUserNames.remove(initialUserName);
						// }
						// else if (remainingUserNames.contains("Fred"))
						// {
							// initialUserName = "Fred";
							// remainingUserNames.remove(initialUserName);
						// }
						// else
						// {
							// initialUserName = remainingUserNames.get(0);
							// remainingUserNames.remove(0);
						// }
						
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
						else if (remainingUserNames.contains("TorontoStar"))
						{
							initialUserName = "TorontoStar";
							remainingUserNames.remove(initialUserName);
						}
						else if (remainingUserNames.contains("Doggos"))
						{
							initialUserName = "Doggos";
							remainingUserNames.remove(initialUserName);
						}
						else if (remainingUserNames.contains("MoviesGuy"))
						{
							initialUserName = "MoviesGuy";
							remainingUserNames.remove(initialUserName);
						}
						else if (remainingUserNames.contains("SportsGuy"))
						{
							initialUserName = "SportsGuy";
							remainingUserNames.remove(initialUserName);
						}
						else
						{
							initialUserName = remainingUserNames.get(0);
							remainingUserNames.remove(0);
						}
						
						
//						boolean isGenerated = true;
//						while (isGenerated)
//						{
//							String pattern = "_generated";
//							System.out.println("currentChoice: "+remainingUserNames.get(0));
//							
//							if (remainingUserNames.get(0).contains(pattern))
//							{
//								remainingUserNames.remove(0);
//								System.out.println("Removed generated");
//							}
//							else
//							{
//								isGenerated = false;
//							}
//						}
//						initialUserName = remainingUserNames.get(0);
//						remainingUserNames.remove(0);
						System.out.println("initialUserName: "+initialUserName);
						
						Cluster cluster = new Cluster(i);
						LinkedHashMap<String,Double> currTFIDF = allUserDocumentsTFIDF.get(initialUserName);
//						LinkedHashMap<String,Double> currTF = allUserDocumentsTF.get(initialUserName);
						Point initialPoint = new Point(initialUserName,currTFIDF);
//						Point initialPoint = new Point(initialUserName,currTF);
						initialPoint.setCluster(i);
						cluster.addPoint(initialPoint);

						centroidTFIDF = new LinkedHashMap<String,Double>(baseCentroidTFIDF);
//						centroidTF = new LinkedHashMap<String,Double>(baseCentroidTF);

						for (String term : currTFIDF.keySet())
//						for (String term : currTF.keySet())
						{
							centroidTFIDF.put(term,currTFIDF.get(term));
//							centroidTF.put(term,currTF.get(term));
						}

						Point centroid = new Point(-1,centroidTFIDF);
//						Point centroid = new Point(-1,centroidTF);
						cluster.setCentroid(centroid);

						allClusters.add(cluster);

					}



					//Create the initial clusters
					// for (Cluster currCluster: allClusters)
					// {
						// List<Point> points = currCluster.getPoints();
						//System.out.println(currCluster.getPoints());
						//	for (Point p : points)
						//	{
						//		System.out.println(p.getTweetId());
						//	}
						//	System.out.println("currCluster centroid: "+currCluster.getCentroid());

					// }

					//for (int i = 0; i < kClusters; i++) {
					//		Cluster c = allClusters.get(i);
					//		c.plotClusterTweets();
					//	}


					//Assign remaining points to the closest cluster
					//assignCluster();
					double highestCosSim = 0.0; 
					int cluster = 0;                 
					double cosSim = 0.0; 

					for(String currUserName : remainingUserNames) {

						LinkedHashMap<String,Double> currTFIDF = allUserDocumentsTFIDF.get(currUserName);
//						LinkedHashMap<String,Double> currTF = allUserDocumentsTF.get(currUserName);
						Point currPoint = new Point(currUserName,currTFIDF);
//						Point currPoint = new Point(currUserName,currTF);
						Cluster c;

						for(int i = 0; i < kClusters; i++) {
							c = allClusters.get(i);
							cosSim = currPoint.cosSimDistance(c.getCentroid());
							//if(cosSim >= highestCosSim){
							if(cosSim > highestCosSim){
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
							LinkedHashMap<String,Double> newCentroidTFIDF = new LinkedHashMap<String,Double>(baseCentroidTFIDF);
//							LinkedHashMap<String,Double> newCentroidTF = new LinkedHashMap<String,Double>(baseCentroidTF);
							for (Point p : listOfPoints)
							{
								Map<String,Double> currTFIDF = p.getTfidf_or_Tf();
//								LinkedHashMap<String,Double> currTF = p.getTfidf_or_Tf();
								for (String term : currTFIDF.keySet())
//								for (String term : currTF.keySet())
								{
									double pointTFIDFValue = 0.0;
									double centroidTFIDFValue = 0.0;
									pointTFIDFValue = currTFIDF.get(term);
									centroidTFIDFValue = newCentroidTFIDF.get(term);
									newCentroidTFIDF.put(term, pointTFIDFValue+centroidTFIDFValue);
//									double pointTFValue = 0.0;
//									double centroidTFValue = 0.0;
//									pointTFValue = currTF.get(term);
//									centroidTFValue = newCentroidTF.get(term);
//									newCentroidTF.put(term, pointTFValue+centroidTFValue);
								}
							}

							double newClusterMag = 0.0;
							double currTermTFIDF = 0.0;
//							double currTermTF = 0.0;

							for (String term : newCentroidTFIDF.keySet())
//							for (String term : newCentroidTF.keySet())
							{
								currTermTFIDF = 0.0;
//								currTermTF = 0.0;

								currTermTFIDF = newCentroidTFIDF.get(term);
//								currTermTF = newCentroidTF.get(term);
								
								if (currTermTFIDF > 0.0)
//								if (currTermTF > 0.0)
								{
									currTermTFIDF = currTermTFIDF / numPoints;
									newClusterMag += currTermTFIDF * currTermTFIDF;
									newCentroidTFIDF.put(term, currTermTFIDF);
//									currTermTF = currTermTF / numPoints;
//									newClusterMag += currTermTF * currTermTF;
//									newCentroidTF.put(term, currTermTF);
								}

							}

							//Normalize the new cluster centroid as it might not be normalized

							newClusterMag = Math.sqrt(newClusterMag);
							for (String term : newCentroidTFIDF.keySet())
//							for (String term : newCentroidTF.keySet())
							{
								currTermTFIDF = newCentroidTFIDF.get(term);
//								currTermTF = newCentroidTF.get(term);
								
								if (currTermTFIDF > 0.0)
//								if (currTermTF > 0.0)
								{
									currTermTFIDF = currTermTFIDF / newClusterMag;
									newCentroidTFIDF.put(term, currTermTFIDF);
//									currTermTF = currTermTF / newClusterMag;
//									newCentroidTF.put(term, currTermTF);
								}

							}

							centroid.setTfidf_or_Tf(newCentroidTFIDF);
//							centroid.setTfidf_or_Tf(newCentroidTF);
							clusterI.setCentroid(centroid);
						}
					}

//					System.out.println("~~~~~~~~~~~~~~~~~~~TESTING IF CENTROID UNIT VECTOR~~~~~~~~~~~~~~~~~~~~~~~");
					Double sumOfMags;
					Point cent;
					for(Cluster clusterI : allClusters) {
						sumOfMags = 0.0;
						cent = clusterI.getCentroid();
						Map<String,Double> centtfidf = cent.getTfidf_or_Tf();
//						LinkedHashMap<String,Double> centtf = cent.getTfidf_or_Tf();
						for (String term : centtfidf.keySet())
//						for (String term : centtf.keySet())
						{
							sumOfMags += centtfidf.get(term)*centtfidf.get(term);
//							sumOfMags += centtf.get(term)*centtf.get(term);
						}
						sumOfMags = Math.sqrt(sumOfMags);
//						System.out.println("cluster #"+clusterI.getId()+" sumOfMags: "+sumOfMags);
////						System.out.println(cent.getTfidf_or_Tf());
//						System.out.println(cent.getTfidf_or_Tf());
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
						//clearClusters();
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

							LinkedHashMap<String,Double> currTFIDF = allUserDocumentsTFIDF.get(currUserName);
//							LinkedHashMap<String,Double> currTF = allUserDocumentsTF.get(currUserName);
							Point currPoint = new Point(currUserName,currTFIDF);
//							Point currPoint = new Point(currUserName,currTF);

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
								LinkedHashMap<String,Double> newCentroidTFIDF = new LinkedHashMap<String,Double>(baseCentroidTFIDF);
//								LinkedHashMap<String,Double> newCentroidTF = new LinkedHashMap<String,Double>(baseCentroidTF);
								for (Point p : listOfPoints)
								{
									Map<String,Double> currTFIDF = p.getTfidf_or_Tf();
//									LinkedHashMap<String,Double> currTF = p.getTfidf_or_Tf();
									for (String term : currTFIDF.keySet())
//									for (String term : currTF.keySet())
									{
										double pointTFIDFValue = 0.0;
										double centroidTFIDFValue = 0.0;
										pointTFIDFValue = currTFIDF.get(term);
										centroidTFIDFValue = newCentroidTFIDF.get(term);
										newCentroidTFIDF.put(term, pointTFIDFValue+centroidTFIDFValue);
//										double pointTFValue = 0.0;
//										double centroidTFValue = 0.0;
//										pointTFValue = currTF.get(term);
//										centroidTFValue = newCentroidTF.get(term);
//										newCentroidTF.put(term, pointTFValue+centroidTFValue);
									}
								}

								double newClusterMag = 0.0;
								double currTermTFIDF = 0.0;
//								double currTermTF = 0.0;

								for (String term : newCentroidTFIDF.keySet())
//								for (String term : newCentroidTF.keySet())
								{
									currTermTFIDF = 0.0;
									currTermTFIDF = newCentroidTFIDF.get(term);
									if (currTermTFIDF > 0.0)
//									currTermTF = 0.0;
//									currTermTF = newCentroidTF.get(term);
//									if (currTermTF > 0.0)
									{
										currTermTFIDF = currTermTFIDF / numPoints;
										newClusterMag += currTermTFIDF * currTermTFIDF;
										newCentroidTFIDF.put(term, currTermTFIDF);
//										currTermTF = currTermTF / numPoints;
//										newClusterMag += currTermTF * currTermTF;
//										newCentroidTF.put(term, currTermTF);
									}

								}

								//Normalize the new cluster centroid as it might not be normalized

								newClusterMag = Math.sqrt(newClusterMag);
								for (String term : newCentroidTFIDF.keySet())
//								for (String term : newCentroidTF.keySet())
								{
									currTermTFIDF = newCentroidTFIDF.get(term);
//									currTermTF = newCentroidTF.get(term);

									if (currTermTFIDF > 0.0)
//									if (currTermTF > 0.0)
									{
										currTermTFIDF = currTermTFIDF / newClusterMag;
										newCentroidTFIDF.put(term, currTermTFIDF);
//										currTermTF = currTermTF / newClusterMag;
//										newCentroidTF.put(term, currTermTF);
									}

								}

								centroid.setTfidf_or_Tf(newCentroidTFIDF);
//								centroid.setTfidf_or_Tf(newCentroidTF);
								clusterI.setCentroid(centroid);
							}
						}


						System.out.println("~~~~~~~~~~~~~~~~~~~TESTING IF CENTROID UNIT VECTOR AFTER ITERATION "+iteration+"~~~~~~~~~~~~~~~~~~~~~~~");

						for(Cluster clusterI : allClusters) {
							sumOfMags = 0.0;
							cent = clusterI.getCentroid();
							Map<String,Double> centtfidf = cent.getTfidf_or_Tf();
//							LinkedHashMap<String,Double> centtf = cent.getTfidf_or_Tf();
							for (String term : centtfidf.keySet())
//							for (String term : centtf.keySet())
							{
								sumOfMags += centtfidf.get(term)*centtfidf.get(term);
//								sumOfMags += centtf.get(term)*centtf.get(term);
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
					// for (Cluster c : allClusters)
					// {
						// c.plotCluster();
					// }

					String[] users = new String[allUserDocumentsTFIDF.keySet().size()];
					allUserDocumentsTFIDF.keySet().toArray(users);
					allUserScores = new TreeMap<String,TreeMap<String,Double>>();
					Map<String,Double> userScore1 = new TreeMap<String,Double>();
					Map<String,Double> userScore2 = new TreeMap<String,Double>();
					double dpVectors = 0.0, score = 0.0, prevScore = 0.0;

					System.out.println("kmeans clustering completed Initialized Scores to 0.0");
					for (int i = 0; i < users.length; i++)
					{
						for (int j = 0; j < users.length; j++)
						{
							if (!users[i].equals(users[j]))
							{
								userScore1.put(users[j], 0.0);							
							}
						}
						//userScore1 = new LinkedHashMap<String,Double>();
						allUserScores.put(users[i],(TreeMap<String,Double>) userScore1);
						userScore1 = new TreeMap<String,Double>();
					}

					System.out.println(getLocalName() + " All scores initialized to 0");
					//System.out.println("allUserScores:" +allUserScores);
					
					System.out.println(getLocalName() + " Calculating Scores");
					
					int scoreTimes = 0;
					
					for (int i = 0; i < allClusters.size(); i++)
					{
						//Not comparing where clusters have a size of 1 or 0
						List<Point> pointsInCluster = allClusters.get(i).getPoints();
						if (pointsInCluster.size() > 1)
						{
							for (int j = 0; j < pointsInCluster.size()-1; j++)
							{
								String userName1 = pointsInCluster.get(j).getUserName();
								Map<String,Double> userName1Tfidf = pointsInCluster.get(j).getTfidf_or_Tf();
								Set<String> terms1 = userName1Tfidf.keySet();
//								LinkedHashMap<String,Double> userName1Tf = pointsInCluster.get(j).getTfidf_or_Tf();
//								Set<String> terms1 = userName1Tf.keySet();

								for (int k = j+1; k < pointsInCluster.size(); k++)
								{
									String userName2 = pointsInCluster.get(k).getUserName();
									Map<String,Double> userName2Tfidf = pointsInCluster.get(k).getTfidf_or_Tf();
									Set<String> terms2 = userName2Tfidf.keySet();
//									LinkedHashMap<String,Double> userName2Tf = pointsInCluster.get(k).getTfidf_or_Tf();
//									Set<String> terms2 = userName2Tf.keySet();

									for (String term1 : terms1)
									{
										for (String term2 : terms2)
										{

											if (term1.equals(term2))
											{
												dpVectors+=userName1Tfidf.get(term1)*userName2Tfidf.get(term2);
//												dpVectors+=userName1Tf.get(term1)*userName2Tf.get(term2);
											}
										}
									}

									//System.out.println("user1: "+user1+" user2: "+user2);
									//Update the scores of the users
									//System.out.println(allUserScores.get(user1));

									//prevScore = allUserScores.get(user1).get(user2);
									//	score = prevScore + dpVectors;
									//	userScore1 = allUserScores.get(user1);
									//	userScore1.put(user2, score);
									//	allUserScores.put(user1,(TreeMap<String,Double>)userScore1);
									//	userScore2 = allUserScores.get(user2);
									//	userScore2.put(user1, score);
									//	allUserScores.put(user2,(TreeMap<String,Double>)userScore2);

									score = dpVectors;
									allUserScores.get(userName1).put(userName2, score);
									allUserScores.get(userName2).put(userName1, score);

									scoreTimes++;
									
									if (scoreTimes == users.length*users.length || scoreTimes % 1000 == 0)
										System.out.println("scoreTimes: "+scoreTimes);
									
									dpVectors=0.0;
									score=0.0;

								} //end for (int k = j+1; k < pointsInCluster.size(); k++)
							} //end for (int j = 0; j < pointsInCluster.size()-1; j++)
						} //end if (pointsInCluster.size() > 1)
					} //for (int i = 0; i < allClusters.size(); i++)

					//for (String s : allUserScores.keySet())
					//	{
					//		System.out.print("user: "+s+"\t");
					//		System.out.println(allUserScores.get(s));
					//	}

					System.out.println(getLocalName() + " Completed the scores Total Score Times: "+scoreTimes);

					
					


					endTimeAlgorithm = System.nanoTime();
					completionTimeAlgorithm = endTimeAlgorithm - startTimeAlgorithm;						

					//Output for K-means	   					
					textprocessing_wb_or_tfidf_Data.add("K-means=TP+TFIDF+K-means" + "\t" + agentName + "\t" + tweetCount + "\t" + completionTimeTextProcessing + "\t" + completionTimeTFIDF    + "\t" + completionTimeAlgorithm + "\t" + System.getProperty("line.separator"));
//					System.out.println(agentName+"- Total Tweets Processed: " + tweetCount + " TP:" + convertMs(completionTimeTextProcessing) + "ms TFIDF:" + convertMs(completionTimeTFIDF) + "ms K-means:" + convertMs(completionTimeAlgorithm) + "ms Total:" + convertMs(completionTimeTextProcessing+completionTimeTFIDF+completionTimeAlgorithm)+"ms");
					System.out.println("Mapper"+nodeNumber+"- Total Tweets Processed: " + tweetCount + " TP: " + convertMs(completionTimeTextProcessing) + " ms TFIDF: " + convertMs(completionTimeTFIDF) + " ms Reducer"+ nodeNumber+ " K-means: " + convertMs(completionTimeAlgorithm) + " ms Total: " + convertMs(completionTimeTextProcessing+completionTimeTFIDF+completionTimeAlgorithm)+" ms");
//					myGui.appendResult(agentName+"\nTotal Tweets Processed: " + tweetCount + " TP:" + round(completionTimeTextProcessing/1000000.00,2) + "ms TFIDF:" + round(completionTimeTFIDF/1000000.00,2) + "ms K-means:" + round(completionTimeAlgorithm/1000000.00,2) + "ms Total:" + round((completionTimeTextProcessing+completionTimeTFIDF+completionTimeAlgorithm)/1000000.00,2)+"ms");
					myGui.appendResult("Mapper"+nodeNumber+"- Total Tweets Processed: " + tweetCount + " TP: " + convertMs(completionTimeTextProcessing) + " ms TFIDF: " + convertMs(completionTimeTFIDF) + " ms Reducer"+ nodeNumber+ " K-means: " + convertMs(completionTimeAlgorithm) + " ms Total: " + convertMs(completionTimeTextProcessing+completionTimeTFIDF+completionTimeAlgorithm)+" ms");

				}
				
				//SVM Recommendation?
				else if (algorithmRec == SVM)
				{
					
					Classifier svmModel = null;
					BufferedReader datafile = readDataFile(trainSetFilePath);
					Instances data = null;
					try{
						data = new Instances(datafile);
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
					
					//Train if single node and SVM
					if (numRecAgents < 2)
					{
						//Train the SVM
						data.setClassIndex(data.numAttributes() - 1);
						svmModel = new SMO();
											
						try{
							svmModel.buildClassifier(data);
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
					else
						svmModel = trainedCentralSVM;
					
											
					//Test the SVM

					startTimeAlgorithm = System.nanoTime();
					
					BufferedReader testfile = readDataFile(testSetFilePath);
					BufferedReader recommendFile = readDataFile(recommendSetFilePath);

					
					Instances test = null;
					Instances recommendInstances = null;
					
					try{
						test = new Instances(testfile);
						recommendInstances = new Instances(recommendFile);
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
					
					Remove remove = new Remove();                
					remove.setAttributeIndices("last");
					FilteredClassifier fc = new FilteredClassifier();
					fc.setFilter(remove);

					FilteredClassifier svmFiltered = new FilteredClassifier();
					
					data.setClassIndex(data.numAttributes() - 1);
					test.setClassIndex(test.numAttributes() - 1);
					recommendInstances.setClassIndex(recommendInstances.numAttributes() - 1);

					// Collect every group of predictions for current model in a FastVector
					FastVector predictionsRec = new FastVector();
					FastVector predictions = new FastVector();
				
					// evaluate classifier and print some statistics
					Evaluation evalSvm = null;
					Evaluation eval = null;
					try{
						evalSvm = new Evaluation(data);
						eval = new Evaluation(data);
						eval.evaluateModel(svmModel, test);
						evalSvm.evaluateModel(svmModel, recommendInstances);
						predictionsRec.appendElements(evalSvm.predictions());
						predictions.appendElements(eval.predictions());
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					

					// create copy
					Instances predictedLabels = new Instances(test);
					Instances predictedLabelsRec = new Instances(recommendInstances);

					// label instances
					// for SMO/SVM
					// for (int i = 0; i < test.numInstances(); i++) {
					double clsLabel = 0.0;
					Map<String,String> userPredictedFollowee = new LinkedHashMap<String,String>();
					String predictedClass;
					//For just recommended user
					for (int i = 0; i < recommendInstances.numInstances(); i++) {
						
						try {
							clsLabel = svmModel.classifyInstance(recommendInstances.instance(i));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						
						predictedLabelsRec.instance(i).setClassValue(clsLabel);
						
						if (i == recommendInstances.numInstances()-1)
						{
							predictedClass = predictedLabelsRec.instance(i).stringValue(data.numAttributes()-1);
			//				System.out.println(predictedLabels.instance(i).stringValue(data.numAttributes()-2));
							System.out.println(getLocalName()+" Recommended Class: "+predictedLabelsRec.instance(i).stringValue(data.numAttributes()-1));
							userPredictedFollowee.put(usersRec.get(i),predictedClass);
						}
					}

					//For entire test set
					for (int i = 0; i < test.numInstances(); i++) {
						
						try {
							clsLabel = svmModel.classifyInstance(test.instance(i));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						
						predictedLabels.instance(i).setClassValue(clsLabel);
						
						predictedClass = predictedLabels.instance(i).stringValue(data.numAttributes()-1);
			//			System.out.println(predictedLabels.instance(i).stringValue(data.numAttributes()-2));
						System.out.println(getLocalName()+" Predicted Class: "+predictedLabels.instance(i).stringValue(data.numAttributes()-1));
					}						

					// Calculate overall accuracy of current classifier on all splits
					double accuracy = calculateAccuracy(predictions);

					
					System.out.println(getLocalName()+" Accuracy of " + svmModel.getClass().getSimpleName() + ": "
					+ String.format("%.2f%%", accuracy)
					+ "\n---------------------------------");					
					myGui.appendResult(getLocalName()+" Accuracy of " + svmModel.getClass().getSimpleName() + ": "
					+ String.format("%.2f%%", accuracy)
					+ "\n---------------------------------");
					
					allUserScores = new TreeMap<String,TreeMap<String,Double>>();
					Map<String,Double> userScore1 = new TreeMap<String,Double>();
					double followeeScore = 0.0;
					for (String recUser : userPredictedFollowee.keySet())
					{
						for (String followeeUser: followeeFollowers.keySet())
						{
							if (userPredictedFollowee.get(recUser).equals(followeeUser))
								followeeScore = 1.0;
							else
								followeeScore = 0.0;
							
							userScore1.put(followeeUser,followeeScore);
						}
						allUserScores.put(recUser,(TreeMap<String,Double>)userScore1);
						userScore1 = new TreeMap<String,Double>();
					}
					
					
					endTimeAlgorithm = System.nanoTime();
					completionTimeAlgorithm = endTimeAlgorithm - startTimeAlgorithm;						

					//Output for SVM	   					
					textprocessing_wb_or_tfidf_Data.add("SVM=TP+TFIDF+SVM" + "\t" + agentName + "\t" + tweetCount + "\t" + completionTimeTextProcessing + "\t" + completionTimeTFIDF    + "\t" + completionTimeAlgorithm + "\t" + System.getProperty("line.separator"));
//					System.out.println(agentName+"- Total Tweets Processed: " + tweetCount + " TP:" + convertMs(completionTimeTextProcessing) + "ms TFIDF:" + convertMs(completionTimeTFIDF) + "ms K-means:" + convertMs(completionTimeAlgorithm) + "ms Total:" + convertMs(completionTimeTextProcessing+completionTimeTFIDF+completionTimeAlgorithm)+"ms");
					System.out.println("Mapper"+nodeNumber+"- Total Tweets Processed: " + tweetCount + " TP: " + convertMs(completionTimeTextProcessing) + " ms TFIDF: " + convertMs(completionTimeTFIDF) + " ms Reducer"+ nodeNumber+ " SVM: " + convertMs(completionTimeAlgorithm) + " ms Total: " + convertMs(completionTimeTextProcessing+completionTimeTFIDF+completionTimeAlgorithm)+" ms");
//					myGui.appendResult(agentName+"\nTotal Tweets Processed: " + tweetCount + " TP:" + round(completionTimeTextProcessing/1000000.00,2) + "ms TFIDF:" + round(completionTimeTFIDF/1000000.00,2) + "ms K-means:" + round(completionTimeAlgorithm/1000000.00,2) + "ms Total:" + round((completionTimeTextProcessing+completionTimeTFIDF+completionTimeAlgorithm)/1000000.00,2)+"ms");
					myGui.appendResult("Mapper"+nodeNumber+"- Total Tweets Processed: " + tweetCount + " TP: " + convertMs(completionTimeTextProcessing) + " ms TFIDF: " + convertMs(completionTimeTFIDF) + " ms Reducer"+ nodeNumber+ " SVM: " + convertMs(completionTimeAlgorithm) + " ms Total: " + convertMs(completionTimeTextProcessing+completionTimeTFIDF+completionTimeAlgorithm)+" ms");
				}

				// try {
					// FileWriter writer = new FileWriter("scores_Not_Normalized.txt", true); //append
					// BufferedWriter bufferedWriter = new BufferedWriter(writer);
					
					// for (String userRec: allUserScores.keySet())
					// {
						// bufferedWriter.write(getLocalName()+" "+ userRec+" Scores: [\t");
						
						// TreeMap<String,Double> otherUserScores = allUserScores.get(userRec);
						
						// for (String otherUser: otherUserScores.keySet())
						// {
							// double oldScore = otherUserScores.get(otherUser);
							
							// bufferedWriter.write(otherUser+":"+oldScore+"\t");				
						// }
						
					// }
					// bufferedWriter.write("]");
					// bufferedWriter.newLine();
					// bufferedWriter.close();
				// } catch (IOException e) {
					//TODO Auto-generated catch block
					// e.printStackTrace();
				// }
				
				
				//Added in @Jason display text processing wb/tfidf kmean/cosSIM time in ms
				for (String s : textprocessing_wb_or_tfidf_Data){
					System.out.print(s);
				}

				myGui.setTPTime(completionTimeTextProcessing/1000000.00);
				myGui.setTfidfTime(completionTimeTFIDF/1000000.00);
				myGui.setAlgorithmTime(completionTimeAlgorithm/1000000.00);


				System.out.println(getLocalName()+" tweetCount: "+ tweetCount+"\ttweetreceived: "+tweetsToReceive);


				//OUTPUT TO TIMING, NEED TO EDIT

				String outputFilename = "Results/Timing/" + referenceUser + "/" + "Distributed_Server_TP_TFIDF_Algorithm" + numRecAgents + ".txt"; 
				try {
					saveToFile_array(outputFilename, textprocessing_wb_or_tfidf_Data, "append");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				textprocessing_wb_or_tfidf_Data.clear();

				ACLMessage msg7 = new ACLMessage( ACLMessage.INFORM );
				msg7.addReceiver( new AID("Starter Agent", AID.ISLOCALNAME) );
				msg7.setPerformative( ACLMessage.INFORM );
				msg7.setContent("Tweeting TFIDF Algorithm Calculation Completed");
				//@Jason took out conversationID
				msg7.setOntology("Tweets TFIDF Algorithm Calculation Done");
				send(msg7);

				ACLMessage toMergeMsg = new ACLMessage(ACLMessage.INFORM);
				toMergeMsg.addReceiver( new AID("Organizing Agent1", AID.ISLOCALNAME) );
				toMergeMsg.setPerformative(ACLMessage.INFORM);
				toMergeMsg.setOntology("Merge Lists");
				try {
					toMergeMsg.setContentObject((Serializable) allUserScores);
					send(toMergeMsg);
					System.out.println(getLocalName()+" sent toMergeMsg");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}

		protected void saveToFile_array(String filename, ArrayList<String> result, String append) throws IOException 
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true));
			if(append=="not_append")
			{
				writer = new BufferedWriter(new FileWriter(filename, false));
			}
			for(int i=0; i<result.size(); i++)
			{
				writer.write(result.get(i));
				writer.flush();
			}
			writer.close();
			return;
		}		
		
		private void determineTrainingTestSet()
		{
			int numUsers = allUserDocuments.keySet().size();
			int numTestUsers = (int) Math.floor(numUsers * TEST_SET_PERCENT);
			int numTrainUsers = numUsers - numTestUsers;
			int currTestUsers = 0;
			int currTrainUsers = 0;
			testSetUsers = new ArrayList<String>(); //list of users in test set
			trainSetUsers = new ArrayList<String>(); //list of users in training set
			List<String> currFollowers; //list of followers for the current followee
			Map<String,List<String>> tempFolloweeFollowers = new LinkedHashMap<String,List<String>>();
			tempFolloweeFollowers.putAll(followeeFollowers);
			
			//loop through each followee for 1 follower at a time until numTestUsers is reached
			while (currTestUsers < numTestUsers)
			{
				for (String followeeName: tempFolloweeFollowers.keySet())
				{
					currFollowers = tempFolloweeFollowers.get(followeeName);
					// if a followee set runs out of followers before another
					if (currFollowers.size() > 0)
					{
						testSetUsers.add(currFollowers.remove(0));
						currTestUsers++;
					}
					
					tempFolloweeFollowers.put(followeeName,currFollowers);
					
					if (currTestUsers == numTestUsers)
						break;
				}
			}
			
			while (currTrainUsers < numTrainUsers)
			{
				for (String followeeName: tempFolloweeFollowers.keySet())
				{
					currFollowers = tempFolloweeFollowers.get(followeeName);
					//if a followee set runs out of followers before another
					if (currFollowers.size() > 0)
					{
						trainSetUsers.add(currFollowers.remove(0));
						currTrainUsers++;
					}
					
					tempFolloweeFollowers.put(followeeName,currFollowers);
					
					if (currTrainUsers == numTrainUsers)
						break;
				}
			}
			// followeeFollowerCounts
			//followeeFollowers;
			
		}
		
		private StringJoiner vectorArffFormat(Map<String,Double> currDocTfidf, TreeSet<String> uniqueDocTerms)
		{
			StringJoiner tfidfJoinerTemp = new StringJoiner(",");
			double currTfidf;
			
			// int uniqueWordCount = 0;
			for (String uniqueWord: uniqueDocTerms)
			{
				// uniqueWordCount++;
				// System.out.println(getLocalName()+" uniqueWordCount: "+uniqueWordCount);
				if (currDocTfidf.keySet().contains(uniqueWord))
					currTfidf = currDocTfidf.get(uniqueWord);
				else
					currTfidf = 0.0;
				
				tfidfJoinerTemp.add(String.valueOf(currTfidf));
			}
			return tfidfJoinerTemp;
		}
		
	}

	
	
	
	//Source from: http://stackoverflow.com/questions/2808535/round-a-double-to-2-decimal-places
	public static double round(double value, int places) {
		if (places < 0) throw new IllegalArgumentException();

		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}
	
	///Source from: https://stackoverflow.com/questions/11296490/assigning-hashmap-to-hashmap
	// public static <K,J,V> Map<K, Map<J, V>> deepCopyLHM(Map<K, Map<J, V>> original)
	// {
		// Map<K, Map<J, V>> copy;

		// iterate over the map copying values into new map
		// for(Map.Entry<K, Map<J, V>> entry : original.entrySet())
		// {
		   // copy.put(entry.getKey(), new HashMap<J, V>(entry.getValue()));
		// }

		// return copy;
	// }
	
	public static long convertMs(long nanoTimeDiff)
	{
		return nanoTimeDiff/1000000;
	}
	
	public static BufferedReader readDataFile(String filename) {
		BufferedReader inputReader = null;

		try {
			inputReader = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException ex) {
			System.err.println("File not found: " + filename);
		}

		return inputReader;
	}

	public static Evaluation classify(Classifier model,
			Instances trainingSet, Instances testingSet) throws Exception {
		Evaluation evaluation = new Evaluation(trainingSet);

		model.buildClassifier(trainingSet);
		evaluation.evaluateModel(model, testingSet);

		return evaluation;
	}

	public static double calculateAccuracy(FastVector predictions) {
		double correct = 0;

		for (int i = 0; i < predictions.size(); i++) {
			NominalPrediction np = (NominalPrediction) predictions.elementAt(i);
			if (np.predicted() == np.actual()) {
				correct++;
			}
		}

		return 100 * correct / predictions.size();
	}

	protected void takeDown() 
	{
		try {
			DFService.deregister(this);
			System.out.println(getLocalName()+" DEREGISTERED WITH THE DF");
			//doDelete();
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}

}