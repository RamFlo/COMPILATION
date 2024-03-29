/***********/
/* PACKAGE */
/***********/
package SYMBOL_TABLE;

/*******************/
/* GENERAL IMPORTS */
/*******************/
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import MyExceptions.SemanticRuntimeException;
import SYMBOL_TABLE.ENTRY_CATEGORY.Category;
/*******************/
/* PROJECT IMPORTS */
/*******************/
import TYPES.*;

/****************/
/* SYMBOL TABLE */
/****************/
public class SYMBOL_TABLE
{
	
	/**********************************************/
	/* The actual symbol table data structure ... */
	/**********************************************/
	public Map<String, List<SYMBOL_TABLE_ENTRY>> symbol_table_hash = new HashMap<String, List<SYMBOL_TABLE_ENTRY>>();
	private SYMBOL_TABLE_ENTRY top;
	private int top_index = 0, cur_scope_level = 0;
	public TYPE curFunctionReturnType = null;
	public TYPE_CLASS curClassExtends = null;
	
	/****************************************************************************/
	/* Enter a variable, function, class type or array type to the symbol table */
	/****************************************************************************/
	
//	public TYPE getReturnTypeOfClosestFunction() {
//		SYMBOL_TABLE_ENTRY cur = top;
//		while (cur != null && !(cur.type instanceof TYPE_FUNCTION)) {
//			cur = cur.prevtop;
//		}
//		if (cur != null) return ((TYPE_FUNCTION)cur.type).returnType;
//		return null;
//	}
	
	public void enter(String name,TYPE t, Category entryCat)
	{
		if (t == null)
			System.out.println(String.format("%s type is null!",name));
		SYMBOL_TABLE_ENTRY new_entry = new SYMBOL_TABLE_ENTRY(name,t,entryCat,top,top_index++,cur_scope_level);
		top = new_entry;
		
		if(!symbol_table_hash.containsKey(name)){
			List<SYMBOL_TABLE_ENTRY> l = new LinkedList<SYMBOL_TABLE_ENTRY>();
			symbol_table_hash.put(name, l);
		}
		symbol_table_hash.get(name).add(new_entry);
		
		PrintMe();
	}
	
	public void enterObject(String name,TYPE t)
	{
		this.enter(name, t, Category.object);
	}
	
	public void enterDataType(String name,TYPE t)
	{
		this.enter(name, t, Category.dataType);
	}

	/***********************************************/
	/* Find the inner-most scope element with name */
	/***********************************************/
	public TYPE find(String name)
	{
		if (symbol_table_hash.containsKey(name)) return ((LinkedList<SYMBOL_TABLE_ENTRY>)(symbol_table_hash.get(name))).getLast().type;
		return null;
	}
	
	public TYPE findByCategory(String name,Category entryCat)
	{
		SYMBOL_TABLE_ENTRY searchRes = null;
		if (symbol_table_hash.containsKey(name)) {
			searchRes = ((LinkedList<SYMBOL_TABLE_ENTRY>)(symbol_table_hash.get(name))).getLast();
			if (searchRes.entryCat == entryCat)
				return searchRes.type;
		}
		return null;
	}
	
	public TYPE findDataType(String name)
	{
		return findByCategory(name, Category.dataType);
	}
	
	public TYPE findObject(String name)
	{
		return findByCategory(name, Category.object);
	}
	
	/***********************************************/
	/* Find current scope element with name 'name' */
	/***********************************************/
	public TYPE findInCurrentScope(String name)
	{
		if (symbol_table_hash.containsKey(name))
		{ 
			SYMBOL_TABLE_ENTRY temp = ((LinkedList<SYMBOL_TABLE_ENTRY>)(symbol_table_hash.get(name))).getLast();
			if (temp.scope_level == cur_scope_level)
				return temp.type;
		}
		return null;
	}
	
	/***********************************************/
	/* Update symbol table entry */
	/***********************************************/
	public void findAndUpdateEntryTypeForDataType(String name, TYPE t)
	{
		SYMBOL_TABLE_ENTRY searchRes = null;
		SYMBOL_TABLE_ENTRY searchRes2 = null;
		if (symbol_table_hash.containsKey(name)) {
			searchRes = ((LinkedList<SYMBOL_TABLE_ENTRY>)(symbol_table_hash.get(name))).getLast();
			if (searchRes.entryCat == Category.dataType)
			{
				searchRes.updateType(t);
				searchRes2 = ((LinkedList<SYMBOL_TABLE_ENTRY>)(symbol_table_hash.get(name))).getLast();
				if (searchRes2.type instanceof TYPE_CLASS && ((TYPE_CLASS)searchRes2.type).data_members != null){
					if (((TYPE_CLASS)searchRes2.type).data_members.head !=null)
						System.out.println(String.format("in findAndUpdateEntryTypeForDataType: data_members list head: %s", ((TYPE_CLASS)t).data_members.head.name));
				}
			}
				
		}
	}

	/***************************************************************************/
	/* begin scope = Enter the <SCOPE-BOUNDARY> element to the data structure */
	/***************************************************************************/
	public void beginScope(String scope_name)
	{
		/************************************************************************/
		/* Though <SCOPE-BOUNDARY> entries are present inside the symbol table, */
		/* they are not really types. In order to be ablt to debug print them,  */
		/* a special TYPE_FOR_SCOPE_BOUNDARIES was developed for them. This     */
		/* class only contain their type name which is the bottom sign: _|_     */
		/************************************************************************/
		enter("SCOPE-BOUNDARY",	new TYPE_FOR_SCOPE_BOUNDARIES(scope_name), Category.misc);
		
		/*******************************************************/
		/* Increase scope level for future SYMBOL_TABLE_ENTRYs */
		/*******************************************************/
		cur_scope_level++;

		/*********************************************/
		/* Print the symbol table after every change */
		/*********************************************/
		PrintMe();
	}
	
	public void beginFunctionScope(String scope_name,TYPE funcReturnType)
	{
		this.curFunctionReturnType = funcReturnType;
		this.beginScope(scope_name);
	}
	
	public void beginClassScope(String scope_name,TYPE_CLASS curClassExtends)
	{
		this.curClassExtends = curClassExtends;
		this.beginScope(scope_name);
	}

	/********************************************************************************/
	/* end scope = Keep popping elements out of the data structure,                 */
	/* from most recent element entered, until a <NEW-SCOPE> element is encountered */
	/********************************************************************************/
	public void endScope()
	{
		/**************************************************************************/
		/* Pop elements from the symbol table stack until a SCOPE-BOUNDARY is hit */		
		/**************************************************************************/
		SYMBOL_TABLE_ENTRY temp;
		
		while (top.name != "SCOPE-BOUNDARY")
		{
			temp = top;
			top_index = top_index-1;
			top = top.prevtop;
			((LinkedList<SYMBOL_TABLE_ENTRY>)(symbol_table_hash.get(temp.name))).removeLast();
			
			// if entry's linked list is empty, remove it from the hashmap (to allow printing easily)
			if (((LinkedList<SYMBOL_TABLE_ENTRY>)(symbol_table_hash.get(temp.name))).size() == 0)
				symbol_table_hash.remove(temp.name);
		}
		/**************************************/
		/* Pop the SCOPE-BOUNDARY sign itself */		
		/**************************************/
		((LinkedList<SYMBOL_TABLE_ENTRY>)(symbol_table_hash.get(top.name))).removeLast();
		
		// if SCOPE-BOUNDARY linked list is empty, remove it from the hashmap (to allow printing easily)
		if (((LinkedList<SYMBOL_TABLE_ENTRY>)(symbol_table_hash.get(top.name))).size() == 0)
			symbol_table_hash.remove(top.name);
		
		top_index = top_index-1;
		top = top.prevtop;
		
		/*******************************************************/
		/* Decrease scope level for future SYMBOL_TABLE_ENTRYs */
		/*******************************************************/
		cur_scope_level--;
		
		/*********************************************/
		/* Print the symbol table after every change */		
		/*********************************************/
		PrintMe();
	}
	
	public void endFunctionScope()
	{
		this.curFunctionReturnType = null;
		this.endScope();
	}
	
	public void endClassScope()
	{
		this.curClassExtends = null;
		this.endScope();
	}
	
	
	public static int n=0;
	
	public void PrintMe()
	{
		int i=0;
		int j=0;
		String dirname="./FOLDER_5_OUTPUT/";
		String filename=String.format("SYMBOL_TABLE_%d_IN_GRAPHVIZ_DOT_FORMAT.txt",n++);

		try
		{
			/*******************************************/
			/* [1] Open Graphviz text file for writing */
			/*******************************************/
			PrintWriter fileWriter = new PrintWriter(dirname+filename);

			/*********************************/
			/* [2] Write Graphviz dot prolog */
			/*********************************/
			fileWriter.print("digraph structs {\n");
			fileWriter.print("rankdir = LR\n");
			fileWriter.print("node [shape=record];\n");

			/*******************************/
			/* [3] Write Hash Table Itself */
			/*******************************/
			fileWriter.print("hashTable [label=\"");
			for (i=0; i < symbol_table_hash.entrySet().size() - 1; i++) { fileWriter.format("<f%d>\n%d\n|",i,i); }
			fileWriter.format("<f%d>\n%d\n\"];\n", symbol_table_hash.entrySet().size() - 1, symbol_table_hash.entrySet().size() - 1);
		
			/****************************************************************************/
			/* [4] Loop over hash table array and print all linked lists per array cell */
			/****************************************************************************/
			i = 0;
			for (List<SYMBOL_TABLE_ENTRY> curList : symbol_table_hash.values()) 
			{
				/*****************************************************/
				/* [4a] Print hash table array[i] -> entry(i,0) edge */
				/*****************************************************/
				fileWriter.format("hashTable:f%d -> node_%d_0:f0;\n",i,i);
				
				j=0;
				for (SYMBOL_TABLE_ENTRY curEntry : curList)
				{
					/*******************************/
					/* [4b] Print entry(i,it) node */
					/*******************************/
					fileWriter.format("node_%d_%d ",i,j);
					fileWriter.format("[label=\"<f0>name=%s|<f1>TYPE=%s|<f2>type.name=%s|<f3>entryCat=%s|<f4>scope_lvl=%d|<f5>prevtop=%d|<f6>next\"];\n",
							curEntry.name,
							curEntry.type.getClass().getName(),
							curEntry.type.name,
							curEntry.entryCat.toString(),
							curEntry.scope_level,
							curEntry.prevtop_index);

					if (j != curList.size() - 1) //if not currently on last entry in list
					{
						/***************************************************/
						/* [4c] Print entry(i,it) -> entry(i,it.next) edge */
						/***************************************************/
						fileWriter.format(
							"node_%d_%d -> node_%d_%d [style=invis,weight=10];\n",
							i,j,i,j+1);
						fileWriter.format(
							"node_%d_%d:f3 -> node_%d_%d:f0;\n",
							i,j,i,j+1);
					}
					j++;
				}
				i++;
			}
			fileWriter.print("}\n");
			fileWriter.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}		
	}

	/**************************************/
	/* USUAL SINGLETON IMPLEMENTATION ... */
	/**************************************/
	private static SYMBOL_TABLE instance = null;

	/*****************************/
	/* PREVENT INSTANTIATION ... */
	/*****************************/
	protected SYMBOL_TABLE() {}

	/******************************/
	/* GET SINGLETON INSTANCE ... */
	/******************************/
	public static SYMBOL_TABLE getInstance()
	{
		if (instance == null)
		{
			/*******************************/
			/* [0] The instance itself ... */
			/*******************************/
			instance = new SYMBOL_TABLE();

			/*****************************************/
			/* [1] Enter primitive types int, string */
			/*****************************************/
			instance.enterDataType("int", TYPE_INT.getInstance());
			instance.enterDataType("string",TYPE_STRING.getInstance());

			/*************************************/
			/* [2] How should we handle void ??? */
			/*************************************/

			/***************************************/
			/* [3] Enter library function PrintInt */
			/***************************************/
			instance.enterObject(
				"PrintInt",
				new TYPE_FUNCTION(
					TYPE_VOID.getInstance(),
					"PrintInt",
					new TYPE_LIST(
						TYPE_INT.getInstance(),
						null)));
			
			instance.enterObject(
					"PrintString",
					new TYPE_FUNCTION(
						TYPE_VOID.getInstance(),
						"PrintString",
						new TYPE_LIST(
							TYPE_STRING.getInstance(),
							null)));
			
			instance.enterObject("PrintTrace",new TYPE_FUNCTION(TYPE_VOID.getInstance(),"PrintTrace",null));
		}
		return instance;
	}
}
