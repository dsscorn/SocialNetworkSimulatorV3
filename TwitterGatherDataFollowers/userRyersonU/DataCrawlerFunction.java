package TwitterGatherDataFollowers.userRyersonU;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;

import twitter4j.IDs;
import twitter4j.Paging;
import twitter4j.RateLimitStatus;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

public class DataCrawlerFunction {

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
		String dirName = followeeScreenName;
		File dir = new File(dirName);
		//User currentUser = twitter.showUser(userScreenName);
		int pageNum=1;
		int size=0;
		Date userCreation;
		int userTweetCount=0;
		int userFollowersCount=0;
		int userFollowingsCount=0;
		int userFavouritesCount=0;
		int minTweetCount = 1;	//minimum number of tweet counts to accept and record
		int latestFollowersMax = 20; //amount of latest followers to look at
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
		 ArrayList<String> prevFollowers = new ArrayList<String>();
		 String fileNamePrevFollowers = followeeScreenName+"/"+followeeScreenName+"_Followers.txt";
		 try {
		 	
			 //FileReader reads text files in the default encoding.
			 FileReader fileReader = new FileReader(fileNamePrevFollowers);
	
	         Scanner myScanner = new Scanner(fileReader);
	         
	         while (myScanner.hasNext()){
	         	prevFollowers.add(myScanner.next());
	         }
	         // Always close files.
	         fileReader.close();
	         myScanner.close();
		     }
		     catch(FileNotFoundException ex) {
		         System.out.println(
		             "Unable to open file '" + 
		             fileNamePrevFollowers + "'");  
		     }
		     catch(IOException ex) {
		         System.out.println(
		             "Error reading file '" 
		             + fileNamePrevFollowers + "'");                  
		         // Or we could just do this: 
		         // ex.printStackTrace();
		     } 

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
							
					if (!prevFollowers.contains(userScreenName)){
						
						//Write userScreenName to follower list
						if (!dir.exists())
					 		dir.mkdir();
						FileWriter followerFileWriter = new FileWriter(fileNamePrevFollowers,true);
						followerFileWriter.write(userScreenName+"\n");
						followerFileWriter.close();
						
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
						maxId = lastTweetBeforeFollow.getId();
						System.out.print("\tTweetID: "+Long.toString(maxId));
						System.out.print("\tcreatedAt: "+dateFormat.format(lastTweetBeforeFollow.getCreatedAt()));
						System.out.println();
						
						//Record user information
						String fileNameProfile = followeeScreenName+"/"+userScreenName+"_Profile.txt";
						try {
				            FileWriter writer = new FileWriter(fileNameProfile, false);
						  
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
						 	String fileNameTweets = followeeScreenName+"/"+userScreenName+"_Tweets.txt";
				            FileWriter writer = new FileWriter(fileNameTweets, false);		  
				            BufferedWriter bufferedWriter = new BufferedWriter(writer);
				 
				           
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
					            
					            if (it.hasNext())
					            	bufferedWriter.newLine();
				            }
				            
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
					} //end if !prevFollowers.contain(userScreenName)
					else
						System.out.println("\t"+userScreenName+" was already in the follow list");
						
				} //end if (userTweetCount >= minTweetCount && userProtected == false)
				 
				else //userTweetCount < minTweetCount && userProtected != false
					System.out.println();
				 
			 } //end for loop numberOfFollowers
		} //end if (longFollowerIDs.length > 0)

	}

}
