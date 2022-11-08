package me.ssu.springbootjdbc.exception;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Slf4j
public class UnCheckedCatchTest {

	@Test
	@DisplayName("언체크 예외, 잡지 않아도 되는 코드 테스트")
	void unchecked_catch() {
		Service service = new Service();
		service.callCatch();
	}

	/**
	 * RuntimeException을 상속받은 예외는 언체크 예외가 된다.
	 */
	static class MyUncheckedException extends RuntimeException {
		public MyUncheckedException(String message) {
			super(message);
		}
	}

	/**
	 * UnChecked 예외는
	 * 예외를 잡거나, 던지지 않아도 된다.
	 * 예외를 잡지 않으면 자동으로 밖으로 던진다.
	 */
	static class Service {
		Repository repository = new Repository();

		/**
		 * 필요한 경우 예외를 잡아서 처리하면 된다.
		 */
		public void callCatch() {
			try {
				repository.call();
			} catch (MyUncheckedException e) {
				//예외 처리 로직
				log.info("예외 처리, message={}", e.getMessage(), e);
			}
		}
	}

	static class Repository {
		public void call() {
			throw new MyUncheckedException("ex");
		}
	}
}