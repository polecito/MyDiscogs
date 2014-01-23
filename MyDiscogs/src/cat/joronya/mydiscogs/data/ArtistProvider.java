package cat.joronya.mydiscogs.data;

import java.util.ArrayList;

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
public class ArtistProvider extends ContentProvider 
{
	public static final int ARTISTS = 1;
	public static final int ARTIST_ID = 2;
	
	public static final UriMatcher uriMatcher;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(Artist.AUTHORITY, "artists", ARTISTS);
		uriMatcher.addURI(Artist.AUTHORITY, "artist/#", ARTIST_ID);
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
		case ARTISTS:
			count = db.delete(DatabaseHelper.ARTISTS_TABLE_NAME, where, whereArgs);
			break;

		case ARTIST_ID:
			String id = uri.getPathSegments().get(1);
			count = db.delete(DatabaseHelper.ARTISTS_TABLE_NAME, Artist._ID + "=" + id
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
        case ARTISTS:
            return Artist.CONTENT_TYPE;

        case ARTIST_ID:
            return Artist.CONTENT_ITEM_TYPE;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) 
	{
		// Validate the requested uri
        if (uriMatcher.match(uri) != ARTISTS) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }
        
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.replace(DatabaseHelper.ARTISTS_TABLE_NAME, "", values);
        if (rowId > 0) {
            Uri hUri = ContentUris.withAppendedId(Artist.CONTENT_URI, rowId);
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
		sqlBuilder.setTables(DatabaseHelper.ARTISTS_TABLE_NAME+
			" LEFT JOIN "+DatabaseHelper.RELEASES_TABLE_NAME+
			" ON ( "+
				DatabaseHelper.RELEASES_TABLE_NAME+"."+Release.ARTISTS+" LIKE "+
				" '%' || "+DatabaseHelper.ARTISTS_TABLE_NAME+"."+Artist.ID+" || '%' "+
			" ) "+
			" LEFT JOIN "+DatabaseHelper.COLLECTION_TABLE_NAME+
			" ON ("+
				DatabaseHelper.COLLECTION_TABLE_NAME+"."+Collection.RELEASE_ID+" = "+
				DatabaseHelper.RELEASES_TABLE_NAME+"."+Release.ID+
			" ) ");

		// recuperant un jugador concreta
		if( uriMatcher.match(uri) == ARTIST_ID )
			sqlBuilder.appendWhere(Artist._ID + " = " + uri.getPathSegments().get(1));                

		String groupBy = "";
		ArrayList<String> groupByList = new ArrayList<String>();
		for( int i=0; i<projection.length; i++ )
		{
			if( Artist.IN_COLLECTION.equals(projection[i]) )
				continue;
			//if( Artist.IN_WANTLIST.equals(projection[i]) )
			//	continue;
			
			groupByList.add(projection[i]);
		}
		boolean first = true;
		for( String field: groupByList )
		{
			if( first )
			{
				groupBy += field;
				first = false;
			}
			else
			{
				groupBy += ", "+field;
			}
		}
		
		// If no sort order is specified use the default
        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = Artist.DEFAULT_SORT_ORDER;
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
			groupBy, 
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
		case ARTISTS:
			count = db.update(DatabaseHelper.ARTISTS_TABLE_NAME, values, where, whereArgs);
			break;

		case ARTIST_ID:
			String id = uri.getPathSegments().get(1);
			count = db.update(DatabaseHelper.ARTISTS_TABLE_NAME, values, Artist._ID + "=" + id
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
			// insert dels items, realment es un db.replace
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
