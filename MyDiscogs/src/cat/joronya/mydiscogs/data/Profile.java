package cat.joronya.mydiscogs.data;

import android.content.ContentValues;
import android.net.Uri;
import android.provider.BaseColumns;
import cat.joronya.mydiscogs.MyDiscogs;

import com.google.gson.annotations.SerializedName;

public class Profile implements BaseColumns 
{
	public static final String ID = "id";
	public int id;
	
	public static final String USERNAME = "username";
	public String username;
	
	public static final String NAME = "name";
	public String name;
	
	public static final String EMAIL = "email";
	public String email;
	
	public static final String PROFILE = "profile";
	public String profile;
	
	public static final String HOME_PAGE = "home_page";
	@SerializedName("home_page")
	public String homePage;
	
	public static final String LOCATION = "location";
	public String location;
	
	public static final String REGISTERED = "registered";
	public String registered;
	
	public static final String NUM_LISTS = "num_lists";
	@SerializedName("num_lists")
	public int numLists;
	
	public static final String NUM_FOR_SALE = "num_for_sale";
	@SerializedName("num_for_sale")
	public int numForSale;
	
	public static final String NUM_COLLECTION = "num_collection";
	@SerializedName("num_collection")
	public int numCollection;
	
	public static final String NUM_WANTLIST = "num_wantlist";
	@SerializedName("num_wantlist")
	public int numWantlist;
	
	public static final String NUM_PENDING = "num_pending";
	@SerializedName("num_pending")
	public int numPending;
	
	public static final String RELEASES_CONTRIBUTED = "releases_contributed";
	@SerializedName("releases_contributed")
	public int releasesContributed;
	
	public static final String RANK = "rank";
	public float rank;
	
	public static final String RELEASES_RATED = "releases_rated";
	@SerializedName("releases_rated")
	public int releasesRated;
	
	public static final String RATING_AVG = "rating_avg";
	@SerializedName("rating_avg")
	public float ratingAvg;
	
	public static final String AUTHORITY = MyDiscogs.AUTHORITY_BASE+".profile";
	
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/profiles");
    
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/cat.joronya.mydiscogs.profile";

    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/cat.joronya.mydiscogs.profile";

    public static final String BROADCAST_UPDATE = "profile_updated";
    
    public static final String ERROR_EXTRA = "error";
    
    public static final String USERNAME_EXTRA = "username";
    
    public static final String[] PROJECTION = new String[]{
    	_ID,					// 0
		ID,						// 1
		USERNAME,				// 2
		NAME,					// 3
		EMAIL,					// 4
		PROFILE,				// 5
		HOME_PAGE,				// 6
		LOCATION,				// 7
		REGISTERED,				// 8
		NUM_LISTS,				// 9
		NUM_FOR_SALE,			// 10
		NUM_COLLECTION,			// 11
		NUM_WANTLIST,			// 12
		NUM_PENDING,			// 13
		RELEASES_CONTRIBUTED,	// 14
		RANK,					// 15
		RELEASES_RATED,			// 16
		RATING_AVG				// 17
	};
	
    public static final String DEFAULT_SORT_ORDER = NAME+" ASC";
	
	public Profile()
	{}
	
	public ContentValues toContentValues()
	{
		ContentValues values = new ContentValues();
		values.put(ID, id);
		values.put(USERNAME, username);
		values.put(NAME, name);
		values.put(EMAIL, email);
		values.put(PROFILE, profile);
		values.put(HOME_PAGE, homePage);
		values.put(LOCATION, location);
		values.put(REGISTERED, registered);
		values.put(NUM_LISTS, numLists);
		values.put(NUM_FOR_SALE, numForSale);
		values.put(NUM_COLLECTION, numCollection);
		values.put(NUM_WANTLIST, numWantlist);
		values.put(NUM_PENDING, numPending);
		values.put(RELEASES_CONTRIBUTED, releasesContributed);
		values.put(RANK, rank);
		values.put(RELEASES_RATED, releasesRated);
		values.put(RATING_AVG, ratingAvg);
		
		return values;
	}
}
