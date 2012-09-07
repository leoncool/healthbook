package server.exception;

public class InnerException extends Exception {

	private long errorCode;
	
	public InnerException(String desc, long code) {
		super(desc+" - "+code);
		
		errorCode=code;
	}
	
	public long getErrorCode() {
		return errorCode;
	}
}
