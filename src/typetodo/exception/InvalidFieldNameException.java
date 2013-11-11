package typetodo.exception;

/**
 * 
 * @author A0091024U
 *
 */
@SuppressWarnings("serial")
public class InvalidFieldNameException extends Exception {
	public InvalidFieldNameException() {
		super();
	}

	public InvalidFieldNameException(String message) {
		super(message);
	}
}
