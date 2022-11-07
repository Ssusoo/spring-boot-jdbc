package me.ssu.springbootjdbc.exception;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Slf4j
public class CheckedCatchTest {

	@Test
	@DisplayName("예외를 잡아서 처리하는 코드")
	void checked_catch() {
		Service service = new Service();
		service.callCatch();
	}

	/**
	 * Exception을 상속받은 예외는 체크 예외가 된다.
	 */
	static class MyCheckedException extends Exception {
		public MyCheckedException(String message) {
			super(message);
		}
	}

	/**
	 * Checked 예외는
	 *  예외를 잡아서 처리하거나 던지거나 둘중 하나를 필수로 선택해야 한다.
	 */
	static class Service {
		Repository repository = new Repository();

		/**
		 * 예외를 잡아서 처리하는 코드
		 */
		public void callCatch() {
			try {
				repository.call();
			} catch (MyCheckedException e) {
				// 예외 처리 로직
				log.info("예외 처리, message={}", e.getMessage(), e);
			}
		}
	}

	// 예외를 던질 때 throws로 던져야 된다. 밖으로 선언을 해줘야 함.
	static class Repository {
		public void call() throws MyCheckedException {
			throw new MyCheckedException("ex");
		}
	}
}