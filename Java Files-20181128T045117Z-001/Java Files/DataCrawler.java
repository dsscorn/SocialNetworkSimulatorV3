package TwitterGatherDataFollowers.userRyersonU;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;
import java.text.ParseException;

import twitter4j.IDs;
import twitter4j.Paging;
import twitter4j.RateLimitStatus;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

public class DataCrawler {

	public static void main(String[] args) throws TwitterException, IOException {
		final ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true);
		cb.setOAuthConsumerKey("nz5M3lZWGoTImeSKzTArfugwe");
		cb.setOAuthConsumerSecret("7COHctky30Jpb4O4ilVy8QMPCcJq1Kwz8RjZ1Us5gn2x5oRFct");
		cb.setOAuthAccessToken("702093889453174784-LRGk2gKp2uGdUjHsPu0AqJFf6pat5IT");
		cb.setOAuthAccessTokenSecret("VhsBXlvkgr6xiHrtii1lwlOhaXLF70ISt2NJVNiWVkUwP");
		
		
		Twitter twitter = new TwitterFactory(cb.build()).getInstance();
	
		String userScreenName = "";
		String followeeScreenName = "RyersonU";
		long maxId;
		String dirName = "Dataset/TwitterObtained/";
		File dir = new File(dirName);
		String collectionDirName = followeeScreenName+"_"+System.currentTimeMillis();
		String twitterCorpusName = dirName+collectionDirName+"/TotalTweets/"+followeeScreenName + "_" + System.currentTimeMillis()+".txt";
		//User currentUser = twitter.showUser(userScreenName);
		int pageNum=1;
		int size=0;
		Date userCreation;
		int userTweetCount=0;
		int userFollowersCount=0;
		int userFollowingsCount=0;
		int userFavouritesCount=0;
		int minTweetCount = 1;	//minimum number of tweet counts to accept and record
		int latestFollowersMax = 5; //amount of latest followers to look at
		int remainingStatusCalls;
		boolean userProtected=false;
		long userID;
		String userPreferLang;
		String userLocation;
		String userTimeZone;
		String userURL;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		Date dateFollowed = new Date();
		cal.setTime(dateFollowed);
		cal.add(Calendar.DATE, -1);
		dateFollowed = cal.getTime();
		RateLimitStatus rateTweetCheck;
	     
		IDs followerIDs = null; //followee's list of follower ids
		
		if (!dir.exists())
		{
			dir.mkdirs();
		}
		
		
		//Check rate for getting rate limit status
		//Assumes you have at least 1 call to use in the beginning
		rateTweetCheck = twitter.getRateLimitStatus("application").get("/application/rate_limit_status");
		remainingStatusCalls = rateTweetCheck.getRemaining();
		System.out.println("/application/rate_limit_status remainingStatusCalls: " + remainingStatusCalls);
		if (remainingStatusCalls > 1)
			rateTweetCheck = twitter.getRateLimitStatus("followers").get("/followers/ids");
		else{
			try {
				int sleepTime = rateTweetCheck.getSecondsUntilReset()*1000+5000;
				System.out.println("getSecondsUntilReset(): "+rateTweetCheck.getSecondsUntilReset());
				System.out.println("Sleeping..."+sleepTime/1000+"s");
				Thread.sleep(sleepTime);
				
				rateTweetCheck = twitter.getRateLimitStatus("followers").get("/followers/ids");
			} catch (InterruptedException e) {
			 // TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		remainingStatusCalls = rateTweetCheck.getRemaining();
		System.out.println("/followers/ids remainingStatusCalls: "+remainingStatusCalls);
		if (remainingStatusCalls > 1)
			followerIDs = twitter.getFollowersIDs(followeeScreenName, -1);
		else{
			try {
				int sleepTime = rateTweetCheck.getSecondsUntilReset()*1000+5000;
				System.out.println("getSecondsUntilReset(): "+rateTweetCheck.getSecondsUntilReset());
				System.out.println("Sleeping..."+sleepTime/1000+"s");
				Thread.sleep(sleepTime);
				
				followerIDs = twitter.getFollowersIDs(followeeScreenName, -1);
			} catch (InterruptedException e) {
			 // TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		 
		 long[] longFollowerIDs = followerIDs.getIDs();
		 
		 //Get the list of followers collected
		 // ArrayList<String> prevFollowers = new ArrayList<String>();
		 // String fileNamePrevFollowers = followeeScreenName+"/"+followeeScreenName+"_Followers.txt";
		 // try {
		 	
			 // //FileReader reads text files in the default encoding.
			 // FileReader fileReader = new FileReader(fileNamePrevFollowers);
	
	         // Scanner myScanner = new Scanner(fileReader);
	         
	         // while (myScanner.hasNext()){
	         	// prevFollowers.add(myScanner.next());
	         // }
	         // //Always close files.
	         // fileReader.close();
	         // myScanner.close();
		     // }
		     // catch(FileNotFoundException ex) {
		         // System.out.println(
		             // "Unable to open file '" + 
		             // fileNamePrevFollowers + "'");  
		     // }
		     // catch(IOException ex) {
		         // System.out.println(
		             // "Error reading file '" 
		             // + fileNamePrevFollowers + "'");                  
		        //// Or we could just do this: 
		         // ex.printStackTrace();
		     // } 

		 if (longFollowerIDs.length > 0){
			 int numberOfFollowers = Math.min(longFollowerIDs.length, latestFollowersMax); //get minimum of followee's list of followers or latestFollowersMax
			 for (int i=0; i < numberOfFollowers; i++){
				 User currentUser = twitter.showUser(longFollowerIDs[i]);
				 userTweetCount = currentUser.getStatusesCount();
				 userProtected = currentUser.isProtected();
				 userScreenName = currentUser.getScreenName();
				 userFollowersCount = currentUser.getFollowersCount();
				 userFollowingsCount = currentUser.getFriendsCount();
				 System.out.print("ScreenName: "+ userScreenName);
				 System.out.print("\tProtected: "+ userProtected);
				 System.out.print("\tFollowersCount: "+ userFollowersCount);
				 System.out.print("\tFollowingCount: "+ userFollowingsCount);
				 System.out.print("\tTweetsCount: "+ userTweetCount);
				 pageNum = 1; //reset the pageNum to 1 for each user
				 
				 if (userTweetCount >= minTweetCount && userProtected == false){
							
											
					//Write userScreenName to follower list
					if (!dir.exists())
					{
						// dir.mkdirs();
						System.out.println("Made dirs: "+dir.mkdirs());
					}
						
					
					
					
					//Check rate for getting rate limit status
					rateTweetCheck = twitter.getRateLimitStatus("application").get("/application/rate_limit_status");
					remainingStatusCalls = rateTweetCheck.getRemaining();
					System.out.println("\n/application/rate_limit_status remainingStatusCalls: " + remainingStatusCalls);
					if (remainingStatusCalls > 1)
						rateTweetCheck = twitter.getRateLimitStatus("followers").get("/followers/ids");
					else{
						try {
							int sleepTime = rateTweetCheck.getSecondsUntilReset()*1000+5000;
							System.out.println("getSecondsUntilReset(): "+rateTweetCheck.getSecondsUntilReset());
							System.out.println("Sleeping..."+sleepTime/1000+"s");
							Thread.sleep(sleepTime);
							
							rateTweetCheck = twitter.getRateLimitStatus("followers").get("/followers/ids");
						} catch (InterruptedException e) {
						 // TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					//Get list of followers for current user
					remainingStatusCalls = rateTweetCheck.getRemaining();
					System.out.println("/followers/ids remainingStatusCalls: "+remainingStatusCalls);
					IDs userFollowerIDs = null;
					if (remainingStatusCalls > 2)
						userFollowerIDs = twitter.getFollowersIDs(userScreenName, -1);
					else{
						try {
							int sleepTime = rateTweetCheck.getSecondsUntilReset()*1000+5000;
							System.out.println("getSecondsUntilReset(): "+rateTweetCheck.getSecondsUntilReset());
							System.out.println("Sleeping..."+sleepTime/1000+"s");
							Thread.sleep(sleepTime);			
							userFollowerIDs = twitter.getFollowersIDs(userScreenName, -1);
						} catch (InterruptedException e) {
						 // TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
										
					//Check rate for getting rate limit status
					rateTweetCheck = twitter.getRateLimitStatus("application").get("/application/rate_limit_status");
					remainingStatusCalls = rateTweetCheck.getRemaining();
					System.out.println("/application/rate_limit_status remainingStatusCalls: " + remainingStatusCalls);
					if (remainingStatusCalls > 2)
						rateTweetCheck = twitter.getRateLimitStatus("friends").get("/friends/ids");
					else{
						try {
							int sleepTime = rateTweetCheck.getSecondsUntilReset()*1000+5000;
							System.out.println("getSecondsUntilReset(): "+rateTweetCheck.getSecondsUntilReset());
							System.out.println("Sleeping..."+sleepTime/1000+"s");
							Thread.sleep(sleepTime);
							
							rateTweetCheck = twitter.getRateLimitStatus("friends").get("/friends/ids");
						} catch (InterruptedException e) {
						 // TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					//Get list of followees for current user
					rateTweetCheck = twitter.getRateLimitStatus("friends").get("/friends/ids");
					remainingStatusCalls = rateTweetCheck.getRemaining();
					System.out.println("/friends/ids remainingStatusCalls: "+remainingStatusCalls); 
					IDs userFollowingIDs = null;
					if (remainingStatusCalls > 2)
						userFollowingIDs = twitter.getFriendsIDs(userScreenName, -1);
					else{
						try {
							int sleepTime = rateTweetCheck.getSecondsUntilReset()*1000+5000;
							System.out.println("getSecondsUntilReset(): "+rateTweetCheck.getSecondsUntilReset());
							System.out.println("Sleeping..."+sleepTime/1000+"s");
							Thread.sleep(sleepTime);
							userFollowingIDs = twitter.getFriendsIDs(userScreenName, -1);
						} catch (InterruptedException e) {
						 // TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					long[] userLongFollowerIDs = userFollowerIDs.getIDs();
					long[] userLongFollowingIDs = userFollowingIDs.getIDs(); 
					userCreation = currentUser.getCreatedAt(); //created_at;
					userFavouritesCount = currentUser.getFavouritesCount(); //favourites_count
					userFollowersCount = currentUser.getFollowersCount(); //followers_count
					userFollowingsCount = currentUser.getFriendsCount(); //friends_count
					userID = currentUser.getId(); //id
					userPreferLang = currentUser.getLang(); //lang
					userLocation = currentUser.getLocation(); //location
					userTimeZone = currentUser.getTimeZone(); //time_zone
					userURL = currentUser.getURL(); //url
					
					Status lastTweetBeforeFollow = currentUser.getStatus();
					
					try{
						maxId = lastTweetBeforeFollow.getId();
						
						System.out.print("\tTweetID: "+Long.toString(maxId));
						System.out.print("\tcreatedAt: "+dateFormat.format(lastTweetBeforeFollow.getCreatedAt()));
						System.out.println();
						
						//Record user information
						String fileNameProfile = dirName+"/"+collectionDirName+"/"+userScreenName+"_Profile.txt";
						try {
							File fileNameNewDirectory = new File(fileNameProfile);
							fileNameNewDirectory.getParentFile().mkdirs();
							FileWriter writer = new FileWriter(fileNameNewDirectory, false);
						  
							BufferedWriter bufferedWriter = new BufferedWriter(writer);
				 
							bufferedWriter.write("\"screen_name\": \""+userScreenName+"\"\n");
							bufferedWriter.write("\"id\": "+userID+"\n");
							bufferedWriter.write("\"created_at\": \""+dateFormat.format(userCreation)+"\"\n");
							bufferedWriter.write("\"lang\": \""+userPreferLang+"\"\n");
							bufferedWriter.write("\"location\": \""+userLocation+"\"\n");
							bufferedWriter.write("\"time_zone\": \""+userTimeZone+"\"\n");
							bufferedWriter.write("\"url\": \""+userURL+"\"\n");
							bufferedWriter.write("\"favourites_count\": "+userFavouritesCount+"\n");
							
							bufferedWriter.write("\"followers_count\": "+userFollowersCount+"\n");  
							bufferedWriter.write("\"followerIDs\": [");
							for (int j = 0; j < userLongFollowerIDs.length; j++){
								bufferedWriter.write(Long.toString(userLongFollowerIDs[j]));
								if (j != userLongFollowerIDs.length-1)
									bufferedWriter.write(", ");
								else
									bufferedWriter.write(" ]\n");
							}
							
							bufferedWriter.write("\"friends_count\": "+userFollowingsCount+"\n");
							bufferedWriter.write("\"friendIDs\": [");
							for (int j = 0; j < userLongFollowingIDs.length; j++){
								bufferedWriter.write(Long.toString(userLongFollowingIDs[j]));
								if (j != userLongFollowingIDs.length-1)
									bufferedWriter.write(", ");
								else
									bufferedWriter.write("]\n");
							}
							
							bufferedWriter.write("\"statuses_count\": "+userTweetCount+"\n");
							bufferedWriter.write("\"date_followed\": \""+dateFormat2.format(dateFollowed)+"\"\n");
							
							
							bufferedWriter.close();
					   } catch (IOException e) {
							e.printStackTrace();
					   } 
					
						ArrayList<Status> statuses = new ArrayList<Status>();
						//Get user tweets
						while (true) {
				
							try {
								 
							size = statuses.size(); 
							Paging page = new Paging(pageNum++, 100);
							page.setMaxId(maxId);
							
							//Check rate for getting rate limit status
							rateTweetCheck = twitter.getRateLimitStatus("application").get("/application/rate_limit_status");
							remainingStatusCalls = rateTweetCheck.getRemaining();
							System.out.println("/application/rate_limit_status remainingStatusCalls: " + remainingStatusCalls);
							if (remainingStatusCalls > 2)
								rateTweetCheck = twitter.getRateLimitStatus("statuses").get("/statuses/user_timeline");
							else{
								try {
									int sleepTime = rateTweetCheck.getSecondsUntilReset()*1000+5000;
									System.out.println("getSecondsUntilReset(): "+rateTweetCheck.getSecondsUntilReset());
									System.out.println("Sleeping..."+sleepTime/1000+"s");
									Thread.sleep(sleepTime);
									
									rateTweetCheck = twitter.getRateLimitStatus("statuses").get("/statuses/user_timeline");
								} catch (InterruptedException e) {
								 // TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							
							remainingStatusCalls = rateTweetCheck.getRemaining();
							System.out.println("/statuses/user_timeline remainingStatusCalls: "+remainingStatusCalls);
							if (remainingStatusCalls > 2)
								statuses.addAll(twitter.getUserTimeline(userScreenName, page));
							else{
								try {
									int sleepTime = rateTweetCheck.getSecondsUntilReset()*1000+5000;
									System.out.println("getSecondsUntilReset(): "+rateTweetCheck.getSecondsUntilReset());
									System.out.println("Sleeping..."+sleepTime/1000+"s");
									Thread.sleep(sleepTime);
									statuses.addAll(twitter.getUserTimeline(userScreenName, page));
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							if (statuses.size() == size)
								break;
							}
							catch(TwitterException e) {
								e.printStackTrace();
							}
						} //end while(true)
						
						int userTweetObtained=0;
						//Record user tweets
						try {
												
							String fileNameTweets = dirName+"/"+collectionDirName+"/"+userScreenName+"_Tweets.txt";
							
							File fileNameNewDirectory = new File(fileNameTweets);
							fileNameNewDirectory.getParentFile().mkdirs();
							FileWriter writer = new FileWriter(fileNameNewDirectory, false);
						
							BufferedWriter bufferedWriter = new BufferedWriter(writer);
				 
							
						
							File fileNameNewCorpus = new File(twitterCorpusName);
							fileNameNewCorpus.getParentFile().mkdirs();
							FileWriter datasetFileWriter = new FileWriter(fileNameNewCorpus, true);
			
							BufferedWriter bufferedDatasetWriter = new BufferedWriter(datasetFileWriter);
							
						   
						   
							//referenceUser,tweetID,created_at,userID,screen_name,text
							for (Iterator<Status> it = statuses.iterator(); it.hasNext();){
								Status currentTweet = (Status)it.next();
								userTweetObtained++;
								
								bufferedWriter.write(followeeScreenName);
								bufferedWriter.write("\t"+Long.toString(currentTweet.getId()));
								bufferedWriter.write("\t"+dateFormat.format(currentTweet.getCreatedAt()));
								bufferedWriter.write("\t"+userID);
								bufferedWriter.write("\t"+userScreenName);
								bufferedWriter.write("\t"+currentTweet.getText());
								
								bufferedDatasetWriter.write(followeeScreenName);
								bufferedDatasetWriter.write("\t"+Long.toString(currentTweet.getId()));
								bufferedDatasetWriter.write("\t"+dateFormat.format(currentTweet.getCreatedAt()));
								bufferedDatasetWriter.write("\t"+userID);
								bufferedDatasetWriter.write("\t"+userScreenName);
								bufferedDatasetWriter.write("\t"+currentTweet.getText());
								
								if (it.hasNext())
								{
									bufferedWriter.newLine();
									bufferedDatasetWriter.newLine();
								}
									
							}
							
							datasetFileWriter.close();
							bufferedWriter.close();
					   } catch (IOException e) {
							e.printStackTrace();
					   }
						
						//add count of user tweets to user profile
						FileWriter writer;
						try {
							writer = new FileWriter(fileNameProfile, true); //append
							BufferedWriter bufferedWriter = new BufferedWriter(writer);
							bufferedWriter.write("\"obtained_tweets\": "+userTweetObtained);
							bufferedWriter.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println("Error retrieving user's tweets. Skipping collection of this user");
					}					
					
						
				} //end if (userTweetCount >= minTweetCount && userProtected == false)
				 
				else //userTweetCount < minTweetCount && userProtected != false
					System.out.println();
				 
			 } //end for loop numberOfFollowers
		} //end if (longFollowerIDs.length > 0)

			
		File fileNameNewCorpus = new File(twitterCorpusName);
		fileNameNewCorpus.getParentFile().mkdirs();
		FileWriter datasetFileWriter = new FileWriter(fileNameNewCorpus, true);

		BufferedWriter bufferedDatasetWriter = new BufferedWriter(datasetFileWriter);
		
		bufferedDatasetWriter.newLine();
		
		try{
				String fileName = twitterCorpusName;
				RandomAccessFile file = new RandomAccessFile(fileName, "r");
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");		
				String line = "";
				String[] info;
				long seekPosition = 0;
				ArrayList<String> tweetParts = new ArrayList<String>();
				Date currentDateFormat = null;
				long tweetId = 0;
				String tweetText = "";
				String currentUserName = "";
		
				String fileNameOutput = fileName + "_fixed.txt";
		
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

}
