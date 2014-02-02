package cat.joronya.mydiscogs.data;

import java.lang.reflect.Type;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cat.joronya.discogs.ReleaseSyncer;
import cat.joronya.mydiscogs.MyDiscogs;
import cat.joronya.utils.sync.OnParseResults;

public class ReleaseWorker implements OnParseResults
{
	Context mContext;
	boolean mError = false;
	
	public boolean sync(Context context, int releaseId)
	{
		mContext = context;
		
		// auths settings
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		String accessToken = settings.getString(MyDiscogs.DISCOGS_ACCESS_TOKEN_KEY, null);
		String secretToken = settings.getString(MyDiscogs.DISCOGS_SECRET_TOKEN_KEY, null);
		//String username = settings.getString(MyDiscogs.DISCOGS_USERNAME_KEY, null);
		
		if( accessToken == null || secretToken == null || releaseId == 0 )
		{
			Log.d(MyDiscogs.TAG, "ReleaseWorker.sync error: authentication required!");
			return false;
		}
		
		ReleaseSyncer syncer = new ReleaseSyncer(releaseId, true);
		syncer.setConsumerKey(MyDiscogs.DISCOGS_CONSUMER_KEY);
		syncer.setConsumerSecret(MyDiscogs.DISCOGS_CONSUMER_SECRET);
		syncer.setAccessToken(accessToken);
		syncer.setAccessSecret(secretToken);
		syncer.setUserAgent(MyDiscogs.USER_AGENT);
		syncer.setOnParseResultsListener(this);
		syncer.sync();
		
		// enviem broadcast de dades de profile updatades
		Intent intent = new Intent(Release.BROADCAST_UPDATE);
		intent.putExtra(Release.ERROR_EXTRA, mError);
		LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
		
		// si no hi ha error, retornem true, en cas d'error false
		return !mError;
	}

	@Override
	public void parseResults(int request, String items, int page, int pages) 
	{
		Log.d(MyDiscogs.TAG, "ReleaseWorker.parseResults page: "+page);

		// get object
		Type type = new TypeToken<Release>(){}.getType();
		Release release = new Gson().fromJson(items, type);

		// persist results received
		long downloaded = new Date().getTime();
		ContentValues values = release.toContentValues(true, downloaded);
		
		Uri uri = Uri.withAppendedPath(Release.CONTENT_URI, release.id+"");
		
		// update or insert
		int updated = mContext.getContentResolver().update(uri, values, null, null);
		
		// no s'ha updatat, fem insert
		if( updated == 0 )
		{
			Uri insertedUri = mContext.getContentResolver().insert(Release.CONTENT_URI, values);
			Log.d(MyDiscogs.TAG, "ReleaseWorker.parseResults inserted uri: "+insertedUri.toString());
		}
		else
		{
			Log.d(MyDiscogs.TAG, "ReleaseWorker.parseResults updated: "+updated);
		}
	}

	@Override
	public void parseError(int request, String error) 
	{
		Log.d(MyDiscogs.TAG, "ReleaseWorker.parseError error: "+error);
		mError = true;
	}
}
