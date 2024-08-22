package TwitterGatherDataFollowers.userRyersonU;

public class Timings {

	private double textProcessingTime;
	private double tfidfTime;
	private double algorithmTime;
	private String algorithm;

	public Timings()
	{
		textProcessingTime = 0; 
		tfidfTime = 0;
		algorithmTime = 0;
		algorithm = "CosSim";
	}

	public Timings(double tp, double tfidf, double algorithmTime, String algorithmName)
	{
		textProcessingTime = tp;
		tfidfTime = tfidf;
		this.algorithmTime = algorithmTime;
		algorithm = algorithmName;
	}

	public double getTPTime()
	{
		return textProcessingTime;
	}

	public void setTPTime(double tp)
	{
		textProcessingTime = tp;
	}

	public double getTFIDFTime()
	{
		return tfidfTime;
	}

	public void setTFIDFTime(double tfidf)
	{
		tfidfTime = tfidf;
	}

	public double getAlgorithmTime()
	{
		return algorithmTime;
	}

	public void setAlgorithmTime(double algorithm)
	{
		algorithmTime = algorithm;
	}

	public String toString()
	{
		return "TP: "+ textProcessingTime + " TFIDF: "+ tfidfTime + " "+ algorithm + ":"+algorithmTime+ " Total:"+(textProcessingTime+tfidfTime+algorithmTime);
	}
}
