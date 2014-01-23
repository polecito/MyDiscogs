package cat.joronya.mydiscogs.collection;

import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import cat.joronya.mydiscogs.MyDiscogs;
import cat.joronya.mydiscogs.NeededAuthenticationFragment;
import cat.joronya.mydiscogs.R;
import cat.joronya.utils.ui.BaseActionBarActivity;
import cat.joronya.utils.ui.OnProgressListener;

public class CollectionSearchResultsActivity extends BaseActionBarActivity
	implements OnProgressListener
{
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		// set layout and call super onCreate
      	setmLayoutResource(R.layout.drawer_layout);
       	setmLayoutId(R.id.drawer_layout);
       	super.onCreate(savedInstanceState);
    	
    	handleIntent(getIntent());
	}
	
	@Override
	protected void onNewIntent(Intent intent) 
	{
		super.onNewIntent(intent);
		
		handleIntent(intent);
	}
	
	protected void handleIntent(Intent intent)
	{
		// definim el home button per tirar enrera
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        
        String query = null;
        if( Intent.ACTION_SEARCH.equals(intent.getAction()) )
            query = intent.getStringExtra(SearchManager.QUERY);
        
        // authenticated?
        if( isAuthenticated() )
        {
        	CollectionFragment cf = new CollectionFragment();
        	cf.setQuery(query);
        	cf.setShowMenu(false);
        	
        	FragmentManager fm = getSupportFragmentManager();
        	fm.beginTransaction()
        		.replace(R.id.fragment, cf)
        		.commit();
        }
        else
        {
        	// setejem el fragment de pagina autenticada
        	FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction()
            	.replace(R.id.fragment, new NeededAuthenticationFragment())
            	.commit();
        }
        
        setTitle(query);
	}
	
	private boolean isAuthenticated()
	{
		// auths settings
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String accessToken = settings.getString(MyDiscogs.DISCOGS_ACCESS_TOKEN_KEY, null);
		String secretToken = settings.getString(MyDiscogs.DISCOGS_SECRET_TOKEN_KEY, null);
		String username = settings.getString(MyDiscogs.DISCOGS_USERNAME_KEY, null);
		
		if( accessToken != null && secretToken != null && username != null )
			return true;
		
		return false;
	}

	@Override
	public void showDialog(String message, String negativeButton, String positiveButton, boolean cancelable) 
	{
	}

	@Override
	public void dismissDialog() 
	{
	}

	@Override
	public void setProgressValue(int value) 
	{
	}
}
