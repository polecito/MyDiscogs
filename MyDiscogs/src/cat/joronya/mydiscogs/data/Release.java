package cat.joronya.mydiscogs.data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import cat.joronya.mydiscogs.MyDiscogs;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class Release implements BaseColumns 
{
	public int _id;
	
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
	
	public static final String LABELS = "labels";
	public List<Label> labels;
	
	public static final String SERIES = "series";
	public List<Label> series;
	
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
    
    public static final String PERCENT_EXTRA = "percent";
    
    public static final String[] PROJECTION = new String[]{
    	_ID,			// 0
		ID,				// 1
		TITLE,			// 2
		YEAR,			// 3
		URI,			// 4
		THUMB,			// 5
		MASTER_ID,		// 6
		MASTER_URL,		// 7
		COUNTRY,		// 8
		RELEASED,		// 9
		NOTES,			// 10
		STYLES,			// 11
		GENRES,			// 12
		COMMUNITY,		// 13
		LABELS,			// 14
		SERIES,			// 15
		COMPANIES,		// 16
		FORMATS,		// 17
		IMAGES,			// 18
		ARTISTS,		// 19
		EXTRA_ARTISTS,	// 20
		TRACKLIST,		// 21
		IDENTIFIERS,	// 22
		VIDEOS,			// 23
		DOWNLOADED		// 24
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
	public ContentValues toContentValues(boolean fullExport, long downloadTimestamp)
	{
		ContentValues values = new ContentValues();
		values.put(ID, id);
		values.put(TITLE, title);
		values.put(YEAR, year);
		values.put(THUMB, thumb);
		if( formats != null )
		{
			String sFormats = new Gson().toJson(formats);
			values.put(FORMATS, sFormats);
		}
		if( labels != null )
		{
			String sLabels = new Gson().toJson(labels);
			values.put(LABELS, sLabels);
		}
		if( series != null )
		{
			String sSeries = new Gson().toJson(series);
			values.put(SERIES, sSeries);
		}
		if( artists != null )
		{
			String sArtists = new Gson().toJson(artists);
			values.put(ARTISTS, sArtists);
		}
		
		if( fullExport )
		{
			values.put(URI, uri);
			values.put(MASTER_ID, masterId);
			values.put(MASTER_URL, masterUrl);
			values.put(COUNTRY, country);
			values.put(RELEASED, released);
			values.put(NOTES, notes);
			if( genres != null )
			{
				String sGenres = new Gson().toJson(genres);
				values.put(GENRES, sGenres);
			}
			if( styles != null )
			{
				String sStyles = new Gson().toJson(styles);
				values.put(STYLES, sStyles);
			}
			if(community != null)
			{
				String sCommunity = new Gson().toJson(community);
				values.put(COMMUNITY, sCommunity);
			}
			if( companies != null )
			{
				String sCompanies = new Gson().toJson(companies);
				values.put(COMPANIES, sCompanies);
			}
			if( images != null )
			{
				String sImages = new Gson().toJson(images);
				values.put(IMAGES, sImages);
			}
			if( extraArtists != null )
			{
				String sExtraArtists = new Gson().toJson(extraArtists);
				values.put(EXTRA_ARTISTS, sExtraArtists);
			}
			if( tracklist != null )
			{
				String sTracklist = new Gson().toJson(tracklist);
				values.put(TRACKLIST, sTracklist);
			}
			if( identifiers != null )
			{
				String sIdentifiers = new Gson().toJson(identifiers);
				values.put(IDENTIFIERS, sIdentifiers);
			}
			if( videos != null )
			{
				String sVideos = new Gson().toJson(videos);
				values.put(VIDEOS, sVideos);
			}
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
	

	public String getCompoundArtistName()
	{
		String name = "";
		
		for(Artist artist: artists)
		{
			if( !TextUtils.isEmpty(artist.anv) )
				name += artist.anv;
			else
				name += artist.name;
			
			if( !TextUtils.isEmpty(artist.join) )
				name += " "+artist.join+" ";
			
		}
		
		return name;
	}
	
	public String getCompoundLabelName(Label label)
	{
		String retLabel = "";
		retLabel += label.name;
		if( !TextUtils.isEmpty(label.catno) )
			retLabel += " - " + label.catno;
		
		return retLabel;
	}
	
	public String getCompoundGenresName()
	{
		String retGenres = "";
		
		for(int i=0; i<genres.size(); i++)
		{
			if( i != 0 )
				retGenres += ", ";
			
			retGenres += genres.get(i);
		}
		
		return retGenres;
	}
	
	public String getCompoundStylesName()
	{
		String retStyles = "";
		
		for(int i=0; i<styles.size(); i++)
		{
			if( i != 0 )
				retStyles += ", ";
			
			retStyles += styles.get(i);
		}
		
		return retStyles;
	}
}
