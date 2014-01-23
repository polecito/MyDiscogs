package cat.joronya.mydiscogs.data;

import java.util.List;

import android.content.ContentValues;
import android.net.Uri;
import android.provider.BaseColumns;
import cat.joronya.mydiscogs.MyDiscogs;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class Collection implements BaseColumns 
{
	public static final String RELEASE_ID = "release_id";
	@SerializedName("id")
	public int releaseId;
	
	public static final String INSTANCE_ID = "instance_id";
	@SerializedName("instance_id")
	public int instanceId;
	
	public static final String FOLDER_ID = "folder_id";
	@SerializedName("folder_id")
	public int folderId;
	
	public static final String RATING = "rating";
	public int rating;
	
	public static final String NOTES = "collection_notes";
	@SerializedName("notes")
	public List<Notes> notes;
	
	// thumb ve dins el basic_information (release resumit) al llistat nomes
	public static final String THUMB = "thumb";
	
	public static final String BASIC_INFORMATION = "basic_information";
	@SerializedName("basic_information")
	public Release basicInformation;
	
	public static final String AUTHORITY = MyDiscogs.AUTHORITY_BASE+".collection";
	
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/collections");
    
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/cat.joronya.mydiscogs.collection";

    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/cat.joronya.mydiscogs.collection";

    public static final String BROADCAST_UPDATE = "collection_updated";
    
    public static final String ERROR_EXTRA = "error";
    
    public static final String PERCENT_EXTRA = "percent";
    
    public static final String[] PROJECTION = new String[]{
    	DatabaseHelper.COLLECTION_TABLE_NAME+"."+_ID,	// 0
		RELEASE_ID,										// 1
		INSTANCE_ID,									// 2
		FOLDER_ID,										// 3
		RATING,											// 4
		NOTES,											// 5
		THUMB,											// 6
		Release.TITLE,									// 7
		Release.ARTISTS									// 8
    };
    
    public static final String NAME_SORT_ORDER = DatabaseHelper.RELEASES_TABLE_NAME+"."+Release.TITLE+" ASC";

    public static final String RECENT_SORT_ORDER = DatabaseHelper.COLLECTION_TABLE_NAME+"."+_ID+" DESC";
    
    public static final String RATING_SORT_ORDER = DatabaseHelper.COLLECTION_TABLE_NAME+"."+RATING+" DESC";
    
	public Collection()
	{}
	
	public ContentValues toContentValues()
	{
		ContentValues values = new ContentValues();
		values.put(RELEASE_ID, releaseId);
		values.put(INSTANCE_ID, instanceId);
		values.put(FOLDER_ID, folderId);
		values.put(RATING, rating);
		String sNotes = new Gson().toJson(notes);
		values.put(NOTES, sNotes);
		values.put(THUMB, basicInformation.thumb);
		
		return values;
	}
}
