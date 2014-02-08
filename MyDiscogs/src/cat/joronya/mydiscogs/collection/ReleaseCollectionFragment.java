package cat.joronya.mydiscogs.collection;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import cat.joronya.mydiscogs.MyDiscogs;
import cat.joronya.mydiscogs.R;
import cat.joronya.mydiscogs.data.Collection;
import cat.joronya.mydiscogs.data.Release;
import cat.joronya.utils.ui.ListViewUtils;

public class ReleaseCollectionFragment extends Fragment 
	implements OnGetTitle, OnReloadRelease
{
	protected String mTitle;
	protected ListView mInCollection;
	protected ListView mInWantlist;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		View mView = inflater.inflate(R.layout.release_collection_fragment_layout, null);
        
		// avisem q el fragment te menu pq es cridi
        setHasOptionsMenu(true);
		
		return mView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);
		
		mInCollection = (ListView)getView().findViewById(R.id.release_collection_col_list);
		
		//mInWantlist = (ListView)getView().findViewById(R.id.release_collection_want_list);
		
	}
	
	@Override
	public void onResume() 
	{
		super.onResume();

		ReleaseDetailActivity rda = (ReleaseDetailActivity)getActivity();
		reloadRelease(rda.mRelease);
	}

	@Override
	public void reloadRelease(Release release)
	{
		// si el fragment no esta added, no cal fer res, ja ho farem al onResume
		if( !isAdded() )
			return;
		
		// donada la release cerquem totes les instancies a la col·lecció
		Cursor inCollectionCursor = getActivity().getContentResolver().query(
				Collection.CONTENT_URI, 
				Collection.PROJECTION, 
				Collection.RELEASE_ID+" = ? ", 
				new String[]{release.id+""}, 
				Collection.RECENT_SORT_ORDER);
		
		Log.d(MyDiscogs.TAG, "Releases in collection: "+inCollectionCursor.getCount());
		
		InCollectionRealseAdapter inCollectionAdapter = new InCollectionRealseAdapter(getActivity(), inCollectionCursor);
		mInCollection.setAdapter(inCollectionAdapter);
		
		ListViewUtils.setListViewHeightBasedOnChildren(mInCollection);
	}
	
	@Override
	public String getTitle() {
		return mTitle;
	}
	
	public void setTitle(String title){
		mTitle = title;
	}
	
}
