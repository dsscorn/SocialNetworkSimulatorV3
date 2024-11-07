package TwitterGatherDataFollowers.userRyersonU;
 

import java.awt.AWTException;
import java.awt.Robot;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.io.*;

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

import org.neuroph.core.NeuralNetwork;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.util.TransferFunctionType;
import org.neuroph.core.Layer;
import org.neuroph.core.Neuron;
import org.neuroph.core.Connection;
import org.neuroph.core.Weight;
import org.neuroph.core.transfer.TransferFunction;
import org.neuroph.contrib.learning.SoftMax;
import weka.classifiers.functions.MultilayerPerceptron;   //Sepide
import weka.core.Instances;  //Sepide
import weka.classifiers.Classifier;   //Sepide
import weka.classifiers.functions.neural.LinearUnit;   //Sepide
import weka.classifiers.functions.neural.NeuralConnection;   //Sepide
import weka.classifiers.functions.neural.NeuralNode;     //Sepide
import java.lang.reflect.*;   //Sepide 
//S import weka.classifiers.functions.neural;      //Sepide


public class OrganizingAgent extends Agent 
{
	private static final long serialVersionUID = 1L;

	private static final int COS_SIM = 0;
	private static final int K_MEANS = 1;
	private static final int SVM = 2;
	private static final int MLP = 3;
	public static final int Doc2Vec = 4;             // added by Sepide
    public static final int CommonNeighbors = 5;        // added by Sepide	
	public static final int K_MEANSEUCLIDEAN = 6;   // added by Sepide 
	
	private ArrayList<String> global_interestlistData = new ArrayList<String>();		

	private int numNodes = 0;
	private int count_contiuneTweeting_messages   = 0;
	private int count_contiuneTweeting_messages_2 = 0;

	private	int totalvectorstosend = 0;			
	private int numberofuserparticipated =0;

	private long a = 0;

	private AID[] allRecAgents;		
	private AID[] allUserAgents;
	private Agent thisAgent = this;

	private LinkedHashMap<String, AID> useragent_replyIDs = new LinkedHashMap<String, AID>(); 
	private long timestampstart =0;
	private AID   AID_agent_name;
	private String agent_name;
	private int numberofusers = 0;
	private int usercounter=0;

	private LinkedHashMap<String, String> user_is_connectedtothis_RecServer = new LinkedHashMap<String, String>();
	private LinkedHashMap<String, LinkedHashMap<String, Double>> ListofUsers_temp = new LinkedHashMap<String, LinkedHashMap<String, Double>>();
	private LinkedHashMap<String, Long> ListofUsers_for_time       = new LinkedHashMap<String, Long>();
	private LinkedHashMap<String, Long> ListofUsers_for_MaxServerProctime = new LinkedHashMap<String, Long>();

	private LinkedHashMap<String, Integer> user_respondedRecAgents = new LinkedHashMap<String, Integer>();
	private LinkedHashMap<String, Long> userRequestTimes = new LinkedHashMap<String, Long>();
	private LinkedHashMap<String, Long> userMsgPassTimes = new LinkedHashMap<String, Long>();

	//@Jason added algorithmRec 
	private int algorithmRec;

	private ArrayList<Map<String,TreeMap<String,Double>>> listOfScores = new ArrayList<Map<String,TreeMap<String,Double>>>();
	private Map<String,SortedSet<Map.Entry<String,Double>>> sortedAllUserScores = new TreeMap<String,SortedSet<Map.Entry<String,Double>>>();
	private int recMergeCount = 0;

	private int usersRequestedCount = 0;
	private int userInterestListSent = 0;

	transient protected ControllerAgentGui myGui;
	
	private List<MultiLayerPerceptron> listMLP;
	private List<NeuralNetwork> listNN;
	private int averageWeightCount;

	protected void setup() 
	{
		Object[] args = getArguments();


		numberofuserparticipated	 = (Integer) args[13];	  
		numNodes 		 = (Integer) args[15];

		//@Jason added algorithmRec argument
		algorithmRec = (Integer) args[16];


		myGui = (ControllerAgentGui) args[18];

		if (algorithmRec == MLP && numNodes > 1)
		{
			listMLP = new ArrayList<MultiLayerPerceptron>();
			listNN = new ArrayList<NeuralNetwork>();
			averageWeightCount = 0;
		}
			

		recMergeCount = 0;

		try {
			DFAgentDescription dfd = new DFAgentDescription();
			dfd.setName(getAID());
			ServiceDescription sd = new ServiceDescription();
			sd.setName("Distributed Recommender System");
			sd.setType("Organizing Agent");
			dfd.addServices(sd);
			DFService.register(this, dfd);
			System.out.println(getLocalName()+" REGISTERED WITH THE DF");
		} catch (FIPAException e) {
			e.printStackTrace();
		}		
		System.out.println("Hello! I am " + getAID().getName()+ " and is setup properly.");

		agent_name = getLocalName();
		AID_agent_name = getAID();

		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Recommender Agent");
		template.addServices(sd);
		try {
			DFAgentDescription[] result = DFService.search(this, template);
			allRecAgents = new AID[result.length];
			//allRecAgents = new AID[2];   // added by Sepide 
			for (int i = 0; i < result.length; ++i) {
				allRecAgents[i] = result[i].getName();
			}
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}


		DFAgentDescription template2 = new DFAgentDescription();
		ServiceDescription sd2 = new ServiceDescription();
		sd2.setType("User-Agent");
		template2.addServices(sd2);
		try {
			DFAgentDescription[] result = DFService.search(this, template2);
			allUserAgents = new AID[result.length];
			for (int i = 0; i < result.length; ++i) {
				allUserAgents[i] = result[i].getName();
			}
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}

		InitializeBehaviour initialBehaviour = new InitializeBehaviour(this);
		addBehaviour(initialBehaviour);
		
		setQueueSize(0);
	}

	private class InitializeBehaviour extends CyclicBehaviour {
		private static final long serialVersionUID = 1L;

		public InitializeBehaviour(Agent a) {
			super(a);
		}

		public void action() {
			ACLMessage msg = this.myAgent.receive();
			//Message from user agents
			if (msg!=null && msg.getOntology() == "Ready")
			{
//				++numberofusers;

//				String user_name1[] = msg.getSender().getLocalName().split("-",2);
//				String requestedBy = user_name1[0];
//
//				String ConnectedtoServer1[] = msg.getContent().split("Tweets and connected to ",2);
//				String ConnectedtoServer = ConnectedtoServer1[1];
//
//				if(!user_is_connectedtothis_RecServer.containsKey(requestedBy))
//				{
//					user_is_connectedtothis_RecServer.put(requestedBy, ConnectedtoServer);
//				}
				usercounter++;

				System.out.println(getLocalName()+" usercounter: "+usercounter+"\tnumberofuserparticipated: "+numberofuserparticipated);

				if(usercounter == numberofuserparticipated)
				{
					
//					DFAgentDescription template = new DFAgentDescription();
//					ServiceDescription sd = new ServiceDescription();
//					sd.setType("Recommender Agent");
//					template.addServices(sd);
//					try {
//						DFAgentDescription[] result = DFService.search(myAgent, template);
//						allRecAgents = new AID[result.length];
//						for (int i = 0; i < result.length; ++i) {
//							allRecAgents[i] = result[i].getName();
//						}
//					}
//					catch (FIPAException fe) {
//						fe.printStackTrace();
//					}
					
					while (allRecAgents.length < numNodes)
					{
						DFAgentDescription template = new DFAgentDescription();
						ServiceDescription sd = new ServiceDescription();
						sd.setType("Recommender Agent");
						template.addServices(sd);
						try {
							DFAgentDescription[] result = DFService.search(myAgent, template);
							allRecAgents = new AID[result.length];
							for (int i = 0; i < result.length; ++i) {
								allRecAgents[i] = result[i].getName();
							}
						}
						catch (FIPAException fe) {
							fe.printStackTrace();
						}
					}
					
					ACLMessage msg2 = new ACLMessage( ACLMessage.REQUEST );
					for(int i=0; i<allRecAgents.length; i++)
					{
						msg2.addReceiver(allRecAgents[i]);  
					}
					String result = "Update user list";				
					msg2.setContent(result);
					msg2.setOntology("Update Connected UserAgent List for this Rec Server");
					send(msg2);
					
					myGui.enableAllButtons();
					myGui.enableList();
					myGui.showMessageBox("initialize");
					
					System.out.println(getLocalName()+" Press Start Button");
					myGui.enableStartButton();
					myGui.testPrint();
				}


			}

			if (msg != null && msg.getOntology()== "Merge Lists")
			{
				
				recMergeCount++;
				Map<String,TreeMap<String,Double>> scoresFromRecAgent = null;
				Map<String,TreeMap<String,Double>> finalScores = new TreeMap<String,TreeMap<String,Double>>();

				System.out.println(getLocalName()+" received Merge Lists recMergeCount: "+recMergeCount);

				try {
					scoresFromRecAgent = (TreeMap<String,TreeMap<String,Double>>)msg.getContentObject();
					// System.out.println("scoresFromRecAgent: "+scoresFromRecAgent);
					listOfScores.add(scoresFromRecAgent);
				} catch (UnreadableException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				System.out.println("recMergeCount: "+recMergeCount+" allRecAgents.length: "+allRecAgents.length);
				 
				if (recMergeCount == allRecAgents.length)
				{
					//Merge lists when > 1 recAgents otherwise just stores into finalScores
					for (int i = 0; i < listOfScores.size(); i++)
					{
						for (String recUser : listOfScores.get(i).keySet())
						{
							TreeMap<String,Double> scoreOtherUsers = listOfScores.get(i).get(recUser);

							//System.out.println(getLocalName()+ " scoreOtherUsers: "+scoreOtherUsers);
							for(Map.Entry<String,Double> entry : scoreOtherUsers.entrySet()) 
							{
								String otherUser = entry.getKey();
								Double otherUserScore = entry.getValue();

								//System.out.println(otherUser + " => " + otherUserScore);
								if (finalScores.containsKey(recUser))
								{
									//Case for classifier scores in distributed nodes
									if (finalScores.get(recUser).containsKey(otherUser))
									{
										double oldScore = finalScores.get(recUser).get(otherUser);
										finalScores.get(recUser).put(otherUser, oldScore+otherUserScore);
										// System.out.println("FOUND "+otherUser+" AGAIN");
									}
									else
										finalScores.get(recUser).put(otherUser, otherUserScore);
									
									// System.out.println("ENTERED THIS JUNK HERERERERERERE "+getLocalName());
									// System.out.println(recUser+","+otherUser+","+otherUserScore);
								}
								else
								{
									TreeMap<String,Double> newScoreOtherUsers = new TreeMap<String,Double>();
									newScoreOtherUsers.put(otherUser, otherUserScore);
									finalScores.put(recUser, newScoreOtherUsers);
									// System.out.println("FIRST "+recUser+","+newScoreOtherUsers);
								}
							}
						}		
					}

					//System.out.println("finalScores: "+finalScores);
					
					try {
						FileWriter writer = new FileWriter("finalScores_Not_Normalized.txt", true); //append
						BufferedWriter bufferedWriter = new BufferedWriter(writer);
						
						for (String userRec: finalScores.keySet())
						{
							bufferedWriter.write(userRec+" Scores: [\t");
							
							TreeMap<String,Double> otherUserScores = finalScores.get(userRec);
							
							for (String otherUser: otherUserScores.keySet())
							{
								double oldScore = otherUserScores.get(otherUser);
								
								bufferedWriter.write(otherUser+":"+oldScore+"\t");				
							}
							
						}
						bufferedWriter.write("]");
						bufferedWriter.newLine();
						bufferedWriter.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					//Normalize the scores
					double maxScore;
					for (String userRec: finalScores.keySet())
					{

						TreeMap<String,Double> otherUserScores = finalScores.get(userRec);
						maxScore = Collections.max(otherUserScores.values());
						for (String otherUser: otherUserScores.keySet())
						{
							double oldScore = otherUserScores.get(otherUser);
							double newScore = 0.0;
							if (maxScore <= 0.0)
							{
								newScore = oldScore * 100;
							}
							else
							{
								newScore = (oldScore / maxScore) * 100;
							}
							
							otherUserScores.put(otherUser, newScore);					
						}
						finalScores.put(userRec, otherUserScores);
					}




					//Sort the scores descending order by score
					for (String userScore : finalScores.keySet()){
						//System.out.print(userScore+" scores: ");
						//System.out.println(entriesSortedByValues(finalScores.get(userScore)));
						sortedAllUserScores.put(userScore,entriesSortedByValues(finalScores.get(userScore)));
					}



					ACLMessage mergedMsg = new ACLMessage( ACLMessage.INFORM );

					mergedMsg.addReceiver(new AID("Starter Agent",AID.ISLOCALNAME));  

					mergedMsg.setContent("Can query now");
					mergedMsg.setOntology("Merge Lists Completed");
					send(mergedMsg);
				}		  	

			}

			//Message from user agent to get scores
			if (msg!=null && msg.getOntology() == "Get Score List")		
			{
				usersRequestedCount++;


				String requestedBy = msg.getSender().getLocalName().split("-",2)[0];
				

				System.out.println(getLocalName()+" Interest List requestedBy:" + requestedBy + " usersRequestedCount: "+usersRequestedCount);
                 
				 
				// This is the code causing the problem 
				 
				if(!userRequestTimes.containsKey(requestedBy))
				{
					userRequestTimes.put(requestedBy, (long) System.nanoTime());
				}

				ACLMessage msg2 = new ACLMessage( ACLMessage.REQUEST );
				msg2.addReceiver(msg.getSender());
				msg2.setOntology("Scores for User");



				LinkedHashMap<String,Double> userScores = new LinkedHashMap<String,Double>();
				Iterator<Entry<String,Double>> entries = sortedAllUserScores.get(requestedBy).iterator();
				

				 while (entries.hasNext())
				{
					Entry<String,Double> currentEntry = entries.next();
					String otherUser;
					Double otherUserScore;
					otherUser = currentEntry.getKey();
					otherUserScore = currentEntry.getValue();
					userScores.put(otherUser, otherUserScore);
				} 
				try {
					msg2.setContentObject(userScores);
					send(msg2);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	

			}

			//Message from user agent that user received score list
			if (msg!=null && msg.getOntology()=="Scores Received")
			{
				long messagePassTime;
				String requestedBy = msg.getSender().getLocalName().split("-",2)[0];
				messagePassTime = System.nanoTime() - userRequestTimes.get(requestedBy);
				userMsgPassTimes.put(requestedBy, messagePassTime);

				ACLMessage msg2 = new ACLMessage( ACLMessage.REQUEST );
				msg2.addReceiver(msg.getSender());
				msg2.setOntology("Stop Querying");
				msg2.setContent("Querying completed");
				send(msg2);

				ACLMessage queryDoneMsg = new ACLMessage( ACLMessage.INFORM );
				queryDoneMsg.addReceiver(new AID("Starter Agent",AID.ISLOCALNAME));  			
				queryDoneMsg.setContent("Query is done");
				queryDoneMsg.setOntology("Querying Done from Organizing Agent");
				send(queryDoneMsg);
			}
			
			//Message from multiple rec agents to average weight for general MLP
						
			if (msg!=null && msg.getOntology()=="Average Weights MLP")
			{   
				averageWeightCount++;
				MultiLayerPerceptron receivedMLP = null;
				NeuralNetwork receivedNN = null;
				
				//May-1 2023 MultilayerPerceptron receivedMLP = new MultilayerPerceptron();
				//System.out.println("test1 in organizing Agent file");
				//Sepide MultiLayerPerceptron receivedMLP = null;
				//Sepide NeuralNetwork receivedNN = null;

				System.out.println(getLocalName()+" received Average Weights MLP averageWeightCount: "+averageWeightCount);
				
				receivedMLP = (MultiLayerPerceptron) NeuralNetwork.createFromFile(msg.getContent());
				listMLP.add(receivedMLP);
				//Sepide SerializedClassifier classifier = new SerializedClassifier();
                //Sepide receivedMLP = classifier.setModelFile(new File(msg.getContent()));
				//Sepide Classifier receivedMLP = (Classifier) weka.core.SerializationHelper.read(new File(msg.getContent()));
				/*May-1 2023  try {
				receivedMLP = (MultilayerPerceptron) weka.core.SerializationHelper.read(msg.getContent());
				listMLP.add(receivedMLP);
				    }
				
				 catch (Exception e) {
                        e.printStackTrace();
                       }  */
				
				//System.out.println("test2 in organizing Agent file");
				// try {
					// receivedMLP = (MultiLayerPerceptron)msg.getContentObject();
					// listMLP.add(receivedMLP);
					// receivedNN = NeuralNetwork.createFromFile(msg.getContent());
					// listNN.add(receivedNN);
				// } catch (UnreadableException e) {
					// e.printStackTrace();
				// }
				

				System.out.println("averageWeightCount: "+averageWeightCount+" allRecAgents.length: "+allRecAgents.length);
				if (averageWeightCount == allRecAgents.length)
				{
					
					//May-1 2023 MultilayerPerceptron averagedMLP = listMLP.get(0);
					MultiLayerPerceptron averagedMLP = listMLP.get(0);
					// NeuralNetwork averagedNN = listNN.get(0);

					// System.out.println("listMLP.size(): "+listMLP.size());
					// System.out.println("listNN.size(): "+listNN.size());
										
					List<Layer> trainedLayers = listMLP.get(0).getLayers();
					//May-1 2023 int trainedLayers = 3;
					//System.out.println("test3 in organizing Agent file");
					// List<Layer> trainedLayers = listNN.get(0).getLayers();
					//May-1 2023 String hL = averagedMLP.getHiddenLayers();
					//May-1 2023 System.out.println("Number of nodes in Hidden Layer: "+ hL);
					
					//May-1 2023 char firsthL = hL.charAt(0);
					//May-1 2023 int a = Integer.parseInt(String.valueOf(firsthL));
					
					//S-recent for (int i=0; i < a; i++) {
						
						//S-recent myNeurons(averagedMLP);
						
						/* Field f =  MultilayerPerceptron.class.getDeclaredField("averagedMLP.m_neuralNodes");
						f.setAccessible(true);
						NeuralConnection[]  node = f.get(new MultilayerPerceptron());
						  
						//averagedMLP.m_neuralNodes;
											
						//NeuralConnection[] inputs = node.getInputs();
						
						
						 for (int j=0; j < inputs.length; j++) {
							 
							 
							  double averagedInputWeight = 0.0;
							  
							   for (MultilayerPerceptron currentNN : listMLP)
							   {
								   
								     NeuralConnection s = null;
							         double weights = s.weightValue(j);
							         averagedInputWeight += weights;
							   }
							   
							   averagedInputWeight /= listMLP.size();
							   System.out.println("averaged of weights by Sepide: " +averagedInputWeight);
							    // you have to put the code for updating the weights 
							  	   
						}*/
						 
						  
					//S-recent }
					for (int i = 0 ; i < trainedLayers.size(); i++)
						 // Sepide for (int i = 0 ; i < trainedLayers ; i++)
					  {
						// System.out.println("Layer "+i+": "+trainedLayers.get(i).getNeuronsCount());
						List<Neuron> neuronsInLayer = trainedLayers.get(i).getNeurons();
						//S NeuralNode nn = new NeuralNode();
						//List<NeuralNode> neuronsInLayer; 
						//RecommenderAgent rag = new RecommenderAgent();
                        //int uniqueWordsSize = rag.allUniqueDocTerms.size();
                        //Sepide int numNeurons = uniqueWordsSize + 10 + rag.datasetFollowees.size();
						//int numNeurons =  10;
                        //Sepide NeuralConnection[] temp1 = new NeuralConnection[numNeurons - 1];
						//NeuralConnection[] temp1;
                        //System.out.println("test3 in organizing Agent");						
						
						 for (int j = 0; j < neuronsInLayer.size(); j++)
							// for (int j = 0; j < numNeurons ; j++)
							
						{
							//No input connections in the input layer
							 if (i > 0)
								//if (temp1[j].getType() == NeuralConnection.INPUT)
							{
								List<Connection> inputConnections = neuronsInLayer.get(j).getInputConnections();
								//S List<NeuralConnection> inputConnections = neuronsInLayer.get(j).getInputConnections();
								// System.out.println("inputConnection weights Neuron "+j);
								 for (int k = 0; k < inputConnections.size(); k++)
									 //S for (int k = 0; k < neuronsInLayer.size(); k++) 
								    {
									// double inputWeight = inputConnections.get(k).getWeight().getValue();
									// System.out.print(inputWeight+"/");
																	
									double averagedInputWeight = 0.0;
									//System.out.println("test for in organizing Agent");
									
									for (MultiLayerPerceptron currentNN : listMLP)
									// for (NeuralNetwork currentNN : listNN)
									{								
										// averagedInputWeight+= currentNN.getLayers().get(i).getNeurons().get(j).getInputConnections().get(k).getWeight().getValue();
										averagedInputWeight+= currentNN.getLayers().get(i).getNeurons().get(j).getInputConnections().get(k).getWeight().getValue();
										//May-1 2023 averagedInputWeight+= temp1[j].weightValue(j);
										// if (k == inputConnections.size()-1 && j == neuronsInLayer.size()-1)
										// {
											// System.out.println(getLocalName()+" "+currentNN.getLayers().get(i).getNeurons().get(j).getInputConnections().get(k).getWeight().getValue());
										// }
									}
									averagedInputWeight /= listMLP.size();
									// averagedInputWeight /= listNN.size();
									
									// if (k == inputConnections.size()-1 && j == neuronsInLayer.size()-1)
									// {
										// System.out.println(getLocalName()+" averaged: "+averagedInputWeight);
									// }
									
									averagedMLP.getLayers().get(i).getNeurons().get(j).getInputConnections().get(k).setWeight(new Weight(averagedInputWeight));
									//May-1 2023 temp1[j].updateWeights(0.1, 0.2);
									// averagedNN.getLayers().get(i).getNeurons().get(j).getInputConnections().get(k).setWeight(new Weight(averagedInputWeight));
									
									// System.out.print(averagedInputWeight+" ");
								 }
								// System.out.println();
							}
							
							//No out connections in the output layer				
							 if (i < trainedLayers.size()-1 )
							 {
								// System.out.println("outConnection weights Neuron "+j);
								List<Connection> outConnections = neuronsInLayer.get(j).getOutConnections();
								//May-1 2023 NeuralConnection[] temp2 = new NeuralConnection[numNeurons - 1];
								for (int l = 0; l < outConnections.size(); l++)
									//for (int l = 0; l < numNeurons ; l++)
									//if  (temp2[l].getType() == NeuralConnection.OUTPUT)
								{
									// double outWeight = outConnections.get(l).getWeight().getValue();
									// System.out.print(outWeight+"/");
																		
									double averagedOutWeight = 0.0;
									
									for (MultiLayerPerceptron currentNN : listMLP)
									// for (NeuralNetwork currentNN : listNN)
									{										
										// System.out.println(getLocalName()+ " i: "+i+" j: "+j+" l: "+l);
										averagedOutWeight+= currentNN.getLayers().get(i).getNeurons().get(j).getOutConnections().get(l).getWeight().getValue();
										//May-1 2023 averagedOutWeight+= temp1[l].weightValue(l);
									}
									averagedOutWeight /= listMLP.size();
									// averagedOutWeight /= listNN.size();
									
									averagedMLP.getLayers().get(i).getNeurons().get(j).getOutConnections().get(l).setWeight(new Weight(averagedOutWeight));
									//May-1 2023 temp1[l].updateWeights(0.1, 0.2);
									// averagedNN.getLayers().get(i).getNeurons().get(j).getOutConnections().get(l).setWeight(new Weight(averagedOutWeight));
									// System.out.print(averagedOutWeight+" ");
									
								}
								// System.out.println();
							 }
							// System.out.println();
						}
						
					 }		
			
					String nnDirName = "Stored_NN/";
					// String averagedNNFileName = nnDirName+"averaged_NN_weights.nnet";
					// averagedNN.save(averagedNNFileName);
					String averagedMLPFileName = nnDirName+"averaged_MLP_weights.nnet";
					//May-1 2023 String averagedMLPFileName = nnDirName+getLocalName()+"_MLP.model";
					averagedMLP.save(averagedMLPFileName);
					/*May-1 2023  try {  
						weka.core.SerializationHelper.write(averagedMLPFileName, averagedMLP);
					}
					catch(Exception ex){
                        ex.printStackTrace();
                    }  */ 
			        
					ACLMessage averagedMsg = new ACLMessage( ACLMessage.INFORM );
					averagedMsg.setOntology("Averaged MLP Complete");
					System.out.println("test to see if Averaged MLP is complete");
					
					for(int i=0; i<allRecAgents.length; i++)
					{
						averagedMsg.addReceiver(allRecAgents[i]);  
					}
					
					// try
					// {
						// averagedMsg.setContentObject((Serializable) averagedMLP);
						// averagedMsg.setContent(averagedNNFileName);
						// send(averagedMsg);
					// }
					// catch (IOException e)
					// {
						// e.printStackTrace();
					// }
					 
					 averagedMsg.setContent(averagedMLPFileName);
					 send(averagedMsg);
					
				}		  	
			}


		}	
	}	


	//code from http://stackoverflow.com/questions/2864840/treemap-sort-by-value
	static <String,Double extends Comparable<? super Double>>
	SortedSet<Map.Entry<String,Double>> entriesSortedByValues(Map<String,Double> map) {
		SortedSet<Map.Entry<String,Double>> sortedEntries = new TreeSet<Map.Entry<String,Double>>(
				new Comparator<Map.Entry<String,Double>>() {
					public int compare(Map.Entry<String,Double> e1, Map.Entry<String,Double> e2) {
						//int res = e2.getValue().compareTo(e1.getValue());
						int res = e1.getValue().compareTo(e2.getValue());
						//return res != 0 ? res : 1;
						if (res > 0)
							return -1;
						else
							return 1;
					}
				}
				);
		sortedEntries.addAll(map.entrySet());
		return sortedEntries;
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
	
	/* public NeuralConnection[] myNeurons(MultilayerPerceptron mlp) {
							
		NeuralConnection[]  node = mlp.m_neuralNodes;
		return node;
							
	} */


}
