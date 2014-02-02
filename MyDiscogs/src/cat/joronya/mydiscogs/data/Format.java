package cat.joronya.mydiscogs.data;

import java.util.List;

public class Format 
{
	public static final String NAME = "name";
	public String name;
	
	public static final String DESCRIPTIONS = "descriptions";
	public List<String> descriptions;

	public static final String QTY = "qty";
	public String qty;
	
	public static final String TEXT = "text";
	public String text;

	public Format() {}
	
	public String getCompoundFormatName()
	{
		String retFormat = "";
		
		if( !"1".equals(qty))
			retFormat += qty + " x ";
		
		retFormat += name;
		
		if( descriptions != null )
		{
			for(String desc: descriptions)
			{
				retFormat += ", "+desc;
			}
		}
		
		if( text != null )
			retFormat += ", "+text;
		
		return retFormat;
	}
}
