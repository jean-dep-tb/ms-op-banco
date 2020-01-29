package spring.boot.webflu.ms.op.banco.app.exception;

public class ResponseStatus extends RuntimeException{
	
	public ResponseStatus(String message)
	{
	  super(message);
	}
	
	public ResponseStatus(String message, Throwable cause)
	{
	  super(message, cause);
	}
	
}
