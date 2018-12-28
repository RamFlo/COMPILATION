package MyExceptions;
public class SemanticRuntimeException extends RuntimeException {
	private int lineNum;
	private int colNum;
	public SemanticRuntimeException(int errorLineNum,int errorColNum,String message){
		super(message);
		this.lineNum = errorLineNum;
		this.colNum = errorColNum;
	}
	
	public int getLineNum() {
		return lineNum;
	}
	
	public int getColNum() {
		return colNum;
	}
}
