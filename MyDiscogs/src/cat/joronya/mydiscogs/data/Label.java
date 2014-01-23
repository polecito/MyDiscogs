package cat.joronya.mydiscogs.data;

import java.util.List;

import android.content.ContentValues;
import android.net.Uri;
import android.provider.BaseColumns;
import cat.joronya.mydiscogs.MyDiscogs;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class Label implements BaseColumns 
{
	public static final String ID = "id";
	public int id;
	
	public static final String NAME = "name";
	public String name;
	
	public static final String CATNO = "catno";
	public String catno;
	
	public static final String ENTITY_TYPE = "entity_type";
	@SerializedName("entity_type")
	public String entityType;
	
	// detail fields
	public static final String PROFILE = "profile";
	public String profile;
	
	public static final String URI = "uri";
	public String uri;
	
	public static final String DATA_QUALITY = "data_quality";
	@SerializedName("data_quality")
	public String dataQuality;
	
	public static final String CONTACT_INFO = "contact_info";
	@SerializedName("contact_info")
	public String contactInfo;
	
	public static final String PARENT_LABEL = "parent_label";
	@SerializedName("parent_label")
	public String parentLabel;
	
	public static final String SUBLABELS = "sublabels";
	public List<Label> sublabels;
	
	public static final String URLS = "urls";
	public List<String> urls;
	
	public static final String IMAGES = "images";
	public List<Image> images;
	
	public static final String DOWNLOADED = "downloaded";
	public int downloaded; // timestamp
	
	public static final String AUTHORITY = MyDiscogs.AUTHORITY_BASE+".label";
	
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/labels");
    
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/cat.joronya.mydiscogs.label";

    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/cat.joronya.mydiscogs.label";

    public static final String BROADCAST_UPDATE = "label_updated";
    
    public static final String ERROR_EXTRA = "error";
    
    public static final String[] PROJECTION = new String[]{
    	_ID,			// 0
		ID,				// 1
		NAME,			// 2
		CATNO,			// 3
		ENTITY_TYPE,	// 4
		PROFILE,		// 5
		URI,			// 6
		DATA_QUALITY,	// 7
		CONTACT_INFO,	// 8
		PARENT_LABEL,	// 9
		SUBLABELS,		// 10
		URLS,			// 11
		IMAGES,			// 12
		DOWNLOADED		// 13
    };
    
    public static final String DEFAULT_SORT_ORDER = NAME+" ASC";
	
	public Label()
	{}
	
	public ContentValues toContentValues(boolean fullExport, int downloadTimestamp)
	{
		ContentValues values = new ContentValues();
		values.put(ID, id);
		values.put(NAME, name);
		values.put(ENTITY_TYPE, entityType);
		
		// this is not really saved on Database Label table,
		// it's on each release array
		//values.put(CATNO, catno);
		
		// when saving label detail, this is full export, needed
		// all label attributes on database
		if( fullExport )
		{
			values.put(PROFILE, profile);
			values.put(URI, uri);
			values.put(DATA_QUALITY, dataQuality);
			values.put(CONTACT_INFO, contactInfo);
			String sParentLabel = new Gson().toJson(parentLabel);
			values.put(PARENT_LABEL, sParentLabel);
			String sSublabels = new Gson().toJson(sublabels);
			values.put(SUBLABELS, sSublabels);
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
