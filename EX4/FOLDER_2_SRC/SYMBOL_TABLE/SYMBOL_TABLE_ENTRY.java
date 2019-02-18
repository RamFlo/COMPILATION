/***********/
/* PACKAGE */
/***********/
package SYMBOL_TABLE;

import AST.AST_Node;
import SYMBOL_TABLE.ENTRY_CATEGORY.Category;
import SYMBOL_TABLE.ENUM_OBJECT_CONTEXT.ObjectContext;
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
	
	/******************************************/
	/* entry AST Node  ... */
	/******************************************/
	public AST_Node entryOriginASTNode;
	
	/******************************************/
	/* entry object context  */
	/******************************************/
	//public ObjectContext objContext;
	
	/******************************************/
	/* entry object index in context  */
	/******************************************/
	//public int objectIndexInContext;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public SYMBOL_TABLE_ENTRY(String name, TYPE type, Category entryCat, SYMBOL_TABLE_ENTRY prevtop, int prevtop_index,
			int scope_level,AST_Node originASTNode) {
		this.name = name;
		this.type = type;
		this.entryCat = entryCat;
		//this.objContext = ObjectContext.nonObject;
		//this.objectIndexInContext = -1;
		this.prevtop = prevtop;
		this.prevtop_index = prevtop_index;
		this.scope_level = scope_level;
		this.entryOriginASTNode = originASTNode;
	}
	
//	public SYMBOL_TABLE_ENTRY(String name, TYPE type, Category entryCat, ObjectContext objContext,
//			int objectIndexInContext, SYMBOL_TABLE_ENTRY prevtop, int prevtop_index, int scope_level) {
//		this(name, type, entryCat, prevtop, prevtop_index, scope_level);
//		this.objContext = objContext;
//		this.objectIndexInContext = objectIndexInContext;
//	}
	
	public void updateType(TYPE t) {
		this.type = t;
		System.out.println("updated TYPE for class");
		if (t instanceof TYPE_CLASS && ((TYPE_CLASS) t).data_members != null) {
			if (((TYPE_CLASS) t).data_members.head != null)
				System.out.println(String.format("in update: data_members list head: %s",
						((TYPE_CLASS) t).data_members.head.name));
		}
	}
}
