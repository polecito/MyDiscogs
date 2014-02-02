package cat.joronya.mydiscogs.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import cat.joronya.mydiscogs.MyDiscogs;

/**
 * This class helps open, create, and upgrade the database file.
 */
public class DatabaseHelper extends SQLiteOpenHelper 
{
	// actual database version
	private static final int DATABASE_VERSION = 5;

	public static final String DATABASE_NAME = "MyDiscogs.db";
	public static final String PROFILE_TABLE_NAME = "profiles";
	public static final String FOLDER_TABLE_NAME = "folders";
	public static final String FIELDS_TABLE_NAME = "fields";
	public static final String RELEASES_TABLE_NAME = "releases";
	public static final String COLLECTION_TABLE_NAME = "collection";
	public static final String WANTLIST_TABLE_NAME = "wantlist";
	public static final String LABELS_TABLE_NAME = "labels";
	public static final String ARTISTS_TABLE_NAME = "artists";
	
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * Clean database install. All upgrades included.
	 */
	@Override
	public void onCreate(SQLiteDatabase db) 
	{
		db.execSQL("CREATE TABLE " + PROFILE_TABLE_NAME + " ( "
				+ Profile._ID + " INTEGER PRIMARY KEY, "
				+ Profile.ID + " INTEGER NOT NULL, "
				+ Profile.USERNAME + " TEXT NOT NULL, "
				+ Profile.NAME + " TEXT NOT NULL, "
				+ Profile.EMAIL + " TEXT, "
				+ Profile.PROFILE + " TEXT, "
				+ Profile.HOME_PAGE + " TEXT, "
				+ Profile.LOCATION + " TEXT, "
				+ Profile.REGISTERED + " TEXT, "
				+ Profile.NUM_LISTS + " INTEGER, "
				+ Profile.NUM_FOR_SALE + " INTEGER, "
				+ Profile.NUM_COLLECTION + " INTEGER, "
				+ Profile.NUM_WANTLIST + " INTEGER, "
				+ Profile.NUM_PENDING + " INTEGER, "
				+ Profile.RELEASES_CONTRIBUTED + " INTEGER, "
				+ Profile.RANK + " FLOAT, "
				+ Profile.RELEASES_RATED + " INTEGER, "
				+ Profile.RATING_AVG + " FLOAT, "
				+ "UNIQUE("+Profile.ID+"), "
				+ "UNIQUE("+Profile.USERNAME+") "
				+ ");");
		
		// we need to apply all upgrades...
		applyUpgrades(db, 1, DATABASE_VERSION);
	}

	/**
	 * Starting from oldVersion installed apply all upgrades in all versions
	 * to newVersion.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		Log.w(MyDiscogs.TAG, "DatabaseHelper.Upgrading database from version " + oldVersion + " to "
				+ newVersion);
		applyUpgrades(db, oldVersion, newVersion);
	}
	
	private void applyUpgrades(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		Log.d(MyDiscogs.TAG, "DatabaseHelper.applyUpgrades oldVersion: "+oldVersion+", newVersion: "+newVersion);
		for( int i=oldVersion+1; i<=newVersion; i++ )
		{
			if( i == 2 )
				upgradeToVersion2(db);
			if( i == 3 )
				upgradeToVersion3(db);
			if( i == 4 )
				upgradeToVersion4(db);
			if( i == 5 )
				upgradeToVersion5(db);
		}
	}

	/**
	 * Update from version #1 to version #2.
	 * Nothing to do.
	 * @param db
	 */
	private void upgradeToVersion2(SQLiteDatabase db)
	{
		Log.d(MyDiscogs.TAG, "DatabaseHelper.upgradeToVersion2... ");
		
		db.execSQL("CREATE TABLE " + FOLDER_TABLE_NAME + " ( "
			+ Folder._ID + " INTEGER PRIMARY KEY, "
			+ Folder.ID + " INTEGER NOT NULL, "
			+ Folder.COUNT + " INTEGER NOT NULL, "
			+ Folder.NAME + " TEXT NOT NULL, "
			+ "UNIQUE("+Folder.ID+") "
			+ ");");
	}

	/**
	 * Update from version #2 to version #3.
	 * Nothing to do.
	 * @param db
	 */
	private void upgradeToVersion3(SQLiteDatabase db)
	{
		Log.d(MyDiscogs.TAG, "DatabaseHelper.upgradeToVersion3... ");
		
		db.execSQL("CREATE TABLE " + FIELDS_TABLE_NAME + " ( "
			+ Field._ID + " INTEGER PRIMARY KEY, "
			+ Field.ID + " INTEGER NOT NULL, "
			+ Field.NAME + " TEXT NOT NULL, "
			+ Field.POSITION + " INTEGER NOT NULL, "
			+ Field.PUB + " INTEGER NOT NULL, "
			+ Field.TYPE + " TEXT NOT NULL, "
			+ Field.OPTIONS + " TEXT, "
			+ Field.LINES + " INTEGER, "
			+ "UNIQUE("+Field.ID+") "
			+ ");");
	}
	
	/**
	 * Update from version #3 to version #4.
	 * Nothing to do.
	 * @param db
	 */
	private void upgradeToVersion4(SQLiteDatabase db)
	{
		Log.d(MyDiscogs.TAG, "DatabaseHelper.upgradeToVersion4... ");
		
		db.execSQL("CREATE TABLE " + RELEASES_TABLE_NAME + " ( "
			+ Release._ID + " INTEGER PRIMARY KEY, "
			+ Release.ID + " INTEGER NOT NULL, "
			+ Release.TITLE + " TEXT NOT NULL, "
			+ Release.YEAR + " INTEGER, "
			+ Release.URI + " TEXT, "
			+ Release.THUMB+ " TEXT, "
			+ Release.MASTER_ID + " INTEGER, "
			+ Release.MASTER_URL + " TEXT, "
			+ Release.COUNTRY + " TEXT, "
			+ Release.RELEASED + " TEXT, "
			+ Release.NOTES + " TEXT, "
			+ Release.STYLES + " TEXT, "
			+ Release.GENRES + " TEXT, "
			+ Release.COMMUNITY + " TEXT, "
			+ Release.LABELS + " TEXT, "
			+ Release.SERIES + " TEXT, "
			+ Release.COMPANIES + " TEXT, "
			+ Release.FORMATS + " TEXT, "
			+ Release.IMAGES + " TEXT, "
			+ Release.ARTISTS + " TEXT, "
			+ Release.EXTRA_ARTISTS + " TEXT, "
			+ Release.TRACKLIST + " TEXT, "
			+ Release.IDENTIFIERS + " TEXT, "
			+ Release.VIDEOS + " TEXT, "
			+ Release.DOWNLOADED + " INTEGER, "
			+ "UNIQUE("+Release.ID+") "
			+ ");");
		
		db.execSQL("CREATE TABLE " + COLLECTION_TABLE_NAME + " ( "
			+ Collection._ID + " INTEGER PRIMARY KEY, "
			+ Collection.RELEASE_ID + " INTEGER NOT NULL, "
			+ Collection.INSTANCE_ID + " INTEGER NOT NULL, "
			+ Collection.FOLDER_ID + " INTEGER NOT NULL, "
			+ Collection.RATING + " INTEGER DEFAULT 0, "
			+ Collection.NOTES + " TEXT, "
			+ "UNIQUE("+Collection.RELEASE_ID+","+Collection.INSTANCE_ID+","+Collection.FOLDER_ID+") "
			+ ");");
	}

	/**
	 * Update from version #4 to version #5.
	 * Nothing to do.
	 * @param db
	 */
	private void upgradeToVersion5(SQLiteDatabase db)
	{
		Log.d(MyDiscogs.TAG, "DatabaseHelper.upgradeToVersion5... ");
		
		db.execSQL("CREATE TABLE " + LABELS_TABLE_NAME + " ( "
			+ Label._ID + " INTEGER PRIMARY KEY, "
			+ Label.ID + " INTEGER NOT NULL, "
			+ Label.NAME + " TEXT NOT NULL, "
			+ Label.ENTITY_TYPE + " TEXT, "
			+ Label.PROFILE + " TEXT, "
			+ Label.URI + " TEXT, "
			+ Label.DATA_QUALITY + " TEXT, "
			+ Label.CONTACT_INFO + " TEXT, "
			+ Label.PARENT_LABEL + " TEXT, "
			+ Label.SUBLABELS + " TEXT, "
			+ Label.URLS + " TEXT, "
			+ Label.IMAGES + " TEXT, "
			+ Label.DOWNLOADED + " INTEGER, "
			+ "UNIQUE("+Label.ID+") "
			+ ");");

		db.execSQL("CREATE TABLE " + ARTISTS_TABLE_NAME + " ( "
			+ Artist._ID + " INTEGER PRIMARY KEY, "
			+ Artist.ID + " INTEGER NOT NULL, "
			+ Artist.NAME + " TEXT NOT NULL, "
			+ Artist.URI + " TEXT, "
			+ Artist.REALNAME + " TEXT, "
			+ Artist.PROFILE + " TEXT, "
			+ Artist.DATA_QUALITY + " TEXT, "
			+ Artist.NAME_VARIATIONS + " TEXT, "
			+ Artist.ALIASES + " TEXT, "
			+ Artist.URLS + " TEXT, "
			+ Artist.IMAGES + " TEXT, "
			+ Artist.DOWNLOADED + " INTEGER, "
			+ "UNIQUE("+Artist.ID+") "
			+ ");");
	}
}
