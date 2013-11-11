package typetodo.exception;

@SuppressWarnings("serial")
public class DuplicateKeyException extends Exception {

	public DuplicateKeyException() {
		super();
	}

	public DuplicateKeyException(String message) {
		super(message);
	}

}
