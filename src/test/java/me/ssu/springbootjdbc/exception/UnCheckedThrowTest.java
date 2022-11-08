package me.ssu.springbootjdbc.exception;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
public class UnCheckedThrowTest {

	@Test
	@DisplayName("언체크 예외, 예외를 잡지 않아도 된다. 자연스럽게 상위로 넘어가는 코드 테스트")
	void unchecked_throw() {
		Service service = new Service();
		assertThatThrownBy(() -> service.callThrow())
				.isInstanceOf(MyUncheckedException.class);
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
		 * 예외를 잡지 않아도 된다. 자연스럽게 상위로 넘어간다.
		 * 체크 예외와 다르게 throws 예외 선언을 하지 않아도 된다.
		 */
		public void callThrow() {
			repository.call();

		}

		static class Repository {
			public void call() {
				throw new MyUncheckedException("ex");
			}
		}
	}
}