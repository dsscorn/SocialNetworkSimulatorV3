package TwitterGatherDataFollowers.userRyersonU;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Collections;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.time.LocalDateTime;

import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.Location;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAException;

import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import jade.domain.JADEAgentManagement.KillAgent;
import jade.domain.JADEAgentManagement.ShutdownPlatform;
import jade.domain.mobility.MobilityOntology;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.awt.Desktop;  // added by Sepide
import java.io.*;  // added by Sepide
import org.gephi.statistics.plugin.Modularity;   // added by Sepide 
import org.gephi.statistics.plugin.builder.ModularityBuilder;  // added by Sepide 
import org.gephi.io.importer.plugin.file.ImporterBuilderGML;   // added by Sepide
import org.gephi.io.importer.spi.*;  // added by Sepide 
//import org.gephi.io.*;   // added by Sepide
import org.gephi.io.importer.plugin.file.ImporterGML;  // added by Sepide 
import org.gephi.io.importer.api.ImportController;  // added by Sepide 
import org.gephi.io.importer.api.Container;  // added by Sepide 
import org.gephi.io.importer.api.EdgeDirectionDefault;  // added by Sepide 
import org.openide.util.Lookup;      // added by Sepide
import org.gephi.graph.api.GraphModel;  // added by Sepide 
import org.gephi.graph.api.GraphController;   // added by Sepide 
import org.gephi.io.processor.plugin.DefaultProcessor;    // added by Sepide
import org.gephi.graph.api.UndirectedGraph;    // added by Sepide 
import org.gephi.graph.api.DirectedGraph;    // added by Sepide 
import org.gephi.project.api.Workspace;    // added by Sepide
import org.gephi.project.api.ProjectController;   // added by Sepide 
import org.gephi.statistics.plugin.Modularity;  // added by Sepide 
import org.gephi.appearance.api.Partition;      // added by Sepide 
import org.gephi.appearance.api.PartitionFunction;   // added by Sepide 
import org.gephi.appearance.plugin.PartitionElementColorTransformer;   // added by Sepide 
import org.gephi.appearance.plugin.palette.Palette;   // added by Sepide 
import org.gephi.appearance.plugin.palette.PaletteManager;   // added by Sepide 
import org.gephi.graph.api.Column;   // added by Sepide 
import org.gephi.appearance.api.AppearanceController;    // added by Sepide 
import org.gephi.appearance.api.AppearanceModel;        // added by Sepide 
import org.gephi.appearance.api.Function;       // added by Sepide
import org.gephi.io.exporter.api.ExportController;   // added by Sepide
import org.gephi.layout.plugin.forceAtlas.ForceAtlasLayout;      // added by Sepide
import org.gephi.layout.plugin.forceAtlas.ForceAtlas;   // added by Sepide
import java.util.concurrent.TimeUnit;   // added by Sepide
import org.gephi.layout.plugin.AutoLayout;   // added by Sepide
import org.gephi.layout.plugin.force.StepDisplacement;   // added by Sepide 
import org.gephi.layout.plugin.force.yifanHu.YifanHuLayout;  // added by Sepide 
import org.gephi.layout.plugin.forceAtlas2.ForceAtlas2; // added by Sepide 
import org.ghost4j.*;   // added by Sepide 
import org.ghost4j.converter.PDFConverter;    // added by Sepide 
import org.ghost4j.renderer.SimpleRenderer;   // added by Sepide 
import java.awt.Image;       // added by Sepide 
import java.awt.image.RenderedImage;    // added by Sepide 
import javax.imageio.ImageIO;     // added by Sepide 
import org.ghost4j.document.PDFDocument;   // added by Sepide 
import org.ghost4j.analyzer.AnalysisItem;   // added by Sepide 
import org.ghost4j.analyzer.FontAnalyzer;     // added by Sepide   
import java.awt.image.BufferedImage;    // added by Sepide 
import com.spire.pdf.PdfDocument;   // added by Sepide 
import org.gephi.preview.api.*;       // added by Sepide 

public class StarterAgent extends Agent 
{
	private static final long serialVersionUID = 1L;

	private AID[] alltfidfserviceAgents;
	private AID controllerAID;
	private Location destination;
	private int numberofusers=0;
	private int numberofusers_counter=0;
	private int queryUserCounter=0;
	private int numberofusermessagesfromserver=0;
	private int userCounter=0;


	private int tfidfservercount=0;



	private String conversationIDReceived;
	private String conversationIDInitial = "StartSim";
	private boolean alltweetsflag = false;
	private boolean alltfidfflag = false;

	//@Jason added numOfRecAgentsCount recommender agents
	private int numOfRecAgentsCount=0;
	private int numNodes = 1;

	//@Jason changed datastructure of allUserAgents to a list
	private ArrayList<AID> allUserAgentsList = new ArrayList<AID>();

	private ArrayList<String> usersRec;

	transient protected ControllerAgentGui myGui;

	private boolean endSimulation = false; //To determine if user that is looking for recommendation is removed after processing

	private long[] messagePassingTimes; //Message passing times for each node
	private long maxMessagePassingTime; //Maximum message passing of parallel nodes
	
	private int[] messagePassingCosts; //Message passing cost bytes for each node
	private int totalMessagePassingCost; //Total message passing cost of parallel nodes

	private long kmeansMessageTime; //Message passing time for k-means results
	private long beginKmeansMergeTime;
	private long endKmeansMergeTime;
	
	protected void setup() 
	{


		getContentManager().registerLanguage(new SLCodec());
		getContentManager().registerOntology(JADEManagementOntology.getInstance());

		Object[] args 				= getArguments();
		controllerAID				= (AID) args[0];
		destination 				= here();

		String temptweetvector    	= (String) args[5];
		numberofusers 				= (Integer) args[6];

		//usersRec = (ArrayList<String>) args[10];
		myGui = (ControllerAgentGui) args[11];
		numNodes = (Integer) args[12];


		final int numberofuserparticipated = numberofusers;
		
		//@Jason checking numberofuserparticipated
		System.out.println("numberofuserparticipated: "+numberofuserparticipated);

		try {
			DFAgentDescription dfd = new DFAgentDescription();
			dfd.setName(getAID());
			ServiceDescription sd = new ServiceDescription();
			sd.setName("Distributed Recommender System");
			sd.setType("Starter Agent");
			dfd.addServices(sd);
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			e.printStackTrace();
		}		

		do 
		{
			DFAgentDescription recAgentDFTemplate = new DFAgentDescription();
			ServiceDescription sd2 = new ServiceDescription();
			sd2.setType("Recommender Agent");
			recAgentDFTemplate.addServices(sd2);
			try {
				DFAgentDescription[] result2 = DFService.search(this, recAgentDFTemplate);
				alltfidfserviceAgents = new AID[result2.length];
				for (int i = 0; i < result2.length; ++i) {
					alltfidfserviceAgents[i] = result2[i].getName();
				}
			}
			catch (FIPAException fe) {
				fe.printStackTrace();
			}
			System.out.println(getLocalName()+" ENTERED CHECKING NUM NODES");
		} while (alltfidfserviceAgents.length < numNodes);

		//Set controllerAgentDFTemplate to look in DF for user agents
		DFAgentDescription controllerAgentDFTemplate = new DFAgentDescription();
		ServiceDescription sd3 = new ServiceDescription();
		sd3.setType("Controller Agent");
		controllerAgentDFTemplate.addServices(sd3);

		//@Jason get controller AID
		try{
			DFAgentDescription[] result = DFService.search(this, controllerAgentDFTemplate);
			controllerAID = result[0].getName();
		}

		catch (FIPAException fe) {
			fe.printStackTrace();
		}



		//Set userAgentDFTemplate to look in DF for user agents
		DFAgentDescription userAgentDFTemplate = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("User-Agent");
		userAgentDFTemplate.addServices(sd);

		//@Jason using allUserAgentsList
		try{
			DFAgentDescription[] result = DFService.search(this, userAgentDFTemplate);
			for (int i = 0; i < result.length; i++){
				allUserAgentsList.add(result[i].getName());
			}
		}

		catch (FIPAException fe) {
			fe.printStackTrace();
		}

		//@Jason print out all the initial user agents
		/*for (int i = 0; i < allUserAgentsList.size(); i++){
			System.out.println(this.getLocalName()+" allUserAgentsList.get("+i+"): "+allUserAgentsList.get(i));
		}*/

		ACLMessage msg2 = new ACLMessage( ACLMessage.REQUEST );
		//@Jason using allUserAgentsList
		for(int i=0; i<allUserAgentsList.size(); i++)
		{
			msg2.addReceiver(allUserAgentsList.get(i));  
		}	  	

		//@Jason added simulation start message and time
		System.out.println("Simulation has started...");
		final long startSimTime = System.currentTimeMillis();
		System.out.println("The start time: "+startSimTime);
		System.out.println("The readable start time: "+LocalDateTime.now());

		msg2.setConversationId(conversationIDInitial);
		msg2.setOntology("Start SIM");
		send(msg2);
		System.out.println(getLocalName()+": Tweets have started...");

		myGui.disableList();

		setQueueSize(0);

		messagePassingTimes = new long[alltfidfserviceAgents.length];
		messagePassingCosts = new int[alltfidfserviceAgents.length];

		addBehaviour(new TickerBehaviour(this, 10) 
		{
			private static final long serialVersionUID = 1L;
			private int messagePassingTimesReceived;
			private int messagePassingCostReceived;
			private boolean messagePassingCompleted = false;
			
			protected void onTick() 
			{
				ACLMessage msg= myAgent.receive();

				if (messagePassingCostReceived == numNodes && messagePassingTimesReceived == numNodes && !messagePassingCompleted)
				{
					maxMessagePassingTime = 0;
					totalMessagePassingCost = 0;
					// for (int i = 0; i < messagePassingTimes.length; i++)
					for (int i = 0; i < messagePassingCosts.length; i++)
					{
						System.out.println("messagePassingTime["+i+"]: "+messagePassingTimes[i]);
						System.out.println("messagePassingCosts["+i+"]: "+messagePassingCosts[i]);
						if (messagePassingTimes[i] > maxMessagePassingTime)
						{
							maxMessagePassingTime = messagePassingTimes[i];
						}
						totalMessagePassingCost += messagePassingCosts[i];
					}

					System.out.println("maxMessagePassingTime: "+ maxMessagePassingTime + " ms");
					// System.out.println("totalMessagePassingTime: "+ maxMessagePassingTime + "bytes");
					System.out.println("totalMessagePassingCost: "+ totalMessagePassingCost + " bytes");
					
					myGui.addMessagePassingTime(messagePassingTimes);
					myGui.addMessagePassingTime(maxMessagePassingTime);
					
					myGui.addMessagePassingCost(messagePassingCosts);
					myGui.addMessagePassingCost(totalMessagePassingCost);

					BufferedWriter writer;
					try {
						writer = new BufferedWriter(new FileWriter("MessagePassingTimes.txt", true));
						writer.write("totalMessagePassingCost: "+ totalMessagePassingCost + " bytes Nodes: "+ messagePassingCosts.length);
						writer.newLine();
						writer.write("maxMessagePassingTime: "+ maxMessagePassingTime + " ms Nodes: "+ messagePassingTimes.length);
						writer.newLine();
						writer.flush();
						writer.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					messagePassingCompleted = true;
				}
				
				//Get message passing cost from each node if there is more than 1 node
				if (msg!=null && msg.getOntology() == "Message Passing Cost" && msg.getPerformative() == ACLMessage.INFORM)
				{
					
					messagePassingCostReceived++;
					System.out.println("StarterAgent Message Passing Cost Received: "+ Integer.parseInt(msg.getContent()));
					// if (alltfidfserviceAgents.length > 1)
					// {
						String recAgentName = msg.getSender().getLocalName();
						int recIndex = Integer.parseInt(recAgentName.split("(?=\\d*$)",2)[1])-1;
						// int recIndex = Integer.parseInt(recAgentName.substring(recAgentName.length()-1)) - 1;
						System.out.println(getLocalName()+" recIndex: "+recIndex);
						// messagePassingTimes[recIndex] = Long.valueOf(msg.getContent());
						messagePassingCosts[recIndex] = Integer.parseInt(msg.getContent());
					// }

				}
				
				if (msg!=null && msg.getOntology() == "Message Passing Time" && msg.getPerformative() == ACLMessage.INFORM)
				{
					messagePassingTimesReceived++;
					System.out.println("StarterAgent Message Passing Time Received: "+ Long.valueOf(msg.getContent()));
					// if (alltfidfserviceAgents.length > 1)
					// {
						String recAgentName = msg.getSender().getLocalName();
						int recIndex = Integer.parseInt(recAgentName.split("(?=\\d*$)",2)[1])-1;
						// int recIndex = Integer.parseInt(recAgentName.substring(recAgentName.length()-1)) - 1;
						messagePassingTimes[recIndex] = Long.valueOf(msg.getContent());
						// messagePassingTimes[recIndex] = Integer.parseInt(msg.getContent());
					// }

				}

				if (msg!=null && msg.getOntology() == "Tweeting Completed" && msg.getPerformative() == ACLMessage.INFORM)
				{
					//System.out.println(getLocalName()+" Tweeting Completed from: "+msg.getSender().getLocalName());
					ACLMessage reply = msg.createReply();
					reply.setPerformative( ACLMessage.REQUEST );
					reply.setContent("Stop Tweeting");
					reply.setOntology("Stop Tweeting");
					send(reply);

					numberofusers_counter++;

					//@Jason changed to numberofuserparticipated
					if(numberofusers_counter == numberofuserparticipated)
						//if(numberofusers_counter == numberofusers)
					{
						numberofusers_counter = 0;
						//@Jason changed to -1 to reuse counter in ontology: Querying Done from Organizing Agent below
						alltweetsflag = true;
						System.out.println(myAgent.getLocalName()+" TWEETING COMPLETED numberofusers: "+numberofusers);

						// System.out.println("STARTER AGENT BEFORE CALCULATE MESSAGE PASSING COST TIME");
						//Get the max message passing time from nodes if more than 1 node
						// if (alltfidfserviceAgents.length > 1)
						// {
							// maxMessagePassingTime = 0;
							// totalMessagePassingCost = 0;
							// for (int i = 0; i < messagePassingTimes.length; i++)
							// for (int i = 0; i < messagePassingCosts.length; i++)
							// {
								// System.out.println("messagePassingTime["+i+"]: "+messagePassingTimes[i]);
								// System.out.println("messagePassingCosts["+i+"]: "+messagePassingCosts[i]);
								// if (messagePassingTimes[i] > maxMessagePassingTime)
								// {
									// maxMessagePassingTime = messagePassingTimes[i];
								// }
								// totalMessagePassingCost += messagePassingCosts[i];
							// }

							// System.out.println("maxMessagePassingTime: "+ maxMessagePassingTime + "ms");
							// System.out.println("totalMessagePassingTime: "+ maxMessagePassingTime + "bytes");
							// System.out.println("totalMessagePassingCost: "+ totalMessagePassingCost + "bytes");
							
							// myGui.addMessagePassingTime(messagePassingTimes);
							// myGui.addMessagePassingTime(maxMessagePassingTime);
							
							// myGui.addMessagePassingCost(messagePassingCosts);
							// myGui.addMessagePassingCost(totalMessagePassingCost);

							// BufferedWriter writer;
							// try {
								// writer = new BufferedWriter(new FileWriter("MessagePassingTimes.txt", true));
								// writer.write("totalMessagePassingCost: "+ totalMessagePassingCost + "bytes Nodes: "+ messagePassingCosts.length);
								// writer.newLine();
								// writer.write("maxMessagePassingTime: "+ maxMessagePassingTime + "ms Nodes: "+ messagePassingTimes.length);
								// writer.newLine();
								// writer.flush();
								// writer.close();
							// } catch (IOException e) {
								// TODO Auto-generated catch block
								// e.printStackTrace();
							// }			


						// }

					}

				}

				//@Jason added remove user from total number of users msg from recommender agent
				if (msg!=null && msg.getOntology() == "Remove Users From Total" && msg.getPerformative() == ACLMessage.INFORM)
				{
					ArrayList<String> usersToRemove;
					try {
						usersToRemove = (ArrayList<String>)msg.getContentObject();
						numberofusers-=usersToRemove.size();

						for (int i = allUserAgentsList.size()-1; i >= 0; i--)
						{
							for (String nameToRemove : usersToRemove)
							{
								String suffixAgentName = "-UserAgent";
								if (allUserAgentsList.get(i).getLocalName().equals(nameToRemove+suffixAgentName))
								{
									allUserAgentsList.remove(i);
									break;
								}
							}
						}

						System.out.println(getLocalName()+" Received Remove Users From Total from: "+ msg.getSender().getLocalName());
						System.out.println(getLocalName()+" Removed User From Total Amount: "+usersToRemove.size());
						System.out.println(getLocalName()+" numberofusers: "+numberofusers);



						for (AID a : allUserAgentsList)
						{
							System.out.println(a.getLocalName());
						}


					} catch (UnreadableException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}




				/*
				//@Jason added remove user from total number of users msg from recommenderagent
				if (msg!=null && msg.getOntology() == "Remove This User From List of Agents" && msg.getPerformative() == ACLMessage.INFORM)
				{
					int indexToRemove = 0;

					for (int i = 0; i < allUserAgentsList.size(); i++){
						//System.out.println("allUserAgentsList.get(i).getLocalName(): "+allUserAgentsList.get(i).getLocalName()+"\tmsg.getContent(): "+msg.getContent());
						if (allUserAgentsList.get(i).getLocalName().equals(msg.getContent())){
							indexToRemove = i;
							break;
						}
					}

					allUserAgentsList.remove(indexToRemove);




				}
				 */
				//@Jason added wait for all recommender agents to finish text processing before calculating recommendations
				if (msg!=null && msg.getOntology() == "Text Processing Complete" && msg.getPerformative() == ACLMessage.INFORM)
				{
					numOfRecAgentsCount++;
					System.out.println(getLocalName()+" Text Processing Completed: "+numOfRecAgentsCount);

					//All recommender agents ready to cluster since text processing is completed
					if (numOfRecAgentsCount == alltfidfserviceAgents.length){

						//System.exit(0);
						
						//Send show followers message before clustering
						ACLMessage showFollowersMsg = new ACLMessage( ACLMessage.REQUEST );
						for(int i=0; i<allUserAgentsList.size(); i++)
						{
							showFollowersMsg.addReceiver(allUserAgentsList.get(i));  
						}	  	

						try {
							showFollowersMsg.setContentObject(allUserAgentsList);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						showFollowersMsg.setOntology("Show Followers");
						send(showFollowersMsg);

						int recUserInList = 0;
						usersRec = myGui.getUsersRec();

						for (String recUser : usersRec)
						{
							for (int i = 0; i < allUserAgentsList.size(); i++){

								String suffixAgentName = "-UserAgent";
								if (allUserAgentsList.get(i).getLocalName().equals(recUser+suffixAgentName))
								{
									recUserInList++;
									System.out.println(getLocalName()+" recUser: "+recUser+" recUserInList: "+recUserInList);
								}
							}

						}

						if (recUserInList == usersRec.size())
						{
							ACLMessage startRecMsg = new ACLMessage(ACLMessage.REQUEST);
							startRecMsg.setContent("Start Recommend Algorithms");
							startRecMsg.setOntology("Start Recommend Algorithms");

							for (int i=0; i < alltfidfserviceAgents.length; i++){
								String recAgentToSend = "Recommender-ServiceAgent"+(i+1);
								System.out.println(getLocalName()+ " Start Recommend Algorithm Msg Sent To: "+recAgentToSend);
								startRecMsg.addReceiver(new AID(recAgentToSend,AID.ISLOCALNAME));
								send(startRecMsg);
							}
						}
						else
						{
							for (String recUser : usersRec)
							{
								System.out.println("ERROR: RECOMMENDED USER " + recUser + " REMOVED FROM LIST OF USERS AFTER TEXT PROCESSING");
								myGui.appendResult("ERROR: RECOMMENDED USER " + recUser + " REMOVED FROM LIST OF USERS AFTER TEXT PROCESSING");
							}
							myGui.appendResult("Re-choose Recommendees");
							myGui.enableList();
						}
					}					
				}

				//Msg from rec agents
				if (msg!=null && msg.getOntology() == "Tweets TFIDF Algorithm Calculation Done" && msg.getPerformative() == ACLMessage.INFORM)
				{
					tfidfservercount++;
					System.out.println(getLocalName()+" received: Tweets TFIDF Algorithm Calculation Done");
					if(tfidfservercount == 1 && alltfidfserviceAgents.length > 1)
					{

						beginKmeansMergeTime = System.nanoTime();
					}
					if(tfidfservercount == alltfidfserviceAgents.length)
					{
						tfidfservercount = 0;
					}
				}
				if (msg!=null && msg.getOntology()=="Merge Lists Completed" && msg.getPerformative() == ACLMessage.INFORM)
				{

					System.out.println(getLocalName()+" received Merge Lists Completed");

					endKmeansMergeTime = System.nanoTime();

					if (alltfidfserviceAgents.length > 1)
					{
						kmeansMessageTime = endKmeansMergeTime - beginKmeansMergeTime;
						myGui.addKmeansMergeTime(kmeansMessageTime);
						myGui.addKmeansMergeTimeNano(kmeansMessageTime);
					}

					myGui.addTiming();
					usersRec = myGui.getUsersRec();

					ACLMessage msg2 = new ACLMessage( ACLMessage.REQUEST);
					String result = "requestedBy";					
					msg2.setContent(result);
					msg2.setOntology("Start Querying");

					//Send query to only users that are supposed to get recommendations

					int queryMessageCount = 0;
					for (int i = 0; i < allUserAgentsList.size(); i++){

						String suffixAgentName = "-UserAgent";
						for (String queryUserName : usersRec)
						{
							if (allUserAgentsList.get(i).getLocalName().equals(queryUserName+suffixAgentName))
							{
								queryMessageCount++;
								System.out.println(getLocalName()+" "+queryUserName+suffixAgentName+" queryMessageCount: "+queryMessageCount);
								msg2.addReceiver(allUserAgentsList.get(i));
								send(msg2);
							}
						}
						if (queryMessageCount == usersRec.size())
							break;
					}
				}

				//When user got its recommendation list
				if (msg!=null && msg.getOntology() == "Querying Done from Organizing Agent" && msg.getPerformative() == ACLMessage.INFORM) 
				{
					queryUserCounter++;
					numberofusers_counter++;
					//@Jason checking numberofusers_counter
					//System.out.println(myAgent.getLocalName()+ " numberofusers_counter: "+numberofusers_counter + "\tnumberofusers: "+numberofusers);
                      
					// Code added by Sepide 
                     /* try {
							 //Runtime.getRuntime().exec("cmd /c start cmd.exe /K \"gcc -o C:\\Users\\s2baniha\\Desktop\\important-stuff\\TXT2GMLv1.0 conver C:\\Users\\s2baniha\\Desktop\\important-stuff\\TXT2GMLv1.0 conversion.c");
                             //Runtime.getRuntime().exec("cmd /c start cmd.exe /K \"C:\\Users\\s2baniha\\Desktop\\important-stuff\\TXT2GMLv1.0\\conver C:\\Users\\s2baniha\\Desktop\\important-stuff\\edges-numbers");
							 Process p1 = Runtime.getRuntime().exec( "gcc -o C:\\Users\\s2baniha\\Desktop\\important-stuff\\TXT2GMLv1.0 conver C:\\Users\\s2baniha\\Desktop\\important-stuff\\TXT2GMLv1.0 conversion.c" );
							 Process p = Runtime.getRuntime().exec( "C:\\Users\\s2baniha\\Desktop\\important-stuff\\TXT2GMLv1.0\\conver C:\\Users\\s2baniha\\Desktop\\important-stuff\\edges-numbers" );  
							 
						 }
						 catch (Exception ex) {
                               ex.printStackTrace();
							   return;
                      
                        }  */

                    // End of code added by Sepide 					
					  
					System.out.println("queryUserCounter: "+queryUserCounter+" usersRec.size(): "+ usersRec.size());
					if (queryUserCounter == usersRec.size()) 				
						//if(numberofusers_counter == numberofusers)
					{
						numberofusers_counter = 0;
						queryUserCounter = 0;

						//@Jason timing whole execution
						final long endSimTime = System.currentTimeMillis();
						System.out.println("Simulation Completed");
						System.out.println("Final Total execution time: " + (endSimTime - startSimTime) + "ms" );
						System.out.println("Simulated ended at "+ LocalDateTime.now());
						
						myGui.enableAllButtons();
						myGui.enableList();
						//myGui.showMessageBox("finished simulation");    // commented out by Sepide
						
						// Code added by Sepide
                          /*try {
							 Runtime.getRuntime().exec("cmd /c start cmd.exe /K \"gcc -o C:\\Users\\s2baniha\\Desktop\\important-stuff\\TXT2GMLv1.0\\conver C:\\Users\\s2baniha\\Desktop\\important-stuff\\TXT2GMLv1.0\\conversion.c");
                             Runtime.getRuntime().exec("cmd /c start cmd.exe /K \"C:\\Users\\s2baniha\\Desktop\\important-stuff\\TXT2GMLv1.0\\conver C:\\Users\\s2baniha\\Desktop\\important-stuff\\edges-numbers");
							 //Process p1 = Runtime.getRuntime().exec( "gcc -o C:\\Users\\s2baniha\\Desktop\\important-stuff\\TXT2GMLv1.0 conver C:\\Users\\s2baniha\\Desktop\\important-stuff\\TXT2GMLv1.0 conversion.c" );
							 //Process p = Runtime.getRuntime().exec( "C:\\Users\\s2baniha\\Desktop\\important-stuff\\TXT2GMLv1.0\\conver C:\\Users\\s2baniha\\Desktop\\important-stuff\\edges-numbers" );  
							 
						 }
						 catch (Exception ex) {
                               ex.printStackTrace();
							   return;
                      
                         } */
						
						ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
						pc.newProject();
						AppearanceController appearanceController = Lookup.getDefault().lookup(AppearanceController.class);
						AppearanceModel appearanceModel = appearanceController.getModel();
						ImportController importController = Lookup.getDefault().lookup(ImportController.class);
						GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel();
						Workspace workspace = pc.getCurrentWorkspace();
						Container container;
						
						String importantStuffDirName = "important-stuff/";
						File importantStuffDir = new File(importantStuffDirName);
						if (!importantStuffDir.exists())
						{
								importantStuffDir.mkdirs();
						}
						
						try {
                               //File file = new File(getClass().getResource("C:\\Users\\Sepide\\Desktop\\project2\\94k_after2runSimulation.gml").toURI());
                               //Process p = Runtime.getRuntime().exec("C:/Users/s2baniha/Desktop/important-stuff/TXT2GMLv1.0/conver C:/Users/s2baniha/Desktop/important-stuff/edges-numbers");  
							   File file = new File(importantStuffDirName +"edges-numbers.gml");
							   //System.out.println("the gml file gets inputted in Gephi Software");
							   container = importController.importFile(file);
                               container.getLoader().setEdgeDefault(EdgeDirectionDefault.UNDIRECTED);   //Force DIRECTED
                               container.getLoader().setAllowAutoNode(false);  //Don't create missing nodes
                              } catch (Exception ex) {
                               ex.printStackTrace();
							   return;
                      
                        }
						
						     //Append imported data to GraphAPI
                             importController.process(container, new DefaultProcessor(), workspace);
							 
					         //See if graph is well imported
                             //UndirectedGraph graph = graphModel.getUndirectedGraph();
							 DirectedGraph graph = graphModel.getDirectedGraph();
                             System.out.println("Nodes: " + graph.getNodeCount());
                             System.out.println("Edges: " + graph.getEdgeCount());
							 
							 //Run modularity algorithm - community detection
                             Modularity modularity = new Modularity();
							 modularity.setResolution(1.0);
                             modularity.execute(graphModel);
							 
							//Partition with 'modularity_class', just created by Modularity algorithm
                            Column modColumn = graphModel.getNodeTable().getColumn(Modularity.MODULARITY_CLASS);
                            Function func2 = appearanceModel.getNodeFunction(graph, modColumn, PartitionElementColorTransformer.class);
                            Partition partition2 = ((PartitionFunction) func2).getPartition();
                            System.out.println(partition2.size() + " partitions found");
                            Palette palette2 = PaletteManager.getInstance().randomPalette(partition2.size());
                            partition2.setColors(palette2.getColors());
                            appearanceController.transform(func2);
							
							//Preview 
							PreviewModel model = Lookup.getDefault().lookup(PreviewController.class).getModel();
                            model.getProperties().putValue(PreviewProperty.SHOW_NODE_LABELS, Boolean.TRUE);
							model.getProperties().putValue(PreviewProperty.NODE_LABEL_FONT, model.getProperties().getFontValue(PreviewProperty.NODE_LABEL_FONT).deriveFont(8));
							model.getProperties().putValue(PreviewProperty.NODE_LABEL_PROPORTIONAL_SIZE, Boolean.FALSE);
							model.getProperties().putValue(PreviewProperty.NODE_LABEL_OUTLINE_SIZE, 12);
							
							//Export
                            ExportController ec = Lookup.getDefault().lookup(ExportController.class);
                              try {
                                ec.exportFile(new File(importantStuffDirName+"partition.pdf"));
                                } catch (IOException ex) {
                                   ex.printStackTrace();
                                   return;
                                }
								
								//Layout for 1 minute
								AutoLayout autoLayout = new AutoLayout(1, TimeUnit.MINUTES);
                                autoLayout.setGraphModel(graphModel);
								//YifanHuLayout secondLayout = new YifanHuLayout(null, new StepDisplacement(1f));
								ForceAtlasLayout firstLayout = new ForceAtlasLayout(null);
								//firstLayout.setRepulsionStrength(20000.00);
								//ForceAtlas fa = new ForceAtlas(); 
                                //fa.buildLayout();
								//AutoLayout.DynamicProperty adjustBySizeProperty = AutoLayout.createDynamicProperty("forceAtlas.adjustSizes.name", Boolean.TRUE, 0f);//True after 10% of layout time
                                AutoLayout.DynamicProperty repulsionProperty = AutoLayout.createDynamicProperty("forceAtlas.repulsionStrength.name", 20000., 0f);//500 for the complete period
								//autoLayout.addLayout(firstLayout, 0.0f, new AutoLayout.DynamicProperty[]{adjustBySizeProperty, repulsionProperty});
                                autoLayout.addLayout(firstLayout, 0.0f, new AutoLayout.DynamicProperty[]{repulsionProperty});
								ForceAtlas2 secondLayout = new ForceAtlas2(null);
								secondLayout.setLinLogMode(true);
								secondLayout.setAdjustSizes(true);
								secondLayout.setGravity(10d);
								secondLayout.setScalingRatio(30d);
								secondLayout.setEdgeWeightInfluence(0.2);
								System.out.println("Scaling Ratio of ForceAtlas2: " + secondLayout.getScalingRatio());
								//AutoLayout.DynamicProperty preventOver = AutoLayout.createDynamicProperty("forceAtlas2.preventOverlap.namePreventOverlap", Boolean.FALSE, 0f);
								//autoLayout.addLayout(secondLayout, 1f,new AutoLayout.DynamicProperty[]{preventOver});
								AutoLayout.DynamicProperty adjustBySizeProperty = AutoLayout.createDynamicProperty("ForceAtlas2.adjustSizes.name",Boolean.TRUE,0f);//True for the complete period
                                AutoLayout.DynamicProperty distrAttraction = AutoLayout.createDynamicProperty("ForceAtlas2.distributedAttraction.name",Boolean.TRUE,0f);//True for the complete period
                                autoLayout.addLayout(secondLayout, 1.0f, new AutoLayout.DynamicProperty[]{adjustBySizeProperty,distrAttraction});
								//autoLayout.addLayout(secondLayout, 1.0f);
								autoLayout.execute();
								
								//Export full graph
								ExportController ec3 = Lookup.getDefault().lookup(ExportController.class);
                                 try {
                                       ec3.exportFile(new File(importantStuffDirName+"graph.gexf"));
                                   } catch (IOException ex) {
                                       ex.printStackTrace();
                                     return;
									}
								
								
								
								//Export
                                  ExportController ec2 = Lookup.getDefault().lookup(ExportController.class);
                                  try {
                                   ec2.exportFile(new File(importantStuffDirName+"autolayout.pdf"));
                                    } catch (IOException ex) {
                                     ex.printStackTrace();
                                     }
									 
									 try {
										 
										PdfDocument pdf = new PdfDocument();
									 pdf.loadFromFile(importantStuffDirName+"autolayout.pdf");
									 
									BufferedImage image;

                                      for (int i = 0; i < pdf.getPages().getCount(); i++) {
                                           image = pdf.saveAsImage(i);
                                           File file = new File( String.format(importantStuffDirName+"layout.jpg", i));
                                         ImageIO.write(image, "png", file);
                                         }
                                        pdf.close();
									 }
									 catch (Exception e) {
                                                   System.out.println("ERROR: " + e.getMessage());
                                            }
																		
									 
									 /* try {
										 PDFDocument document = new PDFDocument();
										 document.load(new File("C:\\Users\\s2baniha\\Desktop\\important-stuff\\autolayout.pdf"));
									     SimpleRenderer renderer = new SimpleRenderer();
                                         
										 // set resolution (in DPI)
                                         renderer.setResolution(300);
										 List<Image> images = renderer.render(document);
										 
										 try {
											for (int i = 0; i < images.size(); i++) {
                                               ImageIO.write((RenderedImage) images.get(i), "PNJ", new File((i + 1) + ".png"));
                                           } 
										 }
										  catch (Exception e) {
                                                   System.out.println("ERROR: " + e.getMessage());
                                            }
								 
									}
									catch (Exception e) {
                                           System.out.println("ERROR: " + e.getMessage());
                                    }  */
									
									 try  
	                                 {  
	                                   //constructor of file class having file as argument  
	                                   File file = new File(importantStuffDirName+"layout.jpg");   
	                                   if(!Desktop.isDesktopSupported())//check if Desktop is supported by Platform or not  
	                                     {  
	                                             System.out.println("not supported");  
	                                             return;  
	                                     }  
	                                  Desktop desktop = Desktop.getDesktop();  
	                                  if(file.exists())         //checks file exists or not  
	                                  desktop.open(file);              //opens the specified file  
	                                   }  
	                                catch(Exception e)  
	                                 {  
	                                       e.printStackTrace();  
	                                }
						
						// End of code added by Sepide 
						
						// Code added by Sepide for automation of Simulator 
						
						myGui.showMessageBox("finished simulation");
						//if (myGui.indexToRecommend < myGui.listSize - 1){  // commented out on Jan 30, 2021
							//myGui.simulationButton.doClick();
						//}
						//else {
							//myGui.showMessageBox("finished simulation");
							//if (myGui.simulationSelectionBox.getSelectedIndex() <= 5 ) {
								//myGui.showAgentsList.setSelectedIndex(0);
								//myGui.simulationSelectionBox.setSelectedIndex(myGui.simulationSelectionBox.getSelectedIndex()+1);
								//TwitterGatherDataFollowers.userRyersonU.myGui.simulationSelectionListener simList = new TwitterGatherDataFollowers.userRyersonU.myGui.simulationSelectionListener();
								//myGui.simulationSelectionBox.addActionListener(myGui.simulationSelectionListener simList = new myGui.simulationSelectionListener());
								
							//} 
							//else {
								 //myGui.showMessageBox("finished simulation");
							//}
						//}    // commendted out on Jan 30, 2021
						//End of code added by Sepide 
						
						//@Jason added exit to save CPU from running forever doing nothing and causing an early death for the CPU's lifespan
						//System.exit(0);


					}
				}
				
				block();
			}
		});


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
