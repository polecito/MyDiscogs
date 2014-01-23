package cat.joronya.mydiscogs;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NeededAuthenticationFragment extends Fragment 
{
	public interface OnDiscogsLoginListener
	{
		public void discogsLogin(View view);
	}
	
	protected OnDiscogsLoginListener mLoginListener;
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
        	mLoginListener = (OnDiscogsLoginListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnDiscogsLoginListener");
        }
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		View view = inflater.inflate(R.layout.needed_authentication_layout, null);
        
		return view;
	}
	
}
