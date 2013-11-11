package typetodo.exception;

/**
 * 
 * @author A0091024U
 *
 */
@SuppressWarnings("serial")
public class InvalidCommandException extends Exception {
	public InvalidCommandException() {
		super();
	}

	public InvalidCommandException(String message) {
		super(message);
	}
}
