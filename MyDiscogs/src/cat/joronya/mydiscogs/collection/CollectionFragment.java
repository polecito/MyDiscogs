package cat.joronya.mydiscogs.collection;

import android.app.Activity;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import cat.joronya.mydiscogs.R;
import cat.joronya.mydiscogs.data.Collection;
import cat.joronya.mydiscogs.data.Release;
import cat.joronya.utils.ui.OnProgressListener;

public class CollectionFragment extends Fragment 
	implements LoaderManager.LoaderCallbacks<Cursor>, OnGetTitle, OnSetQuery
{
    // loader id
 	private static final int GRID_LOADER = 0x01;
    
	OnProgressListener mOnProgressListener;
    protected MenuItem mRefreshMenuItem;
    protected String mTitle;
    protected CollectionRealseAdapter mAdapter;
    protected String mQuery;
    protected boolean mShowMenu = true;
    protected String mSortOrder = Collection.NAME_SORT_ORDER;
	
	public void setShowMenu(boolean showMenu) 
	{
		mShowMenu = showMenu;
	}
	
	public void setSortOrder(String order)
	{
		mSortOrder = order;
	}

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
		View view = inflater.inflate(R.layout.collection_fragment_layout, null);
        
		// avisem q el fragment te menu pq es cridi
        setHasOptionsMenu(true);
		
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);

		// instanciem el adapter
		mAdapter = new CollectionRealseAdapter(getActivity(), null);
				
		// recuperem el gridview i setejem l'adapter
		GridView gridview = (GridView)getView().findViewById(R.id.gridview);
		gridview.setAdapter(mAdapter);
		        
		 // initialize the loader
        getLoaderManager().initLoader(GRID_LOADER, null, this);
	}
	
	@Override
	public void onResume() 
	{
		// activem el LocalBroadcastReceiver
		LocalBroadcastManager.getInstance(getActivity()).
			registerReceiver(mMessageReceiver, new IntentFilter(Collection.BROADCAST_UPDATE));
				
		super.onResume();
	}
	
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() 
	{
	    @Override
	    public void onReceive(Context context, Intent intent) 
	    {
	    	// al rebre el primer missatge de percentatge actualitzem el 
	    	// fragment pq mostri la listview i no la empty view
	    	int value = intent.getIntExtra(Collection.PERCENT_EXTRA, -1);
	    	mOnProgressListener.setProgressValue(value);
	    	
	    	// dismiss el progress si rebem darrer missatge (sense percentatge)
	    	if( value == -1 )
	    	{
	    		mOnProgressListener.dismissDialog();
	    		
	    		// remove the progress bar view
	    		hideProgressbar();
	    	}
	    }
	};
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) 
	{
		if( mShowMenu )
		{
			inflater.inflate(R.menu.collection, menu);

			// Associate searchable configuration with the SearchView
			SearchManager searchManager =
					(SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

			MenuItem menuItem = menu.findItem(R.id.search);
			SearchView searchView = (SearchView)MenuItemCompat.getActionView(menuItem);
			searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
		}
		
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch(item.getItemId())
		{
		case R.id.refresh:
			mRefreshMenuItem = item;
			discogsUpdateCollection();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void discogsUpdateCollection()
	{
		// set the progress bar view
		showProgressbar();
		
		mOnProgressListener.showDialog(getString(R.string.collection_text_progress),
				getString(R.string.collection_cancel_progress),
				getString(R.string.collection_ok_progress),
				true);
	} 
	
	public void showProgressbar()
	{
		if( mRefreshMenuItem == null )
			return;
			
		mRefreshMenuItem.setActionView(R.layout.action_progressbar);
        mRefreshMenuItem.expandActionView();
	}
	
	public void hideProgressbar()
	{
		if( mRefreshMenuItem == null )
			return;
			
		mRefreshMenuItem.collapseActionView();
        mRefreshMenuItem.setActionView(null);
	}
	
	@Override
	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		this.mTitle = title;
	}
	
	@Override
	public void setQuery(String query)
    {
		mQuery = query;
		if( isResumed() )
		{
			getLoaderManager().restartLoader(GRID_LOADER, null, this);
		}
    }

	@Override
	public Loader<Cursor> onCreateLoader(int code, Bundle bundle) 
	{
		String selection = null;
		String[] selectionArgs = null;
		
		if( mQuery != null )
		{
			selection = Release.TITLE + " LIKE ? ";
			selection += " OR "+Release.ARTISTS + " LIKE ? ";
			
			selectionArgs = new String[]{
				"%"+mQuery+"%",
				"%"+mQuery+"%"
			};
		}
		
		CursorLoader cursorLoader = new CursorLoader(getActivity(),
				Collection.CONTENT_URI, 
				Collection.PROJECTION, 
				selection, 
				selectionArgs, 
				mSortOrder);
		
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) 
	{
		mAdapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) 
	{
		mAdapter.swapCursor(null);
	}
}
