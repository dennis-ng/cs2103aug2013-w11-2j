package typetodo.exception;

/**
 * 
 * @author A0091024U
 *
 */
@SuppressWarnings("serial")
public class ReservedCharacterException extends Exception {
	public ReservedCharacterException() {
		super();
	}

	public ReservedCharacterException(String message) {
		super(message);
	}
}