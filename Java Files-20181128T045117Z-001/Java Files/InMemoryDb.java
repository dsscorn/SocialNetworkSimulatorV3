package TwitterGatherDataFollowers.userRyersonU;

import java.util.ArrayList;

public class InMemoryDb {

	private ArrayList<Tweet> tweets;

	public InMemoryDb()
	{
		tweets = new ArrayList<Tweet>();
	}

	public ArrayList<Tweet> getTweets()
	{
		return tweets;
	}
	
	public void setTweets(ArrayList<Tweet> tweets)
	{
		this.tweets = tweets;
	}

	public void addTweet(Tweet t)
	{
		tweets.add(t);
	}

	public ArrayList<Tweet> getTweetsFromUser(String username)
	{
		ArrayList<Tweet> tweetsFromUser = new ArrayList<Tweet>();
		for (Tweet t : tweets)
		{
			if (t.getUser().equals(username))
				tweetsFromUser.add(t);
		}

		return tweetsFromUser;
	}

	public int getTweetCountFromUser(String username)
	{
		ArrayList<Tweet> tweetsFromUser = getTweetsFromUser(username);
		return tweetsFromUser.size();
	}
	
	public int getTotalTweets()
	{
		return tweets.size(); 
	}

	public void clearDb()
	{
		tweets.clear();
	}
}
