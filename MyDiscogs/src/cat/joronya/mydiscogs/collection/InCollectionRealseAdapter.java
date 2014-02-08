package cat.joronya.mydiscogs.collection;

import java.lang.reflect.Type;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import cat.joronya.mydiscogs.MyDiscogs;
import cat.joronya.mydiscogs.R;
import cat.joronya.mydiscogs.data.Field;
import cat.joronya.mydiscogs.data.Notes;
import cat.joronya.utils.ui.ListViewUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class InCollectionRealseAdapter extends CursorAdapter 
{
	protected Cursor mFieldsCursor;

	public class ViewHolder
	{
		public ListView mFieldsListView;
	}
	
	public InCollectionRealseAdapter(Context context, Cursor cursor) 
	{
		super(context, cursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
	
		// get fields cursor, for every item is the same
		mFieldsCursor = context.getContentResolver().
				query(Field.CONTENT_URI, Field.PROJECTION, null, null, Field.DEFAULT_SORT_ORDER);
		
		Log.d(MyDiscogs.TAG, "Num. fields: "+mFieldsCursor.getCount());
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) 
	{
		String sNotes = cursor.getString(5);
		Type type = new TypeToken<java.util.Collection<Notes>>() {}.getType();
		java.util.Collection<Notes> notes = new Gson().fromJson(sNotes, type);
		
		FieldsAdapter fieldsAdapter = new FieldsAdapter(context, mFieldsCursor, (List<Notes>) notes);
		
		ViewHolder holder = (ViewHolder)view.getTag();
		holder.mFieldsListView.setAdapter(fieldsAdapter);
		ListViewUtils.setListViewHeightBasedOnChildren(holder.mFieldsListView);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) 
	{
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.release_collection_col_list_item,
				parent, false);

		ViewHolder holder = new ViewHolder();
		holder.mFieldsListView = (ListView)view.findViewById(R.id.release_collection_col_list_item_fields);
		
		view.setTag(holder);
		
		return view;
	}
}