package server.exception;

public class ErrorCodeException extends Exception {
	private static final long serialVersionUID = 6097632570014357179L;
	private String errorCode;
	
	public ErrorCodeException(String errCode) {
		super(""+errCode);
		this.errorCode=errCode;
	}
	
	public String getErrorCode() {
		return errorCode;
	}
}
