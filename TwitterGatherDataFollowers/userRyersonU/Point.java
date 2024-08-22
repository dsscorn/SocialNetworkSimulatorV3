package TwitterGatherDataFollowers.userRyersonU;

import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.lang.*;

public class Point {

	private double x = 0;
	private double y = 0;
	private int cluster_number = 0;
	private Long tweetId;
	private String userName;
	private Map<String,Double> tfidf_or_tf;

	public Point(long tweetId,Map<String,Double> tfidf_or_tf)
	{
		this.setTweetId(tweetId);
		this.setTfidf_or_Tf(tfidf_or_tf);
	}

	public Point(String userName,Map<String,Double> tfidf_or_tf)
	{
		this.setUserName(userName);
		this.setTfidf_or_Tf(tfidf_or_tf);
	}

	public void setTweetId(long tweetId)
	{
		this.tweetId = tweetId;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	public long getTweetId()
	{
		return this.tweetId;
	}

	public String getUserName()
	{
		return this.userName;
	}

	public void setTfidf_or_Tf(Map<String,Double> tfidf_or_tf)
	{
		this.tfidf_or_tf = tfidf_or_tf;
	}

	public Map<String,Double> getTfidf_or_Tf()
	{
		return this.tfidf_or_tf;
	} 

	public void setCluster(int n) {
		this.cluster_number = n;
	}

	public int getCluster() {
		return this.cluster_number;
	}
	/* 
    //Calculates the distance between two points.
    protected static double distance(Point p, Point centroid) {
        return Math.sqrt(Math.pow((centroid.getY() - p.getY()), 2) + Math.pow((centroid.getX() - p.getX()), 2));
    }  */
	 

	//Calculates the cosine similarity between two points
	//protected static double cosSimDistance(Point p, Point centroid) {
	public double cosSimDistance(Point centroid) {
		double cosSim = 0.0;
		Map<String, Double> pointTfidf_or_Tf = getTfidf_or_Tf();
		Map<String, Double> centroidTfidf_or_Tf = centroid.getTfidf_or_Tf();
		for (String term : pointTfidf_or_Tf.keySet())
		{
			cosSim+= pointTfidf_or_Tf.get(term) * centroidTfidf_or_Tf.get(term);
		}

		return cosSim;
	}
	
	// Calculates the Euclidean distance between two points added by Sepide
	
	public double eucliDistance(Point centroid) {
		double diff_square_sum = 0.0;
		Map<String, Double> pointTfidf_or_Tf = getTfidf_or_Tf();
		Map<String, Double> centroidTfidf_or_Tf = centroid.getTfidf_or_Tf();
		for (String term : pointTfidf_or_Tf.keySet())
		{
			diff_square_sum += (pointTfidf_or_Tf.get(term) - centroidTfidf_or_Tf.get(term)) * (pointTfidf_or_Tf.get(term) - centroidTfidf_or_Tf.get(term));
		}

		return Math.sqrt(diff_square_sum);
	}

	//public String toString() {
	//	return "("+tweetId+","+tfidf+")";
	//}
	public String toString() {
		return "("+userName+","+tfidf_or_tf+")";
	}

}