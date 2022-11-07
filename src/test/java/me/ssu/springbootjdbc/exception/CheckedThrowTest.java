package me.ssu.springbootjdbc.exception;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
public class CheckedThrowTest {

	@Test
	@DisplayName("체크 예외를 밖으로 던지는 코드 테스트")
	void checked_throw() {
		Service service = new Service();
		assertThatThrownBy(() -> service.callThrow())
				.isInstanceOf(MyCheckedException.class);
	}

	/**
	 * Exception을 상속받은 예외는 체크 예외가 된다.
	 */
	static class MyCheckedException extends Exception {
		public MyCheckedException(String message) {
			super(message);
		}
	}

	// 예외를 던질 때 throws를 던져야 한다. 밖으로 선언을 해줘야 함.
	static class Repository {
		public void call() throws MyCheckedException {
			throw new MyCheckedException("ex");
		}
	}

	/**
	 * Checked 예외는
	 *  예외를 잡아서 처리하거나 던지거나 둘중 하나를 필수로 선택해야 한다.
	 */
	static class Service {
		Repository repository = new Repository();

		/**
		 * 체크 예외를 밖으로 던지는 코드
		 * 체크 예외는 예외를 잡지않고 밖으로 던지려면 throws 예외를
		 * 메서드에 필수로 선언해야 한다.
		 * @throws MyCheckedException
		 */
		public void callThrow() throws MyCheckedException {
			repository.call();
		}
	}
}