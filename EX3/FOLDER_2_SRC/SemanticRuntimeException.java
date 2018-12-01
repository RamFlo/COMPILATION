public class SemanticRuntimeException extends RuntimeException {
	int lineNum,colNum;
	public SemanticRuntimeException(String message,int errorLineNum,int errorColNum){
		super(message);
		this.lineNum = errorLineNum;
		this.colNum = errorColNum;
	}
}