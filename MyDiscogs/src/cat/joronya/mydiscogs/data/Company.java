package cat.joronya.mydiscogs.data;

import android.content.ContentValues;
import android.net.Uri;
import android.provider.BaseColumns;
import cat.joronya.mydiscogs.MyDiscogs;

import com.google.gson.annotations.SerializedName;

public class Company implements BaseColumns 
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
	
	public static final String ENTITY_TYPE_NAME = "entity_type_name";
	@SerializedName("entity_type_name")
	public String entityTypeName;
	
	public static final String AUTHORITY = MyDiscogs.AUTHORITY_BASE+".company";
	
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/companies");
    
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/cat.joronya.mydiscogs.company";

    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/cat.joronya.mydiscogs.company";

    public static final String BROADCAST_UPDATE = "company_updated";
    
    public static final String ERROR_EXTRA = "error";
    
    public static final String[] PROJECTION = new String[]{
    	_ID,				// 0
		ID,					// 1
		NAME,				// 2
		CATNO,				// 3
		ENTITY_TYPE,		// 4
		ENTITY_TYPE_NAME	// 5
    };
    
    public static final String DEFAULT_SORT_ORDER = NAME+" ASC";
	
	public Company()
	{}
	
	public ContentValues toContentValues()
	{
		ContentValues values = new ContentValues();
		values.put(ID, id);
		values.put(NAME, name);
		values.put(CATNO, catno);
		values.put(ENTITY_TYPE, entityType);
		values.put(ENTITY_TYPE_NAME, entityTypeName);
		
		return values;
	}
}
