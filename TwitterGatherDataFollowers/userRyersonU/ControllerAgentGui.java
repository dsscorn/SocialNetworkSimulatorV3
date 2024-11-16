package TwitterGatherDataFollowers.userRyersonU;


import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.event.*;    // added by Sepide

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.UIManager.*;
import java.util.Collections;
import java.awt.event.*;

import jade.gui.*;


public class ControllerAgentGui extends JFrame implements ActionListener {

	private ControllerAgent myAgent;
	private StarterAgent myStarter;   // added by Sepide 

	private static final int FRAME_WIDTH = 1575;
	private static final int FRAME_HEIGHT = 670;	
	private static final int AREA_ROWS = 15;
	private static final int AREA_COLUMNS = 40;
	private static final int DEFAULT_K_REC = 3;
	private static final int HASH_TAGS = 1;
	private static final int RE_TWEETS = 1;
	private static final int STOP_WORDS = 1;
	private static final String BEGIN_DATE = "2007-01-01";
	// private static final String END_DATE = "2017-01-01";
	private static String END_DATE;
	private static final String DEFAULT_DATASET = "RyersonU";
	public static final int COS_SIM = 0;
	public static final int K_MEANS = 1;
	public static final int SVM = 2;
	public static final int MLP = 3;
	public static final int Doc2Vec = 4;
	public static final int CommonNeighbors = 5;        // added by Sepide
	public static final int K_MEANSEUCLIDEAN = 6;   // added by Sepide 
	public static final int zero = 0;               // added by Sepide
	public static final int one = 1;               // added by Sepide
	public static final int two = 2;               // added by Sepide
	public static final int three = 3;               // added by Sepide
	public static final int four = 4;                // added by Sepide
	public static final int FROM_DB = 1;
	public static final int FROM_TEXT = 0;
	public static final int FROM_GENERATION = 2;
	public static final int FROM_ARTIFICIAL = 3;
	public static final int FOLLOWEES = 1;
	public static final int FOLLOWERS = 2;
	public static final int DEFAULT_TWEETS_GENERATED = 3;
	public static final int DEFAULT_MAPPERS = 1;
	public static final int DEFAULT_REDUCERS = 1;
	public static final int DEFAULT_NUM_ARTIFICIAL_TWEETS = 0;
	public static final int FEATURE_EXTRACTION = 0;
	public static final int ALGORITHM_CLUSTERING = 1;
	public static final int CHANGE_TO_PERFORMANCE = 0;
	public static final int CHANGE_TO_USERSIM = 1;
	public static final int COLLECT_DATA = 4;
	public static final int START_USER_GEN_SIM =  5;
	public static final String USERSIM_PANEL_ID = "Change to User Sim";
	public static final String PERFORMANCE_PANEL_ID = "Performance Measurement";
	private int simulationNo;    // added by Sepide 
	private int sepNum = 0;   // added by Sepide  71 was the index for journal-authors
	public int listSize;

	public JComboBox<String> algorithmSelectionBox;
	private JComboBox<String> mapperSelectionBox;
	private JComboBox<String> reducerSelectionBox;
	public JComboBox<String> simulationSelectionBox;             // added by Sepide
	private JLabel enterDatasetLabel;
	private JLabel numNodesLabel;
	private JLabel tweetLimitLabel;
	private JLabel beginDateLabel;
	private JLabel endDateLabel;
	private JLabel recommendationLabel;
	private JLabel algorithmLabel;
	private JLabel numReducersLabel;
	private JLabel numMappersLabel;
	private JLabel reducerChoiceLabel;
	private JLabel mapperChoiceLabel;
	private JLabel labNameLabel;
	private JLabel userGenTitleLabel;
	private JLabel userGenProgressLabel;
	private JLabel performanceProgressLabel;
	private JLabel simulationNumber;      // added by Sepide
	private JTextField enterDatasetField;
	public JTextField numNodesField;
	private JTextField tweetLimitField;
	private JTextField beginDateField;
	private JTextField endDateField;
	public JTextField recommendationField;
	private JTextField numReducersField;
	private JTextField numMappersField;
	public JButton initializeButton;
	private JButton quitButton;
	public JButton simulationButton;  // added by Sepide 
	public JButton startButton;
	private JButton getUsersButton;
	private JButton changePerformanceButton;
	private JButton changeUserSimButton;
	private JButton helpUserSimButton;
	private JButton exitButton;
	private JButton dataCollectionButton;
	private JButton loadCorpusButton;
	private JButton setSimulatedTweetsButton;
	private JButton startUserSimButton;
	private JTextArea recommendationArea;
	private JTextArea resultArea;
	private JTextArea previousResultArea;
	private JTextArea clusterResultArea;
	private JTextArea userGenTweetsResultArea;
	private JTextArea grabTwitterResultArea;
	private TitledBorder agentsListTitle;
	private TitledBorder resultsTitle;
	private TitledBorder previousResultsTitle;
	private TitledBorder initializationTitle;
	private TitledBorder labNameTitle;
	private TitledBorder commandsTitle;
	private TitledBorder chooseDatasetTitle;
	private TitledBorder textProcessingTitle;
	private TitledBorder recommendationTitle;
	private TitledBorder clusterTitle;
	private TitledBorder userGenTweetsResultTitle;
	private TitledBorder grabTwitterResultTitle;
	private JCheckBox removeHashTags;
	private JCheckBox removeRetweets;
	private JCheckBox removeStopWords;
	private DefaultListModel<String> agentsList;
	public JList showAgentsList;
	private Border blackBorder;
	private JMenuItem fromTextMenu;
	private JMenuItem fromDbMenu;
	private JMenuItem fromGenerationMenu;
	private JMenuItem fromArtificialMenu;
	private JMenuItem helpPerformanceMenu;
	private JMenuItem helpUserGenSimMenu;
	private JPanel cards; //panel to hold the cards in card layout
	public JFileChooser fileChooser;  // private access changed to public by Sepide
	public ArrayList<String> usersRec = new ArrayList<String>(); //Users looking for recommendation  added by Sepide

	private ArrayList<Timings> timings;
	public Timings currentTiming;

	private String referenceUser;
	private String beginDate;
	private String endDate;
	private String recommendeeName; //Selected from list
	private String recommendeeName2; // added by Sepide
	private String nameFolloweesToGrab; //Followee name(s) separated by comma to get from Twitter API
	private int numNodes;
	private int kRecommend;
	private int hashTags;
	private int retweets;
	private int stopWords;
	private int simulationIteration;
	public int indexToRecommend;
	private int[] indexArray;  // added by Sepide
	//S private int[] indexToRecommend;   //added by Sepide
	private int indexToRecommend2;  // added by Sepide 
	private int tweetLimit;
	private int algorithmRec;
	private int numFollowers;
	private int numFollowees;
	private int numTweetsGenerated;
	private int numReducers;
	private int numMappers;
	private int reducerChoice;
	private int mapperChoice;
	private int numFollowersToGrab; //Number of followers to get from Twitter API
	private int numArtificialTweets; //Number of artificial tweets to generate
	private int[] a;   // added by Sepide
	private int[] b;   // added by Sepide
	private int[] c;   // added by Sepide
	private Object[] names;   // added by Sepide
	

	private int countRecServersTP;
	private int countRecServersTfidf;
	private int countRecServersAlgorithm;
	private double currentMaxTPTime;
	private double currentMaxTfidfTime;
	private double currentMaxAlgorithmTime;
	private long currentMessagePassingTime;
	private long currentMessagePassingCost;

	public ControllerAgentGui(ControllerAgent controller) {
		super("Multi-Agent System Simulator for Distributed Recommender System");

		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
			// If Nimbus is not available, you can set the GUI to another look and feel.
		}

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
		END_DATE = dateFormat.format(date);
		
		myAgent = controller;
		simulationIteration = 0;
		agentsList = new DefaultListModel();
		blackBorder = BorderFactory.createLineBorder(Color.BLACK, 1);
		numNodes = 1;
		tweetLimit = 0;
		numReducers = DEFAULT_REDUCERS;
		numMappers = DEFAULT_MAPPERS;
		beginDate = BEGIN_DATE;
		endDate = END_DATE;
		kRecommend = DEFAULT_K_REC;
		referenceUser = DEFAULT_DATASET;
		hashTags = HASH_TAGS;
		retweets = RE_TWEETS;
		stopWords = STOP_WORDS;
		algorithmRec = K_MEANS;
		numFollowees = FOLLOWEES;
		numFollowers = FOLLOWERS;
		numTweetsGenerated = DEFAULT_TWEETS_GENERATED;
		mapperChoice = FEATURE_EXTRACTION;
		reducerChoice = ALGORITHM_CLUSTERING;
		numArtificialTweets = DEFAULT_NUM_ARTIFICIAL_TWEETS;

		fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
		// Action details = fileChooser.getActionMap().get("viewTypeDetails");
		// details.actionPerformed(null);
		
		
		indexToRecommend = 0;
		indexToRecommend2 = 0;    // added by Sepide 
		timings = new ArrayList<Timings>();
		countRecServersTP = 0;
		countRecServersTfidf = 0;
		countRecServersAlgorithm = 0;
		currentMaxTPTime = 0;
		currentMaxTfidfTime = 0;
		currentMaxAlgorithmTime = 0;
		currentMessagePassingTime = 0;
		
		createTextAreas();
		createTextFields();
		createInitializationBoxes();
		createButtons();
		createList();
		createPanels();

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				shutDown();
			}
		});


		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double widthScreen = screenSize.getWidth();
		double heightScreen = screenSize.getHeight();

		if (widthScreen >= FRAME_WIDTH && heightScreen >= FRAME_HEIGHT)
		{
			setSize(FRAME_WIDTH, FRAME_HEIGHT);
		}
		else
		{
			double widthRatio = 0.0;
			double heightRatio = 0.0;

			if (widthScreen < FRAME_WIDTH && heightScreen < FRAME_HEIGHT)
			{
				widthRatio = widthScreen/FRAME_WIDTH;
				heightRatio = heightScreen/FRAME_HEIGHT;
			}
			else if (widthScreen < FRAME_WIDTH && heightScreen >= FRAME_HEIGHT)
			{
				widthRatio = widthScreen/FRAME_WIDTH;
				heightRatio = widthRatio;
			}
			else if (widthScreen >= FRAME_WIDTH && heightScreen < FRAME_HEIGHT)
			{
				heightRatio = heightScreen/FRAME_HEIGHT;
				widthRatio = heightRatio;
			}

			setSize((int) (FRAME_WIDTH * widthRatio), (int)(FRAME_HEIGHT * heightRatio + 75));
		}
		this.setResizable(true);
		
		//		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
	}

	private void createTextAreas()
	{
		resultArea = new JTextArea(AREA_ROWS, AREA_COLUMNS);
		resultArea.setBorder(blackBorder);
		resultArea.setText("");
		resultArea.setEditable(false);

		previousResultArea = new JTextArea(AREA_ROWS,AREA_COLUMNS);
		previousResultArea.setBorder(blackBorder);
		previousResultArea.setText("");
		previousResultArea.setEditable(false);

		recommendationArea = new JTextArea(10,AREA_COLUMNS);
		recommendationArea.setBorder(blackBorder);
		recommendationArea.setText("");
		recommendationArea.setEditable(false);
		recommendationArea.setFont(new Font("Arial",Font.BOLD,12));
		
		clusterResultArea = new JTextArea(AREA_ROWS,AREA_COLUMNS);
		clusterResultArea.setBorder(blackBorder);
		clusterResultArea.setText("");
		clusterResultArea.setEditable(false);
		
		userGenTweetsResultArea = new JTextArea(AREA_ROWS,AREA_COLUMNS);
		userGenTweetsResultArea.setBorder(blackBorder);
		userGenTweetsResultArea.setText("");
		userGenTweetsResultArea.setEditable(false);
		
		grabTwitterResultArea = new JTextArea(AREA_ROWS,AREA_COLUMNS);
		grabTwitterResultArea.setBorder(blackBorder);
		grabTwitterResultArea.setText("");
		grabTwitterResultArea.setEditable(false);
	}

	private void createTextFields()
	{
		final int FIELD_WIDTH = 10;

		enterDatasetLabel = new JLabel("Enter dataset: ");
		enterDatasetLabel.setForeground(Color.WHITE);
		enterDatasetLabel.setFont(new Font("Arial",Font.BOLD,12));
		enterDatasetField = new JTextField(FIELD_WIDTH);
		enterDatasetField.setText(DEFAULT_DATASET);
		enterDatasetField.setHorizontalAlignment(JTextField.CENTER);
		enterDatasetField.addActionListener(new GetReferenceListener());

		numNodesLabel = new JLabel("Number of Nodes: ");
		numNodesLabel.setHorizontalAlignment(JLabel.RIGHT);
		numNodesLabel.setForeground(Color.WHITE);
		numNodesLabel.setFont(new Font("Arial",Font.BOLD,12));
		numNodesField = new JTextField(FIELD_WIDTH);
		numNodesField.setText("1");
		numNodesField.setHorizontalAlignment(JTextField.CENTER);
		numNodesField.addActionListener(new NumNodesListener());

		tweetLimitLabel = new JLabel("Number of Latest Tweets: ");
		tweetLimitLabel.setHorizontalAlignment(JLabel.RIGHT);
		tweetLimitLabel.setForeground(Color.WHITE);
		tweetLimitLabel.setFont(new Font("Arial",Font.BOLD,12));
		tweetLimitField = new JTextField(FIELD_WIDTH);
		tweetLimitField.setText("0");
		tweetLimitField.setHorizontalAlignment(JTextField.CENTER);
		tweetLimitField.addActionListener(new TweetLimitListener());

		numMappersLabel = new JLabel("Number of Mappers: ");
		numMappersLabel.setHorizontalAlignment(JLabel.RIGHT);
		numMappersLabel.setForeground(Color.WHITE);
		numMappersLabel.setFont(new Font("Arial",Font.BOLD,12));
		numMappersField = new JTextField(FIELD_WIDTH);
		numMappersField.setText("1");
		numMappersField.setHorizontalAlignment(JTextField.CENTER);
		numMappersField.addActionListener(new NumMappersListener());

		numReducersLabel = new JLabel("Number of Reducers: ");
		numReducersLabel.setHorizontalAlignment(JLabel.RIGHT);
		numReducersLabel.setForeground(Color.WHITE);
		numReducersLabel.setFont(new Font("Arial",Font.BOLD,12));
		numReducersField = new JTextField(FIELD_WIDTH);
		numReducersField.setText("1");
		numReducersField.setHorizontalAlignment(JTextField.CENTER);
		numReducersField.addActionListener(new NumReducersListener());

		beginDateLabel = new JLabel("Begin date: ");
		beginDateLabel.setHorizontalAlignment(JLabel.RIGHT);
		beginDateLabel.setForeground(Color.WHITE);
		beginDateLabel.setFont(new Font("Arial",Font.BOLD,12));
		beginDateField = new JTextField(FIELD_WIDTH);
		beginDateField.setText(BEGIN_DATE);
		beginDateField.setHorizontalAlignment(JTextField.CENTER);
		beginDateField.addActionListener(new BeginDateListener());

		endDateLabel = new JLabel("End date: ");
		endDateLabel.setHorizontalAlignment(JLabel.RIGHT);
		endDateLabel.setForeground(Color.WHITE);
		endDateLabel.setFont(new Font("Arial",Font.BOLD,12));
		endDateField = new JTextField(FIELD_WIDTH);
		endDateField.setText(END_DATE);
		endDateField.setHorizontalAlignment(JTextField.CENTER);
		endDateField.addActionListener(new EndDateListener());

		recommendationLabel = new JLabel("Top Recommendations: ");
		recommendationLabel.setHorizontalAlignment(JLabel.RIGHT);
		recommendationLabel.setForeground(Color.WHITE);
		recommendationLabel.setFont(new Font("Arial",Font.BOLD,12));
		recommendationField = new JTextField(FIELD_WIDTH);
		recommendationField.setText("3");
		recommendationField.setHorizontalAlignment(JTextField.CENTER);
		recommendationField.addActionListener(new RecommendationListener());

		algorithmLabel = new JLabel("Algorithm: ");
		algorithmLabel.setForeground(Color.WHITE);
		algorithmLabel.setHorizontalAlignment(JLabel.RIGHT);
		algorithmLabel.setFont(new Font("Arial",Font.BOLD,12));
		
		simulationNumber = new JLabel("Iteration Number: ");       // added by Sepide 
		simulationNumber.setForeground(Color.WHITE);         // added by Sepide 
		simulationNumber.setHorizontalAlignment(JLabel.RIGHT);      // added by Sepide 
		simulationNumber.setFont(new Font("Arial",Font.BOLD,12));     // added by Sepide
		
		mapperChoiceLabel = new JLabel("Mapper Choice: ");
		mapperChoiceLabel.setForeground(Color.WHITE);
		mapperChoiceLabel.setHorizontalAlignment(JLabel.RIGHT);
		mapperChoiceLabel.setFont(new Font("Arial",Font.BOLD,12));

		reducerChoiceLabel = new JLabel("Reducer Choice: ");
		reducerChoiceLabel.setForeground(Color.WHITE);
		reducerChoiceLabel.setHorizontalAlignment(JLabel.RIGHT);
		reducerChoiceLabel.setFont(new Font("Arial",Font.BOLD,12));

		String[] algorithmsSelection = {"Similarity", "K-Means", "SVM", "MLP", "Doc2Vec", "Common-Neighbors", "K-meansEuclidean"};
		algorithmSelectionBox = new JComboBox<String>(algorithmsSelection);
		algorithmSelectionBox.setSelectedIndex(K_MEANS);
		((JLabel)algorithmSelectionBox.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
		algorithmSelectionBox.addActionListener(new AlgorithmSelectionListener());
		
		String[] simulationSelection = {"0", "1", "2", "3", "4", "5"};       // added by Sepide 
		simulationSelectionBox = new JComboBox<String>(simulationSelection);       // added by Sepide 
		simulationSelectionBox.setSelectedIndex(zero);          // added by Sepide 
		((JLabel)simulationSelectionBox.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);     // added by Sepide
		//simulationSelectionBox.addActionListener(new simulationSelectionListener());       // added by Sepide
	
		String[] mappersSelection = {"Feature Extraction", "Clustering"};
		mapperSelectionBox = new JComboBox<String>(mappersSelection);
		mapperSelectionBox.setSelectedIndex(FEATURE_EXTRACTION);
		((JLabel)mapperSelectionBox.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
		mapperSelectionBox.addActionListener(new MapperSelectionListener());

		String[] reducersSelection = {"Feature Extraction", "Clustering"};
		reducerSelectionBox = new JComboBox<String>(reducersSelection);
		reducerSelectionBox.setSelectedIndex(ALGORITHM_CLUSTERING);
		((JLabel)reducerSelectionBox.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
		reducerSelectionBox.addActionListener(new ReducerSelectionListener());
	}

	class AlgorithmSelectionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			System.out.println("algorithmSelectionBox.getSelectedIndex(): "+ algorithmSelectionBox.getSelectedIndex());
			algorithmRec = algorithmSelectionBox.getSelectedIndex();
		}
	}

	class MapperSelectionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			System.out.println("mapperSelectionBox.getSelectedIndex(): "+ mapperSelectionBox.getSelectedIndex());
			mapperChoice = mapperSelectionBox.getSelectedIndex();
			//			System.out.println("mapperChoice: "+mapperChoice+" reducerChoice: "+reducerChoice);
			if (reducerChoice == mapperChoice)
			{
				//				System.out.println("Entered mapper selection equals");
				if (reducerChoice == FEATURE_EXTRACTION)
				{
					mapperChoice = ALGORITHM_CLUSTERING;

				}
				else if (reducerChoice == ALGORITHM_CLUSTERING)
				{
					mapperChoice = FEATURE_EXTRACTION;
				}
				mapperSelectionBox.setSelectedIndex(mapperChoice);
			}
		}
	}
	
	// Added by Sepide 
	 /* class simulationSelectionListener implements ActionListener
		{
			
			public void actionPerformed(ActionEvent event)
				{
					System.out.println("simulationSelectionBox.getSelectedIndex(): "+ simulationSelectionBox.getSelectedIndex());
					simulationNo = simulationSelectionBox.getSelectedIndex();
					listSize = showAgentsList.getModel().getSize();
					
					while (sepNum < listSize) {
					indexToRecommend = sepNum;
					showAgentsList.setSelectedIndex(indexToRecommend);
					initializeAgents();
					sepNum++;
					
					
					break;
							
					}
						//for (int i=0; i<simulationNo; i++){
					startSimulation();
					return;
							
						//}
					
				}
		} */ 
      // End of code added by Sepide 
	  
	class ReducerSelectionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			System.out.println("reducerSelectionBox.getSelectedIndex(): "+ reducerSelectionBox.getSelectedIndex());
			reducerChoice = reducerSelectionBox.getSelectedIndex();
			//			System.out.println("mapperChoice: "+mapperChoice+" reducerChoice: "+reducerChoice);
			if (mapperChoice == reducerChoice)
			{
				//				System.out.println("Entered reducer selection equals");
				if (mapperChoice == FEATURE_EXTRACTION)
				{
					reducerChoice = ALGORITHM_CLUSTERING;

				}
				else if (mapperChoice == ALGORITHM_CLUSTERING)
				{
					reducerChoice = FEATURE_EXTRACTION;
				}
				reducerSelectionBox.setSelectedIndex(reducerChoice);
			}

		}
	}

	class GetReferenceListener implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			String enteredData = enterDatasetField.getText();
			if (!enteredData.equals(""))
			{
				referenceUser = enteredData;
				System.out.println("referenceUser: "+referenceUser);
			}
		}
	}

	class NumNodesListener implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			if (numNodesField.getText().equals("") || Integer.parseInt(numNodesField.getText()) < 1)
				numNodes = 1;
			else
				numNodes = Integer.parseInt(numNodesField.getText());

			System.out.println("numNodes: "+numNodes);

		}
	}

	class NumMappersListener implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			if (numMappersField.getText().equals("") || Integer.parseInt(numMappersField.getText()) < 1)
				numMappers = 1;
			else
				numMappers = Integer.parseInt(numMappersField.getText());

			System.out.println("numMappers: "+numMappers);

		}
	}

	class NumReducersListener implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			if (numReducersField.getText().equals("") || Integer.parseInt(numReducersField.getText()) < 1)
				numReducers = 1;
			else
				numReducers = Integer.parseInt(numReducersField.getText());

			System.out.println("numReducers: "+numReducers);

		}
	}

	//If blank, assume 1000 tweets limit
	class TweetLimitListener implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			if (tweetLimitField.getText().equals(""))
				tweetLimit = 1000;
			else
				tweetLimit = Integer.parseInt(tweetLimitField.getText());

			System.out.println("tweetLimit: "+tweetLimit);

		}
	}

	class BeginDateListener implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			if (beginDateField.getText().equals(""))
				beginDate = BEGIN_DATE;
			else
				beginDate = beginDateField.getText();

			System.out.println("beginDate: "+beginDate);

		}
	}

	class EndDateListener implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			if (endDateField.getText().equals(""))
				endDate = END_DATE;
			else
				endDate = endDateField.getText();

			System.out.println("endDate: "+endDate);

		}
	}

	class RecommendationListener implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			if (recommendationField.getText().equals("") || Integer.parseInt(recommendationField.getText()) < DEFAULT_K_REC)
				kRecommend = DEFAULT_K_REC;
			else
				kRecommend = Integer.parseInt(recommendationField.getText());

			System.out.println("kRecommend: "+kRecommend);

		}
	}

	private void createInitializationBoxes()
	{
		removeHashTags = new JCheckBox("Remove #'s");
		removeHashTags.setForeground(Color.WHITE);
		removeHashTags.setFont(new Font("Arial",Font.BOLD,12));
		removeHashTags.setSelected(true);
		removeHashTags.addItemListener(new RemoveBoxListener());

		removeRetweets = new JCheckBox("Remove RT's");
		removeRetweets.setForeground(Color.WHITE);
		removeRetweets.setFont(new Font("Arial",Font.BOLD,12));
		removeRetweets.setSelected(true);
		removeRetweets.addItemListener(new RemoveBoxListener());

		removeStopWords = new JCheckBox("Remove Stop Words");
		removeStopWords.setForeground(Color.WHITE);
		removeStopWords.setFont(new Font("Arial",Font.BOLD,12));
		removeStopWords.setSelected(true);
		removeStopWords.addItemListener(new RemoveBoxListener());
	}

	class RemoveBoxListener implements ItemListener
	{
		public void itemStateChanged(ItemEvent event)
		{
			int index = 0;
			Object source = event.getItemSelectable();
			if (source == removeHashTags)
			{
				index = 0;
				hashTags = HASH_TAGS;
			}
			else if (source == removeRetweets)
			{
				index = 1;
				retweets = RE_TWEETS;
			}
			else if (source == removeStopWords)
			{
				index = 2;
				stopWords = STOP_WORDS;
			}

			if (event.getStateChange() == ItemEvent.DESELECTED){
				if (index == 0)
					hashTags = 0;
				else if (index == 1)
					retweets = 0;
				else if (index == 2)
					stopWords = 0;

			}

			System.out.println("hashTags: "+hashTags+" retweets: "+retweets+ "stopWords: "+stopWords);
		}
	}


	private void createButtons()
	{
		initializeButton = new JButton("2. Initialize");
		initializeButton.setFont(new Font("Arial",Font.BOLD,12));
		initializeButton.addActionListener(this);

		startButton = new JButton("3. Run Simulation");
		startButton.setFont(new Font("Arial",Font.BOLD,12));
		disableStartButton();
		//		startButton.setVisible(false);
		startButton.addActionListener(this);

		quitButton = new JButton("Quit");
		quitButton.setFont(new Font("Arial",Font.BOLD,12));
		quitButton.addActionListener(this);
		
		// added by Sepide
		simulationButton = new JButton("Number of Simulations");
		simulationButton.setFont(new Font("Arial",Font.BOLD,12));
		//simulationButton.addActionListener(new simulationSelectionListener());
		// added by Sepide 

		getUsersButton = new JButton("1. Get Users");
		getUsersButton.setFont(new Font("Arial",Font.BOLD,12));
		getUsersButton.addActionListener(this);

		changePerformanceButton = new JButton("Performance Measurement");
		changePerformanceButton.setFont(new Font("Arial",Font.BOLD,12));
		changePerformanceButton.addActionListener(this);
		
		changeUserSimButton = new JButton("Return to User Sim");
		changeUserSimButton.setFont(new Font("Arial",Font.BOLD,12));
		changeUserSimButton.addActionListener(this);
		
		helpUserSimButton = new JButton("Help");
		helpUserSimButton.setFont(new Font("Arial",Font.BOLD,12));
		helpUserSimButton.addActionListener(this);
		
		dataCollectionButton = new JButton("1. Collect Data");
		dataCollectionButton.setFont(new Font("Arial",Font.BOLD,12));
		dataCollectionButton.addActionListener(this);
		
		loadCorpusButton = new JButton("2. Load A Corpus");
		loadCorpusButton.setFont(new Font("Arial",Font.BOLD,12));
		loadCorpusButton.addActionListener(this);
		
		setSimulatedTweetsButton = new JButton("3. Set Number Of Artificial Tweets");
		setSimulatedTweetsButton.setFont(new Font("Arial",Font.BOLD,12));
		setSimulatedTweetsButton.addActionListener(this);
		
		startUserSimButton = new JButton("4. Start User Sim");
		startUserSimButton.setFont(new Font("Arial",Font.BOLD,12));
		startUserSimButton.addActionListener(this);	
		
		exitButton = new JButton("Exit");
		exitButton.setFont(new Font("Arial",Font.BOLD,12));
		exitButton.addActionListener(this);		
	}

	private void createList()
	{
		showAgentsList = new JList(agentsList);
		/* S showAgentsList.setSelectionMode(
                     ListSelectionModel.MULTIPLE_INTERVAL_SELECTION); */  // added by Sepide to allow multiple users to be selected for Recommendation
		showAgentsList.setVisibleRowCount(28);
		showAgentsList.setPrototypeCellValue(String.format("%80s", ""));
		showAgentsList.setLayoutOrientation(JList.VERTICAL);
		//Only allow one user to be selected for recommendation
		showAgentsList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		showAgentsList.addListSelectionListener(new AddListListener());
		showAgentsList.setBorder(blackBorder);

	}

	class AddListListener implements ListSelectionListener
	{
		public void valueChanged(ListSelectionEvent event)
		{
			if (event.getValueIsAdjusting() == false) 
			{
				if (showAgentsList.getSelectedIndex() == -1) 
				{
					//No selection, disable start button.
					//					startButton.setEnabled(false);
					disableStartButton();
				}
				
				// Code added by Sepide 
				else if (simulationNo == one || simulationNo == two || simulationNo == three) {
					
					//algorithmRec = COS_SIM;
					enableStartButton();
					indexToRecommend = showAgentsList.getSelectedIndex();
					//recommendeeName = showAgentsList.getSelectedValue().toString();
					System.out.println("Please press the Initialize Button");
					System.out.println("Then Please press the Start Simulation Button");
					recommendeeName = showAgentsList.getSelectedValue().toString();
					// if (event.getSource() == simulationSelectionBox){
						// startSimulation(); 
					// }
					
					// simulationSelectionBox.addItemListener(new ItemListener(){
						// public void itemStateChanged(ItemEvent e){
							// System.out.println(e.getItem() + " " + e.getStateChange() );
							// startSimulation();
						// }
					// });
					
					//int selectedIndex = simulationSelectionBox.getSelectedIndex();
					
					 /* if (selectedIndex == 1) {
						  startSimulation();
					  }  */

					//for (int i=0; i< 10; i++)  {
						
						//usersRec = showAgentsList.selectValueAt(i);
						//usersRec.add(agentsList.selectValueAt(i));
						//indexToRecommend = 0;
						//showAgentsList.setSelectedIndex(indexToRecommend);
						
						//getUsersRec();
						
						//initializeAgents();
						//myStarter.setup();						
						
					//} 
					
				}
				// End of code added by Sepide 
				else 
				{
					//Selection, enable the start button.
					//					startButton.setEnabled(true);
					enableStartButton();
					//S indexArray = showAgentsList.getSelectedIndices();  // added by Sepide to get an array of selected indices for selected users 
					indexToRecommend = showAgentsList.getSelectedIndex();
					     //S indexToRecommend = indexArray[0];   // added by Sepideh
						 //S indexToRecommend2 = indexArray[1];   // added by Sepideh
					//S indexToRecommend2 = showAgentsList.getSelectedIndex();  // added by Sepide
					/*S names = showAgentsList.getSelectedValues();
					recommendeeName = names[0].toString();
					recommendeeName2 = names[1].toString();  */
					recommendeeName = showAgentsList.getSelectedValue().toString();
					//S recommendeeName2 = showAgentsList.getSelectedValue().toString();   // added by Sepide
				}
			}
		}
	}

	//Creates all the panels in the GUI and add to the frame
	private void createPanels()
	{
		/*setLayout(new BorderLayout());
	    //setContentPane(new JLabel(new ImageIcon("background_image_mas2.jpg")));
		setContentPane(new JLabel(new ImageIcon("ryerson_wallpaper_blue.jpg")));
	    setLayout(new FlowLayout());
		 */
		// getContentPane().setBackground(new Color(0,168,239));
		
		JLabel logo = (new JLabel(new ImageIcon("ryerson_logo_resized.png")));

		Border empty = BorderFactory.createEmptyBorder();

		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		// this.setJMenuBar(menuBar);
		fromTextMenu = new JMenuItem("Dataset From Text",KeyEvent.VK_T);
		fromTextMenu.addActionListener(this);
		fromDbMenu = new JMenuItem("Dataset From Database",KeyEvent.VK_D);
		fromDbMenu.addActionListener(this);
		fromGenerationMenu = new JMenuItem("Generated Dataset",KeyEvent.VK_G);
		fromGenerationMenu.addActionListener(this);
		fromArtificialMenu = new JMenuItem("Artificial Dataset",KeyEvent.VK_A);
		fromArtificialMenu.addActionListener(this);
		helpPerformanceMenu = new JMenuItem("Help",KeyEvent.VK_H);
		helpPerformanceMenu.addActionListener(this);
		fileMenu.add(fromTextMenu);
		fileMenu.add(fromDbMenu);
		fileMenu.add(fromGenerationMenu);
		fileMenu.add(fromArtificialMenu);
		fileMenu.add(helpPerformanceMenu);
		
		// JMenuBar menuBarUserGenSim = new JMenuBar();
		// JMenu fileMenuUserGenSim = new JMenu("File");
		// menuBarUserGenSim.add(fileMenuUserGenSim);
		// helpUserGenSimMenu = new JMenuItem("Help",KeyEvent.VK_H);
		// helpUserGenSimMenu.addActionListener(this);
		// fileMenuUserGenSim.add(helpUserGenSimMenu);
	
		JPanel mainPanel = new JPanel();
		mainPanel.setOpaque(true);
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(menuBar,BorderLayout.NORTH);
		mainPanel.setBackground(new Color(0,168,239));
		
		JPanel userGenMainPanel = new JPanel();
		userGenMainPanel.setOpaque(true);
		userGenMainPanel.setLayout(new BorderLayout());
		// userGenMainPanel.add(menuBarUserGenSim,BorderLayout.PAGE_START);
		userGenMainPanel.setBackground(new Color(0,168,239));
				
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
		centerPanel.setOpaque(false);
		JPanel resultsPanel = new JPanel();
		resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
		resultsPanel.setOpaque(false);
		JPanel inputDbPanel = new JPanel();
		inputDbPanel.setOpaque(false);
		JPanel commandsPanel = new JPanel(new GridLayout(2,2));
		commandsPanel.setOpaque(false);
		JPanel labNamePanel = new JPanel();
		labNamePanel.setOpaque(false);
		JPanel labNamePanel2 = new JPanel();
		labNamePanel2.setLayout(new BoxLayout(labNamePanel2, BoxLayout.Y_AXIS));
		labNamePanel2.setOpaque(false);
		JPanel initializationsPanel = new JPanel(new GridLayout(4,2));        // (3,2) changed to (4,2) by Sepide
		initializationsPanel.setOpaque(false);
		JPanel textProcessingPanel = new JPanel();
		textProcessingPanel.setOpaque(false);
		JPanel userGenOptionsPanel = new JPanel();
		userGenOptionsPanel.setLayout(new BoxLayout(userGenOptionsPanel, BoxLayout.Y_AXIS));
		userGenOptionsPanel.setOpaque(false);
		JPanel userGenResultsPanel = new JPanel();
		userGenResultsPanel.setLayout(new BoxLayout(userGenResultsPanel, BoxLayout.Y_AXIS));
		userGenResultsPanel.setOpaque(false);
		JPanel userGenPanel = new JPanel();
		userGenPanel.setLayout(new BoxLayout(userGenPanel, BoxLayout.X_AXIS));
		userGenPanel.setBackground(new Color(0,168,239));
		userGenPanel.setOpaque(false);
		JPanel userGenProgressPanel = new JPanel();
		userGenProgressPanel.setOpaque(false);
		JPanel progressPanel = new JPanel();
		progressPanel.setOpaque(false);		
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
		centerPanel.setOpaque(false);
		JScrollPane resultScrollPane = new JScrollPane(resultArea);
		resultScrollPane.setOpaque(false);
		//resultScrollPane.setBackground(new Color(0,0,0,0));
		JScrollPane previousResultScrollPane = new JScrollPane(previousResultArea);
		previousResultScrollPane.setOpaque(false);
		//previousResultScrollPane.setBackground(new Color(0,0,0,0));
		JScrollPane dataPane = new JScrollPane(showAgentsList);
		dataPane.setOpaque(false);
		//dataPane.setBackground(new Color(0,0,0,0));
		JScrollPane recommendationPane = new JScrollPane(recommendationArea);
		recommendationPane.setPreferredSize(new Dimension(300,125));
		recommendationPane.setMaximumSize(new Dimension(500,150));
		recommendationPane.setOpaque(false);
		JScrollPane userGenTweetsScrollPane = new JScrollPane(userGenTweetsResultArea);
		userGenTweetsScrollPane.setMaximumSize(new Dimension(600,750));
		userGenTweetsScrollPane.setOpaque(false);
		JScrollPane clusterResultScrollPane = new JScrollPane(clusterResultArea);
		clusterResultScrollPane.setMaximumSize(new Dimension(600,750));
		clusterResultScrollPane.setOpaque(false);
		JScrollPane grabTwitterResultScrollPane = new JScrollPane(grabTwitterResultArea);
		grabTwitterResultScrollPane.setMaximumSize(new Dimension(750,750));
		grabTwitterResultScrollPane.setOpaque(false);

		agentsListTitle = BorderFactory.createTitledBorder(empty,"List of Users");
		agentsListTitle.setTitleJustification(TitledBorder.CENTER);
		agentsListTitle.setTitleFont(new Font("Arial",Font.BOLD,20));
		agentsListTitle.setTitleColor(Color.WHITE);
		chooseDatasetTitle = BorderFactory.createTitledBorder(empty,"Dataset Chooser");
		chooseDatasetTitle.setTitleJustification(TitledBorder.CENTER);
		chooseDatasetTitle.setTitleFont(new Font("Arial",Font.BOLD,20));
		chooseDatasetTitle.setTitleColor(Color.WHITE);
		initializationTitle = BorderFactory.createTitledBorder(empty,"Performance Measurement of Simulated Recommender System");
		initializationTitle.setTitleJustification(TitledBorder.CENTER);
		initializationTitle.setTitleFont(new Font("Arial",Font.BOLD,20));
		initializationTitle.setTitleColor(Color.WHITE);
		labNameTitle = BorderFactory.createTitledBorder(empty,"DSMP Lab");
		labNameTitle.setTitleJustification(TitledBorder.CENTER);
		labNameTitle.setTitleFont(new Font("Arial",Font.PLAIN,20));
		labNameTitle.setTitleColor(Color.WHITE);
		commandsTitle = BorderFactory.createTitledBorder(empty,"Commands");
		commandsTitle.setTitleJustification(TitledBorder.CENTER);
		commandsTitle.setTitleFont(new Font("Arial",Font.BOLD,20));
		commandsTitle.setTitleColor(Color.WHITE);
		resultsTitle = BorderFactory.createTitledBorder(empty,"Timing Results");
		resultsTitle.setTitleJustification(TitledBorder.CENTER);
		resultsTitle.setTitleFont(new Font("Arial",Font.BOLD,20));
		resultsTitle.setTitleColor(Color.WHITE);
		previousResultsTitle = BorderFactory.createTitledBorder(empty,"Previous Timing Results");
		previousResultsTitle.setTitleJustification(TitledBorder.CENTER);
		previousResultsTitle.setTitleFont(new Font("Arial",Font.BOLD,20));
		previousResultsTitle.setTitleColor(Color.WHITE);
		textProcessingTitle = BorderFactory.createTitledBorder(empty,"Text Processing Parameters"); 
		textProcessingTitle.setTitleJustification(TitledBorder.CENTER);
		textProcessingTitle.setTitleFont(new Font("Arial",Font.BOLD,20));
		textProcessingTitle.setTitleColor(Color.WHITE);
		recommendationTitle = BorderFactory.createTitledBorder(empty,"Recommendations for User");
		recommendationTitle.setTitleJustification(TitledBorder.CENTER);
		recommendationTitle.setTitleFont(new Font("Arial",Font.BOLD,20));
		recommendationTitle.setTitleColor(Color.WHITE);
		userGenTweetsResultTitle = BorderFactory.createTitledBorder(empty,"Artificial Tweets");
		userGenTweetsResultTitle.setTitleJustification(TitledBorder.CENTER);
		userGenTweetsResultTitle.setTitleFont(new Font("Arial",Font.BOLD,20));
		userGenTweetsResultTitle.setTitleColor(Color.WHITE);
		clusterTitle = BorderFactory.createTitledBorder(empty,"Homophily Validation");
		clusterTitle.setTitleJustification(TitledBorder.CENTER);
		clusterTitle.setTitleFont(new Font("Arial",Font.BOLD,20));
		clusterTitle.setTitleColor(Color.WHITE);
		grabTwitterResultTitle = BorderFactory.createTitledBorder(empty,"Twitter Gathering Results");
		grabTwitterResultTitle.setTitleJustification(TitledBorder.CENTER);
		grabTwitterResultTitle.setTitleFont(new Font("Arial",Font.BOLD,20));
		grabTwitterResultTitle.setTitleColor(Color.WHITE);

		dataPane.setBorder(agentsListTitle);
		resultScrollPane.setBorder(resultsTitle);
		previousResultScrollPane.setBorder(previousResultsTitle);
		textProcessingPanel.setBorder(textProcessingTitle);
		recommendationPane.setBorder(recommendationTitle);
		userGenTweetsScrollPane.setBorder(userGenTweetsResultTitle);
		clusterResultScrollPane.setBorder(clusterTitle);
		grabTwitterResultScrollPane.setBorder(grabTwitterResultTitle);

		/*
		inputDbPanel.add(enterDatasetLabel);
		inputDbPanel.add(enterDatasetField);
		inputDbPanel.setBorder(chooseDatasetTitle);
		 */
		inputDbPanel.add(logo);

		labNameLabel = new JLabel("DSMP Lab: Dr. Abdolreza Abhari, Sepideh Banihashemi");
		labNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		labNameLabel.setForeground(Color.WHITE);
		labNameLabel.setFont(new Font("Arial",Font.PLAIN,20));
		
		userGenTitleLabel = new JLabel("TWITTER USER GENERATION SIMULATION");
		userGenTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		userGenTitleLabel.setForeground(Color.WHITE);
		userGenTitleLabel.setFont(new Font("Arial",Font.BOLD,20));
		
		userGenProgressLabel = new JLabel("Current Progress: ");
		userGenProgressLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		userGenProgressLabel.setForeground(Color.WHITE);
		userGenProgressLabel.setFont(new Font("Arial",Font.PLAIN,14));
		
		performanceProgressLabel = new JLabel("Current Progress: ");
		performanceProgressLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		performanceProgressLabel.setForeground(Color.WHITE);
		performanceProgressLabel.setFont(new Font("Arial",Font.PLAIN,14));
		
		labNamePanel.setBorder(labNameTitle);
		labNamePanel2.add(labNameLabel);
		labNamePanel2.add(userGenTitleLabel);

		initializationsPanel.add(numNodesLabel);
		initializationsPanel.add(numNodesField);
		initializationsPanel.add(tweetLimitLabel);
		initializationsPanel.add(tweetLimitField);
		// initializationsPanel.add(numMappersLabel);
		// initializationsPanel.add(numMappersField);
		// initializationsPanel.add(numReducersLabel);
		// initializationsPanel.add(numReducersField);	
		// initializationsPanel.add(mapperChoiceLabel);
		// initializationsPanel.add(mapperSelectionBox);
		// initializationsPanel.add(reducerChoiceLabel);
		// initializationsPanel.add(reducerSelectionBox);
		initializationsPanel.add(beginDateLabel);
		initializationsPanel.add(beginDateField);
		initializationsPanel.add(endDateLabel);
		initializationsPanel.add(endDateField);
		initializationsPanel.add(recommendationLabel);
		initializationsPanel.add(recommendationField);
		initializationsPanel.add(algorithmLabel);
		initializationsPanel.add(algorithmSelectionBox);
		initializationsPanel.add(simulationNumber);      // added by Sepide
		initializationsPanel.add(simulationSelectionBox);        // added by Sepide
		initializationsPanel.setBorder(initializationTitle);

		textProcessingPanel.add(removeHashTags);
		textProcessingPanel.add(removeRetweets);
		textProcessingPanel.add(removeStopWords);
		
		commandsPanel.add(getUsersButton);
		commandsPanel.add(initializeButton);
		commandsPanel.add(startButton);
		commandsPanel.add(quitButton);
		commandsPanel.add(simulationButton);   // added by Sepide 
		commandsPanel.add(changeUserSimButton);
		commandsPanel.setBorder(commandsTitle);

		userGenOptionsPanel.add(helpUserSimButton);
		userGenOptionsPanel.add(dataCollectionButton);
		userGenOptionsPanel.add(loadCorpusButton);
		userGenOptionsPanel.add(setSimulatedTweetsButton);
		userGenOptionsPanel.add(startUserSimButton);
		userGenOptionsPanel.add(changePerformanceButton);
		userGenOptionsPanel.add(exitButton);
		
		userGenResultsPanel.add(userGenTweetsScrollPane);
		userGenResultsPanel.add(clusterResultScrollPane);
		
		userGenProgressPanel.add(userGenProgressLabel);
		
		progressPanel.add(performanceProgressLabel);
		
		//centerPanel.add(inputDbPanel);
		Dimension maxSize = new Dimension(Short.MAX_VALUE, 100);

		centerPanel.add(labNamePanel);
		centerPanel.add(new Box.Filler(new Dimension(10,10), new Dimension(10,10), maxSize));
		centerPanel.add(initializationsPanel);
		centerPanel.add(new Box.Filler(new Dimension(10,10), new Dimension(10,10), maxSize));
		centerPanel.add(textProcessingPanel);
		centerPanel.add(new Box.Filler(new Dimension(10,20), new Dimension(10,20), new Dimension(30,20)));
		centerPanel.add(recommendationPane);
		centerPanel.add(new Box.Filler(new Dimension(10,20), new Dimension(10,20), maxSize));
		centerPanel.add(commandsPanel);

		resultsPanel.add(resultScrollPane);
		resultsPanel.add(previousResultScrollPane);

		mainPanel.add(dataPane,BorderLayout.WEST);
		mainPanel.add(centerPanel,BorderLayout.CENTER);
		mainPanel.add(resultsPanel,BorderLayout.EAST);
		mainPanel.add(progressPanel,BorderLayout.SOUTH);
		
		userGenPanel.add(userGenOptionsPanel);
		userGenPanel.add(grabTwitterResultScrollPane);
		
		userGenMainPanel.add(labNamePanel2,BorderLayout.NORTH);
		userGenMainPanel.add(userGenResultsPanel,BorderLayout.CENTER);
		userGenMainPanel.add(userGenPanel,BorderLayout.WEST);
		userGenMainPanel.add(userGenProgressPanel,BorderLayout.SOUTH);
				
		cards = new JPanel(new CardLayout());
		cards.add(userGenMainPanel,USERSIM_PANEL_ID);
		cards.add(mainPanel,PERFORMANCE_PANEL_ID);
		
		add(cards);
		// add(mainPanel);

	}

	public void actionPerformed(ActionEvent event) {

		if (event.getSource() == quitButton || event.getSource() == exitButton)
		{
			shutDown();
		}	
		else if (event.getSource() == getUsersButton)
		{
			getUsers();
		}
		else if (event.getSource() == initializeButton)
		{
			initializeAgents();
		}
		else if (event.getSource() == startButton)
		{
			startSimulation();
		}
		else if (event.getSource() == dataCollectionButton)
		{
			System.out.println("DATA COLLECTION");
			selectTwitterOptions();
		}
		else if (event.getSource() == loadCorpusButton)
		{
			System.out.println("LOAD A CORPUS");
			selectCorpusFile();
		}
		else if (event.getSource() == setSimulatedTweetsButton)
		{
			System.out.println("SETTING SIZE OF SIMULATED TWEETS");
			selectNumArtificialTweets();
		}		
		else if (event.getSource() == startUserSimButton)
		{
			System.out.println("STARTING USER SIM");
			startUserGenSimulation();
		}
		else if (event.getSource() == changePerformanceButton)
		{
			changeCardPanel(CHANGE_TO_PERFORMANCE);
		}
		else if (event.getSource() == changeUserSimButton)
		{
			changeCardPanel(CHANGE_TO_USERSIM);
		}
		else if (event.getSource() == helpUserSimButton)
		{
			helpUserSimText();
		}
		else if (event.getSource() == fromTextMenu)
		{
			selectFile(FROM_TEXT);
			System.out.println("fromTextMenu");
		}
		else if (event.getSource() == fromDbMenu)
		{
			myAgent.setReadFrom(FROM_DB);
			System.out.println("fromDbMenu");
		}
		else if (event.getSource() == fromGenerationMenu)
		{
			selectNumFolloweesFollowers();
			System.out.println("fromGenerationMenu");
		}
		else if (event.getSource() == fromArtificialMenu)
		{
			myAgent.setReadFrom(FROM_ARTIFICIAL);
			System.out.println("fromArtificialMenu");
		}
		else if (event.getSource() == helpPerformanceMenu)
		{
			helpPerformanceText();
			System.out.println("helpPerformanceMenu");
		}
	}

	public void noFileSelected()
	{
		JOptionPane.showMessageDialog(this,"No file was selected. Please select a file before proceeding in simulation!","No File Selected", JOptionPane.INFORMATION_MESSAGE);
	}
	
	//performance measurement file chooser
	public void selectFile(int fileOption)
	{
		// JFileChooser fileChooser = new JFileChooser();
		// fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
		int result = fileChooser.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			myAgent.setFile(selectedFile);
			System.out.println("Selected file: " + selectedFile.getAbsolutePath());
			JOptionPane.showMessageDialog(this,"Loaded File: "+selectedFile.getName(),"File Selected", JOptionPane.INFORMATION_MESSAGE);
		}
		
		if (fileOption == FROM_TEXT)
			myAgent.setReadFrom(FROM_TEXT);			
	}
	
	//user gen sim corpus chooser
	public void selectCorpusFile()
	{
		// JFileChooser fileChooser = new JFileChooser();
		// fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
		int result = fileChooser.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			myAgent.setCorpusGenFile(selectedFile);
			System.out.println("Selected corpus file: " + selectedFile.getAbsolutePath()); 
			JOptionPane.showMessageDialog(this,"Loaded File: "+selectedFile.getName(),"File Selected", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	public void selectNumFolloweesFollowers()
	{
		JTextField followeesField = new JTextField(5);
		JTextField followersField = new JTextField(5);
		JTextField tweetsGeneratedField = new JTextField(9);

		JPanel myPanel = new JPanel();
		myPanel.add(new JLabel("Number of Followees: "));
		myPanel.add(followeesField);
		myPanel.add(Box.createHorizontalStrut(15)); // a spacer
		myPanel.add(new JLabel("Number of Followers: "));
		myPanel.add(followersField);
		myPanel.add(Box.createHorizontalStrut(15)); // a spacer
		myPanel.add(new JLabel("Number of Tweets: "));
		myPanel.add(tweetsGeneratedField);

		int result = JOptionPane.showConfirmDialog(null, myPanel, 
				"Please Enter Followee, Follower, Total Tweets Values", JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			System.out.println("Number of Followees: " + followeesField.getText());
			System.out.println("Number of Followers: " + followersField.getText());
			System.out.println("Number of Tweets: " + tweetsGeneratedField.getText());
			numFollowees = Integer.parseInt(followeesField.getText());
			numFollowers = Integer.parseInt(followersField.getText());
			numTweetsGenerated = Integer.parseInt(tweetsGeneratedField.getText());
		}
		myAgent.setReadFrom(FROM_GENERATION);
	}
	
	public void selectTwitterOptions()
	{
		JTextField numFollowersToGrabField = new JTextField(5);
		JTextField nameFolloweesToGrabField = new JTextField(30);

		JPanel myPanel = new JPanel();
		myPanel.add(new JLabel("Name of Followee(s): "));
		myPanel.add(nameFolloweesToGrabField);
		myPanel.add(Box.createHorizontalStrut(15)); // a spacer
		myPanel.add(new JLabel("Number of Followers: "));
		myPanel.add(numFollowersToGrabField);
	

		int result = JOptionPane.showConfirmDialog(null, myPanel, 
				"Please Enter Followee Name(s) separated by comma if more than 1, Number of Followers", JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			System.out.println("Name of Followee(s): " + nameFolloweesToGrabField.getText());
			System.out.println("Number of Followers: " + numFollowersToGrabField.getText());
			nameFolloweesToGrab = nameFolloweesToGrabField.getText();
			numFollowersToGrab = Integer.parseInt(numFollowersToGrabField.getText());
			
			GuiEvent ge = new GuiEvent(this,myAgent.COLLECT_DATA);
			ge.addParameter(nameFolloweesToGrab);
			ge.addParameter(numFollowersToGrab);
	
			myAgent.postGuiEvent(ge);
		}
	}
	
	public void selectNumArtificialTweets()
	{
		JTextField numArtificialTweetsField = new JTextField(10);

		JPanel myPanel = new JPanel();
		myPanel.add(new JLabel("Number of artificial tweets: "));
		myPanel.add(numArtificialTweetsField);

		int result = JOptionPane.showConfirmDialog(null, myPanel, 
				"Please Enter number of artificial tweets to generate, 0 for same number as corpus", JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			try
			{
				System.out.println("Number of Artificial Tweets: " + numArtificialTweetsField.getText());
				numArtificialTweets = Integer.parseInt(numArtificialTweetsField.getText());
			} 
			catch (Exception e)
			{
				numArtificialTweets = DEFAULT_NUM_ARTIFICIAL_TWEETS;
			}
			
			JOptionPane.showMessageDialog(this,"Selected number of artificial tweets: "+numArtificialTweets, "Selected number of artificial tweets", JOptionPane.INFORMATION_MESSAGE);
			
			myAgent.setNumArtificialTweets(numArtificialTweets);
		}	
	}

	public void changeCardPanel(int panelNum)
	{
		CardLayout cl = (CardLayout)(cards.getLayout());
		if (panelNum == CHANGE_TO_PERFORMANCE)
			cl.show(cards,PERFORMANCE_PANEL_ID);
		else if (panelNum == CHANGE_TO_USERSIM)
			cl.show(cards,USERSIM_PANEL_ID);
	}
	
	public void getUsers()
	{
		//		String enteredData = enterDatasetField.getText();
		//		if (!enteredData.equals(""))
		//		{
		//			referenceUser = enteredData;
		//			System.out.println("referenceUser: "+referenceUser);
		//		}
		//		else
		//		{
		//			referenceUser = DEFAULT_DATASET;
		//		}
		
		
		initializeButton.setEnabled(false);
		startButton.setEnabled(false);
		getUsersButton.setEnabled(false);
		showAgentsList.clearSelection();
		
		recommendationArea.setText("");
		
		if (tweetLimitField.getText().equals(""))
			tweetLimit = 1000;
		else
			tweetLimit = Integer.parseInt(tweetLimitField.getText());

		if (beginDateField.getText().equals(""))
			beginDate = BEGIN_DATE;
		else
			beginDate = beginDateField.getText();

		if (endDateField.getText().equals(""))
			endDate = END_DATE;
		else
			endDate = endDateField.getText();

		if (numFollowees < 1)
			numFollowees = FOLLOWEES;

		if (numFollowers < 2)
			numFollowers = FOLLOWERS;

		if (numTweetsGenerated < 3)
			numTweetsGenerated = DEFAULT_TWEETS_GENERATED;

		GuiEvent ge = new GuiEvent(this,myAgent.GET_USERS);
		ge.addParameter(referenceUser);
		ge.addParameter(tweetLimit);
		ge.addParameter(beginDate);
		ge.addParameter(endDate);
		ge.addParameter(numFollowees);
		ge.addParameter(numFollowers);
		ge.addParameter(numTweetsGenerated);
		myAgent.postGuiEvent(ge);

	}

	public void initializeAgents()
	{
		getUsersButton.setEnabled(false);
		startButton.setEnabled(false);
		initializeButton.setEnabled(false);
		
		recommendationArea.setText("");

		if (numNodesField.getText().equals("") || Integer.parseInt(numNodesField.getText()) < 1)
			numNodes = 1;
		else
			numNodes = Integer.parseInt(numNodesField.getText());

		if (tweetLimitField.getText().equals(""))
			tweetLimit = 1000;
		else
			tweetLimit = Integer.parseInt(tweetLimitField.getText());

		if (beginDateField.getText().equals(""))
			beginDate = BEGIN_DATE;
		else
			beginDate = beginDateField.getText();

		if (endDateField.getText().equals(""))
			endDate = END_DATE;
		else
			endDate = endDateField.getText();

		if (recommendationField.getText().equals("") || Integer.parseInt(recommendationField.getText()) < DEFAULT_K_REC)
			kRecommend = DEFAULT_K_REC;
		else
			kRecommend = Integer.parseInt(recommendationField.getText());

		String enteredData = enterDatasetField.getText();
		if (!enteredData.equals(""))
		{
			referenceUser = enteredData;
			System.out.println("referenceUser: "+referenceUser);
		}

		if (numFollowees < 1)
			numFollowees = FOLLOWEES;

		if (numFollowers < 2)
			numFollowers = FOLLOWERS;

		if (numTweetsGenerated < 3)
			numTweetsGenerated = DEFAULT_TWEETS_GENERATED;

		GuiEvent ge = new GuiEvent(this,myAgent.INITIALIZE);
		ge.addParameter(numNodes);
		ge.addParameter(tweetLimit);
		ge.addParameter(beginDate);
		ge.addParameter(endDate);
		ge.addParameter(kRecommend);
		ge.addParameter(referenceUser);
		ge.addParameter(hashTags);
		ge.addParameter(retweets);
		ge.addParameter(stopWords);
		ge.addParameter(algorithmRec);
		ge.addParameter(numFollowees);
		ge.addParameter(numFollowers);
		ge.addParameter(numTweetsGenerated);
		myAgent.postGuiEvent(ge);

	}

	public void startSimulation()
	{
		startButton.setEnabled(false);
		initializeButton.setEnabled(false);
		getUsersButton.setEnabled(false);
		
		GuiEvent ge = new GuiEvent(this,myAgent.START_SIM);
		ge.addParameter(numNodes);
		ge.addParameter(tweetLimit);
		ge.addParameter(beginDate);
		ge.addParameter(endDate);
		ge.addParameter(kRecommend);
		ge.addParameter(referenceUser);
		ge.addParameter(hashTags);
		ge.addParameter(retweets);
		ge.addParameter(stopWords);
		ge.addParameter(algorithmRec);
		myAgent.postGuiEvent(ge);

		simulationIteration++;
		if (simulationIteration > 1)
		{
			previousResultArea.append(resultArea.getText());
			resultArea.setText("");
		}

		resultArea.append("====================== Simulation Iteration "+ simulationIteration+" =======================\n");

		countRecServersTP = 0;
		countRecServersTfidf = 0;
		countRecServersAlgorithm = 0;
		currentMaxTPTime = 0;
		currentMaxTfidfTime = 0;
		currentMaxAlgorithmTime = 0;
		currentMessagePassingTime = 0;

		if (algorithmRec == COS_SIM)
			currentTiming = new Timings(currentMaxTPTime,currentMaxTfidfTime,currentMaxAlgorithmTime,"CosSim");
		else if (algorithmRec == K_MEANS)
		     currentTiming = new Timings(currentMaxTPTime,currentMaxTfidfTime,currentMaxAlgorithmTime,"K-means"); 
		else if (algorithmRec == SVM)
			currentTiming = new Timings(currentMaxTPTime,currentMaxTfidfTime,currentMaxAlgorithmTime,"SVM");
		else if (algorithmRec == MLP)
			currentTiming = new Timings(currentMaxTPTime,currentMaxTfidfTime,currentMaxAlgorithmTime,"MLP");
		else if (algorithmRec == Doc2Vec)
		   currentTiming = new Timings(currentMaxTPTime,currentMaxTfidfTime,currentMaxAlgorithmTime,"Doc2Vec");
		else if (algorithmRec == K_MEANSEUCLIDEAN)
	       currentTiming = new Timings(currentMaxTPTime,currentMaxTfidfTime,currentMaxAlgorithmTime,"K-meansEuclidean");
		
		
		//simulationSelectionListener sSep = new simulationSelectionListener();
		//ActionEvent event = new ActionEvent(this, );
		//sSep.actionPerformed(event);
		//simulationSelectionBox.setSelectedIndex(1);
		//simulationButton.doClick();
		
	}
	
	public void startUserGenSimulation()
	{
		GuiEvent ge = new GuiEvent(this,myAgent.START_USER_GEN_SIM);
		myAgent.postGuiEvent(ge);
	}

	public ArrayList<String> getUsersRec()
	{
		String userToRecommend;
		//S String userToRecommend2;  // add the second user by Sepide
		// S ArrayList<String> usersToRec = new ArrayList<String>();
		System.out.println("indexToRecommend: "+indexToRecommend);
		//S System.out.println("indexToRecommend: "+indexToRecommend2);  // added by Sepide
		// code added by Sepide to allow selecting two users from the GUI
		/*S agentsList.add(indexToRecommend,usersToRec);
		agentsList.add(indexToRecommend2,usersToRec); */  // added by Sepide 
		userToRecommend = agentsList.getElementAt(indexToRecommend);
		//S userToRecommend2 = agentsList.getElementAt(indexToRecommend2);  // added by Sepide
		userToRecommend = userToRecommend.split("-",2)[0];
		//S userToRecommend2 = userToRecommend2.split("-",2)[0]; // added by Sepide
		ArrayList<String> usersRec = new ArrayList<String>();
		usersRec.add(userToRecommend);
		//S usersRec.add(userToRecommend2);  // added by Sepide
		System.out.println("controllerGUI usersRec: "+usersRec);
		return usersRec;
		
	}

	public void shutDown() 
	{
		System.exit(0);
		GuiEvent ge = new GuiEvent(this, myAgent.QUIT);
		myAgent.postGuiEvent(ge);
	}

	public void appendResult(String resultText)
	{
		resultArea.append(resultText+"\n");
	}

	public void appendPreviousResult(String resultText)
	{
		previousResultArea.append(resultText+"\n");
	}

	public void appendRecommendation(String recommendationText)
	{
		recommendationArea.append(recommendationText);
	}
	
	public void appendTwitterResult(String resultText)
	{
		grabTwitterResultArea.append(resultText+"\n");
	}
	
	public void appendUserGenTweetsResult(String resultText)
	{
		userGenTweetsResultArea.append(resultText+"\n");
	}
	
	public void appendClustersResult(String resultText)
	{
		clusterResultArea.append(resultText+"\n");
	}

	public void showMessageBox(String phase)
	{
		if (phase.equals("initialize"))
		{
			JOptionPane.showMessageDialog(this,"Finished initializing with given parameters", "Finished Step 2", JOptionPane.INFORMATION_MESSAGE);
		}
		else if (phase.equals("get users"))
		{
			JOptionPane.showMessageDialog(this,"Finished getting users", "Finished Step 1", JOptionPane.INFORMATION_MESSAGE);
		}
		else if (phase.equals("finished simulation"))
		{
			printTimingResults();
			JOptionPane.showMessageDialog(this,"Finished running simulation", "Finished Step 3", JOptionPane.INFORMATION_MESSAGE);
			
		}
		else if (phase.equals("finished collecting tweets"))
		{
			JOptionPane.showMessageDialog(this,"Finished collecting tweets", "Finished Collection Step", JOptionPane.INFORMATION_MESSAGE);
		}
		else if (phase.equals("finished user generated simulation"))
		{
			JOptionPane.showMessageDialog(this,"Finished user generated simulation", "Finished User Generation Simulation Step", JOptionPane.INFORMATION_MESSAGE);
		}
		
	}
	
	public void enableAllButtons()
	{
		startButton.setEnabled(true);	
		initializeButton.setEnabled(true);
		getUsersButton.setEnabled(true);
	}
	
	public void disableStartButton()
	{
		if (startButton.isEnabled())
		{
			startButton.setEnabled(false);
//			FileWriter writer;
//			try {
//				writer = new FileWriter("testEnablingStartButton.txt", true); //append
//				BufferedWriter bufferedWriter = new BufferedWriter(writer);
//				bufferedWriter.write("disabledStartButton");
//				bufferedWriter.newLine();
//				bufferedWriter.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
	}

	public void enableStartButton()
	{
		if (!startButton.isEnabled())
		{
			startButton.setEnabled(true);
			FileWriter writer;
			try {
				writer = new FileWriter("testEnablingStartButton.txt", true); //append
				BufferedWriter bufferedWriter = new BufferedWriter(writer);
				bufferedWriter.write("enabledStartButton");
				bufferedWriter.newLine();
				bufferedWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	public void disableUserGenSimButtons()
	{
		changePerformanceButton.setEnabled(false);
		dataCollectionButton.setEnabled(false);
		loadCorpusButton.setEnabled(false);
		setSimulatedTweetsButton.setEnabled(false);
		startUserSimButton.setEnabled(false);
	}
	
	public void enableUserGenSimButtons()
	{
		changePerformanceButton.setEnabled(true);
		dataCollectionButton.setEnabled(true);
		loadCorpusButton.setEnabled(true);
		setSimulatedTweetsButton.setEnabled(true);
		startUserSimButton.setEnabled(true);
	}
	
	public void changeRecommendUserTitle()
	{
		String recommendingUserName = agentsList.get(indexToRecommend);
		String newRecommendationTitle = "Recommendations for User: " + recommendingUserName;
		recommendationTitle.setTitle(newRecommendationTitle);
		this.repaint();
	}
	
	public void changeUserListTitle(int readFrom)
	{
		if (readFrom == FROM_GENERATION)
		{
			agentsListTitle.setTitle("List of Generated Users");
		}
		else if (readFrom == FROM_ARTIFICIAL)
		{
			agentsListTitle.setTitle("List of Artificial Users");
		}
		else
		{
			agentsListTitle.setTitle("List of Real Users");
		}
		this.repaint();
	}

	public void testPrint()
	{
		System.out.println("I AM PRINTING");
	}

	public void disableList()
	{
		showAgentsList.setEnabled(false);
	}

	public void enableList()
	{
		showAgentsList.setEnabled(true);
	}

	public void updateList(ArrayList<String> listOfAgents) 
	{
		agentsList.clear();
		System.out.println("listOfAgents.size(): "+listOfAgents.size());
		for (int i = 0; i < listOfAgents.size(); i++){
			agentsList.addElement(listOfAgents.get(i));
		}
	}
	
	public void reselectRecommendee()
	{
		if (agentsList.get(indexToRecommend).equals(recommendeeName))  // after && was added by Sepide
		{
			showAgentsList.setSelectedIndex(indexToRecommend);
			/*S showAgentsList.setSelectedIndex(indexToRecommend2);   // added by Sepide
			a = new int[] {indexToRecommend, indexToRecommend2};   // added by Sepide
			showAgentsList.setSelectedIndices(a);  */ // added by Sepide
		}
		else
		{
			if (agentsList.contains(recommendeeName))  // after && was added by Sepide
			{
				showAgentsList.setSelectedIndex(agentsList.indexOf(recommendeeName));
				//S showAgentsList.setSelectedIndex(agentsList.indexOf(recommendeeName2));  // added by Sepide
				//S int b1 = agentsList.indexOf(recommendeeName); // added by Sepide
				//S int b2 = agentsList.indexOf(recommendeeName2); // added by Sepide
				 /* S b = new int[] {b1, b2};  // added by Sepide
				showAgentsList.setSelectedIndices(b);  // added by Sepide    */ 
			}
			else
			{
				showAgentsList.setSelectedIndex(0);
				 /* S c = new int[] {0,1};               // added by Sepide
				showAgentsList.setSelectedIndices(c);   // added by Sepide  */ 
			}
			
		}
	}

	
	
	public void helpUserSimText()
	{
		JOptionPane.showMessageDialog(this,"1. Collect Data from Twitter by entering followee name(s) separated by commas then enter the number of followers you want for them. All followees will get try to get the same number of followers. The collected data to be used in the simulator will be stored in Dataset/TwitterObtained/allFolloweeNames_yyyy-MM-dd HH-mm-ss/*_final.txt.\n***Skip if you already have a correctly-formatted corpus.\n2. Load a corpus that was collected by Step 1 or a correctly-formatted corpus.\n3. Set Number of Artificial Tweets. 0 is default which means use the number of original processed corpus tweets. A number bigger than the original corpus size will try to keep the same proportions in the generated corpus.\n4. Start User Gen Simulation. Generated tweets will be saved under Dataset/TwitterObtained/GENERATED/ORIGINALFILENAME_yyyy-MM-dd HH-mm-ss_GENERATED.txt. Clustering results will appear at the end after clustering the generated tweets", "How To Use", JOptionPane.INFORMATION_MESSAGE);
	}
	
	public void helpPerformanceText()
	{
		JOptionPane.showMessageDialog(this,"1. Load a text file from the File Menu.\n2. Get Users after selecting a text file.\n3. Change any parameters then Initialize.\n4. Run simulation.\n5. If you want to run another simulation with different parameters, make sure to re-Initialize again before running the simulation.\n6. Loading in a new text file requires starting from step 1.", "How To Use", JOptionPane.INFORMATION_MESSAGE);
	}
	
	public void setTPTime(double tpTime)
	{
		countRecServersTP++;
		if (countRecServersTP <= numNodes)
		    currentMaxTPTime = getMax(currentMaxTPTime,tpTime);
		currentTiming.setTPTime(currentMaxTPTime);
	}

	public void setTfidfTime(double tfidfTime)
	{
		countRecServersTfidf++;
		if (countRecServersTfidf <= numNodes)
			currentMaxTfidfTime = getMax(currentMaxTfidfTime,tfidfTime);
		currentTiming.setTFIDFTime(currentMaxTfidfTime);
	}

	public void setAlgorithmTime(double algorithmTime)
	{
		countRecServersAlgorithm++;
		if (countRecServersAlgorithm <= numNodes)
			currentMaxAlgorithmTime = getMax(currentMaxAlgorithmTime,algorithmTime);
		currentTiming.setAlgorithmTime(currentMaxAlgorithmTime);
	}

	public double getMax(double time1, double time2)
	{
		if (time1 < time2)
			return time2;
		else
			return time1;
	}

	public void addMessagePassingTime(long messagePassingTime)
	{
		String messagePassingResult = "";
		messagePassingResult = "MAX Message Passing: " + messagePassingTime + " ms";
		currentMessagePassingTime = messagePassingTime;
		appendResult(messagePassingResult);
	}

	public void addMessagePassingTime(long[] messagePassingTimes)
	{
		String messagePassingResult = "";
		for (int i = 0; i < messagePassingTimes.length; i++)
		{
			messagePassingResult = "Message Passing: " + messagePassingTimes[i] + " ms";
			currentMessagePassingTime = messagePassingTimes[i];
			appendResult(messagePassingResult);
		}
	}
	
	public void addMessagePassingCost(int messagePassingCost)
	{
		String messagePassingResult = "";
		messagePassingResult = "TOTAL Messages Cost: " + messagePassingCost + " bytes";
		currentMessagePassingCost = messagePassingCost;
		appendResult(messagePassingResult);
	}

	public void addMessagePassingCost(int[] messagePassingCosts)
	{
		String messagePassingResult = "";
		for (int i = 0; i < messagePassingCosts.length; i++)
		{
			messagePassingResult = "Messages Cost: " + messagePassingCosts[i] + " bytes";
			currentMessagePassingTime = messagePassingCosts[i];
			appendResult(messagePassingResult);
		}
	}

	//Also works for cosine similarity too, just naming needs changing
	public void addKmeansMergeTime(long kmeansMergeTime)
	{
		long kmeansMergeTimeMs = kmeansMergeTime / 1000000;
		System.out.println("nanoseconds: " + kmeansMergeTime);
		System.out.println("merge time: " + kmeansMergeTimeMs+ " ms");
		String kmeansMergeResult = "Merge Message Time: ";
		kmeansMergeResult += kmeansMergeTimeMs + " ms";
		appendResult(kmeansMergeResult);
	}

	//Also works for cosine similarity too, just naming needs changing
	public void addKmeansMergeTimeNano(long kmeansMergeTime)
	{
		String kmeansMergeResult = "Merge Message Time: ";
		kmeansMergeResult += kmeansMergeTime + " ns";
		appendResult(kmeansMergeResult);
	}

	public void addTiming()
	{
		timings.add(currentTiming);
		if (simulationIteration > 1)
		{
			double differenceTP, differenceTfidf, differenceAlgorithm;
			double currentTP, currentTfidf, currentAlgorithm;
			double previousTP, previousTfidf, previousAlgorithm;
			DecimalFormat df = new DecimalFormat("#.##");

			currentTP = timings.get(timings.size()-1).getTPTime();
			currentTfidf = timings.get(timings.size()-1).getTFIDFTime();
			currentAlgorithm = timings.get(timings.size()-1).getAlgorithmTime();
			previousTP = timings.get(timings.size()-2).getTPTime();
			previousTfidf = timings.get(timings.size()-2).getTFIDFTime();
			previousAlgorithm = timings.get(timings.size()-2).getAlgorithmTime();

			System.out.println("currentTP: "+currentTP + " currentTfidf: "+currentTfidf + " currentAlgorithm: "+currentAlgorithm);
			System.out.println("previousTP: "+previousTP+" previousTfidf: "+previousTfidf+ " previousAlgorithm: "+previousAlgorithm);
			System.out.println("(currentTP - previousTP): "+(currentTP - previousTP));
			System.out.println("(currentTfidf - previousTfidf): "+(currentTfidf - previousTfidf));
			System.out.println("(currentAlgorithm - previousAlgorithm): "+(currentAlgorithm - previousAlgorithm));

			differenceTP = ((currentTP - previousTP) / previousTP)*100*-1;
			differenceTfidf = ((currentTfidf - previousTfidf) / previousTfidf)*100*-1;
			differenceAlgorithm = ((currentAlgorithm - previousAlgorithm) / previousAlgorithm)*100*-1;

			if (differenceTP == -0)
				differenceTP = 0.0;
			if (differenceTfidf == -0)
				differenceTfidf = 0.0;
			if (differenceAlgorithm == -0)
				differenceAlgorithm = 0.0;

			resultArea.append("Improvements - TP: "+df.format(differenceTP)+"% TFIDF: "+df.format(differenceTfidf)+"% Algorithm: "+df.format(differenceAlgorithm)+"%\n");
		}
	}

	public void printTimingResults()
	{
			FileWriter writer;
			String resultTextArea = resultArea.getText();
			String removeText = resultTextArea;
			
			if (removeText.contains("Improvement"))
			{
				removeText = resultTextArea.substring(0, resultTextArea.indexOf("Improvement"));
			}
			String finalText = removeText.substring(removeText.lastIndexOf("=")+1).trim()+"\n";
			
			try {
				writer = new FileWriter("resultsTiming.txt", true); //append
				BufferedWriter bufferedWriter = new BufferedWriter(writer);
				bufferedWriter.write(finalText);
				bufferedWriter.newLine();
				bufferedWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public void updateProgressLabel(String updateText)
	{
		performanceProgressLabel.setText("Current Progress: "+updateText);
	}
	
	public void updateUserGenProgressLabel(String updateText)
	{
		userGenProgressLabel.setText("Current Progress: "+updateText);
	}
	
}

