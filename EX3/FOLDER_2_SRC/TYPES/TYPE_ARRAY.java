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
	public TYPE_ARRAY(TYPE arrayType,String name)
	{
		this.name = name;
		this.arrayType = arrayType;
	}
}
