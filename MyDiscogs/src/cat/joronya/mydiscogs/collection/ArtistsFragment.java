package cat.joronya.mydiscogs.collection;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import cat.joronya.mydiscogs.R;
import cat.joronya.mydiscogs.data.Artist;

public class ArtistsFragment extends ListFragment 
	implements LoaderManager.LoaderCallbacks<Cursor>, OnGetTitle, OnSetQuery
{
    // loader id
 	private static final int LIST_LOADER = 0x01;
    
	protected MenuItem mRefreshMenuItem;
    protected String mTitle;
    protected ArtistAdapter mAdapter;
    protected String mQuery;
    protected boolean mShowMenu = true;
	
	public void setShowMenu(boolean showMenu) 
	{
		mShowMenu = showMenu;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		View view = inflater.inflate(R.layout.artists_fragment_layout, null);
        
		// avisem q el fragment te menu pq es cridi
        setHasOptionsMenu(true);
		
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);

		// instanciem el adapter
		mAdapter = new ArtistAdapter(getActivity(), null);
		setListAdapter(mAdapter);		
		        
		 // initialize the loader
        getLoaderManager().initLoader(LIST_LOADER, null, this);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) 
	{
		if( mShowMenu )
		{
			inflater.inflate(R.menu.artists, menu);

			// Associate searchable configuration with the SearchView
			SearchManager searchManager =
					(SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

			MenuItem menuItem = menu.findItem(R.id.search);
			SearchView searchView = (SearchView)MenuItemCompat.getActionView(menuItem);
			ComponentName cn = new ComponentName(getActivity().getApplicationContext(), ArtistsActivity.class);
			searchView.setSearchableInfo(searchManager.getSearchableInfo(cn));
			
		}
		
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		return super.onOptionsItemSelected(item);
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
			getLoaderManager().restartLoader(LIST_LOADER, null, this);
		}
    }

	@Override
	public Loader<Cursor> onCreateLoader(int code, Bundle bundle) 
	{
		String selection = null;
		String[] selectionArgs = null;
		
		if( mQuery != null )
		{
			selection = Artist.NAME + " LIKE ? ";
			
			selectionArgs = new String[]{
				"%"+mQuery+"%"
			};
		}
		
		CursorLoader cursorLoader = new CursorLoader(getActivity(),
				Artist.CONTENT_URI, 
				Artist.PROJECTION, 
				selection, 
				selectionArgs, 
				Artist.DEFAULT_SORT_ORDER);
		
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
