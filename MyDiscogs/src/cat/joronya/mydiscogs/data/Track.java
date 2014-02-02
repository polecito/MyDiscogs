package cat.joronya.mydiscogs.data;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Track 
{
	public static final String DURATION = "duration";
	public String duration;
	
	public static final String POSITION = "position";
	public String position;
	
	public static final String TITLE = "title";
	public String title;
	
	public static final String EXTRA_ARTISTS = "extra_artists";
	@SerializedName("extraartists")
	public List<Artist> extraArtists;
	
	public Track() {}
}
