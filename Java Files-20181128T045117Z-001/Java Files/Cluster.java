package TwitterGatherDataFollowers.userRyersonU;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import TwitterGatherDataFollowers.userRyersonU.Point;
 
public class Cluster {
	
	public List<Point> points;
	public Point centroid;
	public int id;
	public Map<String,Double> allTermsTfidf;
	
	//Creates a new Cluster
	public Cluster(int id) {
		this.id = id;
		this.points = new ArrayList<Point>();
		this.centroid = null;
	}
 
	public List getPoints() {
		return points;
	}
	
	public void addPoint(Point point) {
		points.add(point);
	}
 
	public void setPoints(List points) {
		this.points = points;
	}
 
	public Point getCentroid() {
		return centroid;
	}
 
	public void setCentroid(Point centroid) {
		this.centroid = centroid;
	}
 
	public int getId() {
		return id;
	}
	
	public void clear() {
		points.clear();
	}
	
	public void plotCluster() {
		Map<String,Double> tfidf;
		String userName;
		String fileNameOutput = "clustersIdTFIDF_"+id+"_result.txt";
		FileWriter writer;
		try {
			writer = new FileWriter(fileNameOutput, true);
			BufferedWriter bufferedWriter = new BufferedWriter(writer);
			//System.out.println("[Cluster: " + id+"]");
			bufferedWriter.write("[Cluster: " + id+"]");
			bufferedWriter.newLine();
			//System.out.println("[Centroid: " + centroid + "]");
			//bufferedWriter.write("[Centroid: " + centroid + "]");
			bufferedWriter.write("[Centroid: ");
			tfidf = centroid.getTfidf_or_Tf();
			String centroidWords="";
			for (String word : tfidf.keySet())
			{
				if (tfidf.get(word) > 0)
					centroidWords = centroidWords + " " + word;
			}
			bufferedWriter.write(centroidWords);
			bufferedWriter.write("]");
			bufferedWriter.newLine();
			//System.out.println("[Points: "+points.size());
			bufferedWriter.write("[Points: "+points.size());
			bufferedWriter.newLine();
			for(Point p : points) {
				//System.out.println(p);
				String words="";
				tfidf = p.getTfidf_or_Tf();
				userName = p.getUserName();
				bufferedWriter.write("userName: " + userName + ",");
				for (String word : tfidf.keySet())
				{
					words = words + " " + word;
				}
				bufferedWriter.write(words);
				//bufferedWriter.write(p.toString());
				bufferedWriter.newLine();
			}
			//System.out.println("]");
			bufferedWriter.write("]");
			bufferedWriter.newLine();
			bufferedWriter.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //append
		
		
	}
	
	
	public void plotClusterTweets() {
		Map<String,Double> tfidf;
		long tweetId;
		String fileNameOutput = "clustersIdTFIDF_"+id+"_result.txt";
		FileWriter writer;
		try {
			writer = new FileWriter(fileNameOutput, true);
			BufferedWriter bufferedWriter = new BufferedWriter(writer);
			//System.out.println("[Cluster: " + id+"]");
			bufferedWriter.write("[Cluster: " + id+"]");
			bufferedWriter.newLine();
			//System.out.println("[Centroid: " + centroid + "]");
			//bufferedWriter.write("[Centroid: " + centroid + "]");
			bufferedWriter.write("[Centroid: ");
			tfidf = centroid.getTfidf_or_Tf();
			String centroidWords="";
			for (String word : tfidf.keySet())
			{
				if (tfidf.get(word) > 0)
					centroidWords = centroidWords + " " + word;
			}
			bufferedWriter.write(centroidWords);
			bufferedWriter.write("]");
			bufferedWriter.newLine();
			//System.out.println("[Points: "+points.size());
			bufferedWriter.write("[Points: "+points.size());
			bufferedWriter.newLine();
			for(Point p : points) {
				//System.out.println(p);
				String words="";
				tfidf = p.getTfidf_or_Tf();
				tweetId = p.getTweetId();
				bufferedWriter.write(tweetId+",");
				for (String word : tfidf.keySet())
				{
					words = words + " " + word;
				}
				bufferedWriter.write(words);
				//bufferedWriter.write(p.toString());
				bufferedWriter.newLine();
			}
			//System.out.println("]");
			bufferedWriter.write("]");
			bufferedWriter.newLine();
			bufferedWriter.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //append
		
		
	}
	
	public void plotClusterTweets(LinkedHashMap<Long,String> tweetIdUser) {
		Map<String,Double> tfidf;
		long tweetId;
		String fileNameOutput = "clustersIdTFIDF_"+id+"_result.txt";
		FileWriter writer;
		try {
			writer = new FileWriter(fileNameOutput, true);
			BufferedWriter bufferedWriter = new BufferedWriter(writer);
			//System.out.println("[Cluster: " + id+"]");
			bufferedWriter.write("[Cluster: " + id+"]");
			bufferedWriter.newLine();
			//System.out.println("[Centroid: " + centroid + "]");
			//bufferedWriter.write("[Centroid: " + centroid + "]");
			bufferedWriter.write("[Centroid: ");
			tfidf = centroid.getTfidf_or_Tf();
			String centroidWords="";
			for (String word : tfidf.keySet())
			{ 
				if (tfidf.get(word) > 0)
					centroidWords = centroidWords + " " + word;
			}
			bufferedWriter.write(centroidWords);
			bufferedWriter.write("]");
			bufferedWriter.newLine();
			//System.out.println("[Points: "+points.size());
			bufferedWriter.write("[Cluster Member(s): "+points.size());
			bufferedWriter.newLine();
			for(Point p : points) {
				//System.out.println(p);
				String words="";
				tfidf = p.getTfidf_or_Tf();
				tweetId = p.getTweetId();
				bufferedWriter.write(tweetIdUser.get(tweetId)+",");
				bufferedWriter.write(tweetId+",");
				for (String word : tfidf.keySet())
				{
					words = words + " " + word;
				}
				bufferedWriter.write(words);
				//bufferedWriter.write(p.toString());
				bufferedWriter.newLine();
			}
			//System.out.println("]");
			bufferedWriter.write("]");
			bufferedWriter.newLine();
			bufferedWriter.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //append
		
		
	}
	
	public String getClusterResultAreaText() {
		Map<String,Double> tfidf;
		String userName;
		String resultText = "";
		
		//Original result text
		// resultText+="[Cluster: " + id+"]\n";
		// resultText+="[Centroid: ";
		// tfidf = centroid.getTfidf_or_Tf();	
		// String centroidWords="";
		// for (String word : tfidf.keySet())
		// {
			// if (tfidf.get(word) > 0)
				// centroidWords = centroidWords + " " + word;
		// }
		// resultText+=centroidWords+"]"+"\n";
		
		// resultText+="[Points: "+points.size()+"\n";
		// for(Point p : points) {
			// String words="";
			// tfidf = p.getTfidf_or_Tf();
			// userName = p.getUserName();
			// resultText+="userName: " + userName + ",";
			// for (String word : tfidf.keySet())
			// {
				// words = words + " " + word;
			// }
			// resultText+=words+"\n";
		// }
		// resultText+="]\n"
		
		//Modified Result Text
		resultText+="[Cluster: " + id+"]\n";
				
		resultText+="[Points: "+points.size()+"]\n";
		resultText+="[Usernames: ";
		for (int i = 0; i < points.size(); i++)
		{
			userName = points.get(i).getUserName();
			if (i == points.size()-1)
			{
				resultText+=userName+"]\n";
			}
			else
			{
				resultText+=userName+", ";
			}
		}
			
		return resultText;
	}
 
}