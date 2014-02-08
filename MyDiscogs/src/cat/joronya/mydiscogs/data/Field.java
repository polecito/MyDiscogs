package cat.joronya.mydiscogs.data;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import cat.joronya.mydiscogs.MyDiscogs;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.BaseColumns;

public class Field  implements BaseColumns 
{
	public static final String ID = "id";
	public int id;
	
	public static final String NAME = "name";
	public String name;
	
	public static final String POSITION = "position";
	public int position;
	
	public static final String PUB = "pub";
	@SerializedName("public")
	public boolean pub;
	
	public static final String TYPE = "type";
	public String type;
	
	public static final String OPTIONS = "options";
	public List<String> options;
	
	public static final String LINES = "lines";
	public int lines;
	
	public static final String AUTHORITY = MyDiscogs.AUTHORITY_BASE+".field";
	
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/fields");
    
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/cat.joronya.mydiscogs.field";

    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/cat.joronya.mydiscogs.field";

    public static final String BROADCAST_UPDATE = "field_updated";
    
    public static final String ERROR_EXTRA = "error";
    
    public static final String[] PROJECTION = new String[]{
    	_ID,		// 0
		ID,			// 1
		NAME,		// 2
		POSITION,	// 3
		PUB,		// 4
		TYPE,		// 5
		OPTIONS,	// 6
		LINES		// 7
    };
    
    public static final String DEFAULT_SORT_ORDER = POSITION+" ASC";
	
	public Field()
	{}
	
	public ContentValues toContentValues()
	{
		ContentValues values = new ContentValues();
		values.put(ID, id);
		values.put(NAME, name);
		values.put(POSITION, position);
		values.put(PUB, pub);
		values.put(TYPE, type);
		String sOptions = new Gson().toJson(options);
		values.put(OPTIONS, sOptions);
		values.put(LINES, lines);
		
		return values;
	}
}
