package cat.joronya.mydiscogs.collection;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import cat.joronya.mydiscogs.MyDiscogs;
import cat.joronya.mydiscogs.NeededAuthenticationFragment;
import cat.joronya.mydiscogs.NeededAuthenticationFragment.OnDiscogsLoginListener;
import cat.joronya.mydiscogs.R;
import cat.joronya.mydiscogs.home.DiscogsActivity;
import cat.joronya.mydiscogs.profile.ProfileActivity;
import cat.joronya.utils.ui.DrawerActivity;
import cat.joronya.utils.ui.DrawerItem;

public class ArtistsActivity extends DrawerActivity
	implements OnDiscogsLoginListener
{
	Intent mIntent;
		
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		// authenticated?
        if( isAuthenticated() )
        {
        	// set layout and call super onCreate
        	setmLayoutResource(R.layout.collection_drawer_layout);
        	setmLayoutId(R.id.drawer_layout);
        	super.onCreate(savedInstanceState);
        	
        	// setejem el fragment d'artistes
        	FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction()
            	.replace(R.id.fragment, new ArtistsFragment())
            	.commit();
        }
        else
        {
        	// set layout and call super onCreate
        	setmLayoutResource(R.layout.drawer_layout);
        	setmLayoutId(R.id.drawer_layout);
        	super.onCreate(savedInstanceState);
        	
        	// setejem el fragment de pagina autenticada
        	FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction()
            	.replace(R.id.fragment, new NeededAuthenticationFragment())
            	.commit();
        }
    	
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
		
		// definim el home button per tirar enrera
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        
        // titol de la pantalla home inicalment
        setTitle(getResources().getString(R.string.artists_actionbar_title));
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

	@Override
	protected DrawerItem[] getDrawerItems() 
	{
		mDrawerTitles = getResources().getStringArray(R.array.drawer_titles);
		mDrawerIcons = getResources().getStringArray(R.array.drawer_icons);

		DrawerItem[] items = new DrawerItem[mDrawerTitles.length];
		for(int i=0; i<mDrawerTitles.length; i++)
		{
			int icon = getResources().getIdentifier(mDrawerIcons[i], "drawable", this.getPackageName());
			items[i] = DrawerItem.create(mDrawerTitles[i], icon);
		}

		return items;
	}

	@Override
	protected void selectItem(int position) 
	{
		setTitle(mDrawerTitles[position]);
		Log.d(MyDiscogs.TAG,"ArtistsActivity.selectItem on drawer");

		switch(position) 
		{
			case 0:
				// discogs activity
				Intent discogsActivity = new Intent(getBaseContext(), DiscogsActivity.class);
				startActivity(discogsActivity);
				overridePendingTransition(0,0);
				break;
			case 1:
				// profile activity
				Intent profileActivity = new Intent(getBaseContext(), ProfileActivity.class);
				startActivity(profileActivity);
				overridePendingTransition(0,0);
				break;
			case 2:
				// collection activity
				break;
			case 3:
				// wantlist activity
				break;
			case 4:
				// marketplace activity
				break;
			default:

			break;
		}

		mDrawerLayout.closeDrawer(mDrawerList);
	}	
}
