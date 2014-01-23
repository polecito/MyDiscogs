package cat.joronya.mydiscogs.profile;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import cat.joronya.discogs.IdentitySyncer;
import cat.joronya.discogs.ProfileSyncer;
import cat.joronya.discogs.oauth.DiscogsAuthFragment;
import cat.joronya.discogs.oauth.DiscogsAuthFragment.OnDiscogsAuth;
import cat.joronya.mydiscogs.MyDiscogs;
import cat.joronya.mydiscogs.NeededAuthenticationFragment.OnDiscogsLoginListener;
import cat.joronya.mydiscogs.R;
import cat.joronya.mydiscogs.collection.CollectionActivity;
import cat.joronya.mydiscogs.home.DiscogsActivity;
import cat.joronya.mydiscogs.service.MyDiscogsServiceHelper;
import cat.joronya.utils.sync.OnParseResults;
import cat.joronya.utils.ui.ActionProgressDialogFragment;
import cat.joronya.utils.ui.DrawerActivity;
import cat.joronya.utils.ui.DrawerItem;
import cat.joronya.utils.ui.OnProgressListener;

public class ProfileActivity extends DrawerActivity
	implements OnDiscogsLoginListener, OnProgressListener, OnDiscogsAuth, OnParseResults
{
	public static int IDENTITY_REQUEST = 0;
	public static int PROFILE_REQUEST = 1;
	
	public static final String UPDATE_PROGRESS = "progress_dialog";
	
	Intent mIntent;
	String mUsername;
	
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
		
        // authenticated?
        if( isAuthenticated() )
        {
        	SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        	mUsername = settings.getString(MyDiscogs.DISCOGS_USERNAME_KEY, null);
        	
    		// setejem el fragment de collection
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction()
            	.replace(R.id.fragment, new ProfileFragment())
            	.commit();
            
            // titol de la pantalla home inicalment
            setTitle(getResources().getString(R.string.profile_actionbar_title));
        }
        else
        {
        	// inicialitzem el auth fragment, si encara no l'hem iniciat
    		FragmentManager fm = getSupportFragmentManager();
    		DiscogsAuthFragment af = (DiscogsAuthFragment)fm.findFragmentByTag(MyDiscogs.DISCOGS_AUTH_FRAGMENT_KEY);
    		if( af == null )
    		{
    			fm.beginTransaction()
    				.add(new DiscogsAuthFragment(), MyDiscogs.DISCOGS_AUTH_FRAGMENT_KEY)
    				.commit();
    		}
    		
    		// setejem el fragment de login
            fm.beginTransaction()
            	.replace(R.id.fragment, new LoginFragment())
            	.commit();
            
            // titol de la pantalla home inicalment
            setTitle(getResources().getString(R.string.login_actionbar_title));
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
	
	@Override
	protected void onResume() 
	{
		super.onResume();
		
		// revisem si venim del authorization de OAuth
		Uri uri = mIntent.getData();
	    if( uri == null)
		    return;
	    
	    FragmentManager fm = getSupportFragmentManager();
		DiscogsAuthFragment af = (DiscogsAuthFragment)fm.findFragmentByTag(MyDiscogs.DISCOGS_AUTH_FRAGMENT_KEY);
			
		if( af == null )
			return;
			
		// si rebem URL amb verifier, recuperem access token
	    showDialog(getString(R.string.login_text_progress), null, null, false);
		af.getAccessToken(uri);
	}
	
	// show progress dialog with message and cancelable status
	public void showDialog(String message, String negativeButton, String positiveButton, boolean cancelable) 
	{
		ActionProgressDialogFragment newFragment = ActionProgressDialogFragment.newInstance(message, 
				negativeButton, positiveButton, cancelable);
		newFragment.show(getSupportFragmentManager(), "progress_dialog");
		newFragment.setCancelable(cancelable);
	}

	// dismiss progress dialog if its available
	public void dismissDialog() 
	{
		ActionProgressDialogFragment dialog = (ActionProgressDialogFragment)getSupportFragmentManager().
				findFragmentByTag("progress_dialog");
		if( dialog != null )
			dialog.dismissAllowingStateLoss();
	} 
	
	public void setProgressValue(int value){}
	
	public void discogsLogin(View view)
	{
		Log.d(MyDiscogs.TAG, "ProfileActivity.discogsLogin...");
		
		FragmentManager fm = getSupportFragmentManager();
		DiscogsAuthFragment af = (DiscogsAuthFragment)fm.findFragmentByTag(MyDiscogs.DISCOGS_AUTH_FRAGMENT_KEY);
		
		showDialog(getString(R.string.login_text_progress), null, null, false);
		af.requestToken(MyDiscogs.DISCOGS_CONSUMER_KEY, 
				MyDiscogs.DISCOGS_CONSUMER_SECRET, 
				MyDiscogs.DISCOGS_REQUEST_TOKEN_URL,
				MyDiscogs.DISCOGS_ACCESS_TOKEN_URL, 
				MyDiscogs.DISCOGS_AUTHORIZE_URL,
				MyDiscogs.DISCOGS_CALLBACK_URL);
	}
	
	public void discogsRegister(View view)
	{
		Log.d(MyDiscogs.TAG, "ProfileActivity.discogsRegister...");
		
		// init discogs register webpage 
		startActivity(new Intent("android.intent.action.VIEW", Uri.parse(MyDiscogs.DISCOGS_REGISTER_URL)));
	}
	
	public void discogsProfile(View view)
	{
		Log.d(MyDiscogs.TAG, "ProfileActivity.discogsProfile...");
		
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String username = settings.getString(MyDiscogs.DISCOGS_USERNAME_KEY, null);
		
		showDialog(getString(R.string.profile_text_progress), null, null, false);
		MyDiscogsServiceHelper.getInstance(getApplicationContext()).profile(username);
	}
	
	public void showHomePage(View view)
	{
		Log.d(MyDiscogs.TAG, "ProfileActivity.showHomePage...");
		
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String username = settings.getString(MyDiscogs.DISCOGS_USERNAME_KEY, null);
		
		// init discogs register webpage 
		startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://www.discogs.com/user/"+username)));
	}
	
	@Override
	public void authorizationSuccess(String uri) 
	{
		Log.d(MyDiscogs.TAG, "ProfileActivity.authorizationSuccess...");
		dismissDialog();
		startActivity(new Intent("android.intent.action.VIEW", Uri.parse(uri)));
	}

	@Override
	public void authorizationFailure() 
	{
		Log.d(MyDiscogs.TAG, "ProfileActivity.authorizationFailure...");
		dismissDialog();
	}

	@Override
	public void accessSuccess(String theAccessTokem, String theAccessSecret) 
	{
		final String accessToken = theAccessTokem;
		final String accessSecret = theAccessSecret; 
		new Thread()
		{
			@Override
			public void run() 
			{
				Log.d(MyDiscogs.TAG, "ProfileActivity.accessSuccess...");
				
				SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				settings.edit().putString(MyDiscogs.DISCOGS_ACCESS_TOKEN_KEY, accessToken).commit();
				settings.edit().putString(MyDiscogs.DISCOGS_SECRET_TOKEN_KEY, accessSecret).commit();
				
				// recuperem el username cridant el identity
				IdentitySyncer is = new IdentitySyncer();
				is.setConsumerKey(MyDiscogs.DISCOGS_CONSUMER_KEY);
				is.setConsumerSecret(MyDiscogs.DISCOGS_CONSUMER_SECRET);
				is.setAccessToken(accessToken);
				is.setAccessSecret(accessSecret);
				is.setUserAgent(MyDiscogs.USER_AGENT);
				is.setOnParseResultsListener(ProfileActivity.this);
				is.sync();
				super.run();
			}
		}.start();
	}

	@Override
	public void accessFailure() 
	{
		Log.d(MyDiscogs.TAG, "ProfileActivity.accessFailure...");
		dismissDialog();
	}

	public void getProfile(View view)
	{
		new Thread()
		{
			@Override
			public void run() 
			{
				Context context = ProfileActivity.this.getApplicationContext();
				
				// auths settings
				SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
				String accessToken = settings.getString(MyDiscogs.DISCOGS_ACCESS_TOKEN_KEY, null);
				String secretToken = settings.getString(MyDiscogs.DISCOGS_SECRET_TOKEN_KEY, null);
				String username = settings.getString(MyDiscogs.DISCOGS_USERNAME_KEY, null);
				
				ProfileSyncer ps = new ProfileSyncer(username, true);
				ps.setConsumerKey(MyDiscogs.DISCOGS_CONSUMER_KEY);
				ps.setConsumerSecret(MyDiscogs.DISCOGS_CONSUMER_SECRET);
				ps.setAccessToken(accessToken);
				ps.setAccessSecret(secretToken);
				ps.setUserAgent(MyDiscogs.USER_AGENT);
				ps.setOnParseResultsListener(ProfileActivity.this);
				ps.sync();
				super.run();
			}
		}.start();
	}
	
	public void restartProfile()
	{
		// recarreguem el intent, ja que ara estem autenticats
		Intent profileActivity = new Intent(getBaseContext(), ProfileActivity.class);
		startActivity(profileActivity);
		overridePendingTransition(0,0);
	}
	
	@Override
	public void parseResults(int request, String items, int page, int pages) 
	{
		switch(request)
		{
		case IdentitySyncer.REQUEST:
			try 
			{
				JSONObject jo = new JSONObject(items);
				String username = jo.getString("username");
				SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				settings.edit().putString(MyDiscogs.DISCOGS_USERNAME_KEY, username).commit();
				
				Log.d(MyDiscogs.TAG, "ProfileActivity.parseResults(IdentitySyncer) username: "+username);
				
				// recarreguem el intent, ja que ara estem autenticats
				restartProfile();
			} 
			catch(Exception e) 
			{
				Log.d(MyDiscogs.TAG, "ProfileActivity.parseResults(IdentitySyncer) parse username error: "+e.getMessage());
			}
			dismissDialog();
			break;
		
		case ProfileSyncer.REQUEST:
			Log.d(MyDiscogs.TAG, "ProfileActivity.parseResults(ProfileSyncer) items: "+items);
			break;
		}
	}

	@Override
	public void parseError(int request, String error) 
	{
		switch(request)
		{
		case IdentitySyncer.REQUEST:
			Log.d(MyDiscogs.TAG, "ProfileActivity.parseError(IdentitySyncer) error: "+error);
			dismissDialog();
			break;
			
		case ProfileSyncer.REQUEST:
			Log.d(MyDiscogs.TAG, "ProfileActivity.parseError(ProfileSyncer) error: "+error);
			break;
		}
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
		Log.d(MyDiscogs.TAG,"ProfileActivity.selectItem on drawer");

		switch(position) 
		{
			case 0:
				// discogs activity
				Intent homeActivity = new Intent(getBaseContext(), DiscogsActivity.class);
				startActivity(homeActivity);
				overridePendingTransition(0,0);
				break;
			case 1:
				// profile activity
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
