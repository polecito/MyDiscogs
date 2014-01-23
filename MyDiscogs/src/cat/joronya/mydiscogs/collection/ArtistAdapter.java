package cat.joronya.mydiscogs.collection;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cat.joronya.mydiscogs.MyDiscogs;
import cat.joronya.mydiscogs.R;
import cat.joronya.utils.image.ImageDownloader;

public class ArtistAdapter extends CursorAdapter
{
	protected ImageDownloader mImageDownloader;
	protected Drawable mDefaultDrawable;
	
	public ArtistAdapter(Context context, Cursor c) 
	{
		super(context, c, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		mImageDownloader = new ImageDownloader(context, MyDiscogs.CACHE_DIR, MyDiscogs.ARTISTS_DIR, 150);
		mDefaultDrawable = context.getResources().getDrawable(R.drawable.noavatar);
	}
	
	static class ViewHolder
	{
		ImageView imageView;
		TextView artistView;
		TextView inCollectionView;
		TextView inWantlistView;
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
			viewHolder.imageView = (ImageView)view.findViewById(R.id.item_image);
			viewHolder.artistView = (TextView)view.findViewById(R.id.item_artist);
			viewHolder.inCollectionView = (TextView)view.findViewById(R.id.item_incollection);
			viewHolder.inWantlistView = (TextView)view.findViewById(R.id.item_inwantlist);
		}
		
		// release_id, thumb, artist, incollection and inwantlist
		int id = cursor.getInt(1);
		String thumb = null;//cursor.getString(10);
		String artist = cursor.getString(2);
		int inCollection = cursor.getInt(12);
		int inWantlist = 0;//cursor.getInt(13);
		
		// taguejem el item amb el release_id
		viewHolder.id=id;
		
		// setejem el artist (pot ser compost) de la release
		viewHolder.artistView.setText(artist);
		
		// setejem la imatge
		if( thumb != null && !"".equals(thumb) )
			mImageDownloader.download(thumb, viewHolder.imageView, mDefaultDrawable);
		else
			viewHolder.imageView.setImageDrawable(mDefaultDrawable);

		// setejem els inCollection
		if( inCollection != 0 )
		{
			viewHolder.inCollectionView.setVisibility(View.VISIBLE);
			viewHolder.inCollectionView.setText(""+inCollection);
		}
		else
		{
			viewHolder.inCollectionView.setVisibility(View.GONE);
		}
		
		// setejem els inWantlist
		if( inWantlist != 0 )
		{
			viewHolder.inWantlistView.setVisibility(View.VISIBLE);
			viewHolder.inWantlistView.setText(""+inWantlist);
		}
		else
		{
			viewHolder.inWantlistView.setVisibility(View.GONE);
		}
		
		// registrem el ViewHolder
		view.setTag(viewHolder);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) 
	{
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.artist_item, parent, false);
		
		return view;
	}
}
