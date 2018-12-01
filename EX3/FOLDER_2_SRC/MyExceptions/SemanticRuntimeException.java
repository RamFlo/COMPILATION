package MyExceptions;
public class SemanticRuntimeException extends RuntimeException {
	int lineNum,colNum;
	public SemanticRuntimeException(int errorLineNum,int errorColNum,String message){
		super(message);
		this.lineNum = errorLineNum;
		this.colNum = errorColNum;
	}
}