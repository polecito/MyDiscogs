package cat.joronya.mydiscogs.home;

import cat.joronya.mydiscogs.R;
import cat.joronya.utils.ui.OnAboutListener;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DiscogsFragment extends Fragment 
{
	protected OnAboutListener mAboutListener;
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mAboutListener = (OnAboutListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnAboutListener");
        }
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		View home = inflater.inflate(R.layout.discogs_layout, null);
        
        // avisem q el fragment te menu pq es cridi
        setHasOptionsMenu(true);
		
		return home;
	}
	
}
