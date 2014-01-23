package cat.joronya.mydiscogs.data;

import cat.joronya.mydiscogs.MyDiscogs;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.BaseColumns;

public class Folder  implements BaseColumns 
{
	public static final String ID = "id";
	public int id;
	
	public static final String COUNT = "count";
	public int count;
	
	public static final String NAME = "name";
	public String name;
	
	public static final String AUTHORITY = MyDiscogs.AUTHORITY_BASE+".folder";
	
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/folders");
    
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/cat.joronya.mydiscogs.folder";

    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/cat.joronya.mydiscogs.folder";

    public static final String BROADCAST_UPDATE = "folder_updated";
    
    public static final String ERROR_EXTRA = "error";
    
    public static final String[] PROJECTION = new String[]{
    	_ID,	// 0
		ID,		// 1
		COUNT,	// 2
		NAME	// 3
    };
    
    public static final String DEFAULT_SORT_ORDER = NAME+" ASC";
	
	public Folder()
	{}
	
	public ContentValues toContentValues()
	{
		ContentValues values = new ContentValues();
		values.put(ID, id);
		values.put(COUNT, count);
		values.put(NAME, name);
		
		return values;
	}
}
