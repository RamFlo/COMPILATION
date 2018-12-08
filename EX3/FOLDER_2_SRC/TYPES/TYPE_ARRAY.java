package TYPES;

public class TYPE_ARRAY extends TYPE
{
	/*************************/
	/* The type of the array */
	/*************************/
	public TYPE arrayType;
	
	/****************/
	/* CTROR(S) ... */
	/****************/
	public TYPE_ARRAY(String name, TYPE arrayType)
	{
		this.name = name;
		this.arrayType = arrayType;
	}
}
