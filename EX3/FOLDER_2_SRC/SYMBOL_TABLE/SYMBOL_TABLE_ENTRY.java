/***********/
/* PACKAGE */
/***********/
package SYMBOL_TABLE;

import SYMBOL_TABLE.ENTRY_CATEGORY.Category;
/*******************/
/* PROJECT IMPORTS */
/*******************/
import TYPES.*;

/**********************/
/* SYMBOL TABLE ENTRY */
/**********************/
public class SYMBOL_TABLE_ENTRY
{	
	/********/
	/* name */
	/********/
	public String name;

	/******************/
	/* TYPE value ... */
	/******************/
	public TYPE type;

	/*********************************************/
	/* prevtop and next symbol table entries ... */
	/*********************************************/
	public SYMBOL_TABLE_ENTRY prevtop;

	/****************************************************/
	/* The prevtop_index is just for debug purposes ... */
	/****************************************************/
	public int prevtop_index;
	
	/***************************************/
	/* scope level (0 is global scope) ... */
	/***************************************/
	public int scope_level;
	
	/******************************************/
	/* entry category (object/dataType) ... */
	/******************************************/
	public Category entryCat;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public SYMBOL_TABLE_ENTRY(
		String name,
		TYPE type,
		Category entryCat,
		SYMBOL_TABLE_ENTRY prevtop,
		int prevtop_index,
		int scope_level)
	{
		this.name = name;
		this.type = type;
		this.entryCat = entryCat;
		this.prevtop = prevtop;
		this.prevtop_index = prevtop_index;
		this.scope_level = scope_level;
	}
	
	public void updateType(TYPE t)
	{
		this.type = t;
	}
}
