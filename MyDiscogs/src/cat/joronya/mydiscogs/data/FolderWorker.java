package cat.joronya.mydiscogs.data;

import java.lang.reflect.Type;
import java.util.Collection;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import cat.joronya.discogs.FolderSyncer;
import cat.joronya.mydiscogs.MyDiscogs;
import cat.joronya.utils.sync.OnParseResults;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class FolderWorker implements OnParseResults
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
			Log.d(MyDiscogs.TAG, "FolderWorker.sync error: authentication required!");
			return false;
		}
		
		FolderSyncer syncer = new FolderSyncer(username, true);
		syncer.setConsumerKey(MyDiscogs.DISCOGS_CONSUMER_KEY);
		syncer.setConsumerSecret(MyDiscogs.DISCOGS_CONSUMER_SECRET);
		syncer.setAccessToken(accessToken);
		syncer.setAccessSecret(secretToken);
		syncer.setUserAgent(MyDiscogs.USER_AGENT);
		syncer.setOnParseResultsListener(this);
		syncer.sync();
		
		// enviem broadcast de dades de profile updatades
		Intent intent = new Intent(Folder.BROADCAST_UPDATE);
		intent.putExtra(Folder.ERROR_EXTRA, mError);
		LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
		
		// si no hi ha error, retornem true, en cas d'error false
		return !mError;
	}

	@Override
	public void parseResults(int request, String items, int page, int pages) 
	{
		Log.d(MyDiscogs.TAG, "FolderWorker.parseResults page: "+page);

		// get object
		Type type = new TypeToken<Collection<Folder>>(){}.getType();
		Collection<Folder> folders = new Gson().fromJson(items, type);

		ContentValues[] values = new ContentValues[folders.size()];
		int i=0;
		for(Folder folder: folders) 
		{
			values[i++] = folder.toContentValues();
		}
		
		int count = mContext.getContentResolver().bulkInsert(Folder.CONTENT_URI, values);
		Log.d(MyDiscogs.TAG, "FolderWorker.parseResults updated: "+count);
	}

	@Override
	public void parseError(int request, String error) 
	{
		Log.d(MyDiscogs.TAG, "FolderWorker.parseError error: "+error);
		mError = true;
	}
}
