package cat.joronya.mydiscogs.collection;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import cat.joronya.mydiscogs.MyDiscogs;
import cat.joronya.mydiscogs.NeededAuthenticationFragment;
import cat.joronya.mydiscogs.R;
import cat.joronya.mydiscogs.data.Artist;
import cat.joronya.mydiscogs.data.Community;
import cat.joronya.mydiscogs.data.Company;
import cat.joronya.mydiscogs.data.Format;
import cat.joronya.mydiscogs.data.Identifier;
import cat.joronya.mydiscogs.data.Image;
import cat.joronya.mydiscogs.data.Label;
import cat.joronya.mydiscogs.data.Release;
import cat.joronya.mydiscogs.data.Track;
import cat.joronya.mydiscogs.data.Video;
import cat.joronya.utils.ui.ActionProgressDialogFragment;
import cat.joronya.utils.ui.BaseActionBarActivity;
import cat.joronya.utils.ui.OnProgressListener;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ReleaseDetailActivity extends BaseActionBarActivity
	implements OnProgressListener
{
	public static final String RELEASE_ID_EXTRA = "cat.joronya.mydiscogs.release_id";
	public static final String TITLE_EXTRA = "cat.joronya.mydiscogs.title";
	
	public static final String UPDATE_DIALOG = "update_dialog";
	public static final String UPDATE_TASK = "update_task";
	
	MyAdapter mPagerAdapter;
	ViewPager mViewPager;
	Intent mIntent;
	boolean mFirstPercentageReceived;
	
	int mReleaseId;
	String mTitle;
	Release mRelease;
	
	public static class MyAdapter extends FragmentPagerAdapter 
	{
		List<Fragment> fragments = new ArrayList<Fragment>();
		
        public MyAdapter(FragmentManager fm, Context context) 
        {
            super(fm);
            ReleaseFragment release = new ReleaseFragment();
            release.setTitle(context.getString(R.string.release_release_tab_title));
            fragments.add(release);
            
            MasterFragment master = new MasterFragment();
            master.setTitle(context.getString(R.string.release_master_tab_title));
            fragments.add(master);
            
            MasterFragment collection = new MasterFragment();
            collection.setTitle(context.getString(R.string.release_collection_tab_title));
            fragments.add(collection);
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
        
        public void reloadRelease(Release release)
        {
        	// force all fragments on every tab to reload data
        	for(Fragment fragment: fragments)
        	{
        		if( !(fragment instanceof OnReloadRelease) )
        			continue;
        		
        		OnReloadRelease reloadedFragment = (OnReloadRelease)fragment;
        		reloadedFragment.reloadRelease(release);
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
        
        mReleaseId = intent.getIntExtra(RELEASE_ID_EXTRA, -1);
        mTitle = intent.getStringExtra(TITLE_EXTRA);
        
        // titol de la pantalla home inicalment
        setTitle(mTitle);
        
        // recuperem de DB la release
        mRelease = getRelease();
        
        // comprovem si tenim dades descarregades, sino les tenim
        // cridem el syncer
        if( mRelease == null || mRelease.downloaded == 0 )
        {
        	// mostrem progress dialog
        	showDialog("Retrieving release information...", null, null, false);
        	
        	// cridem el AsyncTask per carregar Release de Discogs
        	FragmentManager fm = getSupportFragmentManager();
        	ReleaseTaskFragment rtf = ReleaseTaskFragment.newInstance(mReleaseId);
        	fm.beginTransaction().add(rtf, UPDATE_TASK).commit();
        }
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
	
	/**
	 * Return Release parsed from database data.
	 * @return
	 */
	private Release getRelease()
	{
		Uri uri = Uri.withAppendedPath(Release.CONTENT_URI, mReleaseId+"");
		
		Release release = null;
		Cursor cursor = getContentResolver().query(uri, Release.PROJECTION, null, null, Release.DEFAULT_SORT_ORDER);
		if( cursor.moveToFirst() )
		{
			release = new Release();
			release._id = cursor.getInt(0);
			release.id = cursor.getInt(1);
			release.title = cursor.getString(2);
			release.year = cursor.getInt(3);
			release.uri = cursor.getString(4);
			release.thumb = cursor.getString(5);
			release.masterId = cursor.getInt(6);
			release.masterUrl = cursor.getString(7);
			release.country = cursor.getString(8);
			release.released = cursor.getString(9);
			release.notes = cursor.getString(10);
			
			String sStyles = cursor.getString(11);
			if( sStyles != null && !"".equals(sStyles) )
			{
				Type type = new TypeToken<List<String>>(){}.getType();
				List<String> styles = new Gson().fromJson(sStyles, type);
				release.styles = styles;
			}
			
			String sGenres = cursor.getString(12);
			if( sGenres != null && !"".equals(sGenres) )
			{
				Type type = new TypeToken<List<String>>(){}.getType();
				List<String> genres = new Gson().fromJson(sGenres, type);
				release.genres = genres;
			}
			
			String sComunity = cursor.getString(13);
			if( sComunity != null && !"".equals(sComunity) )
			{
				Type type = new TypeToken<Community>(){}.getType();
				Community community = new Gson().fromJson(sComunity, type);
				release.community = community;
			}
			
			String sLabels = cursor.getString(14);
			if( sLabels != null && !"".equals(sLabels) )
			{
				Type type = new TypeToken<List<Label>>(){}.getType();
				List<Label> labels = new Gson().fromJson(sLabels, type);
				release.labels = labels;
			}
			
			String sSeries = cursor.getString(15);
			if( sSeries != null && !"".equals(sSeries) )
			{
				Type type = new TypeToken<List<Label>>(){}.getType();
				List<Label> series = new Gson().fromJson(sSeries, type);
				release.series = series;
			}
			
			String sCompanies = cursor.getString(16);
			if( sCompanies != null && !"".equals(sCompanies) )
			{
				Type type = new TypeToken<List<Company>>(){}.getType();
				List<Company> companies = new Gson().fromJson(sCompanies, type);
				release.companies = companies;
			}
			
			String sFormats = cursor.getString(17);
			if( sFormats != null && !"".equals(sFormats) )
			{
				Type type = new TypeToken<List<Format>>(){}.getType();
				List<Format> formats = new Gson().fromJson(sFormats, type);
				release.formats = formats;
			}
			
			String sImages = cursor.getString(18);
			if( sImages != null && !"".equals(sImages) )
			{
				Type type = new TypeToken<List<Image>>(){}.getType();
				List<Image> images = new Gson().fromJson(sImages, type);
				release.images = images;
			}
			
			String sArtists = cursor.getString(19);
			if( sArtists != null && !"".equals(sArtists) )
			{
				Type type = new TypeToken<List<Artist>>(){}.getType();
				List<Artist> artists = new Gson().fromJson(sArtists, type);
				release.artists = artists;
			}
			
			String sExtraArtists = cursor.getString(20);
			if( sExtraArtists != null && !"".equals(sExtraArtists) )
			{
				Type type = new TypeToken<List<Artist>>(){}.getType();
				List<Artist> extraArtists = new Gson().fromJson(sExtraArtists, type);
				release.extraArtists = extraArtists;
			}
			
			String sTracklist = cursor.getString(21);
			if( sTracklist != null && !"".equals(sTracklist) )
			{
				Type type = new TypeToken<List<Track>>(){}.getType();
				List<Track> tracklist = new Gson().fromJson(sTracklist, type);
				release.tracklist = tracklist;
			}
			
			String sIdentifiers = cursor.getString(22);
			if( sIdentifiers != null && !"".equals(sIdentifiers) )
			{
				Type type = new TypeToken<List<Identifier>>(){}.getType();
				List<Identifier> identifiers = new Gson().fromJson(sIdentifiers, type);
				release.identifiers = identifiers;
			}
			
			String sVideos = cursor.getString(23);
			if( sVideos != null && !"".equals(sVideos) )
			{
				Type type = new TypeToken<List<Video>>(){}.getType();
				List<Video> videos = new Gson().fromJson(sVideos, type);
				release.videos = videos;
			}
			
			release.downloaded = cursor.getInt(24);
		}
		
		cursor.close();
		
		return release;
	}
	
	@Override
	public void onResume() 
	{
		// activem el LocalBroadcastReceiver
		LocalBroadcastManager.getInstance(this).
			registerReceiver(mMessageReceiver, new IntentFilter(Release.BROADCAST_UPDATE));
				
		super.onResume();
	}
	
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() 
	{
	    @Override
	    public void onReceive(Context context, Intent intent) 
	    {
	    	// al rebre el primer missatge de percentatge actualitzem el 
	    	// fragment pq mostri la listview i no la empty view
	    	int value = intent.getIntExtra(Release.PERCENT_EXTRA, -1);
	    	ReleaseDetailActivity.this.setProgressValue(value);
	    	
	    	// dismiss el progress si rebem darrer missatge (sense percentatge)
	    	if( value == -1 )
	    	{
	    		ReleaseDetailActivity.this.reloadAndDismiss();
	    	}
	    }
	};
	
	public void reloadAndDismiss()
	{
		mRelease = getRelease();
		mPagerAdapter.reloadRelease(mRelease);
		dismissDialog();
	}

	@Override
	public void showDialog(String message, String negativeButton, String positiveButton, boolean cancelable) 
	{
		mFirstPercentageReceived = false;
		ActionProgressDialogFragment newFragment = ActionProgressDialogFragment.newInstance(message, 
				negativeButton, positiveButton, cancelable);
		newFragment.show(getSupportFragmentManager(), UPDATE_DIALOG);
		newFragment.setCancelable(cancelable);
	}

	@Override
	public void dismissDialog() 
	{
		FragmentManager fm = getSupportFragmentManager();
		ActionProgressDialogFragment dialog = (ActionProgressDialogFragment)fm.findFragmentByTag(UPDATE_DIALOG);
		if( dialog != null )
		{
			dialog.dismissAllowingStateLoss();
			fm.beginTransaction().remove(dialog).commit();
		}
	}

	@Override
	public void setProgressValue(int value){}
}
