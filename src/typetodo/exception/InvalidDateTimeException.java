package typetodo.exception;
@SuppressWarnings("serial")

/**
 * 
 * @author A0091024U
 *
 */
public class InvalidDateTimeException extends Exception {
	public InvalidDateTimeException(){
		super();
	}
	
	public InvalidDateTimeException(String message) {
		super(message);
	}
}
