package cat.joronya.mydiscogs.data;

import com.google.gson.annotations.SerializedName;

public class Notes 
{
	public static final String VALUE = "value";
	public String value;
	
	public static final String FIELD_ID = "field_id";
	@SerializedName("field_id")
	public String fieldId;

	public Notes() {}
}
