   
import java.io.*;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import java_cup.runtime.Symbol;
import AST.*;
import CFG.CFGBuilder;
import CFG.LivenessAnalyzer;
import IR.*;
import KempAlgorithm.Kemp;
import KempAlgorithm.KempAlgorithmException;
import KempAlgorithm.KempGraph;
import MIPS.*;
import MyExceptions.SemanticRuntimeException;

public class Main
{
	static public void main(String argv[])
	{
		Lexer l;
		Parser p;
		Symbol s;
		AST_DEC_LIST AST;
		FileReader file_reader;
		PrintWriter file_writer = null;
		String inputFilename = argv[0];
		String outputFilename = argv[1];
		
		try
		{
			/********************************/
			/* [1] Initialize a file reader */
			/********************************/
			file_reader = new FileReader(inputFilename);

			/********************************/
			/* [2] Initialize a file writer */
			/********************************/
			file_writer = new PrintWriter(outputFilename);
			
			/******************************/
			/* [3] Initialize a new lexer */
			/******************************/
			l = new Lexer(file_reader);
			
			/*******************************/
			/* [4] Initialize a new parser */
			/*******************************/
			p = new Parser(l);

			/***********************************/
			/* [5] 3 ... 2 ... 1 ... Parse !!! */
			/***********************************/
			AST = (AST_DEC_LIST) p.parse().value;
			
			/*************************/
			/* [6] Print the AST ... */
			/*************************/
			AST.PrintMe();

			/**************************/
			/* [7] Semant the AST ... */
			/**************************/
			AST.SemantMe();

			/**********************/
			/* [8] IR the AST ... */
			/**********************/
			AST.IRme();
			
			/***********************/
			/* [9] MIPS the IR ... */
			/***********************/
			IR.getInstance().MIPSme();

			/**************************************/
			/* [10] Finalize AST GRAPHIZ DOT file */
			/**************************************/
			AST_GRAPHVIZ.getInstance().finalizeFile();			

			/***************************/
			/* [11] Finalize MIPS file */
			/***************************/
			sir_MIPS_a_lot.getInstance().finalizeFile();			

			/**************************/
			/* [12] Close output file */
			/**************************/
			//ok for semantic parsing!
			file_writer.println("OK");
			file_writer.close();
			
			
			CFGBuilder.linkBlockByLabels();
			LivenessAnalyzer la = new LivenessAnalyzer(CFGBuilder.buttomElement, CFGBuilder.CFG);
			la.analyzeLiveness();
			KempGraph graphToColor = la.buildKempGraphFromLiveAnalysis();
			Kemp kempAlgRunner = new Kemp(graphToColor);
			Map<Integer,Integer> coloring = kempAlgRunner.kempAlg();
			
			//debug print
//			for(Integer curTemp:coloring.keySet())
//				System.out.println(String.format("Temp_%d: $t%d", curTemp,coloring.get(curTemp)));
			//debug print
			
			Path path = Paths.get("./FOLDER_5_OUTPUT/MIPS.txt");
			Charset charset = StandardCharsets.UTF_8;

			String content = new String(Files.readAllBytes(path), charset);
			
			for(Integer curTemp:coloring.keySet())
				content = content.replaceAll(String.format("Temp_%d", curTemp), String.format("$t%d", coloring.get(curTemp)));
			
			//debug print
//			System.out.println(String.format("Program is: \n\n %s", content));
			//debug print
			
			Files.write(path, content.getBytes(charset));
    	}
		catch (ParserRuntimeException e)
		{
			if (file_writer != null) {
				file_writer.print(e.getMessage());
				file_writer.close();
			}
			e.printStackTrace();
			return;
		}
		catch (SemanticRuntimeException e)
		{
			if (file_writer != null) {
				file_writer.println(String.format("ERROR(%d)",e.getLineNum()));
				file_writer.close();
			}
			System.out.format(">> ERROR [%d:%d] %s", e.getLineNum(),e.getColNum(),e.getMessage());
			e.printStackTrace();
			return;
		}	
		catch (KempAlgorithmException e)
		{
			System.out.println(e.getMessage());
		}
			     
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}


