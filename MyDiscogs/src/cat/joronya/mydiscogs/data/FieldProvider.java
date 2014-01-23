package cat.joronya.mydiscogs.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

/**
 * Provides access to a database of profile data.
 */
public class FieldProvider extends ContentProvider 
{
	public static final int FIELDS = 1;
	public static final int FIELD_ID = 2;
	
	public static final UriMatcher uriMatcher;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(Field.AUTHORITY, "fields", FIELDS);
		uriMatcher.addURI(Field.AUTHORITY, "field/#", FIELD_ID);
	}
	
    private DatabaseHelper mOpenHelper;
    
    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }
    
	@Override
	public int delete(Uri uri, String where, String[] whereArgs) 
	{
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		switch (uriMatcher.match(uri)) {
		case FIELDS:
			count = db.delete(DatabaseHelper.FIELDS_TABLE_NAME, where, whereArgs);
			break;

		case FIELD_ID:
			String id = uri.getPathSegments().get(1);
			count = db.delete(DatabaseHelper.FIELDS_TABLE_NAME, Field._ID + "=" + id
					+ (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
	
	@Override
	public String getType(Uri uri) 
	{
		switch (uriMatcher.match(uri)) {
        case FIELDS:
            return Field.CONTENT_TYPE;

        case FIELD_ID:
            return Field.CONTENT_ITEM_TYPE;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) 
	{
		// Validate the requested uri
        if (uriMatcher.match(uri) != FIELDS) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }
        
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.replace(DatabaseHelper.FIELDS_TABLE_NAME, "", values);
        if (rowId > 0) {
            Uri hUri = ContentUris.withAppendedId(Field.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(hUri, null);
            return hUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) 
	{
		SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
		sqlBuilder.setTables(DatabaseHelper.FIELDS_TABLE_NAME);

		// recuperant un jugador concreta
		if( uriMatcher.match(uri) == FIELD_ID )
			sqlBuilder.appendWhere(Field._ID + " = " + uri.getPathSegments().get(1));                

		// If no sort order is specified use the default
        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = Field.DEFAULT_SORT_ORDER;
        } else {
            orderBy = sortOrder;
        }
        
        // Get the database and run the query
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        
        Cursor c = sqlBuilder.query(
			db, 
			projection, 
			selection, 
			selectionArgs, 
			null, 
			null, 
			orderBy);

		// register to watch a content URI for changes
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String where, String[] whereArgs) 
	{
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		switch (uriMatcher.match(uri)) {
		case FIELDS:
			count = db.update(DatabaseHelper.FIELDS_TABLE_NAME, values, where, whereArgs);
			break;

		case FIELD_ID:
			String id = uri.getPathSegments().get(1);
			count = db.update(DatabaseHelper.FIELDS_TABLE_NAME, values, Field._ID + "=" + id
					+ (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
	
	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) 
	{
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count = 0;
		db.beginTransaction();
		
		try 
		{
		    // insertem items, realment fem db.replace
			count = super.bulkInsert(uri, values);
			
		    db.setTransactionSuccessful();
		} 
		finally 
		{
		    db.endTransaction();
		}
		   
		return count;
	}
}
