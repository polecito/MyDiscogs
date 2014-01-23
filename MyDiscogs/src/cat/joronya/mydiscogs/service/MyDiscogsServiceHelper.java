package cat.joronya.mydiscogs.service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import cat.joronya.mydiscogs.MyDiscogs;
import cat.joronya.mydiscogs.data.Collection;
import cat.joronya.mydiscogs.data.Profile;

/**
 * Exposa l'API de metodes que podem cridar a la
 * web.
 * @author pol
 *
 */
public class MyDiscogsServiceHelper 
{
	private static MyDiscogsServiceHelper mSingleton = null;
	
	public static MyDiscogsServiceHelper getInstance(Context context)
	{
		if(mSingleton == null)
			mSingleton = new MyDiscogsServiceHelper(context);
		
		return mSingleton;
	}
	
	private Context mContext;
	
	public MyDiscogsServiceHelper(Context context)
	{
		// el necessitem per crear els intents i per les prefs.
		mContext = context;
	}
	
	/**
	 * Recuperem el profile del username passat.
	 */
	public void profile(String username)
	{
		Log.d(MyDiscogs.TAG,"MyDiscogsServiceHelper.profile('"+username+"')");
		
		// preparem intent
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, Profile.CONTENT_URI, mContext, MyDiscogsService.class);
		intent.putExtra(MyDiscogs.USERNAME_EXTRA, username);
		
		// executem l'intent al service
		mContext.startService(intent);
	}

	public void updateCollection(String username)
	{
		Log.d(MyDiscogs.TAG,"MyDiscogsServiceHelper.updateCollection('"+username+"')");
		
		// preparem intent
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, Collection.CONTENT_URI, mContext, MyDiscogsService.class);
		intent.putExtra(MyDiscogs.USERNAME_EXTRA, username);
		
		// executem l'intent al service
		mContext.startService(intent);
	}
}
