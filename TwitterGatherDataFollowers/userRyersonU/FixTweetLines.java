package TwitterGatherDataFollowers.userRyersonU;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.*;   // added by Sepide 

public class FixTweetLines {
	
	static String[] stopWordsArray = {"a","able","about","above","abst","accordance","according","accordingly","across","act",
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
			"needs","neither","never","nevertheless","new","next","nine","ninety","no","nobody",
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
	
	public static int countTabs(char[] charArray)
	{
		int tabs = 0;
		for (int i = 0; i < charArray.length; i++)
		{
			if (charArray[i] == '\t')
				tabs++;
		}
		return tabs;
	}
	
	public static String cleanText(String currentText)
	{
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
		if (currentText.contains("RT @") )
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
		currentText=currentText.trim().replaceAll(" +", " ");
		
		System.out.println("currentText: "+currentText+" length: "+currentText.length());
		return currentText;
	}
	

	public static void main(String[] args) {

		String fileName = "NASA/Tarathorn_R_Tweets";
		String fileType = ".txt";
		String textFile = fileName + fileType;
		String fileNameOutput = fileName + "_fixed" + fileType;
		
		String currentWord,splitWord1,splitWord2;
		String[] words;

		int count = 0;
		int newLineCount=0;
		int lastIndexNewLine=0; //the last index of newline
		char[] charArray;
		
		    
			try{
				RandomAccessFile file = new RandomAccessFile(textFile, "r");
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");		
				String line = "";
				String[] info;
				long seekPosition = 0;
				ArrayList<String> tweetParts = new ArrayList<String>();
				Date currentDateFormat = null;
				long tweetId = 0;
				String tweetText = "";
				//S tweetText.setLength(1000);   // added by Sepide
				String currentUserName = "";
		
		
				line = file.readLine();
				seekPosition = file.getFilePointer();
		
				while(line != null)
				{
					FileWriter writer = new FileWriter(fileNameOutput, true); //append
					BufferedWriter bufferedWriter = new BufferedWriter(writer);
					tweetParts.clear();
					int tabsCount = 0;
		
					info = line.split("\t",6);
		
					for (int i = 0; i < info.length; i++)
					{
						System.out.print(i+": "+info[i]+" ");
					}
					System.out.println();
					 
					tweetId = Long.valueOf(info[1]);
					currentDateFormat = sdf.parse(info[2]);
					currentUserName = info[4];
					tweetText = info[5];
		
					//Check if nextline is proper format or part of previous tweet with newline(s) in it
					line = file.readLine();
		
					//If the nextline is null, add the current line to the db
					if (line == null)
					{
						if (tweetText.length() > 0)
						{
							bufferedWriter.write(info[0]+"\t"+tweetId+"\t"+info[2]+"\t"+info[3]+"\t"+currentUserName+"\t"+tweetText);
							bufferedWriter.newLine();
							bufferedWriter.close();
						}
						break;
					}
		
		
					tabsCount = countTabs(line.toCharArray());
		
					//Proper format with tabs in the tweet text
					if(tabsCount >= 5)
					{
						file.seek(seekPosition);
					}
					//Not proper format so it is text from the previous tweet with newlines in it
					else
					{
						do
						{
							seekPosition = file.getFilePointer();
							//System.out.println("seekPosition: "+seekPosition + " line: "+line);
							tweetParts.add(line);
							line = file.readLine();
							if (line != null)
								tabsCount = countTabs(line.toCharArray());
							else
								break;
						} while (tabsCount < 5);
		
						for (String partText : tweetParts)
						{
							tweetText = tweetText + partText;
						}
		
						file.seek(seekPosition);
					}

					if (tweetText.length() > 0)
					//if (words.length >= 3)
					{

						bufferedWriter.write(info[0]+"\t"+tweetId+"\t"+info[2]+"\t"+info[3]+"\t"+currentUserName+"\t"+tweetText);
						bufferedWriter.newLine();
						bufferedWriter.close();
					}
					line=file.readLine();
					seekPosition = file.getFilePointer();
				}
		
				file.close();
		
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			catch (ParseException e)
			{
				e.printStackTrace();
			}		
			
		}			
		
	

}
