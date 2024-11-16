package TwitterGatherDataFollowers.userRyersonU;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Tweet implements Comparable<Tweet> {
 
	private String tweetText;
	private long tweetId;
	private Date date;
	private String username;
	private String dateString;

	public Tweet()
	{
		tweetText = "";
		tweetId = 0;
		date = new Date();
		username = "";
	}

	public Tweet(String text, long id, Date date, String username)
	{
		tweetText = text;
		tweetId = id;
		this.date = date;
		this.username = username;
	}
	
	public Tweet(String text, long id, String date, String username)
	{
		dateString = date;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		tweetText = text;
		tweetId = id;
		try {
			this.date = sdf.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.username = username;
	}

	
	
	public String getTweetText()
	{
		return tweetText;
	}

	public void setTweetText(String text)
	{
		tweetText = text;
	}

	public long getTweetId()
	{
		return tweetId;
	}

	public void setTweetId(long tweetId)
	{
		this.tweetId = tweetId;
	}

	public Date getDate()
	{
		return date;
	}

	public void setDate(Date date)
	{
		this.date = date;
	}
		
	public String getDateString()
	{
		return dateString;
	}
	
	public void setDateString(String dateString)
	{
		this.dateString = dateString;
	}

	public String getUser()
	{
		return username;
	}

	public void setUser(String user)
	{
		username = user;
	}

	public int compareTo(Tweet otherTweet) {
		
		if (tweetId < otherTweet.getTweetId())
			return -1;
		else if (tweetId > otherTweet.getTweetId())
			return 1;
		else
			return 0;
	}
}
