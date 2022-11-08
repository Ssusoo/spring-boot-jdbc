package me.ssu.springbootjdbc.repository.exception;

/**
 * 데이터 중복의 경우에만 던지는 예외
 */
public class MyDuplicateKeyException extends MyDbException {

	public MyDuplicateKeyException() {
	}

	public MyDuplicateKeyException(String message) {
		super(message);
	}

	public MyDuplicateKeyException(String message, Throwable cause) {
		super(message, cause);
	}

	public MyDuplicateKeyException(Throwable cause) {
		super(cause);
	}
}