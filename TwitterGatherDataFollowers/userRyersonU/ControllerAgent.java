package TwitterGatherDataFollowers.userRyersonU;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.lang.*;   //added by Sepide
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.math3.random.RandomDataGenerator;

import java.io.*;

import jade.lang.acl.*;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import jade.content.*;
import jade.content.onto.basic.*;
import jade.content.lang.*;
import jade.content.lang.sl.*;
import jade.core.*;
import jade.core.Runtime;
import jade.core.behaviours.*;
import jade.domain.*;
import jade.domain.mobility.*;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.JADEAgentManagement.*;
import jade.gui.*;
import jade.domain.FIPAAgentManagement.*;
import jade.util.leap.Properties;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.functions.SMO;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.FastVector;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.Remove;

public class ControllerAgent extends GuiAgent {
	//public class ControllerAgent extends Agent {

	private static final long serialVersionUID = 1L;
	private AgentContainer workContainer;
	private AgentContainer userSimContainer;

	private AID[] allRecAgents;
	private long tweetDelay;

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
	
	
	private int totalUsers = 0;
	private ArrayList<String> usersRec = new ArrayList<String>(); //Users looking for recommendation
	private ArrayList<String> listOfUsers = new ArrayList<String>(); //List of users from dataset
	private ArrayList<String> listOfAgents = new ArrayList<String>(); //List of all agents created
	private ArrayList<AgentController> listOfAgentControllers = new ArrayList<AgentController>(); //List of all agents AID created
	private ArrayList<Date> allUniqueDateTimes = new ArrayList<Date>(); 
	
	private static String serverName = "127.0.0.1";
	private static String portNumber = "3306";
	private static String sid = "testmysql";	 

	private Connection con;
	private Statement stmt = null;
	private ResultSet resultSet = null;
	private static String user = "root";
	private static String pass = "Asdf1234";

	public static final int QUIT = 0;
	public static final int START_SIM = 1;
	public static final int INITIALIZE = 2;
	public static final int GET_USERS = 3;
	public static final int COLLECT_DATA =  4;
	public static final int START_USER_GEN_SIM =  5;
	public static final int FROM_ARTIFICIAL = 3;
	public static final int FROM_GENERATION = 2;
	public static final int FROM_DB = 1;
	public static final int FROM_TEXT = 0;
	public static final int COS_SIM = 0;
	public static final int K_MEANS = 1;
	public static final int SVM = 2;
	public static final int MLP = 3;
	public static final int Doc2Vec = 4;
	public static final int CommonNeighbors = 5;        // added by Sepide
	public static final int K_MEANSEUCLIDEAN = 6;      // added by Sepide 
	private static final int HASH_TAGS = 1;
	private static final int RE_TWEETS = 1;
	private static final int STOP_WORDS = 1;
	public static final int MAX_WORDS = 7; //Maximum number of words in tweet after processing
	public static final int MIN_WORDS = 3; //Minimum number of words in tweet after processing
	private static final int TOTAL_ORIGINAL_WORDS = 1148; //Total number of words from original corpus
	private static final int TOTAL_ORIGINAL_USERS = 10; //Total number of users from original corpus
	private static final long TWEET_DELAY = 10L; //default 10ms
	private static final String REFERENCE_USER = "RyersonU";
	private static final double DEFAULT_SHAPE = 0.435008; //Average of shape parameters from users for tweet counts from RyersonU
	private static final double DEFAULT_SCALE = 304.842738; //Average of scale parameters from users for tweet counts from RyersonU
	private static final String BEGIN_DATE = "2007-01-01";
	private static final String END_DATE = "2017-01-01";
	private static final double DEFAULT_SHAPE2 = 0.48694;
	private static final double DEFAULT_SCALE2 = 2.09345E-6;
	private static final int TOTAL_ORIGINAL_WORDS_DEMO = 30506;
	private static final double MIN_TFIDF = 0;
	private static final double TEST_SET_PERCENT = 0.30;
	private static final double TRAIN_SET_PERCENT = 0.70;
	
	private AID agentNameAID;
	private String agentName;

	private int algorithmRec;
	private int readFrom;
	private int totalTweetLimit;
	private int numFollowers;
	private int numFollowees;
	private int numTweetsGenerated;
	private int numRecAgents; //numRecAgents #RecommenderAgents numNodes

	private String referenceUser;
	private String beginDate;
	private String endDate;	

	private Runtime runTime;

	transient protected ControllerAgentGui myGui;

	AgentController agentController = null;

	private boolean initialized;
	private boolean firstRun;

	private InMemoryDb localDb;
	private InMemoryDb availableDb;

	private LinkedHashMap<String,LinkedHashMap<String,Integer>> allUserFollowTweetCounts = new LinkedHashMap<String,LinkedHashMap<String,Integer>>(); //LHM of users and how many tweets before each follower follows
	
	public File textFile; //text file for performance measurement  // private access changed to public by Sepide
	private File corpusGenFile; //text file for user gen simulation
	
	private double[][] topicsParameters = new double[TOTAL_ORIGINAL_USERS][2];
	private double[][] topicsWordTfidf = new double[TOTAL_ORIGINAL_USERS][TOTAL_ORIGINAL_WORDS];
	private String[][] topicsWords = new String[TOTAL_ORIGINAL_USERS][TOTAL_ORIGINAL_WORDS];
	private double[][] topicsMinMax = new double[TOTAL_ORIGINAL_USERS][2];
	private	double[][] topicsWordsIndex = new double[TOTAL_ORIGINAL_USERS][TOTAL_ORIGINAL_WORDS];
	private ArrayList<String> currentBagWords = new ArrayList<String>(); //words already used in random generation
	private static RandomDataGenerator randomDataGenerator = new RandomDataGenerator(); //should not construct in method, make it static
	private static Random r = new Random(); //should not construct in method, make it static
	
	private double[] topicsWordsTfidfDemo = new double[TOTAL_ORIGINAL_WORDS_DEMO];
	private String[] topicsWordsDemo = new String[TOTAL_ORIGINAL_WORDS_DEMO];
	private double[] topicsWordsIndexDemo = new double[TOTAL_ORIGINAL_WORDS_DEMO];
	
	private String generatingParameterFolderName = "GeneratingParameterFiles/";
	
	private double averageUserShape;
	private double averageUserScale;
	private String pathToUserParameters = generatingParameterFolderName + "user_parameters_CombinedMatrix_avg_SMALL.txt"; //gen3
	private String[] pathsToTfidfMatrix = {"RyersonU_tfidf_matrix_SMALL.txt","TheCatTweeting_tfidf_matrix_SMALL.txt","TorontoStar_tfidf_matrix_SMALL.txt"};
	private String pathToTfidfMatrix = "RyersonU_tfidf_matrix_SMALL.txt"; //default, will change based on pathsToTfidfMatrix for loop in initialization
	
	String[] corpusfileNames = {"RyersonU_SMALL_cleaned.txt","TheCatTweeting_SMALL_cleaned.txt","TorontoStar_SMALL_cleaned.txt"};
	
	private LinkedHashMap<String,String> followerFollowee = new LinkedHashMap<String,String>();
	private LinkedHashMap<String,ArrayList<String>> allWordsInSet = new LinkedHashMap<String,ArrayList<String>>();
	
	private LinkedHashMap<String,Double> usersShape = new LinkedHashMap<String,Double>(); //shape parameter for user
	private LinkedHashMap<String,Double> usersScale = new LinkedHashMap<String,Double>(); //scale parameter for user
	private LinkedHashMap<String,Double> userTfidfVector; //each user tfidf vector
	private LinkedHashMap<String,LinkedHashMap<String,Double>> allUserTfidfVectors = new LinkedHashMap<String,LinkedHashMap<String,Double>>(); //all the users tfidf vectors
	private LinkedHashMap<String,Integer> userTweetCounts = new LinkedHashMap<String,Integer>(); //each user's tweet counts
	
	private LinkedHashMap<String,LinkedHashMap<Double,ArrayList<String>>> allUserTfidfWordsBins = new LinkedHashMap<String,LinkedHashMap<Double,ArrayList<String>>>(); //All bins for each user
	
	private HashMap<String,String> userFollowee;
	
	private int tweetsReadCount = 0;
	
	private int numArtificialTweets = 0;
	
	private Map<String,String> userFolloweeMap = new LinkedHashMap<String,String>(); //list of users and their followeeNames before processing (may have extra since processing can remove users) From reading text file 
	private Map<String,Integer> followeeFollowerCounts = new LinkedHashMap<String,Integer>(); //number of followers for each followee/class
	private Map<String,List<String>> followeeFollowers = new LinkedHashMap<String,List<String>>(); //list of followees and their followers
	private List<String> testSetUsers; //list of users in test set
	private List<String> trainSetUsers; //list of users in training set
	
	private TreeSet<String> allUniqueDocTerms = new TreeSet<String>();
	private LinkedHashMap<Long,String> tweetIdUser = new LinkedHashMap<Long,String>();
	private LinkedHashMap<Long,String> tweetIdText = new LinkedHashMap<Long,String>();
	private LinkedHashMap<Long,LinkedHashMap<String,Double>> tweetIdDocumentVector = new LinkedHashMap<Long,LinkedHashMap<String,Double>>();
	
	private LinkedHashMap<String,Integer> allTermsDocumentFreq = new LinkedHashMap<String,Integer>();
	private LinkedHashMap<String,LinkedHashMap<String,Double>> allUserDocuments = new LinkedHashMap<String,LinkedHashMap<String,Double>>();
	private LinkedHashMap<String,Double> aggregatedUserTweets = new LinkedHashMap<String,Double>();
	private int totalDocuments = 0;
	private Map<String,LinkedHashMap<String,Double>> allUserDocumentsTFIDF;
	private List<String> datasetFollowees; //followees in the whole dataset for MLP distributed
	
	protected void setup() {

		//Get access to current jade runtime system
		runTime = jade.core.Runtime.instance();

		getContentManager().registerLanguage(new SLCodec(),FIPANames.ContentLanguage.FIPA_SL0);
		getContentManager().registerOntology(MobilityOntology.getInstance());

		setUpWorkContainer();
		setUpUserSimContainer();

		myGui = new ControllerAgentGui(this);
		myGui.setVisible(true);

		agentName = getLocalName();
		agentNameAID = getAID();

		initialized = false;
		firstRun = true;
		readFrom = FROM_TEXT;
		textFile = null;
		corpusGenFile = null;
		//readFrom = FROM_DB;
		localDb = new InMemoryDb();
		availableDb = new InMemoryDb();
		referenceUser = REFERENCE_USER;
		numRecAgents = 1;
		
		try {
//			readInGeneratingParameters();
			readInGeneratingParameters2();
			readInGeneratingParameters4();
			for (int i = 0; i < pathsToTfidfMatrix.length; i++)
			{
				pathToTfidfMatrix = generatingParameterFolderName+pathsToTfidfMatrix[i];
				readInTfidfMatrix();
			}
			
			for (String follower: followerFollowee.keySet())
			{
				System.out.println("follower: "+follower+"\tfollowee: "+followerFollowee.get(follower));
			}
			
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
//		setGeneratedWordBins();
		setGeneratedWordBins2();
		
		System.out.println("ControllerAGENT MADE agentName:" + agentName + " agentNameAID: "+agentNameAID);


		try {
			DFAgentDescription dfd = new DFAgentDescription();
			dfd.setName(getAID());
			ServiceDescription sd = new ServiceDescription();
			sd.setName("Distributed Recommender System");
			sd.setType("Controller Agent");
			dfd.addServices(sd);
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			e.printStackTrace();
		}

		setQueueSize(0);
		

		
		
	}


	protected void onGuiEvent(GuiEvent ev) {
		int actionToPerform;
		actionToPerform = ev.getType();
		System.out.println("actionToPerform: "+actionToPerform);
		if (actionToPerform == QUIT)
		{
			System.exit(0);
		}
		else if (actionToPerform == GET_USERS)
		{
			loadUsers(ev);
		}
		else if (actionToPerform == INITIALIZE)
		{
			if (initialized)
			{
				killContainer();
				setUpWorkContainer();
			}
			initializeSetup(ev);
		}
		else if (actionToPerform == START_SIM)
		{
			startSimulation();
		}
		else if (actionToPerform == COLLECT_DATA)
		{
			collectData(ev);
		}
		else if (actionToPerform == START_USER_GEN_SIM)
		{
			startUserGenSimulation();
		}
	}

	private void setUpWorkContainer()
	{
		System.out.println("Set up work container");
		try {
			// Properties containerProperties = new Properties();
			// containerProperties.put("jade_core_messaging_MessageManager_poolsize", "20");
			// containerProperties.put("jade_core_messaging_MessageManager_maxqueuesize", "2000000000");
			// containerProperties.put("jade_core_messaging_MessageManager_deliverytimethreshold", "10000");
			// Profile mainProfile = new ProfileImpl(containerProperties);
		
			Profile mainProfile = new ProfileImpl();
			mainProfile.setParameter(Profile.CONTAINER_NAME, "workContainer");
			mainProfile.setParameter("jade_core_messaging_MessageManager_poolsize", "20");
			mainProfile.setParameter("jade_core_messaging_MessageManager_maxqueuesize", "2000000000");
			mainProfile.setParameter("jade_core_messaging_MessageManager_deliverytimethreshold", "10000");
			workContainer = runTime.createAgentContainer(mainProfile);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void setUpUserSimContainer()
	{
		System.out.println("Set up user sim container");
		try {
			Profile userSimProfile = new ProfileImpl();
			userSimProfile.setParameter(Profile.CONTAINER_NAME, "userSimContainer");
			userSimContainer = runTime.createAgentContainer(userSimProfile);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void collectData(GuiEvent ev)
	{
		String followeesToGet = (String) ev.getParameter(0);
		int followersToGet = (Integer) ev.getParameter(1);
		//S int followerNameToGet = (String) ev.getParameter(1);  // added by Sepide
		
		String[] followeesToGetArray = followeesToGet.split("\\s*,\\s*");
		HashMap<String,Integer> followeeNumFollowersToGet = new LinkedHashMap<String,Integer>();
		
		for (int i = 0; i < followeesToGetArray.length; i++)
		{
			followeeNumFollowersToGet.put(followeesToGetArray[i], followersToGet);
		}
		
		System.out.println("Collect Data Time");
		System.out.println("followeesToGet: "+followeesToGet);
		System.out.println("followersToGet: "+followersToGet);
		for (String followeeName : followeeNumFollowersToGet.keySet())
		{
				System.out.println("LinkedHashMap followeeName: "+ followeeName + " followerNums: "+ followeeNumFollowersToGet.get(followeeName));
		}
		
		Object[] dataCrawlerAgentArgs = new Object[2];
		dataCrawlerAgentArgs[0] = followeeNumFollowersToGet;
		dataCrawlerAgentArgs[1] = myGui;								

		String dataCrawlerAgentName = "Data Crawler Agent";
		
		try{
			agentController = userSimContainer.createNewAgent(dataCrawlerAgentName, DataCrawlerAgent.class.getName(), dataCrawlerAgentArgs);
			agentController.start();
		}
		catch (StaleProxyException e)
		{
			e.printStackTrace();
		}
		
		
		System.out.println("Made agent: "+dataCrawlerAgentName);
		
		myGui.disableUserGenSimButtons();
				
	}
	
	public void startUserGenSimulation()
	{
		if (corpusGenFile != null)
		{
			Object[] userGenSimAgentArgs = new Object[3];
			userGenSimAgentArgs[0] = numArtificialTweets;
			userGenSimAgentArgs[1] = corpusGenFile;
			userGenSimAgentArgs[2] = myGui;	
			
			String userGenSimAgentName = "User Generation Simulation Agent";
			
			try{
				agentController = userSimContainer.createNewAgent(userGenSimAgentName, UserGenSimAgent.class.getName(), userGenSimAgentArgs);
				agentController.start();
			}
			catch (StaleProxyException e)
			{
				e.printStackTrace();
			}
			
			
			System.out.println("Made agent: "+userGenSimAgentName);
			
			myGui.disableUserGenSimButtons();
		}
		else
		{
			myGui.noFileSelected();
		}
		
	}
	
	public void loadUsers(GuiEvent ev)
	{
		boolean readyToInitialize = true;
		
		localDb.clearDb();
		availableDb.clearDb();

//		referenceUser = (String) ev.getParameter(0);
		totalTweetLimit = (Integer) ev.getParameter(1); //DO NOT SET < 300	
		beginDate = (String) ev.getParameter(2);
		endDate = (String) ev.getParameter(3);
		numFollowees = (Integer) ev.getParameter(4);
		numFollowers = (Integer) ev.getParameter(5);
		numTweetsGenerated = (Integer) ev.getParameter(6);
		
		myGui.disableList();

		long startTime = System.currentTimeMillis();

		if (readFrom == FROM_ARTIFICIAL)
		{
			tweetsReadCount = 0;
			for (String corpusName : corpusfileNames)
			{
				textFile = new File(generatingParameterFolderName+corpusName);
				readFromTextFile();
			}
			ArrayList<Tweet> availableTweets;
			availableTweets = getAvailableTweets();
						
			availableDb.setTweets(availableTweets);
			
			// for (Tweet currTweet : availableDb.getTweetsFromUser("TetraRyerson"))
			// {
				// System.out.println("ARTIFICIAL TetraRyerson tweet: "+currTweet.getTweetText());
			// }
			// System.out.println("ARTIFICIAL TetraRyerson: "+availableDb.getTweetCountFromUser("TetraRyerson"));
			// System.out.println("ARTIFICIAL tweetsReadCount: "+tweetsReadCount);
			System.out.println("ARTIFICIAL TOTAL TWEETS READ: "+availableDb.getTotalTweets());
		}
		
		if (readFrom == FROM_GENERATION)
		{
			generateUserNames();
			
			int tweetCount = 0;
			int totalGeneratedUsers = numFollowees + numFollowers;
			int tweetsForEachUser = numTweetsGenerated / totalGeneratedUsers;
			int generatedUserIndex = 0;
			
			int[] tweetCountsForUsers = new int[totalGeneratedUsers]; //followees first then followers
			
			//Assign number of tweets to each generated user's bin
			while (tweetCount < numTweetsGenerated)
			{
				tweetCount+=tweetsForEachUser;
				
				if (generatedUserIndex == totalGeneratedUsers-1)
				{
					if (tweetCount > numTweetsGenerated)
					{
//						System.out.println("tweetsForEachUser: "+tweetsForEachUser);
						tweetsForEachUser-=(tweetCount-numTweetsGenerated); //Lower number of tweets to maintain the number of tweets that should be generated
//						System.out.println("tweetsForEachUser: "+tweetsForEachUser);
					}
					else if (tweetCount < numTweetsGenerated)
					{
//						System.out.println("tweetsForEachUser: "+tweetsForEachUser);
						tweetsForEachUser+=(numTweetsGenerated-tweetCount); //Increase number of tweets to maintain the number of tweets that should be generated
//						System.out.println("tweetsForEachUser: "+tweetsForEachUser);
					}
					
					tweetCount = numTweetsGenerated; //Update tweetCount to numTweetsGenerated after adjusting the tweet bin for the last user
				}
				
				tweetCountsForUsers[generatedUserIndex] = tweetsForEachUser;
				generatedUserIndex++;
			}
			
			for (int i = 0; i < tweetCountsForUsers.length; i++)
			{
				System.out.print(tweetCountsForUsers[i]+" ");
			}
			
			System.out.println("WILL GENERATE DATA");
			
			int generatedTweetId = 1;
			String generatedDate = BEGIN_DATE;
			String generatedTweetText = "";
			String generatedUserName = "";
			
			//Generate the tweets to be stored InMemoryDb for each user
			for (int i = 0; i < tweetCountsForUsers.length; i++)
			{
				generatedUserName = "User"+(i+1);
				if (i < numFollowees)
				{
					generatedUserName += "_Followee";
				}
				else
				{
					generatedUserName += "_Follower";
				}
				
				for (int j = 0; j < tweetCountsForUsers[i]; j++)
				{
					//Generate stochastic tweet
			
//					generatedTweetText = generateTweetText();
					generatedTweetText = generateTweetText2();

					Tweet currentGeneratedTweet = new Tweet(generatedTweetText,generatedTweetId,generatedDate,generatedUserName);
					
					localDb.addTweet(currentGeneratedTweet);
					
					generatedTweetId++;
					
					//Generate a fake date starting from BEGIN_DATE
					SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );   
					Calendar cal = Calendar.getInstance();    
					try {
						cal.setTime( dateFormat.parse(generatedDate));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}    
					cal.add( Calendar.DATE, 1 ); 
					
					generatedDate=dateFormat.format(cal.getTime());
					
					//Check if the fake date is still in range, if not set it back to the default BEGIN_DATE;
					boolean dateRangeValid = true;
					dateRangeValid = checkDateRange(generatedDate,beginDate,endDate);
					
					if (!dateRangeValid)
					{
						generatedDate = BEGIN_DATE;
					}
				}
			}
			
			referenceUser = listOfUsers.get(0); //Assumes first user is the followee/referenceUser
			
			System.out.println("Done Generating Tweets");
			System.out.println(getLocalName()+" total tweets in local db: "+localDb.getTotalTweets());
			
		}
		if (readFrom == FROM_TEXT)
		{

			if (textFile != null)
			{
				readFromTextFile();

				ArrayList<Tweet> availableTweets;
				availableTweets = getAvailableTweets();
							
				availableDb.setTweets(availableTweets);

				//			for (Tweet t : datasetAvailable)
				//			{
				//				try {
				//					FileWriter writer = new FileWriter("demoTweetsVerification1000.txt", true); //append	
				//					BufferedWriter bufferedWriter = new BufferedWriter(writer);
				//					bufferedWriter.write("RyersonU\t"+t.getTweetId()+"\t"+t.getDateString()+"\t123456\t"+t.getUser()+"\t"+t.getTweetText());					
				//					bufferedWriter.newLine();
				//					bufferedWriter.close();
				//				} catch (IOException e) {
				//					e.printStackTrace();
				//				}
				//			
				//			}
			}
			else
			{
				readyToInitialize = false;
			}
		}

		if (readFrom == FROM_DB)
		{				 
			listOfUsers.clear();

			try {

				String url = "jdbc:mysql://" + serverName + ":" + portNumber + "/" + sid + "?useSSL=false";
				con = DriverManager.getConnection(url, user, pass);

				String query;

				if (totalTweetLimit > 0)
					query = "select screen_name,referenceUser,count(*) from (select * from usertweet where referenceUser='"+referenceUser+"' AND CAST(created_at AS DATE) BETWEEN '" + beginDate + "' AND '" + endDate + "' order by tweetid DESC LIMIT "+ totalTweetLimit +") as T1 group by screen_name order by count(*) DESC";				  
				else
					query = "select screen_name,referenceUser,count(*) from (select * from usertweet where referenceUser='"+referenceUser+"' AND CAST(created_at AS DATE) BETWEEN '" + beginDate + "' AND '" + endDate + "') as T1 group by screen_name order by count(*) DESC";

				//String queryst = "select * from ryersontop41users";

				stmt = con.createStatement();
				resultSet = stmt.executeQuery(query);

				String currentTwitterUser;

				resultSet.beforeFirst();
				while(!resultSet.isLast())
				{
					resultSet.next();
					currentTwitterUser = resultSet.getString(1);
					listOfUsers.add(currentTwitterUser);
				}

				resultSet.close();
				stmt.close();
				con.close();

			} catch (SQLException e1) {
				e1.printStackTrace();
			}

			//myGui.updateList(listOfUsers);

		}

		if (readyToInitialize)
		{
			myGui.updateList(listOfUsers);
			myGui.enableAllButtons();
			myGui.enableList();
			myGui.changeUserListTitle(readFrom);
			long endTime = System.currentTimeMillis();
			System.out.println("Load time: "+(endTime-startTime)+"ms");
			System.out.println("READY TO INITIALIZE MULTI-AGENT SYSTEM");
			myGui.showMessageBox("get users");
		}
		else
		{
			myGui.noFileSelected();
			myGui.enableAllButtons();
		}
		
	} //loadUsers

	public void startSimulation()
	{
		try{
			Object[] starterAgentArgs = new Object[13];							
			starterAgentArgs[0] = getAID();										
			starterAgentArgs[1] = referenceUser;		//referenceUser			NOT USED IN STARTER AGENT ANYMORE			
			starterAgentArgs[2] = "1"; //1
			starterAgentArgs[5] = "0"; //0
			starterAgentArgs[6] = totalUsers; //1

			starterAgentArgs[8] = "1"; //1
			starterAgentArgs[9] = 1;	//1	
			//starterAgentArgs[10] = usersRec;
			starterAgentArgs[11] = myGui;
			starterAgentArgs[12] = numRecAgents;

			String starterAgentName = "Starter Agent";
			agentController = workContainer.createNewAgent(starterAgentName, StarterAgent.class.getName(), starterAgentArgs);
			listOfAgentControllers.add(agentController);
			agentController.start();
			listOfAgents.add(starterAgentName);
			System.out.println("Made agent: "+starterAgentName);
			System.out.println(getLocalName()+" getAID(): "+getAID());
			
			if (textFile != null)
				System.out.println("Reading from textFile: "+ textFile.getName());

		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}

		myGui.disableStartButton();
		myGui.changeRecommendUserTitle();
		
		firstRun = false;
		
	}

	public void initializeSetup(GuiEvent ev)
	{
		String currentTwitterUser = "RyersonU";
		/*referenceUser = "Ryerson";
		String beginDate = "2007-04-04";
		String endDate = "2015-05-24";	
		int kRecommend = 3;
		int hashtags_temp = 1; //1 to remove hash tags	
		int retweetedby_temp = 1; //1 if it is retweeted	
		int stopWordFlag_temp = 0; //remove stop words	

		int readFrom = 1;	//1 to read FROM_DB	
		//Modify for servers @Jason	
		int numOrgAgents = 1;	//number of recommender servers (RecommenderServiceAgents) # Organizing Agents
		int numRecAgents = 4; //numRecAgentss OrganizingAgent, numRecAgentss RecommenderServiceAgent 	 #RecommenderAgents
		//@Jason change limit of overall tweets  
		//  int totalTweetLimit = 7446;
		int totalTweetLimit = 300; //DO NOT SET < 300
		//  int totalTweetLimit = 2000;


		//algorithmRec = COS_SIM;
		  algorithmRec=K_MEANS;
		 */

		/*
		ge.addParameter(numServers);
		ge.addParameter(tweetLimit);
		ge.addParameter(beginDate);
		ge.addParameter(endDate);
		ge.addParameter(kRecommend);
		ge.addParameter(referenceUser);
		ge.addParameter(hashTags);
		ge.addParameter(retweets);
		ge.addParameter(stopWords);
		ge.addParameter(algorithmRec);
		 */
		//readFrom = FROM_DB;	//1 to read FROM_DB	

//		referenceUser = (String) ev.getParameter(5);
		String beginDate = (String) ev.getParameter(2);
		String endDate = (String) ev.getParameter(3);	
		int kRecommend = (Integer) ev.getParameter(4);
		int hashtags_temp = (Integer) ev.getParameter(6); //1 to remove hash tags	
		int retweetedby_temp = (Integer) ev.getParameter(7); //1 if it is retweeted	
		int stopWordFlag_temp = (Integer) ev.getParameter(8); //remove stop words			
		//Modify for servers @Jason	
		int numOrgAgents = 1;	//number of recommender servers (RecommenderServiceAgents) # Organizing Agents
		numRecAgents = (Integer) ev.getParameter(0); //numRecAgentss OrganizingAgent, numRecAgentss RecommenderServiceAgent 	 #RecommenderAgents
		//@Jason change limit of overall tweets  
		totalTweetLimit = (Integer) ev.getParameter(1); //DO NOT SET < 300
		System.out.println(getLocalName()+" totalTweetLimits initialize: "+totalTweetLimit);
		
		if (totalTweetLimit == 0)
			totalTweetLimit = localDb.getTotalTweets();

		algorithmRec=(Integer) ev.getParameter(9);
		numFollowees = (Integer) ev.getParameter(10);
		numFollowers = (Integer) ev.getParameter(11);
		numTweetsGenerated = (Integer) ev.getParameter(12);
		
		
		tweetDelay = TWEET_DELAY;

		initialized = true;
		usersRec = myGui.getUsersRec();

		myGui.disableList();
		
		String directoryNameTiming = "Results/Timing/" + referenceUser;
		String directoryNameRecommendation = "Results/Recommendations/" + referenceUser;
						
		createDir(directoryNameTiming);
		createDir(directoryNameRecommendation);
		
		long startTime = System.currentTimeMillis();
		//@Jason
		
		if (readFrom == FROM_ARTIFICIAL)
		{
			localDb.clearDb();
			availableDb.clearDb();
			allUserFollowTweetCounts.clear();
			
			for (String corpusName : corpusfileNames)
			{
				textFile = new File(generatingParameterFolderName+corpusName);
				readFromTextFile();
			}

			ArrayList<Tweet> availableTweets;
			availableTweets = getAvailableTweets();
						
			availableDb.setTweets(availableTweets);		

			setGeneratedWordBins4();
			
			System.out.println("controller availableDb.getTweetsSize(): "+ availableDb.getTotalTweets());
			
			
			//Making agents

			try{

				//@Jason added algorithmRec arg
				//Object[] orgAgentArgs = new Object[16];
				Object[] orgAgentArgs = new Object[19];
				orgAgentArgs[0] = getAID();										
				orgAgentArgs[1] = referenceUser;	//NOT USED IN ORGANIZING AGENT ANYMORE								

				orgAgentArgs[3] = beginDate;
				orgAgentArgs[4] = endDate;
				orgAgentArgs[5] = hashtags_temp;

				orgAgentArgs[7] = retweetedby_temp;
				orgAgentArgs[8] = stopWordFlag_temp;



				orgAgentArgs[11] = 1;
				orgAgentArgs[12] = 1;
				orgAgentArgs[13] = listOfUsers.size();
				orgAgentArgs[14] = 1;		
				orgAgentArgs[15] = numRecAgents;

				//@Jason added algorithmRec argument
				orgAgentArgs[16] = algorithmRec;

				orgAgentArgs[18] = myGui;

				for(int i=1; i<numOrgAgents+1; i++)
				{
					String orgAgentName = "Organizing Agent" + i;
					agentController = workContainer.createNewAgent(orgAgentName, OrganizingAgent.class.getName(), orgAgentArgs);
					agentController.start();
					listOfAgents.add(orgAgentName);
					listOfAgentControllers.add(agentController);
					System.out.println("Made agent: "+orgAgentName);

				}


				
				int[] bins = new int[numRecAgents]; //bins for recommender agents

				int currentTweetCount;
				int totalRecBin = 0; //Number of tweets in a recommender agent's bin
				int smallestBinIndex;

				totalUsers = 0;
				
				for (String user : listOfUsers)
				{
					totalUsers++;

					currentTweetCount = availableDb.getTweetCountFromUser(user);
				
					smallestBinIndex = findSmallestBin(bins);
//					totalRecBin = bins.get(smallestBinIndex);
					totalRecBin = bins[smallestBinIndex];
					totalRecBin += currentTweetCount;
//					bins.set(smallestBinIndex, totalRecBin);
					bins[smallestBinIndex] = totalRecBin;
					smallestBinIndex++; //add 1 to index to make index start from 1 instead of 0

					//Add list of recommender servers and increase bins accordingly if distributed
					//ie. add the tweet count to all recommenders if there are 2 for the user looking for recommendation
					ArrayList<Integer> recServers = new ArrayList<Integer>();
					if (usersRec.contains(user))
					{	
						for (int i = 0; i < numRecAgents; i++)
						{
							if (smallestBinIndex-1 != i)
							{
//								totalRecBin = bins.get(i);
								totalRecBin = bins[i];
								totalRecBin += currentTweetCount;
//								bins.set(i,totalRecBin);
								bins[i] = totalRecBin;
							}
							recServers.add(i+1);
						}
					}
					else
					{
						recServers.add(smallestBinIndex);
					}
					


					long userTweetDelay = calculateTweetDelay(user);

					
//					userFollowTweetCounts = new LinkedHashMap<String,Integer>();
					LinkedHashMap<String,Integer> userFollowTweetCounts = new LinkedHashMap<String,Integer>();
					
					for (String otherUser : listOfUsers)
					{
						if (!otherUser.equals(user))
						{
							if (user.equals(referenceUser))
							{
								int tweetCountToFollow;
								tweetCountToFollow = calculateTweetsToFollow(availableDb.getTweetCountFromUser(otherUser));
								userFollowTweetCounts.put(otherUser, tweetCountToFollow);
							}
							
						}
					}

					allUserFollowTweetCounts.put(user, userFollowTweetCounts);
//					System.out.println("user: "+user+" userFollowTweetCounts: " + userFollowTweetCounts);
					
					//Object[] userAgentArgs = new Object[15];							
					Object[] userAgentArgs = new Object[30];
					userAgentArgs[0] = getAID();										
					userAgentArgs[1] = referenceUser;							

					userAgentArgs[3] = beginDate;
					userAgentArgs[4] = endDate;
					userAgentArgs[5] = hashtags_temp;

					userAgentArgs[7] = retweetedby_temp;
					userAgentArgs[8] = stopWordFlag_temp;

					userAgentArgs[10] = recServers;

					userAgentArgs[11] = smallestBinIndex;				

					userAgentArgs[12] = 1;
					userAgentArgs[13] = 1;	
					//					userAgentArgs[14] = tweetDelay;
					userAgentArgs[14] = userTweetDelay;

					//@Jason added limit and referenceUser;
					userAgentArgs[15] = totalTweetLimit;
					userAgentArgs[16] = referenceUser;
					userAgentArgs[17] = kRecommend;
					userAgentArgs[18] = algorithmRec;
					userAgentArgs[19] = myGui;
					userAgentArgs[20] = readFrom;
					userAgentArgs[21] = availableDb.getTweetsFromUser(user);
					userAgentArgs[22] = true;
					userAgentArgs[23] = 1;
					userAgentArgs[24] = referenceUser;
					
					if (user.equals(referenceUser))
					{
						userAgentArgs[25] = true;
					}
					else
					{	
						userAgentArgs[25] = false;
					}
					
					userAgentArgs[26] = averageUserShape;
					userAgentArgs[27] = averageUserScale;
					userAgentArgs[28] = allUserTfidfWordsBins.get(user); //LinkedHashMap<Double,ArrayList<String>>
					userAgentArgs[29] = userFollowee.get(user); //followee of User

					String userAgentName = user + "-UserAgent";
					agentController = workContainer.createNewAgent(userAgentName, MobileAgent.class.getName(), userAgentArgs);   
					agentController.start();
					listOfAgents.add(userAgentName);
					listOfAgentControllers.add(agentController);
					//System.out.println("Made agent: "+userAgentName);
//					System.out.println("totalUsers: "+totalUsers);

				}

				int totalnumberoftweets =0;
//				for(int k=0; k<bins.size();k++)
				for(int k=0; k<bins.length;k++)
				{
					//totalnumberoftweets += bins.get(k);
//					System.out.println("bin "+k+": "+bins.get(k));
					System.out.println("bin "+k+": "+bins[k]);
				}

				myGui.updateList(listOfUsers); //update before creating recommender agents

				int j =0;
				for(int i=1; i<numRecAgents+1; i++)
				{

					Object[] recAgentArgs = new Object[19];
					recAgentArgs[0] = getAID();										
					recAgentArgs[1] = referenceUser;								

					recAgentArgs[3] = beginDate;
					recAgentArgs[4] = endDate;
					recAgentArgs[5] = hashtags_temp;

					recAgentArgs[7] = retweetedby_temp;
					recAgentArgs[8] = stopWordFlag_temp;



					recAgentArgs[11] = 1;
					recAgentArgs[12] = 1;
//					recAgentArgs[13] = bins.get(j);
					recAgentArgs[13] = bins[j];

					recAgentArgs[15] = numRecAgents;

					//@Jason added argument for algorithm to use
					recAgentArgs[16] = algorithmRec;
					//recAgentArgs[17] = usersRec;
					recAgentArgs[18] = myGui;

					String recAgentName = "Recommender-ServiceAgent" + i;
					agentController = workContainer.createNewAgent(recAgentName, RecommenderAgent.class.getName(), recAgentArgs);
					agentController.start();
					listOfAgents.add(recAgentName);
					listOfAgentControllers.add(agentController);
					System.out.println("Made agent: "+recAgentName);
					j++;
				}

			}catch (Exception e){
				System.err.println("ARTIFICIAL Text Read Initialize Error: " + e.getMessage());
			}  

			System.out.println("ARTIFICIAL TOTALUSERS: "+totalUsers);
			
		}
		
		if (readFrom == FROM_GENERATION)
		{
			availableDb.clearDb();
			allUserFollowTweetCounts.clear();
			
			System.out.println(getLocalName()+" initialize total tweets localDb: "+localDb.getTotalTweets());
			
			ArrayList<Tweet> availableTweets;
			availableTweets = getAvailableTweets();
						
			availableDb.setTweets(availableTweets);	
			
			Collections.reverse(listOfUsers); //Change order of users back to original with followee at the top
					
			//Making agents

			try{

				//@Jason added algorithmRec arg
				//Object[] orgAgentArgs = new Object[16];
				Object[] orgAgentArgs = new Object[19];
				orgAgentArgs[0] = getAID();										
				orgAgentArgs[1] = referenceUser;			//Not used	in organizing agent anymore			

				orgAgentArgs[3] = beginDate;
				orgAgentArgs[4] = endDate;
				orgAgentArgs[5] = hashtags_temp;

				orgAgentArgs[7] = retweetedby_temp;
				orgAgentArgs[8] = stopWordFlag_temp;



				orgAgentArgs[11] = 1;
				orgAgentArgs[12] = 1;
				orgAgentArgs[13] = listOfUsers.size();
				orgAgentArgs[14] = 1;		
				orgAgentArgs[15] = numRecAgents;

				//@Jason added algorithmRec argument
				orgAgentArgs[16] = algorithmRec;

				orgAgentArgs[18] = myGui;

				for(int i=1; i<numOrgAgents+1; i++)
				{
					String orgAgentName = "Organizing Agent" + i;
					agentController = workContainer.createNewAgent(orgAgentName, OrganizingAgent.class.getName(), orgAgentArgs);
					agentController.start();
					listOfAgents.add(orgAgentName);
					listOfAgentControllers.add(agentController);
					System.out.println("Made agent: "+orgAgentName);

				}


				//Create bins for recommender agents
//				ArrayList<Integer> bins = new ArrayList<Integer>();
//				for(int i=0; i<numRecAgents; i++)
//				{
//					bins.add(0);
//				}
				
				int[] bins = new int[numRecAgents]; //bins for recommender agents

				int currentTweetCount;
				int totalRecBin = 0; //Number of tweets in a recommender agent's bin
				int smallestBinIndex;

				totalUsers = 0;
				
				for (String user : listOfUsers)
				{
					totalUsers++;

					currentTweetCount = availableDb.getTweetCountFromUser(user);
					smallestBinIndex = findSmallestBin(bins);
//					totalRecBin = bins.get(smallestBinIndex);
					totalRecBin = bins[smallestBinIndex];
					totalRecBin += currentTweetCount;
//					bins.set(smallestBinIndex, totalRecBin);
					bins[smallestBinIndex] = totalRecBin;
					smallestBinIndex++; //add 1 to index to make index start from 1 instead of 0

					//Add list of recommender servers and increase bins accordingly if distributed
					//ie. add the tweet count to all recommenders if there are 2 for the user looking for recommendation
					ArrayList<Integer> recServers = new ArrayList<Integer>();
					if (usersRec.contains(user))
					{	
						for (int i = 0; i < numRecAgents; i++)
						{
							if (smallestBinIndex-1 != i)
							{
//								totalRecBin = bins.get(i);
								totalRecBin = bins[i];
								totalRecBin += currentTweetCount;
//								bins.set(i,totalRecBin);
								bins[i] = totalRecBin;
							}
							recServers.add(i+1);
						}
					}
					else
					{
						recServers.add(smallestBinIndex);
					}

					long userTweetDelay = calculateTweetDelay(user);

					
//					userFollowTweetCounts = new LinkedHashMap<String,Integer>();
					LinkedHashMap<String,Integer> userFollowTweetCounts = new LinkedHashMap<String,Integer>();
					
					for (String otherUser : listOfUsers)
					{
						if (!otherUser.equals(user))
						{
							if (user.equals(referenceUser))
							{
								int tweetCountToFollow;
								tweetCountToFollow = calculateTweetsToFollow(availableDb.getTweetCountFromUser(otherUser));
								userFollowTweetCounts.put(otherUser, tweetCountToFollow);
							}
							
						}
					}

					allUserFollowTweetCounts.put(user, userFollowTweetCounts);
					System.out.println("user: "+user+" userFollowTweetCounts: " + userFollowTweetCounts);
					
					//Object[] userAgentArgs = new Object[15];							
					Object[] userAgentArgs = new Object[30];
					userAgentArgs[0] = getAID();										
					userAgentArgs[1] = referenceUser;							

					userAgentArgs[3] = beginDate;
					userAgentArgs[4] = endDate;
					userAgentArgs[5] = hashtags_temp;

					userAgentArgs[7] = retweetedby_temp;
					userAgentArgs[8] = stopWordFlag_temp;

					userAgentArgs[10] = recServers;

					userAgentArgs[11] = smallestBinIndex;				

					userAgentArgs[12] = 1;
					userAgentArgs[13] = 1;	
					//					userAgentArgs[14] = tweetDelay;
					userAgentArgs[14] = userTweetDelay;

					//@Jason added limit and referenceUser;
					userAgentArgs[15] = totalTweetLimit;
					userAgentArgs[16] = referenceUser;
					userAgentArgs[17] = kRecommend;
					userAgentArgs[18] = algorithmRec;
					userAgentArgs[19] = myGui;
					userAgentArgs[20] = readFrom;
					userAgentArgs[21] = availableDb.getTweetsFromUser(user);
					userAgentArgs[22] = true;
					userAgentArgs[23] = 1;
					userAgentArgs[24] = referenceUser;
					
					if (user.equals(referenceUser))
					{
						userAgentArgs[25] = true;
					}
					else
					{	
						userAgentArgs[25] = false;
					}
					userAgentArgs[29] = userFollowee.get(user); //followee of User

					String userAgentName = user + "-UserAgent";
					agentController = workContainer.createNewAgent(userAgentName, MobileAgent.class.getName(), userAgentArgs);   
					agentController.start();
					listOfAgents.add(userAgentName);
					listOfAgentControllers.add(agentController);
					//System.out.println("Made agent: "+userAgentName);
//					System.out.println("totalUsers: "+totalUsers);

				}

				int totalnumberoftweets =0;
//				for(int k=0; k<bins.size();k++)
				for(int k=0; k<bins.length;k++)
				{
					//totalnumberoftweets += bins.get(k);
//					System.out.println("bin "+k+": "+bins.get(k));
					System.out.println("bin "+k+": "+bins[k]);
				}
				
				myGui.updateList(listOfUsers); //update before creating recommender agents

				int j =0;
				for(int i=1; i<numRecAgents+1; i++)
				{

					Object[] recAgentArgs = new Object[19];
					recAgentArgs[0] = getAID();										
					recAgentArgs[1] = referenceUser;								

					recAgentArgs[3] = beginDate;
					recAgentArgs[4] = endDate;
					recAgentArgs[5] = hashtags_temp;

					recAgentArgs[7] = retweetedby_temp;
					recAgentArgs[8] = stopWordFlag_temp;



					recAgentArgs[11] = 1;
					recAgentArgs[12] = 1;
					recAgentArgs[13] = bins[j];

					recAgentArgs[15] = numRecAgents;

					//@Jason added argument for algorithm to use
					recAgentArgs[16] = algorithmRec;
					//recAgentArgs[17] = usersRec;
					recAgentArgs[18] = myGui;

					String recAgentName = "Recommender-ServiceAgent" + i;
					agentController = workContainer.createNewAgent(recAgentName, RecommenderAgent.class.getName(), recAgentArgs);
					agentController.start();
					listOfAgents.add(recAgentName);
					listOfAgentControllers.add(agentController);
					System.out.println("Made agent: "+recAgentName);
					j++;
				}


			}catch (Exception e){
				System.err.println("Generate Initialize Error: " + e.getMessage());
			}  

			System.out.println("allUserFollowTweetCounts: "+allUserFollowTweetCounts);
			
			
			
			System.out.println("Generated Dataset");
		}
		
		///*IMPORTANT CODE FOR READING FROM TEXT FILE*/
		if (readFrom == FROM_TEXT)
		{
			localDb.clearDb();
			availableDb.clearDb();
			allUserFollowTweetCounts.clear();
			
			readFromTextFile();

			ArrayList<Tweet> availableTweets;
			Classifier trainedSVM = null; //SVM to train
			
			availableTweets = getAvailableTweets();
			myGui.updateList(listOfUsers);
			
			datasetFollowees = findListFollowees();
			
			//Distributed classifiers
			// if (algorithmRec == MLP && numRecAgents > 1)
			// {
				// findUniqueDocTerms(retweetedby_temp,hashtags_temp,stopWordFlag_temp);
			// }
			if (algorithmRec == MLP)
			{
				processTweets(retweetedby_temp,hashtags_temp,stopWordFlag_temp);
				determineTrainingTestSet();
			}
			
			// if (algorithmRec == SVM && numRecAgents > 1)
			// {
				// System.out.println("Controller Entered Distributed Classifiers");
				// processTweets(retweetedby_temp,hashtags_temp,stopWordFlag_temp);
				// System.out.println("Controller Finished Text Processing Tweets");
				// determineTrainingTestSet();
				// System.out.println("Controller Finished Determining TrainingTest Sets");
				// String arffDirName = "Dataset/Arff_files/";
				// createDir(arffDirName);			
				// String trainSetFilePath = arffDirName + "train_set_controller.txt";
				// createTrainingFile(trainSetFilePath);
				// trainedSVM = trainCentralSVM(trainSetFilePath);
				
				
				// /*To set availableDb based on test set Users*/
				// availableTweets = getDistributedTestSet();
			// }
			
			availableDb.setTweets(availableTweets);		

			
			System.out.println("controller availableDb.getTweetsSize(): "+ availableDb.getTotalTweets());
			//Making agents
			try{

				//@Jason added algorithmRec arg
				//Object[] orgAgentArgs = new Object[16];
				Object[] orgAgentArgs = new Object[19];
				orgAgentArgs[0] = getAID();										
				orgAgentArgs[1] = referenceUser;								

				orgAgentArgs[3] = beginDate;
				orgAgentArgs[4] = endDate;
				orgAgentArgs[5] = hashtags_temp;

				orgAgentArgs[7] = retweetedby_temp;
				orgAgentArgs[8] = stopWordFlag_temp;



				orgAgentArgs[11] = 1;
				orgAgentArgs[12] = 1;
				orgAgentArgs[13] = listOfUsers.size();
				orgAgentArgs[14] = 1;		
				orgAgentArgs[15] = numRecAgents;

				//@Jason added algorithmRec argument
				orgAgentArgs[16] = algorithmRec;

				orgAgentArgs[18] = myGui;

				for(int i=1; i<numOrgAgents+1; i++)
				{
					String orgAgentName = "Organizing Agent" + i;
					agentController = workContainer.createNewAgent(orgAgentName, OrganizingAgent.class.getName(), orgAgentArgs);
					agentController.start();
					listOfAgents.add(orgAgentName);
					listOfAgentControllers.add(agentController);
					System.out.println("Made agent: "+orgAgentName);

				}


				//Create bins for recommender agents
//				ArrayList<Integer> bins = new ArrayList<Integer>();
//				for(int i=0; i<numRecAgents; i++)
//				{
//					bins.add(0);
//				}
				
				int[] bins = new int[numRecAgents]; //bins for recommender agents

				int currentTweetCount;
				int totalRecBin = 0; //Number of tweets in a recommender agent's bin
				int smallestBinIndex = 0;

				totalUsers = 0;
				ArrayList<Integer> recServers;
				
				for (String user : listOfUsers)
				{
					totalUsers++;

					recServers = new ArrayList<Integer>(); //need to make new list for each user
					
					//INCORRECT implementation of distributed SVM since this replicates dataset
					if (algorithmRec == CommonNeighbors && numRecAgents > 1)
					{
						for (int i = 0; i < numRecAgents; i++)
						{
							bins[i] = availableDb.getTotalTweets();
							recServers.add(i+1);
						}
						System.out.println("Controller ENTERD HERE if condition: "+user);
					}
					//ORIGINAL CORRECT evenly distributed data
					else
					{
						System.out.println("Controller ENTERD HERE else condition: "+user);
						currentTweetCount = availableDb.getTweetCountFromUser(user);
						smallestBinIndex = findSmallestBin(bins);
	//					totalRecBin = bins.get(smallestBinIndex);
						totalRecBin = bins[smallestBinIndex];
						totalRecBin += currentTweetCount;
	//					bins.set(smallestBinIndex, totalRecBin);
						bins[smallestBinIndex] = totalRecBin;
						smallestBinIndex++; //add 1 to index to make index start from 1 instead of 0

						//Add list of recommender servers and increase bins accordingly if distributed
						//ie. add the tweet count to all recommenders if there are 2 for the user looking for recommendation
						// K-means Euclidean was added by Sepide 
						if (usersRec.contains(user) || ((algorithmRec == K_MEANS || algorithmRec == MLP || algorithmRec == K_MEANSEUCLIDEAN || algorithmRec == Doc2Vec || algorithmRec == SVM) && numRecAgents > 1 && datasetFollowees.contains(user)))
						{	
							for (int i = 0; i < numRecAgents; i++)
							{
								if (smallestBinIndex-1 != i)
								{
	//								totalRecBin = bins.get(i);
									totalRecBin = bins[i];
									totalRecBin += currentTweetCount;
	//								bins.set(i,totalRecBin);
									bins[i] = totalRecBin;
								}
								recServers.add(i+1);
							}
						}
						else
						{
							recServers.add(smallestBinIndex);
						}	
						
					}
					
					System.out.println("Controller recServers: "+user+" "+recServers+" smallestBinIndex: "+smallestBinIndex);
					
					long userTweetDelay = calculateTweetDelay(user);

					
//					userFollowTweetCounts = new LinkedHashMap<String,Integer>();
					LinkedHashMap<String,Integer> userFollowTweetCounts = new LinkedHashMap<String,Integer>();
					
					for (String otherUser : listOfUsers)
					{
						if (!otherUser.equals(user))
						{
							if (user.equals(referenceUser))
							{
								int tweetCountToFollow;
								tweetCountToFollow = calculateTweetsToFollow(availableDb.getTweetCountFromUser(otherUser));
								userFollowTweetCounts.put(otherUser, tweetCountToFollow);
							}
							
						}
					}

					allUserFollowTweetCounts.put(user, userFollowTweetCounts);
//					System.out.println("user: "+user+" userFollowTweetCounts: " + userFollowTweetCounts);
					
					//Object[] userAgentArgs = new Object[15];							
					Object[] userAgentArgs = new Object[30];
					userAgentArgs[0] = getAID();										
					userAgentArgs[1] = referenceUser;							

					userAgentArgs[3] = beginDate;
					userAgentArgs[4] = endDate;
					userAgentArgs[5] = hashtags_temp;

					userAgentArgs[7] = retweetedby_temp;
					userAgentArgs[8] = stopWordFlag_temp;

					userAgentArgs[10] = recServers;

					userAgentArgs[11] = smallestBinIndex;				

					userAgentArgs[12] = 1;
					userAgentArgs[13] = 1;	
					//					userAgentArgs[14] = tweetDelay;
					userAgentArgs[14] = userTweetDelay;

					//@Jason added limit and referenceUser;
					userAgentArgs[15] = totalTweetLimit;
					userAgentArgs[16] = referenceUser;
					userAgentArgs[17] = kRecommend;
					userAgentArgs[18] = algorithmRec;
					userAgentArgs[19] = myGui;
					userAgentArgs[20] = readFrom;
					userAgentArgs[21] = availableDb.getTweetsFromUser(user);
					userAgentArgs[22] = true;
					userAgentArgs[23] = 1;
					userAgentArgs[24] = referenceUser;
					
					if (user.equals(referenceUser))
					{
						userAgentArgs[25] = true;
					}
					else
					{	
						userAgentArgs[25] = false;
					}
					userAgentArgs[29] = userFollowee.get(user); //followee of User
					
					String userAgentName = user + "-UserAgent";
					agentController = workContainer.createNewAgent(userAgentName, MobileAgent.class.getName(), userAgentArgs);   
					agentController.start();
					listOfAgents.add(userAgentName);
					listOfAgentControllers.add(agentController);
					//System.out.println("Made agent: "+userAgentName);
//					System.out.println("totalUsers: "+totalUsers);

				}

				int totalnumberoftweets =0;
//				for(int k=0; k<bins.size();k++)
				for(int k=0; k<bins.length;k++)
				{
					//totalnumberoftweets += bins.get(k);
//					System.out.println("bin "+k+": "+bins.get(k));
					System.out.println("bin "+k+": "+bins[k]);
				}

				//Only update if it is not distributed SVM
				if ((algorithmRec != SVM) || (algorithmRec == SVM && numRecAgents == 1))
					myGui.updateList(listOfUsers); //update before creating recommender agents
				
				int j =0;
				for(int i=1; i<numRecAgents+1; i++)
				{

					Object[] recAgentArgs = new Object[24];
					recAgentArgs[0] = getAID();										
					recAgentArgs[1] = referenceUser;								

					recAgentArgs[3] = beginDate;
					recAgentArgs[4] = endDate;
					recAgentArgs[5] = hashtags_temp;

					recAgentArgs[7] = retweetedby_temp;
					recAgentArgs[8] = stopWordFlag_temp;



					recAgentArgs[11] = 1;
					recAgentArgs[12] = 1;
//					recAgentArgs[13] = bins.get(j);
					recAgentArgs[13] = bins[j];

					recAgentArgs[15] = numRecAgents;

					//@Jason added argument for algorithm to use
					recAgentArgs[16] = algorithmRec;
					//recAgentArgs[17] = usersRec;
					recAgentArgs[18] = myGui;
					
					if (algorithmRec == SVM && numRecAgents > 1)
					{
						recAgentArgs[19] = trainedSVM;
						
					}
					
					// if (algorithmRec == MLP && numRecAgents > 1)
					if (algorithmRec == MLP)
					{
						recAgentArgs[20] = allUniqueDocTerms;
						recAgentArgs[22] = trainSetUsers;
						recAgentArgs[23] = testSetUsers;
					}
					recAgentArgs[21] = datasetFollowees;
					
					String recAgentName = "Recommender-ServiceAgent" + i;
					agentController = workContainer.createNewAgent(recAgentName, RecommenderAgent.class.getName(), recAgentArgs);
					agentController.start();
					listOfAgents.add(recAgentName);
					listOfAgentControllers.add(agentController);
					System.out.println("Made agent: "+recAgentName);
					j++;
				}

				/*//@Jason added algorithmRec arg
				//Object[] orgAgentArgs = new Object[16];
				Object[] orgAgentArgs = new Object[19];
				orgAgentArgs[0] = getAID();										
				orgAgentArgs[1] = referenceUser;								

				orgAgentArgs[3] = beginDate;
				orgAgentArgs[4] = endDate;
				orgAgentArgs[5] = hashtags_temp;

				orgAgentArgs[7] = retweetedby_temp;
				orgAgentArgs[8] = stopWordFlag_temp;



				orgAgentArgs[11] = 1;
				orgAgentArgs[12] = 1;
				orgAgentArgs[13] = totalUsers;
				orgAgentArgs[14] = 1;		
				orgAgentArgs[15] = numRecAgents;

				//@Jason added algorithmRec argument
				orgAgentArgs[16] = algorithmRec;

				orgAgentArgs[18] = myGui;

				for(int i=1; i<numOrgAgents+1; i++)
				{
					String orgAgentName = "Organizing Agent" + i;
					agentController = workContainer.createNewAgent(orgAgentName, OrganizingAgent.class.getName(), orgAgentArgs);
					agentController.start();
					listOfAgents.add(orgAgentName);
					listOfAgentControllers.add(agentController);
					System.out.println("Made agent: "+orgAgentName);

				}*/
			}catch (Exception e){
				System.err.println("Text Read Initialize Error: " + e.getMessage());
			}  

			System.out.println("FROM TEXTFILE TotalUsers: "+totalUsers);
			System.out.println("allUserFollowTweetCounts: "+allUserFollowTweetCounts);
			
		}


		if (readFrom == FROM_DB)
		{	

			listOfUsers.clear();

			try{


				try {

					String url = "jdbc:mysql://" + serverName + ":" + portNumber + "/" + sid + "?useSSL=false";
					con = DriverManager.getConnection(url, user, pass);


					//@Jason Change the top* tables queryst

					String query;

					if (totalTweetLimit > 0)
						query = "select screen_name,referenceUser,count(*) from (select * from usertweet where referenceUser='"+referenceUser+"' AND CAST(created_at AS DATE) BETWEEN '" + beginDate + "' AND '" + endDate + "' order by tweetid DESC LIMIT "+ totalTweetLimit +") as T1 group by screen_name order by count(*) DESC";				  
					else
						query = "select screen_name,referenceUser,count(*) from (select * from usertweet where referenceUser='"+referenceUser+"' AND CAST(created_at AS DATE) BETWEEN '" + beginDate + "' AND '" + endDate + "') as T1 group by screen_name order by count(*) DESC";
					stmt = con.createStatement();
					resultSet = stmt.executeQuery(query);


				} catch (SQLException e1) {
					e1.printStackTrace();
				}

				resultSet.beforeFirst();
				while(!resultSet.isLast())
				{
					resultSet.next();
					currentTwitterUser = resultSet.getString(1);
					listOfUsers.add(currentTwitterUser);
				}

				int[] bins = new int[numRecAgents]; //bins for recommender agents

				//adds referenceUser tweets to first recommender agent
				//bins.set(0, referenceUserCount);    
				//totalUsers++;

				int currentTweetCount;
				int totalRecBin = 0;
				int smallestBinIndex;

				resultSet.beforeFirst();
				while(!resultSet.isLast())
				{

					resultSet.next();
					currentTwitterUser = resultSet.getString(1);
					//if(!currentTwitterUser.equalsIgnoreCase(referenceUser))			
					//{
					totalUsers++;		

					currentTweetCount  = resultSet.getInt(3);

					smallestBinIndex = findSmallestBin(bins);
					/*System.out.println("smallestBinIndex: "+smallestBinIndex);
					  		for (int i = 0; i < bins.length; i++){
					  			System.out.println("bin "+i+": "+bins[i]);
					  		}*/

					totalRecBin = bins[smallestBinIndex];
					totalRecBin += currentTweetCount;
					bins[smallestBinIndex] = totalRecBin;
					
					smallestBinIndex++;
					
					//Add list of recommender servers and increase bins accordingly if distributed
					//ie. add the tweet count to all recommenders if there are 2 for the user looking for recommendation
					ArrayList<Integer> recServers = new ArrayList<Integer>();
					if (usersRec.contains(user))
					{	
						for (int i = 0; i < numRecAgents; i++)
						{
							if (smallestBinIndex-1 != i)
							{
//								totalRecBin = bins.get(i);
								totalRecBin = bins[i];
								totalRecBin += currentTweetCount;
//								bins.set(i,totalRecBin);
								bins[i] = totalRecBin;
							}
							recServers.add(i+1);
						}
					}
					else
					{
						recServers.add(smallestBinIndex);
					}
					
					//Object[] userAgentArgs = new Object[15];							
					Object[] userAgentArgs = new Object[30];
					userAgentArgs[0] = getAID();										
					userAgentArgs[1] = referenceUser;							

					userAgentArgs[3] = beginDate;
					userAgentArgs[4] = endDate;
					userAgentArgs[5] = hashtags_temp;

					userAgentArgs[7] = retweetedby_temp;
					userAgentArgs[8] = stopWordFlag_temp;

					userAgentArgs[10] = recServers;

					userAgentArgs[11] = smallestBinIndex;				

					userAgentArgs[12] = 1;
					userAgentArgs[13] = 1;	
					userAgentArgs[14] = tweetDelay;

					//@Jason added limit and referenceUser;
					userAgentArgs[15]=totalTweetLimit;
					userAgentArgs[16]=referenceUser;
					userAgentArgs[17] = kRecommend;
					userAgentArgs[18] = algorithmRec;
					userAgentArgs[19] = myGui;
					userAgentArgs[20] = readFrom;
					userAgentArgs[29] = userFollowee.get(user); //followee of User


					String userAgentName = currentTwitterUser + "-UserAgent";
					agentController = workContainer.createNewAgent(userAgentName, MobileAgent.class.getName(), userAgentArgs);   
					agentController.start();
					listOfAgents.add(userAgentName);
					listOfAgentControllers.add(agentController);
					System.out.println("Made agent: "+userAgentName);
//					System.out.println("totalUsers: "+totalUsers);

					//++totalUsers;		
				}
				// }
				resultSet.close();
				stmt.close();
				con.close();


				int totalnumberoftweets =0;
//				for(int k=0; k<bins.size();k++)
				for(int k=0; k<bins.length;k++)
				{
					//totalnumberoftweets += bins.get(k);
//					System.out.println("bin "+k+": "+bins.get(k));
					System.out.println("bin "+k+": "+bins[k]);
				}

				myGui.updateList(listOfUsers); //update before creating recommender agents

				int j =0;
				for(int i=1; i<numRecAgents+1; i++)
				{

					Object[] recAgentArgs = new Object[19];
					recAgentArgs[0] = getAID();										
					recAgentArgs[1] = referenceUser;								

					recAgentArgs[3] = beginDate;
					recAgentArgs[4] = endDate;
					recAgentArgs[5] = hashtags_temp;

					recAgentArgs[7] = retweetedby_temp;
					recAgentArgs[8] = stopWordFlag_temp;



					recAgentArgs[11] = 1;
					recAgentArgs[12] = 1;
//					recAgentArgs[13] = bins.get(j);
					recAgentArgs[13] = bins[j];

					recAgentArgs[15] = numRecAgents;

					//@Jason added argument for algorithm to use
					recAgentArgs[16] = algorithmRec;
					//recAgentArgs[17] = usersRec;
					recAgentArgs[18] = myGui;

					String recAgentName = "Recommender-ServiceAgent" + i;
					agentController = workContainer.createNewAgent(recAgentName, RecommenderAgent.class.getName(), recAgentArgs);
					agentController.start();
					listOfAgents.add(recAgentName);
					listOfAgentControllers.add(agentController);
					System.out.println("Made agent: "+recAgentName);
					j++;
				}

				//@Jason added algorithmRec arg
				//Object[] orgAgentArgs = new Object[16];
				Object[] orgAgentArgs = new Object[19];
				orgAgentArgs[0] = getAID();										
				orgAgentArgs[1] = referenceUser;								

				orgAgentArgs[3] = beginDate;
				orgAgentArgs[4] = endDate;
				orgAgentArgs[5] = hashtags_temp;

				orgAgentArgs[7] = retweetedby_temp;
				orgAgentArgs[8] = stopWordFlag_temp;

 

				orgAgentArgs[11] = 1;
				orgAgentArgs[12] = 1;
				orgAgentArgs[13] = totalUsers;
				orgAgentArgs[14] = 1;		
				orgAgentArgs[15] = numRecAgents;

				//@Jason added algorithmRec argument
				orgAgentArgs[16] = algorithmRec;

				orgAgentArgs[18] = myGui;

				for(int i=1; i<numOrgAgents+1; i++)
				{
					String orgAgentName = "Organizing Agent" + i;
					agentController = workContainer.createNewAgent(orgAgentName, OrganizingAgent.class.getName(), orgAgentArgs);
					agentController.start();
					listOfAgents.add(orgAgentName);
					listOfAgentControllers.add(agentController);
					System.out.println("Made agent: "+orgAgentName);

				}
			}catch (Exception e){
				System.err.println("Error: " + e.getMessage());
			}


		}   //From_DB

		// myGui.updateList(listOfUsers); //update for db or text
		// myGui.enableAllButtons();
		// myGui.reselectRecommendee();
		// myGui.enableList();
		long endTime = System.currentTimeMillis();
		System.out.println("Initialize time: "+(endTime-startTime)+"ms");
		// myGui.showMessageBox("initialize");

	} //inititalize

	public void killContainer()
	{
		try {
			for (AgentController ac : listOfAgentControllers)
			{
				ac.kill();
			}
			workContainer.kill();
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		listOfAgentControllers.clear();
		listOfAgents.clear();
		totalUsers=0;
		usersRec.clear();

		System.out.println("Killed container");

	}

	public void readInGeneratingParameters() throws FileNotFoundException
	{
		//Read in the parameters for each topic's weibull distribution averaged
		Scanner sc = new Scanner(new File("topic_parameters_tfidf_avg.txt")).useDelimiter("[,\n]");
		for (int i = 0; i < topicsParameters.length; i++) {
			for (int j = 0; j < topicsParameters[i].length; j++) {
				if (sc.hasNext()) {
					topicsParameters[i][j] = Double.parseDouble(sc.next());
				}
			}
		}
		sc.close();
		
		// Read in the tf-idf of each word for each topic
		Scanner sc2 = new Scanner(new File("topic_word_tfidf.txt")).useDelimiter("[,\n]");
		for (int i = 0; i < topicsWordTfidf.length; i++) {
			for (int j = 0; j < topicsWordTfidf[i].length; j++) {
				if (sc2.hasNext()) {
					String s = sc2.next();
					topicsWordTfidf[i][j] = Double.parseDouble(sc2.next());
				}
			}
		}
		sc2.close();
		
		// Read in the list of words sorted according to tfidf in
		// ascending order
		Scanner sc3 = new Scanner(new File("topic_words_tfidf.txt"));
		for (int i = 0; i < topicsWords.length; i++) {
			for (int j = 0; j < topicsWords[i].length; j++) {
				if (sc3.hasNext()) {
					topicsWords[i][j] = sc3.next();
//							System.out.println(topicsWords[i][j]);
				}
			}
		}
		sc3.close();
		
		// Read in which user belongs to which topic and how many tweets to
		// generate for each user and min, max values
		Scanner sc4 = new Scanner(new File("topic_generate_user_parameters_tfidf.txt"));
		for (int i = 0; i < TOTAL_ORIGINAL_USERS; i++) {
			sc4.next();
			sc4.nextInt();
			sc4.nextInt();
			topicsMinMax[i][0] = sc4.nextDouble();
			topicsMinMax[i][1] = sc4.nextDouble();
		}
		sc4.close();
	}
	
	public void readInGeneratingParameters2() throws FileNotFoundException
	{
		// Read in the tf-idf of each word for each topic
		Scanner sc = new Scanner(new File("words_100k.txt")).useDelimiter("[\t\n]");
		for (int i = 0; i < TOTAL_ORIGINAL_WORDS_DEMO; i++) {
			String s = sc.next();
			topicsWordsDemo[i] = s;
			Double d = Double.parseDouble(sc.next());
			topicsWordsTfidfDemo[i] = d;
//			System.out.println(s+" "+d);
		}
		sc.close();
	}
	
	// Sets the bins for generating random indices for words
	public void setGeneratedWordBins()
	{
		for (int i = 0; i < totalUsers; i++) {
			
			double dist = (topicsMinMax[i][1] - topicsMinMax[i][0]) / TOTAL_ORIGINAL_WORDS;
			double currentValue = topicsMinMax[i][0];

			for (int j = 0; j < TOTAL_ORIGINAL_WORDS; j++) {
				topicsWordsIndex[i][j] = currentValue;
				currentValue += dist;
//				System.out.println(currentValue);
			}
		}
	}

	// Sets the bins for generating random indices for words
	public void setGeneratedWordBins2()
	{
		for (int i = 0; i < totalUsers; i++) {
			
			double dist = (1.55907e-5 - 4.91893e-6) / TOTAL_ORIGINAL_WORDS_DEMO;
			double currentValue = 4.91893e-6;

			for (int j = 0; j < TOTAL_ORIGINAL_WORDS; j++) {
				topicsWordsIndexDemo[i] = currentValue;
				currentValue += dist;
//				System.out.println(currentValue);
			}
		}
	}
	
//	public double weibull2(double shape, double scale) {
////		RandomDataGenerator r = new RandomDataGenerator();
//		double i;
//		i = randomDataGenerator.nextWeibull(shape, scale);
//		return i;
//	}

	//Finds the smallest bin to fill in when using distributed system
//	public int findSmallestBin(ArrayList<Integer> numbers) { 
	public int findSmallestBin(int[] currentBins) {
		int smallestIndex = 0;
//		int smallest = numbers.get(0);
		int smallest = currentBins[0];
//		for(int i = 0; i < numbers.size(); i++) { 
//			if(numbers.get(i) < smallest) { 
//				smallest = numbers.get(i); 
//				smallestIndex = i;
//			} 
//		}
		for(int i = 0; i < currentBins.length; i++) { 
			if(currentBins[i] < smallest) { 
				smallest = currentBins[i]; 
				smallestIndex = i;
			} 
		} 
		return smallestIndex;
	}
	
	public void generateUserNames()
	{
		int totalUsers = numFollowees + numFollowers;
		listOfUsers.clear(); //Clear list of usernames if it already exists
		String username = "";
		for (int i = 0; i < totalUsers; i++)
		{
			username = "User"+(i+1);
			if (i < numFollowees)
			{
				username += "_Followee";
			}
			else
			{
				username += "_Follower";
			}
			
			listOfUsers.add(username);
		}
	}
	
	public String generateTweetText()
	{
	
		String generatedTweetText = "";
		double averageShape = 0.174651; //Average shape parameter from original corpus
		double averageScale = 5.50E-05; //Average scale parameter from original corpus
				

				
//		Random r = new Random();
		int wordsInTweet = r.nextInt((MAX_WORDS - MIN_WORDS) + 1) + MIN_WORDS;
		int userTopic = r.nextInt(TOTAL_ORIGINAL_USERS);
		
//		System.out.println(getLocalName()+" userTopic: "+userTopic);
		
		for (int j = 0; j < wordsInTweet; j++) {

//			double x = weibull2(averageShape, averageScale);
////			System.out.println(x);
//			while (x < topicsMinMax[userTopic][0] || x > topicsMinMax[userTopic][1])
//			{
//				x = weibull2(averageShape,averageScale);
////				System.out.println("new x: "+x);
//			}
////			System.out.println(x + " min: " + topicsMinMax[userTopic][0] + " max: " + topicsMinMax[userTopic][1]);
			String word = "";
//			int indexOfWord = 0;
//			
//			while (x > topicsWordsIndex[userTopic][indexOfWord] && indexOfWord < TOTAL_ORIGINAL_WORDS-1)
//			{
//				indexOfWord++;
//			}
//			word = topicsWords[userTopic][indexOfWord];
			
			word = generateWord(averageShape, averageScale, userTopic);
			System.out.println("word: "+word);
			//Checks if generated word is already used
			if (currentBagWords.size() < TOTAL_ORIGINAL_WORDS)
			{
				//Generate word until it is a word that has not been used
				while(currentBagWords.contains(word))
				{
					word = generateWord(averageShape, averageScale, userTopic);
					System.out.println("word: "+word);
				}
			}
			//Clear bag of words if all words are in the bag
			else
			{
				currentBagWords.clear();
			}
			
			//Add newly generated word to bag of words
			currentBagWords.add(word);
								
//			System.out.println("bin = "+indexOfWord);
				
			generatedTweetText += word + " ";
		}
		
//		System.out.println("generatedTweet: "+ generatedTweetText);
		
		return generatedTweetText;
	}

	//Testing for Demo
	public String generateTweetText2()
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
			if (currentBagWords.size() < TOTAL_ORIGINAL_WORDS_DEMO)
			{
				//Generate word until it is a word that has not been used
				//while(currentBagWords.contains(word))
				//{
					word = generateWord2(averageShape, averageScale);
					System.out.println("word: "+word);
				//}
			}
			//Clear bag of words if all words are in the bag
			else
			{
				currentBagWords.clear();
			}
			
			//Add newly generated word to bag of words
			currentBagWords.add(word);
								
//			System.out.println("bin = "+indexOfWord);
				
			generatedTweetText += word + " ";
		}
		
//		System.out.println("generatedTweet: "+ generatedTweetText);
		
		return generatedTweetText;
	}
	
	
	public String generateWord(double averageShape, double averageScale, int userTopic)
	{
		String generatedWord = "";
		
//		double x = weibull2(averageShape, averageScale);
		double x = randomDataGenerator.nextWeibull(averageShape, averageScale);
//		System.out.println(x);
		while (x < topicsMinMax[userTopic][0] || x > topicsMinMax[userTopic][1])
		{
//			x = weibull2(averageShape,averageScale);
			x = randomDataGenerator.nextWeibull(averageShape, averageScale);
			System.out.println("new x: "+x);
		}
//		System.out.println(x + " min: " + topicsMinMax[userTopic][0] + " max: " + topicsMinMax[userTopic][1]);
		
		int indexOfWord = 0;
		
		while (x > topicsWordsIndex[userTopic][indexOfWord] && indexOfWord < TOTAL_ORIGINAL_WORDS-1)
		{
			indexOfWord++;
		}
		
		generatedWord = topicsWords[userTopic][indexOfWord];
		
		
		return generatedWord;
	}
	
	public String generateWord2(double averageShape, double averageScale)
	{
		String generatedWord = "";
		ArrayList<String> bagOfWordsFound = new ArrayList<String>();

		double x = randomDataGenerator.nextWeibull(averageShape, averageScale);
//		System.out.println(x);
//		while (x < topicsMinMax[userTopic][0] || x > topicsMinMax[userTopic][1])
//		{
//			x = weibull2(averageShape,averageScale);
			x = randomDataGenerator.nextWeibull(averageShape, averageScale) + 4.91893e-6;
			System.out.println("new x: "+x);
//		}
//		System.out.println(x + " min: " + topicsMinMax[userTopic][0] + " max: " + topicsMinMax[userTopic][1]);
		
		int indexOfWord = 0;
		
		// for (int i = 0; i < topicsWordsDemo.length; i++)
		// {
			// System.out.print(topicsWordsDemo[i]+" ");
		// }
		// System.out.println();
		
		for (int i = 0; i < TOTAL_ORIGINAL_WORDS_DEMO; i++)
		{
			if (topicsWordsTfidfDemo[i] > x)
			{
				bagOfWordsFound.add(topicsWordsDemo[i]);
			}
		}
		
		indexOfWord = r.nextInt(bagOfWordsFound.size());
		
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
	
	public long calculateTweetDelay(String username)
	{
		int numUserTweets = availableDb.getTweetCountFromUser(username);
		int totalTweets = availableDb.getTotalTweets();
		double ratioOfTweets = (double) numUserTweets/totalTweets;
		double tweetDelay = TWEET_DELAY - (ratioOfTweets)*TWEET_DELAY;
		long tweetDelayLong;
		//		System.out.println("Total Tweets: "+totalTweets+" "+username+" tweets: "+numUserTweets + " tweetDelay: "+tweetDelay);
		//		System.out.println("Math.ceil(tweetDelay): "+Math.ceil(tweetDelay));

		//return (long) Math.ceil(tweetDelay);
		tweetDelayLong = Math.round(tweetDelay);
		
		if (tweetDelayLong < 1)
			tweetDelayLong = 1;
		
		return tweetDelayLong;
	}

	//Returns the index of the followee
	public int calculateWhoToFollow(int numFollowees)
	{
//		Random r = new Random();
		return r.nextInt(numFollowees);
	}

	//Returns how many tweets before user follows a followee
	public int calculateTweetsToFollow(int tweetCount)
	{
		int tweetsToFollow = 0;
//		double value = weibull2(DEFAULT_SHAPE,DEFAULT_SCALE);
		double value = randomDataGenerator.nextWeibull(DEFAULT_SHAPE,DEFAULT_SCALE);
		tweetsToFollow = (int) Math.round(tweetCount * value);
		if (tweetsToFollow > tweetCount)
			tweetsToFollow = tweetCount;
		if (tweetsToFollow < 1)
			tweetsToFollow = 1;
		return tweetsToFollow;
	}

	public boolean checkDateRange(String tweetDate, String beginDate, String endDate)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		boolean dateRangeValid = true;
		Date tweetDateFormat = null;
		Date beginDateFormat = null;
		Date endDateFormat = null;
		try {
			tweetDateFormat = sdf.parse(tweetDate);
			beginDateFormat = sdf.parse(beginDate);
			endDateFormat = sdf.parse(endDate);

			if (beginDateFormat.before(tweetDateFormat) && endDateFormat.after(tweetDateFormat))
				dateRangeValid = true;
			else
				dateRangeValid = false;

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return dateRangeValid;
	}

	//Read text file of tweets and store into InMemoryDb
	public void readFromTextFile()
	{
		try {
			final char END_OF_TWEET = '\r';
			int character;
			StringBuffer lineBuffer = new StringBuffer(1024);
			FileInputStream fileInput = new FileInputStream(textFile);
			BufferedInputStream bufferedInput = new BufferedInputStream(fileInput);
			
			Long tweetId;
			String tweetDate;
			String currentUserName;
			String tweetText;
			//S StringBuffer tT = new StringBuffer(); // added by Sepide 
			//S  tweetText.setLength(1000);   // added by Sepide
			//S String tweetText = tT.toString();    // added by Sepide 
			boolean dateRangeValid;
			Tweet currentTweet;
			String followeeName;
			userFollowee = new LinkedHashMap<String,String>();
			tweetIdText.clear();
			tweetIdUser.clear();
			userFolloweeMap.clear();
			
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
						//for (String s : info)
						//{
						//	System.out.print(s+",");
						//}
						//System.out.println();

//						referenceUser = info[0];
//						Long tweetId = Long.valueOf(info[1]);
//						String tweetDate = info[2];
//						String currentUserName = info[4];
//						String tweetText = info[5];
//						boolean dateRangeValid = true;
//
//						//dateRangeValid = checkDateRange(tweetDate,"2007-01-01","2017-01-01");
//						dateRangeValid = checkDateRange(tweetDate,beginDate,endDate);
//
//						Tweet currentTweet = new Tweet(tweetText,tweetId,tweetDate,currentUserName);
						followeeName = info[0];
						referenceUser = info[0];
						tweetId = Long.valueOf(info[1]);
						tweetDate = info[2];
						currentUserName = info[4];
						tweetText = info[5];
						
						//dateRangeValid = checkDateRange(tweetDate,"2007-01-01","2017-01-01");
						dateRangeValid = checkDateRange(tweetDate,beginDate,endDate);

						currentTweet = new Tweet(tweetText,tweetId,tweetDate,currentUserName);
						
						

						tweetsReadCount++;
						
						if (dateRangeValid)
						{
							localDb.addTweet(currentTweet);
							
							tweetIdText.put(tweetId, tweetText);
							tweetIdUser.put(tweetId, currentUserName);
							
							if (!userFollowee.containsKey(currentUserName))
								userFollowee.put(currentUserName,followeeName);
							
							if (!userFolloweeMap.containsKey(currentUserName))
								userFolloweeMap.put(currentUserName,followeeName);
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
	
	public ArrayList<Tweet> getAvailableTweets()
	{
		ArrayList<Tweet> tweetsFromDb = localDb.getTweets();
		ArrayList<Tweet> datasetAvailable = new ArrayList<Tweet>();

		Collections.sort(tweetsFromDb); //Sort by tweetId

		int tweetToLimitCount = 0;
		// int currUserTweetCount = 0;
		String prevUserName = "";
		listOfUsers.clear(); //Clear list of users if there is already a list

		System.out.println(getLocalName()+" getAvailableTweets totalTweetLimit: "+totalTweetLimit);
		for (int i = tweetsFromDb.size()-1; i >= 0; i--)
		{
			tweetToLimitCount++;
			//System.out.println(getLocalName()+" tweetToLimitCount: "+tweetToLimitCount+" totalTweetLimit: "+totalTweetLimit);
			String currentUser;
			Tweet currentTweet = tweetsFromDb.get(i);
//			System.out.println(getLocalName()+" "+currentTweet.getTweetId()+" "+currentTweet.getUser()+" "+currentTweet.getTweetText());


			datasetAvailable.add(currentTweet);
			currentUser = currentTweet.getUser();

			// if (!userTweetCounts.containsKey(currentUser))
			// {
				// currUserTweetCount = 1;
				// userTweetCounts.put(currentUser,currUserTweetCount);
			// }
			// else
			// {
				// currUserTweetCount = userTweetCounts.get(currentUser);
				// currUserTweetCount++;
				// userTweetCounts.put(currentUser,currUserTweetCount);
			// }
			
			if (!currentUser.equals(prevUserName))
			{
				if (!listOfUsers.contains(currentUser))
					listOfUsers.add(currentUser);
				prevUserName = currentUser;
			}
			if (tweetToLimitCount == totalTweetLimit)
				break;
		}
		
		return datasetAvailable;
	}
	
	public void findUniqueDocTerms(int retweetsFlag, int hashTagsFlag, int stopWordsFlag)
	{
		
		System.out.println("Controller Finding Unique Doc Terms");
		int tweetIdCount = 0;
		
		for (Long currTweetId : tweetIdText.keySet())
		{
			tweetIdCount++;
			// System.out.println("controller text processing "+tweetIdCount+" tweetid: "+currTweetId);
			
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
			if (currentText.contains("RT @") && retweetsFlag == RE_TWEETS)
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
			if (hashTagsFlag == HASH_TAGS)
			{
				Pattern specialCharacters = Pattern.compile("[^a-zA-Z\\p{Z}]");
				matcher = specialCharacters.matcher(currentText);
				currentText = matcher.replaceAll(" ");
			}
			//System.out.println("After special characters: " + currentText);

			//Remove stop words if flagged
			if (stopWordsFlag == STOP_WORDS)
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
			if (wordCount >= 3)
			{
				while (sc.hasNext()){
					stringToken = sc.next();

					//Add all unique terms to allUniqueDocTerms
					allUniqueDocTerms.add(stringToken);

				}
				/*for (String s : list){
						System.out.print(s+" ");
					}
					System.out.println();*/
				sc.close();
			}


		} //end for (Long currTweetId : tweetIdText.keySet())
	
	}
	
	public List<String> findListFollowees()
	{
		List<String> listFolloweeNames = new ArrayList<String>();
		for (String currUser : userFollowee.keySet())
		{
			if (!listFolloweeNames.contains(userFollowee.get(currUser)))
				listFolloweeNames.add(userFollowee.get(currUser));
		}
		return listFolloweeNames;


	}
	
	public void processTweets(int retweetsFlag, int hashTagsFlag, int stopWordsFlag)
	{
		ArrayList<Long> tweetIdsToRemove = new ArrayList<Long>(); //tweetIdsToRemove because no useful info
		
		System.out.println("Controller Processing Tweets");
		int tweetIdCount = 0;
		
		for (Long currTweetId : tweetIdText.keySet())
		{
			tweetIdCount++;
			// System.out.println("controller text processing "+tweetIdCount+" tweetid: "+currTweetId);
			
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
			if (currentText.contains("RT @") && retweetsFlag == RE_TWEETS)
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
			if (hashTagsFlag == HASH_TAGS)
			{
				Pattern specialCharacters = Pattern.compile("[^a-zA-Z\\p{Z}]");
				matcher = specialCharacters.matcher(currentText);
				currentText = matcher.replaceAll(" ");
			}
			//System.out.println("After special characters: " + currentText);

			//Remove stop words if flagged
			if (stopWordsFlag == STOP_WORDS)
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

		} //end for (Long currTweetId : tweetIdText.keySet())
		
		for (Long tweetIdToRemove : tweetIdsToRemove)
		{
			if (tweetIdDocumentVector.containsKey(tweetIdToRemove))
				tweetIdDocumentVector.remove(tweetIdToRemove);
			if (tweetIdText.containsKey(tweetIdToRemove))
				tweetIdText.remove(tweetIdToRemove);
			if (tweetIdUser.containsKey(tweetIdToRemove))
				tweetIdUser.remove(tweetIdToRemove);
		} //end for (Long tweetIdToRemove : tweetIdsToRemove)
		
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
		
		int docFreq=0;
		//Get the document frequency of terms
		
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
		
		//Calculate tfidf
		double tf,df,idf,tfidf;
		allUserDocumentsTFIDF = new LinkedHashMap<String,LinkedHashMap<String,Double>>();
		LinkedHashMap<String,LinkedHashMap<String,Double>> allUserDocumentsTF = new LinkedHashMap<String,LinkedHashMap<String,Double>>();
		ArrayList<LinkedHashMap<String,Double>> userDocumentsTFIDFList = new ArrayList<LinkedHashMap<String,Double>>();
		ArrayList<LinkedHashMap<String,Double>> userDocumentsTFList = new ArrayList<LinkedHashMap<String,Double>>();
		totalDocuments = allUserDocuments.size();
		System.out.println(getLocalName()+" Calculating TF-IDF");		

		double vectorMagnitude=0.0;
		double vectorMagnitudeTF=0.0;
		
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
				tf=userDoc.get(docTerm); //tf raw frequency
				df=allTermsDocumentFreq.get(docTerm);
				idf=(double)Math.log10(totalDocuments/df) / Math.log10(2); //log base 2
				tfidf=tf*idf;
				userDocumentTFIDF.put(docTerm, tfidf);
				userDocumentTF.put(docTerm, tf);
//				System.out.println("docTerm: "+docTerm+"\ttf: "+tf+"\tdf: "+df+"\tidf: "+idf+"\ttfidf: "+tfidf);					
				vectorMagnitude+=tfidf*tfidf;
				vectorMagnitudeTF+=tf*tf;
			}

			//System.out.println("tweetId: "+currTweetId+","+userDocumentTFIDF);

			vectorMagnitude = Math.sqrt(vectorMagnitude);
			vectorMagnitudeTF = Math.sqrt(vectorMagnitudeTF);
		
			//precalculate the magnitude of vectors and element-wise division of documents to the magnitude x./|x| ie. normalize the document to unit vectors
			double docSumMag = 0.0;
			double docSumMagTF = 0.0;
			for (String docTerm : userDoc.keySet())
			{
				tfidf = userDocumentTFIDF.get(docTerm);
				tfidf = tfidf / vectorMagnitude;
				userDocumentTFIDF.put(docTerm,tfidf);
				docSumMag += tfidf*tfidf;
				
				tf = userDocumentTF.get(docTerm);
				tf = tf/vectorMagnitude;
				userDocumentTF.put(docTerm,tf);
				docSumMagTF += tf*tf;
			}

			//System.out.println("Normalized_tweetId: "+currTweetId+","+userDocumentTFIDF);

			//System.out.println(Math.sqrt(docSumMag));

			allUserDocumentsTFIDF.put(curName,userDocumentTFIDF);
			allUserDocumentsTF.put(curName, userDocumentTF);
		// } //end for (String curName : usersTweetIdsList.keySet())
		} //String curName : allUserDocuments.keySet()
		
		
	}
	
	public void determineTrainingTestSet()
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
		
		System.out.println("Entered determineTrainingTestSet");
		System.out.println("numTestUsers: "+numTestUsers+" numTrainUsers: "+numTrainUsers);
		
		int countTestUser = 0;
		//loop through each followee for 1 follower at a time until numTestUsers is reached
		while (currTestUsers < numTestUsers)
		{
			for (String followeeName: tempFolloweeFollowers.keySet())
			{
				System.out.println("countTestUser: "+countTestUser);
				currFollowers = tempFolloweeFollowers.get(followeeName);
				// if a followee set runs out of followers before another
				if (currFollowers.size() > 0)
				{
					testSetUsers.add(currFollowers.remove(0));
					currTestUsers++;
					countTestUser++;
				}
				
				tempFolloweeFollowers.put(followeeName,currFollowers);
				
				if (currTestUsers == numTestUsers)
					break;
			}
		}
		
		System.out.println("Determined Test Set");
		
		int countTrainUser = 0;
		while (currTrainUsers < numTrainUsers)
		{
			
			for (String followeeName: tempFolloweeFollowers.keySet())
			{
				System.out.println("countTrainUser: "+countTrainUser);
				currFollowers = tempFolloweeFollowers.get(followeeName);
				//if a followee set runs out of followers before another
				if (currFollowers.size() > 0)
				{
					trainSetUsers.add(currFollowers.remove(0));
					currTrainUsers++;
					countTrainUser++;
				}
				
				tempFolloweeFollowers.put(followeeName,currFollowers);
				
				if (currTrainUsers == numTrainUsers)
					break;
			}
		}
		
		System.out.println("Determined Training Set");
		
	}
	
	public void createTrainingFile(String pathTrainSet)
	{
		System.out.println(getLocalName()+" test set "+ testSetUsers.size() +": "+testSetUsers);
		System.out.println(getLocalName()+" train set "+ trainSetUsers.size() +": "+trainSetUsers);
		
		String algorithmString = "";
		if (algorithmRec == SVM)
			algorithmString = "SVM ";
		myGui.appendResult(algorithmString + " train set "+ trainSetUsers.size() +": "+trainSetUsers);
		myGui.appendResult(algorithmString + " test set "+ testSetUsers.size() +": "+testSetUsers);
		
		// String arffDirName = "Dataset/Arff_files/";
		// File arffDir = new File(arffDirName);
		// if (!arffDir.exists())
		// {
				// arffDir.mkdirs();
		// }
		
		//Create the arff training file attributes
		FileWriter trainWriter;
		// FileWriter testWriter;
		// FileWriter recommendWriter;
		BufferedWriter bufferedWriterTrain;
		// BufferedWriter bufferedWriterTest;
		// BufferedWriter bufferedWriterRecommend;
		int uniqueWordCount = 0;
		// String trainSetFilePath = arffDirName + "train_set_rec"+nodeNumber+".txt";
		// String testSetFilePath = arffDirName + "test_set_rec"+nodeNumber+".txt";
		// String recommendSetFilePath = arffDirName + "recommend_set_rec"+nodeNumber+".txt";
		
		try {
				trainWriter = new FileWriter(pathTrainSet, false);
				// testWriter = new FileWriter(testSetFilePath, false);
				// recommendWriter = new FileWriter(recommendSetFilePath, false);
				bufferedWriterTrain = new BufferedWriter(trainWriter);
				// bufferedWriterTest = new BufferedWriter(testWriter);
				// bufferedWriterRecommend = new BufferedWriter(recommendWriter);
				
				bufferedWriterTrain.write("@relation trainingSetCentral");
				bufferedWriterTrain.newLine();
				bufferedWriterTrain.newLine();
				// bufferedWriterTest.write("@relation testSet");
				// bufferedWriterTest.newLine();
				// bufferedWriterTest.newLine();
				// bufferedWriterRecommend.write("@relation recommendSet");
				// bufferedWriterRecommend.newLine();
				// bufferedWriterRecommend.newLine();
				
				for (String uniqueWord: allUniqueDocTerms)
				{
					bufferedWriterTrain.write("@attribute word"+ uniqueWordCount +" numeric");
					bufferedWriterTrain.newLine();
					// bufferedWriterTest.write("@attribute word"+ uniqueWordCount +" numeric");
					// bufferedWriterTest.newLine();
					// bufferedWriterRecommend.write("@attribute word"+ uniqueWordCount +" numeric");
					// bufferedWriterRecommend.newLine();
					
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
				
				// bufferedWriterTest.write(attributeClass);
				// bufferedWriterTest.newLine();
				// bufferedWriterTest.newLine();
				// bufferedWriterTest.newLine();
				// bufferedWriterTest.write("@data");
				// bufferedWriterTest.newLine();
				
				// bufferedWriterRecommend.write(attributeClass);
				// bufferedWriterRecommend.newLine();
				// bufferedWriterRecommend.newLine();
				// bufferedWriterRecommend.newLine();
				// bufferedWriterRecommend.write("@data");
				// bufferedWriterRecommend.newLine();
				
				bufferedWriterTrain.close();
				// bufferedWriterTest.close();
				// bufferedWriterRecommend.close();
				
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		//Write the vector data of each user for training, test, recommend set to arff files
		try {
				trainWriter = new FileWriter(pathTrainSet, true);
				// testWriter = new FileWriter(testSetFilePath, true);
				// recommendWriter = new FileWriter(recommendSetFilePath, true);
				bufferedWriterTrain = new BufferedWriter(trainWriter);
				// bufferedWriterTest = new BufferedWriter(testWriter);
				// bufferedWriterRecommend = new BufferedWriter(recommendWriter);

				StringJoiner tfidfJoiner;
				double currTfidf;
				
				// for (String currUser : testSetUsers)
				// {
					// Map<String,Double> currDocTfidf = allUserDocumentsTFIDF.get(currUser);
					
					// tfidfJoiner = vectorArffFormat(currDocTfidf,allUniqueDocTerms);
						
					// bufferedWriterTest.write(tfidfJoiner.toString() + "," + userFollowee.get(currUser));
					// bufferedWriterTest.newLine();	
				// }
				// bufferedWriterTest.close();
				
				for (String currUser : trainSetUsers)
				{
					Map<String,Double> currDocTfidf = allUserDocumentsTFIDF.get(currUser);
					tfidfJoiner = vectorArffFormat(currDocTfidf,allUniqueDocTerms);
					
					bufferedWriterTrain.write(tfidfJoiner.toString() + "," + userFollowee.get(currUser));
					bufferedWriterTrain.newLine();
					
				}
				bufferedWriterTrain.close();	

				// for (String currUser : usersRec)
				// {
					
					// Map<String,Double> currDocTfidf = allUserDocumentsTFIDF.get(currUser);
					
					// tfidfJoiner = vectorArffFormat(currDocTfidf,allUniqueDocTerms);
					
					// bufferedWriterRecommend.write(tfidfJoiner.toString() + "," + userFollowee.get(currUser));
					// bufferedWriterRecommend.newLine();
				// }
				// bufferedWriterRecommend.close();	
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public StringJoiner vectorArffFormat(Map<String,Double> currDocTfidf, Set<String> uniqueDocTerms)
	{
		StringJoiner tfidfJoinerTemp = new StringJoiner(",");
		double currTfidf;
		
		for (String uniqueWord: uniqueDocTerms)
		{
			if (currDocTfidf.keySet().contains(uniqueWord))
				currTfidf = currDocTfidf.get(uniqueWord);
			else
				currTfidf = 0.0;
			
			tfidfJoinerTemp.add(String.valueOf(currTfidf));
		}
		return tfidfJoinerTemp;
	}
	
	public ArrayList<Tweet> getDistributedTestSet()
	{
		ArrayList<Tweet> distributedTestSet = new ArrayList<Tweet>();
		listOfUsers.clear();
		
		//update new list of users to only include test set users
		for (String testUser : testSetUsers)
		{
			distributedTestSet.addAll(localDb.getTweetsFromUser(testUser));
			listOfUsers.add(testUser);
		}
		
		//add recommended user(s) to new list of users
		for (String recUser : usersRec)
		{
			if (!listOfUsers.contains(recUser))
			{
				listOfUsers.add(recUser);
				distributedTestSet.addAll(localDb.getTweetsFromUser(recUser));
			}
				
		}
		System.out.println("Controller Distributed SVM listOfUsers: "+listOfUsers);
		
		return distributedTestSet;
	}
	
	public Classifier trainCentralSVM(String pathTrainSet)
	{
		//Train the SVM
		BufferedReader datafile = readDataFile(pathTrainSet);
		Instances data = null;
		try{
			data = new Instances(datafile);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		data.setClassIndex(data.numAttributes() - 1);
		Classifier svmModel = new SMO();
							
		try{
			svmModel.buildClassifier(data);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return svmModel;
	}
	
	public BufferedReader readDataFile(String filename) {
		BufferedReader inputReader = null;

		try {
			inputReader = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException ex) {
			System.err.println("File not found: " + filename);
		}

		return inputReader;
	}

	public Evaluation classify(Classifier model,
			Instances trainingSet, Instances testingSet) throws Exception {
		Evaluation evaluation = new Evaluation(trainingSet);

		model.buildClassifier(trainingSet);
		evaluation.evaluateModel(model, testingSet);

		return evaluation;
	}

	public double calculateAccuracy(FastVector predictions) {
		double correct = 0;

		for (int i = 0; i < predictions.size(); i++) {
			NominalPrediction np = (NominalPrediction) predictions.elementAt(i);
			if (np.predicted() == np.actual()) {
				correct++;
			}
		}

		return 100 * correct / predictions.size();
	}
	
	
	//Creates a directory if it does not exist
	public void createDir(String dirName) {
		File dir = new File(dirName);
		if (!dir.exists())
			dir.mkdirs();
	}

	public void setFile(File textFile)
	{
		this.textFile = textFile;
		System.out.println("textFile.getName(): "+textFile.getName());
		// myGui.appendResult("Loaded File: "+textFile.getName());
	}
	
	public void setCorpusGenFile(File corpusGenFile)
	{
		this.corpusGenFile = corpusGenFile;
		System.out.println("corpusGenFile.getName(): "+corpusGenFile.getName());
		// myGui.appendUserGenTweetsResult("Loaded File: "+corpusGenFile.getName());
	}

	public void setReadFrom(int readFrom)
	{
		this.readFrom = readFrom;
	}
	
	public void setNumArtificialTweets(int numArtificialTweets)
	{
		this.numArtificialTweets = numArtificialTweets;
		System.out.println("numArtificialTweets: "+numArtificialTweets);
	}
	
	//Read in individual user's shape scale then get average
	public void readInGeneratingParameters4() throws FileNotFoundException
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
	
	//Modified to include finding out followee name from filename
	public void readInTfidfMatrix() throws FileNotFoundException
	{
		Scanner sc = new Scanner(new File(pathToTfidfMatrix)).useDelimiter("[\t\n]");
		ArrayList<String> allWordsInSet = new ArrayList<String>();
		ArrayList<String> userNamesIndex = new ArrayList<String>();
		// for (int i = 0; i < usersShape.keySet().size(); i++)
			
		String followeeName = pathToTfidfMatrix.split("_",3)[0].split("/",2)[1];
		
		int numInSet = 4; //for separate groups
		for (int i = 0; i < numInSet; i++)
		{
			String s = sc.next();
			userNamesIndex.add(s);
			followerFollowee.put(s,followeeName);
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
	
	public void setGeneratedWordBins4()
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

}
