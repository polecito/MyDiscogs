package cat.joronya.mydiscogs.data;

import java.lang.reflect.Type;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import cat.joronya.discogs.CollectionReleasesInFolderSyncer;
import cat.joronya.mydiscogs.MyDiscogs;
import cat.joronya.utils.sync.OnParseResults;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class CollectionWorker implements OnParseResults
{
	Context mContext;
	boolean mError = false;
	
	public boolean sync(Context context, String username)
	{
		mContext = context;
		
		// auths settings
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		String accessToken = settings.getString(MyDiscogs.DISCOGS_ACCESS_TOKEN_KEY, null);
		String secretToken = settings.getString(MyDiscogs.DISCOGS_SECRET_TOKEN_KEY, null);
		//String username = settings.getString(MyDiscogs.DISCOGS_USERNAME_KEY, null);
		
		if( accessToken == null || secretToken == null || username == null )
		{
			Log.d(MyDiscogs.TAG, "CollectionWorker.sync error: authentication required!");
			return false;
		}
		
		// recuperem releases del folder 0 (All)
		//TODO: recuperar tots els folders parsejats anteriorment amb count>0
		CollectionReleasesInFolderSyncer syncer = new CollectionReleasesInFolderSyncer(username, 0, true);
		syncer.setConsumerKey(MyDiscogs.DISCOGS_CONSUMER_KEY);
		syncer.setConsumerSecret(MyDiscogs.DISCOGS_CONSUMER_SECRET);
		syncer.setAccessToken(accessToken);
		syncer.setAccessSecret(secretToken);
		syncer.setUserAgent(MyDiscogs.USER_AGENT);
		syncer.setOnParseResultsListener(this);
		syncer.sync();
		
		// enviem broadcast de dades de profile updatades
		Intent intent = new Intent(Collection.BROADCAST_UPDATE);
		intent.putExtra(Collection.ERROR_EXTRA, mError);
		LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
		
		// si no hi ha error, retornem true, en cas d'error false
		return !mError;
	}

	@Override
	public void parseResults(int request, String items, int page, int pages) 
	{
		Log.d(MyDiscogs.TAG, "CollectionWorker.parseResults page: "+page);

		// get object
		Type type = new TypeToken<java.util.Collection<Collection>>(){}.getType();
		java.util.Collection<Collection> collections = new Gson().fromJson(items, type);

		// collection and releases registers
		ArrayList<ContentValues> colValues = new ArrayList<ContentValues>();
		ArrayList<ContentValues> relValues = new ArrayList<ContentValues>();
		ArrayList<ContentValues> artValues = new ArrayList<ContentValues>();
		ArrayList<ContentValues> labValues = new ArrayList<ContentValues>();
		for(Collection col: collections) 
		{
			// export collection values
			colValues.add(col.toContentValues());
			// disable full export of release attributes
			relValues.add(col.basicInformation.toContentValues(false, 0));
			// afegim els artistes de la release (ve sense full export)
			artValues.addAll(col.basicInformation.getArtists());
			// afegim els labels de la release (ve sense full export)
			labValues.addAll(col.basicInformation.getLabels());
		}
		
		ContentValues artValues2[] = new ContentValues[]{};
		artValues2 = artValues.toArray(artValues2);
		int artCount = mContext.getContentResolver().bulkInsert(Artist.CONTENT_URI, artValues2);
		Log.d(MyDiscogs.TAG, "CollectionWorker.parseResults artists updated: "+artCount);
		
		ContentValues labValues2[] = new ContentValues[]{};
		labValues2 = labValues.toArray(labValues2);
		int labCount = mContext.getContentResolver().bulkInsert(Label.CONTENT_URI, labValues2);
		Log.d(MyDiscogs.TAG, "CollectionWorker.parseResults artists updated: "+labCount);
		
		ContentValues relValues2[] = new ContentValues[]{};
		relValues2 = relValues.toArray(relValues2);
		int relCount = mContext.getContentResolver().bulkInsert(Release.CONTENT_URI, relValues2);
		Log.d(MyDiscogs.TAG, "CollectionWorker.parseResults releases updated: "+relCount);
		
		ContentValues colValues2[] = new ContentValues[]{};
		colValues2 = colValues.toArray(colValues2);
		int colCount = mContext.getContentResolver().bulkInsert(Collection.CONTENT_URI, colValues2);
		Log.d(MyDiscogs.TAG, "CollectionWorker.parseResults collections updated: "+colCount);
		
		// enviem broadcast de dades de profile updatades
		Intent intent = new Intent(Collection.BROADCAST_UPDATE);
		int percentage = (100 * page)/pages;
		intent.putExtra(Collection.PERCENT_EXTRA, percentage);
		LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
	}

	@Override
	public void parseError(int request, String error) 
	{
		Log.d(MyDiscogs.TAG, "CollectionWorker.parseError error: "+error);
		mError = true;
	}
}
