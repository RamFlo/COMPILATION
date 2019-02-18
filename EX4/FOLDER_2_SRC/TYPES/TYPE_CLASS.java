package TYPES;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import MyClasses.ClassMethodDetails;

public class TYPE_CLASS extends TYPE
{
	/*********************************************************************/
	/* If this class does not extend a father class this should be null  */
	/*********************************************************************/
	public TYPE_CLASS father;

	/**************************************************/
	/* Gather up all data members in one place        */
	/* Note that data members coming from the AST are */
	/* packed together with the class methods         */
	/**************************************************/
	public TYPE_CLASS_DATA_MEMBERS_LIST data_members;
	
	public Map<String, Integer> dataMembersMap = new LinkedHashMap<String, Integer>();
	
	public Map<String, ClassMethodDetails> methodsMap = new LinkedHashMap<String, ClassMethodDetails>();
	
	//public List<String[]> listOfFunctionsForClassVFTable = new LinkedList<String[]>();
	
	/****************/
	/* CTROR(S) ... */
	/****************/
	public TYPE_CLASS(TYPE_CLASS father,String name,TYPE_CLASS_DATA_MEMBERS_LIST data_members)
	{
		this.name = name;
		this.father = father;
		this.data_members = data_members;
	}
	
	public int getSizeOfClassVFTable() {
		return listOfFunctionsForClassVFTable.size();
	}
	
	
}
