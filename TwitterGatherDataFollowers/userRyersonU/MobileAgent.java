package TwitterGatherDataFollowers.userRyersonU;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.*;
import jade.content.*;
import jade.content.lang.*;
import jade.content.lang.sl.*;
import jade.content.onto.basic.*;
import jade.domain.*;
import jade.domain.mobility.*;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.JADEAgentManagement.*;
import jade.gui.*;
import java.util.Scanner;    // added by Sepide 
import java.util.Random;     // added by Sepide 
import org.junit.Assert;   // Sepide

import org.apache.commons.math3.random.RandomDataGenerator;


public class MobileAgent extends Agent {
	private static final long serialVersionUID = 1L;
	private AID controller;
	private Location destination;

	private long tweetpublishdelayinmillisecond=500000000;
	private long beginTweetTime; //Timing when tweets first begin to organizing agent, mapper to reducer
	private long endTweetTime; //Timing when tweets received by recommender, mapper to reducer
	private long firstTweetTime;   // added by Sepide 
	
	
	private int requestnumber=0;

	private Behaviour TweetingFromDb;
	private Behaviour TweetingFromText;
	private Behaviour Communication;
	private Behaviour Querying;

	private AID[] alltfidfserviceAgents;

	private String conversationIDReceived="";
	private String referenceUser;
	private String twitterUserName; 
	private String beginDate;
	private String endDate;

	//@Jason added for sql statement to know referenceUser
	private String twitter_referenceUser;
	//added for sql statement to know total limit from tweet table
	private int totalTweetLimit;

	//@Jason added check if user can query for recommendation, if user is not in db deny querying
	private boolean canQuery = true;

	private int    connectedtoTfidfservernumber; //not important
	private int    connectedtoRecservernumber; //useless
	private AID[]  allRecommenderAgents;

	private int tweetCounter = 0;
	private int tweetCount = 0;
	private int totalTweet; //Number of tweets for the user
	private String strLine;
	private Timestamp tweetDateTime;
	private String whoTweeted;
	private String tweetText;
	private long tweetId;
	private String hashTags;
	private int messageCount=0;
	private int kRecommend= 1;
	//private static Scanner x;     // added by Sepide 

	static String serverName = "127.0.0.1";
	static String portNumber = "3306";
	static String sid = "testmysql";

	private Connection con;
	private Statement stmt = null;

	private ResultSet resultSet = null;

	static String user = "root";
	static String pass = "Asdf1234";                      


	private ArrayList<Integer> listRecServers; //List of recommender agents to tweet to
	public Set<Integer> sepArray;     // added by sepide
	private ArrayList<String> usersRec; //Users to be given recommendations    // added by Sepide 

	private boolean finishTweeting = false;
	private int algorithmRec;
	private int readFrom;
	private boolean followSomeone; //True if user follows another user
	private int followAfterTweet; //Follow someone after this number of tweet
	private String userToFollow; //Name of user agent to follow if following is true
	private boolean isFollowee; //True if user is a followee
	private double shapeParameter; //shape parameter 
	private double scaleParameter; //scale parameter

	private static final int COS_SIM = 0;
	private static final int K_MEANS = 1;
	public static final int SVM = 2;
	public static final int MLP = 3;
	public static final int Doc2Vec = 4;   // added by Sepide 
	public static final int CommonNeighbors = 5;        // added by Sepide
	public static final int K_MEANSEUCLIDEAN = 6;   // added by Sepide 
	private static final int FROM_DB = 1;
	private static final int FROM_TEXT = 0;
	private static final int FROM_GENERATION = 2;
	private static final int FROM_ARTIFICIAL = 3;
	public static final int MAX_WORDS = 7; //Maximum number of words in tweet after processing
	public static final int MIN_WORDS = 3; //Minimum number of words in tweet after processing
	private static final double MIN_TFIDF = 0;
	     

	transient protected ControllerAgentGui myGui;
	transient protected RecommenderAgent myRec;

	private ArrayList<Tweet> usersTweetFromDb;
	private ArrayList<String> followerNames;
	
 
	private long recServerBeginMessagePassingTime; 	//Time for first tweets to rec agent(s)
	private long[] recServerEndMessagePassingTimes; //Times when rec agent(s) receives last tweet
	private long[] userMessagePassingTimes; //Times for sending all tweets from user to rec agent (reducer)
	
	LinkedHashMap<Double,ArrayList<String>> userTfidfWordsBins; //tf-idf word bins of user
	//public Map<String,String> userFollowee = new LinkedHashMap<String,String>(); //list of users and their followee names before processing (may have extra since processing can remove users)   // added by Sepide 
	
	private static Random r = new Random(); //should not construct in method, make it static
	private static RandomDataGenerator randomDataGenerator = new RandomDataGenerator(); //should not construct in method, make it static
	
	private String followeeName;


	protected void setup() {

		followerNames = new ArrayList<String>();
		
		Object[] args = getArguments();
		controller = (AID) args[0];
		destination = here();
		referenceUser = (String) args[1]; 
		beginDate = (String) args[3];
		endDate = (String) args[4];


		listRecServers = (ArrayList<Integer>) args[10];
		connectedtoTfidfservernumber = (Integer) args[11];	  
		connectedtoRecservernumber = (Integer) args[12];	//useless  
		tweetpublishdelayinmillisecond = (Long) args[14];	

//		System.out.println(getLocalName()+" tweetDelay: "+ tweetpublishdelayinmillisecond);
		
		//@Jason added in total tweet limit and referenceUser for sql statements
		totalTweetLimit = (Integer) args[15];
		twitter_referenceUser = (String) args[16];
		kRecommend = (Integer) args[17];
		algorithmRec = (Integer) args[18];

		myGui = (ControllerAgentGui) args[19];
		readFrom = (Integer) args[20];

		if (readFrom != FROM_DB)
			usersTweetFromDb = (ArrayList<Tweet>) args[21];
		
		followSomeone = (Boolean) args[22]; //True if user follows another user
		followAfterTweet = (Integer) args[23]; //Follow someone after this number of tweet
		userToFollow = (String) args[24]; //Name of user agent to follow if following is true
		isFollowee = (Boolean) args[25];
		
		if (readFrom == FROM_ARTIFICIAL)
		{
			shapeParameter = (Double) args[26];
			scaleParameter = (Double) args[27];
			userTfidfWordsBins = (LinkedHashMap<Double,ArrayList<String>>) args[28];
			generateArtificialTweets();
		}

		followeeName = (String) args[29];
		
		System.out.println(getLocalName()+"'s followee:	"+followeeName);
		
		recServerEndMessagePassingTimes = new long[listRecServers.size()];
		userMessagePassingTimes = new long[listRecServers.size()];;
		
		
		
//		System.out.println(getLocalName()+" has value for isFollowee: "+isFollowee);
//		if (isFollowee)
//			System.out.println(getLocalName()+" is a followee.");
		

		twitterUserName = getLocalName().split("-",2)[0];
		System.out.println(twitterUserName+" listRecServers "+listRecServers);
		//System.out.println(getLocalName()+" twitterUserName: "+twitterUserName);

		try {
			DFAgentDescription dfd = new DFAgentDescription();
			dfd.setName(getAID());
			ServiceDescription sd = new ServiceDescription();
			sd.setName("Distributed Recommender System");
			sd.setType("User-Agent");
			sd.setOwnership(String.valueOf(connectedtoTfidfservernumber));
			dfd.addServices(sd);
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			e.printStackTrace();
		}		



		final MessageTemplate mt_startAgent = MessageTemplate.and(  
				MessageTemplate.MatchPerformative( ACLMessage.REQUEST ),
				MessageTemplate.MatchSender( new AID("Starter Agent", AID.ISLOCALNAME))) ;

		final MessageTemplate mt_organizingAgent = MessageTemplate.and(  
				MessageTemplate.MatchPerformative( ACLMessage.REQUEST ),
				MessageTemplate.MatchSender( new AID("Organizing Agent1", AID.ISLOCALNAME))) ;

		if (readFrom == FROM_DB)
		{
			String driverName = "com.mysql.jdbc.Driver";
			try {
				String url = "jdbc:mysql://" + serverName + ":" + portNumber + "/" + sid + "?useSSL=false";
				con = DriverManager.getConnection(url, user, pass);

				//@Jason change to allow limit of tweets
				String queryCount;
				if (totalTweetLimit > 0)
					queryCount="select count(*) AS rowcount from (select * from usertweet where referenceUser='"+twitter_referenceUser+"' AND CAST(created_at AS DATE) BETWEEN '" + beginDate + "' AND '" + endDate + "' ORDER BY tweetid DESC limit "+totalTweetLimit+") AS T1 where screen_name='"+twitterUserName+"'";
				else
					queryCount="select count(*) AS rowcount from (select * from usertweet where referenceUser='"+twitter_referenceUser+"' AND CAST(created_at AS DATE) BETWEEN '" + beginDate + "' AND '" + endDate + "' ORDER BY tweetid) AS T1 where screen_name='"+twitterUserName+"'";


				stmt = con.createStatement();
				resultSet = stmt.executeQuery(queryCount);
				resultSet.next();
				tweetCount = resultSet.getInt("rowcount");
				totalTweet = tweetCount;
				resultSet.close();

				//@Jason change limit of tweets
				String query;
				if (totalTweetLimit > 0)
					query="select * from (select * from usertweet where referenceUser='"+twitter_referenceUser+"' AND CAST(created_at AS DATE) BETWEEN '" + beginDate + "' AND '" + endDate + "' ORDER BY tweetid DESC limit "+totalTweetLimit+") AS T1 where screen_name='"+twitterUserName+"'";
				else
					query="select * from (select * from usertweet where referenceUser='"+twitter_referenceUser+"' AND CAST(created_at AS DATE) BETWEEN '" + beginDate + "' AND '" + endDate + "' ORDER BY tweetid) AS T1 where screen_name='"+twitterUserName+"'";

				stmt = con.createStatement();
				resultSet = stmt.executeQuery(query);

			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		else //read from text
		{
			totalTweet = usersTweetFromDb.size();
		}

		//Send to organizing agent to let it know user agent is ready
		//String result = " is Ready to send: " + totalTweet + " Tweets and connected to Rec Server" + connectedtoTfidfservernumber;
		String result = totalTweet + " " + connectedtoTfidfservernumber;
		// System.out.println(agent_name1 + result);
		
		// System.out.println(getLocalName()+" "+result);
		
		ACLMessage msg = new ACLMessage( ACLMessage.INFORM );
		msg.addReceiver( new AID("Organizing Agent1", AID.ISLOCALNAME) ); 
		msg.setContent(result);
		msg.setOntology("Ready");
		//doWait(100);
		send(msg);

		setQueueSize(0);
		
		try {
			FileWriter writer = new FileWriter("tweetCounts.txt", true);
			BufferedWriter bufferedWriter = new BufferedWriter(writer);

			bufferedWriter.write(twitterUserName+ "\t" + totalTweet);
			bufferedWriter.newLine();

			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		TweetingFromText = new TickerBehaviour( this, tweetpublishdelayinmillisecond ) 
		{
			private static final long serialVersionUID = 1L;
			protected void onTick() {
				if(tweetCount == 0 && finishTweeting == false)
				{
					finishTweeting = true;
					//System.out.println(getLocalName()+" Tweeting Completed");

					ACLMessage msg = new ACLMessage( ACLMessage.INFORM );
					String temp1 = Integer.toString(totalTweet);
					String temp2 = Integer.toString(connectedtoTfidfservernumber);
					msg.setContent("Tweets Send(" + temp1 + ") connected to TFIDF" + temp2 + " ConversionID: " + conversationIDReceived);
					msg.addReceiver( new AID("Starter Agent", AID.ISLOCALNAME) ); 
					msg.setConversationId(conversationIDReceived);
					msg.setOntology("Tweeting Completed");
					send(msg);
				}

				if(tweetCount > 0 && finishTweeting == false)
				{
					tweetCount--;				 

					Tweet currentTweet = usersTweetFromDb.get(tweetCount);
					whoTweeted = currentTweet.getUser();
					tweetText = currentTweet.getTweetText();
					tweetId = currentTweet.getTweetId();

					tweetCounter++;

					ACLMessage msg2 = new ACLMessage( ACLMessage.INFORM );
					//ACLMessage msg24 = new ACLMessage( ACLMessage.INFORM );   // added by Sepide 
					msg2.setContent(totalTweet + " " + whoTweeted+" "+ tweetId + " " + followeeName +" "+ tweetText);
					//msg24.setContent(totalTweet + " " + whoTweeted+" "+ tweetId + " " + followeeName +" "+ tweetText);  // added by Sepide 
					// msg2.setContent(totalTweet + " " + whoTweeted+" "+ tweetId + " " + tweetText);
					for (int i = 0; i < listRecServers.size(); i++)
					{
						msg2.addReceiver( new AID("Recommender-ServiceAgent"+listRecServers.get(i), AID.ISLOCALNAME) );
						//System.out.println(getLocalName()+" Receiver: Recommender-ServiceAgent"+listRecServers.get(i));
					}
					msg2.setConversationId(conversationIDReceived);
					//msg24.setOntology("Tweet Sepide");
					msg2.setOntology("Tweet From User Agent");			
					send(msg2);
					//send(msg24);
					
					if (tweetCounter == 1)
					{
						recServerBeginMessagePassingTime = System.nanoTime();
					}
					
					if (tweetCounter == followAfterTweet)
					{
						ACLMessage msg3 = new ACLMessage( ACLMessage.INFORM );
						msg3.addReceiver( new AID(userToFollow+"-UserAgent", AID.ISLOCALNAME) );
//						System.out.println(getLocalName()+" Receiver: "+userToFollow+"-UserAgent");
						msg3.setOntology("Followed From User Agent");			
						send(msg3);
					}

					//@Jason see the tweet
					/*  System.out.println(twitterUserName+ ": " +whoTweeted + " " + tweetDateTime + " " + tweetText + " " +tweetId);
				  System.out.println("tweetCount: "+tweetCount + " tweetCounter: "+tweetCounter);
				  try {
			            FileWriter writer = new FileWriter("tweetsTest.txt", true);
			            BufferedWriter bufferedWriter = new BufferedWriter(writer);

			            bufferedWriter.write(twitterUserName+ ": " +whoTweeted + " " + tweetDateTime + " " + tweetText + " " +tweetId);
			            bufferedWriter.newLine();

			            bufferedWriter.close();
			        } catch (IOException e) {
			            e.printStackTrace();
			        }*/
				}
			}
		};


		//Needs to be updated to be like TweetingFromText
		TweetingFromDb = new TickerBehaviour( this, tweetpublishdelayinmillisecond ) 
		{
			private static final long serialVersionUID = 1L;
			protected void onTick() {
				if(tweetCount == 0 && finishTweeting == false)
				{
					finishTweeting = true;
					//System.out.println(getLocalName()+" Tweeting Completed");

					try {
						resultSet.close();
						stmt.close();
						con.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}


					ACLMessage msg = new ACLMessage( ACLMessage.INFORM );
					String temp1 = Integer.toString(totalTweet);
					String temp2 = Integer.toString(connectedtoTfidfservernumber);
					msg.setContent("Tweets Send(" + temp1 + ") connected to TFIDF" + temp2 + " ConversionID: " + conversationIDReceived);
					msg.addReceiver( new AID("Starter Agent", AID.ISLOCALNAME) ); 
					msg.setConversationId(conversationIDReceived);
					msg.setOntology("Tweeting Completed");
					send(msg);
					
					
				}

				if(tweetCount > 0 && resultSet != null && finishTweeting == false)
				{
					tweetCount--;				 
					try {
						resultSet.next();
						whoTweeted = resultSet.getString(5);
						tweetText = resultSet.getString(6);
						tweetId = resultSet.getLong(2);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					tweetCounter++;

					ACLMessage msg2 = new ACLMessage( ACLMessage.INFORM );
					msg2.setContent(whoTweeted+" "+ tweetId + " " + tweetText);
					for (int i = 0; i < listRecServers.size(); i++)
					{
						msg2.addReceiver( new AID("Recommender-ServiceAgent"+listRecServers.get(i), AID.ISLOCALNAME) );
						//System.out.println(getLocalName()+"Receiver: Recommender-ServiceAgent"+listRecServers.get(i));
					}
					msg2.setConversationId(conversationIDReceived);
					msg2.setOntology("Tweet From User Agent");			
					send(msg2);

					//@Jason see the tweet
					/*  System.out.println(twitterUserName+ ": " +whoTweeted + " " + tweetDateTime + " " + tweetText + " " +tweetId);
				  System.out.println("tweetCount: "+tweetCount + " tweetCounter: "+tweetCounter);
				  try {
			            FileWriter writer = new FileWriter("tweetsTest.txt", true);
			            BufferedWriter bufferedWriter = new BufferedWriter(writer);

			            bufferedWriter.write(twitterUserName+ ": " +whoTweeted + " " + tweetDateTime + " " + tweetText + " " +tweetId);
			            bufferedWriter.newLine();

			            bufferedWriter.close();
			        } catch (IOException e) {
			            e.printStackTrace();
			        }*/
				}
			}
		};

		Communication = new TickerBehaviour( this, 1 ) {
			private static final long serialVersionUID = 1L;
			protected void onTick() {
//				ACLMessage msg = myAgent.receive(mt_startAgent);
				ACLMessage msg = myAgent.receive();

				//@Jason if user is not in db after text processing, change canQuery to false from Rec agent
				if (msg!=null && msg.getOntology() == "Denied Querying" && msg.getPerformative() == ACLMessage.REQUEST) 
				{
					canQuery = false;
					System.out.println(this.getAgent().getLocalName()+" Denied Querying\tcanQuery = "+canQuery);
				}

				//Message from starter agent
				if (msg!=null && msg.getOntology() == "Start SIM" && msg.getPerformative() == ACLMessage.REQUEST) 
				{
					conversationIDReceived = msg.getConversationId();

					beginTweetTime = System.currentTimeMillis();
					
					if (readFrom == FROM_DB)
					{
						try {
							resultSet.beforeFirst();
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					tweetCount = totalTweet;
					tweetCounter = 0;
					finishTweeting = false;

					if (readFrom == FROM_DB)
						addBehaviour(TweetingFromDb);
					else //read from text
						addBehaviour(TweetingFromText);
				}

				//Msg starter agent
				if (msg!=null && msg.getOntology() == "Stop Tweeting" && msg.getPerformative() == ACLMessage.REQUEST) 
				{
					endTweetTime = System.currentTimeMillis();
					
					//System.out.println(getLocalName()+" received Stop Tweeting");
					if (readFrom == FROM_DB)
						removeBehaviour(TweetingFromDb);
					else //read from text
						removeBehaviour(TweetingFromText);
				}


				//Msg sent from no one
				if (msg!=null && msg.getOntology() == "Stop Querying" && msg.getPerformative() == ACLMessage.REQUEST) 
				{
					System.out.println(getLocalName()+" received Stop Querying");
					removeBehaviour( Querying );
				}

				//@Jason added canQuery condition, msg from starter agent
				if (msg!=null && msg.getOntology() == "Start Querying" && msg.getPerformative() == ACLMessage.REQUEST && canQuery==true)
					//if (msg!=null && msg.getOntology() == "Start Querying" && msg.getPerformative() == ACLMessage.REQUEST) 
				{
					//removeBehaviour( Tweeting );
					addBehaviour( Querying );
					String conversationID_received = msg.getConversationId();

					requestnumber++;

					//@Jason added condition that only if requestnumber is 1
					//if (requestnumber <= 1){
					String result = " Send me latest Recommendation List........... ";
					ACLMessage msg4 = new ACLMessage( ACLMessage.REQUEST );
					msg4.addReceiver( new AID("Organizing Agent1", AID.ISLOCALNAME) ); 
					msg4.setConversationId(conversationID_received);
					msg4.setContent(Integer.toString(requestnumber));
					msg4.setOntology("Get Score List");
					send(msg4);

					//@Jason added condition to query only once
					canQuery=false;
					// } 
				}
				
				if (msg!=null && msg.getOntology().equals("Last Tweet Received From Rec Agent"))
				{
					long recServerEndMessagePassingTime = System.nanoTime();
					String recSenderName = msg.getSender().getLocalName();
					System.out.println(getLocalName()+" recSenderName: "+recSenderName);
					// int recAgentIndex = Character.getNumericValue(recSenderName.charAt(recSenderName.length()-1))-1;
					int recAgentIndex = Integer.parseInt(recSenderName.split("(?=\\d*$)",2)[1])-1;
					System.out.println(getLocalName()+" recAgentIndex: " + recAgentIndex);
					recServerEndMessagePassingTimes[recAgentIndex] = recServerEndMessagePassingTime;
					userMessagePassingTimes[recAgentIndex] = recServerEndMessagePassingTime - recServerBeginMessagePassingTime;
					
					long userMessagePassingTimeMs = userMessagePassingTimes[recAgentIndex] / 1000000;
					String textResult = twitterUserName + " to Reducer" + (recAgentIndex+1) + ": " + userMessagePassingTimeMs + " ms";
					System.out.println(textResult);
					myGui.appendResult(textResult);
				}
				
			// Code added by Sepide 
				
		   /* if (msg!=null && msg.getOntology() == "Tweet Sepide")
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
				//totalMessageBytes += utf16MessageBytes.length;
				
				
				}  */
				
				// End of code added by Sepide 
				
				if (msg!=null && msg.getOntology().equals("Followed From User Agent"))
				{
					String userAgentName = msg.getSender().getLocalName();
					String followerName = userAgentName.split("-",2)[0];
					if (!followerName.equals(twitterUserName))
					{
						followerNames.add(followerName);
					}
				}
				
				if (msg!=null && msg.getOntology().equals("Show Followers"))
				{
					if (isFollowee)
					{
						//Remove any users not a part of the clustering from the follower list
						try {
							ArrayList<AID> allUserAgentsList =  (ArrayList<AID>) msg.getContentObject();
							ArrayList<String> availableUserNames = new ArrayList<String>();
							for (AID agentId : allUserAgentsList)
							{
								availableUserNames.add(agentId.getLocalName().split("-",2)[0]);
							}
							
							System.out.println("followerNames: "+followerNames);
							System.out.println("availableUserNames: "+availableUserNames);
							followerNames.retainAll(availableUserNames);
							
						} catch (UnreadableException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.out.println(getLocalName()+" followerNames: "+followerNames);
					}
				}
				
			}
		};

		//Write recommendation list to file for user
		Querying = new CyclicBehaviour( this ) {
			private static final long serialVersionUID = 1L;
			public void action() {

				ACLMessage msg = myAgent.receive(mt_organizingAgent);

				if (msg!=null && msg.getOntology() == "Scores for User") 
				{
					System.out.println(myAgent.getLocalName()+" Scores for User Received");

					int recCount = 0;
					LinkedHashMap<String,Double> scoreReceived;
					
					String outputFileName = outputFileName = "Results/Recommendations/" + referenceUser + "/Recommendations_" + referenceUser + ".txt";

					try {
						scoreReceived = (LinkedHashMap<String,Double>) msg.getContentObject();
						BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName,true));
						//bob	Similarity: 4 Server(s)	bob	400.0
						String userAgentName = getLocalName().split("-",2)[0];
						String userScoresText="";
						userScoresText = "================== Recommendations for " + userAgentName + " ==================\n";

						if (algorithmRec == K_MEANS || algorithmRec == K_MEANSEUCLIDEAN)  // K-means Euclidean was added by Sepide
						{
							writer.write(userAgentName+"\tK_Means: "+listRecServers.size()+" Server(s)\t");
							//userScoresText = userAgentName+" K_Means: "+listRecServers.size()+" Server(s) ";
						}
						else if (algorithmRec == COS_SIM)
						{
							writer.write(userAgentName+"\tCos_Sim: "+listRecServers.size()+" Server(s)\t");
							//userScoresText = userAgentName+" Cos_Sim: "+listRecServers.size()+" Server(s) ";
						}
						System.out.print(getLocalName()+" scores: ");
						for (String otherUser: scoreReceived.keySet())
						{
							recCount++;
							if (recCount <= kRecommend)
							{
								System.out.print(otherUser+": "+scoreReceived.get(otherUser)+" ");
								Object maxEntry = Collections.max(scoreReceived.entrySet(), Map.Entry.comparingByValue()).getKey();     // added by Sepide 
								System.out.println("Max Rating:" + maxEntry);    // added by Sepide 
								writer.write(otherUser+": "+scoreReceived.get(otherUser)+"\t");
								userScoresText += "Recommendation "+recCount+": "+otherUser+"\n";
							}
							else
							{
								writer.write(otherUser+": "+scoreReceived.get(otherUser)+"\t");
							}
						}
						System.out.println();
						writer.newLine();
						writer.close();

						myGui.appendRecommendation(userScoresText);
						recCount = 0;
						
						//  Code added by Sepide 
						
						// Calculating the Max value of Scores
						
						
						File selectedFile = myGui.fileChooser.getSelectedFile();
						//System.out.println("Selected file:" + selectedFile.getAbsolutePath());
						
						String maxKey = "";
						double maxValue = 0;
						
						for(String user : scoreReceived.keySet())
							{
								if(scoreReceived.get(user) > maxValue)
								{
									maxKey = user;
									maxValue = scoreReceived.get(user);
								}
							}
							
							System.out.println("The Max User rating for Sepide:" + maxKey);
						
						//sepArray = scoreReceived.keySet();
						 ArrayList<String> sepTest = new ArrayList<String>(); 
						 Iterator itr = scoreReceived.keySet().iterator();
						 String someTest3 = "";
						 String[] someTest = new String[1000];
						 String[] wordTest = new String[1000];
						 int si = 0;
						 int k = 0;  // this should be hard coded by Sepide 
						 
						String importantStuffDirName = "important-stuff/";
						File importantStuffDir = new File(importantStuffDirName);
						if (!importantStuffDir.exists())
						{
								importantStuffDir.mkdirs();
						}
						try {
							
							BufferedWriter writerSep = new BufferedWriter(new FileWriter(importantStuffDirName+"outPutSep.txt",false));
							writerSep.write(userScoresText);
							writerSep.newLine();
							writerSep.close();
							usersRec = myGui.getUsersRec();
							System.out.println("UsersRec:" +usersRec);
							BufferedWriter writerSep3 = new BufferedWriter(new FileWriter(importantStuffDirName+"outPutSepResult.txt",false));
							BufferedWriter writerSepFollowee2 = new BufferedWriter(new FileWriter(importantStuffDirName+"outPutSepFolloweeResults.txt",true));
							//BufferedReader readerSep = new BufferedReader(new FileReader("D:/important-stuff/Dataset-Journals-Authors-Titles-August6.txt"));	
							//RandomAccessFile fileSep = new RandomAccessFile("D:/important-stuff/Dataset-Journals-Authors-Titles-August6.txt", "r");
							RandomAccessFile fileSep = new RandomAccessFile(importantStuffDirName + selectedFile.getName(), "r");
							//RandomAccessFile fileSep = new RandomAccessFile("D:/important-stuff/Reduced_94k.txt", "r");
							//June 12 RandomAccessFile fileSep = new RandomAccessFile("D:/important-stuff/Reduced_14k.txt", "r");
							//RandomAccessFile fileSep = new RandomAccessFile("D:/important-stuff/Reduced_57k.txt", "r");
							String[] words = null;
							
							
							while(itr.hasNext() && si < 5 ) {   // changed to 5 for the number of Recommendation
									String key = itr.next().toString();
									sepTest.add(key);
									//sepTest.replace(", ",",");
									//sepTest.replace(" ,",",");
									//sepTest.replace(" , ",",");
									writerSep3.write(key);
									//writerSep3.newLine();
									fileSep.seek(0);
									//String line = readerSep.readLine();
									String line = fileSep.readLine();
									//readerSep.mark(1);
									
									mostOuter: while(line != null) 
									{
										words = line.split("\t");
										for (String word : words) 
											  {
													 if (word.equals(key))   //Search for the given word
													 {
													  writerSep3.write(" : ");
													  String wordTest2 = line.split("\t",6)[0];
													  writerSep3.write(wordTest2);
													  writerSep3.newLine();
													  //writerSepFollowee2.newLine();
													  writerSepFollowee2.write(wordTest2);
													  writerSepFollowee2.write(" : ");
														  for(String str: usersRec) {
															  writerSepFollowee2.write(str);
															  writerSepFollowee2.newLine();
														  }
														 break mostOuter; 
														  
													 }
											  }
										//line = readerSep.readLine();
										line = fileSep.readLine();
											  
									}
									//String sepSepTest = myRec.userFollowee.get(key);
									//String sepSepTest = userFollowee.get(key);
									//writerSep3.write(sepSepTest);
									//writerSep3.newLine();   
									//writerSepFollowee2.newLine();
									si++;
									//readerSep.reset();
						        }
								//readerSep.close();
								fileSep.close();
								writerSep3.newLine();
								writerSep3.close();
								writerSepFollowee2.close();
								
									
								} catch (IOException e) {
									  System.out.println("An error occurred.");
									  e.printStackTrace();
								}
								
								for (int i=0; i<sepTest.size();i++) {
									System.out.println("sepTest.get(i)"+sepTest.get(i));
								}
								
						String someTest2 = null;		
						String wordTest1 = null;
						//ArrayList<WriteObject> followeeUserArray = new ArrayList<>();
						FileWriter fileWriterSep = new FileWriter(importantStuffDirName + "test-file-name.txt", true);
						PrintWriter printWriterSep = new PrintWriter(fileWriterSep);
						try {
							
							//BufferedReader readerSepi1 = new BufferedReader(new FileReader("D:/important-stuff/Dataset-Journals-Authors-Titles-August6.txt"));	
							//BufferedReader readerSepi1 = new BufferedReader(new FileReader("D:/important-stuff/Reduced_94k.txt"));
							BufferedReader readerSepi1 = new BufferedReader(new FileReader(importantStuffDirName + selectedFile.getName()));
							//June 12BufferedReader readerSepi1 = new BufferedReader(new FileReader("D:/important-stuff/Reduced_14k.txt"));
							//BufferedReader readerSepi1 = new BufferedReader(new FileReader("D:/important-stuff/Reduced_57k.txt"));
							//BufferedWriter writerSepi1 = new BufferedWriter(new FileWriter("D:/important-stuff/Dataset-Journals-Authors-Titles-August6.txt"));	
							//BufferedReader readerSepi2 = new BufferedReader(new FileReader("D:/important-stuff/Dataset-Journals-Authors-Titles-August6.txt"));
							//BufferedReader readerSepi2 = new BufferedReader(new FileReader("D:/important-stuff/Reduced_94k.txt"));
							BufferedReader readerSepi2 = new BufferedReader(new FileReader(importantStuffDirName + selectedFile.getName()));
							//June 12BufferedReader readerSepi2 = new BufferedReader(new FileReader("D:/important-stuff/Reduced_14k.txt"));
							//BufferedReader readerSepi2 = new BufferedReader(new FileReader("D:/important-stuff/Reduced_57k.txt"));
							String[] words1 = null;
							
							String line1 = readerSepi1.readLine();
						    
						loopOuter: while (line1 != null)  {
				
										words1 = line1.split("\t");
										
										for (String word : words1){
											
											
											if (word.equals(sepTest.get(0))) {
												System.out.println("tessssssst");
													wordTest1 = line1.split("\t",6)[0];
													System.out.println("sepTest.get(0)" + sepTest.get(0));
													printWriterSep.print(sepTest.get(0)+" ");
													//Commented out Oct. 23printWriterSep.print(wordTest1+" ");
													wordTest[k] = line1.split("\t",6)[0];
													//myGui.appendResult("The first K in the program:" + k + wordTest[k]);
													System.out.println("The first K in the program:" + k + wordTest[k]);
												   
												break loopOuter;
											}
											
										}
									line1 = readerSepi1.readLine();	
										
							}
							readerSepi1.close();
							
							String line2 = readerSepi2.readLine();
							loopOuter3: while (line2 != null)  {
								words1 = line2.split("\t");
								for (String word : words1){
									for(String str: usersRec) {
												if (word.equals(str)) {
													//line2.replace((line2.split("\t",6)[0]),wordTest1);
													someTest3 = line2.split("\t",6)[0];
													someTest2 = str;
													System.out.println("str" + str);
													printWriterSep.println(str);
													someTest[k] = str;
													//writerSepi1.write(line2);
													//myGui.appendResult("Sepide is Testing:" + someTest3);
													System.out.println("Sepide is Testing:" + someTest3);
													//myGui.appendResult("Sepide is Testing again:" + someTest2);
													System.out.println("Sepide is Testing again:" + someTest2);
													//myGui.appendResult("The second k in the program:" + k + someTest[k]);
													System.out.println("The second k in the program:" + k + someTest[k]);
													break loopOuter3; 
												}
										}
								
							   }
							   line2 = readerSepi2.readLine();
							
							}
							readerSepi2.close();
							//writerSepi1.close();
							printWriterSep.close();
							
						
						}
						catch (IOException e) {
							  System.out.println("An error occurred.");
							  e.printStackTrace();
						}
						
						//File f = new File("D:/important-stuff/Dataset-Journals-Authors-Titles-August6.txt");
						//FileReader fr = new FileReader(f);
						//String myLine;
						
						/* try {
							
							BufferedReader br = new BufferedReader(fr);
							
							   while ((myLine = br.readLine()) != null && myLine.split("\t",6)[5] == someTest2) {
								   myLine = myLine.replace(myLine.split("\t",6)[0], wordTest1);
							   }
							   br.close();
						}
						catch(IOException e){
						 e.printStackTrace();
					 }  */
					 
					 //String filepath="D:/important-stuff/Dataset-Journals-Authors-Titles-August6.txt";
					 //String filepath="D:/important-stuff/Reduced_94k.txt";
					 String filepath=importantStuffDirName + selectedFile.getName();
					 //June 12String filepath="D:/important-stuff/Reduced_14k.txt";
					 //String filepath="D:/important-stuff/Reduced_57k.txt";
					 //String tempFile="D:/important-stuff/Dataset-Test-August11.txt";
					 //String tempFile="D:/important-stuff/Dataset-Test-Oct15.txt";
					 //String tempFile="D:/important-stuff/Dataset-Test-sepide.txt";
					 String tempFile=importantStuffDirName +"Dataset-Test-" + selectedFile.getName();
					 //June 12String tempFile="D:/important-stuff/Dataset-Test-Reduced14k.txt";
					 //String tempFile="D:/important-stuff/Dataset-Test-Reduced57k.txt";
					 File oldFile=new File(filepath);
					 File newFile=new File(tempFile);
					 String sepFollowee = "";
					 String sepID = "";
					 String sepDate = "";
					 String sepID2 = "";
					 String sepFollower = "";
					 String sepText = "";
					 int bufferSize = 40 * 1024;
					 
					try {
						 FileInputStream fin = new FileInputStream(importantStuffDirName + "test-file-name.txt");
						 BufferedReader fileSepTest = new BufferedReader(new InputStreamReader(fin), bufferSize);
						 //RandomAccessFile fileSepTest = new RandomAccessFile("D:/important-stuff/test-file-name.txt", "r");
						 //BufferedReader fileSepTest = new BufferedReader(new FileReader("D:/important-stuff/test-file-name.txt"));
						 FileWriter fw = new FileWriter(tempFile,false);
						 BufferedWriter bw = new BufferedWriter(fw, bufferSize);
						 //PrintWriter pw = new PrintWriter(bw);
						 Scanner x = new Scanner(new File(filepath));
						 x.useDelimiter("[\t\n]");
						 int i =0;
						 String[] wordsFile = null;
								
						while(x.hasNext())
							{
								
								sepFollowee = x.next();
								sepID = x.next();
								sepDate = x.next();
								sepID2 = x.next();
								sepFollower = x.next();
								sepText = x.next();
								//bw.write("Test-1");
								
								//fileSepTest.seek(0);
								//fileSepTest.mark(0);
								fin.getChannel().position(0);
								String lineFile = fileSepTest.readLine();
							outerloop: {
									
								
								 while (lineFile != null) {
									
									wordsFile = lineFile.split(" ");
									if(sepFollower.equals(wordsFile[1])){
										bw.write(wordsFile[0]+"\t"+sepID+"\t"+sepDate+"\t"+sepID2+"\t"+wordsFile[1]+"\t"+sepText);
										//bw.write("Test-2");
										break outerloop;
										}
									/* else {
											pw.print(sepFollowee+"\t"+sepID+"\t"+sepDate+"\t"+sepID2+"\t"+sepFollower+"\t"+sepText);
										   break outerloop;
										} */
										
										lineFile = fileSepTest.readLine();
										//bw.write("Test-3");
								    } 
									bw.write(sepFollowee+"\t"+sepID+"\t"+sepDate+"\t"+sepID2+"\t"+sepFollower+"\t"+sepText);
									//bw.write("Test-4");
								
							    }
								//fileSepTest.reset();
								/* if(sepFollower.equals(someTest[i]))
								{
										pw.print(wordTest[i]+"\t"+sepID+"\t"+sepDate+"\t"+sepID2+"\t"+someTest[i]+"\t"+sepText);
										i++;
								}
								
								else
								{
									pw.print(sepFollowee+"\t"+sepID+"\t"+sepDate+"\t"+sepID2+"\t"+sepFollower+"\t"+sepText);
								} */ 
								
							}
							x.close();
							bw.flush();
							bw.close();
							fileSepTest.close();
							//fileSepTest = new BufferedReader(new InputStreamReader(fin));
						
					}
					catch(Exception e)
						{
							System.out.println("Error");
						}
						
						
						// Code added for calculating the probability of following a recommendation by the user 
						
						int kRecommend = Integer.parseInt(myGui.recommendationField.getText());
						int min = 1;
						int max = kRecommend;
						Random rand = new Random();
						int randomNum = rand.nextInt((max - min) + 1) + min;
						System.out.println("Random number generated: " + randomNum);
						
						
						// Code added for calculating the weighted probability of following a recommendation by the user
						ArrayList<String> KeYs = new ArrayList<String>();
						ArrayList<Double> vaLues = new ArrayList<Double>();

						for (String otherUser: scoreReceived.keySet())
							{
									recCount++;
									if (recCount <= kRecommend)
									{
										KeYs.add(otherUser);
										vaLues.add(scoreReceived.get(otherUser));
										
									}
								
							}
							
							/* for(int i = 0; i < KeYs.size(); i++) {   
								System.out.print("Keys: " + KeYs.get(i)+ "\n");
							}
							for(int i = 0; i < vaLues.size(); i++) {   
								System.out.print("Values: " + vaLues.get(i)+ "\n");
							} */
							
							
							
							String lineC = "";
							String[] option = null;
							
							try {
								
								String doc2vecDirLoc = "TwitterGatherDataFollowers/userRyersonU/";
								File doc2vecLocDir = new File(doc2vecDirLoc);
								if (!doc2vecLocDir.exists())
								{
										doc2vecLocDir.mkdirs();
								}
								//java.lang.ProcessBuilder pb = new ProcessBuilder("C:/Program Files/Python39/python.exe","D:/Simulator-S-15-May-2020/TwitterGatherDataFollowers/userRyersonU/probab.py",""+KeYs.get(0),""+KeYs.get(1),""+KeYs.get(2),""+vaLues.get(0),""+vaLues.get(1),""+vaLues.get(2)).inheritIO();
                                java.lang.ProcessBuilder pb = new ProcessBuilder("C:/Program Files/Python39/python.exe",doc2vecDirLoc + "probab.py",""+KeYs,""+vaLues,""+myGui.algorithmSelectionBox.getSelectedIndex()).inheritIO();
								Process p = pb.start();
								
								int exitCode = p.waitFor();
								Assert.assertEquals("No errors should be detected", 0, exitCode);
					
					             try 
									 {
										  Thread.sleep(500);
									 } 
								  catch(InterruptedException e)
									{
									  e.printStackTrace();
									}
									
									System.out.println("Probability Calculation is finished");
									BufferedReader readerChoice = new BufferedReader(new FileReader(importantStuffDirName +"outputChoice.txt"));
									lineC = readerChoice.readLine();
									
									while (lineC != null ) {
										
										
										option = lineC.split(" ");
										lineC = readerChoice.readLine();
										System.out.println("the option with probability selected is: " +option[0]);
									}
									readerChoice.close();
									 
									
								
							}
							catch(InterruptedException e)
							{
								 // this part is executed when an exception (in this example InterruptedException) occurs
							}
						
					
                    BufferedWriter writerFollowee = new BufferedWriter(new FileWriter(importantStuffDirName + "outPutSepFollowee.txt"));					
					File file = new File(importantStuffDirName + "outPutSepFollowee.txt");	
					try {
					
				   Scanner scanner = new Scanner(file);
				   Scanner myObj = new Scanner(System.in); 
				   String line = null;   
				   BufferedWriter writerSepFollowee = new BufferedWriter(new FileWriter(importantStuffDirName + "outPutSepFollowee.txt",true));
				   usersRec = myGui.getUsersRec();
				   //Commented out by Sepide in Des.30 int iterNum = myGui.simulationSelectionBox.getSelectedIndex();
				   //commented out on Feb17 int iterNum = (sepTest.indexOf(maxKey) + 2);
				   //Commented out on Feb. 17myGui.simulationSelectionBox.setSelectedIndex(iterNum);
				   //int iterNum  = randomNum;
				   int iterNum  = KeYs.indexOf(option[0]) + 1;  // added on March 13
				   myGui.simulationSelectionBox.setSelectedIndex(iterNum);
                   //String iterNum = myObj.nextLine();
                   //System.out.println("number of iteration for this user is: " + iterNum);				   
				   //int lineNum = 1;
				   /* if (file.exists()){
					   
				   }  */
				   something: for(String str: usersRec) {
						  
						 while (true) { 
                            if(iterNum == 1) {
								writerSepFollowee.write(str);
						        writerSepFollowee.write(" Followes ");
								writerSepFollowee.write(someTest3);
								writerSepFollowee.newLine();
								writerSepFollowee.write(str);
							    writerSepFollowee.write(" Followes ");
							    writerSepFollowee.write(sepTest.get(0));
								writerSepFollowee.newLine();
								break something;
			                }							
							else if(iterNum == 2) {
								
								for (int i = 1; scanner.hasNext()== true; i++){
									    line = scanner.nextLine();
								    // if (line.contains(str + " Followes " + sepTest.get(0)))		
									if (line.contains(str + " Followes " + sepTest.get(0))){
										//writerSepFollowee.write("\n");
										writerSepFollowee.write("\n" + str);
							            writerSepFollowee.write(" Followes ");
							            writerSepFollowee.write(sepTest.get(1));
								        //writerSepFollowee.newLine();
								        break something;
									}else continue;
								}
								writerSepFollowee.write(str);
						        writerSepFollowee.write(" Followes ");
								writerSepFollowee.write(someTest3);
								writerSepFollowee.newLine();
								writerSepFollowee.write(str);
							    writerSepFollowee.write(" Followes ");
							    writerSepFollowee.write(sepTest.get(0));
								writerSepFollowee.newLine();
								writerSepFollowee.write(str);
							    writerSepFollowee.write(" Followes ");
							    writerSepFollowee.write(sepTest.get(1));
								writerSepFollowee.newLine();
								break something;
	
			                }
							else if (iterNum == 3){
								for (int i = 1; scanner.hasNext()== true; i++){
									    line = scanner.nextLine();
									if (line.contains(str + " Followes " + sepTest.get(1))){
										writerSepFollowee.write(str);
							            writerSepFollowee.write(" Followes ");
							            writerSepFollowee.write(sepTest.get(2));
								        writerSepFollowee.newLine();
								        break something;
									}else continue;
								}
								writerSepFollowee.write(str);
						        writerSepFollowee.write(" Followes ");
								writerSepFollowee.write(someTest3);
								writerSepFollowee.newLine();
								writerSepFollowee.write(str);
							    writerSepFollowee.write(" Followes ");
							    writerSepFollowee.write(sepTest.get(0));
								writerSepFollowee.newLine();
								writerSepFollowee.write(str);
							    writerSepFollowee.write(" Followes ");
							    writerSepFollowee.write(sepTest.get(1));
								writerSepFollowee.newLine();
								writerSepFollowee.write(str);
							    writerSepFollowee.write(" Followes ");
							    writerSepFollowee.write(sepTest.get(2));
								writerSepFollowee.newLine();
								break something;
			                }
							
							else if (iterNum == 4){
								for (int i = 1; scanner.hasNext()== true; i++){
									    line = scanner.nextLine();
									if (line.contains(str + " Followes " + sepTest.get(2))){
										writerSepFollowee.write(str);
							            writerSepFollowee.write(" Followes ");
							            writerSepFollowee.write(sepTest.get(3));
								        writerSepFollowee.newLine();
								        break something;
									}else continue;
								}
								writerSepFollowee.write(str);
						        writerSepFollowee.write(" Followes ");
								writerSepFollowee.write(someTest3);
								writerSepFollowee.newLine();
								writerSepFollowee.write(str);
							    writerSepFollowee.write(" Followes ");
							    writerSepFollowee.write(sepTest.get(0));
								writerSepFollowee.newLine();
								writerSepFollowee.write(str);
							    writerSepFollowee.write(" Followes ");
							    writerSepFollowee.write(sepTest.get(1));
								writerSepFollowee.newLine();
								writerSepFollowee.write(str);
							    writerSepFollowee.write(" Followes ");
							    writerSepFollowee.write(sepTest.get(2));
								writerSepFollowee.newLine();
								writerSepFollowee.write(str);
							    writerSepFollowee.write(" Followes ");
							    writerSepFollowee.write(sepTest.get(3));
								writerSepFollowee.newLine();
								break something;
			                }
							else if (iterNum == 5){
								for (int i = 1; scanner.hasNext()== true; i++){
									    line = scanner.nextLine();
									if (line.contains(str + " Followes " + sepTest.get(3))){
										writerSepFollowee.write(str);
							            writerSepFollowee.write(" Followes ");
							            writerSepFollowee.write(sepTest.get(4));
								        writerSepFollowee.newLine();
								        break something;
									}else continue;
								}
								writerSepFollowee.write(str);
						        writerSepFollowee.write(" Followes ");
								writerSepFollowee.write(someTest3);
								writerSepFollowee.newLine();
								writerSepFollowee.write(str);
							    writerSepFollowee.write(" Followes ");
							    writerSepFollowee.write(sepTest.get(0));
								writerSepFollowee.newLine();
								writerSepFollowee.write(str);
							    writerSepFollowee.write(" Followes ");
							    writerSepFollowee.write(sepTest.get(1));
								writerSepFollowee.newLine();
								writerSepFollowee.write(str);
							    writerSepFollowee.write(" Followes ");
							    writerSepFollowee.write(sepTest.get(2));
								writerSepFollowee.newLine();
								writerSepFollowee.write(str);
							    writerSepFollowee.write(" Followes ");
							    writerSepFollowee.write(sepTest.get(3));
								writerSepFollowee.newLine();
								writerSepFollowee.write(str);
							    writerSepFollowee.write(" Followes ");
							    writerSepFollowee.write(sepTest.get(4));
								writerSepFollowee.newLine();
								break something;
			                }
						/* else if (scanner.hasNextLine() == true) {
							      line = scanner.nextLine();
							if (line.contains(str + " Followes " + someTest3)){
								if (scanner.hasNextLine() == true){
									line = scanner.nextLine();
									if (line.contains(str + " Followes " + sepTest.get(0))){
										if (scanner.hasNextLine() == true){
											line = scanner.nextLine();
											if (line.contains(str + " Followes " + sepTest.get(1))){
												if (scanner.hasNextLine() == true){
													line = scanner.nextLine();
													if (line.contains(str + " Followes " + sepTest.get(2))){
														break something;
													}
												}
											}
										}
									}
								}
							}
							
			             }  */
							
					}		
							
						  //String followeeNameSep = followeeFollowers.getKey("Germany");
							//Commented out Oct. 23 String followeeSep = userFollowee.get(str);
							// Commented out Oct 23 writerSepFollowee.newLine();
							//String followeeSep = userFollowee.get(str);
							//writerSepFollowee.write(" Followes ");							
						    
						  		
					}
				   
				   writerSepFollowee.close();
				   scanner.close();  
				   }
				   
				   catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
						
						//Iterator itr2 = sepTest.iterator(); 
						  /* while(itr2.hasNext())
						  { 
							String key2 = itr2.next().toString(); 
							writerSep.write(key2);
							//String sepTesting = recAg.userFollowee.get(key2);
							//writerSep.write(sepTesting);
							writerSep.newLine();
							}
						writerSep.newLine();  */
						
						selectedFile = myGui.fileChooser.getSelectedFile();
						String[] wordsFile3 = null;
						String[] wordsFile4 = null;
						String[] wordsFile5 = null;
						String userFolloweeNum = "";
						 if (selectedFile.getName().equals("Reduced_94k.txt")) {
							try { 
							    BufferedReader readerSepi3 = new BufferedReader(new FileReader(importantStuffDirName +"name-number-94.txt"));
							    String line3 = readerSepi3.readLine();
								
								outer: while (line3 != null) {
									wordsFile3 = line3.split(" ");
									for(String str: usersRec) {
										if (wordsFile3[0].equals(str)){
											System.out.println("User's Number:" + wordsFile3[1]);
											break outer;
										}
									}
									line3 = readerSepi3.readLine();
									
								}
								readerSepi3.close();
								
								BufferedReader readerSepi5 = new BufferedReader(new FileReader(importantStuffDirName +"name-number-94.txt"));
							    String line5 = readerSepi5.readLine();
								
								outerR: while (line5 != null) {
									   wordsFile5 = line5.split(" ");
								
								     if (wordsFile5[0].equals(someTest3)){
											System.out.println("User's Followee Number:" + wordsFile5[1]);
											userFolloweeNum = wordsFile5[1];
											break outerR;
										}
									    line5 = readerSepi5.readLine();
									}
								readerSepi5.close();
							}
							catch (IOException e) {
						      // TODO Auto-generated catch block
						       e.printStackTrace();
					        }
							
							try {
								int iterNum = myGui.simulationSelectionBox.getSelectedIndex();
								BufferedReader readerSepi4 = new BufferedReader(new FileReader(importantStuffDirName +"name-number-94.txt"));
							    String line4 = readerSepi4.readLine();
								System.out.println("iterNum:  " + iterNum);
								outer2: while (line4 != null) {
									wordsFile4 = line4.split(" ");
										if (iterNum == 0 && wordsFile4[0].equals(someTest3)){
											System.out.println("Followee's Number:" + wordsFile4[1]);
											break outer2;
										}
										else if (iterNum == 1 && wordsFile4[0].equals(sepTest.get(0))){
											System.out.println("Followee's Number:" + wordsFile4[1]);
											break outer2;
										}
                                    
									   else if (iterNum == 2 && wordsFile4[0].equals(sepTest.get(1))){
											System.out.println("Followee's Number:" + wordsFile4[1]);
											break outer2;
										}
                                 
									   else if (iterNum == 3 && wordsFile4[0].equals(sepTest.get(2))){
											System.out.println("Followee's Number:" + wordsFile4[1]);
											break outer2;
										}
									   else if (iterNum == 4 && wordsFile4[0].equals(sepTest.get(3))){
											System.out.println("Followee's Number:" + wordsFile4[1]);
											break outer2;
										}
									   else if (iterNum == 5 && wordsFile4[0].equals(sepTest.get(4))){
											System.out.println("Followee's Number:" + wordsFile4[1]);
											break outer2;
										}
                                  else 
									  line4 = readerSepi4.readLine();
								  }
								readerSepi4.close();  
							}
							catch (IOException e) {
						      // TODO Auto-generated catch block
						       e.printStackTrace();
					        }
							
						} 
                        else if (selectedFile.getName().equals("Reduced_57k.txt")) {
							try { 
							    BufferedReader readerSepi4 = new BufferedReader(new FileReader(importantStuffDirName +"name-number-57.txt"));
							    String line4 = readerSepi4.readLine();
								
								outer: while (line4 != null) {
									wordsFile3 = line4.split(" ");
									 for(String str: usersRec) {
										if (wordsFile3[0].equals(str)){
											System.out.println("User's Number:" + wordsFile3[1]);
											break outer;
										}
										
									}
									
									line4 = readerSepi4.readLine();
									
							    }
								readerSepi4.close();
								
								BufferedReader readerSepi5 = new BufferedReader(new FileReader(importantStuffDirName +"name-number-57.txt"));
							    String line5 = readerSepi5.readLine();
								
								outerR: while (line5 != null) {
									wordsFile5 = line5.split(" ");
								
								if (wordsFile5[0].equals(someTest3)){
											System.out.println("User's Followee Number:" + wordsFile5[1]);
											userFolloweeNum = wordsFile5[1];
											break outerR;
										}
									line5 = readerSepi5.readLine();
									}
								readerSepi5.close();
								
							}
							catch (IOException e) {
						      // TODO Auto-generated catch block
						       e.printStackTrace();
					        }
							
							
							try {
								int iterNum = myGui.simulationSelectionBox.getSelectedIndex();
								BufferedReader readerSepi4a = new BufferedReader(new FileReader(importantStuffDirName +"name-number-57.txt"));
							    String line4a = readerSepi4a.readLine();
								System.out.println("iterNum:  " + iterNum);
								outer2: while (line4a != null) {
									wordsFile4 = line4a.split(" ");
										if (iterNum == 0 && wordsFile4[0].equals(someTest3)){
											System.out.println("Followee's Number:" + wordsFile4[1]);
											break outer2;
										}
										else if (iterNum == 1 && wordsFile4[0].equals(sepTest.get(0))){
											System.out.println("Followee's Number:" + wordsFile4[1]);
											break outer2;
										}
                                    
									   else if (iterNum == 2 && wordsFile4[0].equals(sepTest.get(1))){
											System.out.println("Followee's Number:" + wordsFile4[1]);
											break outer2;
										}
                                 
									   else if (iterNum == 3 && wordsFile4[0].equals(sepTest.get(2))){
											System.out.println("Followee's Number:" + wordsFile4[1]);
											break outer2;
										}
									   else if (iterNum == 4 && wordsFile4[0].equals(sepTest.get(3))){
											System.out.println("Followee's Number:" + wordsFile4[1]);
											break outer2;
										}
									   else if (iterNum == 5 && wordsFile4[0].equals(sepTest.get(4))){
											System.out.println("Followee's Number:" + wordsFile4[1]);
											break outer2;
										}
                                  else 
									  line4a = readerSepi4a.readLine();
								  }
								readerSepi4a.close();  
							}
							catch (IOException e) {
						      // TODO Auto-generated catch block
						       e.printStackTrace();
					        }
						}
						else if (selectedFile.getName().equals("Reduced_14k.txt")) {
							try { 
							    BufferedReader readerSepi5 = new BufferedReader(new FileReader(importantStuffDirName + "name-number-14.txt"));
							    String line5 = readerSepi5.readLine();
								
								outer: while (line5 != null) {
									wordsFile3 = line5.split(" ");
									for(String str: usersRec) {
										if (wordsFile3[0].equals(str)){
											System.out.println("User's Number:" + wordsFile3[1]);
											break outer;
										}
									}
									line5 = readerSepi5.readLine();
									
								}
								readerSepi5.close();
								
								readerSepi5 = new BufferedReader(new FileReader(importantStuffDirName + "name-number-14.txt"));
							    line5 = readerSepi5.readLine();
								
								outerR: while (line5 != null) {
									wordsFile5 = line5.split(" ");
								
								if (wordsFile5[0].equals(someTest3)){
											System.out.println("User's Followee Number:" + wordsFile5[1]);
											userFolloweeNum = wordsFile5[1];
											break outerR;
										}
									line5 = readerSepi5.readLine();
									}
								readerSepi5.close();
							}
							catch (IOException e) {
						      // TODO Auto-generated catch block
						       e.printStackTrace();
					        }
							
							try {
								int iterNum = myGui.simulationSelectionBox.getSelectedIndex();
								BufferedReader readerSepi4 = new BufferedReader(new FileReader(importantStuffDirName + "name-number-14.txt"));
							    String line4 = readerSepi4.readLine();
								System.out.println("iterNum:  " + iterNum);
								outer2: while (line4 != null) {
									wordsFile4 = line4.split(" ");
										if (iterNum == 0 && wordsFile4[0].equals(someTest3)){
											System.out.println("Followee's Number:" + wordsFile4[1]);
											break outer2;
										}
										else if (iterNum == 1 && wordsFile4[0].equals(sepTest.get(0))){
											System.out.println("Followee's Number:" + wordsFile4[1]);
											break outer2;
										}
                                    
									   else if (iterNum == 2 && wordsFile4[0].equals(sepTest.get(1))){
											System.out.println("Followee's Number:" + wordsFile4[1]);
											break outer2;
										}
                                 
									   else if (iterNum == 3 && wordsFile4[0].equals(sepTest.get(2))){
											System.out.println("Followee's Number:" + wordsFile4[1]);
											break outer2;
										}
									   else if (iterNum == 4 && wordsFile4[0].equals(sepTest.get(3))){
											System.out.println("Followee's Number:" + wordsFile4[1]);
											break outer2;
										}
									   else if (iterNum == 5 && wordsFile4[0].equals(sepTest.get(4))){
											System.out.println("Followee's Number:" + wordsFile4[1]);
											break outer2;
										}
                                  else 
									  line4 = readerSepi4.readLine();
								  }
								readerSepi4.close();  
							}
							catch (IOException e) {
						      // TODO Auto-generated catch block
						       e.printStackTrace();
					        }
						}
						
						else if (selectedFile.getName().equals("Dataset-Journals-Authors-Titles-August6.txt")) {
							try { 
							    BufferedReader readerSepi5 = new BufferedReader(new FileReader(importantStuffDirName + "name-number-august6.txt"));
							    String line5 = readerSepi5.readLine();
								
								outer: while (line5 != null) {
									wordsFile3 = line5.split(" ");
									for(String str: usersRec) {
										if (wordsFile3[0].equals(str)){
											System.out.println("User's Number:" + wordsFile3[1]);
											break outer;
										}
									}
									line5 = readerSepi5.readLine();
									
								}
								readerSepi5.close();
								
								readerSepi5 = new BufferedReader(new FileReader(importantStuffDirName + "name-number-august6.txt"));
							    line5 = readerSepi5.readLine();
								
								outerR: while (line5 != null) {
									wordsFile5 = line5.split(" ");
								
								if (wordsFile5[0].equals(someTest3)){
											System.out.println("User's Followee Number:" + wordsFile5[1]);
											userFolloweeNum = wordsFile5[1];
											break outerR;
										}
									line5 = readerSepi5.readLine();
									}
								readerSepi5.close();
							}
							catch (IOException e) {
						      // TODO Auto-generated catch block
						       e.printStackTrace();
					        }
							
							try {
								int iterNum = myGui.simulationSelectionBox.getSelectedIndex();
								BufferedReader readerSepi4 = new BufferedReader(new FileReader(importantStuffDirName + "name-number-august6.txt"));
							    String line4 = readerSepi4.readLine();
								System.out.println("iterNum:  " + iterNum);
								outer2: while (line4 != null) {
									wordsFile4 = line4.split(" ");
										if (iterNum == 0 && wordsFile4[0].equals(someTest3)){
											System.out.println("Followee's Number:" + wordsFile4[1]);
											break outer2;
										}
										else if (iterNum == 1 && wordsFile4[0].equals(sepTest.get(0))){
											System.out.println("Followee's Number:" + wordsFile4[1]);
											break outer2;
										}
                                    
									   else if (iterNum == 2 && wordsFile4[0].equals(sepTest.get(1))){
											System.out.println("Followee's Number:" + wordsFile4[1]);
											break outer2;
										}
                                 
									   else if (iterNum == 3 && wordsFile4[0].equals(sepTest.get(2))){
											System.out.println("Followee's Number:" + wordsFile4[1]);
											break outer2;
										}
									   else if (iterNum == 4 && wordsFile4[0].equals(sepTest.get(3))){
											System.out.println("Followee's Number:" + wordsFile4[1]);
											break outer2;
										}
									   else if (iterNum == 5 && wordsFile4[0].equals(sepTest.get(4))){
											System.out.println("Followee's Number:" + wordsFile4[1]);
											break outer2;
										}
                                  else 
									  line4 = readerSepi4.readLine();
								  }
								readerSepi4.close();  
							}
							catch (IOException e) {
						      // TODO Auto-generated catch block
						       e.printStackTrace();
					        }
						}
						
						else if (selectedFile.getName().equals("Dataset-Journals-Authors-Titles-1k.txt")) {
							try { 
							    BufferedReader readerSepi5 = new BufferedReader(new FileReader(importantStuffDirName + "name-number-1.txt"));
							    String line5 = readerSepi5.readLine();
								
								outer: while (line5 != null) {
									wordsFile3 = line5.split(" ");
									for(String str: usersRec) {
										if (wordsFile3[0].equals(str)){
											System.out.println("User's Number:" + wordsFile3[1]);
											break outer;
										}
									}
									line5 = readerSepi5.readLine();
									
								}
								readerSepi5.close();
								
								readerSepi5 = new BufferedReader(new FileReader(importantStuffDirName + "name-number-1.txt"));
							    line5 = readerSepi5.readLine();
								
								outerR: while (line5 != null) {
									wordsFile5 = line5.split(" ");
								
								if (wordsFile5[0].equals(someTest3)){
											System.out.println("User's Followee Number:" + wordsFile5[1]);
											userFolloweeNum = wordsFile5[1];
											break outerR;
										}
									line5 = readerSepi5.readLine();
									}
								readerSepi5.close();
							}
							catch (IOException e) {
						      // TODO Auto-generated catch block
						       e.printStackTrace();
					        }
							
							try {
								int iterNum = myGui.simulationSelectionBox.getSelectedIndex();
								BufferedReader readerSepi4 = new BufferedReader(new FileReader(importantStuffDirName + "name-number-1.txt"));
							    String line4 = readerSepi4.readLine();
								System.out.println("iterNum:  " + iterNum);
								outer2: while (line4 != null) {
									wordsFile4 = line4.split(" ");
										if (iterNum == 0 && wordsFile4[0].equals(someTest3)){
											System.out.println("Followee's Number:" + wordsFile4[1]);
											break outer2;
										}
										else if (iterNum == 1 && wordsFile4[0].equals(sepTest.get(0))){
											System.out.println("Followee's Number:" + wordsFile4[1]);
											break outer2;
										}
                                    
									   else if (iterNum == 2 && wordsFile4[0].equals(sepTest.get(1))){
											System.out.println("Followee's Number:" + wordsFile4[1]);
											break outer2;
										}
                                 
									   else if (iterNum == 3 && wordsFile4[0].equals(sepTest.get(2))){
											System.out.println("Followee's Number:" + wordsFile4[1]);
											break outer2;
										}
									   else if (iterNum == 4 && wordsFile4[0].equals(sepTest.get(3))){
											System.out.println("Followee's Number:" + wordsFile4[1]);
											break outer2;
										}
									   else if (iterNum == 5 && wordsFile4[0].equals(sepTest.get(4))){
											System.out.println("Followee's Number:" + wordsFile4[1]);
											break outer2;
										}
                                  else 
									  line4 = readerSepi4.readLine();
								  }
								readerSepi4.close();  
							}
							catch (IOException e) {
						      // TODO Auto-generated catch block
						       e.printStackTrace();
					        }
						}
						else if (selectedFile.getName().equals("Retail.txt")) {
							try { 
							    BufferedReader readerSepi5 = new BufferedReader(new FileReader(importantStuffDirName + "name-number-retail.txt"));
							    String line5 = readerSepi5.readLine();
								
								outer: while (line5 != null) {
									wordsFile3 = line5.split(" ");
									for(String str: usersRec) {
										if (wordsFile3[0].equals(str)){
											System.out.println("User's Number:" + wordsFile3[1]);
											break outer;
										}
									}
									line5 = readerSepi5.readLine();
									
								}
								readerSepi5.close();
								
								readerSepi5 = new BufferedReader(new FileReader(importantStuffDirName + "name-number-retail.txt"));
							    line5 = readerSepi5.readLine();
								
								outerR: while (line5 != null) {
									wordsFile5 = line5.split(" ");
								
								if (wordsFile5[0].equals(someTest3)){
											System.out.println("User's Followee Number:" + wordsFile5[1]);
											userFolloweeNum = wordsFile5[1];
											break outerR;
										}
									line5 = readerSepi5.readLine();
									}
								readerSepi5.close();
							}
							catch (IOException e) {
						      // TODO Auto-generated catch block
						       e.printStackTrace();
					        }
							
							try {
								int iterNum = myGui.simulationSelectionBox.getSelectedIndex();
								BufferedReader readerSepi4 = new BufferedReader(new FileReader(importantStuffDirName + "name-number-retail.txt"));
							    String line4 = readerSepi4.readLine();
								System.out.println("iterNum:  " + iterNum);
								outer2: while (line4 != null) {
									wordsFile4 = line4.split(" ");
										if (iterNum == 0 && wordsFile4[0].equals(someTest3)){
											System.out.println("Followee's Number:" + wordsFile4[1]);
											break outer2;
										}
										else if (iterNum == 1 && wordsFile4[0].equals(sepTest.get(0))){
											System.out.println("Followee's Number:" + wordsFile4[1]);
											break outer2;
										}
                                    
									   else if (iterNum == 2 && wordsFile4[0].equals(sepTest.get(1))){
											System.out.println("Followee's Number:" + wordsFile4[1]);
											break outer2;
										}
                                 
									   else if (iterNum == 3 && wordsFile4[0].equals(sepTest.get(2))){
											System.out.println("Followee's Number:" + wordsFile4[1]);
											break outer2;
										}
									   else if (iterNum == 4 && wordsFile4[0].equals(sepTest.get(3))){
											System.out.println("Followee's Number:" + wordsFile4[1]);
											break outer2;
										}
									   else if (iterNum == 5 && wordsFile4[0].equals(sepTest.get(4))){
											System.out.println("Followee's Number:" + wordsFile4[1]);
											break outer2;
										}
                                  else 
									  line4 = readerSepi4.readLine();
								  }
								readerSepi4.close();  
							}
							catch (IOException e) {
						      // TODO Auto-generated catch block
						       e.printStackTrace();
					        }
						}
						
						try {
							BufferedWriter writeredges = new BufferedWriter(new FileWriter(importantStuffDirName + "edges-numbers.txt",true));
							writeredges.write(wordsFile3[1]+ " " + userFolloweeNum + "\n");  // added on June 17 for adding the new link not replacing the previous link
							//writeredges.newLine();   // added on June 17
							writeredges.write(wordsFile3[1]+ " " + wordsFile4[1]);
						    writeredges.newLine();
							writeredges.close();
							//java.lang.Runtime.getRuntime().exec("cmd /c start cmd.exe /K \"gcc C:\\Users\\s2baniha\\Desktop\\important-stuff\\TXT2GMLv1.0\\conversion.c");
							//java.lang.Runtime.getRuntime().exec("cmd /c start cmd.exe /K \"C:\\Users\\s2baniha\\Desktop\\important-stuff\\TXT2GMLv1.0\\conversion C:\\Users\\s2baniha\\Desktop\\important-stuff\\edges-numbers");
							File dir = new File(importantStuffDirName + "TXT2GMLv1.0");
							Process p1 = java.lang.Runtime.getRuntime().exec( "gcc " +  importantStuffDirName + "TXT2GMLv1.0/conversion.c" + " -o " + importantStuffDirName + "TXT2GMLv1.0/conversion", null, dir );
							try 
                                 {
                                      Thread.sleep(1000);
                                 } 
                              catch(InterruptedException e)
                                {
                                  e.printStackTrace();
                                     }
							  //Process p = java.lang.Runtime.getRuntime().exec( "D:/Jorge/Simulator-S-15-May-2020/important-stuff/TXT2GMLv1.0/conversion  D:/Jorge/Simulator-S-15-May-2020/important-stuff/edges-numbers");
							  Process p = java.lang.Runtime.getRuntime().exec( importantStuffDirName +"TXT2GMLv1.0/conversion" + "  " + importantStuffDirName +"edges-numbers");
						}
						
						catch (IOException e) {
						      // TODO Auto-generated catch block
						       e.printStackTrace();
					        }
							try 
                                 {
                                      Thread.sleep(10000);
                                 } 
                              catch(InterruptedException e)
                                {
                                  e.printStackTrace();
                                     }
							
						// End of code added by Sepide 
						
						FileWriter recommendationWriter;
						try {
							recommendationWriter = new FileWriter("recommendations_lists.txt", true); //append
							BufferedWriter bufferedWriter = new BufferedWriter(recommendationWriter);
							String recListScores = "";
							String thisUserName = getLocalName().split("-")[0];
							bufferedWriter.write(thisUserName+"| ");
							for (String otherUserName : scoreReceived.keySet())
							{
								recListScores += otherUserName + ": " + scoreReceived.get(otherUserName) + ", ";
							}
							recListScores = recListScores.substring(0, recListScores.length()-1);
							bufferedWriter.write(recListScores);
							bufferedWriter.newLine();
							bufferedWriter.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					} catch (UnreadableException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					// Code added by Sepide 
					
					ACLMessage msg5 = new ACLMessage( ACLMessage.INFORM );
					for (int i = 0; i < listRecServers.size(); i++)
					{
						msg5.addReceiver( new AID("Recommender-ServiceAgent"+listRecServers.get(i), AID.ISLOCALNAME) );
						//System.out.println(getLocalName()+" Receiver: Recommender-ServiceAgent"+listRecServers.get(i));
					}  
					//msg5.addReceiver(msg.getSender());
					msg5.setContent("Sepide Testing");
					msg5.setOntology("Sepide Testing");
					send(msg5);
					
					// End of code added by Sepide
					
				
					ACLMessage msg4 = new ACLMessage( ACLMessage.INFORM );
					msg4.addReceiver( new AID("Organizing Agent1", AID.ISLOCALNAME) ); 
					msg4.setContent(Integer.toString(requestnumber));
					msg4.setOntology("Scores Received");
					send(msg4);
					
					 
				}			  
			}
		};				

		addBehaviour( Communication );
	}	

	//generate tweets based on individual users
	public String generateTweetText4(String tweetUserName)
	{

		String generatedTweetText = "";
		double userShape = shapeParameter; //Average shape parameter from original corpus
		double userScale = scaleParameter; //Average scale parameter from original corpus

		int wordsInTweet = r.nextInt((MAX_WORDS - MIN_WORDS) + 1) + MIN_WORDS;		

		for (int j = 0; j < wordsInTweet; j++) {

			String word = "";


			word = generateWord4(tweetUserName,userShape, userScale);
			// System.out.println("word: "+word);
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
	public String generateWord4(String currTweetUserName, double currUserShape, double currUserScale)
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
		// System.out.println("new x: "+x);
		//		}
		//		System.out.println(x + " min: " + topicsMinMax[userTopic][0] + " max: " + topicsMinMax[userTopic][1]);

		int indexOfWord = 0;
		int indexClosest = 0;

		LinkedHashMap<Double,ArrayList<String>> currUserTfidfWordsBins = userTfidfWordsBins; 
		
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
	
	public void generateArtificialTweets()
	{
		ArrayList<Tweet> artificialTweets = new ArrayList<Tweet>();
		for (Tweet currentRealTweet : usersTweetFromDb )
		{
			String artificialTweetText;
			Tweet artificialTweet = new Tweet();
			artificialTweet.setTweetId(currentRealTweet.getTweetId());
			artificialTweet.setDateString(currentRealTweet.getDateString());
			artificialTweet.setUser(currentRealTweet.getUser());
			
			artificialTweetText = generateTweetText4(currentRealTweet.getUser());
			artificialTweet.setTweetText(artificialTweetText);
			
			artificialTweets.add(artificialTweet);
		}
		
		usersTweetFromDb.clear();
		usersTweetFromDb = artificialTweets;
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
