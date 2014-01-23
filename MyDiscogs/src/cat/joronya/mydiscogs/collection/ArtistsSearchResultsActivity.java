package cat.joronya.mydiscogs.collection;

import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.view.View;
import cat.joronya.mydiscogs.MyDiscogs;
import cat.joronya.mydiscogs.NeededAuthenticationFragment;
import cat.joronya.mydiscogs.NeededAuthenticationFragment.OnDiscogsLoginListener;
import cat.joronya.mydiscogs.R;
import cat.joronya.mydiscogs.profile.ProfileActivity;
import cat.joronya.utils.ui.BaseActionBarActivity;

public class ArtistsSearchResultsActivity extends BaseActionBarActivity
	implements OnDiscogsLoginListener
{
	Intent mIntent;
		
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
		mIntent = intent;
		
		 String query = null;
	     if( Intent.ACTION_SEARCH.equals(intent.getAction()) )
	    	 query = intent.getStringExtra(SearchManager.QUERY);
	     
	     // authenticated?
	     if( isAuthenticated() )
	     {
	    	 ArtistsFragment af = new ArtistsFragment();
	    	 af.setQuery(query);
	    	 af.setShowMenu(false);
	    	 
	    	 // setejem el fragment d'artistes
	    	 FragmentManager fm = getSupportFragmentManager();
	    	 fm.beginTransaction()
	    	 .replace(R.id.fragment, af)
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
		
		// definim el home button per tirar enrera
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        
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
	public void discogsLogin(View view) 
	{
		// call ProfileActivity where can start login process
		Intent profileActivity = new Intent(getBaseContext(), ProfileActivity.class);
		startActivity(profileActivity);
		overridePendingTransition(0,0);
	}
	
	public void reloadCollectionFragment()
	{
		// setejem el fragment de collection
        //FragmentManager fm = getSupportFragmentManager();
        //fm.beginTransaction()
        //	.replace(R.id.fragment, new CollectionFragment())
        //	.commit();
	}
}
