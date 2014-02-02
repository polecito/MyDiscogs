package cat.joronya.mydiscogs.collection;

import cat.joronya.mydiscogs.R;
import cat.joronya.utils.ui.OnProgressListener;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MasterFragment extends Fragment 
	implements OnGetTitle
{
	OnProgressListener mOnProgressListener;
	protected String mTitle;
	
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
		View view = inflater.inflate(R.layout.master_fragment_layout, null);
        
		// avisem q el fragment te menu pq es cridi
        setHasOptionsMenu(true);
		
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public String getTitle() {
		return mTitle;
	}
	
	public void setTitle(String title) {
		this.mTitle = title;
	}
}
