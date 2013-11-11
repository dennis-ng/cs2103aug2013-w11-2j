package typetodo.exception;

/**
 * 
 * @author A0091024U
 *
 */
@SuppressWarnings("serial")
public class InvalidIdException extends Exception {

	public InvalidIdException() {
		super();
	}

	public InvalidIdException(String message) {
		super(message);
	}
}