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

import org.apache.commons.math3.random.RandomDataGenerator;


public class MobileAgent extends Agent {
	private static final long serialVersionUID = 1L;
	private AID controller;
	private Location destination;

	private long tweetpublishdelayinmillisecond=500000000;
	private long beginTweetTime; //Timing when tweets first begin to organizing agent, mapper to reducer
	private long endTweetTime; //Timing when tweets received by recommender, mapper to reducer
	
	
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

	private int    connectedtoTfidfservernumber;	  
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

	static String serverName = "127.0.0.1";
	static String portNumber = "3306";
	static String sid = "testmysql";

	private Connection con;
	private Statement stmt = null;

	private ResultSet resultSet = null;

	static String user = "root";
	static String pass = "Asdf1234";                      


	private ArrayList<Integer> listRecServers; //List of recommender agents to tweet to

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
	private static final int FROM_DB = 1;
	private static final int FROM_TEXT = 0;
	private static final int FROM_GENERATION = 2;
	private static final int FROM_ARTIFICIAL = 3;
	public static final int MAX_WORDS = 7; //Maximum number of words in tweet after processing
	public static final int MIN_WORDS = 3; //Minimum number of words in tweet after processing
	private static final double MIN_TFIDF = 0;

	transient protected ControllerAgentGui myGui;

	private ArrayList<Tweet> usersTweetFromDb;
	private ArrayList<String> followerNames;
	
 
	private long recServerBeginMessagePassingTime; 	//Time for first tweets to rec agent(s)
	private long[] recServerEndMessagePassingTimes; //Times when rec agent(s) receives last tweet
	private long[] userMessagePassingTimes; //Times for sending all tweets from user to rec agent (reducer)
	
	LinkedHashMap<Double,ArrayList<String>> userTfidfWordsBins; //tf-idf word bins of user
	
	private static Random r = new Random(); //should not construct in method, make it static
	private static RandomDataGenerator randomDataGenerator = new RandomDataGenerator(); //should not construct in method, make it static
	
	private String followeeName;


	protected void setup() {

		followerNames = new ArrayList<String>();
		
		Object[] args = getArguments();
		controller = (AID) args[0];
		destination = here();
		referenceUser  = (String) args[1]; 
		beginDate    = (String) args[3];
		endDate    = (String) args[4];


		listRecServers = (ArrayList<Integer>) args[10];
		connectedtoTfidfservernumber 		= (Integer) args[11];	  
		connectedtoRecservernumber   		= (Integer) args[12];	//useless  
		tweetpublishdelayinmillisecond   	= (Long) args[14];	

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
		//System.out.println(getLocalName()+" listRecServers"+listRecServers);

		twitterUserName = getLocalName().split("-",2)[0];

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
		//System.out.println(agent_name1 + result);
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
					msg2.setContent(totalTweet + " " + whoTweeted+" "+ tweetId + " " + followeeName +" "+ tweetText);
					// msg2.setContent(totalTweet + " " + whoTweeted+" "+ tweetId + " " + tweetText);
					for (int i = 0; i < listRecServers.size(); i++)
					{
						msg2.addReceiver( new AID("Recommender-ServiceAgent"+listRecServers.get(i), AID.ISLOCALNAME) );
						//System.out.println(getLocalName()+" Receiver: Recommender-ServiceAgent"+listRecServers.get(i));
					}
					msg2.setConversationId(conversationIDReceived);
					msg2.setOntology("Tweet From User Agent");			
					send(msg2);
					
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

						if (algorithmRec == K_MEANS)
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
