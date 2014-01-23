package cat.joronya.mydiscogs.profile;

import cat.joronya.mydiscogs.R;
import cat.joronya.utils.ui.OnProgressListener;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LoginFragment extends Fragment 
{
	protected OnProgressListener mProgressListener;
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
        	mProgressListener = (OnProgressListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnProgressListener");
        }
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		View view = inflater.inflate(R.layout.login_layout, null);
        
        // avisem q el fragment te menu pq es cridi
        setHasOptionsMenu(true);
		
		return view;
	}
	
}
