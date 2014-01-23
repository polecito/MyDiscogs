package cat.joronya.mydiscogs.collection;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cat.joronya.mydiscogs.MyDiscogs;
import cat.joronya.mydiscogs.R;
import cat.joronya.mydiscogs.data.Artist;
import cat.joronya.utils.image.ImageDownloader;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class CollectionRealseAdapter extends CursorAdapter
{
	protected ImageDownloader mImageDownloader;
	protected Drawable mDefaultDrawable;
	
	public CollectionRealseAdapter(Context context, Cursor c) 
	{
		super(context, c, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		mImageDownloader = new ImageDownloader(context, MyDiscogs.CACHE_DIR, MyDiscogs.RELEASES_DIR, 150);
		mDefaultDrawable = context.getResources().getDrawable(R.drawable.default_release);
	}
	
	static class ViewHolder
	{
		TextView titleView;
		TextView artistView;
		ImageView imageView;
		int id;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) 
	{
		// tenim ViewHolder?
		ViewHolder viewHolder = (ViewHolder)view.getTag();
		if( viewHolder == null )
		{
			viewHolder = new ViewHolder();
			viewHolder.titleView = (TextView)view.findViewById(R.id.item_title);
			viewHolder.artistView = (TextView)view.findViewById(R.id.item_artist);
			viewHolder.imageView = (ImageView)view.findViewById(R.id.item_image);
		}
		
		// release_id, thumb, title and artist
		int _id = cursor.getInt(0);
		int id = cursor.getInt(1);
		String thumb = cursor.getString(6);
		String title = cursor.getString(7);
		String jsonArtists = cursor.getString(8);
		
		Type type = new TypeToken<java.util.Collection<Artist>>(){}.getType();
		java.util.Collection<Artist> artists = new Gson().fromJson(jsonArtists, type);
		
		// taguejem el item amb el release_id
		viewHolder.id=id;
		
		// setejem el title de la release
		viewHolder.titleView.setText(_id+"-"+title);
		
		// setejem el artist (pot ser compost) de la release
		viewHolder.artistView.setText(getCompoundArtistName(artists));
		
		// setejem la imatge
		mImageDownloader.download(thumb, viewHolder.imageView, mDefaultDrawable);
		
		// registrem el ViewHolder
		view.setTag(viewHolder);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) 
	{
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.collection_release_item, parent, false);
		
		return view;
	}
	
	public String getCompoundArtistName(java.util.Collection<Artist> artists)
	{
		String compoundName = "";
		
		for(Artist artist: artists)
		{
			// si tenim ANV el posem, altrament el artist name
			if( !"".equals(artist.anv) )
			{
				compoundName += artist.anv+" ";
			}
			else
			{
				compoundName += artist.name+" ";
			}
			
			// afegim el join
			compoundName += artist.join+" ";
		}
		
		return compoundName;
	}

}
