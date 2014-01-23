package cat.joronya.mydiscogs.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import cat.joronya.mydiscogs.MyDiscogs;
import cat.joronya.mydiscogs.R;
import cat.joronya.mydiscogs.collection.CollectionActivity;
import cat.joronya.mydiscogs.profile.ProfileActivity;
import cat.joronya.utils.ui.DrawerActivity;
import cat.joronya.utils.ui.DrawerItem;
import cat.joronya.utils.ui.OnAboutListener;

public class DiscogsActivity extends DrawerActivity
	implements OnAboutListener
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
		
		// definim el home button per tirar enrera
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
		
		// setejem el fragment q toca
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
        	.replace(R.id.fragment, new DiscogsFragment())
        	.commit();
        
        // titol de la pantalla home inicalment
        setTitle(getResources().getString(R.string.discogs_actionbar_title));
	}
	
	@Override
	public void showAboutDialog() 
	{
		Log.d(MyDiscogs.TAG, "DiscogsActivity.showAboutDialog...");
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
		Log.d(MyDiscogs.TAG,"DiscogsActivity.selectItem on drawer");

		switch(position) 
		{
			case 0:
				// discogs activity
				break;
			case 1:
				// profile activity
				Intent profileActivity = new Intent(getBaseContext(), ProfileActivity.class);
				startActivity(profileActivity);
				overridePendingTransition(0,0);
				break;
			case 2:
				// collection activity
				Intent collectionActivity = new Intent(getBaseContext(), CollectionActivity.class);
				startActivity(collectionActivity);
				overridePendingTransition(0,0);
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
