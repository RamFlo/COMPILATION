package AST;

import IR.IR;
import IR.IRcommand;
import IR.IRcommandConstInt;
import IR.IRcommand_Add_Immediate;
import IR.IRcommand_BEQ;
import IR.IRcommand_BEQZ;
import IR.IRcommand_BLT;
import IR.IRcommand_BNE;
import IR.IRcommand_BNEZ;
import IR.IRcommand_Binop_Add_Integers;
import IR.IRcommand_Binop_Div_Integers;
import IR.IRcommand_Binop_EQ_Integers;
import IR.IRcommand_Binop_LT_Integers;
import IR.IRcommand_Binop_Mul_Integers;
import IR.IRcommand_Binop_Sub_Integers;
import IR.IRcommand_Exit;
import IR.IRcommand_Jump_If_Eq_To_Zero;
import IR.IRcommand_Jump_Label;
import IR.IRcommand_Label;
import IR.IRcommand_Load;
import IR.IRcommand_Load_Address;
import IR.IRcommand_Load_Byte_Offset;
import IR.IRcommand_Malloc;
import IR.IRcommand_Move;
import IR.IRcommand_Print_String_By_Address;
import IR.IRcommand_Store_Byte_Offset;
import IR.IRcommand_li_Null_Term;
import MIPS.sir_MIPS_a_lot;
import MyExceptions.SemanticRuntimeException;
import TEMP.TEMP;
import TEMP.TEMP_FACTORY;
import TYPES.TYPE;
import TYPES.TYPE_ARRAY;
import TYPES.TYPE_CLASS;
import TYPES.TYPE_INT;
import TYPES.TYPE_NIL;
import TYPES.TYPE_STRING;

public class AST_EXP_BINOP extends AST_EXP
{
	int OP;
	public AST_EXP left;
	public AST_EXP right;
	
	public TYPE leftType,rightType;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_EXP_BINOP(AST_EXP left,AST_EXP right,int OP)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.print("====================== exp -> exp BINOP exp\n");

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.left = left;
		this.right = right;
		this.OP = OP;
	}
	
	/*************************************************/
	/* The printing message for a binop exp AST node */
	/*************************************************/
	public void PrintMe()
	{
		String sOP="";
		
		/*********************************/
		/* CONVERT OP to a printable sOP */
		/*********************************/
		switch (OP) {
		case 0:
			sOP = "+";
			break;
		case 1:
			sOP = "-";
			break;
		case 2:
			sOP = "*";
			break;
		case 3:
			sOP = "/";
			break;
		case 4:
			sOP = "<";
			break;
		case 5:
			sOP = ">";
			break;
		case 6:
			sOP = "=";
			break;
		}
		
		/*************************************/
		/* AST NODE TYPE = AST SUBSCRIPT VAR */
		/*************************************/
		System.out.print("AST NODE BINOP EXP\n");

		/**************************************/
		/* RECURSIVELY PRINT left + right ... */
		/**************************************/
		if (left != null) left.PrintMe();
		if (right != null) right.PrintMe();
		
		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("BINOP(%s)",sOP));
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (left  != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,left.SerialNumber);
		if (right != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,right.SerialNumber);
	}
	
	private boolean ExtendingOrSameClass(TYPE_CLASS t1, TYPE_CLASS t2){
		if (t2 == null) return false;
		if (t1.name.equals(t2.name)) return true;
		return ExtendingOrSameClass(t1, t2.father);
		
	}
	
	public TYPE SemantMe()
	{
		TYPE t1 = null;
		TYPE t2 = null;
		
		if (left  != null) t1 = left.SemantMe();
		if (right != null) t2 = right.SemantMe();
		
		this.leftType = t1;
		this.rightType = t2;
		
		//allow any binop for integers
		if ((t1 == TYPE_INT.getInstance()) && (t2 == TYPE_INT.getInstance()))
			return TYPE_INT.getInstance();
		
		//allow + for two strings
		if ((t1 == TYPE_STRING.getInstance()) && (t2 == TYPE_STRING.getInstance()) && (OP == 0))
			return TYPE_STRING.getInstance();
		
		//equality testing
		if (OP == 6)
		{
			//nil handling
			if (t1 instanceof TYPE_NIL || t2 instanceof TYPE_NIL)
			{
				// int = nil or nil = int
				if (t1 == TYPE_INT.getInstance() || t2 == TYPE_INT.getInstance())
					throw new SemanticRuntimeException(lineNum, colNum, "equality testing cannot be done between 'int' and 'nil'\n");
				
				// string = nil or nil = string
				if (t1 == TYPE_STRING.getInstance() || t2 == TYPE_STRING.getInstance())
					throw new SemanticRuntimeException(lineNum, colNum, "equality testing cannot be done between 'string' and 'nil'\n");
				
				//nil = <CLASS OBJECT> or nil = <ARRAY OBJECT>
				return TYPE_INT.getInstance();
			}
			
			
			if (t1.getClass() != t2.getClass())
				throw new SemanticRuntimeException(lineNum, colNum, "equality testing cannot be done between two expressions of different types\n");
			
			//string = string
			if ((t1 == TYPE_STRING.getInstance()) && (t2 == TYPE_STRING.getInstance()))
				return TYPE_INT.getInstance();
			
			//t1 and t2 are arrays
			if (t1 instanceof TYPE_ARRAY)
			{
				String t1ArrayTypeName = ((TYPE_ARRAY)t1).name;
				String t2ArrayTypeName = ((TYPE_ARRAY)t2).name;
				if (t1ArrayTypeName.equals(t2ArrayTypeName))
					return TYPE_INT.getInstance();;
					
				//what about two arrays of different classes that have inheritance relation? should this case be handled differently?
					
				throw new SemanticRuntimeException(lineNum, colNum, "equality testing cannot be done between two arrays of different types\n");
			}
			
			//t1 and t2 are classes
			if (t1 instanceof TYPE_CLASS)
			{
				if (ExtendingOrSameClass((TYPE_CLASS)t1,(TYPE_CLASS)t2) || ExtendingOrSameClass((TYPE_CLASS)t2,(TYPE_CLASS)t1))
					return TYPE_INT.getInstance();;
					
				throw new SemanticRuntimeException(lineNum, colNum, "equality testing cannot be done between two classes that have no inheritance realtion\n");
			}
			
		}
		
		throw new SemanticRuntimeException(lineNum, colNum, "illegal binary operation\n");
	}
	
	private TEMP calcStringLength(TEMP stringAdd)
	{
		TEMP strAddCopyTemp = TEMP_FACTORY.getInstance().getFreshTEMP();
		IR.getInstance().Add_currentListIRcommand(new IRcommand_Move(strAddCopyTemp,stringAdd));
		
		TEMP counterTemp = TEMP_FACTORY.getInstance().getFreshTEMP();
		IR.getInstance().Add_currentListIRcommand(new IRcommandConstInt(counterTemp,-1));
		
		TEMP nullTermTemp = TEMP_FACTORY.getInstance().getFreshTEMP();
		IR.getInstance().Add_currentListIRcommand(new IRcommand_li_Null_Term(nullTermTemp));
		
		TEMP curChTemp = TEMP_FACTORY.getInstance().getFreshTEMP();
		
		String label_loop_start_s= IRcommand.getFreshLabel("str_len_loop_start");
		IR.getInstance().Add_currentListIRcommand(new IRcommand_Label(label_loop_start_s));
		
		// advance counter to next char
		IR.getInstance().Add_currentListIRcommand(new IRcommand_Add_Immediate(counterTemp,counterTemp,1));
		IR.getInstance().Add_currentListIRcommand(new IRcommand_Binop_Add_Integers(strAddCopyTemp,strAddCopyTemp,counterTemp));
		
		IR.getInstance().Add_currentListIRcommand(new IRcommand_Load_Byte_Offset(curChTemp,0,strAddCopyTemp));
		
		IR.getInstance().Add_currentListIRcommand(new IRcommand_BNE(curChTemp,nullTermTemp,label_loop_start_s));
		
		return counterTemp;
	}
	
	//advances stringAdd!
	private void copyStringWithoutNullTerm(TEMP stringAdd, TEMP dstAdd)
	{
		TEMP nullTermTemp = TEMP_FACTORY.getInstance().getFreshTEMP();
		IR.getInstance().Add_currentListIRcommand(new IRcommand_li_Null_Term(nullTermTemp));
		
		TEMP curChTemp = TEMP_FACTORY.getInstance().getFreshTEMP();
		
		String label_copy_end_s= IRcommand.getFreshLabel("str_copy_loop_end");
		String label_copy_start_s= IRcommand.getFreshLabel("str_copy_loop_start");
		IR.getInstance().Add_currentListIRcommand(new IRcommand_Label(label_copy_start_s));
		
		IR.getInstance().Add_currentListIRcommand(new IRcommand_Load_Byte_Offset(curChTemp,0,stringAdd));
		
		IR.getInstance().Add_currentListIRcommand(new IRcommand_BEQ(curChTemp,nullTermTemp,label_copy_end_s));
		// advance counter to next char
		IR.getInstance().Add_currentListIRcommand(new IRcommand_Add_Immediate(stringAdd,stringAdd,1));
		
		// copy next char
		IR.getInstance().Add_currentListIRcommand(new IRcommand_Jump_Label(label_copy_start_s));
		
		IR.getInstance().Add_currentListIRcommand(new IRcommand_Label(label_copy_end_s));
	}
	
	private void concatStrings(TEMP s1add, TEMP s2add, TEMP concatRes)
	{
		TEMP s1Len = calcStringLength(s1add);
		TEMP s2Len = calcStringLength(s2add);
		
		TEMP concatLen = TEMP_FACTORY.getInstance().getFreshTEMP();
		
		IR.getInstance().Add_currentListIRcommand(new IRcommand_Binop_Add_Integers(concatLen,s1Len,s2Len));
		IR.getInstance().Add_currentListIRcommand(new IRcommand_Add_Immediate(concatLen,concatLen,1));
		
		IR.getInstance().Add_currentListIRcommand(new IRcommand_Malloc(concatLen,concatRes));
		
		copyStringWithoutNullTerm(s1add,concatRes);
		IR.getInstance().Add_currentListIRcommand(new IRcommand_Binop_Add_Integers(concatRes,concatRes,s1Len));
		
		copyStringWithoutNullTerm(s2add,concatRes);
		IR.getInstance().Add_currentListIRcommand(new IRcommand_Binop_Add_Integers(concatRes,concatRes,s2Len));
		
		TEMP nullTermTemp = TEMP_FACTORY.getInstance().getFreshTEMP();
		IR.getInstance().Add_currentListIRcommand(new IRcommand_li_Null_Term(nullTermTemp));
		
		IR.getInstance().Add_currentListIRcommand(new IRcommand_Store_Byte_Offset(nullTermTemp,0,concatRes));
		
		IR.getInstance().Add_currentListIRcommand(new IRcommand_Binop_Sub_Integers(concatRes,concatRes,concatLen));
		IR.getInstance().Add_currentListIRcommand(new IRcommand_Add_Immediate(concatRes,concatRes,1));
		
	}
	
	private void compareStrings(TEMP s1add, TEMP s2add, TEMP eqRes)
	{
		/*******************************/
		/* [1] Allocate 3 fresh labels */
		/*******************************/
		String label_end        = IRcommand.getFreshLabel("end");
		String label_loop_start = IRcommand.getFreshLabel("cmp_str_loop_start");
		String label_AssignOne  = IRcommand.getFreshLabel("AssignOne");
		String label_AssignZero = IRcommand.getFreshLabel("AssignZero");
		
		// if addresses are equal, Assign 1 and exit
		IR.getInstance().Add_currentListIRcommand(new IRcommand_BEQ(s1add,s2add,label_AssignOne));
		
		// if one of the addresses is NIL, Assign 0 and exit (undefined behavior - we decided)
		IR.getInstance().Add_currentListIRcommand(new IRcommand_BEQZ(s1add,label_AssignZero));
		IR.getInstance().Add_currentListIRcommand(new IRcommand_BEQZ(s2add,label_AssignZero));
		
		// count current char in both strings
		TEMP counterTemp = TEMP_FACTORY.getInstance().getFreshTEMP();
		IR.getInstance().Add_currentListIRcommand(new IRcommandConstInt(counterTemp,0));
		
		// load '\0' into nullTermTemp for future use
		TEMP nullTermTemp = TEMP_FACTORY.getInstance().getFreshTEMP();
		IR.getInstance().Add_currentListIRcommand(new IRcommand_li_Null_Term(nullTermTemp));
		
		TEMP ch1Temp = TEMP_FACTORY.getInstance().getFreshTEMP();
		TEMP ch2Temp = TEMP_FACTORY.getInstance().getFreshTEMP();
		
		// string comparison loop start
		IR.getInstance().Add_currentListIRcommand(new IRcommand_Label(label_loop_start));
		
		// advance both string addresses by the counter
		IR.getInstance().Add_currentListIRcommand(new IRcommand_Binop_Add_Integers(s1add,s1add,counterTemp));
		IR.getInstance().Add_currentListIRcommand(new IRcommand_Binop_Add_Integers(s2add,s2add,counterTemp));
		
		// load current char into ch1Temp, ch2Temp
		IR.getInstance().Add_currentListIRcommand(new IRcommand_Load_Byte_Offset(ch1Temp,0,s1add));
		IR.getInstance().Add_currentListIRcommand(new IRcommand_Load_Byte_Offset(ch2Temp,0,s2add));
		
		// if ch1Temp != ch2Temp, jump to assign zero and exit
		IR.getInstance().Add_currentListIRcommand(new IRcommand_BNE(ch1Temp,ch2Temp,label_AssignZero));
		
		// if ch1Temp == nullTermTemp (reached end of both strings), jump to assign one and exit
		IR.getInstance().Add_currentListIRcommand(new IRcommand_BEQ(ch1Temp,nullTermTemp,label_AssignOne));
		
		// advance counter to next char
		IR.getInstance().Add_currentListIRcommand(new IRcommand_Add_Immediate(counterTemp,counterTemp,1));
		
		// compare next char
		IR.getInstance().Add_currentListIRcommand(new IRcommand_Jump_Label(label_loop_start));
		
		
		/************************/
		/* [3] label_AssignOne: */
		/************************/
		IR.getInstance().Add_currentListIRcommand(new IRcommand_Label(label_AssignOne));
		
		IR.getInstance().Add_currentListIRcommand(new IRcommandConstInt(eqRes,1));

		IR.getInstance().Add_currentListIRcommand(new IRcommand_Jump_Label(label_end));

		/*************************/
		/* [4] label_AssignZero: */
		/*************************/
		IR.getInstance().Add_currentListIRcommand(new IRcommand_Label(label_AssignZero));
		
		IR.getInstance().Add_currentListIRcommand(new IRcommandConstInt(eqRes,0));

		IR.getInstance().Add_currentListIRcommand(new IRcommand_Jump_Label(label_end));

		/******************/
		/* [5] label_end: */
		/******************/
		IR.getInstance().Add_currentListIRcommand(new IRcommand_Label(label_end));
	}
	
	private void checkBinopBoundaries(TEMP opRes)
	{
		String label_end   = IRcommand.getFreshLabel("end");
		String label_neg_overflow   = IRcommand.getFreshLabel("neg_overflow");
		
		TEMP immediateTemp = TEMP_FACTORY.getInstance().getFreshTEMP();
		
		IR.getInstance().Add_currentListIRcommand(new IRcommandConstInt(immediateTemp,-32768));
		
		IR.getInstance().Add_currentListIRcommand(new IRcommand_BLT(opRes,immediateTemp,label_neg_overflow));
		
		IR.getInstance().Add_currentListIRcommand(new IRcommandConstInt(immediateTemp,32768));
		
		IR.getInstance().Add_currentListIRcommand(new IRcommand_BLT(opRes,immediateTemp,label_end));
		
		IR.getInstance().Add_currentListIRcommand(new IRcommandConstInt(opRes,32767));
		
		IR.getInstance().Add_currentListIRcommand(new IRcommand_Jump_Label(label_end));
		
		IR.getInstance().Add_currentListIRcommand(new IRcommand_Label(label_neg_overflow));
		
		IR.getInstance().Add_currentListIRcommand(new IRcommandConstInt(opRes,-32768));
		
		IR.getInstance().Add_currentListIRcommand(new IRcommand_Label(label_end));
	}
	
	private void checkDivByZero(TEMP denominator)
	{
		String label_not_zero   = IRcommand.getFreshLabel("not_zero_denominator");
		
		IR.getInstance().Add_currentListIRcommand(new IRcommand_BNEZ(denominator,label_not_zero));
		
		String label_div_exception = "string_illegal_div_by_0";
		
		TEMP div_exception_str_add = TEMP_FACTORY.getInstance().getFreshTEMP();
		
		IR.getInstance().Add_currentListIRcommand(new IRcommand_Load_Address(label_div_exception,div_exception_str_add));
		
		IR.getInstance().Add_currentListIRcommand(new IRcommand_Print_String_By_Address(div_exception_str_add));
		
		IR.getInstance().Add_currentListIRcommand(new IRcommand_Exit());
		
		IR.getInstance().Add_currentListIRcommand(new IRcommand_Label(label_not_zero));
	}
	
	public TEMP IRme()
	{
		TEMP t1 = null;
		TEMP t2 = null;
		TEMP dst = TEMP_FACTORY.getInstance().getFreshTEMP();
				
		if (left  != null) t1 = left.IRme();
		if (right != null) t2 = right.IRme();
		
		if (OP == 0) {
			if (this.leftType == TYPE_STRING.getInstance() && this.rightType == TYPE_STRING.getInstance())
				this.concatStrings(t1, t2, dst);
			else
				IR.getInstance().Add_currentListIRcommand(new IRcommand_Binop_Add_Integers(dst, t1, t2));
		}
		if (OP == 1) // (int only)
			IR.getInstance().Add_currentListIRcommand(new IRcommand_Binop_Sub_Integers(dst,t1,t2));
		if (OP == 2) //(int only)
			IR.getInstance().Add_currentListIRcommand(new IRcommand_Binop_Mul_Integers(dst,t1,t2));
		if (OP == 3){ //(int only) 
			checkDivByZero(t2);
			IR.getInstance().Add_currentListIRcommand(new IRcommand_Binop_Div_Integers(dst,t1,t2));
		}
		if (OP == 4) //(int only)
			IR.getInstance().Add_currentListIRcommand(new IRcommand_Binop_LT_Integers(dst,t1,t2));
		if (OP == 5) //(int only)
			IR.getInstance().Add_currentListIRcommand(new IRcommand_Binop_LT_Integers(dst,t2,t1));
		if (OP == 6)
		{
			if (this.leftType == TYPE_STRING.getInstance() && this.rightType == TYPE_STRING.getInstance())
				this.compareStrings(t1, t2, dst);
			else
				IR.getInstance().Add_currentListIRcommand(new IRcommand_Binop_EQ_Integers(dst,t1,t2));
		}
		
		if ((OP >= 1 && OP <=3) || (OP == 0 && this.leftType == TYPE_INT.getInstance() && this.rightType == TYPE_INT.getInstance()))
			checkBinopBoundaries(dst);
		
		return dst;
	}
}
