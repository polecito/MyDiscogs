package cat.joronya.mydiscogs.data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.net.Uri;
import android.provider.BaseColumns;
import cat.joronya.mydiscogs.MyDiscogs;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class Release implements BaseColumns 
{
	public static final String ID = "id";
	public int id;
	
	public static final String TITLE = "title";
	public String title;
	
	public static final String YEAR = "year";
	public int year;
	
	public static final String URI = "uri";
	public String uri;
	
	public static final String THUMB = "thumb";
	public String thumb;
	
	public static final String STATUS = "status";
	public String status;
	
	public static final String MASTER_ID = "master_id";
	@SerializedName("master_id")
	public int masterId;
	
	public static final String MASTER_URL = "master_url";
	@SerializedName("master_url")
	public String masterUrl;
	
	public static final String COUNTRY = "country";
	public String country;
	
	public static final String RELEASED = "released";
	public String released;
	
	public static final String NOTES = "release_notes";
	public String notes;
	
	public static final String STYLES = "styles";
	public List<String> styles;
	
	public static final String GENRES = "genres";
	public List<String> genres;
	
	public static final String COMMUNITY = "community";
	public Community community;
	
	public static final String SUBMITTER = "submitter";
	public User submitter;
	
	public static final String CONTRIBUTORS = "contributors";
	public List<User> contributors;
	
	public static final String LABELS = "labels";
	public List<Label> labels;
	
	public static final String COMPANIES = "companies";
	public List<Company> companies;
	
	public static final String FORMATS = "formats";
	public List<Format> formats;
	
	public static final String IMAGES = "images";
	public List<Image> images;
	
	public static final String ARTISTS = "artists";
	public List<Artist> artists;
	
	public static final String EXTRA_ARTISTS = "extra_artists";
	@SerializedName("extraartists")
	public List<Artist> extraArtists;
	
	public static final String TRACKLIST = "tracklist";
	public List<Track> tracklist;
	
	public static final String IDENTIFIERS = "identifier";
	public List<Identifier> identifiers;
	
	public static final String VIDEOS = "videos";
	public List<Video> videos;
	
	public static final String DOWNLOADED = "downloaded";
	public int downloaded; // timestamp
	
	
	public static final String AUTHORITY = MyDiscogs.AUTHORITY_BASE+".release";
	
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/releases");
    
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/cat.joronya.mydiscogs.release";

    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/cat.joronya.mydiscogs.release";

    public static final String BROADCAST_UPDATE = "release_updated";
    
    public static final String ERROR_EXTRA = "error";
    
    public static final String[] PROJECTION = new String[]{
    	_ID,			// 0
		ID,				// 1
		TITLE,			// 2
		YEAR,			// 3
		URI,			// 4
		STATUS,			// 5
		MASTER_ID,		// 6
		MASTER_URL,		// 7
		COUNTRY,		// 8
		RELEASED,		// 9
		NOTES,			// 10
		STYLES,			// 11
		GENRES,			// 12
		COMMUNITY,		// 13
		SUBMITTER,		// 14
		CONTRIBUTORS,	// 15
		LABELS,			// 16
		COMPANIES,		// 17
		FORMATS,		// 18
		IMAGES,			// 19
		ARTISTS,		// 20
		EXTRA_ARTISTS,	// 21
		TRACKLIST,		// 22
		IDENTIFIERS,	// 23
		VIDEOS,			// 24
		DOWNLOADED		// 25
    };
    
    public static final String DEFAULT_SORT_ORDER = TITLE+" ASC";
	
	public Release()
	{}
	
	/**
	 * Export Release object to ContentValues, usefull on database operations.
	 * When fullExport param is true, all attributes are exported, if release 
	 * object built from collection or wantlist lits, resumed one is exported.
	 * Added timestamp to mark download date.
	 *  
	 * @param fullExport Full attribute export, or resumed for list operations.
	 * @param downloadTimestamp Download timestamp.
	 * @return
	 */
	public ContentValues toContentValues(boolean fullExport, int downloadTimestamp)
	{
		ContentValues values = new ContentValues();
		values.put(ID, id);
		values.put(TITLE, title);
		values.put(YEAR, year);
		String sFormats = new Gson().toJson(formats);
		values.put(FORMATS, sFormats);
		String sLabels = new Gson().toJson(labels);
		values.put(LABELS, sLabels);
		String sArtists = new Gson().toJson(artists);
		values.put(ARTISTS, sArtists);
		
		if( fullExport )
		{
			values.put(URI, uri);
			values.put(STATUS, status);
			values.put(MASTER_ID, masterId);
			values.put(MASTER_URL, masterUrl);
			values.put(COUNTRY, country);
			values.put(RELEASED, released);
			values.put(NOTES, notes);
			String sGenres = new Gson().toJson(genres);
			values.put(GENRES, sGenres);
			String sStyles = new Gson().toJson(styles);
			values.put(STYLES, sStyles);
			String sCommunity = new Gson().toJson(community);
			values.put(COMMUNITY, sCommunity);
			values.put(SUBMITTER, submitter.username);
			String sContributors = new Gson().toJson(contributors);
			values.put(CONTRIBUTORS, sContributors);
			String sCompanies = new Gson().toJson(companies);
			values.put(COMPANIES, sCompanies);
			String sImages = new Gson().toJson(images);
			values.put(IMAGES, sImages);
			String sExtraArtists = new Gson().toJson(extraArtists);
			values.put(EXTRA_ARTISTS, sExtraArtists);
			String sTracklist = new Gson().toJson(tracklist);
			values.put(TRACKLIST, sTracklist);
			String sIdentifiers = new Gson().toJson(identifiers);
			values.put(IDENTIFIERS, sIdentifiers);
			String sVideos = new Gson().toJson(videos);
			values.put(VIDEOS, sVideos);
			
			// mark that detail download has been done
			values.put(DOWNLOADED, downloadTimestamp);
		}
		
		return values;
	}
	
	public List<ContentValues> getArtists()
	{
		ArrayList<ContentValues> ret = new ArrayList<ContentValues>();
		
		// per cada artista de l'array
		for(Artist artist : artists) 
		{
			// recuperem els valors a insertar, no es full export
			ret.add(artist.toContentValues(false, 0));
		}
		
		return ret;
	}
	
	public List<ContentValues> getLabels()
	{
		ArrayList<ContentValues> ret = new ArrayList<ContentValues>();
		
		// per cada artista de l'array
		for(Label label : labels) 
		{
			// recuperem els valors a insertar, no es full export
			ret.add(label.toContentValues(false, 0));
		}
		
		return ret;
	}
}
