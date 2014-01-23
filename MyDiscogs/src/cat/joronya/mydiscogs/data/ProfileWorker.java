package cat.joronya.mydiscogs.data;

import java.lang.reflect.Type;

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

import cat.joronya.discogs.ProfileSyncer;
import cat.joronya.mydiscogs.MyDiscogs;
import cat.joronya.utils.sync.OnParseResults;

public class ProfileWorker implements OnParseResults
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
			Log.d(MyDiscogs.TAG, "ProfileWorker.sync error: authentication required!");
			return false;
		}
		
		ProfileSyncer ps = new ProfileSyncer(username, true);
		ps.setConsumerKey(MyDiscogs.DISCOGS_CONSUMER_KEY);
		ps.setConsumerSecret(MyDiscogs.DISCOGS_CONSUMER_SECRET);
		ps.setAccessToken(accessToken);
		ps.setAccessSecret(secretToken);
		ps.setUserAgent(MyDiscogs.USER_AGENT);
		ps.setOnParseResultsListener(this);
		ps.sync();
		
		// enviem broadcast de dades de profile updatades
		Intent intent = new Intent(Profile.BROADCAST_UPDATE);
		intent.putExtra(Profile.ERROR_EXTRA, mError);
		intent.putExtra(Profile.USERNAME_EXTRA, username);
		LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
		
		// si no hi ha error, retornem true, en cas d'error false
		return !mError;
	}

	@Override
	public void parseResults(int request, String items, int page, int pages) 
	{
		Log.d(MyDiscogs.TAG, "ProfileWorker.parseResults page: "+page);

		// get object
		Type type = new TypeToken<Profile>(){}.getType();
		Profile profile = new Gson().fromJson(items, type);

		// persist results received
		ContentValues values = profile.toContentValues();
		
		// update or insert
		int updated = mContext.getContentResolver().update(Profile.CONTENT_URI, 
				values, 
				Profile.USERNAME + " = ? ", 
				new String[]{profile.username});
		// no s'ha updatat, fem insert
		if( updated == 0 )
		{
			Uri insertedUri = mContext.getContentResolver().insert(Profile.CONTENT_URI, values);
			Log.d(MyDiscogs.TAG, "ProfileWorker.parseResults inserted uri: "+insertedUri.toString());
		}
		else
		{
			Log.d(MyDiscogs.TAG, "ProfileWorker.parseResults updated: "+updated);
		}
	}

	@Override
	public void parseError(int request, String error) 
	{
		Log.d(MyDiscogs.TAG, "ProfileWorker.parseError error: "+error);
		mError = true;
	}
}
