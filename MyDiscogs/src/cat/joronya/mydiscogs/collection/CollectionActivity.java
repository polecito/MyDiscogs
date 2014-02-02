package cat.joronya.mydiscogs.collection;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import cat.joronya.mydiscogs.MyDiscogs;
import cat.joronya.mydiscogs.NeededAuthenticationFragment;
import cat.joronya.mydiscogs.NeededAuthenticationFragment.OnDiscogsLoginListener;
import cat.joronya.mydiscogs.R;
import cat.joronya.mydiscogs.data.Collection;
import cat.joronya.mydiscogs.home.DiscogsActivity;
import cat.joronya.mydiscogs.profile.ProfileActivity;
import cat.joronya.mydiscogs.service.MyDiscogsServiceHelper;
import cat.joronya.utils.ui.DrawerActivity;
import cat.joronya.utils.ui.DrawerItem;
import cat.joronya.utils.ui.MessageDialogFragment;
import cat.joronya.utils.ui.OnMessageDialogListener;
import cat.joronya.utils.ui.OnProgressListener;

public class CollectionActivity extends DrawerActivity
	implements OnDiscogsLoginListener, OnProgressListener, OnMessageDialogListener
{
	public static final String UPDATE_DIALOG = "update_dialog";
	
	MyAdapter mPagerAdapter;
	ViewPager mViewPager;
	Intent mIntent;
	boolean mFirstPercentageReceived;
	
	public static class MyAdapter extends FragmentPagerAdapter 
	{
		List<Fragment> fragments = new ArrayList<Fragment>();
		
        public MyAdapter(FragmentManager fm, Context context) 
        {
            super(fm);
            CollectionFragment releases = new CollectionFragment();
            releases.setTitle(context.getString(R.string.collection_releases_tab_title));
            releases.setSortOrder(Collection.NAME_SORT_ORDER);
            fragments.add(releases);
            
            CollectionFragment recents = new CollectionFragment();
            recents.setTitle(context.getString(R.string.collection_recent_tab_title));
            recents.setSortOrder(Collection.RECENT_SORT_ORDER);
            fragments.add(recents);

            ArtistsFragment artists = new ArtistsFragment();
            artists.setTitle(context.getString(R.string.collection_artists_tab_title));
            fragments.add(artists);

            CollectionFragment labels = new CollectionFragment();
            labels.setTitle(context.getString(R.string.collection_labels_tab_title));
            labels.setSortOrder(Collection.NAME_SORT_ORDER);
            fragments.add(labels);
        }

        @Override
        public int getCount() 
        {
            return fragments.size();
        }

        @Override
        public Fragment getItem(int position) 
        {
            return fragments.get(position);
        }
        
        @Override
        public CharSequence getPageTitle(int position) 
        {
        	OnGetTitle fragment = (OnGetTitle)fragments.get(position);
            return fragment.getTitle();
        }
        
        public void setQuery(String query)
        {
        	for(Fragment fragment: fragments) 
        	{
        		OnSetQuery osq = (OnSetQuery)fragment;
        		osq.setQuery(query);
			}
        }
    }
	
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
    		
        	final ActionBar actionBar = getSupportActionBar();

        	mPagerAdapter = new MyAdapter(getSupportFragmentManager(), this);
        	mViewPager = (ViewPager) findViewById(R.id.pager);
            mViewPager.setAdapter(mPagerAdapter);

        	// Specify that tabs should be displayed in the action bar.
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

            // Create a tab listener that is called when the user changes tabs.
            ActionBar.TabListener tabListener = new ActionBar.TabListener() 
            {
            	@Override
                public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                    // show the given tab
            		mViewPager.setCurrentItem(tab.getPosition());
                }

            	@Override
                public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
                    // hide the given tab
                }

            	@Override
                public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
                    // probably ignore this event
                }
            };

            // Add tabs, specifying the tab's text and TabListener
            for (int i = 0; i < mPagerAdapter.getCount(); i++) 
            {
            	actionBar.addTab(actionBar.newTab()
            		.setText(mPagerAdapter.getPageTitle(i))
                    .setTabListener(tabListener));
            }
            
            mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() 
            {
            	@Override
                public void onPageSelected(int position) 
            	{
            		// When swiping between pages, select the
                    // corresponding tab.
                    getActionBar().setSelectedNavigationItem(position);
            	}
            });
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
        setTitle(getResources().getString(R.string.collection_actionbar_title));
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
		Log.d(MyDiscogs.TAG,"CollectionActivity.selectItem on drawer");

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
	
	// show progress dialog with message and cancelable status
	public void showDialog(String message, String negativeButton, String positiveButton, boolean cancelable) 
	{
		mFirstPercentageReceived = false;
		MessageDialogFragment dialog = MessageDialogFragment.newInstance(message, negativeButton, positiveButton);
		dialog.show(getSupportFragmentManager(), UPDATE_DIALOG);
	}

	// dismiss progress dialog if its available
	public void dismissDialog() 
	{
		MessageDialogFragment dialog = (MessageDialogFragment)getSupportFragmentManager().findFragmentByTag(UPDATE_DIALOG);
		if( dialog != null )
			dialog.dismissAllowingStateLoss();
	} 
	
	public void setProgressValue(int value)
	{
		// quan rebem el primer progress value, recarreguem el fragment pq el ListView no mostri el empty view
		if( !mFirstPercentageReceived )
		{
			mFirstPercentageReceived = true;
			reloadCollectionFragment();
		}
	}

	@Override
	public void onDialogPositiveClick(DialogFragment dialog) 
	{
		// tanquem dialog
		dismissDialog();
		
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String username = settings.getString(MyDiscogs.DISCOGS_USERNAME_KEY, null);
		
		// inciem la tasca
		MyDiscogsServiceHelper.getInstance(this).updateCollection(username);
	}

	@Override
	public void onDialogNegativeClick(DialogFragment dialog) 
	{
		// tanquem dialog
		dismissDialog();
		
		// ocultem el progressbar
		FragmentManager fm = getSupportFragmentManager();
		CollectionFragment cf = (CollectionFragment)fm.findFragmentById(R.id.fragment);
		if( cf != null )
			cf.hideProgressbar();
	}
}
