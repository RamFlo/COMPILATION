/***********/
/* PACKAGE */
/***********/
package SYMBOL_TABLE;

/*******************/
/* GENERAL IMPORTS */
/*******************/
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
	
	/****************************************************************************/
	/* Enter a variable, function, class type or array type to the symbol table */
	/****************************************************************************/
	public void enter(String name,TYPE t, Category entryCat)
	{
		SYMBOL_TABLE_ENTRY new_entry = new SYMBOL_TABLE_ENTRY(name,t,entryCat,top,top_index++,cur_scope_level);
		top = new_entry;
		
		if(!symbol_table_hash.containsKey(name)){
			List<SYMBOL_TABLE_ENTRY> l = new LinkedList<SYMBOL_TABLE_ENTRY>();
			symbol_table_hash.put(name, l);
		}
		symbol_table_hash.get(name).add(new_entry);
		
		//PrintMe();
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
		enter(
			"SCOPE-BOUNDARY",
			new TYPE_FOR_SCOPE_BOUNDARIES(scope_name));
		
		/*******************************************************/
		/* Increase scope level for future SYMBOL_TABLE_ENTRYs */
		/*******************************************************/
		cur_scope_level++;

		/*********************************************/
		/* Print the symbol table after every change */
		/*********************************************/
		//PrintMe();
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
		}
		/**************************************/
		/* Pop the SCOPE-BOUNDARY sign itself */		
		/**************************************/
		((LinkedList<SYMBOL_TABLE_ENTRY>)(symbol_table_hash.get(top.name))).removeLast();		
		top_index = top_index-1;
		top = top.prevtop;
		
		/*******************************************************/
		/* Decrease scope level for future SYMBOL_TABLE_ENTRYs */
		/*******************************************************/
		cur_scope_level--;
		
		/*********************************************/
		/* Print the symbol table after every change */		
		/*********************************************/
		//PrintMe();
	}
	
/*	public static int n=0;
	
	public void PrintMe()
	{
		int i=0;
		int j=0;
		String dirname="./FOLDER_5_OUTPUT/";
		String filename=String.format("SYMBOL_TABLE_%d_IN_GRAPHVIZ_DOT_FORMAT.txt",n++);

		try
		{
			*//*******************************************//*
			 [1] Open Graphviz text file for writing 
			*//*******************************************//*
			PrintWriter fileWriter = new PrintWriter(dirname+filename);

			*//*********************************//*
			 [2] Write Graphviz dot prolog 
			*//*********************************//*
			fileWriter.print("digraph structs {\n");
			fileWriter.print("rankdir = LR\n");
			fileWriter.print("node [shape=record];\n");

			*//*******************************//*
			 [3] Write Hash Table Itself 
			*//*******************************//*
			fileWriter.print("hashTable [label=\"");
			for (i=0;i<hashArraySize-1;i++) { fileWriter.format("<f%d>\n%d\n|",i,i); }
			fileWriter.format("<f%d>\n%d\n\"];\n",hashArraySize-1,hashArraySize-1);
		
			*//****************************************************************************//*
			 [4] Loop over hash table array and print all linked lists per array cell 
			*//****************************************************************************//*
			for (i=0;i<hashArraySize;i++)
			{
				if (table[i] != null)
				{
					*//*****************************************************//*
					 [4a] Print hash table array[i] -> entry(i,0) edge 
					*//*****************************************************//*
					fileWriter.format("hashTable:f%d -> node_%d_0:f0;\n",i,i);
				}
				j=0;
				for (SYMBOL_TABLE_ENTRY it=table[i];it!=null;it=it.next)
				{
					*//*******************************//*
					 [4b] Print entry(i,it) node 
					*//*******************************//*
					fileWriter.format("node_%d_%d ",i,j);
					fileWriter.format("[label=\"<f0>%s|<f1>%s|<f2>prevtop=%d|<f3>next\"];\n",
						it.name,
						it.type.name,
						it.prevtop_index);

					if (it.next != null)
					{
						*//***************************************************//*
						 [4c] Print entry(i,it) -> entry(i,it.next) edge 
						*//***************************************************//*
						fileWriter.format(
							"node_%d_%d -> node_%d_%d [style=invis,weight=10];\n",
							i,j,i,j+1);
						fileWriter.format(
							"node_%d_%d:f3 -> node_%d_%d:f0;\n",
							i,j,i,j+1);
					}
					j++;
				}
			}
			fileWriter.print("}\n");
			fileWriter.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}		
	}*/

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
			instance.enter("int",   TYPE_INT.getInstance());
			instance.enter("string",TYPE_STRING.getInstance());

			/*************************************/
			/* [2] How should we handle void ??? */
			/*************************************/

			/***************************************/
			/* [3] Enter library function PrintInt */
			/***************************************/
			instance.enter(
				"PrintInt",
				new TYPE_FUNCTION(
					TYPE_VOID.getInstance(),
					"PrintInt",
					new TYPE_LIST(
						TYPE_INT.getInstance(),
						null)));
			
			instance.enter(
					"PrintString",
					new TYPE_FUNCTION(
						TYPE_VOID.getInstance(),
						"PrintString",
						new TYPE_LIST(
							TYPE_STRING.getInstance(),
							null)));
			
			instance.enter(
					"PrintTrace",
					new TYPE_FUNCTION(
						TYPE_VOID.getInstance(),
						"PrintTrace",
						new TYPE_LIST(
							TYPE_VOID.getInstance(),
							null)));
		}
		return instance;
	}
}
