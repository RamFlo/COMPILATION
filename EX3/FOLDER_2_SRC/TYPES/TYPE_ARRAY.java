package TYPES;

public class TYPE_ARRAY extends TYPE
{
	/*************************/
	/* The type of the array */
	/*************************/
	public TYPE arrayType;
	public String arrayTypeString;
	
	/****************/
	/* CTROR(S) ... */
	/****************/
	public TYPE_ARRAY(String name, TYPE arrayType, String arrayTypeString)
	{
		this.name = name;
		this.arrayType = arrayType;
		this.arrayTypeString = arrayTypeString;
	}
}
