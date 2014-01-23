package cat.joronya.mydiscogs.profile;

import java.text.NumberFormat;
import java.util.Locale;

import cat.joronya.mydiscogs.MyDiscogs;
import cat.joronya.mydiscogs.R;
import cat.joronya.mydiscogs.data.Profile;
import cat.joronya.utils.image.ImageDownloader;
import cat.joronya.utils.md5.MD5Util;
import cat.joronya.utils.ui.OnProgressListener;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileFragment extends Fragment 
{
	protected OnProgressListener mOnProgressListener;
	protected Drawable mDefaultDrawable;
	
	@Override
	public void onAttach(Activity activity) 
	{
		super.onAttach(activity);
		try {
			mOnProgressListener = (OnProgressListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnProgressListener");
        }
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		View view = inflater.inflate(R.layout.profile_layout, null);
        
		mDefaultDrawable = getActivity().getResources().getDrawable(R.drawable.noavatar);
		
        // avisem q el fragment te menu pq es cridi
        setHasOptionsMenu(true);
		
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		refreshLayout();
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onResume() 
	{
		// activem el LocalBroadcastReceiver
		LocalBroadcastManager.getInstance(getActivity()).
			registerReceiver(mMessageReceiver, new IntentFilter(Profile.BROADCAST_UPDATE));
				
		super.onResume();
	}
	
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() 
	{
	    @Override
	    public void onReceive(Context context, Intent intent) 
	    {
	    	// dismiss el progress
	    	if( mOnProgressListener != null )
	    		mOnProgressListener.dismissDialog();
	    	
	    	// refresquem el layout
	    	refreshLayout();
	    }
	};
	
	private void refreshLayout()
	{
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
		String username = settings.getString(MyDiscogs.DISCOGS_USERNAME_KEY, null);
		
		Cursor cursor = getActivity().getContentResolver().query(Profile.CONTENT_URI, 
				Profile.PROJECTION, 
				Profile.USERNAME + " = ? ", 
				new String[]{username}, 
				Profile.DEFAULT_SORT_ORDER);
		
		View noDataView = getView().findViewById(R.id.profile_no_data);
		View dataView = getView().findViewById(R.id.profile_data);

		if( cursor.moveToFirst() )
		{
			// mostrem layout de dades
			noDataView.setVisibility(View.GONE);
			dataView.setVisibility(View.VISIBLE);
			
			// init data
			String name = cursor.getString(3);
			String email = cursor.getString(4);
			String profile = cursor.getString(5);
			//String homePage = cursor.getString(6);
			String location = cursor.getString(7);
			String registered = cursor.getString(8);
			//int numLists = cursor.getInt(9);
			int numForSale = cursor.getInt(10);
			int numCollection = cursor.getInt(11);
			int numWantlist = cursor.getInt(12);
			int numPending = cursor.getInt(13);
			int releasesContributed = cursor.getInt(14);
			float rank = cursor.getFloat(15);
			int releasesRated = cursor.getInt(16);
			float ratingAvg = cursor.getFloat(17);
			
			NumberFormat decNumberFormat = NumberFormat.getNumberInstance(Locale.ENGLISH);
			decNumberFormat.setMaximumFractionDigits(2);
			decNumberFormat.setMinimumFractionDigits(2);
			
			ImageView avatarView = (ImageView)getView().findViewById(R.id.profile_avatar);
			String hash = MD5Util.md5Hex(email);
			ImageDownloader imageDownloader = 
					new ImageDownloader(getActivity().getApplicationContext(), MyDiscogs.CACHE_DIR, MyDiscogs.PROFILE_DIR, 100);
			imageDownloader.download(String.format(MyDiscogs.GRAVATAR_AVATAR_URL, hash), avatarView, mDefaultDrawable);
			
			TextView usernameView = (TextView)getView().findViewById(R.id.profile_username);
			usernameView.setText(username);
			
			TextView nameView = (TextView)getView().findViewById(R.id.profile_name);
			nameView.setText(name);
			
			TextView locationView = (TextView)getView().findViewById(R.id.profile_location);
			locationView.setText(location);

			TextView registeredView = (TextView)getView().findViewById(R.id.profile_registered);
			registeredView.setText(registered); 
			
			//TextView homePageView = (TextView)getView().findViewById(R.id.profile_home_page);
			//homePageView.setText(homePage);
			
			TextView profileView = (TextView)getView().findViewById(R.id.profile_profile);
			profileView.setText(profile);
			
			TextView contributedView = (TextView)getView().findViewById(R.id.profile_contributed);
			contributedView.setText(""+releasesContributed);
			
			TextView pendingView = (TextView)getView().findViewById(R.id.profile_pending);
			pendingView.setText(""+numPending);
			
			TextView collectionView = (TextView)getView().findViewById(R.id.profile_collection);
			collectionView.setText(""+numCollection);

			TextView wantlistView = (TextView)getView().findViewById(R.id.profile_wantlist);
			wantlistView.setText(""+numWantlist);

			TextView forSaleView = (TextView)getView().findViewById(R.id.profile_for_sale);
			forSaleView.setText(""+numForSale);

			TextView ratedView = (TextView)getView().findViewById(R.id.profile_rated);
			ratedView.setText(""+releasesRated);
			
			TextView ratingavgView = (TextView)getView().findViewById(R.id.profile_ratingavg);
			ratingavgView.setText(decNumberFormat.format(ratingAvg));

			TextView rankView = (TextView)getView().findViewById(R.id.profile_rank);
			rankView.setText(""+rank);
		}
		else
		{
			// mostrem layout no data
			noDataView.setVisibility(View.VISIBLE);
			dataView.setVisibility(View.GONE);
		}
		
		cursor.close();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) 
	{
		inflater.inflate(R.menu.profile, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch(item.getItemId())
		{
		case R.id.refresh:
			ProfileActivity pa = (ProfileActivity)getActivity();
			pa.discogsProfile(null);
			break;
		case R.id.logout:
			logout();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void logout()
	{
		// auths settings
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
		settings.edit().remove(MyDiscogs.DISCOGS_ACCESS_TOKEN_KEY).commit();
		settings.edit().remove(MyDiscogs.DISCOGS_SECRET_TOKEN_KEY).commit();
		settings.edit().remove(MyDiscogs.DISCOGS_USERNAME_KEY).commit();
		
		// restart de la activity, ara estem deslogats
		ProfileActivity activity = (ProfileActivity)getActivity();
		activity.restartProfile();
	}
	
}
