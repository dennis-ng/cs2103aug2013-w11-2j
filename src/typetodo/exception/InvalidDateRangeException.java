package typetodo.exception;

@SuppressWarnings("serial")
public class InvalidDateRangeException extends Exception {

	public InvalidDateRangeException() {
		super();
	}

	public InvalidDateRangeException(String message) {
		super(message);
	}

}
