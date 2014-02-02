package cat.joronya.mydiscogs.collection;

import cat.joronya.mydiscogs.data.ReleaseWorker;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class ReleaseTaskFragment extends Fragment
{
	public static final String RELEASE_ID_EXTRA = "cat.joronya.mydiscogs.release_id";
	
	protected int mReleaseId;
	protected ReleaseAsyncTask mTask;
	
	public static ReleaseTaskFragment newInstance(int releaseId)
	{
		ReleaseTaskFragment fragment = new ReleaseTaskFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(RELEASE_ID_EXTRA, releaseId);
		fragment.setArguments(bundle);
		
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		setRetainInstance(true);
		
		mReleaseId = getArguments().getInt(RELEASE_ID_EXTRA);
		mTask = new ReleaseAsyncTask();
		mTask.execute((Void[])null);
		
		super.onCreate(savedInstanceState);
	}
	
	private class ReleaseAsyncTask extends AsyncTask<Void, Void, Void>
	{
		@Override
		protected Void doInBackground(Void... params) 
		{
			// TODO: master worker
			
			// init release worker, and fetch release data
			ReleaseWorker worker = new ReleaseWorker();
			boolean ret = worker.sync(ReleaseTaskFragment.this.getActivity(), ReleaseTaskFragment.this.mReleaseId);

			return null;
		}
	}
}
