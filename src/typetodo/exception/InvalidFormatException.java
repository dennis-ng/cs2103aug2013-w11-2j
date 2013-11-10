package typetodo.exception;

@SuppressWarnings("serial")
/**
 * Exception thrown if user input does not follow the correct formatting
 * @author Shiyu
 *
 */
public class InvalidFormatException extends Exception {
	public InvalidFormatException() {
		super();
	}

	public InvalidFormatException(String message) {
		super(message);
	}
}
