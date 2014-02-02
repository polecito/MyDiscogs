package cat.joronya.mydiscogs.data;

import java.util.List;

import com.google.gson.annotations.SerializedName;


public class Community 
{
	public static final String STATUS = "status";
	public String status;
	
	public static final String HAVE = "have";
	public int have;
	
	public static final String WANT = "want";
	public int want;
	
	public static final String RATING = "rating";
	public Rating rating;
	
	public static final String CONTRIBUTORS = "contributors";
	public List<User> contributors;
	
	public static final String SUBMITTER = "submitter";
	public User submitter;
	
	public static final String DATA_QUALITY = "data_quality";
	@SerializedName("data_quality")
	public String dataQuality;
	
	public Community() {}	
}
