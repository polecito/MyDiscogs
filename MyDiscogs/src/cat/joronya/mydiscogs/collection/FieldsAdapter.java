package cat.joronya.mydiscogs.collection;

import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import cat.joronya.mydiscogs.R;
import cat.joronya.mydiscogs.data.Notes;

public class FieldsAdapter extends CursorAdapter
{
	List<Notes> mNotes;
	
	public class ViewHolder
	{
		public TextView fieldName;
		public TextView fieldValue;
	}
	
	public FieldsAdapter(Context context, Cursor cursor, List<Notes> notes)
	{
		super(context, cursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		mNotes = notes;
	}
	
	private String getValueForFieldId(int fieldId)
	{
		if( mNotes == null )
			return "";
		
		for( Notes note: mNotes) 
		{
			if( note.fieldId == fieldId )
				return note.value;
		}
		return "";
	}
	
	@Override
	public void bindView(View view, Context context, Cursor cursor) 
	{
		int fieldId = cursor.getInt(1);
		String fieldValue = getValueForFieldId(fieldId);
		String fieldName = cursor.getString(2);
		
		ViewHolder holder = (ViewHolder)view.getTag();
		holder.fieldName.setText(fieldName);
		holder.fieldValue.setText(fieldValue);
	}
	
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) 
	{
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.release_collection_col_list_fields_item, parent, false);
		
		ViewHolder holder = new ViewHolder();
		holder.fieldName = (TextView)view.findViewById(R.id.release_collection_col_list_fields_item_label);
		holder.fieldValue = (TextView)view.findViewById(R.id.release_collection_col_list_fields_item_value);
		
		view.setTag(holder);
		
		return view;
	}
}