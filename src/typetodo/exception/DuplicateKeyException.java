package typetodo.exception;

/**
 * 
 * @author A0091024U
 *
 */
@SuppressWarnings("serial")
public class DuplicateKeyException extends Exception {

	public DuplicateKeyException() {
		super();
	}

	public DuplicateKeyException(String message) {
		super(message);
	}

}
