package cat.joronya.mydiscogs.data;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import android.content.ContentValues;
import android.net.Uri;
import android.provider.BaseColumns;
import cat.joronya.mydiscogs.MyDiscogs;


public class Artist implements BaseColumns 
{
	public static final String ID = "id";
	public int id;
	
	public static final String NAME = "name";
	public String name;
	
	public static final String TRACKS = "tracks";
	public String tracks;
	
	public static final String ROLE = "role";
	public String role;
	
	public static final String ANV = "anv";
	public String anv;
	
	public static final String JOIN = "join";
	public String join;
	
	// full export fields
	public static final String URI = "uri";
	public String uri;
	
	public static final String REALNAME = "realname";
	public String realname;
	
	public static final String PROFILE = "profile";
	public String profile;
	
	public static final String DATA_QUALITY = "data_quality";
	@SerializedName("data_quality")
	public String dataQuality;
	
	public static final String NAME_VARIATIONS = "name_variations";
	@SerializedName("namevariations")
	public List<String> nameVariations;
	
	public static final String ALIASES = "aliases";
	public List<Alias> aliases;
	
	public static final String URLS = "urls";
	public List<String> urls;
	
	public static final String IMAGES = "images";
	public List<Image> images;
	
	public static final String DOWNLOADED = "downloaded";
	public int downloaded; // timestamp
	
	public static final String IN_COLLECTION = 
		" COUNT("+DatabaseHelper.COLLECTION_TABLE_NAME+"."+Collection._ID+") AS in_collection";
	//public static final String IN_WANTLIST = 
	//	" COUNT("+DatabaseHelper.WANTLIST_TABLE_NAME+"."+Wantlist._ID+") AS in_wantlist";
	
	public static final String AUTHORITY = MyDiscogs.AUTHORITY_BASE+".artist";
	
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/artists");
    
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/cat.joronya.mydiscogs.artist";

    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/cat.joronya.mydiscogs.artist";

    public static final String BROADCAST_UPDATE = "artist_updated";
    
    public static final String ERROR_EXTRA = "error";
    
    public static final String[] PROJECTION = new String[]{
    	DatabaseHelper.ARTISTS_TABLE_NAME+"."+_ID,				// 0
    	DatabaseHelper.ARTISTS_TABLE_NAME+"."+ID,				// 1
    	DatabaseHelper.ARTISTS_TABLE_NAME+"."+NAME,				// 2
    	DatabaseHelper.ARTISTS_TABLE_NAME+"."+URI,				// 3
    	DatabaseHelper.ARTISTS_TABLE_NAME+"."+REALNAME,			// 4
    	DatabaseHelper.ARTISTS_TABLE_NAME+"."+PROFILE,			// 5
    	DatabaseHelper.ARTISTS_TABLE_NAME+"."+DATA_QUALITY,		// 6
    	DatabaseHelper.ARTISTS_TABLE_NAME+"."+NAME_VARIATIONS,	// 7
    	DatabaseHelper.ARTISTS_TABLE_NAME+"."+ALIASES,			// 8
    	DatabaseHelper.ARTISTS_TABLE_NAME+"."+URLS,				// 9
    	DatabaseHelper.ARTISTS_TABLE_NAME+"."+IMAGES,			// 10
    	DatabaseHelper.ARTISTS_TABLE_NAME+"."+DOWNLOADED,		// 11
		IN_COLLECTION											// 12
		//IN_WANTLIST											// 13
    };
    
    public static final String DEFAULT_SORT_ORDER = NAME+" ASC";
	
	public Artist()
	{}
	
	public ContentValues toContentValues(boolean fullExport, int downloadTimestamp)
	{
		ContentValues values = new ContentValues();
		values.put(ID, id);
		values.put(NAME, name);
		
		// items not exportable to ContentValues, they're not on Artist DB, 
		// they're on Release entity
		//values.put(TRACKS, tracks);
		//values.put(ROLE, role);
		//values.put(ANV, anv);
		//values.put(JOIN, join);
		
		// si no estem al llistat, fem full export del Artist per insertar
		// sino nomes els camps id i name
		if( fullExport )
		{
			values.put(URI, uri);
			values.put(REALNAME, realname);
			values.put(PROFILE, profile);
			values.put(DATA_QUALITY, dataQuality);
			String sNameVariations = new Gson().toJson(nameVariations);
			values.put(NAME_VARIATIONS, sNameVariations);
			String sAliases = new Gson().toJson(aliases);
			values.put(ALIASES, sAliases);
			String sUrls = new Gson().toJson(urls);
			values.put(URLS, sUrls);
			String sImages = new Gson().toJson(images);
			values.put(IMAGES, sImages);

			// mark that detail download has been done
			values.put(DOWNLOADED, downloadTimestamp);
		}
		
		return values;
	}
}
