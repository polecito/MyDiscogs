package cat.joronya.mydiscogs.collection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import cat.joronya.mydiscogs.MyDiscogs;
import cat.joronya.mydiscogs.R;
import cat.joronya.mydiscogs.data.Artist;
import cat.joronya.mydiscogs.data.Company;
import cat.joronya.mydiscogs.data.Format;
import cat.joronya.mydiscogs.data.Identifier;
import cat.joronya.mydiscogs.data.Label;
import cat.joronya.mydiscogs.data.Release;
import cat.joronya.mydiscogs.data.Track;
import cat.joronya.utils.image.ImageDownloader;
import cat.joronya.utils.ui.OnProgressListener;

public class ReleaseFragment extends Fragment 
	implements OnGetTitle, OnReloadRelease
{
	public static final String[] FROM_LABELS = 
			new String[]{ Label.ID, Label.NAME, Label.CATNO };
	public static final int[] TO_LABELS = 
			new int[]{ R.id.release_label_item, R.id.release_label_item_name, R.id.release_label_item_catno };

	public static final String[] FROM_FORMATS = 
			new String[]{ Format.NAME };
	public static final int[] TO_FORMATS = 
			new int[]{ R.id.release_format_item_name };

	public static final String[] FROM_TRACKLIST = 
			new String[]{ Track.POSITION, Track.TITLE, Track.DURATION, 
						  Track.EXTRA_ARTISTS, Track.EXTRA_ARTISTS };
	public static final int[] TO_TRACKLIST = 
			new int[]{ R.id.release_tracklist_item_position, 
					   R.id.release_tracklist_item_title, 
					   R.id.release_tracklist_item_duration, 
					   R.id.release_tracklist_item_row2, 
					   R.id.release_tracklist_item_role};

	public static final String[] FROM_COMPANIES = 
			new String[]{ Company.ID, Company.ENTITY_TYPE_NAME, Company.NAME };
	public static final int[] TO_COMPANIES = 
			new int[]{ R.id.release_company_item, 
					   R.id.release_company_item_name, 
					   R.id.release_company_item_value };
	
	public static final String[] FROM_EXTRA_ARTISTS = 
			new String[]{ Artist.ID, Artist.ROLE, Artist.NAME };
	public static final int[] TO_EXTRA_ARTISTS = 
			new int[]{ R.id.release_extraartist_item, 
					   R.id.release_extraartist_item_name, 
					   R.id.release_extraartist_item_value };
	
	public static final String[] FROM_IDENTIFIERS = 
			new String[]{ Identifier.TYPE, Identifier.VALUE};
	public static final int[] TO_IDENTIFIERS = 
			new int[]{ R.id.release_indentifier_item_type, 
					   R.id.release_indentifier_item_value };
	
	
	protected OnProgressListener mOnProgressListener;
	protected String mTitle;
	protected int mReleaseId;
	protected Drawable mDefaultDrawable;
	protected ImageDownloader mImageDownloader;
	
	protected View mView;
	protected ViewHolder mHolder;
	
	public class ViewHolder
	{
		TextView artistView;
		TextView titleView;
		ImageView coverView;
		RelativeLayout labelView;
		LinearLayout labelList;
		RelativeLayout seriesView;
		LinearLayout seriesList;
		RelativeLayout formatsView;
		LinearLayout foramtsList;
		TextView countryView;
		TextView releasedView;
		RelativeLayout genresView;
		LinearLayout genresList;
		RelativeLayout stylesView;
		LinearLayout stylesList;
		RelativeLayout tracklistView;
		LinearLayout tracklistList;
		RelativeLayout companiesView;
		LinearLayout companiesList;
		RelativeLayout extraArtistView;
		LinearLayout extraArtistsList;
		RelativeLayout notesView;
		TextView notesTextView;
		RelativeLayout identifiersView;
		LinearLayout identifiersList;
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
		mView = inflater.inflate(R.layout.release_fragment_layout, null);
        
		// avisem q el fragment te menu pq es cridi
        setHasOptionsMenu(true);
		
		return mView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);
		
		mDefaultDrawable = getResources().getDrawable(R.drawable.default_release);
		mImageDownloader = new ImageDownloader(getActivity(), MyDiscogs.CACHE_DIR, MyDiscogs.RELEASES_DIR, 150);
		
		mHolder = new ViewHolder();
		mHolder.artistView = (TextView)mView.findViewById(R.id.release_artist);
		mHolder.titleView = (TextView)mView.findViewById(R.id.release_title);
		mHolder.coverView = (ImageView)mView.findViewById(R.id.release_cover);
		mHolder.labelView = (RelativeLayout)mView.findViewById(R.id.release_label);
		mHolder.labelList = (LinearLayout)mView.findViewById(R.id.release_label_list);
		mHolder.seriesView = (RelativeLayout)mView.findViewById(R.id.release_series);
		mHolder.seriesList = (LinearLayout)mView.findViewById(R.id.release_series_list);
		mHolder.formatsView = (RelativeLayout)mView.findViewById(R.id.release_format);
		mHolder.foramtsList = (LinearLayout)mView.findViewById(R.id.release_formats_list);
		mHolder.countryView = (TextView)mView.findViewById(R.id.release_country_value);
		mHolder.releasedView = (TextView)mView.findViewById(R.id.release_released_value);
		mHolder.genresView = (RelativeLayout)mView.findViewById(R.id.release_genres);
		mHolder.genresList = (LinearLayout)mView.findViewById(R.id.release_genres_list);
		mHolder.stylesView = (RelativeLayout)mView.findViewById(R.id.release_styles);
		mHolder.stylesList = (LinearLayout)mView.findViewById(R.id.release_styles_list);
		mHolder.tracklistView = (RelativeLayout)mView.findViewById(R.id.release_tracklist);
		mHolder.tracklistList = (LinearLayout)mView.findViewById(R.id.release_tracklist_list);
		mHolder.companiesView = (RelativeLayout)mView.findViewById(R.id.release_companies);
		mHolder.companiesList = (LinearLayout)mView.findViewById(R.id.release_companies_list);
		mHolder.extraArtistView = (RelativeLayout)mView.findViewById(R.id.release_credits);
		mHolder.extraArtistsList = (LinearLayout)mView.findViewById(R.id.release_credits_list);
		mHolder.notesView = (RelativeLayout)mView.findViewById(R.id.release_notes);
		mHolder.notesTextView = (TextView)mView.findViewById(R.id.release_notes_value);
		mHolder.identifiersView = (RelativeLayout)mView.findViewById(R.id.release_identifiers);
		mHolder.identifiersList = (LinearLayout)mView.findViewById(R.id.release_identifiers_list);
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
		
		mHolder.artistView.setText(release.getCompoundArtistName());
		mHolder.titleView.setText(release.title);
		
		if( release.thumb != null )
		{
			mImageDownloader.download(release.thumb, mHolder.coverView, mDefaultDrawable);
		}
		
		
		if( release.labels != null && release.labels.size() != 0 )
		{
			mHolder.labelView.setVisibility(View.VISIBLE);
			
			// instanciem i setejem l'adapter
	    	LabelAdapter labelAdapter = new LabelAdapter(getActivity(), getLabels(release.labels));
	    	
	    	// inflem els label items
			
			mHolder.labelList.removeAllViews();
			for( int i=0; i<labelAdapter.getCount(); i++ )
			{
				View row = labelAdapter.getView(i, null, mHolder.labelList);
				row.setOnClickListener(new OnClickListener() 
				{
					@Override
					public void onClick(View view) 
					{
						Log.d(MyDiscogs.TAG, "Label click id: "+view.getTag().toString());
					}
				});
				mHolder.labelList.addView(row);
			}
		}
		else
		{
			mHolder.labelView.setVisibility(View.GONE);
		}
		
		if( release.series != null && release.series.size() != 0 )
		{
			mHolder.seriesView.setVisibility(View.VISIBLE);
			
			// instanciem i setejem l'adapter
	    	LabelAdapter seriesAdapter = new LabelAdapter(getActivity(), getLabels(release.series));
	    	
	    	// inflem els label items
	    	mHolder.seriesList.removeAllViews();
			for( int i=0; i<seriesAdapter.getCount(); i++ )
			{
				View row = seriesAdapter.getView(i, null, mHolder.seriesList);
				row.setOnClickListener(new OnClickListener() 
				{
					@Override
					public void onClick(View view) 
					{
						Log.d(MyDiscogs.TAG, "Serie click id: "+view.getTag().toString());
					}
				});
				mHolder.seriesList.addView(row);
			}
		}
		else
		{
			mHolder.seriesView.setVisibility(View.GONE);
		}
		
		if( release.formats != null && release.formats.size() != 0 )
		{
			mHolder.formatsView.setVisibility(View.VISIBLE);
			
			// instanciem i setejem l'adapter
	    	FormatAdapter formatAdapter = new FormatAdapter(getActivity(), getFormats(release.formats));
	    	
	    	// inflem els label items
	    	mHolder.foramtsList.removeAllViews();
			for( int i=0; i<formatAdapter.getCount(); i++ )
			{
				View row = formatAdapter.getView(i, null, mHolder.foramtsList);
				mHolder.foramtsList.addView(row);
			}
		}
		else
		{
			mHolder.formatsView.setVisibility(View.GONE);
		}
		
		mHolder.countryView.setText(release.country);
		mHolder.releasedView.setText(release.released);
		
		if( release.genres != null && release.genres.size() != 0 )
		{
			mHolder.genresView.setVisibility(View.VISIBLE);
			
			mHolder.genresList.removeAllViews();
			
			TextView lab = new TextView(getActivity());
			lab.setTextColor(getResources().getColor(R.color.black));
			lab.setTextSize(14);
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.setMargins(0, 0, 10, 0);
			lab.setLayoutParams(params);
			lab.setText(release.getCompoundGenresName());

			mHolder.genresList.addView(lab);
		}
		else
		{
			mHolder.genresView.setVisibility(View.GONE);
		}
		
		if( release.styles != null && release.styles.size() != 0 )
		{
			mHolder.stylesView.setVisibility(View.VISIBLE);
			
			mHolder.stylesList.removeAllViews();
			
			TextView lab = new TextView(getActivity());
			lab.setTextColor(getResources().getColor(R.color.black));
			lab.setTextSize(14);
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.setMargins(0, 0, 10, 0);
			lab.setLayoutParams(params);
			lab.setText(release.getCompoundStylesName());

			mHolder.stylesList.addView(lab);
		}
		else
		{
			mHolder.stylesView.setVisibility(View.GONE);
		}
		
		if( release.tracklist != null && release.tracklist.size() != 0 )
		{
			mHolder.tracklistView.setVisibility(View.VISIBLE);
			
			// instanciem i setejem l'adapter
	    	TracklistAdapter tracklistAdapter = 
	    		new TracklistAdapter(getActivity(), getTracklist(release.tracklist), release.tracklist);
	    	
	    	// inflem els label items
	    	mHolder.tracklistList.removeAllViews();
			for( int i=0; i<tracklistAdapter.getCount(); i++ )
			{
				View row = tracklistAdapter.getView(i, null, mHolder.tracklistList);
				mHolder.tracklistList.addView(row);
			}
		}
		else
		{
			mHolder.tracklistView.setVisibility(View.GONE);
		}
		
		if( release.companies != null && release.companies.size() != 0 )
		{
			mHolder.companiesView.setVisibility(View.VISIBLE);
			
			// instanciem i setejem l'adapter
	    	CompaniesAdapter companiesAdapter = 
	    		new CompaniesAdapter(getActivity(), getCompanies(release.companies));
	    	
	    	// inflem els label items
			mHolder.companiesList.removeAllViews();
			for( int i=0; i<companiesAdapter.getCount(); i++ )
			{
				View row = companiesAdapter.getView(i, null, mHolder.companiesList);
				mHolder.companiesList.addView(row);
			}
		}
		else
		{
			mHolder.companiesView.setVisibility(View.GONE);
		}
		
		if( release.extraArtists != null && release.extraArtists.size() != 0 )
		{
			mHolder.extraArtistView.setVisibility(View.VISIBLE);
			
			// instanciem i setejem l'adapter
	    	ExtraArtistsAdapter extraArtistsAdapter = 
	    		new ExtraArtistsAdapter(getActivity(), getExtraArtists(release.extraArtists));
	    	
	    	// inflem els label items
			mHolder.extraArtistsList.removeAllViews();
			for( int i=0; i<extraArtistsAdapter.getCount(); i++ )
			{
				View row = extraArtistsAdapter.getView(i, null, mHolder.extraArtistsList);
				mHolder.extraArtistsList.addView(row);
			}
		}
		else
		{
			mHolder.extraArtistView.setVisibility(View.GONE);
		}
		
		if( !TextUtils.isEmpty(release.notes) )
		{
			mHolder.notesView.setVisibility(View.VISIBLE);
			mHolder.notesTextView.setText(release.notes);
		}
		else
		{
			mHolder.notesView.setVisibility(View.GONE);
		}
		
		if( release.identifiers != null && release.identifiers.size() != 0 )
		{
			mHolder.identifiersView.setVisibility(View.VISIBLE);
			
			// instanciem i setejem l'adapter
	    	IdentifiersAdapter identifiersAdapter = 
	    		new IdentifiersAdapter(getActivity(), getIdentifiers(release.identifiers));
	    	
	    	// inflem els label items
			mHolder.identifiersList.removeAllViews();
			for( int i=0; i<identifiersAdapter.getCount(); i++ )
			{
				View row = identifiersAdapter.getView(i, null, mHolder.identifiersList);
				mHolder.identifiersList.addView(row);
			}
		}
		else
		{
			mHolder.identifiersView.setVisibility(View.GONE);
		}
	}
	
	private class LabelAdapter extends SimpleAdapter
	{
		public LabelAdapter(Context context, List<HashMap<String, String>> labels)
		{
			super(context, 
				labels, 
				R.layout.release_label_item, 
				FROM_LABELS, 
				TO_LABELS);
			
			setViewBinder(new ViewBinder() 
	    	{
				@Override
				public boolean setViewValue(View view, Object data, String textRepresentation) 
				{
					switch( view.getId() )
					{
					case R.id.release_label_item:
						view.setTag(textRepresentation);
						break;
					case R.id.release_label_item_name:
						TextView nameView = (TextView)view;
						nameView.setText(textRepresentation);
						break;
					case R.id.release_label_item_catno:
						TextView catnoView = (TextView)view;
						catnoView.setText(textRepresentation);
						break;
					}
					
					return true;
				}
	    	});
		}
	}
	
	private List<HashMap<String, String>> getLabels(List<Label> labels)
	{
		ArrayList<HashMap<String, String>> items = new ArrayList<HashMap<String, String>>();
		
		for(Label label: labels)
		{
			// address 1
			HashMap<String, String> item = new HashMap<String, String>();
			item.put(Label.ID, ""+label.id);
			item.put(Label.NAME, label.name);
			item.put(Label.CATNO, label.catno);
			
			items.add(item);
		}
		
		return items;
	}
	
	private class FormatAdapter extends SimpleAdapter
	{
		public FormatAdapter(Context context, List<HashMap<String, String>> formats)
		{
			super(context, 
				formats, 
				R.layout.release_format_item, 
				FROM_FORMATS, 
				TO_FORMATS);
			
			setViewBinder(new ViewBinder() 
	    	{
				@Override
				public boolean setViewValue(View view, Object data, String textRepresentation) 
				{
					switch( view.getId() )
					{
					case R.id.release_format_item_name:
						TextView nameView = (TextView)view;
						nameView.setText(textRepresentation);
						break;
					}
					
					return true;
				}
	    	});
		}
	}
	
	private List<HashMap<String, String>> getFormats(List<Format> formats)
	{
		ArrayList<HashMap<String, String>> items = new ArrayList<HashMap<String, String>>();
		
		for(Format format: formats)
		{
			// address 1
			HashMap<String, String> item = new HashMap<String, String>();
			item.put(Format.NAME, format.getCompoundFormatName());
			
			items.add(item);
		}
		
		return items;
	}
	
	private class TracklistAdapter extends SimpleAdapter
	{
		List<Track> mTracks;
		
		public TracklistAdapter(Context context, List<HashMap<String, String>> tracklist, List<Track> tracks)
		{
			super(context, 
				tracklist, 
				R.layout.release_tracklist_item, 
				FROM_TRACKLIST, 
				TO_TRACKLIST);
			
			mTracks = tracks;
			
			setViewBinder(new ViewBinder() 
	    	{
				@Override
				public boolean setViewValue(View view, Object data, String textRepresentation) 
				{
					switch( view.getId() )
					{
					case R.id.release_tracklist_item_position:
						TextView positionView = (TextView)view;
						positionView.setText(textRepresentation);
						break;
					case R.id.release_tracklist_item_title:
						TextView titleView = (TextView)view;
						titleView.setText(textRepresentation);
						break;
					case R.id.release_tracklist_item_duration:
						TextView durationView = (TextView)view;
						durationView.setText(textRepresentation);
						break;
					case R.id.release_tracklist_item_row2:
						if( TextUtils.isEmpty(textRepresentation) )
							view.setVisibility(View.GONE);
						else
							view.setVisibility(View.VISIBLE);
						break;
					case R.id.release_tracklist_item_role:
						TextView roleView = (TextView)view;
						roleView.setText(textRepresentation);
						break;
					}
					
					return true;
				}
	    	});
		}
	}
	
	private List<HashMap<String, String>> getTracklist(List<Track> tracks)
	{
		ArrayList<HashMap<String, String>> items = new ArrayList<HashMap<String, String>>();
		
		for(Track track: tracks)
		{
			// address 1
			HashMap<String, String> item = new HashMap<String, String>();
			item.put(Track.POSITION, track.position);
			item.put(Track.TITLE, track.title);
			item.put(Track.DURATION, track.duration);
			item.put(Track.EXTRA_ARTISTS, "Written-by - E. Sobredo*");//track.extraArtists);
			
			items.add(item);
		}
		
		return items;
	}
	
	private class CompaniesAdapter extends SimpleAdapter
	{
		public CompaniesAdapter(Context context, List<HashMap<String, String>> companies)
		{
			super(context, 
				companies, 
				R.layout.release_company_item, 
				FROM_COMPANIES, 
				TO_COMPANIES);
			
			setViewBinder(new ViewBinder() 
	    	{
				@Override
				public boolean setViewValue(View view, Object data, String textRepresentation) 
				{
					switch( view.getId() )
					{
					case R.id.release_company_item:
						view.setTag(textRepresentation);
						break;
					case R.id.release_company_item_name:
						TextView nameView = (TextView)view;
						nameView.setText(textRepresentation);
						break;
					case R.id.release_company_item_value:
						TextView valueView = (TextView)view;
						valueView.setText(textRepresentation);
						break;
					}
					
					return true;
				}
	    	});
		}
	}
	
	private List<HashMap<String, String>> getCompanies(List<Company> companies)
	{
		ArrayList<HashMap<String, String>> items = new ArrayList<HashMap<String, String>>();
		
		for(Company company: companies)
		{
			// company 1
			HashMap<String, String> item = new HashMap<String, String>();
			item.put(Company.ID, company.id+"");
			item.put(Company.ENTITY_TYPE_NAME, company.entityTypeName);
			item.put(Company.NAME, company.name);
			
			items.add(item);
		}
		
		return items;
	}

	private class ExtraArtistsAdapter extends SimpleAdapter
	{
		public ExtraArtistsAdapter(Context context, List<HashMap<String, String>> artists)
		{
			super(context, 
				artists, 
				R.layout.release_extraartist_item, 
				FROM_EXTRA_ARTISTS, 
				TO_EXTRA_ARTISTS);
			
			setViewBinder(new ViewBinder() 
	    	{
				@Override
				public boolean setViewValue(View view, Object data, String textRepresentation) 
				{
					switch( view.getId() )
					{
					case R.id.release_extraartist_item:
						view.setTag(textRepresentation);
						break;
					case R.id.release_extraartist_item_name:
						TextView nameView = (TextView)view;
						nameView.setText(textRepresentation);
						break;
					case R.id.release_extraartist_item_value:
						TextView valueView = (TextView)view;
						valueView.setText(textRepresentation);
						break;
					}
					
					return true;
				}
	    	});
		}
	}
	
	private List<HashMap<String, String>> getExtraArtists(List<Artist> artists)
	{
		ArrayList<HashMap<String, String>> items = new ArrayList<HashMap<String, String>>();
		
		for(Artist artist: artists)
		{
			// company 1
			HashMap<String, String> item = new HashMap<String, String>();
			item.put(Artist.ID, artist.id+"");
			item.put(Artist.ROLE, artist.role);
			item.put(Artist.NAME, artist.name);
			
			items.add(item);
		}
		
		return items;
	}
	
	private class IdentifiersAdapter extends SimpleAdapter
	{
		public IdentifiersAdapter(Context context, List<HashMap<String, String>> identifiers)
		{
			super(context, 
				identifiers, 
				R.layout.release_identifier_item, 
				FROM_IDENTIFIERS, 
				TO_IDENTIFIERS);
			
			setViewBinder(new ViewBinder() 
	    	{
				@Override
				public boolean setViewValue(View view, Object data, String textRepresentation) 
				{
					switch( view.getId() )
					{
					case R.id.release_indentifier_item_type:
						TextView typeView = (TextView)view;
						typeView.setText(textRepresentation);
						break;
					case R.id.release_indentifier_item_value:
						TextView valueView = (TextView)view;
						valueView.setText(textRepresentation);
						break;
					}
					
					return true;
				}
	    	});
		}
	}
	
	private List<HashMap<String, String>> getIdentifiers(List<Identifier> identifiers)
	{
		ArrayList<HashMap<String, String>> items = new ArrayList<HashMap<String, String>>();
		
		for(Identifier identifier: identifiers)
		{
			// company 1
			HashMap<String, String> item = new HashMap<String, String>();
			
			String type = identifier.type;
			if( !TextUtils.isEmpty(identifier.description) )
				type += " ("+identifier.description+")";
			
			item.put(Identifier.TYPE, type);
			item.put(Identifier.VALUE, identifier.value);
			
			items.add(item);
		}
		
		return items;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) 
	{
		inflater.inflate(R.menu.release, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch(item.getItemId())
		{
		case R.id.refresh:
			// mostrem progress dialog
        	mOnProgressListener.showDialog("Retrieving release information...", null, null, false);
        	
        	// release id from intent
        	mReleaseId = getActivity().getIntent().getIntExtra(ReleaseDetailActivity.RELEASE_ID_EXTRA, -1);
        	
        	// cridem el AsyncTask per carregar Release de Discogs
        	FragmentManager fm = getActivity().getSupportFragmentManager();
        	ReleaseTaskFragment rtf = ReleaseTaskFragment.newInstance(mReleaseId);
        	fm.beginTransaction().add(rtf, ReleaseDetailActivity.UPDATE_TASK).commit();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public String getTitle() {
		return mTitle;
	}
	
	public void setTitle(String title) {
		this.mTitle = title;
	}

}
